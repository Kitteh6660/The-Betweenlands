package thebetweenlands.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.farming.BlockGenericCrop;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;

public class TileEntityDugSoil extends TileEntity {
	private int compost = 0;
	private int decay = 0;
	private int purifiedHarvests = 0;

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.readFromNBT(nbt);
		this.decay = nbt.getInt("decay");
		this.compost = nbt.getInt("compost");
		this.purifiedHarvests = nbt.getInt("purifiedHarvests");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt.putInt("compost", this.compost);
		nbt.putInt("decay", this.decay);
		nbt.putInt("purifiedHarvests", this.purifiedHarvests);
		return super.save(nbt);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("compost", this.compost);
		nbt.putInt("decay", this.decay);
		return new SUpdateTileEntityPacket(this.getPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();
		this.decay = nbt.getInt("decay");
		this.compost = nbt.getInt("compost");
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
		//Only recreate/remove if block has changed
		return oldState.getBlock() != newSate.getBlock();
	}

	public void copy(TileEntityDugSoil other) {
		this.setDecay(other.decay);
		this.setCompost(other.compost);
	}

	public void setPurifiedHarvests(int harvests) {
		BlockState blockState = this.world.getBlockState(this.pos);
		BlockGenericDugSoil soil = ((BlockGenericDugSoil)blockState.getBlock());
		if(soil.isPurified(this.world, this.pos, blockState)) {
			if(harvests < 0) {
				harvests = 0;
			}
			int maxHarvests = soil.getPurifiedHarvests(this.world, this.pos, blockState);
			this.purifiedHarvests = harvests;
			if(this.purifiedHarvests >= maxHarvests) {
				this.world.setBlockState(this.pos, soil.getUnpurifiedDugSoil(this.world, this.pos, blockState), 3);
				BlockGenericDugSoil.copy(this.world, this.pos, this);
			}
		} else {
			this.purifiedHarvests = 0;
		}
		this.setChanged();
	}

	public int getPurifiedHarvests() {
		return this.purifiedHarvests;
	}

	public void setCompost(int compost) {
		if(compost < 0) {
			compost = 0;
		}
		boolean wasComposted = this.isComposted();
		this.compost = compost;
		if(wasComposted != this.isComposted()) {
			BlockState blockState = this.world.getBlockState(this.pos);
			if(!this.isFullyDecayed()) {
				this.world.setBlockState(this.pos, blockState.setValue(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).setValue(BlockGenericDugSoil.COMPOSTED, this.isComposted()), 3);
			} else {
				this.world.setBlockState(this.pos, blockState.setValue(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).setValue(BlockGenericDugSoil.COMPOSTED, false), 3);
			}
		} else {
			BlockState state = this.world.getBlockState(this.pos);
			this.world.sendBlockUpdated(this.pos, state, state, 3);
		}
		this.setChanged();
	}

	public int getCompost() {
		return this.compost;
	}

	public boolean isComposted() {
		return this.compost > 0;
	}

	public void setDecay(int decay) {
		if(decay < 0) {
			decay = 0;
		}
		boolean wasDecayed = this.isFullyDecayed();
		this.decay = Math.min(20, decay);
		if(wasDecayed != this.isFullyDecayed()) {
			BlockState blockState = this.world.getBlockState(this.pos);
			if(this.isFullyDecayed()) {
				blockState = blockState.setValue(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).setValue(BlockGenericDugSoil.COMPOSTED, false);
			} else {
				blockState = blockState.setValue(BlockGenericDugSoil.DECAYED, false).setValue(BlockGenericDugSoil.COMPOSTED, this.isComposted());
			}
			this.world.setBlockState(this.pos, blockState, 3);

			BlockState blockUp = this.world.getBlockState(this.pos.above());
			if(blockUp.getBlock() instanceof BlockGenericCrop) {
				BlockPos pos = this.pos.above();
				for(int i = 0; i < ((BlockGenericCrop)blockUp.getBlock()).getMaxHeight(); i++) {
					BlockState cropBlockState = this.world.getBlockState(pos);
					if(cropBlockState.getBlock() instanceof BlockGenericCrop) {
						if(this.isFullyDecayed()) {
							this.world.setBlockState(pos, cropBlockState.setValue(BlockGenericCrop.DECAYED, true), 3);
						} else {
							this.world.setBlockState(pos, cropBlockState.setValue(BlockGenericCrop.DECAYED, false), 3);
						}
					} else {
						break;
					}
					pos = pos.above();
				}
			}
		} else {
			BlockState state = this.world.getBlockState(this.pos);
			this.world.sendBlockUpdated(this.pos, state, state, 3);
		}
		this.setChanged();
	}

	public int getDecay() {
		return this.decay;
	}

	public boolean isFullyDecayed() {
		return this.decay >= 20;
	}
}
