package com.veteam.voluminousenergy.blocks.blocks.storage.raw;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class RawBoneBlock extends VEBlock {
    public RawBoneBlock() {
        super(Block.Properties.of()
                .sound(SoundType.BONE_BLOCK)
                .strength(2F)
                .requiresCorrectToolForDrops()
        );
        setRName("raw_bone_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.addTierBasedOnInt(1, this);
    }
}
