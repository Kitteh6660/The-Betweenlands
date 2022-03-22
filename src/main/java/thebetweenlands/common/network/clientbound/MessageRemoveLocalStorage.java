package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.world.storage.WorldStorageImpl;

public class MessageRemoveLocalStorage extends MessageBase {
	private StorageID id;

	public MessageRemoveLocalStorage() {}

	public MessageRemoveLocalStorage(StorageID id) {
		this.id = id;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			this.id = StorageID.readFromNBT(buf.readCompoundTag());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeCompoundTag(this.id.save(new CompoundNBT()));
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
			IWorldStorage worldStorage = WorldStorageImpl.getCapability(world);
			ILocalStorageHandler localStorageHandler = worldStorage.getLocalStorageHandler();
			ILocalStorage loadedStorage = localStorageHandler.getLocalStorage(this.id);
			if(loadedStorage != null) {
				localStorageHandler.removeLocalStorage(loadedStorage);
			}
		}
	}
}