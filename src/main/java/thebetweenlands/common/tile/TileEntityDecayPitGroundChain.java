package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickableTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityDecayPitGroundChain extends TileEntity implements ITickableTileEntity {

	public int animationTicksChain = 0;
	public int animationTicksChainPrev = 0;
	public int LENGTH = 5; //hard-coded for now
	public boolean IS_MOVING = false;
	public boolean IS_SLOW = false;
	public boolean IS_RAISING = false;
	public boolean IS_BROKEN = false;
	public int breakTimer = 0;
	@Override
	public void tick() {
		animationTicksChainPrev = animationTicksChain;
		if (isMoving()) {
			if (isSlow())
				animationTicksChain++;
			else
				if(isBroken())
					animationTicksChain += 32;
				else
					animationTicksChain += 8;
		}

		if (getEntityCollidedWithChains(getHangingLengthCollision(0.625F, 5F, 0.625F)) != null)
			checkCollisions(getEntityCollidedWithChains(getHangingLengthCollision(0.625F, 5F, 0.625F)));

		if (animationTicksChainPrev >= 128) {
			animationTicksChain = animationTicksChainPrev = 0;
			if(!isBroken())
				setMoving(false);
		}

		if (!getLevel().isClientSide() && isBroken()) {
			breakTimer++;
			if (breakTimer > 32) {
				if (breakTimer % 4 == 0) {
					setLength(getLength() - 1);
					updateBlock();
				}
				if (getLength() <= 0) {
					getLevel().setBlockToAir(getBlockPos());
				}
			}
		}	
	}

	public List<Entity> getEntityCollidedWithChains(AxisAlignedBB chainBox) {
		return getLevel().<Entity>getEntitiesOfClass(Entity.class, chainBox);
    }

	private void checkCollisions(List<Entity> list) {
		for (Entity entity : list) {
			if (entity instanceof EntityArrow) { // just arrows for now
				Entity arrow = ((EntityArrow) entity);
				arrow.setPositionAndUpdate(arrow.xOld, arrow.yOld, arrow.zOld);
				arrow.motionX *= -0.10000000149011612D;
				arrow.motionY *= -0.10000000149011612D;
				arrow.motionZ *= -0.10000000149011612D;
				arrow.yRot += 180.0F;
				arrow.prevRotationYaw += 180.0F;
				getLevel().playSound((PlayerEntity) null, arrow.getX(), arrow.getY(), arrow.getZ(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5F, 3F);
				// this.ticksInAir = 0;
			}
		}
	}

	public AxisAlignedBB getHangingLengthCollision(double x, double y, double z) {
		return new AxisAlignedBB(getBlockPos().getX() + 0.5D - x * 0.5D, getBlockPos().getY(), getBlockPos().getZ() + 0.5D + - z * 0.5D, getBlockPos().getX() + 0.5D + x * 0.5D, getBlockPos().getY() + y, getBlockPos().getZ() + 0.5D + z * 0.5D);
	}

	public void setMoving(boolean moving) {
		IS_MOVING = moving;
	}

	public boolean isMoving() {
		return IS_MOVING;
	}

	public void setSlow(boolean slow) {
		IS_SLOW = slow;
	}

	public boolean isSlow() {
		return IS_SLOW;
	}

	public void setRaising(boolean raising) {
		IS_RAISING = raising;
	}

	public boolean isRaising() {
		return IS_RAISING;
	}

	public void setLength(int length) {
		LENGTH = length;
	}

	public int getLength() {
		return LENGTH;
	}

	public void setBroken(boolean broken) {
		IS_BROKEN = broken;
	}

	public boolean isBroken() {
		return IS_BROKEN;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putInt("animationTicksChain", animationTicksChain);
		nbt.putInt("animationTicksChainPrev", animationTicksChainPrev);
		nbt.putInt("length", LENGTH);
		nbt.putBoolean("raising", IS_RAISING);
		nbt.putBoolean("moving", IS_MOVING);
		nbt.putBoolean("broken", IS_BROKEN);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		animationTicksChain = nbt.getInt("animationTicksChain");
		animationTicksChainPrev = nbt.getInt("animationTicksChainPrev");
		LENGTH = nbt.getInt("length");
		IS_RAISING = nbt.getBoolean("raising");
		IS_MOVING = nbt.getBoolean("moving");
		IS_BROKEN = nbt.getBoolean("broken");
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
		return new SUpdateTileEntityPacket(getBlockPos(), 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		readFromNBT(packet.getTag());
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	public void updateBlock() {
		getLevel().sendBlockUpdated(pos, getLevel().getBlockState(pos), getLevel().getBlockState(pos), 3);
	}
}
