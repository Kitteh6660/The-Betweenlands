package thebetweenlands.common.block.structure;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.capability.IPortalCapability;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationPortal;
import thebetweenlands.common.world.teleporter.TeleporterHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockTreePortal extends Block {
	
	public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.<Direction.Axis>create("axis", Direction.Axis.class, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z});

	protected static final VoxelShape X_AABB = Block.box(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D);
	protected static final VoxelShape Z_AABB = Block.box(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D);
	protected static final VoxelShape Y_AABB = Block.box(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);

	public BlockTreePortal(Properties properties) {
		super(properties);
		/*super(Material.PORTAL);
		setLightLevel(1.0F);
		setBlockUnbreakable();
		setSoundType2(SoundType.GLASS);*/
		registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
	}

	public static boolean makePortalX(World world, BlockPos pos) {
		world.setBlockAndUpdate(pos.offset(0, 2, -1), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_TOP_RIGHT).setValue(BlockPortalFrame.X_AXIS, true));
		world.setBlockAndUpdate(pos.offset(0, 2, 0), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.TOP).setValue(BlockPortalFrame.X_AXIS, true));
		world.setBlockAndUpdate(pos.offset(0, 2, 1), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_TOP_LEFT).setValue(BlockPortalFrame.X_AXIS, true));
		world.setBlockAndUpdate(pos.offset(0, 1, -1), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_RIGHT).setValue(BlockPortalFrame.X_AXIS, true));
		world.setBlockAndUpdate(pos.offset(0, 1, 1), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_LEFT).setValue(BlockPortalFrame.X_AXIS, true));
		world.setBlockAndUpdate(pos.offset(0, 0, -1), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_RIGHT).setValue(BlockPortalFrame.X_AXIS, true));
		world.setBlockAndUpdate(pos.offset(0, 0, 1), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_LEFT).setValue(BlockPortalFrame.X_AXIS, true));
		world.setBlockAndUpdate(pos.offset(0, -1, -1), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_BOTTOM_RIGHT).setValue(BlockPortalFrame.X_AXIS, true));
		world.setBlockAndUpdate(pos.offset(0, -1, 0), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.BOTTOM).setValue(BlockPortalFrame.X_AXIS, true));
		world.setBlockAndUpdate(pos.offset(0, -1, 1), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_BOTTOM_LEFT).setValue(BlockPortalFrame.X_AXIS, true));

		if (isPatternValidX(world, pos)) {
			world.setBlock(pos, BlockRegistry.TREE_PORTAL.get().defaultBlockState().setValue(AXIS, Direction.Axis.Z), 2);
			world.setBlock(pos.above(), BlockRegistry.TREE_PORTAL.get().defaultBlockState().setValue(AXIS, Direction.Axis.Z), 2);
			return true;
		}
		return false;
	}

	public static boolean makePortalZ(World world, BlockPos pos) {
		world.setBlockAndUpdate(pos.offset(-1, 2, 0), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_TOP_RIGHT).setValue(BlockPortalFrame.X_AXIS, false));
		world.setBlockAndUpdate(pos.offset(0, 2, 0), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.TOP).setValue(BlockPortalFrame.X_AXIS, false));
		world.setBlockAndUpdate(pos.offset(1, 2, 0), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_TOP_LEFT).setValue(BlockPortalFrame.X_AXIS, false));
		world.setBlockAndUpdate(pos.offset(-1, 1, 0), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_RIGHT).setValue(BlockPortalFrame.X_AXIS, false));
		world.setBlockAndUpdate(pos.offset(1, 1, 0), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_LEFT).setValue(BlockPortalFrame.X_AXIS, false));
		world.setBlockAndUpdate(pos.offset(-1, 0, 0), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_RIGHT).setValue(BlockPortalFrame.X_AXIS, false));
		world.setBlockAndUpdate(pos.offset(1, 0, 0), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_LEFT).setValue(BlockPortalFrame.X_AXIS, false));
		world.setBlockAndUpdate(pos.offset(-1, -1, 0), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_BOTTOM_RIGHT).setValue(BlockPortalFrame.X_AXIS, false));
		world.setBlockAndUpdate(pos.offset(0, -1, 0), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.BOTTOM).setValue(BlockPortalFrame.X_AXIS, false));
		world.setBlockAndUpdate(pos.offset(1, -1, 0), BlockRegistry.PORTAL_FRAME.get().defaultBlockState().setValue(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_BOTTOM_LEFT).setValue(BlockPortalFrame.X_AXIS, false));

		if (isPatternValidZ(world, pos)) {
			world.setBlock(pos, BlockRegistry.TREE_PORTAL.get().defaultBlockState().setValue(AXIS, Direction.Axis.X), 2);
			world.setBlock(pos.above(), BlockRegistry.TREE_PORTAL.get().defaultBlockState().setValue(AXIS, Direction.Axis.X), 2);
			return true;
		}

		return false;
	}

	public static boolean isPatternValidX(IBlockReader world, BlockPos pos) {
		// Layer 0
		if (!check(world, pos.below(), BlockRegistry.PORTAL_FRAME.get()) && !checkPortal(world, pos.below(), BlockRegistry.TREE_PORTAL.get(), Direction.Axis.Z)) {
			return false;
		}

		// Layer 1
		if (!check(world, pos.north(), BlockRegistry.PORTAL_FRAME.get())) {
			return false;
		}
		if (!check(world, pos.south(), BlockRegistry.PORTAL_FRAME.get())) {
			return false;
		}

		// Layer 2
		if (!check(world, pos.above().north(), BlockRegistry.PORTAL_FRAME.get())) {
			return false;
		}
		if (!check(world, pos.above().south(), BlockRegistry.PORTAL_FRAME.get())) {
			return false;
		}

		// Layer 3
		if (!check(world, pos.above(2), BlockRegistry.PORTAL_FRAME.get())) {
			return false;
		}

		return true;
	}

	public static boolean isPatternValidZ(IBlockReader world, BlockPos pos) {
		// Layer 0
		if (!check(world, pos.below(), BlockRegistry.PORTAL_FRAME.get()) && !checkPortal(world, pos.below(), BlockRegistry.TREE_PORTAL.get(), Direction.Axis.X)) {
			return false;
		}

		// Layer 1
		if (!check(world, pos.west(), BlockRegistry.PORTAL_FRAME.get())) {
			return false;
		}
		if (!check(world, pos.east(), BlockRegistry.PORTAL_FRAME.get())) {
			return false;
		}

		// Layer 2
		if (!check(world, pos.above().west(), BlockRegistry.PORTAL_FRAME.get())) {
			return false;
		}
		if (!check(world, pos.above().east(), BlockRegistry.PORTAL_FRAME.get())) {
			return false;
		}

		// Layer 3
		if (!check(world, pos.above(2), BlockRegistry.PORTAL_FRAME.get())) {
			return false;
		}

		return true;
	}

	private static boolean check(IBlockReader world, BlockPos pos, Block target) {
		return world.getBlockState(pos).getBlock() == target;
	}

	private static boolean checkPortal(IBlockReader world, BlockPos pos, Block target, Direction.Axis axis) {
		BlockState state = world.getBlockState(pos);
		return state.getBlock() == target && state.getValue(AXIS) == axis;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		switch ((Direction.Axis) state.getValue(AXIS)) {
		case X:
			return X_AABB;
		case Y:
		default:
			return Y_AABB;
		case Z:
			return Z_AABB;
		}
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		canBlockStay(worldIn, pos);
	}

	protected boolean canBlockStay(World world, BlockPos pos) {
		if (checkPortal(world, pos.above(), BlockRegistry.TREE_PORTAL.get(), Direction.Axis.Z) && isPatternValidX(world, pos))
			return true;
		if (checkPortal(world, pos.below(), BlockRegistry.TREE_PORTAL.get(), Direction.Axis.Z) && isPatternValidX(world, pos.below()))
			return true;
		if (checkPortal(world, pos.above(), BlockRegistry.TREE_PORTAL.get(), Direction.Axis.X) && isPatternValidZ(world, pos))
			return true;
		if (checkPortal(world, pos.below(), BlockRegistry.TREE_PORTAL.get(), Direction.Axis.X) && isPatternValidZ(world, pos.below()))
			return true;
		else {
			world.levelEvent(null, 2001, pos, Block.getIdFromBlock(BlockRegistry.TREE_PORTAL.get()));
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> state) {
		return new BlockStateContainer(this, new IProperty[]{AXIS});
	}

	@Override
	public int getMetaFromState(BlockState state) {
		Direction.Axis axis = state.getValue(AXIS);
		return axis == Direction.Axis.X ? 1 : (axis == Direction.Axis.Z ? 2 : 0);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(AXIS, (meta & 3) == 2 ? Direction.Axis.Z : Direction.Axis.X);
	}

	@Override
	public int quantityDropped(Random rand) {
		return 0;
	}


	@Nullable
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		if (!entityIn.isRiding() && !entityIn.isBeingRidden() && entityIn.timeUntilPortal <= 0 && BetweenlandsConfig.WORLD_AND_DIMENSION.portalDimensionWhitelistSet.isListed(entityIn.dimension)) {
			AxisAlignedBB aabb = state.getBoundingBox(worldIn, pos);
			if (aabb != null && aabb.offset(pos).intersects(entityIn.getBoundingBox())) {
				BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(worldIn);
				AxisAlignedBB entityAabb = entityIn.getBoundingBox();
				List<LocationPortal> portals = worldStorage.getLocalStorageHandler().getLocalStorages(LocationPortal.class, entityAabb, loc -> loc.intersects(entityAabb));
				LocationPortal portal = null;
				if(!portals.isEmpty()) {
					portal = portals.get(0);
				}
				int targetDim = BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId;
				if(portal != null && (portal.getOtherPortalPosition() != null || portal.hasTargetDimension())) {
					//Portal already linked, teleport to linked dimension
					targetDim = portal.getOtherPortalDimension();
				} else if (entityIn.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
					targetDim = BetweenlandsConfig.WORLD_AND_DIMENSION.portalDefaultReturnDimension;
				}
				if(targetDim != entityIn.dimension) {
					IPortalCapability cap = entityIn.getCapability(CapabilityRegistry.CAPABILITY_PORTAL, null);
					if (cap != null) {
						cap.setInPortal(true);
					} else if (!worldIn.isClientSide() && worldIn instanceof ServerWorld) {

						ServerWorld otherDim = ((ServerWorld) worldIn).getServer().getWorld(targetDim);
						if(otherDim != null) {
							TeleporterHandler.transferToDim(entityIn, otherDim);
						}
						entityIn.timeUntilPortal = entityIn.getPortalCooldown();
					}
				}
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return side != Direction.DOWN || side != Direction.UP;
	}

	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		for (int i = 0; i < 4; i++) {
			double particleX = pos.getX() + rand.nextFloat();
			double particleY = pos.getY() + rand.nextFloat();
			double particleZ = pos.getZ() + rand.nextFloat();
			double motionX;
			double motionY;
			double motionZ;
			float multi = (rand.nextFloat() * 2.0F - 1.0F) / 4.0F;

			motionX = (rand.nextFloat() - 0.5D) * 0.25D;
			motionY = (rand.nextFloat() - 0.5D) * 0.25D;
			motionZ = (rand.nextFloat() - 0.5D) * 0.25D;

			if (worldIn.getBlockState(pos.offset(-1, 0, 0)).getBlock() != this && worldIn.getBlockState(pos.offset(1, 0, 0)).getBlock() != this) {
				particleX = pos.getX() + 0.5D + 0.25D * multi;
				motionX = rand.nextFloat() * 2.0F * multi;
			} else {
				particleZ = pos.getZ() + 0.5D + 0.25D * multi;
				motionZ = rand.nextFloat() * 2.0F * multi;
			}

			BLParticles.PORTAL.spawn(worldIn, particleX, particleY, particleZ, ParticleArgs.get().withMotion(motionX, motionY, motionZ));
		}

		if (rand.nextInt(20) == 0) {
			worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundRegistry.PORTAL, SoundCategory.BLOCKS, 0.3F, rand.nextFloat() * 0.4F + 0.8F, false);
		}
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
		super.breakBlock(worldIn, pos, state);

		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(worldIn);
		List<LocationPortal> portals = worldStorage.getLocalStorageHandler().getLocalStorages(LocationPortal.class, Block.box(pos).inflate(1, 1, 1), null);
		for(LocationPortal portal : portals) {
			portal.validateAndRemove();
		}
	}
	
	@Override
	public BlockItem getItemBlock() {
		return null;
	}
}
