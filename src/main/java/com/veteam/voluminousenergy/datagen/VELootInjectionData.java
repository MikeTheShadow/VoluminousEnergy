package com.veteam.voluminousenergy.datagen;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.VEItems;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Deprecated
public class VELootInjectionData implements DataProvider {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator dataGenerator;
    
    public VELootInjectionData(DataGenerator dataGenerator){
        this.dataGenerator = dataGenerator;
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        CompletableFuture<?> completableFuture;
        try {
            completableFuture = addMysteriousMultiplierSpawns(cache);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return completableFuture;
    }

    private CompletableFuture<?> addMysteriousMultiplierSpawns(CachedOutput cache) throws IOException {
        ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();

        HashMap<ResourceLocation, LootTable.Builder> tableMap = new HashMap<>();

        tableMap.put(BuiltInLootTables.BASTION_BRIDGE, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),82));
        tableMap.put(BuiltInLootTables.END_CITY_TREASURE, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),92));
        tableMap.put(BuiltInLootTables.SIMPLE_DUNGEON, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),98));
        tableMap.put(BuiltInLootTables.ABANDONED_MINESHAFT, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),99));
        tableMap.put(BuiltInLootTables.BURIED_TREASURE, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),84));
        tableMap.put(BuiltInLootTables.ARMORER_GIFT, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),22));
        tableMap.put(BuiltInLootTables.BASTION_HOGLIN_STABLE, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),82));
        tableMap.put(BuiltInLootTables.BASTION_OTHER, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),82));
        tableMap.put(BuiltInLootTables.BASTION_TREASURE, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),88));
        tableMap.put(BuiltInLootTables.DESERT_PYRAMID, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),99));
        tableMap.put(BuiltInLootTables.FISHING_TREASURE, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),44));
        tableMap.put(BuiltInLootTables.IGLOO_CHEST, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),44));
        tableMap.put(BuiltInLootTables.JUNGLE_TEMPLE, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),82));
        tableMap.put(BuiltInLootTables.SHIPWRECK_SUPPLY, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),48));
        tableMap.put(BuiltInLootTables.SPAWN_BONUS_CHEST, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),56));
        tableMap.put(BuiltInLootTables.NETHER_BRIDGE, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),76));
        tableMap.put(BuiltInLootTables.STRONGHOLD_CORRIDOR, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),52));
        tableMap.put(BuiltInLootTables.STRONGHOLD_CROSSING, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),78));
        tableMap.put(BuiltInLootTables.VILLAGE_WEAPONSMITH, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),22));
        tableMap.put(BuiltInLootTables.VILLAGE_TOOLSMITH, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),22));
        tableMap.put(BuiltInLootTables.VILLAGE_ARMORER, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),22));
        tableMap.put(BuiltInLootTables.VILLAGE_MASON, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),24));
        tableMap.put(BuiltInLootTables.UNDERWATER_RUIN_BIG, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),99));
        tableMap.put(BuiltInLootTables.UNDERWATER_RUIN_SMALL, builder(VEItems.MYSTERIOUS_MULTIPLIER.get(),92));


        for (Map.Entry<ResourceLocation, LootTable.Builder> entry : tableMap.entrySet()){
            Path path = dataGenerator.getPackOutput().getOutputFolder().resolve("data/" + VoluminousEnergy.MODID + "/loot_tables/inject/mysterious_multiplier/" + entry.getKey().getPath() + ".json");

            if (path.toString().contains("multiplier/chests/")){ // TODO: PORT DATAGEN INJECTOR
//                futuresBuilder.add(DataProvider.saveStable(cache, LootTables.serialize(entry.getValue().setParamSet(LootContextParamSets.CHEST).build()), path));
            } else if (path.toString().contains("multiplier/gameplay/fishing")){ // TODO: PORT DATAGEN INJECTOR
//                futuresBuilder.add(DataProvider.saveStable(cache, LootTables.serialize(entry.getValue().setParamSet(LootContextParamSets.FISHING).build()), path));
            } else { // TODO: PORT DATAGEN INJECTOR
//                futuresBuilder.add(DataProvider.saveStable(cache, LootTables.serialize(entry.getValue().setParamSet(LootContextParamSets.ALL_PARAMS).build()), path));
            }

        }
        return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
    }
    
    private LootTable.Builder builder(Item loot, int weight){
        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(loot).setWeight(weight);
        
        LootPool.Builder pool = LootPool.lootPool().setBonusRolls(ConstantValue.exactly(1)).setRolls(ConstantValue.exactly(1));
        pool.add(entry);
        pool.add(EmptyLootItem.emptyItem().setWeight(100-weight));
        return LootTable.lootTable().withPool(pool);
    }
    
    @Override
    public String getName() {
        return "Voluminous Energy Loot Injection";
    }
}
