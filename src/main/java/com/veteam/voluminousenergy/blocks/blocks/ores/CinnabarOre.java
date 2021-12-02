package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import java.util.Random;

public class CinnabarOre extends VEOreBlock {
    public CinnabarOre(){
        super(Properties.of(Material.STONE)
            .sound(SoundType.STONE)
            .strength(1.5f)
            .requiresCorrectToolForDrops()
        );
        setRegistryName("cinnabarore");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setCinnabarTier(this);
    }

    @Override
    protected int xpOnDrop(Random rand) {
        return 0;
    }
}
