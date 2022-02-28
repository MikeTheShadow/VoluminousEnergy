package com.veteam.voluminousenergy.blocks.blocks.multiblocks;

import com.veteam.voluminousenergy.datagen.MaterialConstants;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class SimpleModelBlock extends Block {

    public SimpleModelBlock(){
        super(BlockBehaviour.Properties.of(Material.WOOD)
                .sound(SoundType.WOOD)
                .strength(1.0f)
                .requiresCorrectToolForDrops()
                .dynamicShape()
                .noOcclusion()
        );
        setRegistryName("simple_model");
        VETagDataGenerator.setRequiresAxe(this);
        VETagDataGenerator.setRequiresStone(this);
    }


}
