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
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class LiquefiedCoke {
    public static final ResourceLocation LIQUEFIED_COKE_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"block/fluids/liquefied_coke_still");
    public static final ResourceLocation LIQUEFIED_COKE_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"block/fluids/liquefied_coke_flowing");

    public static Block.Properties stdProp = Block.Properties.of().noCollission().strength(100.0F).noLootTable();

    public static FlowingFluid LIQUEFIED_COKE;
    public static FlowingFluid FLOWING_LIQUEFIED_COKE;
    public static VEFlowingFluidBlock LIQUEFIED_COKE_BLOCK;
    public static Item LIQUEFIED_COKE_BUCKET;

    public static FlowingFluid LiquefiedCokeFluid(){
        LIQUEFIED_COKE = new ForgeFlowingFluid.Source(LiquefiedCoke.properties);
        return LIQUEFIED_COKE;
    }

    public static FlowingFluid FlowingLiquefiedCokeFluid(){
        FLOWING_LIQUEFIED_COKE = new ForgeFlowingFluid.Flowing(LiquefiedCoke.properties);
        return FLOWING_LIQUEFIED_COKE;
    }

    public static VEFlowingFluidBlock FlowingLiquefiedCokeBlock(){
        LIQUEFIED_COKE_BLOCK = new VEFlowingFluidBlock(() -> LIQUEFIED_COKE, stdProp);
        return LIQUEFIED_COKE_BLOCK;
    }

    public static Item LiquefiedCokeBucket(){
        LIQUEFIED_COKE_BUCKET = new BucketItem(() -> LIQUEFIED_COKE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        return LIQUEFIED_COKE_BUCKET;
    }


    public static final FluidType LIQUEFIED_COKE_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            LIQUEFIED_COKE_STILL_TEXTURE,
            LIQUEFIED_COKE_FLOWING_TEXTURE
    );

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> LIQUEFIED_COKE_FLUID_TYPE, () -> LIQUEFIED_COKE, () -> FLOWING_LIQUEFIED_COKE)
            .block(() -> LIQUEFIED_COKE_BLOCK).bucket(() -> LIQUEFIED_COKE_BUCKET);
}

