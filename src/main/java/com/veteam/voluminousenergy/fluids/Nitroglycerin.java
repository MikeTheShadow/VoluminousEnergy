package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
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

public class Nitroglycerin {
    public static final ResourceLocation NITROGLYCERIN_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/nitroglycerin_still");
    public static final ResourceLocation NITROGLYCERIN_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/nitroglycerin_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable();

    public static FlowingFluid NITROGLYCERIN;
    public static FlowingFluid FLOWING_NITROGLYCERIN;
    public static VEFlowingFluidBlock NITROGLYCERIN_BLOCK;
    public static Item NITROGLYCERIN_BUCKET;

    public static FlowingFluid NitroglycerinFluid(){
        NITROGLYCERIN = new ForgeFlowingFluid.Source(Nitroglycerin.properties);
        return NITROGLYCERIN;
    }

    public static FlowingFluid FlowingNitroglycerinFluid(){
        FLOWING_NITROGLYCERIN = new ForgeFlowingFluid.Flowing(Nitroglycerin.properties);
        return FLOWING_NITROGLYCERIN;
    }

    public static VEFlowingFluidBlock FlowingNitroglycerinBlock(){
        NITROGLYCERIN_BLOCK = new VEFlowingFluidBlock(() -> NITROGLYCERIN, stdProp);
        return NITROGLYCERIN_BLOCK;
    }

    public static Item NitroglycerinBucket(){
        NITROGLYCERIN_BUCKET = new BucketItem(() -> NITROGLYCERIN, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        return NITROGLYCERIN_BUCKET;
    }


    public static final FluidType NITROGLYCERIN_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            NITROGLYCERIN_STILL_TEXTURE,
            NITROGLYCERIN_FLOWING_TEXTURE
    );

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> NITROGLYCERIN_FLUID_TYPE, () -> NITROGLYCERIN, () -> FLOWING_NITROGLYCERIN)
            .block(() -> NITROGLYCERIN_BLOCK).bucket(() -> NITROGLYCERIN_BUCKET);
}

