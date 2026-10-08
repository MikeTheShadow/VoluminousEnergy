package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Registers blocks and items while exposing the id being registered, so constructors that build
 * their own properties can call {@code setId} with it. Block and item properties must carry their
 * registry id before the block or item is constructed.
 */
public class VERegistryHelper {

    private static final ThreadLocal<Identifier> CURRENT_BLOCK_ID = new ThreadLocal<>();
    private static final ThreadLocal<Identifier> CURRENT_ITEM_ID = new ThreadLocal<>();

    private VERegistryHelper() {
    }

    /** Returns the key of the block being registered, or null outside {@link #registerBlock}. */
    @Nullable
    public static ResourceKey<Block> currentBlockId() {
        Identifier id = CURRENT_BLOCK_ID.get();
        return id == null ? null : ResourceKey.create(Registries.BLOCK, id);
    }

    /** Returns the key of the item being registered, or null outside {@link #registerItem}. */
    @Nullable
    public static ResourceKey<Item> currentItemId() {
        Identifier id = CURRENT_ITEM_ID.get();
        return id == null ? null : ResourceKey.create(Registries.ITEM, id);
    }

    public static <B extends Block> DeferredHolder<Block, B> registerBlock(DeferredRegister<Block> registry, String name, Supplier<? extends B> supplier) {
        Identifier id = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, name);
        return registry.register(name, () -> {
            CURRENT_BLOCK_ID.set(id);
            try {
                return supplier.get();
            } finally {
                CURRENT_BLOCK_ID.remove();
            }
        });
    }

    public static <I extends Item> DeferredHolder<Item, I> registerItem(DeferredRegister<Item> registry, String name, Supplier<? extends I> supplier) {
        Identifier id = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, name);
        return registry.register(name, () -> {
            CURRENT_ITEM_ID.set(id);
            try {
                return supplier.get();
            } finally {
                CURRENT_ITEM_ID.remove();
            }
        });
    }
}
