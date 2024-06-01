package thebetweenlands.api.runechain.io;

import java.io.IOException;

import net.minecraft.network.FriendlyByteBuf;
import thebetweenlands.api.runechain.IRuneChainUser;

public interface IInputSerializer<T> {
	public void write(T obj, FriendlyByteBuf buffer);

	public T read(IRuneChainUser user, FriendlyByteBuf buffer) throws IOException;
}