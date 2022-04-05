package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.fluid.SwampWaterBlock;
import thebetweenlands.common.block.fluid.SwampWaterFluid;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockRootUnderwater extends SwampWaterBlock {
	
	public BlockRootUnderwater(Properties properties) {
		super(properties);
		/*super(FluidRegistry.SWAMP_WATER, Material.WATER);
		this.setSoundType(SoundType.WOOD);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
		this.setUnderwaterBlock(true);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHarvestLevel("axe", 0);*/
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return BlockStateContainerHelper.extendBlockstateContainer((ExtendedBlockState) super.createBlockState(), new IProperty[0], new IUnlistedProperty[]{
				BlockRoot.POS_X,
				BlockRoot.POS_Y,
				BlockRoot.POS_Z,
				BlockRoot.NO_BOTTOM,
				BlockRoot.NO_TOP,
				BlockRoot.DIST_UP,
				BlockRoot.DIST_DOWN
		});
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return ItemRegistry.TANGLED_ROOT;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}

	@Override
	public boolean isBlockNormalCube(BlockState blockState) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(BlockState blockState) {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public BlockState getExtendedState(BlockState oldState, IBlockReader worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) super.getExtendedState(oldState, worldIn, pos);

		final int maxLength = 32;
		int distUp = 0;
		int distDown = 0;
		boolean noTop = false;
		boolean noBottom = false;

		BlockState blockState;
		//Block block;
		for(distUp = 0; distUp < maxLength; distUp++) {
			blockState = worldIn.getBlockState(pos.offset(0, 1 + distUp, 0));
			if(blockState.getBlock() == this || blockState.getBlock() == BlockRegistry.ROOT)
				continue;
			if(blockState.getBlock() == Blocks.AIR || !blockState.canOcclude())
				noTop = true;
			break;
		}
		for(distDown = 0; distDown < maxLength; distDown++)
		{
			blockState = worldIn.getBlockState(pos.offset(0, -(1 + distDown), 0));
			if(blockState.getBlock() == this || blockState.getBlock() == BlockRegistry.ROOT)
				continue;
			if(blockState.getBlock() == Blocks.AIR || !blockState.canOcclude())
				noBottom = true;
			break;
		}

		return state.setValue(BlockRoot.POS_X, pos.getX()).setValue(BlockRoot.POS_Y, pos.getY()).setValue(BlockRoot.POS_Z, pos.getZ()).setValue(BlockRoot.DIST_UP, distUp).setValue(BlockRoot.DIST_DOWN, distDown).setValue(BlockRoot.NO_TOP, noTop).setValue(BlockRoot.NO_BOTTOM, noBottom);
	}
	
	@Override
	public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		super.getBlockFaceShape(worldIn, state, pos, face)
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
		this.onBlockHarvested(world, pos, state, player);
		return world.setBlockState(pos, BlockRegistry.SWAMP_WATER.defaultBlockState(), level.isClientSide() ? 11 : 3);
	}
	
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(BlockRegistry.ROOT);
	}
}
