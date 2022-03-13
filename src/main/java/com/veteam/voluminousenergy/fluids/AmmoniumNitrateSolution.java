package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.VEFlowingFluidBlock;
import com.veteam.voluminousenergy.items.AmmoniumNitrateBucket;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class AmmoniumNitrateSolution {
    public static final ResourceLocation AMMONIUM_NITRATE_SOLUTION_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/ammonium_nitrate_solution_still");
    public static final ResourceLocation AMMONIUM_NITRATE_SOLUTION_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/ammonium_nitrate_solution_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid AMMONIUM_NITRATE_SOLUTION;
    public static FlowingFluid FLOWING_AMMONIUM_NITRATE_SOLUTION;
    public static VEFlowingFluidBlock AMMONIUM_NITRATE_SOLUTION_BLOCK;
    public static Item AMMONIUM_NITRATE_SOLUTION_BUCKET;

    public static FlowingFluid AmmoniumNitrateSolutionFluid(){
        AMMONIUM_NITRATE_SOLUTION = new ForgeFlowingFluid.Source(AmmoniumNitrateSolution.properties);
        return AMMONIUM_NITRATE_SOLUTION;
    }

    public static FlowingFluid FlowingAmmoniumNitrateSolutionFluid(){
        FLOWING_AMMONIUM_NITRATE_SOLUTION = new ForgeFlowingFluid.Flowing(AmmoniumNitrateSolution.properties);
        return FLOWING_AMMONIUM_NITRATE_SOLUTION;
    }

    public static VEFlowingFluidBlock FlowingAmmoniumNitrateSolutionBlock(){
        AMMONIUM_NITRATE_SOLUTION_BLOCK = new VEFlowingFluidBlock(() -> AMMONIUM_NITRATE_SOLUTION, stdProp);
        return AMMONIUM_NITRATE_SOLUTION_BLOCK;
    }

    public static Item AmmoniumNitrateSolutionBucket(){
        AMMONIUM_NITRATE_SOLUTION_BUCKET = new AmmoniumNitrateBucket(() -> AMMONIUM_NITRATE_SOLUTION, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return AMMONIUM_NITRATE_SOLUTION_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> AMMONIUM_NITRATE_SOLUTION, () -> FLOWING_AMMONIUM_NITRATE_SOLUTION, FluidAttributes.builder(AMMONIUM_NITRATE_SOLUTION_STILL_TEXTURE, AMMONIUM_NITRATE_SOLUTION_FLOWING_TEXTURE).viscosity(5))
                    .bucket(() -> AMMONIUM_NITRATE_SOLUTION_BUCKET).block(() -> AMMONIUM_NITRATE_SOLUTION_BLOCK);
}
