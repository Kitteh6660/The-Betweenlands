package thebetweenlands.common.handler;

import java.util.UUID;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.Difficulty;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.capability.decay.DecayStats;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.config.properties.ItemDecayFoodProperty.DecayFoodStats;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.util.MathUtils;

public class PlayerDecayHandler {
	public static final UUID DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID = UUID.fromString("033f5f10-67b3-42f3-8511-67a575fbb099");

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		PlayerEntity player = event.player;

		if(!player.level.isClientSide() && event.phase == Phase.START) {
			IDecayCapability cap = (IDecayCapability) player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
			if(cap != null) {
				DecayStats stats = cap.getDecayStats();

				ModifiableAttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);

				if(attr != null) {
					if(BetweenlandsConfig.GENERAL.decayPercentual) {
						float decayMaxBaseHealthPercentage = cap.getMaxPlayerHealthPercentage(stats.getDecayLevel());   
						float prevDecayMaxBaseHealthPercentage = cap.getMaxPlayerHealthPercentage(stats.getPrevDecayLevel());

						AttributeModifier currentDecayModifier = attr.getModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

						if(!MathUtils.epsilonEquals(decayMaxBaseHealthPercentage, prevDecayMaxBaseHealthPercentage) || (currentDecayModifier == null && decayMaxBaseHealthPercentage < 1)) {
							attr.removeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

							if(decayMaxBaseHealthPercentage < 1) {
								attr.addTransientModifier(new AttributeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID, "Decay health modifier", -1 + decayMaxBaseHealthPercentage, Operation.MULTIPLY_TOTAL));
							}
						}
					} else {
						int currentMaxHealth = (int) attr.getValue();

						int decayMaxBaseHealth = (int)(cap.getMaxPlayerHealth(stats.getDecayLevel()) / 2.0F) * 2;   
						int prevDecayMaxBaseHealth = (int)(cap.getMaxPlayerHealth(stats.getPrevDecayLevel()) / 2.0F) * 2;

						boolean decayHealthChange = (decayMaxBaseHealth - prevDecayMaxBaseHealth) != 0;

						int decayHealthDiff = decayMaxBaseHealth - 20;

						AttributeModifier currentDecayModifier = attr.getModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

						//Only change modifier if deay modifier is missing, decay health modifier value has changed or if player has less than 3 hearts (in which case decay modifier should be reduced or removed)
						if((currentMaxHealth > BetweenlandsConfig.GENERAL.decayMinHealth && decayHealthDiff != 0 && (currentDecayModifier == null || decayHealthDiff != (int)currentDecayModifier.getAmount())) ||
								decayHealthChange ||
								(currentMaxHealth < BetweenlandsConfig.GENERAL.decayMinHealth && currentDecayModifier != null)) {
							attr.removeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID);

							//Get current max health without the decay modifier
							currentMaxHealth = (int) attr.getValue();

							//Don't go below 3 hearts
							int newHealth = (int) Math.max(currentMaxHealth + decayHealthDiff, BetweenlandsConfig.GENERAL.decayMinHealth);

							int attributeHealth = newHealth - currentMaxHealth;

							if(attributeHealth < 0) {
								attr.addTransientModifier(new AttributeModifier(DECAY_HEALTH_MODIFIER_ATTRIBUTE_UUID, "Decay health modifier", attributeHealth, Operation.ADDITION));
								cap.setRemovedHealth(-attributeHealth);
							} else {
								cap.setRemovedHealth(0);
							}
						}
					}
				}

				if(cap.isDecayEnabled()) {
					int decay = stats.getDecayLevel();

					if (decay >= 16) {
						player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 40, 2, true, false));
						player.jumpMovementFactor = 0.001F;
					} else if (decay >= 13) {
						player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 40, 1, true, false));
						player.jumpMovementFactor = 0.002F;
					} else if (decay >= 10) {
						player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 40, 0, true, false));
					}

					if(!event.player.isRiding()) {
						Difficulty difficulty = player.level.getDifficulty();

						float decayBaseSpeed = getDecayBaseSpeed(difficulty);

						float decaySpeed = 0;

						if(player.distanceWalkedModified - player.prevDistanceWalkedModified > 0) {
							decaySpeed += (player.distanceWalkedModified - player.prevDistanceWalkedModified) * 4 * decayBaseSpeed;
						}

						BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(player.level);
						if(storage.getEnvironmentEventRegistry().heavyRain.isActive() && player.level.canSeeSky(player.blockPosition())) {
							decaySpeed += decayBaseSpeed;
						}

						if(player.isInWater()) {
							decaySpeed += decayBaseSpeed * 2.75F;
						}

						if(decaySpeed > 0.0F) {
							stats.addDecayAcceleration(decaySpeed);
						}
					}
					
					stats.onUpdate(player);
				} else {
					stats.setDecayLevel(0);
					stats.setDecaySaturationLevel(1);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityAttacked(LivingHurtEvent event) {
		if(!event.getEntityLiving().level.isClientSide() && event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) event.getEntityLiving();

			IDecayCapability cap = (IDecayCapability) player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
			if(cap != null) {
				float decayBaseSpeed = getDecayBaseSpeed(player.level.getDifficulty());
				cap.getDecayStats().addDecayAcceleration(decayBaseSpeed * 60);
			}
		}
	}

	/**
	 * Returns the base decay speed per tick
	 * @param difficulty
	 * @return
	 */
	public static float getDecayBaseSpeed(Difficulty difficulty) {
		switch(difficulty) {
		case PEACEFUL:
			return 0.0F;
		case EASY:
			return 0.0025F;
		default:
		case NORMAL:
			return 0.0033F;
		case HARD:
			return 0.005F;
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerRespawnEvent event) {
		//Workaround for client not receiving the new MAX_HEALTH attribute after a respawn
		PlayerEntity player = event.getPlayer();
		if(!player.level.isClientSide() && player instanceof ServerPlayerEntity && player.getAttribute(Attributes.MAX_HEALTH) != null) {
			((ServerPlayerEntity)player).connection.sendPacket(new SPacketEntityProperties(player.getEntityId(), ImmutableList.of(player.getAttribute(Attributes.MAX_HEALTH))));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onUseItemTick(LivingEntityUseItemEvent.Tick event) {
		//Check if item will be consumed this tick
		if(!event.getEntityLiving().level.isClientSide() && event.getDuration() <= 1) {
			if (!event.getItem().isEmpty() && event.getEntityLiving() instanceof PlayerEntity) {
				DecayFoodStats decayFoodStats = OverworldItemHandler.getDecayFoodStats(event.getItem());
				if(decayFoodStats != null) {
					PlayerEntity player = (PlayerEntity) event.getEntityLiving();
					IDecayCapability cap = (IDecayCapability) player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
					if(cap != null) {
						cap.getDecayStats().addStats(-decayFoodStats.decay, decayFoodStats.saturation);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onStartUsingItem(LivingEntityUseItemEvent.Start event) {
		if(!event.getItem().isEmpty() && event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) event.getEntityLiving();
			boolean isDecayFood = OverworldItemHandler.getDecayFoodStats(event.getItem()) != null;
			if(isDecayFood) {
				boolean canEatFood = player.getFoodData().needsFood() && event.getItem().getItem().isEdible() && event.getItem().getItem().getFoodProperties().getNutrition() > 0;
				boolean canEatDecayFood = false;
				IDecayCapability cap = (IDecayCapability) player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
				if(cap != null) {
					canEatDecayFood = cap.getDecayStats().getDecayLevel() > 0;
				}
				if (!canEatFood && !canEatDecayFood) {
					event.setDuration(-1);
					event.setCanceled(true);
				}
			}
		}
	}
}
