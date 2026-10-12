package com.veteam.voluminousenergy.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.material.FluidState;

import java.util.List;

public class VELakesFeature extends Feature<VELakesFeature.Configuration> {
    public VELakesFeature(Codec<VELakesFeature.Configuration> lakesConfiguration) {
        super(lakesConfiguration);
    }

    private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();


    @Override
    public boolean place(FeaturePlaceContext<VELakesFeature.Configuration> context) {
        return place(context.level(), context.chunkGenerator(), context.random(), context.origin(), context.config());
    }

    protected boolean place(WorldGenLevel worldIn, ChunkGenerator generator, RandomSource rand, BlockPos pos, Configuration config) {
        while (pos.getY() > 5 && worldIn.isEmptyBlock(pos)) {
            pos = pos.below();
        }

        if (pos.getY() <= 4) {
            return false;
        } else {
            pos = pos.below(4);

            LakeMask mask = LakeMask.generate(rand);
            BlockState fluidBlock = config.fluidState().createLegacyBlock();

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
                            if (isDirt(worldIn.getBlockState(blockpos)) && worldIn.getBrightness(LightLayer.SKY, pos.offset(x, y, z)) > 0) {
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
                            BlockState rimState = LakeMask.perimeterState(config.perimeterTargets(), worldIn.getBlockState(rimPos), rand);
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

    /**
     * Configures the lake's fluid and the blocks its rim is replaced with. Without
     * {@code perimeter_targets} the rim is stone, as vanilla lakes had.
     */
    public record Configuration(FluidState fluidState, List<OreConfiguration.TargetBlockState> perimeterTargets) implements FeatureConfiguration {
        private static final List<OreConfiguration.TargetBlockState> STONE_PERIMETER =
                List.of(OreConfiguration.target(AlwaysTrueTest.INSTANCE, Blocks.STONE.defaultBlockState()));

        public static final Codec<VELakesFeature.Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FluidState.CODEC.fieldOf("fluidstate").forGetter(Configuration::fluidState),
                OreConfiguration.TargetBlockState.CODEC.listOf().optionalFieldOf("perimeter_targets", STONE_PERIMETER).forGetter(Configuration::perimeterTargets)
        ).apply(instance, VELakesFeature.Configuration::new));
    }
}
