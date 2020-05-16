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
    public static RegistryObject<FlowingFluid> OXYGEN = VE_FLUIDS.register("oxygen",
            () -> new ForgeFlowingFluid.Source(Oxygen.properties));

    public static RegistryObject<FlowingFluid> FLOWING_OXYGEN = VE_FLUIDS.register("flowing_oxygen",
            () -> new ForgeFlowingFluid.Flowing(Oxygen.properties));

    public static RegistryObject<FlowingFluidBlock> OXYGEN_BLOCK = VE_FLUID_BLOCKS.register("oxygen_block",
            () -> new FlowingFluidBlock(OXYGEN, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));

    public static RegistryObject<Item> OXYGEN_BUCKET = VE_FLUID_ITEMS.register("oxygen_bucket",
            () -> new BucketItem(OXYGEN, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(VESetup.itemGroup)));

    // Crude Oil
    public static RegistryObject<FlowingFluid> CRUDE_OIL_REG = VE_FLUIDS.register("crude_oil",
            () -> CrudeOil.CrudeOilFluid());
    public static RegistryObject<FlowingFluid> FLOWING_CRUDE_OIL_REG = VE_FLUIDS.register("flowing_crude_oil",
            () -> CrudeOil.FlowingCrudeOilFluid());
    public static RegistryObject<FlowingFluidBlock> FLOWING_CRUDE_OIL_BLOCK_REG = VE_FLUID_BLOCKS.register("crude_oil_block",
            () -> CrudeOil.FlowingCrudeOilBlock());
    public static RegistryObject<Item> CRUDE_OIL_BUCKET_REG = VE_FLUID_ITEMS.register("crude_oil_bucket",
            () -> CrudeOil.CrudeOilBucket());

    // Naptha
    public static RegistryObject<FlowingFluid> NAPHTHA = VE_FLUIDS.register("naphtha",
            () -> new ForgeFlowingFluid.Source(Naphtha.properties));
    public static RegistryObject<FlowingFluid> FLOWING_NAPHTHA = VE_FLUIDS.register("flowing_naphtha",
            () -> new ForgeFlowingFluid.Flowing(Naphtha.properties));
    public static RegistryObject<FlowingFluidBlock> NAPHTHA_BLOCK = VE_FLUID_BLOCKS.register("naphtha_block",
            () -> new FlowingFluidBlock(NAPHTHA, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
    public static RegistryObject<Item> NAPHTHA_BUCKET = VE_FLUID_ITEMS.register("naphtha_bucket",
            () -> new BucketItem(NAPHTHA, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(VESetup.itemGroup)));

    // Red Fuming Nitric Acid
    public static RegistryObject<FlowingFluid> RED_FUMING_NITRIC_ACID = VE_FLUIDS.register("red_fuming_nitric_acid",
            () -> new ForgeFlowingFluid.Source(RedFumingNitricAcid.properties));
    public static RegistryObject<FlowingFluid> FLOWING_RED_FUMING_NITRIC_ACID = VE_FLUIDS.register("flowing_red_fuming_nitric_acid",
            () -> new ForgeFlowingFluid.Flowing(RedFumingNitricAcid.properties));
    public static RegistryObject<FlowingFluidBlock> RED_FUMING_NITRIC_ACID_BLOCK = VE_FLUID_BLOCKS.register("red_fuming_nitric_acid_block",
            () -> new FlowingFluidBlock(RED_FUMING_NITRIC_ACID, Block.Properties.create(Material.LAVA).tickRandomly().doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
    public static RegistryObject<Item> RED_FUMING_NITRIC_ACID_BUCKET = VE_FLUID_ITEMS.register("red_fuming_nitric_acid_bucket",
            () -> new BucketItem(RED_FUMING_NITRIC_ACID, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(VESetup.itemGroup)));

    // White Fuming Nitric Acid
    public static RegistryObject<FlowingFluid> WHITE_FUMING_NITRIC_ACID = VE_FLUIDS.register("white_fuming_nitric_acid",
            () -> new ForgeFlowingFluid.Source(WhiteFumingNitricAcid.properties));
    public static RegistryObject<FlowingFluid> FLOWING_WHITE_FUMING_NITRIC_ACID = VE_FLUIDS.register("flowing_white_fuming_nitric_acid",
            () -> new ForgeFlowingFluid.Flowing(WhiteFumingNitricAcid.properties));
    public static RegistryObject<FlowingFluidBlock> WHITE_FUMING_NITRIC_ACID_BLOCK = VE_FLUID_BLOCKS.register("white_fuming_nitric_acid_block",
            () -> new FlowingFluidBlock(WHITE_FUMING_NITRIC_ACID, Block.Properties.create(Material.LAVA).tickRandomly().doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
    public static RegistryObject<Item> WHITE_FUMING_NITRIC_ACID_BUCKET = VE_FLUID_ITEMS.register("white_fuming_nitric_acid_bucket",
            () -> new BucketItem(WHITE_FUMING_NITRIC_ACID, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(VESetup.itemGroup)));

    // Mercury
    public static RegistryObject<FlowingFluid> MERCURY = VE_FLUIDS.register("mercury",
            () -> new ForgeFlowingFluid.Source(Mercury.properties));
    public static RegistryObject<FlowingFluid> FLOWING_MERCURY = VE_FLUIDS.register("flowing_mercury",
            () -> new ForgeFlowingFluid.Flowing(Mercury.properties));
    public static RegistryObject<FlowingFluidBlock> MERCURY_BLOCK = VE_FLUID_BLOCKS.register("mercury_block",
            () -> new FlowingFluidBlock(MERCURY, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
    public static RegistryObject<Item> MERCURY_BUCKET = VE_FLUID_ITEMS.register("mercury_bucket",
            () -> new BucketItem(MERCURY, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(VESetup.itemGroup)));

    // Sulfuric Acid
    public static RegistryObject<FlowingFluid> SULFURIC_ACID = VE_FLUIDS.register("sulfuric_acid",
            () -> new ForgeFlowingFluid.Source(SulfuricAcid.properties));
    public static RegistryObject<FlowingFluid> FLOWING_SULFURIC_ACID = VE_FLUIDS.register("flowing_sulfuric_acid",
            () -> new ForgeFlowingFluid.Flowing(SulfuricAcid.properties));
    public static RegistryObject<FlowingFluidBlock> SULFURIC_ACID_BLOCK = VE_FLUID_BLOCKS.register("sulfuric_acid_block",
            () -> new FlowingFluidBlock(SULFURIC_ACID, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
    public static RegistryObject<Item> SULFURIC_ACID_BUCKET = VE_FLUID_ITEMS.register("sulfuric_acid_bucket",
            () -> new BucketItem(SULFURIC_ACID, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(VESetup.itemGroup)));
}
