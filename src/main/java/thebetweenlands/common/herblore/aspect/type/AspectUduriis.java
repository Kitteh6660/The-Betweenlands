package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectUduriis implements IAspectType {
	@Override
	public String getName() {
		return "Uduriis";
	}

	@Override
	public String getType() {
		return I18n.get("manual.chaos");
	}

	@Override
	public String getDescription() {
		return "The aspect that correlates to the natural orderless state of the universe.";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/misc/aspect_uduriis.png");
	}

	@Override
	public int getColor() {
		return 0xFF3A1352;
	}
}
