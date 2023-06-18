package com.veteam.voluminousenergy.loot.modifiers;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AnimalFatLootModifier extends LootModifier {
    public static final Supplier<Codec<AnimalFatLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(animalFatLootModifierInstance -> animalFatLootModifierInstance.group(
                            LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(AnimalFatLootModifier::getLootItemConditions),
                            ItemStack.CODEC.fieldOf("addition").forGetter(AnimalFatLootModifier::getItemStackAddition),
                            Codec.INT.fieldOf("minimum_count").forGetter(AnimalFatLootModifier::getMinAmount),
                            Codec.INT.fieldOf("maximum_count").forGetter(AnimalFatLootModifier::getMaxAmount)
                    ).apply(animalFatLootModifierInstance, AnimalFatLootModifier::new)
            ));

    private final ItemStack itemAddition;
    private final int minAmount;
    private final int maxAmount;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    public AnimalFatLootModifier(LootItemCondition[] conditionsIn, ItemStack itemStack, int minAmount, int maxAmount) {
        super(conditionsIn);
        this.itemAddition = itemStack;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public AnimalFatLootModifier(LootItemCondition[] conditionsIn, Item item, int minAmount, int maxAmount) {
        super(conditionsIn);
        this.itemAddition = new ItemStack(item, 1);
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    @Override
    public @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        float luck = context.getLuck() > 0 ? context.getLuck() : 1;
        float lootingModif = context.getLootingModifier() > 0 ? context.getLootingModifier() : 1;
        RandomSource contextualizedRandom = context.getRandom();
        int amount = contextualizedRandom.nextInt(this.minAmount, this.maxAmount);
        amount = Math.round(amount * luck);
        amount = Math.round(amount * lootingModif);
        ItemStack stackToAdd = itemAddition.copy();
        stackToAdd.setCount(amount);
        generatedLoot.add(stackToAdd);
        System.out.println("Generated New Loot");
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec(){
        return CODEC.get();
    }

    public int getMinAmount(){
        return this.minAmount;
    }

    public int getMaxAmount(){
        return this.maxAmount;
    }

    public ItemStack getItemStackAddition(){
        return this.itemAddition;
    }

    public LootItemCondition[] getLootItemConditions(){
        return super.conditions;
    }
}
