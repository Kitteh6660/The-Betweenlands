package thebetweenlands.common.block.structure;

import java.util.Random;

import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntitySwordEnergy;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityItemCage;


public class BlockItemCage extends ContainerBlock {

	public BlockItemCage(Properties properties) {
		super(properties);
		/*super(Material.WOOD);
		setHardness(10F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setLightLevel(0.8F);
		setCreativeTab(BLCreativeTabs.BLOCKS);*/
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return new TileEntityItemCage();
	}

	@Override
	public float getBlockHardness(BlockState blockState, World world, BlockPos pos) {
		TileEntityItemCage swordStone = (TileEntityItemCage)world.getBlockEntity(pos);
        if (swordStone != null && !swordStone.canBreak)
        	return -1;
        return blockHardness;
    }

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader pevel, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.block();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return FULL_BLOCK_AABB;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, BlockState state) {
		if (!level.isClientSide()) {
			TileEntityItemCage swordStone = (TileEntityItemCage) world.getBlockEntity(pos);
			if (swordStone != null && swordStone.isSwordEnergyBelow() != null) {
				EntitySwordEnergy energyBall = (EntitySwordEnergy) swordStone.isSwordEnergyBelow();
				world.playLocalSound(null, pos, SoundRegistry.FORTRESS_PUZZLE_CAGE_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
				switch (swordStone.type) {
					case 0:
						energyBall.setSwordPart1Pos(energyBall.getSwordPart1Pos() - 0.05F);
						break;
					case 1:
						energyBall.setSwordPart2Pos(energyBall.getSwordPart2Pos() - 0.05F);
						break;
					case 2:
						energyBall.setSwordPart3Pos(energyBall.getSwordPart3Pos() - 0.05F);
						break;
					case 3:
						energyBall.setSwordPart4Pos(energyBall.getSwordPart4Pos() - 0.05F);
						break;
					}
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    	return BlockFaceShape.UNDEFINED;
    }
}
