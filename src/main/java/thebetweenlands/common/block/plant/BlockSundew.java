package thebetweenlands.common.block.plant;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockSundew extends BlockDoublePlantBL {
	
	public BlockSundew(Properties properties) {
		super(properties);
		this.setSickleDrop(new ItemStack(ItemRegistry.SUNDEW_HEAD.get()));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (world.random.nextInt(35) == 0) {
			BLParticles.FLY.spawn(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		}
	}
}
