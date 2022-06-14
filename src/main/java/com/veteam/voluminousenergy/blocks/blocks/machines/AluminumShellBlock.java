package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class AluminumShellBlock extends VEBlock {
    public AluminumShellBlock() {
        super(Properties.of(Material.METAL)
            .sound(SoundType.METAL)
            .strength(2.0f)
            .requiresCorrectToolForDrops()
        );
        setRegistryName("aluminum_shell");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresStone(this);
    }
}
