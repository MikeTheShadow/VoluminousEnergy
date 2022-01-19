package com.veteam.voluminousenergy.blocks.blocks.storage.raw;

import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class RawBauxiteBlock extends Block {
    public RawBauxiteBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(2F)
                .requiresCorrectToolForDrops()
        );
        setRegistryName("raw_bauxite_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setBauxiteTier(this);
    }
}
