package thebetweenlands.common.item.misc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.gen.feature.structure.WorldGenChiromawNest;


//MINE!!
public class TestItemChimp extends Item {
	public TestItemChimp() {
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hitResult) {
			if (!worldIn.isClientSide()) {
		/*	BlockState state = worldIn.getBlockState(pos);
			if (state.getBlock() instanceof BlockGenericDugSoil) {
				TileEntityDugSoil te = (TileEntityDugSoil) worldIn.getBlockEntity(pos);
				te.setDecay(20);
			}
		/*	
		/*
			WorldGenTarPoolDungeon gen = new WorldGenTarPoolDungeon();
			gen.generate(worldIn, itemRand, pos.above());
		*/
		/*
			WorldGenDruidCircle worldGenDruidCircle = new WorldGenDruidCircle();
			worldGenDruidCircle.generateStructure(worldIn, itemRand, pos.above());
		*/
		/*
            WorldGenIdolHeads head = new WorldGenIdolHeads();
            head.generate(worldIn, itemRand, pos.above());
        */
		/*
            WorldGenSpawnerStructure smallRuins = new WorldGenSpawnerStructure();
            smallRuins.generate(worldIn, itemRand, pos.above());
		*/
		
		/*	WorldGenWightFortress fortress = new WorldGenWightFortress();
			fortress.generate(worldIn, itemRand, pos.above());
		*/
		/*
			WorldGenSmallRuins ruins = new WorldGenSmallRuins();
			ruins.generate(worldIn, itemRand, pos.above());
		*/
		/*
			if(player.isCrouching()) {
				BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(worldIn);
				List<SharedStorage> storages = worldStorage.getSharedStorageAt(SharedStorage.class, (storage) -> {
					if(storage instanceof LocationStorage) {
						return ((LocationStorage)storage).isInside(pos);
					}
					return true;
				}, pos.getX(), pos.getZ());
				for(SharedStorage storage : storages) {
					worldStorage.removeSharedStorage(storage);
				}
			} else {
				WorldGenWightFortress fortress = new WorldGenWightFortress();
				fortress.generate(worldIn, itemRand, pos.above());
			}
		*/
		/*
			ItemAspectContainer container = ItemAspectContainer.fromItem(player.getItemInHand(hand));
			if(!player.isCrouching()) {
				container.add(AspectRegistry.AZUWYNN, 10);
				System.out.println("Added: 10");
			} else {
				System.out.println("Drained: " + container.drain(AspectRegistry.AZUWYNN, 8));
			}
		*/
		/*
			WorldGenSpawner spawner = new WorldGenSpawner();
			if(spawner.generate(worldIn, itemRand, pos)) {
				//playerIn.setItemInHand(hand, null);
			}
		*/
		
		/*	WorldGenCragrockTower tower = new WorldGenCragrockTower();

		*/
		/*	WorldGenCragrockTower tower = new WorldGenCragrockTower();

			if(tower.generate(worldIn, itemRand, pos.above(8).add(8, 0, 0))) {
				//playerIn.setItemInHand(hand, null);
			}
		 	
		*/

		/*
			WorldGenNibbletwigTree tree = new WorldGenNibbletwigTree();
			if(tree.generate(worldIn, itemRand, pos.above(1))) {
				//playerIn.setItemInHand(hand, null);
			}
		
		/*
			WorldGenHearthgroveTree tree = new WorldGenHearthgroveTree();
			if(tree.generate(worldIn, itemRand, pos.above(1))) {
				//playerIn.setItemInHand(hand, null);
			}
		*/
		/*
			WorldGenSmallSpiritTree tree = new WorldGenSmallSpiritTree();
			if(tree.generate(worldIn, itemRand, pos.above(1))) {
				//playerIn.setItemInHand(hand, null);
			}
		*/
		/*
			WorldGenSpiritTreeStructure tree = new WorldGenSpiritTreeStructure();
			if(tree.generate(worldIn, itemRand, pos.above(1))) {
				//playerIn.setItemInHand(hand, null);
			}

		
		/*
			ItemStack stack = player.getItemInHand(hand);
			CompoundNBT nbt = stack.getOrCreateSubCompound("pos");
			
			if(!nbt.contains("x1")) {
				nbt.putInt("x1", pos.getX());
				nbt.putInt("y1", pos.getY());
				nbt.putInt("z1", pos.getZ());
			} else {
				long time = System.nanoTime();
				
				WorldGenGiantRoot root = new WorldGenGiantRoot(new BlockPos(nbt.getInt("x1"), nbt.getInt("y1"), nbt.getInt("z1")), pos, 14);
				root.generate(worldIn, itemRand, pos);
				
				System.out.println("ms: " + (System.nanoTime() - time) / 1000000.0F);
				
				nbt.removeTag("x1");
			}
		*/
			/*
			WorldGenMangroveTree tree = new WorldGenMangroveTree();
			tree.generate(worldIn, itemRand, pos.offset(0, 10, 0));
			*/
			/*WorldGenGiantTree tree = new WorldGenGiantTree();
			tree.generate(worldIn, itemRand, pos.offset(0, 10, 0));*/
		/*
			WorldGenSmallPortal portal = new WorldGenSmallPortal(player.getDirection());
			if(portal.generate(worldIn, itemRand, pos.above())) {
				//playerIn.setItemInHand(hand, null);
			}
	*/
		/*		WorldGenSludgeWormDungeon dungeon = new WorldGenSludgeWormDungeon();
			//dungeon.makeMaze(worldIn, itemRand, pos.above().add(1, 0, 1));
			//dungeon.generate(worldIn, itemRand, pos.above(59).add(3, 0, 3)); //generates up
			dungeon.generate(worldIn, itemRand, pos.above(1).add(3, 0, 3)); //generates down
			//dungeon.generateTower(worldIn, itemRand, pos.offset(15, 0, 15));
			//dungeon.generateDecayPit(worldIn, itemRand, pos.above(14));
	
		
	
			EntityDecayPitTarget target = new EntityDecayPitTarget(worldIn);
			target.setPosition(pos.getX() + 0.5F, pos.getY() + 8, pos.getZ() + 0.5F);
			worldIn.spawnEntity(target);
	
			
	
			EntityTinyWormEggSac sac = new EntityTinyWormEggSac(worldIn);
			sac.setPosition(pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F);
			worldIn.spawnEntity(sac);

*/
	/*		EntitySplodeshroom trap = new EntitySplodeshroom(worldIn);
			BlockPos offset = pos.offset(facing);
			trap.setPosition(offset.getX() + 0.5F, offset.getY(), offset.getZ() + 0.5F);
			worldIn.spawnEntity(trap);
		*/
			/*	
			EntityCCGroundSpawner trap = new EntityCCGroundSpawner(worldIn);
			trap.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
			trap.onInitialSpawn(worldIn.getDifficultyForLocation(trap.getPosition()), null);
			worldIn.spawnEntity(trap);*/
			/*
				EntityMovingWall wall = new EntityMovingWall(worldIn);
				wall.setPosition(pos.getX() + 0.5F, pos.getY() + 2F, pos.getZ() + 0.5F);
				//wall.motionZ = 0.05F; //X or Z movement - renderer rotates automagic atm
				worldIn.spawnEntity(wall);
			*/	
				
				WorldGenChiromawNest nest = new WorldGenChiromawNest();
				nest .generate(worldIn, itemRand, pos.above());
				
		}

		return ActionResultType.SUCCESS;
	}
	
	@Override
	public CreativeTabs getCreativeTab() {
		return BetweenlandsConfig.DEBUG.debug ? BLCreativeTabs.SPECIALS : null;
	}
}
