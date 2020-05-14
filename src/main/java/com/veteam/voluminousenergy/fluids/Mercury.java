package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static com.veteam.voluminousenergy.fluids.VEFluids.*;

public class Mercury {
    public static final ResourceLocation MERCURY_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/mercury_still");
    public static final ResourceLocation MERCURY_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/mercury_flowing");

    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(MERCURY, FLOWING_MERCURY, FluidAttributes.builder(MERCURY_STILL_TEXTURE, MERCURY_FLOWING_TEXTURE))
                    .bucket(MERCURY_BUCKET).block(MERCURY_BLOCK);
}
