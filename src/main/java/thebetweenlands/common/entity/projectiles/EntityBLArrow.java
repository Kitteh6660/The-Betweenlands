package thebetweenlands.common.entity.projectiles;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.EntityShock;
import thebetweenlands.common.entity.mobs.EntityChiromawMatriarch;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWormHelper;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityBLArrow extends ArrowEntity implements IThrowableEntity /*for shooter sync*/ {
	@SuppressWarnings("unchecked")
	private static final Predicate<Entity> ARROW_TARGETS = Predicates.and(EntityPredicates.NO_SPECTATORS, EntityPredicates.ENTITY_STILL_ALIVE, new Predicate<Entity>() {
		@Override
		public boolean apply(@Nullable Entity entity) {
			return entity.canBeCollidedWith();
		}
	});

	private static final DataParameter<String> DW_TYPE = EntityDataManager.<String>createKey(EntityBLArrow.class, DataSerializers.STRING);

	private int ticksSpentInAir = 0;
	private int ticksSpentInGround = 0;

	public EntityBLArrow(World worldIn) {
		super(worldIn);
	}

	public EntityBLArrow(World worldIn, LivingEntity shooter) {
		super(worldIn, shooter);
	}

	@Override
	public Entity getThrower() {
		return this.shootingEntity;
	}

	@Override
	public void setThrower(Entity entity) {
		this.shootingEntity = entity;
	}

	@Override
	public void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DW_TYPE, "");
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putString("arrowType", this.getArrowType().getName());
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		this.setType(EnumArrowType.getEnumFromString(nbt.getString("arrowType")));
	}

	@Override
	@Nullable
	protected Entity findEntityOnPath(Vector3d start, Vector3d end) {
		List<Entity> list = this.level.getEntitiesInAABBexcluding(this, this.getBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D), ARROW_TARGETS);

		Entity hit = null;
		double minDstSq = 0.0D;

		for(Entity entity : list) {
			if(this.isNotShootingEntity(entity) || this.ticksSpentInAir >= 5) {
				AxisAlignedBB checkBox = entity.getBoundingBox().grow(0.3D);
				RayTraceResult rayTrace = checkBox.calculateIntercept(start, end);

				if(rayTrace != null) {
					double dstSq = start.squareDistanceTo(rayTrace.hitVec);

					if(dstSq < minDstSq || minDstSq == 0.0D) {
						hit = entity;
						minDstSq = dstSq;
					}
				}
			}
		}

		return hit;
	}

	private boolean isNotShootingEntity(Entity entity) {
		if(entity == this.shootingEntity) {
			return false;
		} else if(this.shootingEntity instanceof PlayerEntity == false && this.shootingEntity != null && this.shootingEntity.getRidingEntity() == entity) {
			return false;
		} else if(this.shootingEntity instanceof PlayerEntity && this.shootingEntity != null && entity instanceof IEntityOwnable &&
				((IEntityOwnable) entity).getOwner() == this.shootingEntity && this.shootingEntity.getRecursivePassengers().contains(entity)) {
			return false;
		}
		return true;
	}

	@Override
	public void tick() {
		super.tick();

		if(this.inGround) {
			this.ticksSpentInAir = 0;
			this.ticksSpentInGround++;
		} else {
			this.ticksSpentInAir++;
			this.ticksSpentInGround = 0;
		}

		if(!this.level.isClientSide() && (this.getArrowType() == EnumArrowType.CHIROMAW_BARB || this.getArrowType() == EnumArrowType.CHIROMAW_SHOCK_BARB) && this.pickupStatus != PickupStatus.ALLOWED && this.ticksSpentInGround > 100) {
			this.remove();
		}
		
		if(this.level.isClientSide() && (this.getArrowType() == EnumArrowType.SHOCK || this.getArrowType() == EnumArrowType.CHIROMAW_SHOCK_BARB)) {
			this.spawnLightningArcs();
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnLightningArcs() {
		Entity view = Minecraft.getInstance().getRenderViewEntity();
		if(view != null && view.getDistance(this) < 16 && this.world.rand.nextInt(!this.inGround ? 2 : 20) == 0) {
			float ox = this.world.rand.nextFloat() - 0.5f + (!this.inGround ? (float)this.motionX : 0);
			float oy = this.world.rand.nextFloat() - 0.5f + (!this.inGround ? (float)this.motionY : 0);
			float oz = this.world.rand.nextFloat() - 0.5f + (!this.inGround ? (float)this.motionZ : 0);

			Particle particle = BLParticles.LIGHTNING_ARC.create(this.world, this.getX(), this.getY(), this.getZ(), 
					ParticleArgs.get()
					.withMotion(!this.inGround ? this.motionX : 0, !this.inGround ? this.motionY : 0, !this.inGround ? this.motionZ : 0)
					.withColor(0.3f, 0.5f, 1.0f, 0.9f)
					.withData(new Vector3d(this.getX() + ox, this.getY() + oy, this.getZ() + oz)));

			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, particle);
		}
	}

	@Override
	protected void arrowHit(LivingEntity living) {
		switch(this.getArrowType()) {
		case ANGLER_POISON:
			living.addEffect(new EffectInstance(Effects.POISON, 200, 2));
			break;
		case OCTINE:
			if(living.isOnFire()) {
				living.setFire(9);
			} else {
				living.setFire(5);
			}
			break;
		case BASILISK:
			if(living.isNonBoss() && !(living instanceof EntityChiromawMatriarch)) {
				living.addEffect(ElixirEffectRegistry.EFFECT_PETRIFY.createEffect(100, 1));
			} else {
				living.addEffect(ElixirEffectRegistry.EFFECT_PETRIFY.createEffect(40, 1));
			}
			break;
		case WORM:
			if (!level.isClientSide()) {
				EntityTinySludgeWormHelper worm = new EntityTinySludgeWormHelper(level);
				worm.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
				worm.setAttackTarget(living);
				if(this.shootingEntity instanceof PlayerEntity) {
					worm.setOwnerId(this.shootingEntity.getUUID());
				}
				level.spawnEntity(worm);
				this.remove();
			}
			break;
		case SHOCK:
			if(!this.level.isClientSide()) {
				this.world.spawnEntity(new EntityShock(this.world, this, living, this.isWet() || this.isInWater() || this.world.isRainingAt(this.getPosition().above())));
			}
			break;
		case CHIROMAW_BARB:
			if(living.isNonBoss() && !(living instanceof EntityChiromawMatriarch)) {
				living.addEffect(ElixirEffectRegistry.EFFECT_PETRIFY.createEffect(40, 1));
			} 
			break;
		case CHIROMAW_SHOCK_BARB:
			if(!this.level.isClientSide()) {
				this.world.spawnEntity(new EntityShock(this.world, this, living, this.isWet() || this.isInWater() || this.world.isRainingAt(this.getPosition().above())));
			}
			if(living.isNonBoss() && !(living instanceof EntityChiromawMatriarch)) {
				living.addEffect(ElixirEffectRegistry.EFFECT_PETRIFY.createEffect(40, 1));
			} 
			break;
		default:
		}
	}

	@Override
	protected void onHit(RayTraceResult raytrace) {
		super.onHit(raytrace);

		if(raytrace.entityHit == null && raytrace.getBlockPos() != null && raytrace.sideHit != null && this.getArrowType() == EnumArrowType.OCTINE) {
			BlockPos pos = raytrace.getBlockPos().offset(raytrace.sideHit);
			BlockState state = this.world.getBlockState(pos);

			if(ItemRegistry.OCTINE_INGOT.isTinder(new ItemStack(ItemRegistry.OCTINE_INGOT), ItemStack.EMPTY, state)) {
				this.world.setBlockState(pos, Blocks.FIRE.defaultBlockState());
			}
		}
	}

	@Override
	public void playSound(SoundEvent soundIn, float volume, float pitch) {
		if (!this.isSilent()) {
			if(getArrowType() == EnumArrowType.CHIROMAW_BARB || getArrowType() == EnumArrowType.CHIROMAW_SHOCK_BARB) {
				if(soundIn == SoundEvents.ENTITY_ARROW_HIT)
					soundIn = SoundRegistry.CHIROMAW_MATRIARCH_BARB_HIT;
			}	
		}
		super.playSound(soundIn, volume, pitch);
	}

	/**
	 * Sets the arrow type
	 * @param type
	 */
	public void setType(EnumArrowType type) {
		this.dataManager.set(DW_TYPE, type.getName());
	}

	/**
	 * Returns the arrow type
	 * @return
	 */
	public EnumArrowType getArrowType(){
		return EnumArrowType.getEnumFromString(this.dataManager.get(DW_TYPE));
	}

	@Override
	protected ItemStack getArrowStack() {
		switch(this.getArrowType()) {
		case ANGLER_POISON:
			return new ItemStack(ItemRegistry.POISONED_ANGLER_TOOTH_ARROW);
		case OCTINE:
			return new ItemStack(ItemRegistry.OCTINE_ARROW);
		case BASILISK:
			return new ItemStack(ItemRegistry.BASILISK_ARROW);
		case WORM:
			return new ItemStack(ItemRegistry.SLUDGE_WORM_ARROW);
		case SHOCK:
			return new ItemStack(ItemRegistry.SHOCK_ARROW);
		case CHIROMAW_BARB:
			return new ItemStack(ItemRegistry.CHIROMAW_BARB);
		case DEFAULT:
			return new ItemStack(ItemRegistry.ANGLER_TOOTH_ARROW);
		default:
			return ItemStack.EMPTY;
		}
	}
}
