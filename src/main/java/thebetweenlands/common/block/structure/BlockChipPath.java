package thebetweenlands.common.block.structure;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockChipPath extends Block {
	
	protected static final VoxelShape AABB = Block.box(0, 0, 0, 1, 0.1F, 1);

	public BlockChipPath(Properties properties) {
		super(properties);
		/*super(Material.WOOD);
		this.setSoundType(SoundType.WOOD);
		this.setHardness(0.5F);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return AABB;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.below()).isSideSolid(worldIn, pos.below(), Direction.UP);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos posFrom) {
		if(!this.canPlaceBlockAt(worldIn, pos)) {
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		if(entityIn instanceof LivingEntity) {
			((LivingEntity)entityIn).addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 1, 1, false, false));
		}
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
}
