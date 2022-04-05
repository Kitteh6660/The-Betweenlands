package thebetweenlands.common.world.gen.feature.tree;

import java.util.Random;
import java.util.Stack;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;



public class Fungus 
{
	private int posX;
	private int posY;
	private int posZ;

	private int radius;

	public Fungus(BlockPos pos, int radius) {
		this.posX = pos.getX();
		this.posY = pos.getY();
		this.posZ = pos.getZ();
		this.radius = radius;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public int getPosZ() {
		return posZ;
	}

	public int getRadius() {
		return radius;
	}

	public void generate(World world, Random rand) {
		for (int y = posY; y > posY - radius; y -= 2) {
			if (radius > 1) {
				radius--;
			}
			BlockPos center = new BlockPos(posX, y, posZ);
			Stack<BlockPos> pendingCoords = new Stack<BlockPos>();
			pendingCoords.add(center);
			while (!pendingCoords.isEmpty()) {
				BlockPos coord = pendingCoords.pop();
				if (world.getBlockState(coord).getMaterial().isReplaceable() || world.getBlockState(coord.above()).getMaterial().isReplaceable()) {
					world.setBlockAndUpdate(coord, BlockRegistry.SHELF_FUNGUS.get().defaultBlockState());
				}
				for (Direction direction : GiantTreeTrunkFeature.DIRECTIONS) {
					BlockPos neighborCoord = new BlockPos(coord.getX() + direction.getStepX(), coord.getY(), coord.getZ() + direction.getStepZ());
					BlockState block = world.getBlockState(neighborCoord);
					BlockState above = world.getBlockState(neighborCoord.above());
					if (!pendingCoords.contains(neighborCoord) && getDistanceBetweenChunkCoordinates(center, neighborCoord) <= radius && (block.getMaterial().isReplaceable() || (above.getMaterial().isReplaceable() && block.getBlock() != BlockRegistry.SHELF_FUNGUS.get()))) {
						pendingCoords.add(neighborCoord);
					}
				}
			}
		}
	}

	private int getDistanceBetweenChunkCoordinates(BlockPos a, BlockPos b) {
		return (int) Math.round(Math.sqrt(a.distSqr(new Vector3i(b.getX(), b.getY(), b.getZ()))));
	}
}
