package com.veteam.voluminousenergy.loot;

import com.mojang.serialization.Codec;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.loot.functions.MysteriousMultiplierLootFunction;
import com.veteam.voluminousenergy.loot.modifiers.AnimalFatLootModifier;
import com.veteam.voluminousenergy.loot.modifiers.MysteriousMultiplierModifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class VELoot {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> VE_LOOT_MODIFIER_REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, VoluminousEnergy.MODID);

    public static RegistryObject<Codec<AnimalFatLootModifier>> ANIMAL_FAT_LOOT_MODIFIER_CODEC = VE_LOOT_MODIFIER_REGISTRY.register(
            "animal_fat", AnimalFatLootModifier.CODEC);

    public static RegistryObject<Codec<MysteriousMultiplierModifier>> MYSTERIOUS_MULTIPLIER_MODIFIER_CODEC = VE_LOOT_MODIFIER_REGISTRY.register(
            "mysterious_multiplier", MysteriousMultiplierModifier.CODEC);

    private VELoot() {}

    public static final LootItemFunctionType MYSTERIOUS_MULTIPLIER_LOOT_FUNCTION = new LootItemFunctionType(new MysteriousMultiplierLootFunction.Serializer());
    public static final List<ResourceLocation> SPAWN_MYSTERIOUS_MULTIPLIERS_IN = List.of(
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
        modEventBus.addListener(VELoot::registerLootFunctions);
//        MinecraftForge.EVENT_BUS.addListener(VELoot::loadLootTable);
    }

    private static void registerLootFunctions(RegisterEvent event){

        event.register(Registries.LOOT_FUNCTION_TYPE, registerer -> registerer.register(
                ResourceKey.create(Registries.LOOT_FUNCTION_TYPE, new ResourceLocation(VoluminousEnergy.MODID, "mysterious_multiplier_loot")),
                MYSTERIOUS_MULTIPLIER_LOOT_FUNCTION
        ));

    }
//
//    @Deprecated
//    public static void loadLootTable(LootTableLoadEvent event){
//        // Inject Mysterious Multipliers
//        if (SPAWN_MYSTERIOUS_MULTIPLIERS_IN.contains(event.getName())){
////            event.getTable().addPool(buildPoolFromInjectionEntry("mysterious_multiplier/" + event.getName().getPath()));
//        }
//
//    }
//
//    @Deprecated
//    private static LootPool buildPoolFromInjectionEntry(String name){
//        return LootPool.lootPool().add(getInjectionEntry(new ResourceLocation(VoluminousEnergy.MODID, "inject/" + name))).build();
//    }
//
//    @Deprecated
//    private static LootPoolEntryContainer.Builder<?> getInjectionEntry(ResourceLocation resourceLocation){
//        return LootTableReference.lootTableReference(resourceLocation).setWeight(1);
//    }
}
