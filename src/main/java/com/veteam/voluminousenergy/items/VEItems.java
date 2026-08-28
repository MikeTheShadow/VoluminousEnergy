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
import com.veteam.voluminousenergy.items.upgrades.MysteriousMultiplier;
import com.veteam.voluminousenergy.items.upgrades.QuartzMultiplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class VEItems {
    public static final DeferredRegister<Item> VE_ITEM_REGISTRY = DeferredRegister.create(Registries.ITEM, VoluminousEnergy.MODID);

    // Misc
    public static DeferredHolder<Item, Petcoke> PETCOKE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "petcoke", Petcoke::new);
    public static Supplier<CoalCoke> COALCOKE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "coalcoke", CoalCoke::new);
    public static Supplier<SaltpeterChunk> SALTPETERCHUNK = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "saltpeterchunk", SaltpeterChunk::new);
    public static Supplier<Silicon> SILICON = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "silicon", Silicon::new);
    public static Supplier<ShreddedBiomass> SHREDDED_BIOMASS = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "shredded_biomass", ShreddedBiomass::new);
    public static Supplier<Item> ROSIN = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "rosin", Rosin::new);
    public static Supplier<AnimalFat> ANIMAL_FAT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "animal_fat", AnimalFat::new);
    public static Supplier<Dough> DOUGH = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "dough", Dough::new);

    // Raw Ore
    public static Supplier<RawBauxite> RAW_BAUXITE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "raw_bauxite", RawBauxite::new);
    public static Supplier<RawCinnabar> RAW_CINNABAR = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "raw_cinnabar", RawCinnabar::new);
    public static Supplier<RawGalena> RAW_GALENA = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "raw_galena", RawGalena::new);
    public static Supplier<RawRutile> RAW_RUTILE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "raw_rutile", RawRutile::new);
    public static Supplier<RawSulfur> RAW_SULFUR = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "raw_sulfur", RawSulfur::new);
    public static Supplier<RawEighzo> RAW_EIGHZO = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "raw_eighzo", RawEighzo::new);

    //Dusts
    public static Supplier<CoalDust> COALDUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "coaldust", CoalDust::new);
    public static Supplier<CokeDust> COKEDUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "cokedust", CokeDust::new);
    public static Supplier<LapisDust> LAPISDUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "lapisdust", LapisDust::new);
    public static Supplier<SulfurDust> SULFURDUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "sulfurdust", SulfurDust::new);
    public static Supplier<Item> CARBONDUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "carbondust", CarbonDust::new);
    public static Supplier<Item> SALTPETERDUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "saltpeterdust", SaltpeterDust::new);
    public static Supplier<Item> ALUMINUM_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "aluminum_dust", AluminumDust::new);
    public static Supplier<Item> BAUXITE_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "bauxite_dust", BauxiteDust::new);
    public static Supplier<Item> CINNABAR_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "cinnabar_dust", CinnabarDust::new);
    public static Supplier<Item> IRON_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "iron_dust", IronDust::new);
    public static Supplier<Item> QUARTZ_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "quartz_dust", QuartzDust::new);
    public static Supplier<Item> SAND_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "sand_dust", SandDust::new);
    public static Supplier<Item> SOULSAND_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "soulsand_dust", SoulsandDust::new);
    public static Supplier<Item> TITANIUM_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "titanium_dust", TitaniumDust::new);
    public static Supplier<Item> RUTILE_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "rutile_dust", RutileDust::new);
    public static Supplier<Item> GALENA_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "galena_dust", GalenaDust::new);
    public static Supplier<Item> LEAD_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "lead_dust", LeadDust::new);
    public static Supplier<Item> SILVER_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "silver_dust", SilverDust::new);
    public static Supplier<Item> GOLD_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "gold_dust", GoldDust::new);
    public static Supplier<Item> PHOTOVOLTAIC_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "photovoltaic_dust", PhotovoltaicDust::new);
    public static Supplier<Item> END_STONE_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "end_stone_dust", EndStoneDust::new);
    public static Supplier<Item> BASALT_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "basalt_dust", BasaltDust::new);
    public static Supplier<Item> FLINT_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "flint_dust", FlintDust::new);
    public static Supplier<Item> NETHERRACK_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "netherrack_dust", NetherrackDust::new);
    public static Supplier<Item> NETHERITE_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "netherite_dust", NetheriteDust::new);
    public static Supplier<Item> TUNGSTEN_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tungsten_dust", TungstenDust::new);
    public static Supplier<Item> EIGHZO_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "eighzo_dust", EighzoDust::new);
    public static Supplier<Item> SOLARIUM_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "solarium_dust", SolariumDust::new);
    public static Supplier<Item> COPPER_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "copper_dust", CopperDust::new);
    public static Supplier<Item> COPPER_CARBONATE_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "copper_carbonate_dust", CopperCarbonateDust::new);
    public static Supplier<Item> CUPRIC_OXIDE_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "cupric_oxide_dust", CupricOxideDust::new);
    public static Supplier<Item> SAW_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "saw_dust", SawDust::new);
    public static Supplier<FlourDust> FLOUR_DUST = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "flour_dust", FlourDust::new);

    //Ingots and Bricks
    public static Supplier<CarbonBrick> CARBON_BRICK = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "carbonbrick", CarbonBrick::new);
    public static Supplier<AluminumIngot> ALUMINUM_INGOT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "aluminum_ingot", AluminumIngot::new);
    public static Supplier<TitaniumIngot> TITANIUM_INGOT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "titanium_ingot", TitaniumIngot::new);
    public static Supplier<LeadIngot> LEAD_INGOT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "lead_ingot", LeadIngot::new);
    public static Supplier<SilverIngot> SILVER_INGOT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "silver_ingot", SilverIngot::new);
    public static Supplier<SteelIngot> STEEL_INGOT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "steel_ingot", SteelIngot::new);
    public static Supplier<TungstenIngot> TUNGSTEN_INGOT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tungsten_ingot", TungstenIngot::new);
    public static Supplier<TungstenSteelIngot> TUNGSTEN_STEEL_INGOT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tungsten_steel_ingot", TungstenSteelIngot::new);
    public static Supplier<NighaliteIngot> NIGHALITE_INGOT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "nighalite_ingot", NighaliteIngot::new);
    public static Supplier<EighzoIngot> EIGHZO_INGOT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "eighzo_ingot", EighzoIngot::new);
    public static Supplier<SolariumIngot> SOLARIUM_INGOT = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "solarium_ingot", SolariumIngot::new);

    //Gears
    public static Supplier<Item> STONE_GEAR = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "stonegear", StoneGear::new);
    public static Supplier<Item> IRON_GEAR = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "irongear", IronGear::new);
    public static Supplier<Item> CARBON_GEAR = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "carbongear", CarbonGear::new);
    public static Supplier<Item> ALUMINUM_GEAR = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "aluminum_gear", AluminumGear::new);
    public static Supplier<Item> TITANIUM_GEAR = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "titanium_gear", TitaniumGear::new);
    public static Supplier<Item> SOLARIUM_GEAR = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "solarium_gear", SolariumGear::new);

    //Plates
    public static Supplier<Item> ALUMINUM_PLATE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "aluminum_plate", AluminumPlate::new);
    public static Supplier<Item> CARBON_PLATE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "carbon_plate", CarbonPlate::new);
    public static Supplier<Item> TITANIUM_PLATE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "titanium_plate", TitaniumPlate::new);
    public static Supplier<Item> SOLARIUM_PLATE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "solarium_plate", SolariumPlate::new);

    //Microchips
    public static Supplier<Item> GOLD_MICROCHIP = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "gold_microchip", GoldMicrochip::new);
    public static Supplier<Item> SILVER_MICROCHIP = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "silver_microchip", SilverMicrochip::new);

    //Upgrades
    public static Supplier<Item> QUARTZ_MULTIPLIER = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "quartz_multiplier", QuartzMultiplier::new);
    public static Supplier<Item> MYSTERIOUS_MULTIPLIER = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "mysterious_multiplier", MysteriousMultiplier::new);

    //Batteries
    public static Supplier<Item> MERCURY_BATTERY = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "mercury_battery", MercuryBattery::new);
    public static Supplier<Item> LEAD_ACID_BATTERY = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "lead_acid_battery", LeadAcidBattery::new);
    public static Supplier<Item> MERCURY_BATTERY_PACK = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "mercury_battery_pack", MercuryBatteryPack::new);
    public static Supplier<Item> LEAD_ACID_BATTERY_PACK = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "lead_acid_battery_pack", LeadAcidBatteryPack::new);

    public static Supplier<Item> TITANIUM_SAWBLADE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "titanium_sawblade", TitaniumSawblade::new);

    //Tank Frames
    public static Supplier<Item> STANDARD_TANK_FRAME = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "standard_tank_frame", StandardTankFrame::new);
    public static Supplier<Item> ROBUST_TANK_FRAME = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "robust_tank_frame", RobustTankFrame::new);
    public static Supplier<Item> IMPECCABLE_TANK_FRAME = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "impeccable_tank_frame", ImpeccableTankFrame::new);

    //Crops
    public static Supplier<Item> RICE_GRAIN = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "rice_grain", () -> new RiceItem(new Item.Properties().setId(VERegistryHelper.currentItemId()))); // Can refactor to call the block here or in the item's class
    public static Supplier<Item> COOKED_RICE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "cooked_rice", () -> new Item(new Item.Properties().setId(VERegistryHelper.currentItemId()).food(VEFoods.COOKED_RICE)));

    //Scanner
    public static Supplier<FluidScanner> FLUID_SCANNER = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "fluid_scanner", FluidScanner::new);
    public static Supplier<CreativeFluidScanner> CREATIVE_FLUID_SCANNER = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "creative_fluid_scanner", CreativeFluidScanner::new);
    public static Supplier<RFIDChip> RFID_CHIP = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "rfid_chip", RFIDChip::new);

    //Tiny fuels
    public static Supplier<Item> TINY_CHARCOAL = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tiny_charcoal", TinyCharcoal::new);
    public static Supplier<Item> TINY_COAL = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tiny_coal", TinyCoal::new);
    public static Supplier<Item> TINY_COAL_COKE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tiny_coal_coke", TinyCoalCoke::new);
    public static Supplier<Item> TINY_PETCOKE = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tiny_petcoke", TinyPetcoke::new);
    public static Supplier<Item> TINY_ROSIN = VERegistryHelper.registerItem(VE_ITEM_REGISTRY, "tiny_rosin", TinyRosin::new);
}
