package com.veteam.voluminousenergy.persistence;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChunkFluids extends SavedData {

    // TODO consider moving this somewhere
    private static ChunkFluids CHUNK_FLUIDS;

    private final Set<ChunkFluid> chunkFluidSet = new HashSet<>();
    private final ServerLevel level;
    private int nextAvailableID;
    private int tick;

    public ChunkFluids(ServerLevel serverLevel) {
        this.level = serverLevel;
        this.nextAvailableID = 1;
        this.setDirty();
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

//    public ChunkFluid getOrCreateChunkFluid(ServerLevel serverLevel, ChunkPos chunkPos) {
//        ChunkFluid chunkFluid = getChunkFluid(chunkPos);
//        return chunkFluid != null ? chunkFluid : new ChunkFluid(this.getUniqueId(), serverLevel, chunkPos);
//    }

    public static ChunkFluids load(ServerLevel serverLevel, CompoundTag compoundTag) {
        ChunkFluids chunkFluid = new ChunkFluids(serverLevel);
        chunkFluid.nextAvailableID = compoundTag.getInt("NextAvailableID");
        chunkFluid.tick = compoundTag.getInt("Tick");
        ListTag listtag = compoundTag.getList("ChunkFluids", 10);
        for (int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            ChunkFluid cf = new ChunkFluid(serverLevel, compoundtag);
            chunkFluid.chunkFluidSet.add(cf);
        }
        return chunkFluid;
    }

    public @NotNull CompoundTag save(CompoundTag p_37976_) {
        p_37976_.putInt("NextAvailableID", this.nextAvailableID);
        p_37976_.putInt("Tick", this.tick);
        ListTag listtag = new ListTag();

        for (ChunkFluid chunkFluid : this.chunkFluidSet.stream().toList()) {
            CompoundTag compoundtag = new CompoundTag();
            chunkFluid.save(compoundtag);
            listtag.add(compoundtag);
        }

        p_37976_.put("ChunkFluids", listtag);
        return p_37976_;
    }

    /*
    This will allow us to technically do this on a per-dimension basis
     */
    public static String getFileId(Holder<DimensionType> dimensionTypeHolder) {
        //return dimensionTypeHolder.is(DimensionType.END_LOCATION) ? "raids_end" : "raids";
        return "chunk_fluids";
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
            CHUNK_FLUIDS = serverLevel.getDataStorage().computeIfAbsent((compoundTag)
                            -> ChunkFluids.load(serverLevel, compoundTag),
                    () -> new ChunkFluids(serverLevel),
                    ChunkFluids.getFileId(serverLevel.dimensionTypeRegistration()));
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
