package thebetweenlands.common.block.structure;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockBrazier extends Block {

	public static final PropertyEnum<EnumBrazierHalf> HALF = PropertyEnum.<EnumBrazierHalf>create("half", EnumBrazierHalf.class);
	//protected static final AxisAlignedBB BRAZIER_AABB = Block.box(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);

	public BlockBrazier() {
		super(Material.WOOD);
		setHardness(1.5F);
		setResistance(10.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(this.blockState.getBaseState().setValue(HALF, EnumBrazierHalf.LOWER));
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return FULL_BLOCK_AABB;
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
    public boolean isFireSource(World world, BlockPos pos, Direction side) {
		BlockState state = world.getBlockState(pos);
        if (state.getValue(HALF) == EnumBrazierHalf.UPPER && side == Direction.UP)
            return true;
        return false;
    }

	@Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        world.setBlockState(pos.above(), defaultBlockState().setValue(HALF, EnumBrazierHalf.UPPER), 2);
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
			world.setBlockToAir(pos);
			if (state.getValue(HALF) == EnumBrazierHalf.LOWER)
				dropBlockAsItem(world, pos, state, 0);
			return false;
		}
		return true;
	}

	@Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (state.getValue(HALF) == EnumBrazierHalf.UPPER) {
            if (world.getBlockState(pos.below()).getBlock() == this) {
                if (player.isCreative())
                    world.setBlockToAir(pos.below());
                else {
                    world.destroyBlock(pos.below(), true);
                    if (world.isClientSide())
                        world.setBlockToAir(pos.below());
                }
            }
        }
        super.onBlockHarvested(world, pos, state, player);
    }

	@Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        if (state.getValue(HALF) == EnumBrazierHalf.UPPER)
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
        return meta > 0 ? defaultBlockState().setValue(HALF, EnumBrazierHalf.UPPER) : defaultBlockState().setValue(HALF, EnumBrazierHalf.LOWER);
    }

	@Override
    public BlockState getActualState(BlockState state, IBlockReader world, BlockPos pos) {
        if (state.getValue(HALF) == EnumBrazierHalf.UPPER) {
            BlockState iblockstate = world.getBlockState(pos.below());
            if (iblockstate.getBlock() == this)
            	return state;
        }
        return state;
    }

	@Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(HALF) == EnumBrazierHalf.UPPER ? 1 : 0;
    }

	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {HALF});
    }
	
	@Override
	public boolean isSideSolid(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return side == Direction.UP && state.getValue(HALF) == EnumBrazierHalf.UPPER;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return face == Direction.UP && state.getValue(HALF) == EnumBrazierHalf.UPPER ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}
	
    public static enum EnumBrazierHalf implements IStringSerializable {
        UPPER,
        LOWER;

    	@Override
        public String toString() {
            return getName();
        }

    	@Override
        public String getName() {
            return this == UPPER ? "upper" : "lower";
        }
    }
}