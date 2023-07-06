package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.AcidFlowingFluidBlock;
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

public class SulfuricAcid {
    public static final ResourceLocation SULFURIC_ACID_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"block/fluids/sulfuric_acid_still");
    public static final ResourceLocation SULFURIC_ACID_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"block/fluids/sulfuric_acid_flowing");

    public static Block.Properties stdProp = Block.Properties.of().noCollission().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid();

    public static FlowingFluid SULFURIC_ACID;
    public static FlowingFluid FLOWING_SULFURIC_ACID;
    public static AcidFlowingFluidBlock SULFURIC_ACID_BLOCK;
    public static Item SULFURIC_ACID_BUCKET;

    public static FlowingFluid SulfuricAcidFluid(){
        SULFURIC_ACID = new ForgeFlowingFluid.Source(SulfuricAcid.properties);
        return SULFURIC_ACID;
    }

    public static FlowingFluid FlowingSulfuricAcidFluid(){
        FLOWING_SULFURIC_ACID = new ForgeFlowingFluid.Flowing(SulfuricAcid.properties);
        return FLOWING_SULFURIC_ACID;
    }

    public static AcidFlowingFluidBlock FlowingSulfuricAcidBlock(){
        SULFURIC_ACID_BLOCK = new AcidFlowingFluidBlock(() -> SULFURIC_ACID, stdProp);
        return SULFURIC_ACID_BLOCK;
    }

    public static Item SulfuricAcidBucket(){
        SULFURIC_ACID_BUCKET = new BucketItem(() -> SULFURIC_ACID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        return SULFURIC_ACID_BUCKET;
    }


    public static final FluidType SULFURIC_ACID_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
            .adjacentPathType(BlockPathTypes.DAMAGE_FIRE)
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
            SULFURIC_ACID_STILL_TEXTURE,
            SULFURIC_ACID_FLOWING_TEXTURE
    );

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> SULFURIC_ACID_FLUID_TYPE, () -> SULFURIC_ACID, () -> FLOWING_SULFURIC_ACID)
            .block(() -> SULFURIC_ACID_BLOCK).bucket(() -> SULFURIC_ACID_BUCKET);

}