package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
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

public class Oxygen {
    public static final ResourceLocation OXYGEN_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/oxygen_still");
    public static final ResourceLocation OXYGEN_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/oxygen_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid OXYGEN;
    public static FlowingFluid FLOWING_OXYGEN;
    public static VEFlowingFluidBlock OXYGEN_BLOCK;
    public static Item OXYGEN_BUCKET;

    public static FlowingFluid OxygenFluid(){
        OXYGEN = new ForgeFlowingFluid.Source(Oxygen.properties);
        return OXYGEN;
    }

    public static FlowingFluid FlowingOxygenFluid(){
        FLOWING_OXYGEN = new ForgeFlowingFluid.Flowing(Oxygen.properties);
        return FLOWING_OXYGEN;
    }

    public static VEFlowingFluidBlock FlowingOxygenBlock(){
        OXYGEN_BLOCK = new VEFlowingFluidBlock(() -> OXYGEN, stdProp);
        return OXYGEN_BLOCK;
    }

    public static Item OxygenBucket(){
        OXYGEN_BUCKET = new BucketItem(() -> OXYGEN, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return OXYGEN_BUCKET;
    }

    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> OXYGEN, () -> FLOWING_OXYGEN, FluidAttributes.builder(OXYGEN_STILL_TEXTURE, OXYGEN_FLOWING_TEXTURE).gaseous())
                    .bucket(() -> OXYGEN_BUCKET).block( () -> OXYGEN_BLOCK);
}