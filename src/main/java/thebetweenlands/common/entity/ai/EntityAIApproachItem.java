package thebetweenlands.common.entity.ai;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.vector.Vector3d;

public class EntityAIApproachItem extends EntityAIBase {
	public final Predicate<Entity> entitySelector = new Predicate<Entity>() {
		@Override
		public boolean apply(Entity entity) {
			return entity.isEntityAlive() && entity.getDistance(EntityAIApproachItem.this.entity) <= EntityAIApproachItem.this.targetDistance
					&& entity instanceof ItemEntity && 
					(EntityAIApproachItem.this.ignoreDamage ? ((ItemEntity)entity).getItem().getItem() == EntityAIApproachItem.this.targetItem.getItem() : ((ItemEntity)entity).getItem().isItemEqual(EntityAIApproachItem.this.targetItem));
		}
	};

	private EntityCreature entity;
	private double farSpeed;
	private double nearSpeed;
	private ItemEntity targetEntity;
	private Vector3d targetPos;
	private float targetDistance;
	private Path entityPathEntity;
	private PathNavigate entityPathNavigate;
	private ItemStack targetItem;
	private boolean ignoreDamage;
	private int distractionTime;
	private int distractionTicks;

	public EntityAIApproachItem(EntityCreature entity, Item item, int distractionTime, float targetDistance, double farSpeed, double nearSpeed) {
		this(entity, new ItemStack(item), distractionTime, targetDistance, farSpeed, nearSpeed, true);
	}

	public EntityAIApproachItem(EntityCreature entity, ItemStack item, int distractionTime, float targetDistance, double farSpeed, double nearSpeed, boolean ignoreDamage) {
		this.entity = entity;
		this.targetItem = item;
		this.targetDistance = targetDistance;
		this.farSpeed = farSpeed;
		this.nearSpeed = nearSpeed;
		this.entityPathNavigate = entity.getNavigator();
		this.ignoreDamage = ignoreDamage;
		this.distractionTime = distractionTime;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		List<ItemEntity> list = this.entity.world.getEntitiesOfClass(ItemEntity.class, this.entity.getBoundingBox().grow(this.targetDistance, 3, this.targetDistance), this.entitySelector);
		if (list == null || list.isEmpty()) {
			return false;
		}
		this.targetEntity = list.get(0);
		if(this.targetEntity == null || !this.targetEntity.isEntityAlive()) {
			return false;
		}
		this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ());
		Vector3d pos = new Vector3d(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ());
		this.targetPos = pos;
		boolean execute = this.entityPathEntity == null ? false : true;
		return execute;
	}

	@Override
	public boolean shouldContinueExecuting() {
		boolean cont = (this.entityPathEntity != null && this.targetEntity != null && this.targetEntity.isEntityAlive()) || !this.entityPathNavigate.noPath();
		return cont;
	}

	@Override
	public void startExecuting() {
		this.entityPathNavigate.setPath(this.entityPathEntity, this.farSpeed);
		this.distractionTicks = 0;
	}

	@Override
	public void resetTask() {
		this.targetEntity = null;
		this.entityPathNavigate.clearPath();
	}

	@Override
	public void updateTask() {
		if(this.targetEntity.getDistance(this.targetPos.x, this.targetPos.y, this.targetPos.z) > 0.5) {
			this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ());
			this.entityPathNavigate.setPath(this.entityPathEntity, this.farSpeed);
			this.targetPos = new Vector3d(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ());;
		}
		if (this.entity.getDistance(this.targetEntity) < 4.0D) {
			this.entity.getNavigator().setSpeed(this.getNearSpeed());
			if(this.entity.getDistance(this.targetEntity) < 2.0D) {
				this.distractionTicks++;
				if(this.distractionTicks > this.getDistractionTime()) {
					this.targetEntity.remove();
					this.onPickup();
					this.resetTask();
				}
			}
		} else {
			this.entity.getNavigator().setSpeed(this.getFarSpeed());
		}
	}

	protected double getNearSpeed() {
		return this.nearSpeed;
	}

	protected double getFarSpeed() {
		return this.farSpeed;
	}

	protected int getDistractionTime() {
		return this.distractionTime;
	}

	protected void onPickup() {}
}
