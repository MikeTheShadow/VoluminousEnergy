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

public class WhiteFumingNitricAcid {
    public static final ResourceLocation WFNA_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/wfna_still");
    public static final ResourceLocation WFNA_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/wfna_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid WHITE_FUMING_NITRIC_ACID;
    public static FlowingFluid FLOWING_WHITE_FUMING_NITRIC_ACID;
    public static FumingAcidFlowingFluidBlock WHITE_FUMING_NITRIC_ACID_BLOCK;
    public static Item WHITE_FUMING_NITRIC_ACID_BUCKET;

    public static FlowingFluid WhiteFumingNitricAcidFluid(){
        WHITE_FUMING_NITRIC_ACID = new ForgeFlowingFluid.Source(WhiteFumingNitricAcid.properties);
        return WHITE_FUMING_NITRIC_ACID;
    }

    public static FlowingFluid FlowingWhiteFumingNitricAcidFluid(){
        FLOWING_WHITE_FUMING_NITRIC_ACID = new ForgeFlowingFluid.Flowing(WhiteFumingNitricAcid.properties);
        return FLOWING_WHITE_FUMING_NITRIC_ACID;
    }

    public static FumingAcidFlowingFluidBlock FlowingWhiteFumingNitricAcidBlock(){
        WHITE_FUMING_NITRIC_ACID_BLOCK = new FumingAcidFlowingFluidBlock(() -> WHITE_FUMING_NITRIC_ACID, stdProp);
        return WHITE_FUMING_NITRIC_ACID_BLOCK;
    }

    public static Item WhiteFumingNitricAcidBucket(){
        WHITE_FUMING_NITRIC_ACID_BUCKET = new BucketItem(() -> WHITE_FUMING_NITRIC_ACID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return WHITE_FUMING_NITRIC_ACID_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> WHITE_FUMING_NITRIC_ACID, () -> FLOWING_WHITE_FUMING_NITRIC_ACID, FluidAttributes.builder(WFNA_STILL_TEXTURE, WFNA_FLOWING_TEXTURE))
                    .bucket(() -> WHITE_FUMING_NITRIC_ACID_BUCKET).block(() -> WHITE_FUMING_NITRIC_ACID_BLOCK);

}