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

public class Hydrogen {
    public static final ResourceLocation HYDROGEN_STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/hydrogen_still");
    public static final ResourceLocation HYDROGEN_FLOWING_TEXTURE = ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/hydrogen_flowing");

    public static Block.Properties stdProp = Block.Properties.of().noCollission().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).air();

    public static FlowingFluid HYDROGEN;
    public static FlowingFluid FLOWING_HYDROGEN;
    public static VEFlowingFluidBlock HYDROGEN_BLOCK;
    public static Item HYDROGEN_BUCKET;

    public static FlowingFluid HydrogenFluid() {
        HYDROGEN = new VEFlowingGasFluid.Source(Hydrogen.properties, 4);
        return HYDROGEN;
    }

    public static FlowingFluid FlowingHydrogenFluid() {
        FLOWING_HYDROGEN = new VEFlowingGasFluid.Flowing(Hydrogen.properties, 4);
        return FLOWING_HYDROGEN;
    }

    public static VEFlowingFluidBlock FlowingHydrogenBlock() {
        HYDROGEN_BLOCK = new VEFlowingFluidBlock(HYDROGEN, stdProp);
        return HYDROGEN_BLOCK;
    }

    public static Item HydrogenBucket() {
        HYDROGEN_BUCKET = new BucketItem(HYDROGEN, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        return HYDROGEN_BUCKET;
    }

    public static final VEFluidType HYDROGEN_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            HYDROGEN_STILL_TEXTURE,
            HYDROGEN_FLOWING_TEXTURE
    ).setCeilingFlush(true);

    public static final BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(() -> HYDROGEN_FLUID_TYPE, () -> HYDROGEN, () -> FLOWING_HYDROGEN)
            .block(() -> HYDROGEN_BLOCK).bucket(() -> HYDROGEN_BUCKET);
}
