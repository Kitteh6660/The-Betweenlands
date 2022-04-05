package thebetweenlands.common.block.plant;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;

public class BlockPhragmites extends BlockDoublePlantBL {
	
	public BlockPhragmites(Properties properties) {
		super(properties);
		//this.setSickleDrop(EnumItemPlantDrop.PHRAGMITE_STEMS.create(1));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (world.random.nextInt(15) == 0) {
			if (world.random.nextInt(6) != 0) {
				BLParticles.FLY.spawn(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			} else {
				BLParticles.MOTH.spawn(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			}
		}
	}
}
