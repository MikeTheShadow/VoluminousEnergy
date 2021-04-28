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

public class LiquefiedCoke {
    public static final ResourceLocation LIQUEFIED_COKE_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/liquefied_coke_still");
    public static final ResourceLocation LIQUEFIED_COKE_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/liquefied_coke_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid LIQUEFIED_COKE;
    public static FlowingFluid FLOWING_LIQUEFIED_COKE;
    public static VEFlowingFluidBlock LIQUEFIED_COKE_BLOCK;
    public static Item LIQUEFIED_COKE_BUCKET;

    public static FlowingFluid LiquefiedCokeFluid(){
        LIQUEFIED_COKE = new ForgeFlowingFluid.Source(LiquefiedCoke.properties);
        return LIQUEFIED_COKE;
    }

    public static FlowingFluid FlowingLiquefiedCokeFluid(){
        FLOWING_LIQUEFIED_COKE = new ForgeFlowingFluid.Flowing(LiquefiedCoke.properties);
        return FLOWING_LIQUEFIED_COKE;
    }

    public static VEFlowingFluidBlock FlowingLiquefiedCokeBlock(){
        LIQUEFIED_COKE_BLOCK = new VEFlowingFluidBlock(() -> LIQUEFIED_COKE, stdProp);
        return LIQUEFIED_COKE_BLOCK;
    }

    public static Item LiquefiedCokeBucket(){
        LIQUEFIED_COKE_BUCKET = new BucketItem(() -> LIQUEFIED_COKE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return LIQUEFIED_COKE_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> LIQUEFIED_COKE, () -> FLOWING_LIQUEFIED_COKE, FluidAttributes.builder(LIQUEFIED_COKE_STILL_TEXTURE, LIQUEFIED_COKE_FLOWING_TEXTURE).viscosity(5))
                    .bucket(() -> LIQUEFIED_COKE_BUCKET).block(() -> LIQUEFIED_COKE_BLOCK);
}

