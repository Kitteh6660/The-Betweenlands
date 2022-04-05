package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.block.misc.BlockSludge;
import thebetweenlands.common.block.structure.BlockSpikeTrap;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.registries.TileEntityRegistry;

public class TileEntitySpikeTrap extends TileEntity implements ITickableTileEntity {

	public int prevAnimationTicks;
	public int animationTicks;
	public boolean active;
	public byte type;

	public TileEntitySpikeTrap() {
		super(TileEntityRegistry.SPIKE_TRAP.get());
	}
	
	@Override
	public void tick() {
		if (!getLevel().isClientSide()) {
			BlockState state = getLevel().getBlockState(getBlockPos());
			Direction facing = state.getValue(BlockSpikeTrap.FACING);

			BlockState stateFacing = getLevel().getBlockState(getBlockPos().relative(facing, 1));
			if (stateFacing.getBlock() != Blocks.AIR && stateFacing.getBlockHardness(getLevel(), getBlockPos().relative(facing, 1)) >= 0.0F && !(stateFacing.getBlock() instanceof BlockSludge)) {
				setType((byte) 1);
				setActive(true);
				Block block = stateFacing.getBlock();
				getLevel().levelEvent(null, 2001, getBlockPos().relative(facing, 1), Block.getIdFromBlock(block));
				block.dropBlockAsItem(getLevel(), getBlockPos().relative(facing, 1), getLevel().getBlockState(getBlockPos().relative(facing, 1)), 0);
				getLevel().setBlockToAir(getBlockPos().relative(facing, 1));
			}
			BlockState stateFacing2 = getLevel().getBlockState(getBlockPos().relative(facing, 2));
			if (stateFacing2.getBlock() != Blocks.AIR && stateFacing2.getBlockHardness(getLevel(), getBlockPos().relative(facing, 2)) >= 0.0F && !(stateFacing.getBlock() instanceof BlockSludge)) {
				setType((byte) 1);
				setActive(true);
				Block block = stateFacing2.getBlock();
				getLevel().levelEvent(null, 2001, getBlockPos().relative(facing, 2), Block.getIdFromBlock(block));
				block.dropBlockAsItem(getLevel(), getBlockPos().relative(facing, 2), getLevel().getBlockState(getBlockPos().relative(facing, 2)), 0);
				getLevel().setBlockToAir(getBlockPos().relative(facing, 2));
			}
			if (getLevel().random.nextInt(500) == 0) {
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
				getLevel().playSound(null, (double) getBlockPos().getX(), (double)getBlockPos().getY(), (double)getBlockPos().getZ(), SoundRegistry.SPIKE, SoundCategory.BLOCKS, 1.25F, 1.0F);
			if (animationTicks <= 20)
				animationTicks += 4;
			if (animationTicks == 20 && !this.getLevel().isClientSide())
				setActive(false);
		}
		if (!active)
			if (animationTicks >= 1)
				animationTicks--;
	}

	public void setActive(boolean isActive) {
		active = isActive;
		getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 2);
	}

	public void setType(byte blockType) {
		type = blockType;
		getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 2);
	}

	protected Entity activateBlock() {
		BlockState state = getLevel().getBlockState(getBlockPos());
		Direction facing = state.getValue(BlockSpikeTrap.FACING);
		BlockPos hitArea = getBlockPos().relative(facing, 1);
		List<LivingEntity> list = getLevel().getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(hitArea));
		if (animationTicks >= 1)
			for (int i = 0; i < list.size(); i++) {
				Entity entity = list.get(i);
				if (entity != null)
					if (!(entity instanceof IEntityBL))
						((LivingEntity) entity).hurt(DamageSource.GENERIC, 2);
			}
		return null;
	}

	protected Entity isBlockOccupied() {
		BlockState state = getLevel().getBlockState(getBlockPos());
		Direction facing = state.getValue(BlockSpikeTrap.FACING);
		BlockPos hitArea = getBlockPos().relative(facing , 1);
		List<LivingEntity> list = getLevel().getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(hitArea).deflate(0.25D));
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
		super.load(state, nbt);
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
		return new SUpdateTileEntityPacket(getBlockPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		load(this.getBlockState(), packet.getTag());
	}

	@Override
	public boolean hasFastRenderer() {
		return true;
	}
}