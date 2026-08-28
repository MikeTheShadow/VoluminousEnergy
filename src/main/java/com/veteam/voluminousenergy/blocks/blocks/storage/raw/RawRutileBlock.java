package com.veteam.voluminousenergy.blocks.blocks.storage.raw;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class RawRutileBlock extends VEBlock {
    public RawRutileBlock() {
        super(Block.Properties.of().setId(VERegistryHelper.currentBlockId())
                .sound(SoundType.METAL)
                .strength(4F)
                .requiresCorrectToolForDrops()
        );
        setRName("raw_rutile_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setRutileTier(this);
    }
}
