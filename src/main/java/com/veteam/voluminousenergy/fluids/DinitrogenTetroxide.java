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

public class DinitrogenTetroxide {
    public static final ResourceLocation DINITROGEN_TETROXIDE_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/dinitrogen_tetroxide_still");
    public static final ResourceLocation DINITROGEN_TETROXIDE_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/dinitrogen_tetroxide_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid DINITROGEN_TETROXIDE;
    public static FlowingFluid FLOWING_DINITROGEN_TETROXIDE;
    public static VEFlowingFluidBlock DINITROGEN_TETROXIDE_BLOCK;
    public static Item DINITROGEN_TETROXIDE_BUCKET;

    public static FlowingFluid DinitrogenTetroxideFluid(){
        DINITROGEN_TETROXIDE = new ForgeFlowingFluid.Source(DinitrogenTetroxide.properties);
        return DINITROGEN_TETROXIDE;
    }

    public static FlowingFluid FlowingDinitrogenTetroxideFluid(){
        FLOWING_DINITROGEN_TETROXIDE = new ForgeFlowingFluid.Flowing(DinitrogenTetroxide.properties);
        return FLOWING_DINITROGEN_TETROXIDE;
    }

    public static VEFlowingFluidBlock FlowingDinitrogenTetroxideBlock(){
        DINITROGEN_TETROXIDE_BLOCK = new VEFlowingFluidBlock(() -> DINITROGEN_TETROXIDE, stdProp);
        return DINITROGEN_TETROXIDE_BLOCK;
    }

    public static Item DinitrogenTetroxideBucket(){
        DINITROGEN_TETROXIDE_BUCKET = new BucketItem(() -> DINITROGEN_TETROXIDE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return DINITROGEN_TETROXIDE_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> DINITROGEN_TETROXIDE, () -> FLOWING_DINITROGEN_TETROXIDE, FluidAttributes.builder(DINITROGEN_TETROXIDE_STILL_TEXTURE, DINITROGEN_TETROXIDE_FLOWING_TEXTURE).viscosity(5))
                    .bucket(() -> DINITROGEN_TETROXIDE_BUCKET).block(() -> DINITROGEN_TETROXIDE_BLOCK);
}
