package thebetweenlands.common.item.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;

public class ItemRing extends Item implements IEquippable 
{
	public ItemRing(Item.Properties properties) {
		super(properties);
		IEquippable.addEquippedPropertyOverrides(this);
	}

	public boolean canBeUsed(ItemStack stack) {
		return stack.getDamageValue() < stack.getMaxDamage();
	}

	protected float getXPConversionRate(ItemStack stack, PlayerEntity player) {
		//1 xp = 5 damage repaired
		return 5.0F;
	}

	public void drainPower(ItemStack stack, Entity entity) {
		if(stack.getDamageValue() < stack.getMaxDamage() && stack.getItem() instanceof ItemRing && ((ItemRing)stack.getItem()).canBeUsed(stack)) {
			stack.setDamageValue(stack.getDamageValue() + 1);
		}
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(!player.isCrouching()) {
			if(stack.getDamageValue() > 0 && (player.totalExperience > 0 || player.experienceLevel > 0 || player.experienceProgress > 0)) {
				if(!world.isClientSide()) {
					int repairPerClick = 40;
					float conversion = this.getXPConversionRate(stack, player);
					float requiredRepair = Math.min(repairPerClick, stack.getDamageValue() / conversion);
					stack.setDamageValue(Math.max(0, stack.getDamageValue() - MathHelper.ceil(MathHelper.abs(removeXp(player, MathHelper.ceil(requiredRepair))) * conversion)));
				}

				return new ActionResult<>(ActionResultType.SUCCESS, stack);
			}
		}

		return new ActionResult<>(ActionResultType.PASS, stack);
	}

	public static int removeXp(PlayerEntity player, int amount) {
		int change = amount;

		float playerXp = player.experienceProgress * (float)player.getXpNeededForNextLevel();
		player.experienceProgress -= (float) amount / (float) player.getXpNeededForNextLevel();
		player.totalExperience = MathHelper.clamp(player.totalExperience - amount, 0, Integer.MAX_VALUE);

		while (player.experienceProgress < 0) {
			float xp = player.experienceProgress * (float)player.getXpNeededForNextLevel();

			if (player.experienceLevel > 0) {
				player.giveExperienceLevels(-1);
				player.experienceProgress = 1.0F + xp / (float)player.getXpNeededForNextLevel();
				playerXp += 1.0F * (float) player.getXpNeededForNextLevel();
			} else {
				player.giveExperienceLevels(-1);
				change = MathHelper.abs(Math.round(playerXp));
				player.experienceProgress = 0.0F;
			}
		}

		return change;
	}

	@Override
	public EnumEquipmentInventory getEquipmentCategory(ItemStack stack) {
		return EnumEquipmentInventory.RING;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, PlayerEntity player, Entity target) {
		return stack.getDamageValue() == 0 || player.totalExperience == 0 || player.experienceProgress == 0 || player.experienceLevel == 0 || player.isCrouching();
	}

	@Override
	public boolean canEquip(ItemStack stack, PlayerEntity player, Entity target) {
		return player == target;
	}

	@Override
	public boolean canUnequip(ItemStack stack, PlayerEntity player, Entity target, IInventory inventory) {
		return true;
	}

	@Override
	public boolean canDrop(ItemStack stack, Entity entity, IInventory inventory) {
		return true;
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) { }

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { }

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(entity.tickCount % 20 == 0) {
			this.drainPower(stack, entity);
		}
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	/**
	 * Called when the ring use keybind is pressed
	 * @param player
	 * @param stack
	 * @param inventory
	 * @param active Whether the key is pressed or not
	 */
	public void onKeybindState(PlayerEntity player, ItemStack stack, IInventory inventory, boolean active) {

	}
}
