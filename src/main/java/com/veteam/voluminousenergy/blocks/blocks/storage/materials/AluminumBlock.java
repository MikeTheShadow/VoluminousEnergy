package com.veteam.voluminousenergy.blocks.blocks.storage.materials;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class AluminumBlock extends VEBlock {
    public AluminumBlock() {
        super(Block.Properties.of().setId(VERegistryHelper.currentBlockId())
                .sound(SoundType.METAL)
                .strength(2F)
                .requiresCorrectToolForDrops()
        );
        setRName("aluminum_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresStoneAndBlacklistLowerTiers(this);
    }
}