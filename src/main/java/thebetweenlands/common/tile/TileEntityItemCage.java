package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import thebetweenlands.common.entity.EntitySwordEnergy;


public class TileEntityItemCage extends TileEntity implements ITickableTileEntity {

	public TileEntityItemCage(TileEntityType<?> typeIn) {
		super(typeIn);
	}

	public byte type; // type will be used for each sword part rendering
	public boolean canBreak;

	@Override
	public void tick() {
		if (!level.isClientSide()) {
			if (isBlockOccupied() != null) {
				if (!canBreak) { setCanBeBroken(true); }
			}
			if (isBlockOccupied() == null) {
				if (canBreak) { setCanBeBroken(false); }
			}
		}
	}

	public void setCanBeBroken(boolean isBreakable) {
		canBreak = isBreakable;
		level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
	}

	public void setType(byte blockType) {
		type = blockType;
		level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
	}

	protected Entity isBlockOccupied() {
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(worldPosition.getX() + 0.25D, worldPosition.getY() - 3D, worldPosition.getZ() + 0.25D, worldPosition.getX() + 0.75D, worldPosition.getY(), worldPosition.getZ() + 0.75D));
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity != null)
				if (entity instanceof PlayerEntity)
					return entity;
		}
		return null;
	}

	public Entity isSwordEnergyBelow() {
		List<EntitySwordEnergy> list = level.getEntitiesOfClass(EntitySwordEnergy.class, new AxisAlignedBB(worldPosition.getX() - 9D, worldPosition.getY() - 2D, worldPosition.getZ() - 9D, worldPosition.getX() + 10D, worldPosition.getY() + 3D, worldPosition.getZ() + 10D));
		for (int i = 0; i < list.size(); i++) {
			EntitySwordEnergy entity = list.get(i);
			if (entity != null)
				return entity;
		}
		return null;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putBoolean("canBreak", canBreak);
		nbt.putByte("type", type);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		canBreak = nbt.getBoolean("canBreak");
		type = nbt.getByte("type");
	}

	@Override
    public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();
        return save(nbt);
    }

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		save(nbt);
		return new SUpdateTileEntityPacket(worldPosition, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		load(this.getBlockState(), packet.getTag());
	}
}