package thebetweenlands.common.item.herblore;

import net.minecraft.item.Item;

public class ItemPlantDrop extends Item {
	
	public ItemPlantDrop(Properties properties) {
		super(properties);
		//setMaxDamage(0);
		//setHasSubtypes(true);
	}

	// Old code, no longer needed. To be removed in 1.16.5+
	/*@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (isInCreativeTab(tab)) {
			for (EnumItemPlantDrop type : EnumItemPlantDrop.values())
				list.add(type.create(1));
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		try {
			return "item.thebetweenlands." + IGenericItem.getFromStack(EnumItemPlantDrop.class, stack).getTranslationKey();
		} catch (Exception e) {
			return "item.thebetweenlands.unknown_plant_drop";
		}
	}

	@Override
	public Map<Integer, ResourceLocation> getModels() {
		Map<Integer, ResourceLocation> models = new HashMap<>();
		for(EnumItemPlantDrop type : EnumItemPlantDrop.values())
			models.put(type.getID(), new ResourceLocation(TheBetweenlands.MOD_ID, type.getModelName()));
		return models;
	}

	public enum EnumItemPlantDrop implements IGenericItem {
		GENERIC_LEAF(0),
		ALGAE_ITEM(1),
		ARROW_ARUM_LEAF(2),
		BLUE_EYED_GRASS_FLOWERS(3),
		BLUE_IRIS_PETAL(4),
		MIRE_CORAL_ITEM(5),
		DEEP_WATER_CORAL_ITEM(6),
		BOG_BEAN_FLOWER_ITEM(7),
		BONESET_FLOWERS(8),
		BOTTLE_BRUSH_GRASS_BLADES(9),
		BROOM_SEDGE_LEAVES(10),
		BUTTON_BUSH_FLOWERS(11),
		CARDINAL_FLOWER_PETALS(12),
		CATTAIL_HEAD(13),
		CAVE_GRASS_BLADES(14),
		COPPER_IRIS_PETALS(15),
		GOLDEN_CLUB_FLOWER_ITEM(16),
		LICHEN_ITEM(17),
		MARSH_HIBISCUS_FLOWER(18),
		MARSH_MALLOW_FLOWER(19),
		MARSH_MARIGOLD_FLOWER_ITEM(20),
		NETTLE_LEAF(21),
		PHRAGMITE_STEMS(22),
		PICKEREL_WEED_FLOWER(23),
		SHOOT_LEAVES(24),
		SLUDGECREEP_LEAVES(25),
		SOFT_RUSH_LEAVES(26),
		SUNDEW_HEAD(27),
		SWAMP_TALL_GRASS_BLADES(28),
		CAVE_MOSS_ITEM(29),
		MOSS_ITEM(30),
		MILKWEED_ITEM(31),
		HANGER_ITEM(32),
		PITCHER_PLANT_TRAP(33),
		WATER_WEEDS_ITEM(34),
		VENUS_FLY_TRAP_ITEM(35),
		VOLARPAD_ITEM(36),
		THORNS_ITEM(37),
		POISON_IVY_ITEM(38),
		BLADDERWORT_STALK_ITEM(39),
		BLADDERWORT_FLOWER_ITEM(40),
		EDGE_SHROOM_GILLS(41),
		EDGE_MOSS_CLUMP(42),
		EDGE_LEAF_ITEM(43),
		ROTBULB_STALK(44),
		PALE_GRASS_BLADES(45),
		STRING_ROOT_FIBERS(46),
		CRYPTWEED_BLADES(47);

		private final int id;
		private final String unlocalizedName;
		private final String modelName;

		EnumItemPlantDrop(int id) {
			this.id = id;
			this.modelName = this.name().toLowerCase(Locale.ENGLISH);
			this.unlocalizedName = this.modelName;
		}

		@Override
		public String getTranslationKey() {
			return this.unlocalizedName;
		}

		@Override
		public String getModelName() {
			return this.modelName;
		}

		@Override
		public int getID() {
			return this.id;
		}

		@Override
		public Item getItem() {
			return ItemRegistry.ITEMS_PLANT_DROP;
		}
	}*/
}
