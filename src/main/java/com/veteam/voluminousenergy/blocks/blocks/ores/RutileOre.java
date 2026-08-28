package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SoundType;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class RutileOre extends VEOreBlock {
    public RutileOre() {
        super(Properties.of().setId(VERegistryHelper.currentBlockId())
                .sound(SoundType.STONE)
                .strength(4.0f)
                .requiresCorrectToolForDrops()
        );
        setRName("rutileore");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setRutileTier(this);
    }

    @Override
    protected int xpOnDrop(RandomSource rand) {
        return 0;
    }
}
