package thebetweenlands.common.entity.projectiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import thebetweenlands.common.entity.mobs.EntityDreadfulMummy;
import thebetweenlands.common.entity.mobs.EntityMultipartDummy;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;

public class EntitySludgeBall extends EntityThrowable {
	private int bounces = 0;
	private String ownerUUID;
	private boolean breakBlocks;

	public EntitySludgeBall(World world) {
		super(world);
		this.setSize(0.75F, 0.75F);
		this.ownerUUID = "";
		this.breakBlocks = false;
	}

	public EntitySludgeBall(World world, LivingEntity owner, boolean breakBlocks) {
		this(world);
		this.ownerUUID = owner.getUUID().toString();
		this.thrower = owner;
		this.breakBlocks = breakBlocks;
	}

	public Entity getOwner() {
		try {
			UUID uuid = UUID.fromString(this.ownerUUID);
			return uuid == null ? null : this.getEntityByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	private Entity getEntityByUUID(UUID uuid) {
		for (int i = 0; i < this.level.loadedEntityList.size(); ++i) {
			Entity entity = (Entity)this.level.loadedEntityList.get(i);
			if (uuid.equals(entity.getUUID())) {
				return entity;
			}
		}
		return null;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
	}

	@Override
	public void tick() {
		double prevMotionX = this.motionX;
		double prevMotionY = this.motionY;
		double prevMotionZ = this.motionZ;
		double prevX = this.getX();
		double prevY = this.getY();
		double prevZ = this.getZ();
		move(MoverType.SELF,this.motionX, this.motionY, this.motionZ);
		if(!this.level.isClientSide() && new Vector3d(prevX - this.getX(), prevY - this.getY(), prevZ - this.getZ()).lengthSqr() < 0.001D && this.tickCount > 10) {
			this.explode();
		}
		this.motionX = prevMotionX;
		this.motionY = prevMotionY;
		this.motionZ = prevMotionZ;
		this.setPosition(prevX, prevY, prevZ);
		
		super.tick();
	}
	
	@Override
	public void move(MoverType type, double x, double y, double z) {
		if(this.tickCount < 2) {
			//Stupid EntityTrackerEntry is broken and desyncs server position.
			//Tracker updates server side position but *does not* send the change to the client
			//when tracker.updateCounter == 0, causing a desync until the next force teleport
			//packet.......
			//By not moving the entity until then it works.
			return;
		}
		
		super.move(type, x, y, z);
	}

	@Override
	protected void onImpact(RayTraceResult collision) {
		if(collision.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockState state = level.getBlockState(collision.getBlockPos());
			
			if (state.getBlock().canCollideCheck(state, false)) {
				List<AxisAlignedBB> aabbs = new ArrayList<>();
				state.addCollisionBoxToList(this.world, collision.getBlockPos(), this.getBoundingBox().offset(this.motionX, this.motionY, this.motionZ), aabbs, this, true);
				
				if(!aabbs.isEmpty()) {
					if(!this.level.isClientSide() && ForgeEventFactory.getMobGriefingEvent(this.world, this) && this.bounces == 0) {
						Entity owner = this.getOwner();
						
						if(owner instanceof LivingEntity && this.getY() > owner.getBoundingBox().maxY && this.motionY > 0.1D) {
							BlockPos pos = collision.getBlockPos();
							BlockState hitState = this.world.getBlockState(pos);
							float hardness = hitState.getBlockHardness(this.world, pos);
							
							if(!hitState.getBlock().isAir(hitState, this.world, pos) && hardness >= 0 && hardness <= 2.5F
									&& hitState.getBlock().canEntityDestroy(hitState, this.world, pos, (LivingEntity) owner)
									&& ForgeEventFactory.onEntityDestroyBlock((LivingEntity) owner, pos, hitState)) {
								this.world.levelEvent(2001, pos, Block.getStateId(hitState));
								this.world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
								
								explode();
							}
						}
					}
					
					if(Math.abs(this.motionY) <= 0.001) {
						if(this.level.isClientSide())
							this.motionX = this.motionY = this.motionZ = 0.0D;
						else 
							explode();
					}
					
					if (collision.sideHit.getAxis() == Axis.Y) {
						this.motionY *= -0.9D;
						this.velocityChanged = true;
						this.bounces++;
						if (this.bounces >= 3) {
							if(this.level.isClientSide())
								this.motionX = this.motionY = this.motionZ = 0.0D;
							else 
								explode();
						} else {
							level.playSound(null, getPosition(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.HOSTILE, 1, 0.9f);
							spawnBounceParticles(8);
						}
					} else if (collision.sideHit.getAxis() == Axis.Z) {
						this.motionZ *= -0.9D;
						this.velocityChanged = true;
						this.bounces++;
						if(this.bounces >= 3) {
							if(this.level.isClientSide())
								this.motionX = this.motionY = this.motionZ = 0.0D;
							else 
								explode();
						}
					} else if (collision.sideHit.getAxis() == Axis.X) {
						this.motionX *= -0.9D;
						this.velocityChanged = true;
						this.bounces++;
						if(this.bounces >= 3) {
							if(this.level.isClientSide())
								this.motionX = this.motionY = this.motionZ = 0.0D;
							else 
								explode();
						}
					}
				}
			}
		}
		
		if (collision.typeOfHit == RayTraceResult.Type.ENTITY) {
			if(collision.entityHit != this.thrower && !(collision.entityHit instanceof EntitySludgeBall) && !(collision.entityHit instanceof MultiPartEntityPart) &&  !(collision.entityHit instanceof EntityMultipartDummy) && !(collision.entityHit instanceof EntityPeatMummy) && !(collision.entityHit instanceof EntityDreadfulMummy)) {
				if(this.attackEntity(collision.entityHit)) {
					explode();
				} else {
					this.motionX *= -0.1D;
	                this.motionY *= -0.1D;
	                this.motionZ *= -0.1D;
	                this.bounces++;
				}
			}
		}
	}

	private void explode() {
		if(!this.level.isClientSide()) {
			float radius = 3;
			AxisAlignedBB region = new AxisAlignedBB(this.getX() - radius, this.getY() - radius, this.getZ() - radius, this.getX() + radius, this.getY() + radius, this.getZ() + radius);
			List<Entity> entities = this.level.getEntitiesWithinAABBExcludingEntity(this, region);
			double radiusSq = radius * radius;
			for (Entity entity : entities) {
				if (entity instanceof LivingEntity && !(entity instanceof EntityPeatMummy) && !(entity instanceof EntityDreadfulMummy) && getDistanceSq(entity) < radiusSq) {
					this.attackEntity(entity);
				}
			}
			level.playSound(null, getPosition(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.HOSTILE, 1, 0.5f);
			level.playSound(null, getPosition(), SoundEvents.ENTITY_SMALL_SLIME_SQUISH, SoundCategory.HOSTILE, 1, 0.5f);
			remove();
		} else {
			//TODO Better explosion particle effects
			spawnBounceParticles(20);
		}
	}

	private boolean attackEntity(Entity entity) {
		boolean attacked = false;
		Entity owner = this.getOwner();
		if (owner != null) {
			attacked = entity.hurt(new EntityDamageSourceIndirect("mob", this, owner).setProjectile(), 8);
		} else {
			attacked = entity.hurt(new EntityDamageSource("entity", this).setProjectile(), 8);
		}
		if(!this.level.isClientSide() && attacked && entity instanceof LivingEntity) {
			((LivingEntity) entity).addEffect(new EffectInstance(Effects.SLOWNESS, 80, 3));
		}
		return attacked;
	}
	
	@Override
	protected float getGravityVelocity() {
		return 0.08F;
	}

	private void spawnBounceParticles(int amount) {
		for (int i = 0; i <= amount; i++) {
			this.level.addParticle(ParticleTypes.SLIME, this.getX() + (amount/8) * (this.random.nextFloat() - 0.5), this.getY() + 0.3, this.getZ() + (amount/8) * (this.random.nextFloat() - 0.5), 0, 0, 0);
		}
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putInt("bounces", this.bounces);
		nbt.putString("ownerUUID", this.ownerUUID);
		nbt.putBoolean("breakBlocks", this.breakBlocks);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		this.bounces = nbt.getInt("bounces");
		this.ownerUUID = nbt.getString("ownerUUID");
		this.breakBlocks = nbt.getBoolean("breakBlocks");
	}
}
