package com.veteam.voluminousenergy.world.noise;

import com.veteam.voluminousenergy.fluids.CrudeOil;
import com.veteam.voluminousenergy.fluids.Nitrogen;
import net.minecraft.SharedConstants;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;

public class VENoiseChunkGenerator extends NoiseBasedChunkGenerator {
    protected Aquifer.FluidPicker globalFluidPicker;
    protected SurfaceSystem surfaceSystem;
    protected NoiseGeneratorSettings settings;

    public VENoiseChunkGenerator(Registry<NormalNoise.NoiseParameters> noiseParameters, BiomeSource biomeSource, long seed, Supplier<NoiseGeneratorSettings> settings) {
        super(noiseParameters, biomeSource, seed, settings);
        int i = 63; // SEA LEVEL
        int naturalGasLevel = -22; //Maybe configify if this can work
        this.globalFluidPicker = (p_198228_, y_value, p_198230_) -> {
            //return p_198229_ < Math.min(-54, i) ? CrudeOil.CRUDE_OIL.defaultFluidState().createLegacyBlock() : Gasoline.GASOLINE.defaultFluidState().createLegacyBlock();
            return y_value > naturalGasLevel ? new Aquifer.FluidStatus(54, CrudeOil.CRUDE_OIL.defaultFluidState().createLegacyBlock())
                     : new Aquifer.FluidStatus(-22, Nitrogen.NITROGEN.defaultFluidState().createLegacyBlock()); // Natural Gas?
         };
        this.settings = settings.get();
        this.surfaceSystem = new SurfaceSystem(noiseParameters, this.defaultBlock, i, seed, settings.get().getRandomSource());
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureFeatureManager structureFeatureManager, ChunkAccess access) {
        if (!SharedConstants.debugVoidTerrain(access.getPos())) {
            WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(this, region);
            NoiseGeneratorSettings noisegeneratorsettings = this.settings;
            NoiseChunk noisechunk = access.getOrCreateNoiseChunk(this.sampler, () -> {
                return new VEBeardifier(structureFeatureManager, access);
            }, noisegeneratorsettings, this.globalFluidPicker, Blender.of(region));
            this.surfaceSystem.buildSurface(region.getBiomeManager(), region.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), noisegeneratorsettings.useLegacyRandomSource(), worldgenerationcontext, access, noisechunk, noisegeneratorsettings.surfaceRule());
        }
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, BiomeManager biomeManager, StructureFeatureManager structureFeatureManager, ChunkAccess access, GenerationStep.Carving carving) {
        BiomeManager biomemanager = biomeManager.withDifferentSource((p_188620_, p_188621_, p_188622_) -> {
            return this.biomeSource.getNoiseBiome(p_188620_, p_188621_, p_188622_, this.climateSampler());
        });
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(RandomSupport.seedUniquifier()));
        int i = 8;
        ChunkPos chunkpos = access.getPos();
        NoiseChunk noisechunk = access.getOrCreateNoiseChunk(this.sampler, () -> {
            return new VEBeardifier(structureFeatureManager, access);
        }, this.settings, this.globalFluidPicker, Blender.of(region));
        Aquifer aquifer = noisechunk.aquifer(); // TODO: Investigate
        CarvingContext carvingcontext = new CarvingContext(this, region.registryAccess(), access.getHeightAccessorForGeneration(), noisechunk);
        CarvingMask carvingmask = ((ProtoChunk)access).getOrCreateCarvingMask(carving);

        for(int j = -8; j <= 8; ++j) {
            for(int k = -8; k <= 8; ++k) {
                ChunkPos chunkpos1 = new ChunkPos(chunkpos.x + j, chunkpos.z + k);
                ChunkAccess chunkaccess = region.getChunk(chunkpos1.x, chunkpos1.z);
                BiomeGenerationSettings biomegenerationsettings = chunkaccess.carverBiome(() -> {
                    return this.biomeSource.getNoiseBiome(QuartPos.fromBlock(chunkpos1.getMinBlockX()), 0, QuartPos.fromBlock(chunkpos1.getMinBlockZ()), this.climateSampler());
                }).getGenerationSettings();
                List<Supplier<ConfiguredWorldCarver<?>>> list = biomegenerationsettings.getCarvers(carving);
                ListIterator<Supplier<ConfiguredWorldCarver<?>>> listiterator = list.listIterator();

                while(listiterator.hasNext()) {
                    int l = listiterator.nextIndex();
                    ConfiguredWorldCarver<?> configuredworldcarver = listiterator.next().get();
                    worldgenrandom.setLargeFeatureSeed(seed + (long)l, chunkpos1.x, chunkpos1.z);
                    if (configuredworldcarver.isStartChunk(worldgenrandom)) {
                        configuredworldcarver.carve(carvingcontext, access, biomemanager::getBiome, worldgenrandom, aquifer, chunkpos1, carvingmask);
                    }
                }
            }
        }
    }


}
