package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class CarbonShieldedAluminumMachineFrame extends Block {
    public CarbonShieldedAluminumMachineFrame() {
        super(Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(2.0f)
                .requiresCorrectToolForDrops()
        );
        setRegistryName("carbon_shielded_aluminum_machine_frame");
        VETagDataGenerator.mineableWithPickaxe.add(this);
        VETagDataGenerator.addTierBasedOnInt(1, this);
    }
}