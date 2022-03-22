package thebetweenlands.common.block.structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.DirectionProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockMudTilesWater extends BasicBlock {

	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

	public BlockMudTilesWater() {
		super(Material.ROCK);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction facing = Direction.byIndex(meta); // Using this instead of 'byHorizontalIndex' because the ids don't match and previous was release
		return defaultBlockState().setValue(FACING, facing.getAxis().isHorizontal() ? facing: Direction.NORTH);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		int meta = 0;
		meta = meta | state.getValue(FACING).getIndex();
		return meta;
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
	}

	@Override
	public BlockState withRotation(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@OnlyIn(Dist.CLIENT)
	@Override //grrrr 
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
		double d0 = (double) pos.getX() + 0.375D;
		double d1 = (double) pos.getY();
		double d2 = (double) pos.getZ() + 0.375D;
		int distance = 0;
		for (distance = 1; distance < 10; distance++) {
			Material material = world.getBlockState(pos.above(distance)).getMaterial();
			if (state.getBlock() != null && material.blocksMovement() && !material.isLiquid())
				break;
		}

		if (distance > 1 && distance < 10) {
			double d3 = d0 + (double) rand.nextFloat() * 0.25F;
			double d5 = (d1 + distance) - 0.05D;
			double d7 = d2 + (double) rand.nextFloat() * 0.25F;
			BLParticles.CAVE_WATER_DRIP.spawn(world, d3, d5, d7).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
		}
	}

	@Override
	public void onEntityWalk(World world, BlockPos pos, Entity entity) {
		if (!world.isClientSide())
			if(entity instanceof PlayerEntity && !entity.isCrouching()) {
				world.playSound(null, pos, blockSoundType.getBreakSound(), SoundCategory.BLOCKS, 0.5F, 1F);
				world.playEvent(null, 2001, pos, Block.getIdFromBlock(BlockRegistry.MUD_TILES)); //this will do unless we want specific particles
				world.setBlockState(pos, BlockRegistry.STAGNANT_WATER.defaultBlockState());
			}
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
		this.onBlockHarvested(world, pos, state, player);
		return world.setBlockState(pos, BlockRegistry.STAGNANT_WATER.defaultBlockState(), world.isClientSide() ? 11 : 3);
	}

	@Override
	public boolean canPlaceTorchOnTop(BlockState state, IBlockReader world, BlockPos pos) {
		return false;
	}
}