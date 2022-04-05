package thebetweenlands.common.block.terrain;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.farming.BlockGenericCrop;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.util.AdvancedStateMap;

public class BlockPuddle extends Block implements ITintedBlock, IStateMappedBlock {

	private static final VoxelShape AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D/16.0D, 1.0D);
	public final static IntegerProperty AMOUNT = IntegerProperty.create("amount", 0, 15);

	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");

	public BlockPuddle(Properties properties) {
		super(properties);
		/*super(Material.GROUND);
		setHardness(0.1F);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
		//setTickRandomly(true);
		registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(AMOUNT, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[] { AMOUNT, NORTH, EAST, SOUTH, WEST });
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(AMOUNT, meta);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(AMOUNT);
	}

	@Override
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(AMOUNT);
	}

	@Override
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		PooledMutableBlockPos offset = PooledMutableBlockPos.retain();
		PooledMutableBlockPos offsetDown = PooledMutableBlockPos.retain();

		for(Direction facing : Direction.Plane.HORIZONTAL) {
			offset.setPos(pos.getX() + facing.getStepX(), pos.getY(), pos.getZ() + facing.getStepZ());
			BlockState offsetState = worldIn.getBlockState(offset);

			offsetDown.setPos(pos.getX() + facing.getStepX(), pos.getY() - 1, pos.getZ() + facing.getStepZ());
			BlockState offsetDownState = worldIn.getBlockState(offsetDown);

			BooleanProperty prop;
			switch(facing) {
			default:
			case NORTH:
				prop = NORTH;
				break;
			case EAST:
				prop = EAST;
				break;
			case SOUTH:
				prop = SOUTH;
				break;
			case WEST:
				prop = WEST;
				break;
			}

			state = state.setValue(prop, offsetState.getBlock() instanceof BlockPuddle == false && offsetDownState.isSideSolid(worldIn, offsetDown, Direction.UP) && offsetDownState.getBlockFaceShape(worldIn, offsetDown, Direction.UP) == BlockFaceShape.SOLID);
		}

		offset.release();
		offsetDown.release();

		return state;
	}

	@Override
	public void updateTick(World world, BlockPos pos, BlockState state, Random rand) {
		if(!level.isClientSide()) {
			int amount = state.getValue(AMOUNT);
			if(!BetweenlandsWorldStorage.forWorld(world).getEnvironmentEventRegistry().heavyRain.isActive()) {
				world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				amount = 0;
			} else if(world.canBlockSeeSky(pos)) {
				amount = Math.min(amount + rand.nextInt(6), 15);
				world.setBlock(pos, state.setValue(AMOUNT, amount), 2);
			}
			if(amount > 2) {
				amount = Math.max(0, amount - 3);
				world.setBlock(pos, state.setValue(AMOUNT, amount), 2);
				for(int xo = -1; xo <= 1; xo++) {
					for(int zo = -1; zo <= 1; zo++) {
						BlockPos newPos = pos.offset(xo, 0, zo);
						if((xo == 0 && zo == 0) || xo*xo == zo*zo) continue;
						if((world.isEmptyBlock(newPos) || world.getBlockState(newPos).getBlock() instanceof BlockGenericCrop) && this.canPlaceBlockAt(world, newPos)) {
							world.setBlock(newPos, defaultBlockState());
						} else if(world.getBlockState(newPos).getBlock() == BlockRegistry.PUDDLE) {
							world.setBlock(newPos, state.setValue(AMOUNT, Math.min(amount + rand.nextInt(6), 15)), 2);
						}
					}
				}
			}
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return AABB;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean shouldSideBeRendered(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (blockAccess.getBlockState(pos.offset(side)).getBlock() == this) {
			return false;
		} else {
			return side == Direction.UP || side == Direction.DOWN || super.shouldSideBeRendered(blockState, blockAccess, pos, side);
		}
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return Items.AIR;
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
	public boolean isPassable(IBlockReader worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		return (block.isReplaceable(world, pos) || block instanceof BlockGenericCrop) && world.isSideSolid(pos.below(), Direction.UP) && world.getBlockState(pos.below()).getBlockFaceShape(world, pos.below(), Direction.UP) == BlockFaceShape.SOLID;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!world.getBlockState(pos.below()).isSideSolid(world, pos, Direction.UP)) {
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public int getColorMultiplier(BlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		if (worldIn == null || pos == null) return -1;
		int avgRed = 0;
		int avgGreen = 0;
		int avgBlue = 0;

		for (int xOff = -1; xOff <= 1; ++xOff) {
			for (int yOff = -1; yOff <= 1; ++yOff) {
				int colorMultiplier = worldIn.getBiome(pos.offset(xOff, 0, yOff)).getWaterColor();
				avgRed += (colorMultiplier & 16711680) >> 16;
			avgGreen += (colorMultiplier & 65280) >> 8;
					avgBlue += colorMultiplier & 255;
			}
		}

		return (avgRed / 9 & 255) << 16 | (avgGreen / 9 & 255) << 8 | avgBlue / 9 & 255;
	}

	@Override
	public boolean canCollideCheck(BlockState state, boolean hitIfLiquid) {
		return false;
	}

	@Nullable
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return NULL_AABB;
	}

	@Override
	public boolean isReplaceable(IBlockReader worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public SoundType getSoundType(BlockState state, World world, BlockPos pos, Entity entity) {
		BlockState stateBelow = world.getBlockState(pos.below());
		return stateBelow.getBlock().getSoundType(stateBelow, world, pos.below(), entity);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		if(entityIn.level.isClientSide() && entityIn instanceof PlayerEntity && entityIn.getY() <= pos.getY() + 0.01f && entityIn.tickCount % 5 == 0) {
			float strength = MathHelper.sqrt(entityIn.motionX * entityIn.motionX * 0.2D + entityIn.motionY * entityIn.motionY + entityIn.motionZ * entityIn.motionZ * 0.2D) * 0.2f;

			if(strength > 0.01f) {
				entityIn.playSound(SoundEvents.ENTITY_GENERIC_SWIM, strength * 8, 1.0F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.4F);

				for(int j = 0; (float)j < 10.0F + entityIn.width * 20.0F; ++j) {
					float rx = (worldIn.rand.nextFloat() * 2.0F - 1.0F) * entityIn.width;
					float rz = (worldIn.rand.nextFloat() * 2.0F - 1.0F) * entityIn.width;
					worldIn.addParticle(ParticleTypes.WATER_SPLASH, entityIn.getX() + rx, pos.getY() + 0.1f, entityIn.getZ() + rz, entityIn.motionX + (worldIn.rand.nextFloat() - 0.5f) * strength * 20, entityIn.motionY, entityIn.motionZ + (worldIn.rand.nextFloat() - 0.5f) * strength * 20);
				}
			}
		}
	}
}
