package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class Gasoline {
    public static final ResourceLocation GASOLINE_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/gasoline_still");
    public static final ResourceLocation GASOLINE_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/gasoline_flowing");

    public static Block.Properties stdProp = Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops();

    public static FlowingFluid GASOLINE;
    public static FlowingFluid FLOWING_GASOLINE;
    public static FlowingFluidBlock GASOLINE_BLOCK;
    public static Item GASOLINE_BUCKET;

    public static FlowingFluid GasolineFluid(){
        GASOLINE = new ForgeFlowingFluid.Source(Gasoline.properties);
        return GASOLINE;
    }

    public static FlowingFluid FlowingGasolineFluid(){
        FLOWING_GASOLINE = new ForgeFlowingFluid.Flowing(Gasoline.properties);
        return FLOWING_GASOLINE;
    }

    public static FlowingFluidBlock FlowingGasolineBlock(){
        GASOLINE_BLOCK = new FlowingFluidBlock(() -> GASOLINE, stdProp);
        return GASOLINE_BLOCK;
    }

    public static Item GasolineBucket(){
        GASOLINE_BUCKET = new BucketItem(() -> GASOLINE, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(VESetup.itemGroup));
        return GASOLINE_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> GASOLINE, () -> FLOWING_GASOLINE, FluidAttributes.builder(GASOLINE_STILL_TEXTURE, GASOLINE_FLOWING_TEXTURE).viscosity(5))
                    .bucket(() -> GASOLINE_BUCKET).block(() -> GASOLINE_BLOCK);
}

