package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
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

public class DinitrogenTetroxide {
    public static final ResourceLocation DINITROGEN_TETROXIDE_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/dinitrogen_tetroxide_still");
    public static final ResourceLocation DINITROGEN_TETROXIDE_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/dinitrogen_tetroxide_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable();

    public static FlowingFluid DINITROGEN_TETROXIDE;
    public static FlowingFluid FLOWING_DINITROGEN_TETROXIDE;
    public static VEFlowingFluidBlock DINITROGEN_TETROXIDE_BLOCK;
    public static Item DINITROGEN_TETROXIDE_BUCKET;

    public static FlowingFluid DinitrogenTetroxideFluid(){
        DINITROGEN_TETROXIDE = new ForgeFlowingFluid.Source(DinitrogenTetroxide.properties);
        return DINITROGEN_TETROXIDE;
    }

    public static FlowingFluid FlowingDinitrogenTetroxideFluid(){
        FLOWING_DINITROGEN_TETROXIDE = new ForgeFlowingFluid.Flowing(DinitrogenTetroxide.properties);
        return FLOWING_DINITROGEN_TETROXIDE;
    }

    public static VEFlowingFluidBlock FlowingDinitrogenTetroxideBlock(){
        DINITROGEN_TETROXIDE_BLOCK = new VEFlowingFluidBlock(() -> DINITROGEN_TETROXIDE, stdProp);
        return DINITROGEN_TETROXIDE_BLOCK;
    }

    public static Item DinitrogenTetroxideBucket(){
        DINITROGEN_TETROXIDE_BUCKET = new BucketItem(() -> DINITROGEN_TETROXIDE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return DINITROGEN_TETROXIDE_BUCKET;
    }


    public static final FluidType DINITROGEN_TETROXIDE_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            DINITROGEN_TETROXIDE_STILL_TEXTURE,
            DINITROGEN_TETROXIDE_FLOWING_TEXTURE
    );

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> DINITROGEN_TETROXIDE_FLUID_TYPE, () -> DINITROGEN_TETROXIDE, () -> FLOWING_DINITROGEN_TETROXIDE)
            .block(() -> DINITROGEN_TETROXIDE_BLOCK).bucket(() -> DINITROGEN_TETROXIDE_BUCKET);
}
