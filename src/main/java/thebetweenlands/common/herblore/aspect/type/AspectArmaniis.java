package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectArmaniis implements IAspectType {
	@Override
	public ITextComponent getName() {
		return "Armaniis";
	}

	@Override
	public String getType() {
		return I18n.get("manual.desire");
	}

	@Override
	public String getDescription() {
		return "Has effect on the desires of a mob or the player. Could be useful for food, but also things like trading or corrupting the desire.";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(TheBetweenlands.MOD_ID, "textures/items/strictly_herblore/misc/aspect_armaniis.png");
	}

	@Override
	public int getColor() {
		return 0xFFFFCC00;
	}
}
