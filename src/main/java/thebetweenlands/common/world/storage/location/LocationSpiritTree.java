package thebetweenlands.common.world.storage.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.registries.BlockRegistry;

public class LocationSpiritTree extends LocationGuarded {
	private List<BlockPos> notGeneratedWispPositions = new ArrayList<>();
	private List<BlockPos> generatedWispPositions = new ArrayList<>();

	private List<BlockPos> largeFacePositions = new ArrayList<>();
	private List<BlockPos> smallFacePositions = new ArrayList<>();

	public LocationSpiritTree(IWorldStorage worldStorage, StorageID id, LocalRegion region) {
		super(worldStorage, id, region, "spirit_tree", EnumLocationType.SPIRIT_TREE);
	}

	public void addLargeFacePosition(BlockPos pos) {
		this.largeFacePositions.add(pos);
		this.setDirty(true);
	}

	public void addSmallFacePosition(BlockPos pos) {
		this.smallFacePositions.add(pos);
		this.setDirty(true);
	}

	public List<BlockPos> getLargeFacePositions() {
		return Collections.unmodifiableList(this.largeFacePositions);
	}

	public List<BlockPos> getSmallFacePositions() {
		return Collections.unmodifiableList(this.smallFacePositions);
	}

	public void addGeneratedWispPosition(BlockPos pos) {
		this.generatedWispPositions.add(pos);
		this.setDirty(true);
	}

	public List<BlockPos> getGeneratedWispPositions() {
		return Collections.unmodifiableList(this.generatedWispPositions);
	}

	public void addNotGeneratedWispPosition(BlockPos pos) {
		this.notGeneratedWispPositions.add(pos);
		this.setDirty(true);
	}

	public List<BlockPos> getNotGeneratedWispPositions() {
		return Collections.unmodifiableList(this.notGeneratedWispPositions);
	}

	public int getActiveWisps() {
		int i = 0;
		for(BlockPos pos : this.notGeneratedWispPositions) {
			if(this.getWorldStorage().getWorld().getBlockState(pos).getBlock() == BlockRegistry.WISP) {
				i++;
			}
		}
		for(BlockPos pos : this.generatedWispPositions) {
			if(this.getWorldStorage().getWorld().getBlockState(pos).getBlock() == BlockRegistry.WISP) {
				i++;
			}
		}
		return i;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt = super.save(nbt);
		this.saveBlockList(nbt, "generatedWispPositions", this.generatedWispPositions);
		this.saveBlockList(nbt, "notGeneratedWispPositions", this.notGeneratedWispPositions);
		this.saveBlockList(nbt, "largeFacePositions", this.largeFacePositions);
		this.saveBlockList(nbt, "smallFacePositions", this.smallFacePositions);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.readFromNBT(nbt);
		this.readBlockList(nbt, "generatedWispPositions", this.generatedWispPositions);
		this.readBlockList(nbt, "notGeneratedWispPositions", this.notGeneratedWispPositions);
		this.readBlockList(nbt, "largeFacePositions", this.largeFacePositions);
		this.readBlockList(nbt, "smallFacePositions", this.smallFacePositions);
	}

	protected void saveBlockList(CompoundNBT nbt, String name, List<BlockPos> blocks) {
		ListNBT blockList = new ListNBT();
		for(BlockPos pos : blocks) {
			blockList.appendTag(new LongNBT(pos.toLong()));
		}
		nbt.setTag(name, blockList);
	}

	protected void readBlockList(CompoundNBT nbt, String name, List<BlockPos> blocks) {
		blocks.clear();
		ListNBT blockList = nbt.getList(name, Constants.NBT.TAG_LONG);
		for(int i = 0; i < blockList.size(); i++) {
			LongNBT posNbt = (LongNBT) blockList.get(i);
			blocks.add(BlockPos.of(posNbt.getLong()));
		}
	}
}
