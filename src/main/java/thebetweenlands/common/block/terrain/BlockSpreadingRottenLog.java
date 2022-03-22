package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.EntityTermite;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockSpreadingRottenLog extends BlockSpreadingDeath {
	public BlockSpreadingRottenLog() {
		super(Material.WOOD);
		this.setHardness(2.0F);
		this.setSoundType(SoundType.WOOD);
		this.setHarvestLevel("axe", 0);
		this.setCreativeTab(BLCreativeTabs.PLANTS);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public boolean isWood(IBlockReader world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean canSpreadInto(World world, BlockPos pos, BlockState state, BlockPos offsetPos, BlockState offsetState) {
		return offsetState.getBlock() == BlockRegistry.LOG_SPIRIT_TREE;
	}

	@Override
	public void spreadInto(World world, BlockPos pos, BlockState state, BlockPos offsetPos, BlockState offsetState) {
		world.setBlockState(offsetPos, this.defaultBlockState());
	}
	
	@Override
	protected boolean shouldSpread(World world, BlockPos pos, BlockState state) {
		return world.rand.nextInt(4) == 0;
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(BlockRegistry.LOG_ROTTEN_BARK);
	}

	@Override
	public void onPlayerDestroy(World world, BlockPos pos, BlockState state) {
		super.onPlayerDestroy(world, pos, state);

		if (!world.isClientSide() && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
			if (world.rand.nextInt(6) == 0) {
				EntityTermite entity = new EntityTermite(world);
				entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
				if(!entity.isNotColliding()) {
					entity.setSmall(true);
				}
				world.spawnEntity(entity);
			}
		}
	}
}
