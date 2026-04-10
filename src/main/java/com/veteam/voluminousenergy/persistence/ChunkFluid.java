package com.veteam.voluminousenergy.persistence;

import com.veteam.voluminousenergy.util.RegistryLookups;
import com.veteam.voluminousenergy.util.records.ChunkFluidData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
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
                            BuiltInRegistries.FLUID.get(Identifier.parse(compoundTag.getString("SCF_" + i)))
                            , compoundTag.getInt("FS_" + i));
            this.chunkFluidList.add(singleChunkFluid);
            i++;
        }
    }

    public ChunkFluid(ChunkFluidData data) {
        this.chunkPos = new ChunkPos(data.x(), data.z());
        for (FluidStack stack : data.fluids()) {
            SingleChunkFluid singleChunkFluid =
                    new SingleChunkFluid(stack.getFluid()
                            ,stack.getAmount());
            this.chunkFluidList.add(singleChunkFluid);
        }
    }

    public ChunkFluid(ChunkPos chunkPos, ArrayList<Pair<Fluid, Integer>> fluidPairs) {
        this.chunkPos = chunkPos;
        for (var value : fluidPairs) {
            this.chunkFluidList.add(new SingleChunkFluid(value.getA(), value.getB()));
        }
    }

    public void save(CompoundTag compoundTag) {
        compoundTag.putInt("CX", this.chunkPos.x);
        compoundTag.putInt("CZ", this.chunkPos.z);

        for (int i = 0; i < chunkFluidList.size(); i++) {
            SingleChunkFluid fluid = chunkFluidList.get(i);
            compoundTag.putInt("FS_" + i, fluid.getAmount());
            compoundTag.putString("SCF_" + i, RegistryLookups.lookupFluid(fluid.getFluid()).toString());
        }

    }

    public ChunkPos getChunkPos() {
        return this.chunkPos;
    }

    public List<SingleChunkFluid> getFluids() {
        return chunkFluidList;
    }

//    public void setFluidRemaining(SingleChunkFluid fluidRemaining) {
//        chunkFluidList.stream()
//                .filter(fr -> fr.getFluid().isSame(fluidRemaining.getFluid())).findFirst().
//                ifPresent(fr -> fr.setAmount(fluidRemaining.getAmount()));
//    }
}
