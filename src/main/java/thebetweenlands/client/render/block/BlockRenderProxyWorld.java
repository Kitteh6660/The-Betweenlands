package thebetweenlands.client.render.block;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class BlockRenderProxyWorld implements IBlockReader 
{
	private final TLongObjectMap<BlockState> blockMap = new TLongObjectHashMap<>();

	@Nullable
	private final IBlockReader world;

	public BlockRenderProxyWorld(@Nullable IBlockReader world) {
		this.world = world;
	}

	public void setBlockState(BlockPos pos, BlockState state) {
		this.blockMap.put(pos.toLong(), state);
	}

	@Override
	public TileEntity getBlockEntity(BlockPos pos) {
		return null;
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue) {
		return 0;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		BlockState state = this.blockMap.get(pos.toLong());
		if(state != null) {
			return state;
		}
		return this.world == null ? Blocks.AIR.defaultBlockState() : this.world.getBlockState(pos);
	}

	@Override
	public boolean isEmptyBlock(BlockPos pos) {
		BlockState state = this.blockMap.get(pos.toLong());
		if(state != null) {
			return state.getBlock().isAir(state, this, pos);
		}
		return this.world == null || this.world.isEmptyBlock(pos);
	}

	@Override
	public Biome getBiome(BlockPos pos) {
		return this.world == null ? Biomes.PLAINS : this.world.getBiome(pos);
	}

	@Override
	public int getStrongPower(BlockPos pos, Direction direction) {
		return this.world != null ? this.world.getStrongPower(pos, direction) : 0;
	}

	@Override
	public WorldType getWorldType() {
		return this.world != null ? this.world.getWorldType() : WorldType.DEFAULT;
	}

	@Override
	public boolean isSideSolid(BlockPos pos, Direction side, boolean _default) {
		BlockState state = this.blockMap.get(pos.toLong());
		if(state != null) {
			return state.isSideSolid(world, pos, side);
		}
		return this.world == null ? false : this.world.isSideSolid(pos, side, _default);
	}

	@Override
	public FluidState getFluidState(BlockPos p_204610_1_) {
		// TODO Auto-generated method stub
		return null;
	}
}
