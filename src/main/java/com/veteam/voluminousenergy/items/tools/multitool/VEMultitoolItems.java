package com.veteam.voluminousenergy.items.tools.multitool;

import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.items.tools.multitool.bits.VEMultitoolBitData;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;
import com.veteam.voluminousenergy.util.VERegistryHelper;

import static com.veteam.voluminousenergy.items.VEItems.VE_ITEM_REGISTRY;

public class VEMultitoolItems {
    public static Item.Properties MULTITOOL_PROPERTIES = new Item.Properties().setId(VERegistryHelper.currentItemId()).stacksTo(1);

    //Multitool
    public static Supplier<Item> MULTI_TOOL = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "multitool", Multitool::new);

    // Iron
    public static Supplier<BitItem> IRON_DRILL_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "iron_drill_bit", () -> new BitItem(VEMultitoolBitData.IRON_DRILL_BIT_DATA, "iron_drill_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> IRON_CHAIN_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "iron_chain_bit", () -> new BitItem(VEMultitoolBitData.IRON_CHAIN_BIT_DATA, "iron_chain_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> IRON_SCOOPER_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "iron_scooper_bit", () -> new BitItem(VEMultitoolBitData.IRON_SCOOPER_BIT_DATA, "iron_scooper_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> IRON_TRIMMER_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "iron_trimmer_bit", () -> new BitItem(VEMultitoolBitData.IRON_TRIMMER_BIT_DATA, "iron_trimmer_bit", MULTITOOL_PROPERTIES));

    // Diamond
    public static Supplier<BitItem> DIAMOND_DRILL_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "diamond_drill_bit", () -> new BitItem(VEMultitoolBitData.DIAMOND_DRILL_BIT_DATA, "diamond_drill_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> DIAMOND_CHAIN_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "diamond_chain_bit", () -> new BitItem(VEMultitoolBitData.DIAMOND_CHAIN_BIT_DATA, "diamond_chain_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> DIAMOND_SCOOPER_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "diamond_scooper_bit", () -> new BitItem(VEMultitoolBitData.DIAMOND_SCOOPER_BIT_DATA, "diamond_scooper_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> DIAMOND_TRIMMER_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "diamond_trimmer_bit", () -> new BitItem(VEMultitoolBitData.DIAMOND_TRIMMER_BIT_DATA, "diamond_trimmer_bit", MULTITOOL_PROPERTIES));

    // Titanium
    public static Supplier<BitItem> TITANIUM_DRILL_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "titanium_drill_bit", () -> new BitItem(VEMultitoolBitData.TITANIUM_DRILL_BIT_DATA, "titanium_drill_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> TITANIUM_CHAIN_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "titanium_chain_bit", () -> new BitItem(VEMultitoolBitData.TITANIUM_CHAIN_BIT_DATA, "titanium_chain_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> TITANIUM_SCOOPER_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "titanium_scooper_bit", () -> new BitItem(VEMultitoolBitData.TITANIUM_SCOOPER_BIT_DATA, "titanium_scooper_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> TITANIUM_TRIMMER_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "titanium_trimmer_bit", () -> new BitItem(VEMultitoolBitData.TITANIUM_TRIMMER_BIT_DATA, "titanium_trimmer_bit", MULTITOOL_PROPERTIES));

    // Nighalite
    public static Supplier<BitItem> NIGHALITE_DRILL_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "nighalite_drill_bit", () -> new BitItem(VEMultitoolBitData.NIGHALITE_DRILL_BIT_DATA, "nighalite_drill_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> NIGHALITE_CHAIN_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "nighalite_chain_bit", () -> new BitItem(VEMultitoolBitData.NIGHALITE_CHAIN_BIT_DATA, "nighalite_chain_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> NIGHALITE_SCOOPER_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "nighalite_scooper_bit", () -> new BitItem(VEMultitoolBitData.NIGHALITE_SCOOPER_BIT_DATA, "nighalite_scooper_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> NIGHALITE_TRIMMER_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "nighalite_trimmer_bit", () -> new BitItem(VEMultitoolBitData.NIGHALITE_TRIMMER_BIT_DATA, "nighalite_trimmer_bit", MULTITOOL_PROPERTIES));

    // Eighzo
    public static Supplier<BitItem> EIGHZO_DRILL_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "eighzo_drill_bit", () -> new BitItem(VEMultitoolBitData.EIGHZO_DRILL_BIT_DATA, "eighzo_drill_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> EIGHZO_CHAIN_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "eighzo_chain_bit", () -> new BitItem(VEMultitoolBitData.EIGHZO_CHAIN_BIT_DATA, "eighzo_chain_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> EIGHZO_SCOOPER_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "eighzo_scooper_bit", () -> new BitItem(VEMultitoolBitData.EIGHZO_SCOOPER_BIT_DATA, "eighzo_scooper_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> EIGHZO_TRIMMER_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "eighzo_trimmer_bit", () -> new BitItem(VEMultitoolBitData.EIGHZO_TRIMMER_BIT_DATA, "eighzo_trimmer_bit", MULTITOOL_PROPERTIES));

    // Solarium
    public static Supplier<BitItem> SOLARIUM_DRILL_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "solarium_drill_bit", () -> new BitItem(VEMultitoolBitData.SOLARIUM_DRILL_BIT_DATA, "solarium_drill_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> SOLARIUM_CHAIN_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "solarium_chain_bit", () -> new BitItem(VEMultitoolBitData.SOLARIUM_CHAIN_BIT_DATA, "solarium_chain_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> SOLARIUM_SCOOPER_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "solarium_scooper_bit", () -> new BitItem(VEMultitoolBitData.SOLARIUM_SCOOPER_BIT_DATA, "solarium_scooper_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> SOLARIUM_TRIMMER_BIT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "solarium_trimmer_bit", () -> new BitItem(VEMultitoolBitData.SOLARIUM_TRIMMER_BIT_DATA, "solarium_trimmer_bit", MULTITOOL_PROPERTIES));

    public static void init() {

    }
}
