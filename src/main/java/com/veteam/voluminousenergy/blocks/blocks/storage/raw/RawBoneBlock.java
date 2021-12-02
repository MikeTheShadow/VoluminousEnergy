package com.veteam.voluminousenergy.blocks.blocks.storage.raw;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class RawBoneBlock extends Block{
    public RawBoneBlock() {
        super(Block.Properties.of(Material.STONE, MaterialColor.SAND)
                .sound(SoundType.BONE_BLOCK)
                .strength(2F)
                .requiresCorrectToolForDrops()
        );
        setRegistryName("raw_bone_block");
        VETagDataGenerator.mineableWithPickaxe.add(this);
        VETagDataGenerator.addTierBasedOnInt(1, this);
    }
}
