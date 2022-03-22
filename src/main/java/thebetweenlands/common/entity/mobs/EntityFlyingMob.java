package thebetweenlands.common.entity.mobs;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.common.entity.movement.PathNavigateFlyingBL;

public class EntityFlyingMob extends MobEntity {
	
	public EntityFlyingMob(EntityType<? extends MobEntity> entity, World worldIn) {
		super(entity, worldIn);
	}

	@Override
	protected PathNavigate createNavigator(World world) {
		return new PathNavigateFlyingBL(this, world, 2);
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	@Override
	public void travel(float strafe, float vertical, float forward) {
		if (this.isInWater()) {
			this.moveRelative(0.02F, new Vector3d(strafe, vertical, forward));
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.800000011920929D;
			this.motionY *= 0.800000011920929D;
			this.motionZ *= 0.800000011920929D;
		} else if (this.isInLava()) {
			this.moveRelative(0.02F, new Vector3d(strafe, vertical, forward));
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
		} else {
			float f = 0.91F;

			if (this.onGround) {
				BlockPos underPos = new BlockPos(MathHelper.floor(this.getX()),
						MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.getZ()));
				BlockState underState = this.level.getBlockState(underPos);
				f = underState.getBlock().getSlipperiness(underState, this.level, underPos, this) * 0.91F;
			}

			float f1 = 0.16277136F / (f * f * f);
			this.moveRelative(this.onGround ? 0.1F * f1 : 0.02F, new Vector3d(strafe, vertical, forward));
			f = 0.91F;

			if (this.onGround) {
				BlockPos underPos = new BlockPos(MathHelper.floor(this.getX()),
						MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.getZ()));
				BlockState underState = this.level.getBlockState(underPos);
				f = underState.getBlock().getSlipperiness(underState, this.level, underPos, this) * 0.91F;
			}

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double) f;
			this.motionY *= (double) f;
			this.motionZ *= (double) f;
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double d1 = this.getX() - this.xOld;
		double d0 = this.getZ() - this.zOld;
		float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

		if (f2 > 1.0F) {
			f2 = 1.0F;
		}

		this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}

	@Override
	public boolean isOnLadder() {
		return false;
	}
}
