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
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class Biofuel {
    public static final ResourceLocation BIOFUEL_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"block/fluids/biofuel_still");
    public static final ResourceLocation BIOFUEL_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"block/fluids/biofuel_flowing");

    public static Block.Properties stdProp = Block.Properties.of().noCollission().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid();

    public static FlowingFluid BIOFUEL;
    public static FlowingFluid FLOWING_BIOFUEL;
    public static VEFlowingFluidBlock BIOFUEL_BLOCK;
    public static Item BIOFUEL_BUCKET;

    public static FlowingFluid BiofuelFluid(){
        BIOFUEL = new ForgeFlowingFluid.Source(Biofuel.properties);
        return BIOFUEL;
    }

    public static FlowingFluid FlowingBiofuelFluid(){
        FLOWING_BIOFUEL = new ForgeFlowingFluid.Flowing(Biofuel.properties);
        return FLOWING_BIOFUEL;
    }

    public static VEFlowingFluidBlock FlowingBiofuelBlock(){
        BIOFUEL_BLOCK = new VEFlowingFluidBlock(() -> BIOFUEL, stdProp);
        return BIOFUEL_BLOCK;
    }

    public static Item BiofuelBucket(){
        BIOFUEL_BUCKET = new BucketItem(() -> BIOFUEL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        return BIOFUEL_BUCKET;
    }

    public static final FluidType BIOFUEL_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            BIOFUEL_STILL_TEXTURE,
            BIOFUEL_FLOWING_TEXTURE
    );

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> BIOFUEL_FLUID_TYPE, () -> BIOFUEL, () -> FLOWING_BIOFUEL)
            .block(() -> BIOFUEL_BLOCK).bucket(() -> BIOFUEL_BUCKET);
}

