package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.block.structure.BlockPossessedBlock;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.AnimationMathHelper;

public class TileEntityPossessedBlock extends TileEntity implements ITickableTileEntity {

	public int animationTicks, coolDown;
	public boolean active;
	AnimationMathHelper headShake = new AnimationMathHelper();
	public float moveProgress;

	public TileEntityPossessedBlock(TileEntityType<?> te) {
		super(te);
	}
	
	@Override
	public void tick() {
		if (!level.isClientSide()) {
			findEnemyToAttack();
			if (active) {
				activateBlock();
				if (animationTicks == 0)
					level.playSound(null, getBlockPos(), SoundRegistry.POSSESSED_SCREAM, SoundCategory.BLOCKS, 0.25F, 1.25F - this.level.random.nextFloat() * 0.5F);
				if (animationTicks <= 24)
					animationTicks++;
				if (animationTicks == 24) {
					setActive(false);
					coolDown = 200;
				}
			}
			if (!active) {
				if (animationTicks >= 1)
					animationTicks--;
				if(coolDown >= 0)
					coolDown--;
			}
			level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
		}
		moveProgress = 1 + headShake.swing(4, 1F, false);
		if (level.isClientSide())
			if(!active && animationTicks %8 > 0)
				addParticles();
	}

	private void addParticles() {
		BlockState state = getLevel().getBlockState(worldPosition);
		Direction facing = state.getValue(BlockPossessedBlock.FACING);
		float x = 0, z = 0;
		if(facing == Direction.WEST)
			x = -1F;
		if(facing == Direction.EAST)
			x = 1F;
		if(facing == Direction.NORTH)
			z = -1F;
		if(facing == Direction.SOUTH)
			z = 1F;

		float xx = (float) getBlockPos().getX() + 0.5F + x;
		float yy = (float) getBlockPos().getY() + 0.5F;
		float zz = (float) getBlockPos().getZ() + 0.5F + z;
		float randomOffset = level.random.nextFloat() * 0.6F - 0.3F;
		BLParticles.SMOKE.spawn(level, (double) (xx - randomOffset), (double) (yy + randomOffset), (double) (zz + randomOffset));
		BLParticles.SMOKE.spawn(level, (double) (xx + randomOffset), (double) (yy - randomOffset), (double) (zz + randomOffset));
		BLParticles.SMOKE.spawn(level, (double) (xx + randomOffset), (double) (yy + randomOffset), (double) (zz - randomOffset));
		BLParticles.SMOKE.spawn(level, (double) (xx + randomOffset), (double) (yy - randomOffset), (double) (zz + randomOffset));
	}

	public void setActive(boolean isActive) {
		active = isActive;
		level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
	}

	protected Entity findEnemyToAttack() {
		BlockState state = getLevel().getBlockState(worldPosition);
		Direction facing = state.getValue(BlockPossessedBlock.FACING);
		float x = 0, z = 0;
		if(facing == Direction.WEST)
			x = -1.25F;
		if(facing == Direction.EAST)
			x = 1.25F;
		if(facing == Direction.NORTH)
			z = -1.25F;
		if(facing == Direction.SOUTH)
			z = 1.25F;
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(worldPosition.getX() + x, worldPosition.getY(), worldPosition.getZ() + z, worldPosition.getX() + 1D + x, worldPosition.getY() + 1D, worldPosition.getZ() + 1D + z));
		for (int i = 0; i < list.size(); i++) {
				Entity entity = list.get(i);
				if (entity != null)
					if (entity instanceof PlayerEntity)
						if (!active && animationTicks == 0 && coolDown <= 0)
							setActive(true);
			}
		return null;
	}

	protected Entity activateBlock() {
		BlockState state = getLevel().getBlockState(worldPosition);
		Direction facing = state.getValue(BlockPossessedBlock.FACING);
		float x = 0, z = 0;
		if(facing == Direction.WEST)
			x = -1.25F;
		if(facing == Direction.EAST)
			x = 1.25F;
		if(facing == Direction.NORTH)
			z = -1.25F;
		if(facing == Direction.SOUTH)
			z = 1.25F;
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(worldPosition.getX() + x, worldPosition.getY(), worldPosition.getZ() + z, worldPosition.getX() + 1D + x, worldPosition.getY() + 1D, worldPosition.getZ() + 1D + z));
		if (animationTicks == 1)
			for (int i = 0; i < list.size(); i++) {
				Entity entity = list.get(i);
				if (entity != null)
					if (entity instanceof PlayerEntity) {
						int Knockback = 4;
						entity.setDeltaMovement(MathHelper.sin(entity.yRot * 3.141593F / 180.0F) * Knockback * 0.2F, 0.3D, -MathHelper.cos(entity.yRot * 3.141593F / 180.0F) * Knockback * 0.2F);
						((LivingEntity) entity).hurt(DamageSource.GENERIC, 2);
					}
			}
		return null;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putInt("animationTicks", animationTicks);
		nbt.putBoolean("active", active);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		animationTicks = nbt.getInt("animationTicks");
		active = nbt.getBoolean("active");
	}

	@Override
    public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();
        return save(nbt);
    }

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		save(nbt);
		return new SUpdateTileEntityPacket(worldPosition, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		load(level.getBlockState(packet.getPos()), packet.getTag());
	}
}