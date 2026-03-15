package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static com.veteam.voluminousenergy.items.VEItems.VE_ITEM_REGISTRY;


public class VEFluids {
    public static DeferredRegister<Fluid> VE_FLUIDS = DeferredRegister.create(Registries.FLUID, VoluminousEnergy.MODID);
    public static DeferredRegister<Block> VE_FLUID_BLOCKS = DeferredRegister.create(Registries.BLOCK, VoluminousEnergy.MODID);
    public static DeferredRegister<FluidType> VE_FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, VoluminousEnergy.MODID);

    // Oxygen
    public static Supplier<FlowingFluid> OXYGEN_REG = VE_FLUIDS.register("oxygen",
            Oxygen::OxygenFluid);
    public static Supplier<FlowingFluid> FLOWING_OXYGEN_REG = VE_FLUIDS.register("flowing_oxygen",
            Oxygen::FlowingOxygenFluid);
    public static Supplier<LiquidBlock> FLOWING_OXYGEN_BLOCK_REG = VE_FLUID_BLOCKS.register("oxygen_block",
            Oxygen::FlowingOxygenBlock);
    public static Supplier<Item> OXYGEN_BUCKET_REG = VE_ITEM_REGISTRY.register("oxygen_bucket",
            Oxygen::OxygenBucket);
    public static Supplier<FluidType> OXYGEN_FLUID_TYPE_REG = VE_FLUID_TYPES.register("oxygen",
            () -> Oxygen.OXYGEN_FLUID_TYPE);

    // Crude Oil
    public static Supplier<FlowingFluid> CRUDE_OIL_REG = VE_FLUIDS.register("crude_oil",
            CrudeOil::CrudeOilFluid);
    public static Supplier<FlowingFluid> FLOWING_CRUDE_OIL_REG = VE_FLUIDS.register("flowing_crude_oil",
            CrudeOil::FlowingCrudeOilFluid);
    public static Supplier<LiquidBlock> FLOWING_CRUDE_OIL_BLOCK_REG = VE_FLUID_BLOCKS.register("crude_oil_block",
            CrudeOil::FlowingCrudeOilBlock);
    public static Supplier<Item> CRUDE_OIL_BUCKET_REG = VE_ITEM_REGISTRY.register("crude_oil_bucket",
            CrudeOil::CrudeOilBucket);
    public static Supplier<FluidType> CRUDE_OIL_FLUID_TYPE_REG = VE_FLUID_TYPES.register("crude_oil",
            () -> CrudeOil.CRUDE_OIL_FLUID_TYPE);

    // Naphtha
    public static Supplier<FlowingFluid> NAPHTHA_REG = VE_FLUIDS.register("naphtha",
            Naphtha::NaphthaFluid);
    public static Supplier<FlowingFluid> FLOWING_NAPHTHA_REG = VE_FLUIDS.register("flowing_naphtha",
            Naphtha::FlowingNaphthaFluid);
    public static Supplier<LiquidBlock> FLOWING_NAPHTHA_BLOCK_REG = VE_FLUID_BLOCKS.register("naphtha_block",
            Naphtha::FlowingNaphthaBlock);
    public static Supplier<Item> NAPHTHA_BUCKET_REG = VE_ITEM_REGISTRY.register("naphtha_bucket",
            Naphtha::NaphthaBucket);
    public static Supplier<FluidType> NAPHTHA_FLUID_TYPE_REG = VE_FLUID_TYPES.register("naphtha",
            () -> Naphtha.NAPHTHA_FLUID_TYPE);

    // Red Fuming Nitric Acid (Shortened to RFNA)
    public static Supplier<FlowingFluid> RFNA_REG = VE_FLUIDS.register("red_fuming_nitric_acid",
            RedFumingNitricAcid::RedFumingNitricAcidFluid);
    public static Supplier<FlowingFluid> FLOWING_RFNA_REG = VE_FLUIDS.register("flowing_red_fuming_nitric_acid",
            RedFumingNitricAcid::FlowingRedFumingNitricAcidFluid);
    public static Supplier<LiquidBlock> FLOWING_RFNA_BLOCK_REG = VE_FLUID_BLOCKS.register("red_fuming_nitric_acid_block",
            RedFumingNitricAcid::FlowingRedFumingNitricAcidBlock);
    public static Supplier<Item> RFNA_BUCKET_REG = VE_ITEM_REGISTRY.register("red_fuming_nitric_acid_bucket",
            RedFumingNitricAcid::RedFumingNitricAcidBucket);
    public static Supplier<FluidType> RFNA_FLUID_TYPE_REG = VE_FLUID_TYPES.register("red_fuming_nitric_acid",
            () -> RedFumingNitricAcid.RFNA_FLUID_TYPE);

    // White Fuming Nitric Acid (Shortened to WFNA)
    public static Supplier<FlowingFluid> WFNA_REG = VE_FLUIDS.register("white_fuming_nitric_acid",
            WhiteFumingNitricAcid::WhiteFumingNitricAcidFluid);
    public static Supplier<FlowingFluid> FLOWING_WFNA_REG = VE_FLUIDS.register("flowing_white_fuming_nitric_acid",
            WhiteFumingNitricAcid::FlowingWhiteFumingNitricAcidFluid);
    public static Supplier<LiquidBlock> FLOWING_WFNA_BLOCK_REG = VE_FLUID_BLOCKS.register("white_fuming_nitric_acid_block",
            WhiteFumingNitricAcid::FlowingWhiteFumingNitricAcidBlock);
    public static Supplier<Item> WFNA_BUCKET_REG = VE_ITEM_REGISTRY.register("white_fuming_nitric_acid_bucket",
            WhiteFumingNitricAcid::WhiteFumingNitricAcidBucket);
    public static Supplier<FluidType> WFNA_FLUID_TYPE_REG = VE_FLUID_TYPES.register("white_fuming_nitric_acid",
            () -> WhiteFumingNitricAcid.WFNA_FLUID_TYPE);

    // Mercury
    public static Supplier<FlowingFluid> MERCURY_REG = VE_FLUIDS.register("mercury",
            Mercury::MercuryFluid);
    public static Supplier<FlowingFluid> FLOWING_MERCURY_REG = VE_FLUIDS.register("flowing_mercury",
            Mercury::FlowingMercuryFluid);
    public static Supplier<LiquidBlock> FLOWING_MERCURY_BLOCK_REG = VE_FLUID_BLOCKS.register("mercury_block",
            Mercury::FlowingMercuryBlock);
    public static Supplier<Item> MERCURY_BUCKET_REG = VE_ITEM_REGISTRY.register("mercury_bucket",
            Mercury::MercuryBucket);
    public static Supplier<FluidType> MERCURY_FLUID_TYPE_REG = VE_FLUID_TYPES.register("mercury",
            () -> Mercury.MERCURY_FLUID_TYPE);

    // Sulfuric Acid
    public static Supplier<FlowingFluid> SULFURIC_ACID_REG = VE_FLUIDS.register("sulfuric_acid",
            SulfuricAcid::SulfuricAcidFluid);
    public static Supplier<FlowingFluid> FLOWING_SULFURIC_ACID_REG = VE_FLUIDS.register("flowing_sulfuric_acid",
            SulfuricAcid::FlowingSulfuricAcidFluid);
    public static Supplier<LiquidBlock> FLOWING_SULFURIC_ACID_BLOCK_REG = VE_FLUID_BLOCKS.register("sulfuric_acid_block",
            SulfuricAcid::FlowingSulfuricAcidBlock);
    public static Supplier<Item> SULFURIC_ACID_BUCKET_REG = VE_ITEM_REGISTRY.register("sulfuric_acid_bucket",
            SulfuricAcid::SulfuricAcidBucket);
    public static Supplier<FluidType> SULFURIC_ACID_FLUID_TYPE_REG = VE_FLUID_TYPES.register("sulfuric_acid",
            () -> SulfuricAcid.SULFURIC_ACID_FLUID_TYPE);

    // Dinitrogen Tetroxide
    public static Supplier<FlowingFluid> DINITROGEN_TETROXIDE_REG = VE_FLUIDS.register("dinitrogen_tetroxide",
            DinitrogenTetroxide::DinitrogenTetroxideFluid);
    public static Supplier<FlowingFluid> FLOWING_DINITROGEN_TETROXIDE_REG = VE_FLUIDS.register("flowing_dinitrogen_tetroxide",
            DinitrogenTetroxide::FlowingDinitrogenTetroxideFluid);
    public static Supplier<LiquidBlock> FLOWING_DINITROGEN_TETROXIDE_BLOCK_REG = VE_FLUID_BLOCKS.register("dinitrogen_tetroxide_block",
            DinitrogenTetroxide::FlowingDinitrogenTetroxideBlock);
    public static Supplier<Item> DINITROGEN_TETROXIDE_BUCKET_REG = VE_ITEM_REGISTRY.register("dinitrogen_tetroxide_bucket",
            DinitrogenTetroxide::DinitrogenTetroxideBucket);
    public static Supplier<FluidType> DINITROGEN_TETROXIDE_FLUID_TYPE_REG = VE_FLUID_TYPES.register("dinitrogen_tetroxide",
            () -> DinitrogenTetroxide.DINITROGEN_TETROXIDE_FLUID_TYPE);

    // Compressed Air
    public static Supplier<FlowingFluid> COMPRESSED_AIR_REG = VE_FLUIDS.register("compressed_air",
            CompressedAir::CompressedAirFluid);
    public static Supplier<FlowingFluid> FLOWING_COMPRESSED_AIR_REG = VE_FLUIDS.register("flowing_compressed_air",
            CompressedAir::FlowingCompressedAirFluid);
    public static Supplier<LiquidBlock> FLOWING_COMPRESSED_AIR_BLOCK_REG = VE_FLUID_BLOCKS.register("compressed_air_block",
            CompressedAir::FlowingCompressedAirBlock);
    public static Supplier<Item> COMPRESSED_AIR_BUCKET_REG = VE_ITEM_REGISTRY.register("compressed_air_bucket",
            CompressedAir::CompressedAirBucket);
    public static Supplier<FluidType> COMPRESSED_AIR_FLUID_TYPE_REG = VE_FLUID_TYPES.register("compressed_air",
            () -> CompressedAir.COMPRESSED_AIR_FLUID_TYPE);

    // Nitrogen
    public static Supplier<FlowingFluid> NITROGEN_REG = VE_FLUIDS.register("nitrogen",
            Nitrogen::NitrogenFluid);
    public static Supplier<FlowingFluid> FLOWING_NITROGEN_REG = VE_FLUIDS.register("flowing_nitrogen",
            Nitrogen::FlowingNitrogenFluid);
    public static Supplier<LiquidBlock> FLOWING_NITROGEN_BLOCK_REG = VE_FLUID_BLOCKS.register("nitrogen_block",
            Nitrogen::FlowingNitrogenBlock);
    public static Supplier<Item> NITROGEN_BUCKET_REG = VE_ITEM_REGISTRY.register("nitrogen_bucket",
            Nitrogen::NitrogenBucket);
    public static Supplier<FluidType> NITROGEN_FLUID_TYPE_REG = VE_FLUID_TYPES.register("nitrogen",
            () -> Nitrogen.NITROGEN_FLUID_TYPE);

    // Biofuel
    public static Supplier<FlowingFluid> BIOFUEL_REG = VE_FLUIDS.register("biofuel",
            Biofuel::BiofuelFluid);
    public static Supplier<FlowingFluid> FLOWING_BIOFUEL_REG = VE_FLUIDS.register("flowing_biofuel",
            Biofuel::FlowingBiofuelFluid);
    public static Supplier<LiquidBlock> FLOWING_BIOFUEL_BLOCK_REG = VE_FLUID_BLOCKS.register("biofuel_block",
            Biofuel::FlowingBiofuelBlock);
    public static Supplier<Item> BIOFUEL_BUCKET_REG = VE_ITEM_REGISTRY.register("biofuel_bucket",
            Biofuel::BiofuelBucket);
    public static Supplier<FluidType> BIOFUEL_FLUID_TYPE_REG = VE_FLUID_TYPES.register("biofuel",
            () -> Biofuel.BIOFUEL_FLUID_TYPE);

    // Diesel
    public static Supplier<FlowingFluid> DIESEL_REG = VE_FLUIDS.register("diesel",
            Diesel::DieselFluid);
    public static Supplier<FlowingFluid> FLOWING_DIESEL_REG = VE_FLUIDS.register("flowing_diesel",
            Diesel::FlowingDieselFluid);
    public static Supplier<LiquidBlock> FLOWING_DIESEL_BLOCK_REG = VE_FLUID_BLOCKS.register("diesel_block",
            Diesel::FlowingDieselBlock);
    public static Supplier<Item> DIESEL_BUCKET_REG = VE_ITEM_REGISTRY.register("diesel_bucket",
            Diesel::DieselBucket);
    public static Supplier<FluidType> DIESEL_FLUID_TYPE_REG = VE_FLUID_TYPES.register("diesel",
            () -> Diesel.DIESEL_FLUID_TYPE);

    // Gasoline
    public static Supplier<FlowingFluid> GASOLINE_REG = VE_FLUIDS.register("gasoline",
            Gasoline::GasolineFluid);
    public static Supplier<FlowingFluid> FLOWING_GASOLINE_REG = VE_FLUIDS.register("flowing_gasoline",
            Gasoline::FlowingGasolineFluid);
    public static Supplier<LiquidBlock> FLOWING_GASOLINE_BLOCK_REG = VE_FLUID_BLOCKS.register("gasoline_block",
            Gasoline::FlowingGasolineBlock);
    public static Supplier<Item> GASOLINE_BUCKET_REG = VE_ITEM_REGISTRY.register("gasoline_bucket",
            Gasoline::GasolineBucket);
    public static Supplier<FluidType> GASOLINE_FLUID_TYPE_REG = VE_FLUID_TYPES.register("gasoline",
            () -> Gasoline.GASOLINE_FLUID_TYPE);

    // Nitroglycerin
    public static Supplier<FlowingFluid> NITROGLYCERIN_REG = VE_FLUIDS.register("nitroglycerin",
            Nitroglycerin::NitroglycerinFluid);
    public static Supplier<FlowingFluid> FLOWING_NITROGLYCERIN_REG = VE_FLUIDS.register("flowing_nitroglycerin",
            Nitroglycerin::FlowingNitroglycerinFluid);
    public static Supplier<LiquidBlock> FLOWING_NITROGLYCERIN_BLOCK_REG = VE_FLUID_BLOCKS.register("nitroglycerin_block",
            Nitroglycerin::FlowingNitroglycerinBlock);
    public static Supplier<Item> NITROGLYCERIN_BUCKET_REG = VE_ITEM_REGISTRY.register("nitroglycerin_bucket",
            Nitroglycerin::NitroglycerinBucket);
    public static Supplier<FluidType> NITROGLYCERIN_FLUID_TYPE_REG = VE_FLUID_TYPES.register("nitroglycerin",
            () -> Nitroglycerin.NITROGLYCERIN_FLUID_TYPE);

    // Light Fuel
    public static Supplier<FlowingFluid> LIGHT_FUEL_REG = VE_FLUIDS.register("light_fuel",
            LightFuel::LightFuelFluid);
    public static Supplier<FlowingFluid> FLOWING_LIGHT_FUEL_REG = VE_FLUIDS.register("flowing_light_fuel",
            LightFuel::FlowingLightFuelFluid);
    public static Supplier<LiquidBlock> FLOWING_LIGHT_FUEL_BLOCK_REG = VE_FLUID_BLOCKS.register("light_fuel_block",
            LightFuel::FlowingLightFuelBlock);
    public static Supplier<Item> LIGHT_FUEL_BUCKET_REG = VE_ITEM_REGISTRY.register("light_fuel_bucket",
            LightFuel::LightFuelBucket);
    public static Supplier<FluidType> LIGHT_FUEL_FLUID_TYPE_REG = VE_FLUID_TYPES.register("light_fuel",
            () -> LightFuel.LIGHT_FUEL_FLUID_TYPE);

    // Liquefied Coal
    public static Supplier<FlowingFluid> LIQUEFIED_COAL_REG = VE_FLUIDS.register("liquefied_coal",
            LiquefiedCoal::LiquefiedCoalFluid);
    public static Supplier<FlowingFluid> FLOWING_LIQUEFIED_COAL_REG = VE_FLUIDS.register("flowing_liquefied_coal",
            LiquefiedCoal::FlowingLiquefiedCoalFluid);
    public static Supplier<LiquidBlock> FLOWING_LIQUEFIED_COAL_BLOCK_REG = VE_FLUID_BLOCKS.register("liquefied_coal_block",
            LiquefiedCoal::FlowingLiquefiedCoalBlock);
    public static Supplier<Item> LIQUEFIED_COAL_BUCKET_REG = VE_ITEM_REGISTRY.register("liquefied_coal_bucket",
            LiquefiedCoal::LiquefiedCoalBucket);
    public static Supplier<FluidType> LIQUEFIED_COAL_TYPE_REG = VE_FLUID_TYPES.register("liquefied_coal",
            () -> LiquefiedCoal.LIQUEFIED_COAL_FLUID_TYPE);

    // Liquefied Coke
    public static Supplier<FlowingFluid> LIQUEFIED_COKE_REG = VE_FLUIDS.register("liquefied_coke",
            LiquefiedCoke::LiquefiedCokeFluid);
    public static Supplier<FlowingFluid> FLOWING_LIQUEFIED_COKE_REG = VE_FLUIDS.register("flowing_liquefied_coke",
            LiquefiedCoke::FlowingLiquefiedCokeFluid);
    public static Supplier<LiquidBlock> FLOWING_LIQUEFIED_COKE_BLOCK_REG = VE_FLUID_BLOCKS.register("liquefied_coke_block",
            LiquefiedCoke::FlowingLiquefiedCokeBlock);
    public static Supplier<Item> LIQUEFIED_COKE_BUCKET_REG = VE_ITEM_REGISTRY.register("liquefied_coke_bucket",
            LiquefiedCoke::LiquefiedCokeBucket);
    public static Supplier<FluidType> LIQUEFIED_COKE_FLUID_TYPE_REG = VE_FLUID_TYPES.register("liquefied_coke",
            () -> LiquefiedCoke.LIQUEFIED_COKE_FLUID_TYPE);

    // Tree Sap
    public static Supplier<FlowingFluid> TREE_SAP_REG = VE_FLUIDS.register("tree_sap",
            TreeSap::TreeSapFluid);
    public static Supplier<FlowingFluid> FLOWING_TREE_SAP_REG = VE_FLUIDS.register("flowing_tree_sap",
            TreeSap::FlowingTreeSapFluid);
    public static Supplier<LiquidBlock> FLOWING_TREE_SAP_BLOCK_REG = VE_FLUID_BLOCKS.register("tree_sap_block",
            TreeSap::FlowingTreeSapBlock);
    public static Supplier<Item> TREE_SAP_BUCKET_REG = VE_ITEM_REGISTRY.register("tree_sap_bucket",
            TreeSap::TreeSapBucket);
    public static Supplier<FluidType> TREE_SAP_FLUID_TYPE_REG = VE_FLUID_TYPES.register("tree_sap",
            () -> TreeSap.TREE_SAP_FLUID_TYPE);

    // Treethanol
    public static Supplier<FlowingFluid> TREETHANOL_REG = VE_FLUIDS.register("treethanol",
            Treethanol::TreethanolFluid);
    public static Supplier<FlowingFluid> FLOWING_TREETHANOL_REG = VE_FLUIDS.register("flowing_treethanol",
            Treethanol::FlowingTreethanolFluid);
    public static Supplier<LiquidBlock> FLOWING_TREETHANOL_BLOCK_REG = VE_FLUID_BLOCKS.register("treethanol_block",
            Treethanol::FlowingTreethanolBlock);
    public static Supplier<Item> TREETHANOL_BUCKET_REG = VE_ITEM_REGISTRY.register("treethanol_bucket",
            Treethanol::TreethanolBucket);
    public static Supplier<FluidType> TREETHANOL_FLUID_TYPE_REG = VE_FLUID_TYPES.register("treethanol",
            () -> Treethanol.TREETHANOL_FLUID_TYPE);

    // Ammonia
    public static Supplier<FlowingFluid> AMMONIA_REG = VE_FLUIDS.register("ammonia",
            Ammonia::AmmoniaFluid);
    public static Supplier<FlowingFluid> FLOWING_AMMONIA_REG = VE_FLUIDS.register("flowing_ammonia",
            Ammonia::FlowingAmmoniaFluid);
    public static Supplier<LiquidBlock> FLOWING_AMMONIA_BLOCK_REG = VE_FLUID_BLOCKS.register("ammonia_block",
            Ammonia::FlowingAmmoniaBlock);
    public static Supplier<Item> AMMONIA_BUCKET_REG = VE_ITEM_REGISTRY.register("ammonia_bucket",
            Ammonia::AmmoniaBucket);
    public static Supplier<FluidType> AMMONIA_FLUID_TYPE_REG = VE_FLUID_TYPES.register("ammonia",
            () -> Ammonia.AMMONIA_FLUID_TYPE);

    // AmmoniumNitrateSolution
    public static Supplier<FlowingFluid> AMMONIUM_NITRATE_SOLUTION_REG = VE_FLUIDS.register("ammonium_nitrate_solution",
            AmmoniumNitrateSolution::AmmoniumNitrateSolutionFluid);
    public static Supplier<FlowingFluid> FLOWING_AMMONIUM_NITRATE_SOLUTION_REG = VE_FLUIDS.register("flowing_ammonium_nitrate_solution",
            AmmoniumNitrateSolution::FlowingAmmoniumNitrateSolutionFluid);
    public static Supplier<LiquidBlock> FLOWING_AMMONIUM_NITRATE_SOLUTION_BLOCK_REG = VE_FLUID_BLOCKS.register("ammonium_nitrate_solution_block",
            AmmoniumNitrateSolution::FlowingAmmoniumNitrateSolutionBlock);
    public static Supplier<Item> AMMONIUM_NITRATE_SOLUTION_BUCKET_REG = VE_ITEM_REGISTRY.register("ammonium_nitrate_solution_bucket",
            AmmoniumNitrateSolution::AmmoniumNitrateSolutionBucket);
    public static Supplier<FluidType> AMMONIUM_NITRATE_SOLUTION_FLUID_TYPE_REG = VE_FLUID_TYPES.register("ammonium_nitrate_solution",
            () -> AmmoniumNitrateSolution.AMMONIUM_NITRATE_SOLUTION_FLUID_TYPE);

    // Hydrogen
    public static Supplier<FlowingFluid> HYDROGEN_REG = VE_FLUIDS.register("hydrogen",
            Hydrogen::HydrogenFluid);
    public static Supplier<FlowingFluid> FLOWING_HYDROGEN_REG = VE_FLUIDS.register("flowing_hydrogen",
            Hydrogen::FlowingHydrogenFluid);
    public static Supplier<LiquidBlock> FLOWING_HYDROGEN_BLOCK_REG = VE_FLUID_BLOCKS.register("hydrogen_block",
            Hydrogen::FlowingHydrogenBlock);
    public static Supplier<Item> HYDROGEN_BUCKET_REG = VE_ITEM_REGISTRY.register("hydrogen_bucket",
            Hydrogen::HydrogenBucket);
    public static Supplier<FluidType> HYDROGEN_FLUID_TYPE_REG = VE_FLUID_TYPES.register("hydrogen",
            () -> Hydrogen.HYDROGEN_FLUID_TYPE);
}
