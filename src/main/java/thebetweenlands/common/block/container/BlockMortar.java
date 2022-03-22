package thebetweenlands.common.block.container;

import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.DirectionProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.misc.ItemLifeCrystal;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityMortar;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockMortar extends ContainerBlock {
	public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;

	public BlockMortar() {
		super(Material.ROCK);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand) {
		return this.defaultBlockState().setValue(FACING, placer.getDirection());
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlockState(pos, state.setValue(FACING, placer.getDirection()), 2);
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, PlayerEntity playerIn) {
		if(!worldIn.isClientSide()) {
			if (worldIn.getBlockEntity(pos) instanceof TileEntityMortar) {
				TileEntityMortar tile = (TileEntityMortar) worldIn.getBlockEntity(pos);

				tile.manualGrinding = true;
				BlockState state = worldIn.getBlockState(pos);
				worldIn.sendBlockUpdated(pos, state, state, 3);
			}
		}
	}

	@Override
	public ActionResultType use(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand,Direction side, BlockRayTraceResult hitResult) {
		if (worldIn.isClientSide())
			return true;
		if (worldIn.getBlockEntity(pos) instanceof TileEntityMortar) {
			TileEntityMortar tile = (TileEntityMortar) worldIn.getBlockEntity(pos);

			if (!playerIn.getItemInHand(hand).isEmpty()) {
				if (playerIn.getItemInHand(hand).getItem() == ItemRegistry.PESTLE && tile.getItem(1).isEmpty()) {
					tile.setItem(1, playerIn.getItemInHand(hand));
					tile.hasPestle = true;
					playerIn.setItemInHand(hand, ItemStack.EMPTY);
				} else if (playerIn.getItemInHand(hand).getItem() instanceof ItemLifeCrystal && tile.getItem(3).isEmpty()) {
					tile.setItem(3, playerIn.getItemInHand(hand));
					playerIn.setItemInHand(hand, ItemStack.EMPTY);
				} else {
					playerIn.openGui(TheBetweenlands.instance, CommonProxy.GUI_PESTLE_AND_MORTAR, worldIn, pos.getX(), pos.getY(), pos.getZ());
				}
			} else {
				playerIn.openGui(TheBetweenlands.instance, CommonProxy.GUI_PESTLE_AND_MORTAR, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, BlockState state) {
		IInventory tile = (IInventory) world.getBlockEntity(pos);
		if (tile != null)
			for (int i = 0; i < tile.getContainerSize(); i++) {
				ItemStack stack = tile.getItem(i);
				if (!stack.isEmpty()) {
					if (!world.isClientSide()) {
						float f = 0.7F;
						double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						if (stack.getItem() == ItemRegistry.PESTLE){
							stack.getTag().putBoolean("active", false);
						}
						ItemEntity ItemEntity = new ItemEntity(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
						ItemEntity.setPickupDelay(10);
						world.spawnEntity(ItemEntity);
					}
				}
			}
		super.breakBlock(world, pos, state);
	}

	@Override
	public void randomDisplayTick(BlockState stateIn, World world, BlockPos pos, Random rand) {
		TileEntityMortar tile = (TileEntityMortar) world.getBlockEntity(pos);
		if (tile.progress > 0 && tile.progress < 84 && rand.nextInt(3) == 0) {
			float f = pos.getX() + 0.5F;
			float f1 = pos.getY() + 1.1F + rand.nextFloat() * 6.0F / 16.0F;
			float f2 = pos.getZ() + 0.5F;
			world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, f, f1, f2, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMortar();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(FACING, Direction.byHorizontalIndex(meta));
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}
}