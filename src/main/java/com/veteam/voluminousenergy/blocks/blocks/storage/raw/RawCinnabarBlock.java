package com.veteam.voluminousenergy.blocks.blocks.storage.raw;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class RawCinnabarBlock extends VEBlock {
    public RawCinnabarBlock() {
        super(Block.Properties.of()
                .sound(SoundType.METAL)
                .strength(1.5f)
                .requiresCorrectToolForDrops()
        );
        setRName("raw_cinnabar_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setCinnabarTier(this);
    }
}
