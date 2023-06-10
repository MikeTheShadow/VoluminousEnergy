package com.veteam.voluminousenergy.blocks.blocks.ores.deepslate;

import com.veteam.voluminousenergy.blocks.blocks.ores.VEOreBlock;
import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SoundType;

public class DeepslateRutileOre extends VEOreBlock {
    public DeepslateRutileOre(){
        super(Properties.of()
                .sound(SoundType.STONE)
                .strength(4.0f)
                .requiresCorrectToolForDrops()
        );
        setRName("deepslate_rutile_ore");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setRutileTier(this);
    }

    @Override
    protected int xpOnDrop(RandomSource rand) {
        return 0;
    }
}