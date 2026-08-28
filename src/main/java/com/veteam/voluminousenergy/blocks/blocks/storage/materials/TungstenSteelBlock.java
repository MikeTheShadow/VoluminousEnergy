package com.veteam.voluminousenergy.blocks.blocks.storage.materials;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class TungstenSteelBlock extends VEBlock {
    public TungstenSteelBlock() {
        super(Block.Properties.of().setId(VERegistryHelper.currentBlockId())
                .sound(SoundType.METAL)
                .strength(2F)
                .requiresCorrectToolForDrops()
        );
        setRName("tungsten_steel_block");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresNetheriteAndBlacklistLowerTiers(this); // TODO: maybe tungstensteel instead
    }
}
