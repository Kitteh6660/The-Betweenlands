package thebetweenlands.common.item.tools.bow;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.TranslationHelper;

import javax.annotation.Nullable;

public class ItemBLArrow extends ArrowItem {
	private EnumArrowType type;

	public ItemBLArrow(Item.Properties properties, EnumArrowType type) {
		super(properties);
		this.type = type;
	}

	@Override
	public EntityBLArrow createArrow(World worldIn, ItemStack stack, LivingEntity shooter) {
		EntityBLArrow entityArrow = new EntityBLArrow(worldIn, shooter);
		entityArrow.setType(this.type);
		return entityArrow;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ItemBLArrow item = (ItemBLArrow) stack.getItem();

		if (item == ItemRegistry.OCTINE_ARROW) {
			tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.arrow.octine"));
		}

		if (item == ItemRegistry.BASILISK_ARROW) {
			tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.arrow.basilisk"));
		}
	}

	public EnumArrowType getType() {
		return this.type;
	}
}
