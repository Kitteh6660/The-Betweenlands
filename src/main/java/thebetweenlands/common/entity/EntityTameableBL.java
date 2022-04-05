package thebetweenlands.common.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;

public abstract class EntityTameableBL extends TameableEntity implements IEntityBL {
	
	public EntityTameableBL(EntityType<? extends EntityTameableBL> entity, World worldIn) {
		super(entity, worldIn);
	}

	@Override
	public boolean getCanSpawnHere() {
		BlockState state = this.level.getBlockState((new BlockPos(this)).below());
		return state.canEntitySpawn(this) && this.getBlockPathWeight(new BlockPos(this.getX(), this.getBoundingBox().minY, this.getZ())) >= 0.0F;
	}
	
	@Override
	public float getBlockPathWeight(BlockPos pos) {
        return 0.0F;
    }
}
