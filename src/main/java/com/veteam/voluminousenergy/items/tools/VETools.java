package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;
import com.veteam.voluminousenergy.util.VERegistryHelper;

import static com.veteam.voluminousenergy.items.VEItems.VE_ITEM_REGISTRY;

public class VETools {

    // Material Tiers
    public static final TagKey<Block> ALUMINUM_TIER_TAG = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "incorrect_for_iron_tool"));
    public static final TagKey<net.minecraft.world.item.Item> ALUMINUM_REPAIR_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "repairs_aluminum_tool"));
    public static final ToolMaterial ALUMINUM = new ToolMaterial(ALUMINUM_TIER_TAG, 250, 6.6F, 2.0F, 14, ALUMINUM_REPAIR_TAG);

    public static final TagKey<Block> CARBON_TIER_TAG = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "incorrect_for_stone_tool"));
    public static final TagKey<net.minecraft.world.item.Item> CARBON_REPAIR_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "repairs_carbon_tool"));
    public static final ToolMaterial CARBON = new ToolMaterial(CARBON_TIER_TAG, 95, 4.0F, 0.8F, 5, CARBON_REPAIR_TAG);

    public static final TagKey<Block> TITANIUM_TIER_TAG = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "incorrect_for_titanium_tool"));
    public static final TagKey<net.minecraft.world.item.Item> TITANIUM_REPAIR_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "repairs_titanium_tool"));
    public static final ToolMaterial TITANIUM = new ToolMaterial(TITANIUM_TIER_TAG, 2133, 8.5F, 3.5F, 15, TITANIUM_REPAIR_TAG);

    public static final TagKey<Block> TUNGSTEN_TIER_TAG = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "incorrect_for_tungsten_tool"));
    public static final TagKey<net.minecraft.world.item.Item> TUNGSTEN_REPAIR_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "repairs_tungsten_tool"));
    public static final ToolMaterial TUNGSTEN = new ToolMaterial(TUNGSTEN_TIER_TAG, 2666, 9.0F, 4.0F, 15, TUNGSTEN_REPAIR_TAG);

    public static final TagKey<Block> TUNGSTEN_STEEL_TIER_TAG = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "incorrect_for_tungstensteel_tool"));
    public static final TagKey<net.minecraft.world.item.Item> TUNGSTEN_STEEL_REPAIR_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "repairs_tungsten_steel_tool"));
    public static final ToolMaterial TUNGSTEN_STEEL = new ToolMaterial(TUNGSTEN_STEEL_TIER_TAG, 2933, 11.0F, 5.0F, 18, TUNGSTEN_STEEL_REPAIR_TAG);

    public static final TagKey<Block> NIGHALITE_TIER_TAG = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "incorrect_for_nighalite_tool"));
    public static final TagKey<net.minecraft.world.item.Item> NIGHALITE_REPAIR_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "repairs_nighalite_tool"));
    public static final ToolMaterial NIGHALITE = new ToolMaterial(NIGHALITE_TIER_TAG, 2434, 13F, 4.6F, 18, NIGHALITE_REPAIR_TAG);

    public static final TagKey<Block> EIGHZO_TIER_TAG = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "incorrect_for_eighzo_tool"));
    public static final TagKey<net.minecraft.world.item.Item> EIGHZO_REPAIR_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "repairs_eighzo_tool"));
    public static final ToolMaterial EIGHZO = new ToolMaterial(EIGHZO_TIER_TAG, 7125, 17F, 6.5F, 18, EIGHZO_REPAIR_TAG);

    public static final TagKey<Block> SOLARIUM_TIER_TAG = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "incorrect_for_solarium_tool"));
    public static final TagKey<net.minecraft.world.item.Item> SOLARIUM_REPAIR_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "repairs_solarium_tool"));
    public static final ToolMaterial SOLARIUM = new ToolMaterial(SOLARIUM_TIER_TAG, 17815 /* or 25912 */, 20F, 8F, 22, SOLARIUM_REPAIR_TAG);

    /* TOOLS */

    // Swords
    public static Supplier<Item> CARBON_SWORD = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "carbon_sword", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).sword(CARBON, 3, -2.4f)));
    public static Supplier<Item> ALUMINUM_SWORD = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "aluminum_sword", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).sword(ALUMINUM, 3, -2.4F)));
    public static Supplier<Item> TITANIUM_SWORD = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "titanium_sword", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).sword(TITANIUM, 3, -2.4f)));
    public static Supplier<Item> TUNGSTEN_SWORD = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tungsten_sword", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).sword(TUNGSTEN, 3, -2.4f)));
    public static Supplier<Item> TUNGSTEN_STEEL_SWORD = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tungsten_steel_sword", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).sword(TUNGSTEN_STEEL, 3, -2.4f)));
    public static Supplier<Item> NIGHALITE_SWORD = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "nighalite_sword", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).sword(NIGHALITE, 3, -2.4f)));
    public static Supplier<Item> EIGHZO_SWORD = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "eighzo_sword", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).sword(EIGHZO, 3, -2.4f)));
    public static Supplier<Item> SOLARIUM_SWORD = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "solarium_sword", () -> new VESwordItem(new Item.Properties().setId(VERegistryHelper.currentItemId()).sword(SOLARIUM, 3, -2.4f)));

    // Shovels
    public static Supplier<Item> CARBON_SHOVEL = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "carbon_shovel", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).shovel(CARBON, 1.5f, -3.0f)));
    public static Supplier<Item> ALUMINUM_SHOVEL = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "aluminum_shovel", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).shovel(ALUMINUM, 1.5f, -3.0f)));
    public static Supplier<Item> TITANIUM_SHOVEL = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "titanium_shovel", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).shovel(TITANIUM, 1.5f, -3.0f)));
    public static Supplier<Item> TUNGSTEN_SHOVEL = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tungsten_shovel", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).shovel(TUNGSTEN, 1.5f, -3.0f)));
    public static Supplier<Item> TUNGSTEN_STEEL_SHOVEL = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tungsten_steel_shovel", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).shovel(TUNGSTEN_STEEL, 1.5f, -3.0f)));
    public static Supplier<Item> NIGHALITE_SHOVEL = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "nighalite_shovel", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).shovel(NIGHALITE, 1.5f, -3.0f)));
    public static Supplier<Item> EIGHZO_SHOVEL = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "eighzo_shovel", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).shovel(EIGHZO, 1.5f, -3.0f)));
    public static Supplier<Item> SOLARIUM_SHOVEL = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "solarium_shovel", () -> new VEShovelItem(new Item.Properties().setId(VERegistryHelper.currentItemId()).shovel(SOLARIUM, 1.5f, -3.0f)));

    // Pickaxes
    public static Supplier<Item> CARBON_PICKAXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "carbon_pickaxe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).pickaxe(CARBON, 1.0f, -2.8f)));
    public static Supplier<Item> ALUMINUM_PICKAXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "aluminum_pickaxe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).pickaxe(ALUMINUM, 1.0f, -2.8f)));
    public static Supplier<Item> TITANIUM_PICKAXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "titanium_pickaxe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).pickaxe(TITANIUM, 1.0f, -2.8f)));
    public static Supplier<Item> TUNGSTEN_PICKAXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tungsten_pickaxe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).pickaxe(TUNGSTEN, 1.0f, -2.8f)));
    public static Supplier<Item> TUNGSTEN_STEEL_PICKAXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tungsten_steel_pickaxe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).pickaxe(TUNGSTEN_STEEL, 1.0f, -2.8f)));
    public static Supplier<Item> NIGHALITE_PICKAXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "nighalite_pickaxe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).pickaxe(NIGHALITE, 1.0f, -2.8f)));
    public static Supplier<Item> EIGHZO_PICKAXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "eighzo_pickaxe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).pickaxe(EIGHZO, 1.0f, -2.8f)));
    public static Supplier<Item> SOLARIUM_PICKAXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "solarium_pickaxe", () -> new VEPickaxeItem(new Item.Properties().setId(VERegistryHelper.currentItemId()).pickaxe(SOLARIUM, 1.0f, -2.8f)));

    // Axes
    public static Supplier<Item> CARBON_AXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "carbon_axe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).axe(CARBON, 6.0f, -3.0f)));
    public static Supplier<Item> ALUMINUM_AXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "aluminum_axe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).axe(ALUMINUM, 6.0f, -3.0f)));
    public static Supplier<Item> TITANIUM_AXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "titanium_axe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).axe(TITANIUM, 6.0f, -3.0f)));
    public static Supplier<Item> TUNGSTEN_AXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tungsten_axe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).axe(TUNGSTEN, 6.0f, -3.0f)));
    public static Supplier<Item> TUNGSTEN_STEEL_AXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tungsten_steel_axe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).axe(TUNGSTEN_STEEL, 6.0f, -3.0f)));
    public static Supplier<Item> NIGHALITE_AXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "nighalite_axe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).axe(NIGHALITE, 6.0f, -3.0f)));
    public static Supplier<Item> EIGHZO_AXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "eighzo_axe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).axe(EIGHZO, 6.0f, -3.0f)));
    public static Supplier<Item> SOLARIUM_AXE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "solarium_axe", () -> new VEAxeItem(new Item.Properties().setId(VERegistryHelper.currentItemId()).axe(SOLARIUM, 6.0f, -3.0f)));

    // Hoes
    public static Supplier<Item> CARBON_HOE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "carbon_hoe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).hoe(CARBON, -4.0f, -3.0f)));
    public static Supplier<Item> ALUMINUM_HOE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "aluminum_hoe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).hoe(ALUMINUM, -4.0f, -3.0f)));
    public static Supplier<Item> TITANIUM_HOE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "titanium_hoe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).hoe(TITANIUM, -4.0f, -3.0f)));
    public static Supplier<Item> TUNGSTEN_HOE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tungsten_hoe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).hoe(TUNGSTEN, -4.0f, -3.0f)));
    public static Supplier<Item> TUNGSTEN_STEEL_HOE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tungsten_steel_hoe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).hoe(TUNGSTEN_STEEL, -4.0f, -3.0f)));
    public static Supplier<Item> NIGHALITE_HOE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "nighalite_hoe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).hoe(NIGHALITE, -4.0f, -3.0f)));
    public static Supplier<Item> EIGHZO_HOE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "eighzo_hoe", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).hoe(EIGHZO, -4.0f, -3.0f)));
    public static Supplier<Item> SOLARIUM_HOE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "solarium_hoe", () -> new VEHoeItem(new Item.Properties().setId(VERegistryHelper.currentItemId()).hoe(SOLARIUM, -4.0f, -3.0f)));

    public static void init() {

    }
}
