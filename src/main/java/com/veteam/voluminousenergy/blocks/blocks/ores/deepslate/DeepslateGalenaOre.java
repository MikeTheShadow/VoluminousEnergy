package com.veteam.voluminousenergy.blocks.blocks.ores.deepslate;

import com.veteam.voluminousenergy.blocks.blocks.ores.VEOreBlock;
import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SoundType;

public class DeepslateGalenaOre extends VEOreBlock {
    public DeepslateGalenaOre(){
        super(Properties.of()
                .sound(SoundType.STONE)
                .lightLevel(l -> Config.GALENA_ORE_BLOCK_LIGHT_LEVEL.get())
                .requiresCorrectToolForDrops()
                .strength(2.0f)
        );
        setRName("deepslate_galena_ore");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setGalenaTier(this);
    }

    @Override
    protected int xpOnDrop(RandomSource rand) {
        return 0;
    }
}
