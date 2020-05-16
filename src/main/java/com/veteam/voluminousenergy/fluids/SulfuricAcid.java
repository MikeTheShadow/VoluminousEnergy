package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static com.veteam.voluminousenergy.fluids.VEFluids.*;

public class SulfuricAcid {
    public static final ResourceLocation SULFURIC_ACID_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/rfna_flowing");
    public static final ResourceLocation SULFURIC_ACID_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/rfna_flowing");

    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(SULFURIC_ACID, FLOWING_SULFURIC_ACID, FluidAttributes.builder(SULFURIC_ACID_STILL_TEXTURE, SULFURIC_ACID_FLOWING_TEXTURE))
                    .bucket(SULFURIC_ACID_BUCKET).block(SULFURIC_ACID_BLOCK);
}