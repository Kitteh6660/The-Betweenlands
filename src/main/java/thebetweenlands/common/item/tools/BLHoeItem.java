package thebetweenlands.common.item.tools;

import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HoeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.common.item.BLMaterialRegistry;

public class BLHoeItem extends HoeItem implements ICorrodible, IAnimatorRepairable {

	public BLHoeItem(IItemTier itemTier, int damage, float speed, Properties properties) {
		super(itemTier, damage, speed, properties);
	}


	
	@Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return CorrosionHelper.shouldCauseBlockBreakReset(oldStack, newStack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return CorrosionHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
		Material material = state.getMaterial();
		float str = material != Material.LEAVES && material != Material.VEGETABLE && material != Material.PLANT ? super.getDestroySpeed(stack, state) : this.speed;
		str = CorrosionHelper.getDestroySpeed(str, stack, state);
		return str; 
    }

    //TODO: Update this code.
    /*@Override
    public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
        CorrosionHelper.updateCorrosion(itemStack, world, holder, slot, isHeldItem);
    }*/
	
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return CorrosionHelper.getAttributeModifiers(super.getAttributeModifiers(slot, stack), slot, stack, BASE_ATTACK_DAMAGE_UUID, this.getAttackDamage());
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
