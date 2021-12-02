package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class AluminumShellBlock extends Block {
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
