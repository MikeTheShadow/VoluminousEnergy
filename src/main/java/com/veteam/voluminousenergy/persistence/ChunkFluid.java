package com.veteam.voluminousenergy.persistence;

import com.veteam.voluminousenergy.util.RegistryLookups;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class ChunkFluid {
    private final ChunkPos chunkPos;
    private final List<SingleChunkFluid> chunkFluidList = new ArrayList<>();

    public ChunkFluid(CompoundTag compoundTag) {
        this.chunkPos = new ChunkPos(compoundTag.getInt("CX"), compoundTag.getInt("CZ"));
        int i = 0;
        while (compoundTag.contains("SCF_" + i)) {
            SingleChunkFluid singleChunkFluid =
                    new SingleChunkFluid(
                            ForgeRegistries.FLUIDS.getValue(new ResourceLocation(compoundTag.getString("SCF_" + i)))
                            ,compoundTag.getInt("FS_" + i));
            this.chunkFluidList.add(singleChunkFluid);
            i++;
        }
    }

    public ChunkFluid(ChunkPos chunkPos, ArrayList<Pair<Fluid, Integer>> fluidPairs) {
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
            compoundTag.putString("SCF_" + i, RegistryLookups.lookupFluid(fluid.getFluid()).toString());
        }

        return compoundTag;
    }

    public ChunkPos getChunkPos() {
        return this.chunkPos;
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
