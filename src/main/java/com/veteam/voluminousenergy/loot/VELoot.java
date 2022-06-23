package com.veteam.voluminousenergy.loot;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.loot.AnimalFat.AnimalFatLootModifier;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VELoot {
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> VE_LOOT_MODIFIER_REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, VoluminousEnergy.MODID);

    public static RegistryObject<AnimalFatLootModifier.Serializer> ANIMAL_FAT_LOOT_MODIFIER_SERIALIZER = VE_LOOT_MODIFIER_REGISTRY.register(
            "animal_fat", AnimalFatLootModifier.Serializer::new);
}
