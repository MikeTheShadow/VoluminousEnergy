package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
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

    public static Block.Properties stdProp = Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops();

    public static FlowingFluid CRUDE_OIL;
    public static FlowingFluid FLOWING_CRUDE_OIL;
    public static FlowingFluidBlock CRUDE_OIL_BLOCK;
    public static Item CRUDE_OIL_BUCKET;

    public static FlowingFluid CrudeOilFluid(){
        CRUDE_OIL = new ForgeFlowingFluid.Source(CrudeOil.properties);
        return CRUDE_OIL;
    }

    public static FlowingFluid FlowingCrudeOilFluid(){
        FLOWING_CRUDE_OIL = new ForgeFlowingFluid.Flowing(CrudeOil.properties);
        return FLOWING_CRUDE_OIL;
    }

    public static FlowingFluidBlock FlowingCrudeOilBlock(){
        CRUDE_OIL_BLOCK = new FlowingFluidBlock(() -> CRUDE_OIL, stdProp);
        return CRUDE_OIL_BLOCK;
    }

    public static Item CrudeOilBucket(){
        CRUDE_OIL_BUCKET = new BucketItem(() -> CRUDE_OIL, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(VESetup.itemGroup));
        return CRUDE_OIL_BUCKET;
    }

    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> CRUDE_OIL, () -> FLOWING_CRUDE_OIL, FluidAttributes.builder(CRUDE_OIL_STILL_TEXTURE, CRUDE_OIL_FLOWING_TEXTURE).viscosity(5))
                    .bucket(() -> CRUDE_OIL_BUCKET).block(() -> CRUDE_OIL_BLOCK);
}
