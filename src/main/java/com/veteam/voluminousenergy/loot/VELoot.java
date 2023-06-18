package com.veteam.voluminousenergy.loot;

import com.mojang.serialization.Codec;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.loot.functions.MysteriousMultiplierLootFunction;
import com.veteam.voluminousenergy.loot.modifiers.AnimalFatLootModifier;
import com.veteam.voluminousenergy.loot.modifiers.MysteriousMultiplierModifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

public class VELoot {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> VE_LOOT_MODIFIER_REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, VoluminousEnergy.MODID);

    public static RegistryObject<Codec<AnimalFatLootModifier>> ANIMAL_FAT_LOOT_MODIFIER_CODEC = VE_LOOT_MODIFIER_REGISTRY.register(
            "animal_fat", AnimalFatLootModifier.CODEC);

    public static RegistryObject<Codec<MysteriousMultiplierModifier>> MYSTERIOUS_MULTIPLIER_MODIFIER_CODEC = VE_LOOT_MODIFIER_REGISTRY.register(
            "mysterious_multiplier", MysteriousMultiplierModifier.CODEC);

    private VELoot() {}

    public static final LootItemFunctionType MYSTERIOUS_MULTIPLIER_LOOT_FUNCTION = new LootItemFunctionType(new MysteriousMultiplierLootFunction.Serializer());

    public static void registerLoot(IEventBus modEventBus){
        modEventBus.addListener(VELoot::registerLootFunctions);
    }

    private static void registerLootFunctions(RegisterEvent event){

        event.register(Registries.LOOT_FUNCTION_TYPE, registerer -> registerer.register(
                ResourceKey.create(Registries.LOOT_FUNCTION_TYPE, new ResourceLocation(VoluminousEnergy.MODID, "mysterious_multiplier_loot")),
                MYSTERIOUS_MULTIPLIER_LOOT_FUNCTION
        ));

    }
}
