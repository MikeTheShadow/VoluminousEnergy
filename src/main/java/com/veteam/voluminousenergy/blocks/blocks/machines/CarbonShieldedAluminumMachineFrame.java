package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.SoundType;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class CarbonShieldedAluminumMachineFrame extends VEBlock {
    public CarbonShieldedAluminumMachineFrame() {
        super(Properties.of().setId(VERegistryHelper.currentBlockId())
                .sound(SoundType.METAL)
                .strength(2.0f)
                .requiresCorrectToolForDrops()
        );
        setRName("carbon_shielded_aluminum_machine_frame");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresStoneAndBlacklistLowerTiers(this);
    }
}