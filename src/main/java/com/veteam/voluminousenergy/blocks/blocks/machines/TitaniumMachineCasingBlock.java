package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class TitaniumMachineCasingBlock extends VEBlock {
    public TitaniumMachineCasingBlock() {
        super(Block.Properties.of()
                .sound(SoundType.METAL)
                .strength(2.0f)
                .requiresCorrectToolForDrops()
        );
        setRName("titanium_machine_casing");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresDiamond(this);
    }
}
