package thebetweenlands.common.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.registries.ItemRegistry;

public class EntitySpiritTreeFaceMask extends HangingEntity implements IEntityAdditionalSpawnData {
	
	public static enum Type {
		LARGE, SMALL;
	}

	private Type type;

	public EntitySpiritTreeFaceMask(World world) {
		super(world);
	}

	public EntitySpiritTreeFaceMask(World world, BlockPos pos, Direction facing, Type type) {
		super(world, pos);
		this.type = type;
		this.updateFacingWithBoundingBox(facing);
	}

	public Type getType() {
		return this.type;
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putInt("type", this.type.ordinal());
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		this.type = Type.values()[nbt.getInt("type")];
	}

	@Override
	public int getWidth() {
		return this.type == Type.LARGE ? 32 : 16;
	}

	@Override
	public int getHeight() {
		return this.type == Type.LARGE ? 32 : 16;
	}

	@Override
	public void dropItem(Entity brokenEntity) {
		if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			this.playSound(SoundEvents.WOOD_BREAK, 1.0F, 1.0F);

			if (brokenEntity instanceof PlayerEntity) {
				PlayerEntity entityplayer = (PlayerEntity)brokenEntity;

				if (entityplayer.isCreative()) {
					return;
				}
			}

			this.entityDropItem(new ItemStack(this.type == Type.LARGE ? ItemRegistry.SPIRIT_TREE_FACE_LARGE_MASK : ItemRegistry.SPIRIT_TREE_FACE_SMALL_MASK), 0.0F);
		}
	}

	@Override
	public void playPlacementSound() {
		this.playSound(SoundEvents.WOOD_PLACE, 1.0F, 1.0F);
	}

	@Override
	public void moveTo(double x, double y, double z, float yaw, float pitch)  {
		this.setPosition(x, y, z);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		this.setPosition((double)this.hangingPosition.getX(), (double)this.hangingPosition.getY(), (double)this.hangingPosition.getZ());
	}

	@Override
	public void writeSpawnData(PacketBuffer buf) {
		buf.writeInt(this.type.ordinal());
		buf.writeLong(this.hangingPosition.toLong());
		buf.writeBoolean(this.facingDirection != null);
		if(this.facingDirection != null) {
			buf.writeInt(this.facingDirection.getIndex());
		}
	}

	@Override
	public void readSpawnData(PacketBuffer buf) {
		this.type = Type.values()[buf.readInt()];
		this.hangingPosition = BlockPos.of(buf.readLong());
		if(buf.readBoolean()) {
			this.facingDirection = Direction.byIndex(buf.readInt());
			this.updateFacingWithBoundingBox(this.facingDirection);
		}
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}