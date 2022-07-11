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

public class Hydrogen {
    public static final ResourceLocation HYDROGEN_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/hydrogen_still");
    public static final ResourceLocation HYDROGEN_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/hydrogen_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid HYDROGEN;
    public static FlowingFluid FLOWING_HYDROGEN;
    public static VEFlowingFluidBlock HYDROGEN_BLOCK;
    public static Item HYDROGEN_BUCKET;

    public static FlowingFluid HydrogenFluid(){
        HYDROGEN = new VEFlowingGasFluid.Source(Hydrogen.properties,4);
        return HYDROGEN;
    }

    public static FlowingFluid FlowingHydrogenFluid(){
        FLOWING_HYDROGEN = new VEFlowingGasFluid.Flowing(Hydrogen.properties,4);
        return FLOWING_HYDROGEN;
    }

    public static VEFlowingFluidBlock FlowingHydrogenBlock(){
        HYDROGEN_BLOCK = new VEFlowingFluidBlock(() -> HYDROGEN, stdProp);
        return HYDROGEN_BLOCK;
    }

    public static Item HydrogenBucket(){
        HYDROGEN_BUCKET = new BucketItem(() -> HYDROGEN, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return HYDROGEN_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> HYDROGEN, () -> FLOWING_HYDROGEN, FluidAttributes.builder(HYDROGEN_STILL_TEXTURE, HYDROGEN_FLOWING_TEXTURE).viscosity(5).gaseous())
                    .bucket(() -> HYDROGEN_BUCKET).block(() -> HYDROGEN_BLOCK);
}
