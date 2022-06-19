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

public class LightFuel {
    public static final ResourceLocation LIGHT_FUEL_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/light_fuel_still");
    public static final ResourceLocation LIGHT_FUEL_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/light_fuel_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable();

    public static FlowingFluid LIGHT_FUEL;
    public static FlowingFluid FLOWING_LIGHT_FUEL;
    public static VEFlowingFluidBlock LIGHT_FUEL_BLOCK;
    public static Item LIGHT_FUEL_BUCKET;

    public static FlowingFluid LightFuelFluid(){
        LIGHT_FUEL = new VEFlowingGasFluid.Source(LightFuel.properties,4);
        return LIGHT_FUEL;
    }

    public static FlowingFluid FlowingLightFuelFluid(){
        FLOWING_LIGHT_FUEL = new VEFlowingGasFluid.Flowing(LightFuel.properties,4);
        return FLOWING_LIGHT_FUEL;
    }

    public static VEFlowingFluidBlock FlowingLightFuelBlock(){
        LIGHT_FUEL_BLOCK = new VEFlowingFluidBlock(() -> LIGHT_FUEL, stdProp);
        return LIGHT_FUEL_BLOCK;
    }

    public static Item LightFuelBucket(){
        LIGHT_FUEL_BUCKET = new BucketItem(() -> LIGHT_FUEL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return LIGHT_FUEL_BUCKET;
    }


    public static final FluidType LIGHT_FUEL_FLUID_TYPE = new FluidType(FluidType.Properties.create()
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

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> LIGHT_FUEL_FLUID_TYPE, () -> LIGHT_FUEL, () -> FLOWING_LIGHT_FUEL)
            .block(() -> LIGHT_FUEL_BLOCK).bucket(() -> LIGHT_FUEL_BUCKET);
}

