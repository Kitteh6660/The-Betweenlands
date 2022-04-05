package thebetweenlands.common.item.food;

import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import thebetweenlands.api.item.IFoodSicknessItem;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.farming.ItemAspectrusSeeds;
import thebetweenlands.common.item.farming.ItemMiddleFruitBushSeeds;
import thebetweenlands.common.item.farming.ItemSpores;
import thebetweenlands.common.item.farming.ItemSwampKelp;
import thebetweenlands.common.item.farming.ItemSwampReed;

public class BLFoods
{
    public static final Food SAP_BALL = (new Food.Builder()).nutrition(0).saturationMod(0.0F).alwaysEat().build(); // new ItemSapBall();
    public static final Food ROTTEN_FOOD = (new Food.Builder()).nutrition(0).saturationMod(0.0F).effect(() -> new EffectInstance(Effects.HUNGER, 200, 0), 1.0F).effect(() -> new EffectInstance(Effects.POISON, 200, 0), 1.0F).alwaysEat().build(); //(ItemRottenFood) new ItemRottenFood().setAlwaysEdible();
    public static final Food MIDDLE_FRUIT_BUSH_SEEDS = new ItemMiddleFruitBushSeeds();
    public static final Food SPORES = new ItemSpores();
    public static final Food ASPECTRUS_SEEDS = new ItemAspectrusSeeds();
    public static final Food MIRE_SNAIL_EGG = new ItemMireSnailEgg();
    public static final Food MIRE_SNAIL_EGG_COOKED = (new Food.Builder()).nutrition(8).saturationMod(1.0F).build(); // new BLFoods(8, 1, false);
    public static final Food ANGLER_MEAT_RAW = (new Food.Builder()).nutrition(4).saturationMod(0.4F).meat().build(); // new BLFoods(4, 0.4F, false);
    public static final Food ANGLER_MEAT_COOKED = (new Food.Builder()).nutrition(8).saturationMod(0.8F).meat().build(); // new BLFoods(8, 0.8F, false);
    public static final Food FROG_LEGS_RAW = (new Food.Builder()).nutrition(3).saturationMod(0.3F).meat().build(); // new BLFoods(3, 0.4F, false);
    public static final Food FROG_LEGS_COOKED = (new Food.Builder()).nutrition(6).saturationMod(0.8F).meat().build(); // new BLFoods(6, 0.8F, false);
    public static final Food SNAIL_FLESH_RAW = (new Food.Builder()).nutrition(3).saturationMod(0.4F).meat().build(); // new BLFoods(3, 0.4F, false);
    public static final Food SNAIL_FLESH_COOKED = (new Food.Builder()).nutrition(6).saturationMod(0.9F).meat().build(); // new BLFoods(6, 0.9F, false);
    public static final Food REED_DONUT = (new Food.Builder()).nutrition(6).saturationMod(0.6F).build(); // new BLFoods(6, 0.6F, false);
    public static final Food JAM_DONUT = (new Food.Builder()).nutrition(10).saturationMod(0.6F).build(); // new BLFoods(10, 0.6F, false);
    public static final Food GERTS_DONUT = (new Food.Builder()).nutrition(6).saturationMod(0.6F).build(); // new ItemGertsDonut();
    public static final Food ASPECTRUS_FRUIT = new ItemAspectrusFruit();
    public static final Food PUFFSHROOM_TENDRIL = (new Food.Builder()).nutrition(8).saturationMod(0.9F).build(); // new BLFoods(8, 0.9F, false);
    public static final Food KRAKEN_TENTACLE = (new Food.Builder()).nutrition(8).saturationMod(0.9F).build(); // new BLFoods(8, 0.9F, false);
    public static final Food KRAKEN_CALAMARI = (new Food.Builder()).nutrition(14).saturationMod(1.2F).build(); // new BLFoods(14, 1.2F, false);
    public static final Food MIDDLE_FRUIT = (new Food.Builder()).nutrition(6).saturationMod(0.6F).build(); // new BLFoods(6, 0.6F, false);
    public static final Food MINCE_PIE = (new Food.Builder()).nutrition(8).saturationMod(1.0F).build(); // new BLFoods(8, 1F, false);
    public static final Food CHRISTMAS_PUDDING = (new Food.Builder()).nutrition(6).saturationMod(0.95F).build(); // new BLFoods(6, 0.95F, false);
    public static final Food CANDY_CANE = (new Food.Builder()).nutrition(4).saturationMod(0.85F).build(); // new BLFoods(4, 0.85F, false);
    public static final Food WEEPING_BLUE_PETAL = (new Food.Builder()).nutrition(1).saturationMod(1.0F).effect(() -> new EffectInstance(ElixirEffectRegistry.EFFECT_RIPENING.getPotionEffect(), 600, 2), 0.1F).build(); // new ItemWeepingBluePetal();
    public static final Food WIGHT_HEART = new ItemWightHeart();
    public static final Food YELLOW_DOTTED_FUNGUS = (new Food.Builder()).nutrition(8).saturationMod(0.6F).build(); // new BLFoods(8, 0.6F, false);
    public static final Food SILT_CRAB_CLAW = (new Food.Builder()).nutrition(2).saturationMod(0.6F).build(); // new BLFoods(2, 0.6F, false);
    public static final Food CRAB_STICK = (new Food.Builder()).nutrition(6).saturationMod(0.9F).build(); // new BLFoods(6, 0.9F, false);
    public static final Food NETTLE_SOUP = (new Food.Builder()).nutrition(10).saturationMod(1.0F).build(); // new ItemNettleSoup();
    public static final Food SLUDGE_JELLO = (new Food.Builder()).nutrition(4).saturationMod(0.9F).build(); // new BLFoods(4, 0.9F, false);
    public static final Food MIDDLE_FRUIT_JELLO = (new Food.Builder()).nutrition(10).saturationMod(1.0F).build(); // new BLFoods(10, 1.0F, false);
    public static final Food SAP_JELLO = (new Food.Builder()).nutrition(4).saturationMod(0.9F).alwaysEat().build(); // new ItemSapJello();
    public static final Food MARSHMALLOW = (new Food.Builder()).nutrition(10).saturationMod(1.0F).effect(() -> new EffectInstance(Effects.MOVEMENT_SPEED, 400, 1), 0.1F).build(); // new ItemMarshmallow();
    public static final Food MARSHMALLOW_PINK = (new Food.Builder()).nutrition(4).saturationMod(0.3F).effect(() -> new EffectInstance(Effects.JUMP, 400, 1), 1.0F).build(); // new ItemMarshmallowPink();
    public static final Food FLAT_HEAD_MUSHROOM = (new Food.Builder()).nutrition(3).saturationMod(0.6F).effect(() -> new EffectInstance(Effects.HUNGER, 500, 1), 1.0F).build(); // new ItemFlatHeadMushroom();
    public static final Food BLACK_HAT_MUSHROOM = (new Food.Builder()).nutrition(3).saturationMod(0.6F).effect(() -> new EffectInstance(Effects.HUNGER, 500, 1), 1.0F).build(); // new ItemBlackHatMushroom();
    public static final Food BULB_CAPPED_MUSHROOM = (new Food.Builder()).nutrition(3).saturationMod(0.6F).effect(() -> new EffectInstance(Effects.CONFUSION, 200, 0), 1.0F).effect(() -> new EffectInstance(Effects.NIGHT_VISION, 200, 0), 1.0F).build(); // new ItemBulbCappedMushroom();
    public static final Food SWAMP_REED = new ItemSwampReed();
    public static final Food SWAMP_KELP = new ItemSwampKelp();
    public static final Food FRIED_SWAMP_KELP = (new Food.Builder()).nutrition(5).saturationMod(0.6F).build(); // new BLFoods(5, 0.6F, false);
    public static final Food FORBIDDEN_FIG = new ItemForbiddenFig();
    public static final Food CANDY_BLUE = (new Food.Builder()).nutrition(4).saturationMod(1.0F).build(); // new BLFoods(4, 1.0F, false);
    public static final Food CANDY_RED = (new Food.Builder()).nutrition(4).saturationMod(1.0F).build(); // new BLFoods(4, 1.0F, false);
    public static final Food CANDY_YELLOW = (new Food.Builder()).nutrition(4).saturationMod(1.0F).build(); // new BLFoods(4, 1.0F, false);
    public static final Food CHIROMAW_WING = (new Food.Builder()).nutrition(0).saturationMod(0.0F).alwaysEat().build(); // new ItemChiromawWing();
    public static final Food TANGLED_ROOT = new ItemTangledRoot();
    public static final Food MIRE_SCRAMBLE = (new Food.Builder()).nutrition(12).saturationMod(1.2F).build(); // new ItemMireScramble();
    public static final Food WEEPING_BLUE_PETAL_SALAD = (new Food.Builder()).nutrition(6).saturationMod(1.2F).effect(() -> new EffectInstance(ElixirEffectRegistry.EFFECT_RIPENING.getPotionEffect(), 4200, 2), 1.0F).alwaysEat().build(); // new ItemWeepingBluePetalSalad();
    public static final Food NIBBLESTICK = (new Food.Builder()).nutrition(1).saturationMod(0.1F).build(); // new ItemNibblestick();
    public static final Food SPIRIT_FRUIT = (new Food.Builder()).nutrition(4).saturationMod(1.2F).effect(() -> new EffectInstance(Effects.REGENERATION, 200, 1), 1.0F).effect(() -> new EffectInstance(Effects.ABSORPTION, 2400, 0), 1.0F).alwaysEat().build(); // new ItemSpiritFruit();
    public static final Food SUSHI = (new Food.Builder()).nutrition(5).saturationMod(1.0F).build(); // new BLFoods(5, 1.0F, false);
	
	/*public ItemBLFood(int healAmount, float saturationModifier, boolean isWolfsFavoriteMeat) {
		super(healAmount, saturationModifier, isWolfsFavoriteMeat);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}*/
}
