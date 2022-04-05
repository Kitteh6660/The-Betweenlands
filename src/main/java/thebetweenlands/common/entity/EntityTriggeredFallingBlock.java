package thebetweenlands.common.entity;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityTriggeredFallingBlock extends EntityProximitySpawner {

	private static final DataParameter<Boolean> IS_WALK_WAY = EntityDataManager.defineId(EntityTriggeredFallingBlock.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_HANGING = EntityDataManager.defineId(EntityTriggeredFallingBlock.class, DataSerializers.BOOLEAN);
	public EntityTriggeredFallingBlock(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		setNoGravity(true);
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_WALK_WAY, false);
		this.entityData.define(IS_HANGING, false);
	}

	@Override
	public void tick() {
		if (!level.isClientSide() && level.getGameTime()%5 == 0) {
			if(!isHanging())
				checkArea();
			else
				checkBlockAbove();
		}
			if (level.isClientSide())
				dustParticles();
	}

	private void checkBlockAbove() {
		if (level.isEmptyBlock(getPosition().above())) {
			Entity spawn = getEntitySpawned();
			if (spawn != null) {
				performPreSpawnaction(this, spawn);
				if (!spawn.isDead) // just in case of pre-emptive removal
					level.addFreshEntity(spawn);
			}
			if (!isDead && isSingleUse())
				remove();
		}
	}

	public void dustParticles() {
		if (rand.nextInt(16) == 0) {
			BlockPos blockpos = getPosition().below();
			if (canFallThrough(level.getBlockState(blockpos))) {
				double d0 = (double) ((float) getPosition().getX() + rand.nextFloat());
				double d1 = !isWalkway() ? (double) getPosition().getY() - 0.05D : (double) getPosition().getY() + 1D;
				double d2 = (double) ((float) getPosition().getZ() + rand.nextFloat());
				if(!isWalkway())
					level.addParticle(ParticleTypes.BLOCK_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(getBlockType(level, getPosition())));
				else {
					double motionX = level.rand.nextDouble() * 0.1F - 0.05F;
					double motionY = level.rand.nextDouble() * 0.025F + 0.025F;
					double motionZ = level.rand.nextDouble() * 0.1F - 0.05F;
					level.addParticle(ParticleTypes.BLOCK_DUST, d0, d1, d2, motionX, motionY, motionZ, Block.getStateId(getBlockType(level, getPosition())));
				}
			}
		}
	}

    public static boolean canFallThrough(BlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
    }

	@Override
	protected void performPreSpawnaction(Entity targetEntity, Entity entitySpawned) {
		((EntityFallingBlock)entitySpawned).setHurtEntities(true);
		 if (!level.isClientSide()) {
			 targetEntity.level.playSound(null, targetEntity.getPosition(), SoundRegistry.ROOF_COLLAPSE, SoundCategory.BLOCKS, 0.5F, 1.0F);
		 }
	}

	@Override
	protected boolean isMovementBlocked() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
    public boolean canBeCollidedWith() {
        return false;
    }

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public void onKillCommand() {
		this.remove();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if(sourceEntity instanceof PlayerEntity && ((PlayerEntity) sourceEntity).isCreative()) {
				this.remove();
			}
		}
		return false;
	}

	@Override
	protected float getProximityHorizontal() {
		return isWalkway() ? 0.0625F : 1F;
	}

	@Override
	protected float getProximityVertical() {
		return 1F;
	}

	protected AxisAlignedBB proximityBox() {
		return new AxisAlignedBB(getPosition()).inflate(getProximityHorizontal(), getProximityVertical(), getProximityHorizontal()).offset(0D, isWalkway() ? 1D : - getProximityVertical () * 2 , 0D);
	}

	@Override
	protected boolean canSneakPast() {
		return true;
	}

	@Override
	protected boolean checkSight() {
		return false;
	}

	@Override
	protected Entity getEntitySpawned() {
		if(getBlockType(level, getPosition()).getBlock() != null) {
			EntityFallingBlock entity = new EntityFallingBlock(level, posX, posY, posZ, getBlockType(level, getPosition()));
			entity.shouldDropItem = false;
			return entity;
		}
		return null;
	}

	private BlockState getBlockType(World world, BlockPos pos) {
		return world.getBlockState(pos);
	}

	@Override
	protected int getEntitySpawnCount() {
		return 1;
	}

	@Override
	protected boolean isSingleUse() {
		return true;
	}

	@Override
	protected int maxUseCount() {
		return 0;
	}

	public void setWalkway(boolean walkway) {
		entityData.set(IS_WALK_WAY, walkway);
	}

	public boolean isWalkway() {
		return entityData.get(IS_WALK_WAY);
	}

	public boolean isHanging() {
		return entityData.get(IS_HANGING);
	}

	public void setHanging(boolean walkway) {
		entityData.set(IS_HANGING, walkway);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		setWalkway(nbt.getBoolean("walk_way"));
		setHanging(nbt.getBoolean("hanging"));
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		nbt.putBoolean("walk_way", isWalkway());
		nbt.putBoolean("hanging", isHanging());
	}
}