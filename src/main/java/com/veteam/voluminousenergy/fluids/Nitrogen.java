package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class Nitrogen {
    public static final ResourceLocation NITROGEN_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/nitrogen_still");
    public static final ResourceLocation NITROGEN_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/nitrogen_flowing");
    public static final int NITROGEN_FLUID_WIDTH = 4;

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable();

    public static FlowingFluid NITROGEN;
    public static FlowingFluid FLOWING_NITROGEN;
    public static VEFlowingFluidBlock NITROGEN_BLOCK;
    public static Item NITROGEN_BUCKET;

    public static FlowingFluid NitrogenFluid(){
        NITROGEN = new VEFlowingGasFluid.Source(Nitrogen.properties, NITROGEN_FLUID_WIDTH);
        return NITROGEN;
    }

    public static FlowingFluid FlowingNitrogenFluid(){
        FLOWING_NITROGEN = new VEFlowingGasFluid.Flowing(Nitrogen.properties, NITROGEN_FLUID_WIDTH);
        return FLOWING_NITROGEN;
    }

    public static VEFlowingFluidBlock FlowingNitrogenBlock(){
        NITROGEN_BLOCK = new VEFlowingFluidBlock(() -> NITROGEN, stdProp);
        return NITROGEN_BLOCK;
    }

    public static Item NitrogenBucket(){
        NITROGEN_BUCKET = new BucketItem(() -> NITROGEN, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return NITROGEN_BUCKET;
    }


    public static final FluidType NITROGEN_FLUID_TYPE = new FluidType(FluidType.Properties.create()
            .adjacentPathType(BlockPathTypes.WATER)
            .canConvertToSource(false)
            .canDrown(false)
            .canExtinguish(false)
            .canHydrate(false)
            .canPushEntity(true)
            .canConvertToSource(false)
            .canSwim(false)
            .lightLevel(0)
            .density(1)
            .temperature(300)
            .viscosity(1)
            .motionScale(0.75)
            .fallDistanceModifier(0)
            .rarity(Rarity.COMMON)
            .supportsBoating(false)
            //.sound(,)
    );

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> NITROGEN_FLUID_TYPE, () -> NITROGEN, () -> FLOWING_NITROGEN)
            .block(() -> NITROGEN_BLOCK).bucket(() -> NITROGEN_BUCKET);
}
