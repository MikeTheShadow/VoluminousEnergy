package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.AcidFlowingFluidBlock;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static com.veteam.voluminousenergy.fluids.VEFluids.*;

public class SulfuricAcid {
    public static final ResourceLocation SULFURIC_ACID_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/sulfuric_acid_still");
    public static final ResourceLocation SULFURIC_ACID_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/sulfuric_acid_flowing");

    public static Block.Properties stdProp = Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops();

    public static FlowingFluid SULFURIC_ACID;
    public static FlowingFluid FLOWING_SULFURIC_ACID;
    public static AcidFlowingFluidBlock SULFURIC_ACID_BLOCK;
    public static Item SULFURIC_ACID_BUCKET;

    public static FlowingFluid SulfuricAcidFluid(){
        SULFURIC_ACID = new ForgeFlowingFluid.Source(SulfuricAcid.properties);
        return SULFURIC_ACID;
    }

    public static FlowingFluid FlowingSulfuricAcidFluid(){
        FLOWING_SULFURIC_ACID = new ForgeFlowingFluid.Flowing(SulfuricAcid.properties);
        return FLOWING_SULFURIC_ACID;
    }

    public static AcidFlowingFluidBlock FlowingSulfuricAcidBlock(){
        SULFURIC_ACID_BLOCK = new AcidFlowingFluidBlock(() -> SULFURIC_ACID, stdProp);
        return SULFURIC_ACID_BLOCK;
    }

    public static Item SulfuricAcidBucket(){
        SULFURIC_ACID_BUCKET = new BucketItem(() -> SULFURIC_ACID, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(VESetup.itemGroup));
        return SULFURIC_ACID_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> SULFURIC_ACID, () -> FLOWING_SULFURIC_ACID, FluidAttributes.builder(SULFURIC_ACID_STILL_TEXTURE, SULFURIC_ACID_FLOWING_TEXTURE))
                    .bucket(() -> SULFURIC_ACID_BUCKET).block(() -> SULFURIC_ACID_BLOCK);

}