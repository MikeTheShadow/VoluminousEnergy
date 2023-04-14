package com.veteam.voluminousenergy.loot;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.loot.functions.MysteriousMultiplierLootFunction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.List;

public class VELoot {

    private VELoot() {}

    public static final LootItemFunctionType MYSTERIOUS_MULTIPLIER_LOOT_FUNCTION = new LootItemFunctionType(new MysteriousMultiplierLootFunction.Serializer());
    private static final List<ResourceLocation> SPAWN_MYSTERIOUS_MULTIPLIERS_IN = List.of(
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

    public static void registerLoot(IEventBus modEventBus){
        modEventBus.addGenericListener(Block.class, VELoot::registerLootFunctions);
        MinecraftForge.EVENT_BUS.addListener(VELoot::loadLootTable);
    }

    private static void registerLootFunctions(RegistryEvent<Block> event){
        Registry.register(
                Registry.LOOT_FUNCTION_TYPE,
                new ResourceLocation(VoluminousEnergy.MODID, "mysterious_multiplier_loot"),
                MYSTERIOUS_MULTIPLIER_LOOT_FUNCTION
        );
    }

    public static void loadLootTable(LootTableLoadEvent event){
        // Inject Mysterious Multipliers
        if (SPAWN_MYSTERIOUS_MULTIPLIERS_IN.contains(event.getName())){
            event.getTable().addPool(buildPoolFromInjectionEntry("mysterious_multiplier/" + event.getName().getPath()));
        }

    }

    private static LootPool buildPoolFromInjectionEntry(String name){
        return LootPool.lootPool().add(getInjectionEntry(new ResourceLocation(VoluminousEnergy.MODID, "inject/" + name))).build();
    }

    private static LootPoolEntryContainer.Builder<?> getInjectionEntry(ResourceLocation resourceLocation){
        return LootTableReference.lootTableReference(resourceLocation).setWeight(1);
    }
}