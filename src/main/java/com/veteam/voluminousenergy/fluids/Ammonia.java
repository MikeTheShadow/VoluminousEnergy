package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class Ammonia {
    public static final ResourceLocation AMMONIA_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/ammonia_still");
    public static final ResourceLocation AMMONIA_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/ammonia_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid AMMONIA;
    public static FlowingFluid FLOWING_AMMONIA;
    public static VEFlowingFluidBlock AMMONIA_BLOCK;
    public static Item AMMONIA_BUCKET;

    public static FlowingFluid AmmoniaFluid(){
        AMMONIA = new VEFlowingGasFluid.Source(Ammonia.properties, 4);
        return AMMONIA;
    }

    public static FlowingFluid FlowingAmmoniaFluid(){
        FLOWING_AMMONIA = new VEFlowingGasFluid.Flowing(Ammonia.properties, 4);
        return FLOWING_AMMONIA;
    }

    public static VEFlowingFluidBlock FlowingAmmoniaBlock(){
        AMMONIA_BLOCK = new VEFlowingFluidBlock(() -> AMMONIA, stdProp);
        return AMMONIA_BLOCK;
    }

    public static Item AmmoniaBucket(){
        AMMONIA_BUCKET = new BucketItem(() -> AMMONIA, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return AMMONIA_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> AMMONIA, () -> FLOWING_AMMONIA, FluidAttributes.builder(AMMONIA_STILL_TEXTURE, AMMONIA_FLOWING_TEXTURE).gaseous())
                    .bucket(() -> AMMONIA_BUCKET).block(() -> AMMONIA_BLOCK);
}
