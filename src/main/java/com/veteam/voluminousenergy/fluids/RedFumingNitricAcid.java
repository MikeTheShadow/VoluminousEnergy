package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.FumingAcidFlowingFluidBlock;
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

public class RedFumingNitricAcid {
    public static final ResourceLocation RFNA_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/rfna_still");
    public static final ResourceLocation RFNA_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/rfna_flowing");

    public static Block.Properties stdProp = Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops();

    public static FlowingFluid RED_FUMING_NITRIC_ACID;
    public static FlowingFluid FLOWING_RED_FUMING_NITRIC_ACID;
    public static FumingAcidFlowingFluidBlock RED_FUMING_NITRIC_ACID_BLOCK;
    public static Item RED_FUMING_NITRIC_ACID_BUCKET;

    public static FlowingFluid RedFumingNitricAcidFluid(){
        RED_FUMING_NITRIC_ACID = new ForgeFlowingFluid.Source(RedFumingNitricAcid.properties);
        return RED_FUMING_NITRIC_ACID;
    }

    public static FlowingFluid FlowingRedFumingNitricAcidFluid(){
        FLOWING_RED_FUMING_NITRIC_ACID = new ForgeFlowingFluid.Flowing(RedFumingNitricAcid.properties);
        return FLOWING_RED_FUMING_NITRIC_ACID;
    }

    public static FumingAcidFlowingFluidBlock FlowingRedFumingNitricAcidBlock(){
        RED_FUMING_NITRIC_ACID_BLOCK = new FumingAcidFlowingFluidBlock(() -> RED_FUMING_NITRIC_ACID, stdProp);
        return RED_FUMING_NITRIC_ACID_BLOCK;
    }

    public static Item RedFumingNitricAcidBucket(){
        RED_FUMING_NITRIC_ACID_BUCKET = new BucketItem(() -> RED_FUMING_NITRIC_ACID, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(VESetup.itemGroup));
        return RED_FUMING_NITRIC_ACID_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> RED_FUMING_NITRIC_ACID, () -> FLOWING_RED_FUMING_NITRIC_ACID, FluidAttributes.builder(RFNA_STILL_TEXTURE, RFNA_FLOWING_TEXTURE))
                    .bucket(() -> RED_FUMING_NITRIC_ACID_BUCKET).block(() -> RED_FUMING_NITRIC_ACID_BLOCK);

}