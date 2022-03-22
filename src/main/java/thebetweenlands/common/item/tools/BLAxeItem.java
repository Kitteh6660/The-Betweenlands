package thebetweenlands.common.item.tools;

import java.util.List;

import com.google.common.collect.Multimap;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.common.item.BLMaterialRegistry;

import javax.annotation.Nullable;


public class BLAxeItem extends AxeItem implements ICorrodible, IAnimatorRepairable {
	
	public BLAxeItem(IItemTier itemTier, float damage, float speed, Properties properties) {
		super(itemTier, damage, speed, properties);
		CorrosionHelper.addCorrosionPropertyOverrides(this);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		Material material = state.getMaterial();
		float str = material != Material.WOOD && material != Material.BAMBOO ? super.getDestroySpeed(stack, state) : this.speed;
		str = CorrosionHelper.getDestroySpeed(str, stack, state);
		return str; 
	}
	
	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return CorrosionHelper.shouldCauseBlockBreakReset(oldStack, newStack);
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return CorrosionHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}
	
	//TODO: Update this.
	/*@Override
	public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		CorrosionHelper.updateCorrosion(itemStack, world, holder, slot, isHeldItem);
	}*/

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return CorrosionHelper.getAttributeModifiers(super.getAttributeModifiers(slot, stack), slot, stack, BASE_ATTACK_DAMAGE_UUID, this.getAttackDamage());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		CorrosionHelper.addCorrosionTooltips(stack, tooltip, flagIn.isAdvanced());
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairFuelCost(this.getTier());
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairFuelCost(this.getTier());
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairLifeCost(this.getTier());
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairLifeCost(this.getTier());
	}
}
