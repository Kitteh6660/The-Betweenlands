package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.registries.AspectRegistry;

public class TileEntityGeckoCage extends TileEntity implements ITickable {
	private int ticks = 0;
	private int prevTicks = 0;
	private int recoverTicks = 0;
	private IAspectType aspectType = null;
	private int geckoUsages = 0;
	private String geckoName;

	@Override
	public void update() {
		this.prevTicks = this.ticks;
		++this.ticks;
		if(!this.level.isClientSide()) {
			if(this.recoverTicks > 0) {
				--this.recoverTicks;
				if(this.recoverTicks == 0) {
					BlockState state = this.world.getBlockState(this.pos);
					this.world.sendBlockUpdated(this.pos, state, state, 3);
				}
			} else {
				if(this.aspectType != null && this.geckoUsages == 0) {
					this.geckoName = "";
					BlockState state = this.world.getBlockState(this.pos);
					this.world.sendBlockUpdated(this.pos, state, state, 3);
				}
				if(this.aspectType != null) {
					this.aspectType = null;
					BlockState state = this.world.getBlockState(this.pos);
					this.world.sendBlockUpdated(this.pos, state, state, 3);
				}
			}
		}
	}

	public int getTicks() {
		return this.ticks;
	}

	public float getInterpolatedTicks(float delta) {
		return this.prevTicks + (this.ticks - this.prevTicks) * delta;
	}

	public IAspectType getAspectType() {
		return this.aspectType;
	}

	public void setAspectType(IAspectType type, int recoverTime) {
		--this.geckoUsages;
		this.aspectType = type;
		this.recoverTicks = recoverTime;
		BlockState state = this.world.getBlockState(this.pos);
		this.world.sendBlockUpdated(this.pos, state, state, 3);
		if (!hasGecko())
			this.recoverTicks = 0;
	}

	public boolean hasGecko() {
		return this.geckoUsages > 0;
	}

	public void setGeckoUsages(int usages) {
		this.geckoUsages = usages;
		this.setChanged();
		BlockState state = this.world.getBlockState(this.pos);
		this.world.sendBlockUpdated(this.pos, state, state, 3);
	}
	
	public int getGeckoUsages() {
		return this.geckoUsages;
	}

	@Nullable
	public String getGeckoName() {
		return this.geckoName;
	}

	public void addGecko(int usages, @Nullable String name) {
		this.geckoUsages = usages;
		this.geckoName = name;
		this.ticks = 0;
		this.setChanged();
		BlockState state = this.world.getBlockState(this.pos);
		this.world.sendBlockUpdated(this.pos, state, state, 3);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putInt("RecoverTicks", this.recoverTicks);
		nbt.putInt("GeckoUsages", this.geckoUsages);
		if(this.geckoName != null) {
			nbt.putString("GeckoName", this.geckoName);
		}
		nbt.putString("AspectType", this.aspectType == null ? "" : this.aspectType.getName());
		nbt.putInt("Ticks", this.ticks);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.readFromNBT(nbt);
		this.recoverTicks = nbt.getInt("RecoverTicks");
		this.geckoUsages = nbt.getInt("GeckoUsages");
		if(nbt.contains("GeckoName", Constants.NBT.TAG_STRING)) {
			this.geckoName = nbt.getString("GeckoName");
		} else {
			this.geckoName = null;
		}
		this.aspectType = AspectRegistry.getAspectTypeFromName(nbt.getString("AspectType"));
		this.ticks = nbt.getInt("Ticks");
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("GeckoUsages", this.geckoUsages);
		nbt.putString("AspectType", this.aspectType == null ? "" : this.aspectType.getName());
		return new SUpdateTileEntityPacket(this.getPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();
		this.geckoUsages = nbt.getInt("GeckoUsages");
		this.aspectType = AspectRegistry.getAspectTypeFromName(nbt.getString("AspectType"));
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putInt("GeckoUsages", this.geckoUsages);
		nbt.putString("AspectType", this.aspectType == null ? "" : this.aspectType.getName());
		return nbt;
	}
}
