package com.veteam.voluminousenergy.blocks.blocks.storage.raw;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class RawEighzoBlock extends VEBlock {
    public RawEighzoBlock() {
        super(Block.Properties.of()
                .sound(SoundType.METAL)
                .strength(30F, 1200F)
                .requiresCorrectToolForDrops()
        );
        setRName("raw_eighzo_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setEighzoTier(this);
    }
}
