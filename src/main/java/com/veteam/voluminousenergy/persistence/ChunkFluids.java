package com.veteam.voluminousenergy.persistence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ChunkFluids extends SavedData {
    private static ChunkFluids CHUNK_FLUIDS;

    public static final Codec<ChunkFluids> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("NextAvailableID").forGetter(cf -> cf.nextAvailableID),
            Codec.INT.fieldOf("Tick").forGetter(cf -> cf.tick),
            ChunkFluid.CODEC.listOf().fieldOf("ChunkFluids").forGetter(cf -> List.copyOf(cf.chunkFluidSet))
    ).apply(instance, ChunkFluids::new));

    public static final SavedDataType<ChunkFluids> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("voluminousenergy", "chunk_fluids"),
            ChunkFluids::new,
            CODEC,
            DataFixTypes.SAVED_DATA_RAIDS);

    private final Set<ChunkFluid> chunkFluidSet = new HashSet<>();
    private int nextAvailableID;
    private int tick;

    public ChunkFluids() {
        this.nextAvailableID = 1;
        this.setDirty();
    }

    private ChunkFluids(int nextAvailableID, int tick, List<ChunkFluid> chunkFluids) {
        this.nextAvailableID = nextAvailableID;
        this.tick = tick;
        this.chunkFluidSet.addAll(chunkFluids);
    }

    public void tick() {
        ++this.tick;
        Iterator<ChunkFluid> iterator = this.chunkFluidSet.iterator();

        // Possible this is for saving. Marking as dirty must do something
        // Done every 10s might want to save more often since players could abuse this theoretically?
        if (this.tick % 20 == 0) {
            this.setDirty();
        }
    }

    private int getUniqueId() {
        return ++this.nextAvailableID;
    }

    @Nullable
    public ChunkFluid getChunkFluid(ChunkPos chunkPos) {
        for (ChunkFluid chunkFluid : this.chunkFluidSet.stream().toList()) {
            if (chunkFluid.getChunkPos().equals(chunkPos)) return chunkFluid;
        }
        return null;
    }

    public boolean hasChunkFluid(ChunkFluid cf) {
        for (ChunkFluid chunkFluid : this.chunkFluidSet.stream().toList()) {
            if (chunkFluid.getChunkPos().equals(cf.getChunkPos())) return true;
        }
        return false;
    }

    public static void loadInstance(ServerLevel serverLevel) {
        if (CHUNK_FLUIDS == null) {
            CHUNK_FLUIDS = serverLevel.getDataStorage().computeIfAbsent(TYPE);
        }
    }

    public static ChunkFluids getInstance() {
        return CHUNK_FLUIDS;
    }

    public void add(ChunkFluid chunkFluid) {
        this.chunkFluidSet.add(chunkFluid);
    }

    public ChunkFluid getOrElse(ChunkFluid fluid) {
        return chunkFluidSet.stream().filter(
                        chunkFluid -> chunkFluid.getChunkPos().equals(fluid.getChunkPos()))
                .findFirst().orElse(fluid);
    }
}
