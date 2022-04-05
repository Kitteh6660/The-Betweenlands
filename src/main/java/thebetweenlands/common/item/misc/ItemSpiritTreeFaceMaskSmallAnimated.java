package thebetweenlands.common.item.misc;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thebetweenlands.common.registries.SoundRegistry;

public class ItemSpiritTreeFaceMaskSmallAnimated extends Item {
	
	public ItemSpiritTreeFaceMaskSmallAnimated(Properties properties) {
		super(properties);
		//this.setCreativeTab(BLCreativeTabs.SPECIALS);
	}

	@Override
	public void onArmorTick(ItemStack stack, World worldIn, PlayerEntity entityIn) {
		super.onArmorTick(stack, worldIn, entityIn);

		if(entityIn instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) entityIn;

			if(!worldIn.isClientSide()) {
				boolean mainhand = living.getItemInHand(Hand.MAIN_HAND) == stack;
				boolean offhand = living.getItemInHand(Hand.OFF_HAND) == stack;
				if((mainhand || offhand) && worldIn.random.nextInt(60) == 0) {
					worldIn.playSound(null, entityIn.getX(), entityIn.getY() + entityIn.getBbHeight() / 2, entityIn.getZ(), SoundRegistry.SPIRIT_TREE_FACE_SMALL_LIVING, SoundCategory.PLAYERS, 0.35F, 1.4F);
				}
			}
		}
	}
}
