package com.veteam.voluminousenergy.blocks.blocks.storage.materials;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class SolariumBlock extends Block {
    public SolariumBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(25.0F, 1200.0F)
                .requiresCorrectToolForDrops()
        );
        setRegistryName("solarium_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.addTierBasedOnInt(7, this);
    }
}