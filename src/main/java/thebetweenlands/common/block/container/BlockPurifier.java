package thebetweenlands.common.block.container;

import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
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
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.tile.TileEntityPurifier;

import java.util.Random;

public class BlockPurifier extends Block implements ITileEntityProvider {
	
	public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;

	public BlockPurifier(Properties properties) {
		super(properties);
		/*super(Material.ROCK);
		setHardness(2.0F);
		setResistance(5.0F);
		setTranslationKey("thebetweenlands.purifier");
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
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
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand,  Direction side, BlockRayTraceResult hitResult) {
		ItemStack heldItem = player.getItemInHand(hand);

		if (world.getBlockEntity(pos) instanceof TileEntityPurifier) {
			TileEntityPurifier tile = (TileEntityPurifier) world.getBlockEntity(pos);

			if (player.isCrouching()) {
				return false;
			}

			if (!heldItem.isEmpty()) {
				IFluidHandler handler = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				if(handler != null) {
					Fluid fluid = FluidRegistry.SWAMP_WATER;
					FluidStack bucketFluid = handler.drain(new FluidStack(fluid, Fluid.BUCKET_VOLUME), false);

					if (bucketFluid != null) {
						IItemHandler playerInventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
						if (playerInventory != null) {
							FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainerAndStow(heldItem, tile, playerInventory, Integer.MAX_VALUE, player, !level.isClientSide());

							if (fluidActionResult.isSuccess()) {
								if (!level.isClientSide()) {
									player.setItemInHand(hand, fluidActionResult.getResult());
								}
								return true;
							}
						}
					}
				}
			}
			
			if (!level.isClientSide() && tile != null) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_PURIFIER, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof TileEntityPurifier) {
			TileEntityPurifier tile = (TileEntityPurifier) world.getBlockEntity(pos);
			if (tile == null) {
				return 0;
			}
			return tile.lightOn ? 13 : 0;
		}
		return 0;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);

		if (tileEntity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileEntity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (world.getBlockEntity(pos) instanceof TileEntityPurifier) {
			TileEntityPurifier tile = (TileEntityPurifier) world.getBlockEntity(pos);
			if (tile.isPurifying() && tile.lightOn) {
				float x = pos.getX() + 0.5F;
				float y = pos.getY() + rand.nextFloat() * 6.0F / 16.0F;
				float z = pos.getZ() + 0.5F;
				float fixedOffset = 0.25F;
				float randomOffset = rand.nextFloat() * 0.6F - 0.3F;

				BLParticles.PURIFIER_STEAM.spawn(world, (double) (x - fixedOffset), (double) y + 0.5D, (double) (z + randomOffset));
				//BLParticle.STEAM_PURIFIER.spawn(world, (double) (x - fixedOffset), (double) y + 0.5D, (double) (z + randomOffset), 0.0D, 0.0D, 0.0D, 0);
				world.addParticle(ParticleTypes.FLAME, (double) (x - fixedOffset), (double) y, (double) (z + randomOffset), 0.0D, 0.0D, 0.0D);

				BLParticles.PURIFIER_STEAM.spawn(world, (double) (x + fixedOffset), (double) y + 0.5D, (double) (z + randomOffset));
				//BLParticle.STEAM_PURIFIER.spawn(world, (double) (x + fixedOffset), (double) y + 0.5D, (double) (z + randomOffset), 0.0D, 0.0D, 0.0D, 0);
				world.addParticle(ParticleTypes.FLAME, (double) (x + fixedOffset), (double) y, (double) (z + randomOffset), 0.0D, 0.0D, 0.0D);

				BLParticles.PURIFIER_STEAM.spawn(world, (x + randomOffset), (double) y, (double) (z - fixedOffset));
				//BLParticle.STEAM_PURIFIER.spawn(world, (double) (x + randomOffset), (double) y + 0.5D, (double) (z - fixedOffset), 0.0D, 0.0D, 0.0D, 0);
				world.addParticle(ParticleTypes.FLAME, (double) (x + randomOffset), (double) y, (double) (z - fixedOffset), 0.0D, 0.0D, 0.0D);

				BLParticles.PURIFIER_STEAM.spawn(world, (double) (x + randomOffset), (double) y + 0.5D, (double) (z + fixedOffset));
				//BLParticle.STEAM_PURIFIER.spawn(world, (double) (x + randomOffset), (double) y + 0.5D, (double) (z + fixedOffset), 0.0D, 0.0D, 0.0D, 0);
				world.addParticle(ParticleTypes.FLAME, (double) (x + randomOffset), (double) y, (double) (z + fixedOffset), 0.0D, 0.0D, 0.0D);

				if (world.isEmptyBlock(pos.above())) {
					BLParticles.BUBBLE_PURIFIER.spawn(world, x, y + 1, z);
				}
			}
		}
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
		return new TileEntityPurifier();
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
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
    }
	
	@Override
	public void fillWithRain(World world, BlockPos pos) {
		if (world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId && world.getBlockEntity(pos) instanceof TileEntityPurifier) {
			TileEntityPurifier tile = (TileEntityPurifier) world.getBlockEntity(pos);
			
			if(tile != null) {
				tile.fill(new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME / 2), true);
			}
		}
	}
}