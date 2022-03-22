package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import thebetweenlands.common.entity.mobs.EntitySporeJet;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class TileEntityPuffshroom extends TileEntity implements ITickableTileEntity
{
	public TileEntityPuffshroom(TileEntityType<?> typeIn) {
		super(typeIn);
	}

	public int animation_1 = 0, prev_animation_1 = 0, cooldown = 0;
	public int animation_2 = 0, prev_animation_2 = 0;
	public int animation_3 = 0, prev_animation_3 = 0;
	public int animation_4 = 0, prev_animation_4 = 0;
	public boolean active_1 = false, active_2 = false, active_3 = false, active_4 = false, active_5 = false, pause = true;
	public int renderTicks = 0, prev_renderTicks = 0, pause_count = 30;

	@Override
	public void tick() {
		prev_animation_1 = animation_1;
		prev_animation_2 = animation_2;
		prev_animation_3 = animation_3;
		prev_animation_4 = animation_4;
		prev_renderTicks = renderTicks;

		if (!getLevel().isClientSide() && cooldown <= 0 && getLevel().getGameTime() % 5 == 0)
			findEnemyToAttack();

		if (active_1 || active_5) {
			if (getLevel().isClientSide()) {
				if (animation_1 < 3) {
					double px = getBlockPos().getX() + 0.5D;
					double py = getBlockPos().getY() + 1.0625D;
					double pz = getBlockPos().getZ() + 0.5D;
					for (int i = 0, amount = 5 + getLevel().random.nextInt(2); i < amount; i++) {
						double ox = getLevel().random.nextDouble() * 0.1F - 0.05F;
						double oz = getLevel().random.nextDouble() * 0.1F - 0.05F;
						double motionX = getLevel().random.nextDouble() * 0.2F - 0.1F;
						double motionY = getLevel().random.nextDouble() * 0.1F + 0.075F;
						double motionZ = getLevel().random.nextDouble() * 0.2F - 0.1F;
						level.addParticle(ParticleTypes.BLOCK, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getId(BlockRegistry.MUD_TILES.defaultBlockState()));
					}
				}
			}
		}

		if (!getLevel().isClientSide()) {
			if (active_4) {
				if (animation_4 <= 1)
					getLevel().playSound((PlayerEntity) null, getBlockPos().getX() + 0.5D, getBlockPos().getY() + 1D, getBlockPos().getZ() + 0.5D, SoundRegistry.PUFF_SHROOM, SoundCategory.BLOCKS, 0.5F, 0.95F + getLevel().random.nextFloat() * 0.2F);
				if (animation_4 == 10) {
					EntitySporeJet jet = new EntitySporeJet(getLevel());
					jet.setPos(getBlockPos().getX() + 0.5D, getBlockPos().getY() + 1D, getBlockPos().getZ() + 0.5D);
					getLevel().addFreshEntity(jet);
				}
			}
		}

		if (active_1) {
			if (animation_1 <= 8)
				animation_1++;
			if (animation_1 > 8) {
				prev_animation_1 = animation_1 = 8;
				active_2 = true;
				active_1 = false;
			}
		}

		if (active_2) {
			if (animation_2 <= 8)
				animation_2++;
			if (animation_2 == 8)
				active_3 = true;
			if (animation_2 > 8) {
				prev_animation_2 = animation_2 = 8;
				active_2 = false;
			}
		}

		if (active_3) {
			if (animation_3 <= 8)
				animation_3++;
			if (animation_3 > 8) {
				prev_animation_3 = animation_3 = 8;
				active_3 = false;
				active_4 = true;
			}
		}

		if (active_4) {
			if (animation_4 <= 12)
				animation_4++;
			if (animation_4 > 12) {
				prev_animation_4 = animation_4 = 12;
				active_4 = false;
			}
		}

		if (pause) {
			if (animation_4 >= 12) {
				if (pause_count > 0)
					pause_count--;
				if (pause_count <= 0) {
					pause = false;
					pause_count = 30;
					active_5 = true;
				}
			}
		}

		if (active_5) {
			prev_animation_4 = animation_4 = 0;
			if (animation_1 >= 0)
				animation_3--;
			if (animation_3 <= 0)
				animation_2--;
			if (animation_2 <= 0)
				animation_1--;
			if (animation_3 <= 0)
				prev_animation_3 = animation_3 = 0;
			if (animation_2 <= 0)
				prev_animation_2 = animation_2 = 0;
			if (animation_1 <= 0) {
				prev_animation_1 = animation_1 = 0;
				active_5 = false;
			}
		}

		if (cooldown >= 0)
			cooldown--;
		if (cooldown < 0)
			cooldown = 0;

		renderTicks++;
	}

	public void markForUpdate() {
        BlockState state = this.getLevel().getBlockState(this.getBlockPos());
        this.getLevel().sendBlockUpdated(this.getBlockPos(), state, state, 3);
    }

	protected Entity findEnemyToAttack() {
		if(!active_1 && animation_1 == 0) {
			List<PlayerEntity> list = getLevel().getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(getBlockPos()).inflate(2D, 2D, 2D));
			for(PlayerEntity player : list) {
				if (!player.isCreative() && !player.isSpectator()) {
					active_1 = true;
					cooldown = 120;
					pause = true;
					markForUpdate();
					return player;
				}
			}
		}
		return null;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putBoolean("active_1", active_1);
		nbt.putBoolean("active_2", active_2);
		nbt.putBoolean("active_3", active_3);
		nbt.putBoolean("active_4", active_4);
		nbt.putBoolean("active_5", active_5);
		nbt.putBoolean("pause", pause);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		active_1 = nbt.getBoolean("active_1");
		active_2 = nbt.getBoolean("active_2");
		active_3 = nbt.getBoolean("active_3");
		active_4 = nbt.getBoolean("active_4");
		active_5 = nbt.getBoolean("active_5");
		pause = nbt.getBoolean("pause");
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
		load(getBlockState(), packet.getTag());
	}
}