package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.VEItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class VETools {

    public static final DeferredRegister<Item> VE_TOOL_REGISTRY = DeferredRegister.create(Registries.ITEM, VoluminousEnergy.MODID);

    // Material Tiers
    public static final TagKey<Block> ALUMINUM_TIER_TAG = TagKey.create(Registries.BLOCK, new ResourceLocation(VoluminousEnergy.MODID, "incorrect_for_iron"));
    public static final Tier ALUMINUM = new VEItemToolTier(250, 6.6F, 2.0F, ALUMINUM_TIER_TAG, 14, () -> Ingredient.of(VEItems.ALUMINUM_INGOT.get())); // Actual tier

    public static final TagKey<Block> CARBON_TIER_TAG = TagKey.create(Registries.BLOCK, new ResourceLocation(VoluminousEnergy.MODID, "incorrect_for_stone"));
    public static final Tier CARBON = new VEItemToolTier(95, 4.0F, 0.8F, CARBON_TIER_TAG, 5, () -> Ingredient.of(VEItems.CARBON_BRICK.get())); // Actual tier

    public static final TagKey<Block> TITANIUM_TIER_TAG = TagKey.create(Registries.BLOCK, new ResourceLocation(VoluminousEnergy.MODID, "incorrect_for_netherite"));
    public static final Tier TITANIUM = new VEItemToolTier(2133, 8.5F, 3.5F, TITANIUM_TIER_TAG, 15, () -> Ingredient.of(VEItems.TITANIUM_INGOT.get())); // Actual tier

    public static final TagKey<Block> TUNGSTEN_TIER_TAG = TagKey.create(Registries.BLOCK, new ResourceLocation(VoluminousEnergy.MODID, "incorrect_for_netherite"));
    public static final Tier TUNGSTEN = new VEItemToolTier(2666, 9.0F, 4.0F, TUNGSTEN_TIER_TAG, 15, () -> Ingredient.of(VEItems.TUNGSTEN_INGOT.get())); // Actual tier

    public static final TagKey<Block> TUNGSTEN_STEEL_TIER_TAG = TagKey.create(Registries.BLOCK, new ResourceLocation(VoluminousEnergy.MODID, "incorrect_for_tungstensteel"));
    public static final Tier TUNGSTEN_STEEL = new VEItemToolTier(2933, 11.0F, 5.0F, TUNGSTEN_STEEL_TIER_TAG, 18, () -> Ingredient.of(VEItems.TUNGSTEN_STEEL_INGOT.get())); // Actual tier

    public static final TagKey<Block> NIGHALITE_TIER_TAG = TagKey.create(Registries.BLOCK, new ResourceLocation(VoluminousEnergy.MODID, "incorrect_for_nighalite"));
    public static final Tier NIGHALITE = new VEItemToolTier(2434, 13F, 4.6F, NIGHALITE_TIER_TAG, 18, () -> Ingredient.of(VEItems.NIGHALITE_INGOT.get())); // Actual tier

    public static final TagKey<Block> EIGHZO_TIER_TAG = TagKey.create(Registries.BLOCK, new ResourceLocation(VoluminousEnergy.MODID, "incorrect_for_eighzo"));
    public static final Tier EIGHZO = new VEItemToolTier(7125, 17F, 6.5F, EIGHZO_TIER_TAG, 18, () -> Ingredient.of(VEItems.EIGHZO_INGOT.get())); // Actual tier

    public static final TagKey<Block> SOLARIUM_TIER_TAG = TagKey.create(Registries.BLOCK, new ResourceLocation(VoluminousEnergy.MODID, "incorrect_for_solarium"));
    public static final Tier SOLARIUM = new VEItemToolTier(17815 /* or 25912 */, 20F, 8F, SOLARIUM_TIER_TAG, 22, () -> Ingredient.of(VEItems.SOLARIUM_INGOT.get())); // Actual tier

    /* TOOLS */
    public static final Item.Properties CARBON_PROP = (new Item.Properties()).durability(CARBON.getUses());
    public static final Item.Properties ALUMINUM_PROP = (new Item.Properties()).durability(ALUMINUM.getUses());
    public static final Item.Properties TITANIUM_PROP = (new Item.Properties()).durability(TITANIUM.getUses());
    public static final Item.Properties TUNGSTEN_PROP = (new Item.Properties()).durability(TUNGSTEN.getUses());
    public static final Item.Properties TUNGSTEN_STEEL_PROP = (new Item.Properties()).durability(TUNGSTEN_STEEL.getUses());
    public static final Item.Properties NIGHALITE_PROP = (new Item.Properties()).durability(NIGHALITE.getUses());
    public static final Item.Properties EIGHZO_PROP = (new Item.Properties()).durability(EIGHZO.getUses());
    public static final Item.Properties SOLARIUM_PROP = (new Item.Properties()).durability(SOLARIUM.getUses());

    // Swords
    public static Supplier<Item> CARBON_SWORD = VE_TOOL_REGISTRY.register("carbon_sword", () -> new SwordItem(CARBON, CARBON_PROP));
    public static Supplier<Item> ALUMINUM_SWORD = VE_TOOL_REGISTRY.register("aluminum_sword", () -> new SwordItem(ALUMINUM, ALUMINUM_PROP));
    public static Supplier<Item> TITANIUM_SWORD = VE_TOOL_REGISTRY.register("titanium_sword", () -> new SwordItem(TITANIUM, TITANIUM_PROP));
    public static Supplier<Item> TUNGSTEN_SWORD = VE_TOOL_REGISTRY.register("tungsten_sword", () -> new SwordItem(TUNGSTEN, TUNGSTEN_PROP));
    public static Supplier<Item> TUNGSTEN_STEEL_SWORD = VE_TOOL_REGISTRY.register("tungsten_steel_sword", () -> new SwordItem(TUNGSTEN_STEEL, TUNGSTEN_STEEL_PROP));
    public static Supplier<Item> NIGHALITE_SWORD = VE_TOOL_REGISTRY.register("nighalite_sword", () -> new SwordItem(NIGHALITE, NIGHALITE_PROP));
    public static Supplier<Item> EIGHZO_SWORD = VE_TOOL_REGISTRY.register("eighzo_sword", () -> new SwordItem(EIGHZO, EIGHZO_PROP));
    public static Supplier<Item> SOLARIUM_SWORD = VE_TOOL_REGISTRY.register("solarium_sword", () -> new VESwordItem(SOLARIUM, SOLARIUM_PROP));

    // Shovels
    public static Supplier<Item> CARBON_SHOVEL = VE_TOOL_REGISTRY.register("carbon_shovel", () -> new ShovelItem(CARBON, CARBON_PROP));
    public static Supplier<Item> ALUMINUM_SHOVEL = VE_TOOL_REGISTRY.register("aluminum_shovel", () -> new ShovelItem(ALUMINUM, ALUMINUM_PROP));
    public static Supplier<Item> TITANIUM_SHOVEL = VE_TOOL_REGISTRY.register("titanium_shovel", () -> new ShovelItem(TITANIUM, TITANIUM_PROP));
    public static Supplier<Item> TUNGSTEN_SHOVEL = VE_TOOL_REGISTRY.register("tungsten_shovel", () -> new ShovelItem(TUNGSTEN, TUNGSTEN_PROP));
    public static Supplier<Item> TUNGSTEN_STEEL_SHOVEL = VE_TOOL_REGISTRY.register("tungsten_steel_shovel", () -> new ShovelItem(TUNGSTEN_STEEL, TUNGSTEN_STEEL_PROP));
    public static Supplier<Item> NIGHALITE_SHOVEL = VE_TOOL_REGISTRY.register("nighalite_shovel", () -> new ShovelItem(NIGHALITE, NIGHALITE_PROP));
    public static Supplier<Item> EIGHZO_SHOVEL = VE_TOOL_REGISTRY.register("eighzo_shovel", () -> new ShovelItem(EIGHZO, EIGHZO_PROP));
    public static Supplier<Item> SOLARIUM_SHOVEL = VE_TOOL_REGISTRY.register("solarium_shovel", () -> new VEShovelItem(SOLARIUM, SOLARIUM_PROP));

    // Pickaxes
    public static Supplier<Item> CARBON_PICKAXE = VE_TOOL_REGISTRY.register("carbon_pickaxe", () -> new PickaxeItem(CARBON, CARBON_PROP));
    public static Supplier<Item> ALUMINUM_PICKAXE = VE_TOOL_REGISTRY.register("aluminum_pickaxe", () -> new PickaxeItem(ALUMINUM, ALUMINUM_PROP));
    public static Supplier<Item> TITANIUM_PICKAXE = VE_TOOL_REGISTRY.register("titanium_pickaxe", () -> new PickaxeItem(TITANIUM, TITANIUM_PROP));
    public static Supplier<Item> TUNGSTEN_PICKAXE = VE_TOOL_REGISTRY.register("tungsten_pickaxe", () -> new PickaxeItem(TUNGSTEN, TUNGSTEN_PROP));
    public static Supplier<Item> TUNGSTEN_STEEL_PICKAXE = VE_TOOL_REGISTRY.register("tungsten_steel_pickaxe", () -> new PickaxeItem(TUNGSTEN_STEEL, TUNGSTEN_STEEL_PROP));
    public static Supplier<Item> NIGHALITE_PICKAXE = VE_TOOL_REGISTRY.register("nighalite_pickaxe", () -> new PickaxeItem(NIGHALITE, NIGHALITE_PROP));
    public static Supplier<Item> EIGHZO_PICKAXE = VE_TOOL_REGISTRY.register("eighzo_pickaxe", () -> new PickaxeItem(EIGHZO, EIGHZO_PROP));
    public static Supplier<Item> SOLARIUM_PICKAXE = VE_TOOL_REGISTRY.register("solarium_pickaxe", () -> new VEPickaxeItem(SOLARIUM, SOLARIUM_PROP));

    // Axes
    public static Supplier<Item> CARBON_AXE = VE_TOOL_REGISTRY.register("carbon_axe", () -> new AxeItem(CARBON, CARBON_PROP));
    public static Supplier<Item> ALUMINUM_AXE = VE_TOOL_REGISTRY.register("aluminum_axe", () -> new AxeItem(ALUMINUM, ALUMINUM_PROP));
    public static Supplier<Item> TITANIUM_AXE = VE_TOOL_REGISTRY.register("titanium_axe", () -> new AxeItem(TITANIUM, TITANIUM_PROP));
    public static Supplier<Item> TUNGSTEN_AXE = VE_TOOL_REGISTRY.register("tungsten_axe", () -> new AxeItem(TUNGSTEN, TUNGSTEN_PROP));
    public static Supplier<Item> TUNGSTEN_STEEL_AXE = VE_TOOL_REGISTRY.register("tungsten_steel_axe", () -> new AxeItem(TUNGSTEN_STEEL, TUNGSTEN_STEEL_PROP));
    public static Supplier<Item> NIGHALITE_AXE = VE_TOOL_REGISTRY.register("nighalite_axe", () -> new AxeItem(NIGHALITE, NIGHALITE_PROP));
    public static Supplier<Item> EIGHZO_AXE = VE_TOOL_REGISTRY.register("eighzo_axe", () -> new AxeItem(EIGHZO, EIGHZO_PROP));
    public static Supplier<Item> SOLARIUM_AXE = VE_TOOL_REGISTRY.register("solarium_axe", () -> new VEAxeItem(SOLARIUM, SOLARIUM_PROP));

    // Hoes
    public static Supplier<Item> CARBON_HOE = VE_TOOL_REGISTRY.register("carbon_hoe", () -> new HoeItem(CARBON, CARBON_PROP));
    public static Supplier<Item> ALUMINUM_HOE = VE_TOOL_REGISTRY.register("aluminum_hoe", () -> new HoeItem(ALUMINUM, ALUMINUM_PROP));
    public static Supplier<Item> TITANIUM_HOE = VE_TOOL_REGISTRY.register("titanium_hoe", () -> new HoeItem(TITANIUM, TITANIUM_PROP));
    public static Supplier<Item> TUNGSTEN_HOE = VE_TOOL_REGISTRY.register("tungsten_hoe", () -> new HoeItem(TUNGSTEN, TUNGSTEN_PROP));
    public static Supplier<Item> TUNGSTEN_STEEL_HOE = VE_TOOL_REGISTRY.register("tungsten_steel_hoe", () -> new HoeItem(TUNGSTEN_STEEL, TUNGSTEN_STEEL_PROP));
    public static Supplier<Item> NIGHALITE_HOE = VE_TOOL_REGISTRY.register("nighalite_hoe", () -> new HoeItem(NIGHALITE, NIGHALITE_PROP));
    public static Supplier<Item> EIGHZO_HOE = VE_TOOL_REGISTRY.register("eighzo_hoe", () -> new HoeItem(EIGHZO, EIGHZO_PROP));
    public static Supplier<Item> SOLARIUM_HOE = VE_TOOL_REGISTRY.register("solarium_hoe", () -> new VEHoeItem(SOLARIUM, SOLARIUM_PROP));

}
