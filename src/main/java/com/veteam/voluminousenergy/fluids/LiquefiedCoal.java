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

public class LiquefiedCoal {
    public static final ResourceLocation LIQUEFIED_COAL_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/liquefied_coal_still");
    public static final ResourceLocation LIQUEFIED_COAL_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/liquefied_coal_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid LIQUEFIED_COAL;
    public static FlowingFluid FLOWING_LIQUEFIED_COAL;
    public static VEFlowingFluidBlock LIQUEFIED_COAL_BLOCK;
    public static Item LIQUEFIED_COAL_BUCKET;

    public static FlowingFluid LiquefiedCoalFluid(){
        LIQUEFIED_COAL = new ForgeFlowingFluid.Source(LiquefiedCoal.properties);
        return LIQUEFIED_COAL;
    }

    public static FlowingFluid FlowingLiquefiedCoalFluid(){
        FLOWING_LIQUEFIED_COAL = new ForgeFlowingFluid.Flowing(LiquefiedCoal.properties);
        return FLOWING_LIQUEFIED_COAL;
    }

    public static VEFlowingFluidBlock FlowingLiquefiedCoalBlock(){
        LIQUEFIED_COAL_BLOCK = new VEFlowingFluidBlock(() -> LIQUEFIED_COAL, stdProp);
        return LIQUEFIED_COAL_BLOCK;
    }

    public static Item LiquefiedCoalBucket(){
        LIQUEFIED_COAL_BUCKET = new BucketItem(() -> LIQUEFIED_COAL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return LIQUEFIED_COAL_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> LIQUEFIED_COAL, () -> FLOWING_LIQUEFIED_COAL, FluidAttributes.builder(LIQUEFIED_COAL_STILL_TEXTURE, LIQUEFIED_COAL_FLOWING_TEXTURE).viscosity(5))
                    .bucket(() -> LIQUEFIED_COAL_BUCKET).block(() -> LIQUEFIED_COAL_BLOCK);
}

