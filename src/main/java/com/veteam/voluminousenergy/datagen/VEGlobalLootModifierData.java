package com.veteam.voluminousenergy.datagen;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.loot.modifiers.AnimalFatLootModifier;
import com.veteam.voluminousenergy.loot.modifiers.MysteriousMultiplierModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class VEGlobalLootModifierData extends GlobalLootModifierProvider {

    public VEGlobalLootModifierData(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(packOutput, provider, VoluminousEnergy.MODID);
    }

    @Override
    protected void start() {
        LootItemCondition fiftyFiftyChanceCondition = LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.5F, 0.5F).build();
        LootItemCondition alwaysMetCondition = LootItemRandomChanceCondition.randomChance(1F).build();


        animalFatModifierProvider(fiftyFiftyChanceCondition);
        mysteriousMultiplierModifierProvider(alwaysMetCondition);

    }

    private void mysteriousMultiplierModifierProvider(LootItemCondition lootCondition) {

        for (ResourceKey<LootTable> lootTabe : LootSpawns.SPAWN_MYSTERIOUS_MULTIPLIERS_IN) {
            String lootTableString = "mysterious_multiplier/" + lootTabe.location().getPath();

            add(lootTableString,
                    new MysteriousMultiplierModifier(new LootItemCondition[]{
                            lootCondition,
                            LootTableIdCondition.builder(lootTabe.location()).build()
                    })
            );
        }

    }

    private void animalFatModifierProvider(LootItemCondition lootCondition) {
        String lootTableString = "animal_fat/";

        // Sheep
        for (ResourceKey<LootTable> lootTable : LootSpawns.SHEEP_THAT_DROP_ANIMAL_FAT) {

            add(lootTableString + lootTable.location().getPath(),
                    new AnimalFatLootModifier(new LootItemCondition[]{
                            lootCondition,
                            LootTableIdCondition.builder(lootTable.location()).build()
                    }, VEItems.ANIMAL_FAT.get(), 0, 2)
            );
        }
        lootTableString += "entities/";

        // Pig
        add(lootTableString + "pig",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        lootCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft", "entities/pig")).build()
                }, VEItems.ANIMAL_FAT.get(), 1, 3)
        );

        // Cow
        add(lootTableString + "cow",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        lootCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft", "entities/cow")).build()
                }, VEItems.ANIMAL_FAT.get(), 1, 2)
        );

        // Mooshroom
        add(lootTableString + "mooshroom",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        lootCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft", "entities/mooshroom")).build()
                }, VEItems.ANIMAL_FAT.get(), 1, 2)
        );

        // Llama
        add(lootTableString + "llama",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        lootCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft", "entities/llama")).build()
                }, VEItems.ANIMAL_FAT.get(), 1, 2)
        );

        // Polar bear
        add(lootTableString + "polar_bear",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        lootCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft", "entities/polar_bear")).build()
                }, VEItems.ANIMAL_FAT.get(), 3, 6)
        );

        // Panda
        add(lootTableString + "panda",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        lootCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft", "entities/panda")).build()
                }, VEItems.ANIMAL_FAT.get(), 0, 2)
        );

        // Dolphin
        add(lootTableString + "dolphin",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        lootCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft", "entities/dolphin")).build()
                }, VEItems.ANIMAL_FAT.get(), 2, 5)
        );

    }


    @Override
    public @NotNull String getName() {
        return "Voluminous Energy Loot Injection for Global Loot Modifiers";
    }
}
