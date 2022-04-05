package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class BlockSwampReed extends BlockStackablePlant implements IWaterLoggable {
	
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public BlockSwampReed(Properties properties) {
		super(properties);
		this.setMaxHeight(4);
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
	}

	@Override
	protected boolean isSamePlant(BlockState blockState) {
		return super.isSamePlant(blockState) || blockState.getBlock() == BlockRegistry.SWAMP_REED_UNDERWATER.get();
	}

	@Override
	protected boolean canSustainBush(BlockState state) {
		return super.canSustainBush(state) || state.getBlock() == BlockRegistry.SWAMP_REED_UNDERWATER.get() || SurfaceType.SAND.matches(state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		BlockState soil = worldIn.getBlockState(pos.below());
		boolean canPlace = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) &&
				soil.getBlock().canSustainPlant(soil, worldIn, pos.below(), Direction.UP, this);
		if(canPlace) {
			if(this.isSamePlant(worldIn.getBlockState(pos.below())))
				return true;
			BlockPos blockpos = pos.below();
			for (Direction Direction : Direction.Plane.HORIZONTAL) {
				if (worldIn.isBlockLoaded(blockpos.offset(Direction))) {
					BlockState iblockstate = worldIn.getBlockState(blockpos.relative(Direction));
					if (iblockstate.getMaterial() == Material.WATER) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean canSurvive(World worldIn, BlockPos pos, BlockState state) {
		BlockState soil = worldIn.getBlockState(pos.below());
		if(soil.getBlock().canSustainPlant(soil, worldIn, pos.below(), net.minecraft.util.Direction.UP, this)) {
			if(this.isSamePlant(worldIn.getBlockState(pos.below())))
				return true;
			BlockPos blockpos = pos.below();
			for (Direction Direction : Direction.Plane.HORIZONTAL) {
				BlockState iblockstate = worldIn.getBlockState(blockpos.relative(Direction));
				if (iblockstate.getMaterial() == Material.WATER) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (world.random.nextInt(65) == 0) {
			BLParticles.MOSQUITO.spawn(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		}
	}
	
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
	}
	
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}
	
	@SuppressWarnings("deprecation")
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.getValue(WATERLOGGED)) {
			worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}
		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
}
