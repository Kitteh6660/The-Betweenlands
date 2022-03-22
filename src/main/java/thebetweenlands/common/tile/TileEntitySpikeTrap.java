package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.block.misc.BlockSludge;
import thebetweenlands.common.block.structure.BlockSpikeTrap;
import thebetweenlands.common.registries.SoundRegistry;

public class TileEntitySpikeTrap extends TileEntity implements ITickable {

	public int prevAnimationTicks;
	public int animationTicks;
	public boolean active;
	public byte type;

	@Override
	public void update() {
		if (!getWorld().isClientSide()) {
			BlockState state = getWorld().getBlockState(getPos());
			Direction facing = state.getValue(BlockSpikeTrap.FACING);

			BlockState stateFacing = getWorld().getBlockState(getPos().offset(facing, 1));
			if (stateFacing.getBlock() != Blocks.AIR && stateFacing.getBlockHardness(getWorld(), getPos().offset(facing, 1)) >= 0.0F && !(stateFacing.getBlock() instanceof BlockSludge)) {
				setType((byte) 1);
				setActive(true);
				Block block = stateFacing.getBlock();
				getWorld().playEvent(null, 2001, getPos().offset(facing, 1), Block.getIdFromBlock(block));
				block.dropBlockAsItem(getWorld(), getPos().offset(facing, 1), getWorld().getBlockState(getPos().offset(facing, 1)), 0);
				getWorld().setBlockToAir(getPos().offset(facing, 1));
			}
			BlockState stateFacing2 = getWorld().getBlockState(getPos().offset(facing, 2));
			if (stateFacing2.getBlock() != Blocks.AIR && stateFacing2.getBlockHardness(getWorld(), getPos().offset(facing, 2)) >= 0.0F && !(stateFacing.getBlock() instanceof BlockSludge)) {
				setType((byte) 1);
				setActive(true);
				Block block = stateFacing2.getBlock();
				getWorld().playEvent(null, 2001, getPos().offset(facing, 2), Block.getIdFromBlock(block));
				block.dropBlockAsItem(getWorld(), getPos().offset(facing, 2), getWorld().getBlockState(getPos().offset(facing, 2)), 0);
				getWorld().setBlockToAir(getPos().offset(facing, 2));
			}
			if (getWorld().rand.nextInt(500) == 0) {
				if (type != 0 && !active && animationTicks == 0)
					setType((byte) 0);
				else if (isBlockOccupied() == null)
					setType((byte) 1);
			}

			if (isBlockOccupied() != null && type != 0)
				if(!active && animationTicks == 0)
					setActive(true);

		}
		this.prevAnimationTicks = this.animationTicks;
		if (active) {
			activateBlock();
			if (animationTicks == 0)
				getWorld().playSound(null, (double) getPos().getX(), (double)getPos().getY(), (double)getPos().getZ(), SoundRegistry.SPIKE, SoundCategory.BLOCKS, 1.25F, 1.0F);
			if (animationTicks <= 20)
				animationTicks += 4;
			if (animationTicks == 20 && !this.getWorld().isClientSide())
				setActive(false);
		}
		if (!active)
			if (animationTicks >= 1)
				animationTicks--;
	}

	public void setActive(boolean isActive) {
		active = isActive;
		getWorld().sendBlockUpdated(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 2);
	}

	public void setType(byte blockType) {
		type = blockType;
		getWorld().sendBlockUpdated(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 2);
	}

	protected Entity activateBlock() {
		BlockState state = getWorld().getBlockState(getPos());
		Direction facing = state.getValue(BlockSpikeTrap.FACING);
		BlockPos hitArea = getPos().offset(facing, 1);
		List<LivingEntity> list = getWorld().getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(hitArea));
		if (animationTicks >= 1)
			for (int i = 0; i < list.size(); i++) {
				Entity entity = list.get(i);
				if (entity != null)
					if (!(entity instanceof IEntityBL))
						((LivingEntity) entity).attackEntityFrom(DamageSource.GENERIC, 2);
			}
		return null;
	}

	protected Entity isBlockOccupied() {
		BlockState state = getWorld().getBlockState(getPos());
		Direction facing = state.getValue(BlockSpikeTrap.FACING);
		BlockPos hitArea = getPos().offset(facing , 1);
		List<LivingEntity> list = getWorld().getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(hitArea).shrink(0.25D));
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity != null)
				if (!(entity instanceof IEntityBL))
					return entity;
		}
		return null;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putInt("animationTicks", animationTicks);
		nbt.putBoolean("active", active);
		nbt.putByte("type", type);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.readFromNBT(nbt);
		animationTicks = nbt.getInt("animationTicks");
		active = nbt.getBoolean("active");
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
		return new SUpdateTileEntityPacket(getPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public boolean hasFastRenderer() {
		return true;
	}
}