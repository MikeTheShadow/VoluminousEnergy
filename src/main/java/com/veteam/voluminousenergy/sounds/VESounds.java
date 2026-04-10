package com.veteam.voluminousenergy.sounds;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class VESounds {

    public static SoundEvent ENERGY_BEAM_ACTIVATE = SoundEvent.createVariableRangeEvent(Identifier.parse("voluminousenergy:energy_beam_activate"));
    public static SoundEvent ENERGY_BEAM_FIRED = SoundEvent.createVariableRangeEvent(Identifier.parse("voluminousenergy:energy_beam_fired"));
    public static SoundEvent AIR_COMPRESSOR = SoundEvent.createVariableRangeEvent(Identifier.parse("voluminousenergy:air_compressor_active"));
    public static SoundEvent AQUEOULIZER = SoundEvent.createVariableRangeEvent(Identifier.parse("voluminousenergy:aqueoulizer_active"));
    public static SoundEvent COMPRESSOR = SoundEvent.createVariableRangeEvent(Identifier.parse("voluminousenergy:compressor_active"));
    public static SoundEvent CRUSHER = SoundEvent.createVariableRangeEvent(Identifier.parse("voluminousenergy:crusher_active"));
    public static SoundEvent FURNACE = SoundEvent.createVariableRangeEvent(Identifier.parse("voluminousenergy:furnace_active"));
    public static SoundEvent GENERAL_MACHINE_NOISE = SoundEvent.createVariableRangeEvent(Identifier.parse("voluminousenergy:general_machine_noise"));
    public static SoundEvent IMPLOSION_COMPRESSOR = SoundEvent.createVariableRangeEvent(Identifier.parse("voluminousenergy:implosion_compressor_active"));
    // TODO decide what a distillation unit sounds like. I think a hissing sound as it involves water/liquid moving through it


}
