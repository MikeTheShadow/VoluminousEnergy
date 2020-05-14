package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static com.veteam.voluminousenergy.fluids.VEFluids.*;

public class Naphtha {
    public static final ResourceLocation NAPHTHA_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/naphtha_still");
    public static final ResourceLocation NAPHTHA_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/naphtha_flowing");

    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(NAPHTHA, FLOWING_NAPHTHA, FluidAttributes.builder(NAPHTHA_STILL_TEXTURE, NAPHTHA_FLOWING_TEXTURE))
                    .bucket(NAPHTHA_BUCKET).block(NAPHTHA_BLOCK);
}
