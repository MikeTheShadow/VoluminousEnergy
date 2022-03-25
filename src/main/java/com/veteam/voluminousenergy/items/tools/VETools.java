package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

public class VETools {

    // Tool Constants
    final static int SWORD_1 = 3;
    final static float SWORD_2 = -2.4F;

    final static float SHOVEL_1 = 1.5F;
    final static float SHOVEL_2 = -3.0F;

    final static int PICKAXE_1 = 1;
    final static float PICKAXE_2 = -2.8F;

    final static float AXE_1 = 6.0F;
    final static float AXE_2 = -3.2F;

    final static int HOE_1 = -1;
    final static float HOE_2 = -2.0F;

    // Material Tiers
    public static final TagKey<Block> ALUMINUM_TIER_TAG = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(VoluminousEnergy.MODID,"needs_aluminum_tool"));
    public static final Tier ALUMINUM = TierSortingRegistry.registerTier(
            new ForgeTier(2, 250, 6.6F, 2.0F, 14, ALUMINUM_TIER_TAG, () -> Ingredient.of(VEItems.ALUMINUM_INGOT)), // Actual tier
            new ResourceLocation(VoluminousEnergy.MODID, "needs_aluminum_tool"), // Resource Location
            List.of(Tiers.IRON), // After tier
            List.of()); // Before tier

    public static final TagKey<Block> CARBON_TIER_TAG = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(VoluminousEnergy.MODID,"needs_carbon_tool"));
    public static final Tier CARBON = TierSortingRegistry.registerTier(
            new ForgeTier(1, 95, 4.0F, 0.8F, 5, CARBON_TIER_TAG, () -> Ingredient.of(VEItems.CARBON_BRICK)), // Actual tier
            new ResourceLocation(VoluminousEnergy.MODID, "needs_carbon_tool"), // Resource Location
            List.of(Tiers.WOOD), // After tier
            List.of()); // Before tier

    public static final TagKey<Block> TITANIUM_TIER_TAG = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(VoluminousEnergy.MODID,"needs_titanium_tool"));
    public static final Tier TITANIUM = TierSortingRegistry.registerTier(
            new ForgeTier(3, 2133, 8.5F, 3.5F, 15, TITANIUM_TIER_TAG, () -> Ingredient.of(VEItems.TITANIUM_INGOT)), // Actual tier
            new ResourceLocation(VoluminousEnergy.MODID, "needs_titanium_tool"), // Resource Location
            List.of(Tiers.DIAMOND), // After tier
            List.of()); // Before tier


    public static final TagKey<Block> TUNGSTEN_TIER_TAG =  TagKey.create(Registry.BLOCK_REGISTRY,new ResourceLocation(VoluminousEnergy.MODID,"needs_tungsten_tool"));
    public static final Tier TUNGSTEN = TierSortingRegistry.registerTier(
            new ForgeTier(4, 2666, 9.0F, 4.0F, 15, TUNGSTEN_TIER_TAG, () -> Ingredient.of(VEItems.TUNGSTEN_INGOT)), // Actual tier
            new ResourceLocation(VoluminousEnergy.MODID, "needs_tungsten_tool"), // Resource Location
            List.of(Tiers.NETHERITE), // After tier
            List.of()); // Before tier

    public static final TagKey<Block> TUNGSTEN_STEEL_TIER_TAG = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(VoluminousEnergy.MODID,"needs_tungsten_steel_tool"));
    public static final Tier TUNGSTEN_STEEL = TierSortingRegistry.registerTier(
            new ForgeTier(5, 2933, 11.0F, 5.0F, 18, TUNGSTEN_STEEL_TIER_TAG, () -> Ingredient.of(VEItems.TUNGSTEN_STEEL_INGOT)), // Actual tier
            new ResourceLocation(VoluminousEnergy.MODID, "needs_tungsten_steel_tool"), // Resource Location
            List.of(Tiers.NETHERITE), // After tier
            List.of()); // Before tier

    public static final TagKey<Block> NIGHALITE_TIER_TAG = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(VoluminousEnergy.MODID,"needs_nighalite_tool"));
    public static final Tier NIGHALITE = TierSortingRegistry.registerTier(
            new ForgeTier(5, 2434, 13F, 4.6F, 18, NIGHALITE_TIER_TAG, () -> Ingredient.of(VEItems.NIGHALITE_INGOT)), // Actual tier
            new ResourceLocation(VoluminousEnergy.MODID, "needs_nighalite_tool"), // Resource Location
            List.of(Tiers.NETHERITE), // After tier
            List.of()); // Before tier

    public static final TagKey<Block> EIGHZO_TIER_TAG = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(VoluminousEnergy.MODID,"needs_eighzo_tool"));
    public static final Tier EIGHZO = TierSortingRegistry.registerTier(
            new ForgeTier(6, 7125, 17F, 6.5F, 18, EIGHZO_TIER_TAG, () -> Ingredient.of(VEItems.EIGHZO_INGOT)), // Actual tier
            new ResourceLocation(VoluminousEnergy.MODID, "needs_eighzo_tool"), // Resource Location
            List.of(NIGHALITE), // After tier
            List.of()); // Before tier

    public static final TagKey<Block> SOLARIUM_TIER_TAG = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(VoluminousEnergy.MODID,"needs_solarium_tool"));
    public static final Tier SOLARIUM = TierSortingRegistry.registerTier(
            new ForgeTier(7, 17_815/*25_912*/, 20F, 8F, 22, SOLARIUM_TIER_TAG, () -> Ingredient.of(VEItems.SOLARIUM_INGOT)), // Actual tier
            new ResourceLocation(VoluminousEnergy.MODID, "needs_solarium_tool"), // Resource Location
            List.of(EIGHZO), // After tier
            List.of()); // Before tier

    /* TOOLS */
    public static final Item.Properties CARBON_PROP = (new Item.Properties().tab(VESetup.itemGroup)).durability(CARBON.getUses());
    public static final Item.Properties ALUMINUM_PROP = (new Item.Properties().tab(VESetup.itemGroup)).durability(ALUMINUM.getUses());
    public static final Item.Properties TITANIUM_PROP = (new Item.Properties().tab(VESetup.itemGroup)).durability(TITANIUM.getUses());
    public static final Item.Properties TUNGSTEN_PROP = (new Item.Properties().tab(VESetup.itemGroup)).durability(TUNGSTEN.getUses());
    public static final Item.Properties TUNGSTEN_STEEL_PROP = (new Item.Properties().tab(VESetup.itemGroup)).durability(TUNGSTEN_STEEL.getUses());
    public static final Item.Properties NIGHALITE_PROP = (new Item.Properties().tab(VESetup.itemGroup)).durability(NIGHALITE.getUses());
    public static final Item.Properties EIGHZO_PROP = (new Item.Properties().tab(VESetup.itemGroup)).durability(EIGHZO.getUses());
    public static final Item.Properties SOLARIUM_PROP = (new Item.Properties().tab(VESetup.itemGroup)).durability(SOLARIUM.getUses());

    // Swords
    public static Item CARBON_SWORD = new SwordItem(CARBON, SWORD_1, SWORD_2, CARBON_PROP).setRegistryName("carbon_sword");
    public static Item ALUMINUM_SWORD = new SwordItem(ALUMINUM, SWORD_1, SWORD_2, ALUMINUM_PROP).setRegistryName("aluminum_sword");
    public static Item TITANIUM_SWORD = new SwordItem(TITANIUM, SWORD_1, SWORD_2, TITANIUM_PROP).setRegistryName("titanium_sword");
    public static Item TUNGSTEN_SWORD = new SwordItem(TUNGSTEN, SWORD_1, SWORD_2, TUNGSTEN_PROP).setRegistryName("tungsten_sword");
    public static Item TUNGSTEN_STEEL_SWORD = new SwordItem(TUNGSTEN_STEEL, SWORD_1, SWORD_2, TUNGSTEN_STEEL_PROP).setRegistryName("tungsten_steel_sword");
    public static Item NIGHALITE_SWORD = new SwordItem(NIGHALITE, SWORD_1, SWORD_2, NIGHALITE_PROP).setRegistryName("nighalite_sword");
    public static Item EIGHZO_SWORD = new SwordItem(EIGHZO, SWORD_1, SWORD_2, EIGHZO_PROP).setRegistryName("eighzo_sword");
    public static Item SOLARIUM_SWORD = new VESwordItem(SOLARIUM, SWORD_1, SWORD_2, SOLARIUM_PROP).setRegistryName("solarium_sword");

    // Shovels
    public static Item CARBON_SHOVEL = new ShovelItem(CARBON, SHOVEL_1, SHOVEL_2, CARBON_PROP).setRegistryName("carbon_shovel");
    public static Item ALUMINUM_SHOVEL = new ShovelItem(ALUMINUM, SHOVEL_1, SHOVEL_2, ALUMINUM_PROP).setRegistryName("aluminum_shovel");
    public static Item TITANIUM_SHOVEL = new ShovelItem(TITANIUM, SHOVEL_1, SHOVEL_2, TITANIUM_PROP).setRegistryName("titanium_shovel");
    public static Item TUNGSTEN_SHOVEL = new ShovelItem(TUNGSTEN, SHOVEL_1, SHOVEL_2, TUNGSTEN_PROP).setRegistryName("tungsten_shovel");
    public static Item TUNGSTEN_STEEL_SHOVEL = new ShovelItem(TUNGSTEN_STEEL, SHOVEL_1, SHOVEL_2, TUNGSTEN_STEEL_PROP).setRegistryName("tungsten_steel_shovel");
    public static Item NIGHALITE_SHOVEL = new ShovelItem(NIGHALITE, SHOVEL_1, SHOVEL_2, NIGHALITE_PROP).setRegistryName("nighalite_shovel");
    public static Item EIGHZO_SHOVEL = new ShovelItem(EIGHZO, SHOVEL_1, SHOVEL_2, EIGHZO_PROP).setRegistryName("eighzo_shovel");
    public static Item SOLARIUM_SHOVEL = new VEShovelItem(SOLARIUM, SHOVEL_1, SHOVEL_2, SOLARIUM_PROP).setRegistryName("solarium_shovel");

    // Pickaxes
    public static Item CARBON_PICKAXE = new PickaxeItem(CARBON, PICKAXE_1, PICKAXE_2, CARBON_PROP).setRegistryName("carbon_pickaxe");
    public static Item ALUMINUM_PICKAXE = new PickaxeItem(ALUMINUM, PICKAXE_1, PICKAXE_2, ALUMINUM_PROP).setRegistryName("aluminum_pickaxe");
    public static Item TITANIUM_PICKAXE = new PickaxeItem(TITANIUM, PICKAXE_1, PICKAXE_2, TITANIUM_PROP).setRegistryName("titanium_pickaxe");
    public static Item TUNGSTEN_PICKAXE = new PickaxeItem(TUNGSTEN, PICKAXE_1, PICKAXE_2, TUNGSTEN_PROP).setRegistryName("tungsten_pickaxe");
    public static Item TUNGSTEN_STEEL_PICKAXE = new PickaxeItem(TUNGSTEN_STEEL, PICKAXE_1, PICKAXE_2, TUNGSTEN_STEEL_PROP).setRegistryName("tungsten_steel_pickaxe");
    public static Item NIGHALITE_PICKAXE = new PickaxeItem(NIGHALITE, PICKAXE_1, PICKAXE_2, NIGHALITE_PROP).setRegistryName("nighalite_pickaxe");
    public static Item EIGHZO_PICKAXE = new PickaxeItem(EIGHZO, PICKAXE_1, PICKAXE_2, EIGHZO_PROP).setRegistryName("eighzo_pickaxe");
    public static Item SOLARIUM_PICKAXE = new VEPickaxeItem(SOLARIUM, PICKAXE_1, PICKAXE_2, SOLARIUM_PROP).setRegistryName("solarium_pickaxe");

    // Axes
    public static Item CARBON_AXE = new AxeItem(CARBON, AXE_1, AXE_2, CARBON_PROP).setRegistryName("carbon_axe");
    public static Item ALUMINUM_AXE = new AxeItem(ALUMINUM, AXE_1, AXE_2, ALUMINUM_PROP).setRegistryName("aluminum_axe");
    public static Item TITANIUM_AXE = new AxeItem(TITANIUM, AXE_1, AXE_2, TITANIUM_PROP).setRegistryName("titanium_axe");
    public static Item TUNGSTEN_AXE = new AxeItem(TUNGSTEN, AXE_1, AXE_2, TUNGSTEN_PROP).setRegistryName("tungsten_axe");
    public static Item TUNGSTEN_STEEL_AXE = new AxeItem(TUNGSTEN_STEEL, AXE_1, AXE_2, TUNGSTEN_STEEL_PROP).setRegistryName("tungsten_steel_axe");
    public static Item NIGHALITE_AXE = new AxeItem(NIGHALITE, AXE_1, AXE_2, NIGHALITE_PROP).setRegistryName("nighalite_axe");
    public static Item EIGHZO_AXE = new AxeItem(EIGHZO, AXE_1, AXE_2, EIGHZO_PROP).setRegistryName("eighzo_axe");
    public static Item SOLARIUM_AXE = new VEAxeItem(SOLARIUM, AXE_1, AXE_2, SOLARIUM_PROP).setRegistryName("solarium_axe");

    // Hoes
    public static Item CARBON_HOE = new HoeItem(CARBON, HOE_1, HOE_2, CARBON_PROP).setRegistryName("carbon_hoe");
    public static Item ALUMINUM_HOE = new HoeItem(ALUMINUM, HOE_1, HOE_2, ALUMINUM_PROP).setRegistryName("aluminum_hoe");
    public static Item TITANIUM_HOE = new HoeItem(TITANIUM, HOE_1, HOE_2, TITANIUM_PROP).setRegistryName("titanium_hoe");
    public static Item TUNGSTEN_HOE = new HoeItem(TUNGSTEN, HOE_1, HOE_2, TUNGSTEN_PROP).setRegistryName("tungsten_hoe");
    public static Item TUNGSTEN_STEEL_HOE = new HoeItem(TUNGSTEN_STEEL, HOE_1, HOE_2, TUNGSTEN_STEEL_PROP).setRegistryName("tungsten_steel_hoe");
    public static Item NIGHALITE_HOE = new HoeItem(NIGHALITE, HOE_1, HOE_2, NIGHALITE_PROP).setRegistryName("nighalite_hoe");
    public static Item EIGHZO_HOE = new HoeItem(EIGHZO, HOE_1, HOE_2, EIGHZO_PROP).setRegistryName("eighzo_hoe");
    public static Item SOLARIUM_HOE = new VEHoeItem(SOLARIUM, HOE_1, HOE_2, SOLARIUM_PROP).setRegistryName("solarium_hoe");
}
