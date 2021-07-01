package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.foods.VEFoods;
import com.veteam.voluminousenergy.items.batteries.LeadAcidBattery;
import com.veteam.voluminousenergy.items.batteries.MercuryBattery;
import com.veteam.voluminousenergy.items.crops.RiceItem;
import com.veteam.voluminousenergy.items.dusts.*;
import com.veteam.voluminousenergy.items.gears.*;
import com.veteam.voluminousenergy.items.ingots.*;
import com.veteam.voluminousenergy.items.microchips.GoldMicrochip;
import com.veteam.voluminousenergy.items.microchips.SilverMicrochip;
import com.veteam.voluminousenergy.items.ore.*;
import com.veteam.voluminousenergy.items.plates.AluminumPlate;
import com.veteam.voluminousenergy.items.plates.CarbonPlate;
import com.veteam.voluminousenergy.items.plates.SolariumPlate;
import com.veteam.voluminousenergy.items.plates.TitaniumPlate;
import com.veteam.voluminousenergy.items.upgrades.QuartzMultiplier;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class VEItems {
    public static PetCoke PETCOKE = new PetCoke();
    public static CoalCoke COALCOKE = new CoalCoke();
    public static SaltpeterChunk SALTPETERCHUNK = new SaltpeterChunk();
    public static Silicon SILICON = new Silicon();
    public static ShreddedBiomass SHREDDED_BIOMASS = new ShreddedBiomass();

    // Raw Ore
    public static RawBauxite RAW_BAUXITE = new RawBauxite();
    public static RawCinnabar RAW_CINNABAR = new RawCinnabar();
    public static RawGalena RAW_GALENA = new RawGalena();
    public static RawRutile RAW_RUTILE = new RawRutile();
    public static RawSulfur RAW_SULFUR = new RawSulfur();
    public static RawEighzo RAW_EIGHZO = new RawEighzo();

    //Dusts
    public static CoalDust COALDUST = new CoalDust();
    public static CokeDust COKEDUST = new CokeDust();
    public static LapisDust LAPISDUST = new LapisDust();
    public static SulfurDust SULFURDUST = new SulfurDust();
    public static CarbonDust CARBONDUST = new CarbonDust();
    public static SaltpeterDust SALTPETERDUST = new SaltpeterDust();
    public static AluminumDust ALUMINUM_DUST = new AluminumDust();
    public static BauxiteDust BAUXITE_DUST = new BauxiteDust();
    public static CinnabarDust CINNABAR_DUST = new CinnabarDust();
    public static IronDust IRON_DUST = new IronDust();
    public static QuartzDust QUARTZ_DUST = new QuartzDust();
    public static SandDust SAND_DUST = new SandDust();
    public static SoulsandDust SOULSAND_DUST = new SoulsandDust();
    public static TitaniumDust TITANIUM_DUST = new TitaniumDust();
    public static RutileDust RUTILE_DUST = new RutileDust();
    public static GalenaDust GALENA_DUST = new GalenaDust();
    public static LeadDust LEAD_DUST = new LeadDust();
    public static SilverDust SILVER_DUST = new SilverDust();
    public static GoldDust GOLD_DUST = new GoldDust();
    public static PhotovoltaicDust PHOTOVOLTAIC_DUST = new PhotovoltaicDust();
    public static EndStoneDust END_STONE_DUST = new EndStoneDust();
    public static BasaltDust BASALT_DUST = new BasaltDust();
    public static FlintDust FLINT_DUST = new FlintDust();
    public static NetherrackDust NETHERRACK_DUST = new NetherrackDust();
    public static NetheriteDust NETHERITE_DUST = new NetheriteDust();
    public static TungstenDust TUNGSTEN_DUST = new TungstenDust();
    public static EighzoDust EIGHZO_DUST = new EighzoDust();
    public static SolariumDust SOLARIUM_DUST = new SolariumDust();

    //Ingots and Bricks
    public static CarbonBrick CARBON_BRICK = new CarbonBrick();
    public static AluminumIngot ALUMINUM_INGOT = new AluminumIngot();
    public static TitaniumIngot TITANIUM_INGOT = new TitaniumIngot();
    public static LeadIngot LEAD_INGOT = new LeadIngot();
    public static SilverIngot SILVER_INGOT = new SilverIngot();
    public static SteelIngot STEEL_INGOT = new SteelIngot();
    public static TungstenIngot TUNGSTEN_INGOT = new TungstenIngot();
    public static TungstenSteelIngot TUNGSTEN_STEEL_INGOT = new TungstenSteelIngot();
    public static NighaliteIngot NIGHALITE_INGOT = new NighaliteIngot();
    public static EighzoIngot EIGHZO_INGOT = new EighzoIngot();
    public static SolariumIngot SOLARIUM_INGOT = new SolariumIngot();

    //Gears
    public static StoneGear STONE_GEAR = new StoneGear();
    public static IronGear IRON_GEAR = new IronGear();
    public static CarbonGear CARBON_GEAR = new CarbonGear();
    public static AluminumGear ALUMINUM_GEAR = new AluminumGear();
    public static TitaniumGear TITANIUM_GEAR = new TitaniumGear();
    public static SolariumGear SOLARIUM_GEAR = new SolariumGear();

    //Plates
    public static AluminumPlate ALUMINUM_PLATE = new AluminumPlate();
    public static CarbonPlate CARBON_PLATE = new CarbonPlate();
    public static TitaniumPlate TITANIUM_PLATE = new TitaniumPlate();
    public static SolariumPlate SOLARIUM_PLATE = new SolariumPlate();

    //Microchips
    public static GoldMicrochip GOLD_MICROCHIP = new GoldMicrochip();
    public static SilverMicrochip SILVER_MICROCHIP = new SilverMicrochip();

    //Upgrades
    public static QuartzMultiplier QUARTZ_MULTIPLIER = new QuartzMultiplier();

    //Batteries
    public static MercuryBattery MERCURY_BATTERY = new MercuryBattery();
    public static LeadAcidBattery LEAD_ACID_BATTERY = new LeadAcidBattery();

    //Crops
    public static RiceItem RICE_GRAIN = new RiceItem(new Item.Properties().tab(VESetup.itemGroup)); // Can refactor to call the block here or in the item's class
    public static Item COOKED_RICE = new Item((new Item.Properties()).tab(VESetup.itemGroup).food(VEFoods.COOKED_RICE)).setRegistryName("cooked_rice");
}
