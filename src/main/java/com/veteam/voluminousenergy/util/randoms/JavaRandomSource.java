package com.veteam.voluminousenergy.util.randoms;

import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.MarsagliaPolarGaussian;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import org.apache.commons.io.Charsets;

import java.util.Random;

public class JavaRandomSource implements RandomSource {
    private Random randomNumberGenerator;
    private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

    // WARN: not 128 bit
    public JavaRandomSource(long seed){
        this.randomNumberGenerator = new Random(seed);
    }

    @Override
    public RandomSource fork() {
        return new JavaRandomSource(this.randomNumberGenerator.nextLong());
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return new JavaRandomPositionalRandomFactory(this.randomNumberGenerator.nextLong());
    }

    @Override
    public void setSeed(long seed) {
        this.randomNumberGenerator = new Random(seed);
        this.gaussianSource.reset();
    }

    @Override
    public int nextInt() {
        return this.randomNumberGenerator.nextInt();
    }

    @Override
    public int nextInt(int bound) {
        return this.randomNumberGenerator.nextInt(bound);
    }

    @Override
    public long nextLong() {
        return this.randomNumberGenerator.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return this.randomNumberGenerator.nextBoolean();
    }

    @Override
    public float nextFloat() {
        return this.randomNumberGenerator.nextFloat();
    }

    @Override
    public double nextDouble() {
        return this.randomNumberGenerator.nextDouble();
    }

    @Override
    public double nextGaussian() {
        return this.gaussianSource.nextGaussian();
    }

    public static class JavaRandomPositionalRandomFactory implements PositionalRandomFactory {
        private final long seed;

        public JavaRandomPositionalRandomFactory(long seed){
            this.seed = seed;
        }

        @Override
        public RandomSource fromHashOf(String string) {
            byte[] abyte = Hashing.goodFastHash(128).hashString(string, Charsets.UTF_8).asBytes();
            // goodFastHash instead of md5. Not sure if it's actually faster like the JavaDoc says, but MD5 is...
            long i = Longs.fromBytes(abyte[0], abyte[1], abyte[2], abyte[3], abyte[4], abyte[5], abyte[6], abyte[7]);

            return new JavaRandomSource(i ^ this.seed);
        }

        @Override
        public RandomSource at(int one, int two, int three) {
            return new JavaRandomSource(Mth.getSeed(one, two, three));
        }

        @Override
        public void parityConfigString(StringBuilder stringBuilder) {
            stringBuilder.append("seed: ").append(this.seed);
        }
    }
}
