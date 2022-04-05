package thebetweenlands.common.registries;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.tile.BLSignTileEntity;
import thebetweenlands.common.tile.TileEntityAlembic;
import thebetweenlands.common.tile.TileEntityAnimator;
import thebetweenlands.common.tile.TileEntityAspectVial;
import thebetweenlands.common.tile.TileEntityAspectrusCrop;
import thebetweenlands.common.tile.TileEntityBLDualFurnace;
import thebetweenlands.common.tile.TileEntityBLFurnace;
import thebetweenlands.common.tile.TileEntityBarrel;
import thebetweenlands.common.tile.BeamOriginTileEntity;
import thebetweenlands.common.tile.BeamRelayTileEntity;
import thebetweenlands.common.tile.TileEntityCenser;
import thebetweenlands.common.tile.TileEntityChestBetweenlands;
import thebetweenlands.common.tile.TileEntityCompostBin;
import thebetweenlands.common.tile.TileEntityDecayPitControl;
import thebetweenlands.common.tile.TileEntityDecayPitGroundChain;
import thebetweenlands.common.tile.TileEntityDecayPitHangingChain;
import thebetweenlands.common.tile.TileEntityDruidAltar;
import thebetweenlands.common.tile.TileEntityDugSoil;
import thebetweenlands.common.tile.TileEntityDungeonDoorCombination;
import thebetweenlands.common.tile.TileEntityDungeonDoorRunes;
import thebetweenlands.common.tile.TileEntityGeckoCage;
import thebetweenlands.common.tile.TileEntityGroundItem;
import thebetweenlands.common.tile.TileEntityHopperBetweenlands;
import thebetweenlands.common.tile.TileEntityInfuser;
import thebetweenlands.common.tile.TileEntityItemCage;
import thebetweenlands.common.tile.TileEntityItemShelf;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.tile.TileEntityLootUrn;
import thebetweenlands.common.tile.TileEntityMortar;
import thebetweenlands.common.tile.TileEntityMossBed;
import thebetweenlands.common.tile.TileEntityMudBrickAlcove;
import thebetweenlands.common.tile.TileEntityMudBricksSpikeTrap;
import thebetweenlands.common.tile.TileEntityMudFlowerPot;
import thebetweenlands.common.tile.TileEntityMudTilesSpikeTrap;
import thebetweenlands.common.tile.TileEntityOfferingTable;
import thebetweenlands.common.tile.TileEntityPossessedBlock;
import thebetweenlands.common.tile.TileEntityPresent;
import thebetweenlands.common.tile.TileEntityPuffshroom;
import thebetweenlands.common.tile.TileEntityPurifier;
import thebetweenlands.common.tile.TileEntityRepeller;
import thebetweenlands.common.tile.TileEntityRubberTap;
import thebetweenlands.common.tile.TileEntityRuneCarvingTable;
import thebetweenlands.common.tile.TileEntityRuneCarvingTableFiller;
import thebetweenlands.common.tile.TileEntityRuneWeavingTable;
import thebetweenlands.common.tile.TileEntityRuneWeavingTableFiller;
import thebetweenlands.common.tile.TileEntitySimulacrum;
import thebetweenlands.common.tile.TileEntitySpikeTrap;
import thebetweenlands.common.tile.TileEntityTarLootPot1;
import thebetweenlands.common.tile.TileEntityTarLootPot2;
import thebetweenlands.common.tile.TileEntityTarLootPot3;
import thebetweenlands.common.tile.TileEntityWaystone;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;
import thebetweenlands.common.tile.TileEntityWindChime;
import thebetweenlands.common.tile.TileEntityWisp;
import thebetweenlands.common.tile.spawner.TileEntityMobSpawnerBetweenlands;
import thebetweenlands.common.tile.spawner.TileEntityTarBeastSpawner;

public class TileEntityRegistry {
	
	private TileEntityRegistry() { }

	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, TheBetweenlands.MOD_ID);
	
	public static RegistryObject<TileEntityType<TileEntityChestBetweenlands>> BL_CHEST = TILE_ENTITIES.register("betweenlands_chest", () -> TileEntityType.Builder.of(TileEntityChestBetweenlands::new, BlockRegistry.WEEDWOOD_CHEST.get()).build(null));	
	public static RegistryObject<TileEntityType<TileEntityBLFurnace>> BL_FURNACE = TILE_ENTITIES.register("sulfur_furnace", () -> TileEntityType.Builder.of(TileEntityBLFurnace::new, BlockRegistry.SULFUR_FURNACE.get()).build(null));	
	public static RegistryObject<TileEntityType<TileEntityBLDualFurnace>> BL_FURNACE_DUAL = TILE_ENTITIES.register("sulfur_furnace_dual", () -> TileEntityType.Builder.of(TileEntityBLDualFurnace::new, BlockRegistry.SULFUR_FURNACE_DUAL.get()).build(null));	
	public static RegistryObject<TileEntityType<TileEntityDruidAltar>> DRUID_ALTAR = TILE_ENTITIES.register("druid_altar", () -> TileEntityType.Builder.of(TileEntityDruidAltar::new, BlockRegistry.DRUID_ALTAR.get()).build(null));
	public static RegistryObject<TileEntityType<BeamOriginTileEntity>> BEAM_ORIGIN = TILE_ENTITIES.register("beam_origin", () -> TileEntityType.Builder.of(BeamOriginTileEntity::new, BlockRegistry.MUD_TOWER_BEAM_ORIGIN.get()).build(null));
	public static RegistryObject<TileEntityType<BeamRelayTileEntity>> BEAM_RELAY = TILE_ENTITIES.register("beam_relay", () -> TileEntityType.Builder.of(BeamRelayTileEntity::new, BlockRegistry.MUD_TOWER_BEAM_RELAY.get()).build(null));
	public static RegistryObject<TileEntityType<BLSignTileEntity>> BL_SIGN = TILE_ENTITIES.register("bl_sign", () -> TileEntityType.Builder.of(BLSignTileEntity::new, BlockRegistry.STANDING_WEEDWOOD_SIGN.get(), BlockRegistry.WALL_WEEDWOOD_SIGN.get()).build(null));
	public static RegistryObject<TileEntityType<TileEntityMudBrickAlcove>> MUD_BRICK_ALCOVE = TILE_ENTITIES.register("mud_bricks_alcove", () -> TileEntityType.Builder.of(TileEntityMudBrickAlcove::new, BlockRegistry.MUD_BRICK_ALCOVE.get()).build(null));
	public static RegistryObject<TileEntityType<TileEntityLootPot>> LOOT_POT = TILE_ENTITIES.register("loot_pot", () -> TileEntityType.Builder.of(TileEntityLootPot::new, BlockRegistry.LOOT_POT.get()).build(null));
	public static RegistryObject<TileEntityType<TileEntityPresent>> PRESENT = TILE_ENTITIES.register("present", () -> TileEntityType.Builder.of(TileEntityPresent::new, BlockRegistry.PRESENT.get()).build(null));
	public static RegistryObject<TileEntityType<TileEntitySpikeTrap>> SPIKE_TRAP = TILE_ENTITIES.register("spike_trap", () -> TileEntityType.Builder.of(TileEntitySpikeTrap::new, BlockRegistry.SPIKE_TRAP.get()).build(null));
	public static RegistryObject<TileEntityType<TileEntityMudBricksSpikeTrap>> MUD_BRICK_SPIKE_TRAP = TILE_ENTITIES.register("mud_brick_spike_trap", () -> TileEntityType.Builder.of(TileEntityMudBricksSpikeTrap::new, BlockRegistry.MUD_BRICK_SPIKE_TRAP.get()).build(null));
	
	//TODO: Move those over to the new Deferred Registry.
	public static void init() {
		//registerTileEntity(TileEntityDruidAltar.class, "druid_altar");
		registerTileEntity(TileEntityPurifier.class, "purifier");
		registerTileEntity(TileEntityWeedwoodWorkbench.class, "weedwood_workbench");
		registerTileEntity(TileEntityCompostBin.class, "compost_bin");
		registerTileEntity(TileEntityLootPot.class, "loot_pot");
		registerTileEntity(TileEntityMobSpawnerBetweenlands.class, "mob_spawner");
		registerTileEntity(TileEntityWisp.class, "wisp");
		//registerTileEntity(TileEntityBLFurnace.class, "sulfur_furnace");
		//registerTileEntity(TileEntityBLDualFurnace.class, "sulfur_furnace_dual");
		//registerTileEntity(TileEntityChestBetweenlands.class, "betweenlands_chest");
		registerTileEntity(TileEntityRubberTap.class, "rubber_tap");
		//registerTileEntity(TileEntitySpikeTrap.class, "spike_trap");
		registerTileEntity(TileEntityPossessedBlock.class, "possessed_block");
		registerTileEntity(TileEntityItemCage.class, "item_cage");
		//registerTileEntity(BLSignBlockEntity.class, "weedwood_sign");
		registerTileEntity(TileEntityMudFlowerPot.class, "mud_flower_pot");
		registerTileEntity(TileEntityGeckoCage.class, "gecko_cage");
		registerTileEntity(TileEntityInfuser.class, "infuser");
		registerTileEntity(TileEntityMortar.class, "mortar");
		registerTileEntity(TileEntityAnimator.class, "animator");
		registerTileEntity(TileEntityAlembic.class, "alembic");
		registerTileEntity(TileEntityDugSoil.class, "dug_soil");
		registerTileEntity(TileEntityItemShelf.class, "item_shelf");
		registerTileEntity(TileEntityTarBeastSpawner.class, "tar_beast_spawner");
		registerTileEntity(TileEntityTarLootPot1.class, "tar_loot_pot_1");
		registerTileEntity(TileEntityTarLootPot2.class, "tar_loot_pot_2");
		registerTileEntity(TileEntityTarLootPot3.class, "tar_loot_pot_3");
		registerTileEntity(TileEntityHopperBetweenlands.class, "syrmorite_hopper");
		registerTileEntity(TileEntityMossBed.class, "moss_bed");
		registerTileEntity(TileEntityAspectVial.class, "aspect_vial");
		registerTileEntity(TileEntityAspectrusCrop.class, "aspectrus_crop");
		registerTileEntity(TileEntityRepeller.class, "repeller");
		//registerTileEntity(TileEntityPresent.class, "present");
		registerTileEntity(TileEntityPuffshroom.class, "puffshroom");
		registerTileEntity(TileEntityRuneWeavingTable.class, "rune_weaving_table");
		registerTileEntity(TileEntityRuneWeavingTableFiller.class, "rune_weaving_table_filler");
		registerTileEntity(TileEntityRuneCarvingTable.class, "rune_carving_table");
		registerTileEntity(TileEntityRuneCarvingTableFiller.class, "rune_carving_table_filler");
		registerTileEntity(TileEntityWaystone.class, "waystone");
		registerTileEntity(TileEntityLootUrn.class, "loot_urn");
		registerTileEntity(TileEntityDungeonDoorRunes.class, "dungeon_door_runes");
		registerTileEntity(TileEntityDungeonDoorCombination.class, "dungeon_door_combination");
		registerTileEntity(TileEntityMudBricksSpikeTrap.class, "mud_bricks_spike_trap");
		registerTileEntity(TileEntityMudTilesSpikeTrap.class, "mud_tiles_spike_trap");
		registerTileEntity(TileEntityGroundItem.class, "ground_item");
		registerTileEntity(TileEntityDecayPitControl.class, "decay_pit_control");
		registerTileEntity(TileEntityDecayPitHangingChain.class, "decay_pit_hanging_chain");
		registerTileEntity(TileEntityDecayPitGroundChain.class, "decay_pit_ground_chain");
		registerTileEntity(TileEntityCenser.class, "censer");
		registerTileEntity(TileEntityBarrel.class, "tar_barrel");
		registerTileEntity(TileEntitySimulacrum.class, "simulacrum");
		registerTileEntity(TileEntityOfferingTable.class, "offering_bowl");
		registerTileEntity(TileEntityWindChime.class, "wind_chime");
	}

    @Mod.EventBusSubscriber(modid = TheBetweenlands.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration
    {
        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> e)
        {
            e.getRegistry().registerAll();
        }
    }
}
