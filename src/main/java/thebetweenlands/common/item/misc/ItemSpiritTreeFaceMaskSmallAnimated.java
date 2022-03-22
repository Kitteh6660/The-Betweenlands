package thebetweenlands.common.item.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.SoundRegistry;

public class ItemSpiritTreeFaceMaskSmallAnimated extends Item {
	public ItemSpiritTreeFaceMaskSmallAnimated() {
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

		if(entityIn instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) entityIn;

			if(!worldIn.isClientSide()) {
				boolean mainhand = living.getItemInHand(Hand.MAIN_HAND) == stack;
				boolean offhand = living.getItemInHand(Hand.OFF_HAND) == stack;
				if((mainhand || offhand) && worldIn.rand.nextInt(60) == 0) {
					worldIn.playSound(null, entityIn.getX(), entityIn.getY() + entityIn.height / 2, entityIn.getZ(), SoundRegistry.SPIRIT_TREE_FACE_SMALL_LIVING, SoundCategory.PLAYERS, 0.35F, 1.4F);
				}
			}
		}
	}
}
