package thebetweenlands.common.block.terrain;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityTermite;

public class BlockRottenLog extends BlockLogBetweenlands {
	
	public BlockRottenLog(Properties properties) {
		super(properties);
	}

	@Override
	public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.playerWillDestroy(world, pos, state, player);

		if (!world.isClientSide() && world.getDifficulty() != Difficulty.PEACEFUL) {
			if (world.random.nextInt(6) == 0) {
				EntityTermite entity = new EntityTermite(world);
				entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
				if(!entity.canBeCollidedWith()) {
					entity.setSmall(true);
				}
				world.addFreshEntity(entity);
			}
		}
	}
	
	// To be removed...
	/*@Override
	public void getSubBlocks(CreativeTabs item, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, 0));
	}*/
}