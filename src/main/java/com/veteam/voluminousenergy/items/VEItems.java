package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.foods.VEFoods;
import com.veteam.voluminousenergy.items.batteries.LeadAcidBattery;
import com.veteam.voluminousenergy.items.batteries.LeadAcidBatteryPack;
import com.veteam.voluminousenergy.items.batteries.MercuryBattery;
import com.veteam.voluminousenergy.items.batteries.MercuryBatteryPack;
import com.veteam.voluminousenergy.items.crops.RiceItem;
import com.veteam.voluminousenergy.items.dusts.*;
import com.veteam.voluminousenergy.items.gears.*;
import com.veteam.voluminousenergy.items.ingots.*;
import com.veteam.voluminousenergy.items.microchips.GoldMicrochip;
import com.veteam.voluminousenergy.items.microchips.SilverMicrochip;
import com.veteam.voluminousenergy.items.ores.*;
import com.veteam.voluminousenergy.items.plates.AluminumPlate;
import com.veteam.voluminousenergy.items.plates.CarbonPlate;
import com.veteam.voluminousenergy.items.plates.SolariumPlate;
import com.veteam.voluminousenergy.items.plates.TitaniumPlate;
import com.veteam.voluminousenergy.items.solid_fuels.AnimalFat;
import com.veteam.voluminousenergy.items.solid_fuels.CoalCoke;
import com.veteam.voluminousenergy.items.solid_fuels.Petcoke;
import com.veteam.voluminousenergy.items.solid_fuels.Rosin;
import com.veteam.voluminousenergy.items.solid_fuels.tiny.*;
import com.veteam.voluminousenergy.items.tank_frames.ImpeccableTankFrame;
import com.veteam.voluminousenergy.items.tank_frames.RobustTankFrame;
import com.veteam.voluminousenergy.items.tank_frames.StandardTankFrame;
import com.veteam.voluminousenergy.items.tools.CreativeFluidScanner;
import com.veteam.voluminousenergy.items.tools.FluidScanner;
import com.veteam.voluminousenergy.items.tools.RFIDChip;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.items.upgrades.MysteriousMultiplier;
import com.veteam.voluminousenergy.items.upgrades.QuartzMultiplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class VEItems {
    public static final DeferredRegister<Item> VE_ITEM_REGISTRY = DeferredRegister.create(Registries.ITEM, VoluminousEnergy.MODID);

    // Misc
    public static DeferredHolder<Item, Petcoke> PETCOKE = VE_ITEM_REGISTRY.register("petcoke", Petcoke::new);
    public static Supplier<CoalCoke> COALCOKE = VE_ITEM_REGISTRY.register("coalcoke", CoalCoke::new);
    public static Supplier<SaltpeterChunk> SALTPETERCHUNK = VE_ITEM_REGISTRY.register("saltpeterchunk", SaltpeterChunk::new);
    public static Supplier<Silicon> SILICON = VE_ITEM_REGISTRY.register("silicon", Silicon::new);
    public static Supplier<ShreddedBiomass> SHREDDED_BIOMASS = VE_ITEM_REGISTRY.register("shredded_biomass", ShreddedBiomass::new);
    public static Supplier<Item> ROSIN = VE_ITEM_REGISTRY.register("rosin", Rosin::new);
    public static Supplier<AnimalFat> ANIMAL_FAT = VE_ITEM_REGISTRY.register("animal_fat", AnimalFat::new);
    public static Supplier<Dough> DOUGH = VE_ITEM_REGISTRY.register("dough", Dough::new);

    // Raw Ore
    public static Supplier<RawBauxite> RAW_BAUXITE = VE_ITEM_REGISTRY.register("raw_bauxite", RawBauxite::new);
    public static Supplier<RawCinnabar> RAW_CINNABAR = VE_ITEM_REGISTRY.register("raw_cinnabar", RawCinnabar::new);
    public static Supplier<RawGalena> RAW_GALENA = VE_ITEM_REGISTRY.register("raw_galena", RawGalena::new);
    public static Supplier<RawRutile> RAW_RUTILE = VE_ITEM_REGISTRY.register("raw_rutile", RawRutile::new);
    public static Supplier<RawSulfur> RAW_SULFUR = VE_ITEM_REGISTRY.register("raw_sulfur", RawSulfur::new);
    public static Supplier<RawEighzo> RAW_EIGHZO = VE_ITEM_REGISTRY.register("raw_eighzo", RawEighzo::new);

    //Dusts
    public static Supplier<CoalDust> COALDUST = VE_ITEM_REGISTRY.register("coaldust", CoalDust::new);
    public static Supplier<CokeDust> COKEDUST = VE_ITEM_REGISTRY.register("cokedust", CokeDust::new);
    public static Supplier<LapisDust> LAPISDUST = VE_ITEM_REGISTRY.register("lapisdust", LapisDust::new);
    public static Supplier<SulfurDust> SULFURDUST = VE_ITEM_REGISTRY.register("sulfurdust", SulfurDust::new);
    public static Supplier<Item> CARBONDUST = VE_ITEM_REGISTRY.register("carbondust", CarbonDust::new);
    public static Supplier<Item> SALTPETERDUST = VE_ITEM_REGISTRY.register("saltpeterdust", SaltpeterDust::new);
    public static Supplier<Item> ALUMINUM_DUST = VE_ITEM_REGISTRY.register("aluminum_dust", AluminumDust::new);
    public static Supplier<Item> BAUXITE_DUST = VE_ITEM_REGISTRY.register("bauxite_dust", BauxiteDust::new);
    public static Supplier<Item> CINNABAR_DUST = VE_ITEM_REGISTRY.register("cinnabar_dust", CinnabarDust::new);
    public static Supplier<Item> IRON_DUST = VE_ITEM_REGISTRY.register("iron_dust", IronDust::new);
    public static Supplier<Item> QUARTZ_DUST = VE_ITEM_REGISTRY.register("quartz_dust", QuartzDust::new);
    public static Supplier<Item> SAND_DUST = VE_ITEM_REGISTRY.register("sand_dust", SandDust::new);
    public static Supplier<Item> SOULSAND_DUST = VE_ITEM_REGISTRY.register("soulsand_dust", SoulsandDust::new);
    public static Supplier<Item> TITANIUM_DUST = VE_ITEM_REGISTRY.register("titanium_dust", TitaniumDust::new);
    public static Supplier<Item> RUTILE_DUST = VE_ITEM_REGISTRY.register("rutile_dust", RutileDust::new);
    public static Supplier<Item> GALENA_DUST = VE_ITEM_REGISTRY.register("galena_dust", GalenaDust::new);
    public static Supplier<Item> LEAD_DUST = VE_ITEM_REGISTRY.register("lead_dust", LeadDust::new);
    public static Supplier<Item> SILVER_DUST = VE_ITEM_REGISTRY.register("silver_dust", SilverDust::new);
    public static Supplier<Item> GOLD_DUST = VE_ITEM_REGISTRY.register("gold_dust", GoldDust::new);
    public static Supplier<Item> PHOTOVOLTAIC_DUST = VE_ITEM_REGISTRY.register("photovoltaic_dust", PhotovoltaicDust::new);
    public static Supplier<Item> END_STONE_DUST = VE_ITEM_REGISTRY.register("end_stone_dust", EndStoneDust::new);
    public static Supplier<Item> BASALT_DUST = VE_ITEM_REGISTRY.register("basalt_dust", BasaltDust::new);
    public static Supplier<Item> FLINT_DUST = VE_ITEM_REGISTRY.register("flint_dust", FlintDust::new);
    public static Supplier<Item> NETHERRACK_DUST = VE_ITEM_REGISTRY.register("netherrack_dust", NetherrackDust::new);
    public static Supplier<Item> NETHERITE_DUST = VE_ITEM_REGISTRY.register("netherite_dust", NetheriteDust::new);
    public static Supplier<Item> TUNGSTEN_DUST = VE_ITEM_REGISTRY.register("tungsten_dust", TungstenDust::new);
    public static Supplier<Item> EIGHZO_DUST = VE_ITEM_REGISTRY.register("eighzo_dust", EighzoDust::new);
    public static Supplier<Item> SOLARIUM_DUST = VE_ITEM_REGISTRY.register("solarium_dust", SolariumDust::new);
    public static Supplier<Item> COPPER_DUST = VE_ITEM_REGISTRY.register("copper_dust", CopperDust::new);
    public static Supplier<Item> COPPER_CARBONATE_DUST = VE_ITEM_REGISTRY.register("copper_carbonate_dust", CopperCarbonateDust::new);
    public static Supplier<Item> CUPRIC_OXIDE_DUST = VE_ITEM_REGISTRY.register("cupric_oxide_dust", CupricOxideDust::new);
    public static Supplier<Item> SAW_DUST = VE_ITEM_REGISTRY.register("saw_dust", SawDust::new);
    public static Supplier<FlourDust> FLOUR_DUST = VE_ITEM_REGISTRY.register("flour_dust", FlourDust::new);

    //Ingots and Bricks
    public static Supplier<CarbonBrick> CARBON_BRICK = VE_ITEM_REGISTRY.register("carbonbrick", CarbonBrick::new);
    public static Supplier<AluminumIngot> ALUMINUM_INGOT = VE_ITEM_REGISTRY.register("aluminum_ingot", AluminumIngot::new);
    public static Supplier<TitaniumIngot> TITANIUM_INGOT = VE_ITEM_REGISTRY.register("titanium_ingot", TitaniumIngot::new);
    public static Supplier<LeadIngot> LEAD_INGOT = VE_ITEM_REGISTRY.register("lead_ingot", LeadIngot::new);
    public static Supplier<SilverIngot> SILVER_INGOT = VE_ITEM_REGISTRY.register("silver_ingot", SilverIngot::new);
    public static Supplier<SteelIngot> STEEL_INGOT = VE_ITEM_REGISTRY.register("steel_ingot", SteelIngot::new);
    public static Supplier<TungstenIngot> TUNGSTEN_INGOT = VE_ITEM_REGISTRY.register("tungsten_ingot", TungstenIngot::new);
    public static Supplier<TungstenSteelIngot> TUNGSTEN_STEEL_INGOT = VE_ITEM_REGISTRY.register("tungsten_steel_ingot", TungstenSteelIngot::new);
    public static Supplier<NighaliteIngot> NIGHALITE_INGOT = VE_ITEM_REGISTRY.register("nighalite_ingot", NighaliteIngot::new);
    public static Supplier<EighzoIngot> EIGHZO_INGOT = VE_ITEM_REGISTRY.register("eighzo_ingot", EighzoIngot::new);
    public static Supplier<SolariumIngot> SOLARIUM_INGOT = VE_ITEM_REGISTRY.register("solarium_ingot", SolariumIngot::new);

    //Gears
    public static Supplier<Item> STONE_GEAR = VE_ITEM_REGISTRY.register("stonegear", StoneGear::new);
    public static Supplier<Item> IRON_GEAR = VE_ITEM_REGISTRY.register("irongear", IronGear::new);
    public static Supplier<Item> CARBON_GEAR = VE_ITEM_REGISTRY.register("carbongear", CarbonGear::new);
    public static Supplier<Item> ALUMINUM_GEAR = VE_ITEM_REGISTRY.register("aluminum_gear", AluminumGear::new);
    public static Supplier<Item> TITANIUM_GEAR = VE_ITEM_REGISTRY.register("titanium_gear", TitaniumGear::new);
    public static Supplier<Item> SOLARIUM_GEAR = VE_ITEM_REGISTRY.register("solarium_gear", SolariumGear::new);

    //Plates
    public static Supplier<Item> ALUMINUM_PLATE = VE_ITEM_REGISTRY.register("aluminum_plate", AluminumPlate::new);
    public static Supplier<Item> CARBON_PLATE = VE_ITEM_REGISTRY.register("carbon_plate", CarbonPlate::new);
    public static Supplier<Item> TITANIUM_PLATE = VE_ITEM_REGISTRY.register("titanium_plate", TitaniumPlate::new);
    public static Supplier<Item> SOLARIUM_PLATE = VE_ITEM_REGISTRY.register("solarium_plate", SolariumPlate::new);

    //Microchips
    public static Supplier<Item> GOLD_MICROCHIP = VE_ITEM_REGISTRY.register("gold_microchip", GoldMicrochip::new);
    public static Supplier<Item> SILVER_MICROCHIP = VE_ITEM_REGISTRY.register("silver_microchip", SilverMicrochip::new);

    //Upgrades
    public static Supplier<Item> QUARTZ_MULTIPLIER = VE_ITEM_REGISTRY.register("quartz_multiplier", QuartzMultiplier::new);
    public static Supplier<Item> MYSTERIOUS_MULTIPLIER = VE_ITEM_REGISTRY.register("mysterious_multiplier", MysteriousMultiplier::new);

    //Batteries
    public static Supplier<Item> MERCURY_BATTERY = VE_ITEM_REGISTRY.register("mercury_battery", MercuryBattery::new);
    public static Supplier<Item> LEAD_ACID_BATTERY = VE_ITEM_REGISTRY.register("lead_acid_battery", LeadAcidBattery::new);
    public static Supplier<Item> MERCURY_BATTERY_PACK = VE_ITEM_REGISTRY.register("mercury_battery_pack", MercuryBatteryPack::new);
    public static Supplier<Item> LEAD_ACID_BATTERY_PACK = VE_ITEM_REGISTRY.register("lead_acid_battery_pack", LeadAcidBatteryPack::new);

    public static Supplier<Item> TITANIUM_SAWBLADE = VE_ITEM_REGISTRY.register("titanium_sawblade", TitaniumSawblade::new);

    //Tank Frames
    public static Supplier<Item> STANDARD_TANK_FRAME = VE_ITEM_REGISTRY.register("standard_tank_frame", StandardTankFrame::new);
    public static Supplier<Item> ROBUST_TANK_FRAME = VE_ITEM_REGISTRY.register("robust_tank_frame", RobustTankFrame::new);
    public static Supplier<Item> IMPECCABLE_TANK_FRAME = VE_ITEM_REGISTRY.register("impeccable_tank_frame", ImpeccableTankFrame::new);

    //Crops
    public static Supplier<Item> RICE_GRAIN = VE_ITEM_REGISTRY.register("rice_grain", () -> new RiceItem(new Item.Properties())); // Can refactor to call the block here or in the item's class
    public static Supplier<Item> COOKED_RICE = VE_ITEM_REGISTRY.register("cooked_rice", () -> new Item(new Item.Properties().food(VEFoods.COOKED_RICE)));

    //Scanner
    public static Supplier<FluidScanner> FLUID_SCANNER = VE_ITEM_REGISTRY.register("fluid_scanner", FluidScanner::new);
    public static Supplier<CreativeFluidScanner> CREATIVE_FLUID_SCANNER = VE_ITEM_REGISTRY.register("creative_fluid_scanner", CreativeFluidScanner::new);
    public static Supplier<RFIDChip> RFID_CHIP = VE_ITEM_REGISTRY.register("rfid_chip", RFIDChip::new);

    //Tiny fuels
    public static Supplier<Item> TINY_CHARCOAL = VE_ITEM_REGISTRY.register("tiny_charcoal", TinyCharcoal::new);
    public static Supplier<Item> TINY_COAL = VE_ITEM_REGISTRY.register("tiny_coal", TinyCoal::new);
    public static Supplier<Item> TINY_COAL_COKE = VE_ITEM_REGISTRY.register("tiny_coal_coke", TinyCoalCoke::new);
    public static Supplier<Item> TINY_PETCOKE = VE_ITEM_REGISTRY.register("tiny_petcoke", TinyPetcoke::new);
    public static Supplier<Item> TINY_ROSIN = VE_ITEM_REGISTRY.register("tiny_rosin", TinyRosin::new);

    //Multitool
    public static Supplier<Item> MULTI_TOOL = VE_ITEM_REGISTRY.register("multitool", Multitool::new);
}
