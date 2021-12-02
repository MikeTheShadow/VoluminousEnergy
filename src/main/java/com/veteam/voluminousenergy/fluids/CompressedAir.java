package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
import com.veteam.voluminousenergy.items.VENoPlaceBucket;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class CompressedAir {
    public static final ResourceLocation COMPRESSED_AIR_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/compressed_air_still");
    public static final ResourceLocation COMPRESSED_AIR_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/compressed_air_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

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


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> COMPRESSED_AIR, () -> FLOWING_COMPRESSED_AIR, FluidAttributes.builder(COMPRESSED_AIR_STILL_TEXTURE, COMPRESSED_AIR_FLOWING_TEXTURE).viscosity(5))
                    .bucket(() -> COMPRESSED_AIR_BUCKET).block(() -> COMPRESSED_AIR_BLOCK);
}
