package thebetweenlands.common.entity.movement;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import thebetweenlands.common.entity.mobs.EntityClimberBase;

public class ClimberMoveHelper extends EntityMoveHelper {
	protected int courseChangeCooldown;
	protected boolean blocked = false;

	protected final EntityClimberBase climber;

	public ClimberMoveHelper(EntityClimberBase entity) {
		super(entity);
		this.climber = entity;
	}

	@Override
	public void onUpdateMoveHelper() {
		double speed = this.climber.getMovementSpeed() * this.speed;

		if(this.action == EntityMoveHelper.Action.MOVE_TO) {
			this.action = EntityMoveHelper.Action.WAIT;

			Pair<Direction, Vector3d> walkingSide = this.climber.getWalkingSide();
			Vector3d normal = new Vector3d(walkingSide.getLeft().getStepX(), walkingSide.getLeft().getStepY(), walkingSide.getLeft().getStepZ());

			double dx = this.getX() - this.entity.getX();
			double dy = this.getY() + 0.5f - (this.entity.getY() + this.entity.height / 2.0f);
			double dz = this.getZ() - this.entity.getZ();

			Vector3d dir = new Vector3d(dx, dy, dz);

			Vector3d targetDir = dir.subtract(normal.scale(dir.dotProduct(normal)));
			double targetDist = targetDir.length();
			targetDir = targetDir.normalize();

			if(targetDist < 0.0001D) {
				this.entity.setMoveForward(0);
			} else {
				EntityClimberBase.Orientation orientation = this.climber.getOrientation(1);

				float rx = (float)orientation.forward.dotProduct(targetDir);
				float ry = (float)orientation.right.dotProduct(targetDir);

				this.entity.yRot = this.limitAngle(this.entity.yRot, 270.0f - (float)Math.toDegrees(Math.atan2(rx, ry)), 90.0f);

				this.entity.setAIMoveSpeed((float)speed);
			}
		} else if(this.action == EntityMoveHelper.Action.WAIT) {
			this.entity.setMoveForward(0);
		}
	}
}