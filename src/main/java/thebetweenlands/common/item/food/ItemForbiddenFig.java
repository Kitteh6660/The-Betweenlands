package thebetweenlands.common.item.food;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.item.IDecayFood;
import thebetweenlands.common.registries.SoundRegistry;
import javax.annotation.Nullable;

public class ItemForbiddenFig extends BLFoodItem implements IDecayFood {
	
    public ItemForbiddenFig(Properties properties) {
        super(false, 20, 0.2F, properties);
    }

    @Override
    public int getDecayHealAmount(ItemStack stack) {
        return 20;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tooltip.bl.fig"));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {

        if (world.isClientSide()) {
        	if (entity instanceof PlayerEntity) {
        		((PlayerEntity)entity).displayClientMessage(new TranslationTextComponent("chat.item.forbiddenfig"), true);
        		world.playLocalSound(((PlayerEntity)entity), entity.getX(), entity.getY(), entity.getZ(), SoundRegistry.FIG, SoundCategory.AMBIENT, 0.7F, 0.8F);
        	}
        } else {
            entity.addEffect(new EffectInstance(Effects.BLINDNESS, 1200, 1));
            entity.addEffect(new EffectInstance(Effects.WEAKNESS, 1200, 1));
        }
        return super.finishUsingItem(stack, world, entity);
    }
    
    /*@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}*/
}
