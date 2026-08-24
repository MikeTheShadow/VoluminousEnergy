package com.veteam.voluminousenergy.util;

import com.mojang.serialization.Codec;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.util.records.BlastFurnaceData;
import com.veteam.voluminousenergy.util.records.CounterLength;
import com.veteam.voluminousenergy.util.records.FluidPumpData;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static com.veteam.voluminousenergy.util.VECodecs.*;

public class VEAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPE_DEFERRED_REGISTER
            = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, VoluminousEnergy.MODID);

    public static final Supplier<AttachmentType<FluidPumpData>> FLUID_PUMP = ATTACHMENT_TYPE_DEFERRED_REGISTER.register(
            "fluid_pump_data", () -> AttachmentType.builder((iAttachmentHolder)
                    -> new FluidPumpData(1,1,1,null)).serialize(FLUID_PUMP_DATA_CODEC.fieldOf("value")).build());

    public static final Supplier<AttachmentType<Integer>> SOUND_TICK = ATTACHMENT_TYPE_DEFERRED_REGISTER.register(
            "sound_tick", () -> AttachmentType.builder((iAttachmentHolder)
                    -> 0).serialize(Codec.INT.fieldOf("value")).build());

    public static final Supplier<AttachmentType<CounterLength>> COUNTER_LENGTH = ATTACHMENT_TYPE_DEFERRED_REGISTER.register(
            "counter_length", () -> AttachmentType.builder((iAttachmentHolder)
                    -> new CounterLength(0,0)).serialize(COUNTER_LENGTH_CODEC.fieldOf("value")).build());

    public static final Supplier<AttachmentType<CounterLength>> FUEL_COUNTER_LENGTH = ATTACHMENT_TYPE_DEFERRED_REGISTER.register(
            "fuel_counter_length", () -> AttachmentType.builder((iAttachmentHolder)
                    -> new CounterLength(0,0)).serialize(COUNTER_LENGTH_CODEC.fieldOf("value")).build());

    public static final Supplier<AttachmentType<Integer>> BUILD_TICK = ATTACHMENT_TYPE_DEFERRED_REGISTER.register(
            "build_tick", () -> AttachmentType.builder((iAttachmentHolder)
                    -> 0).serialize(Codec.INT.fieldOf("value")).build());

    public static final Supplier<AttachmentType<Boolean>> IS_COMPLETE = ATTACHMENT_TYPE_DEFERRED_REGISTER.register(
            "is_complete", () -> AttachmentType.builder((iAttachmentHolder)
                    -> false).serialize(Codec.BOOL.fieldOf("value")).build());

    public static final Supplier<AttachmentType<BlastFurnaceData>> BLAST_FURNACE_DATA = ATTACHMENT_TYPE_DEFERRED_REGISTER.register(
            "blast_furnace_data", () -> AttachmentType.builder((iAttachmentHolder)
                    -> new BlastFurnaceData(0,0,0)).serialize(BLAST_FURNACE_CODEC.fieldOf("value")).build());

    public static final Supplier<AttachmentType<Boolean>> IS_RECIPE_AWAITING_COMPLETE = ATTACHMENT_TYPE_DEFERRED_REGISTER.register(
        "is_await_complete", () -> AttachmentType.builder((iAttachmentHolder)
            -> false).serialize(Codec.BOOL.fieldOf("value")).build());
}
