package thebetweenlands.common.registries;

import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.api.distmarker.Dist;

//TODO: Trim down this file as some of those use the new Forge registry system.
public class Registries {
	public static final Registries INSTANCE = new Registries();

	public void preInit() {
		FluidRegistry.preInit();
		BlockRegistry.preInit();
		ItemRegistry.preInit();
		EntityRegistry.preInit();
		SoundRegistry.preInit();
		CapabilityRegistry.preInit();
		RuneRegistry.preInit();
		StorageRegistry.preInit();
		CustomRecipeRegistry.preInit();
		AdvancementCriterionRegistry.preInit();
		LootTableRegistry.preInit();

		if (FMLEnvironment.dist == Dist.CLIENT) {
			ModelRegistry.preInit();
			AmbienceRegistry.preInit();
		}
	}

	public void init() {
		//this.blockRegistry.init();
		TileEntityRegistry.init();
		AspectRegistry.init();
		FluidRegistry.init();
	}
}
