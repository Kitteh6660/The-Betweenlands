package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectYunugaz implements IAspectType {
	@Override
	public ITextComponent getName() {
		return "Yunugaz";
	}

	@Override
	public String getType() {
		return I18n.get("manual.wind");
	}

	@Override
	public String getDescription() {
		return "Magical property which relates to wind. Any combination with this effect can be related to the element wind.";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(TheBetweenlands.MOD_ID, "textures/items/strictly_herblore/misc/aspect_yunugaz.png");
	}

	@Override
	public int getColor() {
		return 0xFF00FFBB;
	}
}
