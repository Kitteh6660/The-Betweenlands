package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectDayuniis implements IAspectType {
	@Override
	public ITextComponent getName() {
		return "Dayuniis";
	}

	@Override
	public String getType() {
		return I18n.get("manual.mind");
	}

	@Override
	public String getDescription() {
		return "Has effect on the player's mind and on how senses work. Could be positive, or negative (think nausea/schizophrenia).";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(TheBetweenlands.MOD_ID, "textures/items/strictly_herblore/misc/aspect_dayuniis.png");
	}

	@Override
	public int getColor() {
		return 0xFFB148CE;
	}
}
