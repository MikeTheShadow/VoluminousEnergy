package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import com.veteam.voluminousenergy.tools.Config;
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
        VETagDataGenerator.mineableWithPickaxe.add(this);
        VETagDataGenerator.addTierBasedOnInt(Config.RUTILE_HARVEST_LEVEL.get(), this);
    }

    @Override
    protected int xpOnDrop(Random rand) {
        return 0;
    }
}
