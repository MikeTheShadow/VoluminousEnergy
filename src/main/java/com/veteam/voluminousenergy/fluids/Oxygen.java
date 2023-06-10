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

public class Oxygen {
    public static final ResourceLocation OXYGEN_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"block/fluids/oxygen_still");
    public static final ResourceLocation OXYGEN_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"block/fluids/oxygen_flowing");

    public static Block.Properties stdProp = Block.Properties.of().noCollission().strength(100.0F).noLootTable();

    public static FlowingFluid OXYGEN;
    public static FlowingFluid FLOWING_OXYGEN;
    public static VEFlowingFluidBlock OXYGEN_BLOCK;
    public static Item OXYGEN_BUCKET;

    public static FlowingFluid OxygenFluid(){
        OXYGEN = new VEFlowingGasFluid.Source(Oxygen.properties, 4);
        return OXYGEN;
    }

    public static FlowingFluid FlowingOxygenFluid(){
        FLOWING_OXYGEN = new VEFlowingGasFluid.Flowing(Oxygen.properties, 4);
        return FLOWING_OXYGEN;
    }

    public static VEFlowingFluidBlock FlowingOxygenBlock(){
        OXYGEN_BLOCK = new VEFlowingFluidBlock(() -> OXYGEN, stdProp);
        return OXYGEN_BLOCK;
    }

    public static Item OxygenBucket(){
        OXYGEN_BUCKET = new BucketItem(() -> OXYGEN, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        return OXYGEN_BUCKET;
    }

    public static final FluidType OXYGEN_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
            .adjacentPathType(BlockPathTypes.LAVA)
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

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> OXYGEN_FLUID_TYPE, () -> OXYGEN, () -> FLOWING_OXYGEN)
            .block(() -> OXYGEN_BLOCK).bucket(() -> OXYGEN_BUCKET);
}