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

public class LiquefiedCoke {
    public static final Identifier LIQUEFIED_COKE_STILL_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/liquefied_coke_still");
    public static final Identifier LIQUEFIED_COKE_FLOWING_TEXTURE = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "block/fluids/liquefied_coke_flowing");

    public static Block.Properties stdProp = Block.Properties.of().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentBlockId()).noCollision().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid();

    public static FlowingFluid LIQUEFIED_COKE;
    public static FlowingFluid FLOWING_LIQUEFIED_COKE;
    public static VEFlowingFluidBlock LIQUEFIED_COKE_BLOCK;
    public static Item LIQUEFIED_COKE_BUCKET;

    public static FlowingFluid LiquefiedCokeFluid() {
        LIQUEFIED_COKE = new BaseFlowingFluid.Source(LiquefiedCoke.properties);
        return LIQUEFIED_COKE;
    }

    public static FlowingFluid FlowingLiquefiedCokeFluid() {
        FLOWING_LIQUEFIED_COKE = new BaseFlowingFluid.Flowing(LiquefiedCoke.properties);
        return FLOWING_LIQUEFIED_COKE;
    }

    public static VEFlowingFluidBlock FlowingLiquefiedCokeBlock() {
        LIQUEFIED_COKE_BLOCK = new VEFlowingFluidBlock(LIQUEFIED_COKE, stdProp.setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentBlockId()));
        return LIQUEFIED_COKE_BLOCK;
    }

    public static Item LiquefiedCokeBucket() {
        LIQUEFIED_COKE_BUCKET = new BucketItem(LIQUEFIED_COKE, new Item.Properties().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentItemId()).craftRemainder(Items.BUCKET).stacksTo(1));
        return LIQUEFIED_COKE_BUCKET;
    }


    public static final VEFluidType LIQUEFIED_COKE_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            LIQUEFIED_COKE_STILL_TEXTURE,
            LIQUEFIED_COKE_FLOWING_TEXTURE
    );

    public static final BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(() -> LIQUEFIED_COKE_FLUID_TYPE, () -> LIQUEFIED_COKE, () -> FLOWING_LIQUEFIED_COKE)
            .block(() -> LIQUEFIED_COKE_BLOCK).bucket(() -> LIQUEFIED_COKE_BUCKET);
}

