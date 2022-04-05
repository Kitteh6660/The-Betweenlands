package thebetweenlands.common.herblore.elixir;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;

public class PotionEnlightened extends Effect 
{
	public PotionEnlightened() {
		super(EffectType.BENEFICIAL, 5926017);
		/*this.setRegistryName(new ResourceLocation(TheBetweenlands.MOD_ID, "enlightened"));
		this.setPotionName("bl.potion.enlightened");
		this.setIconIndex(1, 0);*/
	}

	@Override
	public boolean shouldRender(EffectInstance effect) {
		return false;
	}

	@Override
	public boolean shouldRenderHUD(EffectInstance effect) {
		return false;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return Collections.emptyList();
	}
}
