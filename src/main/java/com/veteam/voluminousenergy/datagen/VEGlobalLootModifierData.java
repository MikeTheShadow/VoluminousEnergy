package com.veteam.voluminousenergy.datagen;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.loot.modifiers.AnimalFatLootModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;
import org.jetbrains.annotations.NotNull;

public class VEGlobalLootModifierData extends GlobalLootModifierProvider {

    public VEGlobalLootModifierData(PackOutput packOutput) {
        super(packOutput, VoluminousEnergy.MODID);
    }

    @Override
    protected void start() {
        LootItemCondition fiftyFiftyChanceCondition = LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.5F, 0.5F).build();


        // Sheep
        add("animal_fat_from_black_sheep",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        fiftyFiftyChanceCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft","entities/sheep/black")).build()
                }, VEItems.ANIMAL_FAT.get(), 0, 2)
        );

        add("animal_fat_from_brown_sheep",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        fiftyFiftyChanceCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft","entities/sheep/brown")).build()
                }, VEItems.ANIMAL_FAT.get(), 0, 2)
        );

        add("animal_fat_from_gray_sheep",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        fiftyFiftyChanceCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft","entities/sheep/gray")).build()
                }, VEItems.ANIMAL_FAT.get(), 0, 2)
        );

        add("animal_fat_from_light_gray_sheep",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        fiftyFiftyChanceCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft","entities/sheep/light_gray")).build()
                }, VEItems.ANIMAL_FAT.get(), 0, 2)
        );

        add("animal_fat_from_pink_sheep",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        fiftyFiftyChanceCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft","entities/sheep/pink")).build()
                }, VEItems.ANIMAL_FAT.get(), 0, 2)
        );

        add("animal_fat_from_white_sheep",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        fiftyFiftyChanceCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft","entities/sheep/white")).build()
                }, VEItems.ANIMAL_FAT.get(), 0, 2)
        );

        // Pig
        add("animal_fat_from_pig",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        fiftyFiftyChanceCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft","entities/pig")).build()
                }, VEItems.ANIMAL_FAT.get(), 1, 3)
        );

        // Cow
        add("animal_fat_from_cow",
                new AnimalFatLootModifier(new LootItemCondition[]{
                        fiftyFiftyChanceCondition,
                        LootTableIdCondition.builder(new ResourceLocation("minecraft","entities/cow")).build()
                }, VEItems.ANIMAL_FAT.get(), 1, 2)
        );


    }



    @Override
    public @NotNull String getName() {
        return "Voluminous Energy Loot Injection for Global Loot Modifiers";
    }
}
