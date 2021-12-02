package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import java.util.Random;

public class BauxiteOre extends VEOreBlock {
    public BauxiteOre(){
        super(Properties.of(Material.STONE)
            .sound(SoundType.STONE)
            .strength(2.0f)
            .requiresCorrectToolForDrops()
        );
        setRegistryName("bauxiteore");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setBauxiteTier(this);
    }

    @Override
    protected int xpOnDrop(Random rand) {
        return 0;
    }
}
