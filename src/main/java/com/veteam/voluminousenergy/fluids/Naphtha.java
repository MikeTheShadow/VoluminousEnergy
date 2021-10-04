package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
import com.veteam.voluminousenergy.items.VENoPlaceBucket;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class Naphtha {
    public static final ResourceLocation NAPHTHA_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/naphtha_still");
    public static final ResourceLocation NAPHTHA_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/naphtha_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();


    public static FlowingFluid NAPHTHA;
    public static FlowingFluid FLOWING_NAPHTHA;
    public static VEFlowingFluidBlock NAPHTHA_BLOCK;
    public static Item NAPHTHA_BUCKET;

    public static FlowingFluid NaphthaFluid(){
        NAPHTHA = new ForgeFlowingFluid.Source(Naphtha.properties);
        return NAPHTHA;
    }

    public static FlowingFluid FlowingNaphthaFluid(){
        FLOWING_NAPHTHA = new ForgeFlowingFluid.Flowing(Naphtha.properties);
        return FLOWING_NAPHTHA;
    }

    public static VEFlowingFluidBlock FlowingNaphthaBlock(){
        NAPHTHA_BLOCK = new VEFlowingFluidBlock(() -> NAPHTHA, stdProp);
        return NAPHTHA_BLOCK;
    }

    public static Item NaphthaBucket(){
        NAPHTHA_BUCKET = new VENoPlaceBucket(() -> NAPHTHA, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return NAPHTHA_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> NAPHTHA, () -> FLOWING_NAPHTHA, FluidAttributes.builder(NAPHTHA_STILL_TEXTURE, NAPHTHA_FLOWING_TEXTURE))
                    .bucket(() -> NAPHTHA_BUCKET).block(() -> NAPHTHA_BLOCK);
}
