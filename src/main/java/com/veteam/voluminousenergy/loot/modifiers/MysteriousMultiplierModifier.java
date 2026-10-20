package com.veteam.voluminousenergy.loot.modifiers;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.items.VEItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class MysteriousMultiplierModifier extends LootModifier {

    public static final Supplier<MapCodec<MysteriousMultiplierModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(instance -> codecStart(instance)
                    .apply(instance, MysteriousMultiplierModifier::new)
            ));

    public MysteriousMultiplierModifier(Optional<Holder<LootItemCondition>> condition, int priority) {
        super(condition, priority);
    }

    public MysteriousMultiplierModifier(Optional<Holder<LootItemCondition>> condition) {
        this(condition, IGlobalLootModifier.DEFAULT_PRIORITY);
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
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
