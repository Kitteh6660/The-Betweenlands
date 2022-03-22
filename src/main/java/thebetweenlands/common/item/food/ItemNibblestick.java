package thebetweenlands.common.item.food;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.registries.CapabilityRegistry;

import javax.annotation.Nullable;

public class ItemNibblestick extends BLFoodItem {
	public ItemNibblestick() {
		super(1, 0.1F, false);
	}

	@Override
	public boolean canGetSickOf(@Nullable PlayerEntity player, ItemStack stack) {
		return true; //Keep food sickness always enabled
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, PlayerEntity player) {
		IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
		if(cap != null && FoodSickness.getSicknessForHatred(cap.getFoodHatred(this)) != FoodSickness.SICK) {
			int xp = worldIn.rand.nextInt(4);
			if(xp > 0) {
				if(!worldIn.isClientSide()) {
					cap.increaseFoodHatred(this, 4, 0); //Increased food sickness speed
					player.addExperience(xp);
					worldIn.playSound(null, player.getX(), player.getY() + 0.5D, player.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
				} else {
					this.addFinishEatingParticles(stack, worldIn, player);
				}
			}
		}

		super.onFoodEaten(stack, worldIn, player);
	}

	@OnlyIn(Dist.CLIENT)
	public void addFinishEatingParticles(ItemStack stack, World world, PlayerEntity player) {
		for(int i = 0; i < 20; i++) {
			BLParticles.XP_PIECES.spawn(world, player.getX() + world.rand.nextFloat() * 0.6F - 0.3F, player.getY() + player.getEyeHeight() - 0.1F + world.rand.nextFloat() * 0.6F - 0.3F, player.getZ() + world.rand.nextFloat() * 0.6F - 0.3F);
		}
	}
}
