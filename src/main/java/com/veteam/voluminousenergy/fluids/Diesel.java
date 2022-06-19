package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
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

public class Diesel {
    public static final ResourceLocation DIESEL_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/diesel_still");
    public static final ResourceLocation DIESEL_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/diesel_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable();

    public static FlowingFluid DIESEL;
    public static FlowingFluid FLOWING_DIESEL;
    public static VEFlowingFluidBlock DIESEL_BLOCK;
    public static Item DIESEL_BUCKET;

    public static FlowingFluid DieselFluid(){
        DIESEL = new ForgeFlowingFluid.Source(Diesel.properties);
        return DIESEL;
    }

    public static FlowingFluid FlowingDieselFluid(){
        FLOWING_DIESEL = new ForgeFlowingFluid.Flowing(Diesel.properties);
        return FLOWING_DIESEL;
    }

    public static VEFlowingFluidBlock FlowingDieselBlock(){
        DIESEL_BLOCK = new VEFlowingFluidBlock(() -> DIESEL, stdProp);
        return DIESEL_BLOCK;
    }

    public static Item DieselBucket(){
        DIESEL_BUCKET = new BucketItem(() -> DIESEL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return DIESEL_BUCKET;
    }


    public static final FluidType DIESEL_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY),
            DIESEL_STILL_TEXTURE,
            DIESEL_FLOWING_TEXTURE
    );

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> DIESEL_FLUID_TYPE, () -> DIESEL, () -> FLOWING_DIESEL)
            .block(() -> DIESEL_BLOCK).bucket(() -> DIESEL_BUCKET);
}

