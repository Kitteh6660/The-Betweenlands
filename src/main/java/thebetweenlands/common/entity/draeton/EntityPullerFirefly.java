package thebetweenlands.common.entity.draeton;

import java.util.UUID;

import io.netty.buffer.PacketBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.api.entity.IPullerEntity;
import thebetweenlands.common.entity.mobs.EntityFirefly;

public class EntityPullerFirefly extends EntityFirefly implements IPullerEntity, IEntityAdditionalSpawnData {
	private int carriageId;
	private int pullerId;

	private DraetonPhysicsPart puller;

	public EntityPullerFirefly(World world) {
		super(world);
	}

	public EntityPullerFirefly(World world, EntityDraeton carriage, DraetonPhysicsPart puller) {
		super(world);
		this.setPuller(carriage, puller);
	}

	@Override
	public String getName() {
		return I18n.get("entity.thebetweenlands.firefly.name");
	}

	@Override
	public EntityDraeton getCarriage() {
		Entity entity = this.world.getEntityByID(this.carriageId);
		return entity instanceof EntityDraeton ? (EntityDraeton)entity : null;
	}
	
	@Override
	public void setPuller(EntityDraeton carriage, DraetonPhysicsPart puller) {
		this.puller = puller;
		this.pullerId = puller.id;
		this.carriageId = carriage.getEntityId();
	}

	@Override
	public float getPull(float pull) {
		return pull * 0.15f;
	}

	@Override
	public float getCarriageDrag(float drag) {
		return 1 - (1 - drag) * 0.5f;
	}

	@Override
	public float getDrag(float drag) {
		return drag;
	}

	@Override
	public Entity createReleasedEntity() {
		EntityFirefly entity = new EntityFirefly(this.world);
		entity.readFromNBT(this.save(new CompoundNBT()));
		entity.setNoAI(false);
		entity.putUUID(UUID.randomUUID());
		entity.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
		return entity;
	}
	
	@Override
	public void spawnReleasedEntity() {
		if(!this.level.isClientSide()) {
			this.world.spawnEntity(this.createReleasedEntity());

			this.remove();
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source == DamageSource.IN_WALL) {
			return false;
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean writeToNBTOptional(CompoundNBT compound) {
		//Entity is saved and handled by carriage
		return false;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean isAIDisabled() {
		if(this.puller != null) {
			return this.puller.carriage.isControlling(this.puller);
		}
		return true;
	}

	@Override
	public void tick() {
		super.tick();

		if(this.puller == null || !this.puller.isActive) {
			if(!this.level.isClientSide()) {
				//Don't remove immediately if entity is already dying
				if(this.isEntityAlive()) {
					this.remove();
				}
			} else {
				Entity entity = this.world.getEntityByID(this.carriageId);
				if(entity instanceof EntityDraeton) {
					DraetonPhysicsPart puller = ((EntityDraeton) entity).getPhysicsPartById(this.pullerId);
					if(puller != null) {
						this.puller = puller;
						puller.setEntity(this);
					}
				}
			}
		} else {
			if(this.puller.carriage.isControlling(this.puller)) {
				this.setPositionAndRotation(this.puller.x, this.puller.y, this.puller.z, 0, 0);
				this.yRot = this.rotationYawHead = this.renderYawOffset = (float)Math.toDegrees(Math.atan2(this.puller.motionZ, this.puller.motionX)) - 90;
				this.xRot = (float)Math.toDegrees(-Math.atan2(this.puller.motionY, Math.sqrt(this.puller.motionX * this.puller.motionX + this.puller.motionZ * this.puller.motionZ)));
				this.onGround = false;
			} else {
				this.puller.x = this.getX();
				this.puller.y = this.getY();
				this.puller.z = this.getZ();
			}
		}
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeInt(this.carriageId);
		buffer.writeInt(this.pullerId);
	}

	@Override
	public void readSpawnData(PacketBuffer buffer) {
		this.carriageId = buffer.readInt();
		this.pullerId = buffer.readInt();
	}
}