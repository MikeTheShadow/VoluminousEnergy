package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class VEFluids {
    public static DeferredRegister<Fluid> VE_FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, VoluminousEnergy.MODID);
    public static DeferredRegister<Item> VE_FLUID_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VoluminousEnergy.MODID);
    public static DeferredRegister<Block> VE_FLUID_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VoluminousEnergy.MODID);
    public static DeferredRegister<FluidType> VE_FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, VoluminousEnergy.MODID);

    // Oxygen
    public static RegistryObject<FlowingFluid> OXYGEN_REG = VE_FLUIDS.register("oxygen",
            Oxygen::OxygenFluid);
    public static RegistryObject<FlowingFluid> FLOWING_OXYGEN_REG = VE_FLUIDS.register("flowing_oxygen",
            Oxygen::FlowingOxygenFluid);
    public static RegistryObject<LiquidBlock> FLOWING_OXYGEN_BLOCK_REG = VE_FLUID_BLOCKS.register("oxygen_block",
            Oxygen::FlowingOxygenBlock);
    public static RegistryObject<Item> OXYGEN_BUCKET_REG = VE_FLUID_ITEMS.register("oxygen_bucket",
            Oxygen::OxygenBucket);
    public static RegistryObject<FluidType> OXYGEN_FLUID_TYPE_REG = VE_FLUID_TYPES.register("oxygen",
            () -> Oxygen.OXYGEN_FLUID_TYPE);

    // Crude Oil
    public static RegistryObject<FlowingFluid> CRUDE_OIL_REG = VE_FLUIDS.register("crude_oil",
            CrudeOil::CrudeOilFluid);
    public static RegistryObject<FlowingFluid> FLOWING_CRUDE_OIL_REG = VE_FLUIDS.register("flowing_crude_oil",
            CrudeOil::FlowingCrudeOilFluid);
    public static RegistryObject<LiquidBlock> FLOWING_CRUDE_OIL_BLOCK_REG = VE_FLUID_BLOCKS.register("crude_oil_block",
            CrudeOil::FlowingCrudeOilBlock);
    public static RegistryObject<Item> CRUDE_OIL_BUCKET_REG = VE_FLUID_ITEMS.register("crude_oil_bucket",
            CrudeOil::CrudeOilBucket);
    public static RegistryObject<FluidType> CRUDE_OIL_FLUID_TYPE_REG = VE_FLUID_TYPES.register("crude_oil",
            () -> CrudeOil.CRUDE_OIL_FLUID_TYPE);

    // Naphtha
    public static RegistryObject<FlowingFluid> NAPHTHA_REG = VE_FLUIDS.register("naphtha",
            Naphtha::NaphthaFluid);
    public static RegistryObject<FlowingFluid> FLOWING_NAPHTHA_REG = VE_FLUIDS.register("flowing_naphtha",
            Naphtha::FlowingNaphthaFluid);
    public static RegistryObject<LiquidBlock> FLOWING_NAPHTHA_BLOCK_REG = VE_FLUID_BLOCKS.register("naphtha_block",
            Naphtha::FlowingNaphthaBlock);
    public static RegistryObject<Item> NAPHTHA_BUCKET_REG = VE_FLUID_ITEMS.register("naphtha_bucket",
            Naphtha::NaphthaBucket);
    public static RegistryObject<FluidType> NAPHTHA_FLUID_TYPE_REG = VE_FLUID_TYPES.register("naphtha",
            () -> Naphtha.NAPHTHA_FLUID_TYPE);

    // Red Fuming Nitric Acid (Shortened to RFNA)
    public static RegistryObject<FlowingFluid> RFNA_REG = VE_FLUIDS.register("red_fuming_nitric_acid",
            RedFumingNitricAcid::RedFumingNitricAcidFluid);
    public static RegistryObject<FlowingFluid> FLOWING_RFNA_REG = VE_FLUIDS.register("flowing_red_fuming_nitric_acid",
            RedFumingNitricAcid::FlowingRedFumingNitricAcidFluid);
    public static RegistryObject<LiquidBlock> FLOWING_RFNA_BLOCK_REG = VE_FLUID_BLOCKS.register("red_fuming_nitric_acid_block",
            RedFumingNitricAcid::FlowingRedFumingNitricAcidBlock);
    public static RegistryObject<Item> RFNA_BUCKET_REG = VE_FLUID_ITEMS.register("red_fuming_nitric_acid_bucket",
            RedFumingNitricAcid::RedFumingNitricAcidBucket);
    public static RegistryObject<FluidType> RFNA_FLUID_TYPE_REG = VE_FLUID_TYPES.register("red_fuming_nitric_acid",
            () -> RedFumingNitricAcid.RFNA_FLUID_TYPE);

    // White Fuming Nitric Acid (Shortened to WFNA)
    public static RegistryObject<FlowingFluid> WFNA_REG = VE_FLUIDS.register("white_fuming_nitric_acid",
            WhiteFumingNitricAcid::WhiteFumingNitricAcidFluid);
    public static RegistryObject<FlowingFluid> FLOWING_WFNA_REG = VE_FLUIDS.register("flowing_white_fuming_nitric_acid",
            WhiteFumingNitricAcid::FlowingWhiteFumingNitricAcidFluid);
    public static RegistryObject<LiquidBlock> FLOWING_WFNA_BLOCK_REG = VE_FLUID_BLOCKS.register("white_fuming_nitric_acid_block",
            WhiteFumingNitricAcid::FlowingWhiteFumingNitricAcidBlock);
    public static RegistryObject<Item> WFNA_BUCKET_REG = VE_FLUID_ITEMS.register("white_fuming_nitric_acid_bucket",
            WhiteFumingNitricAcid::WhiteFumingNitricAcidBucket);
    public static RegistryObject<FluidType> WFNA_FLUID_TYPE_REG = VE_FLUID_TYPES.register("white_fuming_nitric_acid",
            () -> WhiteFumingNitricAcid.WFNA_FLUID_TYPE);

    // Mercury
    public static RegistryObject<FlowingFluid> MERCURY_REG = VE_FLUIDS.register("mercury",
            Mercury::MercuryFluid);
    public static RegistryObject<FlowingFluid> FLOWING_MERCURY_REG = VE_FLUIDS.register("flowing_mercury",
            Mercury::FlowingMercuryFluid);
    public static RegistryObject<LiquidBlock> FLOWING_MERCURY_BLOCK_REG = VE_FLUID_BLOCKS.register("mercury_block",
            Mercury::FlowingMercuryBlock);
    public static RegistryObject<Item> MERCURY_BUCKET_REG = VE_FLUID_ITEMS.register("mercury_bucket",
            Mercury::MercuryBucket);
    public static RegistryObject<FluidType> MERCURY_FLUID_TYPE_REG = VE_FLUID_TYPES.register("mercury",
            () -> Mercury.MERCURY_FLUID_TYPE);

    // Sulfuric Acid
    public static RegistryObject<FlowingFluid> SULFURIC_ACID_REG = VE_FLUIDS.register("sulfuric_acid",
            SulfuricAcid::SulfuricAcidFluid);
    public static RegistryObject<FlowingFluid> FLOWING_SULFURIC_ACID_REG = VE_FLUIDS.register("flowing_sulfuric_acid",
            SulfuricAcid::FlowingSulfuricAcidFluid);
    public static RegistryObject<LiquidBlock> FLOWING_SULFURIC_ACID_BLOCK_REG = VE_FLUID_BLOCKS.register("sulfuric_acid_block",
            SulfuricAcid::FlowingSulfuricAcidBlock);
    public static RegistryObject<Item> SULFURIC_ACID_BUCKET_REG = VE_FLUID_ITEMS.register("sulfuric_acid_bucket",
            SulfuricAcid::SulfuricAcidBucket);
    public static RegistryObject<FluidType> SULFURIC_ACID_FLUID_TYPE_REG = VE_FLUID_TYPES.register("sulfuric_acid",
            () -> SulfuricAcid.SULFURIC_ACID_FLUID_TYPE);

    // Dinitrogen Tetroxide
    public static RegistryObject<FlowingFluid> DINITROGEN_TETROXIDE_REG = VE_FLUIDS.register("dinitrogen_tetroxide",
            DinitrogenTetroxide::DinitrogenTetroxideFluid);
    public static RegistryObject<FlowingFluid> FLOWING_DINITROGEN_TETROXIDE_REG = VE_FLUIDS.register("flowing_dinitrogen_tetroxide",
            DinitrogenTetroxide::FlowingDinitrogenTetroxideFluid);
    public static RegistryObject<LiquidBlock> FLOWING_DINITROGEN_TETROXIDE_BLOCK_REG = VE_FLUID_BLOCKS.register("dinitrogen_tetroxide_block",
            DinitrogenTetroxide::FlowingDinitrogenTetroxideBlock);
    public static RegistryObject<Item> DINITROGEN_TETROXIDE_BUCKET_REG = VE_FLUID_ITEMS.register("dinitrogen_tetroxide_bucket",
            DinitrogenTetroxide::DinitrogenTetroxideBucket);
    public static RegistryObject<FluidType> DINITROGEN_TETROXIDE_FLUID_TYPE_REG = VE_FLUID_TYPES.register("dinitrogen_tetroxide",
            () -> DinitrogenTetroxide.DINITROGEN_TETROXIDE_FLUID_TYPE);

    // Compressed Air
    public static RegistryObject<FlowingFluid> COMPRESSED_AIR_REG = VE_FLUIDS.register("compressed_air",
            CompressedAir::CompressedAirFluid);
    public static RegistryObject<FlowingFluid> FLOWING_COMPRESSED_AIR_REG = VE_FLUIDS.register("flowing_compressed_air",
            CompressedAir::FlowingCompressedAirFluid);
    public static RegistryObject<LiquidBlock> FLOWING_COMPRESSED_AIR_BLOCK_REG = VE_FLUID_BLOCKS.register("compressed_air_block",
            CompressedAir::FlowingCompressedAirBlock);
    public static RegistryObject<Item> COMPRESSED_AIR_BUCKET_REG = VE_FLUID_ITEMS.register("compressed_air_bucket",
            CompressedAir::CompressedAirBucket);
    public static RegistryObject<FluidType> COMPRESSED_AIR_FLUID_TYPE_REG = VE_FLUID_TYPES.register("compressed_air",
            () -> CompressedAir.COMPRESSED_AIR_FLUID_TYPE);

    // Nitrogen
    public static RegistryObject<FlowingFluid> NITROGEN_REG = VE_FLUIDS.register("nitrogen",
            Nitrogen::NitrogenFluid);
    public static RegistryObject<FlowingFluid> FLOWING_NITROGEN_REG = VE_FLUIDS.register("flowing_nitrogen",
            Nitrogen::FlowingNitrogenFluid);
    public static RegistryObject<LiquidBlock> FLOWING_NITROGEN_BLOCK_REG = VE_FLUID_BLOCKS.register("nitrogen_block",
            Nitrogen::FlowingNitrogenBlock);
    public static RegistryObject<Item> NITROGEN_BUCKET_REG = VE_FLUID_ITEMS.register("nitrogen_bucket",
            Nitrogen::NitrogenBucket);
    public static RegistryObject<FluidType> NITROGEN_FLUID_TYPE_REG = VE_FLUID_TYPES.register("nitrogen",
            () -> Nitrogen.NITROGEN_FLUID_TYPE);

    // Biofuel
    public static RegistryObject<FlowingFluid> BIOFUEL_REG = VE_FLUIDS.register("biofuel",
            Biofuel::BiofuelFluid);
    public static RegistryObject<FlowingFluid> FLOWING_BIOFUEL_REG = VE_FLUIDS.register("flowing_biofuel",
            Biofuel::FlowingBiofuelFluid);
    public static RegistryObject<LiquidBlock> FLOWING_BIOFUEL_BLOCK_REG = VE_FLUID_BLOCKS.register("biofuel_block",
            Biofuel::FlowingBiofuelBlock);
    public static RegistryObject<Item> BIOFUEL_BUCKET_REG = VE_FLUID_ITEMS.register("biofuel_bucket",
            Biofuel::BiofuelBucket);
    public static RegistryObject<FluidType> BIOFUEL_FLUID_TYPE_REG = VE_FLUID_TYPES.register("biofuel",
            () -> Biofuel.BIOFUEL_FLUID_TYPE);

    // Diesel
    public static RegistryObject<FlowingFluid> DIESEL_REG = VE_FLUIDS.register("diesel",
            Diesel::DieselFluid);
    public static RegistryObject<FlowingFluid> FLOWING_DIESEL_REG = VE_FLUIDS.register("flowing_diesel",
            Diesel::FlowingDieselFluid);
    public static RegistryObject<LiquidBlock> FLOWING_DIESEL_BLOCK_REG = VE_FLUID_BLOCKS.register("diesel_block",
            Diesel::FlowingDieselBlock);
    public static RegistryObject<Item> DIESEL_BUCKET_REG = VE_FLUID_ITEMS.register("diesel_bucket",
            Diesel::DieselBucket);
    public static RegistryObject<FluidType> DIESEL_FLUID_TYPE_REG = VE_FLUID_TYPES.register("diesel",
            () -> Diesel.DIESEL_FLUID_TYPE);

    // Gasoline
    public static RegistryObject<FlowingFluid> GASOLINE_REG = VE_FLUIDS.register("gasoline",
            Gasoline::GasolineFluid);
    public static RegistryObject<FlowingFluid> FLOWING_GASOLINE_REG = VE_FLUIDS.register("flowing_gasoline",
            Gasoline::FlowingGasolineFluid);
    public static RegistryObject<LiquidBlock> FLOWING_GASOLINE_BLOCK_REG = VE_FLUID_BLOCKS.register("gasoline_block",
            Gasoline::FlowingGasolineBlock);
    public static RegistryObject<Item> GASOLINE_BUCKET_REG = VE_FLUID_ITEMS.register("gasoline_bucket",
            Gasoline::GasolineBucket);
    public static RegistryObject<FluidType> GASOLINE_FLUID_TYPE_REG = VE_FLUID_TYPES.register("gasoline",
            () -> Gasoline.GASOLINE_FLUID_TYPE);

    // Nitroglycerin
    public static RegistryObject<FlowingFluid> NITROGLYCERIN_REG = VE_FLUIDS.register("nitroglycerin",
            Nitroglycerin::NitroglycerinFluid);
    public static RegistryObject<FlowingFluid> FLOWING_NITROGLYCERIN_REG = VE_FLUIDS.register("flowing_nitroglycerin",
            Nitroglycerin::FlowingNitroglycerinFluid);
    public static RegistryObject<LiquidBlock> FLOWING_NITROGLYCERIN_BLOCK_REG = VE_FLUID_BLOCKS.register("nitroglycerin_block",
            Nitroglycerin::FlowingNitroglycerinBlock);
    public static RegistryObject<Item> NITROGLYCERIN_BUCKET_REG = VE_FLUID_ITEMS.register("nitroglycerin_bucket",
            Nitroglycerin::NitroglycerinBucket);
    public static RegistryObject<FluidType> NITROGLYCERIN_FLUID_TYPE_REG = VE_FLUID_TYPES.register("nitroglycerin",
            () -> Nitroglycerin.NITROGLYCERIN_FLUID_TYPE);

    // Light Fuel
    public static RegistryObject<FlowingFluid> LIGHT_FUEL_REG = VE_FLUIDS.register("light_fuel",
            LightFuel::LightFuelFluid);
    public static RegistryObject<FlowingFluid> FLOWING_LIGHT_FUEL_REG = VE_FLUIDS.register("flowing_light_fuel",
            LightFuel::FlowingLightFuelFluid);
    public static RegistryObject<LiquidBlock> FLOWING_LIGHT_FUEL_BLOCK_REG = VE_FLUID_BLOCKS.register("light_fuel_block",
            LightFuel::FlowingLightFuelBlock);
    public static RegistryObject<Item> LIGHT_FUEL_BUCKET_REG = VE_FLUID_ITEMS.register("light_fuel_bucket",
            LightFuel::LightFuelBucket);
    public static RegistryObject<FluidType> LIGHT_FUEL_FLUID_TYPE_REG = VE_FLUID_TYPES.register("light_fuel",
            () -> LightFuel.LIGHT_FUEL_FLUID_TYPE);

    // Liquefied Coal
    public static RegistryObject<FlowingFluid> LIQUEFIED_COAL_REG = VE_FLUIDS.register("liquefied_coal",
            LiquefiedCoal::LiquefiedCoalFluid);
    public static RegistryObject<FlowingFluid> FLOWING_LIQUEFIED_COAL_REG = VE_FLUIDS.register("flowing_liquefied_coal",
            LiquefiedCoal::FlowingLiquefiedCoalFluid);
    public static RegistryObject<LiquidBlock> FLOWING_LIQUEFIED_COAL_BLOCK_REG = VE_FLUID_BLOCKS.register("liquefied_coal_block",
            LiquefiedCoal::FlowingLiquefiedCoalBlock);
    public static RegistryObject<Item> LIQUEFIED_COAL_BUCKET_REG = VE_FLUID_ITEMS.register("liquefied_coal_bucket",
            LiquefiedCoal::LiquefiedCoalBucket);
    public static RegistryObject<FluidType> LIQUEFIED_COAL_TYPE_REG = VE_FLUID_TYPES.register("liquefied_coal",
            () -> LiquefiedCoal.LIQUEFIED_COAL_FLUID_TYPE);

    // Liquefied Coke
    public static RegistryObject<FlowingFluid> LIQUEFIED_COKE_REG = VE_FLUIDS.register("liquefied_coke",
            LiquefiedCoke::LiquefiedCokeFluid);
    public static RegistryObject<FlowingFluid> FLOWING_LIQUEFIED_COKE_REG = VE_FLUIDS.register("flowing_liquefied_coke",
            LiquefiedCoke::FlowingLiquefiedCokeFluid);
    public static RegistryObject<LiquidBlock> FLOWING_LIQUEFIED_COKE_BLOCK_REG = VE_FLUID_BLOCKS.register("liquefied_coke_block",
            LiquefiedCoke::FlowingLiquefiedCokeBlock);
    public static RegistryObject<Item> LIQUEFIED_COKE_BUCKET_REG = VE_FLUID_ITEMS.register("liquefied_coke_bucket",
            LiquefiedCoke::LiquefiedCokeBucket);
    public static RegistryObject<FluidType> LIQUEFIED_COKE_FLUID_TYPE_REG = VE_FLUID_TYPES.register("liquefied_coke",
            () -> LiquefiedCoke.LIQUEFIED_COKE_FLUID_TYPE);

    // Tree Sap
    public static RegistryObject<FlowingFluid> TREE_SAP_REG = VE_FLUIDS.register("tree_sap",
            TreeSap::TreeSapFluid);
    public static RegistryObject<FlowingFluid> FLOWING_TREE_SAP_REG = VE_FLUIDS.register("flowing_tree_sap",
            TreeSap::FlowingTreeSapFluid);
    public static RegistryObject<LiquidBlock> FLOWING_TREE_SAP_BLOCK_REG = VE_FLUID_BLOCKS.register("tree_sap_block",
            TreeSap::FlowingTreeSapBlock);
    public static RegistryObject<Item> TREE_SAP_BUCKET_REG = VE_FLUID_ITEMS.register("tree_sap_bucket",
            TreeSap::TreeSapBucket);
    public static RegistryObject<FluidType> TREE_SAP_FLUID_TYPE_REG = VE_FLUID_TYPES.register("tree_sap",
            () -> TreeSap.TREE_SAP_FLUID_TYPE);

    // Treethanol
    public static RegistryObject<FlowingFluid> TREETHANOL_REG = VE_FLUIDS.register("treethanol",
            Treethanol::TreethanolFluid);
    public static RegistryObject<FlowingFluid> FLOWING_TREETHANOL_REG = VE_FLUIDS.register("flowing_treethanol",
            Treethanol::FlowingTreethanolFluid);
    public static RegistryObject<LiquidBlock> FLOWING_TREETHANOL_BLOCK_REG = VE_FLUID_BLOCKS.register("treethanol_block",
            Treethanol::FlowingTreethanolBlock);
    public static RegistryObject<Item> TREETHANOL_BUCKET_REG = VE_FLUID_ITEMS.register("treethanol_bucket",
            Treethanol::TreethanolBucket);
    public static RegistryObject<FluidType> TREETHANOL_FLUID_TYPE_REG = VE_FLUID_TYPES.register("treethanol",
            () -> Treethanol.TREETHANOL_FLUID_TYPE);
}
