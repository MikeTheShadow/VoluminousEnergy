package com.veteam.voluminousenergy.blocks.blocks.ores.deepslate;

import com.veteam.voluminousenergy.blocks.blocks.ores.VEOreBlock;
import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class DeepslateBauxiteOre extends VEOreBlock {
    public DeepslateBauxiteOre(){
        super(Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .strength(2.0f)
                .requiresCorrectToolForDrops()
        );
        setRName("deepslate_bauxite_ore");
        VETagDataGenerator.setRequiresPickaxe(this);
        MaterialConstants.setBauxiteTier(this);
    }

    @Override
    protected int xpOnDrop(RandomSource rand) {
        return 0;
    }
}
