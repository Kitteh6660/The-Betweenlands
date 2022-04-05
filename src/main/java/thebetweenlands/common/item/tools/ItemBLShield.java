package thebetweenlands.common.item.tools;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.UseAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

@SuppressWarnings("deprecation")
public class ItemBLShield extends ShieldItem implements IAnimatorRepairable {
	
	private IItemTier material;

	public ItemBLShield(IItemTier material, Properties properties) {
		super(properties);
		this.material = material;
		/*this.setMaxDamage(material.getUses() * 2);
		this.setCreativeTab(BLCreativeTabs.GEARS);*/
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return ("" + I18n.get(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public CreativeTabs getCreativeTab() {
		return BLCreativeTabs.GEARS; //Minecraft seems to override the creative tab for some reason...
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		if (material == BLMaterialRegistry.TOOL_WEEDWOOD) {
			return repair.getItem() == ItemRegistry.WEEDWOOD_PLANKS.get(); // Item.getItemFromBlock(BlockRegistry.WEEDWOOD);
		} else if (material == BLMaterialRegistry.TOOL_BONE) {
			return repair.getItem() == ItemRegistry.BETWEENSTONE.get();// Item.getItemFromBlock(BlockRegistry.BETWEENSTONE);
		} else if (material == BLMaterialRegistry.TOOL_OCTINE) {
			return repair.getItem() == ItemRegistry.OCTINE_INGOT.get();
		} else if (material == BLMaterialRegistry.TOOL_VALONITE) {
			return repair.getItem() == ItemRegistry.VALONITE_SHARD.get();// EnumItemMisc.VALONITE_SHARD.isItemOf(repair);
		}
		return false;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
		playerIn.setActiveHand(hand);
		return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getItemInHand(hand));
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	public boolean isShield(ItemStack stack, LivingEntity entity) {
		return true;
	}

	/**
	 * Returns the blocking cooldown
	 * @param stack
	 * @param attacked
	 * @param source
	 * @return
	 */
	public int getShieldBlockingCooldown(ItemStack stack, LivingEntity attacked, float damage, DamageSource source) {
		return 0;
	}

	/**
	 * Called when an attack was successfully blocked
	 * @param stack
	 * @param attacked
	 * @param source
	 */
	public void onAttackBlocked(ItemStack stack, LivingEntity attacked, float damage, DamageSource source) {
		if(!attacked.level.isClientSide()) {
			damage = CircleGemHelper.handleAttack(source, attacked, damage);
			
			if(source.getTrueSource() instanceof LivingEntity) {
				LivingEntity attacker = (LivingEntity) source.getTrueSource();
				ItemStack attackerItem = attacker.getMainHandItem();
				if(!attackerItem.isEmpty() && attackerItem.getItem().canDisableShield(attackerItem, stack, attacked, attacker)) {
					float attackStrength = attacker instanceof PlayerEntity ? ((PlayerEntity)attacker).getCooledAttackStrength(0.5F) : 1.0F;
					float criticalChance = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(attacker) * 0.05F;
					if(attacker.isSprinting() && attackStrength > 0.9F) {
						criticalChance += 0.75F;
					}
					if (attacked.level.random.nextFloat() < criticalChance) {
						if(attacked instanceof PlayerEntity) {
							((PlayerEntity)attacked).getCooldownTracker().setCooldown(this, 100);
							attacked.stopActiveHand();
						}
						//Shield break sound effect
						attacked.level.setEntityState(attacked, (byte)30);
					}
				}
			}
		}
	}

	/**
	 * Returns the damage for the blocked attack
	 * @param stack
	 * @param attacked
	 * @param source
	 * @return
	 */
	public float getBlockedDamage(ItemStack stack, LivingEntity attacked, float damage, DamageSource source) {
		//float multiplier = 0.4F - Math.min(this.material.getAttackDamage() / 3.0F, 1.0F) * 0.4F;
		//return Math.min(damage * multiplier, 8.0F);
		return 0.0F;
	}

	/**
	 * Returns the knockback multiplier for defender
	 * @param stack
	 * @param attacked
	 * @param source
	 * @return
	 */
	public float getDefenderKnockbackMultiplier(ItemStack stack, LivingEntity attacked, float damage, DamageSource source) {
		//Uses durability as "weight"
		return 0.6F - Math.min(this.material.getUses() / 2500.0F, 1.0F) * 0.6F;
	}

	/**
	 * Returns the knockback multiplier for the attacker
	 * @param stack
	 * @param attacked
	 * @param source
	 * @return
	 */
	public float getAttackerKnockbackMultiplier(ItemStack stack, LivingEntity attacked, float damage, DamageSource source) {
		return 0.6F;
	}

	/**
	 * Returns whether this shield can block the specified damage source
	 * @param stack
	 * @param attacked
	 * @param hand
	 * @param source
	 * @return
	 */
	public boolean canBlockDamageSource(ItemStack stack, LivingEntity attacked, Hand hand, DamageSource source) {
		if (attacked.isHandActive() && attacked.getActiveHand() == hand && !source.isUnblockable() && attacked.isActiveItemStackBlocking() && (source instanceof EntityDamageSource == false || source.getTrueSource() != null)) {
			Vector3d vec3d = source.getDamageLocation();
			if (vec3d != null) {
				Vector3d vec3d1 = attacked.getLook(1.0F);
				Vector3d vec3d2 = vec3d.subtractReverse(new Vector3d(attacked.getX(), attacked.getY(), attacked.getZ())).normalize();
				vec3d2 = new Vector3d(vec3d2.x, 0.0D, vec3d2.z);
				if (vec3d2.dotProduct(vec3d1) < 0.0D) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Called when the shield breaks
	 * @param stack
	 * @param attacked
	 */
	protected void onShieldBreak(ItemStack stack, LivingEntity attacked, Hand hand, DamageSource source) {
		Hand hand = attacked.getUsedItemHand();
		if(attacked instanceof PlayerEntity)
			net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem((PlayerEntity)attacked, stack, Hand);
		if (hand == Hand.MAIN_HAND)
			attacked.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
		else
			attacked.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
		//Shield break sound effect
		attacked.level.setEntityState(attacked, (byte)30);
	}

	public static enum EventHandler {
		INSTANCE;

		private boolean ignoreEvent = false;

		@SubscribeEvent
		public void onLivingAttacked(LivingAttackEvent event) {
			if(this.ignoreEvent) {
				return;
			}
			this.ignoreEvent = true;
			LivingEntity attacked = event.getEntityLiving();
			DamageSource source = event.getSource();
			for(Hand hand : Hand.values()) {
				ItemStack stack = attacked.getItemInHand(hand);
				if(!stack.isEmpty() && stack.getItem() instanceof ItemBLShield) {
					ItemBLShield shield = (ItemBLShield) stack.getItem();

					if(shield.canBlockDamageSource(stack, attacked, hand, source)) {
						//Cancel event
						if(!attacked.level.isClientSide()) {
							event.setCanceled(true);
						}

						if(!attacked.level.isClientSide()) {
							//Apply damage with multiplier
							float defenderKbMultiplier = shield.getDefenderKnockbackMultiplier(stack, attacked, event.getAmount(), source);
							float newDamage = shield.getBlockedDamage(stack, attacked, event.getAmount(), source);
							if(newDamage > 0.0F) {
								double prevMotionX = attacked.motionX;
								double prevMotionY = attacked.motionY;
								double prevMotionZ = attacked.motionZ;
								DamageSource newSource;
								//getDamageLocation() == null so that vanilla shield blocking does not happen
								if(source instanceof EntityDamageSourceIndirect) {
									newSource = new EntityDamageSourceIndirect(source.damageType, source.getImmediateSource(), source.getTrueSource()) {
										@Override
										public Vector3d getDamageLocation() {
											return null;
										}
									};
								} else if(source instanceof EntityDamageSource) {
									newSource = new EntityDamageSource(source.damageType, source.getTrueSource()) {
										@Override
										public Vector3d getDamageLocation() {
											return null;
										}
									};
								} else {
									newSource = new DamageSource(source.damageType) {
										@Override
										public Vector3d getDamageLocation() {
											return null;
										}
									};
								}
								if(source.isDamageAbsolute()) {
									newSource.setDamageIsAbsolute();
								}
								if(source.isUnblockable()) {
									newSource.setDamageBypassesArmor();
								}
								if(source.isFireDamage()) {
									newSource.setFireDamage();
								}
								if(source.isMagicDamage()) {
									newSource.setMagicDamage();
								}
								if(source.isDifficultyScaled()) {
									newSource.setDifficultyScaled();
								}
								if(source.isExplosion()) {
									newSource.setExplosion();
								}
								if(source.isProjectile()) {
									newSource.setProjectile();
								}
								attacked.hurt(newSource, newDamage);
								attacked.motionX = prevMotionX;
								attacked.motionY = prevMotionY;
								attacked.motionZ = prevMotionZ;
							}
							if(source.getTrueSource() != null) {
								//Knock back defender
								double prevMotionY = attacked.motionY;
								attacked.knockBack(source.getTrueSource(), defenderKbMultiplier, source.getTrueSource().getX() - attacked.getX(), source.getTrueSource().getZ() - attacked.getZ());
								attacked.motionY = prevMotionY;
								attacked.velocityChanged = true;
							}
							//Shield block sound effect
							attacked.level.setEntityState(attacked, (byte)29);
						}

						//Knock back attacker
						if(!attacked.level.isClientSide()) {
							if (source.getTrueSource() == source.getImmediateSource() && source.getTrueSource() instanceof LivingEntity) {
								float attackerKbMultiplier = shield.getAttackerKnockbackMultiplier(stack, attacked, event.getAmount(), source);
								if(attackerKbMultiplier > 0.0F) {
									((LivingEntity)source.getTrueSource()).knockBack(attacked, attackerKbMultiplier, attacked.getX() - source.getTrueSource().getX(), attacked.getZ() - source.getTrueSource().getZ());
								}
							}
						}

						if(attacked instanceof PlayerEntity) {
							int cooldown = shield.getShieldBlockingCooldown(stack, (PlayerEntity)attacked, event.getAmount(), source);
							if(cooldown > 0) {
								((PlayerEntity)attacked).getCooldownTracker().setCooldown(shield, cooldown);
								attacked.stopActiveHand();
							}
						}

						shield.onAttackBlocked(stack, attacked, event.getAmount(), source);

						if(!attacked.level.isClientSide()) {
							//Damage item
							int itemDamage = 1 + MathHelper.floor(event.getAmount());
							stack.hurtAndBreak(itemDamage, attacked, (entity) -> {
								entity.broadcastBreakEvent(attacked.getUsedItemHand());
							});
							//Shield broke
							if (stack.getCount() <= 0) {
								shield.onShieldBreak(stack, attacked, hand, source);
							}
						}

						break;
					}
				}
			}
			this.ignoreEvent = false;
		}
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairFuelCost(this.material);
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairFuelCost(this.material);
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairLifeCost(this.material);
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairLifeCost(this.material);
	}
}
