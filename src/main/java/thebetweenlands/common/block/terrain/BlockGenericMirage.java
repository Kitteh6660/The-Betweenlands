package thebetweenlands.common.block.terrain;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import thebetweenlands.common.block.BasicBlock;

import javax.annotation.Nullable;

public class BlockGenericMirage extends BasicBlock {
    public BlockGenericMirage(Material materialIn) {
        super(materialIn);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }
}
