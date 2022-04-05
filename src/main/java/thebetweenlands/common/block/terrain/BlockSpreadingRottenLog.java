package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.EntityTermite;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockSpreadingRottenLog extends BlockSpreadingDeath {
	
	public BlockSpreadingRottenLog(Properties properties) {
		super(properties);
		/*super(Material.WOOD);
		this.setHardness(2.0F);
		this.setSoundType(SoundType.WOOD);
		this.setHarvestLevel("axe", 0);
		this.setCreativeTab(BLCreativeTabs.PLANTS);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	@Override
	public boolean canSpreadInto(World world, BlockPos pos, BlockState state, BlockPos offsetPos, BlockState offsetState) {
		return offsetState.getBlock() == BlockRegistry.SPIRIT_TREE_LOG.get() || offsetState.getBlock() == BlockRegistry.SPIRIT_TREE_WOOD.get();
	}

	@Override
	public void spreadInto(World world, BlockPos pos, BlockState state, BlockPos offsetPos, BlockState offsetState) {
		world.setBlockAndUpdate(offsetPos, this.defaultBlockState());
	}
	
	@Override
	protected boolean shouldSpread(World world, BlockPos pos, BlockState state) {
		return world.random.nextInt(4) == 0;
	}

	@Override
	public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.playerWillDestroy(world, pos, state, player);
		if (this == BlockRegistry.STRIPPED_ROTTEN_LOG.get() || this == BlockRegistry.STRIPPED_ROTTEN_LOG.get()) {
			return; // Stripping logs eliminates termites.
		}
		if (!world.isClientSide() && world.getDifficulty() != Difficulty.PEACEFUL) {
			if (world.random.nextInt(6) == 0) {
				EntityTermite entity = new EntityTermite(world);
				entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
				if(!entity.isNotColliding()) {
					entity.setSmall(true);
				}
				world.addFreshEntity(entity);
			}
		}
	}
}
