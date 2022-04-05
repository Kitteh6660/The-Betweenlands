package thebetweenlands.common.block.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.Half;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.item.herblore.ItemAspectVial;
import thebetweenlands.common.item.herblore.ItemDentrothystVial;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityRepeller;

public class BlockRepeller extends ContainerBlock {
	
	public static final DirectionProperty FACING = HorizontalFaceBlock.FACING;
	public static final EnumProperty<Half> HALF = EnumProperty.<Half>create("half", Half.class);

	protected static final VoxelShape AABB_BOTTOM = Block.box(0.15F, 0, 0.15F, 0.85F, 1.0F, 0.85F);
	protected static final VoxelShape AABB_TOP = Block.box(0.15F, 0, 0.15F, 0.85F, 0.7F, 0.85F);

	public BlockRepeller(Properties properties) {
		super(properties);
		/*super(Material.WOOD);
		setHardness(1.0F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
		registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, Half.BOTTOM));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return state.getValue(HALF) == Half.TOP ? AABB_TOP : AABB_BOTTOM;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		BlockState state = this.defaultBlockState();
		if(state.getValue(HALF) == Half.BOTTOM) {
			return new TileEntityRepeller();
		}
		return null;
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer, Hand hand){
		return this.defaultBlockState().setValue(HALF, Half.BOTTOM).setValue(FACING, placer.getDirection());
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlock(pos, state.setValue(HALF, Half.BOTTOM).setValue(FACING, placer.getDirection()), 2);
		world.setBlock(pos.above(), this.defaultBlockState().setValue(HALF, Half.TOP).setValue(FACING, placer.getDirection()), 2);
	}

	@Override
	public ActionResultType use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction facing, BlockRayTraceResult hitResult){
		if(state.getValue(HALF) == Half.TOP && world.getBlockState(pos.below()).getBlock() == this) {
			this.onBlockActivated(world, pos.below(), world.getBlockState(pos.below()), player, hand, facing, hitX, hitY, hitZ);
		} else if(state.getValue(HALF) == Half.BOTTOM) {
			TileEntityRepeller tile = (TileEntityRepeller) world.getBlockEntity(pos);
			ItemStack held = player.getItemInHand(hand);
			if(!player.isCrouching() && !held.isEmpty()) {
				if(held.getItem() == ItemRegistry.SHIMMER_STONE.get()) {
					if(!tile.hasShimmerstone()) {
						tile.addShimmerstone();
						if(!player.isCreative()) {
							held.shrink(1);
							if(held.getCount() <= 0) {
								player.setItemInHand(hand, ItemStack.EMPTY);
							}
						}
					}
					return true;
				} else if(held.getItem() instanceof ItemAspectVial) {
					if(tile.hasShimmerstone()) {
						if(tile.getFuel() < tile.getMaxFuel()) {
							ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(held);
							int amount = aspectContainer.get(AspectRegistry.BYARIIS);
							int loss = 10; //Loss when adding
							if(amount >= loss) {
								if(!world.isClientSide()) {
									int added = tile.addFuel(amount - loss);
									if(!player.isCreative()) {
										int leftAmount = amount - added - loss;
										if(leftAmount > 0) {
											aspectContainer.set(AspectRegistry.BYARIIS, leftAmount);
										} else {
											player.setItemInHand(hand, held.getItem().getContainerItem(held));
										}
									}
								}
								player.swing(hand);
								return true;
							}
						}
					} else {
						if(!world.isClientSide()) {
							player.displayClientMessage(new TranslationTextComponent("chat.repeller.shimmerstone_missing"), true);
						}
					}
				} else if(held.getItem() instanceof ItemDentrothystVial && tile.getFuel() > 0) {
					if (held.getDamageValue() == 0 || held.getDamageValue() == 2) {
						ItemStack newStack = held.getItem() == ItemRegistry.ORANGE_DENTROTHYST_VIAL.get() ? new ItemStack(ItemRegistry.ORANGE_ASPECT_VIAL.get()) : new ItemStack(ItemRegistry.GREEN_ASPECT_VIAL.get()); // new ItemStack(ItemRegistry.ASPECT_VIAL, 1, held.getDamageValue() == 0 ? 0 : 1);
						if(!world.isClientSide()) {
							ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(newStack);
							aspectContainer.set(AspectRegistry.BYARIIS, tile.removeFuel(Amounts.VIAL));
						}
						held.shrink(1);
						if(held.getCount() <= 0) {
							player.setItemInHand(hand, ItemStack.EMPTY);
						}
						if(!player.inventory.add(newStack)) {
							player.drop(newStack, false);
						}
						return true;
					}
				}
			} else if(player.isCrouching() && held.isEmpty() && tile.hasShimmerstone()) {
				tile.removeShimmerstone();
				ItemStack stack = new ItemStack(ItemRegistry.SHIMMER_STONE.get(), 1);
				if(!player.inventory.add(stack)) {
					player.drop(stack, false);
				}
				return true;
			} else if(!player.isCrouching() && held.isEmpty()) {
				if(!world.isClientSide()) {
					tile.cycleRadiusState();
				}
				player.swing(hand);
			}
		}
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.isSideSolid(pos.below(), Direction.UP) && worldIn.isEmptyBlock(pos.above());
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.checkAndBreakBlock(worldIn, pos);
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
	}

	protected void checkAndBreakBlock(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		Half half = state.getValue(HALF);
		if ((half == Half.BOTTOM && (!world.isSideSolid(pos.below(), Direction.UP) || world.getBlockState(pos.above()).getBlock() != this))
				|| (half == Half.TOP) && world.getBlockState(pos.below()).getBlock() != this) {
			this.breakBlock(world, pos, state);
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
		if(!worldIn.isClientSide()) {
			TileEntityRepeller tile = (TileEntityRepeller) worldIn.getBlockEntity(pos);
			if(tile != null) {
				float f = 0.7F;

				if(tile.hasShimmerstone()) {
					double d0 = worldIn.random.nextFloat() * f + (1.0F - f) * 0.5D;
					double d1 = worldIn.random.nextFloat() * f + (1.0F - f) * 0.5D;
					double d2 = worldIn.random.nextFloat() * f + (1.0F - f) * 0.5D;
					ItemEntity ItemEntity = new ItemEntity(worldIn, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, new ItemStack(ItemRegistry.SHIMMER_STONE, 1));
					ItemEntity.setDefaultPickupDelay();
					worldIn.addFreshEntity(ItemEntity);
				}

				double d0 = worldIn.random.nextFloat() * f + (1.0F - f) * 0.5D;
				double d1 = worldIn.random.nextFloat() * f + (1.0F - f) * 0.5D;
				double d2 = worldIn.random.nextFloat() * f + (1.0F - f) * 0.5D;
				ItemEntity ItemEntity = new ItemEntity(worldIn, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, new ItemStack(BlockRegistry.REPELLER, 1));
				ItemEntity.setDefaultPickupDelay();
				worldIn.addFreshEntity(ItemEntity);
			}
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public List<ItemStack> getDrops(IBlockReader world, BlockPos pos, BlockState state, int fortune) {
		return Collections.emptyList();
	}

	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(rand.nextInt(6) == 0) {
			BlockState state = worldIn.getBlockState(pos);
			if(state.getValue(HALF) == Half.BOTTOM) {
				TileEntityRepeller tile = (TileEntityRepeller) worldIn.getBlockEntity(pos);
				if(tile != null && tile.isRunning())  {
					Direction facing = state.getValue(FACING);
					for(int i = 0; i < 60; i++) {
						float rot = (float) (Math.PI * 2.0F / 60.0F * i + Math.PI * rand.nextFloat() / 60.0F);
						double radius = Math.max(tile.getRadius(0.0F), 1.0D);
						double rotX = Math.sin(rot) * radius;
						double rotZ = Math.cos(rot) * radius;
						double xOff = -facing.getStepX() * 0.23F;
						double zOff = facing.getStepZ() * 0.23F;
						double centerX = pos.getX() + 0.5F + xOff;
						double centerY = pos.getY() + 1.3F;
						double centerZ = pos.getZ() + 0.5F - zOff;
						List<Vector3d> points = new ArrayList<Vector3d>();
						points.add(new Vector3d(centerX, centerY, centerZ));
						points.add(new Vector3d(centerX, centerY + radius, centerZ));
						points.add(new Vector3d(centerX + rotX, centerY + radius, centerZ + rotZ));
						points.add(new Vector3d(centerX + rotX, pos.getY() + 0.1D, centerZ + rotZ));
						BLParticles.ANIMATOR.spawn(worldIn, centerX, centerY, centerZ, ParticleArgs.get().withData(points));
					}
				}
			}
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
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, FACING, HALF);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		int facing = (meta >> 1) & 0b11;
		boolean isUpper = (meta & 1) == 1;
		return this.defaultBlockState().setValue(HALF, isUpper ? Half.TOP : Half.BOTTOM).setValue(FACING, Direction.byHorizontalIndex(facing));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		int facing = ((Direction)state.getValue(FACING)).getHorizontalIndex();
		boolean isUpper = state.getValue(HALF) == Half.TOP;
		int meta = facing << 1;
		meta |= isUpper ? 1 : 0;
		return meta;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World world, BlockPos pos) {
		TileEntity tile = world.getBlockEntity(pos);
		if (tile instanceof TileEntityRepeller) {
			TileEntityRepeller repeller = (TileEntityRepeller) tile;
			return Math.round((float)repeller.getFuel() / (float)repeller.getMaxFuel() * 16.0F);
		}
		return 0;
	}

	@Override
	public boolean isFullBlock(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
}