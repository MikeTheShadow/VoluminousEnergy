package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import java.util.Random;

public class EighzoOre extends VEOreBlock {
    public EighzoOre(){
        super(Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .strength(30F, 1200F)
                .requiresCorrectToolForDrops()
        );
        setRegistryName("eighzo_ore");
        VETagDataGenerator.mineableWithPickaxe.add(this);
        VETagDataGenerator.addTierBasedOnInt(Config.EIGHZO_HARVEST_LEVEL.get(), this);
    }

    @Override
    protected int xpOnDrop(Random rand) {
        return 0;
    }
}