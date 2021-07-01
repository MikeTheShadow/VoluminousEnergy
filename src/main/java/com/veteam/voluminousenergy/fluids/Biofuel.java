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

public class Biofuel {
    public static final ResourceLocation BIOFUEL_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/biofuel_still");
    public static final ResourceLocation BIOFUEL_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/biofuel_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid BIOFUEL;
    public static FlowingFluid FLOWING_BIOFUEL;
    public static VEFlowingFluidBlock BIOFUEL_BLOCK;
    public static Item BIOFUEL_BUCKET;

    public static FlowingFluid BiofuelFluid(){
        BIOFUEL = new ForgeFlowingFluid.Source(Biofuel.properties);
        return BIOFUEL;
    }

    public static FlowingFluid FlowingBiofuelFluid(){
        FLOWING_BIOFUEL = new ForgeFlowingFluid.Flowing(Biofuel.properties);
        return FLOWING_BIOFUEL;
    }

    public static VEFlowingFluidBlock FlowingBiofuelBlock(){
        BIOFUEL_BLOCK = new VEFlowingFluidBlock(() -> BIOFUEL, stdProp);
        return BIOFUEL_BLOCK;
    }

    public static Item BiofuelBucket(){
        BIOFUEL_BUCKET = new BucketItem(() -> BIOFUEL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return BIOFUEL_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> BIOFUEL, () -> FLOWING_BIOFUEL, FluidAttributes.builder(BIOFUEL_STILL_TEXTURE, BIOFUEL_FLOWING_TEXTURE).viscosity(5))
                    .bucket(() -> BIOFUEL_BUCKET).block(() -> BIOFUEL_BLOCK);
}

