package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectGeoliirgaz implements IAspectType {
	@Override
	public ITextComponent getName() {
		return "Geoliirgaz";
	}

	@Override
	public String getType() {
		return I18n.get("manual.void");
	}

	@Override
	public String getDescription() {
		return "Magical property which relates to the void or ether. Any combination with this effect can be related to void or darkness.";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(TheBetweenlands.MOD_ID, "textures/items/strictly_herblore/misc/aspect_geoliirgaz.png");
	}

	@Override
	public int getColor() {
		return 0xFF222228;
	}
}
