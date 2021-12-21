package com.veteam.voluminousenergy.world.non_functional.noise;

import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Beardifier;

public class VEBeardifier extends Beardifier {
    protected VEBeardifier(StructureFeatureManager structureFeatureManager, ChunkAccess access) {
        super(structureFeatureManager, access);
    }
}
