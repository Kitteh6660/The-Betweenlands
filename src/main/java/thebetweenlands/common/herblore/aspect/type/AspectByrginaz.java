package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectByrginaz implements IAspectType {
	@Override
	public ITextComponent getName() {
		return "Byrginaz";
	}

	@Override
	public String getType() {
		return I18n.get("manual.water");
	}

	@Override
	public String getDescription() {
		return "Magical property which relates to water. Any combination with this effect can be related to water.";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(TheBetweenlands.MOD_ID, "textures/items/strictly_herblore/misc/aspect_byrginaz.png");
	}

	@Override
	public int getColor() {
		return 0xFF1EBBDB;
	}
}
