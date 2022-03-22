package thebetweenlands.common.block.structure;

import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.tile.TileEntityDecayPitHangingChain;

public class BlockDecayPitHangingChain extends HorizontalFaceBlock implements ITileEntityProvider, ICustomItemBlock {

	public BlockDecayPitHangingChain() {
		super(Material.ROCK);
		this.setBlockUnbreakable();
		setResistance(2000.0F);
		setLightLevel(0.5F);
		setSoundType(SoundType.STONE);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		// S = 0, W = 1, N = 2, E = 3
		return defaultBlockState().setValue(FACING, Direction.byIndex(meta));
	}

	@Override
	public int getMetaFromState(BlockState state) {
		// S = 0, W = 1, N = 2, E = 3
		int meta = 0;
		meta = meta | ((Direction) state.getValue(FACING)).getIndex();
		return meta;
	}

	@Override
	 public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hitResult, int meta, LivingEntity placer) {
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
	}

	@Override
	public BlockState withRotation(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate((Direction) state.getValue(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((Direction) state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityDecayPitHangingChain();
	}
	
	@Override
	public BlockItem getItemBlock() {
		return null;
	}
}
