package thebetweenlands.common.block.container;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.item.misc.ItemLifeCrystal;
import thebetweenlands.common.item.tools.ItemBLBucket;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityInfuser;

public class BlockInfuser extends ContainerBlock {
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

	public BlockInfuser(Properties properties) {
		super(properties);
		/*super(Material.IRON);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(FACING, Direction.byHorizontalIndex(meta & 3));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[]{FACING});
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		int rotation = MathHelper.floor(placer.yRot * 4.0F / 360.0F + 0.5D) & 3;
		state = state.setValue(FACING, Direction.byHorizontalIndex(rotation));
		worldIn.setBlockState(pos, state, 3);
	}

	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, BlockRayTraceResult hitResult) {
		ItemStack heldItem = player.getItemInHand(hand);
		TileEntity tileEntity = world.getBlockEntity(pos);
		if (!level.isClientSide() && tileEntity instanceof TileEntityInfuser) {
			TileEntityInfuser tile = (TileEntityInfuser) tileEntity;

			final IFluidHandler fluidHandler = getFluidHandler(world, pos);
			if (fluidHandler != null && FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) {
				return true;
			}

			if (!player.isCrouching()) {
				if (heldItem.isEmpty() && tile.getStirProgress() >= 90) {
					tile.setStirProgress(0);
					return true;
				}
				if (!heldItem.isEmpty() && !tile.hasInfusion()) {
					ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(heldItem, AspectManager.get(world));
					if(aspectContainer.getAspects().size() > 0) {
						ItemStack ingredient = heldItem;
						for (int i = 0; i < TileEntityInfuser.MAX_INGREDIENTS; i++) {
							if(tile.getItem(i).isEmpty()) {
								ItemStack singleIngredient = ingredient.copy();
								singleIngredient.setCount(1);
								tile.setItem(i, singleIngredient);
								tile.updateInfusingRecipe();
								if (!player.isCreative()) 
									heldItem.shrink(1);
								world.sendBlockUpdated(pos, state, state, 2);
								if(tile.getWaterAmount() > 0) {
									world.playLocalSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 0.3f, 0.9f + world.rand.nextFloat() * 0.3f);
								} else {
									world.playLocalSound(null, pos, SoundEvents.ENTITY_ITEMFRAME_ADD_ITEM, SoundCategory.BLOCKS, 0.3f, 0.9f + world.rand.nextFloat() * 0.3f);
								}
								return true;
							}
						}
					}
				}

				if(!heldItem.isEmpty() && heldItem.getItem() instanceof ItemLifeCrystal) {
					if(tile.getItem(TileEntityInfuser.MAX_INGREDIENTS + 1).isEmpty()) {
						tile.setItem(TileEntityInfuser.MAX_INGREDIENTS + 1, heldItem);
						tile.updateInfusingRecipe();
						if (!player.isCreative()) player.setItemInHand(hand, ItemStack.EMPTY);
					}
					return true;
				}
			}

			if(player.isCrouching() && !tile.hasInfusion()) {
				for (int i = TileEntityInfuser.MAX_INGREDIENTS; i >= 0; i--) {
					if(!tile.getItem(i).isEmpty()) {
						ItemEntity itemEntity = player.drop(tile.getItem(i).copy(), false);
						if(itemEntity != null) itemEntity.setPickUpDelay(0);
						tile.setItem(i, ItemStack.EMPTY);
						tile.updateInfusingRecipe();
						world.sendBlockUpdated(pos, state, state, 2);
						return true;
					}
				}
			}

			if(player.isCrouching()) {
				if (heldItem.getItem() instanceof ItemBLBucket && ((ItemBLBucket) heldItem.getItem()).getFluid(heldItem) == null && tile.hasInfusion() && tile.getWaterAmount() >= Fluid.BUCKET_VOLUME) {
					ItemStack infusionBucket = new ItemStack(ItemRegistry.BL_BUCKET_INFUSION, 1, heldItem.getMetadata());
					CompoundNBT nbtCompound = new CompoundNBT();
					infusionBucket.setTag(nbtCompound);
					nbtCompound.putString("infused", "Infused");
					ListNBT nbtList = new ListNBT();
					for (int i = 0; i < tile.getContainerSize() - 1; i++) {
						ItemStack stackInSlot = tile.getItem(i);
						if (!stackInSlot.isEmpty()) {
							nbtList.appendTag(stackInSlot.save(new CompoundNBT()));
						}
					}
					nbtCompound.setTag("ingredients", nbtList);
					nbtCompound.putInt("infusionTime", tile.getInfusionTime());
					tile.extractFluids(new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME));
					if (heldItem.getCount() == 1) {
						player.setItemInHand(hand, infusionBucket.copy());
						return true;
					} else {
						if (!player.addItemStackToInventory(infusionBucket.copy()))
							player.drop(infusionBucket.copy(), false);
						heldItem.shrink(1);
						return true;
					}
				}

				if(!tile.getItem(TileEntityInfuser.MAX_INGREDIENTS + 1).isEmpty()) {
					ItemEntity itemEntity = player.drop(tile.getItem(TileEntityInfuser.MAX_INGREDIENTS + 1).copy(), false);
					if(itemEntity != null) itemEntity.setPickUpDelay(0);
					tile.setItem(TileEntityInfuser.MAX_INGREDIENTS + 1, ItemStack.EMPTY);
					tile.updateInfusingRecipe();
					world.sendBlockUpdated(pos, state, state, 2);
					return true;
				}
			}
		}
		return true;
	}

	@Nullable
	private IFluidHandler getFluidHandler(IBlockReader world, BlockPos pos) {
		TileEntity tileentity = (TileEntity) world.getBlockEntity(pos);
		return tileentity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, BlockState state) {
		if(!level.isClientSide()) {
			IInventory tileInventory = (IInventory) world.getBlockEntity(pos);
			TileEntityInfuser tile = (TileEntityInfuser) world.getBlockEntity(pos);
			if (tileInventory != null && !tile.hasInfusion()) {
				for (int i = 0; i <= TileEntityInfuser.MAX_INGREDIENTS + 1; i++) {
					ItemStack stack = tileInventory.getItem(i);
					if (!stack.isEmpty()) {
						float f = 0.7F;
						double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						ItemEntity ItemEntity = new ItemEntity(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
						ItemEntity.setDefaultPickupDelay();
						world.addFreshEntity(ItemEntity);
					}
				}
			} else if (tileInventory != null && tile.hasInfusion()) {
				ItemStack stack = tileInventory.getItem(TileEntityInfuser.MAX_INGREDIENTS + 1);
				if (!stack.isEmpty()) {
					float f = 0.7F;
					double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
					double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
					double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
					ItemEntity ItemEntity = new ItemEntity(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
					ItemEntity.setDefaultPickupDelay();
					world.addFreshEntity(ItemEntity);
				}
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (world.getBlockEntity(pos) instanceof TileEntityInfuser) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			TileEntityInfuser infuser = (TileEntityInfuser) world.getBlockEntity(pos);
			if (infuser.getWaterAmount() > 0  && infuser.getTemperature() > 0) {
				int amount = infuser.waterTank.getFluidAmount();
				int capacity = infuser.waterTank.getCapacity();
				float size = 1F / capacity * amount;
				float xx = (float) x + 0.5F;
				float yy = (float) (y + 0.35F + size * 0.5F);
				float zz = (float) z + 0.5F;
				float fixedOffset = 0.25F;
				float randomOffset = rand.nextFloat() * 0.6F - 0.3F;
				if(rand.nextInt((101 - infuser.getTemperature()))/4 == 0) {
					float colors[] = infuser.currentInfusionColor;
					BLParticles.BUBBLE_INFUSION.spawn(world, xx + 0.3F - rand.nextFloat() * 0.6F, yy, zz + 0.3F - rand.nextFloat() * 0.6F, ParticleArgs.get().withScale(0.3F).withColor(colors[0], colors[1], colors[2], 1));
					if (rand.nextInt(10) == 0 && infuser.getTemperature() > 70)
						world.playLocalSound(xx, yy, zz, SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 1.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.5F, false);
				}
				if (infuser.getTemperature() >= 100) {
					BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx - fixedOffset), (double) y + 0.75D, (double) (zz + randomOffset));
					BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + fixedOffset), (double) y + 0.75D, (double) (zz + randomOffset));
					BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + randomOffset), (double) y + 0.75D, (double) (zz - fixedOffset));
					BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + randomOffset), (double) y + 0.75D, (double) (zz + fixedOffset));
				}
			}
		}
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(BlockState state) {
		return false;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return new TileEntityInfuser();
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public void fillWithRain(World world, BlockPos pos) {
		if (world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId && world.getBlockEntity(pos) instanceof TileEntityInfuser) {
			TileEntityInfuser tile = (TileEntityInfuser) world.getBlockEntity(pos);
			
			if(tile != null) {
				tile.fill(new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME), true);
			}
		}
	}
}