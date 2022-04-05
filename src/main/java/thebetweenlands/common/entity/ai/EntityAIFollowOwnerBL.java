package thebetweenlands.common.entity.ai;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Same as {@link EntityAIFollowOwner} but without caching the navigator
 */
public class EntityAIFollowOwnerBL extends EntityAIBase
{
	private final EntityTameable tameable;
	private LivingEntity owner;
	World world;
	private final double followSpeed;
	private int timeToRecalcPath;
	float maxDist;
	float minDist;
	private float oldWaterCost;

	public EntityAIFollowOwnerBL(EntityTameable tameableIn, double followSpeedIn, float minDistIn, float maxDistIn)
	{
		this.tameable = tameableIn;
		this.world = tameableIn.world;
		this.followSpeed = followSpeedIn;
		this.minDist = minDistIn;
		this.maxDist = maxDistIn;
		this.setMutexBits(3);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean canUse()
	{
		LivingEntity entitylivingbase = this.tameable.getOwner();

		if (entitylivingbase == null)
		{
			return false;
		}
		else if (entitylivingbase instanceof PlayerEntity && ((PlayerEntity)entitylivingbase).isSpectator())
		{
			return false;
		}
		else if (this.tameable.isSitting())
		{
			return false;
		}
		else if (this.tameable.getDistanceSq(entitylivingbase) < (double)(this.minDist * this.minDist))
		{
			return false;
		}
		else
		{
			this.owner = entitylivingbase;
			return true;
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean canContinueToUse()
	{
		return !this.tameable.getNavigation().noPath() && this.tameable.getDistanceSq(this.owner) > (double)(this.maxDist * this.maxDist) && !this.tameable.isSitting();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void start()
	{
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.tameable.getPathPriority(PathNodeType.WATER);
		this.tameable.setPathPriority(PathNodeType.WATER, 0.0F);
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	@Override
	public void stop()
	{
		this.owner = null;
		this.tameable.getNavigation().clearPath();
		this.tameable.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	@Override
	public void updateTask()
	{
		if (!this.tameable.isSitting())
		{
			this.tameable.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, (float)this.tameable.getVerticalFaceSpeed());

			if (--this.timeToRecalcPath <= 0)
			{
				this.timeToRecalcPath = 10;

				if (!this.tameable.getNavigation().tryMoveToEntityLiving(this.owner, this.followSpeed))
				{
					if (!this.tameable.getLeashed() && !this.tameable.isRiding())
					{
						if (this.tameable.getDistanceSq(this.owner) >= 144.0D)
						{
							int i = MathHelper.floor(this.owner.getX()) - 2;
							int j = MathHelper.floor(this.owner.getZ()) - 2;
							int k = MathHelper.floor(this.owner.getBoundingBox().minY);

							for (int l = 0; l <= 4; ++l)
							{
								for (int i1 = 0; i1 <= 4; ++i1)
								{
									if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.isTeleportFriendlyBlock(i, j, k, l, i1))
									{
										this.tameable.moveTo((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.tameable.yRot, this.tameable.xRot);
										this.tameable.getNavigation().clearPath();
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean isTeleportFriendlyBlock(int x, int z, int y, int xOffset, int zOffset)
	{
		BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
		BlockState iblockstate = this.world.getBlockState(blockpos);
		return iblockstate.getBlockFaceShape(this.world, blockpos, Direction.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(this.tameable) && this.world.isEmptyBlock(blockpos.above()) && this.world.isEmptyBlock(blockpos.above(2));
	}
}