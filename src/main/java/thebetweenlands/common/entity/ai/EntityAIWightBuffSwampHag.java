package thebetweenlands.common.entity.ai;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.network.clientbound.MessageWightVolatileParticles;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityAIWightBuffSwampHag extends EntityAIBase {
	protected final EntityWight wight;
	protected World world;
	protected final PathNavigate navigator;

	protected int cooldown;

	protected EntitySwampHag hag = null;

	public EntityAIWightBuffSwampHag(EntityWight wight) {
		this.wight = wight;
		this.world = wight.world;
		this.navigator = wight.getNavigation();
		this.cooldown = 10 + this.level.random.nextInt(20);
		this.setMutexBits(1 << 8);
	}

	@Override
	public boolean canUse() {
		boolean canBuff = this.wight.getAttackTarget() != null && !this.wight.isRiding() && this.getTargetSwampHag() != null;
		if(canBuff) {
			if(this.cooldown <= 0 && this.level.random.nextInt(20) == 0) {
				return true;
			}
			this.cooldown--;
		}
		return false;
	}

	@Override
	public void start() {
		this.hag = this.getTargetSwampHag();
	}

	@Override
	public boolean canContinueToUse() {
		return this.hag != null && this.hag.isEntityAlive();
	}

	@Override
	public void stop() {
		this.navigator.clearPath();
		this.cooldown = 80 + this.level.random.nextInt(60);
		if(!this.wight.isRiding() && (this.hag == null || !this.hag.isEntityAlive())) {
			this.wight.setVolatile(false);
		}
	}

	@Override
	public void updateTask() {
		if(this.hag != null) {
			if(!this.wight.isVolatile()) {
				this.wight.setVolatile(true);
				 TheBetweenlands.networkWrapper.sendToAllAround(new MessageWightVolatileParticles(this.wight), new TargetPoint(this.wight.dimension, this.wight.getX(), this.wight.getY(), this.wight.getZ(), 32));
                 this.world.playLocalSound(null, this.wight.getX(), this.wight.getY(), this.wight.getZ(), SoundRegistry.WIGHT_ATTACK, SoundCategory.HOSTILE, 1.6F, 1.0F);
			}

			if(!this.wight.isRiding()) {
				if (this.wight.getDistance(this.hag) < 1.75D) {
					this.wight.startRiding(this.hag);
				}

				this.wight.getLookHelper().setLookPositionWithEntity(this.hag, 10.0F, (float)this.wight.getVerticalFaceSpeed());
				this.wight.getMoveHelper().setMoveTo(this.hag.getX(), this.hag.getY(), this.hag.getZ(), 1);
			}
		}
	}

	@Nullable
	protected EntitySwampHag getTargetSwampHag() {
		LivingEntity target = this.wight.getAttackTarget();
		if(target != null) {
			double range = 16.0D;
			AxisAlignedBB aabb = this.wight.getBoundingBox().inflate(range);
			EntitySwampHag closestSuitableToTarget = null;
			List<EntitySwampHag> nearby = this.world.getEntitiesOfClass(EntitySwampHag.class, aabb);
			for(EntitySwampHag hag : nearby) {
				if(hag.getAttackTarget() == target && hag.getPassengers().isEmpty() && hag.getDistance(this.wight) <= range && (closestSuitableToTarget == null || hag.getDistance(target) <= closestSuitableToTarget.getDistance(target))) {
					closestSuitableToTarget = hag;
				}
			}
			return closestSuitableToTarget;
		}
		return null;
	}
}