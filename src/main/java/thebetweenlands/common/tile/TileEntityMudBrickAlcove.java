package thebetweenlands.common.tile;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;

public class TileEntityMudBrickAlcove extends TileEntityLootInventory {
	public boolean hasUrn, topWeb, bottomWeb, smallCandle, bigCandle, outcrop;
	public int urnType = 0, rotationOffset = 0, dungeonLevel = 0;
	public int facing = 0;

	public TileEntityMudBrickAlcove() {
		super(3, "container.bl.mud_bricks_alcove");
	}

	public void setUpGreeble() {
		Random rand = getWorld().rand;
		if(rand.nextInt(3) == 0)
			hasUrn = true;
		if(hasUrn) {
			urnType = rand.nextInt(3);
			rotationOffset = rand.nextInt(41) - 20;
		}
		topWeb = rand.nextBoolean();
		bottomWeb = rand.nextBoolean();
		smallCandle = rand.nextBoolean();
		bigCandle = rand.nextBoolean();
		outcrop = rand.nextBoolean();
	}

	public void setDungeonLevel(int level) {
		dungeonLevel = level;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.readFromNBT(nbt);
		hasUrn = nbt.getBoolean("has_urn");
		urnType = nbt.getInt("urn_type");
		rotationOffset = nbt.getInt("rotationOffset");
		topWeb = nbt.getBoolean("top_web");
		bottomWeb = nbt.getBoolean("bottom_web");
		smallCandle = nbt.getBoolean("small_candle");
		bigCandle = nbt.getBoolean("big_candle");
		outcrop = nbt.getBoolean("out_crop");
		dungeonLevel = nbt.getInt("dungeon_level");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putBoolean("has_urn", hasUrn);
		nbt.putInt("urn_type", urnType);
		nbt.putInt("rotationOffset", this.rotationOffset);
		nbt.putBoolean("top_web", topWeb);
		nbt.putBoolean("bottom_web", bottomWeb);
		nbt.putBoolean("small_candle", smallCandle);
		nbt.putBoolean("big_candle", bigCandle );
		nbt.putBoolean("out_crop", outcrop);
		nbt.putInt("dungeon_level", dungeonLevel);
		return nbt;
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
		return new SUpdateTileEntityPacket(getPos(), 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		readFromNBT(packet.getNbtCompound());
		BlockState state = this.world.getBlockState(this.pos);
		this.world.sendBlockUpdated(pos, state, state, 1);
	}
}
