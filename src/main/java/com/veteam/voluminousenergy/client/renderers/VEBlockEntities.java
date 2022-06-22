package com.veteam.voluminousenergy.client.renderers;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.DimensionalLaserTile;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VEBlockEntities {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES,     VoluminousEnergy.MODID);

    public static final RegistryObject<BlockEntityType<DimensionalLaserTile>> DIMENSIONAL_LASER = BLOCK_ENTITIES.register("dimensional_laser",        () -> BlockEntityType.Builder.of(DimensionalLaserTile::new,        VEBlocks.DIMENSIONAL_LASER_BLOCK)       .build(null));

}
