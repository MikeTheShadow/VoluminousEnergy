package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.CrudeOilFlowingFluidBlock;
import com.veteam.voluminousenergy.fluids.flowingFluidSource.CrudeOilFlowingFluidSource;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class CrudeOil {
    public static final ResourceLocation CRUDE_OIL_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/crude_oil_still");
    public static final ResourceLocation CRUDE_OIL_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/crude_oil_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable();

    public static FlowingFluid CRUDE_OIL;
    public static FlowingFluid FLOWING_CRUDE_OIL;
    public static CrudeOilFlowingFluidBlock CRUDE_OIL_BLOCK;
    public static Item CRUDE_OIL_BUCKET;

    public static FlowingFluid CrudeOilFluid(){ // Create custom source here to adjust tick rate for spread speed
        CRUDE_OIL = new CrudeOilFlowingFluidSource(CrudeOil.properties);
        return CRUDE_OIL;
    }

    public static FlowingFluid FlowingCrudeOilFluid(){
        FLOWING_CRUDE_OIL = new ForgeFlowingFluid.Flowing(CrudeOil.properties);
        return FLOWING_CRUDE_OIL;
    }

    public static CrudeOilFlowingFluidBlock FlowingCrudeOilBlock(){ // Create a custom block here for block modifications
        CRUDE_OIL_BLOCK = new CrudeOilFlowingFluidBlock(() -> CRUDE_OIL, stdProp);
        return CRUDE_OIL_BLOCK;
    }

    public static Item CrudeOilBucket(){
        CRUDE_OIL_BUCKET = new BucketItem(() -> CRUDE_OIL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return CRUDE_OIL_BUCKET;
    }

    public static final FluidType CRUDE_OIL_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
            .adjacentPathType(BlockPathTypes.WATER)
            .canConvertToSource(false)
            .canDrown(true)
            .canExtinguish(false)
            .canHydrate(false)
            .canPushEntity(false)
            .canConvertToSource(false)
            .canSwim(false)
            .lightLevel(0)
            .density(1)
            .temperature(300)
            .viscosity(1)
            .motionScale(0)
            .fallDistanceModifier(0)
            .rarity(Rarity.COMMON)
            .supportsBoating(false)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.GENERIC_EXPLODE),
            CRUDE_OIL_STILL_TEXTURE,
            CRUDE_OIL_FLOWING_TEXTURE
    );

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> CRUDE_OIL_FLUID_TYPE, () -> CRUDE_OIL, () -> FLOWING_CRUDE_OIL)
            .block(() -> CRUDE_OIL_BLOCK).bucket(() -> CRUDE_OIL_BUCKET);
}
