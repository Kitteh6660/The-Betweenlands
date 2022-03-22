package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.property.PropertyBoolUnlisted;
import thebetweenlands.common.block.property.PropertyIntegerUnlisted;
import thebetweenlands.common.item.ItemBlockRoot;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockRoot extends BasicBlock implements ICustomItemBlock {
	public static final PropertyBoolUnlisted NO_BOTTOM = new PropertyBoolUnlisted("no_bottom");
	public static final PropertyBoolUnlisted NO_TOP = new PropertyBoolUnlisted("no_top");
	public static final PropertyIntegerUnlisted DIST_UP = new PropertyIntegerUnlisted("dist_up");
	public static final PropertyIntegerUnlisted DIST_DOWN = new PropertyIntegerUnlisted("dist_down");
	public static final PropertyIntegerUnlisted POS_X = new PropertyIntegerUnlisted("pos_x");
	public static final PropertyIntegerUnlisted POS_Y = new PropertyIntegerUnlisted("pos_x");
	public static final PropertyIntegerUnlisted POS_Z = new PropertyIntegerUnlisted("pos_z");

	public BlockRoot() {
		super(Material.WOOD);
		this.setSoundType(SoundType.WOOD);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return ItemRegistry.TANGLED_ROOT;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {POS_X, POS_Y, POS_Z, NO_BOTTOM, NO_TOP, DIST_UP, DIST_DOWN});
	}

	@Override
	public boolean isBlockNormalCube(BlockState blockState) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(BlockState blockState) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public BlockState getExtendedState(BlockState oldState, IBlockReader worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) oldState;

		final int maxLength = 32;
		int distUp = 0;
		int distDown = 0;
		boolean noTop = false;
		boolean noBottom = false;

		BlockState blockState;
		//Block block;
		for(distUp = 0; distUp < maxLength; distUp++) {
			blockState = worldIn.getBlockState(pos.offset(0, 1 + distUp, 0));
			if(blockState.getBlock() == this || blockState.getBlock() == BlockRegistry.ROOT_UNDERWATER)
				continue;
			if(blockState.getBlock() == Blocks.AIR || !blockState.isOpaqueCube())
				noTop = true;
			break;
		}
		for(distDown = 0; distDown < maxLength; distDown++)
		{
			blockState = worldIn.getBlockState(pos.offset(0, -(1 + distDown), 0));
			if(blockState.getBlock() == this || blockState.getBlock() == BlockRegistry.ROOT_UNDERWATER)
				continue;
			if(blockState.getBlock() == Blocks.AIR || !blockState.isOpaqueCube())
				noBottom = true;
			break;
		}

		return state.setValue(POS_X, pos.getX()).setValue(POS_Y, pos.getY()).setValue(POS_Z, pos.getZ()).setValue(DIST_UP, distUp).setValue(DIST_DOWN, distDown).setValue(NO_TOP, noTop).setValue(NO_BOTTOM, noBottom);
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
	public BlockItem getItemBlock() {
		return new ItemBlockRoot();
	}
}
