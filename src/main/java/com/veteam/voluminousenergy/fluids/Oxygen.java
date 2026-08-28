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

public class Oxygen {
    public static final Identifier OXYGEN_STILL_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/oxygen_still");
    public static final Identifier OXYGEN_FLOWING_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/oxygen_flowing");

    public static Block.Properties stdProp = Block.Properties.of().setId(VERegistryHelper.currentBlockId()).noCollision().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).air();

    public static FlowingFluid OXYGEN;
    public static FlowingFluid FLOWING_OXYGEN;
    public static VEFlowingFluidBlock OXYGEN_BLOCK;
    public static Item OXYGEN_BUCKET;

    public static FlowingFluid OxygenFluid() {
        OXYGEN = new VEFlowingGasFluid.Source(Oxygen.properties, 4);
        return OXYGEN;
    }

    public static FlowingFluid FlowingOxygenFluid() {
        FLOWING_OXYGEN = new VEFlowingGasFluid.Flowing(Oxygen.properties, 4);
        return FLOWING_OXYGEN;
    }

    public static VEFlowingFluidBlock FlowingOxygenBlock() {
        OXYGEN_BLOCK = new VEFlowingFluidBlock(OXYGEN, stdProp.setId(VERegistryHelper.currentBlockId()));
        return OXYGEN_BLOCK;
    }

    public static Item OxygenBucket() {
        OXYGEN_BUCKET = new BucketItem(OXYGEN, new Item.Properties().setId(VERegistryHelper.currentItemId()).craftRemainder(Items.BUCKET).stacksTo(1));
        return OXYGEN_BUCKET;
    }

    public static final VEFluidType OXYGEN_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            OXYGEN_STILL_TEXTURE,
            OXYGEN_FLOWING_TEXTURE
    );

    public static final BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(() -> OXYGEN_FLUID_TYPE, () -> OXYGEN, () -> FLOWING_OXYGEN)
            .block(() -> OXYGEN_BLOCK).bucket(() -> OXYGEN_BUCKET);
}