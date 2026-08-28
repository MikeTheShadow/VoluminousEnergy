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
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class LightFuel {
    public static final Identifier LIGHT_FUEL_STILL_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/light_fuel_still");
    public static final Identifier LIGHT_FUEL_FLOWING_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/light_fuel_flowing");

    public static Block.Properties stdProp = Block.Properties.of().setId(VERegistryHelper.currentBlockId()).noCollision().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).air();

    public static FlowingFluid LIGHT_FUEL;
    public static FlowingFluid FLOWING_LIGHT_FUEL;
    public static VEFlowingFluidBlock LIGHT_FUEL_BLOCK;
    public static Item LIGHT_FUEL_BUCKET;

    public static FlowingFluid LightFuelFluid() {
        LIGHT_FUEL = new VEFlowingGasFluid.Source(LightFuel.properties, 4);
        return LIGHT_FUEL;
    }

    public static FlowingFluid FlowingLightFuelFluid() {
        FLOWING_LIGHT_FUEL = new VEFlowingGasFluid.Flowing(LightFuel.properties, 4);
        return FLOWING_LIGHT_FUEL;
    }

    public static VEFlowingFluidBlock FlowingLightFuelBlock() {
        LIGHT_FUEL_BLOCK = new VEFlowingFluidBlock(LIGHT_FUEL, stdProp.setId(VERegistryHelper.currentBlockId()));
        return LIGHT_FUEL_BLOCK;
    }

    public static Item LightFuelBucket() {
        LIGHT_FUEL_BUCKET = new BucketItem(LIGHT_FUEL, new Item.Properties().setId(VERegistryHelper.currentItemId()).craftRemainder(Items.BUCKET).stacksTo(1));
        return LIGHT_FUEL_BUCKET;
    }


    public static final VEFluidType LIGHT_FUEL_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
            .adjacentPathType(PathType.LAVA)
            .canConvertToSource(false)
            .canDrown(false)
            .canExtinguish(false)
            .canHydrate(false)
            .canPushEntity(false)
            .canConvertToSource(false)
            .canSwim(false)
            .lightLevel(0)
            .density(0)
            .temperature(300)
            .viscosity(1)
            .motionScale(0)
            .fallDistanceModifier(0)
            .rarity(Rarity.COMMON)
            .supportsBoating(false)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY),
            LIGHT_FUEL_STILL_TEXTURE,
            LIGHT_FUEL_FLOWING_TEXTURE
    );

    public static final BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(() -> LIGHT_FUEL_FLUID_TYPE, () -> LIGHT_FUEL, () -> FLOWING_LIGHT_FUEL)
            .block(() -> LIGHT_FUEL_BLOCK).bucket(() -> LIGHT_FUEL_BUCKET);
}

