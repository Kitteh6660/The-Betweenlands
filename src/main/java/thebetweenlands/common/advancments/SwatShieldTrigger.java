package thebetweenlands.common.advancments;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import thebetweenlands.common.lib.ModInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SwatShieldTrigger extends BLTrigger<SwatShieldTrigger.Instance, SwatShieldTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "swat_shield");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public SwatShieldTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new SwatShieldTrigger.Listener(playerAdvancements);
    }

    @Override
    public SwatShieldTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        EntityPredicate entityPredicates = EntityPredicate.fromJson(json.get("entity"));
        return new SwatShieldTrigger.Instance(entityPredicates);
    }

    public void trigger(ServerPlayerEntity player, LivingEntity entity) {
        SwatShieldTrigger.Listener listener = this.listeners.get(player.getAdvancements());

        if (listener != null) {
            listener.trigger(player, entity);
        }
    }

    public void revert(ServerPlayerEntity player) {
        SwatShieldTrigger.Listener listener = this.listeners.get(player.getAdvancements());

        if (listener != null) {
            listener.revert();
        }
    }

    public static class Instance extends CriterionInstance {

        private final EntityPredicate entity;

        public Instance(EntityPredicate entity) {
            super(SwatShieldTrigger.ID);
            this.entity = entity;
        }

        public boolean test(ServerPlayerEntity player, LivingEntity entity) {
            return this.entity.matches(player, entity);
        }
    }

    static class Listener extends BLTrigger.Listener<SwatShieldTrigger.Instance> {

        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger(ServerPlayerEntity player, LivingEntity entity) {
            List<ICriterionTrigger.Listener<SwatShieldTrigger.Instance>> list = new ArrayList<>();

            for (ICriterionTrigger.Listener<SwatShieldTrigger.Instance> listener : this.listeners) {
                if (listener.getTriggerInstance().test(player, entity)) {
                    list.add(listener);
                    break;
                }
            }

            for (ICriterionTrigger.Listener<SwatShieldTrigger.Instance> listener : list) {
                listener.run(this.playerAdvancements);
            }
        }

        public void revert() {
        	List<Tuple<Advancement, String>> criterions = new ArrayList<>();
            for (ICriterionTrigger.Listener<SwatShieldTrigger.Instance> listener : this.listeners) {
                AdvancementProgress progress = playerAdvancements.getProgress(listener.advancement);
                if (!progress.isDone() && progress.hasProgress()) {
                    for (Map.Entry<String, Criterion> entry: listener.advancement.getCriteria().entrySet()) {
                        if (entry.getValue().getTrigger() instanceof SwatShieldTrigger.Instance) {
                            CriterionProgress criterionProgress = progress.getCriterion(entry.getKey());
                            if (criterionProgress != null && criterionProgress.isDone()) {
                            	criterions.add(new Tuple<>(listener.advancement, entry.getKey()));
                            }
                        }
                    }
                }
            }
            for(Tuple<Advancement, String> criterion : criterions) {
            	this.playerAdvancements.revoke(criterion.getA(), criterion.getB());
            }
        }
    }
}
