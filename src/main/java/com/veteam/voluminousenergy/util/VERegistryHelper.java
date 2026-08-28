package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * 26.1 requires {@link BlockBehaviour.Properties}/{@link Item.Properties} to carry their owning
 * registry id (via {@code setId(...)}) before the Block/Item is constructed, since some lazily
 * evaluated defaults (loot table, description id) read it back during/after registration.
 * <p>
 * Every custom Block/Item in this mod builds its own Properties internally in a no-arg
 * constructor, so there is no natural place to thread the id through. This helper carries the
 * "currently registering" id via a ThreadLocal set by the register* wrapper methods below, and
 * {@link #blockProperties()}/{@link #itemProperties()} pick it up when called from inside that
 * registration window - a drop-in replacement for {@code Block.Properties.of()}/
 * {@code new Item.Properties()}.
 */
public class VERegistryHelper {

    private static final ThreadLocal<Identifier> CURRENT_BLOCK_ID = new ThreadLocal<>();
    private static final ThreadLocal<Identifier> CURRENT_ITEM_ID = new ThreadLocal<>();

    public static BlockBehaviour.Properties blockProperties() {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
        Identifier id = CURRENT_BLOCK_ID.get();
        if (id != null) {
            properties.setId(ResourceKey.create(Registries.BLOCK, id));
        }
        return properties;
    }

    public static Item.Properties itemProperties() {
        Item.Properties properties = new Item.Properties();
        Identifier id = CURRENT_ITEM_ID.get();
        if (id != null) {
            properties.setId(ResourceKey.create(Registries.ITEM, id));
        }
        return properties;
    }

    /** For appending {@code .setId(VERegistryHelper.currentBlockId())} onto an existing Properties chain. */
    @Nullable
    public static ResourceKey<Block> currentBlockId() {
        Identifier id = CURRENT_BLOCK_ID.get();
        return id == null ? null : ResourceKey.create(Registries.BLOCK, id);
    }

    /** For appending {@code .setId(VERegistryHelper.currentItemId())} onto an existing Properties chain. */
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
