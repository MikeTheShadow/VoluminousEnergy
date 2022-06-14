package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class Mercury {
    public static final ResourceLocation MERCURY_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/mercury_still");
    public static final ResourceLocation MERCURY_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/mercury_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable();

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
        MERCURY_BUCKET = new BucketItem(() -> MERCURY, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return MERCURY_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> MERCURY, () -> FLOWING_MERCURY, FluidAttributes.builder(MERCURY_STILL_TEXTURE, MERCURY_FLOWING_TEXTURE))
                    .bucket(() -> MERCURY_BUCKET).block(() -> MERCURY_BLOCK);

}