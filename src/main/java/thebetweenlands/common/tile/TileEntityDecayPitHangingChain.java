package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.audio.DecayPitChainSound;
import thebetweenlands.common.registries.BlockRegistry;

public class TileEntityDecayPitHangingChain extends TileEntity implements ITickableTileEntity {

	public int animationTicksChain = 0;
	public int animationTicksChainPrev = 0;
	public int PROGRESS = 0;
	public final float MOVE_UNIT = 0.0078125F; // unit of movement 
	public boolean IS_MOVING = false;
	public boolean IS_SLOW = false;
	public boolean IS_BROKEN = false;
	public boolean playChainSound = true;
	
	public TileEntityDecayPitHangingChain(TileEntityType<?> te) {
		super(te);
	}
	
	@Override
	public void tick() {
		animationTicksChainPrev = animationTicksChain;

		if (isMoving()) {
			if (isSlow())
				animationTicksChain++;
			else if (isBroken())
				animationTicksChain += 32;
			else 
				animationTicksChain += 8;
		}

		if (isBroken() && getProgress() > -512)
			setProgress(getProgress() - 32);
		
		if (isBroken() && getProgress() <= -512)
			if(!getLevel().isClientSide())
				getLevel().setBlock(getBlockPos(), BlockRegistry.COMPACTED_MUD.get().defaultBlockState(), 3);

		if (getEntityCollidedWithChains(getHangingLengthCollision(1, 0, 2F + getProgress() * MOVE_UNIT)) != null)
			checkCollisions(getEntityCollidedWithChains(getHangingLengthCollision(1, 0, 2F + getProgress() * MOVE_UNIT)));

		if (getEntityCollidedWithChains(getHangingLengthCollision(-1, 0, 2F + getProgress() * MOVE_UNIT)) != null)
			checkCollisions(getEntityCollidedWithChains(getHangingLengthCollision(-1, 0, 2F + getProgress() * MOVE_UNIT)));

		if (getEntityCollidedWithChains(getHangingLengthCollision(0, 1, 2F + getProgress() * MOVE_UNIT)) != null)
			checkCollisions(getEntityCollidedWithChains(getHangingLengthCollision(0, 1, 2F + getProgress() * MOVE_UNIT)));

		if (getEntityCollidedWithChains(getHangingLengthCollision(0, -1, 2F + getProgress() * MOVE_UNIT)) != null)
			checkCollisions(getEntityCollidedWithChains(getHangingLengthCollision(0, -1, 2F + getProgress() * MOVE_UNIT)));

		if (animationTicksChainPrev >= 128) {
			animationTicksChain = animationTicksChainPrev = 0;
			if(!isBroken())
				setMoving(false);
		}

		if (animationTicksChainPrev == 0 && isMoving() && isSlow())
			if (!playChainSound)
				playChainSound = true;

		if (isBroken() && getProgress() >= 640)
			if (!playChainSound)
				playChainSound = true;

		if(getLevel().isClientSide() && playChainSound) {
			if(!isBroken())
				playChainSound(getLevel(), getBlockPos());
			else
				playChainSoundFinal(getLevel(), getBlockPos());
			playChainSound = false;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void playChainSound(World world, BlockPos pos) {
		ISound chain_sound = new DecayPitChainSound(this);
		Minecraft.getInstance().getSoundManager().play(chain_sound);
	}

	@OnlyIn(Dist.CLIENT)
	public void playChainSoundFinal(World world, BlockPos pos) {
		//TODO Add final chain sound/other thing
	}
	
	public List<Entity> getEntityCollidedWithChains(AxisAlignedBB chainBox) {
		return getLevel().<Entity>getEntitiesOfClass(Entity.class, chainBox);
    }

	private void checkCollisions(List<Entity> list) {
		for (Entity entity : list) {
			if (entity instanceof AbstractArrowEntity) { // just arrows for now
				Entity arrow = ((AbstractArrowEntity) entity);
				arrow.setPositionAndUpdate(arrow.xOld, arrow.yOld, arrow.zOld);
				arrow.xo *= -0.10000000149011612D;
				arrow.yo *= -0.10000000149011612D;
				arrow.zo *= -0.10000000149011612D;
				arrow.yRot += 180.0F;
				arrow.yRotO += 180.0F;
				getLevel().playSound((PlayerEntity) null, arrow.getX(), arrow.getY(), arrow.getZ(), SoundEvents.ANVIL_LAND, SoundCategory.BLOCKS, 0.5F, 3F);
				// this.ticksInAir = 0;
			}
		}
	}

	public AxisAlignedBB getHangingLengthCollision(double offX, double offZ, float extended) {
		return new AxisAlignedBB( getBlockPos().getX() + offX + 0.1875D, getBlockPos().getY() - extended,  getBlockPos().getZ() + offZ +0.1875D, getBlockPos().getX() + offX + 0.8125D, getBlockPos().getY(), getBlockPos().getZ() + offZ +0.8125D);
	}

	public void setProgress(int progress) {
		PROGRESS = progress;
	}

	public int getProgress() {
		return PROGRESS;
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
		nbt.putInt("progress", PROGRESS);
		nbt.putBoolean("broken", IS_BROKEN);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		animationTicksChain = nbt.getInt("animationTicksChain");
		animationTicksChainPrev = nbt.getInt("animationTicksChainPrev");
		PROGRESS = nbt.getInt("progress");
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
		this.load(this.getBlockState(), packet.getTag());
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	public void updateBlock() {
		getLevel().sendBlockUpdated(worldPosition, getLevel().getBlockState(worldPosition), getLevel().getBlockState(worldPosition), 3);
	}
}
