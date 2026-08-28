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

public class Treethanol {
    public static final Identifier TREETHANOL_STILL_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/treethanol_still");
    public static final Identifier TREETHANOL_FLOWING_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/treethanol_flowing");

    public static Block.Properties stdProp = Block.Properties.of().setId(VERegistryHelper.currentBlockId()).noCollision().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid();

    public static FlowingFluid TREETHANOL;
    public static FlowingFluid FLOWING_TREETHANOL;
    public static VEFlowingFluidBlock TREETHANOL_BLOCK;
    public static Item TREETHANOL_BUCKET;

    public static FlowingFluid TreethanolFluid() {
        TREETHANOL = new BaseFlowingFluid.Source(Treethanol.properties);
        return TREETHANOL;
    }

    public static FlowingFluid FlowingTreethanolFluid() {
        FLOWING_TREETHANOL = new BaseFlowingFluid.Flowing(Treethanol.properties);
        return FLOWING_TREETHANOL;
    }

    public static VEFlowingFluidBlock FlowingTreethanolBlock() {
        TREETHANOL_BLOCK = new VEFlowingFluidBlock(TREETHANOL, stdProp.setId(VERegistryHelper.currentBlockId()));
        return TREETHANOL_BLOCK;
    }

    public static Item TreethanolBucket() {
        TREETHANOL_BUCKET = new BucketItem(TREETHANOL, new Item.Properties().setId(VERegistryHelper.currentItemId()).craftRemainder(Items.BUCKET).stacksTo(1));
        return TREETHANOL_BUCKET;
    }

    public static final VEFluidType TREETHANOL_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            TREETHANOL_STILL_TEXTURE,
            TREETHANOL_FLOWING_TEXTURE
    );

    public static final BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(() -> TREETHANOL_FLUID_TYPE, () -> TREETHANOL, () -> FLOWING_TREETHANOL)
            .block(() -> TREETHANOL_BLOCK).bucket(() -> TREETHANOL_BUCKET);
}