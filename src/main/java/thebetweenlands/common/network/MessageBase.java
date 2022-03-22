/*package thebetweenlands.common.network;

import java.io.IOException;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public abstract class MessageBase {

	// Encode & Decode no longer needed.
	@Override
	public final void encode(PacketBuffer buf) {
		try {
			serialize(new PacketBuffer(buf));
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public final void decode(PacketBuffer buf) {
		try {
			deserialize(new PacketBuffer(buf));
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public abstract void serialize(PacketBuffer buf) throws IOException;

	public abstract void deserialize(PacketBuffer buf) throws IOException;

	public abstract void handle(<MSG> extends MessageBase message, Supplier<NetworkEvent.Context> ctx);
}
*/