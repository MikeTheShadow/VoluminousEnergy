package com.veteam.voluminousenergy.blocks.blocks.storage.raw;

import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class RawRutileBlock extends Block {
    public RawRutileBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(4F)
                .requiresCorrectToolForDrops()
        );
        setRegistryName("raw_rutile_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setRutileTier(this);
    }
}
