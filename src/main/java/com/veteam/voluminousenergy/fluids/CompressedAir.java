package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Supplier;

public class CompressedAir {
    public static final ResourceLocation COMPRESSED_AIR_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/compressed_air_still");
    public static final ResourceLocation COMPRESSED_AIR_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/compressed_air_flowing");

    public static Block.Properties stdProp = Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops();

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
        COMPRESSED_AIR_BUCKET = new BucketItem(() -> COMPRESSED_AIR, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(VESetup.itemGroup));
        return COMPRESSED_AIR_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> COMPRESSED_AIR, () -> FLOWING_COMPRESSED_AIR, FluidAttributes.builder(COMPRESSED_AIR_STILL_TEXTURE, COMPRESSED_AIR_FLOWING_TEXTURE).viscosity(5))
                    .bucket(() -> COMPRESSED_AIR_BUCKET).block(() -> COMPRESSED_AIR_BLOCK);
}
