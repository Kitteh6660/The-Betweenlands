package thebetweenlands.api.runechain.io.types;

import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;

public final class RuneTokenDescriptors {
	private RuneTokenDescriptors()  {}

	public static final ResourceLocation ANY = new ResourceLocation(TheBetweenlands.MOD_ID, "any");
	public static final ResourceLocation BLOCK = new ResourceLocation(TheBetweenlands.MOD_ID, "block");
	public static final ResourceLocation ENTITY = new ResourceLocation(TheBetweenlands.MOD_ID, "entity");
	public static final ResourceLocation DIRECTION = new ResourceLocation(TheBetweenlands.MOD_ID, "direction");
	public static final ResourceLocation POSITION = new ResourceLocation(TheBetweenlands.MOD_ID, "position");
	public static final ResourceLocation EFFECT = new ResourceLocation(TheBetweenlands.MOD_ID, "effect");
	public static final ResourceLocation ITEM = new ResourceLocation(TheBetweenlands.MOD_ID, "item");
}
