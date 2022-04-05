package thebetweenlands.common.entity;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityResurrection extends Entity {
	protected static final byte EVENT_RESPAWN = 80;

	private Supplier<Vector3d> positionSupplier;
	private CompoundNBT entityNbt;
	private int timer;
	private int resurrectionTime;

	private boolean respawning = false;

	public EntityResurrection(World worldIn) {
		super(worldIn);
		this.setSize(0.1f, 0.1f);
	}

	public EntityResurrection(World world, CompoundNBT entityNbt, Supplier<Vector3d> positionSupplier, int resurrectionTime) {
		this(world);
		this.entityNbt = entityNbt;
		this.positionSupplier = positionSupplier;
		this.resurrectionTime = resurrectionTime;
	}

	@Override
	protected void defineSynchedData() {

	}

	@Override
	public void load(CompoundNBT compound) {
		this.timer = compound.getInt("respawnTimer");
		this.entityNbt = compound.getCompound("entityNbt");
		this.resurrectionTime = compound.getInt("resurrectionTime");
	}

	@Override
	public void save(CompoundNBT compound) {
		compound.putInt("respawnTimer", this.timer);
		compound.put("entityNbt", this.entityNbt);
		compound.putInt("resurrectionTime", this.resurrectionTime);
	}

	@Override
	public void tick() {
		super.tick();

		if(!this.level.isClientSide()) {
			if(this.positionSupplier != null) {
				Vector3d newPosition = this.positionSupplier.get();

				if(newPosition != null) {
					this.moveTo(newPosition.x, newPosition.y, newPosition.z, this.yRot, this.xRot);
				}
			}

			if(this.timer++ > this.resurrectionTime - 20) {
				this.level.setEntityState(this, EVENT_RESPAWN);

				if(this.timer > this.resurrectionTime) {
					if(this.level instanceof ServerWorld) {
						Entity entity = EntityList.createEntityFromNBT(this.entityNbt, this.level);

						if(entity != null) {
							if(entity.isNonBoss() && ((ServerWorld) this.level).getEntityFromUuid(entity.getUUID()) == null) {
								if(entity instanceof LivingEntity) {
									LivingEntity living = (LivingEntity) entity;

									living.setHealth(Math.max(living.getHealth(), living.getMaxHealth() * 0.5f));
								}

								entity.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);

								entity.motionX = entity.motionY = entity.motionZ = 0;

								this.level.addFreshEntity(entity);

								this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundRegistry.RESURRECTION, SoundCategory.BLOCKS, 1, 1);
							} else {
								entity.remove();
							}
						}
					}

					this.remove();
				}
			}
		} else if(this.respawning) {
			this.addParticles();
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_RESPAWN) {
			this.respawning = true;
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void addParticles() {
		for(int i = 0; i < 3; i++) {
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, 
					BLParticles.SMOOTH_SMOKE.create(this.level, this.getX() + (this.level.random.nextFloat() - 0.5f) * 2, this.getY() + 1 + (this.level.random.nextFloat() - 0.5f) * 2, this.getZ() + (this.level.random.nextFloat() - 0.5f) * 2,
							ParticleArgs.get()
							.withMotion((this.level.random.nextFloat() - 0.5f) * 0.1f, (this.level.random.nextFloat() - 0.5f) * 0.1f, (this.level.random.nextFloat() - 0.5f) * 0.1f)
							.withScale(16)
							.withColor(1, 1, 1, 0.5f)
							.withData(40, true, 0.0F, true)));
		}
	}
}
