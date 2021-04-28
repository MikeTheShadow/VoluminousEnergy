package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class BauxiteOre extends VEOreBlock {
    public BauxiteOre(){
        super(Properties.of(Material.STONE)
            .sound(SoundType.STONE)
            .strength(2.0f)
            .requiresCorrectToolForDrops()
            .harvestLevel(Config.BAUXITE_HARVEST_LEVEL.get())
            .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("bauxiteore");
    }

    @Override
    protected int xpOnDrop(Random rand) {
        return 0;
    }

}
