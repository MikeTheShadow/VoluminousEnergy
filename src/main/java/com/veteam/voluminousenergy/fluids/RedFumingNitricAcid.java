package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static com.veteam.voluminousenergy.fluids.VEFluids.*;

public class RedFumingNitricAcid {
    public static final ResourceLocation RFNA_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/rfna_flowing");
    public static final ResourceLocation RFNA_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/rfna_flowing");

    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(RED_FUMING_NITRIC_ACID, FLOWING_RED_FUMING_NITRIC_ACID, FluidAttributes.builder(RFNA_STILL_TEXTURE, RFNA_FLOWING_TEXTURE))
                    .bucket(RED_FUMING_NITRIC_ACID_BUCKET).block(RED_FUMING_NITRIC_ACID_BLOCK);

}