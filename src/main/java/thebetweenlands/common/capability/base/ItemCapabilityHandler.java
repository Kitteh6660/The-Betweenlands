package thebetweenlands.common.capability.base;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.common.base.Preconditions;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.api.capability.ISerializableCapability;

public class ItemCapabilityHandler {
	private static final List<ItemCapability<?, ?>> REGISTERED_CAPABILITIES = new ArrayList<ItemCapability<?, ?>>();

	/**
	 * Registers an item capability
	 * @param itemCapability
	 */
	public static <T, F extends ItemCapability<F, T>> void registerItemCapability(ItemCapability<F, T> itemCapability) {
		//Make sure the item capability is the implementation of the capability
		Preconditions.checkState(itemCapability.getCapabilityClass().isAssignableFrom(itemCapability.getClass()), "Item capability %s must implement %s", itemCapability.getClass().getName(), itemCapability.getCapabilityClass().getName());
		REGISTERED_CAPABILITIES.add(itemCapability);
	}

	/**
	 * Registers all capabilities to the {@link CapabilityManager}. Must be called during pre init.
	 */
	public static void registerCapabilities() {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.PREINITIALIZATION));
		for(ItemCapability<?, ?> capability : REGISTERED_CAPABILITIES) {
			registerCapability(capability);
		}
	}

	private static <T> void registerCapability(ItemCapability<?, T> capability) {
		CapabilityManager.INSTANCE.register(capability.getCapabilityClass(), new IStorage<T>() {
			@Override
			public final INBT writeNBT(Capability<T> capability, T instance, Direction side) {
				CompoundNBT nbt = new CompoundNBT();
				if(instance instanceof ISerializableCapability) {
					((ISerializableCapability)instance).save(nbt);
				}
				return nbt;
			}

			@Override
			public final void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
				if(instance instanceof ISerializableCapability && nbt instanceof CompoundNBT) {
					((ISerializableCapability)instance).readFromNBT((CompoundNBT)nbt);
				}
			}
		}, new Callable<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public final T call() throws Exception {
				return (T) capability.getDefaultCapabilityImplementation();
			}
		});
	}

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
		Item item = event.getObject().getItem(); //I don't know what to use... getItemStack returns a stack with a null item...

		for(ItemCapability<?, ?> itemCapability : REGISTERED_CAPABILITIES) {
			if(itemCapability.isApplicable(item)) {
				final Capability<?> capabilityInstance = itemCapability.getCapability();

				event.addCapability(itemCapability.getID(), new ICapabilitySerializable<CompoundNBT>() {
					private Object itemCapability = this.getNewInstance();

					private ItemCapability<?, ?> getNewInstance() {
						ItemCapability<?, ?> itemCapability = (ItemCapability<?, ?>)capabilityInstance.getDefaultInstance();
						itemCapability.setStack(event.getObject());
						itemCapability.init();
						return itemCapability;
					}

					@Override
					public boolean hasCapability(Capability<?> capability, Direction facing) {
						return capability == capabilityInstance;
					}

					@SuppressWarnings("unchecked")
					@Override
					public <T> T getCapability(Capability<T> capability, Direction facing) {
						return capability == capabilityInstance ? (T)this.itemCapability : null;
					}

					@Override
					public CompoundNBT serializeNBT() {
						return this.serialize(capabilityInstance, this.itemCapability);
					}

					@SuppressWarnings("unchecked")
					private <T> CompoundNBT serialize(Capability<T> capability, Object instance) {
						return (CompoundNBT) capability.getStorage().writeNBT(capability, (T)instance, null);
					}

					@Override
					public void deserializeNBT(CompoundNBT nbt) {
						this.deserialize(capabilityInstance, this.itemCapability, nbt);
					}

					@SuppressWarnings("unchecked")
					private <T> void deserialize(Capability<T> capability, Object instance, CompoundNBT nbt) {
						capability.getStorage().readNBT(capability, (T)instance, null, nbt);
					}
				});
			}
		}
	}
}
