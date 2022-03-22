package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockMudFlowerPotCandle extends Block {
	protected static final AxisAlignedBB CANDLE_AABB = Block.box(0.4D, 0.0D, 0.4D, 0.6D, 0.8D, 0.6D);

	//Wow! So lit
	public static final BooleanProperty LIT = BooleanProperty.create("lit");

	public BlockMudFlowerPotCandle() {
		super(Material.CIRCUITS);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHardness(0.3F);
		this.setSoundType(SoundType.STONE);
		this.setDefaultState(this.blockState.getBaseState().setValue(LIT, true));
		this.useNeighborBrightness = true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { LIT });
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(LIT, meta != 0);
	}
	
	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(LIT) ? 1 : 0;
	}
	
	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		return this.defaultBlockState().setValue(LIT, true);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		return CANDLE_AABB;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, PlayerEntity playerIn) {
		if(!worldIn.isClientSide()) {
			worldIn.setBlockState(pos, BlockRegistry.MUD_FLOWER_POT.defaultBlockState());

			worldIn.playSound(null, pos, SoundType.WOOD.getBreakSound(), SoundCategory.BLOCKS, (SoundType.WOOD.getVolume() + 1.0F) / 2.0F, SoundType.WOOD.getPitch() * 0.8F);

			spawnAsEntity(worldIn, pos, new ItemStack(BlockRegistry.SULFUR_TORCH));
		}
	}

	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		if (world.isClientSide()) {
			return true;
		} else {
			state = state.cycleProperty(LIT);
			world.setBlockState(pos, state, 3);
			if(state.getValue(LIT))
				world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.05F, 1F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			else
				world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.1F, 2F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			return true;
		}
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState(pos.below()).isSideSolid(worldIn, pos.below(), Direction.UP);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!worldIn.getBlockState(pos.below()).isSideSolid(worldIn, pos.below(), Direction.UP)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(BlockRegistry.MUD_FLOWER_POT);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		super.getDrops(drops, world, pos, state, fortune);
		drops.add(new ItemStack(BlockRegistry.SULFUR_TORCH));
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return face == Direction.DOWN ? BlockFaceShape.CENTER_SMALL : BlockFaceShape.UNDEFINED;
	}

	@Override
	public int getLightValue(BlockState state) {
		return state.getValue(LIT) ? 13 : 0;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(stateIn.getValue(LIT)) {
			double x = (double)pos.getX() + 0.5D;
			double y = (double)pos.getY() + 1.0D;
			double z = (double)pos.getZ() + 0.5D;

			worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
			worldIn.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}
}