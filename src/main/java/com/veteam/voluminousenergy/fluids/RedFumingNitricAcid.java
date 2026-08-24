package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.FumingAcidFlowingFluidBlock;
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

public class RedFumingNitricAcid {
    public static final Identifier RFNA_STILL_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/rfna_still");
    public static final Identifier RFNA_FLOWING_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/rfna_flowing");

    public static Block.Properties stdProp = Block.Properties.of().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentBlockId()).noCollision().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid();

    public static FlowingFluid RED_FUMING_NITRIC_ACID;
    public static FlowingFluid FLOWING_RED_FUMING_NITRIC_ACID;
    public static FumingAcidFlowingFluidBlock RED_FUMING_NITRIC_ACID_BLOCK;
    public static Item RED_FUMING_NITRIC_ACID_BUCKET;

    public static FlowingFluid RedFumingNitricAcidFluid() {
        RED_FUMING_NITRIC_ACID = new BaseFlowingFluid.Source(RedFumingNitricAcid.properties);
        return RED_FUMING_NITRIC_ACID;
    }

    public static FlowingFluid FlowingRedFumingNitricAcidFluid() {
        FLOWING_RED_FUMING_NITRIC_ACID = new BaseFlowingFluid.Flowing(RedFumingNitricAcid.properties);
        return FLOWING_RED_FUMING_NITRIC_ACID;
    }

    public static FumingAcidFlowingFluidBlock FlowingRedFumingNitricAcidBlock() {
        RED_FUMING_NITRIC_ACID_BLOCK = new FumingAcidFlowingFluidBlock(RED_FUMING_NITRIC_ACID, stdProp.setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentBlockId()));
        return RED_FUMING_NITRIC_ACID_BLOCK;
    }

    public static Item RedFumingNitricAcidBucket() {
        RED_FUMING_NITRIC_ACID_BUCKET = new BucketItem(RED_FUMING_NITRIC_ACID, new Item.Properties().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentItemId()).craftRemainder(Items.BUCKET).stacksTo(1));
        return RED_FUMING_NITRIC_ACID_BUCKET;
    }


    public static final VEFluidType RFNA_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
            .adjacentPathType(PathType.FIRE)
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
            RFNA_STILL_TEXTURE,
            RFNA_FLOWING_TEXTURE
    );

    public static final BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(() -> RFNA_FLUID_TYPE, () -> RED_FUMING_NITRIC_ACID, () -> FLOWING_RED_FUMING_NITRIC_ACID)
            .block(() -> RED_FUMING_NITRIC_ACID_BLOCK).bucket(() -> RED_FUMING_NITRIC_ACID_BUCKET);

}