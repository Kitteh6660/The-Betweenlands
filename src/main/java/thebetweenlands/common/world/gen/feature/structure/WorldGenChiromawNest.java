package thebetweenlands.common.world.gen.feature.structure;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.block.plant.BlockEdgePlant;
import thebetweenlands.common.block.plant.BlockMoss;
import thebetweenlands.common.block.plant.BlockPlant;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.block.terrain.BlockCragrock.EnumCragrockType;
import thebetweenlands.common.entity.EntityGreeblingCorpse;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.entity.mobs.EntityChiromawMatriarch;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityGroundItem;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationChiromawMatriarchNest;
import thebetweenlands.common.world.storage.location.guard.ILocationGuard;

public class WorldGenChiromawNest extends WorldGenerator {

	public BlockState CRAGROCK = BlockRegistry.CRAGROCK.defaultBlockState();
	public BlockState NESTING_BLOCK_BONES = BlockRegistry.NESTING_BLOCK_BONES.defaultBlockState();
	public BlockState NESTING_BLOCK_STICKS = BlockRegistry.NESTING_BLOCK_STICKS.defaultBlockState();
	public BlockState ROOT = BlockRegistry.ROOT.defaultBlockState();

	//shrooms
	public BlockState BLACK_HAT_MUSHROOM = BlockRegistry.BLACK_HAT_MUSHROOM.defaultBlockState();
	public BlockState FLAT_HEAD_MUSHROOM = BlockRegistry.FLAT_HEAD_MUSHROOM.defaultBlockState();
	public BlockState ROTBULB = BlockRegistry.ROTBULB.defaultBlockState();

	//floor plants
	public BlockState SWAMP_TALLGRASS = BlockRegistry.SWAMP_TALLGRASS.defaultBlockState();
	public BlockState SHOOTS = BlockRegistry.SHOOTS.defaultBlockState();

	//wall plants
	public BlockState MOSS = BlockRegistry.DEAD_MOSS.defaultBlockState();
	public BlockState LICHEN = BlockRegistry.DEAD_LICHEN.defaultBlockState();

	//edge plants
	public BlockState EDGE_SHROOM = BlockRegistry.EDGE_SHROOM.defaultBlockState();
	public BlockState EDGE_MOSS = BlockRegistry.EDGE_MOSS.defaultBlockState();
	public BlockState EDGE_LEAF = BlockRegistry.EDGE_LEAF.defaultBlockState();
	
	//ground item loot
	public BlockState GROUND_ITEM = BlockRegistry.GROUND_ITEM.defaultBlockState();

	private ILocationGuard guard;
	
	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		for(int xo = -4; xo <= 4; xo++) {
			for(int zo = -4; zo <= 4; zo++) {
				BlockPos checkPos = pos.offset(xo, -1, zo);
				if(xo*xo + zo*zo <= 16 && (world.isEmptyBlock(checkPos) || world.getBlockState(checkPos).getMaterial().isLiquid())) {
					return false;
				}
			}
		}
		
		for(int xo = -2; xo <= 2; xo++) {
			for(int yo = 3; yo <= 6; yo++) {
				for(int zo = -2; zo <= 2; zo++) {
					BlockPos checkPos = pos.offset(xo, yo, zo);
					BlockState state = world.getBlockState(checkPos);
					
					if(!state.getBlock().isReplaceable(world, checkPos) && !SurfaceType.MIXED_GROUND.apply(state) && state.getBlock() instanceof BlockPlant == false) {
						return false;
					}
				}
			}
		}
		
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		
		LocationChiromawMatriarchNest location = new LocationChiromawMatriarchNest(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), pos.above(7));
		location.setVisible(true);
		location.addBounds(new AxisAlignedBB(pos).grow(8, 0, 8).expand(0, 9, 0));
		location.setSeed(rand.nextLong());
		
		this.guard = location.getGuard();
		
		generateRockPile(world, rand, pos);
		generateNest(world, rand, pos.above(6));

		location.setDirty(true);
		worldStorage.getLocalStorageHandler().addLocalStorage(location);
		
		return true;
	}

	public void generateNest(World world, Random rand, BlockPos pos) {
		for (int xx = -3; xx <= 3; xx++) {
			for (int zz = -3; zz <= 3; zz++) {
				for (int yy = 0; yy > -3; yy--) {
					double dSqDome = Math.pow(xx, 2.0D) + Math.pow(zz, 2.0D) + Math.pow(yy, 2.0D);

					if (Math.round(Math.sqrt(dSqDome)) < 4) {
						setBlockAndNotifyAdequately(world, pos.offset(xx, yy, zz), rand.nextInt(4) == 0 ? NESTING_BLOCK_BONES : NESTING_BLOCK_STICKS);

						if (yy == 0 && Math.round(Math.sqrt(dSqDome)) == 1) {
							setBlockAndNotifyAdequately(world, pos.offset(xx, yy, zz), Blocks.AIR.defaultBlockState());
							addEntitiesAndLootBlocks(world, rand, pos.offset(xx, yy, zz));
						}
						if (yy == 0 && Math.round(Math.sqrt(dSqDome)) == 0) {
							addMatiarch(world, rand, pos.offset(xx, yy, zz));
						}
					}
					setBlockAndNotifyAdequately(world, pos.offset(0, yy, 0),  CRAGROCK.setValue(BlockCragrock.VARIANT, getCragrockForYLevel(rand, yy + 3)));
				}
			}
		}
	}

	private void addMatiarch(World world, Random rand, BlockPos pos) {
		EntityChiromawMatriarch matriarch = new EntityChiromawMatriarch(world);
		matriarch.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		matriarch.onInitialSpawn(world.getDifficultyForLocation(pos), null);
		world.spawnEntity(matriarch);
	}

	private void addEntitiesAndLootBlocks(World world, Random rand, BlockPos pos) {
		if (rand.nextBoolean()) {
			EntityChiromawHatchling egg = new EntityChiromawHatchling(world);
			egg.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			if (rand.nextBoolean())
				egg.setIsWild(true);
			world.spawnEntity(egg);
		}
		else if(rand.nextBoolean() && rand.nextBoolean()) {
			EntityGreeblingCorpse corpse = new EntityGreeblingCorpse(world);
			corpse.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			world.spawnEntity(corpse);
		}
		else {
			setBlockAndNotifyAdequately(world, pos, GROUND_ITEM);
			setScatteredLoot(world, rand, pos);
		}
	}

	public void setScatteredLoot(World world, Random rand, BlockPos pos) {
		TileEntity tile = world.getBlockEntity(pos);
		if (tile instanceof TileEntityGroundItem) {
			ItemStack stack = getScatteredLoot(world, rand);
			((TileEntityGroundItem) tile).setStack(stack);
			world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		}
	}
	
	public ItemStack getScatteredLoot(World world, Random rand) {
		LootTable lootTable = world.getLootTableManager().getLootTableFromLocation(getScatteredLootTable());
		if (lootTable != null) {
			LootContext.Builder lootBuilder = (new LootContext.Builder((ServerWorld) world));
			List<ItemStack> loot = lootTable.generateLootForPools(rand, lootBuilder.build());
			if (!loot.isEmpty()) {
				Collections.shuffle(loot); // mix it up a bit
				return loot.get(0);
			}
		}
		return EnumItemMisc.SLIMY_BONE.create(1); // to stop null;
	}

	protected ResourceLocation getScatteredLootTable() {
		return LootTableRegistry.CHIROMAW_NEST_SCATTERED_LOOT;
	}

	public void generateRockPile(World world, Random rand, BlockPos pos) {
		for (int xx = -6; xx <= 6; xx++) {
			for (int zz = -6; zz <= 6; zz++) {
				for (int yy = 0; yy < 4; yy++) {
					double dSqDome = Math.pow(xx, 2.0D) + Math.pow(zz, 2.0D) + Math.pow(yy, 2.0D);

					if (yy == 0 && rand.nextBoolean() && Math.round(Math.sqrt(dSqDome)) == 5)
						setBlockAndNotifyAdequately(world, pos.offset(xx, yy, zz), CRAGROCK.setValue(BlockCragrock.VARIANT, getCragrockForYLevel(rand, 1)));

					if (yy == 0 && rand.nextBoolean() && Math.round(Math.sqrt(dSqDome)) == 6)
						if (isPlantableAbove(world, pos.offset(xx, yy, zz)))
							setRandomRoot(world, pos.offset(xx, yy, zz), rand);

					if (Math.round(Math.sqrt(dSqDome)) < 5)
						setBlockAndNotifyAdequately(world, pos.offset(xx, yy, zz), CRAGROCK.setValue(BlockCragrock.VARIANT, getCragrockForYLevel(rand, yy)));
				}
			}
		}

		for (int yy = -1; yy < 4; yy++) {
			addGroundPlants(world, pos.offset(-5, 0, -5), rand, 11, yy, 11, false, true, true);
			addEdgePlant(world, pos.offset(-5, 0, -5), rand, 11, yy, 11);
		}

		addWallPlants(world, pos.offset(2, 0, -2), rand, 3, 4, 5, Direction.EAST);
		addWallPlants(world, pos.offset(-4, 0, -2), rand, 3, 4, 5, Direction.WEST);
		addWallPlants(world, pos.offset(-2, 0, -4), rand, 5, 4, 3, Direction.NORTH);
		addWallPlants(world, pos.offset(-2, 0, 2), rand, 5, 4, 3, Direction.SOUTH);

	}
	
	public void setRandomRoot(World world, BlockPos pos, Random rand) {
		int rnd = rand.nextInt(32);
		if (rnd < 8) {
			setBlockAndNotifyAdequately(world, pos, ROOT);
		} else if (rnd < 16) {
			setBlockAndNotifyAdequately(world, pos, ROOT);
			if (world.isEmptyBlock(pos.above(1)))
				setBlockAndNotifyAdequately(world, pos.above(1), ROOT);
		} else if (rnd < 24) {
			setBlockAndNotifyAdequately(world, pos, ROOT);
			if (world.isEmptyBlock(pos.above(1)) && world.isEmptyBlock(pos.above(2))) {
				setBlockAndNotifyAdequately(world, pos.above(1), ROOT);
				setBlockAndNotifyAdequately(world, pos.above(2), ROOT);
			}
		} else {
			setBlockAndNotifyAdequately(world, pos, ROOT);
			if (world.isEmptyBlock(pos.above(1)) && world.isEmptyBlock(pos.above(2)) && world.isEmptyBlock(pos.above(3))) {
				setBlockAndNotifyAdequately(world, pos.above(1), ROOT);
				setBlockAndNotifyAdequately(world, pos.above(2), ROOT);
				setBlockAndNotifyAdequately(world, pos.above(3), ROOT);
			}
		}
	}

	public void addGroundPlants(World world, BlockPos pos, Random rand, int x, int y, int z, boolean addMoss, boolean addWeeds, boolean addMushrooms) {
		for (int horizontalX = 0; horizontalX < x; horizontalX++)
			for (int horizontalZ = 0; horizontalZ < z; horizontalZ++) {
				if (isPlantableAbove(world, pos.offset(horizontalX, y, horizontalZ)))
					if (addWeeds && plantingChance(rand))
						this.setBlockAndNotifyAdequately(world, pos.offset(horizontalX, y + 1, horizontalZ), getRandomFloorPlant(rand));
					else if (addMushrooms && plantingChance(rand))
						this.setBlockAndNotifyAdequately(world, pos.offset(horizontalX, y + 1, horizontalZ), getRandomMushroom(rand));
					else if (addMoss && rand.nextBoolean())
						this.setBlockAndNotifyAdequately(world, pos.offset(horizontalX, y + 1, horizontalZ), MOSS.setValue(BlockMoss.FACING, Direction.UP));
			}
	}

	public void addEdgePlant(World world, BlockPos pos, Random rand, int x, int y, int z) {
		for (int horizontalX = 0; horizontalX < x; horizontalX++)
			for (int horizontalZ = 0; horizontalZ < z; horizontalZ++) {
				for (Direction facing : Direction.HORIZONTALS) {
					if (world.getBlockState(pos.offset(horizontalX, y + 1, horizontalZ).offset(facing)).isSideSolid(world, pos.offset(horizontalX, y + 1, horizontalZ).offset(facing), facing.getOpposite())) {
						if (plantingChance(rand) && isPlantableAbove(world, pos.offset(horizontalX, y, horizontalZ)))
							this.setBlockAndNotifyAdequately(world, pos.offset(horizontalX, y + 1, horizontalZ), getRandomEdgePlant(rand, facing.getOpposite()));
					}
				}
			}
	}

	public void addWallPlants(World world, BlockPos pos, Random rand, int x, int y, int z, Direction facing) {
		for (int horizontalX = 0; horizontalX < x; horizontalX++)
			for (int horizontalZ = 0; horizontalZ < z; horizontalZ++)
				for (int vertical = 0; vertical < y; vertical++)
					if (plantingChance(rand) && isPlantableWall(world, pos.offset(horizontalX, vertical, horizontalZ), facing))
						setBlockAndNotifyAdequately(world, pos.offset(horizontalX, vertical, horizontalZ).offset(facing), MOSS.setValue(BlockMoss.FACING, facing));
	}

	public boolean isPlantableAbove(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		return SoilHelper.canSustainPlant(state) && world.isEmptyBlock(pos.above());
	}

	public boolean isPlantableWall(World world, BlockPos pos, Direction facing) {
		BlockState state = world.getBlockState(pos);
		return state.isFullBlock() && world.isEmptyBlock(pos.offset(facing));
	}

	public boolean plantingChance(Random rand) {
		return rand.nextBoolean() && rand.nextBoolean();
	}

	public EnumCragrockType getCragrockForYLevel(Random rand, int y) {
		return y < 1 ? EnumCragrockType.DEFAULT : y >= 1 && y < 3 ? (rand.nextBoolean() ? EnumCragrockType.DEFAULT : EnumCragrockType.MOSSY_2) : EnumCragrockType.MOSSY_1;
	}

	public BlockState getRandomFloorPlant(Random rand) {
		return rand.nextBoolean() ? SWAMP_TALLGRASS : SHOOTS; //what plants do we want
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

	public BlockState getRandomEdgePlant(Random rand, Direction facing) {
		int type = rand.nextInt(3);
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

	@Override
	protected void setBlockAndNotifyAdequately(World worldIn, BlockPos pos, BlockState state) {
		super.setBlockAndNotifyAdequately(worldIn, pos, state);
		
		if(this.guard != null && state.getBlock() instanceof BlockPlant == false) {
			this.guard.setGuarded(worldIn, pos, true);
		}
	}
}