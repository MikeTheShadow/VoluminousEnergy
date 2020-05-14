package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static com.veteam.voluminousenergy.fluids.VEFluids.*;

public class WhiteFumingNitricAcid {
    public static final ResourceLocation WFNA_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/wfna_still");
    public static final ResourceLocation WFNA_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/wfna_flowing");

    public static final ForgeFlowingFluid.Properties properties =
            new ForgeFlowingFluid.Properties(WHITE_FUMING_NITRIC_ACID, FLOWING_WHITE_FUMING_NITRIC_ACID, FluidAttributes.builder(WFNA_STILL_TEXTURE, WFNA_FLOWING_TEXTURE))
                    .bucket(WHITE_FUMING_NITRIC_ACID_BUCKET).block(WHITE_FUMING_NITRIC_ACID_BLOCK);
}