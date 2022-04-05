package thebetweenlands.common.advancments;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class CavingRopePlacedTrigger extends BLTrigger<CriterionInstance, CavingRopePlacedTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "cavingrope_placed");

    public CavingRopePlacedTrigger() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CavingRopePlacedTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new CavingRopePlacedTrigger.Listener(playerAdvancements);
    }

    @Override
    public CriterionInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new CriterionInstance(ID);
    }

    public void trigger(ServerPlayerEntity player) {
        CavingRopePlacedTrigger.Listener listeners = this.listeners.get(player.getAdvancements());

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
