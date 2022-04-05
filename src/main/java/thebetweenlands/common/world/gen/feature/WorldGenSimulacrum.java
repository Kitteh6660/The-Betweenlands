package thebetweenlands.common.world.gen.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import thebetweenlands.common.block.misc.BlockOfferingTable;
import thebetweenlands.common.block.structure.BlockSimulacrum;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityGroundItem;
import thebetweenlands.common.tile.TileEntitySimulacrum;

public class WorldGenSimulacrum extends WorldGenerator {
	private final List<BlockSimulacrum> variants;
	private final ResourceLocation lootTableLocation;

	public WorldGenSimulacrum(List<BlockSimulacrum> variants, ResourceLocation lootTable) {
		this.variants = variants;
		this.lootTableLocation = lootTable;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		if(!level.isClientSide() && world.isEmptyBlock(position)) {
			for(int i = 0; i < 8 && world.isEmptyBlock(position); i++) {
				position = position.below();
			}

			position = position.above();

			if(world.isEmptyBlock(position) && world.isSideSolid(position.below(), Direction.UP) && this.canGenerateHere(world, rand, position)) {
				BlockSimulacrum block = this.variants.get(rand.nextInt(this.variants.size()));

				Direction facing = null;

				if(world instanceof ServerWorld && this.shouldGenerateOfferingTable(rand)) {
					List<Direction> offsets = new ArrayList<>(Arrays.asList(Direction.Plane.HORIZONTAL));
					Collections.shuffle(offsets, rand);

					for(Direction dir : offsets) {
						BlockPos offset = position.offset(dir);

						if(world.isEmptyBlock(offset) && world.isSideSolid(offset.below(), Direction.UP)) {
							this.setBlockAndNotifyAdequately(world, offset, BlockRegistry.OFFERING_TABLE.defaultBlockState().setValue(BlockOfferingTable.FACING, dir.getOpposite()));

							facing = dir;

							TileEntity tile = world.getBlockEntity(offset);
							if(tile instanceof TileEntityGroundItem) {
								LootTable lootTable = ((ServerWorld) world).getLootTableManager().getLootTableFromLocation(this.lootTableLocation);

								if(lootTable != null) {
									LootContext.Builder lootBuilder = new LootContext.Builder((ServerWorld) world);

									List<ItemStack> loot = lootTable.generateLootForPools(rand, lootBuilder.build());

									if(!loot.isEmpty()) {
										((TileEntityGroundItem) tile).setStack(loot.get(0));
									}
								}
							}

							break;
						}
					}
				}

				BlockState state = block.defaultBlockState()
						.setValue(BlockSimulacrum.VARIANT, BlockSimulacrum.Variant.values()[rand.nextInt(BlockSimulacrum.Variant.values().length)])
						.setValue(BlockSimulacrum.FACING, facing == null ? Direction.Plane.HORIZONTAL[rand.nextInt(Direction.Plane.HORIZONTAL.length)] : facing);

				this.setBlockAndNotifyAdequately(world, position, state);

				TileEntity tile = world.getBlockEntity(position);
				if(tile instanceof TileEntitySimulacrum) {
					((TileEntitySimulacrum) tile).setEffect(TileEntitySimulacrum.Effect.values()[rand.nextInt(TileEntitySimulacrum.Effect.values().length)]);
				}

				int torches = rand.nextInt(3);

				for(int i = 0; i < 32 && torches > 0; i++) {
					int rx = rand.nextInt(5) - 2;
					int rz = rand.nextInt(5) - 2;

					if((rx != 0 || rz != 0) && Math.abs(rx) + Math.abs(rz) <= 2) {
						BlockPos offset = position.add(rx, rand.nextInt(3) - 2, rz);

						if(world.isEmptyBlock(offset) && world.isSideSolid(offset.below(), Direction.UP)) {
							this.setBlockAndNotifyAdequately(world, offset, BlockRegistry.MUD_FLOWER_POT_CANDLE.defaultBlockState());
							torches--;
						}
					}
				}

				return true;
			}
		}

		return false;
	}

	protected boolean shouldGenerateOfferingTable(Random rand) {
		return rand.nextInt(3) != 0;
	}

	protected boolean canGenerateHere(World world, Random rand, BlockPos pos) {
		return true;
	}
}
