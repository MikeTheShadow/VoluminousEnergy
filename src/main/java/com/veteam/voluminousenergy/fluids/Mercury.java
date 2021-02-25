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

public class Mercury {
    public static final ResourceLocation MERCURY_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/mercury_still");
    public static final ResourceLocation MERCURY_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/mercury_flowing");

    public static Block.Properties stdProp = Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops();

    public static FlowingFluid MERCURY;
    public static FlowingFluid FLOWING_MERCURY;
    public static VEFlowingFluidBlock MERCURY_BLOCK;
    public static Item MERCURY_BUCKET;

    public static FlowingFluid MercuryFluid(){
        MERCURY = new ForgeFlowingFluid.Source(Mercury.properties);
        return MERCURY;
    }

    public static FlowingFluid FlowingMercuryFluid(){
        FLOWING_MERCURY = new ForgeFlowingFluid.Flowing(Mercury.properties);
        return FLOWING_MERCURY;
    }

    public static VEFlowingFluidBlock FlowingMercuryBlock(){
        MERCURY_BLOCK = new VEFlowingFluidBlock(() -> MERCURY, stdProp);
        return MERCURY_BLOCK;
    }

    public static Item MercuryBucket(){
        MERCURY_BUCKET = new BucketItem(() -> MERCURY, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(VESetup.itemGroup));
        return MERCURY_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> MERCURY, () -> FLOWING_MERCURY, FluidAttributes.builder(MERCURY_STILL_TEXTURE, MERCURY_FLOWING_TEXTURE))
                    .bucket(() -> MERCURY_BUCKET).block(() -> MERCURY_BLOCK);

}