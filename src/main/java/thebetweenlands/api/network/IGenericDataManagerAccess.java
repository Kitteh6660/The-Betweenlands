package thebetweenlands.api.network;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IGenericDataManagerAccess extends TickingBlockEntity {
	public static interface IDataEntry<T> {
		public ResourceKey<T> getKey();

		public void setValue(T valueIn);

		public T getValue();

		public boolean isDirty();

		public void setDirty(boolean dirty);

		public IDataEntry<T> copy();
	}

	public static interface IDataManagedObject {
		/**
		 * Called whenever a data key is changing its value
		 * @param key The data key that has changed its value
		 * @param value The new value
		 * @param fromPacket Whether the new value is from a packet
		 * @return Return true if the change was handled and no further processing is required
		 */
		public default boolean onParameterChange(ResourceKey<?> key, Object value, boolean fromPacket) {
			return false;
		}
	}

	public <T> T get(ResourceKey<T> key);

	public boolean isDirty();

	@Nullable
	public List<IDataEntry<?>> getDirty();

	@Nullable
	public List<IDataEntry<?>> getAll();

	@OnlyIn(Dist.CLIENT)
	public void setValuesFromPacket(List<? extends IDataEntry<?>> newEntries);

	public boolean isEmpty();

	public void setClean();

	@Override
	public void tick();
}
