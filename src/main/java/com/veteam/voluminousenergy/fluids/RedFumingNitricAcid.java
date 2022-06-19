package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.FumingAcidFlowingFluidBlock;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class RedFumingNitricAcid {
    public static final ResourceLocation RFNA_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/rfna_still");
    public static final ResourceLocation RFNA_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/rfna_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable();

    public static FlowingFluid RED_FUMING_NITRIC_ACID;
    public static FlowingFluid FLOWING_RED_FUMING_NITRIC_ACID;
    public static FumingAcidFlowingFluidBlock RED_FUMING_NITRIC_ACID_BLOCK;
    public static Item RED_FUMING_NITRIC_ACID_BUCKET;

    public static FlowingFluid RedFumingNitricAcidFluid(){
        RED_FUMING_NITRIC_ACID = new ForgeFlowingFluid.Source(RedFumingNitricAcid.properties);
        return RED_FUMING_NITRIC_ACID;
    }

    public static FlowingFluid FlowingRedFumingNitricAcidFluid(){
        FLOWING_RED_FUMING_NITRIC_ACID = new ForgeFlowingFluid.Flowing(RedFumingNitricAcid.properties);
        return FLOWING_RED_FUMING_NITRIC_ACID;
    }

    public static FumingAcidFlowingFluidBlock FlowingRedFumingNitricAcidBlock(){
        RED_FUMING_NITRIC_ACID_BLOCK = new FumingAcidFlowingFluidBlock(() -> RED_FUMING_NITRIC_ACID, stdProp);
        return RED_FUMING_NITRIC_ACID_BLOCK;
    }

    public static Item RedFumingNitricAcidBucket(){
        RED_FUMING_NITRIC_ACID_BUCKET = new BucketItem(() -> RED_FUMING_NITRIC_ACID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return RED_FUMING_NITRIC_ACID_BUCKET;
    }


    public static final FluidType RFNA_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            RFNA_STILL_TEXTURE,
            RFNA_FLOWING_TEXTURE
    );

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> RFNA_FLUID_TYPE, () -> RED_FUMING_NITRIC_ACID, () -> FLOWING_RED_FUMING_NITRIC_ACID)
            .block(() -> RED_FUMING_NITRIC_ACID_BLOCK).bucket(() -> RED_FUMING_NITRIC_ACID_BUCKET);

}