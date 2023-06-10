package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.SoundType;

public class AluminumShellBlock extends VEBlock {
    public AluminumShellBlock() {
        super(Properties.of()
            .sound(SoundType.METAL)
            .strength(2.0f)
            .requiresCorrectToolForDrops()
        );
        setRName("aluminum_shell");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresStone(this);
    }
}
