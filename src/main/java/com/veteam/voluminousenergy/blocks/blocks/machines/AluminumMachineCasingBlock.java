package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class AluminumMachineCasingBlock extends VEBlock {
    public AluminumMachineCasingBlock() {
        super(Block.Properties.of()
                .sound(SoundType.METAL)
                .strength(2.0f)
                .requiresCorrectToolForDrops()
        );
        setRName("aluminum_machine_casing");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresStone(this);
    }
}