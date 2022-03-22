package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.BooleanProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockLantern extends Block {
	private static final AxisAlignedBB AABB_LARGE = Block.box(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
	private static final AxisAlignedBB AABB_SMALL = Block.box(0.25D, 0.0D, 0.25D, 0.75D, 0.6D, 0.75D);
	
	public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 7);
	public static final BooleanProperty HANGING = BooleanProperty.create("hanging");

	public BlockLantern() {
		this(Material.CLOTH, SoundType.CLOTH);
	}

	protected BlockLantern(Material material, SoundType soundType) {
		super(material);
		this.setHardness(0.1F);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(soundType);
		this.setLightLevel(1.0f);
		this.setDefaultState(this.blockState.getBaseState().setValue(ROTATION, 0).setValue(HANGING, false));
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(ROTATION, meta);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(ROTATION);
	}

	@Override
	public BlockState withRotation(BlockState state, Rotation rot) {
		return state.setValue(ROTATION, rot.rotate(state.getValue(ROTATION), 8));
	}

	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(ROTATION, mirrorIn.mirrorRotation(state.getValue(ROTATION), 8));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { ROTATION, HANGING });
	}

	@Override
	public BlockState getActualState(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return state.setValue(HANGING, worldIn.isSideSolid(pos.above(), Direction.DOWN, false) || worldIn.getBlockState(pos.above()).getBlockFaceShape(worldIn, pos.above(), Direction.DOWN) != BlockFaceShape.UNDEFINED);
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
		return this.getActualState(state, source, pos).getValue(HANGING) ? AABB_LARGE : AABB_SMALL;
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		int rotation = MathHelper.floor(((placer.yRot + 180.0F) * 8.0F / 360.0F) + 0.5D) & 7;
		worldIn.setBlockState(pos, state.setValue(ROTATION, rotation), 11);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) &&
				(worldIn.isSideSolid(pos.above(), Direction.DOWN) || worldIn.getBlockState(pos.above()).getBlockFaceShape(worldIn, pos.above(), Direction.DOWN) != BlockFaceShape.UNDEFINED ||
				worldIn.isSideSolid(pos.below(), Direction.UP) || worldIn.getBlockState(pos.below()).getBlockFaceShape(worldIn, pos.below(), Direction.UP) != BlockFaceShape.UNDEFINED);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, BlockState state) {
		if(!worldIn.isSideSolid(pos.above(), Direction.DOWN) && worldIn.getBlockState(pos.above()).getBlockFaceShape(worldIn, pos.above(), Direction.DOWN) == BlockFaceShape.UNDEFINED &&
				!worldIn.isSideSolid(pos.below(), Direction.UP) && worldIn.getBlockState(pos.below()).getBlockFaceShape(worldIn, pos.below(), Direction.UP) == BlockFaceShape.UNDEFINED) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.defaultBlockState(), 3);
		}
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		double px = (double)pos.getX() + 0.5D;
		double py = (double)pos.getY() + 0.3D;
		double pz = (double)pos.getZ() + 0.5D;

		if(worldIn.rand.nextInt(20) == 0 && worldIn.canBlockSeeSky(pos)) {
			int particle = rand.nextInt(2);
			switch(particle) {
			default:
			case 0:
				BLParticles.FLY.spawn(worldIn, px, py, pz);
				break;
			case 1:
				BLParticles.MOTH.spawn(worldIn, px, py, pz);
				break;
			}
		}
	}
}
