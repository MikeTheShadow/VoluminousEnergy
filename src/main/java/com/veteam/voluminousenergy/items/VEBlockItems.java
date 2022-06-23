package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VEBlockItems {
            public static final DeferredRegister<Item> VE_BLOCK_ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, VoluminousEnergy.MODID);
            protected static final Item.Properties PROPERTIES = new Item.Properties().tab(VESetup.itemGroup);
    
            //Block Items
            //Tile Entities
            public static RegistryObject<BlockItem> PRIMITIVE_BLAST_FURNACE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("primitiveblastfurnace", () -> new BlockItem(VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> PRIMITIVE_STIRLING_GENERATOR_ITEM = VE_BLOCK_ITEM_REGISTRY.register("primitivestirlinggenerator", () -> new BlockItem(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> CRUSHER_ITEM = VE_BLOCK_ITEM_REGISTRY.register("crusher",() -> new BlockItem(VEBlocks.CRUSHER_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> ELECTROLYZER_ITEM = VE_BLOCK_ITEM_REGISTRY.register("electrolyzer", () -> new BlockItem(VEBlocks.ELECTROLYZER_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> CENTRIFUGAL_AGITATOR_ITEM = VE_BLOCK_ITEM_REGISTRY.register("centrifugal_agitator",() -> new BlockItem(VEBlocks.CENTRIFUGAL_AGITATOR_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> COMPRESSOR_ITEM = VE_BLOCK_ITEM_REGISTRY.register("compressor",() -> new BlockItem(VEBlocks.COMPRESSOR_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> STIRLING_GENERATOR_ITEM = VE_BLOCK_ITEM_REGISTRY.register("stirling_generator",() -> new BlockItem(VEBlocks.STIRLING_GENERATOR_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> COMBUSTION_GENERATOR_ITEM = VE_BLOCK_ITEM_REGISTRY.register("combustion_generator",() -> new BlockItem(VEBlocks.COMBUSTION_GENERATOR_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> AQUEOULIZER_ITEM = VE_BLOCK_ITEM_REGISTRY.register("aqueoulizer",() -> new BlockItem(VEBlocks.AQUEOULIZER_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> AIR_COMPRESSOR_ITEM = VE_BLOCK_ITEM_REGISTRY.register("air_compressor",() -> new BlockItem(VEBlocks.AIR_COMPRESSOR_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> DISTILLATION_UNIT_ITEM = VE_BLOCK_ITEM_REGISTRY.register("distillation_unit",() -> new BlockItem(VEBlocks.DISTILLATION_UNIT_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> PUMP_ITEM = VE_BLOCK_ITEM_REGISTRY.register("pump",() -> new BlockItem(VEBlocks.PUMP_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> GAS_FIRED_FURNACE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("gas_fired_furnace",() -> new BlockItem(VEBlocks.GAS_FIRED_FURNACE_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> ELECTRIC_FURNACE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("electric_furnace",() -> new BlockItem(VEBlocks.ELECTRIC_FURNACE_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> BATTERY_BOX_ITEM = VE_BLOCK_ITEM_REGISTRY.register("battery_box",() -> new BlockItem(VEBlocks.BATTERY_BOX_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> PRIMITIVE_SOLAR_PANEL_ITEM = VE_BLOCK_ITEM_REGISTRY.register("primitive_solar_panel",() -> new BlockItem(VEBlocks.PRIMITIVE_SOLAR_PANEL_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> SOLAR_PANEL_ITEM = VE_BLOCK_ITEM_REGISTRY.register("solar_panel",() -> new BlockItem(VEBlocks.SOLAR_PANEL_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> CENTRIFUGAL_SEPARATOR_ITEM = VE_BLOCK_ITEM_REGISTRY.register("centrifugal_separator",() -> new BlockItem(VEBlocks.CENTRIFUGAL_SEPARATOR_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> IMPLOSION_COMPRESSOR_ITEM = VE_BLOCK_ITEM_REGISTRY.register("implosion_compressor",() -> new BlockItem(VEBlocks.IMPLOSION_COMPRESSOR_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> BLAST_FURNACE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("blast_furnace",() -> new BlockItem(VEBlocks.BLAST_FURNACE_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> TOOLING_STATION_ITEM = VE_BLOCK_ITEM_REGISTRY.register("tooling_station",() -> new BlockItem(VEBlocks.TOOLING_STATION_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> SAWMILL_ITEM = VE_BLOCK_ITEM_REGISTRY.register("sawmill",() -> new BlockItem(VEBlocks.SAWMILL_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> DIMENSIONAL_LASER_ITEM = VE_BLOCK_ITEM_REGISTRY.register("dimensional_laser", () -> new BlockItem(VEBlocks.DIMENSIONAL_LASER_BLOCK.get(), PROPERTIES));

            // Tanks
            public static RegistryObject<BlockItem> ALUMINUM_TANK_ITEM = VE_BLOCK_ITEM_REGISTRY.register("aluminum_tank",() -> new BlockItem(VEBlocks.ALUMINUM_TANK_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> TITANIUM_TANK_ITEM = VE_BLOCK_ITEM_REGISTRY.register("titanium_tank",() -> new BlockItem(VEBlocks.TITANIUM_TANK_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> NETHERITE_TANK_ITEM = VE_BLOCK_ITEM_REGISTRY.register("netherite_tank",() -> new BlockItem(VEBlocks.NETHERITE_TANK_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> NIGHALITE_TANK_ITEM = VE_BLOCK_ITEM_REGISTRY.register("nighalite_tank",() -> new BlockItem(VEBlocks.NIGHALITE_TANK_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> EIGHZO_TANK_ITEM = VE_BLOCK_ITEM_REGISTRY.register("eighzo_tank",() -> new BlockItem(VEBlocks.EIGHZO_TANK_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> SOLARIUM_TANK_ITEM = VE_BLOCK_ITEM_REGISTRY.register("solarium_tank",() -> new BlockItem(VEBlocks.SOLARIUM_TANK_BLOCK.get(), PROPERTIES));

            // Crops
            //public static RegistryObject<BlockItem> VE_BLOCK_ITEM_REGISTRY.register(() -> new BlockItem(VEBlocks.LAND_CROP,properties).setRegistryName("land_crop"));
            //@Deprecated // I think the rice registration here is deprecated, as it's now in VEItems
            //public static RegistryObject<BlockItem> RICE_CROP_ITEM = VE_BLOCK_ITEM_REGISTRY.register("rice_grain", VEItems.RICE_GRAIN.get()); 

            //True Blocks
            //Ores
            public static RegistryObject<BlockItem> SALTPETER_ORE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("saltpeterore",() -> new BlockItem(VEBlocks.SALTPETER_ORE.get(), PROPERTIES));
            public static RegistryObject<BlockItem> BAUXITE_ORE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("bauxiteore",() -> new BlockItem(VEBlocks.BAUXITE_ORE.get(), PROPERTIES));
            public static RegistryObject<BlockItem> CINNABAR_ORE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("cinnabarore",() -> new BlockItem(VEBlocks.CINNABAR_ORE.get(), PROPERTIES));
            public static RegistryObject<BlockItem> RUTILE_ORE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("rutileore",() -> new BlockItem(VEBlocks.RUTILE_ORE.get(), PROPERTIES));
            public static RegistryObject<BlockItem> GALENA_ORE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("galena_ore",() -> new BlockItem(VEBlocks.GALENA_ORE.get(), PROPERTIES));
            public static RegistryObject<BlockItem> EIGHZO_ORE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("eighzo_ore",() -> new BlockItem(VEBlocks.EIGHZO_ORE.get(), PROPERTIES));

            //Deepslate ores
            public static RegistryObject<BlockItem> DEEPSLATE_BAUXITE_ORE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("deepslate_bauxite_ore",() -> new BlockItem(VEBlocks.DEEPSLATE_BAUXITE_ORE.get(), PROPERTIES));
            public static RegistryObject<BlockItem> DEEPSLATE_CINNABAR_ORE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("deepslate_cinnabar_ore",() -> new BlockItem(VEBlocks.DEEPSLATE_CINNABAR_ORE.get(), PROPERTIES));
            public static RegistryObject<BlockItem> DEEPSLATE_RUTILE_ORE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("deepslate_rutile_ore",() -> new BlockItem(VEBlocks.DEEPSLATE_RUTILE_ORE.get(), PROPERTIES));
            public static RegistryObject<BlockItem> DEEPSLATE_GALENA_ORE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("deepslate_galena_ore",() -> new BlockItem(VEBlocks.DEEPSLATE_GALENA_ORE.get(), PROPERTIES));

            public static RegistryObject<BlockItem> RED_SALTPETER_ORE_ITEM = VE_BLOCK_ITEM_REGISTRY.register("red_saltpeter_ore",() -> new BlockItem(VEBlocks.RED_SALTPETER_ORE.get(), PROPERTIES));

            //Shells
            public static RegistryObject<BlockItem> ALUMINUM_SHELL_ITEM = VE_BLOCK_ITEM_REGISTRY.register("aluminum_shell",() -> new BlockItem(VEBlocks.ALUMINUM_SHELL.get(), PROPERTIES));
            public static RegistryObject<BlockItem> CARBON_SHIELDED_ALUMINUM_MACHINE_FRAME_ITEM = VE_BLOCK_ITEM_REGISTRY.register("carbon_shielded_aluminum_machine_frame",() -> new BlockItem(VEBlocks.CARBON_SHIELDED_ALUMINUM_MACHINE_FRAME.get(), PROPERTIES));
            public static RegistryObject<BlockItem> ALUMINUM_MACHINE_CASING_ITEM = VE_BLOCK_ITEM_REGISTRY.register("aluminum_machine_casing",() -> new BlockItem(VEBlocks.ALUMINUM_MACHINE_CASING_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> TITANIUM_MACHINE_CASING_ITEM = VE_BLOCK_ITEM_REGISTRY.register("titanium_machine_casing",() -> new BlockItem(VEBlocks.TITANIUM_MACHINE_CASING_BLOCK.get(), PROPERTIES));

            // Raw Material Storage Blocks
            public static RegistryObject<BlockItem> RAW_BAUXITE_BLOCKITEM = VE_BLOCK_ITEM_REGISTRY.register("raw_bauxite_block",() -> new BlockItem(VEBlocks.RAW_BAUXITE_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> RAW_CINNABAR_BLOCKITEM = VE_BLOCK_ITEM_REGISTRY.register("raw_cinnabar_block",() -> new BlockItem(VEBlocks.RAW_CINNABAR_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> RAW_EIGHZO_BLOCKITEM = VE_BLOCK_ITEM_REGISTRY.register("raw_eighzo_block",() -> new BlockItem(VEBlocks.RAW_EIGHZO_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> RAW_GALENA_BLOCKITEM = VE_BLOCK_ITEM_REGISTRY.register("raw_galena_block",() -> new BlockItem(VEBlocks.RAW_GALENA_BLOCK.get(), PROPERTIES));
            public static RegistryObject<BlockItem> RAW_RUTILE_BLOCKITEM = VE_BLOCK_ITEM_REGISTRY.register("raw_rutile_block",() -> new BlockItem(VEBlocks.RAW_RUTILE_BLOCK.get(), PROPERTIES));
            //public static RegistryObject<BlockItem> VE_BLOCK_ITEM_REGISTRY.register(() -> new BlockItem(VEBlocks.RAW_BONE_BLOCK.get(), properties).setRegistryName("raw_bone_block"));

            public static RegistryObject<BlockItem> PRESSURE_LADDER_ITEM = VE_BLOCK_ITEM_REGISTRY.register("pressure_ladder",() -> new BlockItem(VEBlocks.PRESSURE_LADDER.get(), PROPERTIES));

            // Material Storage Blocks
            public static RegistryObject<BlockItem> SOLARIUM_BLOCKITEM = VE_BLOCK_ITEM_REGISTRY.register("solarium_block",() -> new BlockItem(VEBlocks.SOLARIUM_BLOCK.get(), PROPERTIES));
            /*public static RegistryObject<BlockItem> VE_BLOCK_ITEM_REGISTRY.register(() -> new BlockItem(VEBlocks.ALUMINUM_BLOCK.get(),properties).setRegistryName("aluminum_block"));
            public static RegistryObject<BlockItem> VE_BLOCK_ITEM_REGISTRY.register(() -> new BlockItem(VEBlocks.CARBON_BLOCK.get(),properties).setRegistryName("carbon_block"));
            public static RegistryObject<BlockItem> VE_BLOCK_ITEM_REGISTRY.register(() -> new BlockItem(VEBlocks.EIGHZO_BLOCK.get(),properties).setRegistryName("eighzo_block"));
            public static RegistryObject<BlockItem> VE_BLOCK_ITEM_REGISTRY.register(() -> new BlockItem(VEBlocks.NIGHALITE_BLOCK.get(),properties).setRegistryName("nighalite_block"));
            public static RegistryObject<BlockItem> VE_BLOCK_ITEM_REGISTRY.register(() -> new BlockItem(VEBlocks.SALTPETER_BLOCK.get(),properties).setRegistryName("saltpeter_block"));
            public static RegistryObject<BlockItem> VE_BLOCK_ITEM_REGISTRY.register(() -> new BlockItem(VEBlocks.TITANIUM_BLOCK.get(),properties).setRegistryName("titanium_block"));
            public static RegistryObject<BlockItem> VE_BLOCK_ITEM_REGISTRY.register(() -> new BlockItem(VEBlocks.TUNGSTEN_BLOCK.get(),properties).setRegistryName("tungsten_block"));
            public static RegistryObject<BlockItem> VE_BLOCK_ITEM_REGISTRY.register(() -> new BlockItem(VEBlocks.TUNGSTEN_STEEL_BLOCK.get(),properties).setRegistryName("tungsten_steel_block"));
            */
}
