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

public class DinitrogenTetroxide {
    public static final Identifier DINITROGEN_TETROXIDE_STILL_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/dinitrogen_tetroxide_still");
    public static final Identifier DINITROGEN_TETROXIDE_FLOWING_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/dinitrogen_tetroxide_flowing");

    public static Block.Properties stdProp = Block.Properties.of().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentBlockId()).noCollision().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid();

    public static FlowingFluid DINITROGEN_TETROXIDE;
    public static FlowingFluid FLOWING_DINITROGEN_TETROXIDE;
    public static VEFlowingFluidBlock DINITROGEN_TETROXIDE_BLOCK;
    public static Item DINITROGEN_TETROXIDE_BUCKET;

    public static FlowingFluid DinitrogenTetroxideFluid() {
        DINITROGEN_TETROXIDE = new BaseFlowingFluid.Source(DinitrogenTetroxide.properties);
        return DINITROGEN_TETROXIDE;
    }

    public static FlowingFluid FlowingDinitrogenTetroxideFluid() {
        FLOWING_DINITROGEN_TETROXIDE = new BaseFlowingFluid.Flowing(DinitrogenTetroxide.properties);
        return FLOWING_DINITROGEN_TETROXIDE;
    }

    public static VEFlowingFluidBlock FlowingDinitrogenTetroxideBlock() {
        DINITROGEN_TETROXIDE_BLOCK = new VEFlowingFluidBlock(DINITROGEN_TETROXIDE, stdProp.setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentBlockId()));
        return DINITROGEN_TETROXIDE_BLOCK;
    }

    public static Item DinitrogenTetroxideBucket() {
        DINITROGEN_TETROXIDE_BUCKET = new BucketItem(DINITROGEN_TETROXIDE, new Item.Properties().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentItemId()).craftRemainder(Items.BUCKET).stacksTo(1));
        return DINITROGEN_TETROXIDE_BUCKET;
    }


    public static final VEFluidType DINITROGEN_TETROXIDE_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            DINITROGEN_TETROXIDE_STILL_TEXTURE,
            DINITROGEN_TETROXIDE_FLOWING_TEXTURE
    );

    public static final BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(() -> DINITROGEN_TETROXIDE_FLUID_TYPE, () -> DINITROGEN_TETROXIDE, () -> FLOWING_DINITROGEN_TETROXIDE)
            .block(() -> DINITROGEN_TETROXIDE_BLOCK).bucket(() -> DINITROGEN_TETROXIDE_BUCKET);
}
