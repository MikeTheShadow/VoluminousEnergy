package com.veteam.voluminousenergy.achievements.triggers;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ConstructDimensionalLaserTrigger extends SimpleCriterionTrigger<ConstructDimensionalLaserTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation(VoluminousEnergy.MODID,"construct_dimensional_laser");

   public @NotNull ResourceLocation getId() {
      return ID;
   }


   public ConstructDimensionalLaserTrigger.@NotNull TriggerInstance createInstance(JsonObject jsonObject, Optional<ContextAwarePredicate> contextAwarePredicate, DeserializationContext deserializationContext) {
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(jsonObject.get("level"));
      return new ConstructDimensionalLaserTrigger.TriggerInstance(contextAwarePredicate, minmaxbounds$ints);
   }

   public void trigger(ServerPlayer serverPlayer, int p_148031_) {
      this.trigger(serverPlayer, (p_148028_) -> p_148028_.matches(p_148031_));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints level;
      public TriggerInstance(Optional<ContextAwarePredicate> p_297785_, MinMaxBounds.Ints p_286272_) {
         super(p_297785_);
         this.level = p_286272_;
      }

      public static Criterion<ConstructBeaconTrigger.TriggerInstance> constructedDimensionalLaser() {
         return CriteriaTriggers.CONSTRUCT_BEACON.createCriterion(new ConstructBeaconTrigger.TriggerInstance(Optional.empty(), MinMaxBounds.Ints.ANY));
      }

      public static Criterion<ConstructBeaconTrigger.TriggerInstance> constructedDimensionalLaser(MinMaxBounds.Ints p_22766_) {
         return CriteriaTriggers.CONSTRUCT_BEACON.createCriterion(new ConstructBeaconTrigger.TriggerInstance(Optional.empty(), p_22766_));
      }

      public boolean matches(int p_148033_) {
         return this.level.matches(p_148033_);
      }

      @Override
      public @NotNull JsonObject serializeToJson() {
         JsonObject jsonobject = super.serializeToJson();
         jsonobject.add("level", this.level.serializeToJson());
         return jsonobject;
      }
   }
}