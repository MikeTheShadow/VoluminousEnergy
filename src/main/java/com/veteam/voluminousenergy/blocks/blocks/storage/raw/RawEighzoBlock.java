package com.veteam.voluminousenergy.blocks.blocks.storage.raw;

import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class RawEighzoBlock extends Block{
    public RawEighzoBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(30F, 1200F)
                .requiresCorrectToolForDrops()
        );
        setRegistryName("raw_eighzo_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setEighzoTier(this);
    }
}
