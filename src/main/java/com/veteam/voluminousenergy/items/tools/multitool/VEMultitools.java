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
    public static Multitool DIAMOND_CHAIN_MULTITOOL = new CombustionMultitool(VEMultitoolBits.IRON_CHAIN_BIT, "diamond_chain_multitool", MULTITOOL_PROPERTIES);
    public static Multitool DIAMOND_SCOOPER_MULTITOOL = new CombustionMultitool(VEMultitoolBits.IRON_SCOOPER_BIT, "diamond_scooper_multitool", MULTITOOL_PROPERTIES);
    public static Multitool DIAMOND_TRIMMER_MULTITOOL = new CombustionMultitool(VEMultitoolBits.IRON_TRIMMER_BIT, "diamond_trimmer_multitool", MULTITOOL_PROPERTIES);

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
}
