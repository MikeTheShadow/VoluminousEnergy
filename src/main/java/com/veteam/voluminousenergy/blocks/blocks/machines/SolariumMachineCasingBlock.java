package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class SolariumMachineCasingBlock extends Block {
    public SolariumMachineCasingBlock() {
        super(Block.Properties.of()
                .sound(SoundType.METAL)
                .strength(25.0F, 1200.0F)
                .requiresCorrectToolForDrops()
        );
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresSolarium(this);
    }
}
