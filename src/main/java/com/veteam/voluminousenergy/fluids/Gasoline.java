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

public class Gasoline {
    public static final ResourceLocation GASOLINE_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/gasoline_still");
    public static final ResourceLocation GASOLINE_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/gasoline_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable();

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
        GASOLINE_BUCKET = new BucketItem(() -> GASOLINE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return GASOLINE_BUCKET;
    }


    public static final FluidType GASOLINE_FLUID_TYPE = new FluidType(FluidType.Properties.create()
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

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> GASOLINE_FLUID_TYPE, () -> GASOLINE, () -> FLOWING_GASOLINE)
            .block(() -> GASOLINE_BLOCK).bucket(() -> GASOLINE_BUCKET);
}

