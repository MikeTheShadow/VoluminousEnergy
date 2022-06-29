package com.veteam.voluminousenergy.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.VEItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class VELootInjectionData implements DataProvider {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator dataGenerator;
    
    public VELootInjectionData(DataGenerator dataGenerator){
        this.dataGenerator = dataGenerator;
    }
    
    @Override
    public void run(HashCache cache) throws IOException {
        addMysteriousMultiplierSpawns(cache);
    }

    private void addMysteriousMultiplierSpawns(HashCache cache) throws IOException {
        HashMap<ResourceLocation, LootTable.Builder> tableMap = new HashMap<>();

        tableMap.put(BuiltInLootTables.BASTION_BRIDGE, builder(VEItems.MYSTERIOUS_MULTIPLIER,2));
        tableMap.put(BuiltInLootTables.END_CITY_TREASURE, builder(VEItems.MYSTERIOUS_MULTIPLIER,1));
        tableMap.put(BuiltInLootTables.SIMPLE_DUNGEON, builder(VEItems.MYSTERIOUS_MULTIPLIER,3));
        tableMap.put(BuiltInLootTables.ABANDONED_MINESHAFT, builder(VEItems.MYSTERIOUS_MULTIPLIER,5));
        tableMap.put(BuiltInLootTables.BURIED_TREASURE, builder(VEItems.MYSTERIOUS_MULTIPLIER,3));
        tableMap.put(BuiltInLootTables.ARMORER_GIFT, builder(VEItems.MYSTERIOUS_MULTIPLIER,2));
        tableMap.put(BuiltInLootTables.BASTION_HOGLIN_STABLE, builder(VEItems.MYSTERIOUS_MULTIPLIER,2));
        tableMap.put(BuiltInLootTables.BASTION_OTHER, builder(VEItems.MYSTERIOUS_MULTIPLIER,1));
        tableMap.put(BuiltInLootTables.BASTION_TREASURE, builder(VEItems.MYSTERIOUS_MULTIPLIER,4));
        tableMap.put(BuiltInLootTables.DESERT_PYRAMID, builder(VEItems.MYSTERIOUS_MULTIPLIER,5));
        tableMap.put(BuiltInLootTables.FISHING_TREASURE, builder(VEItems.MYSTERIOUS_MULTIPLIER,2));
        tableMap.put(BuiltInLootTables.IGLOO_CHEST, builder(VEItems.MYSTERIOUS_MULTIPLIER,1));
        tableMap.put(BuiltInLootTables.JUNGLE_TEMPLE, builder(VEItems.MYSTERIOUS_MULTIPLIER,2));
        tableMap.put(BuiltInLootTables.SHIPWRECK_SUPPLY, builder(VEItems.MYSTERIOUS_MULTIPLIER,1));
        tableMap.put(BuiltInLootTables.SPAWN_BONUS_CHEST, builder(VEItems.MYSTERIOUS_MULTIPLIER,5));
        tableMap.put(BuiltInLootTables.NETHER_BRIDGE, builder(VEItems.MYSTERIOUS_MULTIPLIER,2));
        tableMap.put(BuiltInLootTables.STRONGHOLD_CORRIDOR, builder(VEItems.MYSTERIOUS_MULTIPLIER,3));
        tableMap.put(BuiltInLootTables.STRONGHOLD_CROSSING, builder(VEItems.MYSTERIOUS_MULTIPLIER,4));
        tableMap.put(BuiltInLootTables.VILLAGE_WEAPONSMITH, builder(VEItems.MYSTERIOUS_MULTIPLIER,2));
        tableMap.put(BuiltInLootTables.VILLAGE_TOOLSMITH, builder(VEItems.MYSTERIOUS_MULTIPLIER,2));
        tableMap.put(BuiltInLootTables.VILLAGE_ARMORER, builder(VEItems.MYSTERIOUS_MULTIPLIER,2));
        tableMap.put(BuiltInLootTables.VILLAGE_MASON, builder(VEItems.MYSTERIOUS_MULTIPLIER,2));
        tableMap.put(BuiltInLootTables.UNDERWATER_RUIN_BIG, builder(VEItems.MYSTERIOUS_MULTIPLIER,3));
        tableMap.put(BuiltInLootTables.UNDERWATER_RUIN_SMALL, builder(VEItems.MYSTERIOUS_MULTIPLIER,2));


        for (Map.Entry<ResourceLocation, LootTable.Builder> entry : tableMap.entrySet()){
            Path path = dataGenerator.getOutputFolder().resolve("data/" + VoluminousEnergy.MODID + "/loot_tables/inject/mysterious_multiplier/" + entry.getKey().getPath() + ".json");

            if (path.toString().contains("multiplier/chests/")){
                DataProvider.save(GSON, cache, LootTables.serialize(entry.getValue().setParamSet(LootContextParamSets.CHEST).build()), path);
            } else if (path.toString().contains("multiplier/gameplay/fishing")){
                DataProvider.save(GSON, cache, LootTables.serialize(entry.getValue().setParamSet(LootContextParamSets.FISHING).build()), path);
            } else {
                DataProvider.save(GSON, cache, LootTables.serialize(entry.getValue().setParamSet(LootContextParamSets.ALL_PARAMS).build()), path);
            }

        }
    }
    
    private LootTable.Builder builder(Item loot, int weight){
        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(loot).setWeight(weight);
        
        LootPool.Builder pool = LootPool.lootPool().name("main").setBonusRolls(ConstantValue.exactly(1)).setRolls(ConstantValue.exactly(1));
        pool.add(entry);
        pool.add(EmptyLootItem.emptyItem().setWeight(100-weight));
        return LootTable.lootTable().withPool(pool);
    }
    
    @Override
    public String getName() {
        return "Voluminous Energy Loot Injection";
    }
}
