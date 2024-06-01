package thebetweenlands.api.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IFarmablePlant {
	/**
	 * Returns whether the plant is farmable
	 * @param world
	 * @param pos
	 * @param state
	 * @return
	 */
	public boolean isFarmable(Level world, BlockPos pos, BlockState state);

	/**
	 * Returns whether this plant can spread to the target position
	 * @param world
	 * @param pos
	 * @param state
	 * @param targetPos
	 * @param rand
	 * @return
	 */
	public boolean canSpreadTo(Level world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand);

	/**
	 * Returns the spreading chance between 0 and 1
	 * @param world
	 * @param pos
	 * @param state
	 * @param taretPos
	 * @param rand
	 * @return
	 */
	public default float getSpreadChance(Level world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand) {
		return 1;
	}
	
	/**
	 * Returns how much compost is consumed when this plant spreads
	 * @param world
	 * @param pos
	 * @param state
	 * @param rand
	 * @return
	 */
	public int getCompostCost(Level world, BlockPos pos, BlockState state, Random rand);

	/**
	 * Called when the plant is decaying
	 * @param world
	 * @param pos
	 * @param state
	 * @param rand
	 */
	public void decayPlant(Level world, BlockPos pos, BlockState state, Random rand);

	/**
	 * Spreads this plant to the specified target position
	 * @param world
	 * @param pos
	 * @param state
	 * @param targetPos
	 * @param rand
	 */
	public void spreadTo(Level world, BlockPos pos, BlockState state, BlockPos targetPos, Random rand);
}
