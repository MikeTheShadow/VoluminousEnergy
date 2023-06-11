package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.CrudeOilFlowingFluidBlock;
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

public class TreeSap {
    public static final ResourceLocation TREE_SAP_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"block/fluids/tree_sap_still");
    public static final ResourceLocation TREE_SAP_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"block/fluids/tree_sap_flowing");

    public static Block.Properties stdProp = Block.Properties.of().noCollission().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid();

    public static FlowingFluid TREE_SAP;
    public static FlowingFluid FLOWING_TREE_SAP;
    public static CrudeOilFlowingFluidBlock TREE_SAP_BLOCK;
    public static Item TREE_SAP_BUCKET;

    public static FlowingFluid TreeSapFluid(){
        TREE_SAP = new ForgeFlowingFluid.Source(TreeSap.properties);
        return TREE_SAP;
    }

    public static FlowingFluid FlowingTreeSapFluid(){
        FLOWING_TREE_SAP = new ForgeFlowingFluid.Flowing(TreeSap.properties);
        return FLOWING_TREE_SAP;
    }

    public static CrudeOilFlowingFluidBlock FlowingTreeSapBlock(){
        TREE_SAP_BLOCK = new CrudeOilFlowingFluidBlock(() -> TREE_SAP, stdProp);
        return TREE_SAP_BLOCK;
    }

    public static Item TreeSapBucket(){
        TREE_SAP_BUCKET = new BucketItem(() -> TREE_SAP, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        return TREE_SAP_BUCKET;
    }


    public static final FluidType TREE_SAP_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            TREE_SAP_STILL_TEXTURE,
            TREE_SAP_FLOWING_TEXTURE
    );

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> TREE_SAP_FLUID_TYPE, () -> TREE_SAP, () -> FLOWING_TREE_SAP)
            .block(() -> TREE_SAP_BLOCK).bucket(() -> TREE_SAP_BUCKET);

}