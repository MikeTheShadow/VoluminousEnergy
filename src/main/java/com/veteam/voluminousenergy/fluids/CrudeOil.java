package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static com.veteam.voluminousenergy.fluids.VEFluids.*;

public class CrudeOil {
    public static final ResourceLocation CRUDE_OIL_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/crude_oil_still");
    public static final ResourceLocation CRUDE_OIL_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/crude_oil_flowing");

    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(CRUDE_OIL, FLOWING_CRUDE_OIL, FluidAttributes.builder(CRUDE_OIL_STILL_TEXTURE, CRUDE_OIL_FLOWING_TEXTURE))
                    .bucket(CRUDE_OIL_BUCKET).block(CRUDE_OIL_BLOCK);
}
