package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class CinnabarOre extends VEOreBlock {
    public CinnabarOre(){
        super(Properties.of(Material.STONE)
            .sound(SoundType.STONE)
            .strength(1.5f)
            .requiresCorrectToolForDrops()
            .harvestLevel(Config.CINNABAR_HARVEST_LEVEL.get())
            .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("cinnabarore");
    }

    @Override
    protected int xpOnDrop(Random rand) {
        return 0;
    }
}
