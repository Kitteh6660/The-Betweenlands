package thebetweenlands.common.entity.movement;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.MobEntity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public class ObstructionAwareWalkNodeProcessor<T extends MobEntity & IPathObstructionAwareEntity> extends WalkNodeProcessor {
	protected T obstructionAwareEntity;
	protected boolean startFromGround = true;
	protected boolean checkObstructions = true;
	protected int pathingSizeOffsetX, pathingSizeOffsetY, pathingSizeOffsetZ;
	protected EnumSet<Direction> pathableFacings = EnumSet.of(Direction.DOWN);

	public void setObstructionAwareEntity(T obstructionAwareEntity) {
		this.obstructionAwareEntity = obstructionAwareEntity;
	}

	public void setStartPathOnGround(boolean startFromGround) {
		this.startFromGround = startFromGround;
	}

	public void setCheckObstructions(boolean checkObstructions) {
		this.checkObstructions = checkObstructions;
	}

	public void setCanPathWalls(boolean canPathWalls) {
		if(canPathWalls) {
			this.pathableFacings.add(Direction.NORTH);
			this.pathableFacings.add(Direction.EAST);
			this.pathableFacings.add(Direction.SOUTH);
			this.pathableFacings.add(Direction.WEST);
		} else {
			this.pathableFacings.remove(Direction.NORTH);
			this.pathableFacings.remove(Direction.EAST);
			this.pathableFacings.remove(Direction.SOUTH);
			this.pathableFacings.remove(Direction.WEST);
		}
	}

	public void setCanPathCeiling(boolean canPathCeiling) {
		if(canPathCeiling) {
			this.pathableFacings.add(Direction.UP);
		} else {
			this.pathableFacings.remove(Direction.UP);
		}
	}

	@Override
	public void init(IBlockReader sourceIn, MobEntity mob) {
		super.init(sourceIn, mob);
		this.pathingSizeOffsetX = Math.max(1, MathHelper.floor(this.entity.width / 2.0f + 1));
		this.pathingSizeOffsetY = Math.max(1, MathHelper.floor(this.entity.height + 1));
		this.pathingSizeOffsetZ = Math.max(1, MathHelper.floor(this.entity.width / 2.0f + 1));
	}

	@Override
	public PathPoint getStart() {
		int startPosY;

		if(this.getCanSwim() && this.entity.isInWater()) {
			startPosY = (int)this.entity.getBoundingBox().minY;
			BlockPos.Mutable blockpos$BlockPos.Mutable = new BlockPos.Mutable(MathHelper.floor(this.entity.getX()), startPosY, MathHelper.floor(this.entity.getZ()));

			for(Block block = this.blockaccess.getBlockState(blockpos$BlockPos.Mutable).getBlock(); block == Blocks.FLOWING_WATER || block == Blocks.WATER; block = this.blockaccess.getBlockState(blockpos$BlockPos.Mutable).getBlock()) {
				++startPosY;
				blockpos$BlockPos.Mutable.setPos(MathHelper.floor(this.entity.getX()), startPosY, MathHelper.floor(this.entity.getZ()));
			}
		} else if(this.entity.onGround || !this.startFromGround) {
			startPosY = MathHelper.floor(this.entity.getBoundingBox().minY + 0.5D);
		} else {
			BlockPos checkPos;

			for(checkPos = new BlockPos(this.entity); (this.blockaccess.getBlockState(checkPos).getMaterial() == Material.AIR || this.blockaccess.getBlockState(checkPos).getBlock().isPassable(this.blockaccess, checkPos)) && checkPos.getY() > 0; checkPos = checkPos.below()) { }

			startPosY = checkPos.above().getY();
		}

		BlockPos startPosXZ = new BlockPos(this.entity);
		PathNodeType nodeType = this.getPathNodeType(this.entity, startPosXZ.getX(), startPosY, startPosXZ.getZ());

		if(this.entity.getPathPriority(nodeType) < 0.0F) {
			Set<BlockPos> startPosOptions = new HashSet<>();
			startPosOptions.add(new BlockPos(this.entity.getBoundingBox().minX, (double)startPosY, this.entity.getBoundingBox().minZ));
			startPosOptions.add(new BlockPos(this.entity.getBoundingBox().minX, (double)startPosY, this.entity.getBoundingBox().maxZ));
			startPosOptions.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)startPosY, this.entity.getBoundingBox().minZ));
			startPosOptions.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)startPosY, this.entity.getBoundingBox().maxZ));

			for(BlockPos startPosOption : startPosOptions) {
				PathNodeType pathnodetype = this.getPathNodeType(this.entity, startPosOption.getX(), startPosOption.getY(), startPosOption.getZ());

				if(this.entity.getPathPriority(pathnodetype) >= 0.0F) {
					return this.openPoint(startPosOption.getX(), startPosOption.getY(), startPosOption.getZ());
				}
			}
		}

		return this.openPoint(startPosXZ.getX(), startPosY, startPosXZ.getZ());
	}

	private boolean shouldAvoidPathOptions(PathPoint[] options) {
		return options == null || options.length == 0 || ((options[0] == null || options[0].nodeType == PathNodeType.OPEN || options[0].costMalus != 0.0F) && (options.length <= 1 || (options[1] == null || options[1].nodeType == PathNodeType.OPEN || options[1].costMalus != 0.0F)));
	}

	private boolean isPassableWithExemptions(IBlockReader blockAccess, int x, int y, int z, @Nullable EnumSet<Direction> exemptions, @Nullable EnumSet<Direction> requirement, @Nullable EnumSet<Direction> found) {
		if(requirement != null && found == null) {
			found = EnumSet.noneOf(Direction.class);
		}

		for(int xo = 0; xo < this.entitySizeX; xo++) {
			for(int yo = 0; yo < this.entitySizeY; yo++) {
				for(int zo = 0; zo < this.entitySizeZ; zo++) {
					PathNodeType nodeType = this.getPathNodeType(blockAccess, x + xo, y + yo, z + zo, exemptions, found);

					if(nodeType != PathNodeType.OPEN && this.entity.getPathPriority(nodeType) >= 0.0f) {
						if(requirement != null) {
							for(Direction facing : requirement) {
								if(found.contains(facing)) {
									return true;
								}
							}

							return false;
						}

						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance){
		int openedNodeCount = 0;
		int stepHeight = 0;

		PathNodeType nodeTypeAbove = this.getPathNodeType(this.entity, currentPoint.x, currentPoint.y + 1, currentPoint.z);

		if(this.entity.getPathPriority(nodeTypeAbove) >= 0.0F) {
			stepHeight = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
		}

		BlockPos posDown = (new BlockPos(currentPoint.x, currentPoint.y, currentPoint.z)).below();

		double height = (double)currentPoint.y - (1.0D - this.blockaccess.getBlockState(posDown).getBoundingBox(this.blockaccess, posDown).maxY);

		PathPoint[] pathsPZ = this.getSafePoints(currentPoint.x, currentPoint.y, currentPoint.z + 1, stepHeight, height, Direction.SOUTH, this.checkObstructions);
		PathPoint[] pathsNX = this.getSafePoints(currentPoint.x - 1, currentPoint.y, currentPoint.z, stepHeight, height, Direction.WEST, this.checkObstructions);
		PathPoint[] pathsPX = this.getSafePoints(currentPoint.x + 1, currentPoint.y, currentPoint.z, stepHeight, height, Direction.EAST, this.checkObstructions);
		PathPoint[] pathsNZ = this.getSafePoints(currentPoint.x, currentPoint.y, currentPoint.z - 1, stepHeight, height, Direction.NORTH, this.checkObstructions);

		for(int k = 0; k < pathsPZ.length; k++) {
			if(pathsPZ[k] != null && !pathsPZ[k].visited && pathsPZ[k].distanceTo(targetPoint) < maxDistance) {
				pathOptions[openedNodeCount++] = pathsPZ[k];
			}
		}

		for(int k = 0; k < pathsNX.length; k++) {
			if(pathsNX[k] != null && !pathsNX[k].visited && pathsNX[k].distanceTo(targetPoint) < maxDistance) {
				pathOptions[openedNodeCount++] = pathsNX[k];
			}
		}

		for(int k = 0; k < pathsPX.length; k++) {
			if(pathsPX[k] != null && !pathsPX[k].visited && pathsPX[k].distanceTo(targetPoint) < maxDistance) {
				pathOptions[openedNodeCount++] = pathsPX[k];
			}
		}

		for(int k = 0; k < pathsNZ.length; k++) {
			if(pathsNZ[k] != null && !pathsNZ[k].visited && pathsNZ[k].distanceTo(targetPoint) < maxDistance) {
				pathOptions[openedNodeCount++] = pathsNZ[k];
			}
		}

		PathPoint[] pathsNY = null;
		if(this.checkObstructions || this.pathableFacings.size() > 1) {
			boolean hasValidPath = false;

			if(this.pathableFacings.size() > 1) {
				EnumSet<Direction> found = EnumSet.noneOf(Direction.class);
				this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y - 1, currentPoint.z, EnumSet.of(Direction.UP, Direction.DOWN), null, found);
				hasValidPath = this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(Direction.UP, Direction.DOWN), found, null);
			}

			if(hasValidPath) {
				pathsNY = this.getSafePoints(currentPoint.x, currentPoint.y - 1, currentPoint.z, stepHeight, height, Direction.DOWN, this.checkObstructions);
				for(int k = 0; k < pathsNY.length; k++) {
					if(pathsNY[k] != null && !pathsNY[k].visited && pathsNY[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsNY[k];
					}
				}
			}
		}

		PathPoint[] pathsPY = null;
		if(this.pathableFacings.size() > 1) {
			EnumSet<Direction> found = EnumSet.noneOf(Direction.class);
			this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y + 1, currentPoint.z, EnumSet.of(Direction.UP, Direction.DOWN), null, found);

			if(this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(Direction.UP, Direction.DOWN), found, null)) {
				pathsPY = this.getSafePoints(currentPoint.x, currentPoint.y + 1, currentPoint.z, stepHeight, height, Direction.UP, this.checkObstructions);
				for(int k = 0; k < pathsPY.length; k++) {
					if(pathsPY[k] != null && !pathsPY[k].visited && pathsPY[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsPY[k];
					}
				}
			}
		}

		boolean avoidPathNZ = this.shouldAvoidPathOptions(pathsNZ);
		boolean avoidPathPZ = this.shouldAvoidPathOptions(pathsPZ);
		boolean avoidPathPX = this.shouldAvoidPathOptions(pathsPX);
		boolean avoidPathNX = this.shouldAvoidPathOptions(pathsNX);

		if(avoidPathNZ && avoidPathNX) {
			PathPoint[] pathsNXNZ = this.getSafePoints(currentPoint.x - 1, currentPoint.y, currentPoint.z - 1, stepHeight, height, Direction.NORTH, this.checkObstructions);

			for(int k = 0; k < pathsNXNZ.length; k++) {
				if(pathsNXNZ[k] != null && !pathsNXNZ[k].visited && pathsNXNZ[k].distanceTo(targetPoint) < maxDistance) {
					pathOptions[openedNodeCount++] = pathsNXNZ[k];
				}
			}
		}

		if(avoidPathNZ && avoidPathPX) {
			PathPoint[] pathsPXNZ = this.getSafePoints(currentPoint.x + 1, currentPoint.y, currentPoint.z - 1, stepHeight, height, Direction.NORTH, this.checkObstructions);

			for(int k = 0; k < pathsPXNZ.length; k++) {
				if(pathsPXNZ[k] != null && !pathsPXNZ[k].visited && pathsPXNZ[k].distanceTo(targetPoint) < maxDistance) {
					pathOptions[openedNodeCount++] = pathsPXNZ[k];
				}
			}
		}

		if(avoidPathPZ && avoidPathNX) {
			PathPoint[] pathsNXPZ = this.getSafePoints(currentPoint.x - 1, currentPoint.y, currentPoint.z + 1, stepHeight, height, Direction.SOUTH, this.checkObstructions);

			for(int k = 0; k < pathsNXPZ.length; k++) {
				if(pathsNXPZ[k] != null && !pathsNXPZ[k].visited && pathsNXPZ[k].distanceTo(targetPoint) < maxDistance) {
					pathOptions[openedNodeCount++] = pathsNXPZ[k];
				}
			}
		}

		if(avoidPathPZ && avoidPathPX) {
			PathPoint[] pathsPXPZ = this.getSafePoints(currentPoint.x + 1, currentPoint.y, currentPoint.z + 1, stepHeight, height, Direction.SOUTH, this.checkObstructions);

			for(int k = 0; k < pathsPXPZ.length; k++) {
				if(pathsPXPZ[k] != null && !pathsPXPZ[k].visited && pathsPXPZ[k].distanceTo(targetPoint) < maxDistance) {
					pathOptions[openedNodeCount++] = pathsPXPZ[k];
				}
			}
		}

		if(this.pathableFacings.size() > 1) {
			boolean avoidPathPY = this.shouldAvoidPathOptions(pathsPY);
			boolean avoidPathNY = this.shouldAvoidPathOptions(pathsNY);

			if(avoidPathNY && avoidPathNX && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(Direction.UP, Direction.EAST), null, null)) {
				PathPoint[] pathsNYNX = this.getSafePoints(currentPoint.x - 1, currentPoint.y - 1, currentPoint.z, stepHeight, height, Direction.WEST, this.checkObstructions);

				for(int k = 0; k < pathsNYNX.length; k++) {
					if(pathsNYNX[k] != null && !pathsNYNX[k].visited && pathsNYNX[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsNYNX[k];
					}
				}
			}

			if(avoidPathNY && avoidPathPX && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(Direction.UP, Direction.WEST), null, null)) {
				PathPoint[] pathsNYPX = this.getSafePoints(currentPoint.x + 1, currentPoint.y - 1, currentPoint.z, stepHeight, height, Direction.EAST, this.checkObstructions);

				for(int k = 0; k < pathsNYPX.length; k++) {
					if(pathsNYPX[k] != null && !pathsNYPX[k].visited && pathsNYPX[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsNYPX[k];
					}
				}
			}

			if(avoidPathNY && avoidPathNZ && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(Direction.UP, Direction.SOUTH), null, null)) {
				PathPoint[] pathsNYNZ = this.getSafePoints(currentPoint.x, currentPoint.y - 1, currentPoint.z - 1, stepHeight, height, Direction.NORTH, this.checkObstructions);

				for(int k = 0; k < pathsNYNZ.length; k++) {
					if(pathsNYNZ[k] != null && !pathsNYNZ[k].visited && pathsNYNZ[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsNYNZ[k];
					}
				}
			}

			if(avoidPathNY && avoidPathPZ && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(Direction.UP, Direction.NORTH), null, null)) {
				PathPoint[] pathsNYPZ = this.getSafePoints(currentPoint.x, currentPoint.y - 1, currentPoint.z + 1, stepHeight, height, Direction.SOUTH, this.checkObstructions);

				for(int k = 0; k < pathsNYPZ.length; k++) {
					if(pathsNYPZ[k] != null && !pathsNYPZ[k].visited && pathsNYPZ[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsNYPZ[k];
					}
				}
			}

			if(avoidPathPY && avoidPathNX && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(Direction.DOWN, Direction.EAST), null, null)) {
				PathPoint[] pathsPYNX = this.getSafePoints(currentPoint.x - 1, currentPoint.y + 1, currentPoint.z, stepHeight, height, Direction.WEST, this.checkObstructions);

				for(int k = 0; k < pathsPYNX.length; k++) {
					if(pathsPYNX[k] != null && !pathsPYNX[k].visited && pathsPYNX[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsPYNX[k];
					}
				}
			}

			if(avoidPathPY && avoidPathPX && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(Direction.DOWN, Direction.WEST), null, null)) {
				PathPoint[] pathsPYPX = this.getSafePoints(currentPoint.x + 1, currentPoint.y + 1, currentPoint.z, stepHeight, height, Direction.EAST, this.checkObstructions);

				for(int k = 0; k < pathsPYPX.length; k++) {
					if(pathsPYPX[k] != null && !pathsPYPX[k].visited && pathsPYPX[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsPYPX[k];
					}
				}
			}

			if(avoidPathPY && avoidPathNZ && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(Direction.DOWN, Direction.SOUTH), null, null)) {
				PathPoint[] pathsPYNZ = this.getSafePoints(currentPoint.x, currentPoint.y + 1, currentPoint.z - 1, stepHeight, height, Direction.NORTH, this.checkObstructions);

				for(int k = 0; k < pathsPYNZ.length; k++) {
					if(pathsPYNZ[k] != null && !pathsPYNZ[k].visited && pathsPYNZ[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsPYNZ[k];
					}
				}
			}

			if(avoidPathPY && avoidPathPZ && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(Direction.DOWN, Direction.NORTH), null, null)) {
				PathPoint[] pathsPYPZ = this.getSafePoints(currentPoint.x, currentPoint.y + 1, currentPoint.z + 1, stepHeight, height, Direction.SOUTH, this.checkObstructions);

				for(int k = 0; k < pathsPYPZ.length; k++) {
					if(pathsPYPZ[k] != null && !pathsPYPZ[k].visited && pathsPYPZ[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsPYPZ[k];
					}
				}
			}
		}

		return openedNodeCount;
	}

	@Nullable
	private PathPoint[] getSafePoints(int x, int y, int z, int stepHeight, double height, Direction facing, boolean allowBlocked) {
		PathPoint directPathPoint = null;

		BlockPos pos = new BlockPos(x, y, z);
		BlockPos posDown = pos.below();

		double blockHeight = (double)y - (1.0D - this.blockaccess.getBlockState(posDown).getBoundingBox(this.blockaccess, posDown).maxY);

		if (blockHeight - height > 1.125D) {
			return new PathPoint[0];
		} else {
			PathNodeType nodeType = this.getPathNodeType(this.entity, x, y, z);

			float malus = this.obstructionAwareEntity.getPathingMalus(this.obstructionAwareEntity, nodeType, pos); //Replaces MobEntity#getPathPriority

			double halfWidth = (double)this.entity.width / 2.0D;

			PathPoint[] result = new PathPoint[1];

			if(malus >= 0.0F && (allowBlocked || nodeType != PathNodeType.BLOCKED)) {
				directPathPoint = this.openPoint(x, y, z);
				directPathPoint.nodeType = nodeType;
				directPathPoint.costMalus = Math.max(directPathPoint.costMalus, malus);

				//Allow other nodes than this obstructed node to also be considered, otherwise jumping/pathing up steps does no longer work
				if(directPathPoint.nodeType == PathNodeType.BLOCKED) {
					result = new PathPoint[2];
					result[1] = directPathPoint;
					directPathPoint = null;
				}
			}

			if(nodeType == PathNodeType.WALKABLE) {
				result[0] = directPathPoint;
				return result;
			} else {
				if (directPathPoint == null && stepHeight > 0 && nodeType != PathNodeType.FENCE && nodeType != PathNodeType.TRAPDOOR && facing.getAxis() != Direction.Axis.Y) {
					PathPoint[] pointsAbove = this.getSafePoints(x, y + 1, z, stepHeight - 1, height, facing, false);
					directPathPoint = pointsAbove.length > 0 ? pointsAbove[0] : null;

					if(directPathPoint != null && (directPathPoint.nodeType == PathNodeType.OPEN || directPathPoint.nodeType == PathNodeType.WALKABLE) && this.entity.width < 1.0F) {
						double offsetX = (double)(x - facing.getStepX()) + 0.5D;
						double offsetZ = (double)(z - facing.getStepZ()) + 0.5D;

						AxisAlignedBB checkAabb = new AxisAlignedBB(offsetX - halfWidth, (double)y + 0.001D, offsetZ - halfWidth, offsetX + halfWidth, (double)((float)y + this.entity.height), offsetZ + halfWidth);
						AxisAlignedBB blockAabb = this.blockaccess.getBlockState(pos).getBoundingBox(this.blockaccess, pos);
						AxisAlignedBB enclosingAabb = checkAabb.expand(0.0D, blockAabb.maxY - 0.002D, 0.0D);

						if(this.entity.world.collidesWithAnyBlock(enclosingAabb)) {
							directPathPoint = null;
						}
					}
				}

				if(nodeType == PathNodeType.OPEN) {
					AxisAlignedBB checkAabb = new AxisAlignedBB((double)x - halfWidth + 0.5D, (double)y + 0.001D, (double)z - halfWidth + 0.5D, (double)x + halfWidth + 0.5D, (double)((float)y + this.entity.height), (double)z + halfWidth + 0.5D);

					if(this.entity.world.collidesWithAnyBlock(checkAabb)) {
						result[0] = null;
						return result;
					}

					if(this.entity.width >= 1.0F) {
						for(Direction pathableFacing : this.pathableFacings) {
							PathNodeType nodeTypeAtFacing = this.getPathNodeType(this.entity, x + pathableFacing.getStepX() * this.pathingSizeOffsetX, y + (pathableFacing == Direction.DOWN ? -1 : pathableFacing == Direction.UP ? this.pathingSizeOffsetY : 0), z + pathableFacing.getStepZ() * this.pathingSizeOffsetZ);

							if(nodeTypeAtFacing == PathNodeType.BLOCKED) {
								directPathPoint = this.openPoint(x, y, z);
								directPathPoint.nodeType = PathNodeType.WALKABLE;
								directPathPoint.costMalus = Math.max(directPathPoint.costMalus, malus);
								result[0] = directPathPoint;
								return result;
							}
						}
					}


					boolean cancelFallDown = false;
					PathPoint fallPathPoint = null;

					int fallDistance = 0;
					int preFallY = y;

					while(y > 0 && nodeType == PathNodeType.OPEN) {
						--y;

						if(fallDistance++ >= this.entity.getMaxFallHeight() || y == 0) {
							cancelFallDown = true;
							break;
						}

						nodeType = this.getPathNodeType(this.entity, x, y, z);
						malus = this.entity.getPathPriority(nodeType);

						if(nodeType != PathNodeType.OPEN && malus >= 0.0F) {
							fallPathPoint = this.openPoint(x, y, z);
							fallPathPoint.nodeType = nodeType;
							fallPathPoint.costMalus = Math.max(fallPathPoint.costMalus, malus);
							break;
						}

						if(malus < 0.0F) {
							cancelFallDown = true;
						}
					}

					boolean hasPathUp = false;

					if(this.pathableFacings.size() > 1) {
						nodeType = this.getPathNodeType(this.entity, x, preFallY, z);
						malus = this.entity.getPathPriority(nodeType);

						if(nodeType != PathNodeType.OPEN && malus >= 0.0F) {
							if(fallPathPoint != null) {
								result = new PathPoint[2];
								result[1] = fallPathPoint;
							}

							result[0] = directPathPoint = this.openPoint(x, preFallY, z);
							directPathPoint.nodeType = nodeType;
							directPathPoint.costMalus = Math.max(directPathPoint.costMalus, malus);
							hasPathUp = true;
						}
					}

					if(fallPathPoint != null) {
						if(!hasPathUp) {
							result[0] = directPathPoint = fallPathPoint;
						} else {
							result = new PathPoint[2];
							result[0] = directPathPoint;
							result[1] = fallPathPoint;
						}
					}

					if(fallPathPoint != null) {
						float bridingMalus = this.obstructionAwareEntity.getBridgePathingMalus(this.obstructionAwareEntity, new BlockPos(x, preFallY, z), fallPathPoint);

						if(bridingMalus >= 0.0f) {
							result = new PathPoint[2];
							result[0] = directPathPoint;

							PathPoint bridgePathPoint = this.openPoint(x, preFallY, z);
							bridgePathPoint.nodeType = PathNodeType.WALKABLE;
							bridgePathPoint.costMalus = Math.max(bridgePathPoint.costMalus, bridingMalus);
							result[1] = bridgePathPoint;
						}
					}

					if(cancelFallDown && !hasPathUp) {
						result[0] = null;
						if(result.length == 2) {
							result[1] = null;
						}
						return result;
					}
				}

				result[0] = directPathPoint;
				return result;
			}
		}
	}

	private PathNodeType getPathNodeType(MobEntity entitylivingIn, int x, int y, int z) {
		return this.getPathNodeType(this.blockaccess, x, y, z, entitylivingIn, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanOpenDoors(), this.getCanEnterDoors());
	}

	@Override
	public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z) {
		return this.getPathNodeType(blockaccessIn, x, y, z, EnumSet.noneOf(Direction.class), null);
	}

	protected PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z, @Nullable EnumSet<Direction> exemptions, @Nullable EnumSet<Direction> found) {
		PathNodeType nodeType = this.getPathNodeTypeRaw(blockaccessIn, x, y, z);

		if(nodeType == PathNodeType.OPEN && y >= 1) {
			PooledMutableBlockPos pos = PooledMutableBlockPos.retain();

			facings: for(Direction pathableFacing : this.pathableFacings) {
				if(exemptions == null || !exemptions.contains(pathableFacing)) {
					int checkHeight = pathableFacing.getAxis() != Axis.Y ? Math.min(4, this.pathingSizeOffsetY - 1) : 0;

					int cx = x + pathableFacing.getStepX() * this.pathingSizeOffsetX;
					int cy = y + (pathableFacing == Direction.DOWN ? -1 : pathableFacing == Direction.UP ? this.pathingSizeOffsetY : 0);
					int cz = z + pathableFacing.getStepZ() * this.pathingSizeOffsetZ;

					for(int yo = 0; yo <= checkHeight; yo++) {
						pos.setPos(cx, cy + yo, cz);

						Block block = blockaccessIn.getBlockState(pos).getBlock();
						PathNodeType offsetNodeType = this.getPathNodeTypeRaw(blockaccessIn, pos.getX(), pos.getY(), pos.getZ());
						nodeType = offsetNodeType != PathNodeType.WALKABLE && offsetNodeType != PathNodeType.OPEN && offsetNodeType != PathNodeType.WATER && offsetNodeType != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;

						if(offsetNodeType == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA) {
							nodeType = PathNodeType.DAMAGE_FIRE;
						}

						if(offsetNodeType == PathNodeType.DAMAGE_CACTUS) {
							nodeType = PathNodeType.DAMAGE_CACTUS;
						}

						if(nodeType == PathNodeType.WALKABLE) {
							if(found != null) {
								found.add(pathableFacing);
							}
							break facings;
						}
					}
				}
			}

			pos.release();
		}

		nodeType = this.checkNeighborBlocks(blockaccessIn, x, y, z, nodeType);
		return nodeType;
	}

	@Override
	public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z, MobEntity entity, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
		BlockPos pos = new BlockPos(entity);

		EnumSet<PathNodeType> applicablePathNodeTypes = EnumSet.noneOf(PathNodeType.class);
		PathNodeType centerPathNodeType = this.getPathNodeType(blockaccessIn, x, y, z, xSize, ySize, zSize, canBreakDoorsIn, canEnterDoorsIn, applicablePathNodeTypes, PathNodeType.BLOCKED, pos);

		if(applicablePathNodeTypes.contains(PathNodeType.FENCE)) {
			return PathNodeType.FENCE;
		} else {
			PathNodeType selectedPathNodeType = PathNodeType.BLOCKED;

			for(PathNodeType applicablePathNodeType : applicablePathNodeTypes) {
				if(entity.getPathPriority(applicablePathNodeType) < 0.0F) {
					return applicablePathNodeType;
				}

				float p1 = entity.getPathPriority(applicablePathNodeType);
				float p2 = entity.getPathPriority(selectedPathNodeType);
				if(p1 > p2 || (p1 == p2 && !(selectedPathNodeType == PathNodeType.WALKABLE && applicablePathNodeType == PathNodeType.OPEN)) || (p1 == p2 && selectedPathNodeType == PathNodeType.OPEN && applicablePathNodeType == PathNodeType.WALKABLE)) {
					selectedPathNodeType = applicablePathNodeType;
				}
			}

			if(centerPathNodeType == PathNodeType.OPEN && entity.getPathPriority(selectedPathNodeType) == 0.0F) {
				return PathNodeType.OPEN;
			} else {
				return selectedPathNodeType;
			}
		}
	}
}