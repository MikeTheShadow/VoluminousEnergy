package com.veteam.voluminousenergy.datagen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LootSpawns {
    public static final Map<ResourceKey<LootTable>, Float> SPAWN_MYSTERIOUS_MULTIPLIERS_IN = inDeclarationOrder(
        Map.entry(BuiltInLootTables.BASTION_BRIDGE, 0.25F),
        Map.entry(BuiltInLootTables.END_CITY_TREASURE, 0.50F),
        Map.entry(BuiltInLootTables.SIMPLE_DUNGEON, 0.15F),
        Map.entry(BuiltInLootTables.ABANDONED_MINESHAFT, 0.15F),
        Map.entry(BuiltInLootTables.BURIED_TREASURE, 0.30F),
        Map.entry(BuiltInLootTables.ARMORER_GIFT, 0.10F),
        Map.entry(BuiltInLootTables.BASTION_HOGLIN_STABLE, 0.25F),
        Map.entry(BuiltInLootTables.BASTION_OTHER, 0.25F),
        Map.entry(BuiltInLootTables.BASTION_TREASURE, 0.40F),
        Map.entry(BuiltInLootTables.DESERT_PYRAMID, 0.20F),
        Map.entry(BuiltInLootTables.FISHING_TREASURE, 0.10F),
        Map.entry(BuiltInLootTables.IGLOO_CHEST, 0.20F),
        Map.entry(BuiltInLootTables.JUNGLE_TEMPLE, 0.20F),
        Map.entry(BuiltInLootTables.SHIPWRECK_SUPPLY, 0.15F),
        Map.entry(BuiltInLootTables.SPAWN_BONUS_CHEST, 1.00F),
        Map.entry(BuiltInLootTables.NETHER_BRIDGE, 0.20F),
        Map.entry(BuiltInLootTables.STRONGHOLD_CORRIDOR, 0.20F),
        Map.entry(BuiltInLootTables.STRONGHOLD_CROSSING, 0.20F),
        Map.entry(BuiltInLootTables.VILLAGE_WEAPONSMITH, 0.10F),
        Map.entry(BuiltInLootTables.VILLAGE_TOOLSMITH, 0.10F),
        Map.entry(BuiltInLootTables.VILLAGE_ARMORER, 0.10F),
        Map.entry(BuiltInLootTables.VILLAGE_MASON, 0.10F),
        Map.entry(BuiltInLootTables.UNDERWATER_RUIN_BIG, 0.20F),
        Map.entry(BuiltInLootTables.UNDERWATER_RUIN_SMALL, 0.10F)
    );


    public static final List<ResourceKey<LootTable>> SHEEP_THAT_DROP_ANIMAL_FAT = BuiltInLootTables.SHEEP.asList();

    // Map.ofEntries iterates in a per-JVM random order, which would reorder the generated loot modifiers on every run.
    @SafeVarargs
    private static <K, V> Map<K, V> inDeclarationOrder(Map.Entry<K, V>... entries) {
        Map<K, V> map = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return Collections.unmodifiableMap(map);
    }

}
