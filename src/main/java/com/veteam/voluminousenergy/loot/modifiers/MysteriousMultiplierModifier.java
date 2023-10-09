package com.veteam.voluminousenergy.loot.modifiers;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.items.VEItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class MysteriousMultiplierModifier extends LootModifier implements LootItemFunction {

    public static final Supplier<Codec<MysteriousMultiplierModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(instance -> instance.group(
                            LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(MysteriousMultiplierModifier::getLootItemConditions)
                    ).apply(instance, MysteriousMultiplierModifier::new)
            ));

    public MysteriousMultiplierModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {

        AtomicBoolean generatedMysteriousMultiplier = new AtomicBoolean(false);

        generatedLoot.parallelStream().forEach(generatedItemStack -> {
            if (generatedItemStack.is(VEItems.MYSTERIOUS_MULTIPLIER.get())) {
                generatedMysteriousMultiplier.set(true);
            }
        });

        if (!generatedMysteriousMultiplier.get()) {
            ObjectArrayList<ItemStack> clonedArray = generatedLoot.clone();
            clonedArray.add(new ItemStack(VEItems.MYSTERIOUS_MULTIPLIER.get(), 1));
            return clonedArray;
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

    public LootItemCondition[] getLootItemConditions(){
        return super.conditions;
    }

    @Override
    public LootItemFunctionType getType() {
        return null;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        return this.doApply(ObjectArrayList.of(stack), lootContext).get(0);
    }
}
