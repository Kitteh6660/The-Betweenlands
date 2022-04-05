package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.aspect.AspectManager.AspectGroup;
import thebetweenlands.common.herblore.aspect.AspectManager.AspectTier;
import thebetweenlands.common.herblore.aspect.DefaultItemStackMatchers;
import thebetweenlands.common.herblore.aspect.type.AspectArmaniis;
import thebetweenlands.common.herblore.aspect.type.AspectAzuwynn;
import thebetweenlands.common.herblore.aspect.type.AspectByariis;
import thebetweenlands.common.herblore.aspect.type.AspectByrginaz;
import thebetweenlands.common.herblore.aspect.type.AspectCelawynn;
import thebetweenlands.common.herblore.aspect.type.AspectDayuniis;
import thebetweenlands.common.herblore.aspect.type.AspectFergalaz;
import thebetweenlands.common.herblore.aspect.type.AspectFirnalaz;
import thebetweenlands.common.herblore.aspect.type.AspectFreiwynn;
import thebetweenlands.common.herblore.aspect.type.AspectGeoliirgaz;
import thebetweenlands.common.herblore.aspect.type.AspectOrdaniis;
import thebetweenlands.common.herblore.aspect.type.AspectUduriis;
import thebetweenlands.common.herblore.aspect.type.AspectWodren;
import thebetweenlands.common.herblore.aspect.type.AspectYeowynn;
import thebetweenlands.common.herblore.aspect.type.AspectYihinren;
import thebetweenlands.common.herblore.aspect.type.AspectYunugaz;

public class AspectRegistry {
	private AspectRegistry() { }

	public static final List<IAspectType> ASPECT_TYPES = new ArrayList<IAspectType>();

	public static final IAspectType AZUWYNN = new AspectAzuwynn();
	public static final IAspectType ARMANIIS = new AspectArmaniis();
	public static final IAspectType BYARIIS = new AspectByariis();
	public static final IAspectType BYRGINAZ = new AspectByrginaz();
	public static final IAspectType CELAWYNN = new AspectCelawynn();
	public static final IAspectType DAYUNIIS = new AspectDayuniis();
	public static final IAspectType FERGALAZ = new AspectFergalaz();
	public static final IAspectType FIRNALAZ = new AspectFirnalaz();
	public static final IAspectType FREIWYNN = new AspectFreiwynn();
	public static final IAspectType GEOLIIRGAZ = new AspectGeoliirgaz();
	public static final IAspectType ORDANIIS = new AspectOrdaniis();
	public static final IAspectType YEOWYNN = new AspectYeowynn();
	public static final IAspectType YUNUGAZ = new AspectYunugaz();
	public static final IAspectType YIHINREN = new AspectYihinren();
	public static final IAspectType WODREN = new AspectWodren();
	public static final IAspectType UDURIIS = new AspectUduriis();
	
	static {
		try {
			for(Field f : AspectRegistry.class.getDeclaredFields()) {
				if(f.getType() == IAspectType.class) {
					Object obj = f.get(null);
					if(obj instanceof IAspectType) {
						ASPECT_TYPES.add((IAspectType)obj);
					}
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static IAspectType getAspectTypeFromName(String name) {
		for(IAspectType type : ASPECT_TYPES) {
			if(type.getName().equals(name)) return type;
		}
		return null;
	}

	public static void init() {
		registerItems();
		registerAspects();
	}

	private static void registerItems() {
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_ALGAE.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 					AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_ARROW_ARUM.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_BLACKHAT_MUSHROOM.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 2);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_BOG_BEAN.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_BONESET.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_BOTTLE_BRUSH_GRASS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 	AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_BROOM_SEDGE.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_BUTTON_BUSH.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_CATTAIL.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_CAVE_GRASS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_CAVE_MOSS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_COPPER_IRIS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_DRIED_SWAMP_REED.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_FLATHEAD_MUSHROOM.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 2);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_HANGER.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_LICHEN.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_MARSH_HIBISCUS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_MARSH_MALLOW.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 2);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_MILKWEED.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 2);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_MOSS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 					AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_NETTLE.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_PHRAGMITES.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_PICKEREL_WEED.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_SHOOTS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_SLUDGECREEP.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_SOFT_RUSH.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_SWAMP_KELP.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_SWAMP_GRASS_TALL.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_ROOTS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 					AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_WEEDWOOD_BARK.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_WATER_WEEDS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_VOLARPAD.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 2);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_THORNS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_POISON_IVY.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_GENERIC_LEAF.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.15F, 0.2F, 4);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_BLADDERWORT_FLOWER.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 	AspectTier.COMMON, AspectGroup.HERB, 0.25F, 0.5F, 2);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_BLADDERWORT_STALK.get()), DefaultItemStackMatchers.ITEM_DAMAGE,		AspectTier.COMMON, AspectGroup.HERB, 0.15F, 0.5F, 4);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_BLOOD_SNAIL_SHELL.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.5F, 2);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_BLUE_IRIS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.5F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_BLUE_EYED_GRASS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_CARDINAL_FLOWER.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_MIRE_CORAL.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_MARSH_MARIGOLD.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_GOLDEN_CLUB.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_DEEP_WATER_CORAL.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_MIRE_SNAIL_SHELL.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F, 2);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_BULB_CAPPED_MUSHROOM.get()), DefaultItemStackMatchers.ITEM_DAMAGE,	AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_ANGLER_TOOTH.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.UNCOMMON, AspectGroup.HERB, 0.85F, 0.45F, 2);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_SUNDEW.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.RARE, AspectGroup.HERB, 1.25F, 0.5F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_PITCHER_PLANT.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.RARE, AspectGroup.HERB, 1.25F, 0.5F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_VENUS_FLY_TRAP.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.RARE, AspectGroup.HERB, 1.25F, 0.5F);

		//Sludge worm dungeon plants
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_EDGE_SHROOM.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.SLUDGE_WORM_DUNGEON, 0.425F, 0.35F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_EDGE_SHROOM.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 1);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_EDGE_MOSS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.SLUDGE_WORM_DUNGEON, 0.425F, 0.35F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_EDGE_MOSS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 2);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_EDGE_LEAF.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.SLUDGE_WORM_DUNGEON, 0.425F, 0.35F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_EDGE_LEAF.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.15F, 0.2F, 3);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_ROTBULB.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.SLUDGE_WORM_DUNGEON, 0.425F, 0.35F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_ROTBULB.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 2);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_PALE_GRASS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.SLUDGE_WORM_DUNGEON, 0.85F, 0.45F, 2);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_PALE_GRASS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.UNCOMMON, AspectGroup.HERB, 0.425F, 0.35F, 1);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_STRING_ROOTS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.SLUDGE_WORM_DUNGEON, 0.425F, 0.35F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_STRING_ROOTS.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 			AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 2);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_CRYPTWEED.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.SLUDGE_WORM_DUNGEON, 0.425F, 0.35F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_CRYPTWEED.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 				AspectTier.COMMON, AspectGroup.HERB, 0.425F, 0.35F, 2);
		
		//Gems
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_GREEN_MIDDLE_GEM.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.GEM_FERGALAZ, 1.75F, 0.25F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_CRIMSON_MIDDLE_GEM.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 	AspectTier.UNCOMMON, AspectGroup.GEM_FIRNALAZ, 1.75F, 0.25F);
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.GROUND_AQUA_MIDDLE_GEM.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 		AspectTier.UNCOMMON, AspectGroup.GEM_BYRGINAZ, 1.75F, 0.25F);
	
		//Sap spit
		AspectManager.addStaticAspectsToItem(new ItemStack(ItemRegistry.SAP_SPIT.get()), DefaultItemStackMatchers.ITEM_DAMAGE, 					AspectTier.UNCOMMON, AspectGroup.SAP_SPIT, 1.0F, 0.1F, 2);
	}

	private static void registerAspects() {
		AspectManager.registerAspect(AspectRegistry.BYARIIS, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.HIGH);
		AspectManager.registerAspect(AspectRegistry.AZUWYNN, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.LOW_MEDIUM);
		AspectManager.registerAspect(AspectRegistry.CELAWYNN, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.LOW_MEDIUM);
		AspectManager.registerAspect(AspectRegistry.ORDANIIS, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.LOW_MEDIUM);
		AspectManager.registerAspect(AspectRegistry.YEOWYNN, 	AspectTier.COMMON, 		AspectGroup.HERB, Amounts.LOW_MEDIUM);
		AspectManager.registerAspect(AspectRegistry.ARMANIIS, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.BYRGINAZ, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.DAYUNIIS, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.FERGALAZ, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.FIRNALAZ, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.FREIWYNN, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.YUNUGAZ, 	AspectTier.UNCOMMON, 	AspectGroup.HERB, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.GEOLIIRGAZ,	AspectTier.RARE, 		AspectGroup.HERB, Amounts.MEDIUM_HIGH);
		AspectManager.registerAspect(AspectRegistry.YIHINREN, 	AspectTier.RARE, 		AspectGroup.HERB, Amounts.MEDIUM_HIGH);

		//For middle gems
		AspectManager.registerAspect(AspectRegistry.BYRGINAZ, 	AspectTier.UNCOMMON, 	AspectGroup.GEM_BYRGINAZ, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.FERGALAZ, 	AspectTier.UNCOMMON, 	AspectGroup.GEM_FERGALAZ, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.FIRNALAZ, 	AspectTier.UNCOMMON, 	AspectGroup.GEM_FIRNALAZ, Amounts.MEDIUM);
		
		//Sap spit
		AspectManager.registerAspect(AspectRegistry.YEOWYNN, 	AspectTier.UNCOMMON, 	AspectGroup.SAP_SPIT, Amounts.HIGH);
		AspectManager.registerAspect(AspectRegistry.ORDANIIS, 	AspectTier.UNCOMMON, 	AspectGroup.SAP_SPIT, Amounts.HIGH);
		
		//Sludge worm dungeon aspects
		AspectManager.registerAspect(AspectRegistry.WODREN, 	AspectTier.COMMON, 		AspectGroup.SLUDGE_WORM_DUNGEON, Amounts.MEDIUM);
		AspectManager.registerAspect(AspectRegistry.UDURIIS, 	AspectTier.COMMON, 		AspectGroup.SLUDGE_WORM_DUNGEON, Amounts.MEDIUM);
	}
}
