package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class TitaniumMachineCasingBlock extends Block {
    public TitaniumMachineCasingBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(2.0f)
                .requiresCorrectToolForDrops()
        );
        setRegistryName("titanium_machine_casing");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresDiamond(this);
    }
}
