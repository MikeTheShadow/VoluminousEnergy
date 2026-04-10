package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class Diesel {
    public static final Identifier DIESEL_STILL_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/diesel_still");
    public static final Identifier DIESEL_FLOWING_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/diesel_flowing");

    public static Block.Properties stdProp = Block.Properties.of().noCollission().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid();

    public static FlowingFluid DIESEL;
    public static FlowingFluid FLOWING_DIESEL;
    public static VEFlowingFluidBlock DIESEL_BLOCK;
    public static Item DIESEL_BUCKET;

    public static FlowingFluid DieselFluid() {
        DIESEL = new BaseFlowingFluid.Source(Diesel.properties);
        return DIESEL;
    }

    public static FlowingFluid FlowingDieselFluid() {
        FLOWING_DIESEL = new BaseFlowingFluid.Flowing(Diesel.properties);
        return FLOWING_DIESEL;
    }

    public static VEFlowingFluidBlock FlowingDieselBlock() {
        DIESEL_BLOCK = new VEFlowingFluidBlock(DIESEL, stdProp);
        return DIESEL_BLOCK;
    }

    public static Item DieselBucket() {
        DIESEL_BUCKET = new BucketItem(DIESEL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        return DIESEL_BUCKET;
    }


    public static final VEFluidType DIESEL_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
            .adjacentPathType(PathType.WATER)
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

    public static final BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(() -> DIESEL_FLUID_TYPE, () -> DIESEL, () -> FLOWING_DIESEL)
            .block(() -> DIESEL_BLOCK).bucket(() -> DIESEL_BUCKET);
}

