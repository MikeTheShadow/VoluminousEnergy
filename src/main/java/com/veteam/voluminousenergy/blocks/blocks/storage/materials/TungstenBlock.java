package com.veteam.voluminousenergy.blocks.blocks.storage.materials;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class TungstenBlock extends VEBlock {
    public TungstenBlock() {
        super(Block.Properties.of()
                .sound(SoundType.METAL)
                .strength(2F)
                .requiresCorrectToolForDrops()
        );
        setRName("tungsten_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.addTierBasedOnInt(3, this);
    }
}
