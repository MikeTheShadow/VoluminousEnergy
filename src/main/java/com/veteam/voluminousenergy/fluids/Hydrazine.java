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

public class Hydrazine {
    public static final ResourceLocation HYDRAZINE_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/hydrazine_still");
    public static final ResourceLocation HYDRAZINE_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/hydrazine_flowing");

    public static Block.Properties stdProp = Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops();

    public static FlowingFluid HYDRAZINE;
    public static FlowingFluid FLOWING_HYDRAZINE;
    public static FlowingFluidBlock HYDRAZINE_BLOCK;
    public static Item HYDRAZINE_BUCKET;

    public static FlowingFluid HydrazineFluid(){
        HYDRAZINE = new ForgeFlowingFluid.Source(Hydrazine.properties);
        return HYDRAZINE;
    }

    public static FlowingFluid FlowingHydrazineFluid(){
        FLOWING_HYDRAZINE = new ForgeFlowingFluid.Flowing(Hydrazine.properties);
        return FLOWING_HYDRAZINE;
    }

    public static FlowingFluidBlock FlowingHydrazineBlock(){
        HYDRAZINE_BLOCK = new FlowingFluidBlock(() -> HYDRAZINE, stdProp);
        return HYDRAZINE_BLOCK;
    }

    public static Item HydrazineBucket(){
        HYDRAZINE_BUCKET = new BucketItem(() -> HYDRAZINE, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(VESetup.itemGroup));
        return HYDRAZINE_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> HYDRAZINE, () -> FLOWING_HYDRAZINE, FluidAttributes.builder(HYDRAZINE_STILL_TEXTURE, HYDRAZINE_FLOWING_TEXTURE).viscosity(5))
                    .bucket(() -> HYDRAZINE_BUCKET).block(() -> HYDRAZINE_BLOCK);
}

