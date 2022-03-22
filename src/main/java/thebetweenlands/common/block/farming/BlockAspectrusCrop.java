package thebetweenlands.common.block.farming;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.api.block.IAspectFogBlock;
import thebetweenlands.common.item.food.ItemAspectrusFruit;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityAspectrusCrop;
import thebetweenlands.common.tile.TileEntityDugSoil;

public class BlockAspectrusCrop extends BlockGenericCrop implements ICustomItemBlock, ITileEntityProvider {
	protected static final int MAX_HEIGHT = 3;

	public BlockAspectrusCrop() {
		this.setCreativeTab(null);
		this.setMaxHeight(MAX_HEIGHT);
	}

	public void setAspect(IBlockReader world, BlockPos pos, @Nullable Aspect aspect) {
		TileEntityAspectrusCrop tile = this.getTile(world, pos);
		if(tile != null) {
			tile.setAspect(aspect);
		}
	}

	@Nullable
	public Aspect getAspect(IBlockReader world, BlockPos pos) {
		TileEntityAspectrusCrop tile = this.getTile(world, pos);
		if(tile != null) {
			return tile.getAspect();
		}
		return null;
	}

	@Nullable
	public TileEntityAspectrusCrop getTile(IBlockReader world, BlockPos pos) {
		TileEntity tile = world.getBlockEntity(pos);
		if(tile instanceof TileEntityAspectrusCrop) {
			return (TileEntityAspectrusCrop) tile;
		}
		return null;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		this.updateAspectSource(worldIn, pos, state);

		super.updateTick(worldIn, pos, state, rand);

		Aspect aspect = this.getAspect(worldIn, pos);

		if(aspect != null && worldIn.rand.nextInt(3) == 0) {
			BlockPos.Mutable checkPos = new BlockPos.Mutable();

			checkPos.setPos(pos.getX(), pos.getY() - 1, pos.getZ());

			for(int i = 0; i < MAX_HEIGHT; i++) {
				BlockState offsetState = worldIn.getBlockState(checkPos);

				if(offsetState.getBlock() instanceof BlockGenericDugSoil) {
					if(!((BlockGenericDugSoil)offsetState.getBlock()).isPurified(worldIn, checkPos, offsetState)) {
						TileEntityDugSoil te = BlockGenericDugSoil.getTile(worldIn, checkPos);

						if(te != null && !te.isFullyDecayed()) {
							te.setDecay(te.getDecay() + 5);
						}
					}

					break;
				}

				checkPos.setPos(checkPos.getX(), checkPos.getY() - 1, checkPos.getZ());
			}
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
		return FenceBlock.PILLAR_AABB;
	}

	@Override
	protected boolean removePlant(World world, BlockPos pos, PlayerEntity player, boolean canHarvest) {
		return world.setBlock(pos, BlockRegistry.RUBBER_TREE_PLANK_FENCE.defaultBlockState(), world.isClientSide() ? 11 : 3);
	}

	@Override
	public int getCropDrops(IBlockReader world, BlockPos pos, Random rand, int fortune) {
		BlockState state = world.getBlockState(pos);
		if(state.getValue(AGE) >= 15) {
			return 1 + rand.nextInt(3 + fortune);
		}
		return 0;
	}

	@Override
	public int getSeedDrops(IBlockReader world, BlockPos pos, Random rand, int fortune) {
		BlockState state = world.getBlockState(pos);
		if(state.getValue(AGE) >= 15) {
			return 1 + (rand.nextInt(8) == 0 ? 1 : 0);
		}
		return 1;
	}

	@Override
	protected boolean canGrow(World world, BlockPos pos, BlockState state) {
		TileEntityAspectrusCrop tile = this.getTile(world, pos);
		return tile.getAspect() != null && tile.hasSource();
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return false;
	}

	@Override
	protected boolean canGrowUp(World world, BlockPos pos, BlockState state, int height) {
		if(this.maxHeight == -1 || height < this.maxHeight) {
			BlockPos posUp = pos.above();
			BlockState upState = world.getBlockState(posUp);
			if(upState.getBlock() == BlockRegistry.RUBBER_TREE_PLANK_FENCE) {
				for(Direction dir : Direction.Axis.Y) {
					if(BlockRegistry.RUBBER_TREE_PLANK_FENCE.canBeConnectedTo(world, posUp, dir)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	protected void growUp(World world, BlockPos pos) {
		super.growUp(world, pos);

		Aspect aspect = this.getAspect(world, pos);
		if(aspect != null) {
			BlockPos posUp = pos.above();

			TileEntityAspectrusCrop tile = this.getTile(world, posUp);
			if(tile != null) {
				tile.setAspect(aspect);
				tile.setHasSource(true);
			}
		}
	}

	@Override
	protected PropertyInteger createStageProperty() {
		return PropertyInteger.create("stage", 0, 15);
	}

	@Override
	public ItemStack getSeedDrop(IBlockReader world, BlockPos pos, Random rand) {
		return new ItemStack(ItemRegistry.ASPECTRUS_SEEDS);	
	}

	@Override
	public ItemStack getCropDrop(IBlockReader world, BlockPos pos, Random rand) {
		if(!this.isDecayed(world, pos)) {
			ItemStack stack = new ItemStack(ItemRegistry.ASPECTRUS_FRUIT);
			Aspect aspect = this.getAspect(world, pos);
			if(aspect != null) {
				ItemAspectContainer.fromItem(stack).set(aspect.type, aspect.amount);
			}
			return stack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(ItemRegistry.ASPECTRUS_SEEDS);
	}

	@Override
	public BlockItem getItemBlock() {
		return null;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
		super.breakBlock(worldIn, pos, state);
		worldIn.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityAspectrusCrop();
	}

	/**
	 * Updates the plant's aspect by looking for nearby aspect fog block producing an aspect
	 * @param world
	 * @param pos
	 * @param state
	 * @return
	 */
	protected void updateAspectSource(World world, BlockPos pos, BlockState state) {
		boolean hasSource = false;

		Aspect aspect = this.getAspect(world, pos);

		for(BlockPos.Mutable checkPos : BlockPos.getAllInBoxMutable(pos.offset(-6, -this.maxHeight, -6), pos.offset(6, 0, 6))) {
			if(world.isBlockLoaded(checkPos)) {
				BlockState offsetState = world.getBlockState(checkPos);
				Block offsetBlock = offsetState.getBlock();

				if(offsetBlock instanceof IAspectFogBlock) {
					IAspectType aspectType = ((IAspectFogBlock) offsetBlock).getAspectFogType(world, checkPos, offsetState);

					if(aspectType != null) {
						if(aspect != null) {
							if(aspect.type == aspectType) {
								hasSource = true;
								break;
							}
						} else {
							this.setAspect(world, pos, new Aspect(aspectType, ItemAspectrusFruit.DEFAULT_AMOUNT));
							hasSource = true;
							break;
						}
					}
				}
			}
		}

		TileEntityAspectrusCrop tile = this.getTile(world, pos);
		if(tile != null) {
			tile.setHasSource(hasSource);
		}
	}
}
