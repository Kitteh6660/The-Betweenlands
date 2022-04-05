package thebetweenlands.common.block.misc;

import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBouncyBetweenlands extends Block {
	
    private float heightFactor;

    public BlockBouncyBetweenlands(float heightFactor, Properties properties) {
    	super(properties);
        /*super(Material.CLAY);
        slipperiness = 0.7f;*/
        this.heightFactor = heightFactor;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        if (entityIn.isCrouching()) {
            super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        } else {
            entityIn.fall(fallDistance, 0.0F);
        }
    }


    @Override
    public void onLanded(World worldIn, Entity entityIn) {
        if (entityIn.isCrouching()) {
            super.onLanded(worldIn, entityIn);
        } else if (entityIn.motionY < 0.0D) {
            entityIn.motionY = -entityIn.motionY;

            if (!(entityIn instanceof LivingEntity)) {
                entityIn.motionY *= heightFactor;
            }
        }
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (Math.abs(entityIn.motionY) < 0.1D && !entityIn.isCrouching()) {
            double d0 = 0.4D + Math.abs(entityIn.motionY) * 0.2D;
            entityIn.motionX *= d0;
            entityIn.motionZ *= d0;
        }

        super.onEntityWalk(worldIn, pos, entityIn);
    }
}
