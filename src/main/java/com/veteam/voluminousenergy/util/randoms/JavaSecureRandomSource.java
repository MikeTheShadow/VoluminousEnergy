package com.veteam.voluminousenergy.util.randoms;

import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.MarsagliaPolarGaussian;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import org.apache.commons.io.Charsets;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class JavaSecureRandomSource implements RandomSource {

    private SecureRandom randomNumberGenerator;
    private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

    // WARN: not 128 bit
    public JavaSecureRandomSource(byte[] seed){
        this.randomNumberGenerator = new SecureRandom(seed);
    }

    public JavaSecureRandomSource(long seed){
        this.randomNumberGenerator = new SecureRandom(longToByte(seed));
    }

    @Override
    public RandomSource fork() {
        return new JavaSecureRandomSource(this.randomNumberGenerator.nextLong());
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return new JavaSecureRandomPositionalRandomFactory(this.randomNumberGenerator.nextLong());
    }

    @Override
    public void setSeed(long seed) {
        this.randomNumberGenerator = new SecureRandom(longToByte(seed));
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

    public static byte[] longToByte(long seed){
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(seed);
        return buffer.array();
    }

    public static class JavaSecureRandomPositionalRandomFactory implements PositionalRandomFactory {
        private final long seed;

        public JavaSecureRandomPositionalRandomFactory(long seed){
            this.seed = seed;
        }

        @Override
        public RandomSource fromHashOf(String string) {
            byte[] abyte = Hashing.goodFastHash(128).hashString(string, Charsets.UTF_8).asBytes();
            // goodFastHash instead of md5. Not sure if it's actually faster like the JavaDoc says, but MD5 is...
            long i = Longs.fromBytes(abyte[0], abyte[1], abyte[2], abyte[3], abyte[4], abyte[5], abyte[6], abyte[7]);

            return new JavaSecureRandomSource(i ^ this.seed);
        }

        @Override
        public RandomSource at(int one, int two, int three) {
            return new JavaSecureRandomSource(Mth.getSeed(one, two, three));
        }

        @Override
        public void parityConfigString(StringBuilder stringBuilder) {
            stringBuilder.append("seed: ").append(this.seed);
        }
    }
}
