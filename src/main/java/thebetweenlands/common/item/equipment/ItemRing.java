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
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;

public class ItemRing extends Item implements IEquippable 
{
	public ItemRing(Item.Properties properties) {
		super(properties);
		IEquippable.addEquippedPropertyOverrides(this);
	}

	public boolean canBeUsed(ItemStack stack) {
		return stack.getItemDamage() < stack.getMaxDamage();
	}

	protected float getXPConversionRate(ItemStack stack, PlayerEntity player) {
		//1 xp = 5 damage repaired
		return 5.0F;
	}

	public void drainPower(ItemStack stack, Entity entity) {
		if(stack.getItemDamage() < stack.getMaxDamage() && stack.getItem() instanceof ItemRing && ((ItemRing)stack.getItem()).canBeUsed(stack)) {
			stack.setItemDamage(stack.getItemDamage() + 1);
		}
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(!player.isCrouching()) {
			if(stack.getItemDamage() > 0 && (player.experienceTotal > 0 || player.experienceLevel > 0 || player.experience > 0)) {
				if(!world.isClientSide()) {
					int repairPerClick = 40;
					float conversion = this.getXPConversionRate(stack, player);
					float requiredRepair = Math.min(repairPerClick, stack.getItemDamage() / conversion);
					stack.setItemDamage(Math.max(0, stack.getItemDamage() - MathHelper.ceil(MathHelper.abs(removeXp(player, MathHelper.ceil(requiredRepair))) * conversion)));
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

		while (player.experience < 0) {
			float xp = player.experience * (float)player.xpBarCap();

			if (player.experienceLevel > 0) {
				player.addExperienceLevel(-1);
				player.experience = 1.0F + xp / (float)player.xpBarCap();
				playerXp += 1.0F * (float) player.xpBarCap();
			} else {
				player.addExperienceLevel(-1);
				change = MathHelper.abs(Math.round(playerXp));
				player.experience = 0.0F;
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
		return stack.getItemDamage() == 0 || player.experienceTotal == 0 || player.experience == 0 || player.experienceLevel == 0 || player.isCrouching();
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
