package com.veteam.voluminousenergy.sounds;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class VESounds {

    public static SoundEvent ENERGY_BEAM_ACTIVATE =  SoundEvent.createVariableRangeEvent(new ResourceLocation("voluminousenergy:energy_beam_activate"));
    public static SoundEvent ENERGY_BEAM_FIRED = SoundEvent.createVariableRangeEvent(new ResourceLocation("voluminousenergy:energy_beam_fired"));
    public static SoundEvent AIR_COMPRESSOR = SoundEvent.createVariableRangeEvent(new ResourceLocation("voluminousenergy:air_compressor_active"));
    public static SoundEvent AQUEOULIZER = SoundEvent.createVariableRangeEvent(new ResourceLocation("voluminousenergy:aqueoulizer_active"));
    public static SoundEvent COMPRESSOR = SoundEvent.createVariableRangeEvent(new ResourceLocation("voluminousenergy:compressor_active"));
    public static SoundEvent CRUSHER = SoundEvent.createVariableRangeEvent(new ResourceLocation("voluminousenergy:crusher_active"));
    public static SoundEvent FURNACE = SoundEvent.createVariableRangeEvent(new ResourceLocation("voluminousenergy:furnace_active"));
    public static SoundEvent GENERAL_MACHINE_NOISE = SoundEvent.createVariableRangeEvent(new ResourceLocation("voluminousenergy:general_machine_noise"));
    public static SoundEvent IMPLOSION_COMPRESSOR = SoundEvent.createVariableRangeEvent(new ResourceLocation("voluminousenergy:implosion_compressor_active"));
    // TODO decide what a distillation unit sounds like. I think a hissing sound as it involves water/liquid moving through it


}
