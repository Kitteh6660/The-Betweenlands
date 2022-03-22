package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;
import java.util.UUID;

import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands.EnumBlockHalfBL;
import thebetweenlands.common.block.structure.BlockStairsBetweenlands;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.block.terrain.BlockCragrock.EnumCragrockType;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.world.gen.feature.WorldGenBlockReplacementCluster;
import thebetweenlands.common.world.gen.feature.WorldGenHelper;
import thebetweenlands.common.world.gen.feature.WorldGenMossCluster;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;


public class WorldGenIdolHeads extends WorldGenHelper {
	private BlockState solid = BlockRegistry.SMOOTH_CRAGROCK.defaultBlockState();
	private BlockState slab = BlockRegistry.SMOOTH_CRAGROCK_SLAB.defaultBlockState();
	private BlockState stairs = BlockRegistry.SMOOTH_CRAGROCK_STAIRS.defaultBlockState();
	private BlockState octine = BlockRegistry.OCTINE_BLOCK.defaultBlockState();

	private final WorldGenerator crackGen = new WorldGenBlockReplacementCluster(BlockRegistry.CRAGROCK.defaultBlockState(), BlockMatcher.forBlock(BlockRegistry.SMOOTH_CRAGROCK));
	private final WorldGenerator crackGenSlabs = new WorldGenBlockReplacementCluster(BlockRegistry.CRAGROCK_BRICK_SLAB.defaultBlockState(), BlockMatcher.forBlock(BlockRegistry.SMOOTH_CRAGROCK_SLAB)).setInheritProperties(true);
	private final WorldGenerator crackGenStairs = new WorldGenBlockReplacementCluster(BlockRegistry.CRAGROCK_BRICK_STAIRS.defaultBlockState(), BlockMatcher.forBlock(BlockRegistry.SMOOTH_CRAGROCK_STAIRS)).setInheritProperties(true);
	private final WorldGenerator mossGen = new WorldGenBlockReplacementCluster(BlockRegistry.CRAGROCK.defaultBlockState().setValue(BlockCragrock.VARIANT, EnumCragrockType.MOSSY_2), BlockMatcher.forBlock(BlockRegistry.SMOOTH_CRAGROCK));
	private final WorldGenerator lichenClusterGen = new WorldGenMossCluster(BlockRegistry.LICHEN.defaultBlockState());
	private final WorldGenerator mossClusterGen = new WorldGenMossCluster(BlockRegistry.MOSS.defaultBlockState());

	public WorldGenIdolHeads() {
		depth = 8;
		width = 8;
		height = 8;
	}

	public boolean generateStructure(World world, Random rand, int x, int y, int z) {
		// air check
		BlockPos.Mutable checkPos = new BlockPos.Mutable();
		for (int xx = x - depth / 2; xx <= x + depth / 2; ++xx) {
			for(int zz = z - width / 2; zz <= z + width / 2; ++zz) {
				for(int yy = y + 1; yy < y + height; ++yy ) {
					if(!world.isEmptyBlock(checkPos.setPos(xx, yy, zz)) && !world.getBlockState(checkPos.setPos(xx, yy, zz)).getBlock().isReplaceable(world, checkPos.setPos(xx, yy, zz))) {
						return false;
					}
				}
				for (int yy = y; yy >= y - 5; yy--) {
					if(!world.isEmptyBlock(checkPos.setPos(xx, yy, zz)) && !world.getBlockState(checkPos.setPos(xx, yy, zz)).getBlock().isReplaceable(world, checkPos.setPos(xx, yy, zz)))
						break;
					if(yy <= y - 5)
						return false;
				}
			}
		}

		int direction = rand.nextInt(4);
		int headType = rand.nextInt(3);
		int xx = x;//- depth / 2;
		int zz = z;//- width / 2;

		switch (headType) {
		case 0:// Gold Head
			rotatedCubeVolume(world, xx, y, zz, 1, 0, 2, solid, 6, 4, 5, direction);
			rotatedCubeVolume(world, xx, y, zz, 0, 3, 4, solid, 1, 2, 2, direction);
			rotatedCubeVolume(world, xx, y, zz, 7, 3, 4, solid, 1, 2, 2, direction);
			rotatedCubeVolume(world, xx, y, zz, 1, 5, 1, solid, 6, 2, 6, direction);
			rotatedCubeVolume(world, xx, y, zz, 1, 4, 3, solid, 6, 1, 4, direction);
			rotatedCubeVolume(world, xx, y, zz, 3, 3, 0, solid, 2, 4, 3, direction);
			rotatedCubeVolume(world, xx, y, zz, 2, 3, 1, solid, 1, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 5, 3, 1, solid, 1, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 3, 7, 1, solid, 2, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 3, 7, 3, solid, 2, 1, 2, direction);
			rotatedCubeVolume(world, xx, y, zz, 3, 7, 6, solid, 2, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 3, 6, 7, solid, 2, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 3, 4, 7, solid, 2, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 2, 4, 3, octine, 1, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 5, 4, 3, octine, 1, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 2, 1, 1, slab, 4, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 2, 2, 1, slab.setValue(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 4, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 1, 1, 1, 
					direction == 0 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.WEST) : 
						direction == 2 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.EAST) : 
							direction == 1 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.NORTH) : 
								stairs.setValue(BlockStairsBetweenlands.FACING, Direction.SOUTH), 1, 1, 1, direction); //bottom right
			rotatedCubeVolume(world, xx, y, zz, 6, 1, 1, 
					direction == 0 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.EAST) : 
						direction == 2 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.WEST) : 
							direction == 1 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.SOUTH) : 
								stairs.setValue(BlockStairsBetweenlands.FACING, Direction.NORTH), 1, 1, 1, direction); //bottom left
			rotatedCubeVolume(world, xx, y, zz, 1, 2, 1, 
					direction == 0 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.WEST).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
						direction == 2 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.EAST).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
							direction == 1 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.NORTH).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
								stairs.setValue(BlockStairsBetweenlands.FACING, Direction.SOUTH).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, direction); //top right
			rotatedCubeVolume(world, xx, y, zz, 6, 2, 1, 
					direction == 0 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.EAST).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
						direction == 2 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.WEST).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
							direction == 1 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.SOUTH).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
								stairs.setValue(BlockStairsBetweenlands.FACING, Direction.NORTH).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, direction); //top left
			break;

		case 1:// Silver Head
			rotatedCubeVolume(world, xx, y, zz, 1, 0, 2, solid, 6, 4, 5, direction);
			rotatedCubeVolume(world, xx, y, zz, 0, 1, 4, solid, 1, 5, 2, direction);
			rotatedCubeVolume(world, xx, y, zz, 7, 1, 4, solid, 1, 5, 2, direction);
			rotatedCubeVolume(world, xx, y, zz, 3, 1, 7, solid, 2, 5, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 1, 4, 3, solid, 6, 3, 4, direction);
			rotatedCubeVolume(world, xx, y, zz, 1, 7, 1, solid, 2, 1, 6, direction);
			rotatedCubeVolume(world, xx, y, zz, 1, 6, 1, slab.setValue(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 2, 1, 2, direction);
			rotatedCubeVolume(world, xx, y, zz, 3, 7, 1, slab, 2, 1, 6, direction);
			rotatedCubeVolume(world, xx, y, zz, 5, 7, 1, solid, 2, 1, 6, direction);
			rotatedCubeVolume(world, xx, y, zz, 5, 6, 1, slab.setValue(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 2, 1, 2, direction); 
			rotatedCubeVolume(world, xx, y, zz, 3, 3, 0, solid, 2, 3, 3, direction);
			rotatedCubeVolume(world, xx, y, zz, 3, 6, 1, solid, 2, 1, 2, direction);
			rotatedCubeVolume(world, xx, y, zz, 1, 4, 2, slab, 2, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 5, 4, 2, slab, 2, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 2, 3, 1, solid, 4, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 2, 5, 3, octine, 1, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 5, 5, 3, octine, 1, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 3, 1, 1, 
					direction == 0 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.WEST) : 
						direction == 2 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.EAST) : 
							direction == 1 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.NORTH) : 
								stairs.setValue(BlockStairsBetweenlands.FACING, Direction.SOUTH), 1, 1, 1, direction); //bottom right
			rotatedCubeVolume(world, xx, y, zz, 4, 1, 1, 
					direction == 0 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.EAST) : 
						direction == 2 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.WEST) : 
							direction == 1 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.SOUTH) : 
								stairs.setValue(BlockStairsBetweenlands.FACING, Direction.NORTH), 1, 1, 1, direction); //bottom left
			rotatedCubeVolume(world, xx, y, zz, 3, 2, 1, 
					direction == 0 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.WEST).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
						direction == 2 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.EAST).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
							direction == 1 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.NORTH).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
								stairs.setValue(BlockStairsBetweenlands.FACING, Direction.SOUTH).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, direction); //top right
			rotatedCubeVolume(world, xx, y, zz, 4, 2, 1, 
					direction == 0 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.EAST).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
						direction == 2 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.WEST).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
							direction == 1 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.SOUTH).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
								stairs.setValue(BlockStairsBetweenlands.FACING, Direction.NORTH).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, direction); //top left
			break;

		case 2://Bronze Head
			rotatedCubeVolume(world, xx, y, zz, 1, 0, 2, solid, 6, 4, 5, direction);
			rotatedCubeVolume(world, xx, y, zz, 0, 1, 4, solid, 1, 5, 2, direction);
			rotatedCubeVolume(world, xx, y, zz, 7, 1, 4, solid, 1, 5, 2, direction);
			rotatedCubeVolume(world, xx, y, zz, 2, 2, 7, solid, 4, 4, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 1, 6, 1, solid, 6, 1, 6, direction);
			rotatedCubeVolume(world, xx, y, zz, 2, 7, 1, solid, 4, 1, 2, direction);
			rotatedCubeVolume(world, xx, y, zz, 2, 7, 4, solid, 4, 1, 2, direction);
			rotatedCubeVolume(world, xx, y, zz, 1, 4, 3, solid, 6, 2, 4, direction);
			rotatedCubeVolume(world, xx, y, zz, 3, 3, 0, solid, 2, 4, 3, direction);
			rotatedCubeVolume(world, xx, y, zz, 1, 4, 2, slab, 2, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 5, 4, 2, slab, 2, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 2, 3, 1, solid, 4, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 2, 5, 3, octine, 1, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 5, 5, 3, octine, 1, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 3, 1, 1, slab, 2, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 3, 2, 1, slab.setValue(BlockSlabBetweenlands.HALF, EnumBlockHalfBL.TOP), 2, 1, 1, direction);
			rotatedCubeVolume(world, xx, y, zz, 2, 1, 1, 
					direction == 0 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.WEST) : 
						direction == 2 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.EAST) : 
							direction == 1 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.NORTH) : 
								stairs.setValue(BlockStairsBetweenlands.FACING, Direction.SOUTH), 1, 1, 1, direction); //bottom right
			rotatedCubeVolume(world, xx, y, zz, 5, 1, 1, 
					direction == 0 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.EAST) : 
						direction == 2 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.WEST) : 
							direction == 1 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.SOUTH) : 
								stairs.setValue(BlockStairsBetweenlands.FACING, Direction.NORTH), 1, 1, 1, direction); //bottom left
			rotatedCubeVolume(world, xx, y, zz, 2, 2, 1, 
					direction == 0 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.WEST).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
						direction == 2 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.EAST).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
							direction == 1 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.NORTH).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
								stairs.setValue(BlockStairsBetweenlands.FACING, Direction.SOUTH).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, direction); //top right
			rotatedCubeVolume(world, xx, y, zz, 5, 2, 1, 
					direction == 0 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.EAST).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
						direction == 2 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.WEST).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
							direction == 1 ? stairs.setValue(BlockStairsBetweenlands.FACING, Direction.SOUTH).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP) : 
								stairs.setValue(BlockStairsBetweenlands.FACING, Direction.NORTH).setValue(BlockStairsBetweenlands.HALF, EnumHalf.TOP), 1, 1, 1, direction); //top left
			break;
		}

		for(int i = 0; i < 40 + rand.nextInt(160); i++) {
			int type = rand.nextInt(4);
			switch(type) {
			default:
			case 0:
				this.crackGen.generate(world, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
				break;
			case 1:
				this.crackGenSlabs.generate(world, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
				break;
			case 2:
				this.crackGenStairs.generate(world, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
				break;
			case 3:
				this.mossGen.generate(world, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
				break;
			}

			if(rand.nextInt(4) == 0) {
				if(rand.nextInt(8) == 0) {
					this.lichenClusterGen.generate(world, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
				} else {
					this.mossClusterGen.generate(world, rand, new BlockPos(x - 3 + rand.nextInt(6), y + 6 - 3 + rand.nextInt(6), z - 3 + rand.nextInt(6)));
				}
			}
		}

		world.setBlockState(new BlockPos(x, y - 1, z), BlockRegistry.WEEDWOOD_CHEST.defaultBlockState());
		TileEntity tile = world.getBlockEntity(new BlockPos(x, y - 1, z));
		if (tile instanceof TileEntityChest) {
			((TileEntityChest) tile).setLootTable(LootTableRegistry.IDOL_HEADS_CHEST, rand.nextLong());
		}
		
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		AxisAlignedBB locationAABB = rotatedAABB(world, xx, y, zz, 0, 0, 0, 8, 8, 8, direction).grow(3, 3, 3);
		LocationStorage locationStorage = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(x, z), "idol_head", EnumLocationType.IDOL_HEAD);
		locationStorage.setSeed(rand.nextLong());
		locationStorage.addBounds(locationAABB);
		locationStorage.setVisible(false);
		locationStorage.setDirty(true);
		worldStorage.getLocalStorageHandler().addLocalStorage(locationStorage);
		
		return true;
	}

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		return generateStructure(worldIn, rand, position.getX(), position.getY(), position.getZ());
	}
}
