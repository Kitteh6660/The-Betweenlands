package thebetweenlands.common.entity.mobs;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;

public class EntityFortressBossSpawner extends EntityMob implements IEntityBL {
	protected static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.<Optional<UUID>>createKey(EntityFortressBossSpawner.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	public int spawnDelay = 40;
	public final int maxSpawnDelay = 40;
	private Entity cachedOwner;

	public EntityFortressBossSpawner(World world) {
		super(world);
		this.setSize(0.4F, 0.4F);
	}

	public EntityFortressBossSpawner(World world, Entity source) {
		super(world);
		this.setOwner(source);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0D);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getDataManager().register(OWNER, Optional.absent());
	}

	public void setOwner(@Nullable Entity entity) {
		this.getDataManager().set(OWNER, entity == null ? Optional.absent() : Optional.of(entity.getUUID()));
	}

	@Nullable
	public UUID getOwnerUUID() {
		Optional<UUID> uuid = this.getDataManager().get(OWNER);
		return uuid.isPresent() ? uuid.get() : null;
	}

	@Nullable
	public Entity getOwner() {
		UUID uuid = this.getOwnerUUID();
		if(uuid == null) {
			this.cachedOwner = null;
		} else if(this.cachedOwner == null || !this.cachedOwner.isEntityAlive() || !this.cachedOwner.getUUID().equals(uuid)) {
			this.cachedOwner = null;
			for(Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().grow(64.0D, 64.0D, 64.0D))) {
				if(entity.getUUID().equals(uuid)) {
					this.cachedOwner = entity;
					break;
				}
			}
		}
		return this.cachedOwner;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putInt("spawnDelay", this.spawnDelay);
		if(this.getOwnerUUID() != null) {
			nbt.putUUID("owner", this.getOwnerUUID());
		}
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		this.spawnDelay = nbt.getInt("spawnDelay");
		if(nbt.hasUUID("owner")) {
			this.getDataManager().set(OWNER, Optional.of(nbt.getUUID("owner")));
		} else {
			this.getDataManager().set(OWNER, Optional.absent());
		}
	}

	@Override
	public void tick() {
		if(!this.level.isClientSide() && (this.world.getDifficulty() == EnumDifficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isEntityAlive()))) {
			this.remove();
			return;
		}

		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;

		super.tick();

		if(this.level.isClientSide()) {
			Entity owner = this.getOwner();
			if(owner != null) {
				for(int i = 0; i < 3; i++) {
					double sx = this.getX() + (this.random.nextDouble() - 0.5D) * (double)this.width;
					double sy = this.getY() + this.random.nextDouble() * (double)this.height - 0.25D;
					double sz = this.getZ() + (this.random.nextDouble() - 0.5D) * (double)this.width;
					double ex = owner.getX() + (this.random.nextDouble() - 0.5D) * (double)owner.width;
					double ey = owner.getY() + this.random.nextDouble() * (double)owner.height - 0.25D;
					double ez = owner.getZ() + (this.random.nextDouble() - 0.5D) * (double)owner.width;
					this.world.spawnParticle(EnumParticleTypes.PORTAL, sx, sy, sz, ex - sx, ey - sy, ez - sz);
				}
			}
		}

		if(this.spawnDelay > 0) {
			this.spawnDelay--;
		} else {
			if(!this.level.isClientSide()) {
				EntityWight wight = new EntityWight(this.world);
				wight.moveTo(this.getX(), this.getY(), this.getZ(), 0, 0);
				wight.setCanTurnVolatile(false);
				wight.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0D);
				wight.setHealth(wight.getMaxHealth());
				if(this.getOwner() instanceof MobEntity) {
					wight.setAttackTarget(((MobEntity)this.getOwner()).getAttackTarget());
				}
				this.world.spawnEntity(wight);
				this.remove();
			} else {
				for(int i = 0; i < 6; i++) {
					this.spawnVolatileParticles();
				}
				this.remove();
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnVolatileParticles() {
		final double radius = 0.3F;
		final double cx = this.getX();
		final double cy = this.getY() + 0.35D;
		final double cz = this.getZ();
		for(int i = 0; i < 8; i++) {
			double px = this.world.rand.nextFloat() * 0.7F;
			double py = this.world.rand.nextFloat() * 0.7F;
			double pz = this.world.rand.nextFloat() * 0.7F;
			Vector3d vec = new Vector3d(px, py, pz).subtract(new Vector3d(0.35F, 0.35F, 0.35F)).normalize();
			px = cx + vec.x * radius;
			py = cy + vec.y * radius;
			pz = cz + vec.z * radius;
			BLParticles.STEAM_PURIFIER.spawn(this.world, px, py, pz);
		}
	}

	@Override
	public void travel(float strafe, float up,  float forward) {
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		return false;
	}

	@Override
	protected void collideWithEntity(Entity entityIn) { }
}
