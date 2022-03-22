package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.client.audio.TeleporterSound;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;

public class EntityFortressBossTeleporter extends Entity implements IEntityScreenShake {
	protected static final DataParameter<Integer> TARGET_ID = EntityDataManager.<Integer>createKey(EntityFortressBossTeleporter.class, DataSerializers.VARINT);

	private Vector3d teleportDestination = Vector3d.ZERO;
	private BlockPos bossSpawnPosition = BlockPos.ORIGIN;

	private PlayerEntity target = null;

	private int teleportTicks = 0;
	private final int maxTeleportTicks = 75;

	public boolean isLookingAtPlayer = false;

	private boolean spawnedBoss = false;

	public EntityFortressBossTeleporter(World world) {
		super(world);
		setSize(1F, 1F);
	}

	@Override
	protected void defineSynchedData() {
		this.getDataManager().register(TARGET_ID, -1);
	}

	@Override
	public void applyEntityCollision(Entity entity) {
	}


	@Override
	public void tick() {
		super.tick();

		double radius = 6.0D;
		double lookRadius = 8.0D;

		if(!this.level.isClientSide()) {
			if(this.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
				if(this.target == null) {
					AxisAlignedBB checkAABB = new AxisAlignedBB(this.getX()-radius, this.getY()-radius, this.getZ()-radius, this.getX()+radius, this.getY()+radius, this.getZ()+radius);
					List<PlayerEntity> players = this.world.getEntitiesOfClass(PlayerEntity.class, checkAABB);
					PlayerEntity closestPlayer = null;
					for(PlayerEntity player : players) {
						if((closestPlayer == null || player.getDistance(this) < closestPlayer.getDistance(this)) && player.getDistance(this) < radius && player.canEntityBeSeen(this)) {
							Vector3d playerLook = player.getLook(1.0F).normalize();
							Vector3d vecDiff = new Vector3d(this.getX() - player.getX(), this.getBoundingBox().minY + (double)(this.height / 2.0F) - (player.getY() + (double)player.getEyeHeight()), this.getZ() - player.getZ());
							double dist = vecDiff.length();
							vecDiff = vecDiff.normalize();
							double angle = playerLook.dotProduct(vecDiff);
							if(angle > 1.0D - 0.01D / dist)
								closestPlayer = player;
						}
					}
					if(closestPlayer != null)
						this.target = closestPlayer;
				} else {
					if(this.target.getDistance(this) > radius) {
						this.target = null;
					} else {
						Vector3d playerLook = this.target.getLook(1.0F).normalize();
						Vector3d vecDiff = new Vector3d(this.getX() - this.target.getX(), this.getBoundingBox().minY + (double)(this.height / 2.0F) - (this.target.getY() + (double)this.target.getEyeHeight()), this.getZ() - this.target.getZ());
						double dist = vecDiff.length();
						vecDiff = vecDiff.normalize();
						double angle = playerLook.dotProduct(vecDiff);
						if(angle <= 1.0D - (0.01D + Math.pow(this.getTeleportProgress(), 3) / 10.0D) / dist)
							this.target = null;
					}
				}
			} else {
				this.target = null;
			}

			if(this.target == null) {
				this.getDataManager().set(TARGET_ID, -1);
			} else {
				this.getDataManager().set(TARGET_ID, this.target.getEntityId());
			}
		} else {
			Entity prevTarget = this.target;
			Entity target = this.world.getEntityByID(this.getDataManager().get(TARGET_ID));
			if(target instanceof PlayerEntity) {
				if(this.target == null) {
					for(int i = 0; i < 60; i++) {
						this.spawnSmokeParticle(this.getX(), this.getY() + this.height / 2.0D, this.getZ(), (this.world.rand.nextFloat() - 0.5F) / 2.5F, (this.world.rand.nextFloat() - 0.5F) / 2.5F, (this.world.rand.nextFloat() - 0.5F) / 2.5F);
					}
				}
				this.target = (PlayerEntity) target;
			} else {
				this.target = null;
			}
			if(this.target != null && prevTarget != this.target && this.level.isClientSide()) {
				this.playTeleportSound();
			}
		}

		if(this.target != null) {
			this.faceEntity(this.target);

			this.teleportTicks++;

			if(!this.level.isClientSide() && this.teleportTicks > this.maxTeleportTicks) {
				//Teleport
				if(this.target instanceof ServerPlayerEntity) {
					ServerPlayerEntity player = (ServerPlayerEntity) this.target;
					player.dismountRidingEntity();
					player.connection.setPlayerLocation(this.teleportDestination.x, this.teleportDestination.y, this.teleportDestination.z, player.yRot, player.xRot);
				} else {
					this.target.dismountRidingEntity();
					this.target.moveTo(this.teleportDestination.x, this.teleportDestination.y, this.teleportDestination.z, this.target.yRot, this.target.xRot);
				}
				if(!this.spawnedBoss) {
					this.spawnBoss();
					this.spawnedBoss = true;
				}
				this.target = null;
				this.teleportTicks = 0;
			} else if(this.level.isClientSide() && this.world.rand.nextInt(2) == 0) {
				double rx = (double)(this.world.rand.nextFloat());
				double ry = (double)(this.world.rand.nextFloat());
				double rz = (double)(this.world.rand.nextFloat());
				double len = Math.sqrt(rx*rx+ry*ry+rz*rz);
				this.spawnSmokeParticle((float)this.getX() - this.width / 2.0F + rx, (float)this.getY() + ry, (float)this.getZ() - this.width / 2.0F + rz, 
						(rx-0.5D)/len*0.2D, (ry-0.5D)/len*0.2D, (rz-0.5D)/len*0.2D);
			}
		} else {
			this.teleportTicks = 0;
			AxisAlignedBB checkAABB = new AxisAlignedBB(this.getX()-lookRadius, this.getY()-lookRadius, this.getZ()-lookRadius, this.getX()+lookRadius, this.getY()+lookRadius, this.getZ()+lookRadius);
			List<PlayerEntity> players = this.world.getEntitiesOfClass(PlayerEntity.class, checkAABB);
			PlayerEntity closestPlayer = null;
			for(PlayerEntity player : players) {
				if((closestPlayer == null || player.getDistance(this) < closestPlayer.getDistance(this)) && player.getDistance(this) < lookRadius && player.canEntityBeSeen(this)) {
					closestPlayer = player;
				}
			}
			if(closestPlayer != null) {
				this.faceEntity(closestPlayer);
				if(this.level.isClientSide()) {
					if(!this.isLookingAtPlayer) {
						for(int i = 0; i < 10; i++) {
							this.spawnSmokeParticle(this.getX(), this.getY() + this.height / 2.0D, this.getZ(), (this.world.rand.nextFloat() - 0.5F) / 2.5F, (this.world.rand.nextFloat() - 0.5F) / 2.5F, (this.world.rand.nextFloat() - 0.5F) / 2.5F);
						}
					}
				}
				this.isLookingAtPlayer = true;
			} else {
				this.isLookingAtPlayer = false;
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void playTeleportSound() {
		Minecraft.getInstance().getSoundHandler().playSound(new TeleporterSound(this, this.getTarget()));
	}

	public void faceEntity(Entity target) {
		double dx = target.getX() - this.getX();
		double dz = target.getZ() - this.getZ();
		double dy;
		if (target instanceof LivingEntity) {
			LivingEntity entitylivingbase = (LivingEntity)target;
			double actualPosY = entitylivingbase.getY();
			dy = actualPosY + (double)entitylivingbase.getEyeHeight() - (this.getY() + (double)this.getEyeHeight());
		} else {
			dy = (target.getBoundingBox().minY + target.getBoundingBox().maxY) / 2.0D - (this.getY() + (double)this.getEyeHeight());
		}
		double dist = (double)MathHelper.sqrt(dx * dx + dz * dz);
		float yaw = (float)(Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float)(-(Math.atan2(dy, dist) * 180.0D / Math.PI));
		this.xRot = this.prevRotationPitch = pitch;
		this.yRot = this.prevRotationYaw = yaw;
	}

	public float getTeleportProgress() {
		return this.teleportTicks / (float)this.maxTeleportTicks;
	}

	public PlayerEntity getTarget() {
		return this.target;
	}

	@Override
	public void load(CompoundNBT nbt) {
		double dx = nbt.getDouble("destinationX");
		double dy = nbt.getDouble("destinationY");
		double dz = nbt.getDouble("destinationZ");
		this.teleportDestination = new Vector3d(dx, dy, dz);
		double sx = nbt.getDouble("bossSpawnX");
		double sy = nbt.getDouble("bossSpawnY");
		double sz = nbt.getDouble("bossSpawnZ");
		this.bossSpawnPosition = new BlockPos(sx, sy, sz);
		this.spawnedBoss = nbt.getBoolean("spawnedBoss");
	}

	@Override
	public void save(CompoundNBT nbt) {
		if(this.teleportDestination != null) {
			nbt.setDouble("destinationX", this.teleportDestination.x);
			nbt.setDouble("destinationY", this.teleportDestination.y);
			nbt.setDouble("destinationZ", this.teleportDestination.z);
		}
		if(this.bossSpawnPosition != null) {
			nbt.setDouble("bossSpawnX", this.bossSpawnPosition.getX());
			nbt.setDouble("bossSpawnY", this.bossSpawnPosition.getY());
			nbt.setDouble("bossSpawnZ", this.bossSpawnPosition.getZ());
		}
		nbt.putBoolean("spawnedBoss", this.spawnedBoss);
	}

	public void setTeleportDestination(Vector3d destination) {
		this.teleportDestination = destination;
	}

	public Vector3d getTeleportDestination() {
		return this.teleportDestination;
	}

	public void setBossSpawnPosition(BlockPos position) {
		this.bossSpawnPosition = position;
	}

	public BlockPos getBossSpawnPosition() {
		return this.bossSpawnPosition;
	}

	protected void spawnBoss() {
		EntityFortressBoss boss = new EntityFortressBoss(this.world);
		boss.setPosition(this.bossSpawnPosition.getX() + 0.5D, this.bossSpawnPosition.getY() + 0.5D, this.bossSpawnPosition.getZ() + 0.5D);
		boss.setAnchor(this.bossSpawnPosition, 6.0D);
		this.world.spawnEntity(boss);
	}

	@Override
	public float getShakeIntensity(Entity viewer, float partialTicks) {
		if(this.getTarget() == viewer)
			return (float)Math.pow(this.getTeleportProgress(), 3) / 2.0F;
		return 0.0F;
	}

	@OnlyIn(Dist.CLIENT)
	protected void spawnSmokeParticle(double x, double y, double z, double mx, double my, double mz) {
		BLParticles.PORTAL.spawn(this.world, x, y, z, ParticleArgs.get().withMotion(mx, my, mz));
	}
}

