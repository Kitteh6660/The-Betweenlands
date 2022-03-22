package thebetweenlands.common.advancments;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class NoCriteriaTrigger extends BLTrigger<CriterionInstance, NoCriteriaTrigger.Listener> {

    public final ResourceLocation id;

    public NoCriteriaTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public NoCriteriaTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new NoCriteriaTrigger.Listener(playerAdvancements);
    }

    @Override
    public CriterionInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new CriterionInstance(id);
    }

    public void trigger(ServerPlayerEntity player) {
        NoCriteriaTrigger.Listener listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger();
        }
    }

    static class Listener extends BLTrigger.Listener<CriterionInstance> {

        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger() {
            this.listeners.stream().findFirst().ifPresent(listener -> listener.run(this.playerAdvancements));
        }
    }

}
