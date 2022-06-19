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

public class CompressedAir {
    public static final ResourceLocation COMPRESSED_AIR_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/compressed_air_still");
    public static final ResourceLocation COMPRESSED_AIR_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/compressed_air_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable();

    public static FlowingFluid COMPRESSED_AIR;
    public static FlowingFluid FLOWING_COMPRESSED_AIR;
    public static VEFlowingFluidBlock COMPRESSED_AIR_BLOCK;
    public static Item COMPRESSED_AIR_BUCKET;

    public static FlowingFluid CompressedAirFluid(){
        COMPRESSED_AIR = new ForgeFlowingFluid.Source(CompressedAir.properties);
        return COMPRESSED_AIR;
    }

    public static FlowingFluid FlowingCompressedAirFluid(){
        FLOWING_COMPRESSED_AIR = new ForgeFlowingFluid.Flowing(CompressedAir.properties);
        return FLOWING_COMPRESSED_AIR;
    }

    public static VEFlowingFluidBlock FlowingCompressedAirBlock(){
        COMPRESSED_AIR_BLOCK = new VEFlowingFluidBlock(() -> COMPRESSED_AIR, stdProp);
        return COMPRESSED_AIR_BLOCK;
    }

    public static Item CompressedAirBucket() {
        COMPRESSED_AIR_BUCKET = new BucketItem(() -> COMPRESSED_AIR, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return COMPRESSED_AIR_BUCKET;
    }


    public static final FluidType COMPRESSED_AIR_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
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
            COMPRESSED_AIR_STILL_TEXTURE,
            COMPRESSED_AIR_FLOWING_TEXTURE
    );

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> COMPRESSED_AIR_FLUID_TYPE, () -> COMPRESSED_AIR, () -> FLOWING_COMPRESSED_AIR)
            .block(() -> COMPRESSED_AIR_BLOCK).bucket(() -> COMPRESSED_AIR_BUCKET);
}
