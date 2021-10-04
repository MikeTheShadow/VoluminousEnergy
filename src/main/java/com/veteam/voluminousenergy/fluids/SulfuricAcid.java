package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.AcidFlowingFluidBlock;
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

public class SulfuricAcid {
    public static final ResourceLocation SULFURIC_ACID_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/sulfuric_acid_still");
    public static final ResourceLocation SULFURIC_ACID_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/sulfuric_acid_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

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
        SULFURIC_ACID_BUCKET = new VENoPlaceBucket(() -> SULFURIC_ACID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return SULFURIC_ACID_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> SULFURIC_ACID, () -> FLOWING_SULFURIC_ACID, FluidAttributes.builder(SULFURIC_ACID_STILL_TEXTURE, SULFURIC_ACID_FLOWING_TEXTURE))
                    .bucket(() -> SULFURIC_ACID_BUCKET).block(() -> SULFURIC_ACID_BLOCK);

}