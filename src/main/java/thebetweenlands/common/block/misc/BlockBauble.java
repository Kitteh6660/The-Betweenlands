package thebetweenlands.common.block.misc;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.world.event.EventWinter;

public class BlockBauble extends Block implements ITintedBlock, IForgeShearable {
	
	private static final VoxelShape AABB = Block.box(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);

	public static final IntegerProperty COLOR = IntegerProperty.create("color", 0, 7);
	public static final BooleanProperty DIAGONAL = BooleanProperty.create("diagonal");

	public BlockBauble(Properties properties) {
		super(properties);
		/*super(Material.GLASS);
		this.setHardness(0.3F);
		this.setCreativeTab(null);
		this.setSoundType(SoundType.GLASS);
		this.setLightLevel(1.0f);
		this.setTickRandomly(true);*/
		this.registerDefaultState(this.defaultBlockState().setValue(COLOR, 0).setValue(DIAGONAL, false));
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(COLOR, meta);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(COLOR);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[] { COLOR, DIAGONAL });
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return AABB;
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if(!worldIn.isClientSide()) {
			int rotation = MathHelper.floor(((placer.yRot + 180.0F) * 8.0F / 360.0F) + 0.5D) & 7;
			worldIn.setBlockState(pos, state.setValue(DIAGONAL, rotation % 2 == 1).setValue(COLOR, worldIn.rand.nextInt(8)), 11);
		}
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && (worldIn.isSideSolid(pos.above(), Direction.DOWN) || worldIn.getBlockState(pos.above()).getBlockFaceShape(worldIn, pos.above(), Direction.DOWN) != BlockFaceShape.UNDEFINED);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, BlockState state) {
		if(!worldIn.isSideSolid(pos.above(), Direction.DOWN) && worldIn.getBlockState(pos.above()).getBlockFaceShape(worldIn, pos.above(), Direction.DOWN) == BlockFaceShape.UNDEFINED) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.defaultBlockState(), 3);
		}
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public int getColorMultiplier(BlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		switch(state.getValue(COLOR)) {
		default:
		case 0:
			return 0xFFFF0000;
		case 1:
			return 0xFF00FF00;
		case 2:
			return 0xFF0000FF;
		case 3:
			return 0xFFFFFF00;
		case 4:
			return 0xFF00FFFF;
		case 5:
			return 0xFFFF00FF;
		case 6:
			return 0xFFFF60CC;
		case 7:
			return 0xFF30FFAA;
		}
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);

		if(!worldIn.isClientSide()) {
			ExperienceOrbEntity orb = new ExperienceOrbEntity(worldIn, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 1);
			worldIn.addFreshEntity(orb);
		}
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		if(!EventWinter.isFroooosty(worldIn)) {
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	public boolean isShearable(ItemStack item, World world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, World world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
	}
}
