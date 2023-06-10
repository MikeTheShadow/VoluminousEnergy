package com.veteam.voluminousenergy.blocks.blocks.ores.deepslate;

import com.veteam.voluminousenergy.blocks.blocks.ores.VEOreBlock;
import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class DeepslateCinnabarOre extends VEOreBlock {
    public DeepslateCinnabarOre(){
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .strength(1.5f)
                .requiresCorrectToolForDrops()
        );
        setRName("deepslate_cinnabar_ore");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setCinnabarTier(this);
    }

    @Override
    protected int xpOnDrop(RandomSource rand) {
        return 0;
    }
}
