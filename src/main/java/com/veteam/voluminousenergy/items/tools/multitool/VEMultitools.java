package com.veteam.voluminousenergy.items.tools.multitool;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.items.tools.multitool.bits.VEMultitoolBits;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VEMultitools {
    public static final DeferredRegister<Item> VE_MULTITOOL_ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, VoluminousEnergy.MODID);
    public static Item.Properties MULTITOOL_PROPERTIES = new Item.Properties().tab(VESetup.itemGroup).stacksTo(1);

    // Multitools
    public static RegistryObject<CombustionMultitool> EMPTY_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("empty_multitool", () -> new CombustionMultitool(null, "empty_multitool", MULTITOOL_PROPERTIES));

    // Iron
    public static RegistryObject<CombustionMultitool> IRON_DRILL_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("iron_drill_multitool", () -> new CombustionMultitool(VEMultitoolBits.IRON_DRILL_BIT, "iron_drill_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> IRON_CHAIN_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("iron_chain_multitool", () -> new CombustionMultitool(VEMultitoolBits.IRON_CHAIN_BIT, "iron_chain_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> IRON_SCOOPER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("iron_scooper_multitool", () -> new CombustionMultitool(VEMultitoolBits.IRON_SCOOPER_BIT, "iron_scooper_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> IRON_TRIMMER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("iron_trimmer_multitool", () -> new CombustionMultitool(VEMultitoolBits.IRON_TRIMMER_BIT, "iron_trimmer_multitool", MULTITOOL_PROPERTIES));

    // Diamond
    public static RegistryObject<CombustionMultitool> DIAMOND_DRILL_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_drill_multitool", () -> new CombustionMultitool(VEMultitoolBits.DIAMOND_DRILL_BIT, "diamond_drill_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> DIAMOND_CHAIN_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_chain_multitool", () -> new CombustionMultitool(VEMultitoolBits.DIAMOND_CHAIN_BIT, "diamond_chain_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> DIAMOND_SCOOPER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_scooper_multitool", () -> new CombustionMultitool(VEMultitoolBits.DIAMOND_SCOOPER_BIT, "diamond_scooper_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> DIAMOND_TRIMMER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_trimmer_multitool", () -> new CombustionMultitool(VEMultitoolBits.DIAMOND_TRIMMER_BIT, "diamond_trimmer_multitool", MULTITOOL_PROPERTIES));

    // Titanium
    public static RegistryObject<CombustionMultitool> TITANIUM_DRILL_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_drill_multitool", () -> new CombustionMultitool(VEMultitoolBits.TITANIUM_DRILL_BIT, "titanium_drill_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> TITANIUM_CHAIN_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_chain_multitool", () -> new CombustionMultitool(VEMultitoolBits.TITANIUM_CHAIN_BIT, "titanium_chain_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> TITANIUM_SCOOPER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_scooper_multitool", () -> new CombustionMultitool(VEMultitoolBits.TITANIUM_SCOOPER_BIT, "titanium_scooper_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> TITANIUM_TRIMMER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_trimmer_multitool", () -> new CombustionMultitool(VEMultitoolBits.TITANIUM_TRIMMER_BIT, "titanium_trimmer_multitool", MULTITOOL_PROPERTIES));

    // Nighalite
    public static RegistryObject<CombustionMultitool> NIGHALITE_DRILL_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_drill_multitool", () -> new CombustionMultitool(VEMultitoolBits.NIGHALITE_DRILL_BIT, "nighalite_drill_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> NIGHALITE_CHAIN_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_chain_multitool", () -> new CombustionMultitool(VEMultitoolBits.NIGHALITE_CHAIN_BIT, "nighalite_chain_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> NIGHALITE_SCOOPER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_scooper_multitool", () -> new CombustionMultitool(VEMultitoolBits.NIGHALITE_SCOOPER_BIT, "nighalite_scooper_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> NIGHALITE_TRIMMER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_trimmer_multitool", () -> new CombustionMultitool(VEMultitoolBits.NIGHALITE_TRIMMER_BIT, "nighalite_trimmer_multitool", MULTITOOL_PROPERTIES));

    // Eighzo
    public static RegistryObject<CombustionMultitool> EIGHZO_DRILL_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_drill_multitool", () -> new CombustionMultitool(VEMultitoolBits.EIGHZO_DRILL_BIT, "eighzo_drill_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> EIGHZO_CHAIN_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_chain_multitool", () -> new CombustionMultitool(VEMultitoolBits.EIGHZO_CHAIN_BIT, "eighzo_chain_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> EIGHZO_SCOOPER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_scooper_multitool", () -> new CombustionMultitool(VEMultitoolBits.EIGHZO_SCOOPER_BIT, "eighzo_scooper_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> EIGHZO_TRIMMER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_trimmer_multitool", () -> new CombustionMultitool(VEMultitoolBits.EIGHZO_TRIMMER_BIT, "eighzo_trimmer_multitool", MULTITOOL_PROPERTIES));

    // Solarium
    public static RegistryObject<CombustionMultitool> SOLARIUM_DRILL_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_drill_multitool", () -> new CombustionMultitool(VEMultitoolBits.SOLARIUM_DRILL_BIT, "solarium_drill_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> SOLARIUM_CHAIN_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_chain_multitool", () -> new CombustionMultitool(VEMultitoolBits.SOLARIUM_CHAIN_BIT, "solarium_chain_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> SOLARIUM_SCOOPER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_scooper_multitool", () -> new CombustionMultitool(VEMultitoolBits.SOLARIUM_SCOOPER_BIT, "solarium_scooper_multitool", MULTITOOL_PROPERTIES));
    public static RegistryObject<CombustionMultitool> SOLARIUM_TRIMMER_MULTITOOL = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_trimmer_multitool", () -> new CombustionMultitool(VEMultitoolBits.SOLARIUM_TRIMMER_BIT, "solarium_trimmer_multitool", MULTITOOL_PROPERTIES));

    // BITS

    // Iron
    public static RegistryObject<BitItem> IRON_DRILL_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("iron_drill_bit", () -> new BitItem(VEMultitoolBits.IRON_DRILL_BIT, "iron_drill_bit", MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> IRON_CHAIN_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("iron_chain_bit", () -> new BitItem(VEMultitoolBits.IRON_CHAIN_BIT,"iron_chain_bit", MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> IRON_SCOOPER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("iron_scooper_bit", () -> new BitItem(VEMultitoolBits.IRON_SCOOPER_BIT, "iron_scooper_bit",MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> IRON_TRIMMER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("iron_trimmer_bit", () -> new BitItem(VEMultitoolBits.IRON_TRIMMER_BIT, "iron_trimmer_bit", MULTITOOL_PROPERTIES));

    // Diamond
    public static RegistryObject<BitItem> DIAMOND_DRILL_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_drill_bit", () -> new BitItem(VEMultitoolBits.DIAMOND_DRILL_BIT, "diamond_drill_bit", MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> DIAMOND_CHAIN_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_chain_bit", () -> new BitItem(VEMultitoolBits.DIAMOND_CHAIN_BIT,"diamond_chain_bit", MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> DIAMOND_SCOOPER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_scooper_bit", () -> new BitItem(VEMultitoolBits.DIAMOND_SCOOPER_BIT, "diamond_scooper_bit",MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> DIAMOND_TRIMMER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("diamond_trimmer_bit", () -> new BitItem(VEMultitoolBits.DIAMOND_TRIMMER_BIT, "diamond_trimmer_bit", MULTITOOL_PROPERTIES));

    // Titanium
    public static RegistryObject<BitItem> TITANIUM_DRILL_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_drill_bit", () -> new BitItem(VEMultitoolBits.TITANIUM_DRILL_BIT, "titanium_drill_bit", MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> TITANIUM_CHAIN_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_chain_bit", () -> new BitItem(VEMultitoolBits.TITANIUM_CHAIN_BIT,"titanium_chain_bit", MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> TITANIUM_SCOOPER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_scooper_bit", () -> new BitItem(VEMultitoolBits.TITANIUM_SCOOPER_BIT, "titanium_scooper_bit",MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> TITANIUM_TRIMMER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("titanium_trimmer_bit", () -> new BitItem(VEMultitoolBits.TITANIUM_TRIMMER_BIT, "titanium_trimmer_bit", MULTITOOL_PROPERTIES));

    // Nighalite
    public static RegistryObject<BitItem> NIGHALITE_DRILL_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_drill_bit", () -> new BitItem(VEMultitoolBits.NIGHALITE_DRILL_BIT, "nighalite_drill_bit", MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> NIGHALITE_CHAIN_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_chain_bit", () -> new BitItem(VEMultitoolBits.NIGHALITE_CHAIN_BIT,"nighalite_chain_bit", MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> NIGHALITE_SCOOPER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_scooper_bit", () -> new BitItem(VEMultitoolBits.NIGHALITE_SCOOPER_BIT, "nighalite_scooper_bit",MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> NIGHALITE_TRIMMER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("nighalite_trimmer_bit", () -> new BitItem(VEMultitoolBits.NIGHALITE_TRIMMER_BIT, "nighalite_trimmer_bit", MULTITOOL_PROPERTIES));

    // Eighzo
    public static RegistryObject<BitItem> EIGHZO_DRILL_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_drill_bit", () -> new BitItem(VEMultitoolBits.EIGHZO_DRILL_BIT, "eighzo_drill_bit", MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> EIGHZO_CHAIN_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_chain_bit", () -> new BitItem(VEMultitoolBits.EIGHZO_CHAIN_BIT,"eighzo_chain_bit", MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> EIGHZO_SCOOPER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_scooper_bit", () -> new BitItem(VEMultitoolBits.EIGHZO_SCOOPER_BIT, "eighzo_scooper_bit",MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> EIGHZO_TRIMMER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("eighzo_trimmer_bit", () -> new BitItem(VEMultitoolBits.EIGHZO_TRIMMER_BIT, "eighzo_trimmer_bit", MULTITOOL_PROPERTIES));

    // Solarium
    public static RegistryObject<BitItem> SOLARIUM_DRILL_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_drill_bit", () -> new BitItem(VEMultitoolBits.SOLARIUM_DRILL_BIT, "solarium_drill_bit", MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> SOLARIUM_CHAIN_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_chain_bit", () -> new BitItem(VEMultitoolBits.SOLARIUM_CHAIN_BIT,"solarium_chain_bit", MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> SOLARIUM_SCOOPER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_scooper_bit", () -> new BitItem(VEMultitoolBits.SOLARIUM_SCOOPER_BIT, "solarium_scooper_bit",MULTITOOL_PROPERTIES));
    public static RegistryObject<BitItem> SOLARIUM_TRIMMER_BIT = VE_MULTITOOL_ITEM_REGISTRY.register("solarium_trimmer_bit", () -> new BitItem(VEMultitoolBits.SOLARIUM_TRIMMER_BIT, "solarium_trimmer_bit", MULTITOOL_PROPERTIES));
}
