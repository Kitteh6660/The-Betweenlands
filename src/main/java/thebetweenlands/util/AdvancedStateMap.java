package thebetweenlands.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancedStateMap extends StateMapperBase {
	
	private final Function<PropertiesMap, String> nameMapper;
	private final String suffix;
	private final List<Property<?>> ignored;
	private final Map<Property<?>, Function<?, String>> suffixProperties;
	private final List<Function<UnmodifiablePropertiesMap, List<Property<?>>>> suffixExclusions;
	private final List<Function<UnmodifiablePropertiesMap, List<Property<?>>>> propertyExclusions;
	private final boolean empty;
	
	private AdvancedStateMap(@Nullable Function<PropertiesMap, String> nameMapper, @Nullable String suffix, List<Property<?>> ignored, Map<Property<?>, Function<?, String>> suffixProperties,
			List<Function<UnmodifiablePropertiesMap, List<Property<?>>>> suffixExclusions, List<Function<UnmodifiablePropertiesMap, List<Property<?>>>> propertyExclusions) {
		this.nameMapper = nameMapper;
		this.suffix = suffix;
		this.ignored = ignored;
		this.suffixProperties = suffixProperties;
		this.suffixExclusions = suffixExclusions;
		this.propertyExclusions = propertyExclusions;
		this.empty = false;
	}
	
	private AdvancedStateMap() {
		this.nameMapper = null;
		this.suffix = null;
		this.ignored = Collections.emptyList();
		this.suffixProperties = Collections.emptyMap();
		this.suffixExclusions = Collections.emptyList();
		this.propertyExclusions = Collections.emptyList();
		this.empty = true;
	}

	@Override
	public Map<BlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
		if(this.empty) {
			return Collections.emptyMap();
		}
		return super.putStateModelLocations(blockIn);
	}
	
	@Override
	protected ModelResourceLocation getModelResourceLocation(BlockState state) {
		Map<Property<?>, Comparable<?>> propertiesMap = Maps.newLinkedHashMap(state.getProperties());
		UnmodifiablePropertiesMap wrappedPropertiesMap = new UnmodifiablePropertiesMap(propertiesMap);
		PropertiesMap wrappedPropertiesMapModifiable = new PropertiesMap(propertiesMap);
		String fileName;

		if (this.nameMapper == null) {
			fileName = ((ResourceLocation)Block.REGISTRY.getNameForObject(state.getBlock())).toString();
		} else {
			fileName = String.format("%s:%s", Block.REGISTRY.getNameForObject(state.getBlock()).getNamespace(), this.nameMapper.apply(wrappedPropertiesMapModifiable));
		}

		if (this.suffix != null) {
			fileName = fileName + this.suffix;
		}

		List<Property<?>> suffixExclusions = new ArrayList<>();

		for(Function<UnmodifiablePropertiesMap, List<Property<?>>> exclusion : this.suffixExclusions) {
			List<Property<?>> excludedProperties = exclusion.apply(wrappedPropertiesMap);
			for(Property<?> excludedProperty : excludedProperties) {
				if(!suffixExclusions.contains(excludedProperty))
					suffixExclusions.add(excludedProperty);
			}
		}

		List<Property<?>> propertyExclusions = new ArrayList<>();

		for(Function<UnmodifiablePropertiesMap, List<Property<?>>> exclusion : this.propertyExclusions) {
			List<Property<?>> excludedProperties = exclusion.apply(wrappedPropertiesMap);
			for(Property<?> excludedProperty : excludedProperties) {
				if(!propertyExclusions.contains(excludedProperty))
					propertyExclusions.add(excludedProperty);
			}
		}

		List<String> propertySuffixes = new ArrayList<String>();
		for(Entry<Property<?>, Comparable<?>> propertyPair : propertiesMap.entrySet()) {
			if(!suffixExclusions.contains(propertyPair.getKey())) {
				@SuppressWarnings("unchecked")
				Function<Comparable<?>, String> nameMapper = (Function<Comparable<?>, String>) this.suffixProperties.get(propertyPair.getKey());
				if(nameMapper != null) {
					String mappedName = nameMapper.apply(propertyPair.getValue());
					if(mappedName != null)
						propertySuffixes.add(mappedName);
				}
			}
		}

		if(!propertySuffixes.isEmpty()) {
			Collections.sort(propertySuffixes);
			StringBuilder strBuilder = new StringBuilder(fileName);
			for(String suffix : propertySuffixes)
				strBuilder.append("_").append(suffix);
			fileName = strBuilder.toString();
		}

		for(Property<?> Property : propertyExclusions) {
			propertiesMap.remove(Property);
		}

		for(Property<?> Property : this.ignored) {
			propertiesMap.remove(Property);
		}

		return new ModelResourceLocation(fileName, this.getPropertyString(propertiesMap));
	}

	@OnlyIn(Dist.CLIENT)
	public static class Builder {
		private Function<PropertiesMap, String> nameMapper;
		private String suffix;
		private final List<Property<?>> ignored = Lists.newArrayList();
		private final Map<Property<?>, Function<?, String>> suffixProperties = new HashMap<>();
		private final List<Function<UnmodifiablePropertiesMap, List<Property<?>>>> suffixExclusions = new ArrayList<>();
		private final List<Function<UnmodifiablePropertiesMap, List<Property<?>>>> propertyExclusions = new ArrayList<>();
		private boolean empty = false;
		
		/**
		 * Sets the state mapper to use the value of the specified property as name
		 * @param builderPropertyIn
		 * @return
		 */
		public <T extends Comparable<T>> AdvancedStateMap.Builder withName(Property<T> property) {
			this.withName((map) -> property.getName(map.remove(property)));
			return this;
		}

		/**
		 * Sets the state mapper to set the name to the value returned by the specified mapper
		 * @param exclusions
		 * @return
		 */
		public AdvancedStateMap.Builder withName(Function<PropertiesMap, String> mapper) {
			this.nameMapper = mapper;
			return this;
		}

		/**
		 * Sets the suffix
		 * @param builderSuffixIn
		 * @return
		 */
		public AdvancedStateMap.Builder withSuffix(String builderSuffixIn) {
			this.suffix = builderSuffixIn;
			return this;
		}

		/**
		 * Sets the state mapper to use the name provided by the name mapper for the value of the specified property as a suffix.
		 * Return null if no suffix should be appended. Sorted alphabetically and separated by '_'.
		 * @param property
		 * @param nameMapper
		 * @return
		 */
		public <T extends Comparable<T>> AdvancedStateMap.Builder withPropertySuffix(Property<T> property, Function<T, String> nameMapper) {
			this.suffixProperties.put(property, nameMapper);
			return this;
		}

		/**
		 * See {@link AdvancedStateMap.Builder#withPropertySuffix(Property, Function)}
		 * @param property
		 * @param nameFalse
		 * @param nameTrue
		 * @return
		 */
		public AdvancedStateMap.Builder withPropertySuffix(Property<Boolean> property, @Nullable String nameFalse, @Nullable String nameTrue) {
			return this.withPropertySuffix(property, (bool) -> bool ? nameTrue : nameFalse);
		}

		/**
		 * See {@link AdvancedStateMap.Builder#withPropertySuffix(Property, Function)}
		 * @param property
		 * @param nameTrue
		 * @return
		 */
		public AdvancedStateMap.Builder withPropertySuffixTrue(Property<Boolean> property, @Nullable String nameTrue) {
			return this.withPropertySuffix(property, b -> b ? nameTrue : null);
		}

		/**
		 * See {@link AdvancedStateMap.Builder#withPropertySuffix(Property, Function)}
		 * @param property
		 * @param nameFalse
		 * @return
		 */
		public AdvancedStateMap.Builder withPropertySuffixFalse(Property<Boolean> property, @Nullable String nameFalse) {
			return this.withPropertySuffix(property, b -> !b ? nameFalse : null);
		}

		/**
		 * Sets the state mapper to exclude certain suffixes if a condition is met.
		 * Return all properties whose suffixes should be ignored.
		 * @param exclusions
		 * @return
		 */
		public AdvancedStateMap.Builder withPropertySuffixExclusions(Function<UnmodifiablePropertiesMap, List<Property<?>>> exclusions) {
			this.suffixExclusions.add(exclusions);
			return this;
		}

		/**
		 * Sets the state mapper to exclude certain properties if a condition is met.
		 * Return all properties which should be ignored.
		 * @param exclusions
		 * @return
		 */
		public AdvancedStateMap.Builder withPropertyExclusions(Function<UnmodifiablePropertiesMap, List<Property<?>>> exclusions) {
			this.propertyExclusions.add(exclusions);
			return this;
		}

		/**
		 * Add properties that will not be used to compute all possible states of a block, used for block rendering
		 * to ignore some property that does not alter block's appearance
		 */
		public AdvancedStateMap.Builder ignore(Property<?>... properties) {
			Collections.addAll(this.ignored, properties);
			return this;
		}

		/**
		 * Whether the state map should be empty, i.e. no state should be mapped at all
		 * @param empty
		 * @return
		 */
		public AdvancedStateMap.Builder empty(boolean empty) {
			this.empty = empty;
			return this;
		}
		
		public AdvancedStateMap build() {
			if(this.empty) {
				return new AdvancedStateMap();
			}
			return new AdvancedStateMap(this.nameMapper, this.suffix, this.ignored, this.suffixProperties, this.suffixExclusions, this.propertyExclusions);
		}
	}

	public static class UnmodifiablePropertiesMap {
		protected final Map<Property<?>, Comparable<?>> propertiesMap;

		protected UnmodifiablePropertiesMap(final Map<Property<?>, Comparable<?>> propertiesMap) {
			this.propertiesMap = propertiesMap;
		}

		/**
		 * Returns the value of the specified property
		 * @param property
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public <T extends Comparable<T>> T getValue(Property<T> property) {
			return (T) this.propertiesMap.get(property);
		}

		/**
		 * Returns all properties
		 * @return
		 */
		public Set<Property<?>> getProperties()  {
			return this.propertiesMap.keySet();
		}
	}

	public static class PropertiesMap extends UnmodifiablePropertiesMap {
		protected PropertiesMap(Map<Property<?>, Comparable<?>> propertiesMap) {
			super(propertiesMap);
		}

		/**
		 * Removes a property and returns the value
		 * @param property
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public <T extends Comparable<T>> T remove(Property<T> property) {
			return (T) this.propertiesMap.remove(property);
		}
	}
}