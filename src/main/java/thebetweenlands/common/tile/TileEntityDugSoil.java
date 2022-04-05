package thebetweenlands.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.common.block.farming.BlockGenericCrop;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;

public class TileEntityDugSoil extends TileEntity {

	private int compost = 0;
	private int decay = 0;
	private int purifiedHarvests = 0;
	
	public TileEntityDugSoil(TileEntityType<?> te) {
		super(te);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
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
		return new SUpdateTileEntityPacket(this.getBlockPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getTag();
		this.decay = nbt.getInt("decay");
		this.compost = nbt.getInt("compost");
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}

	/*@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
		//Only recreate/remove if block has changed
		return oldState.getBlock() != newSate.getBlock();
	}*/

	public void copy(TileEntityDugSoil other) {
		this.setDecay(other.decay);
		this.setCompost(other.compost);
	}

	public void setPurifiedHarvests(int harvests) {
		BlockState blockState = this.level.getBlockState(this.worldPosition);
		BlockGenericDugSoil soil = ((BlockGenericDugSoil)blockState.getBlock());
		if(soil.isPurified(this.level, this.worldPosition, blockState)) {
			if(harvests < 0) {
				harvests = 0;
			}
			int maxHarvests = soil.getPurifiedHarvests(this.level, this.worldPosition, blockState);
			this.purifiedHarvests = harvests;
			if(this.purifiedHarvests >= maxHarvests) {
				this.level.setBlock(this.worldPosition, soil.getUnpurifiedDugSoil(this.level, this.worldPosition, blockState), 3);
				BlockGenericDugSoil.copy(this.level, this.worldPosition, this);
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
			BlockState blockState = this.level.getBlockState(this.worldPosition);
			if(!this.isFullyDecayed()) {
				this.level.setBlock(this.worldPosition, blockState.setValue(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).setValue(BlockGenericDugSoil.COMPOSTED, this.isComposted()), 3);
			} else {
				this.level.setBlock(this.worldPosition, blockState.setValue(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).setValue(BlockGenericDugSoil.COMPOSTED, false), 3);
			}
		} else {
			BlockState state = this.level.getBlockState(this.worldPosition);
			this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
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
			BlockState blockState = this.level.getBlockState(this.worldPosition);
			if(this.isFullyDecayed()) {
				blockState = blockState.setValue(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).setValue(BlockGenericDugSoil.COMPOSTED, false);
			} else {
				blockState = blockState.setValue(BlockGenericDugSoil.DECAYED, false).setValue(BlockGenericDugSoil.COMPOSTED, this.isComposted());
			}
			this.level.setBlock(this.worldPosition, blockState, 3);

			BlockState blockUp = this.level.getBlockState(this.worldPosition.above());
			if(blockUp.getBlock() instanceof BlockGenericCrop) {
				BlockPos pos = this.worldPosition.above();
				for(int i = 0; i < ((BlockGenericCrop)blockUp.getBlock()).getMaxHeight(); i++) {
					BlockState cropBlockState = this.level.getBlockState(pos);
					if(cropBlockState.getBlock() instanceof BlockGenericCrop) {
						if(this.isFullyDecayed()) {
							this.level.setBlock(pos, cropBlockState.setValue(BlockGenericCrop.DECAYED, true), 3);
						} else {
							this.level.setBlock(pos, cropBlockState.setValue(BlockGenericCrop.DECAYED, false), 3);
						}
					} else {
						break;
					}
					pos = pos.above();
				}
			}
		} else {
			BlockState state = this.level.getBlockState(this.worldPosition);
			this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
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
