package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectCelawynn implements IAspectType {
	@Override
	public ITextComponent getName() {
		return "Celawynn";
	}

	@Override
	public String getType() {
		return I18n.get("manual.stomach");
	}

	@Override
	public String getDescription() {
		return "Has effect on the stomach. So this could have effect on the hunger bar for example.";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(TheBetweenlands.MOD_ID, "textures/items/strictly_herblore/misc/aspect_celawynn.png");
	}

	@Override
	public int getColor() {
		return 0xFF4ACE48;
	}
}
