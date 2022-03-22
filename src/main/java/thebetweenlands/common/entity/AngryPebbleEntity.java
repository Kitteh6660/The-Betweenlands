package thebetweenlands.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;

public class AngryPebbleEntity extends ThrowableEntity {
	
	public AngryPebbleEntity(EntityType<? extends AngryPebbleEntity> entity, World world) {
		super(entity, world);
	}

	/*public EntityAngryPebble(World world, LivingEntity entity) {
		super(world, entity);
	}*/

	@Override
	public void tick() {
		super.tick();
		if(this.tickCount > 400) {
			remove();
		}
	}

	@Override
	protected void onHit(RayTraceResult result) {
		if(result.hitInfo != null) {
			if(this.level.isClientSide()) {
				double particleX = MathHelper.floor(this.getX()) + this.random.nextFloat();
				double particleY = MathHelper.floor(this.getY()) + this.random.nextFloat();
				double particleZ = MathHelper.floor(this.getZ()) + this.random.nextFloat();
				for (int count = 0; count < 10; count++) {
					BLParticles.FLAME.spawn(this.level, particleX, particleY, particleZ);
				}
			} else {
				this.explode();
				this.remove();
			}
		}
	}

	/**
	 * Creates the explosion
	 */
	protected void explode() {
		boolean blockDamage = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
		this.level.explode(this, this.getX(), this.getY(), this.getZ(), 4.5F, blockDamage ? Mode.BREAK : Mode.NONE);
	}

	@Override
	protected void defineSynchedData() {
		// TODO Auto-generated method stub
		
	}
}