package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.VENoPlaceBucket;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class LightFuel {
    public static final ResourceLocation LIGHT_FUEL_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/light_fuel_still");
    public static final ResourceLocation LIGHT_FUEL_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/light_fuel_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops();

    public static FlowingFluid LIGHT_FUEL;
    public static FlowingFluid FLOWING_LIGHT_FUEL;
    public static LiquidBlock LIGHT_FUEL_BLOCK;
    public static Item LIGHT_FUEL_BUCKET;

    public static FlowingFluid LightFuelFluid(){
        LIGHT_FUEL = new ForgeFlowingFluid.Source(LightFuel.properties);
        return LIGHT_FUEL;
    }

    public static FlowingFluid FlowingLightFuelFluid(){
        FLOWING_LIGHT_FUEL = new ForgeFlowingFluid.Flowing(LightFuel.properties);
        return FLOWING_LIGHT_FUEL;
    }

    public static LiquidBlock FlowingLightFuelBlock(){
        LIGHT_FUEL_BLOCK = new LiquidBlock(() -> LIGHT_FUEL, stdProp);
        return LIGHT_FUEL_BLOCK;
    }

    public static Item LightFuelBucket(){
        LIGHT_FUEL_BUCKET = new VENoPlaceBucket(() -> LIGHT_FUEL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return LIGHT_FUEL_BUCKET;
    }


    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(() -> LIGHT_FUEL, () -> FLOWING_LIGHT_FUEL, FluidAttributes.builder(LIGHT_FUEL_STILL_TEXTURE, LIGHT_FUEL_FLOWING_TEXTURE).viscosity(5).gaseous())
                    .bucket(() -> LIGHT_FUEL_BUCKET).block(() -> LIGHT_FUEL_BLOCK);
}

