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

public class Biofuel {
    public static final Identifier BIOFUEL_STILL_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/biofuel_still");
    public static final Identifier BIOFUEL_FLOWING_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/biofuel_flowing");

    public static Block.Properties stdProp = Block.Properties.of().setId(VERegistryHelper.currentBlockId()).noCollision().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid();

    public static FlowingFluid BIOFUEL;
    public static FlowingFluid FLOWING_BIOFUEL;
    public static VEFlowingFluidBlock BIOFUEL_BLOCK;
    public static Item BIOFUEL_BUCKET;

    public static FlowingFluid BiofuelFluid() {
        BIOFUEL = new BaseFlowingFluid.Source(Biofuel.properties);
        return BIOFUEL;
    }

    public static FlowingFluid FlowingBiofuelFluid() {
        FLOWING_BIOFUEL = new BaseFlowingFluid.Flowing(Biofuel.properties);
        return FLOWING_BIOFUEL;
    }

    public static VEFlowingFluidBlock FlowingBiofuelBlock() {
        BIOFUEL_BLOCK = new VEFlowingFluidBlock(BIOFUEL, stdProp.setId(VERegistryHelper.currentBlockId()));
        return BIOFUEL_BLOCK;
    }

    public static Item BiofuelBucket() {
        BIOFUEL_BUCKET = new BucketItem(BIOFUEL, new Item.Properties().setId(VERegistryHelper.currentItemId()).craftRemainder(Items.BUCKET).stacksTo(1));
        return BIOFUEL_BUCKET;
    }

    public static final VEFluidType BIOFUEL_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            BIOFUEL_STILL_TEXTURE,
            BIOFUEL_FLOWING_TEXTURE
    );

    public static final BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(() -> BIOFUEL_FLUID_TYPE, () -> BIOFUEL, () -> FLOWING_BIOFUEL)
            .block(() -> BIOFUEL_BLOCK).bucket(() -> BIOFUEL_BUCKET);
}

