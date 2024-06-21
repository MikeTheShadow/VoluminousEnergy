package com.veteam.voluminousenergy.util;

import com.mojang.serialization.Codec;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.util.records.BlastFurnaceData;
import com.veteam.voluminousenergy.util.records.CounterLength;
import com.veteam.voluminousenergy.util.records.FluidPumpData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static com.veteam.voluminousenergy.util.VECodecs.*;

public class VEAttachments {

    public static final DeferredRegister<AttachmentType<?>> DATA_COMPONENT_TYPE_DEFERRED_REGISTER
            = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, VoluminousEnergy.MODID);

    public static final Supplier<AttachmentType<FluidPumpData>> FLUID_PUMP = DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register(
            "fluid_pump_data", () -> AttachmentType.builder((iAttachmentHolder)
                    -> new FluidPumpData(1,1,1,null)).serialize(FLUID_PUMP_DATA_CODEC).build());

    public static final Supplier<AttachmentType<Integer>> SOUND_TICK = DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register(
            "sound_tick", () -> AttachmentType.builder((iAttachmentHolder)
                    -> 0).serialize(Codec.INT).build());

    public static final Supplier<AttachmentType<CounterLength>> COUNTER_LENGTH = DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register(
            "counter_length", () -> AttachmentType.builder((iAttachmentHolder)
                    -> new CounterLength(0,0)).serialize(COUNTER_LENGTH_CODEC).build());

    public static final Supplier<AttachmentType<CounterLength>> FUEL_COUNTER_LENGTH = DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register(
            "fuel_counter_length", () -> AttachmentType.builder((iAttachmentHolder)
                    -> new CounterLength(0,0)).serialize(COUNTER_LENGTH_CODEC).build());

    public static final Supplier<AttachmentType<Integer>> BUILD_TICK = DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register(
            "build_tick", () -> AttachmentType.builder((iAttachmentHolder)
                    -> 0).serialize(Codec.INT).build());

    public static final Supplier<AttachmentType<Boolean>> IS_COMPLETE = DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register(
            "is_complete", () -> AttachmentType.builder((iAttachmentHolder)
                    -> false).serialize(Codec.BOOL).build());

    public static final Supplier<AttachmentType<BlastFurnaceData>> BLAST_FURNACE_DATA = DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register(
            "counter_length", () -> AttachmentType.builder((iAttachmentHolder)
                    -> new BlastFurnaceData(0,0,0)).serialize(BLAST_FURNACE_CODEC).build());
}
