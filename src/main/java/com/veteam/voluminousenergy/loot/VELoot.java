package com.veteam.voluminousenergy.loot;

import com.mojang.serialization.MapCodec;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public class VELoot {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> VE_LOOT_MODIFIER_REGISTRY = DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, VoluminousEnergy.MODID);

    // TODO: MAP CODECS
//    public static Supplier<Codec<AnimalFatLootModifier>> ANIMAL_FAT_LOOT_MODIFIER_CODEC = VE_LOOT_MODIFIER_REGISTRY.register(
//            "animal_fat", AnimalFatLootModifier.CODEC);
//
//    public static Supplier<Codec<MysteriousMultiplierModifier>> MYSTERIOUS_MULTIPLIER_MODIFIER_CODEC = VE_LOOT_MODIFIER_REGISTRY.register(
//            "mysterious_multiplier", MysteriousMultiplierModifier.CODEC);

    private VELoot() {
    }

    public static final List<ResourceKey<LootTable>> SPAWN_MYSTERIOUS_MULTIPLIERS_IN = List.of(
            BuiltInLootTables.BASTION_BRIDGE,
            BuiltInLootTables.END_CITY_TREASURE,
            BuiltInLootTables.SIMPLE_DUNGEON,
            BuiltInLootTables.ABANDONED_MINESHAFT,
            BuiltInLootTables.BURIED_TREASURE,
            BuiltInLootTables.ARMORER_GIFT,
            BuiltInLootTables.BASTION_HOGLIN_STABLE,
            BuiltInLootTables.BASTION_OTHER,
            BuiltInLootTables.BASTION_TREASURE,
            BuiltInLootTables.DESERT_PYRAMID,
            BuiltInLootTables.FISHING_TREASURE,
            BuiltInLootTables.IGLOO_CHEST,
            BuiltInLootTables.JUNGLE_TEMPLE,
            BuiltInLootTables.SHIPWRECK_SUPPLY,
            BuiltInLootTables.SPAWN_BONUS_CHEST,
            BuiltInLootTables.NETHER_BRIDGE,
            BuiltInLootTables.STRONGHOLD_CORRIDOR,
            BuiltInLootTables.STRONGHOLD_CROSSING,
            BuiltInLootTables.VILLAGE_WEAPONSMITH,
            BuiltInLootTables.VILLAGE_TOOLSMITH,
            BuiltInLootTables.VILLAGE_ARMORER,
            BuiltInLootTables.VILLAGE_MASON,
            BuiltInLootTables.UNDERWATER_RUIN_BIG,
            BuiltInLootTables.UNDERWATER_RUIN_SMALL
    );
}
