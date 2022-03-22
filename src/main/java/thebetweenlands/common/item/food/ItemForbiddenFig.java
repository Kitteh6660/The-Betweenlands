package thebetweenlands.common.item.food;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.item.IDecayFood;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.TranslationHelper;

import javax.annotation.Nullable;


public class ItemForbiddenFig extends BLFoodItem implements IDecayFood {
	
    public ItemForbiddenFig(boolean canGetSick, int decayHeal, float decayHealSaturation, Properties properties) {
        super(canGetSick, decayHeal, decayHealSaturation, properties);
    }

    @Override
    public int getDecayHealAmount(ItemStack stack) {
        return 20;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.fig"));
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, PlayerEntity player) {
        super.onFoodEaten(stack, world, player);

        if (world.isClientSide()) {
            player.sendStatusMessage(new TranslationTextComponent("chat.item.forbiddenfig"), true);
            world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundRegistry.FIG, SoundCategory.AMBIENT, 0.7F, 0.8F);
        } else {
            player.addEffect(new EffectInstance(Effects.BLINDNESS, 1200, 1));
            player.addEffect(new EffectInstance(Effects.WEAKNESS, 1200, 1));
        }
    }
    
    @Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}
}
