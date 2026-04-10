package com.veteam.voluminousenergy.achievements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ConstructDimensionalLaserTrigger extends SimpleCriterionTrigger<ConstructDimensionalLaserTrigger.TriggerInstance> {
    public ConstructDimensionalLaserTrigger() {
    }

    public static final Identifier ID = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "construct_dimensional_laser");

    public @NotNull Identifier getId() {
        return ID;
    }

    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer p_148030_, int p_148031_) {
        this.trigger(p_148030_, (p_148028_) -> {
            return p_148028_.matches(p_148031_);
        });
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, MinMaxBounds.Ints level) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((p_312562_) ->
                p_312562_.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
                        .forGetter(TriggerInstance::player),
                MinMaxBounds.Ints.CODEC.optionalFieldOf("level", MinMaxBounds.Ints.ANY)
                        .forGetter(TriggerInstance::level)).apply(p_312562_, TriggerInstance::new));

        public TriggerInstance(Optional<ContextAwarePredicate> player, MinMaxBounds.Ints level) {
            this.player = player;
            this.level = level;
        }

        public static Criterion<TriggerInstance> constructedBeacon() {
            return VECriteriaTriggers.CONSTRUCT_DIMENSIONAL_LASER_TRIGGER.get().createCriterion(new TriggerInstance(Optional.empty(), MinMaxBounds.Ints.ANY));
        }

        public static Criterion<TriggerInstance> constructedBeacon(MinMaxBounds.Ints p_22766_) {
            return VECriteriaTriggers.CONSTRUCT_DIMENSIONAL_LASER_TRIGGER.get().createCriterion(new TriggerInstance(Optional.empty(), p_22766_));
        }

        public boolean matches(int p_148033_) {
            return this.level.matches(p_148033_);
        }

        public @NotNull Optional<ContextAwarePredicate> player() {
            return this.player;
        }

        public MinMaxBounds.Ints level() {
            return this.level;
        }
    }
}