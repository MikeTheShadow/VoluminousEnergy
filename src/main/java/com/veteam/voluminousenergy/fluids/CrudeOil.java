package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.CrudeOilFlowingFluidBlock;
import com.veteam.voluminousenergy.fluids.flowingFluidSource.CrudeOilFlowingFluidSource;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class CrudeOil {
    public static final ResourceLocation CRUDE_OIL_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/crude_oil_still");
    public static final ResourceLocation CRUDE_OIL_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/crude_oil_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid CRUDE_OIL;
    public static FlowingFluid FLOWING_CRUDE_OIL;
    public static CrudeOilFlowingFluidBlock CRUDE_OIL_BLOCK;
    public static Item CRUDE_OIL_BUCKET;

    public static FlowingFluid CrudeOilFluid(){ // Create custom source here to adjust tick rate for spread speed
        CRUDE_OIL = new CrudeOilFlowingFluidSource(CrudeOil.properties);
        return CRUDE_OIL;
    }

    public static FlowingFluid FlowingCrudeOilFluid(){
        FLOWING_CRUDE_OIL = new ForgeFlowingFluid.Flowing(CrudeOil.properties);
        return FLOWING_CRUDE_OIL;
    }

    public static CrudeOilFlowingFluidBlock FlowingCrudeOilBlock(){ // Create a custom block here for block modifications
        CRUDE_OIL_BLOCK = new CrudeOilFlowingFluidBlock(() -> CRUDE_OIL, stdProp);
        return CRUDE_OIL_BLOCK;
    }

    public static Item CrudeOilBucket(){
        CRUDE_OIL_BUCKET = new BucketItem(() -> CRUDE_OIL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return CRUDE_OIL_BUCKET;
    }

    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> CRUDE_OIL, () -> FLOWING_CRUDE_OIL, FluidAttributes.builder(CRUDE_OIL_STILL_TEXTURE, CRUDE_OIL_FLOWING_TEXTURE).density(800).viscosity(10_000))
                    .bucket(() -> CRUDE_OIL_BUCKET).block(() -> CRUDE_OIL_BLOCK);
}
