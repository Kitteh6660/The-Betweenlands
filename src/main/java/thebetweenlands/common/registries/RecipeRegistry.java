package thebetweenlands.common.registries;

import thebetweenlands.common.item.herblore.ItemGenericCrushed;
import thebetweenlands.common.item.herblore.ItemGenericPlantDrop;
import thebetweenlands.common.item.misc.ItemGeneric;
import thebetweenlands.common.item.misc.ItemSwampTalisman;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;

public class RecipeRegistry {
    public void init() {

        registerDruidAltarRecipes();
    }

    private void registerDruidAltarRecipes() {
        DruidAltarRecipe.addRecipe(ItemSwampTalisman.createStack(ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_1, 1), ItemSwampTalisman.createStack(ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_2, 1), ItemSwampTalisman.createStack(ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_3, 1), ItemSwampTalisman.createStack(ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_4, 1), ItemSwampTalisman.createStack(ItemSwampTalisman.EnumTalisman.SWAMP_TALISMAN_0, 1));
    }

    private void registerCompostRecipes() {
        CompostRecipe.addRecipe(30, 12000, ItemRegistry.ITEMS_GENERIC, ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.ROTTEN_BARK).getItemDamage());
//        CompostRecipe.addRecipe(25, 12000, Item.getItemFromBlock(BlockRegistry.rottenWeedwoodBark));
//        CompostRecipe.addRecipe(10, 8000, Item.getItemFromBlock(BlockRegistry.sundew));
//        CompostRecipe.addRecipe(6, 10000, Item.getItemFromBlock(BlockRegistry.doubleSwampTallgrass));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.phragmites));
//        CompostRecipe.addRecipe(6, 10000, Item.getItemFromBlock(BlockRegistry.tallCattail));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.cardinalFlower));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.broomsedge));
//        CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.weepingBlue));
//        CompostRecipe.addRecipe(12, 11000, Item.getItemFromBlock(BlockRegistry.pitcherPlant));
//        CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BlockRegistry.bogBean));
//        CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BlockRegistry.goldenClub));
//        CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BlockRegistry.marshMarigold));
//        CompostRecipe.addRecipe(3, 5000, Item.getItemFromBlock(BlockRegistry.swampKelp));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.waterWeeds));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.waterFlower));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.waterFlowerStalk));
//        CompostRecipe.addRecipe(20, 12000, Item.getItemFromBlock(BlockRegistry.root));
//        CompostRecipe.addRecipe(20, 12000, Item.getItemFromBlock(BlockRegistry.rootUW));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.blackHatMushroom));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.flatHeadMushroom));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.bulbCappedMushroom));
//        CompostRecipe.addRecipe(4, 6000, Item.getItemFromBlock(BlockRegistry.swampPlant));
//        CompostRecipe.addRecipe(12, 10000, Item.getItemFromBlock(BlockRegistry.venusFlyTrap));
//        CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.volarpad));
//        CompostRecipe.addRecipe(20, 12000, Item.getItemFromBlock(BlockRegistry.weedwoodBush));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.thorns));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.poisonIvy));
//        CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.wallPlants));
//        CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.wallPlants), 1);
//        CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.caveMoss));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.caveGrass));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.catTail));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.swampTallGrass));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.shoots));
//        CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.nettleFlowered));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.nettle));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.arrowArum));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.buttonBush));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.marshHibiscus));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.pickerelWeed));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.softRush));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.marshMallow));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.blueIris));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.copperIris));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.blueEyedGrass));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.milkweed));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.boneset));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.bottleBrushGrass));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.sludgecreep));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.deadWeedwoodBush));
//        CompostRecipe.addRecipe(3, 5000, Item.getItemFromBlock(BlockRegistry.hanger));
//        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.waterFlowerStalk));
//        CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.mireCoral));
//        CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.deepWaterCoral));
//        CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.saplingRubberTree));
//        CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.saplingSapTree));
//        CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.saplingWeedwood));
        CompostRecipe.addRecipe(3, 5000, ItemRegistry.ITEMS_GENERIC, ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.SWAMP_REED).getItemDamage());
        CompostRecipe.addRecipe(3, 5000, ItemRegistry.ITEMS_GENERIC, ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.DRIED_SWAMP_REED).getItemDamage());
        CompostRecipe.addRecipe(5, 8000, ItemRegistry.ITEMS_GENERIC, ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.SWAMP_REED_ROPE).getItemDamage());
        CompostRecipe.addRecipe(5, 8000, ItemRegistry.ITEMS_GENERIC, ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.TANGLED_ROOT).getItemDamage());
        CompostRecipe.addRecipe(3, 5000, ItemRegistry.ITEMS_GENERIC, ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.SWAMP_KELP).getItemDamage());
        CompostRecipe.addRecipe(5, 8000, ItemRegistry.FLATHEAD_MUSHROOM);
        CompostRecipe.addRecipe(5, 8000, ItemRegistry.BLACK_HAT_MUSHROOM);
        CompostRecipe.addRecipe(5, 8000, ItemRegistry.BULB_CAPPED_MUSHROOM);
        CompostRecipe.addRecipe(12, 10000, ItemRegistry.YELLOW_DOTTED_FUNGUS);

        for (ItemGenericCrushed.EnumItemGenericCrushed type : ItemGenericCrushed.EnumItemGenericCrushed.values()) {
            CompostRecipe.addRecipe(3, 4000, ItemRegistry.ITEMS_GENERIC_CRUSHED, ItemGenericCrushed.createStack(type).getItemDamage());
        }

        for (ItemGenericPlantDrop.EnumItemPlantDrop type : ItemGenericPlantDrop.EnumItemPlantDrop.values()) {
            CompostRecipe.addRecipe(3, 4000, ItemRegistry.ITEMS_GENERIC_PLANT_DROP, ItemGenericPlantDrop.createStack(type).getItemDamage());
        }
    }
}
