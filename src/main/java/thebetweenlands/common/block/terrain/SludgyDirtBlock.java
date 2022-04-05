package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.block.WebBlock;

public class SludgyDirtBlock extends Block {
	
	private static final VoxelShape BOUNDING_BOX = Block.box(0, 0, 0, 1, 1 - 0.125F, 1);

	public SludgyDirtBlock(Properties properties) {
		super(properties);
		/*super(Material.GRASS);
		setHardness(0.5F);
		setSoundType(SoundType.GROUND);
		setHarvestLevel("shovel", 0);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
		//setTickRandomly(true);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		if (!worldIn.isClientSide()) {
			BlockState blockStateAbove = worldIn.getBlockState(pos.above());
			if(blockStateAbove.getLightOpacity(worldIn, pos.above()) > 2 || blockStateAbove.getBlock() == this) {
				worldIn.setBlockAndUpdate(pos, BlockRegistry.SWAMP_DIRT.get().defaultBlockState());
			}
		}
	}

	//TODO: Replace this with loot tables.
	/*@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return BlockRegistry.SWAMP_DIRT.getItemDropped(state, rand, fortune);
	}*/

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return BOUNDING_BOX;
	}

	@Override
	public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
		if (entity instanceof IEntityBL == false)  {
			entity.makeStuckInBlock(state, new Vector3d(0.25D, (double)0.05F, 0.25D));
		}
	}

	//TODO: Replace this with client render setup.
	/*@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}*/

	/*@Override
	public boolean shouldSideBeRendered(BlockState blockState, IBlockReader iblockaccess, BlockPos pos, Direction side) {
		Block block = iblockaccess.getBlockState(pos.offset(side)).getBlock();
		return block instanceof BlockSludgyDirt == false && block instanceof BlockSpreadingSludgyDirt == false;
	}*/
}
