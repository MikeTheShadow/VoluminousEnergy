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

public class Nitrogen {
    public static final ResourceLocation NITROGEN_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/nitrogen_still");
    public static final ResourceLocation NITROGEN_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/nitrogen_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid NITROGEN;
    public static FlowingFluid FLOWING_NITROGEN;
    public static FlowingFluidBlock NITROGEN_BLOCK;
    public static Item NITROGEN_BUCKET;

    public static FlowingFluid NitrogenFluid(){
        NITROGEN = new ForgeFlowingFluid.Source(Nitrogen.properties);
        return NITROGEN;
    }

    public static FlowingFluid FlowingNitrogenFluid(){
        FLOWING_NITROGEN = new ForgeFlowingFluid.Flowing(Nitrogen.properties);
        return FLOWING_NITROGEN;
    }

    public static FlowingFluidBlock FlowingNitrogenBlock(){
        NITROGEN_BLOCK = new FlowingFluidBlock(() -> NITROGEN, stdProp);
        return NITROGEN_BLOCK;
    }

    public static Item NitrogenBucket(){
        NITROGEN_BUCKET = new BucketItem(() -> NITROGEN, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return NITROGEN_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> NITROGEN, () -> FLOWING_NITROGEN, FluidAttributes.builder(NITROGEN_STILL_TEXTURE, NITROGEN_FLOWING_TEXTURE).viscosity(5).gaseous())
                    .bucket(() -> NITROGEN_BUCKET).block(() -> NITROGEN_BLOCK);
}
