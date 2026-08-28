package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class AluminumMachineCasingBlock extends VEBlock {
    public AluminumMachineCasingBlock() {
        super(Block.Properties.of().setId(VERegistryHelper.currentBlockId())
                .sound(SoundType.METAL)
                .strength(2.0f)
                .requiresCorrectToolForDrops()
        );
        setRName("aluminum_machine_casing");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresStoneAndBlacklistLowerTiers(this);
    }
}