package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
import com.veteam.voluminousenergy.items.AmmoniumNitrateBucket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class AmmoniumNitrateSolution {
    public static final ResourceLocation AMMONIUM_NITRATE_SOLUTION_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"block/fluids/ammonium_nitrate_solution_still");
    public static final ResourceLocation AMMONIUM_NITRATE_SOLUTION_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"block/fluids/ammonium_nitrate_solution_flowing");

    public static Block.Properties stdProp = Block.Properties.of().noCollission().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid();

    public static FlowingFluid AMMONIUM_NITRATE_SOLUTION;
    public static FlowingFluid FLOWING_AMMONIUM_NITRATE_SOLUTION;
    public static VEFlowingFluidBlock AMMONIUM_NITRATE_SOLUTION_BLOCK;
    public static Item AMMONIUM_NITRATE_SOLUTION_BUCKET;

    public static FlowingFluid AmmoniumNitrateSolutionFluid(){
        AMMONIUM_NITRATE_SOLUTION = new ForgeFlowingFluid.Source(AmmoniumNitrateSolution.PROPERTIES);
        return AMMONIUM_NITRATE_SOLUTION;
    }

    public static FlowingFluid FlowingAmmoniumNitrateSolutionFluid(){
        FLOWING_AMMONIUM_NITRATE_SOLUTION = new ForgeFlowingFluid.Flowing(AmmoniumNitrateSolution.PROPERTIES);
        return FLOWING_AMMONIUM_NITRATE_SOLUTION;
    }

    public static VEFlowingFluidBlock FlowingAmmoniumNitrateSolutionBlock(){
        AMMONIUM_NITRATE_SOLUTION_BLOCK = new VEFlowingFluidBlock(() -> AMMONIUM_NITRATE_SOLUTION, stdProp);
        return AMMONIUM_NITRATE_SOLUTION_BLOCK;
    }

    public static Item AmmoniumNitrateSolutionBucket(){
        AMMONIUM_NITRATE_SOLUTION_BUCKET = new AmmoniumNitrateBucket(() -> AMMONIUM_NITRATE_SOLUTION, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        return AMMONIUM_NITRATE_SOLUTION_BUCKET;
    }

    public static final FluidType AMMONIUM_NITRATE_SOLUTION_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            AMMONIUM_NITRATE_SOLUTION_STILL_TEXTURE,
            AMMONIUM_NITRATE_SOLUTION_FLOWING_TEXTURE
    );

    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(() -> AMMONIUM_NITRATE_SOLUTION_FLUID_TYPE, () -> AMMONIUM_NITRATE_SOLUTION, () -> FLOWING_AMMONIUM_NITRATE_SOLUTION)
            .block(() -> AMMONIUM_NITRATE_SOLUTION_BLOCK).bucket(() -> AMMONIUM_NITRATE_SOLUTION_BUCKET);
}
