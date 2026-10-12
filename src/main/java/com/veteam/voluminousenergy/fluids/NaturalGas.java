package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
import net.minecraft.resources.ResourceLocation;
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

public class NaturalGas {
    public static final ResourceLocation NATURAL_GAS_STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/natural_gas_still");
    public static final ResourceLocation NATURAL_GAS_FLOWING_TEXTURE = ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/natural_gas_flowing");

    public static Block.Properties stdProp = Block.Properties.of().noCollission().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).air();

    public static FlowingFluid NATURAL_GAS;
    public static FlowingFluid FLOWING_NATURAL_GAS;
    public static VEFlowingFluidBlock NATURAL_GAS_BLOCK;
    public static Item NATURAL_GAS_BUCKET;

    public static FlowingFluid NaturalGasFluid() {
        NATURAL_GAS = new VEFlowingGasFluid.Source(NaturalGas.properties, 4);
        return NATURAL_GAS;
    }

    public static FlowingFluid FlowingNaturalGasFluid() {
        FLOWING_NATURAL_GAS = new VEFlowingGasFluid.Flowing(NaturalGas.properties, 4);
        return FLOWING_NATURAL_GAS;
    }

    public static VEFlowingFluidBlock FlowingNaturalGasBlock() {
        NATURAL_GAS_BLOCK = new VEFlowingFluidBlock(NATURAL_GAS, stdProp);
        return NATURAL_GAS_BLOCK;
    }

    public static Item NaturalGasBucket() {
        NATURAL_GAS_BUCKET = new BucketItem(NATURAL_GAS, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        return NATURAL_GAS_BUCKET;
    }

    public static final VEFluidType NATURAL_GAS_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            NATURAL_GAS_STILL_TEXTURE,
            NATURAL_GAS_FLOWING_TEXTURE
    );

    public static final BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(() -> NATURAL_GAS_FLUID_TYPE, () -> NATURAL_GAS, () -> FLOWING_NATURAL_GAS)
            .block(() -> NATURAL_GAS_BLOCK).bucket(() -> NATURAL_GAS_BUCKET);
}
