package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.PoisonousFlowingFluidBlock;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VERegistryHelper;
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

public class Mercury {
    public static final Identifier MERCURY_STILL_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/mercury_still");
    public static final Identifier MERCURY_FLOWING_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/mercury_flowing");

    public static Block.Properties stdProp = Block.Properties.of().setId(VERegistryHelper.currentBlockId()).noCollision().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.POPPED).liquid();

    public static FlowingFluid MERCURY;
    public static FlowingFluid FLOWING_MERCURY;
    public static PoisonousFlowingFluidBlock MERCURY_BLOCK;
    public static Item MERCURY_BUCKET;

    public static FlowingFluid MercuryFluid() {
        MERCURY = new BaseFlowingFluid.Source(Mercury.properties);
        return MERCURY;
    }

    public static FlowingFluid FlowingMercuryFluid() {
        FLOWING_MERCURY = new BaseFlowingFluid.Flowing(Mercury.properties);
        return FLOWING_MERCURY;
    }

    public static PoisonousFlowingFluidBlock FlowingMercuryBlock() {
        MERCURY_BLOCK = new PoisonousFlowingFluidBlock(MERCURY, stdProp.setId(VERegistryHelper.currentBlockId()), Config.MERCURY_POISON_DURATION);
        return MERCURY_BLOCK;
    }

    public static Item MercuryBucket() {
        MERCURY_BUCKET = new BucketItem(MERCURY, new Item.Properties().setId(VERegistryHelper.currentItemId()).craftRemainder(Items.BUCKET).stacksTo(1));
        return MERCURY_BUCKET;
    }


    public static final VEFluidType MERCURY_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
            .adjacentPathType(PathType.WATER)
            .canConvertToSource(false)
            .canDrown(false)
            .canExtinguish(false)
            .canHydrate(false)
            .canPushEntity(false)
            .canConvertToSource(false)
            .canSwim(false)
            .lightLevel(0)
            .density(13534)
            .temperature(300)
            .viscosity(1530)
            .motionScale(0)
            .fallDistanceModifier(0)
            .rarity(Rarity.COMMON)
            .supportsBoating(false)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY),
            VEFluidPhysics.MERCURY,
            0xC7C7C7,
            MERCURY_STILL_TEXTURE,
            MERCURY_FLOWING_TEXTURE
    );

    public static final BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(() -> MERCURY_FLUID_TYPE, () -> MERCURY, () -> FLOWING_MERCURY)
            .block(() -> MERCURY_BLOCK).bucket(() -> MERCURY_BUCKET);

}