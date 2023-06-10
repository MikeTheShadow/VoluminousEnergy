package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SoundType;

public class CinnabarOre extends VEOreBlock {
    public CinnabarOre(){
        super(Properties.of()
            .sound(SoundType.STONE)
            .strength(1.5f)
            .requiresCorrectToolForDrops()
        );
        setRName("cinnabarore");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setCinnabarTier(this);
    }

    @Override
    protected int xpOnDrop(RandomSource rand) {
        return 0;
    }
}
