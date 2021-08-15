package thebetweenlands.common.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.item.misc.ItemMobAnadia;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

public class LootFunctionSetAnadiaProperties extends LootFunction {
	private static final Logger LOGGER = LogManager.getLogger();

	private final Optional<Boolean> randomize; 
	private final Optional<RandomValueRange> fishColour;
	private final Optional<RandomValueRange> fishSize;
	private final Optional<LootEntry> headItem;
	private final Optional<LootEntry> bodyItem;
	private final Optional<LootEntry> tailItem;
	private final Optional<RandomValueRange> headType;
	private final Optional<RandomValueRange> bodyType;
	private final Optional<RandomValueRange> tailType;
	private final Optional<Boolean> lootFish;
	private final Optional<Boolean> rotten;

	public LootFunctionSetAnadiaProperties(LootCondition[] conditionsIn, Optional<Boolean> randomize, Optional<RandomValueRange> fishColour,
			Optional<RandomValueRange> fishSize, Optional<LootEntry> headItem, Optional<LootEntry> bodyItem, Optional<LootEntry> tailItem,
			Optional<RandomValueRange> headType, Optional<RandomValueRange> bodyType, Optional<RandomValueRange> tailType, Optional<Boolean> lootFish,
			Optional<Boolean> rotten) {
		super(conditionsIn);
		this.randomize = randomize;
		this.fishColour = fishColour;
		this.fishSize = fishSize;
		this.headItem = headItem;
		this.bodyItem = bodyItem;
		this.tailItem = tailItem;
		this.headType = headType;
		this.bodyType = bodyType;
		this.tailType = tailType;
		this.lootFish = lootFish;
		this.rotten = rotten;
	}

	@Override
	public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
		if(stack.getItem() != ItemRegistry.ANADIA) {
			LOGGER.warn("Loot item {} is not an anadia item stack", stack);
		} else {
			Entity entity = ((ItemMobAnadia) stack.getItem()).createCapturedEntity(context.getWorld(), 0, 0, 0, stack, false);

			if(entity instanceof EntityAnadia == false) {
				LOGGER.warn("Loot item {} contains a {} and not an anadia entity", stack, entity.getClass().getName());
			} else {
				EntityAnadia anadia = (EntityAnadia) entity;

				if(this.randomize.orElse(false)) {
					anadia.randomizeAnadiaProperties();
				}

				if(this.fishColour.isPresent()) {
					anadia.setFishColour((byte) this.fishColour.get().generateInt(rand));
				}

				if(this.fishSize.isPresent()) {
					anadia.setFishSize(this.fishSize.get().generateFloat(rand));
				}

				if(this.lootFish.isPresent()) {
					anadia.setAsLootFish(this.lootFish.get());
				}

				if(this.headType.isPresent()) {
					anadia.setHeadType((byte) this.headType.get().generateInt(rand));
				}

				if(this.bodyType.isPresent()) {
					anadia.setBodyType((byte) this.bodyType.get().generateInt(rand));
				}

				if(this.tailType.isPresent()) {
					anadia.setTailType((byte) this.tailType.get().generateInt(rand));
				}

				if(this.headItem.isPresent()) {
					ItemStack partStack = this.getItem(anadia, this.headItem.get(), rand, context);
					if(!partStack.isEmpty()) {
						anadia.setHeadItem(partStack);
					} else {
						LOGGER.warn("Head item generated by loot entry {} is empty", this.headItem.get().getEntryName());
					}
				}

				if(this.bodyItem.isPresent()) {
					ItemStack partStack = this.getItem(anadia, this.bodyItem.get(), rand, context);
					if(!partStack.isEmpty()) {
						anadia.setBodyItem(partStack);
					} else {
						LOGGER.warn("Body item generated by loot entry {} is empty", this.bodyItem.get().getEntryName());
					}
				}

				if(this.tailItem.isPresent()) {
					ItemStack partStack = this.getItem(anadia, this.tailItem.get(), rand, context);
					if(!partStack.isEmpty()) {
						anadia.setTailItem(partStack);
					} else {
						LOGGER.warn("Tail item generated by loot entry {} is empty", this.tailItem.get().getEntryName());
					}
				}

				stack = ItemRegistry.ANADIA.capture(anadia);

				((ItemMobAnadia) stack.getItem()).setRotten(context.getWorld(), stack, this.rotten.orElse(false));
			}
		}
		return stack;
	}

	private ItemStack getItem(EntityAnadia anadia, LootEntry entry, Random rand, LootContext context) {
		LootContext.Builder lootBuilder = new LootContext.Builder(context.getWorld()).withLootedEntity(anadia);
		List<ItemStack> loot = new ArrayList<>();
		entry.addLoot(loot, rand, lootBuilder.build());
		if(!loot.isEmpty()) {
			return loot.get(0);
		}
		return ItemStack.EMPTY;
	}

	public static class Serializer extends LootFunction.Serializer<LootFunctionSetAnadiaProperties> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "set_anadia_properties"), LootFunctionSetAnadiaProperties.class);
		}

		@Override
		public void serialize(JsonObject object, LootFunctionSetAnadiaProperties functionClazz, JsonSerializationContext context) {
			//no thanks
		}

		@Override
		public LootFunctionSetAnadiaProperties deserialize(JsonObject object, JsonDeserializationContext context, LootCondition[] conditionsIn) {
			return new LootFunctionSetAnadiaProperties(conditionsIn,
					Optional.ofNullable(object.has("randomize") ? JsonUtils.getBoolean(object.get("randomize"), "randomize") : null),
					Optional.ofNullable(JsonUtils.deserializeClass(object, "fish_color", null, context, RandomValueRange.class)),
					Optional.ofNullable(JsonUtils.deserializeClass(object, "fish_size", null, context, RandomValueRange.class)),
					Optional.ofNullable(JsonUtils.deserializeClass(object, "head_item", null, context, LootEntry.class)),
					Optional.ofNullable(JsonUtils.deserializeClass(object, "body_item", null, context, LootEntry.class)),
					Optional.ofNullable(JsonUtils.deserializeClass(object, "tail_item", null, context, LootEntry.class)),
					Optional.ofNullable(JsonUtils.deserializeClass(object, "head_type", null, context, RandomValueRange.class)),
					Optional.ofNullable(JsonUtils.deserializeClass(object, "body_type", null, context, RandomValueRange.class)),
					Optional.ofNullable(JsonUtils.deserializeClass(object, "tail_type", null, context, RandomValueRange.class)),
					Optional.ofNullable(object.has("loot_fish") ? JsonUtils.getBoolean(object.get("loot_fish"), "loot_fish") : null),
					Optional.ofNullable(object.has("rotten") ? JsonUtils.getBoolean(object.get("rotten"), "rotten") : null)
					);
		}
	}
}