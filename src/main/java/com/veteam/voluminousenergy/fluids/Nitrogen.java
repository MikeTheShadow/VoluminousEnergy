package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class Nitrogen {
    public static final ResourceLocation NITROGEN_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/nitrogen_still");
    public static final ResourceLocation NITROGEN_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/nitrogen_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid NITROGEN;
    public static FlowingFluid FLOWING_NITROGEN;
    public static LiquidBlock NITROGEN_BLOCK;
    public static Item NITROGEN_BUCKET;

    public static FlowingFluid NitrogenFluid(){
        NITROGEN = new ForgeFlowingFluid.Source(Nitrogen.properties);
        return NITROGEN;
    }

    public static FlowingFluid FlowingNitrogenFluid(){
        FLOWING_NITROGEN = new ForgeFlowingFluid.Flowing(Nitrogen.properties);
        return FLOWING_NITROGEN;
    }

    public static LiquidBlock FlowingNitrogenBlock(){
        NITROGEN_BLOCK = new LiquidBlock(() -> NITROGEN, stdProp);
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
