package com.veteam.voluminousenergy.world.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

@Deprecated
public class OreData {
    private TagKey<Biome> whitelistedBiomeTag;
    private TagKey<Biome> blacklistedBiomeTag;
    private float discard;
    private int count;
    private int size;
    private int bottomAnchor;
    private int topAnchor;
    private int rarity;

    public final Codec<OreData> ORE_DATA_CODEC = RecordCodecBuilder.create((test) -> {
       return test.group(
               Codec.STRING.fieldOf("whitelisted_biomes").forGetter(whitelistedBiomeTag -> this.whitelistedBiomeTag.toString()),
               Codec.STRING.fieldOf("blacklisted_biomes").forGetter(blacklistedBiomeTag -> this.blacklistedBiomeTag.toString()),
               Codec.FLOAT.fieldOf("discard").forGetter(discard -> this.discard),
               Codec.INT.fieldOf("count").forGetter(count -> this.count),
               Codec.INT.fieldOf("size").forGetter(size -> this.size),
               Codec.INT.fieldOf("bottom_anchor").forGetter(bottomAnchor -> this.bottomAnchor),
               Codec.INT.fieldOf("top_anchor").forGetter(topAnchor -> this.topAnchor),
               Codec.INT.fieldOf("rarity").forGetter(rarity -> this.rarity)
       ).apply(test, OreData::new);
    });


    public OreData(String whitelistedBiomeTag,
                   String blacklistedBiomeTag,
                   float discard,
                   int count,
                   int size,
                   int bottomAnchor,
                   int topAnchor,
                   int rarity){

        this.whitelistedBiomeTag = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(whitelistedBiomeTag));
        this.blacklistedBiomeTag = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(blacklistedBiomeTag));
        this.discard = discard;
        this.count = count;
        this.size = size;
        this.bottomAnchor = bottomAnchor;
        this.topAnchor = topAnchor;
        this.rarity = rarity;
    }
}
