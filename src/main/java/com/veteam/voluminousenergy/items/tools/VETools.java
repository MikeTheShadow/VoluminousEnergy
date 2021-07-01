package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;

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
    final public static VEItemTier ALUMINUM = new VEItemTier(2, 250, 6.6F, 2.0F, 14,
            () -> Ingredient.of(VEItems.ALUMINUM_INGOT));

    final public static VEItemTier CARBON = new VEItemTier(1, 95, 4.0F, 0.8F, 5,
            () -> Ingredient.of(VEItems.CARBON_BRICK));

    final public static VEItemTier TITANIUM = new VEItemTier(3, 2133, 8.5F, 3.5F, 15,
            () -> Ingredient.of(VEItems.TITANIUM_INGOT));

    final public static VEItemTier TUNGSTEN = new VEItemTier(4, 2666, 9.0F, 4.0F, 15,
            () -> Ingredient.of(VEItems.TUNGSTEN_INGOT));

    final public static VEItemTier TUNGSTEN_STEEL = new VEItemTier(5, 2933, 11.0F, 5.0F, 18,
            () -> Ingredient.of(VEItems.TUNGSTEN_STEEL_INGOT));

    final public static VEItemTier NIGHALITE = new VEItemTier(5, 2434, 13F, 4.6F, 18,
            () -> Ingredient.of(VEItems.NIGHALITE_INGOT));

    final public static VEItemTier EIGHZO = new VEItemTier(6, 7125, 17F, 6.5F, 18,
            () -> Ingredient.of(VEItems.EIGHZO_INGOT));

    final public static VEItemTier SOLARIUM = new VEItemTier(7, 17_815/*25_912*/, 20F, 8F, 22,
            () -> Ingredient.of(VEItems.SOLARIUM_INGOT));

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
    public static Item SOLARIUM_SWORD = new SwordItem(SOLARIUM, SWORD_1, SWORD_2, SOLARIUM_PROP).setRegistryName("solarium_sword");

    // Shovels
    public static Item CARBON_SHOVEL = new ShovelItem(CARBON, SHOVEL_1, SHOVEL_2, CARBON_PROP).setRegistryName("carbon_shovel");
    public static Item ALUMINUM_SHOVEL = new ShovelItem(ALUMINUM, SHOVEL_1, SHOVEL_2, ALUMINUM_PROP).setRegistryName("aluminum_shovel");
    public static Item TITANIUM_SHOVEL = new ShovelItem(TITANIUM, SHOVEL_1, SHOVEL_2, TITANIUM_PROP).setRegistryName("titanium_shovel");
    public static Item TUNGSTEN_SHOVEL = new ShovelItem(TUNGSTEN, SHOVEL_1, SHOVEL_2, TUNGSTEN_PROP).setRegistryName("tungsten_shovel");
    public static Item TUNGSTEN_STEEL_SHOVEL = new ShovelItem(TUNGSTEN_STEEL, SHOVEL_1, SHOVEL_2, TUNGSTEN_STEEL_PROP).setRegistryName("tungsten_steel_shovel");
    public static Item NIGHALITE_SHOVEL = new ShovelItem(NIGHALITE, SHOVEL_1, SHOVEL_2, NIGHALITE_PROP).setRegistryName("nighalite_shovel");
    public static Item EIGHZO_SHOVEL = new ShovelItem(EIGHZO, SHOVEL_1, SHOVEL_2, EIGHZO_PROP).setRegistryName("eighzo_shovel");
    public static Item SOLARIUM_SHOVEL = new ShovelItem(SOLARIUM, SHOVEL_1, SHOVEL_2, SOLARIUM_PROP).setRegistryName("solarium_shovel");

    // Pickaxes
    public static Item CARBON_PICKAXE = new PickaxeItem(CARBON, PICKAXE_1, PICKAXE_2, CARBON_PROP).setRegistryName("carbon_pickaxe");
    public static Item ALUMINUM_PICKAXE = new PickaxeItem(ALUMINUM, PICKAXE_1, PICKAXE_2, ALUMINUM_PROP).setRegistryName("aluminum_pickaxe");
    public static Item TITANIUM_PICKAXE = new PickaxeItem(TITANIUM, PICKAXE_1, PICKAXE_2, TITANIUM_PROP).setRegistryName("titanium_pickaxe");
    public static Item TUNGSTEN_PICKAXE = new PickaxeItem(TUNGSTEN, PICKAXE_1, PICKAXE_2, TUNGSTEN_PROP).setRegistryName("tungsten_pickaxe");
    public static Item TUNGSTEN_STEEL_PICKAXE = new PickaxeItem(TUNGSTEN_STEEL, PICKAXE_1, PICKAXE_2, TUNGSTEN_STEEL_PROP).setRegistryName("tungsten_steel_pickaxe");
    public static Item NIGHALITE_PICKAXE = new PickaxeItem(NIGHALITE, PICKAXE_1, PICKAXE_2, NIGHALITE_PROP).setRegistryName("nighalite_pickaxe");
    public static Item EIGHZO_PICKAXE = new PickaxeItem(EIGHZO, PICKAXE_1, PICKAXE_2, EIGHZO_PROP).setRegistryName("eighzo_pickaxe");
    public static Item SOLARIUM_PICKAXE = new PickaxeItem(SOLARIUM, PICKAXE_1, PICKAXE_2, SOLARIUM_PROP).setRegistryName("solarium_pickaxe");

    // Axes
    public static Item CARBON_AXE = new AxeItem(CARBON, AXE_1, AXE_2, CARBON_PROP).setRegistryName("carbon_axe");
    public static Item ALUMINUM_AXE = new AxeItem(ALUMINUM, AXE_1, AXE_2, ALUMINUM_PROP).setRegistryName("aluminum_axe");
    public static Item TITANIUM_AXE = new AxeItem(TITANIUM, AXE_1, AXE_2, TITANIUM_PROP).setRegistryName("titanium_axe");
    public static Item TUNGSTEN_AXE = new AxeItem(TUNGSTEN, AXE_1, AXE_2, TUNGSTEN_PROP).setRegistryName("tungsten_axe");
    public static Item TUNGSTEN_STEEL_AXE = new AxeItem(TUNGSTEN_STEEL, AXE_1, AXE_2, TUNGSTEN_STEEL_PROP).setRegistryName("tungsten_steel_axe");
    public static Item NIGHALITE_AXE = new AxeItem(NIGHALITE, AXE_1, AXE_2, NIGHALITE_PROP).setRegistryName("nighalite_axe");
    public static Item EIGHZO_AXE = new AxeItem(EIGHZO, AXE_1, AXE_2, EIGHZO_PROP).setRegistryName("eighzo_axe");
    public static Item SOLARIUM_AXE = new AxeItem(SOLARIUM, AXE_1, AXE_2, SOLARIUM_PROP).setRegistryName("solarium_axe");

    // Hoes
    public static Item CARBON_HOE = new HoeItem(CARBON, HOE_1, HOE_2, CARBON_PROP).setRegistryName("carbon_hoe");
    public static Item ALUMINUM_HOE = new HoeItem(ALUMINUM, HOE_1, HOE_2, ALUMINUM_PROP).setRegistryName("aluminum_hoe");
    public static Item TITANIUM_HOE = new HoeItem(TITANIUM, HOE_1, HOE_2, TITANIUM_PROP).setRegistryName("titanium_hoe");
    public static Item TUNGSTEN_HOE = new HoeItem(TUNGSTEN, HOE_1, HOE_2, TUNGSTEN_PROP).setRegistryName("tungsten_hoe");
    public static Item TUNGSTEN_STEEL_HOE = new HoeItem(TUNGSTEN_STEEL, HOE_1, HOE_2, TUNGSTEN_STEEL_PROP).setRegistryName("tungsten_steel_hoe");
    public static Item NIGHALITE_HOE = new HoeItem(NIGHALITE, HOE_1, HOE_2, NIGHALITE_PROP).setRegistryName("nighalite_hoe");
    public static Item EIGHZO_HOE = new HoeItem(EIGHZO, HOE_1, HOE_2, EIGHZO_PROP).setRegistryName("eighzo_hoe");
    public static Item SOLARIUM_HOE = new HoeItem(SOLARIUM, HOE_1, HOE_2, SOLARIUM_PROP).setRegistryName("solarium_hoe");
}
