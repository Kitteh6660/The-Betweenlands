package thebetweenlands.common.entity;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import io.netty.buffer.PacketBuffer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;

public class EntityShockwaveBlock extends Entity implements IEntityAdditionalSpawnData {
	private static final DataParameter<String> OWNER_DW = EntityDataManager.<String>createKey(EntityShockwaveBlock.class, DataSerializers.STRING);

	public Block block;
	public int blockMeta;
	public int jumpDelay;
	public BlockPos origin;
	private double waveStartX, waveStartZ;

	public EntityShockwaveBlock(World world) {
		super(world);
		this.setSize(1.0F, 1.0F);
		this.setBlock(Blocks.STONE, 0);
		this.noClip = true;
	}

	public void setBlock(Block blockID, int blockMeta) {
		this.block = blockID;
		this.blockMeta = blockMeta;
	}

	public void setOwner(String ownerUUID) {
		entityData.set(OWNER_DW, ownerUUID);
	}

	public String getOwnerUUID() {
		return entityData.get(OWNER_DW);
	}

	public Entity getOwner() {
		try {
			UUID uuid = UUID.fromString(getOwnerUUID());
			return uuid == null ? null : getEntityByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	private Entity getEntityByUUID(UUID id) {
		for (int i = 0; i < world.loadedEntityList.size(); ++i) {
			Entity entity = (Entity)world.loadedEntityList.get(i);
			if (id.equals(entity.getUUID())) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public void tick() {
		this.world.profiler.startSection("entityBaseTick");

		this.xOld = this.getX();
		this.yOld = this.getY();
		this.zOld = this.getZ();
		this.motionX = 0;
		this.motionZ = 0;
		this.lastTickPosX = this.getX();
		this.lastTickPosY = this.getY();
		this.lastTickPosZ = this.getZ();

		if(this.tickCount >= this.jumpDelay) {
			if(this.tickCount == this.jumpDelay && this.motionY <= 0.0D) {
				this.motionY += 0.25D;
			} else {
				this.motionY -= 0.05D;

				if(!this.level.isClientSide() && (this.getY() <= this.origin.getY() || this.onGround || this.tickCount >= this.jumpDelay + 20)) {
					this.remove();
				}
			}
		} else {
			this.motionY = 0.0D;
		}

		if(this.getY() < -64.0D) {
			this.remove();
		}

		if(this.getY() + this.motionY <= this.origin.getY()) {
			this.motionY = 0.0D;
			this.moveTo(this.getX(), this.origin.getY(), this.getZ(), 0, 0);
		} else {
			this.move(MoverType.SELF, 0, this.motionY, 0);
		}

		if(this.motionY > 0.1D && !this.level.isClientSide()) {
			DamageSource damageSource;
			Entity owner = getOwner();
			if(owner instanceof LivingEntity) {
				if(owner instanceof PlayerEntity) {
					damageSource = new EntityDamageSourceIndirect("player", this, owner);
				} else {
					damageSource = DamageSource.causeIndirectDamage(this, (LivingEntity) owner);
				}
			} else {
				damageSource = new EntityDamageSource("bl.shockwave", this);
			}
			List<LivingEntity> entities = this.world.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.1D, 0.1D, 0.1D));
			for(LivingEntity entity : entities) {      
				if (entity != null) {                      
					if (entity instanceof LivingEntity && entity != getOwner()) { // needs null check on owner?
						if(entity.hurt(damageSource, 10F)) {
							float knockback = 1.5F;
							Vector3d dir = new Vector3d(this.getX() - this.waveStartX, 0, this.getZ() - this.waveStartZ);
							dir = dir.normalize();
							entity.motionX = dir.x * knockback;
							entity.motionY = 0.5D;
							entity.motionZ = dir.z * knockback;
							if (entity.getHealth() <= 0 && owner instanceof ServerPlayerEntity) {
								AdvancementCriterionRegistry.SHOCKWAVE_KILL.trigger((ServerPlayerEntity) owner, entity);
							}
						}
					}
				}
			}
		}

		this.firstUpdate = false;
		this.world.profiler.endSection();
	}

	@Override
	public void remove() {
		super.remove();
	}

	@Override
	public boolean handleWaterMovement() {
		return false;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return entityIn.getBoundingBox();
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.getBoundingBox();
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	@Override
	public void writeSpawnData(PacketBuffer data) {
		PacketBuffer buffer = new PacketBuffer(data);
		buffer.writeInt(Block.getIdFromBlock(this.block));
		buffer.writeInt(this.blockMeta);
		buffer.writeBlockPos(this.origin);
		buffer.writeInt(this.jumpDelay);
	}

	@Override
	public void readSpawnData(PacketBuffer data) {
		PacketBuffer buffer = new PacketBuffer(data);
		this.block = Block.getBlockById(buffer.readInt());
		this.blockMeta = buffer.readInt();
		this.origin = buffer.readBlockPos();
		this.jumpDelay = buffer.readInt();
	}

	public void setOrigin(BlockPos pos, int delay, double waveStartX, double waveStartZ, Entity source) {
		this.origin = pos;
		this.jumpDelay = delay;
		this.waveStartX = waveStartX;
		this.waveStartZ = waveStartZ;
		this.setOwner(source.getUUID().toString());
	}

	@Override
	protected void defineSynchedData() {
		this.getEntityData().register(OWNER_DW, "");
	}

	@Override
	public void load(CompoundNBT data) {
		this.block = Block.getBlockById(data.getInt("blockID"));
		if(this.block == null)
			this.block = Blocks.STONE;
		this.blockMeta = data.getInt("blockMeta");
		this.origin = new BlockPos(data.getInt("originX"), data.getInt("originY"), data.getInt("originZ"));
		this.waveStartX = data.getDouble("waveStartX");
		this.waveStartZ = data.getDouble("waveStartZ");
		this.jumpDelay = data.getInt("jumpDelay");
		setOwner(data.getString("ownerUUID"));
	}

	@Override
	public void save(CompoundNBT data) {
		data.putInt("blockID", Block.getIdFromBlock(this.block));
		data.putInt("blockMeta", this.blockMeta);
		data.putInt("originX", this.origin.getX());
		data.putInt("originY", this.origin.getY());
		data.putInt("originZ", this.origin.getZ());
		data.setDouble("waveStartX", this.waveStartX);
		data.setDouble("waveStartZ", this.waveStartZ);
		data.putInt("jumpDelay", this.jumpDelay);
		data.putString("ownerUUID", this.getOwnerUUID());
	}
}