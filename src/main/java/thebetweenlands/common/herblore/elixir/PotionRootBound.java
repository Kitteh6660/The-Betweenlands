package thebetweenlands.common.herblore.elixir;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes.Attributes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class PotionRootBound extends Effect 
{
	public PotionRootBound() {
		super(EffectType.HARMFUL, 5926017);
		/*super(true, 5926017);
		this.setRegistryName(new ResourceLocation(ModInfo.ID, "root_bound"));
		this.setPotionName("bl.potion.rootBound");
		this.setIconIndex(1, 0);*/
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "be8859c1-024b-4f31-b606-5991011ddd98", -1, AttributeModifier.Operation.MULTIPLY_TOTAL);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return Collections.emptyList();
	}
}
