package thebetweenlands.common.block.container;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.properties.DirectionProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.block.IAspectFogBlock;
import thebetweenlands.api.block.IDungeonFogBlock;
import thebetweenlands.api.recipes.ICenserRecipe;
import thebetweenlands.api.recipes.ICenserRecipe.EffectColorType;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.inventory.container.ContainerCenser;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.tile.TileEntityCenser;

public class BlockCenser extends BasicBlock implements ITileEntityProvider, IDungeonFogBlock, IAspectFogBlock {
	public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;
	public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");

	public BlockCenser() {
		super(Material.ROCK);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ENABLED, true));
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

		if (world.getBlockEntity(pos) instanceof TileEntityCenser) {
			TileEntityCenser tile = (TileEntityCenser) world.getBlockEntity(pos);

			if (player.isCrouching()) {
				return false;
			}

			if (!heldItem.isEmpty()) {
				IFluidHandler handler = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				if(handler != null) {
					IItemHandler playerInventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
					if (playerInventory != null) {
						FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainerAndStow(heldItem, tile, playerInventory, Integer.MAX_VALUE, player, !world.isClientSide());

						if (fluidActionResult.isSuccess()) {
							if (!world.isClientSide()) {
								player.setItemInHand(hand, fluidActionResult.getResult());
							}
							return true;
						}
					}
				}
			}

			if (!world.isClientSide() && tile != null) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_CENSER, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);

		if (tileEntity instanceof IInventory) {
			IInventory inventory = (IInventory) tileEntity;
			if(ContainerCenser.SLOT_INTERNAL < inventory.getContainerSize()) {
				//Destroy contents of internal slot since they shouldn't be dropped
				inventory.setItem(ContainerCenser.SLOT_INTERNAL, ItemStack.EMPTY);
			}
			InventoryHelper.dropInventoryItems(worldIn, pos, inventory);
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction facing = Direction.byIndex(meta & 0b111);

		if (facing.getAxis() == Direction.Axis.Y) {
			facing = Direction.NORTH;
		}

		return this.defaultBlockState().setValue(FACING, facing).setValue(ENABLED, (meta & 0b1000) > 0);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).getIndex() | (state.getValue(ENABLED) ? 0b1000 : 0);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		boolean enabled = !worldIn.isBlockPowered(pos);
		if(enabled != ((Boolean)state.getValue(ENABLED)).booleanValue()) {
			worldIn.setBlockState(pos, state.setValue(ENABLED, enabled), 3);
		}
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
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityCenser();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean shouldSideBeRendered(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, ENABLED);
	}

	@Override
	public boolean isCreatingDungeonFog(IBlockReader world, BlockPos pos, BlockState state) {
		TileEntity te = world.getBlockEntity(pos);
		if(te instanceof TileEntityCenser) {
			return ((TileEntityCenser) te).getDungeonFogStrength(1) >= 0.1F;
		}
		return false;
	}

	@Override
	public IAspectType getAspectFogType(IBlockReader world, BlockPos pos, BlockState state) {
		TileEntity te = world.getBlockEntity(pos);
		if(te instanceof TileEntityCenser) {
			TileEntityCenser censer = (TileEntityCenser) te;

			if(censer.isRecipeRunning()) {
				ICenserRecipe<Object> recipe = censer.getCurrentRecipe();
	
				if(recipe != null) {
					return recipe.getAspectFogType(censer.getCurrentRecipeContext(), censer);
				}
			}
		}
		return null;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		TileEntity te = worldIn.getBlockEntity(pos);
		if(te instanceof TileEntityCenser) {
			TileEntityCenser censer = (TileEntityCenser) te;

			if(censer.isRecipeRunning()) {
				ICenserRecipe<Object> recipe = censer.getCurrentRecipe();

				if(recipe != null) {
					int fogColor = recipe.getEffectColor(censer.getCurrentRecipeContext(), censer, EffectColorType.FOG);

					float r = ((fogColor >> 16) & 0xFF) / 255f;
					float g = ((fogColor >> 8) & 0xFF) / 255f;
					float b = ((fogColor >> 0) & 0xFF) / 255f;

					for(int i = 0; i < 3 + rand.nextInt(5); i++) {
						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(worldIn, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 
								ParticleArgs.get()
								.withMotion((rand.nextFloat() - 0.5f) * 0.08f, rand.nextFloat() * 0.01F + 0.005F, (rand.nextFloat() - 0.5f) * 0.08f)
								.withScale(2.0f + rand.nextFloat() * 8.0F)
								.withColor(r, g, b, 0.1f)
								.withData(80, true, 0.05F, true)));
					}
				}
			}
		}
	}
}