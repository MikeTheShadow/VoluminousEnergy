package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.SwordItem;
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
            () -> Ingredient.of(ItemStack.EMPTY.getItem())); // TODO: Material not implemented

    final public static VEItemTier TUNGSTEN_STEEL = new VEItemTier(5, 2933, 11.0F, 5.0F, 18,
            () -> Ingredient.of(ItemStack.EMPTY.getItem())); // TODO: Material not implemented

    final public static VEItemTier NIGHALITE = new VEItemTier(5, 2434, 13F, 4.6F, 18,
            () -> Ingredient.of(ItemStack.EMPTY.getItem())); // TODO: Material not implemented

    final public static VEItemTier EIGHZO = new VEItemTier(6, 7125, 17F, 6.5F, 18,
            () -> Ingredient.of(ItemStack.EMPTY.getItem())); // TODO: Material not implemented

    final public static VEItemTier SOLARIUM = new VEItemTier(7, 17_815/*25_912*/, 20F, 8F, 22,
            () -> Ingredient.of(ItemStack.EMPTY.getItem())); // TODO: Material not implemented

    /* TOOLS */
    private static final Item.Properties PROPERTIES = (new Item.Properties()).tab(VESetup.itemGroup);

    // Aluminum
    public static Item ALUMINUM_SWORD = new SwordItem(ALUMINUM, SWORD_1, SWORD_2, PROPERTIES);

    // Pickaxes
    public static Item CARBON_PICKAXE = new PickaxeItem(CARBON, PICKAXE_1, PICKAXE_2, PROPERTIES).setRegistryName("carbon_pickaxe");
    public static Item ALUMINUM_PICKAXE = new PickaxeItem(ALUMINUM, PICKAXE_1, PICKAXE_2, PROPERTIES).setRegistryName("aluminum_pickaxe");
    public static Item TITANIUM_PICKAXE = new PickaxeItem(TITANIUM, PICKAXE_1, PICKAXE_2, PROPERTIES).setRegistryName("titanium_pickaxe");
    public static Item TUNGSTEN_PICKAXE = new PickaxeItem(TUNGSTEN, PICKAXE_1, PICKAXE_2, PROPERTIES).setRegistryName("tungsten_pickaxe");
    public static Item TUNGSTEN_STEEL_PICKAXE = new PickaxeItem(TUNGSTEN_STEEL, PICKAXE_1, PICKAXE_2, PROPERTIES).setRegistryName("tungsten_steel_pickaxe");
    public static Item NIGHALITE_PICKAXE = new PickaxeItem(NIGHALITE, PICKAXE_1, PICKAXE_2, PROPERTIES).setRegistryName("nighalite_pickaxe");
    public static Item EIGHZO_PICKAXE = new PickaxeItem(EIGHZO, PICKAXE_1, PICKAXE_2, PROPERTIES).setRegistryName("eighzo_pickaxe");
    public static Item SOLARIUM_PICKAXE = new PickaxeItem(SOLARIUM, PICKAXE_1, PICKAXE_2, PROPERTIES).setRegistryName("solarium_pickaxe");
}
