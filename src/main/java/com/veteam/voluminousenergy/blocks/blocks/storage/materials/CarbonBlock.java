package com.veteam.voluminousenergy.blocks.blocks.storage.materials;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import com.veteam.voluminousenergy.util.VERegistryHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class CarbonBlock extends VEBlock {
    public CarbonBlock() {
        super(Block.Properties.of().setId(VERegistryHelper.currentBlockId())
                .sound(SoundType.METAL)
                .strength(2F)
                .requiresCorrectToolForDrops()
        );
        setRName("carbon_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresWoodAndBlacklistLowerTiers(this);
    }
}
