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

public class Ammonia {
    public static final ResourceLocation AMMONIA_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"block/fluids/ammonia_still");
    public static final ResourceLocation AMMONIA_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"block/fluids/ammonia_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable();

    public static FlowingFluid AMMONIA;
    public static FlowingFluid FLOWING_AMMONIA;
    public static VEFlowingFluidBlock AMMONIA_BLOCK;
    public static Item AMMONIA_BUCKET;

    public static FlowingFluid AmmoniaFluid(){
        AMMONIA = new VEFlowingGasFluid.Source(Ammonia.PROPERTIES, 4);
        return AMMONIA;
    }

    public static FlowingFluid FlowingAmmoniaFluid(){
        FLOWING_AMMONIA = new VEFlowingGasFluid.Flowing(Ammonia.PROPERTIES, 4);
        return FLOWING_AMMONIA;
    }

    public static VEFlowingFluidBlock FlowingAmmoniaBlock(){
        AMMONIA_BLOCK = new VEFlowingFluidBlock(() -> AMMONIA, stdProp);
        return AMMONIA_BLOCK;
    }

    public static Item AmmoniaBucket(){
        AMMONIA_BUCKET = new BucketItem(() -> AMMONIA, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        return AMMONIA_BUCKET;
    }

    public static final FluidType AMMONIA_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            AMMONIA_STILL_TEXTURE,
            AMMONIA_FLOWING_TEXTURE
    );

    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(() -> AMMONIA_FLUID_TYPE, () -> AMMONIA, () -> FLOWING_AMMONIA)
            .block(() -> AMMONIA_BLOCK).bucket(() -> AMMONIA_BUCKET);
}
