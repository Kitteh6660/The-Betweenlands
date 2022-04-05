package thebetweenlands.common.advancments;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import thebetweenlands.common.lib.ModInfo;

import java.util.ArrayList;
import java.util.List;

public class BreakBlockTrigger extends BLTrigger<BreakBlockTrigger.Instance, BreakBlockTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "break_block");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public BreakBlockTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new BreakBlockTrigger.Listener(playerAdvancements);
    }

    @Override
    public BreakBlockTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        BlockPredicate[] blockPredicates = BlockPredicate.deserializeArray(json.getAsJsonArray("blocks"));
        LocationPredicate locationpredicate = LocationPredicate.fromJson(json.get("location"));
        return new BreakBlockTrigger.Instance(blockPredicates, locationpredicate);
    }

    public void trigger(ServerPlayerEntity player, BlockPos pos, BlockState state) {
        BreakBlockTrigger.Listener listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger(state, pos, player.getLevel());
        }
    }

    public static class Instance extends CriterionInstance {
        private final BlockPredicate[] blocks;
        private final LocationPredicate location;

        public Instance(BlockPredicate[] blocks, LocationPredicate location) {
            super(BreakBlockTrigger.ID);
            this.blocks = blocks;
            this.location = location;
        }

        public boolean test(BlockState state, BlockPos pos, ServerWorld world) {
            List<BlockPredicate> list = Lists.newArrayList(this.blocks);
            int amount = list.size();
            list.removeIf(predicate -> predicate.test(state));
            return amount > list.size() && this.location.matches(world, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
        }
    }

    static class Listener extends BLTrigger.Listener<BreakBlockTrigger.Instance> {
        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger(BlockState state, BlockPos pos, ServerWorld world) {
            List<ICriterionTrigger.Listener<BreakBlockTrigger.Instance>> list = new ArrayList<>();

            for (ICriterionTrigger.Listener<BreakBlockTrigger.Instance> listener : this.listeners) {
                if (listener.getTriggerInstance().test(state, pos, world)) {
                    list.add(listener);
                    break;
                }
            }

            for (ICriterionTrigger.Listener<BreakBlockTrigger.Instance> listener : list) {
                listener.run(this.playerAdvancements);
            }
        }
    }
}
