package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import java.util.Random;

public class RutileOre extends VEOreBlock {
    public RutileOre(){
        super(Properties.of(Material.STONE)
            .sound(SoundType.STONE)
            .strength(4.0f)
            .requiresCorrectToolForDrops()
        );
        setRegistryName("rutileore");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setRutileTier(this);
    }

    @Override
    protected int xpOnDrop(RandomSource rand) {
        return 0;
    }
}
