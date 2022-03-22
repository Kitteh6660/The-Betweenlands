package thebetweenlands.common.block.structure;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.ItemBlockSlab;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockSlabBetweenlands extends BasicBlock implements BlockRegistry.ICustomItemBlock {
	public static final PropertyEnum<EnumBlockHalfBL> HALF = PropertyEnum.<EnumBlockHalfBL>create("half", EnumBlockHalfBL.class);
	protected static final AxisAlignedBB AABB_BOTTOM_HALF = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
	protected static final AxisAlignedBB AABB_TOP_HALF = Block.box(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);

	@SuppressWarnings("deprecation")
	public BlockSlabBetweenlands(Block block) {
		super(block.getMaterial(block.defaultBlockState()));
		this.setSoundType(block.getSoundType());
		this.registerDefaultState(this.stateDefinition.any().setValue(HALF, EnumBlockHalfBL.BOTTOM));
		this.setHardness(2.0F);
		this.setResistance(10.0F);
		this.useNeighborBrightness = true;
	}

	@Nonnull
	@Override
	public BlockItem getItemBlock() {
		return new ItemBlockSlab(this);
	}

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return state.getValue(HALF).equals(EnumBlockHalfBL.FULL);
	}

	@Override
	public boolean doesSideBlockRendering(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return (state.getValue(HALF).equals(EnumBlockHalfBL.BOTTOM) && face == Direction.DOWN) || (state.getValue(HALF).equals(EnumBlockHalfBL.TOP) && face == Direction.UP) || state.getValue(HALF).equals(EnumBlockHalfBL.FULL);
	}

	@Override
    public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		BlockState state = getStateFromMeta(meta);
		return state.getValue(HALF).equals(EnumBlockHalfBL.FULL) ? state : (facing != Direction.DOWN && (facing == Direction.UP || (double) hitY <= 0.5D) ? state.setValue(HALF, EnumBlockHalfBL.BOTTOM) : state.setValue(HALF, EnumBlockHalfBL.TOP));
	}

	@Override
	public int quantityDropped(BlockState state, int fortune, Random random) {
		return state.getValue(HALF).equals(EnumBlockHalfBL.FULL) ? 2 : 1;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return state.getValue(HALF).equals(EnumBlockHalfBL.FULL);
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		EnumBlockHalfBL half = state.getValue(HALF);
		switch (half) {
			case TOP:
				return AABB_TOP_HALF;
			case BOTTOM:
				return AABB_BOTTOM_HALF;
			default:
				return FULL_BLOCK_AABB;
		}
	}

	@Override
	public boolean isTopSolid(BlockState state) {
		return state.getValue(HALF).equals(EnumBlockHalfBL.FULL) || state.getValue(HALF).equals(EnumBlockHalfBL.TOP);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
        if (state.getValue(HALF) == EnumBlockHalfBL.FULL) {
            return BlockFaceShape.SOLID;
        } else if (face == Direction.UP && state.getValue(HALF) == EnumBlockHalfBL.TOP) {
            return BlockFaceShape.SOLID;
        } else {
            return face == Direction.DOWN && state.getValue(HALF) == EnumBlockHalfBL.BOTTOM ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
        }
    }

	@Override
    public ActionResultType use(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
	    ItemStack heldItem = playerIn.getItemInHand(hand);
		if (!heldItem.isEmpty() && ((state.getValue(HALF).equals(EnumBlockHalfBL.TOP) && facing.equals(Direction.DOWN)) || (state.getValue(HALF).equals(EnumBlockHalfBL.BOTTOM) && facing.equals(Direction.UP)))){
			if (heldItem.getItem() == Item.getItemFromBlock(this)) {
				worldIn.setBlockState(pos, state.setValue(HALF, EnumBlockHalfBL.FULL));
				if(!playerIn.isCreative())
					heldItem.setCount(heldItem.getCount() - 1);
				SoundType soundtype = this.getSoundType(state, worldIn, pos, playerIn);
				worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				return true;
			}
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, HALF);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return defaultBlockState().setValue(HALF, EnumBlockHalfBL.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(HALF).ordinal();
	}

	public static enum EnumBlockHalfBL implements IStringSerializable {
		TOP("top"),
		BOTTOM("bottom"),
		FULL("full");

		private final String name;

		private EnumBlockHalfBL(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}

		@Override
		public String getName() {
			return this.name;
		}


		public static EnumBlockHalfBL byMetadata(int metadata) {
			if (metadata < 0 || metadata >= values().length) {
				metadata = 0;
			}
			return values()[metadata];
		}
	}
}
