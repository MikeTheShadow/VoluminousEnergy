package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
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

public class Diesel {
    public static final ResourceLocation DIESEL_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/diesel_still");
    public static final ResourceLocation DIESEL_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/diesel_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid DIESEL;
    public static FlowingFluid FLOWING_DIESEL;
    public static VEFlowingFluidBlock DIESEL_BLOCK;
    public static Item DIESEL_BUCKET;

    public static FlowingFluid DieselFluid(){
        DIESEL = new ForgeFlowingFluid.Source(Diesel.properties);
        return DIESEL;
    }

    public static FlowingFluid FlowingDieselFluid(){
        FLOWING_DIESEL = new ForgeFlowingFluid.Flowing(Diesel.properties);
        return FLOWING_DIESEL;
    }

    public static VEFlowingFluidBlock FlowingDieselBlock(){
        DIESEL_BLOCK = new VEFlowingFluidBlock(() -> DIESEL, stdProp);
        return DIESEL_BLOCK;
    }

    public static Item DieselBucket(){
        DIESEL_BUCKET = new BucketItem(() -> DIESEL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return DIESEL_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> DIESEL, () -> FLOWING_DIESEL, FluidAttributes.builder(DIESEL_STILL_TEXTURE, DIESEL_FLOWING_TEXTURE).viscosity(5))
                    .bucket(() -> DIESEL_BUCKET).block(() -> DIESEL_BLOCK);
}

