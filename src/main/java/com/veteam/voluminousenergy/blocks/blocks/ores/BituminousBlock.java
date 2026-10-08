package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

/**
 * Gravel or sand bound with bitumen, placed around oil lakes and deposits. Unlike the loose blocks
 * it replaces it does not fall, so a deposit's shell holds its oil when mined from below.
 */
public class BituminousBlock extends VEBlock {
    public BituminousBlock(String registryName) {
        super(Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .instrument(NoteBlockInstrument.SNARE)
                .sound(SoundType.GRAVEL)
                .strength(0.6F)
                .requiresCorrectToolForDrops()
        );
        setRName(registryName);
        VETagDataGenerator.setRequiresShovel(this);
        VETagDataGenerator.setRequiresWoodAndBlacklistLowerTiers(this);
    }
}
