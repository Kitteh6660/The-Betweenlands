package thebetweenlands.common.block;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.common.block.property.PropertyIntegerUnlisted;

public interface IConnectedTextureBlock {
	public static interface IConnectionRules {
		/**
		 * Can be used to create a block access cache to speed up lookups
		 * @param world
		 * @param pos The position of the connected texture block
		 * @return same as input world or some kind of cached block access
		 */
		public default IBlockReader getBlockAccessCache(IBlockReader world, BlockPos pos) {
			return world;
		}

		/**
		 * Returns whether the face can connect to the specified block pos
		 * @param world
		 * @param pos The position of this connected texture block
		 * @param face The face of this connected texture block
		 * @param to The position that it tries to connect to
		 * @return
		 */
		public boolean canConnectTo(IBlockReader world, BlockPos pos, Direction face, BlockPos.Mutable to);

		/**
		 * Returns whether the face can connect through the specified block pos.
		 * The default implementation lets the texture connect through any non solid
		 * sides
		 * @param world
		 * @param pos The position of this connected texture block
		 * @param face The face of this connected texture block
		 * @param to The position that it tries to connect through
		 * @return
		 */
		public default boolean canConnectThrough(IBlockReader world, BlockPos pos, Direction face, BlockPos.Mutable to) {
			//The block tries to connect to one of its own other sides
			Axis axis = face.getAxis();
			BlockPos planeOffset = new BlockPos(axis == Axis.X ? 0 : (to.getX() - pos.getX()), axis == Axis.Y ? 0 : (to.getY() - pos.getY()), axis == Axis.Z ? 0 : (to.getZ() - pos.getZ()));
			boolean onSamePlane = (axis != Axis.X || (to.getX() - pos.getX()) == 0) && (axis != Axis.Y || (to.getY() - pos.getY()) == 0) && (axis != Axis.Z || (to.getZ() - pos.getZ()) == 0);
			if(Math.abs(planeOffset.getX()) + Math.abs(planeOffset.getY()) + Math.abs(planeOffset.getZ()) > 1) {
				BlockPos.Mutable directNeighbour = new BlockPos.Mutable();
				//Diagonal block connection
				for(Direction offsetFace : Direction.values()) {
					//Check if offsetFace points to a neighbour of the connected texture block
					if(Math.abs(planeOffset.getX() + offsetFace.getStepX()) + Math.abs(planeOffset.getY() + offsetFace.getStepY()) + Math.abs(planeOffset.getZ() + offsetFace.getStepZ()) == 1) {
						//Check if either sides of the diagonal block that point to a direct neighbour of the connected texture block are solid
						if(onSamePlane) {
							if(world.isSideSolid(to, offsetFace, false) || world.isSideSolid(to, face, false)) {
								return false;
							}
						} else {
							if(world.isSideSolid(to, offsetFace, false) || world.isSideSolid(to, face.getOpposite(), false)) {
								return false;
							}
						}
						directNeighbour.setPos(to.getX() + offsetFace.getStepX(), to.getY() + offsetFace.getStepY(), to.getZ() + offsetFace.getStepZ());
						//Check if direct neighbour can be connected to, if not it needs to check if it lets a conncetion through
						if(!this.canConnectTo(world, pos, face, directNeighbour)) {
							//Check if either sides of the direct neighbour block of the connected texture block are solid
							if(onSamePlane) {
								if(world.isSideSolid(directNeighbour, offsetFace.getOpposite(), false) || world.isSideSolid(directNeighbour, face, false)) {
									return false;
								}
							} else {
								if(world.isSideSolid(directNeighbour, offsetFace.getOpposite(), false) || world.isSideSolid(directNeighbour, face.getOpposite(), false)) {
									return false;
								}
							}
						}
					}
				}
				return true;
			} else {
				//Direct neighbour
				Direction offsetDir = Direction.getNearest(planeOffset.getX(), planeOffset.getY(), planeOffset.getZ());
				if(onSamePlane) {
					return !world.isSideSolid(to, offsetDir.getOpposite(), false) && !world.isSideSolid(to, face, false);
				} else {
					return !world.isSideSolid(to, offsetDir.getOpposite(), false) && !world.isSideSolid(to, face.getOpposite(), false);
				}
			}
		}
	}

	//-1, +1, -1, quadrant 0
	public static final PropertyIntegerUnlisted TOP_NORTH_WEST_INDEX = new PropertyIntegerUnlisted("top_north_west_index");
	//+1, +1, -1, quadrant 1
	public static final PropertyIntegerUnlisted TOP_NORTH_EAST_INDEX = new PropertyIntegerUnlisted("top_north_east_index");
	//-1, +1, +1, quadrant 2
	public static final PropertyIntegerUnlisted TOP_SOUTH_WEST_INDEX = new PropertyIntegerUnlisted("top_south_west_index");
	//+1, +1, +1, quadrant 3
	public static final PropertyIntegerUnlisted TOP_SOUTH_EAST_INDEX = new PropertyIntegerUnlisted("top_south_east_index");

	//+1, -1, -1, quadrant 0
	public static final PropertyIntegerUnlisted BOTTOM_NORTH_EAST_INDEX = new PropertyIntegerUnlisted("bottom_north_east_index");
	//-1, -1, -1, quadrant 1
	public static final PropertyIntegerUnlisted BOTTOM_NORTH_WEST_INDEX = new PropertyIntegerUnlisted("bottom_north_west_index");
	//+1, -1, +1, quadrant 2
	public static final PropertyIntegerUnlisted BOTTOM_SOUTH_EAST_INDEX = new PropertyIntegerUnlisted("bottom_south_east_index");
	//-1, -1, +1, quadrant 3
	public static final PropertyIntegerUnlisted BOTTOM_SOUTH_WEST_INDEX = new PropertyIntegerUnlisted("bottom_south_west_index");

	//-1, +1, -1, quadrant 0
	public static final PropertyIntegerUnlisted NORTH_UP_WEST_INDEX = new PropertyIntegerUnlisted("north_up_west_index");
	//+1, +1, -1, quadrant 1
	public static final PropertyIntegerUnlisted NORTH_UP_EAST_INDEX = new PropertyIntegerUnlisted("north_up_east_index");
	//-1, -1, -1, quadrant 2
	public static final PropertyIntegerUnlisted NORTH_DOWN_WEST_INDEX = new PropertyIntegerUnlisted("north_down_west_index");
	//+1, -1, -1, quadrant 3
	public static final PropertyIntegerUnlisted NORTH_DOWN_EAST_INDEX = new PropertyIntegerUnlisted("north_down_east_index");

	//-1, -1, +1, quadrant 0
	public static final PropertyIntegerUnlisted SOUTH_DOWN_WEST_INDEX = new PropertyIntegerUnlisted("south_down_west_index");
	//+1, -1, +1, quadrant 1
	public static final PropertyIntegerUnlisted SOUTH_DOWN_EAST_INDEX = new PropertyIntegerUnlisted("south_down_east_index");
	//-1, +1, +1, quadrant 2
	public static final PropertyIntegerUnlisted SOUTH_UP_WEST_INDEX = new PropertyIntegerUnlisted("south_up_west_index");
	//+1, +1, +1, quadrant 3
	public static final PropertyIntegerUnlisted SOUTH_UP_EAST_INDEX = new PropertyIntegerUnlisted("south_up_east_index");

	//-1, -1, -1, quadrant 0
	public static final PropertyIntegerUnlisted WEST_DOWN_NORTH_INDEX = new PropertyIntegerUnlisted("west_down_north_index");
	//-1, -1, +1, quadrant 1
	public static final PropertyIntegerUnlisted WEST_DOWN_SOUTH_INDEX = new PropertyIntegerUnlisted("west_down_south_index");
	//-1, +1, -1, quadrant 2
	public static final PropertyIntegerUnlisted WEST_UP_NORTH_INDEX = new PropertyIntegerUnlisted("west_up_north_index");
	//-1, +1, +1, quadrant 3
	public static final PropertyIntegerUnlisted WEST_UP_SOUTH_INDEX = new PropertyIntegerUnlisted("west_up_south_index");

	//+1, -1, +1, quadrant 0
	public static final PropertyIntegerUnlisted EAST_DOWN_SOUTH_INDEX = new PropertyIntegerUnlisted("east_down_south_index");
	//+1, -1, -1, quadrant 1
	public static final PropertyIntegerUnlisted EAST_DOWN_NORTH_INDEX = new PropertyIntegerUnlisted("east_down_north_index");
	//+1, +1, +1, quadrant 2
	public static final PropertyIntegerUnlisted EAST_UP_SOUTH_INDEX = new PropertyIntegerUnlisted("east_up_south_index");
	//+1, +1, -1, quadrant 3
	public static final PropertyIntegerUnlisted EAST_UP_NORTH_INDEX = new PropertyIntegerUnlisted("east_up_north_index");

	/**
	 * Adds the connected texture properties to the block state container
	 * @param container
	 * @return
	 */
	public default ExtendedBlockState getConnectedTextureBlockStateContainer(ExtendedBlockState container) {
		List<IUnlistedProperty<?>> props = new ArrayList<>();
		if(this.isFaceConnectedTexture(Direction.UP)) {
			props.add(TOP_NORTH_WEST_INDEX);
			props.add(TOP_NORTH_EAST_INDEX);
			props.add(TOP_SOUTH_WEST_INDEX);
			props.add(TOP_SOUTH_EAST_INDEX);
		}
		if(this.isFaceConnectedTexture(Direction.DOWN)) {
			props.add(BOTTOM_NORTH_WEST_INDEX);
			props.add(BOTTOM_NORTH_EAST_INDEX);
			props.add(BOTTOM_SOUTH_WEST_INDEX);
			props.add(BOTTOM_SOUTH_EAST_INDEX);
		}
		if(this.isFaceConnectedTexture(Direction.NORTH)) {
			props.add(NORTH_UP_WEST_INDEX);
			props.add(NORTH_UP_EAST_INDEX);
			props.add(NORTH_DOWN_WEST_INDEX);
			props.add(NORTH_DOWN_EAST_INDEX);
		}
		if(this.isFaceConnectedTexture(Direction.SOUTH)) {
			props.add(SOUTH_UP_WEST_INDEX);
			props.add(SOUTH_UP_EAST_INDEX);
			props.add(SOUTH_DOWN_WEST_INDEX);
			props.add(SOUTH_DOWN_EAST_INDEX);
		}
		if(this.isFaceConnectedTexture(Direction.WEST)) {
			props.add(WEST_UP_SOUTH_INDEX);
			props.add(WEST_UP_NORTH_INDEX);
			props.add(WEST_DOWN_SOUTH_INDEX);
			props.add(WEST_DOWN_NORTH_INDEX);
		}
		if(this.isFaceConnectedTexture(Direction.EAST)) {
			props.add(EAST_UP_SOUTH_INDEX);
			props.add(EAST_UP_NORTH_INDEX);
			props.add(EAST_DOWN_SOUTH_INDEX);
			props.add(EAST_DOWN_NORTH_INDEX);
		}
		return BlockStateContainerHelper.extendBlockstateContainer(container, new IProperty[0], props.toArray(new IUnlistedProperty[0]));
	}

	/**
	 * Returns whether the specified face has a connected texture
	 * @param face
	 * @return
	 */
	public default boolean isFaceConnectedTexture(Direction face) {
		return true;
	}

	/**
	 * Updates and sets the state of the connected texture properties
	 * @param state
	 * @param world
	 * @param pos
	 * @param canConnectTo Determines which blocks this block can connect to
	 * @param connectToSelf Whether the block can connect to its own faces
	 * @return
	 */
	public default BlockState getExtendedConnectedTextureState(IExtendedBlockState state, IBlockReader world, BlockPos pos, Predicate<BlockPos.Mutable> canConnectTo, boolean connectToSelf) {
		IConnectionRules connectionRules = new IConnectionRules() {
			@Override
			public boolean canConnectTo(IBlockReader world, BlockPos pos, Direction face, BlockPos.Mutable to) {
				return canConnectTo.apply(to);
			}

			@Override
			public boolean canConnectThrough(IBlockReader world, BlockPos pos, Direction face, BlockPos.Mutable to) {
				if(connectToSelf) {
					return IConnectionRules.super.canConnectThrough(world, pos, face, to);
				}
				Axis axis = face.getAxis();
				if((axis == Axis.X && to.getX() - pos.getX() != 0) || (axis == Axis.Y && to.getY() - pos.getY() != 0) || (axis == Axis.Z && to.getZ() - pos.getZ() != 0)) {
					//Tries to connect through the block that is next to the connected texture face. This should always be true
					//if the block can't connect to its own faces because otherwise it wouldn't be able to connect to anything
					return true;
				}
				return canConnectTo.apply(to.setPos(to.getX() - face.getStepX(), to.getY() - face.getStepY(), to.getZ() - face.getStepZ()));
			}
		};
		return this.getExtendedConnectedTextureState(state, world, pos, connectionRules);
	}

	/**
	 * Updates and sets the state of the connected texture properties
	 * @param state
	 * @param world
	 * @param pos
	 * @param connectionRules The connection rules determines which blocks this block can connect to and which blocks let a connection through
	 * @return
	 */
	public default BlockState getExtendedConnectedTextureState(IExtendedBlockState state, IBlockReader world, BlockPos pos, IConnectionRules connectionRules) {
		world = connectionRules.getBlockAccessCache(world, pos);

		if(this.isFaceConnectedTexture(Direction.UP)) {
			int[] quadrantIndicesUp = getQuadrantIndices(getConnectionArray(world, pos, Direction.UP, connectionRules), false);
			state = state.setValue(TOP_NORTH_WEST_INDEX, quadrantIndicesUp[0]);
			state = state.setValue(TOP_NORTH_EAST_INDEX, quadrantIndicesUp[1]);
			state = state.setValue(TOP_SOUTH_WEST_INDEX, quadrantIndicesUp[2]);
			state = state.setValue(TOP_SOUTH_EAST_INDEX, quadrantIndicesUp[3]);
		}

		if(this.isFaceConnectedTexture(Direction.DOWN)) {
			int[] quadrantIndicesDown = getQuadrantIndices(getConnectionArray(world, pos, Direction.DOWN, connectionRules), true);
			state = state.setValue(BOTTOM_NORTH_EAST_INDEX, quadrantIndicesDown[0]);
			state = state.setValue(BOTTOM_NORTH_WEST_INDEX, quadrantIndicesDown[1]);
			state = state.setValue(BOTTOM_SOUTH_EAST_INDEX, quadrantIndicesDown[2]);
			state = state.setValue(BOTTOM_SOUTH_WEST_INDEX, quadrantIndicesDown[3]);
		}

		if(this.isFaceConnectedTexture(Direction.NORTH)) {
			int[] quadrantIndicesNorth = getQuadrantIndices(getConnectionArray(world, pos, Direction.NORTH, connectionRules), false);
			state = state.setValue(NORTH_UP_WEST_INDEX, quadrantIndicesNorth[0]);
			state = state.setValue(NORTH_UP_EAST_INDEX, quadrantIndicesNorth[1]);
			state = state.setValue(NORTH_DOWN_WEST_INDEX, quadrantIndicesNorth[2]);
			state = state.setValue(NORTH_DOWN_EAST_INDEX, quadrantIndicesNorth[3]);
		}

		if(this.isFaceConnectedTexture(Direction.SOUTH)) {
			int[] quadrantIndicesSouth = getQuadrantIndices(getConnectionArray(world, pos, Direction.SOUTH, connectionRules), false);
			state = state.setValue(SOUTH_DOWN_WEST_INDEX, quadrantIndicesSouth[0]);
			state = state.setValue(SOUTH_DOWN_EAST_INDEX, quadrantIndicesSouth[1]);
			state = state.setValue(SOUTH_UP_WEST_INDEX, quadrantIndicesSouth[2]);
			state = state.setValue(SOUTH_UP_EAST_INDEX, quadrantIndicesSouth[3]);
		}

		if(this.isFaceConnectedTexture(Direction.WEST)) {
			int[] quadrantIndicesWest = getQuadrantIndices(getConnectionArray(world, pos, Direction.WEST, connectionRules), true);
			state = state.setValue(WEST_DOWN_NORTH_INDEX, quadrantIndicesWest[0]);
			state = state.setValue(WEST_UP_NORTH_INDEX, quadrantIndicesWest[1]);
			state = state.setValue(WEST_DOWN_SOUTH_INDEX, quadrantIndicesWest[2]);
			state = state.setValue(WEST_UP_SOUTH_INDEX, quadrantIndicesWest[3]);
		}

		if(this.isFaceConnectedTexture(Direction.EAST)) {
			int[] quadrantIndicesEast = getQuadrantIndices(getConnectionArray(world, pos, Direction.EAST, connectionRules), true);
			state = state.setValue(EAST_UP_NORTH_INDEX, quadrantIndicesEast[0]);
			state = state.setValue(EAST_DOWN_NORTH_INDEX, quadrantIndicesEast[1]);
			state = state.setValue(EAST_UP_SOUTH_INDEX, quadrantIndicesEast[2]);
			state = state.setValue(EAST_DOWN_SOUTH_INDEX, quadrantIndicesEast[3]);
		}

		return state;
	}

	/**
	 * Returns the quadrant indices<p>
	 * <p>
	 *
	 * @param connectionArray <p>Connection states, index 4 is the center:
	 *                        <pre>
	 *                                                -------
	 *                                               | 0 1 2 |
	 *                                               | 3 4 5 |
	 *                                               | 6 7 8 |
	 *                                                ------- </pre>
	 * @param flipXZ Flips the X and Z axis, used for EAST, WEST and DOWN face
	 * @return <p>Returned index positions:
	 * <pre>
	 *  -------> 
	 * | 0-----1
	 * | |     |
	 * | |     |
	 * | 2-----3
	 * \/
	 * Which corner corresponds to which coordinates depends on the face the connectionArray was created for.
	 * </pre>
	 * <p>Texture segment indices (arrangement depends on the texture size):
	 * <pre>
	 *  -------
	 * | 0 1 2 |
	 * | 3 4 . |
	 * | . . . |
	 *  ------- </pre>
	 * <ol start = "0">
	 * <li>No connections</li>
	 * <li>Straight connection to the left and right</li>
	 * <li>Straight connection to the top and bottom</li>
	 * <li>Sharp corner</li>
	 * <li>Smooth corner</li>
	 * </ol>
	 */
	public static int[] getQuadrantIndices(boolean[] connectionArray, boolean flipXZ) {
		int tls = 0;
		int trs = 0;
		int bls = 0;
		int brs = 0;
		for (int xo = 0; xo <= 2; xo++) {
			for (int zo = 0; zo <= 2; zo++) {
				boolean currentNeighbourState = connectionArray[getIndex(xo, zo, 3)];
				int segXPath = 1;
				int segZPath = 2;
				if(flipXZ) {
					segXPath = 2;
					segZPath = 1;
				}
				if ((xo != 1 && zo == 1) || (xo == 1 && zo != 1)) {
					//Adjacent neighbour
					if (currentNeighbourState) {
						if (xo == 0) {
							if (!connectionArray[getIndex(1, 2, 3)]) bls = segXPath;
							if (!connectionArray[getIndex(1, 0, 3)]) tls = segXPath;
						} else if (xo == 2) {
							if (!connectionArray[getIndex(1, 2, 3)]) brs = segXPath;
							if (!connectionArray[getIndex(1, 0, 3)]) trs = segXPath;
						} else if (zo == 0) {
							if (!connectionArray[getIndex(0, 1, 3)]) tls = segZPath;
							if (!connectionArray[getIndex(2, 1, 3)]) trs = segZPath;
						} else if (zo == 2) {
							if (!connectionArray[getIndex(0, 1, 3)]) bls = segZPath;
							if (!connectionArray[getIndex(2, 1, 3)]) brs = segZPath;
						}
					}
				} else if (xo != 1 && zo != 1) {
					//Diagonal neighbour
					if (connectionArray[getIndex(xo, 1, 3)] && connectionArray[getIndex(1, zo, 3)]) {
						int segment;
						if (currentNeighbourState) {
							//Full sharp corner
							segment = 3;
						} else {
							//Smooth half corner
							segment = 4;
						}
						if (xo == 2 && zo == 0) {
							trs = segment;
						} else if (xo == 2 && zo == 2) {
							brs = segment;
						} else if (xo == 0 && zo == 2) {
							bls = segment;
						} else {
							tls = segment;
						}
					}
				}
			}
		}
		return new int[]{tls, trs, bls, brs};
	}

	/**
	 * Creates the connection array
	 *
	 * @param pos
	 * @param dir         Face
	 * @param canConnectTo Returns whether this block can connect to the specified block pos
	 * @param canConnectThrough Returns whether this block can connect through the specified block pos;
	 * @return Connection array
	 */
	public static boolean[] getConnectionArray(IBlockReader world, BlockPos pos, Direction dir, IConnectionRules connectionRules) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		boolean xp = true;
		boolean yp = true;
		boolean xr = false;
		boolean yr = false;
		boolean zr = false;

		boolean[] connectionArray = new boolean[9];

		switch (dir) {
		case DOWN:
			xp = false;
		case UP:
			xr = true;
			zr = true;
			break;
		case NORTH:
			yp = false;
		case SOUTH:
			xr = true;
			yr = true;
			break;
		case EAST:
			xp = false;
		case WEST:
			zr = true;
			yr = true;
			break;
		default:
			return connectionArray;
		}
		PooledMutableBlockPos checkPos = PooledMutableBlockPos.retain();

		try {
			for (int xo = xr ? -1 : 0; xo <= (xr ? 1 : 0); xo++) {
				for (int yo = yr ? -1 : 0; yo <= (yr ? 1 : 0); yo++) {
					for (int zo = zr ? -1 : 0; zo <= (zr ? 1 : 0); zo++) {
						int mx = (xr ? xo : yo) + 1;
						int my = (zr ? zo : (xr ? yo : zo)) + 1;
						int blockIndex = getIndex(xp ? mx : 2 - mx, yp ? my : 2 - my, 3);

						checkPos.setPos(x + dir.getStepX(), y + dir.getStepY(), z + dir.getStepZ());

						if(connectionRules.canConnectThrough(world, pos, dir, checkPos)) {
							Axis axis = dir.getAxis();

							if((axis == Axis.X && (yo != 0 || zo != 0)) || (axis == Axis.Y && (xo != 0 || zo != 0)) || (axis == Axis.Z && (xo != 0 || yo != 0))) {

								BlockPos.Mutable diagPos = checkPos.setPos(axis == Axis.X ? (x + dir.getStepX()) : (x + xo), axis == Axis.Y ? (y + dir.getStepY()) : (y + yo), axis == Axis.Z ? (z + dir.getStepZ()) : (z + zo));

								boolean isDiagConnectable = connectionRules.canConnectTo(world, pos, dir, diagPos);

								if(isDiagConnectable || connectionRules.canConnectThrough(world, pos, dir, diagPos)) {

									BlockPos.Mutable obstructionPos = checkPos.setPos(axis == Axis.X ? x : (x + xo), axis == Axis.Y ? y : (y + yo), axis == Axis.Z ? z : (z + zo));

									if(isDiagConnectable || connectionRules.canConnectThrough(world, pos, dir, obstructionPos)) {
										connectionArray[blockIndex] = true;
									} else {
										connectionArray[blockIndex] = connectionRules.canConnectTo(world, pos, dir, checkPos.setPos(x + xo, y + yo, z + zo));
									}
								}
							}
						}
					}
				}
			}
		} finally {
			checkPos.release();
		}

		return connectionArray;
	}

	/**
	 * Calculates an index for the given coordinates and the matrix width
	 *
	 * @param x     X Coordinate
	 * @param y     Y Coordinate
	 * @param width Matrix width
	 * @return Index
	 */
	public static int getIndex(int x, int y, int width) {
		return x % width + y * width;
	}
}
