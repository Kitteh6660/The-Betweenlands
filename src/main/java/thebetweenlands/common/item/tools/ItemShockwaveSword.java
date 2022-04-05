package thebetweenlands.common.item.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemShockwaveSword extends BLSwordItem {
	
	public ItemShockwaveSword(IItemTier tier, int damage, float speed, Properties properties) {
		super(tier, damage, speed, properties);
		//super(material);
		this.addPropertyOverride(new ResourceLocation("charging"), (stack, worldIn, entityIn) -> stack.getTag() != null && stack.getTag().getInt("cooldown") < 60 ? 1 : 0);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if(stack.getDamageValue() == stack.getMaxDamage()) {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.tool.broken", stack.getDisplayName()), 0));
		} else {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.shockwave_sword.usage"), 0));
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeldItem) {
		CorrosionHelper.updateCorrosion(stack, world, entity, slot, isHeldItem);

		if (!stack.hasTag())
			stack.setTag(new CompoundNBT());
		if (!stack.getTag().contains("cooldown"))
			stack.getTag().putInt("cooldown", 0);
		if (!stack.getTag().contains("uses"))
			stack.getTag().putInt("uses", 0);

		if(stack.getTag().getInt("uses") == 3) {
			if (stack.getTag().getInt("cooldown") < 60)
				stack.getTag().putInt("cooldown", stack.getTag().getInt("cooldown") + 1);
			if (stack.getTag().getInt("cooldown") >= 60) {
				stack.getTag().putInt("cooldown", 60);
				stack.getTag().putInt("uses", 0);
			}
		}
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
		ItemStack stack = player.getItemInHand(hand);

		if (!stack.hasTag()) {
			stack.setTag(new CompoundNBT());
			return ActionResultType.PASS;
		}

		if(stack.getDamageValue() == stack.getMaxDamage()) {
			stack.getTag().putInt("cooldown", 0);
			return ActionResultType.PASS;
		}

		if (stack.getTag().getInt("uses") < 3) {
			if (!level.isClientSide()) {
				stack.hurtAndBreak(2, player, (entity) -> {
					entity.broadcastBreakEvent(player.getUsedItemHand());
				});
				world.playLocalSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.SHOCKWAVE_SWORD, SoundCategory.BLOCKS, 1.25F, 1.0F + world.random.nextFloat() * 0.1F);
				double direction = Math.toRadians(player.yRot);
				Vector3d diag = new Vector3d(Math.sin(direction + Math.PI / 2.0D), 0, Math.cos(direction + Math.PI / 2.0D)).normalize();
				List<BlockPos> spawnedPos = new ArrayList<BlockPos>();
				for (int distance = -1; distance <= 16; distance++) {
					for(int distance2 = -distance; distance2 <= distance; distance2++) {
						for(int yo = -1; yo <= 1; yo++) {
							int originX = MathHelper.floor(pos.getX() + 0.5D - Math.sin(direction) * distance - diag.x * distance2 * 0.25D);
							int originY = pos.getY() + yo;
							int originZ = MathHelper.floor(pos.getZ() + 0.5D + Math.cos(direction) * distance + diag.z * distance2 * 0.25D);
							BlockPos origin = new BlockPos(originX, originY, originZ);

							if(spawnedPos.contains(origin))
								continue;

							spawnedPos.add(origin);

							BlockState block = world.getBlockState(new BlockPos(originX, originY, originZ));

							if (block.isNormalCube() && !block.getBlock().hasTileEntity(block) && block.getBlockHardness(world, origin) <= 5.0F && block.getBlockHardness(world, origin) >= 0.0F && !world.getBlockState(origin.above()).canOcclude()) {
								stack.getTag().putInt("blockID", Block.getIdFromBlock(world.getBlockState(origin).getBlock()));
								stack.getTag().putInt("blockMeta", world.getBlockState(origin).getBlock().getMetaFromState(world.getBlockState(origin)));

								EntityShockwaveBlock shockwaveBlock = new EntityShockwaveBlock(world);
								shockwaveBlock.setOrigin(origin, MathHelper.floor(Math.sqrt(distance*distance+distance2*distance2)), pos.getX() + 0.5D, pos.getZ() + 0.5D, player);
								shockwaveBlock.moveTo(originX + 0.5D, originY, originZ + 0.5D, 0.0F, 0.0F);
								shockwaveBlock.setBlock(Block.getBlockById(stack.getTag().getInt("blockID")), stack.getTag().getInt("blockMeta"));
								world.addFreshEntity(shockwaveBlock);
								break;
							}
						}
					}
				}
				stack.getTag().putInt("uses", stack.getTag().getInt("uses") + 1);
				if (stack.getTag().getInt("uses") >= 3) {
					stack.getTag().putInt("uses", 3);
					stack.getTag().putInt("cooldown", 0);
				}
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	private static final ImmutableList<String> STACK_NBT_EXCLUSIONS = ImmutableList.of("cooldown");

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		boolean wasCharging = oldStack.getTag() != null && oldStack.getTag().getInt("cooldown") < 60;
		boolean isCharging = newStack.getTag() != null && newStack.getTag().getInt("cooldown") < 60;
		return (super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !isCharging || isCharging != wasCharging) || !NBTHelper.areItemStackTagsEqual(oldStack, newStack, STACK_NBT_EXCLUSIONS);
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		int maxDamage = stack.getMaxDamage();
		if(damage > maxDamage) {
			//Don't let the sword break
			damage = maxDamage;
		}
		super.setDamage(stack, damage);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		if(slot == EquipmentSlotType.MAINHAND && stack.getDamageValue() == stack.getMaxDamage()) {
			Multimap<Attribute, AttributeModifier> map = HashMultimap.<Attribute, AttributeModifier>create();
			map.put(Attributes.ATTACK_DAMAGE.getRegistryName(), new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", -1, Operation.MULTIPLY_BASE));
			return map;
		}
		return super.getAttributeModifiers(slot, stack);
	}
	
	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairFuelCost(BLMaterialRegistry.TOOL_LEGEND);
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairFuelCost(BLMaterialRegistry.TOOL_LEGEND);
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairLifeCost(BLMaterialRegistry.TOOL_LEGEND);
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairLifeCost(BLMaterialRegistry.TOOL_LEGEND);
	}
	
	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}
}
