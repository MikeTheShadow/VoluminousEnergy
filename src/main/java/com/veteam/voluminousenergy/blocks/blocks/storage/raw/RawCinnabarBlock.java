package com.veteam.voluminousenergy.blocks.blocks.storage.raw;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class RawCinnabarBlock extends Block {
    public RawCinnabarBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(1.5f)
                .requiresCorrectToolForDrops()
        );
        setRegistryName("raw_cinnabar_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.addTierBasedOnInt(Config.CINNABAR_HARVEST_LEVEL.get(), this);
    }
}
