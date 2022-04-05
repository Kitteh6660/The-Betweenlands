package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.attributes.Attributes.IAttribute;
import net.minecraft.entity.ai.attributes.Attributes.RangedAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IRingOfGatheringMinion;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.EntityTameableBL;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityTarminion extends EntityTameableBL implements IEntityBL, IRingOfGatheringMinion {
	public static final IAttribute MAX_TICKS_ATTRIB = (new RangedAttribute(null, "bl.maxAliveTicks", 7200.0D, 0, Integer.MAX_VALUE)).setDescription("Maximum ticks until the Tar Minion despawns");

	private int despawnTicks = 0;

	protected boolean dropContentsWhenDead = true;

	public EntityTarminion(World world) {
		super(world);
		this.setSize(0.3F, 0.5F);
		this.experienceValue = 0;
		this.fireImmune = true;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new EntityAISwimming(this));
		this.goalSelector.addGoal(1, new EntityAIAttackMelee(this, 1.0D, true));
		this.goalSelector.addGoal(2, new EntityAIFollowOwner(this, 1.0D, 3.0F, 40.0F));
		this.goalSelector.addGoal(3, new EntityAIWander(this, 0.5D));

		this.targetSelector.addGoal(0, new EntityAIOwnerHurtByTarget(this));
		this.targetSelector.addGoal(1, new EntityAIOwnerHurtTarget(this));
		this.targetSelector.addGoal(2, new EntityAIHurtByTarget(this, false) {
			@Override
			protected void setEntityAttackTarget(EntityCreature ally, LivingEntity target) {
				if(target instanceof EntityTarminion == false) {
					super.setEntityAttackTarget(ally, target);
				}
			}
		});
		this.targetSelector.addGoal(3, new EntityAINearestAttackableTarget<MobEntity>(this, MobEntity.class, 10, false, false, entity -> {
			return entity instanceof IMob && (entity instanceof IEntityOwnable == false || ((IEntityOwnable) entity).getOwner() != EntityTarminion.this.getOwner());
		}));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5D);
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(60.0D);
		this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(32.0D);
		this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.9D);

		this.getAttributeMap().registerAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
		this.getAttributeMap().registerAttribute(MAX_TICKS_ATTRIB);
	}

	@Override
	public boolean canDespawn() {
		return false;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isTamed() {
		return true;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block state) {
		if(this.random.nextInt(10) == 0) {
			this.playSound(SoundRegistry.TAR_BEAST_STEP, 0.8F, 1.5F);
		}
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SQUISH;
	}

	@Override
	public void tick() {
		super.tick();

		if(!this.level.isClientSide()) {
			this.despawnTicks++;
			if(this.despawnTicks > this.getAttribute(MAX_TICKS_ATTRIB).getValue()) {
				this.hurt(DamageSource.GENERIC, this.getMaxHealth());
			}
		}

		if(this.level.isClientSide() && this.tickCount % 20 == 0) {
			this.addParticles(this.world, this.getX(), this.getY(), this.getZ(), this.rand);
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putInt("despawnTicks", this.despawnTicks);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.readFromNBT(nbt);
		this.despawnTicks = nbt.getInt("despawnTicks");
	}

	@Override
	public void remove() {
		if(!this.isDead && this.dropContentsWhenDead) {
			if(this.getAttackTarget() != null) {
				if(this.level.isClientSide()) {
					for(int i = 0; i < 200; i++) {
						Random rnd = this.world.rand;
						float rx = rnd.nextFloat() * 1.0F - 0.5F;
						float ry = rnd.nextFloat() * 1.0F - 0.5F;
						float rz = rnd.nextFloat() * 1.0F - 0.5F;
						Vector3d vec = new Vector3d(rx, ry, rz);
						vec = vec.normalize();
						BLParticles.SPLASH_TAR.spawn(this.world, this.getX() + rx + 0.1F, this.getY() + ry, this.getZ() + rz + 0.1F, ParticleArgs.get().withMotion(vec.x * 0.4F, vec.y * 0.4F, vec.z * 0.4F));
					}
				} else {
					for(int i = 0; i < 8; i++) {
						this.playSound(SoundRegistry.TAR_BEAST_STEP, 1F, (this.random.nextFloat() * 0.4F + 0.8F) * 0.8F);
					}
					List<EntityCreature> affectedEntities = (List<EntityCreature>)this.world.getEntitiesOfClass(EntityCreature.class, this.getBoundingBox().inflate(5.25F, 5.25F, 5.25F));
					for(EntityCreature e : affectedEntities) {
						if(e == this || e.getDistance(this) > 5.25F || !e.canSee(this) || e instanceof EntityTarminion) continue;
						double dst = e.getDistance(this);
						e.hurt(DamageSource.causeMobDamage(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 4);
						e.addEffect(new EffectInstance(Effects.SLOWNESS, (int)(20 + (1.0F - dst / 5.25F) * 150), 1));
					}
				}
			}

			if(!this.level.isClientSide()) {
				this.dropLoot(false, 0, DamageSource.GENERIC);
			}

			this.playSound(SoundRegistry.TAR_BEAST_STEP, 2.5F, 0.5F);

			if(this.level.isClientSide()) {
				for(int i = 0; i < 100; i++) {
					Random rnd = world.rand;
					float rx = rnd.nextFloat() * 1.0F - 0.5F;
					float ry = rnd.nextFloat() * 1.0F - 0.5F;
					float rz = rnd.nextFloat() * 1.0F - 0.5F;
					Vector3d vec = new Vector3d(rx, ry, rz);
					vec = vec.normalize();
					BLParticles.SPLASH_TAR.spawn(this.world, this.getX() + rx + 0.1F, this.getY() + ry, this.getZ() + rz + 0.1F, ParticleArgs.get().withMotion(vec.x * 0.2F, vec.y * 0.2F, vec.z * 0.2F));
				}
			}
		}

		super.remove();
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		super.attackEntityAsMob(entity);
		return attack(entity);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source == DamageSource.DROWN && this.world.getBlockState(new BlockPos(this.getX(), this.getY() + this.height, this.getZ())).getBlock() == BlockRegistry.TAR) {
			return false;
		}
		if(source.getTrueSource() instanceof EntityCreature) {
			this.attack(source.getTrueSource());
		}
		return super.hurt(source, amount);
	}

	protected boolean attack(Entity entity) {
		if (!this.level.isClientSide()) {
			if (this.onGround) {
				double dx = entity.getX() - this.getX();
				double dz = entity.getZ() - this.getZ();
				float dist = MathHelper.sqrt(dx * dx + dz * dz);
				this.motionX = dx / dist * 0.2D + this.motionX * 0.2D;
				this.motionZ = dz / dist * 0.2D + this.motionZ * 0.2D;
				this.motionY = 0.3D;
			}

			DamageSource damageSource;

			LivingEntity owner = this.getOwner();
			if(owner != null) {
				damageSource = new EntityDamageSourceIndirect("mob", this, owner);
			} else {
				damageSource = DamageSource.causeMobDamage(this);
			}

			entity.hurt(damageSource, (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());

			if(entity instanceof LivingEntity && this.level.random.nextInt(4) == 0) {
				//Set revenge target to tarminion so it can be attacked by the mob
				((LivingEntity) entity).setRevengeTarget(this);
			}

			this.playSound(SoundRegistry.TAR_BEAST_STEP, 1.0F, 2.0F);

			((LivingEntity) entity).addEffect(new EffectInstance(Effects.SLOWNESS, world.getDifficulty().getId() * 50, 0));

			return true;
		}
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	public void addParticles(World world, double x, double y, double z, Random rand) {
		for (int count = 0; count < 3; ++count) {
			double a = Math.toRadians(renderYawOffset);
			double offSetX = -Math.sin(a) * 0D + rand.nextDouble() * 0.1D - rand.nextDouble() * 0.1D;
			double offSetZ = Math.cos(a) * 0D + rand.nextDouble() * 0.1D - rand.nextDouble() * 0.1D;
			BLParticles.TAR_BEAST_DRIP.spawn(world , x + offSetX, y + 0.1D, z + offSetZ);
		}
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entity) {
		return null;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.TARMINION;
	}

	@Override
	protected boolean canDropLoot() {
		return false; //Loot dropping is handled in death update
	}

	@Override
	public void setDropItemsWhenDead(boolean dropWhenDead) {
		this.dropContentsWhenDead = dropWhenDead;
	}

	@Override
	public Entity changeDimension(int dimensionIn) {
		this.dropContentsWhenDead = false;
		return super.changeDimension(dimensionIn);
	}

	@Override
	public CompoundNBT returnToRing(UUID userId) {
		return this.save(new CompoundNBT());
	}
	
	@Override
	public boolean returnFromRing(Entity user, CompoundNBT nbt) {
		double prevX = this.getX();
		double prevY = this.getY();
		double prevZ = this.getZ();
		float prevYaw = this.yRot;
		float prevPitch = this.xRot;
		this.readFromNBT(nbt);
		this.moveTo(prevX, prevY, prevZ, prevYaw, prevPitch);
		
		if(this.isEntityAlive()) {
			//Still alive, just spawn in world
			this.world.addFreshEntity(this);
		} else {
			//Tarminion has died, spawn loot table
			if(this.world instanceof ServerWorld) {
				ServerWorld ServerWorld = (ServerWorld) this.world;

				LootTable lootTable = ServerWorld.getLootTableManager().getLootTableFromLocation(LootTableRegistry.TARMINION);
				LootContext.Builder contextBuilder = (new LootContext.Builder(ServerWorld)).withLootedEntity(this);

				for(ItemStack loot : lootTable.generateLootForPools(this.world.rand, contextBuilder.build())) {
					if(user instanceof PlayerEntity) {
						if(!((PlayerEntity) user).inventory.add(loot)) {
							((PlayerEntity) user).dropItem(loot, false);
						}
					} else {
						user.entityDropItem(loot, 0);
					}
				}
			}
		}

		return true;
	}

	@Override
	public boolean isRespawnedByAnimator() {
		//Instead spawns the inanimate tarminion item which then needs to be reanimated
		return false;
	}

	@Override
	public UUID getRingOwnerId() {
		return this.getOwnerId();
	}
}
