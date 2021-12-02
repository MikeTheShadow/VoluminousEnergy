package com.veteam.voluminousenergy.blocks.blocks.storage.materials;

import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class EighzoBlock extends Block {
    public EighzoBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(2F)
                .requiresCorrectToolForDrops()
        );
        setRegistryName("eighzo_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setEighzoTier(this);
    }
}
