package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockLeavesSpiritTree extends BlockLeavesBetweenlands {
	
	public static final VoxelShape AABB = Block.box(0.3D, 0.3D, 0.3D, 0.7D, 0.7D, 0.7D);

	public BlockLeavesSpiritTree(Properties properties) {
		super(properties);
	}

	/*@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}*/

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(PERSISTENT, false);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		if (this == BlockRegistry.LEAVES_SPIRIT_TREE_MIDDLE.get() || this == BlockRegistry.LEAVES_SPIRIT_TREE_BOTTOM.get()) {
			BlockState above = world.getBlockState(pos.above());
			if(this == BlockRegistry.LEAVES_SPIRIT_TREE_MIDDLE.get()) {
				return above.getBlock() == BlockRegistry.LEAVES_SPIRIT_TREE_TOP.get() || above.getBlock() == BlockRegistry.LEAVES_SPIRIT_TREE_MIDDLE.get();
			} else {
				return above.getBlock() == BlockRegistry.LEAVES_SPIRIT_TREE_MIDDLE.get();
			}
		}
		else {
			return true;
		}
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return state.getValue(DISTANCE) == 7 && !state.getValue(PERSISTENT);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if (!state.getValue(PERSISTENT) && state.getValue(DISTANCE) == 7) {
			dropResources(state, worldIn, pos);
			worldIn.removeBlock(pos, false);
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		if(this == BlockRegistry.LEAVES_SPIRIT_TREE_TOP.get()) {
			super.updateTick(worldIn, pos, state, rand);
		}
		if (!canSurvive(state, worldIn, pos)) {
			dropResources(state, worldIn, pos);
			worldIn.removeBlock(pos, false);
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return Block.box(0.4D, 0.4D, 0.4D, 0.6D, 0.6D, 0.6D);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if(rand.nextInt(100) == 0) {
			double px = (double)pos.getX() + rand.nextDouble() * 0.5D;
			double py = (double)pos.getY() + rand.nextDouble() * 0.5D;
			double pz = (double)pos.getZ() + rand.nextDouble() * 0.5D;
			BLParticles.SPIRIT_BUTTERFLY.spawn(world, px, py, pz);
		}
	}
}