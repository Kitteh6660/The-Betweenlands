package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.BlockRegistry;

public class BLOreBlock extends OreBlock {

	public BLOreBlock(Properties properties) {
		super(properties);
		/*super(materialIn);
		this.setDefaultCreativeTab()
		.setSoundType2(SoundType.STONE)
		.setHardness(1.5F)
		.setResistance(10.0F);*/
	}

	protected int xpOnDrop(Random rand) {
		if (this == BlockRegistry.SULFUR_ORE.get()) {
			return MathHelper.nextInt(rand, 0, 2);
		}
		else if (this == BlockRegistry.SLIMY_BONE_ORE.get()) {
			return MathHelper.nextInt(rand, 1, 4);
		}
		else if (this == BlockRegistry.VALONITE_ORE.get()) {
			return MathHelper.nextInt(rand, 5, 12);
		}
		else if (this == BlockRegistry.SCABYST_ORE.get()) {
			return MathHelper.nextInt(rand, 4, 10);
		}
		else return 0;
	}

	@Override
	public int getExpDrop(BlockState state, IWorldReader reader, BlockPos pos, int fortune, int silktouch) {
		return silktouch == 0 ? this.xpOnDrop(RANDOM) : 0;
	}

	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		double pixel = 0.0625D;
		if(rand.nextInt(3) == 0) {
			for (int l = 0; l < 5; l++) {
				double particleX = pos.getX() + rand.nextFloat();
				double particleY = pos.getY() + rand.nextFloat();
				double particleZ = pos.getZ() + rand.nextFloat();

				if (l == 0 && !worldIn.getBlockState(pos.above()).canOcclude())
					particleY = pos.getY() + 1 + pixel;

				if (l == 1 && !worldIn.getBlockState(pos.below()).canOcclude())
					particleY = pos.getY() - pixel;

				if (l == 2 && !worldIn.getBlockState(pos.offset(0, 0, 1)).canOcclude())
					particleZ = pos.getZ() + 1 + pixel;

				if (l == 3 && !worldIn.getBlockState(pos.offset(0, 0, -1)).canOcclude())
					particleZ = pos.getZ() - pixel;

				if (l == 4 && !worldIn.getBlockState(pos.offset(1, 0, 0)).canOcclude())
					particleX = pos.getX() + 1 + pixel;

				if (l == 5 && !worldIn.getBlockState(pos.offset(-1, 0, 0)).canOcclude())
					particleX = pos.getX() - pixel;

				if (particleX < pos.getX() || particleX > pos.getX() + 1 || particleY < pos.getY() || particleY > pos.getY() + 1 || particleZ < pos.getZ() || particleZ > pos.getZ() + 1) {
					this.addParticle(worldIn, particleX, particleY, particleZ);
				}
			}
		}
	}

	public void addParticle(World world, double x, double y, double z) { 
		if (this == BlockRegistry.OCTINE_ORE.get()) {
			BLParticles.FLAME.spawn(world, x, y, z);
		}
		else if (this == BlockRegistry.SULFUR_ORE.get()) {
			BLParticles.SULFUR_ORE.spawn(world, x, y, z);
		}
	}
}
