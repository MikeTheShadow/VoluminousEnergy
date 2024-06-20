package com.veteam.voluminousenergy.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class RegistryLookups {

    // Lookup an item's ResourceLocation
    public static ResourceLocation lookupItem(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static ResourceLocation lookupItem(ItemStack itemStack) {
        return lookupItem(itemStack.getItem());
    }

    // Get BlockEntityType key

    /**
     * This uses BlockEntityType.getKey instead of looking up in the Forge registry
     **/
    public static ResourceLocation getBlockEntityTypeKey(BlockEntityType<?> blockEntityType) {
        return BlockEntityType.getKey(blockEntityType);
    }

    /**
     * This uses BlockEntityType.getKey instead of looking up in the Forge registry
     **/
    public static ResourceLocation getBlockEntityTypeKey(BlockEntity blockEntity) {
        return getBlockEntityTypeKey(blockEntity.getType());
    }

    public static ResourceLocation lookupBlockEntityType(BlockEntityType<?> blockEntityType) {
        return BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType);
    }

//    public static ResourceLocation lookupBiome(Biome biome) {
//        return NeoForgeRegistries.BIOME_MODIFIER_SERIALIZERS.getKey(biome);
//    }

//    public static ResourceLocation lookupBiome(Holder<Biome> biomeHolder) {
//        return lookupBiome(biomeHolder.get());
//    }

    public static ResourceLocation lookupFluid(Fluid fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid);
    }

    public static ResourceLocation lookupFluidType(FluidType fluidType) {
        return NeoForgeRegistries.FLUID_TYPES.getKey(fluidType);
    }
}
