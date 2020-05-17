package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class VEFluids {
    public static DeferredRegister<Fluid> VE_FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, VoluminousEnergy.MODID);
    public static DeferredRegister<Item> VE_FLUID_ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, VoluminousEnergy.MODID);
    public static DeferredRegister<Block> VE_FLUID_BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, VoluminousEnergy.MODID);

    // Oxygen
    public static RegistryObject<FlowingFluid> OXYGEN_REG = VE_FLUIDS.register("oxygen",
            Oxygen::OxygenFluid);
    public static RegistryObject<FlowingFluid> FLOWING_OXYGEN_REG = VE_FLUIDS.register("flowing_oxygen",
            Oxygen::FlowingOxygenFluid);
    public static RegistryObject<FlowingFluidBlock> FLOWING_OXYGEN_BLOCK_REG = VE_FLUID_BLOCKS.register("oxygen_block",
            Oxygen::FlowingOxygenBlock);
    public static RegistryObject<Item> OXYGEN_BUCKET_REG = VE_FLUID_ITEMS.register("oxygen_bucket",
            Oxygen::OxygenBucket);

    // Crude Oil
    public static RegistryObject<FlowingFluid> CRUDE_OIL_REG = VE_FLUIDS.register("crude_oil",
            CrudeOil::CrudeOilFluid);
    public static RegistryObject<FlowingFluid> FLOWING_CRUDE_OIL_REG = VE_FLUIDS.register("flowing_crude_oil",
            CrudeOil::FlowingCrudeOilFluid);
    public static RegistryObject<FlowingFluidBlock> FLOWING_CRUDE_OIL_BLOCK_REG = VE_FLUID_BLOCKS.register("crude_oil_block",
            CrudeOil::FlowingCrudeOilBlock);
    public static RegistryObject<Item> CRUDE_OIL_BUCKET_REG = VE_FLUID_ITEMS.register("crude_oil_bucket",
            CrudeOil::CrudeOilBucket);

    // Naphtha
    public static RegistryObject<FlowingFluid> NAPHTHA_REG = VE_FLUIDS.register("naphtha",
            Naphtha::NaphthaFluid);
    public static RegistryObject<FlowingFluid> FLOWING_NAPHTHA_REG = VE_FLUIDS.register("flowing_naphtha",
            Naphtha::FlowingNaphthaFluid);
    public static RegistryObject<FlowingFluidBlock> FLOWING_NAPHTHA_BLOCK_REG = VE_FLUID_BLOCKS.register("naphtha_block",
            Naphtha::FlowingNaphthaBlock);
    public static RegistryObject<Item> NAPHTHA_BUCKET_REG = VE_FLUID_ITEMS.register("naphtha_bucket",
            Naphtha::NaphthaBucket);

    // Red Fuming Nitric Acid (Shortened to RFNA)
    public static RegistryObject<FlowingFluid> RFNA_REG = VE_FLUIDS.register("red_fuming_nitric_acid",
            RedFumingNitricAcid::RedFumingNitricAcidFluid);
    public static RegistryObject<FlowingFluid> FLOWING_RFNA_REG = VE_FLUIDS.register("flowing_red_fuming_nitric_acid",
            RedFumingNitricAcid::FlowingRedFumingNitricAcidFluid);
    public static RegistryObject<FlowingFluidBlock> FLOWING_RFNA_BLOCK_REG = VE_FLUID_BLOCKS.register("red_fuming_nitric_acid_block",
            RedFumingNitricAcid::FlowingRedFumingNitricAcidBlock);
    public static RegistryObject<Item> RFNA_BUCKET_REG = VE_FLUID_ITEMS.register("red_fuming_nitric_acid_bucket",
            RedFumingNitricAcid::RedFumingNitricAcidBucket);

    // White Fuming Nitric Acid (Shortened to WFNA)
    public static RegistryObject<FlowingFluid> WFNA_REG = VE_FLUIDS.register("white_fuming_nitric_acid",
            WhiteFumingNitricAcid::WhiteFumingNitricAcidFluid);
    public static RegistryObject<FlowingFluid> FLOWING_WFNA_REG = VE_FLUIDS.register("flowing_white_fuming_nitric_acid",
            WhiteFumingNitricAcid::FlowingWhiteFumingNitricAcidFluid);
    public static RegistryObject<FlowingFluidBlock> FLOWING_WFNA_BLOCK_REG = VE_FLUID_BLOCKS.register("white_fuming_nitric_acid_block",
            WhiteFumingNitricAcid::FlowingWhiteFumingNitricAcidBlock);
    public static RegistryObject<Item> WFNA_BUCKET_REG = VE_FLUID_ITEMS.register("white_fuming_nitric_acid_bucket",
            WhiteFumingNitricAcid::WhiteFumingNitricAcidBucket);

    // Mercury
    public static RegistryObject<FlowingFluid> MERCURY_REG = VE_FLUIDS.register("mercury",
            Mercury::MercuryFluid);
    public static RegistryObject<FlowingFluid> FLOWING_MERCURY_REG = VE_FLUIDS.register("flowing_mercury",
            Mercury::FlowingMercuryFluid);
    public static RegistryObject<FlowingFluidBlock> FLOWING_MERCURY_BLOCK_REG = VE_FLUID_BLOCKS.register("mercury_block",
            Mercury::FlowingMercuryBlock);
    public static RegistryObject<Item> MERCURY_BUCKET_REG = VE_FLUID_ITEMS.register("mercury_bucket",
            Mercury::MercuryBucket);

    // Sulfuric Acid
    public static RegistryObject<FlowingFluid> SULFURIC_ACID_REG = VE_FLUIDS.register("sulfuric_acid",
            SulfuricAcid::SulfuricAcidFluid);
    public static RegistryObject<FlowingFluid> FLOWING_SULFURIC_ACID_REG = VE_FLUIDS.register("flowing_sulfuric_acid",
            SulfuricAcid::FlowingSulfuricAcidFluid);
    public static RegistryObject<FlowingFluidBlock> FLOWING_SULFURIC_ACID_BLOCK_REG = VE_FLUID_BLOCKS.register("sulfuric_acid_block",
            SulfuricAcid::FlowingSulfuricAcidBlock);
    public static RegistryObject<Item> SULFURIC_ACID_BUCKET_REG = VE_FLUID_ITEMS.register("sulfuric_acid_bucket",
            SulfuricAcid::SulfuricAcidBucket);
}
