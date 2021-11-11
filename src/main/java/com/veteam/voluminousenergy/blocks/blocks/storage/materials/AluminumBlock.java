package com.veteam.voluminousenergy.blocks.blocks.storage.materials;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class AluminumBlock extends Block {
    public AluminumBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(2F)
                .requiresCorrectToolForDrops()
        );
        setRegistryName("aluminum_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.addTierBasedOnInt(Config.BAUXITE_HARVEST_LEVEL.get(), this);
    }
}