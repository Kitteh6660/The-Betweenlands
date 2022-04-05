package thebetweenlands.common.advancments;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import thebetweenlands.common.lib.ModInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RightClickBlockTrigger extends BLTrigger<RightClickBlockTrigger.Instance, RightClickBlockTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "right_click_block");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public RightClickBlockTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new RightClickBlockTrigger.Listener(playerAdvancements);
    }

    @Override
    public RightClickBlockTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        JsonElement sides = json.get("sides");
        Direction[] facings = null;
        if (sides != null && !sides.isJsonNull()) {

        }
        BlockPredicate[] blockPredicates = BlockPredicate.deserializeArray(json.getAsJsonArray("blocks"));
        LocationPredicate locationpredicate = LocationPredicate.fromJson(json.get("location"));
        ItemPredicate[] itemPredicate = ItemPredicate.fromJsonArray(json.get("items"));
        return new RightClickBlockTrigger.Instance(itemPredicate, blockPredicates, locationpredicate, facings);
    }

    public void trigger(ItemStack stack, ServerPlayerEntity player, BlockPos pos, BlockState state, Direction face) {
        RightClickBlockTrigger.Listener listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger(stack, state, pos, face, player.getLevel());
        }
    }

    public static class Instance extends CriterionInstance {
        private final ItemPredicate[] items;
        private final BlockPredicate[] blocks;
        private final LocationPredicate location;
        private final Direction[] facings;

        public Instance(ItemPredicate[] items, BlockPredicate[] blocks, LocationPredicate location, Direction[] facings) {
            super(RightClickBlockTrigger.ID);
            this.items = items;
            this.blocks = blocks;
            this.location = location;
            this.facings = facings;
        }

        public boolean test(ItemStack stack, BlockState state, BlockPos pos, Direction face, ServerWorld world) {
            List<BlockPredicate> blockList = Lists.newArrayList(this.blocks);
            List<ItemPredicate> itemList = Lists.newArrayList(this.items);
            int blockAmount = blockList.size();
            int itemAmount = itemList.size();
            blockList.removeIf(predicate -> predicate.test(state));
            itemList.removeIf(predicate -> predicate.matches(stack));
            boolean matchSide = facings == null || facings.length <= 0 || Arrays.stream(facings).anyMatch(Direction -> Direction.equals(face));
            return matchSide && blockAmount > blockList.size() && itemAmount > itemList.size() && this.location.matches(world, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
        }
    }

    static class Listener extends BLTrigger.Listener<RightClickBlockTrigger.Instance> {
        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger(ItemStack stack, BlockState state, BlockPos pos, Direction face, ServerWorld world) {
            List<ICriterionTrigger.Listener<RightClickBlockTrigger.Instance>> list = new ArrayList<>();

            for (ICriterionTrigger.Listener<RightClickBlockTrigger.Instance> listener : this.listeners) {
                if (listener.getTriggerInstance().test(stack, state, pos, face, world)) {
                    list.add(listener);
                    break;
                }
            }

            for (ICriterionTrigger.Listener<RightClickBlockTrigger.Instance> listener : list) {
                listener.run(this.playerAdvancements);
            }
        }
    }
}
