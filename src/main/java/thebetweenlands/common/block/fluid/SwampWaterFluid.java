package thebetweenlands.common.block.fluid;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.item.armor.MarshRunnerBootsItem;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.util.AdvancedStateMap;

public class SwampWaterFluid extends FlowingFluid implements IStateMappedBlock, ITintedBlock, ICustomItemBlock {
	
	private static final int DEEP_COLOR_R = 19;
	private static final int DEEP_COLOR_G = 24;
	private static final int DEEP_COLOR_B = 68;
	private boolean isUnderwaterBlock = false;

	public SwampWaterFluid setUnderwaterBlock(boolean underwaterBlock) {
		this.isUnderwaterBlock = underwaterBlock;
		return this;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
		if(entityIn instanceof PlayerEntity && MarshRunnerBootsItem.checkPlayerWalkOnWater((PlayerEntity) entityIn)) {
				addCollisionBoxToList(pos, entityBox, collidingBoxes, Block.box(0, 0, 0, 1, ((float)this.getQuantaValue(worldIn, pos) / (float)this.quantaPerBlock) * 0.8F + 0.3F, 1));
				return;
		}
		super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
    }
	
	@Override
    @Nonnull
    public Vector3d modifyAcceleration(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Entity entity, @Nonnull Vector3d vec) {
		if(entity instanceof PlayerEntity && MarshRunnerBootsItem.checkPlayerWalkOnWater((PlayerEntity) entity)) {
			return Vector3d.ZERO;
		}
		if (densityDir > 0) return vec;
        Vector3d vec_flow = this.getFlowVector(world, pos);
        return vec.add(
                vec_flow.x,
                vec_flow.y,
                vec_flow.z);
    }
	
	@Override
	public boolean canDisplace(IBlockReader world, BlockPos pos) {
		if (world.isEmptyBlock(pos)) return true;

		BlockState state = world.getBlockState(pos);

		if (state.getBlock() instanceof SwampWaterFluid) {
			return false;
		}

		if (displacements.containsKey(state.getBlock())) {
			return displacements.get(state.getBlock());
		}

		Material material = state.getMaterial();
		if (material.blocksMovement() || material == Material.PORTAL) {
			return false;
		}

		int density = getDensity(world, pos);
		if (density == Integer.MAX_VALUE) {
			return true;
		}

		if (this.density > density) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		if (world instanceof World && !((World) world).isBlockLoaded(pos)) return false;

		if (world.isEmptyBlock(pos)) {
			return true;
		}

		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof SwampWaterFluid) {
			return false;
		}

		if (displacements.containsKey(block)) {
			if (displacements.get(block)) {
				block.dropBlockAsItem(world, pos, state, 0);
				return true;
			}
			return false;
		}

		Material material = state.getMaterial();
		if (material.blocksMovement() || material == Material.PORTAL) {
			return false;
		}

		int density = getDensity(world, pos);
		if (density == Integer.MAX_VALUE) {
			block.dropBlockAsItem(world, pos, state, 0);
			return true;
		}

		if (this.density > density) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public float getFluidHeightForRender(IBlockReader world, BlockPos pos, @Nonnull BlockState up) {
		BlockState here = world.getBlockState(pos);
		if (here.getBlock() instanceof SwampWaterFluid) {
			if ((up.getMaterial().isLiquid() || up.getBlock() instanceof IFluidBlock) && (up.getBlock() instanceof SwampWaterFluid == false || !((SwampWaterFluid)up.getBlock()).isUnderwaterBlock)) {
				return 1;
			}

			if (here.getValue(LEVEL) == getMaxRenderHeightMeta()) {
				return 0.875F;
			}
		}
		return !here.getMaterial().isSolid() && up.getBlock() instanceof SwampWaterFluid ? 1 : this.getQuantaPercentage(world, pos) * 0.875F;
	}

	@Override
	public Vector3d getFlowVector(IBlockReader world, BlockPos pos) {
		Vector3d vec = new Vector3d(0.0D, 0.0D, 0.0D);
		int decay = quantaPerBlock - getQuantaValue(world, pos);

		for (int side = 0; side < 4; ++side)
		{
			int x2 = pos.getX();
			int z2 = pos.getZ();

			switch (side)
			{
			case 0: --x2; break;
			case 1: --z2; break;
			case 2: ++x2; break;
			case 3: ++z2; break;
			}

			BlockPos pos2 = new BlockPos(x2, pos.getY(), z2);
			int otherDecay = quantaPerBlock - getQuantaValue(world, pos2);
			if (otherDecay >= quantaPerBlock)
			{
				if (!world.getBlockState(pos2).getMaterial().blocksMovement())
				{
					otherDecay = quantaPerBlock - getQuantaValue(world, pos2.below());
					if (otherDecay >= 0)
					{
						int power = otherDecay - (decay - quantaPerBlock);
						vec = vec.add((pos2.getX() - pos.getX()) * power, 0, (pos2.getZ() - pos.getZ()) * power);
					}
				}
			}
			else if (otherDecay >= 0)
			{
				int power = otherDecay - decay;
				vec = vec.add((pos2.getX() - pos.getX()) * power, 0, (pos2.getZ() - pos.getZ()) * power);
			}
		}
		
		if (!this.isSourceBlock(world, pos) && world.getBlockState(pos.above()).getBlock() instanceof SwampWaterFluid) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                if (this.causesDownwardCurrent(world, pos.offset(dir), dir) || this.causesDownwardCurrent(world, pos.offset(dir).above(), dir)) {
                	vec = vec.normalize().add(0.0D, -6.0D, 0.0D);
                    break;
                }
            }
        }
		
		vec = vec.normalize();
		return vec;
	}

	@Override
	protected boolean causesDownwardCurrent(IBlockReader world, BlockPos pos, Direction side) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Material material = state.getMaterial();

        if (material == this.material) {
            return false;
        } else if (side == Direction.UP) {
            return true;
        } else if (material == Material.ICE) {
            return false;
        } else {
            boolean flag = isExceptBlockForAttachWithPiston(block) || block instanceof StairsBlock;
            return !flag && isBlockSolid(world, pos, side);
        }
    }
	
	@Override
	public int getQuantaValue(IBlockReader world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if (state.getBlock() instanceof SwampWaterFluid && ((SwampWaterFluid) state.getBlock()).isUnderwaterBlock) {
			return this.quantaPerBlock;
		}

		if (state.getBlock() == Blocks.AIR) {
			return 0;
		}

		if (state.getBlock() instanceof SwampWaterFluid == false) {
			return -1;
		}

		int quantaRemaining = this.quantaPerBlock - state.getValue(LEVEL);
		return quantaRemaining;
	}

	@Override
	public boolean isSourceBlock(IBlockReader world, BlockPos pos) {
		return super.isSourceBlock(world, pos);
	}

	@Override
	protected boolean canFlowInto(IBlockReader world, BlockPos pos) {
		if (world instanceof World && !((World) world).isBlockLoaded(pos)) return false;

		if (world.isEmptyBlock(pos)) return true;

		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof SwampWaterFluid) {
			return true;
		}

		if (displacements.containsKey(state.getBlock())) {
			return displacements.get(state.getBlock());
		}

		Material material = state.getMaterial();
		if (material.blocksMovement() ||
				material == Material.WATER ||
				material == Material.LAVA ||
				material == Material.PORTAL) {
			return false;
		}

		int density = getDensity(world, pos);
		if (density == Integer.MAX_VALUE) {
			return true;
		}

		if (this.density > density) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void flowIntoBlock(World world, BlockPos pos, int meta) {
		if (meta < 0) return;
		if (displaceIfPossible(world, pos)) {
			world.setBlockState(pos, BlockRegistry.SWAMP_WATER.getBlockState().getBaseState().setValue(LEVEL, meta), 3);
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, BlockState state, Random rand) {
		int quantaRemaining = quantaPerBlock - state.getValue(LEVEL);

		//Replenishing source
		if (quantaRemaining < quantaPerBlock && !world.isEmptyBlock(pos.below())) {
			int adjacentSources = 0;
			if (this.isSourceBlock(world, pos.east())) adjacentSources++;
			if (this.isSourceBlock(world, pos.north())) adjacentSources++;
			if (this.isSourceBlock(world, pos.south())) adjacentSources++;
			if (this.isSourceBlock(world, pos.west())) adjacentSources++;
			if (adjacentSources >= 2) {
				world.setBlockState(pos, state.setValue(LEVEL, 0), 2);
				quantaRemaining = quantaPerBlock;
			}
		}

		int expQuanta = -101;

		if (!(state.getBlock() instanceof SwampWaterFluid && ((SwampWaterFluid) state.getBlock()).isUnderwaterBlock)) {
			// check adjacent block levels if non-source
			if (quantaRemaining < quantaPerBlock) {
				if (world.getBlockState(pos.offset(0, -densityDir, 0)).getBlock() == this ||
						world.getBlockState(pos.offset(-1, -densityDir, 0)).getBlock() == this ||
						world.getBlockState(pos.offset(1, -densityDir, 0)).getBlock() == this ||
						world.getBlockState(pos.offset(0, -densityDir, -1)).getBlock() == this ||
						world.getBlockState(pos.offset(0, -densityDir, 1)).getBlock() == this) {
					expQuanta = quantaPerBlock - 1;
				} else {
					int maxQuanta = -100;
					maxQuanta = getLargerQuanta(world, pos.offset(-1, 0, 0), maxQuanta);
					maxQuanta = getLargerQuanta(world, pos.offset(1, 0, 0), maxQuanta);
					maxQuanta = getLargerQuanta(world, pos.offset(0, 0, -1), maxQuanta);
					maxQuanta = getLargerQuanta(world, pos.offset(0, 0, 1), maxQuanta);

					expQuanta = maxQuanta - 1;
				}

				// decay calculation
				if (expQuanta != quantaRemaining) {
					quantaRemaining = expQuanta;

					if (expQuanta <= 0) {
						world.setBlockToAir(pos);
					} else {
						world.setBlockState(pos, state.setValue(LEVEL, quantaPerBlock - expQuanta), 2);
						world.scheduleUpdate(pos, this, tickRate);
						world.notifyNeighborsOfStateChange(pos, this, true);
					}
				}
			}
			// This is a "source" block, set meta to zero, and send a server only update
			else if (quantaRemaining >= quantaPerBlock) {
				world.setBlockState(pos, this.defaultBlockState(), 2);
			}
		}

		// Flow vertically if possible
		if (canDisplace(world, pos.above(densityDir))) {
			flowIntoBlock(world, pos.above(densityDir), 1);
			return;
		}

		// Flow outward if possible
		int flowMeta = quantaPerBlock - quantaRemaining + 1;
		if (flowMeta >= quantaPerBlock) {
			return;
		}

		if (isSourceBlock(world, pos) || !isFlowingVertically(world, pos)) {
			if (world.getBlockState(pos.below(densityDir)).getBlock() instanceof SwampWaterFluid) {
				flowMeta = 1;
			}
			boolean flowTo[] = getOptimalFlowDirections(world, pos);

			if (flowTo[0]) flowIntoBlock(world, pos.offset(-1, 0, 0), flowMeta);
			if (flowTo[1]) flowIntoBlock(world, pos.offset(1, 0, 0), flowMeta);
			if (flowTo[2]) flowIntoBlock(world, pos.offset(0, 0, -1), flowMeta);
			if (flowTo[3]) flowIntoBlock(world, pos.offset(0, 0, 1), flowMeta);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(BlockState state, World worldIn, BlockPos pos) {
		if (state.getBlock() instanceof SwampWaterFluid && ((SwampWaterFluid) state.getBlock()).isUnderwaterBlock)
			return state.getBoundingBox(worldIn, pos).offset(pos);
		return null;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(@Nonnull BlockState blockState, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
		if (blockState.getBlock() instanceof SwampWaterFluid && ((SwampWaterFluid) blockState.getBlock()).isUnderwaterBlock)
			return blockState.getBoundingBox(worldIn, pos);
		return null;
	}

	@Override
	public boolean canCollideCheck(BlockState state, boolean fullHit) {
		return state.getBlock() instanceof SwampWaterFluid && ((SwampWaterFluid) state.getBlock()).isUnderwaterBlock || super.canCollideCheck(state, fullHit);
	}

	@Override
	public boolean isReplaceable(IBlockReader worldIn, BlockPos pos) {
		BlockState state = worldIn.getBlockState(pos);
		return !(state.getBlock() instanceof SwampWaterFluid) || !((SwampWaterFluid) state.getBlock()).isUnderwaterBlock && super.isReplaceable(worldIn, pos);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(SwampWaterFluid.LEVEL);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && (!this.isUnderwaterBlock || worldIn.getBlockState(pos).getMaterial() == Material.WATER);
	}

	@Override
	public int getColorMultiplier(BlockState state, IBlockReader worldIn, BlockPos pos, int tintIndex) {
		if (worldIn == null || pos == null || tintIndex != 0) {
			return -1;
		}

		int r = 0;
		int g = 0;
		int b = 0;
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				int colorMultiplier = worldIn.getBiome(pos.offset(dx, 0, dz)).getWaterColorMultiplier();
				r += (colorMultiplier & 0xFF0000) >> 16;
			g += (colorMultiplier & 0x00FF00) >> 8;
		b += colorMultiplier & 0x0000FF;
			}
		}
		r /= 9;
		g /= 9;
		b /= 9;
		float depth = 0;
		if (pos.getY() > WorldProviderBetweenlands.CAVE_START) {
			depth = 1;
		} else {
			if (pos.getY() < WorldProviderBetweenlands.CAVE_WATER_HEIGHT) {
				depth = 0;
			} else {
				depth = (pos.getY() - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) / (float) (WorldProviderBetweenlands.CAVE_START - WorldProviderBetweenlands.CAVE_WATER_HEIGHT);
			}
		}
		r = (int) (r * depth + DEEP_COLOR_R * (1 - depth) + 0.5F);
		g = (int) (g * depth + DEEP_COLOR_G * (1 - depth) + 0.5F);
		b = (int) (b * depth + DEEP_COLOR_B * (1 - depth) + 0.5F);
		return r << 16 | g << 8 | b;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (rand.nextInt(1500) == 0) {
			if (world.getBlockState(pos.above(2)).getMaterial().isLiquid()) {
				BLParticles.FISH.spawn(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			} else if (world.getBlockState(pos.below()).getBlock() == BlockRegistry.MUD) {
				if (rand.nextInt(2) == 0) {
					BLParticles.MOSQUITO.spawn(world, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D);
				} else {
					BLParticles.FLY.spawn(world, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D);
				}
			}
		}
	}

	@Override
	public boolean shouldSideBeRendered(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull Direction side) {
		if(this.isUnderwaterBlock) {
			BlockState neighbor = world.getBlockState(pos.offset(side));
			if (neighbor.getMaterial() == state.getMaterial()) {
				return false;
			}
			if(this.densityDir == -1 && side == Direction.UP) {
				return true;
			}
			if(this.densityDir == 1 && side == Direction.DOWN) {
				return true;
			}

			//Ignore AABB check, only render sides if there's no block that actually blocks that side visually!
			return !world.getBlockState(pos.offset(side)).doesSideBlockRendering(world, pos.offset(side), side.getOpposite());
		}

		return super.shouldSideBeRendered(state, world, pos, side);
	}

	private boolean isBlockSolid(IBlockReader world, BlockPos pos, Direction face) {
		return world.getBlockState(pos).getBlockFaceShape(world, pos, face) == BlockFaceShape.SOLID;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
        return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return 100;
	}
	
	@Override
	public BlockItem getItemBlock() {
		return null;
	}

	@Override
	public int getColorMultiplier(BlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Fluid getFlowing() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Fluid getSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean canConvertToSource() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(IWorld p_205580_1_, BlockPos p_205580_2_, BlockState p_205580_3_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getSlopeFindDistance(IWorldReader p_185698_1_) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getDropOff(IWorldReader p_204528_1_) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Item getBucket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getTickDelay(IWorldReader p_205569_1_) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected float getExplosionResistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState p_204527_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSource(FluidState p_207193_1_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getAmount(FluidState p_207192_1_) {
		// TODO Auto-generated method stub
		return 0;
	}
}