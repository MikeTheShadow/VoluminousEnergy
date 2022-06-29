package com.veteam.voluminousenergy.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.loot.VELoot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class MysteriousMultiplierLootFunction extends LootItemConditionalFunction {
    protected MysteriousMultiplierLootFunction(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        BlockEntity tile = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
//        VoluminousEnergy.LOGGER.debug("Loot Tile: " + tile.getType().getRegistryName());

        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return VELoot.MYSTERIOUS_MULTIPLIER_LOOT_FUNCTION;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<MysteriousMultiplierLootFunction> {
        @Override
        public MysteriousMultiplierLootFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
            return new MysteriousMultiplierLootFunction(conditions);
        }

    }

    public static class Builder extends LootItemConditionalFunction.Builder<MysteriousMultiplierLootFunction.Builder>{
        @Override
        protected MysteriousMultiplierLootFunction.Builder getThis(){
            return this;
        }

        @Override
        public LootItemFunction build(){
            return new MysteriousMultiplierLootFunction(getConditions());
        }
    }
}
