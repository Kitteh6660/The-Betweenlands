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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockSpreadingSludgyDirt extends BlockSpreadingDeath {
	
	private static final AxisAlignedBB BOUNDING_BOX = Block.box(0, 0, 0, 1, 1 - 0.125F, 1);

	public BlockSpreadingSludgyDirt(Properties properties) {
		super(properties);
		/*super(Material.GRASS);
		setHardness(0.5F);
		setSoundType(SoundType.GROUND);
		setHarvestLevel("shovel", 0);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
		//setTickRandomly(true);
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, BlockState state, Random rand) {
		if(world.getLight(pos.above()) < 4 && world.getBlockLightOpacity(pos.above()) > 2) {
			world.setBlockState(pos, BlockRegistry.SWAMP_DIRT.defaultBlockState());
			this.checkAndRevertBiome(world, pos);
		} else {
			super.updateTick(world, pos, state, rand);
		}
	}

	@Override
	public boolean canSpreadInto(World world, BlockPos pos, BlockState state, BlockPos offsetPos, BlockState offsetState) {
		return super.canSpreadInto(world, pos, state, offsetPos, offsetState) && SurfaceType.GRASS_AND_DIRT.matches(offsetState);
	}

	@Override
	public void spreadInto(World world, BlockPos pos, BlockState state, BlockPos offsetPos, BlockState offsetState) {
		world.setBlockState(offsetPos, this.defaultBlockState());
		for(int yo = 1; yo < 6; yo++) {
			if(this.canSpreadInto(world, pos, state, offsetPos.below(yo), world.getBlockState(offsetPos.below(yo)))) {
				world.setBlockState(offsetPos.below(yo), BlockRegistry.MUD.defaultBlockState());
			}
		}
		if(world.rand.nextInt(3) == 0 && world.isEmptyBlock(offsetPos.above())) {
			world.setBlockState(offsetPos.above(), BlockRegistry.DEAD_WEEDWOOD_BUSH.defaultBlockState());
		}
	}
	
	@Override
	protected boolean shouldSpread(World world, BlockPos pos, BlockState state) {
		return world.rand.nextInt(2) == 0;
	}

	@Override
	public Biome getSpreadingBiome() {
		return BiomeRegistry.SLUDGE_PLAINS;
	}

	@Override
	public Biome getPreviousBiome() {
		return BiomeRegistry.SWAMPLANDS_CLEARING;
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return BlockRegistry.SWAMP_DIRT.getItemDropped(state, rand, fortune);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return BOUNDING_BOX;
	}

	@Override
	public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
		if(entity instanceof IEntityBL == false) entity.setInWeb();
	}

	@Override
	public boolean isOpaqueCube(BlockState s) {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean shouldSideBeRendered(BlockState blockState, IBlockReader iblockaccess, BlockPos pos, Direction side) {
		Block block = iblockaccess.getBlockState(pos.offset(side)).getBlock();
		return block instanceof SludgyDirtBlock == false && block instanceof BlockSpreadingSludgyDirt == false;
	}
}
