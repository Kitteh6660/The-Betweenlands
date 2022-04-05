package thebetweenlands.common.item.food;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.registries.CapabilityRegistry;

import javax.annotation.Nullable;

public class ItemNibblestick extends BLFoodItem {
	
	public ItemNibblestick(Properties properties) {
		//super(1, 0.1F, false);
		super(true, 0, 0, properties);
	}

	@Override
	public boolean canGetSickOf(@Nullable PlayerEntity player, ItemStack stack) {
		return true; //Keep food sickness always enabled
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entity) {
		IFoodSicknessCapability cap = (IFoodSicknessCapability) entity.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
		if(cap != null && FoodSickness.getSicknessForHatred(cap.getFoodHatred(this)) != FoodSickness.SICK && entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			int xp = worldIn.random.nextInt(4);
			if(xp > 0) {
				if(!worldIn.isClientSide()) {
					cap.increaseFoodHatred(this, 4, 0); //Increased food sickness speed
					player.giveExperiencePoints(xp);
					worldIn.playSound(null, player.getX(), player.getY() + 0.5D, player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, 0.8F + worldIn.random.nextFloat() * 0.4F);
				} else {
					this.addFinishEatingParticles(stack, worldIn, player);
				}
			}
		}

		return super.finishUsingItem(stack, worldIn, entity);
	}

	@OnlyIn(Dist.CLIENT)
	public void addFinishEatingParticles(ItemStack stack, World world, PlayerEntity player) {
		for(int i = 0; i < 20; i++) {
			BLParticles.XP_PIECES.spawn(world, player.getX() + world.random.nextFloat() * 0.6F - 0.3F, player.getY() + player.getEyeHeight() - 0.1F + world.random.nextFloat() * 0.6F - 0.3F, player.getZ() + world.random.nextFloat() * 0.6F - 0.3F);
		}
	}
}
