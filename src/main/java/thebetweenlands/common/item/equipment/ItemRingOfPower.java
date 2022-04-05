package thebetweenlands.common.item.equipment;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemRingOfPower extends ItemRing {
	public static final UUID POWER_SPEED_MODIFIER_ATTRIBUTE_UUID = UUID.fromString("ac457979-c0c4-4782-bfc3-53f995b21a4b");

	public ItemRingOfPower(Properties properties) {
		super(properties);
		//this.setMaxDamage(1800);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.get("tooltip.bl.ring.power.bonus"), 0));
		if (Screen.hasShiftDown()) {
			String toolTip = I18n.get("tooltip.bl.ring.power", KeyBindRegistry.RADIAL_MENU.getName());
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(I18n.get("tooltip.bl.press.shift"));
		}
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) { 
		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.putBoolean("ringActive", true);

		if(entity instanceof LivingEntity) {
			ModifiableAttributeInstance speedAttrib = ((LivingEntity) entity).getAttribute(Attributes.MOVEMENT_SPEED);

			if(speedAttrib != null && speedAttrib.getModifier(POWER_SPEED_MODIFIER_ATTRIBUTE_UUID) == null) {
				speedAttrib.addTransientModifier(new AttributeModifier(POWER_SPEED_MODIFIER_ATTRIBUTE_UUID, "Ring of power speed modifier", 0.2D, 2));
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { 
		CompoundNBT nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.putBoolean("ringActive", false);
		
		if(entity instanceof LivingEntity) {
			boolean hasOtherRing = false;
			for(int i = 0; i < inventory.getContainerSize(); i++) {
				ItemStack invStack = inventory.getItem(i);
				if(!invStack.isEmpty() && invStack.getItem() instanceof ItemRingOfPower && invStack != stack) {
					hasOtherRing = true;
					break;
				}
			}
			
			if(!hasOtherRing) {
				ModifiableAttributeInstance speedAttrib = ((LivingEntity) entity).getAttribute(Attributes.MOVEMENT_SPEED);
	
				if(speedAttrib != null) {
					speedAttrib.removeModifier(POWER_SPEED_MODIFIER_ATTRIBUTE_UUID);
				}
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean("ringActive");
	}
}
