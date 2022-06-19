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

public class Treethanol {
    public static final ResourceLocation TREETHANOL_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/treethanol_still");
    public static final ResourceLocation TREETHANOL_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/treethanol_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable();

    public static FlowingFluid TREETHANOL;
    public static FlowingFluid FLOWING_TREETHANOL;
    public static VEFlowingFluidBlock TREETHANOL_BLOCK;
    public static Item TREETHANOL_BUCKET;

    public static FlowingFluid TreethanolFluid(){
        TREETHANOL = new ForgeFlowingFluid.Source(Treethanol.properties);
        return TREETHANOL;
    }

    public static FlowingFluid FlowingTreethanolFluid(){
        FLOWING_TREETHANOL = new ForgeFlowingFluid.Flowing(Treethanol.properties);
        return FLOWING_TREETHANOL;
    }

    public static VEFlowingFluidBlock FlowingTreethanolBlock(){
        TREETHANOL_BLOCK = new VEFlowingFluidBlock(() -> TREETHANOL, stdProp);
        return TREETHANOL_BLOCK;
    }

    public static Item TreethanolBucket(){
        TREETHANOL_BUCKET = new BucketItem(() -> TREETHANOL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return TREETHANOL_BUCKET;
    }

    public static final FluidType TREETHANOL_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            TREETHANOL_STILL_TEXTURE,
            TREETHANOL_FLOWING_TEXTURE
    );

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> TREETHANOL_FLUID_TYPE, () -> TREETHANOL, () -> FLOWING_TREETHANOL)
            .block(() -> TREETHANOL_BLOCK).bucket(() -> TREETHANOL_BUCKET);
}