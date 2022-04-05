package thebetweenlands.common.world.storage.operation;

import net.minecraft.nbt.CompoundNBT;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.IDeferredStorageOperation;
import thebetweenlands.api.storage.ILocalStorageHandle;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.LocalStorageReference;

public class DeferredLinkOperation implements IDeferredStorageOperation {
	
	private LocalStorageReference ref;

	public DeferredLinkOperation() {

	}

	public DeferredLinkOperation(LocalStorageReference ref) {
		this.ref = ref;
	}

	@Override
	public void apply(IChunkStorage chunkStorage) {
		ILocalStorageHandler handler = chunkStorage.getWorldStorage().getLocalStorageHandler();

		try(ILocalStorageHandle handle = handler.getOrLoadLocalStorage(this.ref)) {
			if(handle != null) {
				chunkStorage.linkLocalStorage(handle.get());
			}
		}
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.ref = LocalStorageReference.load(nbt);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		this.ref.save(nbt);
		return nbt;
	}
}
