package com.veteam.voluminousenergy.items.tools.multitool;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.items.tools.multitool.bits.VEMultitoolBitData;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class VEMultitools {
    public static final DeferredRegister<Item> VE_MULTITOOL_ITEM_REGISTRY = DeferredRegister.create(Registries.ITEM, VoluminousEnergy.MODID);
    public static Item.Properties MULTITOOL_PROPERTIES = new Item.Properties().stacksTo(1);

    // Multitools
    public static Supplier<CombustionMultitool> EMPTY_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("empty_multitool", () -> new CombustionMultitool(null, "empty_multitool", MULTITOOL_PROPERTIES));

    // Iron
    public static Supplier<CombustionMultitool> IRON_DRILL_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("iron_drill_multitool", () -> new CombustionMultitool(VEMultitoolBitData.IRON_DRILL_BIT_DATA, "iron_drill_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> IRON_CHAIN_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("iron_chain_multitool", () -> new CombustionMultitool(VEMultitoolBitData.IRON_CHAIN_BIT_DATA, "iron_chain_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> IRON_SCOOPER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("iron_scooper_multitool", () -> new CombustionMultitool(VEMultitoolBitData.IRON_SCOOPER_BIT_DATA, "iron_scooper_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> IRON_TRIMMER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("iron_trimmer_multitool", () -> new CombustionMultitool(VEMultitoolBitData.IRON_TRIMMER_BIT_DATA, "iron_trimmer_multitool", MULTITOOL_PROPERTIES));

    // Diamond
    public static Supplier<CombustionMultitool> DIAMOND_DRILL_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_drill_multitool", () -> new CombustionMultitool(VEMultitoolBitData.DIAMOND_DRILL_BIT_DATA, "diamond_drill_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> DIAMOND_CHAIN_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_chain_multitool", () -> new CombustionMultitool(VEMultitoolBitData.DIAMOND_CHAIN_BIT_DATA, "diamond_chain_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> DIAMOND_SCOOPER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_scooper_multitool", () -> new CombustionMultitool(VEMultitoolBitData.DIAMOND_SCOOPER_BIT_DATA, "diamond_scooper_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> DIAMOND_TRIMMER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_trimmer_multitool", () -> new CombustionMultitool(VEMultitoolBitData.DIAMOND_TRIMMER_DATA, "diamond_trimmer_multitool", MULTITOOL_PROPERTIES));

    // Titanium
    public static Supplier<CombustionMultitool> TITANIUM_DRILL_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_drill_multitool", () -> new CombustionMultitool(VEMultitoolBitData.TITANIUM_DRILL_BIT_DATA, "titanium_drill_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> TITANIUM_CHAIN_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_chain_multitool", () -> new CombustionMultitool(VEMultitoolBitData.TITANIUM_CHAIN_BIT_DATA, "titanium_chain_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> TITANIUM_SCOOPER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_scooper_multitool", () -> new CombustionMultitool(VEMultitoolBitData.TITANIUM_SCOOPER_BIT_DATA, "titanium_scooper_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> TITANIUM_TRIMMER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_trimmer_multitool", () -> new CombustionMultitool(VEMultitoolBitData.TITANIUM_TRIMMER_DATA, "titanium_trimmer_multitool", MULTITOOL_PROPERTIES));

    // Nighalite
    public static Supplier<CombustionMultitool> NIGHALITE_DRILL_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_drill_multitool", () -> new CombustionMultitool(VEMultitoolBitData.NIGHALITE_DRILL_BIT_DATA, "nighalite_drill_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> NIGHALITE_CHAIN_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_chain_multitool", () -> new CombustionMultitool(VEMultitoolBitData.NIGHALITE_CHAIN_BIT_DATA, "nighalite_chain_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> NIGHALITE_SCOOPER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_scooper_multitool", () -> new CombustionMultitool(VEMultitoolBitData.NIGHALITE_SCOOPER_BIT_DATA, "nighalite_scooper_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> NIGHALITE_TRIMMER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_trimmer_multitool", () -> new CombustionMultitool(VEMultitoolBitData.NIGHALITE_TRIMMER_DATA, "nighalite_trimmer_multitool", MULTITOOL_PROPERTIES));

    // Eighzo
    public static Supplier<CombustionMultitool> EIGHZO_DRILL_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_drill_multitool", () -> new CombustionMultitool(VEMultitoolBitData.EIGHZO_DRILL_BIT_DATA, "eighzo_drill_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> EIGHZO_CHAIN_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_chain_multitool", () -> new CombustionMultitool(VEMultitoolBitData.EIGHZO_CHAIN_BIT_DATA, "eighzo_chain_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> EIGHZO_SCOOPER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_scooper_multitool", () -> new CombustionMultitool(VEMultitoolBitData.EIGHZO_SCOOPER_BIT_DATA, "eighzo_scooper_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> EIGHZO_TRIMMER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_trimmer_multitool", () -> new CombustionMultitool(VEMultitoolBitData.EIGHZO_TRIMMER_DATA, "eighzo_trimmer_multitool", MULTITOOL_PROPERTIES));

    // Solarium
    public static Supplier<CombustionMultitool> SOLARIUM_DRILL_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_drill_multitool", () -> new CombustionMultitool(VEMultitoolBitData.SOLARIUM_DRILL_BIT_DATA, "solarium_drill_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> SOLARIUM_CHAIN_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_chain_multitool", () -> new CombustionMultitool(VEMultitoolBitData.SOLARIUM_CHAIN_BIT_DATA, "solarium_chain_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> SOLARIUM_SCOOPER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_scooper_multitool", () -> new CombustionMultitool(VEMultitoolBitData.SOLARIUM_SCOOPER_BIT_DATA, "solarium_scooper_multitool", MULTITOOL_PROPERTIES));
    public static Supplier<CombustionMultitool> SOLARIUM_TRIMMER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_trimmer_multitool", () -> new CombustionMultitool(VEMultitoolBitData.SOLARIUM_TRIMMER_DATA, "solarium_trimmer_multitool", MULTITOOL_PROPERTIES));

    // BITS

    // Iron
    public static Supplier<BitItem> IRON_DRILL_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("iron_drill_bit", () -> new BitItem(VEMultitoolBitData.IRON_DRILL_BIT_DATA, "iron_drill_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> IRON_CHAIN_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("iron_chain_bit", () -> new BitItem(VEMultitoolBitData.IRON_CHAIN_BIT_DATA, "iron_chain_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> IRON_SCOOPER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("iron_scooper_bit", () -> new BitItem(VEMultitoolBitData.IRON_SCOOPER_BIT_DATA, "iron_scooper_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> IRON_TRIMMER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("iron_trimmer_bit", () -> new BitItem(VEMultitoolBitData.IRON_TRIMMER_BIT_DATA, "iron_trimmer_bit", MULTITOOL_PROPERTIES));

    // Diamond
    public static Supplier<BitItem> DIAMOND_DRILL_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_drill_bit", () -> new BitItem(VEMultitoolBitData.DIAMOND_DRILL_BIT_DATA, "diamond_drill_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> DIAMOND_CHAIN_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_chain_bit", () -> new BitItem(VEMultitoolBitData.DIAMOND_CHAIN_BIT_DATA, "diamond_chain_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> DIAMOND_SCOOPER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_scooper_bit", () -> new BitItem(VEMultitoolBitData.DIAMOND_SCOOPER_BIT_DATA, "diamond_scooper_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> DIAMOND_TRIMMER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_trimmer_bit", () -> new BitItem(VEMultitoolBitData.DIAMOND_TRIMMER_DATA, "diamond_trimmer_bit", MULTITOOL_PROPERTIES));

    // Titanium
    public static Supplier<BitItem> TITANIUM_DRILL_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_drill_bit", () -> new BitItem(VEMultitoolBitData.TITANIUM_DRILL_BIT_DATA, "titanium_drill_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> TITANIUM_CHAIN_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_chain_bit", () -> new BitItem(VEMultitoolBitData.TITANIUM_CHAIN_BIT_DATA, "titanium_chain_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> TITANIUM_SCOOPER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_scooper_bit", () -> new BitItem(VEMultitoolBitData.TITANIUM_SCOOPER_BIT_DATA, "titanium_scooper_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> TITANIUM_TRIMMER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_trimmer_bit", () -> new BitItem(VEMultitoolBitData.TITANIUM_TRIMMER_DATA, "titanium_trimmer_bit", MULTITOOL_PROPERTIES));

    // Nighalite
    public static Supplier<BitItem> NIGHALITE_DRILL_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_drill_bit", () -> new BitItem(VEMultitoolBitData.NIGHALITE_DRILL_BIT_DATA, "nighalite_drill_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> NIGHALITE_CHAIN_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_chain_bit", () -> new BitItem(VEMultitoolBitData.NIGHALITE_CHAIN_BIT_DATA, "nighalite_chain_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> NIGHALITE_SCOOPER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_scooper_bit", () -> new BitItem(VEMultitoolBitData.NIGHALITE_SCOOPER_BIT_DATA, "nighalite_scooper_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> NIGHALITE_TRIMMER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_trimmer_bit", () -> new BitItem(VEMultitoolBitData.NIGHALITE_TRIMMER_DATA, "nighalite_trimmer_bit", MULTITOOL_PROPERTIES));

    // Eighzo
    public static Supplier<BitItem> EIGHZO_DRILL_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_drill_bit", () -> new BitItem(VEMultitoolBitData.EIGHZO_DRILL_BIT_DATA, "eighzo_drill_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> EIGHZO_CHAIN_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_chain_bit", () -> new BitItem(VEMultitoolBitData.EIGHZO_CHAIN_BIT_DATA, "eighzo_chain_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> EIGHZO_SCOOPER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_scooper_bit", () -> new BitItem(VEMultitoolBitData.EIGHZO_SCOOPER_BIT_DATA, "eighzo_scooper_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> EIGHZO_TRIMMER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_trimmer_bit", () -> new BitItem(VEMultitoolBitData.EIGHZO_TRIMMER_DATA, "eighzo_trimmer_bit", MULTITOOL_PROPERTIES));

    // Solarium
    public static Supplier<BitItem> SOLARIUM_DRILL_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_drill_bit", () -> new BitItem(VEMultitoolBitData.SOLARIUM_DRILL_BIT_DATA, "solarium_drill_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> SOLARIUM_CHAIN_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_chain_bit", () -> new BitItem(VEMultitoolBitData.SOLARIUM_CHAIN_BIT_DATA, "solarium_chain_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> SOLARIUM_SCOOPER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_scooper_bit", () -> new BitItem(VEMultitoolBitData.SOLARIUM_SCOOPER_BIT_DATA, "solarium_scooper_bit", MULTITOOL_PROPERTIES));
    public static Supplier<BitItem> SOLARIUM_TRIMMER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_trimmer_bit", () -> new BitItem(VEMultitoolBitData.SOLARIUM_TRIMMER_DATA, "solarium_trimmer_bit", MULTITOOL_PROPERTIES));
}
