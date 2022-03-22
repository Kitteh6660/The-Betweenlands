package thebetweenlands.common.world.gen.feature.structure.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.container.BlockLootUrn;
import thebetweenlands.common.block.container.BlockLootUrn.EnumLootUrn;
import thebetweenlands.common.block.container.BlockMudBrickAlcove;
import thebetweenlands.common.block.misc.BlockMudFlowerPotCandle;
import thebetweenlands.common.block.plant.BlockEdgePlant;
import thebetweenlands.common.block.plant.BlockHangingPlant;
import thebetweenlands.common.block.structure.BlockBrazier;
import thebetweenlands.common.block.structure.BlockBrazier.EnumBrazierHalf;
import thebetweenlands.common.block.structure.BlockCarvedMudBrick;
import thebetweenlands.common.block.structure.BlockCarvedMudBrick.EnumCarvedMudBrickType;
import thebetweenlands.common.block.structure.BlockDiagonalEnergyBarrier;
import thebetweenlands.common.block.structure.BlockDungeonDoorCombination;
import thebetweenlands.common.block.structure.BlockDungeonDoorRunes;
import thebetweenlands.common.block.structure.BlockDungeonWallCandle;
import thebetweenlands.common.block.structure.BlockMudBricksClimbable;
import thebetweenlands.common.block.structure.BlockMudTiles;
import thebetweenlands.common.block.structure.BlockMudTiles.EnumMudTileType;
import thebetweenlands.common.block.structure.BlockMudTilesWater;
import thebetweenlands.common.block.structure.BlockRottenBarkCarved;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands.EnumBlockHalfBL;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.block.structure.BlockWoodenSupportBeam;
import thebetweenlands.common.block.structure.BlockWormDungeonPillar;
import thebetweenlands.common.block.structure.BlockWormDungeonPillar.EnumWormPillarType;
import thebetweenlands.common.block.terrain.BlockHanger;
import thebetweenlands.common.block.terrain.BlockLogBetweenlands;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityGroundItem;
import thebetweenlands.common.world.gen.feature.structure.WorldGenSludgeWormDungeon;

public class SludgeWormMazeBlockHelper {
	
	public BlockState AIR = Blocks.AIR.defaultBlockState();
	public BlockState MOB_SPAWNER = BlockRegistry.MOB_SPAWNER.defaultBlockState();

	//shrooms
	public BlockState BLACK_HAT_MUSHROOM = BlockRegistry.BLACK_HAT_MUSHROOM.defaultBlockState();
	public BlockState FLAT_HEAD_MUSHROOM = BlockRegistry.FLAT_HEAD_MUSHROOM.defaultBlockState();
	public BlockState ROTBULB = BlockRegistry.ROTBULB.defaultBlockState();

	//floor plants
	public BlockState TALL_SLUDGECREEP = BlockRegistry.TALL_SLUDGECREEP.defaultBlockState();
	public BlockState PALE_GRASS = BlockRegistry.PALE_GRASS.defaultBlockState();
	
	//wall plants
	public BlockState MOSS = BlockRegistry.DEAD_MOSS.defaultBlockState();
	public BlockState LICHEN = BlockRegistry.DEAD_LICHEN.defaultBlockState();

	//edge plants
	public BlockState EDGE_SHROOM = BlockRegistry.EDGE_SHROOM.defaultBlockState();
	public BlockState EDGE_MOSS = BlockRegistry.EDGE_MOSS.defaultBlockState();
	public BlockState EDGE_LEAF = BlockRegistry.EDGE_LEAF.defaultBlockState();

	//hanging plants
	public BlockState CRYPTWEED = BlockRegistry.CRYPTWEED.defaultBlockState().setValue(BlockHangingPlant.CAN_GROW, false);
	public BlockState STRING_ROOTS = BlockRegistry.STRING_ROOTS.defaultBlockState().setValue(BlockHangingPlant.CAN_GROW, false);
	public BlockState HANGER = BlockRegistry.HANGER.defaultBlockState().setValue(BlockHanger.CAN_GROW, true).setValue(BlockHanger.SEEDED, false);

	public BlockState STAGNANT_WATER = BlockRegistry.STAGNANT_WATER.defaultBlockState();

	public BlockState DUNGEON_WALL_CANDLE_NORTH = BlockRegistry.DUNGEON_WALL_CANDLE.defaultBlockState().setValue(BlockDungeonWallCandle.FACING, Direction.NORTH);
	public BlockState DUNGEON_WALL_CANDLE_EAST = BlockRegistry.DUNGEON_WALL_CANDLE.defaultBlockState().setValue(BlockDungeonWallCandle.FACING, Direction.EAST);
	public BlockState DUNGEON_WALL_CANDLE_SOUTH = BlockRegistry.DUNGEON_WALL_CANDLE.defaultBlockState().setValue(BlockDungeonWallCandle.FACING, Direction.SOUTH);
	public BlockState DUNGEON_WALL_CANDLE_WEST = BlockRegistry.DUNGEON_WALL_CANDLE.defaultBlockState().setValue(BlockDungeonWallCandle.FACING, Direction.WEST);
	public BlockState CHEST = BlockRegistry.WEEDWOOD_CHEST.defaultBlockState();
	public BlockState WORM_DUNGEON_PILLAR = BlockRegistry.WORM_DUNGEON_PILLAR.defaultBlockState();
	public BlockState WORM_DUNGEON_PILLAR_TOP = BlockRegistry.WORM_DUNGEON_PILLAR.defaultBlockState().setValue(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP);
	public BlockState WORM_DUNGEON_PILLAR_DECAY_1 = BlockRegistry.WORM_DUNGEON_PILLAR.defaultBlockState().setValue(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_1);
	public BlockState WORM_DUNGEON_PILLAR_TOP_DECAY_1 = BlockRegistry.WORM_DUNGEON_PILLAR.defaultBlockState().setValue(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_1);
	public BlockState WORM_DUNGEON_PILLAR_DECAY_2 = BlockRegistry.WORM_DUNGEON_PILLAR.defaultBlockState().setValue(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_2);
	public BlockState WORM_DUNGEON_PILLAR_TOP_DECAY_2 = BlockRegistry.WORM_DUNGEON_PILLAR.defaultBlockState().setValue(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_2);
	public BlockState WORM_DUNGEON_PILLAR_DECAY_3 = BlockRegistry.WORM_DUNGEON_PILLAR.defaultBlockState().setValue(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_3);
	public BlockState WORM_DUNGEON_PILLAR_TOP_DECAY_3 = BlockRegistry.WORM_DUNGEON_PILLAR.defaultBlockState().setValue(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_3);
	public BlockState WORM_DUNGEON_PILLAR_DECAY_4 = BlockRegistry.WORM_DUNGEON_PILLAR.defaultBlockState().setValue(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_4);
	public BlockState WORM_DUNGEON_PILLAR_TOP_DECAY_4 = BlockRegistry.WORM_DUNGEON_PILLAR.defaultBlockState().setValue(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_4);
	public BlockState WORM_DUNGEON_PILLAR_DECAY_FULL = BlockRegistry.WORM_DUNGEON_PILLAR.defaultBlockState().setValue(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL_DECAY_FULL);
	public BlockState WORM_DUNGEON_PILLAR_TOP_DECAY_FULL = BlockRegistry.WORM_DUNGEON_PILLAR.defaultBlockState().setValue(BlockWormDungeonPillar.VARIANT, EnumWormPillarType.WORM_PILLAR_TOP_DECAY_FULL);

	public BlockState MUD_TILES = BlockRegistry.MUD_TILES.defaultBlockState();
	public BlockState MUD_TILES_DECAY = BlockRegistry.MUD_TILES.defaultBlockState().setValue(BlockMudTiles.VARIANT, EnumMudTileType.MUD_TILES_DECAY);
	public BlockState MUD_TILES_CRACKED = BlockRegistry.MUD_TILES.defaultBlockState().setValue(BlockMudTiles.VARIANT, EnumMudTileType.MUD_TILES_CRACKED);
	public BlockState MUD_TILES_CRACKED_DECAY = BlockRegistry.MUD_TILES.defaultBlockState().setValue(BlockMudTiles.VARIANT, EnumMudTileType.MUD_TILES_CRACKED_DECAY);
	public BlockState MUD_TILES_WATER = BlockRegistry.MUD_TILES_WATER.defaultBlockState();

	public BlockState MUD_BRICK_STAIRS = BlockRegistry.MUD_BRICK_STAIRS.defaultBlockState();
	public BlockState MUD_BRICK_STAIRS_DECAY_1 = BlockRegistry.MUD_BRICK_STAIRS_DECAY_1.defaultBlockState();
	public BlockState MUD_BRICK_STAIRS_DECAY_2 = BlockRegistry.MUD_BRICK_STAIRS_DECAY_2.defaultBlockState();
	public BlockState MUD_BRICK_STAIRS_DECAY_3 = BlockRegistry.MUD_BRICK_STAIRS_DECAY_3.defaultBlockState();
	public BlockState MUD_BRICK_STAIRS_DECAY_4 = BlockRegistry.MUD_BRICK_STAIRS_DECAY_4.defaultBlockState();

	public BlockState MUD_BRICK_SLAB = BlockRegistry.MUD_BRICK_SLAB.defaultBlockState();
	public BlockState MUD_BRICK_SLAB_DECAY_1 = BlockRegistry.MUD_BRICK_SLAB_DECAY_1.defaultBlockState();
	public BlockState MUD_BRICK_SLAB_DECAY_2 = BlockRegistry.MUD_BRICK_SLAB_DECAY_2.defaultBlockState();
	public BlockState MUD_BRICK_SLAB_DECAY_3 = BlockRegistry.MUD_BRICK_SLAB_DECAY_3.defaultBlockState();
	public BlockState MUD_BRICK_SLAB_DECAY_4 = BlockRegistry.MUD_BRICK_SLAB_DECAY_4.defaultBlockState();

	public BlockState MUD_BRICKS = BlockRegistry.MUD_BRICKS.defaultBlockState();
	public BlockState MUD_BRICKS_DECAY_1 = BlockRegistry.MUD_BRICKS_CARVED.defaultBlockState().setValue(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_1);
	public BlockState MUD_BRICKS_DECAY_2 = BlockRegistry.MUD_BRICKS_CARVED.defaultBlockState().setValue(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_2);
	public BlockState MUD_BRICKS_DECAY_3 = BlockRegistry.MUD_BRICKS_CARVED.defaultBlockState().setValue(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_3);
	public BlockState MUD_BRICKS_DECAY_4 = BlockRegistry.MUD_BRICKS_CARVED.defaultBlockState().setValue(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_DECAY_4);
	public BlockState MUD_BRICKS_CARVED = BlockRegistry.MUD_BRICKS_CARVED.defaultBlockState().setValue(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED);
	public BlockState MUD_BRICKS_CARVED_DECAY_1 = BlockRegistry.MUD_BRICKS_CARVED.defaultBlockState().setValue(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_DECAY_1);
	public BlockState MUD_BRICKS_CARVED_DECAY_2 = BlockRegistry.MUD_BRICKS_CARVED.defaultBlockState().setValue(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_DECAY_2);
	public BlockState MUD_BRICKS_CARVED_DECAY_3 = BlockRegistry.MUD_BRICKS_CARVED.defaultBlockState().setValue(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_DECAY_3);
	public BlockState MUD_BRICKS_CARVED_DECAY_4 = BlockRegistry.MUD_BRICKS_CARVED.defaultBlockState().setValue(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_DECAY_4);
	public BlockState MUD_BRICKS_CARVED_EDGE = BlockRegistry.MUD_BRICKS_CARVED.defaultBlockState().setValue(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE);
	public BlockState MUD_BRICKS_CARVED_EDGE_DECAY_1 = BlockRegistry.MUD_BRICKS_CARVED.defaultBlockState().setValue(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE_DECAY_1);
	public BlockState MUD_BRICKS_CARVED_EDGE_DECAY_2 = BlockRegistry.MUD_BRICKS_CARVED.defaultBlockState().setValue(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE_DECAY_2);
	public BlockState MUD_BRICKS_CARVED_EDGE_DECAY_3 = BlockRegistry.MUD_BRICKS_CARVED.defaultBlockState().setValue(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE_DECAY_3);
	public BlockState MUD_BRICKS_CARVED_EDGE_DECAY_4 = BlockRegistry.MUD_BRICKS_CARVED.defaultBlockState().setValue(BlockCarvedMudBrick.VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED_EDGE_DECAY_4);
	public BlockState MUD_BRICKS_SPIKE_TRAP = BlockRegistry.MUD_BRICK_SPIKE_TRAP.defaultBlockState();
	public BlockState MUD_TILES_SPIKE_TRAP = BlockRegistry.MUD_TILES_SPIKE_TRAP.defaultBlockState();
	
	public BlockState MUD = BlockRegistry.MUD.defaultBlockState();
	public BlockState COMPACTED_MUD = BlockRegistry.COMPACTED_MUD.defaultBlockState();
	public BlockState COMPACTED_MUD_SLOPE = BlockRegistry.COMPACTED_MUD_SLOPE.defaultBlockState();
	public BlockState MUD_BRICK_ROOF = BlockRegistry.MUD_BRICK_ROOF.defaultBlockState();
	public BlockState PUFFSHROOM = BlockRegistry.PUFFSHROOM.defaultBlockState();
	public BlockState ROTTEN_BARK = BlockRegistry.LOG_ROTTEN_BARK.defaultBlockState().setValue(BlockLogBetweenlands.LOG_AXIS, EnumAxis.NONE);

	public BlockState ROOT = BlockRegistry.ROOT.defaultBlockState();

	public BlockState DUNGEON_DOOR_COMBINATION_EAST = BlockRegistry.DUNGEON_DOOR_COMBINATION.defaultBlockState().setValue(BlockDungeonDoorCombination.FACING, Direction.EAST);
	public BlockState DUNGEON_DOOR_COMBINATION_WEST = BlockRegistry.DUNGEON_DOOR_COMBINATION.defaultBlockState().setValue(BlockDungeonDoorCombination.FACING, Direction.WEST);

	public BlockState DUNGEON_DOOR_EAST = BlockRegistry.DUNGEON_DOOR_RUNES.defaultBlockState().setValue(BlockDungeonDoorRunes.FACING, Direction.EAST);
	public BlockState DUNGEON_DOOR_WEST = BlockRegistry.DUNGEON_DOOR_RUNES.defaultBlockState().setValue(BlockDungeonDoorRunes.FACING, Direction.WEST);

	public BlockState DUNGEON_DOOR_NORTH = BlockRegistry.DUNGEON_DOOR_RUNES.defaultBlockState().setValue(BlockDungeonDoorRunes.FACING, Direction.NORTH);
	public BlockState DUNGEON_DOOR_SOUTH = BlockRegistry.DUNGEON_DOOR_RUNES.defaultBlockState().setValue(BlockDungeonDoorRunes.FACING, Direction.SOUTH);

	public BlockState DUNGEON_DOOR_MIMIC_EAST = BlockRegistry.DUNGEON_DOOR_RUNES_MIMIC.defaultBlockState().setValue(BlockDungeonDoorRunes.FACING, Direction.EAST);
	public BlockState DUNGEON_DOOR_MIMIC_WEST = BlockRegistry.DUNGEON_DOOR_RUNES_MIMIC.defaultBlockState().setValue(BlockDungeonDoorRunes.FACING, Direction.WEST);

	public BlockState DUNGEON_DOOR_MIMIC_NORTH = BlockRegistry.DUNGEON_DOOR_RUNES_MIMIC.defaultBlockState().setValue(BlockDungeonDoorRunes.FACING, Direction.NORTH);
	public BlockState DUNGEON_DOOR_MIMIC_SOUTH = BlockRegistry.DUNGEON_DOOR_RUNES_MIMIC.defaultBlockState().setValue(BlockDungeonDoorRunes.FACING, Direction.SOUTH);

	public BlockState DUNGEON_DOOR_CRAWLER_EAST = BlockRegistry.DUNGEON_DOOR_RUNES_CRAWLER.defaultBlockState().setValue(BlockDungeonDoorRunes.FACING, Direction.EAST);
	public BlockState DUNGEON_DOOR_CRAWLER_WEST = BlockRegistry.DUNGEON_DOOR_RUNES_CRAWLER.defaultBlockState().setValue(BlockDungeonDoorRunes.FACING, Direction.WEST);

	public BlockState DUNGEON_DOOR_CRAWLER_NORTH = BlockRegistry.DUNGEON_DOOR_RUNES_CRAWLER.defaultBlockState().setValue(BlockDungeonDoorRunes.FACING, Direction.NORTH);
	public BlockState DUNGEON_DOOR_CRAWLER_SOUTH = BlockRegistry.DUNGEON_DOOR_RUNES_CRAWLER.defaultBlockState().setValue(BlockDungeonDoorRunes.FACING, Direction.SOUTH);

	public BlockState LOOT_URN_1 = BlockRegistry.LOOT_URN.defaultBlockState().setValue(BlockLootUrn.VARIANT, EnumLootUrn.URN_1);
	public BlockState LOOT_URN_2 = BlockRegistry.LOOT_URN.defaultBlockState().setValue(BlockLootUrn.VARIANT, EnumLootUrn.URN_2);
	public BlockState LOOT_URN_3 = BlockRegistry.LOOT_URN.defaultBlockState().setValue(BlockLootUrn.VARIANT, EnumLootUrn.URN_3);

	public BlockState MUD_BRICKS_ALCOVE_NORTH = BlockRegistry.MUD_BRICK_ALCOVE.defaultBlockState().setValue(BlockMudBrickAlcove.FACING, Direction.NORTH);
	public BlockState MUD_BRICKS_ALCOVE_EAST = BlockRegistry.MUD_BRICK_ALCOVE.defaultBlockState().setValue(BlockMudBrickAlcove.FACING, Direction.EAST);
	public BlockState MUD_BRICKS_ALCOVE_SOUTH = BlockRegistry.MUD_BRICK_ALCOVE.defaultBlockState().setValue(BlockMudBrickAlcove.FACING, Direction.SOUTH);
	public BlockState MUD_BRICKS_ALCOVE_WEST = BlockRegistry.MUD_BRICK_ALCOVE.defaultBlockState().setValue(BlockMudBrickAlcove.FACING, Direction.WEST);

	public BlockState MUD_BRICKS_CLIMBABLE_NORTH = BlockRegistry.MUD_BRICKS_CLIMBABLE.defaultBlockState().setValue(BlockMudBricksClimbable.FACING, Direction.NORTH);
	public BlockState MUD_BRICKS_CLIMBABLE_EAST = BlockRegistry.MUD_BRICKS_CLIMBABLE.defaultBlockState().setValue(BlockMudBricksClimbable.FACING, Direction.EAST);
	public BlockState MUD_BRICKS_CLIMBABLE_SOUTH = BlockRegistry.MUD_BRICKS_CLIMBABLE.defaultBlockState().setValue(BlockMudBricksClimbable.FACING, Direction.SOUTH);
	public BlockState MUD_BRICKS_CLIMBABLE_WEST = BlockRegistry.MUD_BRICKS_CLIMBABLE.defaultBlockState().setValue(BlockMudBricksClimbable.FACING, Direction.WEST);

	public BlockState MUD_FLOWER_POT_CANDLE_LIT = BlockRegistry.MUD_FLOWER_POT_CANDLE.defaultBlockState().setValue(BlockMudFlowerPotCandle.LIT, true);
	public BlockState MUD_FLOWER_POT_CANDLE_UNLIT = BlockRegistry.MUD_FLOWER_POT_CANDLE.defaultBlockState().setValue(BlockMudFlowerPotCandle.LIT, false);

	public BlockState MUD_BRICK_WALL = BlockRegistry.MUD_BRICK_WALL.defaultBlockState();
	public BlockState ITEM_SHELF = BlockRegistry.ITEM_SHELF.defaultBlockState();
	public BlockState WOODEN_SUPPORT_BEAM_ROTTEN_1 = BlockRegistry.WOODEN_SUPPORT_BEAM_ROTTEN_1.defaultBlockState();
	public BlockState WOODEN_SUPPORT_BEAM_ROTTEN_2 = BlockRegistry.WOODEN_SUPPORT_BEAM_ROTTEN_2.defaultBlockState();
	public BlockState WOODEN_SUPPORT_BEAM_ROTTEN_3 = BlockRegistry.WOODEN_SUPPORT_BEAM_ROTTEN_2.defaultBlockState();

	public BlockState LOG_ROTTEN_BARK = BlockRegistry.LOG_ROTTEN_BARK.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_1 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_1.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_2 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_2.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_3 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_3.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_4 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_4.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_5 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_5.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_6 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_6.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_7 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_7.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_8 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_8.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_9 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_9.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_10 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_10.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_11 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_11.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_12 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_12.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_13 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_13.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_14 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_14.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_15 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_15.defaultBlockState();
	public BlockState LOG_ROTTEN_BARK_CARVED_16 = BlockRegistry.LOG_ROTTEN_BARK_CARVED_16.defaultBlockState();

	public BlockState ROTTEN_PLANKS = BlockRegistry.ROTTEN_PLANKS.defaultBlockState();
	public BlockState ROTTEN_PLANK_SLAB_UPPER = BlockRegistry.ROTTEN_PLANK_SLAB.defaultBlockState().setValue(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	public BlockState ROTTEN_PLANK_SLAB_LOWER = BlockRegistry.ROTTEN_PLANK_SLAB.defaultBlockState().setValue(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.BOTTOM);
	//Tower
	public BlockState BETWEENSTONE = BlockRegistry.BETWEENSTONE.defaultBlockState();
	
	public BlockState SMOOTH_BETWEENSTONE = BlockRegistry.SMOOTH_BETWEENSTONE.defaultBlockState();
	public BlockState SMOOTH_BETWEENSTONE_STAIRS = BlockRegistry.SMOOTH_BETWEENSTONE_STAIRS.defaultBlockState();
	public BlockState SMOOTH_BETWEENSTONE_SLAB_UPPER = BlockRegistry.SMOOTH_BETWEENSTONE_SLAB.defaultBlockState().setValue(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	public BlockState SMOOTH_BETWEENSTONE_SLAB_LOWER = BlockRegistry.SMOOTH_BETWEENSTONE_SLAB.defaultBlockState().setValue(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.BOTTOM);
	
	public BlockState BETWEENSTONE_BRICKS = BlockRegistry.BETWEENSTONE_BRICKS.defaultBlockState();
	public BlockState BETWEENSTONE_BRICK_STAIRS = BlockRegistry.BETWEENSTONE_BRICK_STAIRS.defaultBlockState();
	public BlockState BETWEENSTONE_BRICK_SLAB_UPPER = BlockRegistry.BETWEENSTONE_BRICK_SLAB.defaultBlockState().setValue(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	public BlockState BETWEENSTONE_BRICK_SLAB_LOWER = BlockRegistry.BETWEENSTONE_BRICK_SLAB.defaultBlockState().setValue(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.BOTTOM);
	public BlockState BETWEENSTONE_PILLAR = BlockRegistry.BETWEENSTONE_PILLAR.defaultBlockState();
	public BlockState BETWEENSTONE_TILES = BlockRegistry.BETWEENSTONE_TILES.defaultBlockState();

	public BlockState SMOOTH_PITSTONE = BlockRegistry.SMOOTH_PITSTONE.defaultBlockState();
	public BlockState SMOOTH_PITSTONE_STAIRS = BlockRegistry.SMOOTH_PITSTONE_STAIRS.defaultBlockState();
	public BlockState SMOOTH_PITSTONE_SLAB_UPPER = BlockRegistry.SMOOTH_PITSTONE_SLAB.defaultBlockState().setValue(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	public BlockState SMOOTH_PITSTONE_SLAB_LOWER = BlockRegistry.SMOOTH_PITSTONE_SLAB.defaultBlockState().setValue(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.BOTTOM);

	public BlockState PITSTONE_BRICKS = BlockRegistry.PITSTONE_BRICKS.defaultBlockState();
	public BlockState PITSTONE_BRICK_STAIRS = BlockRegistry.PITSTONE_BRICK_STAIRS.defaultBlockState();
	public BlockState PITSTONE_BRICK_SLAB_UPPER = BlockRegistry.PITSTONE_BRICK_SLAB.defaultBlockState().setValue(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP);
	public BlockState PITSTONE_BRICK_SLAB_LOWER = BlockRegistry.PITSTONE_BRICK_SLAB.defaultBlockState().setValue(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.BOTTOM);
	public BlockState PITSTONE_PILLAR = BlockRegistry.PITSTONE_PILLAR.defaultBlockState();
	public BlockState PITSTONE_TILES = BlockRegistry.PITSTONE_TILES.defaultBlockState();

	public BlockState PITSTONE_CHISELED = BlockRegistry.PITSTONE_CHISELED.defaultBlockState();
	public BlockState SCABYST_PITSTONE_DOTTED = BlockRegistry.SCABYST_PITSTONE_DOTTED.defaultBlockState();
    public BlockState SCABYST_PITSTONE_HORIZONTAL = BlockRegistry.SCABYST_PITSTONE_HORIZONTAL.defaultBlockState();
    public BlockState ENERGY_BARRIER_MUD = BlockRegistry.ENERGY_BARRIER_MUD.defaultBlockState();
    public BlockState DIAGONAL_ENERGY_BARRIER = BlockRegistry.DIAGONAL_ENERGY_BARRIER.defaultBlockState();
    public BlockState MUD_TOWER_BEAM_ORIGIN = BlockRegistry.MUD_TOWER_BEAM_ORIGIN.defaultBlockState();
    public BlockState MUD_TOWER_BEAM_RELAY = BlockRegistry.MUD_TOWER_BEAM_RELAY.defaultBlockState();
    public BlockState MUD_TOWER_BEAM_TUBE = BlockRegistry.MUD_TOWER_BEAM_TUBE.defaultBlockState();
    public BlockState MUD_TOWER_BEAM_LENS_SUPPORTS = BlockRegistry.MUD_TOWER_BEAM_LENS_SUPPORTS.defaultBlockState();
    public BlockState BRAZIER_TOP = BlockRegistry.MUD_TOWER_BRAZIER.defaultBlockState().setValue(BlockBrazier.HALF, EnumBrazierHalf.UPPER);
    public BlockState BRAZIER_BOTTOM = BlockRegistry.MUD_TOWER_BRAZIER.defaultBlockState().setValue(BlockBrazier.HALF, EnumBrazierHalf.LOWER);
    public BlockState COMPACTED_MUD_MIRAGE = BlockRegistry.COMPACTED_MUD_MIRAGE.defaultBlockState();
    public BlockState COMPACTED_MUD_SLAB = BlockRegistry.COMPACTED_MUD_SLAB.defaultBlockState();
    public BlockState GROUND_ITEM = BlockRegistry.GROUND_ITEM.defaultBlockState();

    public BlockState DECAY_PIT_INVISIBLE_FLOOR_BLOCK = BlockRegistry.DECAY_PIT_INVISIBLE_FLOOR_BLOCK.defaultBlockState();
    public BlockState DECAY_PIT_INVISIBLE_FLOOR_BLOCK_R_1 = BlockRegistry.DECAY_PIT_INVISIBLE_FLOOR_BLOCK_R_1.defaultBlockState();
    public BlockState DECAY_PIT_INVISIBLE_FLOOR_BLOCK_R_2 = BlockRegistry.DECAY_PIT_INVISIBLE_FLOOR_BLOCK_R_2.defaultBlockState();
    public BlockState DECAY_PIT_INVISIBLE_FLOOR_BLOCK_DIAGONAL = BlockRegistry.DECAY_PIT_INVISIBLE_FLOOR_BLOCK_DIAGONAL.defaultBlockState();
    public BlockState DECAY_PIT_INVISIBLE_FLOOR_BLOCK_L_1 = BlockRegistry.DECAY_PIT_INVISIBLE_FLOOR_BLOCK_L_1.defaultBlockState();
    public BlockState DECAY_PIT_INVISIBLE_FLOOR_BLOCK_L_2 = BlockRegistry.DECAY_PIT_INVISIBLE_FLOOR_BLOCK_L_2.defaultBlockState();

	public final Map<BlockState, Boolean> STRUCTURE_BLOCKS = new HashMap<BlockState, Boolean>();

	private final WorldGenSludgeWormDungeon dungeon;
	
	public SludgeWormMazeBlockHelper(WorldGenSludgeWormDungeon dungeon) {
		initStuctureBlockMap();
		this.dungeon = dungeon;
	}

	public @Nullable BlockState getMudBricksForLevel(Random rand, int level, int layer) {
		switch (level) {
		case 0:
			if(layer == 1)
				return MUD_BRICKS;
			if(layer == 2)
				return MUD_BRICKS_CARVED;
			if(layer == 3)
				return MUD_BRICKS_CARVED_EDGE;
		case 1:
			if(layer == 1)
				return rand.nextBoolean() ? MUD_BRICKS : MUD_BRICKS_DECAY_1;
			if(layer == 2)
				return MUD_BRICKS_CARVED;
			if(layer == 3)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED_EDGE : MUD_BRICKS_CARVED_EDGE_DECAY_1;
		case 2:
			if(layer == 1)
				return rand.nextBoolean() ? MUD_BRICKS_DECAY_1 : MUD_BRICKS_DECAY_2;
			if(layer == 2)
				return MUD_BRICKS_CARVED;
			if(layer == 3)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED_EDGE_DECAY_1 : MUD_BRICKS_CARVED_EDGE_DECAY_2;
		case 3:
			if(layer == 1)
				return rand.nextBoolean() ? MUD_BRICKS_DECAY_2 : MUD_BRICKS_DECAY_3;
			if(layer == 2)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED : MUD_BRICKS_CARVED_DECAY_1;
			if(layer == 3)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED_EDGE_DECAY_2 : MUD_BRICKS_CARVED_EDGE_DECAY_3;
		case 4:
			if(layer == 1)
				return rand.nextBoolean() ? MUD_BRICKS_DECAY_3 : MUD_BRICKS_DECAY_4;
			if(layer == 2)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED_DECAY_1 : MUD_BRICKS_CARVED_DECAY_2;
			if(layer == 3)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED_EDGE_DECAY_3 :MUD_BRICKS_CARVED_EDGE_DECAY_4;
		case 5:
			if(layer == 1)
				return MUD_BRICKS_DECAY_4;
			if(layer == 2)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED_DECAY_1 : rand.nextBoolean() ? MUD_BRICKS_CARVED_DECAY_2 : MUD_BRICKS_CARVED_DECAY_3;
			if(layer == 3)
				return MUD_BRICKS_CARVED_EDGE_DECAY_4;
		case 6:
			if(layer == 1)
				return MUD_BRICKS_DECAY_4;
			if(layer == 2)
				return rand.nextBoolean() ? MUD_BRICKS_CARVED_DECAY_2 : rand.nextBoolean() ? MUD_BRICKS_CARVED_DECAY_3 : MUD_BRICKS_CARVED_DECAY_4;
			if(layer == 3)
				return MUD_BRICKS_CARVED_EDGE_DECAY_4;
		case 7:
			if(layer == 1)
				return MUD_BRICKS_DECAY_4;
			if(layer == 2)
				return MUD_BRICKS_CARVED_DECAY_4;
			if(layer == 3)
				return MUD_BRICKS_CARVED_EDGE_DECAY_4;
		}
		return MUD_BRICKS;
	}

	public @Nullable BlockState getMudSlabsForLevel(Random rand, int level, EnumBlockHalfBL half) {
		BlockState state = MUD_BRICK_SLAB;
		switch (level) {
		case 0:
			state = MUD_BRICK_SLAB;
			break;
		case 1:
			state = rand.nextBoolean() ? MUD_BRICK_SLAB : MUD_BRICK_SLAB_DECAY_1;
			break;
		case 2:
			state = MUD_BRICK_SLAB_DECAY_1;
			break;
		case 3:
			state = rand.nextBoolean() ? MUD_BRICK_SLAB_DECAY_1 : MUD_BRICK_SLAB_DECAY_2;
			break;
		case 4:
			state =  MUD_BRICK_SLAB_DECAY_2;
			break;
		case 5:
			state = rand.nextBoolean() ? MUD_BRICK_SLAB_DECAY_2 : MUD_BRICK_SLAB_DECAY_3;
			break;
		case 6:
		case 7:
			state = MUD_BRICK_SLAB_DECAY_3;
			break;
		}
		return state.setValue(BlockSlabBetweenlands.HALF, half);
	}

	public @Nullable BlockState getPillarsForLevel(Random rand, int level, int layer) {
		switch (level) {
		case 0:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP;
		case 1:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR_DECAY_1;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP_DECAY_1;
		case 2:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR_DECAY_2;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP_DECAY_2;
		case 3:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR_DECAY_3;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR_DECAY_1;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP_DECAY_3;
		case 4:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR_DECAY_4;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR_DECAY_2;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP_DECAY_4;
		case 5:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR_DECAY_FULL;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR_DECAY_3;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP_DECAY_4;
		case 6:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR_DECAY_FULL;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR_DECAY_4;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP_DECAY_FULL;
		case 7:
			if(layer == 1)
				return WORM_DUNGEON_PILLAR_DECAY_FULL;
			if(layer == 2)
				return WORM_DUNGEON_PILLAR_DECAY_4;
			if(layer == 3)
				return WORM_DUNGEON_PILLAR_TOP_DECAY_FULL;
		}
		return WORM_DUNGEON_PILLAR;
	}

	public @Nullable BlockState getTilesForLevel(Random rand, int level) {
		int type = rand.nextInt(8);
		switch (level) {
		case 0:
			if(type == 0)
				return MUD_TILES_CRACKED;
			else
				return MUD_TILES;
		case 1:
			if(type == 0 || type == 1)
				return MUD_TILES_CRACKED;
			if(type == 2)
				return MUD_TILES_DECAY;
			else
				return MUD_TILES;
		case 2:
			if(type == 0 || type == 1)
				return MUD_TILES_DECAY;
			if(type == 2)
				return MUD_TILES_CRACKED_DECAY;
			else
				return MUD_TILES;
		case 3:
			if(type == 0 || type == 1)
				return MUD_TILES_DECAY;
			if(type == 2 || type == 3)
				return MUD_TILES_CRACKED_DECAY;
			if(type == 4)
				return MUD_TILES_CRACKED;
			else
				return MUD_TILES;
		case 4:
			if(type == 0 || type == 1 || type == 2)
				return MUD_TILES_DECAY;
			if(type == 3 || type == 4)
				return MUD_TILES_CRACKED;
			if(type == 5)
				return MUD_TILES_CRACKED_DECAY;
			else
				return MUD_TILES;
		case 5:
			if(type == 0 || type == 1 || type == 2)
				return MUD_TILES_DECAY;
			if(type == 3 || type == 4)
				return MUD_TILES_CRACKED_DECAY;
			if(type == 5)
				return MUD_TILES_CRACKED;
			else
				return MUD_TILES;
		case 6:
			if(type == 0 || type == 1)
				return MUD_TILES_CRACKED_DECAY;
			if(type == 3)
				return MUD_TILES_CRACKED;
			else
				return MUD_TILES_DECAY;
		case 7:
			return MUD_BRICKS;
		}
		return MUD_BRICKS;
	}

	public @Nullable BlockState getStairsForLevel(Random rand, int level, Direction facing, EnumHalf half) {
		BlockState state = MUD_BRICK_STAIRS;
		int type = rand.nextInt(3);
		switch (level) {
		case 0:
			state = MUD_BRICK_STAIRS;
			break;
		case 1:
			if(type == 0 || type == 1)
				state = MUD_BRICK_STAIRS;
			if(type == 2)
				state = MUD_BRICK_STAIRS_DECAY_1;
			break;
		case 2:
			if(type == 0)
				state = MUD_BRICK_STAIRS;
			if(type == 1)
				state = MUD_BRICK_STAIRS_DECAY_1;
			if(type == 2)
				state = MUD_BRICK_STAIRS_DECAY_2;
			break;
		case 3:
			if(type == 0 || type == 1)
				state = MUD_BRICK_STAIRS_DECAY_1;
			if(type == 2)
				state = MUD_BRICK_STAIRS_DECAY_2;
			break;
		case 4:
			if(type == 0)
				state = MUD_BRICK_STAIRS_DECAY_1;
			if(type == 1)
				state = MUD_BRICK_STAIRS_DECAY_2;
			if(type == 2)
				state = MUD_BRICK_STAIRS_DECAY_3;
			break;
		case 5:
			if(type == 0 || type == 1)
				state = MUD_BRICK_STAIRS_DECAY_2;
			if(type == 2)
				state = MUD_BRICK_STAIRS_DECAY_3;
			break;
		case 6:
		case 7:
			state = MUD_BRICK_STAIRS_DECAY_3;
			break;
		}
		return state.setValue(BlockStairsBetweenlands.FACING, facing).setValue(BlockStairsBetweenlands.HALF, half);
	}

	public BlockState getRandomBeam(Direction facing, Random rand, int level, int count, boolean randomiseLine) {
		BlockState state = LOG_ROTTEN_BARK_CARVED_1;
		if(randomiseLine)
			count = rand.nextInt(6); // overrides fixed ends and middles with a random choice for multi-placed blocks

		if (count == 1 || count == 3) {
			int endType = rand.nextInt(6);
			switch (endType) {
			case 0:
				state = LOG_ROTTEN_BARK_CARVED_11;
				break;
			case 1:
				state = LOG_ROTTEN_BARK_CARVED_12;
				break;
			case 2:
				state = LOG_ROTTEN_BARK_CARVED_13;
				break;
			case 3:
				state = LOG_ROTTEN_BARK_CARVED_14;
				break;
			case 4:
				state = LOG_ROTTEN_BARK_CARVED_15;
				break;
			case 5:
				state = LOG_ROTTEN_BARK_CARVED_16;
				break;
			}
		} else {
			int midType = rand.nextInt(10);
			switch (midType) {
			case 0:
				state = LOG_ROTTEN_BARK_CARVED_1;
				break;
			case 1:
				state = LOG_ROTTEN_BARK_CARVED_2;
				break;
			case 2:
				state = LOG_ROTTEN_BARK_CARVED_3;
				break;
			case 3:
				state = LOG_ROTTEN_BARK_CARVED_4;
				break;
			case 4:
				state = LOG_ROTTEN_BARK_CARVED_5;
				break;
			case 5:
				state = LOG_ROTTEN_BARK_CARVED_6;
				break;
			case 6:
				state = LOG_ROTTEN_BARK_CARVED_7;
				break;
			case 7:
				state = LOG_ROTTEN_BARK_CARVED_8;
				break;
			case 8:
				state = LOG_ROTTEN_BARK_CARVED_9;
				break;
			case 9:
				state = LOG_ROTTEN_BARK_CARVED_10;
				break;
			}
		}
		return state.setValue(BlockRottenBarkCarved.FACING, facing);
	}

	public BlockState getRandomSupportBeam(Direction facing, boolean isTop, Random rand) {
		BlockState state = WOODEN_SUPPORT_BEAM_ROTTEN_1;
		int type = rand.nextInt(3);
		if(type == 0)
			state = WOODEN_SUPPORT_BEAM_ROTTEN_1;
		if(type == 1)
			state = WOODEN_SUPPORT_BEAM_ROTTEN_2;
		if(type == 2)
			state = WOODEN_SUPPORT_BEAM_ROTTEN_3;
		return state.setValue(BlockWoodenSupportBeam.FACING, facing).setValue(BlockWoodenSupportBeam.TOP, isTop);
	}

	public BlockState getRandomLitCandle(Random rand) {
		//return rand.nextBoolean() ? MUD_FLOWER_POT_CANDLE_UNLIT : MUD_FLOWER_POT_CANDLE_LIT;
		return MUD_FLOWER_POT_CANDLE_UNLIT; // lighting updates kill atm
	}

	public BlockState getRandomMushroom(Random rand) {
		int type = rand.nextInt(30);
		if (type < 10)
			return FLAT_HEAD_MUSHROOM;
		else if (type < 20)
			return BLACK_HAT_MUSHROOM;
		else
			return ROTBULB;
	}

	public BlockState getRandomFloorPlant(Random rand) {
		return rand.nextBoolean() ? TALL_SLUDGECREEP : PALE_GRASS;
	}

	public BlockState getRandomHangingPlant(Random rand) {
		return rand.nextBoolean() ? CRYPTWEED : STRING_ROOTS;
	}

	public BlockState getRandomEdgePlant(Random rand, Direction facing) {
		int type = rand.nextInt(3); //expand for more types
		switch (type) {
		case 0:
			return EDGE_SHROOM.setValue(BlockEdgePlant.FACING, facing);
		case 1:
			return EDGE_MOSS.setValue(BlockEdgePlant.FACING, facing);
		case 2:
			return EDGE_LEAF.setValue(BlockEdgePlant.FACING, facing);
		}
		return EDGE_SHROOM.setValue(BlockEdgePlant.FACING, facing);
	}

	public BlockState getMudTilesWater(Random rand) {
		int randDirection = rand.nextInt(4);
		BlockState state = MUD_TILES_WATER;
		switch (randDirection) {
		case 0:
			state = MUD_TILES_WATER.setValue(BlockMudTilesWater.FACING, Direction.NORTH);
			break;
		case 1:
			state = MUD_TILES_WATER.setValue(BlockMudTilesWater.FACING, Direction.SOUTH);
			break;
		case 2:
			state = MUD_TILES_WATER.setValue(BlockMudTilesWater.FACING, Direction.WEST);
			break;
		case 3:
			state = MUD_TILES_WATER.setValue(BlockMudTilesWater.FACING, Direction.EAST);
			break;
		}
		return state;
	}

	public void setRandomRoot(World world, BlockPos pos, Random rand) {
			if (!isSolidStructureBlock(world.getBlockState(pos)) && world.getBlockState(pos.below()).getBlock() instanceof BlockMudTiles) {
				int rnd = rand.nextInt(32);
				if (rnd < 8) {
					this.dungeon.setBlockAndNotifyAdequately(world, pos, ROOT);
				} else if (rnd < 16) {
					this.dungeon.setBlockAndNotifyAdequately(world, pos, ROOT);
					if (world.isEmptyBlock(pos.above(1)))
						this.dungeon.setBlockAndNotifyAdequately(world, pos.above(1), ROOT);
				} else if (rnd < 24) {
					this.dungeon.setBlockAndNotifyAdequately(world, pos, ROOT);
					if (world.isEmptyBlock(pos.above(1)) && world.isEmptyBlock(pos.above(2))) {
						this.dungeon.setBlockAndNotifyAdequately(world, pos.above(1), ROOT);
						this.dungeon.setBlockAndNotifyAdequately(world, pos.above(2), ROOT);
					}
				} else {
					this.dungeon.setBlockAndNotifyAdequately(world, pos, ROOT);
					if (world.isEmptyBlock(pos.above(1)) && world.isEmptyBlock(pos.above(2)) && world.isEmptyBlock(pos.above(3))) {
						this.dungeon.setBlockAndNotifyAdequately(world, pos.above(1), ROOT);
						this.dungeon.setBlockAndNotifyAdequately(world, pos.above(2), ROOT);
						this.dungeon.setBlockAndNotifyAdequately(world, pos.above(3), ROOT);
					}
				}
			}
	}

	public BlockState getRandomLootUrn(Random rand, Direction facing) {
		int type = rand.nextInt(3);
		switch (type) {
		case 0:
			return LOOT_URN_1.setValue(BlockLootUrn.FACING, facing);
		case 1:
			return LOOT_URN_2.setValue(BlockLootUrn.FACING, facing);
		case 2:
			return LOOT_URN_3.setValue(BlockLootUrn.FACING, facing);
		}
		return LOOT_URN_1.setValue(BlockLootUrn.FACING, facing);
	}

	public void setGreatSword(World world, Random rand, BlockPos pos) {
		TileEntity tile = world.getBlockEntity(pos);
		if (tile instanceof TileEntityGroundItem) {
			((TileEntityGroundItem) tile).setStack(new ItemStack(ItemRegistry.ANCIENT_GREATSWORD));
			world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		}
	}
	
	public void setBattleAxe(World world, Random rand, BlockPos pos) {
		TileEntity tile = world.getBlockEntity(pos);
		if (tile instanceof TileEntityGroundItem) {
			((TileEntityGroundItem) tile).setStack(new ItemStack(ItemRegistry.ANCIENT_BATTLE_AXE));
			world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		}
	}

	public void placeArmourStandLoot(World world, BlockPos pos, Direction facing, Random rand) {
		ItemStack helm = new ItemStack(ItemRegistry.ANCIENT_HELMET);
		ItemStack chest = new ItemStack(ItemRegistry.ANCIENT_CHESTPLATE);
		ItemStack legs = new ItemStack(ItemRegistry.ANCIENT_LEGGINGS);
		ItemStack boots = new ItemStack(ItemRegistry.ANCIENT_BOOTS);
		ArmorStandEntity stand = new ArmorStandEntity(world);
		stand.setPositionAndRotation(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, facing.getHorizontalAngle(), 0F);
		stand.setItemStackToSlot(EquipmentSlotType.HEAD, helm);
		stand.setItemStackToSlot(EquipmentSlotType.CHEST, chest);
		stand.setItemStackToSlot(EquipmentSlotType.LEGS, legs);
		stand.setItemStackToSlot(EquipmentSlotType.FEET, boots);
		world.addFreshEntity(stand);
	}

	/// TOWER STUFF

	// TODO improve for more types
	public @Nullable BlockState getStairsForTowerLevel(Random rand, int level, Direction facing, EnumHalf half, boolean bricks) {
		BlockState state = SMOOTH_PITSTONE_STAIRS;
		if(bricks)
			state = PITSTONE_BRICK_STAIRS;
		return state.setValue(BlockStairsBetweenlands.FACING, facing).setValue(BlockStairsBetweenlands.HALF, half);
	}
	
	public @Nullable BlockState getEnergyBarrier(boolean flipped) {
		return DIAGONAL_ENERGY_BARRIER.setValue(BlockDiagonalEnergyBarrier.FLIPPED, flipped);
	}

	public boolean isSolidStructureBlock(BlockState state) {
		return STRUCTURE_BLOCKS.get(state) != null;
	}

	private void initStuctureBlockMap() {
		if (STRUCTURE_BLOCKS.isEmpty()) {
			STRUCTURE_BLOCKS.put(COMPACTED_MUD, true);
			STRUCTURE_BLOCKS.put(ROTTEN_BARK, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_SLAB, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_SLAB_DECAY_1, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_SLAB_DECAY_2, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_SLAB_DECAY_3, true);
			STRUCTURE_BLOCKS.put(COMPACTED_MUD, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_DECAY_1, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_DECAY_2, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_DECAY_3, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_DECAY_4, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_DECAY_1, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_DECAY_2, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_DECAY_3, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_DECAY_4, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_EDGE, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_EDGE_DECAY_1, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_EDGE_DECAY_2, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_EDGE_DECAY_3, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CARVED_EDGE_DECAY_4, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_STAIRS, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_STAIRS_DECAY_1, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_STAIRS_DECAY_2, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_STAIRS_DECAY_3, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_TOP, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_TOP_DECAY_1, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_TOP_DECAY_2, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_TOP_DECAY_3, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_TOP_DECAY_4, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_TOP_DECAY_FULL, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_DECAY_1, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_DECAY_2, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_DECAY_3, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_DECAY_4, true);
			STRUCTURE_BLOCKS.put(WORM_DUNGEON_PILLAR_DECAY_FULL, true);
			STRUCTURE_BLOCKS.put(MUD_TILES, true);
			STRUCTURE_BLOCKS.put(MUD_TILES_DECAY, true);
			STRUCTURE_BLOCKS.put(MUD_TILES_CRACKED, true);
			STRUCTURE_BLOCKS.put(MUD_TILES_CRACKED_DECAY, true);
			STRUCTURE_BLOCKS.put(ROOT, true);
			STRUCTURE_BLOCKS.put(DUNGEON_DOOR_COMBINATION_EAST, true);
			STRUCTURE_BLOCKS.put(DUNGEON_DOOR_COMBINATION_WEST, true);
			STRUCTURE_BLOCKS.put(DUNGEON_DOOR_EAST, true);
			STRUCTURE_BLOCKS.put(DUNGEON_DOOR_WEST, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_ALCOVE_NORTH, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_ALCOVE_EAST, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_ALCOVE_SOUTH, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_ALCOVE_WEST, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CLIMBABLE_NORTH, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CLIMBABLE_EAST, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CLIMBABLE_SOUTH, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_CLIMBABLE_WEST, true);
			STRUCTURE_BLOCKS.put(MUD_FLOWER_POT_CANDLE_LIT, true);
			STRUCTURE_BLOCKS.put(MUD_FLOWER_POT_CANDLE_UNLIT, true);
			STRUCTURE_BLOCKS.put(LOOT_URN_1, true);
			STRUCTURE_BLOCKS.put(LOOT_URN_2, true);
			STRUCTURE_BLOCKS.put(LOOT_URN_3, true);
			STRUCTURE_BLOCKS.put(MUD_BRICK_WALL, true);
			STRUCTURE_BLOCKS.put(ITEM_SHELF, true);
			STRUCTURE_BLOCKS.put(WOODEN_SUPPORT_BEAM_ROTTEN_1, true);
			STRUCTURE_BLOCKS.put(WOODEN_SUPPORT_BEAM_ROTTEN_2, true);
			STRUCTURE_BLOCKS.put(WOODEN_SUPPORT_BEAM_ROTTEN_3, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_1, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_2, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_3, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_4, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_5, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_6, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_7, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_8, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_9, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_10, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_11, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_12, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_13, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_14, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_15, true);
			STRUCTURE_BLOCKS.put(LOG_ROTTEN_BARK_CARVED_16, true);
			STRUCTURE_BLOCKS.put(CHEST, true);
			STRUCTURE_BLOCKS.put(MUD_BRICKS_SPIKE_TRAP, true);
			STRUCTURE_BLOCKS.put(MUD_TILES_SPIKE_TRAP, true);
		}
	}


}