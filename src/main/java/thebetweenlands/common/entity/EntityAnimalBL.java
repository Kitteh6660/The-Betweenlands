package thebetweenlands.common.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;

public abstract class EntityAnimalBL extends AnimalEntity implements IEntityBL 
{
	public EntityAnimalBL(EntityType<? extends AnimalEntity> entity, World worldIn) {
		super(entity, worldIn);
	}

	/*@Override
	public boolean checkSpawnRules(IWorld world, SpawnReason reason) {
		BlockState state = this.level.getBlockState((new BlockPos(this.getPosition(0F))).below());
		return state.canEntitySpawn(this) && this.getWalkTargetValue(new BlockPos(this.getX(), this.getBoundingBox().minY, this.getZ())) >= 0.0F;
	}
	
	@Override
	public float getWalkTargetValue(BlockPos pos) {
        return 0.0F;
    }*/
}
