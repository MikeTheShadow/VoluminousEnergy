package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static com.veteam.voluminousenergy.fluids.VEFluids.*;

public class Oxygen {
    public static final ResourceLocation OXYGEN_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/blocks/fluids/oxygen_still");
    public static final ResourceLocation OXYGEN_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/blocks/fluids/oxygen_flowing");

    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(OXYGEN, FLOWING_OXYGEN, FluidAttributes.builder(OXYGEN_STILL_TEXTURE, OXYGEN_FLOWING_TEXTURE))
                    .bucket(OXYGEN_BUCKET).block(OXYGEN_BLOCK);
}