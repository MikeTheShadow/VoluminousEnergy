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

public class Nitroglycerin {
    public static final ResourceLocation NITROGLYCERIN_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/nitroglycerin_still");
    public static final ResourceLocation NITROGLYCERIN_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/nitroglycerin_flowing");

    public static Block.Properties stdProp = Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops();

    public static FlowingFluid NITROGLYCERIN;
    public static FlowingFluid FLOWING_NITROGLYCERIN;
    public static VEFlowingFluidBlock NITROGLYCERIN_BLOCK;
    public static Item NITROGLYCERIN_BUCKET;

    public static FlowingFluid NitroglycerinFluid(){
        NITROGLYCERIN = new ForgeFlowingFluid.Source(Nitroglycerin.properties);
        return NITROGLYCERIN;
    }

    public static FlowingFluid FlowingNitroglycerinFluid(){
        FLOWING_NITROGLYCERIN = new ForgeFlowingFluid.Flowing(Nitroglycerin.properties);
        return FLOWING_NITROGLYCERIN;
    }

    public static VEFlowingFluidBlock FlowingNitroglycerinBlock(){
        NITROGLYCERIN_BLOCK = new VEFlowingFluidBlock(() -> NITROGLYCERIN, stdProp);
        return NITROGLYCERIN_BLOCK;
    }

    public static Item NitroglycerinBucket(){
        NITROGLYCERIN_BUCKET = new BucketItem(() -> NITROGLYCERIN, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(VESetup.itemGroup));
        return NITROGLYCERIN_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> NITROGLYCERIN, () -> FLOWING_NITROGLYCERIN, FluidAttributes.builder(NITROGLYCERIN_STILL_TEXTURE, NITROGLYCERIN_FLOWING_TEXTURE).viscosity(5))
                    .bucket(() -> NITROGLYCERIN_BUCKET).block(() -> NITROGLYCERIN_BLOCK);
}

