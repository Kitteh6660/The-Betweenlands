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
import thebetweenlands.common.world.gen.feature.WorldGenLakeCavernSimulacrum;
import thebetweenlands.common.world.gen.feature.structure.WorldGenCragrockTower;
import thebetweenlands.common.world.gen.feature.structure.WorldGenSludgeWormDungeon;
import thebetweenlands.common.world.gen.feature.structure.WorldGenSpawner;
import thebetweenlands.common.world.gen.feature.structure.WorldGenSpawnerStructure;
import thebetweenlands.common.world.gen.feature.structure.WorldGenWightFortress;


//MINE!!
public class TestItem extends Item {
	public TestItem() {
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
		/*
			WorldGenWightFortress fortress = new WorldGenWightFortress();
			fortress.generate(worldIn, itemRand, pos.above());
		*/
				
			WorldGenLakeCavernSimulacrum gen = new WorldGenLakeCavernSimulacrum();
			gen.generate(worldIn, itemRand, pos.above());
			
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
		/*
			WorldGenCragrockTower tower = new WorldGenCragrockTower();

			if(tower.generate(worldIn, itemRand, pos.above(8).add(8, 0, 0))) {
				//playerIn.setItemInHand(hand, null);
			}
		*/
		/*
			WorldGenSludgeWormDungeon dungeon = new WorldGenSludgeWormDungeon();
			//dungeon.makeMaze(worldIn, itemRand, pos.above().add(1, 0, 1));
			//dungeon.generateTower(worldIn, itemRand, pos.offset(15, 0, 15));
			dungeon.generate(worldIn, itemRand, pos.above().add(1, 0, 1));
		*/

			//BlockGroundItem.create(worldIn, pos.above(), new ItemStack(ItemRegistry.ANCIENT_GREATSWORD));

		/*
			WorldGenNibbletwigTree tree = new WorldGenNibbletwigTree();
			if(tree.generate(worldIn, itemRand, pos.above(1))) {
				//playerIn.setItemInHand(hand, null);
			}
		*/
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
		}

		return ActionResultType.SUCCESS;
	}
	
	@Override
	public CreativeTabs getCreativeTab() {
		return BetweenlandsConfig.DEBUG.debug ? BLCreativeTabs.SPECIALS : null;
	}
}
