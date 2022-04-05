package thebetweenlands.common.block.container;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.item.misc.ItemBarrel;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.tile.TileEntityBarrel;
import thebetweenlands.common.tile.TileEntityPurifier;

public class LiquidBarrelBlock extends Block {
	
	public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;

	private final boolean isHeatResistant;
	
	public LiquidBarrelBlock(boolean isHeatResistant, Properties properties) {
		super(properties);
		/*super(material.getMaterial());
		this.setSoundType(material.getBlock().getSoundType());
		this.setHardness(2.0F);
		this.setResistance(5.0F);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
		this.isHeatResistant = isHeatResistant;
	}

	public boolean isHeatResistant(World world, BlockPos pos, BlockState state) {
		return this.isHeatResistant;
	}
	
	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand) {
		return this.defaultBlockState().setValue(FACING, placer.getDirection());
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlock(pos, state.setValue(FACING, placer.getDirection()), 2);
	}

	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand,  Direction side, BlockRayTraceResult hitResult) {
		ItemStack heldItem = player.getItemInHand(hand);

		if(world.getBlockEntity(pos) instanceof TileEntityBarrel) {
			TileEntityBarrel tile = (TileEntityBarrel) world.getBlockEntity(pos);

			if(player.isCrouching()) {
				return false;
			}

			if(!heldItem.isEmpty()) {
				IFluidHandler handler = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				if(handler != null) {
					IItemHandler playerInventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
					if(playerInventory != null) {
						FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainerAndStow(heldItem, tile, playerInventory, Integer.MAX_VALUE, player, !world.isClientSide());

						if(fluidActionResult.isSuccess()) {
							if(!world.isClientSide()) {
								player.setItemInHand(hand, fluidActionResult.getResult());
							}
							return ActionResultType.SUCCESS;
						} else {
							fluidActionResult = FluidUtil.tryFillContainerAndStow(heldItem, tile, playerInventory, Integer.MAX_VALUE, player, !world.isClientSide());
							if(fluidActionResult.isSuccess()) {
								if(!world.isClientSide()) {
									player.setItemInHand(hand, fluidActionResult.getResult());
								}
								return ActionResultType.SUCCESS;
							}
						}
					}
				}
			}

			if(!level.isClientSide() && tile != null) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_BARREL, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction facing = Direction.byIndex(meta);

		if (facing.getAxis() == Direction.Axis.Y) {
			facing = Direction.NORTH;
		}

		return this.defaultBlockState().setValue(FACING, facing);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return new TileEntityBarrel();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean shouldSideBeRendered(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public BlockItem getItemBlock() {
		return new ItemBarrel(this);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
	}

	@Override
	public List<ItemStack> getDrops(IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		return Collections.emptyList();
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if(!worldIn.isClientSide() && !player.isCreative() && worldIn.getGameRules().getBoolean("doTileDrops")) {
			TileEntity te = worldIn.getBlockEntity(pos);

			if(te instanceof TileEntityBarrel) {
				Item item = Item.getItemFromBlock(worldIn.getBlockState(pos).getBlock());
				if(item instanceof ItemBarrel) {
					InventoryHelper.dropItemStack(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ((ItemBarrel) item).fromBarrel((TileEntityBarrel) te));
				}
			}
		}
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}
	
	@Override
	public void fillWithRain(World world, BlockPos pos) {
		if (world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId && world.getBlockEntity(pos) instanceof TileEntityBarrel) {
			TileEntityBarrel tile = (TileEntityBarrel) world.getBlockEntity(pos);
			tile.fill(new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME / 2), true);
		}
	}
}