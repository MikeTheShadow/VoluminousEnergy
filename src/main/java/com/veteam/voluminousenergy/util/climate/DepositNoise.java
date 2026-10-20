package com.veteam.voluminousenergy.util.climate;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

/**
 * Eight octaves of simplex noise summed as Minecraft's former {@code PerlinSimplexNoise} did for
 * octaves 0 to -7, so scanned fluid amounts keep the same noise for a seed. Values lie in [-1, 1].
 */
public class DepositNoise {
    private static final int OCTAVES = 8;

    private DepositNoise() {
    }

    public static double sample(long seed, int x, int z) {
        RandomSource random = new XoroshiroRandomSource(seed);
        double value = 0.0D;
        double inputFactor = 1.0D;
        double valueFactor = 1.0D / ((1 << OCTAVES) - 1);
        for (int octave = 0; octave < OCTAVES; ++octave) {
            SimplexNoise noise = new SimplexNoise(random);
            value += noise.get(x * inputFactor, z * inputFactor) * valueFactor;
            inputFactor /= 2.0D;
            valueFactor *= 2.0D;
        }
        return value;
    }
}
