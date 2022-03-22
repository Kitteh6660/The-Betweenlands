package thebetweenlands.common.item.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.ItemRegistry;


public class RubberBootsItem extends ItemBLArmor {
	
	public RubberBootsItem(Properties properties) {
		super(BLArmorMaterial.RUBBER, EquipmentSlotType.FEET, properties);
		/*super(BLMaterialRegistry.ARMOR_RUBBER, 3, EquipmentSlotType.FEET, "rubber_boots");
		this.setCreativeTab(BLCreativeTabs.GEARS);*/
	}

	public static boolean isEntityWearingRubberBoots(Entity entity) {
		return entity instanceof PlayerEntity && !((PlayerEntity)entity).getItemBySlot(EquipmentSlotType.FEET).isEmpty() && ((PlayerEntity)entity).getItemBySlot(EquipmentSlotType.FEET).getItem() instanceof RubberBootsItem;
	}

	public static boolean canEntityWalkOnMud(Entity entity) {
		if(entity instanceof LivingEntity && ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.isActive((LivingEntity)entity)) return false;
		boolean canWalk = isEntityWearingRubberBoots(entity);
		boolean hasLurkerArmor = entity instanceof PlayerEntity && entity.isInWater() && !((PlayerEntity) entity).getItemBySlot(EquipmentSlotType.FEET).isEmpty() && ((PlayerEntity) entity).getItemBySlot(EquipmentSlotType.FEET).getItem() == ItemRegistry.LURKER_SKIN_BOOTS.get();
		return entity.isInWater() || entity instanceof IEntityBL || entity instanceof ItemEntity || canWalk || hasLurkerArmor || (entity instanceof PlayerEntity && ((PlayerEntity)entity).isCreative() && ((PlayerEntity)entity).abilities.mayfly);
	}
}
