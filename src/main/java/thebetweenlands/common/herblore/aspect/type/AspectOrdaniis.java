package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectOrdaniis implements IAspectType {
	@Override
	public ITextComponent getName() {
		return "Ordaniis";
	}

	@Override
	public String getType() {
		return I18n.get("manual.enhance");
	}

	@Override
	public String getDescription() {
		return "Needs new decription";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(TheBetweenlands.MOD_ID, "textures/items/strictly_herblore/misc/aspect_ordaniis.png");
	}

	@Override
	public int getColor() {
		return 0xFF64EF99;
	}
}
