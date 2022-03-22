package thebetweenlands.common.block.misc;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;

public class BlockRope extends Block implements ICustomItemBlock {
	
	protected static final VoxelShape AABB = Block.box(0.4375f, 0f, 0.4375f, 0.5625f, 1f, 0.5625f);

	public static final EnumProperty<EnumRopeVariant> VARIANT = EnumProperty.<EnumRopeVariant>create("variant", EnumRopeVariant.class);

	public BlockRope(Properties properties) {
		super(properties);
		/*super(Material.PLANTS);
		this.setSoundType(SoundType.PLANT);
		this.setHardness(0.5F);
		this.setDefaultState(this.blockState.getBaseState().setValue(VARIANT, EnumRopeVariant.SINGLE));
		this.setCreativeTab(null);*/
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {VARIANT});
	}

	@Override 
	public boolean isLadder(BlockState state, IBlockReader world, BlockPos pos, LivingEntity entity) { 
		entity.onGround = true;
		entity.fallDistance = 0;
		return true; 
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos) {
		return AABB;
	}

	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand,  Direction side, BlockRayTraceResult hitResult) {
		ItemStack heldItem = player.getItemInHand(hand);
		if(heldItem.isEmpty() && player.isCrouching()) {
			BlockPos offsetPos = pos.below();
			while(world.getBlockState(offsetPos).getBlock() == this) {
				offsetPos = offsetPos.below();
			}
			offsetPos = offsetPos.above();
			if(offsetPos.getY() != pos.getY()) {
				if(!world.isClientSide()) {
					world.setBlockToAir(offsetPos);

					if(!player.isCreative() && !player.inventory.addItemStackToInventory(new ItemStack(ItemRegistry.ROPE_ITEM))) {
						world.addFreshEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), new ItemStack(ItemRegistry.ROPE_ITEM)));
					}
				}

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return !worldIn.getBlockState(pos).getMaterial().isLiquid() && super.canPlaceBlockAt(worldIn, pos) && (worldIn.getBlockState(pos.above()).isSideSolid(worldIn, pos, Direction.DOWN) || worldIn.getBlockState(pos.above()).getBlock() == this);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return ItemRegistry.ROPE_ITEM;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		if (!(worldIn.getBlockState(pos.above()).isSideSolid(worldIn, pos, Direction.DOWN) || worldIn.getBlockState(pos.above()).getBlock() == this)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		state = state.setValue(VARIANT, EnumRopeVariant.MIDDLE);

		if(worldIn.getBlockState(pos.above()).getBlock() != this) {
			if(worldIn.getBlockState(pos.below()).getBlock() == this) {
				state = state.setValue(VARIANT, EnumRopeVariant.TOP);
			} else {
				state = state.setValue(VARIANT, EnumRopeVariant.SINGLE);
			}
		} else if(worldIn.getBlockState(pos.below()).getBlock() != this){
			state = state.setValue(VARIANT, EnumRopeVariant.BOTTOM);
		}

		return state;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		switch(meta) {
		default:
		case 0:
			return this.defaultBlockState().setValue(VARIANT, EnumRopeVariant.SINGLE);
		case 1:
			return this.defaultBlockState().setValue(VARIANT, EnumRopeVariant.TOP);
		case 2:
			return this.defaultBlockState().setValue(VARIANT, EnumRopeVariant.MIDDLE);
		case 3:
			return this.defaultBlockState().setValue(VARIANT, EnumRopeVariant.BOTTOM);
		}
	}

	@Override
	public int getMetaFromState(BlockState state) {
		switch(state.getValue(VARIANT)) {
		default:
		case SINGLE:
			return 0;
		case TOP:
			return 1;
		case MIDDLE:
			return 2;
		case BOTTOM:
			return 3;
		}
	}

	public static enum EnumRopeVariant implements IStringSerializable {
		SINGLE,
		TOP,
		MIDDLE,
		BOTTOM;

		private final String name;

		private EnumRopeVariant() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		@Override
		public String toString() {
			return this.name;
		}

		@Override
		public String getName() {
			return this.name;
		}
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public BlockItem getItemBlock() {
		return null;
	}
}
