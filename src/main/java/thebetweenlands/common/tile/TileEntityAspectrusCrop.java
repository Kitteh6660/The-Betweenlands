package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.aspect.Aspect;

public class TileEntityAspectrusCrop extends TileEntity implements ITickableTileEntity {
	
	public TileEntityAspectrusCrop(TileEntityType<?> te) {
		super(te);
	}

	protected Aspect seedAspect = null;
	protected boolean hasSource = false;
	
	public int glowTicks = 0;

	public void setAspect(@Nullable Aspect aspect) {
		this.seedAspect = aspect;
		BlockState state = this.level.getBlockState(this.worldPosition);
		this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
		this.setChanged();
	}

	
	@Nullable
	public Aspect getAspect() {
		return this.seedAspect;
	}
	
	public void setHasSource(boolean source) {
		this.hasSource = source;
		this.setChanged();
	}
	
	public boolean hasSource() {
		return this.hasSource;
	}
	
	@Override
	public void setLevelAndPosition(World worldIn, BlockPos pos) {
		super.setLevelAndPosition(worldIn, pos);
		this.glowTicks = worldIn.random.nextInt(200);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public double getViewDistance() {
		return 4096.0D * 6.0D;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(this.worldPosition.getX() - 0.5D, this.worldPosition.getY() - 0.5D, this.worldPosition.getZ() - 0.5D, this.worldPosition.getX() + 1.5D, this.worldPosition.getY() + 1.5D, this.worldPosition.getZ() + 1.5D);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		if(this.seedAspect != null) {
			this.seedAspect.save(nbt);
		}
		nbt.putBoolean("hasSource", this.hasSource);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		this.seedAspect = Aspect.load(nbt);
		this.hasSource = nbt.getBoolean("hasSource");
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		if(this.seedAspect != null) {
			this.seedAspect.save(nbt);
		}
		return new SUpdateTileEntityPacket(this.getBlockPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.seedAspect = Aspect.load(pkt.getTag());
		this.level.markBlockRangeForRenderUpdate(this.worldPosition, this.worldPosition);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		if(this.seedAspect != null) {
			this.seedAspect.save(nbt);
		}
		return nbt;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
		//Use vanilla behaviour to prevent TE from resetting when changing state
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public void tick() {
		this.glowTicks++;
	}
}
