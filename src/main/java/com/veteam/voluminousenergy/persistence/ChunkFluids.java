package com.veteam.voluminousenergy.persistence;

import com.google.common.collect.Maps;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;

public class ChunkFluids extends SavedData {

    private final Map<Integer, ChunkFluid> raidMap = Maps.newHashMap();
    private final ServerLevel level;
    private int nextAvailableID;
    private int tick;

    public ChunkFluids(ServerLevel serverLevel) {
        this.level = serverLevel;
        this.nextAvailableID = 1;
        this.setDirty();
    }

    public ChunkFluid get(int pos) {
        return this.raidMap.get(pos);
    }

    public void tick() {
        ++this.tick;
        Iterator<ChunkFluid> iterator = this.raidMap.values().iterator();

        // Possible this is for saving. Marking as dirty must do something
        // Done every 10s might want to save more often since players could abuse this theoretically?
        if (this.tick % 200 == 0) {
            this.setDirty();
        }
    }

    public ChunkFluid getOrCreateChunkFluid(ServerLevel serverLevel, ChunkPos chunkPos) {
        ChunkFluid chunkFluid = getChunkFluid(chunkPos);
        return chunkFluid != null ? chunkFluid : new ChunkFluid(this.getUniqueId(), serverLevel, chunkPos);
    }

    public static ChunkFluids load(ServerLevel serverLevel, CompoundTag compoundTag) {
        ChunkFluids raids = new ChunkFluids(serverLevel);
        raids.nextAvailableID = compoundTag.getInt("NextAvailableID");
        raids.tick = compoundTag.getInt("Tick");
        ListTag listtag = compoundTag.getList("ChunkFluids", 10);
        for (int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            ChunkFluid raid = new ChunkFluid(serverLevel, compoundtag);
            raids.raidMap.put(raid.getId(), raid);
        }
        return raids;
    }

    public @NotNull CompoundTag save(CompoundTag p_37976_) {
        p_37976_.putInt("NextAvailableID", this.nextAvailableID);
        p_37976_.putInt("Tick", this.tick);
        ListTag listtag = new ListTag();

        for (ChunkFluid chunkFluid : this.raidMap.values()) {
            CompoundTag compoundtag = new CompoundTag();
            chunkFluid.save(compoundtag);
            listtag.add(compoundtag);
        }

        p_37976_.put("ChunkFluids", listtag);
        return p_37976_;
    }

    /* Use this to access everything
          this.raids = this.getDataStorage().computeIfAbsent((p_184095_) -> {
         return Raids.load(this, p_184095_);
      }, () -> {
         return new Raids(this);
      }, Raids.getFileId(this.dimensionTypeRegistration()));
     */

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
        for (ChunkFluid chunkFluid : this.raidMap.values()) {
            if (chunkFluid.getChunkPos().equals(chunkPos)) return chunkFluid;
        }
        return null;
    }

}
