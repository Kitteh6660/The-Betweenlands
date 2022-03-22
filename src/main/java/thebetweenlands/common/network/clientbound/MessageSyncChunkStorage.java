package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.world.storage.WorldStorageImpl;

public class MessageSyncChunkStorage extends MessageBase {
	private CompoundNBT nbt;
	private ChunkPos pos;

	public MessageSyncChunkStorage() {}

	public MessageSyncChunkStorage(IChunkStorage storage) {
		this.nbt = storage.save(new CompoundNBT(), true);
		this.pos = storage.getChunk().getPos();
	}
	
	public MessageSyncChunkStorage(IChunkStorage storage, CompoundNBT nbt) {
		this.nbt = nbt;
		this.pos = storage.getChunk().getPos();
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			this.pos = new ChunkPos(buf.readInt(), buf.readInt());
			this.nbt = buf.readCompoundTag();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeInt(this.pos.x);
		buf.writeInt(this.pos.z);
		buf.writeCompoundTag(this.nbt);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.handle();
		}
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private void handle() {
		World world = Minecraft.getInstance().world;
		if(world != null) {
			Chunk chunk = world.getChunk(this.pos.x, this.pos.z);
			if(chunk != null) {
				IWorldStorage worldStorage = WorldStorageImpl.getCapability(world);
				IChunkStorage chunkStorage = worldStorage.getChunkStorage(chunk);
				chunkStorage.readFromNBT(this.nbt, true);
			}
		}
	}
}