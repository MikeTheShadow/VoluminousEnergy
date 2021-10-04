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

public class Gasoline {
    public static final ResourceLocation GASOLINE_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/gasoline_still");
    public static final ResourceLocation GASOLINE_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/gasoline_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid GASOLINE;
    public static FlowingFluid FLOWING_GASOLINE;
    public static VEFlowingFluidBlock GASOLINE_BLOCK;
    public static Item GASOLINE_BUCKET;

    public static FlowingFluid GasolineFluid(){
        GASOLINE = new ForgeFlowingFluid.Source(Gasoline.properties);
        return GASOLINE;
    }

    public static FlowingFluid FlowingGasolineFluid(){
        FLOWING_GASOLINE = new ForgeFlowingFluid.Flowing(Gasoline.properties);
        return FLOWING_GASOLINE;
    }

    public static VEFlowingFluidBlock FlowingGasolineBlock(){
        GASOLINE_BLOCK = new VEFlowingFluidBlock(() -> GASOLINE, stdProp);
        return GASOLINE_BLOCK;
    }

    public static Item GasolineBucket(){
        GASOLINE_BUCKET = new VENoPlaceBucket(() -> GASOLINE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return GASOLINE_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> GASOLINE, () -> FLOWING_GASOLINE, FluidAttributes.builder(GASOLINE_STILL_TEXTURE, GASOLINE_FLOWING_TEXTURE).viscosity(5))
                    .bucket(() -> GASOLINE_BUCKET).block(() -> GASOLINE_BLOCK);
}

