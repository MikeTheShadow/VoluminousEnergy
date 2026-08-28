package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SoundType;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class BauxiteOre extends VEOreBlock {
    public BauxiteOre() {
        super(Properties.of().setId(VERegistryHelper.currentBlockId())
                .sound(SoundType.STONE)
                .strength(2.0f)
                .requiresCorrectToolForDrops()
        );
        setRName("bauxiteore");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setBauxiteTier(this);
    }

    @Override
    protected int xpOnDrop(RandomSource rand) {
        return 0;
    }
}
