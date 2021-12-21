package com.veteam.voluminousenergy.items.tools.multitool;

import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.items.tools.multitool.bits.VEMultitoolBits;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class VEMultitools {

    public static Item.Properties MULTITOOL_PROPERTIES = new Item.Properties().tab(VESetup.itemGroup).stacksTo(1);

    // Multitools
    public static Multitool EMPTY_MULTITOOL = new CombustionMultitool(null, "empty_multitool", MULTITOOL_PROPERTIES);

    // Iron
    public static Multitool IRON_DRILL_MULTITOOL = new CombustionMultitool(VEMultitoolBits.IRON_DRILL_BIT, "iron_drill_multitool", MULTITOOL_PROPERTIES);
    public static Multitool IRON_CHAIN_MULTITOOL = new CombustionMultitool(VEMultitoolBits.IRON_CHAIN_BIT, "iron_chain_multitool", MULTITOOL_PROPERTIES);
    public static Multitool IRON_SCOOPER_MULTITOOL = new CombustionMultitool(VEMultitoolBits.IRON_SCOOPER_BIT, "iron_scooper_multitool", MULTITOOL_PROPERTIES);
    public static Multitool IRON_TRIMMER_MULTITOOL = new CombustionMultitool(VEMultitoolBits.IRON_TRIMMER_BIT, "iron_trimmer_multitool", MULTITOOL_PROPERTIES);

    // Diamond
    public static Multitool DIAMOND_DRILL_MULTITOOL = new CombustionMultitool(VEMultitoolBits.DIAMOND_DRILL_BIT, "diamond_drill_multitool", MULTITOOL_PROPERTIES);
    public static Multitool DIAMOND_CHAIN_MULTITOOL = new CombustionMultitool(VEMultitoolBits.DIAMOND_CHAIN_BIT, "diamond_chain_multitool", MULTITOOL_PROPERTIES);
    public static Multitool DIAMOND_SCOOPER_MULTITOOL = new CombustionMultitool(VEMultitoolBits.DIAMOND_SCOOPER_BIT, "diamond_scooper_multitool", MULTITOOL_PROPERTIES);
    public static Multitool DIAMOND_TRIMMER_MULTITOOL = new CombustionMultitool(VEMultitoolBits.DIAMOND_TRIMMER_BIT, "diamond_trimmer_multitool", MULTITOOL_PROPERTIES);

    // Titanium
    public static Multitool TITANIUM_DRILL_MULTITOOL = new CombustionMultitool(VEMultitoolBits.TITANIUM_DRILL_BIT, "titanium_drill_multitool", MULTITOOL_PROPERTIES);
    public static Multitool TITANIUM_CHAIN_MULTITOOL = new CombustionMultitool(VEMultitoolBits.TITANIUM_CHAIN_BIT, "titanium_chain_multitool", MULTITOOL_PROPERTIES);
    public static Multitool TITANIUM_SCOOPER_MULTITOOL = new CombustionMultitool(VEMultitoolBits.TITANIUM_SCOOPER_BIT, "titanium_scooper_multitool", MULTITOOL_PROPERTIES);
    public static Multitool TITANIUM_TRIMMER_MULTITOOL = new CombustionMultitool(VEMultitoolBits.TITANIUM_TRIMMER_BIT, "titanium_trimmer_multitool", MULTITOOL_PROPERTIES);

    // Nighalite
    public static Multitool NIGHALITE_DRILL_MULTITOOL = new CombustionMultitool(VEMultitoolBits.NIGHALITE_DRILL_BIT, "nighalite_drill_multitool", MULTITOOL_PROPERTIES);
    public static Multitool NIGHALITE_CHAIN_MULTITOOL = new CombustionMultitool(VEMultitoolBits.NIGHALITE_CHAIN_BIT, "nighalite_chain_multitool", MULTITOOL_PROPERTIES);
    public static Multitool NIGHALITE_SCOOPER_MULTITOOL = new CombustionMultitool(VEMultitoolBits.NIGHALITE_SCOOPER_BIT, "nighalite_scooper_multitool", MULTITOOL_PROPERTIES);
    public static Multitool NIGHALITE_TRIMMER_MULTITOOL = new CombustionMultitool(VEMultitoolBits.NIGHALITE_TRIMMER_BIT, "nighalite_trimmer_multitool", MULTITOOL_PROPERTIES);

    // Eighzo
    public static Multitool EIGHZO_DRILL_MULTITOOL = new CombustionMultitool(VEMultitoolBits.EIGHZO_DRILL_BIT, "eighzo_drill_multitool", MULTITOOL_PROPERTIES);
    public static Multitool EIGHZO_CHAIN_MULTITOOL = new CombustionMultitool(VEMultitoolBits.EIGHZO_CHAIN_BIT, "eighzo_chain_multitool", MULTITOOL_PROPERTIES);
    public static Multitool EIGHZO_SCOOPER_MULTITOOL = new CombustionMultitool(VEMultitoolBits.EIGHZO_SCOOPER_BIT, "eighzo_scooper_multitool", MULTITOOL_PROPERTIES);
    public static Multitool EIGHZO_TRIMMER_MULTITOOL = new CombustionMultitool(VEMultitoolBits.EIGHZO_TRIMMER_BIT, "eighzo_trimmer_multitool", MULTITOOL_PROPERTIES);

    // Solarium
    public static Multitool SOLARIUM_DRILL_MULTITOOL = new CombustionMultitool(VEMultitoolBits.SOLARIUM_DRILL_BIT, "solarium_drill_multitool", MULTITOOL_PROPERTIES);
    public static Multitool SOLARIUM_CHAIN_MULTITOOL = new CombustionMultitool(VEMultitoolBits.SOLARIUM_CHAIN_BIT, "solarium_chain_multitool", MULTITOOL_PROPERTIES);
    public static Multitool SOLARIUM_SCOOPER_MULTITOOL = new CombustionMultitool(VEMultitoolBits.SOLARIUM_SCOOPER_BIT, "solarium_scooper_multitool", MULTITOOL_PROPERTIES);
    public static Multitool SOLARIUM_TRIMMER_MULTITOOL = new CombustionMultitool(VEMultitoolBits.SOLARIUM_TRIMMER_BIT, "solarium_trimmer_multitool", MULTITOOL_PROPERTIES);

    // BITS

    // Iron
    public static BitItem IRON_DRILL_BIT = new BitItem(VEMultitoolBits.IRON_DRILL_BIT, "iron_drill_bit", MULTITOOL_PROPERTIES);
    public static BitItem IRON_CHAIN_BIT = new BitItem(VEMultitoolBits.IRON_CHAIN_BIT,"iron_chain_bit", MULTITOOL_PROPERTIES);
    public static BitItem IRON_SCOOPER_BIT = new BitItem(VEMultitoolBits.IRON_SCOOPER_BIT, "iron_scooper_bit",MULTITOOL_PROPERTIES);
    public static BitItem IRON_TRIMMER_BIT = new BitItem(VEMultitoolBits.IRON_TRIMMER_BIT, "iron_trimmer_bit", MULTITOOL_PROPERTIES);

    // Diamond
    public static BitItem DIAMOND_DRILL_BIT = new BitItem(VEMultitoolBits.DIAMOND_DRILL_BIT, "diamond_drill_bit", MULTITOOL_PROPERTIES);
    public static BitItem DIAMOND_CHAIN_BIT = new BitItem(VEMultitoolBits.DIAMOND_CHAIN_BIT,"diamond_chain_bit", MULTITOOL_PROPERTIES);
    public static BitItem DIAMOND_SCOOPER_BIT = new BitItem(VEMultitoolBits.DIAMOND_SCOOPER_BIT, "diamond_scooper_bit",MULTITOOL_PROPERTIES);
    public static BitItem DIAMOND_TRIMMER_BIT = new BitItem(VEMultitoolBits.DIAMOND_TRIMMER_BIT, "diamond_trimmer_bit", MULTITOOL_PROPERTIES);

    // Titanium
    public static BitItem TITANIUM_DRILL_BIT = new BitItem(VEMultitoolBits.TITANIUM_DRILL_BIT, "titanium_drill_bit", MULTITOOL_PROPERTIES);
    public static BitItem TITANIUM_CHAIN_BIT = new BitItem(VEMultitoolBits.TITANIUM_CHAIN_BIT,"titanium_chain_bit", MULTITOOL_PROPERTIES);
    public static BitItem TITANIUM_SCOOPER_BIT = new BitItem(VEMultitoolBits.TITANIUM_SCOOPER_BIT, "titanium_scooper_bit",MULTITOOL_PROPERTIES);
    public static BitItem TITANIUM_TRIMMER_BIT = new BitItem(VEMultitoolBits.TITANIUM_TRIMMER_BIT, "titanium_trimmer_bit", MULTITOOL_PROPERTIES);

    // Nighalite
    public static BitItem NIGHALITE_DRILL_BIT = new BitItem(VEMultitoolBits.NIGHALITE_DRILL_BIT, "nighalite_drill_bit", MULTITOOL_PROPERTIES);
    public static BitItem NIGHALITE_CHAIN_BIT = new BitItem(VEMultitoolBits.NIGHALITE_CHAIN_BIT,"nighalite_chain_bit", MULTITOOL_PROPERTIES);
    public static BitItem NIGHALITE_SCOOPER_BIT = new BitItem(VEMultitoolBits.NIGHALITE_SCOOPER_BIT, "nighalite_scooper_bit",MULTITOOL_PROPERTIES);
    public static BitItem NIGHALITE_TRIMMER_BIT = new BitItem(VEMultitoolBits.NIGHALITE_TRIMMER_BIT, "nighalite_trimmer_bit", MULTITOOL_PROPERTIES);

    // Eighzo
    public static BitItem EIGHZO_DRILL_BIT = new BitItem(VEMultitoolBits.EIGHZO_DRILL_BIT, "eighzo_drill_bit", MULTITOOL_PROPERTIES);
    public static BitItem EIGHZO_CHAIN_BIT = new BitItem(VEMultitoolBits.EIGHZO_CHAIN_BIT,"eighzo_chain_bit", MULTITOOL_PROPERTIES);
    public static BitItem EIGHZO_SCOOPER_BIT = new BitItem(VEMultitoolBits.EIGHZO_SCOOPER_BIT, "eighzo_scooper_bit",MULTITOOL_PROPERTIES);
    public static BitItem EIGHZO_TRIMMER_BIT = new BitItem(VEMultitoolBits.EIGHZO_TRIMMER_BIT, "eighzo_trimmer_bit", MULTITOOL_PROPERTIES);

    // Solarium
    public static BitItem SOLARIUM_DRILL_BIT = new BitItem(VEMultitoolBits.SOLARIUM_DRILL_BIT, "solarium_drill_bit", MULTITOOL_PROPERTIES);
    public static BitItem SOLARIUM_CHAIN_BIT = new BitItem(VEMultitoolBits.SOLARIUM_CHAIN_BIT,"solarium_chain_bit", MULTITOOL_PROPERTIES);
    public static BitItem SOLARIUM_SCOOPER_BIT = new BitItem(VEMultitoolBits.SOLARIUM_SCOOPER_BIT, "solarium_scooper_bit",MULTITOOL_PROPERTIES);
    public static BitItem SOLARIUM_TRIMMER_BIT = new BitItem(VEMultitoolBits.SOLARIUM_TRIMMER_BIT, "solarium_trimmer_bit", MULTITOOL_PROPERTIES);
}
