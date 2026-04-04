package com.veteam.voluminousenergy.blocks.blocks.ores.red_sand;

import com.veteam.voluminousenergy.blocks.blocks.ores.SaltpeterOre;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.SoundType;

public class RedSaltpeterOre extends SaltpeterOre {
    public RedSaltpeterOre() {
        super(new ColorRGBA(-8356741),
                Properties.of()
                        .sound(SoundType.SAND)
                        .strength(0.6f)
                        .requiresCorrectToolForDrops()
        );
        VETagDataGenerator.setRequiresShovel(this);
        VETagDataGenerator.setRequiresWoodAndBlacklistLowerTiers(this);
    }
}
