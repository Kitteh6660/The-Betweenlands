package thebetweenlands.common.item.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemGreataxe extends ItemGreatsword {
	
	public ItemGreataxe(IItemTier itemTier, int damage, float speed, Properties properties) {
		super(itemTier, damage, speed, properties);
		//super(material);
		//this.setHarvestLevel("axe", 3);
		//this.setCreativeTab(BLCreativeTabs.GEARS);
		this.setMaxDamage(itemTier.getUses() * 2);
	}

	protected double getBlockBreakHalfAngle(LivingEntity entity, ItemStack stack) {
		return 45.0D;
	}

	protected double getBlockBreakReach(LivingEntity entity, ItemStack stack) {
		return 2.6D;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity holder, int slot, boolean isHeldItem) {
		super.onUpdate(stack, world, holder, slot, isHeldItem);

		if(holder instanceof ServerPlayerEntity && !holder.level.isClientSide()) {
			ServerPlayerEntity player = (ServerPlayerEntity) holder;

			if(player.getMainHandItem() == stack && this.isLongSwingInProgress(stack) && this.getSwingStartCooledAttackStrength(stack) > 0.85F) {
				int ticksElapsed = player.tickCount - this.getSwingStartTicks(stack) - 1;

				float longSwingTickProgress = 1.0F / (this.getLongSwingDuration(player, stack) - 1);
				float longSwingProgressEnd = (ticksElapsed + 1) / (this.getLongSwingDuration(player, stack) - 1);

				List<BlockPos> targetBlocks = new ArrayList<>();

				for(float longSwingProgressStart = Math.max(0, longSwingProgressEnd - Math.max(0.25F, longSwingTickProgress)); longSwingProgressStart < longSwingProgressEnd; longSwingProgressStart += longSwingTickProgress) {
					double breakReach = this.getBlockBreakReach(player, stack);
					int blockReach = MathHelper.ceil(breakReach);

					double breakHalfAngle = this.getBlockBreakHalfAngle(player, stack);

					double minAngle = -breakHalfAngle + breakHalfAngle * 2 * longSwingProgressStart;
					double maxAngle = -breakHalfAngle + breakHalfAngle * 2 * longSwingProgressEnd;

					float yaw = player.yRot;
					float pitch = player.xRot;

					float yc = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
					float ys = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
					float pc = -MathHelper.cos(-pitch * 0.017453292F);
					float ps = MathHelper.sin(-pitch * 0.017453292F);

					Vector3d forward = new Vector3d((double)(ys * pc), (double)ps, (double)(yc * pc)).normalize();

					pc = -MathHelper.cos(-(pitch - 90) * 0.017453292F);
					ps = MathHelper.sin(-(pitch - 90) * 0.017453292F);

					Vector3d up = new Vector3d((double)(ys * pc), (double)ps, (double)(yc * pc)).normalize();

					Vector3d right = forward.cross(up);

					for(int xo = -blockReach; xo <= blockReach; xo++) {
						for(int yo = -blockReach; yo <= blockReach; yo++) {
							for(int zo = -blockReach; zo <= blockReach; zo++) {
								BlockPos pos = new BlockPos(player.getX() + xo, player.getY() + player.height * 0.5D + yo, player.getZ() + zo);
								Vector3d center = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

								double dist = center.distanceTo(player.getPositionEyes(1));

								if(dist < breakReach) {
									Vector3d dir = center.subtract(player.getPositionEyes(1)).normalize();

									double py = forward.dotProduct(dir);
									double px = right.dotProduct(dir);

									double angle = Math.toDegrees(-Math.atan2(px, py));

									if(angle >= minAngle && angle < maxAngle) {
										double distUp = up.dotProduct(new Vector3d(center.x - player.getX(), center.y - player.getY() - player.getEyeHeight(), center.z - player.getZ()));

										double verticalRange = 1.0D + 1.5D * (3 - MathHelper.clamp(dist, 0, 3)) / 3.0D;

										if(distUp >= -verticalRange - 0.5D && distUp <= verticalRange - 0.5D) {
											BlockState state = player.world.getBlockState(pos);

											if((state.getBlock().isWood(player.world, pos) || state.getMaterial() == Material.WOOD) && state.getBlockHardness(player.world, pos) <= 2.25F &&
													state.getPlayerRelativeBlockHardness(player, player.world, pos) > 0.01F) {
												targetBlocks.add(pos);
											}
										}
									}
								}
							}
						}
					}
				}

				if(!targetBlocks.isEmpty()) {
					Collections.shuffle(targetBlocks, player.world.rand);

					int playedEffects = 0;
					for(BlockPos pos : targetBlocks) {
						if(!world.isEmptyBlock(pos)) {
							BlockState state = player.world.getBlockState(pos);

							if(player.interactionManager.tryHarvestBlock(pos)) {
								if(++playedEffects <= 3) {
									player.world.playEvent(null, 2001, pos, Block.getStateId(state));
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected float getSwingSpeedMultiplier(LivingEntity entity, ItemStack stack) {
		return 0.14F;
	}

	@Override
	protected double getAoEReach(LivingEntity entityLiving, ItemStack stack) {
		return 0;
	}

	@Override
	public double getReach() {
		return 2.5D;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
		Multimap<Attribute, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

		if(equipmentSlot == EquipmentSlotType.MAINHAND) {
			multimap.removeAll(Attributes.ATTACK_SPEED.getRegistryName());
			multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", -3.3D, Operation.ADDITION));
		}

		return multimap;
	}

	@Override
	public float getAttackDamage() {
		return super.getAttackDamage() + 2.0f;
	}

	@Override
	public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
		return true;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.COMMON;
	}
}
