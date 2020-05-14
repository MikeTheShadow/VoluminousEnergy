package com.veteam.voluminousenergy.fluids.RFNA;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class RedFumingNitricAcid {
    public static final ResourceLocation RFNA_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/rfna_still");
    public static final ResourceLocation RFNA_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/rfna_flowing");

    /*
     * Forge requires fluid attributes to be overwritten, which unfortunately does not seem to allow custom FlowingFluids at this time.
     * IE: The requirement of using ForgeFlowingFluid.Properties (or else game will crash) which doesn't allow custom flowing fluids prevents this
     * implementation.
     */
}
