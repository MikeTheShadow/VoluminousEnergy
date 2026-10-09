package com.veteam.voluminousenergy.persistence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.util.records.ChunkFluidData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class ChunkFluid {

    public static final Codec<ChunkFluid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ChunkPos.CODEC.fieldOf("pos").forGetter(ChunkFluid::getChunkPos),
            SingleChunkFluid.CODEC.listOf().fieldOf("fluids").forGetter(ChunkFluid::getFluids)
    ).apply(instance, ChunkFluid::new));

    private final ChunkPos chunkPos;
    private final List<SingleChunkFluid> chunkFluidList = new ArrayList<>();

    public ChunkFluid(ChunkPos chunkPos, List<SingleChunkFluid> fluids) {
        this.chunkPos = chunkPos;
        this.chunkFluidList.addAll(fluids);
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
