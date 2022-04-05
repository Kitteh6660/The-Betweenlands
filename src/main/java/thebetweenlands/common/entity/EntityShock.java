package thebetweenlands.common.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.item.armor.RubberBootsItem;
import thebetweenlands.common.network.clientbound.MessageShockArrowHit;

public class EntityShock extends Entity {
	
	private final EntityBLArrow arrow;

	private final Set<LivingEntity> targets = new HashSet<>();

	private int maxJumps, jumps;
	private boolean isWet;

	public EntityShock(EntityType<? extends EntityShock> entity, World worldIn) {
		super(entity, worldIn);
		this.setSize(0.5f, 0.5f);
		this.arrow = null;
	}

	public EntityShock(World worldIn, EntityBLArrow arrow, LivingEntity hit, boolean isWet) {
		super(worldIn);
		this.setSize(0.5f, 0.5f);

		this.moveTo(arrow.getX(), arrow.getY(), arrow.getZ(), 0, 0);

		this.arrow = arrow;	
		this.targets.add(hit);
		this.isWet = isWet;

		this.maxJumps = 2 + this.level.random.nextInt(3);

		if(isWet) {
			this.maxJumps = this.maxJumps * 2;
		}
	}

	@Override
	protected void defineSynchedData() {

	}

	@Override
	public void load(CompoundNBT compound) {

	}

	@Override
	public boolean save(CompoundNBT compound) {
		return false;
	}

	@Override
	public boolean writeToNBTOptional(CompoundNBT compound) {
		//don't save
		return save(compound);
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		//no moving
	}

	@Override
	public void tick() {
		super.tick();

		if(!this.level.isClientSide()) {
			if(this.arrow == null) {
				this.remove();
			} else {
				Entity shootingEntity = this.arrow.getThrower();
				DamageSource damagesource;
				if (shootingEntity == null) {
					damagesource = DamageSource.causeArrowDamage(this.arrow, this.arrow);
				} else {
					damagesource = DamageSource.causeArrowDamage(this.arrow, shootingEntity);
				}

				List<Pair<Entity, Entity>> chain = new ArrayList<>();

				if(this.jumps < this.maxJumps) {
					if(this.tickCount != 0 && this.tickCount % 3 == 0) {
						Set<LivingEntity> newTargets = new HashSet<>();

						entityLoop: for(Entity entity : this.targets) {
							boolean isWet = entity.isWet() || entity.isInWater() || this.world.isRainingAt(entity.getPosition().above());

							List<LivingEntity> entities = this.world.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(isWet ? 6 : 4), e -> {
								Entity riding = e.getLowestRidingEntity();

								//Passengers are handled further down
								if(riding != e && riding instanceof LivingEntity) {
									return false;
								}

								return true;	
							});

							if(entities.size() > 1) {
								Collections.sort(entities, (e1, e2) -> Double.compare(e1.getDistanceSq(entity), e2.getDistanceSq(entity)));

								for(int j = 1; j < entities.size(); j++) {
									LivingEntity newTarget = entities.get(j);

									if(!this.targets.contains(newTarget) && !newTargets.contains(newTarget)) {
										newTargets.add(newTarget);

										chain.add(Pair.of(entity, newTarget));

										float f = MathHelper.sqrt(this.arrow.motionX * this.arrow.motionX + this.arrow.motionY * this.arrow.motionY + this.arrow.motionZ * this.arrow.motionZ);
										float damage = MathHelper.ceil((double)f * this.arrow.getDamage());
										if (this.arrow.getIsCritical()) {
											damage += this.random.nextInt((int)damage / 2 + 2);
										}

										boolean blocked = false;

										for(ItemStack stack : newTarget.getEquipmentAndArmor()) {
											if(!stack.isEmpty() && stack.getItem() instanceof RubberBootsItem) {
												stack.hurtAndBreak(2, newTarget, (newEntity) -> {
													newEntity.broadcastBreakEvent(newTarget.getUsedItemHand());
												});
												blocked = true;
											}
										}

										if(!blocked) {
											newTarget.hurt(damagesource, isWet ? 2 * damage : damage);

											//Also zap all passengers >:)
											for(Entity passenger : newTarget.getRecursivePassengers()) {
												if(passenger instanceof LivingEntity && !this.targets.contains(passenger) && !newTargets.contains(passenger)) {
													passenger.hurt(damagesource, isWet ? 2 * damage : damage);
													newTargets.add((LivingEntity) passenger);
												}
											}
										}

										continue entityLoop;
									}
								}
							}
						}

						this.targets.addAll(newTargets);

						TheBetweenlands.networkWrapper.sendToAllTracking(new MessageShockArrowHit(chain), this);

						this.jumps++;
					}
				} else {
					this.remove();
				}
			}
		}
	}
}
