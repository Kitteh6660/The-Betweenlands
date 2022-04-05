package thebetweenlands.common.registries;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.StoneButtonBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.BLWoodType;
import thebetweenlands.common.block.container.BlockAlembic;
import thebetweenlands.common.block.container.BlockAnimator;
import thebetweenlands.common.block.container.BlockAspectVial;
import thebetweenlands.common.block.container.BlockBLDualFurnace;
import thebetweenlands.common.block.container.BlockBLFurnace;
import thebetweenlands.common.block.container.BlockCenser;
import thebetweenlands.common.block.container.BlockChestBetweenlands;
import thebetweenlands.common.block.container.BlockCompostBin;
import thebetweenlands.common.block.container.BlockDruidAltar;
import thebetweenlands.common.block.container.BlockGeckoCage;
import thebetweenlands.common.block.container.BlockHopperBetweenlands;
import thebetweenlands.common.block.container.BlockInfuser;
import thebetweenlands.common.block.container.BlockItemShelf;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.block.container.BlockLootUrn;
import thebetweenlands.common.block.container.BlockMortar;
import thebetweenlands.common.block.container.BlockMudBrickAlcove;
import thebetweenlands.common.block.container.BlockPresent;
import thebetweenlands.common.block.container.BlockPurifier;
import thebetweenlands.common.block.container.BlockRepeller;
import thebetweenlands.common.block.container.BlockRuneCarvingTable;
import thebetweenlands.common.block.container.BlockRuneWeavingTable;
import thebetweenlands.common.block.container.BlockTarLootPot;
import thebetweenlands.common.block.container.BlockWeedwoodJukebox;
import thebetweenlands.common.block.container.BlockWeedwoodWorkbench;
import thebetweenlands.common.block.container.BlockWindChime;
import thebetweenlands.common.block.container.LiquidBarrelBlock;
import thebetweenlands.common.block.farming.BlockAspectrusCrop;
import thebetweenlands.common.block.farming.BlockDugDirt;
import thebetweenlands.common.block.farming.BlockDugGrass;
import thebetweenlands.common.block.farming.BlockFungusCrop;
import thebetweenlands.common.block.farming.BlockMiddleFruitBush;
import thebetweenlands.common.block.fluid.RubberBlock;
import thebetweenlands.common.block.fluid.StagnantWaterBlock;
import thebetweenlands.common.block.fluid.SwampWaterBlock;
import thebetweenlands.common.block.fluid.TarBlock;
import thebetweenlands.common.block.misc.BetweenlanternBlock;
import thebetweenlands.common.block.misc.BlockBauble;
import thebetweenlands.common.block.misc.BlockBouncyBetweenlands;
import thebetweenlands.common.block.misc.BlockBurntScrivenerMark;
import thebetweenlands.common.block.misc.BlockCavingRopeLight;
import thebetweenlands.common.block.misc.DampTorchBlock;
import thebetweenlands.common.block.misc.BlockGlassBetweenlands;
import thebetweenlands.common.block.misc.BlockGroundItem;
import thebetweenlands.common.block.misc.BlockLanternSiltGlass;
import thebetweenlands.common.block.misc.BlockMossBed;
import thebetweenlands.common.block.misc.BlockMudFlowerPot;
import thebetweenlands.common.block.misc.BlockMudFlowerPotCandle;
import thebetweenlands.common.block.misc.BlockOctine;
import thebetweenlands.common.block.misc.BlockOfferingTable;
import thebetweenlands.common.block.misc.BlockPolishedDentrothyst;
import thebetweenlands.common.block.misc.BlockRope;
import thebetweenlands.common.block.misc.BlockRubberTap;
import thebetweenlands.common.block.misc.BlockSludge;
import thebetweenlands.common.block.misc.BlockSulfurScrivenerMark;
import thebetweenlands.common.block.misc.WallSulfurTorchBlock;
import thebetweenlands.common.block.misc.ExtinguishedSulfurTorchBlock;
import thebetweenlands.common.block.misc.SulfurTorchBlock;
import thebetweenlands.common.block.misc.SyrmoritePressurePlateBlock;
import thebetweenlands.common.block.misc.WallExtinguishedSulfurTorchBlock;
import thebetweenlands.common.block.plant.BlockAlgae;
import thebetweenlands.common.block.plant.BlockBlackHatMushroom;
import thebetweenlands.common.block.plant.BlockBladderwortFlower;
import thebetweenlands.common.block.plant.BlockBladderwortStalk;
import thebetweenlands.common.block.plant.BlockBogBeanFlower;
import thebetweenlands.common.block.plant.BlockBogBeanStalk;
import thebetweenlands.common.block.plant.BlockBulbCappedMushroom;
import thebetweenlands.common.block.plant.BlockBulbCappedMushroomCap;
import thebetweenlands.common.block.plant.BlockBulbCappedMushroomStalk;
import thebetweenlands.common.block.plant.BlockCaveGrass;
import thebetweenlands.common.block.plant.BlockCaveMoss;
import thebetweenlands.common.block.plant.BlockDoublePlantBL;
import thebetweenlands.common.block.plant.BlockEdgePlant;
import thebetweenlands.common.block.plant.BlockFlatheadMushroom;
import thebetweenlands.common.block.plant.BlockGoldenClubFlower;
import thebetweenlands.common.block.plant.BlockGoldenClubStalk;
import thebetweenlands.common.block.plant.BlockHollowLog;
import thebetweenlands.common.block.plant.BlockLichen;
import thebetweenlands.common.block.plant.BlockMarshMarigoldFlower;
import thebetweenlands.common.block.plant.BlockMarshMarigoldStalk;
import thebetweenlands.common.block.plant.BlockMoss;
import thebetweenlands.common.block.plant.BlockNesting;
import thebetweenlands.common.block.plant.BlockNettle;
import thebetweenlands.common.block.plant.BlockNettleFlowered;
import thebetweenlands.common.block.plant.BlockPhragmites;
import thebetweenlands.common.block.plant.BlockPlant;
import thebetweenlands.common.block.plant.BlockPlantUnderwater;
import thebetweenlands.common.block.plant.BlockPoisonIvy;
import thebetweenlands.common.block.plant.BlockSaplingBetweenlands;
import thebetweenlands.common.block.plant.BlockShelfFungus;
import thebetweenlands.common.block.plant.BlockSludgeDungeonHangingPlant;
import thebetweenlands.common.block.plant.BlockSludgeDungeonPlant;
import thebetweenlands.common.block.plant.BlockSundew;
import thebetweenlands.common.block.plant.BlockSwampKelp;
import thebetweenlands.common.block.plant.BlockSwampReed;
import thebetweenlands.common.block.plant.BlockSwampReedUnderwater;
import thebetweenlands.common.block.plant.BlockThorns;
import thebetweenlands.common.block.plant.BlockVenusFlyTrap;
import thebetweenlands.common.block.plant.BlockWaterWeeds;
import thebetweenlands.common.block.plant.BlockWeedwoodBush;
import thebetweenlands.common.block.plant.BlockWeepingBlue;
import thebetweenlands.common.block.structure.BLWallSignBlock;
import thebetweenlands.common.block.structure.BlockBeamLensSupport;
import thebetweenlands.common.block.structure.BlockBeamOrigin;
import thebetweenlands.common.block.structure.BlockBeamRelay;
import thebetweenlands.common.block.structure.BlockBeamTube;
import thebetweenlands.common.block.structure.BlockBrazier;
import thebetweenlands.common.block.structure.BlockChipPath;
import thebetweenlands.common.block.structure.BlockCompactedMud;
import thebetweenlands.common.block.structure.BlockCompactedMudSlope;
import thebetweenlands.common.block.structure.BlockDecayPitControl;
import thebetweenlands.common.block.structure.BlockDecayPitGroundChain;
import thebetweenlands.common.block.structure.BlockDecayPitHangingChain;
import thebetweenlands.common.block.structure.BlockDecayPitInvisibleFloorBlock;
import thebetweenlands.common.block.structure.BlockDecayPitInvisibleFloorBlockDiagonal;
import thebetweenlands.common.block.structure.BlockDecayPitInvisibleFloorBlockL1;
import thebetweenlands.common.block.structure.BlockDecayPitInvisibleFloorBlockL2;
import thebetweenlands.common.block.structure.BlockDecayPitInvisibleFloorBlockR1;
import thebetweenlands.common.block.structure.BlockDecayPitInvisibleFloorBlockR2;
import thebetweenlands.common.block.structure.BlockDiagonalEnergyBarrier;
import thebetweenlands.common.block.structure.BlockDruidStone;
import thebetweenlands.common.block.structure.BlockDungeonDoorCombination;
import thebetweenlands.common.block.structure.BlockDungeonDoorRunes;
import thebetweenlands.common.block.structure.BlockDungeonWallCandle;
import thebetweenlands.common.block.structure.BlockEnergyBarrier;
import thebetweenlands.common.block.structure.BlockEnergyBarrierMud;
import thebetweenlands.common.block.structure.BlockItemCage;
import thebetweenlands.common.block.structure.BlockMobSpawnerBetweenlands;
import thebetweenlands.common.block.structure.BlockMudBrickShingleSlab;
import thebetweenlands.common.block.structure.BlockMudBrickSpikeTrap;
import thebetweenlands.common.block.structure.BlockMudBricks;
import thebetweenlands.common.block.structure.BlockMudBricksClimbable;
import thebetweenlands.common.block.structure.BlockMudTiles;
import thebetweenlands.common.block.structure.BlockMudTilesSpikeTrap;
import thebetweenlands.common.block.structure.BlockMudTilesWater;
import thebetweenlands.common.block.structure.BlockPortalFrame;
import thebetweenlands.common.block.structure.BlockPossessedBlock;
import thebetweenlands.common.block.structure.BlockPuffshroom;
import thebetweenlands.common.block.structure.BlockRottenBarkCarved;
import thebetweenlands.common.block.structure.BlockSimulacrum;
import thebetweenlands.common.block.structure.BlockSlanted;
import thebetweenlands.common.block.structure.BlockSmoothCragrock;
import thebetweenlands.common.block.structure.BlockSpikeTrap;
import thebetweenlands.common.block.structure.BLStandingSignBlock;
import thebetweenlands.common.block.structure.BlockTarBeastSpawner;
import thebetweenlands.common.block.structure.BlockTemplePillar;
import thebetweenlands.common.block.structure.BlockTreePortal;
import thebetweenlands.common.block.structure.BlockWalkway;
import thebetweenlands.common.block.structure.BlockWaystone;
import thebetweenlands.common.block.structure.BlockWoodenSupportBeam;
import thebetweenlands.common.block.terrain.BLOreBlock;
import thebetweenlands.common.block.terrain.BlockBlackIce;
import thebetweenlands.common.block.terrain.BlockCircleGem;
import thebetweenlands.common.block.terrain.BlockDeadGrass;
import thebetweenlands.common.block.terrain.BlockDentrothyst;
import thebetweenlands.common.block.terrain.BlockFallenLeaves;
import thebetweenlands.common.block.terrain.BlockGenericCollapsing;
import thebetweenlands.common.block.terrain.BlockGenericMirage;
import thebetweenlands.common.block.terrain.BlockGiantRoot;
import thebetweenlands.common.block.terrain.BlockHanger;
import thebetweenlands.common.block.terrain.BlockHearthgroveLog;
import thebetweenlands.common.block.terrain.BlockLeavesSpiritTree;
import thebetweenlands.common.block.terrain.BlockLifeCrystalStalactite;
import thebetweenlands.common.block.terrain.BlockLogBetweenlands;
import thebetweenlands.common.block.terrain.BlockMud;
import thebetweenlands.common.block.terrain.BlockPeat;
import thebetweenlands.common.block.terrain.BlockPuddle;
import thebetweenlands.common.block.terrain.BlockRoot;
import thebetweenlands.common.block.terrain.BlockRootUnderwater;
import thebetweenlands.common.block.terrain.BlockRottenLog;
import thebetweenlands.common.block.terrain.BlockRubberLog;
import thebetweenlands.common.block.terrain.BlockSilt;
import thebetweenlands.common.block.terrain.BlockSlimyGrass;
import thebetweenlands.common.block.terrain.SludgyDirtBlock;
import thebetweenlands.common.block.terrain.BlockSnowBetweenlands;
import thebetweenlands.common.block.terrain.BlockSpreadingRottenLog;
import thebetweenlands.common.block.terrain.BlockSpreadingSludgyDirt;
import thebetweenlands.common.block.terrain.BlockStalactite;
import thebetweenlands.common.block.terrain.BlockSwampDirt;
import thebetweenlands.common.block.terrain.BlockSwampGrass;
import thebetweenlands.common.block.terrain.BlockTintedLeaves;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.common.block.terrain.CragrockBlock;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.world.gen.feature.PodRoots;
import thebetweenlands.common.world.gen.feature.tree.HearthgroveTree;
import thebetweenlands.common.world.gen.feature.tree.NibbletwigTree;
import thebetweenlands.common.world.gen.feature.tree.RubberTree;
import thebetweenlands.common.world.gen.feature.tree.SapTree;
import thebetweenlands.common.world.gen.feature.tree.SpiritTree;
import thebetweenlands.common.world.gen.feature.tree.WeedwoodTree;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.PressurePlateBlock.Sensitivity;

public class BlockRegistry {
	
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TheBetweenlands.MOD_ID);
    
	//FLUID BLOCKS
	public static final RegistryObject<Block> SWAMP_WATER = BLOCKS.register("swamp_water", () -> new SwampWaterBlock(AbstractBlock.Properties.of(Material.WATER)));
	public static final RegistryObject<Block> STAGNANT_WATER = BLOCKS.register("stagnant_water", () -> new StagnantWaterBlock(AbstractBlock.Properties.of(Material.WATER)));
	public static final RegistryObject<Block> TAR = BLOCKS.register("tar", () -> new TarBlock(AbstractBlock.Properties.of(Material.WATER)));
	public static final RegistryObject<Block> RUBBER = BLOCKS.register("rubber", () -> new RubberBlock(AbstractBlock.Properties.of(Material.WATER)));
	//DRUID STONES
    public static final RegistryObject<Block> DRUID_STONE_1 = BLOCKS.register("druid_stone_1", () -> new BlockDruidStone(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 3.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().lightLevel((ll) -> { return 12; })));  // new BlockDruidStone(Material.STONE);
    public static final RegistryObject<Block> DRUID_STONE_2 = BLOCKS.register("druid_stone_2", () -> new BlockDruidStone(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 3.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().lightLevel((ll) -> { return 12; }))); // new BlockDruidStone(Material.STONE);
    public static final RegistryObject<Block> DRUID_STONE_3 = BLOCKS.register("druid_stone_3", () -> new BlockDruidStone(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 3.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().lightLevel((ll) -> { return 12; }))); // new BlockDruidStone(Material.STONE);
    public static final RegistryObject<Block> DRUID_STONE_4 = BLOCKS.register("druid_stone_4", () -> new BlockDruidStone(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 3.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().lightLevel((ll) -> { return 12; }))); // new BlockDruidStone(Material.STONE);
    public static final RegistryObject<Block> DRUID_STONE_5 = BLOCKS.register("druid_stone_5", () -> new BlockDruidStone(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 3.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().lightLevel((ll) -> { return 12; }))); // new BlockDruidStone(Material.STONE);
    public static final RegistryObject<Block> DRUID_STONE_6 = BLOCKS.register("druid_stone_6", () -> new BlockDruidStone(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 3.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().lightLevel((ll) -> { return 12; }))); // new BlockDruidStone(Material.STONE);
    //TERRAIN BLOCKS
    public static final RegistryObject<Block> BETWEENLANDS_BEDROCK = BLOCKS.register("betweenlands_bedrock", () -> new Block(AbstractBlock.Properties.of(Material.STONE).strength(-1.0F, 3600000.0F)));
    public static final RegistryObject<Block> BETWEENSTONE = BLOCKS.register("betweenstone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> CORRUPT_BETWEENSTONE = BLOCKS.register("corrupt_betweenstone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> GENERIC_STONE = BLOCKS.register("generic_stone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> MUD = BLOCKS.register("mud", () -> new BlockMud(AbstractBlock.Properties.of(BLMaterialRegistry.MUD).sound(SoundType.GRAVEL).strength(0.5F).harvestTool(ToolType.SHOVEL))); // new BlockMud();
    public static final RegistryObject<Block> PEAT = BLOCKS.register("peat", () -> new BlockPeat(AbstractBlock.Properties.of(Material.DIRT).sound(SoundType.GRASS).strength(0.5F).harvestTool(ToolType.SHOVEL))); // new BlockPeat();
    public static final RegistryObject<Block> SLUDGY_DIRT = BLOCKS.register("sludgy_dirt", () -> new SludgyDirtBlock(AbstractBlock.Properties.of(Material.GRASS).sound(SoundType.GRAVEL).strength(0.5F).harvestTool(ToolType.SHOVEL))); // new BlockSludgyDirt();
    public static final RegistryObject<Block> SPREADING_SLUDGY_DIRT = BLOCKS.register("spreading_sludgy_dirt", () -> new BlockSpreadingSludgyDirt(AbstractBlock.Properties.of(Material.GRASS).sound(SoundType.WET_GRASS).strength(0.5F).harvestTool(ToolType.SHOVEL).randomTicks())); // new BlockSpreadingSludgyDirt();
    public static final RegistryObject<Block> SLIMY_DIRT = BLOCKS.register("slimy_dirt", () -> new Block(AbstractBlock.Properties.of(Material.DIRT).sound(SoundType.GRAVEL).strength(0.5F).harvestTool(ToolType.SHOVEL))); // new Block(Material.GROUND)
    public static final RegistryObject<Block> SLIMY_GRASS = BLOCKS.register("slimy_grass", () -> new BlockSlimyGrass(AbstractBlock.Properties.of(Material.GRASS).sound(SoundType.WET_GRASS).strength(0.5F).harvestTool(ToolType.SHOVEL))); // new BlockSlimyGrass();
    public static final RegistryObject<Block> CRAGROCK = BLOCKS.register("cragrock", () -> new CragrockBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 2.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops())); // new BlockCragrock(Material.STONE);
    public static final RegistryObject<Block> MOSSY_CRAGROCK_TOP = BLOCKS.register("mossy_cragrock_top", () -> new CragrockBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 2.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops())); // new BlockCragrock(Material.STONE);
    public static final RegistryObject<Block> MOSSY_CRAGROCK_BOTTOM = BLOCKS.register("mossy_cragrock_bottom", () -> new CragrockBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 2.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops())); // new BlockCragrock(Material.STONE);
    public static final RegistryObject<Block> PITSTONE = BLOCKS.register("pitstone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 2.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops())); // new Block(Material.STONE).setDefaultCreativeTab().sound(SoundType.STONE).strength(1.5F, 10.0F);
    public static final RegistryObject<Block> LIMESTONE = BLOCKS.register("limestone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.2F, 1.6F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops())); // new Block(Material.STONE).setDefaultCreativeTab().sound(SoundType.STONE).setHardness(1.2F).setResistance(8.0F);
    public static final RegistryObject<Block> SWAMP_DIRT = BLOCKS.register("swamp_dirt", () -> new BlockSwampDirt(AbstractBlock.Properties.of(Material.DIRT).sound(SoundType.GRASS).strength(0.5F).harvestTool(ToolType.SHOVEL))); // new BlockSwampDirt(Material.GROUND);
    public static final RegistryObject<Block> COARSE_SWAMP_DIRT = BLOCKS.register("coarse_swamp_dirt", () -> new BlockSwampDirt(AbstractBlock.Properties.of(Material.DIRT).sound(SoundType.GRASS).strength(0.5F).harvestTool(ToolType.SHOVEL))); // new BlockSwampDirt(Material.GROUND).setItemDropped(() -> Item.getItemFromBlock(BlockRegistry.SWAMP_DIRT));
    public static final RegistryObject<Block> SWAMP_GRASS = BLOCKS.register("swamp_grass", () -> new BlockSwampGrass(AbstractBlock.Properties.of(Material.DIRT).sound(SoundType.GRASS).strength(0.5F).harvestTool(ToolType.SHOVEL).randomTicks())); // new BlockSwampGrass();
    public static final RegistryObject<Block> SILT = BLOCKS.register("silt", () -> new BlockSilt(AbstractBlock.Properties.of(Material.SAND).sound(SoundType.SAND).strength(0.5F).harvestTool(ToolType.SHOVEL))); // new BlockSilt();
    public static final RegistryObject<Block> DEAD_GRASS = BLOCKS.register("dead_grass", () -> new BlockDeadGrass(AbstractBlock.Properties.of(Material.GRASS).sound(SoundType.GRASS).strength(0.5F).harvestTool(ToolType.SHOVEL).randomTicks())); // new BlockDeadGrass();
    public static final RegistryObject<Block> TAR_SOLID = BLOCKS.register("solid_tar", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 2.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> PUDDLE = BLOCKS.register("puddle", () -> new BlockPuddle(AbstractBlock.Properties.of(Material.WATER).strength(0.0F).randomTicks().noCollission())); // new BlockPuddle();
    public static final RegistryObject<Block> WISP = BLOCKS.register("wisp", () -> new BlockWisp(AbstractBlock.Properties.of(BLMaterialRegistry.WISP).sound(SoundType.STONE).strength(0.0F).randomTicks())); // new BlockWisp();
    //ORES
    public static final RegistryObject<Block> OCTINE_ORE = BLOCKS.register("octine_ore", () -> new BLOreBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).harvestLevel(1).requiresCorrectToolForDrops().lightLevel((ll) -> { return 13; })));
    public static final RegistryObject<Block> VALONITE_ORE = BLOCKS.register("valonite_ore", () -> new BLOreBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).harvestLevel(2).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SULFUR_ORE = BLOCKS.register("sulfur_ore", () -> new BLOreBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).harvestLevel(0).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SLIMY_BONE_ORE = BLOCKS.register("slimy_bone_ore", () -> new BLOreBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).harvestLevel(0).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SCABYST_ORE = BLOCKS.register("scabyst_ore", () -> new BLOreBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).harvestLevel(2).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SYRMORITE_ORE = BLOCKS.register("syrmorite_ore", () -> new BLOreBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).harvestLevel(1).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> AQUA_MIDDLE_GEM_ORE = BLOCKS.register("aqua_middle_gem_ore", () -> new BlockCircleGem(CircleGemType.AQUA, AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).harvestLevel(1).requiresCorrectToolForDrops().lightLevel((ll) -> { return 13; })));
    public static final RegistryObject<Block> CRIMSON_MIDDLE_GEM_ORE = BLOCKS.register("crimson_middle_gem_ore", () -> new BlockCircleGem(CircleGemType.CRIMSON, AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).harvestLevel(1).requiresCorrectToolForDrops().lightLevel((ll) -> { return 13; })));
    public static final RegistryObject<Block> GREEN_MIDDLE_GEM_ORE = BLOCKS.register("green_middle_gem_ore", () -> new BlockCircleGem(CircleGemType.GREEN, AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 6.0F).harvestTool(ToolType.PICKAXE).harvestLevel(1).requiresCorrectToolForDrops().lightLevel((ll) -> { return 13; })));
    public static final RegistryObject<Block> LIFE_CRYSTAL_STALACTITE = BLOCKS.register("life_crystal_stalactite", () -> new BlockLifeCrystalStalactite(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.5F, 10.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops())); // new BlockLifeCrystalStalactite(FluidRegistry.SWAMP_WATER, Material.WATER);
    public static final RegistryObject<Block> LIFE_CRYSTAL_STALACTITE_ORE = BLOCKS.register("life_crystal_stalactite_ore", () -> new BlockLifeCrystalStalactite(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.5F, 10.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops())); // new BlockLifeCrystalStalactite(FluidRegistry.SWAMP_WATER, Material.WATER);
    public static final RegistryObject<Block> STALACTITE = BLOCKS.register("stalactite", () -> new BlockStalactite(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 2.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops())); // new BlockStalactite();
    //TREES
    public static final RegistryObject<Block> WEEDWOOD_LOG = BLOCKS.register("weedwood_log", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockLogBetweenlands();
    public static final RegistryObject<Block> STRIPPED_WEEDWOOD_LOG = BLOCKS.register("stripped_weedwood_log", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockLogBetweenlands();
    public static final RegistryObject<Block> WEEDWOOD = BLOCKS.register("weedwood", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new Block(Material.WOOD).setHarvestLevel2("axe", 0).sound(SoundType.WOOD).setHardness(2.0F); // new BlockLogBetweenlands();
    public static final RegistryObject<Block> STRIPPED_WEEDWOOD = BLOCKS.register("stripped_weedwood", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockLogBetweenlands();
    public static final RegistryObject<Block> ROTTEN_LOG = BLOCKS.register("rotten_log", () -> new BlockRottenLog(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockRottenLog();
    public static final RegistryObject<Block> STRIPPED_ROTTEN_LOG = BLOCKS.register("stripped_rotten_log", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE)));
    public static final RegistryObject<Block> ROTTEN_WOOD = BLOCKS.register("rotten_wood", () -> new BlockRottenLog(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE)));
    public static final RegistryObject<Block> STRIPPED_ROTTEN_WOOD = BLOCKS.register("stripped_rotten_wood", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE)));
    public static final RegistryObject<Block> LOG_SPREADING_ROTTEN_BARK = BLOCKS.register("spreading_rotten_wood", () -> new BlockSpreadingRottenLog(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE)));
    public static final RegistryObject<Block> LOG_RUBBER = BLOCKS.register("rubber_log", () -> new BlockRubberLog(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockLogBetweenlands(); // new BlockRubberLog();
    public static final RegistryObject<Block> HEARTHGROVE_LOG = BLOCKS.register("hearthgrove_log", () -> new BlockHearthgroveLog(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockHearthgroveLog();
    public static final RegistryObject<Block> STRIPPED_HEARTHGROVE_LOG = BLOCKS.register("stripped_hearthgrove_log", () -> new BlockHearthgroveLog(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockHearthgroveLog();
    public static final RegistryObject<Block> HEARTHGROVE_WOOD = BLOCKS.register("hearthgrove_wood", () -> new BlockHearthgroveLog(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockHearthgroveLog();
    public static final RegistryObject<Block> STRIPPED_HEARTHGROVE_WOOD = BLOCKS.register("stripped_hearthgrove_wood", () -> new BlockHearthgroveLog(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockHearthgroveLog();
    public static final RegistryObject<Block> TARRED_HEARTHGROVE_LOG = BLOCKS.register("tarred_hearthgrove_log", () -> new BlockHearthgroveLog(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockHearthgroveLog();
    public static final RegistryObject<Block> TARRED_HEARTHGROVE_WOOD = BLOCKS.register("tarred_hearthgrove_wood", () -> new BlockHearthgroveLog(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockHearthgroveLog();
    public static final RegistryObject<Block> NIBBLETWIG_LOG = BLOCKS.register("nibbletwig_log", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockLogBetweenlands();
    public static final RegistryObject<Block> STRIPPED_NIBBLETWIG_LOG = BLOCKS.register("stripped_nibbletwig_log", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockLogBetweenlands();
    public static final RegistryObject<Block> NIBBLETWIG_WOOD = BLOCKS.register("nibbletwig_wood", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockLogBetweenlands();
    public static final RegistryObject<Block> STRIPPED_NIBBLETWIG_WOOD = BLOCKS.register("stripped_nibbletwig_wood", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockLogBetweenlands();
    public static final RegistryObject<Block> SPIRIT_TREE_LOG = BLOCKS.register("spirit_tree_log", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockLogBetweenlands();
    public static final RegistryObject<Block> STRIPPED_SPIRIT_TREE_LOG = BLOCKS.register("stripped_spirit_tree_log", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockLogBetweenlands();
    public static final RegistryObject<Block> SPIRIT_TREE_WOOD = BLOCKS.register("spirit_tree_wood", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockLogBetweenlands();
    public static final RegistryObject<Block> STRIPPED_SPIRIT_TREE_WOOD = BLOCKS.register("stripped_spirit_tree_wood", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockLogBetweenlands();
    public static final RegistryObject<Block> LOG_SAP = BLOCKS.register("sap_log", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F).harvestTool(ToolType.AXE))); // new BlockLogSap();
    public static final RegistryObject<Block> SAPLING_WEEDWOOD = BLOCKS.register("weedwood_sapling", () -> new BlockSaplingBetweenlands(new WeedwoodTree(), AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).randomTicks().instabreak())); // new BlockSaplingBetweenlands(new WorldGenWeedwoodTree());
    public static final RegistryObject<Block> SAPLING_SAP = BLOCKS.register("sap_sapling", () -> new BlockSaplingBetweenlands(new SapTree(), AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).randomTicks().instabreak())); // new BlockSaplingBetweenlands(new WorldGenSapTree());
    public static final RegistryObject<Block> SAPLING_RUBBER = BLOCKS.register("rubber_sapling", () -> new BlockSaplingBetweenlands(new RubberTree(), AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).randomTicks().instabreak())); // new BlockSaplingBetweenlands(new WorldGenRubberTree());
    public static final RegistryObject<Block> SAPLING_HEARTHGROVE = BLOCKS.register("hearthgrove_sapling", () -> new BlockSaplingBetweenlands(new HearthgroveTree(), AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).randomTicks().instabreak())); // new BlockSaplingBetweenlands(new HearthgroveTreeFeature());
    public static final RegistryObject<Block> SAPLING_NIBBLETWIG = BLOCKS.register("nibbletwig_sapling", () -> new BlockSaplingBetweenlands(new NibbletwigTree(), AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).randomTicks().instabreak())); // new BlockSaplingBetweenlands(new NibbletwigTreeFeature());
    public static final RegistryObject<Block> SAPLING_SPIRIT_TREE = BLOCKS.register("spirit_tree_sapling", () -> new BlockSaplingBetweenlands(new SpiritTree(), AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).randomTicks().instabreak())); // new BlockSaplingSpiritTree();
    public static final RegistryObject<Block> ROOT_POD = BLOCKS.register("root_pod", () -> new BlockSaplingBetweenlands(new PodRoots(), AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).randomTicks().instabreak())); // new BlockSaplingBetweenlands(new WorldGenRootPodRoots());
    public static final RegistryObject<Block> LEAVES_WEEDWOOD_TREE = BLOCKS.register("weedwood_leaves", () -> new BlockTintedLeaves(AbstractBlock.Properties.of(Material.LEAVES).sound(SoundType.GRASS).randomTicks().noOcclusion().strength(0.2F).harvestTool(ToolType.HOE)));
    public static final RegistryObject<Block> LEAVES_SAP_TREE = BLOCKS.register("sap_leaves", () -> new BlockTintedLeaves(AbstractBlock.Properties.of(Material.LEAVES).sound(SoundType.GRASS).randomTicks().noOcclusion().strength(0.2F).harvestTool(ToolType.HOE)));
    public static final RegistryObject<Block> LEAVES_RUBBER_TREE = BLOCKS.register("rubber_leaves", () -> new BlockTintedLeaves(AbstractBlock.Properties.of(Material.LEAVES).sound(SoundType.GRASS).randomTicks().noOcclusion().strength(0.2F).harvestTool(ToolType.HOE)));
    public static final RegistryObject<Block> LEAVES_HEARTHGROVE_TREE = BLOCKS.register("hearthgrove_leaves", () -> new BlockTintedLeaves(AbstractBlock.Properties.of(Material.LEAVES).sound(SoundType.GRASS).randomTicks().noOcclusion().strength(0.2F).harvestTool(ToolType.HOE)));
    public static final RegistryObject<Block> LEAVES_NIBBLETWIG_TREE = BLOCKS.register("nibbletwig_leaves", () -> new BlockTintedLeaves(AbstractBlock.Properties.of(Material.LEAVES).sound(SoundType.GRASS).randomTicks().noOcclusion().strength(0.2F).harvestTool(ToolType.HOE)));
    public static final RegistryObject<Block> LEAVES_SPIRIT_TREE_TOP = BLOCKS.register("spirit_tree_leaves_top", () -> new BlockLeavesSpiritTree(AbstractBlock.Properties.of(Material.LEAVES).sound(SoundType.GRASS).randomTicks().noOcclusion().strength(0.2F).harvestTool(ToolType.HOE))); // new BlockLeavesSpiritTree(BlockLeavesSpiritTree.Type.TOP);
    public static final RegistryObject<Block> LEAVES_SPIRIT_TREE_MIDDLE = BLOCKS.register("spirit_tree_leaves_middle", () -> new BlockLeavesSpiritTree(AbstractBlock.Properties.of(Material.LEAVES).sound(SoundType.GRASS).randomTicks().noOcclusion().strength(0.2F).harvestTool(ToolType.HOE))); // new BlockLeavesSpiritTree(BlockLeavesSpiritTree.Type.MIDDLE);
    public static final RegistryObject<Block> LEAVES_SPIRIT_TREE_BOTTOM = BLOCKS.register("spirit_tree_leaves_bottom", () -> new BlockLeavesSpiritTree(AbstractBlock.Properties.of(Material.LEAVES).sound(SoundType.GRASS).randomTicks().noOcclusion().strength(0.2F).harvestTool(ToolType.HOE))); // new BlockLeavesSpiritTree(BlockLeavesSpiritTree.Type.BOTTOM);
    //STRUCTURE
    public static final RegistryObject<Block> WEEDWOOD_PLANKS = BLOCKS.register("weedwood_planks", () -> new Block(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 5.0F).harvestTool(ToolType.AXE))); // new Block(Material.WOOD).sound(SoundType.WOOD).setHardness(2.0F).setResistance(5.0F);
    public static final RegistryObject<Block> RUBBER_TREE_PLANKS = BLOCKS.register("rubber_tree_planks", () -> new Block(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F).harvestTool(ToolType.AXE))); // new Block(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F);
    public static final RegistryObject<Block> GIANT_ROOT_PLANKS = BLOCKS.register("giant_root_planks", () -> new Block(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F).harvestTool(ToolType.AXE))); // new Block(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F);
    public static final RegistryObject<Block> HEARTHGROVE_PLANKS = BLOCKS.register("hearthgrove_planks", () -> new Block(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F).harvestTool(ToolType.AXE))); // new Block(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F);
    public static final RegistryObject<Block> NIBBLETWIG_PLANKS = BLOCKS.register("nibbletwig_planks", () -> new Block(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F).harvestTool(ToolType.AXE))); // new Block(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F);
	public static final RegistryObject<Block> ROTTEN_PLANKS = BLOCKS.register("rotten_planks", () -> new Block(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.5F, 4.0F)));
    public static final RegistryObject<Block> ANGRY_BETWEENSTONE = BLOCKS.register("angry_betweenstone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().lightLevel((ll) -> { return 12; }))); // new Block(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F).setLightLevel(0.8F);
    public static final RegistryObject<Block> BETWEENSTONE_BRICKS = BLOCKS.register("betweenstone_bricks", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F).harvestTool(ToolType.AXE))); // new Block(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F);
    public static final RegistryObject<Block> BETWEENSTONE_BRICKS_MIRAGE = BLOCKS.register("betweenstone_bricks_mirage", () -> new BlockGenericMirage(AbstractBlock.Properties.of(Material.PISTON).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> BETWEENSTONE_TILES = BLOCKS.register("betweenstone_tiles", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> BETWEENSTONE_CHISELED = BLOCKS.register("chiselled_betweenstone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRAGROCK_CHISELED = BLOCKS.register("chiselled_cragrock", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> LIMESTONE_CHISELED = BLOCKS.register("chiselled_limestone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> PITSTONE_CHISELED = BLOCKS.register("chiselled_pitstone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SCABYST_CHISELED_1 = BLOCKS.register("chiselled_scabyst_1", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SCABYST_CHISELED_2 = BLOCKS.register("chiselled_scabyst_2", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SCABYST_CHISELED_3 = BLOCKS.register("chiselled_scabyst_3", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SCABYST_PITSTONE_DOTTED = BLOCKS.register("dotted_scabyst_pitstone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SCABYST_PITSTONE_HORIZONTAL = BLOCKS.register("horizontal_scabyst_pitstone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SCABYST_BRICKS = BLOCKS.register("scabyst_bricks", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRACKED_BETWEENSTONE_BRICKS = BLOCKS.register("cracked_betweenstone_bricks", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRACKED_BETWEENSTONE_TILES = BLOCKS.register("cracked_betweenstone_tiles", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRACKED_LIMESTONE_BRICKS = BLOCKS.register("cracked_limestone_bricks", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRAGROCK_BRICKS = BLOCKS.register("cragrock_bricks", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRAGROCK_TILES = BLOCKS.register("cragrock_tiles", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> GLOWING_BETWEENSTONE_TILE = BLOCKS.register("glowing_betweenstone_tile", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F).lightLevel((ll) -> { return 13; })));
    public static final RegistryObject<Block> INACTIVE_GLOWING_SMOOTH_CRAGROCK = BLOCKS.register("inactive_glowing_smooth_cragrock", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> GLOWING_SMOOTH_CRAGROCK = BLOCKS.register(" glowing_smooth_cragrock", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F).lightLevel((ll) -> { return 13; })));
    public static final RegistryObject<Block> LIMESTONE_BRICKS = BLOCKS.register("limestone_bricks", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> LIMESTONE_TILES = BLOCKS.register("limestone_tiles", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MOSSY_BETWEENSTONE_BRICKS = BLOCKS.register("mossy_limestone_bricks", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MOSSY_BETWEENSTONE_TILES = BLOCKS.register("mossy_betweenstone_tiles", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MOSSY_LIMESTONE_BRICKS = BLOCKS.register("mossy_limestone_bricks", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MOSSY_SMOOTH_BETWEENSTONE = BLOCKS.register("mossy_smooth_betweenstone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICKS = BLOCKS.register("mud_bricks", () -> new BlockMudBricks(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_SHINGLES = BLOCKS.register("mud_brick_shingles", () -> new BlockMudBricks(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> RUBBER_BLOCK = BLOCKS.register("rubber_block", () -> new BlockBouncyBetweenlands(0.8F, AbstractBlock.Properties.of(Material.CLAY).sound(SoundType.SLIME_BLOCK).strength(1.0F)));
    public static final RegistryObject<Block> PITSTONE_BRICKS = BLOCKS.register("pitstone_bricks", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> PITSTONE_TILES = BLOCKS.register("pitstone_tiles", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> POLISHED_LIMESTONE = BLOCKS.register("polished_limestone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SMOOTH_BETWEENSTONE = BLOCKS.register("smooth_betweenstone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SMOOTH_CRAGROCK = BLOCKS.register("smooth_cragrock", () -> new BlockSmoothCragrock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> OCTINE_BLOCK = BLOCKS.register("octine_block", () -> new BlockOctine(AbstractBlock.Properties.of(Material.METAL).sound(SoundType.METAL).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SYRMORITE_BLOCK = BLOCKS.register("syrmorite_block", () -> new Block(AbstractBlock.Properties.of(Material.METAL).sound(SoundType.METAL).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> VALONITE_BLOCK = BLOCKS.register("valonite_block", () -> new Block(AbstractBlock.Properties.of(Material.METAL).sound(SoundType.METAL).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SCABYST_BLOCK = BLOCKS.register("scabyst_block", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> WEAK_BETWEENSTONE_TILES = BLOCKS.register("weak_betweenstone_tiles", () -> new BlockGenericCollapsing(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> WEAK_POLISHED_LIMESTONE = BLOCKS.register("weak_polished_limestone", () -> new BlockGenericCollapsing(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> WEAK_MOSSY_BETWEENSTONE_TILES = BLOCKS.register("weal_mossy_betweenstone_tiles", () -> new BlockGenericCollapsing(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> DENTROTHYST = BLOCKS.register("dentrothyst", () -> new BlockDentrothyst(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> LOOT_POT = BLOCKS.register("loot_pot", () -> new BlockLootPot(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.4F)));
    public static final RegistryObject<Block> MOB_SPAWNER = BLOCKS.register("mob_spawner", () -> new BlockMobSpawnerBetweenlands(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<Block> TEMPLE_PILLAR = BLOCKS.register("temple_pillar", () -> new BlockTemplePillar(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> BETWEENSTONE_PILLAR = BLOCKS.register("betweenstone_pillar", () -> new BlockTemplePillar(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> PITSTONE_PILLAR = BLOCKS.register("pitstone_pillar", () -> new BlockTemplePillar(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> LIMESTONE_PILLAR = BLOCKS.register("limestone_pillar", () -> new BlockTemplePillar(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRAGROCK_PILLAR = BLOCKS.register("cragrock_pillar", () -> new BlockTemplePillar(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> TAR_BEAST_SPAWNER = BLOCKS.register("tar_beast_spawner", () -> new BlockTarBeastSpawner(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(5.0F)));
    public static final RegistryObject<Block> TAR_LOOT_POT = BLOCKS.register("tar_loot_pit", () -> new BlockTarLootPot(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SULFUR_BLOCK = BLOCKS.register("sulfur_block", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> TEMPLE_BRICKS = BLOCKS.register("temple_bricks", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SMOOTH_PITSTONE = BLOCKS.register("smooth_pitstone", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MIRE_CORAL_BLOCK = BLOCKS.register("mire_coral_block", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F).lightLevel((ll) -> { return 15; })));
    public static final RegistryObject<Block> DEEP_WATER_CORAL_BLOCK = BLOCKS.register("deep_water_coral_block", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F).lightLevel((ll) -> { return 15; })));
    public static final RegistryObject<Block> SLIMY_BONE_BLOCK = BLOCKS.register("slimy_bone_block", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> AQUA_MIDDLE_GEM_BLOCK = BLOCKS.register("aqua_middle_gem_block", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F).lightLevel((ll) -> { return 14; })));
    public static final RegistryObject<Block> CRIMSON_MIDDLE_GEM_BLOCK = BLOCKS.register("crimson_middle_gem_block", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F).lightLevel((ll) -> { return 14; })));
    public static final RegistryObject<Block> GREEN_MIDDLE_GEM_BLOCK = BLOCKS.register("green_middle_gem_block", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F).lightLevel((ll) -> { return 14; })));
    public static final RegistryObject<Block> COMPOST_BLOCK = BLOCKS.register("compost_block", () -> new Block(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.5F, 10.0F)));
    public static final RegistryObject<Block> POLISHED_DENTROTHYST = BLOCKS.register("polished_dentrothyst", () -> new BlockPolishedDentrothyst(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.3F)));
    public static final RegistryObject<Block> SILT_GLASS = BLOCKS.register("silt_glass", () -> new BlockGlassBetweenlands(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.3F)));
    public static final RegistryObject<Block> SILT_GLASS_PANE = BLOCKS.register("silt_glass_pane", () -> new PaneBlock(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.3F)));
    public static final RegistryObject<Block> POLISHED_GREEN_DENTROTHYST_PANE = BLOCKS.register("polished_green_dentrothyst_pane", () -> new PaneBlock(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.3F)));
    public static final RegistryObject<Block> POLISHED_ORANGE_DENTROTHYST_PANE = BLOCKS.register("polished_orange_dentrothyst_pane", () -> new PaneBlock(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.3F)));
    public static final RegistryObject<Block> AMATE_PAPER_PANE_1 = BLOCKS.register("amate_paper_pane_1", () -> new PaneBlock(AbstractBlock.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.3F)));
    public static final RegistryObject<Block> AMATE_PAPER_PANE_2 = BLOCKS.register("amate_paper_pane_2", () -> new PaneBlock(AbstractBlock.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.3F)));
    public static final RegistryObject<Block> AMATE_PAPER_PANE_3 = BLOCKS.register("amate_paper_pane_3", () -> new PaneBlock(AbstractBlock.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.3F)));
    public static final RegistryObject<Block> SPIKE_TRAP = BLOCKS.register("spike_trap", () -> new BlockSpikeTrap(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
    public static final RegistryObject<Block> POSSESSED_BLOCK = BLOCKS.register("possessed_block", () -> new BlockPossessedBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
    public static final RegistryObject<Block> ITEM_CAGE = BLOCKS.register("item_cage", () -> new BlockItemCage(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.STONE).strength(10.0F, 10.0F)));
    public static final RegistryObject<Block> ITEM_SHELF = BLOCKS.register("item_shelf", () -> new BlockItemShelf(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 2.0F)));
    public static final RegistryObject<Block> THATCH = BLOCKS.register("thatch", () -> new Block(AbstractBlock.Properties.of(Material.GRASS).sound(SoundType.GRASS).strength(0.5F)));
    
    //Stairs
    public static final RegistryObject<Block> CRAGROCK_STAIRS = BLOCKS.register("cragrock_stairs", () -> new StairsBlock(() -> CRAGROCK.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> PITSTONE_STAIRS = BLOCKS.register("pitstone_stairs", () -> new StairsBlock(() -> PITSTONE.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SMOOTH_PITSTONE_STAIRS = BLOCKS.register("smooth_pitstone_stairs", () -> new StairsBlock(() -> SMOOTH_PITSTONE.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> TAR_SOLID_STAIRS = BLOCKS.register("solid_tar_stairs", () -> new StairsBlock(() -> TAR_SOLID.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> BETWEENSTONE_STAIRS = BLOCKS.register("betweenstone_stairs", () -> new StairsBlock(() -> BETWEENSTONE.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> BETWEENSTONE_BRICK_STAIRS = BLOCKS.register("betweenstone_brick_stairs", () -> new StairsBlock(() -> BETWEENSTONE_BRICKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_STAIRS = BLOCKS.register("mud_brick_stairs", () -> new StairsBlock(() -> MUD_BRICKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRAGROCK_BRICK_STAIRS = BLOCKS.register("cragrock_brick_stairs", () -> new StairsBlock(() -> CRAGROCK_BRICKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> TEMPLE_BRICK_STAIRS = BLOCKS.register("temple_brick_stairs", () -> new StairsBlock(() -> TEMPLE_BRICKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> LIMESTONE_BRICK_STAIRS = BLOCKS.register("limestone_brick_stairs", () -> new StairsBlock(() -> LIMESTONE_BRICKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> PITSTONE_BRICK_STAIRS = BLOCKS.register("pitstone_brick_stairs", () -> new StairsBlock(() -> PITSTONE_BRICKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> LIMESTONE_STAIRS = BLOCKS.register("limestone_stairs", () -> new StairsBlock(() -> LIMESTONE.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SMOOTH_BETWEENSTONE_STAIRS = BLOCKS.register("smooth_betweenstone_stairs", () -> new StairsBlock(() -> SMOOTH_BETWEENSTONE.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SMOOTH_CRAGROCK_STAIRS = BLOCKS.register("smooth_cragrock_stairs", () -> new StairsBlock(() -> SMOOTH_CRAGROCK.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> POLISHED_LIMESTONE_STAIRS = BLOCKS.register("polished_limestone_stairs", () -> new StairsBlock(() -> POLISHED_LIMESTONE.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MOSSY_BETWEENSTONE_BRICK_STAIRS = BLOCKS.register("mossy_betweenstone_brick_stairs", () -> new StairsBlock(() -> MOSSY_BETWEENSTONE_BRICKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MOSSY_SMOOTH_BETWEENSTONE_STAIRS = BLOCKS.register("mossy_smooth_betweenstone_stairs", () -> new StairsBlock(() -> MOSSY_SMOOTH_BETWEENSTONE.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRACKED_BETWEENSTONE_BRICK_STAIRS = BLOCKS.register("cracked_betweenstone_brick_stairs", () -> new StairsBlock(() -> CRACKED_BETWEENSTONE_BRICKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SCABYST_BRICK_STAIRS = BLOCKS.register("scabyst_brick_stairs", () -> new StairsBlock(() -> SCABYST_BRICKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_SHINGLE_STAIRS = BLOCKS.register("mud_brick_shingle_stairs", () -> new StairsBlock(() -> MUD_BRICK_SHINGLES.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));

    public static final RegistryObject<Block> WEEDWOOD_PLANK_STAIRS = BLOCKS.register("weedwood_stairs", () -> new StairsBlock(() -> WEEDWOOD_PLANKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> RUBBER_TREE_PLANK_STAIRS = BLOCKS.register("rubber_tree_stairs", () -> new StairsBlock(() -> RUBBER_TREE_PLANKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> GIANT_ROOT_PLANK_STAIRS = BLOCKS.register("giant_root_stairs", () -> new StairsBlock(() -> GIANT_ROOT_PLANKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> HEARTHGROVE_PLANK_STAIRS = BLOCKS.register("hearthgrove_stairs", () -> new StairsBlock(() -> HEARTHGROVE_PLANKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> NIBBLETWIG_PLANK_STAIRS = BLOCKS.register("nibbletwig_stairs", () -> new StairsBlock(() -> NIBBLETWIG_PLANKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
	public static final RegistryObject<Block> ROTTEN_PLANK_STAIRS = BLOCKS.register("rotten_stairs", () -> new StairsBlock(() -> ROTTEN_PLANKS.get().defaultBlockState(), AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.5F, 4.0F)));
    
    //Slabs
    public static final RegistryObject<Block> CRAGROCK_SLAB = BLOCKS.register("cragrock_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> PITSTONE_SLAB = BLOCKS.register("pitstone_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SMOOTH_PITSTONE_SLAB = BLOCKS.register("smooth_pitstone_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> TAR_SOLID_SLAB = BLOCKS.register("solid_tar_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> BETWEENSTONE_SLAB = BLOCKS.register("betweenstone_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> BETWEENSTONE_BRICK_SLAB = BLOCKS.register("betweenstone_brick_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_SLAB = BLOCKS.register("mud_brick_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRAGROCK_BRICK_SLAB = BLOCKS.register("cragrock_rick_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> TEMPLE_BRICK_SLAB = BLOCKS.register("temple_brick_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> LIMESTONE_BRICK_SLAB = BLOCKS.register("limestone_brick_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> LIMESTONE_SLAB = BLOCKS.register("limestone_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SMOOTH_BETWEENSTONE_SLAB = BLOCKS.register("smooth_betweenstone_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SMOOTH_CRAGROCK_SLAB = BLOCKS.register("smooth_cragrock_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> POLISHED_LIMESTONE_SLAB = BLOCKS.register("polished_limestone_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> PITSTONE_BRICK_SLAB = BLOCKS.register("pitstone_brick_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MOSSY_BETWEENSTONE_BRICK_SLAB = BLOCKS.register("mossy_betweenstone_brick_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MOSSY_SMOOTH_BETWEENSTONE_SLAB = BLOCKS.register("mossy_smooth_betweenstone_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRACKED_BETWEENSTONE_BRICK_SLAB = BLOCKS.register("cracked_betweenstone_brick_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SCABYST_BRICK_SLAB = BLOCKS.register("scabyst_brick_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_SHINGLE_SLAB = BLOCKS.register("mud_brick_shingle_slab", () -> new BlockMudBrickShingleSlab(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> THATCH_SLAB = BLOCKS.register("thatch_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.5F)));

    public static final RegistryObject<Block> WEEDWOOD_PLANK_SLAB = BLOCKS.register("weedwood_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> RUBBER_TREE_PLANK_SLAB = BLOCKS.register("rubber_tree_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> GIANT_ROOT_PLANK_SLAB = BLOCKS.register("giant_root_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> HEARTHGROVE_PLANK_SLAB = BLOCKS.register("hearthgrove_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> NIBBLETWIG_PLANK_SLAB = BLOCKS.register("nibbletwig_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
	public static final RegistryObject<Block> ROTTEN_PLANK_SLAB = BLOCKS.register("rotten_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.5F, 4.0F)));
    
    //Walls
    public static final RegistryObject<Block> PITSTONE_WALL = BLOCKS.register("pitstone_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> BETWEENSTONE_WALL = BLOCKS.register("betweenstone_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> TAR_SOLID_WALL = BLOCKS.register("solid_tar_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> TEMPLE_BRICK_WALL = BLOCKS.register("temple_brick_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SMOOTH_PITSTONE_WALL = BLOCKS.register("smooth_pitstone_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> BETWEENSTONE_BRICK_WALL = BLOCKS.register("betweenstone_brick_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_WALL = BLOCKS.register("mud_brick_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRAGROCK_WALL = BLOCKS.register("cragrock_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRAGROCK_BRICK_WALL = BLOCKS.register("cragrock_brick_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> LIMESTONE_BRICK_WALL = BLOCKS.register("limestone_brick_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> LIMESTONE_WALL = BLOCKS.register("limestone_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> POLISHED_LIMESTONE_WALL = BLOCKS.register("polished_limestone_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> PITSTONE_BRICK_WALL = BLOCKS.register("pitstone_brick_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SMOOTH_BETWEENSTONE_WALL = BLOCKS.register("smooth_betweenstone_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SMOOTH_CRAGROCK_WALL = BLOCKS.register("smooth_cragrock_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MOSSY_BETWEENSTONE_BRICK_WALL = BLOCKS.register("mossy_betweenstone_brick_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MOSSY_SMOOTH_BETWEENSTONE_WALL = BLOCKS.register("mossy_smooth_betweenstone_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRACKED_BETWEENSTONE_BRICK_WALL = BLOCKS.register("cracked_betweenstone_brick_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SCABYST_BRICK_WALL = BLOCKS.register("scabyst_brick_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_SHINGLE_WALL = BLOCKS.register("mud_brick_shingle_wall", () -> new WallBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    
    //Fences
    public static final RegistryObject<Block> WEEDWOOD_PLANK_FENCE = BLOCKS.register("weedwood_fence", () -> new FenceBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> WEEDWOOD_LOG_FENCE = BLOCKS.register("weedwood_log_fence", () -> new FenceBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> RUBBER_TREE_PLANK_FENCE = BLOCKS.register("rubber_tree_fence", () -> new FenceBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> GIANT_ROOT_PLANK_FENCE = BLOCKS.register("giant_root_fence", () -> new FenceBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> HEARTHGROVE_PLANK_FENCE = BLOCKS.register("hearthgrove_fence", () -> new FenceBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> NIBBLETWIG_PLANK_FENCE = BLOCKS.register("nibbletwig_fence", () -> new FenceBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> ROTTEN_PLANK_FENCE = BLOCKS.register("rotten_fence", () -> new FenceBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    
    //Fence Gates
    public static final RegistryObject<Block> WEEDWOOD_PLANK_FENCE_GATE = BLOCKS.register("weedwood_fence_gate", () -> new FenceGateBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> WEEDWOOD_LOG_FENCE_GATE = BLOCKS.register("weedwood_log_fence_gate", () -> new FenceGateBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> RUBBER_TREE_PLANK_FENCE_GATE = BLOCKS.register("rubber_tree_fence_gate", () -> new FenceGateBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> GIANT_ROOT_PLANK_FENCE_GATE = BLOCKS.register("giant_root_fence_gate", () -> new FenceGateBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> HEARTHGROVE_PLANK_FENCE_GATE = BLOCKS.register("hearthgrove_fence_gate", () -> new FenceGateBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> NIBBLETWIG_PLANK_FENCE_GATE = BLOCKS.register("nibbletwig_fence_gate", () -> new FenceGateBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> ROTTEN_PLANK_FENCE_GATE = BLOCKS.register("rotten_fence_gate", () -> new FenceGateBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    
    //Pressure Plates
    public static final RegistryObject<Block> WEEDWOOD_PLANK_PRESSURE_PLATE = BLOCKS.register("weedwood_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> WEEDWOOD_LOG_PRESSURE_PLATE = BLOCKS.register("weedwood_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> RUBBER_TREE_PLANK_PRESSURE_PLATE = BLOCKS.register("rubber_tree_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> GIANT_ROOT_PLANK_PRESSURE_PLATE = BLOCKS.register("giant_root_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> HEARTHGROVE_PLANK_PRESSURE_PLATE = BLOCKS.register("hearthgrove_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> NIBBLETWIG_PLANK_PRESSURE_PLATE = BLOCKS.register("nibbletwig_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> ROTTEN_PLANK_PRESSURE_PLATE = BLOCKS.register("rotten_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> BETWEENSTONE_PRESSURE_PLATE = BLOCKS.register("betweenstone_pressure_plate", () -> new PressurePlateBlock(Sensitivity.MOBS , AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(0.5F)));
    public static final RegistryObject<Block> SYRMORITE_PRESSURE_PLATE = BLOCKS.register("syrmorite_pressure_plate", () -> new SyrmoritePressurePlateBlock(AbstractBlock.Properties.of(Material.METAL).sound(SoundType.METAL).strength(0.5F)));
    
    //Buttons
    public static final RegistryObject<Block> WEEDWOOD_PLANK_BUTTON = BLOCKS.register("weedwood_button", () -> new WoodButtonBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> RUBBER_TREE_PLANK_BUTTON = BLOCKS.register("rubber_tree_button", () -> new WoodButtonBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> GIANT_ROOT_PLANK_BUTTON = BLOCKS.register("giant_root_button", () -> new WoodButtonBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> HEARTHGROVE_PLANK_BUTTON = BLOCKS.register("hearthgrove_button", () -> new WoodButtonBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> NIBBLETWIG_PLANK_BUTTON = BLOCKS.register("nibbletwig_button", () -> new WoodButtonBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> ROTTEN_PLANK_BUTTON = BLOCKS.register("rotten_button", () -> new WoodButtonBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> BETWEENSTONE_BUTTON = BLOCKS.register("betweenstone_button", () -> new StoneButtonBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(0.5F)));
    
    public static final RegistryObject<Block> WEEDWOOD_LADDER = BLOCKS.register("weedwood_ladder", () -> new LadderBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.4F)));
    public static final RegistryObject<Block> WEEDWOOD_LEVER = BLOCKS.register("weedwood_lever", () -> new LeverBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    
    //Worm Dungeon
    public static final RegistryObject<Block> VERTICAL_WORM_DUNGEON_PILLAR = BLOCKS.register("vertical_worm_dungeon_pillar", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> VERTICAL_WORM_DUNGEON_PILLAR_DECAY_1 = BLOCKS.register("vertical_worm_dungeon_pillar_decay_1", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> VERTICAL_WORM_DUNGEON_PILLAR_DECAY_2 = BLOCKS.register("vertical_worm_dungeon_pillar_decay_2", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> VERTICAL_WORM_DUNGEON_PILLAR_DECAY_3 = BLOCKS.register("vertical_worm_dungeon_pillar_decay_3", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> VERTICAL_WORM_DUNGEON_PILLAR_DECAY_4 = BLOCKS.register("vertical_worm_dungeon_pillar_decay_4", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> VERTICAL_WORM_DUNGEON_PILLAR_DECAY_FULL = BLOCKS.register("vertical_worm_dungeon_pillar_decay_full", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> TOP_WORM_DUNGEON_PILLAR = BLOCKS.register("top_worm_dungeon_pillar", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> TOP_WORM_DUNGEON_PILLAR_DECAY_1 = BLOCKS.register("top_worm_dungeon_pillar_decay_1", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> TOP_WORM_DUNGEON_PILLAR_DECAY_2 = BLOCKS.register("top_worm_dungeon_pillar_decay_2", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> TOP_WORM_DUNGEON_PILLAR_DECAY_3 = BLOCKS.register("top_worm_dungeon_pillar_decay_3", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> TOP_WORM_DUNGEON_PILLAR_DECAY_4 = BLOCKS.register("top_worm_dungeon_pillar_decay_4", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> TOP_WORM_DUNGEON_PILLAR_DECAY_FULL = BLOCKS.register("top_worm_dungeon_pillar_decay_full", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> COMPACTED_MUD = BLOCKS.register("compacted_mud", () -> new BlockCompactedMud(AbstractBlock.Properties.of(Material.DIRT).sound(SoundType.GRAVEL).strength(1.5F, 10.0F).harvestTool(ToolType.SHOVEL)));
    public static final RegistryObject<Block> MUD_TILES = BLOCKS.register("mud_tiles", () -> new BlockMudTiles(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRACKED_MUD_TILES = BLOCKS.register("cracked_mud_tiles", () -> new BlockMudTiles(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_TILES_DECAY = BLOCKS.register("mud_tiles_decay", () -> new BlockMudTiles(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CRACKED_MUD_TILES_DECAY = BLOCKS.register("cracked_mud_tiles_decay", () -> new BlockMudTiles(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> PUFFSHROOM = BLOCKS.register("puffshroom", () -> new BlockPuffshroom(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(8.0F)));
    public static final RegistryObject<Block> MUD_BRICKS_DECAY_1 = BLOCKS.register("mud_bricks_decay_1", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICKS_DECAY_2 = BLOCKS.register("mud_bricks_decay_2", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICKS_DECAY_3 = BLOCKS.register("mud_bricks_decay_3", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICKS_DECAY_4 = BLOCKS.register("mud_bricks_decay_4", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CARVED_MUD_BRICKS = BLOCKS.register("carved_mud_bricks", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CARVED_MUD_BRICKS_DECAY_1 = BLOCKS.register("carved_mud_bricks_decay_1", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CARVED_MUD_BRICKS_DECAY_2 = BLOCKS.register("carved_mud_bricks_decay_2", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CARVED_MUD_BRICKS_DECAY_3 = BLOCKS.register("carved_mud_bricks_decay_3", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CARVED_MUD_BRICKS_DECAY_4 = BLOCKS.register("carved_mud_bricks_decay_4", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CARVED_MUD_BRICKS_EDGE = BLOCKS.register("carved_mud_bricks_edge", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CARVED_MUD_BRICKS_EDGE_DECAY_1 = BLOCKS.register("carved_mud_bricks_edge_decay_1", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CARVED_MUD_BRICKS_EDGE_DECAY_2 = BLOCKS.register("carved_mud_bricks_edge_decay_2", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CARVED_MUD_BRICKS_EDGE_DECAY_3 = BLOCKS.register("carved_mud_bricks_edge_decay_3", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> CARVED_MUD_BRICKS_EDGE_DECAY_4 = BLOCKS.register("carved_mud_bricks_edge_decay_4", () -> new Block(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_STAIRS_DECAY_1 = BLOCKS.register("mud_brick_stairs_decay_1", () -> new StairsBlock(() -> MUD_BRICKS_DECAY_1.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_STAIRS_DECAY_2 = BLOCKS.register("mud_brick_stairs_decay_2", () -> new StairsBlock(() -> MUD_BRICKS_DECAY_2.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_STAIRS_DECAY_3 = BLOCKS.register("mud_brick_stairs_decay_3", () -> new StairsBlock(() -> MUD_BRICKS_DECAY_3.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_STAIRS_DECAY_4 = BLOCKS.register("mud_brick_stairs_decay_4", () -> new StairsBlock(() -> MUD_BRICKS_DECAY_4.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_SLAB_DECAY_1 = BLOCKS.register("mud_brick_slab_decay_1", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_SLAB_DECAY_2 = BLOCKS.register("mud_brick_slab_decay_2", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_SLAB_DECAY_3 = BLOCKS.register("mud_brick_slab_decay_3", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> MUD_BRICK_SLAB_DECAY_4 = BLOCKS.register("mud_brick_slab_decay_4", () -> new SlabBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> EDGE_SHROOM = BLOCKS.register("edge_shroom", () -> new BlockEdgePlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.1F)).setSickleDrop(new ItemStack(ItemRegistry.EDGE_SHROOM_GILLS.get())));
    public static final RegistryObject<Block> EDGE_MOSS = BLOCKS.register("edge_moss", () -> new BlockEdgePlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.1F)).setSickleDrop(new ItemStack(ItemRegistry.EDGE_MOSS_CLUMP.get())));
    public static final RegistryObject<Block> EDGE_LEAF = BLOCKS.register("edge_leaf", () -> new BlockEdgePlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.1F)).setSickleDrop(new ItemStack(ItemRegistry.EDGE_LEAF.get())));
    public static final RegistryObject<Block> MUD_TOWER_BEAM_ORIGIN = BLOCKS.register("mud_tower_beam_origin", () -> new BlockBeamOrigin(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
    public static final RegistryObject<Block> MUD_TOWER_BEAM_RELAY = BLOCKS.register("mud_tower_beam_relay", () -> new BlockBeamRelay(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
    public static final RegistryObject<Block> MUD_TOWER_BEAM_TUBE = BLOCKS.register("mud_tower_beam_tube", () -> new BlockBeamTube(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
    public static final RegistryObject<Block> MUD_TOWER_BEAM_LENS_SUPPORTS = BLOCKS.register("mud_tower_beam_lens_support", () -> new BlockBeamLensSupport(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
    public static final RegistryObject<Block> MUD_BRICK_ALCOVE = BLOCKS.register("mud_brick_alcove", () -> new BlockMudBrickAlcove(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(0.4F)));
    public static final RegistryObject<Block> LOOT_URN_1 = BLOCKS.register("loot_urn_1", () -> new BlockLootUrn(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.4F)));
    public static final RegistryObject<Block> LOOT_URN_2 = BLOCKS.register("loot_urn_2", () -> new BlockLootUrn(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.4F)));
    public static final RegistryObject<Block> LOOT_URN_3 = BLOCKS.register("loot_urn_3", () -> new BlockLootUrn(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.4F)));
	public static final RegistryObject<Block> DUNGEON_DOOR_RUNES = BLOCKS.register("dungeon_door_runes", () -> new BlockDungeonDoorRunes(false, false, AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(-1.0F, 2000.0F)));
	public static final RegistryObject<Block> DUNGEON_DOOR_RUNES_MIMIC = BLOCKS.register("dungeon_door_runes_mimic", () -> new BlockDungeonDoorRunes(true, true, AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(-1.0F, 2000.0F)));
	public static final RegistryObject<Block> DUNGEON_DOOR_RUNES_CRAWLER = BLOCKS.register("dungeon_door_runes_crawler", () -> new BlockDungeonDoorRunes(true, false, AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(-1.0F, 2000.0F)));
	public static final RegistryObject<Block> DUNGEON_DOOR_COMBINATION = BLOCKS.register("dungeon_door_combination", () -> new BlockDungeonDoorCombination(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(0.4F)));
	public static final RegistryObject<Block> MUD_BRICKS_CLIMBABLE = BLOCKS.register("climbable_mud_bricks", () -> new BlockMudBricksClimbable(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(0.4F)));
	public static final RegistryObject<Block> MUD_TILES_WATER = BLOCKS.register("mud_tiles_water", () -> new BlockMudTilesWater(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
	public static final RegistryObject<Block> DUNGEON_WALL_CANDLE = BLOCKS.register("dungeon_wall_candle", () -> new BlockDungeonWallCandle(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(0.1F)));
	public static final RegistryObject<Block> WOODEN_SUPPORT_BEAM_ROTTEN_1 = BLOCKS.register("rotten_wooden_support_beam_1", () -> new BlockWoodenSupportBeam(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> WOODEN_SUPPORT_BEAM_ROTTEN_2 = BLOCKS.register("rotten_wooden_support_beam_2", () -> new BlockWoodenSupportBeam(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> WOODEN_SUPPORT_BEAM_ROTTEN_3 = BLOCKS.register("rotten_wooden_support_beam_3", () -> new BlockWoodenSupportBeam(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_1 = BLOCKS.register("carved_rotten_bark_log_1", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_2 = BLOCKS.register("carved_rotten_bark_log_2", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_3 = BLOCKS.register("carved_rotten_bark_log_3", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_4 = BLOCKS.register("carved_rotten_bark_log_4", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_5 = BLOCKS.register("carved_rotten_bark_log_5", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_6 = BLOCKS.register("carved_rotten_bark_log_6", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_7 = BLOCKS.register("carved_rotten_bark_log_7", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_8 = BLOCKS.register("carved_rotten_bark_log_8", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_9 = BLOCKS.register("carved_rotten_bark_log_9", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_10 = BLOCKS.register("carved_rotten_bark_log_10", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_11 = BLOCKS.register("carved_rotten_bark_log_11", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_12 = BLOCKS.register("carved_rotten_bark_log_12", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_13 = BLOCKS.register("carved_rotten_bark_log_13", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_14 = BLOCKS.register("carved_rotten_bark_log_14", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_15 = BLOCKS.register("carved_rotten_bark_log_15", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> LOG_ROTTEN_BARK_CARVED_16 = BLOCKS.register("carved_rotten_bark_log_16", () -> new BlockRottenBarkCarved(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
	public static final RegistryObject<Block> ENERGY_BARRIER_MUD = BLOCKS.register("mud_energy_barrier", () -> new BlockEnergyBarrierMud(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(-1.0F, 6000000.0F).lightLevel((ll) -> { return 12; } )));
	public static final RegistryObject<Block> MUD_BRICK_SPIKE_TRAP = BLOCKS.register("mud_brick_spike_trap", () -> new BlockMudBrickSpikeTrap(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
	public static final RegistryObject<Block> MUD_TILES_SPIKE_TRAP = BLOCKS.register("mud_tiles_spike_trap", () -> new BlockMudTilesSpikeTrap(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
	public static final RegistryObject<Block> COMPACTED_MUD_SLOPE = BLOCKS.register("compacted_mud_slope", () -> new BlockCompactedMudSlope(AbstractBlock.Properties.of(Material.DIRT).sound(SoundType.GRAVEL).strength(1.0F)));
	public static final RegistryObject<Block> COMPACTED_MUD_SLAB = BLOCKS.register("compacted_mud_slab", () -> new SlabBlock(AbstractBlock.Properties.of(Material.DIRT).sound(SoundType.GRAVEL).strength(1.0F)));
	public static final RegistryObject<Block> COMPACTED_MUD_MIRAGE = BLOCKS.register("compacted_mud_mirage", () -> new BlockGenericMirage(AbstractBlock.Properties.of(Material.PISTON).sound(SoundType.GRAVEL).strength(1.5F, 10.0F)));
	public static final RegistryObject<Block> DECAY_PIT_CONTROL = BLOCKS.register("decay_pit_control", () -> new BlockDecayPitControl(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(-1.0F, 2000.0F)));


    public static final RegistryObject<Block> MUD_TOWER_BRAZIER = BLOCKS.register("mud_tower_brazier", () -> new BlockBrazier(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
	public static final RegistryObject<Block> DECAY_PIT_HANGING_CHAIN = BLOCKS.register("decay_pit_hanging_chain", () -> new BlockDecayPitHangingChain(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(-1.0F, 2000.0F)));
	public static final RegistryObject<Block> DECAY_PIT_GROUND_CHAIN = BLOCKS.register("decay_pit_ground_chain", () -> new BlockDecayPitGroundChain(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
	public static final RegistryObject<Block> DECAY_PIT_INVISIBLE_FLOOR_BLOCK = BLOCKS.register("decay_pit_invisible_floor_block", () -> new BlockDecayPitInvisibleFloorBlock(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
	public static final RegistryObject<Block> DECAY_PIT_INVISIBLE_FLOOR_BLOCK_R_1 = BLOCKS.register("decay_pit_invisible_floor_block_r1", () -> new BlockDecayPitInvisibleFloorBlockR1(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
	public static final RegistryObject<Block> DECAY_PIT_INVISIBLE_FLOOR_BLOCK_R_2 = BLOCKS.register("decay_pit_invisible_floor_block_r2", () -> new BlockDecayPitInvisibleFloorBlockR2(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
	public static final RegistryObject<Block> DECAY_PIT_INVISIBLE_FLOOR_BLOCK_L_1 = BLOCKS.register("decay_pit_invisible_floor_block_l1", () -> new BlockDecayPitInvisibleFloorBlockL1(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
	public static final RegistryObject<Block> DECAY_PIT_INVISIBLE_FLOOR_BLOCK_L_2 = BLOCKS.register("decay_pit_invisible_floor_block_l2", () -> new BlockDecayPitInvisibleFloorBlockL2(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
	public static final RegistryObject<Block> DECAY_PIT_INVISIBLE_FLOOR_BLOCK_DIAGONAL = BLOCKS.register("decay_pit_invisible_floor_block_diagonal", () -> new BlockDecayPitInvisibleFloorBlockDiagonal(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 2000.0F)));
	//Winter Event
    public static final RegistryObject<Block> PRESENT = BLOCKS.register("present", () -> new BlockPresent(AbstractBlock.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.8F)));
    
    //Plants
    public static final RegistryObject<Block> PITCHER_PLANT = BLOCKS.register("pitcher_plant", () -> new BlockDoublePlantBL(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.PITCHER_PLANT_TRAP.get())));
    public static final RegistryObject<Block> WEEPING_BLUE = BLOCKS.register("weeping_blue", () -> new BlockWeepingBlue(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()));
    public static final RegistryObject<Block> SUNDEW = BLOCKS.register("sundew", () -> new BlockSundew(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()));
    public static final RegistryObject<Block> BLACK_HAT_MUSHROOM = BLOCKS.register("black_hat_mushroom", () -> new BlockBlackHatMushroom(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()));
    public static final RegistryObject<Block> BULB_CAPPED_MUSHROOM = BLOCKS.register("bulb_capped_mushroom", () -> new BlockBulbCappedMushroom(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak().lightLevel((ll) -> {return 15; } )));
    public static final RegistryObject<Block> FLAT_HEAD_MUSHROOM = BLOCKS.register("flat_hear_mushroom", () -> new BlockFlatheadMushroom(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()));
    public static final RegistryObject<Block> VENUS_FLY_TRAP = BLOCKS.register("venus_fly_trap", () -> new BlockVenusFlyTrap(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.VENUS_FLY_TRAP.get())));
    public static final RegistryObject<Block> VOLARPAD = BLOCKS.register("volarpad", () -> new BlockDoublePlantBL(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.VOLARPAD.get())));
    public static final RegistryObject<Block> SWAMP_PLANT = BLOCKS.register("swamp_plant", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.GENERIC_LEAF.get())).setReplaceable(true));
    public static final RegistryObject<Block> SWAMP_KELP = BLOCKS.register("swamp_kelp", () -> new BlockSwampKelp(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.1F).lightLevel((ll) -> { return 3; } )));
    public static final RegistryObject<Block> MIRE_CORAL = BLOCKS.register("mire_coral", () -> new BlockPlantUnderwater(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak().lightLevel((ll) -> { return 15; })).setSickleDrop(new ItemStack(ItemRegistry.MIRE_CORAL.get())));
    public static final RegistryObject<Block> DEEP_WATER_CORAL = BLOCKS.register("deep_water_coral", () -> new BlockPlantUnderwater(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak().lightLevel((ll) -> { return 15; })).setSickleDrop(new ItemStack(ItemRegistry.DEEP_WATER_CORAL.get())));
    public static final RegistryObject<Block> WATER_WEEDS = BLOCKS.register("water_weeds", () -> new BlockWaterWeeds(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()));
    public static final RegistryObject<Block> BULB_CAPPED_MUSHROOM_CAP = BLOCKS.register("bulb_capped_mushroom_cap", () -> new BlockBulbCappedMushroomCap(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOL).strength(0.2F).lightLevel((ll) -> { return 15; } )));
    public static final RegistryObject<Block> BULB_CAPPED_MUSHROOM_STALK = BLOCKS.register("bulb_capped_mushroom_stalk", () -> new BlockBulbCappedMushroomStalk(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOL).strength(0.2F)));
    public static final RegistryObject<Block> SHELF_FUNGUS = BLOCKS.register("shelf_fungus", () -> new BlockShelfFungus(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOL).strength(0.2F)));
    public static final RegistryObject<Block> ALGAE = BLOCKS.register("algae", () -> new BlockAlgae(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.ALGAE.get())));
    public static final RegistryObject<Block> POISON_IVY = BLOCKS.register("poison_ivy", () -> new BlockPoisonIvy(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.2F)));
    public static final RegistryObject<Block> ROOT = BLOCKS.register("root", () -> new BlockRoot(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> ROOT_UNDERWATER = BLOCKS.register("underwater_root", () -> new BlockRootUnderwater(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> GIANT_ROOT = BLOCKS.register("giant_root", () -> new BlockGiantRoot(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()));
    public static final RegistryObject<Block> ARROW_ARUM = BLOCKS.register("arrow_arum", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.ARROW_ARUM_LEAF.get())));
    public static final RegistryObject<Block> BLUE_EYED_GRASS = BLOCKS.register("blue_eyed_grass", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.BLUE_EYED_GRASS_FLOWERS.get())));
    public static final RegistryObject<Block> BLUE_IRIS = BLOCKS.register("blue_iris", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.BLUE_IRIS_PETAL.get())));
    public static final RegistryObject<Block> BONESET = BLOCKS.register("boneset", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.BONESET_FLOWERS.get())));
    public static final RegistryObject<Block> BOTTLE_BRUSH_GRASS = BLOCKS.register("bottle_brush_grass", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.BOTTLE_BRUSH_GRASS_BLADES.get())));
    public static final RegistryObject<Block> BROOMSEDGE = BLOCKS.register("broomsedge", () -> new BlockDoublePlantBL(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.BROOM_SEDGE_LEAVES.get())).setReplaceable(true));
    public static final RegistryObject<Block> BUTTON_BUSH = BLOCKS.register("button_bush", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.BUTTON_BUSH_FLOWERS.get())));
    public static final RegistryObject<Block> CARDINAL_FLOWER = BLOCKS.register("cardinal_flower", () -> new BlockDoublePlantBL(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.CARDINAL_FLOWER_PETALS.get())));
    public static final RegistryObject<Block> CATTAIL = BLOCKS.register("cattail", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.CATTAIL_HEAD.get())));
    public static final RegistryObject<Block> CAVE_GRASS = BLOCKS.register("cave_grass", () -> new BlockCaveGrass(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.CAVE_GRASS_BLADES.get())).setReplaceable(true));
    public static final RegistryObject<Block> COPPER_IRIS = BLOCKS.register("copper_iris", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.COPPER_IRIS_PETALS.get())));
    public static final RegistryObject<Block> MARSH_HIBISCUS = BLOCKS.register("marsh_hibiscus", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.MARSH_HIBISCUS_FLOWER.get())));
    public static final RegistryObject<Block> MARSH_MALLOW = BLOCKS.register("marsh_mallow", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.MARSH_MALLOW_FLOWER.get())));
    public static final RegistryObject<Block> BLADDERWORT_FLOWER = BLOCKS.register("bladderwort_flower", () -> new BlockBladderwortFlower(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.BLADDERWORT_FLOWER.get())));
    public static final RegistryObject<Block> BLADDERWORT_STALK = BLOCKS.register("bladderwort_stalk", () -> new BlockBladderwortStalk(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.BLADDERWORT_STALK.get())));
    public static final RegistryObject<Block> BOG_BEAN_FLOWER = BLOCKS.register("bog_bean_flower", () -> new BlockBogBeanFlower(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.BOG_BEAN_FLOWER.get())));
    public static final RegistryObject<Block> BOG_BEAN_STALK = BLOCKS.register("bog_bean_stalk", () -> new BlockBogBeanStalk(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.BOG_BEAN_FLOWER.get())));
    public static final RegistryObject<Block> GOLDEN_CLUB_FLOWER = BLOCKS.register("golden_club_flower", () -> new BlockGoldenClubFlower(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.GOLDEN_CLUB_FLOWER.get())));
    public static final RegistryObject<Block> GOLDEN_CLUB_STALK = BLOCKS.register("golden_club_stalk", () -> new BlockGoldenClubStalk(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.GOLDEN_CLUB_FLOWER.get())));
    public static final RegistryObject<Block> MARSH_MARIGOLD_FLOWER = BLOCKS.register("marsh_marigold_flower", () -> new BlockMarshMarigoldFlower(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.MARSH_MARIGOLD_FLOWER.get())));
    public static final RegistryObject<Block> MARSH_MARIGOLD_STALK = BLOCKS.register("marsh_marigold_stalk", () -> new BlockMarshMarigoldStalk(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.MARSH_MARIGOLD_FLOWER.get())));
    public static final RegistryObject<Block> SWAMP_DOUBLE_TALLGRASS = BLOCKS.register("swamp_double_tallgrass", () -> new BlockDoublePlantBL(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.SWAMP_TALL_GRASS_BLADES.get())).setReplaceable(true));
    public static final RegistryObject<Block> MILKWEED = BLOCKS.register("milkweed", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.MILKWEED.get())));
    public static final RegistryObject<Block> NETTLE = BLOCKS.register("nettle", () -> new BlockNettle(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.NETTLE_LEAF.get())));
    public static final RegistryObject<Block> NETTLE_FLOWERED = BLOCKS.register("nettle_flowered", () -> new BlockNettleFlowered(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.NETTLE_LEAF.get())));
    public static final RegistryObject<Block> PICKEREL_WEED = BLOCKS.register("pickerel_weed", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.PICKEREL_WEED_FLOWER.get())));
    public static final RegistryObject<Block> PHRAGMITES = BLOCKS.register("phragmites", () -> new BlockPhragmites(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setReplaceable(true));
    public static final RegistryObject<Block> SHOOTS = BLOCKS.register("shoots", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.SHOOT_LEAVES.get())).setReplaceable(true));
    public static final RegistryObject<Block> SLUDGECREEP = BLOCKS.register("sludgecreep", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.SLUDGECREEP_LEAVES.get())).setReplaceable(true));
    public static final RegistryObject<Block> TALL_SLUDGECREEP = BLOCKS.register("tall_sludgecreep", () -> new BlockSludgeDungeonPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.SLUDGECREEP_LEAVES.get())).setReplaceable(true));
    public static final RegistryObject<Block> SOFT_RUSH = BLOCKS.register("soft_rush", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.SOFT_RUSH_LEAVES.get())).setReplaceable(true));
    public static final RegistryObject<Block> SWAMP_REED = BLOCKS.register("swamp_reed", () -> new BlockSwampReed(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()));
    public static final RegistryObject<Block> SWAMP_REED_UNDERWATER = BLOCKS.register("swamp_reed_underwater", () -> new BlockSwampReedUnderwater(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak())); //TODO: Merge this.
    public static final RegistryObject<Block> THORNS = BLOCKS.register("thorns", () -> new BlockThorns(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.2F)));
    public static final RegistryObject<Block> TALL_CATTAIL = BLOCKS.register("tall_cattail", () -> new BlockDoublePlantBL(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.CATTAIL_HEAD.get())));
    public static final RegistryObject<Block> SWAMP_TALLGRASS = BLOCKS.register("swamp_tallgrass", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.SWAMP_TALL_GRASS_BLADES.get())).setReplaceable(true));
    public static final RegistryObject<Block> DEAD_WEEDWOOD_BUSH = BLOCKS.register("dead_weedwood_bush", () -> new BlockPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.WEEDWOOD_STICK.get())));
    public static final RegistryObject<Block> WEEDWOOD_BUSH = BLOCKS.register("weedwood_bush", () -> new BlockWeedwoodBush(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()));
    public static final RegistryObject<Block> NESTING_BLOCK_STICKS = BLOCKS.register("nesting_block_sticks", () -> new BlockNesting(new ItemStack(ItemRegistry.WEEDWOOD_STICK.get()), AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
    public static final RegistryObject<Block> NESTING_BLOCK_BONES = BLOCKS.register("nesting_block_bones", () -> new BlockNesting(new ItemStack(ItemRegistry.SLIMY_BONE.get()), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> HOLLOW_LOG = BLOCKS.register("hollow_log", () -> new BlockHollowLog(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
    public static final RegistryObject<Block> CAVE_MOSS = BLOCKS.register("cave_moss", () -> new BlockCaveMoss(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()));
    public static final RegistryObject<Block> CRYPTWEED = BLOCKS.register("cryptweed", () -> new BlockSludgeDungeonHangingPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.CRYPTWEED_BLADES.get())));
    public static final RegistryObject<Block> STRING_ROOTS = BLOCKS.register("string_roots", () -> new BlockSludgeDungeonHangingPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.STRING_ROOT_FIBERS.get())));
    public static final RegistryObject<Block> PALE_GRASS = BLOCKS.register("pale_grass", () -> new BlockSludgeDungeonPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.PALE_GRASS_BLADES.get())).setReplaceable(true)); // BlockRenderLayer of Translucent.
    public static final RegistryObject<Block> ROTBULB = BLOCKS.register("rotbulb", () -> new BlockSludgeDungeonPlant(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak()).setSickleDrop(new ItemStack(ItemRegistry.ROTBULB_STALK.get())).setReplaceable(true));
    public static final RegistryObject<Block> MOSS = BLOCKS.register("moss", () -> new BlockMoss(true, AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.2F).randomTicks()).setSickleDrop(new ItemStack(ItemRegistry.MOSS.get())).setReplaceable(true));
    public static final RegistryObject<Block> DEAD_MOSS = BLOCKS.register("dead_moss", () -> new BlockMoss(false, AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.2F)).setSickleDrop(new ItemStack(ItemRegistry.MOSS.get())).setReplaceable(true));
    public static final RegistryObject<Block> LICHEN = BLOCKS.register("lichen", () -> new BlockLichen(true, AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.2F).randomTicks()).setSickleDrop(new ItemStack(ItemRegistry.LICHEN.get())).setReplaceable(true));
    public static final RegistryObject<Block> DEAD_LICHEN = BLOCKS.register("dead_lichen", () -> new BlockLichen(false, AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.2F)).setSickleDrop(new ItemStack(ItemRegistry.LICHEN.get())).setReplaceable(true));
    public static final RegistryObject<Block> HANGER = BLOCKS.register("hanger", () -> new BlockHanger(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.1F).randomTicks()));
    public static final RegistryObject<Block> MIDDLE_FRUIT_BUSH = BLOCKS.register("middle_fruit_bush", () -> new BlockMiddleFruitBush(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.1F).randomTicks()));
    public static final RegistryObject<Block> FUNGUS_CROP = BLOCKS.register("fungus_crop", () -> new BlockFungusCrop(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak().randomTicks()));
    public static final RegistryObject<Block> ASPECTRUS_CROP = BLOCKS.register("aspectrus_crop", () -> new BlockAspectrusCrop(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).instabreak().randomTicks()));
    public static final RegistryObject<Block> PURIFIED_SWAMP_DIRT = BLOCKS.register("purified_swamp_dirt", () -> new BlockSwampDirt(AbstractBlock.Properties.of(Material.DIRT).sound(SoundType.GRAVEL).strength(0.5F)));
    public static final RegistryObject<Block> DUG_SWAMP_DIRT = BLOCKS.register("dug_swamp_dirt", () -> new BlockDugDirt(false, AbstractBlock.Properties.of(Material.DIRT).sound(SoundType.GRAVEL).strength(0.5F)));
    public static final RegistryObject<Block> DUG_PURIFIED_SWAMP_DIRT = BLOCKS.register("dug_purified_swamp_dirt", () -> new BlockDugDirt(true, AbstractBlock.Properties.of(Material.DIRT).sound(SoundType.GRAVEL).strength(0.5F)));
    public static final RegistryObject<Block> DUG_SWAMP_GRASS = BLOCKS.register("dug_swamp_grass", () -> new BlockDugGrass(false, AbstractBlock.Properties.of(Material.DIRT).sound(SoundType.GRASS).strength(0.5F)));
    public static final RegistryObject<Block> DUG_PURIFIED_SWAMP_GRASS = BLOCKS.register("dug_purified_swamp_grass", () -> new BlockDugGrass(true, AbstractBlock.Properties.of(Material.DIRT).sound(SoundType.GRASS).strength(0.5F)));

    //Misc
    public static final RegistryObject<Block> LOG_PORTAL = BLOCKS.register("portal_log", () -> new BlockLogBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
    public static final RegistryObject<Block> TREE_PORTAL = BLOCKS.register("portal_tree", () -> new BlockTreePortal(AbstractBlock.Properties.of(Material.PORTAL).sound(SoundType.GLASS).strength(-1.0F, 10.0F).lightLevel((ll) -> { return 15; } )));
    public static final RegistryObject<Block> PORTAL_FRAME = BLOCKS.register("portal_frame", () -> new BlockPortalFrame(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
    public static final RegistryObject<Block> DRUID_ALTAR = BLOCKS.register("druid_altar", () -> new BlockDruidAltar(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(8.0F, 100.0F)));
    public static final RegistryObject<Block> PURIFIER = BLOCKS.register("purifier", () -> new BlockPurifier(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
    public static final RegistryObject<Block> WEEDWOOD_WORKBENCH = BLOCKS.register("weedwood_crafting_table", () -> new BlockWeedwoodWorkbench(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.5F)));
    public static final RegistryObject<Block> COMPOST_BIN = BLOCKS.register("compost_bin", () -> new BlockCompostBin(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> WEEDWOOD_JUKEBOX = BLOCKS.register("weedwood_jukebox", () -> new BlockWeedwoodJukebox(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 10.0F)));
    public static final RegistryObject<Block> SULFUR_FURNACE = BLOCKS.register("sulfur_furnace", () -> new BlockBLFurnace(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(3.5F)));
    public static final RegistryObject<Block> SULFUR_FURNACE_DUAL = BLOCKS.register("dual_sulfur_furnace", () -> new BlockBLDualFurnace(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(3.5F)));
    public static final RegistryObject<Block> WEEDWOOD_CHEST = BLOCKS.register("weedwood_chest", () -> new BlockChestBetweenlands(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
    public static final RegistryObject<Block> WEEDWOOD_RUBBER_TAP = BLOCKS.register("weedwood_rubber_tap", () -> new BlockRubberTap(540, AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F)));
    public static final RegistryObject<Block> SYRMORITE_RUBBER_TAP = BLOCKS.register("syrmorite_rubber_tap", () -> new BlockRubberTap(260, AbstractBlock.Properties.of(Material.METAL).sound(SoundType.METAL).strength(2.0F)));
    public static final RegistryObject<Block> SLUDGE = BLOCKS.register("sludge", () -> new BlockSludge(AbstractBlock.Properties.of(BLMaterialRegistry.SLUDGE).sound(SoundType.GRAVEL).strength(0.1F).harvestTool(ToolType.SHOVEL)));
    public static final RegistryObject<Block> FALLEN_LEAVES = BLOCKS.register("fallen_leaves", () -> new BlockFallenLeaves(AbstractBlock.Properties.of(Material.GRASS).sound(SoundType.GRASS).strength(0.1F).harvestTool(ToolType.HOE)));
    public static final RegistryObject<Block> ENERGY_BARRIER = BLOCKS.register("energy_barrier", () -> new BlockEnergyBarrier(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(-1.0F, 6000000.0F).lightLevel((ll) -> { return 12; } )));
    public static final RegistryObject<Block> DIAGONAL_ENERGY_BARRIER = BLOCKS.register("diagonal_energy_barrier", () -> new BlockDiagonalEnergyBarrier(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(-1.0F, 6000000.0F).lightLevel((ll) -> { return 12; } )));
    public static final RegistryObject<Block> WEEDWOOD_DOOR = BLOCKS.register("weedwood_door", () -> new DoorBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> RUBBER_TREE_DOOR = BLOCKS.register("rubber_tree_door", () -> new DoorBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> GIANT_ROOT_DOOR = BLOCKS.register("giant_root_door", () -> new DoorBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> HEARTHGROVE_DOOR = BLOCKS.register("hearthgrove_door", () -> new DoorBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> NIBBLETWIG_DOOR = BLOCKS.register("nibbletwig_door", () -> new DoorBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> SYRMORITE_DOOR = BLOCKS.register("syrmorite_door", () -> new DoorBlock(AbstractBlock.Properties.of(Material.METAL).sound(SoundType.METAL).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SCABYST_DOOR = BLOCKS.register("scabyst_door", () -> new DoorBlock(AbstractBlock.Properties.of(Material.METAL).sound(SoundType.METAL).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> STANDING_WEEDWOOD_SIGN = BLOCKS.register("standing_weedwood_sign", () -> new BLStandingSignBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F), BLWoodType.WEEDWOOD));
    public static final RegistryObject<Block> WALL_WEEDWOOD_SIGN = BLOCKS.register("wall_weedwood_sign", () -> new BLWallSignBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F), BLWoodType.WEEDWOOD));
    public static final RegistryObject<Block> STANDING_RUBBER_TREE_SIGN = BLOCKS.register("standing_rubber_tree_sign", () -> new BLStandingSignBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F), BLWoodType.RUBBER));
    public static final RegistryObject<Block> WALL_RUBBER_TREE_SIGN = BLOCKS.register("wall_rubber_tree_sign", () -> new BLWallSignBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F), BLWoodType.RUBBER));
    public static final RegistryObject<Block> STANDING_ROTTEN_SIGN = BLOCKS.register("standing_rotten_sign", () -> new BLStandingSignBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F), BLWoodType.ROTTEN));
    public static final RegistryObject<Block> WALL_ROTTEN_SIGN = BLOCKS.register("wall_rotten_sign", () -> new BLWallSignBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F), BLWoodType.ROTTEN));
    public static final RegistryObject<Block> SULFUR_TORCH = BLOCKS.register("sulfur_torch", () -> new SulfurTorchBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).instabreak().lightLevel((ll) -> { return 14; } ), null));
    public static final RegistryObject<Block> SULFUR_TORCH_EXTINGUISHED = BLOCKS.register("extinguished_sulfur_torch", () -> new ExtinguishedSulfurTorchBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).instabreak(), null));
    public static final RegistryObject<Block> WALL_SULFUR_TORCH = BLOCKS.register("wall_sulfur_torch", () -> new WallSulfurTorchBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).instabreak().lightLevel((ll) -> { return 14; } ), null));
    public static final RegistryObject<Block> WALL_SULFUR_TORCH_EXTINGUISHED = BLOCKS.register("wall_extinguished_sulfur_torch", () -> new WallExtinguishedSulfurTorchBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).instabreak(), null));
    public static final RegistryObject<Block> WEEDWOOD_TRAPDOOR = BLOCKS.register("weedwood_trapdoor", () -> new TrapDoorBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> RUBBER_TREE_PLANK_TRAPDOOR = BLOCKS.register("rubber_tree_trapdoor", () -> new TrapDoorBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> GIANT_ROOT_PLANK_TRAPDOOR = BLOCKS.register("giant_root_trapdoor", () -> new TrapDoorBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> HEARTHGROVE_PLANK_TRAPDOOR = BLOCKS.register("hearthgrove_trapdoor", () -> new TrapDoorBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> NIBBLETWIG_PLANK_TRAPDOOR = BLOCKS.register("nibbletwig_trapdoor", () -> new TrapDoorBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> SYRMORITE_TRAPDOOR = BLOCKS.register("syrmorite_trapdoor", () -> new TrapDoorBlock(AbstractBlock.Properties.of(Material.METAL).sound(SoundType.METAL).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> SCABYST_TRAPDOOR = BLOCKS.register("scabyst_trapdoor", () -> new TrapDoorBlock(AbstractBlock.Properties.of(Material.METAL).sound(SoundType.STONE).strength(1.75F, 5.0F)));
    public static final RegistryObject<Block> SYRMORITE_HOPPER = BLOCKS.register("syrmorite_hopper", () -> new BlockHopperBetweenlands(AbstractBlock.Properties.of(Material.METAL).sound(SoundType.METAL).strength(3.0F, 8.0F)));
    public static final RegistryObject<Block> MUD_FLOWER_POT = BLOCKS.register("mud_flower_pot", () -> new BlockMudFlowerPot(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(0.3F)));
    public static final RegistryObject<Block> MUD_FLOWER_POT_CANDLE = BLOCKS.register("mud_flower_pot_candle", () -> new BlockMudFlowerPotCandle(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(0.3F)));
    public static final RegistryObject<Block> GECKO_CAGE = BLOCKS.register("gecko_cage", () -> new BlockGeckoCage(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> INFUSER = BLOCKS.register("infuser", () -> new BlockInfuser(AbstractBlock.Properties.of(Material.METAL).sound(SoundType.METAL).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> GREEN_ASPECT_VIAL_BLOCK = BLOCKS.register("green_aspect_vial_block", () -> new BlockAspectVial(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.4F)));
    public static final RegistryObject<Block> ORANGE_ASPECT_VIAL_BLOCK = BLOCKS.register("orange_aspect_vial_block", () -> new BlockAspectVial(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.4F)));
    public static final RegistryObject<Block> MORTAR = BLOCKS.register("mortar", () -> new BlockMortar(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> CENSER = BLOCKS.register("censer", () -> new BlockCenser(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> WEEDWOOD_BARREL = BLOCKS.register("weedwood_barrel", () -> new LiquidBarrelBlock(false, AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> SYRMORITE_BARREL = BLOCKS.register("syrmorite_barrel", () -> new LiquidBarrelBlock(true, AbstractBlock.Properties.of(Material.METAL).sound(SoundType.METAL).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> ANIMATOR = BLOCKS.register("animator", () -> new BlockAnimator(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0F)));
    public static final RegistryObject<Block> ALEMBIC = BLOCKS.register("alembic", () -> new BlockAlembic(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0F, 5.0F)));
    public static final RegistryObject<Block> MOSS_BED = BLOCKS.register("moss_bed", () -> new BlockMossBed(DyeColor.GREEN, AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.2F)));
    public static final RegistryObject<Block> ROPE = BLOCKS.register("rope", () -> new BlockRope(AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> DAMP_TORCH = BLOCKS.register("damp_torch", () -> new DampTorchBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).instabreak().randomTicks(), null));
    public static final RegistryObject<Block> WALKWAY = BLOCKS.register("walkway", () -> new BlockWalkway(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F)));
    public static final RegistryObject<Block> WOOD_CHIP_PATH = BLOCKS.register("wood_chip_path", () -> new BlockChipPath(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<Block> THATCH_ROOF = BLOCKS.register("thatch_roof", () -> new BlockSlanted(THATCH.get().defaultBlockState(), AbstractBlock.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.5F)));
    public static final RegistryObject<Block> MUD_BRICK_ROOF = BLOCKS.register("mud_brick_roof", () -> new BlockSlanted(THATCH.get().defaultBlockState(), AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F)));
    public static final RegistryObject<Block> REPELLER = BLOCKS.register("repeller", () -> new BlockRepeller(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0F)));
    public static final RegistryObject<Block> RUNE_WEAVING_TABLE = BLOCKS.register("rune_weaving_table", () -> new BlockRuneWeavingTable(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.5F)));
    public static final RegistryObject<Block> RUNE_CARVING_TABLE = BLOCKS.register("rune_carving_table", () -> new BlockRuneCarvingTable(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.5F)));
    public static final RegistryObject<Block> SCRIVENER_SULFUR_MARK = BLOCKS.register("scrievener_sulfur_mark", () -> new BlockSulfurScrivenerMark(AbstractBlock.Properties.of(Material.PISTON).strength(0.5F)));
    public static final RegistryObject<Block> SCRIVENER_BURNT_MARK = BLOCKS.register("scrievener_burnt_mark", () -> new BlockBurntScrivenerMark(AbstractBlock.Properties.of(Material.PISTON).strength(0.5F)));
    public static final RegistryObject<Block> WAYSTONE = BLOCKS.register("waystone", () -> new BlockWaystone(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(25.0F, 10000.0F)));
    public static final RegistryObject<Block> CAVING_ROPE_LIGHT = BLOCKS.register("caving_rope_light", () -> new BlockCavingRopeLight(AbstractBlock.Properties.of(Material.AIR).randomTicks().lightLevel((ll) -> { return 6; } )));
    public static final RegistryObject<Block> GROUND_ITEM = BLOCKS.register("ground_item", () -> new BlockGroundItem(AbstractBlock.Properties.of(Material.DIRT).sound(SoundType.GRAVEL).strength(0.1F)));
    public static final RegistryObject<Block> SIMULACRUM_DEEPMAN = BLOCKS.register("", () -> new BlockSimulacrum(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 10000.0F).randomTicks()));
    public static final RegistryObject<Block> SIMULACRUM_LAKE_CAVERN = BLOCKS.register("", () -> new BlockSimulacrum(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10.0F, 10000.0F).randomTicks()));
    public static final RegistryObject<Block> SIMULACRUM_ROOTMAN = BLOCKS.register("", () -> new BlockSimulacrum(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(10.0F, 10000.0F).randomTicks()));
    public static final RegistryObject<Block> OFFERING_TABLE = BLOCKS.register("", () -> new BlockOfferingTable(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(0.5F)));
    public static final RegistryObject<Block> WIND_CHIME = BLOCKS.register("", () -> new BlockWindChime(AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(0.5F)));
    public static final RegistryObject<Block> LANTERN_PAPER_1 = BLOCKS.register("", () -> new BetweenlanternBlock(AbstractBlock.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.1F).lightLevel((ll) -> { return 15; } )));
    public static final RegistryObject<Block> LANTERN_PAPER_2 = BLOCKS.register("", () -> new BetweenlanternBlock(AbstractBlock.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.1F).lightLevel((ll) -> { return 15; } )));
    public static final RegistryObject<Block> LANTERN_PAPER_3 = BLOCKS.register("", () -> new BetweenlanternBlock(AbstractBlock.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.1F).lightLevel((ll) -> { return 15; } )));
    public static final RegistryObject<Block> LANTERN_SILT_GLASS = BLOCKS.register("", () -> new BlockLanternSiltGlass(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.GLASS).strength(0.1F).lightLevel((ll) -> { return 15; } )));
    public static final RegistryObject<Block> BAUBLE = BLOCKS.register("", () -> new BlockBauble(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.3F).lightLevel((ll) -> { return 15; } )));
    public static final RegistryObject<Block> BLACK_ICE = BLOCKS.register("black_ice", () -> new BlockBlackIce(AbstractBlock.Properties.of(Material.ICE).sound(SoundType.GLASS).strength(0.5F).friction(0.98F)));
    public static final RegistryObject<Block> BETWEENSNOW = BLOCKS.register("betweensnow", () -> new BlockSnowBetweenlands(AbstractBlock.Properties.of(Material.SNOW).sound(SoundType.SNOW).strength(0.1F).harvestTool(ToolType.SHOVEL)));
    
    //public static final Set<Block> BLOCKS = new LinkedHashSet<>();
    //public static final List<BlockItem> ITEM_BLOCKS = new ArrayList<BlockItem>();

    // The new, simpler way of registering blocks.
    @EventBusSubscriber(modid = TheBetweenlands.MOD_ID)
	public static class RegistrationHandler 
	{
		public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final String name) {
			return setup(entry, new ResourceLocation(TheBetweenlands.MOD_ID, name));
		}

		public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final ResourceLocation registryName) {
			entry.setRegistryName(registryName);
			return entry;
		}
		
	    @SubscribeEvent
	    public static void registerBlocks(final RegistryEvent.Register<Block> event) { 
	    	event.getRegistry().registerAll();
	    }
	}
    
    // Legacy code that should probably be removed...
    /*private BlockRegistry() {
    }

    public static void preInit() {
        try {
            for (Field field : BlockRegistry.class.getDeclaredFields()) {
                Object obj = field.get(null);
                if (obj instanceof Block) {
                    Block block = (Block) obj;
                    String name = field.getName().toLowerCase(Locale.ENGLISH);
                    registerBlock(name, block);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerBlock(String name, Block block) {
        BLOCKS.add(block);
        block.setRegistryName(TheBetweenlands.MOD_ID, name).setTranslationKey(TheBetweenlands.MOD_ID + "." + name);

        BlockItem item = null;
        if (block instanceof ICustomItemBlock)
            item = ((ICustomItemBlock) block).getItemBlock();
        else
            item = new BlockItem(block);
        if(item != null) {
        	ITEM_BLOCKS.add(item);
        	item.setRegistryName(TheBetweenlands.MOD_ID, name).setTranslationKey(TheBetweenlands.MOD_ID + "." + name);
        	
        	if (BetweenlandsConfig.DEBUG.debug && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                if (block.getCreativeTab() == null)
                    TheBetweenlands.logger.warn(String.format("Block %s doesn't have a creative tab", block.getTranslationKey()));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerBlockRenderers(ModelRegistryEvent event) {
        for (Block block : BlockRegistry.BLOCKS) {
            if (block instanceof IStateMappedBlock) {
                AdvancedStateMap.Builder builder = new AdvancedStateMap.Builder();
                ((IStateMappedBlock) block).setStateMapper(builder);
                ModelLoader.setCustomStateMapper(block, builder.build());
            }
            
            Item item = Item.getItemFromBlock(block);
            
            if(item != Items.AIR) {
	            if (block instanceof ICustomItemBlock) {
	                ICustomItemBlock customItemBlock = (ICustomItemBlock) block;
	                ItemStack renderedItem = customItemBlock.getRenderedItem();
	                if (!renderedItem.isEmpty()) {
	                	Map<Integer, ResourceLocation> map = TheBetweenlands.proxy.getItemModelMap(renderedItem.getItem());
	                	ModelResourceLocation model = (ModelResourceLocation) map.get(renderedItem.getMetadata());
	                    ModelLoader.setCustomModelResourceLocation(item, 0, model);
	                    continue;
	                }
	            }
	            ResourceLocation name = block.getRegistryName();
	            if (block instanceof ISubtypeItemBlockModelDefinition) {
	                ISubtypeItemBlockModelDefinition subtypeBlock = (ISubtypeItemBlockModelDefinition) block;
	                for (int i = 0; i < subtypeBlock.getSubtypeNumber(); i++) {
	                    int meta = subtypeBlock.getSubtypeMeta(i);
	                    ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name.getNamespace() + ":" + String.format(subtypeBlock.getSubtypeName(meta), name.getPath()), "inventory"));
	                }
	            } else {
	                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(name, "inventory"));
	            }
        	}
        }
    }

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();
        for (Block block : BLOCKS) {
            registry.register(block);
        }
    }*/
    

}



