package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.terrain.BlockDentrothyst.EnumDentrothyst;
import thebetweenlands.common.block.terrain.BlockLifeCrystalStalactite;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.EntityGalleryFrame;
import thebetweenlands.common.entity.EntityTinyWormEggSac;
import thebetweenlands.common.entity.mobs.EntityBloodSnail;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityLeech;
import thebetweenlands.common.entity.mobs.EntityMireSnail;
import thebetweenlands.common.entity.mobs.EntitySludge;
import thebetweenlands.common.entity.mobs.EntitySmollSludge;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.mobs.EntitySwarm;
import thebetweenlands.common.entity.mobs.EntityTermite;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.armor.BLArmorMaterial;
import thebetweenlands.common.item.armor.ItemAncientArmor;
import thebetweenlands.common.item.armor.ItemBLArmor;
import thebetweenlands.common.item.armor.ItemBoneArmor;
import thebetweenlands.common.item.armor.ItemExplorersHat;
import thebetweenlands.common.item.armor.ItemLurkerSkinArmor;
import thebetweenlands.common.item.armor.MarshRunnerBootsItem;
import thebetweenlands.common.item.armor.RubberBootsItem;
import thebetweenlands.common.item.armor.SkullMaskItem;
import thebetweenlands.common.item.armor.SpiritTreeFaceMaskLargeItem;
import thebetweenlands.common.item.armor.SpiritTreeFaceMaskSmallItem;
import thebetweenlands.common.item.armor.ItemSyrmoriteArmor;
import thebetweenlands.common.item.armor.ItemValoniteArmor;
import thebetweenlands.common.item.equipment.ItemAmulet;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;
import thebetweenlands.common.item.equipment.ItemRingOfDispersion;
import thebetweenlands.common.item.equipment.ItemRingOfFlight;
import thebetweenlands.common.item.equipment.ItemRingOfPower;
import thebetweenlands.common.item.equipment.ItemRingOfRecruitment;
import thebetweenlands.common.item.equipment.ItemRingOfSummoning;
import thebetweenlands.common.item.farming.ItemAspectrusSeeds;
import thebetweenlands.common.item.farming.ItemMiddleFruitBushSeeds;
import thebetweenlands.common.item.farming.ItemPlantTonic;
import thebetweenlands.common.item.farming.ItemSpores;
import thebetweenlands.common.item.farming.ItemSwampKelp;
import thebetweenlands.common.item.farming.ItemSwampReed;
import thebetweenlands.common.item.food.ItemAspectrusFruit;
import thebetweenlands.common.item.food.BLFoodItem;
import thebetweenlands.common.item.food.BLFoods;
import thebetweenlands.common.item.food.ItemBlackHatMushroom;
import thebetweenlands.common.item.food.ItemChiromawWing;
import thebetweenlands.common.item.food.ItemForbiddenFig;
import thebetweenlands.common.item.food.ItemGertsDonut;
import thebetweenlands.common.item.food.ItemMireScramble;
import thebetweenlands.common.item.food.ItemMireSnailEgg;
import thebetweenlands.common.item.food.ItemNettleSoup;
import thebetweenlands.common.item.food.ItemNibblestick;
import thebetweenlands.common.item.food.ItemSapBall;
import thebetweenlands.common.item.food.ItemSapJello;
import thebetweenlands.common.item.food.ItemSpiritFruit;
import thebetweenlands.common.item.food.ItemTaintedPotion;
import thebetweenlands.common.item.food.ItemTangledRoot;
import thebetweenlands.common.item.food.ItemWeepingBluePetal;
import thebetweenlands.common.item.food.ItemWeepingBluePetalSalad;
import thebetweenlands.common.item.food.ItemWightHeart;
import thebetweenlands.common.item.herblore.ItemAspectVial;
import thebetweenlands.common.item.herblore.ItemCrushed;
import thebetweenlands.common.item.herblore.ItemDentrothystFluidVial;
import thebetweenlands.common.item.herblore.ItemDentrothystVial;
import thebetweenlands.common.item.herblore.ItemElixir;
import thebetweenlands.common.item.herblore.ItemManualHL;
import thebetweenlands.common.item.herblore.ItemPlantDrop;
import thebetweenlands.common.item.herblore.ItemScrivenerTool;
import thebetweenlands.common.item.herblore.rune.ItemRune;
import thebetweenlands.common.item.herblore.rune.ItemRuneChain;
import thebetweenlands.common.item.herblore.rune.ItemRunelet;
import thebetweenlands.common.item.misc.ItemAmateMap;
import thebetweenlands.common.item.misc.ItemAmuletSlot;
import thebetweenlands.common.item.misc.ItemAngryPebble;
import thebetweenlands.common.item.misc.ItemBLRecord;
import thebetweenlands.common.item.misc.ItemBarkAmulet;
import thebetweenlands.common.item.misc.ItemBoneWayfinder;
import thebetweenlands.common.item.misc.ItemCavingRope;
import thebetweenlands.common.item.misc.ItemChiromawEgg;
import thebetweenlands.common.item.misc.ItemChiromawTame;
import thebetweenlands.common.item.misc.ItemCritters;
import thebetweenlands.common.item.misc.ItemDentrothystShard;
import thebetweenlands.common.item.misc.ItemDoorBetweenlands;
import thebetweenlands.common.item.misc.ItemDraeton;
import thebetweenlands.common.item.misc.ItemEmptyAmateMap;
import thebetweenlands.common.item.misc.ItemGalleryFrame;
import thebetweenlands.common.item.misc.ItemGem;
import thebetweenlands.common.item.misc.ItemGemSinger;
import thebetweenlands.common.item.misc.ItemGlue;
import thebetweenlands.common.item.misc.ItemGrapplingHook;
import thebetweenlands.common.item.misc.ItemLifeCrystal;
import thebetweenlands.common.item.misc.ItemLoreScrap;
import thebetweenlands.common.item.misc.ItemLurkerSkinPatch;
import thebetweenlands.common.item.misc.ItemMagicItemMagnet;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.item.misc.ItemMossBed;
import thebetweenlands.common.item.misc.ItemMummyBait;
import thebetweenlands.common.item.misc.ItemOctineIngot;
import thebetweenlands.common.item.misc.ItemPyradFlame;
import thebetweenlands.common.item.misc.ItemRingOfGathering;
import thebetweenlands.common.item.misc.ItemRope;
import thebetweenlands.common.item.misc.ItemRuneDoorKey;
import thebetweenlands.common.item.misc.ItemShimmerStone;
import thebetweenlands.common.item.misc.ItemSpiritTreeFaceMaskSmallAnimated;
import thebetweenlands.common.item.misc.ItemSwampTalisman;
import thebetweenlands.common.item.misc.ItemTarminion;
import thebetweenlands.common.item.misc.ItemVolarkite;
import thebetweenlands.common.item.misc.ItemWeedwoodRowboat;
import thebetweenlands.common.item.misc.ItemWeedwoodSign;
import thebetweenlands.common.item.misc.LocationDebugItem;
import thebetweenlands.common.item.misc.TestItem;
import thebetweenlands.common.item.misc.TestItemChimp;
import thebetweenlands.common.item.misc.TestItemChimpRuler;
import thebetweenlands.common.item.shields.ItemDentrothystShield;
import thebetweenlands.common.item.shields.ItemLivingWeedwoodShield;
import thebetweenlands.common.item.shields.ItemLurkerSkinShield;
import thebetweenlands.common.item.shields.ItemOctineShield;
import thebetweenlands.common.item.shields.ItemSyrmoriteShield;
import thebetweenlands.common.item.shields.ItemValoniteShield;
import thebetweenlands.common.item.shields.ItemWeedwoodShield;
import thebetweenlands.common.item.tools.ItemAncientBattleAxe;
import thebetweenlands.common.item.tools.ItemAncientGreatsword;
import thebetweenlands.common.item.tools.BLAxeItem;
import thebetweenlands.common.item.tools.ItemBLBucket;
import thebetweenlands.common.item.tools.BLPickaxeItem;
import thebetweenlands.common.item.tools.ItemBLShield;
import thebetweenlands.common.item.tools.BLShovelItem;
import thebetweenlands.common.item.tools.BLSwordItem;
import thebetweenlands.common.item.tools.ItemBucketInfusion;
import thebetweenlands.common.item.tools.ItemChirobarbErupter;
import thebetweenlands.common.item.tools.ItemGreataxe;
import thebetweenlands.common.item.tools.ItemHagHacker;
import thebetweenlands.common.item.tools.ItemLootSword;
import thebetweenlands.common.item.tools.ItemNet;
import thebetweenlands.common.item.tools.ItemOctineAxe;
import thebetweenlands.common.item.tools.ItemOctinePickaxe;
import thebetweenlands.common.item.tools.ItemOctineShovel;
import thebetweenlands.common.item.tools.ItemOctineSword;
import thebetweenlands.common.item.tools.ItemPestle;
import thebetweenlands.common.item.tools.ItemShockwaveSword;
import thebetweenlands.common.item.tools.ItemSickle;
import thebetweenlands.common.item.tools.ItemSimpleSlingshot;
import thebetweenlands.common.item.tools.ItemSpecificBucket;
import thebetweenlands.common.item.tools.ItemSwiftPick;
import thebetweenlands.common.item.tools.ItemSyrmoriteBucketSolidRubber;
import thebetweenlands.common.item.tools.ItemSyrmoriteShears;
import thebetweenlands.common.item.tools.ItemVoodooDoll;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.item.tools.bow.ItemBLArrow;
import thebetweenlands.common.item.tools.bow.ItemBLBow;
import thebetweenlands.common.item.tools.bow.ItemPredatorBow;
import thebetweenlands.common.lib.ModInfo;

public class ItemRegistry {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheBetweenlands.MOD_ID);
    
	//Blocks
	  //TODO: Fill out block items here!
    public static final RegistryObject<Item> WEEDWOOD_DOOR_ITEM = ITEMS.register("weedwood_door", () -> new ItemDoorBetweenlands(BlockRegistry.WEEDWOOD_DOOR.get(), new Item.Properties().tab(BLCreativeTabs.BLOCKS)));
    public static final RegistryObject<Item> RUBBER_TREE_PLANK_DOOR_ITEM = ITEMS.register("rubber_tree_plank_door", () -> new ItemDoorBetweenlands(BlockRegistry.RUBBER_TREE_PLANK_DOOR.get(), new Item.Properties().tab(BLCreativeTabs.BLOCKS)));
    public static final RegistryObject<Item> GIANT_ROOT_PLANK_DOOR_ITEM = ITEMS.register("giant_root_plank_door", () -> new ItemDoorBetweenlands(BlockRegistry.GIANT_ROOT_PLANK_DOOR.get(), new Item.Properties().tab(BLCreativeTabs.BLOCKS)));
    public static final RegistryObject<Item> HEARTHGROVE_PLANK_DOOR_ITEM = ITEMS.register("hearthgrove_plank_door", () -> new ItemDoorBetweenlands(BlockRegistry.HEARTHGROVE_PLANK_DOOR.get(), new Item.Properties().tab(BLCreativeTabs.BLOCKS)));
    public static final RegistryObject<Item> NIBBLETWIG_PLANK_DOOR_ITEM = ITEMS.register("nibbletwig_plank_door", () -> new ItemDoorBetweenlands(BlockRegistry.NIBBLETWIG_PLANK_DOOR.get(), new Item.Properties().tab(BLCreativeTabs.BLOCKS)));
    public static final RegistryObject<Item> SYRMORITE_DOOR_ITEM = ITEMS.register("syrmorite_door", () -> new ItemDoorBetweenlands(BlockRegistry.SYRMORITE_DOOR.get(), new Item.Properties().tab(BLCreativeTabs.BLOCKS)));
    public static final RegistryObject<Item> SCABYST_DOOR_ITEM = ITEMS.register("syrmorite_door", () -> new ItemDoorBetweenlands(BlockRegistry.SCABYST_DOOR.get(), new Item.Properties().tab(BLCreativeTabs.BLOCKS)));
	
    //Generic Items
    public static final RegistryObject<Item> BLOOD_SNAIL_SHELL = ITEMS.register("blood_snail_shell", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> MIRE_SNAIL_SHELL = ITEMS.register("mure_snail_shell", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> COMPOST = ITEMS.register("compost", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> DRAGONFLY_WING = ITEMS.register("dragonfly_wings", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> LURKER_SKIN = ITEMS.register("lurker_skin", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> DRIED_SWAMP_REED = ITEMS.register("dried_swamp_reed", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> SWAMP_REED_ROPE = ITEMS.register("swamp_reed_rope", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
	public static final RegistryObject<Item> MUD_BRICK = ITEMS.register("mud_brick", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> SYRMORITE_INGOT = ITEMS.register("syrmorite_ingot", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> DRY_BARK = ITEMS.register("dry_bark", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> SLIMY_BONE = ITEMS.register("slimy_bone", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> SNAPPER_ROOT = ITEMS.register("snapper_root", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> STALKER_EYE = ITEMS.register("stalker_eye", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> SULFUR = ITEMS.register("sulfur", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> VALONITE_SHARD = ITEMS.register("valonite_shard", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> WEEDWOOD_STICK = ITEMS.register("weedwood_stick", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> ANGLER_TOOTH = ITEMS.register("angler_tooth", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> WEEDWOOD_BOWL = ITEMS.register("weedwood_bowl", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> RUBBER_BALL = ITEMS.register("rubber_ball", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> TAR_BEAST_HEART = ITEMS.register("tar_beast_heart", () -> new ItemMisc(new Item.Properties().rarity(Rarity.UNCOMMON).tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> TAR_BEAST_HEART_ANIMATED = ITEMS.register("tar_beast_heart_animated", () -> new ItemMisc(new Item.Properties().rarity(Rarity.UNCOMMON).tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> TAR_DRIP = ITEMS.register("tar_drip", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> LIMESTONE_FLUX = ITEMS.register("limestone_flux", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> INANIMATE_TARMINION = ITEMS.register("inanimate_tarminion", () -> new ItemMisc(new Item.Properties().rarity(Rarity.UNCOMMON).tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> POISON_GLAND = ITEMS.register("poison_gland", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> PARCHMENT = ITEMS.register("parchment", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    //public static final RegistryObject<Item> SHOCKWAVE_SWORD_1(33),
    //public static final RegistryObject<Item> SHOCKWAVE_SWORD_2(34),
    //public static final RegistryObject<Item> SHOCKWAVE_SWORD_3(35),
    //public static final RegistryObject<Item> SHOCKWAVE_SWORD_4(36),
    public static final RegistryObject<Item> AMULET_SOCKET = ITEMS.register("amulet_socket", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> SCABYST = ITEMS.register("scabyst", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
	public static final RegistryObject<Item> SCROLL = ITEMS.register("scroll", () -> new ItemMisc(new Item.Properties().rarity(Rarity.UNCOMMON).tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> SYRMORITE_NUGGET = ITEMS.register("syrmorite_nugget", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> OCTINE_NUGGET = ITEMS.register("octine_nugget", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> VALONITE_SPLINTER = ITEMS.register("valonite_splinter", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> CREMAINS = ITEMS.register("cremains", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> UNDYING_EMBER = ITEMS.register("undying_ember", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> INANIMATE_ANGRY_PEBBLE = ITEMS.register("inanimate_angry_pebble", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> ANCIENT_REMNANT = ITEMS.register("ancient_remnant", () -> new ItemMisc(new Item.Properties().rarity(Rarity.RARE).tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> LOOT_SCRAPS = ITEMS.register("loot_scraps", () -> new ItemMisc(new Item.Properties().rarity(Rarity.RARE).tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> FABRICATED_SCROLL = ITEMS.register("fabricated_scroll", () -> new ItemMisc(new Item.Properties().rarity(Rarity.RARE).tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> BETWEENSTONE_PEBBLE = ITEMS.register("betweenstone_pebble", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    
    public static final RegistryObject<Item> SWAMP_TALISMAN_0 = ITEMS.register("swamp_talisman_0", () -> new ItemSwampTalisman(new Item.Properties().stacksTo(1).tab(BLCreativeTabs.ITEMS))); // new ItemSwampTalisman().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final RegistryObject<Item> SWAMP_TALISMAN_1 = ITEMS.register("swamp_talisman_1", () -> new ItemSwampTalisman(new Item.Properties().stacksTo(1).tab(BLCreativeTabs.ITEMS))); // new ItemSwampTalisman().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final RegistryObject<Item> SWAMP_TALISMAN_2 = ITEMS.register("swamp_talisman_2", () -> new ItemSwampTalisman(new Item.Properties().stacksTo(1).tab(BLCreativeTabs.ITEMS))); // new ItemSwampTalisman().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final RegistryObject<Item> SWAMP_TALISMAN_3 = ITEMS.register("swamp_talisman_3", () -> new ItemSwampTalisman(new Item.Properties().stacksTo(1).tab(BLCreativeTabs.ITEMS))); // new ItemSwampTalisman().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final RegistryObject<Item> SWAMP_TALISMAN_4 = ITEMS.register("swamp_talisman_4", () -> new ItemSwampTalisman(new Item.Properties().stacksTo(1).tab(BLCreativeTabs.ITEMS))); // new ItemSwampTalisman().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final RegistryObject<Item> SWAMP_TALISMAN_5 = ITEMS.register("swamp_talisman_5", () -> new ItemSwampTalisman(new Item.Properties().stacksTo(1).tab(BLCreativeTabs.ITEMS))); // new ItemSwampTalisman().setCreativeTab(BLCreativeTabs.ITEMS);
    
    //Crushed Items
    public static final RegistryObject<Item> GROUND_GENERIC_LEAF = ITEMS.register("ground_generic_leaf", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_CATTAIL = ITEMS.register("ground_cattail", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_SWAMP_GRASS_TALL = ITEMS.register("ground_swamp_grass_tall", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> GROUND_SHOOTS = ITEMS.register("ground_shoots", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_ARROW_ARUM = ITEMS.register("ground_arrow_arum", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_BUTTON_BUSH = ITEMS.register("ground_button_bush", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_MARSH_HIBISCUS = ITEMS.register("ground_marsh_hibiscus", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_PICKEREL_WEED = ITEMS.register("ground_pickerel_weed", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_SOFT_RUSH = ITEMS.register("ground_soft_rush", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_MARSH_MALLOW = ITEMS.register("ground_marsh_mallow", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_MILKWEED = ITEMS.register("ground_milkweed", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_BLUE_IRIS = ITEMS.register("ground_blue_iris", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_COPPER_IRIS = ITEMS.register("ground_copper_iris", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_BLUE_EYED_GRASS = ITEMS.register("ground_blue_eyed_grass", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_BONESET = ITEMS.register("ground_boneset", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_BOTTLE_BRUSH_GRASS = ITEMS.register("ground_bottle_brush_grass", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_WEEDWOOD_BARK = ITEMS.register("ground_weedwood_bark", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_DRIED_SWAMP_REED = ITEMS.register("ground_dried_swamp_reed", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_ALGAE = ITEMS.register("ground_algae", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_ANGLER_TOOTH = ITEMS.register("ground_angler_tooth", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_BLACKHAT_MUSHROOM = ITEMS.register("ground_blackhat_mushroom", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_BLOOD_SNAIL_SHELL = ITEMS.register("ground_blood_snail_shell", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_BOG_BEAN = ITEMS.register("ground_bog_bean", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> GROUND_BROOM_SEDGE = ITEMS.register("ground_broom_sedge", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_BULB_CAPPED_MUSHROOM = ITEMS.register("ground_bulb_capped_mushroom", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_CARDINAL_FLOWER = ITEMS.register("ground_cardinal_flower", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_CAVE_GRASS = ITEMS.register("ground_cave_grass", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_CAVE_MOSS = ITEMS.register("ground_cave_moss", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_CRIMSON_MIDDLE_GEM = ITEMS.register("ground_crimson_middle_gem", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_DEEP_WATER_CORAL = ITEMS.register("ground_deep_water_coral", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_FLATHEAD_MUSHROOM = ITEMS.register("ground_flathead_mushroom", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_GOLDEN_CLUB = ITEMS.register("ground_golden_club", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_GREEN_MIDDLE_GEM = ITEMS.register("ground_ggreen_middle_gem", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_HANGER = ITEMS.register("ground_hanger", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_LICHEN = ITEMS.register("ground_lichen", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_MARSH_MARIGOLD = ITEMS.register("ground_marsh_marigold", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_MIRE_CORAL = ITEMS.register("ground_mire_coral", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_MIRE_SNAIL_SHELL = ITEMS.register("ground_mire_snail_shell", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_MOSS = ITEMS.register("ground_moss", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_NETTLE = ITEMS.register("ground_nettle", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_PHRAGMITES = ITEMS.register("ground_phragmites", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_SLUDGECREEP = ITEMS.register("ground_sludgecreep", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_SUNDEW = ITEMS.register("ground_sundew", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_SWAMP_KELP = ITEMS.register("ground_swamp_kelp", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_ROOTS = ITEMS.register("ground_roots", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_AQUA_MIDDLE_GEM = ITEMS.register("ground_aqua_middle_gem", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_PITCHER_PLANT = ITEMS.register("ground_pitcher_plant", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_WATER_WEEDS = ITEMS.register("ground_water_weeds", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_VENUS_FLY_TRAP = ITEMS.register("ground_venus_fly_trap", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_VOLARPAD = ITEMS.register("ground_volarpad", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> GROUND_THORNS = ITEMS.register("ground_thorns", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> GROUND_POISON_IVY = ITEMS.register("ground_poison_ivy", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_BLADDERWORT_FLOWER = ITEMS.register("ground_bladderwort_flower", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_BLADDERWORT_STALK = ITEMS.register("ground_bladderwort_stalk", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_EDGE_SHROOM = ITEMS.register("ground_edge_shroom", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_EDGE_MOSS = ITEMS.register("ground_edge_moss", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_EDGE_LEAF = ITEMS.register("ground_edge_leaf", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_ROTBULB = ITEMS.register("ground_rotbulb", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_PALE_GRASS = ITEMS.register("ground_pale_grass", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> GROUND_STRING_ROOTS = ITEMS.register("ground_string_roots", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    public static final RegistryObject<Item> GROUND_CRYPTWEED = ITEMS.register("ground_cryptweed", () -> new ItemCrushed(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    //public static final RegistryObject<Item> ITEMS_CRUSHED = new ItemCrushed().setCreativeTab(BLCreativeTabs.HERBLORE);
    
    //Plant Drops
	public static final RegistryObject<Item> GENERIC_LEAF = ITEMS.register("generic_leaf", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> ALGAE = ITEMS.register("algae", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> ARROW_ARUM_LEAF = ITEMS.register("arrow_arum_leaf", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> BLUE_EYED_GRASS_FLOWERS = ITEMS.register("blue_eyed_grass_flowers", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> BLUE_IRIS_PETAL = ITEMS.register("blue_iris_petal", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> MIRE_CORAL = ITEMS.register("mire_coral", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> DEEP_WATER_CORAL = ITEMS.register("deep_water_coral", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> BOG_BEAN_FLOWER = ITEMS.register("bog_bean_flower", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> BONESET_FLOWERS = ITEMS.register("boneset_flowers", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> BOTTLE_BRUSH_GRASS_BLADES = ITEMS.register("bottle_brush_grass_blades", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> BROOM_SEDGE_LEAVES = ITEMS.register("broom_sedge_leaves", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> BUTTON_BUSH_FLOWERS = ITEMS.register("button_bush_flowers", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> CARDINAL_FLOWER_PETALS = ITEMS.register("cardinal_flower_petals", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> CATTAIL_HEAD = ITEMS.register("cattail_head", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> CAVE_GRASS_BLADES = ITEMS.register("cave_grass_blades", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> COPPER_IRIS_PETALS = ITEMS.register("copper_iris_petals", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> GOLDEN_CLUB_FLOWER = ITEMS.register("golden_club_flower", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> LICHEN = ITEMS.register("lichen", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> MARSH_HIBISCUS_FLOWER = ITEMS.register("marsh_hibiscus_flower", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> MARSH_MALLOW_FLOWER = ITEMS.register("marsh_mallow_flower", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> MARSH_MARIGOLD_FLOWER = ITEMS.register("marsh_marigold_flower", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> NETTLE_LEAF = ITEMS.register("nettle_leaf", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> PHRAGMITE_STEMS = ITEMS.register("phragmite_stems", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> PICKEREL_WEED_FLOWER = ITEMS.register("pickerel_weed_flower", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> SHOOT_LEAVES = ITEMS.register("shoot_leaves", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> SLUDGECREEP_LEAVES = ITEMS.register("sludgecreep_leaves", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> SOFT_RUSH_LEAVES = ITEMS.register("soft_rush_leaves", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> SUNDEW_HEAD = ITEMS.register("sundew_head", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> SWAMP_TALL_GRASS_BLADES = ITEMS.register("swamp_tall_grass_blades", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> CAVE_MOSS = ITEMS.register("cave_moss", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> MOSS = ITEMS.register("moss", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> MILKWEED = ITEMS.register("milkweed", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> HANGER = ITEMS.register("hanger", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> PITCHER_PLANT_TRAP = ITEMS.register("pitcher_plant_trap", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> WATER_WEEDS = ITEMS.register("water_weeds", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> VENUS_FLY_TRAP = ITEMS.register("venus_fly_trap", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> VOLARPAD = ITEMS.register("volarpad", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> THORNS = ITEMS.register("thorns", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> POISON_IVY = ITEMS.register("poison_ivy", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> BLADDERWORT_STALK = ITEMS.register("bladderwort_stalk", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> BLADDERWORT_FLOWER = ITEMS.register("bladderwort_flower", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> EDGE_SHROOM_GILLS = ITEMS.register("edge_shroom_gills", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> EDGE_MOSS_CLUMP = ITEMS.register("edge_moss_clump", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> EDGE_LEAF = ITEMS.register("edge_leaf", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> ROTBULB_STALK = ITEMS.register("rotbulb_stalk", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> PALE_GRASS_BLADES = ITEMS.register("pale_grass_blades", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> STRING_ROOT_FIBERS = ITEMS.register("string_foot_fibers", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
	public static final RegistryObject<Item> CRYPTWEED_BLADES = ITEMS.register("cryptweed_blades", () -> new ItemPlantDrop(new Item.Properties().tab(BLCreativeTabs.HERBLORE)));
    //public static final RegistryObject<Item> ITEMS_PLANT_DROP = new ItemPlantDrop().setCreativeTab(BLCreativeTabs.HERBLORE);
    
    
    public static final RegistryObject<Item> WEEDWOOD_ROWBOAT = new ItemWeedwoodRowboat();
    public static final RegistryObject<Item> DENTROTHYST_SHARD_ORANGE = ITEMS.register("orange_dentrothyst_shard", () -> new Item(new Item.Properties().tab(BLCreativeTabs.ITEMS))); // new ItemDentrothystShard(EnumDentrothyst.ORANGE);
    public static final RegistryObject<Item> DENTROTHYST_SHARD_GREEN = ITEMS.register("green_dentrothyst_shard", () -> new Item(new Item.Properties().tab(BLCreativeTabs.ITEMS))); // new ItemDentrothystShard(EnumDentrothyst.GREEN);
    //food
    public static final RegistryObject<Item> SAP_BALL = ITEMS.register("sap_ball", () -> new ItemSapBall(new Item.Properties().tab(BLCreativeTabs.ITEMS))); // new ItemSapBall();
    public static final RegistryObject<Item> ROTTEN_FOOD = ITEMS.register("rotten_food", () -> new BLFoodItem(false, 0, 0, new Item.Properties().food(BLFoods.ROTTEN_FOOD).tab(BLCreativeTabs.ITEMS))); //RottenFood ROTTEN_FOOD = (ItemRottenFood) new ItemRottenFood().setAlwaysEdible();
    public static final RegistryObject<Item> MIDDLE_FRUIT_BUSH_SEEDS = new ItemMiddleFruitBushSeeds();
    public static final RegistryObject<Item> SPORES = new ItemSpores();
    public static final RegistryObject<Item> ASPECTRUS_SEEDS = new ItemAspectrusSeeds();
    public static final RegistryObject<Item> MIRE_SNAIL_EGG = new ItemMireSnailEgg();
    public static final RegistryObject<Item> MIRE_SNAIL_EGG_COOKED = ITEMS.register("cooked_mire_snail_egg", () -> new BLFoodItem(false, 0, 0, new Item.Properties().food(BLFoods.ROTTEN_FOOD).tab(BLCreativeTabs.ITEMS))); // new BLFoods(8, 1, false);
    public static final RegistryObject<Item> ANGLER_MEAT_RAW = ITEMS.register("rotten_food", () -> new BLFoodItem(false, 0, 0, new Item.Properties().food(BLFoods.ANGLER_MEAT_RAW).tab(BLCreativeTabs.ITEMS))); // new BLFoods(4, 0.4F, false);
    public static final RegistryObject<Item> ANGLER_MEAT_COOKED = ITEMS.register("rotten_food", () -> new BLFoodItem(false, 0, 0, new Item.Properties().food(BLFoods.ANGLER_MEAT_COOKED).tab(BLCreativeTabs.ITEMS))); // new BLFoods(8, 0.8F, false);
    public static final RegistryObject<Item> FROG_LEGS_RAW = new BLFoods(3, 0.4F, false);
    public static final RegistryObject<Item> FROG_LEGS_COOKED = new BLFoods(6, 0.8F, false);
    public static final RegistryObject<Item> SNAIL_FLESH_RAW = new BLFoods(3, 0.4F, false);
    public static final RegistryObject<Item> SNAIL_FLESH_COOKED = new BLFoods(6, 0.9F, false);
    public static final RegistryObject<Item> REED_DONUT = new BLFoods(6, 0.6F, false);
    public static final RegistryObject<Item> JAM_DONUT = new BLFoods(10, 0.6F, false);
    public static final RegistryObject<Item> GERTS_DONUT = new ItemGertsDonut();
    public static final RegistryObject<Item> ASPECTRUS_FRUIT = new ItemAspectrusFruit();
    public static final RegistryObject<Item> PUFFSHROOM_TENDRIL = new BLFoods(8, 0.9F, false);
    public static final RegistryObject<Item> KRAKEN_TENTACLE = new BLFoods(8, 0.9F, false);
    public static final RegistryObject<Item> KRAKEN_CALAMARI = new BLFoods(14, 1.2F, false);
    public static final RegistryObject<Item> MIDDLE_FRUIT = new BLFoods(6, 0.6F, false);
    public static final RegistryObject<Item> MINCE_PIE = new BLFoods(8, 1F, false);
    public static final RegistryObject<Item> CHRISTMAS_PUDDING = new BLFoods(6, 0.95F, false);
    public static final RegistryObject<Item> CANDY_CANE = new BLFoods(4, 0.85F, false);
    public static final RegistryObject<Item> WEEPING_BLUE_PETAL = new ItemWeepingBluePetal();
    public static final RegistryObject<Item> WIGHT_HEART = new ItemWightHeart();
    public static final RegistryObject<Item> YELLOW_DOTTED_FUNGUS = new BLFoods(8, 0.6F, false);
    public static final RegistryObject<Item> SILT_CRAB_CLAW = new BLFoods(2, 0.6F, false);
    public static final RegistryObject<Item> CRAB_STICK = new BLFoods(6, 0.9F, false);
    public static final RegistryObject<Item> NETTLE_SOUP = new ItemNettleSoup();
    public static final RegistryObject<Item> SLUDGE_JELLO = new BLFoods(4, 0.9F, false);
    public static final RegistryObject<Item> MIDDLE_FRUIT_JELLO = new BLFoods(10, 1.0F, false);
    public static final RegistryObject<Item> SAP_JELLO = new ItemSapJello();
    public static final RegistryObject<Item> MARSHMALLOW = new ItemMarshmallow();
    public static final RegistryObject<Item> MARSHMALLOW_PINK = new ItemMarshmallowPink();
    public static final RegistryObject<Item> FLAT_HEAD_MUSHROOM_ITEM = new ItemFlatHeadMushroom();
    public static final RegistryObject<Item> BLACK_HAT_MUSHROOM_ITEM = new ItemBlackHatMushroom();
    public static final RegistryObject<Item> BULB_CAPPED_MUSHROOM_ITEM = new ItemBulbCappedMushroom();
    public static final RegistryObject<Item> SWAMP_REED_ITEM = new ItemSwampReed();
    public static final RegistryObject<Item> SWAMP_KELP_ITEM = new ItemSwampKelp();
    public static final RegistryObject<Item> FRIED_SWAMP_KELP = new BLFoods(5, 0.6F, false);
    public static final RegistryObject<Item> FORBIDDEN_FIG = new ItemForbiddenFig();
    public static final RegistryObject<Item> CANDY_BLUE = new BLFoods(4, 1.0F, false);
    public static final RegistryObject<Item> CANDY_RED = new BLFoods(4, 1.0F, false);
    public static final RegistryObject<Item> CANDY_YELLOW = new BLFoods(4, 1.0F, false);
    public static final RegistryObject<Item> CHIROMAW_WING = new ItemChiromawWing();
    public static final RegistryObject<Item> TANGLED_ROOT = new ItemTangledRoot();
    public static final RegistryObject<Item> MIRE_SCRAMBLE = new ItemMireScramble();
    public static final RegistryObject<Item> WEEPING_BLUE_PETAL_SALAD = new ItemWeepingBluePetalSalad();
    public static final RegistryObject<Item> NIBBLESTICK = new ItemNibblestick();
    public static final RegistryObject<Item> SPIRIT_FRUIT = ITEMS.register("spirit_fruit", () -> new BLFoodItem(false, 0, 0, new Item.Properties().food(BLFoods.SPIRIT_FRUIT).rarity(Rarity.RARE).tab(BLCreativeTabs.ITEMS))); // new ItemSpiritFruit();
    public static final RegistryObject<Item> SUSHI = new BLFoods(5, 1.0F, false);
    
    //armor
    public static final RegistryObject<Item> BONE_HELMET = ITEMS.register("bone_helmet", () -> new ItemBoneArmor(EquipmentSlotType.HEAD, new Item.Properties().tab(BLCreativeTabs.GEARS))); // new ItemBoneArmor(EquipmentSlotType.HEAD);
    public static final RegistryObject<Item> BONE_CHESTPLATE = ITEMS.register("bone_chestplate", () -> new ItemBoneArmor(EquipmentSlotType.CHEST, new Item.Properties().tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> BONE_LEGGINGS = ITEMS.register("bone_leggings", () -> new ItemBoneArmor(EquipmentSlotType.LEGS, new Item.Properties().tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> BONE_BOOTS = ITEMS.register("bone_boots", () -> new ItemBoneArmor(EquipmentSlotType.FEET, new Item.Properties().tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> LURKER_SKIN_HELMET = ITEMS.register("lurker_skin_helmet", () -> new ItemLurkerSkinArmor(EquipmentSlotType.HEAD, new Item.Properties().tab(BLCreativeTabs.GEARS))); // new ItemLurkerSkinArmor(EquipmentSlotType.HEAD);
    public static final RegistryObject<Item> LURKER_SKIN_CHESTPLATE = ITEMS.register("lurker_skin_chestplate", () -> new ItemLurkerSkinArmor(EquipmentSlotType.CHEST, new Item.Properties().tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> LURKER_SKIN_LEGGINGS = ITEMS.register("lurker_skin_leggings", () -> new ItemLurkerSkinArmor(EquipmentSlotType.LEGS, new Item.Properties().tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> LURKER_SKIN_BOOTS = ITEMS.register("lurker_skin_feet", () -> new ItemLurkerSkinArmor(EquipmentSlotType.FEET, new Item.Properties().tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> SYRMORITE_HELMET = ITEMS.register("syrmorite_helmet", () -> new ItemSyrmoriteArmor(EquipmentSlotType.HEAD, new Item.Properties().tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> SYRMORITE_CHESTPLATE = ITEMS.register("syrmorite_chestplate", () -> new ItemSyrmoriteArmor(EquipmentSlotType.CHEST, new Item.Properties().tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> SYRMORITE_LEGGINGS = ITEMS.register("syrmorite_leggings", () -> new ItemSyrmoriteArmor(EquipmentSlotType.LEGS, new Item.Properties().tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> SYRMORITE_BOOTS = ITEMS.register("syrmorite_boots", () -> new ItemSyrmoriteArmor(EquipmentSlotType.FEET, new Item.Properties().tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> VALONITE_HELMET = ITEMS.register("valonite_helmet", () -> new ItemValoniteArmor(EquipmentSlotType.HEAD, new Item.Properties().tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> VALONITE_CHESTPLATE = ITEMS.register("valonite_chestplate", () -> new ItemValoniteArmor(EquipmentSlotType.CHEST, new Item.Properties().tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> VALONITE_LEGGINGS = ITEMS.register("valonite_leggings", () -> new ItemValoniteArmor(EquipmentSlotType.LEGS, new Item.Properties().tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> VALONITE_BOOTS = ITEMS.register("valonite_boots", () -> new ItemValoniteArmor(EquipmentSlotType.FEET, new Item.Properties().tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> ANCIENT_HELMET = ITEMS.register("ancient_helmet", () -> new ItemAncientArmor(EquipmentSlotType.HEAD, new Item.Properties().rarity(Rarity.EPIC).tab(BLCreativeTabs.GEARS))); // new ItemAncientArmor(EquipmentSlotType.HEAD);
    public static final RegistryObject<Item> ANCIENT_CHESTPLATE = ITEMS.register("ancient_chestplate", () -> new ItemAncientArmor(EquipmentSlotType.CHEST, new Item.Properties().rarity(Rarity.EPIC).tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> ANCIENT_LEGGINGS = ITEMS.register("ancient_leggings", () -> new ItemAncientArmor(EquipmentSlotType.LEGS, new Item.Properties().rarity(Rarity.EPIC).tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> ANCIENT_BOOTS = ITEMS.register("ancient_boots", () -> new ItemAncientArmor(EquipmentSlotType.FEET, new Item.Properties().rarity(Rarity.EPIC).tab(BLCreativeTabs.GEARS)));
    public static final RegistryObject<Item> RUBBER_BOOTS = ITEMS.register("rubber_boots", () -> new RubberBootsItem(new Item.Properties().tab(BLCreativeTabs.GEARS))); // new ItemRubberBoots();
    public static final RegistryObject<Item> MARSH_RUNNER_BOOTS = ITEMS.register("marsh_runner_boots", () -> new MarshRunnerBootsItem(new Item.Properties().tab(BLCreativeTabs.GEARS))); // new MarshRunnerBootsItem();
    public static final RegistryObject<Item> SKULL_MASK = ITEMS.register("skull_mask", () -> new SkullMaskItem(new Item.Properties().tab(BLCreativeTabs.SPECIALS))); // new ItemSkullMask();
    public static final RegistryObject<Item> EXPLORERS_HAT = ITEMS.register("bone_helmet", () -> new ItemExplorersHat(new Item.Properties().rarity(Rarity.RARE).tab(BLCreativeTabs.SPECIALS))); // new ItemExplorersHat();
    public static final RegistryObject<Item> SPIRIT_TREE_FACE_LARGE_MASK = new SpiritTreeFaceMaskLargeItem();
    public static final RegistryObject<Item> SPIRIT_TREE_FACE_SMALL_MASK = new SpiritTreeFaceMaskSmallItem();
    public static final RegistryObject<Item> SPIRIT_TREE_FACE_SMALL_MASK_ANIMATED = new ItemSpiritTreeFaceMaskSmallAnimated();
    public static final RegistryObject<Item> GALLERY_FRAME_SMALL = new ItemGalleryFrame(EntityGalleryFrame.Type.SMALL);
    public static final RegistryObject<Item> GALLERY_FRAME_LARGE = new ItemGalleryFrame(EntityGalleryFrame.Type.LARGE);
    public static final RegistryObject<Item> GALLERY_FRAME_VERY_LARGE = new ItemGalleryFrame(EntityGalleryFrame.Type.VERY_LARGE);
    //TOOLS
    public static final RegistryObject<Item> WEEDWOOD_SWORD = new BLSwordItem(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> WEEDWOOD_SHOVEL = new BLShovelItem(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> WEEDWOOD_AXE = new BLAxeItem(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> WEEDWOOD_PICKAXE = new BLPickaxeItem(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> WEEDWOOD_HOE = new BLHoeItem(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> BONE_SWORD = new BLSwordItem(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> BONE_SHOVEL = new BLShovelItem(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> BONE_AXE = new BLAxeItem(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> BONE_PICKAXE = new BLPickaxeItem(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> OCTINE_SWORD = new ItemOctineSword();
    public static final RegistryObject<Item> OCTINE_SHOVEL = new ItemOctineShovel();
    public static final RegistryObject<Item> OCTINE_AXE = new ItemOctineAxe();
    public static final RegistryObject<Item> OCTINE_PICKAXE = new ItemOctinePickaxe();
    public static final RegistryObject<Item> VALONITE_SWORD = new BLSwordItem(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> VALONITE_SHOVEL = new BLShovelItem(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> VALONITE_AXE = new BLAxeItem(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> VALONITE_GREATAXE = new ItemGreataxe(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> VALONITE_PICKAXE = new BLPickaxeItem(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> OCTINE_SHIELD = new ItemOctineShield();
    public static final RegistryObject<Item> VALONITE_SHIELD = new ItemValoniteShield();
    public static final RegistryObject<Item> WEEDWOOD_SHIELD = new ItemWeedwoodShield();
    public static final RegistryObject<Item> LIVING_WEEDWOOD_SHIELD = new ItemLivingWeedwoodShield();
    public static final RegistryObject<Item> SYRMORITE_SHIELD = new ItemSyrmoriteShield();
    public static final RegistryObject<Item> BONE_SHIELD = new ItemBLShield(BLMaterialRegistry.TOOL_BONE);
    public static final RegistryObject<Item> DENTROTHYST_SHIELD_GREEN = new ItemDentrothystShield(true);
    public static final RegistryObject<Item> DENTROTHYST_SHIELD_GREEN_POLISHED = new ItemDentrothystShield(true);
    public static final RegistryObject<Item> DENTROTHYST_SHIELD_ORANGE = new ItemDentrothystShield(false);
    public static final RegistryObject<Item> DENTROTHYST_SHIELD_ORANGE_POLISHED = new ItemDentrothystShield(false);
    public static final RegistryObject<Item> LURKER_SKIN_SHIELD = new ItemLurkerSkinShield();
    public static final RegistryObject<Item> MANUAL_HL = new ItemManualHL();
    public static final RegistryObject<Item> SYRMORITE_SHEARS = new ItemSyrmoriteShears();
    public static final RegistryObject<Item> SICKLE = new ItemSickle();
    public static final RegistryObject<Item> SHOCKWAVE_SWORD = new ItemShockwaveSword(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> ANGLER_TOOTH_ARROW = new ItemBLArrow(EnumArrowType.DEFAULT).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> POISONED_ANGLER_TOOTH_ARROW = new ItemBLArrow(EnumArrowType.ANGLER_POISON).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> OCTINE_ARROW = new ItemBLArrow(EnumArrowType.OCTINE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> BASILISK_ARROW = new ItemBLArrow(EnumArrowType.BASILISK).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> SLUDGE_WORM_ARROW = new ItemBLArrow(EnumArrowType.WORM).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> SHOCK_ARROW = new ItemBLArrow(EnumArrowType.SHOCK).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> CHIROMAW_BARB = new ItemBLArrow(EnumArrowType.CHIROMAW_BARB).setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> WEEDWOOD_BOW = new ItemBLBow().setCreativeTab(BLCreativeTabs.GEARS);
    public static final RegistryObject<Item> PREDATOR_BOW = new ItemPredatorBow();
    public static final RegistryObject<Item> WIGHTS_BANE = new ItemLootSword(BLMaterialRegistry.TOOL_WEEDWOOD).addInstantKills(EntityWight.class).setMaxDamage(32);
    public static final RegistryObject<Item> SLUDGE_SLICER = new ItemLootSword(BLMaterialRegistry.TOOL_WEEDWOOD).addInstantKills(EntitySludge.class, EntitySmollSludge.class).setMaxDamage(32);
    public static final RegistryObject<Item> CRITTER_CRUNCHER = new ItemLootSword(BLMaterialRegistry.TOOL_WEEDWOOD).addInstantKills(EntityBloodSnail.class, EntityDragonFly.class, EntityFirefly.class, EntityLeech.class, EntityMireSnail.class, EntitySporeling.class, EntityTermite.class, EntityChiromaw.class, EntitySwarm.class).setMaxDamage(32);
    public static final RegistryObject<Item> HAG_HACKER = new ItemHagHacker();
    public static final RegistryObject<Item> VOODOO_DOLL = new ItemVoodooDoll();
    public static final RegistryObject<Item> SWIFT_PICK = new ItemSwiftPick();
    public static final RegistryObject<Item> ANCIENT_GREATSWORD = new ItemAncientGreatsword();
    public static final RegistryObject<Item> ANCIENT_BATTLE_AXE = new ItemAncientBattleAxe();
    public static final RegistryObject<Item> PESTLE = new ItemPestle();
    public static final RegistryObject<Item> NET = new ItemNet();
    public static final RegistryObject<Item> LURKER_SKIN_POUCH = new ItemLurkerSkinPouch();
    public static final RegistryObject<Item> CAVING_ROPE = new ItemCavingRope();
    public static final RegistryObject<Item> GRAPPLING_HOOK = new ItemGrapplingHook();
    public static final RegistryObject<Item> VOLARKITE = new ItemVolarkite();
    public static final RegistryObject<Item> SIMPLE_SLINGSHOT = new ItemSimpleSlingshot();
    public static final RegistryObject<Item> CHIROBARB_ERUPTER = new ItemChirobarbErupter(false);
    public static final RegistryObject<Item> CHIROBARB_SHOCK_ERUPTER = new ItemChirobarbErupter(true);
    //BUCKETS
    public static final RegistryObject<Item> BL_BUCKET = new ItemBLBucket();
    public static final RegistryObject<Item> BL_BUCKET_RUBBER = new ItemSpecificBucket(FluidRegistry.RUBBER);
    public static final RegistryObject<Item> BL_BUCKET_INFUSION = new ItemBucketInfusion();
    public static final RegistryObject<Item> BL_BUCKET_PLANT_TONIC = new ItemPlantTonic();
    public static final RegistryObject<Item> SYRMORITE_BUCKET_SOLID_RUBBER = new ItemSyrmoriteBucketSolidRubber();
    //RECORDS
    public static final RegistryObject<Item> ASTATOS = new ItemBLRecord(SoundRegistry.ASTATOS);
    public static final RegistryObject<Item> BETWEEN_YOU_AND_ME = new ItemBLRecord(SoundRegistry.BETWEEN_YOU_AND_ME);
    public static final RegistryObject<Item> CHRISTMAS_ON_THE_MARSH = new ItemBLRecord(SoundRegistry.CHRISTMAS_ON_THE_MARSH);
    public static final RegistryObject<Item> THE_EXPLORER = new ItemBLRecord(SoundRegistry.THE_EXPLORER);
    public static final RegistryObject<Item> HAG_DANCE = new ItemBLRecord(SoundRegistry.HAG_DANCE);
    public static final RegistryObject<Item> LONELY_FIRE = new ItemBLRecord(SoundRegistry.LONELY_FIRE);
    public static final RegistryObject<Item> MYSTERIOUS_RECORD = new ItemBLRecord(SoundRegistry._16612);
    public static final RegistryObject<Item> ANCIENT = new ItemBLRecord(SoundRegistry.ACIENT);
    public static final RegistryObject<Item> BENEATH_A_GREEN_SKY = new ItemBLRecord(SoundRegistry.BENEATH_A_GREEN_SKY);
    public static final RegistryObject<Item> DJ_WIGHTS_MIXTAPE = new ItemBLRecord(SoundRegistry.DJ_WIGHTS_MIXTAPE);
    public static final RegistryObject<Item> ONWARDS = new ItemBLRecord(SoundRegistry.ONWARD);
    public static final RegistryObject<Item> STUCK_IN_THE_MUD = new ItemBLRecord(SoundRegistry.STUCK_IN_THE_MUD);
    public static final RegistryObject<Item> WANDERING_WISPS = new ItemBLRecord(SoundRegistry.WANDERING_WISPS);
    public static final RegistryObject<Item> WATERLOGGED = new ItemBLRecord(SoundRegistry.WATERLOGGED);
    //MISC 
    //TODO: Move those into new Blocks section of items.

    public static final RegistryObject<Item> WEEDWOOD_SIGN_ITEM = new ItemWeedwoodSign();
    public static final RegistryObject<Item> CRIMSON_MIDDLE_GEM = new ItemGem(CircleGemType.CRIMSON);
    public static final RegistryObject<Item> AQUA_MIDDLE_GEM = new ItemGem(CircleGemType.AQUA);
    public static final RegistryObject<Item> GREEN_MIDDLE_GEM = new ItemGem(CircleGemType.GREEN);
    public static final RegistryObject<Item> LIFE_CRYSTAL = new ItemLifeCrystal(128, true);
    public static final RegistryObject<Item> LIFE_CRYSTAL_FRAGMENT = new ItemLifeCrystal(64, false);
    public static final RegistryObject<Item> TEST_ITEM = new TestItem();
    public static final RegistryObject<Item> TEST_ITEM_CHIMP = new TestItemChimp();
    public static final RegistryObject<Item> TEST_ITEM_CHIMP_RULER = new TestItemChimpRuler();
    public static final RegistryObject<Item> LOCATION_DEBUG = new LocationDebugItem().setCreativeTab(null);
    public static final RegistryObject<Item> PYRAD_FLAME = new ItemPyradFlame();
    public static final RegistryObject<Item> CRITTER = new ItemCritters();
    public static final RegistryObject<Item> SLUDGE_WORM_EGG_SAC = new ItemMob(16, EntityTinyWormEggSac.class, null);
    public static final RegistryObject<Item> CHIROMAW_EGG = new ItemChiromawEgg(false);
    public static final RegistryObject<Item> CHIROMAW_EGG_LIGHTNING = new ItemChiromawEgg(true);
    public static final RegistryObject<Item> CHIROMAW_TAME = new ItemChiromawTame(false);
    public static final RegistryObject<Item> CHIROMAW_TAME_LIGHTNING = new ItemChiromawTame(true);
    public static final RegistryObject<Item> SHIMMER_STONE = new ItemShimmerStone();
    public static final RegistryObject<Item> TARMINION = new ItemTarminion();
    public static final RegistryObject<Item> MOSS_BED_ITEM = new ItemMossBed();
    public static final RegistryObject<Item> SLUDGE_BALL = new Item().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final RegistryObject<Item> ELIXIR = new ItemElixir();
    public static final RegistryObject<Item> DENTROTHYST_VIAL = new ItemDentrothystVial();
    public static final RegistryObject<Item> DENTROTHYST_FLUID_VIAL = new ItemDentrothystFluidVial();
    public static final RegistryObject<Item> ASPECT_VIAL = new ItemAspectVial();
    public static final RegistryObject<Item> GLUE = new ItemGlue();
    public static final RegistryObject<Item> AMULET = new ItemAmulet();
    public static final RegistryObject<Item> AMULET_SLOT = new ItemAmuletSlot();
    public static final RegistryObject<Item> ROPE_ITEM = new ItemRope();
    public static final RegistryObject<Item> RING_OF_POWER = new ItemRingOfPower();
    public static final RegistryObject<Item> RING_OF_FLIGHT = new ItemRingOfFlight();
    public static final RegistryObject<Item> RING_OF_RECRUITMENT = new ItemRingOfRecruitment();
    public static final RegistryObject<Item> RING_OF_SUMMONING = new ItemRingOfSummoning();
    public static final RegistryObject<Item> RING_OF_DISPERSION = new ItemRingOfDispersion();
    public static final RegistryObject<Item> RING_OF_GATHERING = new ItemRingOfGathering();
    public static final RegistryObject<Item> ANGRY_PEBBLE = new ItemAngryPebble();
    public static final RegistryObject<Item> LORE_SCRAP = new ItemLoreScrap();
    public static final RegistryObject<Item> TAINTED_POTION = new ItemTaintedPotion();
    public static final RegistryObject<Item> OCTINE_INGOT = new ItemOctineIngot();
    public static final RegistryObject<Item> MUMMY_BAIT = new ItemMummyBait();
    public static final RegistryObject<Item> SAP_SPIT = ITEMS.register("sap_spit", () -> new ItemMisc(new Item.Properties().tab(BLCreativeTabs.ITEMS)));
    public static final RegistryObject<Item> BARK_AMULET = new ItemBarkAmulet();
    public static final RegistryObject<Item> EMPTY_AMATE_MAP = new ItemEmptyAmateMap();
    public static final RegistryObject<Item> AMATE_MAP = new ItemAmateMap();
    public static final RegistryObject<Item> BONE_WAYFINDER = new ItemBoneWayfinder();
    public static final RegistryObject<Item> MAGIC_ITEM_MAGNET = new ItemMagicItemMagnet();
    public static final RegistryObject<Item> GEM_SINGER = new ItemGemSinger();
    public static final RegistryObject<Item> SHAMBLER_TONGUE = new Item().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final RegistryObject<Item> RUNE_DOOR_KEY = new ItemRuneDoorKey();
    public static final RegistryObject<Item> LURKER_SKIN_PATCH = new ItemLurkerSkinPatch();
    public static final RegistryObject<Item> DRAETON_BALLOON = new Item().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final RegistryObject<Item> DRAETON_BURNER = new Item().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final RegistryObject<Item> DRAETON = new ItemDraeton();
    public static final RegistryObject<Item> DRAETON_UPGRADE_FURNACE = new Item().setCreativeTab(BLCreativeTabs.ITEMS).setMaxStackSize(1);
    public static final RegistryObject<Item> DRAETON_UPGRADE_ANCHOR = new Item().setCreativeTab(BLCreativeTabs.ITEMS).setMaxStackSize(1);
    public static final RegistryObject<Item> DRAETON_UPGRADE_CRAFTING = new Item().setCreativeTab(BLCreativeTabs.ITEMS).setMaxStackSize(1);
    public static final RegistryObject<Item> WEEDWOOD_ROWBOAT_UPGRADE_LANTERN = new Item().setCreativeTab(BLCreativeTabs.ITEMS).setMaxStackSize(1);
    
    public static final RegistryObject<Item> RUNE_CHAIN = new ItemRuneChain();
    public static final RegistryObject<Item> SCRIVENER_TOOL = new ItemScrivenerTool();
    
    public static final RegistryObject<Item> WEEDWOOD_RUNE = new ItemRune(new ResourceLocation(ModInfo.ID, "weedwood"));
    public static final RegistryObject<Item> PITSTONE_RUNE = new ItemRune(new ResourceLocation(ModInfo.ID, "pitstone"));
    public static final RegistryObject<Item> DENTROTHYST_GREEN_RUNE = new ItemRune(new ResourceLocation(ModInfo.ID, "dentrothyst_green"));
    public static final RegistryObject<Item> DENTROTHYST_ORANGE_RUNE = new ItemRune(new ResourceLocation(ModInfo.ID, "dentrothyst_orange"));
    public static final RegistryObject<Item> BONE_RUNE = new ItemRune(new ResourceLocation(ModInfo.ID, "bone"));
    public static final RegistryObject<Item> ANCIENT_RUNE = new ItemRune(new ResourceLocation(ModInfo.ID, "ancient"));
    
    public static final RegistryObject<Item> WEEDWOOD_RUNELET = new ItemRunelet(() -> WEEDWOOD_RUNE);
    public static final RegistryObject<Item> PITSTONE_RUNELET = new ItemRunelet(() -> PITSTONE_RUNE);
    public static final RegistryObject<Item> DENTROTHYST_GREEN_RUNELET = new ItemRunelet(() -> DENTROTHYST_GREEN_RUNE);
    public static final RegistryObject<Item> DENTROTHYST_ORANGE_RUNELET = new ItemRunelet(() -> DENTROTHYST_ORANGE_RUNE);
    public static final RegistryObject<Item> BONE_RUNELET = new ItemRunelet(() -> BONE_RUNE);
    public static final RegistryObject<Item> ANCIENT_RUNELET = new ItemRunelet(() -> ANCIENT_RUNE);
    
    private static final List<ItemStack> ORES = new ArrayList<ItemStack>();
    private static final List<ItemStack> INGOTS = new ArrayList<ItemStack>();
    private static final List<ItemStack> NUGGETS = new ArrayList<ItemStack>();
    

    private ItemRegistry() {

    }

    public static void preInit() {
        try {
            for (Field field : ItemRegistry.class.getDeclaredFields()) {
                if (field.get(null) instanceof Item) {
                    Item item = (Item) field.get(null);
                    registerItem(item, field.getName());

                    if (BetweenlandsConfig.DEBUG.debug && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                        if (item.getCreativeTab() == null)
                        	TheBetweenlands.logger.warn(String.format("Item %s doesn't have a creative tab", item.getTranslationKey()));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void registerItem(Item item, String fieldName) {
        ITEMS.add(item);
        String name = fieldName.toLowerCase(Locale.ENGLISH);
        item.setRegistryName(ModInfo.ID, name).setTranslationKey(ModInfo.ID + "." + name);
    }

    private static void registerItemTypes() {
        ORES.add(new ItemStack(Item.getItemFromBlock(BlockRegistry.OCTINE_ORE)));
        ORES.add(new ItemStack(Item.getItemFromBlock(BlockRegistry.SYRMORITE_ORE)));
        ORES.add(new ItemStack(Item.getItemFromBlock(BlockRegistry.SULFUR_ORE)));
        ORES.add(new ItemStack(Item.getItemFromBlock(BlockRegistry.VALONITE_ORE)));
        //ORES.add(new ItemStack(Item.getItemFromBlock(BlockRegistry.LIFE_CRYSTAL_STALACTITE)));

        INGOTS.add(new ItemStack(ItemRegistry.OCTINE_INGOT));
        INGOTS.add(new ItemStack(ITEMS_MISC, 1, EnumItemMisc.SYRMORITE_INGOT.getID()));
        INGOTS.add(new ItemStack(ITEMS_MISC, 1, EnumItemMisc.SULFUR.getID()));
        INGOTS.add(new ItemStack(ITEMS_MISC, 1, EnumItemMisc.VALONITE_SHARD.getID()));
        //INGOTS.add(new ItemStack(LIFE_CRYSTAL));

        NUGGETS.add(new ItemStack(ITEMS_MISC, 1, EnumItemMisc.SYRMORITE_NUGGET.getID()));
        NUGGETS.add(new ItemStack(ITEMS_MISC, 1, EnumItemMisc.OCTINE_NUGGET.getID()));
        NUGGETS.add(new ItemStack(ITEMS_MISC, 1, EnumItemMisc.VALONITE_SPLINTER.getID()));
    }
    
    //TODO: Replace this with block/item tags.
    /*private static void registerOreDictionary() {
		OreDictionary.registerOre("oreSulfur", new ItemStack(BlockRegistry.SULFUR_ORE));
		OreDictionary.registerOre("oreSyrmorite", new ItemStack(BlockRegistry.SYRMORITE_ORE));
		OreDictionary.registerOre("oreBone", new ItemStack(BlockRegistry.SLIMY_BONE_ORE));
		OreDictionary.registerOre("oreOctine", new ItemStack(BlockRegistry.OCTINE_ORE));
		OreDictionary.registerOre("oreValonite", new ItemStack(BlockRegistry.VALONITE_ORE));
		OreDictionary.registerOre("oreAquaMiddleGem", new ItemStack(BlockRegistry.AQUA_MIDDLE_GEM_ORE));
		OreDictionary.registerOre("oreGreenMiddleGem", new ItemStack(BlockRegistry.GREEN_MIDDLE_GEM_ORE));
		OreDictionary.registerOre("oreCrimsonMiddleGem", new ItemStack(BlockRegistry.CRIMSON_MIDDLE_GEM_ORE));
		OreDictionary.registerOre("oreLifeCrystal", new ItemStack(BlockRegistry.LIFE_CRYSTAL_STALACTITE, 1, BlockLifeCrystalStalactite.EnumLifeCrystalType.ORE.getMetadata()));
		OreDictionary.registerOre("oreScabyst", new ItemStack(BlockRegistry.SCABYST_ORE));

		OreDictionary.registerOre("nuggetSyrmorite", new ItemStack(ITEMS_MISC, 1, EnumItemMisc.SYRMORITE_NUGGET.getID()));
		OreDictionary.registerOre("nuggetOctine", new ItemStack(ITEMS_MISC, 1, EnumItemMisc.OCTINE_NUGGET.getID()));
		OreDictionary.registerOre("nuggetValonite", new ItemStack(ITEMS_MISC, 1, EnumItemMisc.VALONITE_SPLINTER.getID()));

		OreDictionary.registerOre("blockSulfur", new ItemStack(BlockRegistry.SULFUR_BLOCK));
		OreDictionary.registerOre("blockSyrmorite", new ItemStack(BlockRegistry.SYRMORITE_BLOCK));
		OreDictionary.registerOre("blockBone", new ItemStack(BlockRegistry.SLIMY_BONE_BLOCK));
		OreDictionary.registerOre("blockOctine", new ItemStack(BlockRegistry.OCTINE_BLOCK));
		OreDictionary.registerOre("blockValonite", new ItemStack(BlockRegistry.VALONITE_BLOCK));
		OreDictionary.registerOre("blockAquaMiddleGem", new ItemStack(BlockRegistry.AQUA_MIDDLE_GEM_BLOCK));
		OreDictionary.registerOre("blockGreenMiddleGem", new ItemStack(BlockRegistry.GREEN_MIDDLE_GEM_BLOCK));
		OreDictionary.registerOre("blockCrimsonMiddleGem", new ItemStack(BlockRegistry.CRIMSON_MIDDLE_GEM_BLOCK));

		OreDictionary.registerOre("blockGlass", new ItemStack(BlockRegistry.SILT_GLASS));
		OreDictionary.registerOre("blockGlassColorless", new ItemStack(BlockRegistry.SILT_GLASS));
		OreDictionary.registerOre("paneGlass", new ItemStack(BlockRegistry.SILT_GLASS_PANE));
		OreDictionary.registerOre("paneGlassColorless", new ItemStack(BlockRegistry.SILT_GLASS_PANE));

		OreDictionary.registerOre("dirt", new ItemStack(BlockRegistry.SWAMP_DIRT));
		OreDictionary.registerOre("dirt", new ItemStack(BlockRegistry.COARSE_SWAMP_DIRT));

		OreDictionary.registerOre("grass", new ItemStack(BlockRegistry.SWAMP_GRASS));

		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_WEEDWOOD_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_SAP_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_RUBBER_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_HEARTHGROVE_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_NIBBLETWIG_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_SPIRIT_TREE_TOP));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_SPIRIT_TREE_MIDDLE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_SPIRIT_TREE_BOTTOM));
		
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_WEEDWOOD));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_SAP));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_RUBBER));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_HEARTHGROVE));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_NIBBLETWIG));

		OreDictionary.registerOre("foodMushroom", new ItemStack(ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM));
		OreDictionary.registerOre("foodMushroom", new ItemStack(ItemRegistry.BLACK_HAT_MUSHROOM_ITEM));
		OreDictionary.registerOre("foodMushroom", new ItemStack(ItemRegistry.FLAT_HEAD_MUSHROOM_ITEM));

		OreDictionary.registerOre("ingotSyrmorite", EnumItemMisc.SYRMORITE_INGOT.create(1));
		OreDictionary.registerOre("ingotOctine", new ItemStack(ItemRegistry.OCTINE_INGOT));

		OreDictionary.registerOre("gemValonite", EnumItemMisc.VALONITE_SHARD.create(1));
		OreDictionary.registerOre("gemAquaMiddleGem", new ItemStack(ItemRegistry.AQUA_MIDDLE_GEM));
		OreDictionary.registerOre("gemCrimsonMiddleGem", new ItemStack(ItemRegistry.CRIMSON_MIDDLE_GEM));
		OreDictionary.registerOre("gemGreenMiddleGem", new ItemStack(ItemRegistry.GREEN_MIDDLE_GEM));
		OreDictionary.registerOre("gemLifeCrystal", new ItemStack(ItemRegistry.LIFE_CRYSTAL, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("gemLifeCrystal", new ItemStack(ItemRegistry.LIFE_CRYSTAL_FRAGMENT, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("gemScabyst", EnumItemMisc.SCABYST.create(1));

		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.WEEDWOOD, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_WEEDWOOD, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_SAP, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_RUBBER, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.GIANT_ROOT, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_HEARTHGROVE, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_NIBBLETWIG, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_SPIRIT_TREE, 1, OreDictionary.WILDCARD_VALUE));

		OreDictionary.registerOre("stickWood", EnumItemMisc.WEEDWOOD_STICK.create(1));
		
		OreDictionary.registerOre("plankWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
		OreDictionary.registerOre("plankWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANKS));
		OreDictionary.registerOre("plankWood", new ItemStack(BlockRegistry.GIANT_ROOT_PLANKS));
		OreDictionary.registerOre("plankWood", new ItemStack(BlockRegistry.HEARTHGROVE_PLANKS));
		OreDictionary.registerOre("plankWood", new ItemStack(BlockRegistry.NIBBLETWIG_PLANKS));
		
		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANK_SLAB));
		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_SLAB));
		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.GIANT_ROOT_PLANK_SLAB));
		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.HEARTHGROVE_PLANK_SLAB));
		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.NIBBLETWIG_PLANK_SLAB));
		
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANK_FENCE));
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.WEEDWOOD_LOG_FENCE));
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_FENCE));
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.GIANT_ROOT_PLANK_FENCE));
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.HEARTHGROVE_PLANK_FENCE));
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.NIBBLETWIG_PLANK_FENCE));
        OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.ROTTEN_PLANK_FENCE));

        OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANK_FENCE_GATE));
		OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.WEEDWOOD_LOG_FENCE_GATE));
		OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_FENCE_GATE));
		OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.GIANT_ROOT_PLANK_FENCE_GATE));
		OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.HEARTHGROVE_PLANK_FENCE_GATE));
		OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.NIBBLETWIG_PLANK_FENCE_GATE));


		OreDictionary.registerOre("stairWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANK_STAIRS));
		OreDictionary.registerOre("stairWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_STAIRS));
		OreDictionary.registerOre("stairWood", new ItemStack(BlockRegistry.GIANT_ROOT_PLANK_STAIRS));
		OreDictionary.registerOre("stairWood", new ItemStack(BlockRegistry.HEARTHGROVE_PLANK_STAIRS));
		OreDictionary.registerOre("stairWood", new ItemStack(BlockRegistry.NIBBLETWIG_PLANK_STAIRS));
        OreDictionary.registerOre("stairWood", new ItemStack(BlockRegistry.ROTTEN_PLANK_STAIRS));

        OreDictionary.registerOre("torch", new ItemStack(BlockRegistry.SULFUR_TORCH));

		OreDictionary.registerOre("bone", EnumItemMisc.SLIMY_BONE.create(1));

		OreDictionary.registerOre("cobblestone", new ItemStack(BlockRegistry.BETWEENSTONE));
		OreDictionary.registerOre("stone", new ItemStack(BlockRegistry.SMOOTH_BETWEENSTONE));

		OreDictionary.registerOre("sand", new ItemStack(BlockRegistry.SILT));

		OreDictionary.registerOre("workbench", new ItemStack(BlockRegistry.WEEDWOOD_WORKBENCH));

		OreDictionary.registerOre("chest", new ItemStack(BlockRegistry.WEEDWOOD_CHEST));
		OreDictionary.registerOre("chestWood", new ItemStack(BlockRegistry.WEEDWOOD_CHEST));

		OreDictionary.registerOre("vine", new ItemStack(BlockRegistry.POISON_IVY));
		OreDictionary.registerOre("vine", new ItemStack(BlockRegistry.THORNS));

		OreDictionary.registerOre("sugarcane", new ItemStack(ItemRegistry.SWAMP_REED_ITEM));

		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.ASTATOS));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.BETWEEN_YOU_AND_ME));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.CHRISTMAS_ON_THE_MARSH));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.THE_EXPLORER));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.HAG_DANCE));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.LONELY_FIRE));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.MYSTERIOUS_RECORD));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.ANCIENT));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.BENEATH_A_GREEN_SKY));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.DJ_WIGHTS_MIXTAPE));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.ONWARDS));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.STUCK_IN_THE_MUD));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.WANDERING_WISPS));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.WATERLOGGED));
	}*/

    private static boolean containsItem(List<ItemStack> lst, ItemStack stack) {
        for (ItemStack s : lst) {
            if (s.getItem() == stack.getItem() && s.getItemDamage() == stack.getItemDamage())
                return true;
        }
        return false;
    }

    public static boolean isIngotFromOre(ItemStack input, ItemStack output) {
        if (input.isEmpty() || output.isEmpty()) return false;
        return isOre(input) && isIngot(output);
    }

    public static boolean isOre(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return containsItem(ORES, stack);
    }

    public static boolean isIngot(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return containsItem(INGOTS, stack);
    }
 
    public static boolean isNugget(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return containsItem(NUGGETS, stack);
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        for (Item item : BlockRegistry.ITEM_BLOCKS) {
            registry.register(item);
        }
        for (Item item : ITEMS) {
            registry.register(item);
        }
        registerItemTypes();
        registerOreDictionary();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (Item item : ITEMS) {
            TheBetweenlands.proxy.registerDefaultItemRenderer(item);
        }
    }

    public interface IMultipleItemModelDefinition {
    	/**
    	 * A map from item meta values to different item models
    	 * @return
    	 */
    	@OnlyIn(Dist.CLIENT)
        Map<Integer, ResourceLocation> getModels();
    }

    public interface IBlockStateItemModelDefinition {
    	/**
    	 * A maps from item meta values to blockstate variants
    	 * @return
    	 */
    	@OnlyIn(Dist.CLIENT)
        Map<Integer, String> getVariants();
    }

    public interface ICustomMeshCallback {

        /**
         * A callback to get a custom mesh definition
         * @return
         */
        @OnlyIn(Dist.CLIENT)
        ItemMeshDefinition getMeshDefinition();

    }
}
