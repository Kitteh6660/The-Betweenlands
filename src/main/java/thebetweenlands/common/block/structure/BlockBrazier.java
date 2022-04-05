package thebetweenlands.common.block.structure;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockBrazier extends Block {

	public static final EnumProperty<Half> HALF = EnumProperty.<Half>create("half", Half.class);
	//protected static final VoxelShape BRAZIER_AABB = Block.box(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);

	public BlockBrazier(Properties properties) {
		super(properties);
		/*super(Material.WOOD);
		setHardness(1.5F);
		setResistance(10.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
		registerDefaultState(this.stateDefinition.any().setValue(HALF, Half.BOTTOM));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.block();
	}

	@Override
	@Nullable
	public VoxelShape getCollisionBoundingBox(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return VoxelShapes.block();
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
    public boolean isFireSource(BlockState state, IWorldReader world, BlockPos pos, Direction face) {
        if (state.getValue(HALF) == Half.TOP && face == Direction.UP) {
            return true;
        }
        return false;
    }

	@Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        world.setBlockState(pos.above(), defaultBlockState().setValue(HALF, Half.TOP), 2);
    }

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos.below());
		if (state.getBlock() == null || !world.isEmptyBlock(pos.above()))
			return false;
		return state.getMaterial().blocksMovement();
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		dropBrazierIfCantStay(world, state, pos);
	}

	protected boolean dropBrazierIfCantStay(World world, BlockState state, BlockPos pos) {
		if (world.isEmptyBlock(pos.below())) {
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			if (state.getValue(HALF) == Half.BOTTOM)
				dropBlockAsItem(world, pos, state, 0);
			return false;
		}
		return true;
	}

	@Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (state.getValue(HALF) == Half.TOP) {
            if (world.getBlockState(pos.below()).getBlock() == this) {
                if (player.isCreative())
                    world.setBlockToAir(pos.below());
                else {
                    world.destroyBlock(pos.below(), true);
                    if (level.isClientSide())
                        world.setBlockToAir(pos.below());
                }
            }
        }
        super.onBlockHarvested(world, pos, state, player);
    }

	@Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        if (state.getValue(HALF) == Half.TOP)
            return Items.AIR;
        else {
            return super.getItemDropped(state, rand, fortune);
        }
    }

	@Override
	public int quantityDropped(Random rand) {
		return 1;
	}

	@Override
    public BlockState getStateFromMeta(int meta) {
        return meta > 0 ? defaultBlockState().setValue(HALF, Half.TOP) : defaultBlockState().setValue(HALF, Half.BOTTOM);
    }

	@Override
    public BlockState getActualState(BlockState state, IBlockReader world, BlockPos pos) {
        if (state.getValue(HALF) == Half.TOP) {
            BlockState iblockstate = world.getBlockState(pos.below());
            if (iblockstate.getBlock() == this)
            	return state;
        }
        return state;
    }

	@Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(HALF) == Half.TOP ? 1 : 0;
    }

	@Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
        return new BlockStateContainer(this, new IProperty[] {HALF});
    }
	
	@Override
	public boolean isSideSolid(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return side == Direction.UP && state.getValue(HALF) == Half.TOP;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return face == Direction.UP && state.getValue(HALF) == Half.TOP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}
}