package com.veteam.voluminousenergy.world.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.BlockReplacement;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.material.FluidState;

import java.util.List;

/**
 * A lake of the given fluid. Without {@code perimeter_targets} the rim is stone, as vanilla lakes had.
 */
public record VELakesFeature(FluidState fluidState, List<BlockReplacement> perimeterTargets) implements Feature {
    private static final List<BlockReplacement> STONE_PERIMETER =
            List.of(BlockReplacement.replace(AlwaysTrueTest.INSTANCE, Blocks.STONE.defaultBlockState()));

    public static final MapCodec<VELakesFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FluidState.CODEC.fieldOf("fluidstate").forGetter(VELakesFeature::fluidState),
            BlockReplacement.CODEC.listOf().optionalFieldOf("perimeter_targets", STONE_PERIMETER).forGetter(VELakesFeature::perimeterTargets)
    ).apply(instance, VELakesFeature::new));

    private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

    @Override
    public MapCodec<VELakesFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel worldIn, ChunkGenerator generator, RandomSource rand, BlockPos pos) {
        while (pos.getY() > 5 && worldIn.isEmptyBlock(pos)) {
            pos = pos.below();
        }

        if (pos.getY() <= 4) {
            return false;
        } else {
            pos = pos.below(4);

            LakeMask mask = LakeMask.generate(rand);
            BlockState fluidBlock = fluidState.createLegacyBlock();

            for (int x = 0; x < LakeMask.WIDTH; ++x) {
                for (int z = 0; z < LakeMask.WIDTH; ++z) {
                    for (int y = 0; y < LakeMask.HEIGHT; ++y) {
                        if (mask.isBorder(x, y, z)) {
                            BlockState material = worldIn.getBlockState(pos.offset(x, y, z));
                            if (y >= 4 && material.liquid()) {
                                return false;
                            }

                            if (y < 4 && !material.isSolid() && material != fluidBlock) {
                                return false;
                            }
                        }
                    }
                }
            }

            for (int x = 0; x < LakeMask.WIDTH; ++x) {
                for (int z = 0; z < LakeMask.WIDTH; ++z) {
                    for (int y = 0; y < LakeMask.HEIGHT; ++y) {
                        if (mask.isInside(x, y, z)) {
                            worldIn.setBlock(pos.offset(x, y, z), y >= 4 ? AIR : fluidBlock, 2);
                        }
                    }
                }
            }

            for (int x = 0; x < LakeMask.WIDTH; ++x) {
                for (int z = 0; z < LakeMask.WIDTH; ++z) {
                    for (int y = 4; y < LakeMask.HEIGHT; ++y) {
                        if (mask.isInside(x, y, z)) {
                            BlockPos blockpos = pos.offset(x, y - 1, z);
                            if (WorldUtil.isDirt(worldIn.getBlockState(blockpos)) && worldIn.getBrightness(LightLayer.SKY, pos.offset(x, y, z)) > 0) {
                                Holder<Biome> biome = worldIn.getBiome(blockpos);
                                if (biome.is(Biomes.MUSHROOM_FIELDS)) {
                                    worldIn.setBlock(blockpos, Blocks.MYCELIUM.defaultBlockState(), 2);
                                } else {
                                    worldIn.setBlock(blockpos, Blocks.GRASS_BLOCK.defaultBlockState(), 2);
                                }
                            }
                        }
                    }
                }
            }

            for (int x = 0; x < LakeMask.WIDTH; ++x) {
                for (int z = 0; z < LakeMask.WIDTH; ++z) {
                    for (int y = 0; y < LakeMask.HEIGHT; ++y) {
                        BlockPos rimPos = pos.offset(x, y, z);
                        if (mask.isBorder(x, y, z) && (y < 4 || rand.nextInt(2) != 0) && worldIn.getBlockState(rimPos).isSolid()) {
                            BlockState rimState = LakeMask.perimeterState(perimeterTargets, worldIn.getBlockState(rimPos), rimPos, rand);
                            if (rimState != null) {
                                worldIn.setBlock(rimPos, rimState, 2);
                            }
                        }
                    }
                }
            }

            return true;
        }
    }
}
