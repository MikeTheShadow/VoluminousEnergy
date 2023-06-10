package com.veteam.voluminousenergy.achievements.triggers;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class ConstructDimensionalLaserTrigger extends SimpleCriterionTrigger<ConstructDimensionalLaserTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation(VoluminousEnergy.MODID,"construct_dimensional_laser");

   public @NotNull ResourceLocation getId() {
      return ID;
   }

   public ConstructDimensionalLaserTrigger.@NotNull TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate predicateCtx, DeserializationContext deserializationCtx) {
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(jsonObject.get("level"));
      return new ConstructDimensionalLaserTrigger.TriggerInstance(predicateCtx, minmaxbounds$ints);
   }

   public void trigger(ServerPlayer serverPlayer, int p_148031_) {
      this.trigger(serverPlayer, (p_148028_) -> p_148028_.matches(p_148031_));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints level;

      public TriggerInstance(ContextAwarePredicate contextAwarePredicate, MinMaxBounds.Ints minmaxbounds$ints) {
         super(ConstructDimensionalLaserTrigger.ID, contextAwarePredicate);
         this.level = minmaxbounds$ints;
      }

      public static ConstructDimensionalLaserTrigger.TriggerInstance constructedDimensionalLaser() {
         return new ConstructDimensionalLaserTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY);
      }

      public static ConstructDimensionalLaserTrigger.TriggerInstance constructedDimensionalLaser(MinMaxBounds.Ints minmaxbounds$ints) {
         return new ConstructDimensionalLaserTrigger.TriggerInstance(ContextAwarePredicate.ANY, minmaxbounds$ints);
      }

      public boolean matches(int p_148033_) {
         return this.level.matches(p_148033_);
      }

      public @NotNull JsonObject serializeToJson(SerializationContext serializationContext) {
         JsonObject jsonobject = super.serializeToJson(serializationContext);
         jsonobject.add("level", this.level.serializeToJson());
         return jsonobject;
      }
   }
}