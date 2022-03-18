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

   public ConstructDimensionalLaserTrigger.@NotNull TriggerInstance createInstance(JsonObject p_22753_, EntityPredicate.Composite p_22754_, DeserializationContext p_22755_) {
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_22753_.get("level"));
      return new ConstructDimensionalLaserTrigger.TriggerInstance(p_22754_, minmaxbounds$ints);
   }

   public void trigger(ServerPlayer p_148030_, int p_148031_) {
      this.trigger(p_148030_, (p_148028_) -> p_148028_.matches(p_148031_));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints level;

      public TriggerInstance(EntityPredicate.Composite p_22763_, MinMaxBounds.Ints p_22764_) {
         super(ConstructDimensionalLaserTrigger.ID, p_22763_);
         this.level = p_22764_;
      }

      public static ConstructDimensionalLaserTrigger.TriggerInstance constructedDimensionalLaser() {
         return new ConstructDimensionalLaserTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY);
      }

      public static ConstructDimensionalLaserTrigger.TriggerInstance constructedDimensionalLaser(MinMaxBounds.Ints p_22766_) {
         return new ConstructDimensionalLaserTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_22766_);
      }

      public boolean matches(int p_148033_) {
         return this.level.matches(p_148033_);
      }

      public @NotNull JsonObject serializeToJson(SerializationContext p_22770_) {
         JsonObject jsonobject = super.serializeToJson(p_22770_);
         jsonobject.add("level", this.level.serializeToJson());
         return jsonobject;
      }
   }
}