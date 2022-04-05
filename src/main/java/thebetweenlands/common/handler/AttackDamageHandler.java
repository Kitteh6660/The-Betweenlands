package thebetweenlands.common.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.network.clientbound.MessageDamageReductionParticle;
import thebetweenlands.common.network.clientbound.MessagePowerRingParticles;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class AttackDamageHandler {
	public static final float DAMAGE_REDUCTION = 0.3F;

	@SubscribeEvent
	public static void onEntityKnockback(LivingKnockBackEvent event) {
		LivingEntity attackedEntity = event.getEntityLiving();
		Entity attacker = event.getAttacker();

		if(attackedEntity instanceof IEntityBL && attacker instanceof LivingEntity && ((LivingEntity) attacker).getActiveHand() != null) {
			LivingEntity entityLiving = (LivingEntity) attacker;
			ItemStack heldItem = entityLiving.getItemInHand(entityLiving.getActiveHand());

			if (!heldItem.isEmpty() && OverworldItemHandler.isToolWeakened(heldItem)) {
				event.setStrength(event.getStrength() * 0.3F);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onEntityAttack(LivingAttackEvent event) {
		LivingEntity attackedEntity = event.getEntityLiving();
		DamageSource source = event.getSource();

		//Handle circle gem for blocking
		//For BL shields this is handled in ItemBLShield#onAttackBlocked
		if(canBlockDamageSource(attackedEntity, source)) {
			CircleGemHelper.handleAttack(source, attackedEntity, event.getAmount());
		}
	}

	private static boolean canBlockDamageSource(LivingEntity entity, DamageSource source) {
		if(!source.isUnblockable() && entity.isActiveItemStackBlocking() && (source instanceof EntityDamageSource == false || source.getTrueSource() != null)) {
			Vector3d location = source.getDamageLocation();

			if(location != null) {
				Vector3d look = entity.getLook(1.0F);
				Vector3d diff = location.subtractReverse(new Vector3d(entity.getX(), entity.getY(), entity.getZ())).normalize();
				diff = new Vector3d(diff.x, 0.0D, diff.z);

				if(diff.dotProduct(look) < 0.0D) {
					return true;
				}
			}
		}

		return false;
	}

	@SubscribeEvent
	public static void onEntityAttacked(LivingHurtEvent event) {
		LivingEntity attackedEntity = event.getEntityLiving();
		DamageSource source = event.getSource();
		float damage = event.getAmount();

		Entity entity = source.getTrueSource();
		if(attackedEntity instanceof IEntityBL && entity instanceof LivingEntity && ((LivingEntity) entity).getActiveHand() != null) {
			//BL mobs overworld item resistance
			LivingEntity attacker = (LivingEntity) entity;
			ItemStack heldItem = attacker.getItemInHand(attacker.getActiveHand());

			if (heldItem.isEmpty() || OverworldItemHandler.isToolWeakened(heldItem)) {
				//Cap damage of overly OP weapons
				damage = Math.min(damage, 40.0F);
			}

			if (!heldItem.isEmpty()) {
				if (OverworldItemHandler.isToolWeakened(heldItem)) {
					damage = damage * DAMAGE_REDUCTION;

					if(!attackedEntity.level.isClientSide()) {
						Vector3d center = attackedEntity.getDeltaMovement().add(0, attackedEntity.height / 2.0F, 0);

						Vector3d hitOffset = null;

						Entity immediateAttacker = source.getImmediateSource();

						if(immediateAttacker == null || attacker == immediateAttacker) {
							RayTraceResult result = attackedEntity.getBoundingBox().calculateIntercept(attacker.getPositionEyes(1), attacker.getPositionEyes(1).add(attacker.getLookVec().scale(10)));
							if(result != null) {
								hitOffset = result.hitVec.subtract(center);
							}
						}
						if(immediateAttacker != null && hitOffset == null) {
							hitOffset = immediateAttacker.getDeltaMovement().add(0, immediateAttacker.height / 2.0F, 0).subtract(center);
						}
						if(hitOffset != null) {
							Vector3d offsetDirXZ = new Vector3d(hitOffset.x, 0, hitOffset.z).normalize();
							Vector3d offset = offsetDirXZ.scale(attackedEntity.width).add(0, hitOffset.y + attackedEntity.height / 2.0F, 0);

							attackedEntity.world.playLocalSound(null, attackedEntity.getX(), attackedEntity.getY() + 0.5D, attackedEntity.getZ(), SoundRegistry.DAMAGE_REDUCTION, SoundCategory.PLAYERS, 0.7F, 0.75F + attackedEntity.level.random.nextFloat() * 0.3F);

							TheBetweenlands.networkWrapper.sendToAllAround(new MessageDamageReductionParticle(attackedEntity, offset, offsetDirXZ.scale(attackedEntity.width + 0.2F).normalize()), new TargetPoint(attackedEntity.dimension, attackedEntity.getX(), attackedEntity.getY(), attackedEntity.getZ(), 32.0D));
						}
					}
				}
			}
		}

		damage = CircleGemHelper.handleAttack(source, attackedEntity, damage);

		if(entity instanceof LivingEntity) {
			IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			if(cap != null) {
				IInventory inv = cap.getInventory(EnumEquipmentInventory.RING);
				int rings = 0;

				for(int i = 0; i < inv.getContainerSize(); i++) {
					ItemStack stack = inv.getItem(i);
					if(!stack.isEmpty() && stack.getItem() == ItemRegistry.RING_OF_POWER && stack.getDamageValue() < stack.getMaxDamage()) {
						rings++;
					}
				}

				if(rings > 0) {
					TheBetweenlands.networkWrapper.sendToAllAround(new MessagePowerRingParticles(attackedEntity), new TargetPoint(attackedEntity.dimension, attackedEntity.getX(), attackedEntity.getY(), attackedEntity.getZ(), 32.0D));
				}

				damage *= 1.0F + 0.5F * rings;
			}
		}

		event.setAmount(damage);
	}

	@OnlyIn(Dist.CLIENT)
	public static void spawnDamageReductionParticle(Entity entity, Vector3d offset, Vector3d dir) {
		BLParticles.DAMAGE_REDUCTION.spawn(entity.level, 0, 0, 0, ParticleArgs.get().withScale(2F).withData(entity, offset, dir));
	}

	@OnlyIn(Dist.CLIENT)
	public static void spawnPowerRingParticles(Entity entityHit) {
		BLParticles.GREEN_FLAME.spawn(entityHit.level, entityHit.getX() + entityHit.getBbWidth() / 2.0D, entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ(), ParticleArgs.get().withMotion(0.08D, 0.05D, 0));
		BLParticles.GREEN_FLAME.spawn(entityHit.level, entityHit.getX(), entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ() + entityHit.getBbWidth() / 2.0D, ParticleArgs.get().withMotion(0, 0.05D, 0.08D));
		BLParticles.GREEN_FLAME.spawn(entityHit.level, entityHit.getX() - entityHit.getBbWidth() / 2.0D, entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ(), ParticleArgs.get().withMotion(-0.08D, 0.05D, 0));
		BLParticles.GREEN_FLAME.spawn(entityHit.level, entityHit.getX(), entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ() - entityHit.getBbWidth() / 2.0D, ParticleArgs.get().withMotion(0, 0.05D, -0.08D));
		BLParticles.GREEN_FLAME.spawn(entityHit.level, entityHit.getX() + entityHit.getBbWidth() / 2.0D, entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ(), ParticleArgs.get().withMotion(0.08D, -0.05D, 0));
		BLParticles.GREEN_FLAME.spawn(entityHit.level, entityHit.getX(), entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ() + entityHit.getBbWidth() / 2.0D, ParticleArgs.get().withMotion(0, -0.05D, 0.08D));
		BLParticles.GREEN_FLAME.spawn(entityHit.level, entityHit.getX() - entityHit.getBbWidth() / 2.0D, entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ(), ParticleArgs.get().withMotion(-0.08D, -0.05D, 0));
		BLParticles.GREEN_FLAME.spawn(entityHit.level, entityHit.getX(), entityHit.getY() + entityHit.getBbHeight() / 2.0D + 0.5D, entityHit.getZ() - entityHit.getBbWidth() / 2.0D, ParticleArgs.get().withMotion(0, -0.05D, -0.08D));
	}
}
