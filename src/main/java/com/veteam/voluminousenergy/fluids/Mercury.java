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

public class Mercury {
    public static final ResourceLocation MERCURY_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/mercury_still");
    public static final ResourceLocation MERCURY_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/mercury_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable();

    public static FlowingFluid MERCURY;
    public static FlowingFluid FLOWING_MERCURY;
    public static VEFlowingFluidBlock MERCURY_BLOCK;
    public static Item MERCURY_BUCKET;

    public static FlowingFluid MercuryFluid(){
        MERCURY = new ForgeFlowingFluid.Source(Mercury.properties);
        return MERCURY;
    }

    public static FlowingFluid FlowingMercuryFluid(){
        FLOWING_MERCURY = new ForgeFlowingFluid.Flowing(Mercury.properties);
        return FLOWING_MERCURY;
    }

    public static VEFlowingFluidBlock FlowingMercuryBlock(){
        MERCURY_BLOCK = new VEFlowingFluidBlock(() -> MERCURY, stdProp);
        return MERCURY_BLOCK;
    }

    public static Item MercuryBucket(){
        MERCURY_BUCKET = new BucketItem(() -> MERCURY, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return MERCURY_BUCKET;
    }


    public static final FluidType MERCURY_FLUID_TYPE = new FluidType(FluidType.Properties.create()
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

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> MERCURY_FLUID_TYPE, () -> MERCURY, () -> FLOWING_MERCURY)
            .block(() -> MERCURY_BLOCK).bucket(() -> MERCURY_BUCKET);

}