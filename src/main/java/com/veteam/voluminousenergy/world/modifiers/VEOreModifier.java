package com.veteam.voluminousenergy.world.modifiers;

import com.mojang.serialization.Codec;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.RegistryLookups;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Deprecated
public class VEOreModifier implements BiomeModifier {

//    private List<OreConfiguration.TargetBlockState> oreConfiguration;
    private ArrayList<TagKey<Biome>> taggedBiomeProperties;
    private List<PlacedFeature> placedFeatures;

//    public VEOreModifier (OreConfiguration.TargetBlockState... targetOreBlockStates){
//        this.oreConfiguration = List.of(targetOreBlockStates);
//    }

    public VEOreModifier (PlacedFeature... placedFeatures){
        this.placedFeatures = List.of(placedFeatures);
    }

    /** for use with @see net.minecraftforge.common.Tags#Biomes **/
    public void addTaggedBiomeProperty(TagKey<Biome> biomeTagKey){
        if (taggedBiomeProperties == null) taggedBiomeProperties = new ArrayList<>();
        taggedBiomeProperties.add(biomeTagKey);
    }

    /** for use with @see net.minecraftforge.common.Tags#Biomes **/
    @SafeVarargs
    public final void addTaggedBiomeProperty(TagKey<Biome>... biomeTagKey){
        if (taggedBiomeProperties == null) taggedBiomeProperties = new ArrayList<>();
        taggedBiomeProperties.addAll(Arrays.stream(biomeTagKey).toList());
    }

    public ArrayList<TagKey<Biome>> getTaggedBiomeProperties(){
        return (ArrayList<TagKey<Biome>>) this.taggedBiomeProperties.clone();
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD) return;

        if (Config.WORLD_GEN_LOGGING.get()){
            VoluminousEnergy.LOGGER.info(
                    "Voluminous Energy has received a Biome Modification call for: "
                            + RegistryLookups.lookupBiome(biome)
            );
        }


        if (taggedBiomeProperties != null && !taggedBiomeProperties.isEmpty()){
            // Threaded check all tags
            AtomicBoolean atomicBoolean = new AtomicBoolean(true); // Assume initially, the biome is valid
            taggedBiomeProperties.parallelStream().forEach(tag -> {
                if (!biome.containsTag(tag)) atomicBoolean.set(false);
            });

            if (atomicBoolean.get() && placedFeatures != null && !placedFeatures.isEmpty()){
                addPlacedFeatures(builder);
            } else if (!atomicBoolean.get() && Config.WORLD_GEN_LOGGING.get()){
                VoluminousEnergy.LOGGER.debug(
                        "Criteria not met for Biome: " + RegistryLookups.lookupBiome(biome)
                        + " containing keys:\n "
                        + biome.getTagKeys().toString()
                        + " needed keys:\n "
                        + this.taggedBiomeProperties.stream().toString()
                        + "\n not all needed keys are in containing keys."
                );

            }

        } else if (placedFeatures != null && !placedFeatures.isEmpty()){ // by default, if no tagged properties, assume all biomes are valid (unrestricted)
            addPlacedFeatures(builder);
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return null;
    }

    private void addPlacedFeatures(ModifiableBiomeInfo.BiomeInfo.Builder builder){
        // Normal forloops are used because I don't want atomic/thread conflict issues, since it's unlikely these data stores are atomic
        for (PlacedFeature feature : placedFeatures){
            builder.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES,Holder.direct(feature));
        }
    }

}
