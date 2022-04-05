package thebetweenlands.common.world.gen.feature.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFace;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceLarge;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceSmall;
import thebetweenlands.common.entity.mobs.EntityWallFace.AnchorChecks;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;
import thebetweenlands.util.BlockShapeUtils;

public class WorldGenSpiritTreeStructure extends WorldGenerator {
	public static final int RADIUS_INNER_CIRLCE = 6;
	public static final int RADIUS_OUTER_CIRCLE = 14;
	
	private static final ThreadLocal<Boolean> CASCADING_GEN_MUTEX = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return false;
		}
	};
	
	public WorldGenSpiritTreeStructure() {
		super(true);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		if(CASCADING_GEN_MUTEX.get()) {
			return false;
		}
		
		CASCADING_GEN_MUTEX.set(true);
		
		try {
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
			LocationSpiritTree location = new LocationSpiritTree(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(position));
			location.addBounds(new AxisAlignedBB(new BlockPos(position)).inflate(14 + 18, 16, 14 + 18).offset(0, 6, 0));
			location.setLayer(0);
			location.setSeed(rand.nextLong());
			location.setVisible(true);
	
			SpiritTreeFeature genSpiritTree = new SpiritTreeFeature(location.getGuard(), location);
			if(genSpiritTree.generate(world, rand, position)) {
				this.generateWispCircle(world, rand, position, RADIUS_INNER_CIRLCE, 1, 2, location);
				this.generateWispCircle(world, rand, position, RADIUS_OUTER_CIRCLE, 1, 1, location);
	
				int rootsGenerated = 0;
				for(int i = 0; i < 80; i++) {
					double dir = rand.nextDouble() * Math.PI * 2;
					double dx = Math.cos(dir);
					double dz = Math.sin(dir);
					double bx = dx * 16 + dx * rand.nextDouble() * 16;
					double bz = dz * 16 + dz * rand.nextDouble() * 16;
					BlockPos root = position.add(bx, 0, bz);
					root = this.findGroundPosition(world, root);
					if(root != null && world.isEmptyBlock(root) && world.isEmptyBlock(root.above())) {
						this.generateRoot(world, rand, root, genSpiritTree, location);
						if(rootsGenerated++ > 12) {
							break;
						}
					}
				}
	
				for(int i = 0; i < 120; i++) {
					double dir = rand.nextDouble() * Math.PI * 2;
					double dx = Math.cos(dir);
					double dz = Math.sin(dir);
					double bx = dx * 16 + dx * rand.nextDouble() * 16;
					double bz = dz * 16 + dz * rand.nextDouble() * 16;
					BlockPos root = position.add(bx, 0, bz);
					root = this.findGroundPosition(world, root);
					if(root != null && world.isEmptyBlock(root) && world.isEmptyBlock(root.above())) {
						int height = 2 + rand.nextInt(4);
						for(int yo = 0; yo < height; yo++) {
							this.setBlock(world, root.above(yo), BlockRegistry.ROOT.defaultBlockState(), location);
						}
					}
				}
	
				this.trySpawnFace(world, rand, new EntitySpiritTreeFaceLarge(world), location.getLargeFacePositions());
	
				for(int i = 0; i < 8; i++) {
					this.trySpawnFace(world, rand, new EntitySpiritTreeFaceSmall(world), location.getSmallFacePositions());
				}
	
				location.setDirty(true);
				worldStorage.getLocalStorageHandler().addLocalStorage(location);
	
				return true;
			}
	
			return false;
		} finally {
			CASCADING_GEN_MUTEX.set(false);
		}
	}

	private void trySpawnFace(World world, Random rand, EntitySpiritTreeFace face, List<BlockPos> locations) {
		BlockPos faceAnchor = null;
		Direction faceFacing = null;

		List<BlockPos> largeFacePositions = new ArrayList<>();
		largeFacePositions.addAll(locations);
		Collections.shuffle(largeFacePositions, rand);
		largeFaceLoop: for(BlockPos anchor : largeFacePositions) {
			List<Direction> facings = new ArrayList<>();
			facings.addAll(Arrays.asList(Direction.Plane.HORIZONTAL));
			Collections.shuffle(facings, rand);
			for(Direction facing : facings) {
				if(face.checkAnchorAt(anchor, facing, Direction.UP, AnchorChecks.ALL) == 0) {
					faceAnchor = anchor;
					faceFacing = facing;
					break largeFaceLoop;
				}
			}
		}

		if(faceAnchor != null && faceFacing != null) {
			face.onInitialSpawn(world.getCurrentDifficultyAt(faceAnchor), null);
			face.setPositionToAnchor(faceAnchor, faceFacing, Direction.UP);
			world.addFreshEntity(face);
		}
	}

	@Nullable
	private BlockPos findGroundPosition(World world, BlockPos pos) {
		boolean hadAir = false;
		for(int yo = 6; yo >= -6; yo--) {
			BlockPos offsetPos = pos.above(yo);
			BlockState state = world.getBlockState(offsetPos);
			if(hadAir && SurfaceType.MIXED_GROUND.test(state)) {
				return offsetPos.above();
			}
			if(state.getBlock().isAir(state, world, offsetPos)) {
				hadAir = true;
			}
		}
		return null;
	}

	private void generateWispCircle(World world, Random rand, BlockPos center, int radius, int minHeight, int heightVar, LocationSpiritTree location) {
		List<BlockPos> circle = BlockShapeUtils.getCircle(center, radius, new ArrayList<>());
		for(int i = 0; i < circle.size(); i += 2 + rand.nextInt(2)) {
			if(i == circle.size() - 1) {
				break;
			}
			BlockPos pos = circle.get(i);
			pos = this.findGroundPosition(world, pos);
			if(pos != null && (world.isEmptyBlock(pos) || world.getBlockState(pos).getBlock() instanceof IPlantable)) {
				BlockPos wall = pos.below();
				this.setBlock(world, wall, BlockRegistry.MOSSY_BETWEENSTONE_BRICKS.defaultBlockState(), location);
				int height = minHeight + rand.nextInt(heightVar + 1);
				for(int yo = 0; yo < height; yo++) {
					wall = pos.above(yo);
					this.setBlock(world, wall, BlockRegistry.MOSSY_BETWEENSTONE_BRICK_WALL.defaultBlockState(), location);
				}
				BlockPos wisp = pos.above(height);
				if(rand.nextInt(4) == 0) {
					this.setBlock(world, wisp, BlockRegistry.WISP.defaultBlockState(), location);
					location.addGeneratedWispPosition(wisp);
				} else {
					location.addNotGeneratedWispPosition(wisp);
				}
			}
		}
	}

	private void generateRoot(World world, Random rand, BlockPos pos, SpiritTreeFeature tree, LocationSpiritTree location) {
		List<BlockPos> potentialBlocks = tree.generateBranchPositions(rand, pos, rand.nextInt(7), 32, 0.4D, 0.3D, (i, remainingBlocks) -> i < 2 ? 1 : (i > 4 ? -1 : 0), (i, length) -> true);
		int length = 0;
		for(int i = 0; i < potentialBlocks.size(); i++) {
			BlockPos block = potentialBlocks.get(i);
			if(i > 2 && !world.isEmptyBlock(block)) {
				length = i + 1;
				break;
			}
		}
		BlockPos prevPos = null;
		for(int i = 0; i < length; i++) {
			BlockPos block = potentialBlocks.get(i);
			if(prevPos != null) {
				int xo = block.getX() - prevPos.getX();
				int yo = block.getY() - prevPos.getY();
				int zo = block.getZ() - prevPos.getZ();
				int moves = Math.abs(xo) + Math.abs(yo) + Math.abs(zo);
				if(moves > 1) {
					List<BlockPos> choices = new ArrayList<>();
					if(xo != 0) {
						choices.add(new BlockPos(xo, 0, 0));
					}
					if(yo != 0) {
						choices.add(new BlockPos(0, yo, 0));
					}
					if(zo != 0) {
						choices.add(new BlockPos(0, 0, zo));
					}
					BlockPos filler = prevPos;
					for(int j = 0; j < moves; j++) {
						filler = filler.add(choices.remove(rand.nextInt(choices.size())));
						this.setBlock(world, filler, BlockRegistry.LOG_SPIRIT_TREE.defaultBlockState(), location);
					}
				}
			}
			this.setBlock(world, block, BlockRegistry.LOG_SPIRIT_TREE.defaultBlockState(), location);
			prevPos = block;
		}
	}

	protected void setBlock(World world, BlockPos pos, BlockState state, LocationSpiritTree location) {
		this.setBlockAndNotifyAdequately(world, pos, state);

		if(state.getBlock() != BlockRegistry.WISP) {
			location.getGuard().setGuarded(world, pos, true);
		}

		if(state.getBlock() == BlockRegistry.LOG_SPIRIT_TREE) {
			location.addSmallFacePosition(pos);
		}
	}
}
