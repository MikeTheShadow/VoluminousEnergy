package com.veteam.voluminousenergy.persistence;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class ChunkFluid {

    private final ChunkPos chunkPos;
    private final ServerLevel level;
    private List<SingleChunkFluid> chunkFluidList = new ArrayList<>();

    public ChunkFluid(ServerLevel serverLevel, CompoundTag compoundTag) {
        this.level = serverLevel;
        this.chunkPos = new ChunkPos(compoundTag.getInt("CX"), compoundTag.getInt("CZ"));

        int i = 0;
        while (compoundTag.contains("SCF_" + i++)) {
            SingleChunkFluid singleChunkFluid =
                    new SingleChunkFluid(
                            ForgeRegistries.FLUIDS.getValue(new ResourceLocation(compoundTag.getString("SCF_" + i)))
                            ,compoundTag.getInt("FS_" + i));
            this.chunkFluidList.add(singleChunkFluid);
        }
    }

    public ChunkFluid(ServerLevel serverLevel, ChunkPos chunkPos, ArrayList<Pair<Fluid, Integer>> fluidPairs) {
        this.level = serverLevel;
        this.chunkPos = chunkPos;
        for(var value : fluidPairs) {
            this.chunkFluidList.add(new SingleChunkFluid(value.getA(),value.getB()));
        }
    }

    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putInt("CX", this.chunkPos.x);
        compoundTag.putInt("CZ", this.chunkPos.z);

        for (int i = 0; i < chunkFluidList.size(); i++) {
            SingleChunkFluid fluid = chunkFluidList.get(i);
            compoundTag.putInt("FS_" + i, fluid.getAmount());
            compoundTag.putString("SCF_" + i, fluid.getFluid().getRegistryName().toString());
        }

        return compoundTag;
    }

    public ChunkPos getChunkPos() {
        return this.chunkPos;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public List<SingleChunkFluid> getFluids() {
        return chunkFluidList;
    }

    public void setFluidRemaining(SingleChunkFluid fluidRemaining) {
        chunkFluidList.stream()
                .filter(fr -> fr.getFluid().isSame(fluidRemaining.getFluid())).findFirst().
                ifPresent(fr -> fr.setAmount(fluidRemaining.getAmount()));
    }
}
