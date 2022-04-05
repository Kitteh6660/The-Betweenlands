package thebetweenlands.common.block.terrain;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.world.storage.BetweenlandsChunkStorage;

public class BlockCircleGem extends BLOreBlock {
	
	public final CircleGemType gem;

	public BlockCircleGem(CircleGemType gemType, Properties properties) {
		super(properties);
		/*super(Material.ROCK);
		this.setHarvestLevel2("pickaxe", 1);
		this.setLightLevel(0.8F);*/
		this.gem = gem;
	}

	@Override
	public void onPlace(World worldIn, BlockPos pos, BlockState state) {
		super.onPlace(worldIn, pos, state);

		if(this.gem.gemSingerTarget != null) {
			BetweenlandsChunkStorage.markGem(worldIn, pos, this.gem.gemSingerTarget);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
		super.breakBlock(worldIn, pos, state);

		if(this.gem.gemSingerTarget != null) {
			BetweenlandsChunkStorage.unmarkGem(worldIn, pos, this.gem.gemSingerTarget);
		}
	}
}
