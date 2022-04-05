package thebetweenlands.common.entity.movement;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class PathNavigateFlyingBL extends PathNavigator {
	protected BlockPos targetPos;
	protected long lastTimeUpdated;

	public PathNavigateFlyingBL(MobEntity entitylivingIn, World worldIn) {
		super(entitylivingIn, worldIn);
	}

	public PathNavigateFlyingBL(MobEntity entitylivingIn, World worldIn, int preferredMinHeight) {
		super(entitylivingIn, worldIn);
		if(this.nodeProcessor instanceof FlyingNodeProcessorBL) {
			((FlyingNodeProcessorBL)this.nodeProcessor).preferredMinHeight = preferredMinHeight;
		}
	}

	@Override
	protected PathFinder getPathFinder() {
		return new FlyingPathFinder(this.nodeProcessor = new FlyingNodeProcessorBL());
	}

	@Override
	public void onUpdateNavigation() {
		++this.totalTicks;

		if (this.tryUpdatePath) {
			this.updatePath();
		}

		if (!this.noPath()) {
			if (this.canNavigate()) {
				this.pathFollow();
			} else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
				Vector3d vec3d = this.getEntityPosition();
				Vector3d vec3d1 = this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex());

				if (vec3d.y > vec3d1.y && !this.entity.onGround && MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d1.x) && MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d1.z)) {
					this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
				}
			}

			this.debugPathFinding();

			if (!this.noPath()) {
				Vector3d vec3d2 = this.currentPath.getPosition(this.entity);
				BlockPos blockpos = (new BlockPos(vec3d2)).below();
				AxisAlignedBB axisalignedbb = this.world.getBlockState(blockpos).getBoundingBox(this.world, blockpos);
				vec3d2 = vec3d2.subtract(0.0D, 1.0D - axisalignedbb.maxY, 0.0D);
				this.entity.getMoveHelper().setMoveTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
			}
		}
	}

	@Override
	protected boolean canNavigate() {
		return !this.isInLiquid();
	}

	@Override
	protected Vector3d getEntityPosition() {
		return new Vector3d(this.entity.getX(), this.entity.getY() + (double) this.entity.height * 0.5D, this.entity.getZ());
	}

	@Override
	protected void pathFollow() {
		Vector3d currentPosition = this.getEntityPosition();
		float f = this.entity.width * this.entity.width;

		if (currentPosition.squareDistanceTo(this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex())) < (double) f) {
			this.currentPath.incrementPathIndex();
		}

		for (int j = Math.min(this.currentPath.getCurrentPathIndex() + 6, this.currentPath.getCurrentPathLength() - 1); j > this.currentPath.getCurrentPathIndex(); --j) {
			Vector3d pathNodePosition = this.currentPath.getVectorFromIndex(this.entity, j);

			if (pathNodePosition.squareDistanceTo(currentPosition) <= 256.0D && this.isDirectPathBetweenPoints(currentPosition, pathNodePosition, 0, 0, 0)) {
				this.currentPath.setCurrentPathIndex(j);
				break;
			}
		}

		this.checkForStuck(currentPosition);
	}

	@Override
	protected void removeSunnyPath() {
		// super.removeSunnyPath();
	}

	@Override
	protected boolean isDirectPathBetweenPoints(Vector3d start, Vector3d end, int sizeX, int sizeY, int sizeZ) {
		RayTraceResult raytraceresult = this.world.rayTraceBlocks(start, new Vector3d(end.x, end.y + (double) this.entity.height * 0.5D, end.z), false, true, false);
		return raytraceresult == null || raytraceresult.typeOfHit == RayTraceResult.Type.MISS;
	}

	@Override
	public boolean canEntityStandOnPos(BlockPos pos) {
		return !this.world.getBlockState(pos).isNormalCube();
	}

	@Override
	public void updatePath() {
		if (this.world.getGameTime() - this.lastTimeUpdated > 20L) {
			if (this.targetPos != null) {
				this.currentPath = null;
				this.currentPath = this.getPathToPos(this.targetPos);
				this.lastTimeUpdated = this.world.getGameTime();
				this.tryUpdatePath = false;
			}
		} else {
			this.tryUpdatePath = true;
		}
	}

	@Override
	@Nullable
	public Path getPathToPos(BlockPos pos) {
		if (!this.canNavigate()) {
			return null;
		} else if (this.currentPath != null && !this.currentPath.isFinished() && pos.equals(this.targetPos)) {
			return this.currentPath;
		} else {
			this.targetPos = pos;
			float f = this.getPathSearchRange();
			this.world.profiler.startSection("pathfind");
			BlockPos blockpos = new BlockPos(this.entity);
			int i = (int)(f + 8.0F);
			ChunkCache chunkcache = new ChunkCache(this.world, blockpos.add(-i, -i, -i), blockpos.add(i, i, i), 0);
			Path path = this.getPathFinder().findPath(chunkcache, this.entity, this.targetPos, f);
			this.world.profiler.endSection();
			return path;
		}
	}

	@Override
	@Nullable
	public Path getPathToEntityLiving(Entity entityIn) {
		if (!this.canNavigate()) {
			return null;
		} else {
			BlockPos blockpos = new BlockPos(entityIn);

			if (this.currentPath != null && !this.currentPath.isFinished() && blockpos.equals(this.targetPos)) {
				return this.currentPath;
			} else {
				this.targetPos = blockpos;
				float f = this.getPathSearchRange();
				this.world.profiler.startSection("pathfind");
				BlockPos blockpos1 = (new BlockPos(this.entity)).above();
				int i = (int)(f + 16.0F);
				ChunkCache chunkcache = new ChunkCache(this.world, blockpos1.add(-i, -i, -i), blockpos1.add(i, i, i), 0);
				Path path = this.getPathFinder().findPath(chunkcache, this.entity, new BlockPos(entityIn.getX(), entityIn.getBoundingBox().minY + entityIn.height / 2.0D, entityIn.getZ()), f);
				this.world.profiler.endSection();
				return path;
			}
		}
	}
}