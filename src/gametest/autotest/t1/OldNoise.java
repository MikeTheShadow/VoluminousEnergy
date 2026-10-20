import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

/**
 * Samples the 26.2 petroleum reservoir boundary noise (first octave -4, amplitudes 1 and 1) on the
 * 26.2 vanilla jar, for autotest T1.1.
 */
public class OldNoise {
    public static void main(String[] args) {
        PositionalRandomFactory factory = new XoroshiroRandomSource(Long.parseLong(args[0])).forkPositional();
        NormalNoise noise = NormalNoise.create(factory.fromHashOf("voluminousenergy:petroleum_reservoir_boundary"),
                new NormalNoise.NoiseParameters(-4, new DoubleArrayList(new double[]{1.0, 1.0})));
        StringBuilder out = new StringBuilder();
        for (int x = -4000; x <= 4000; x += 37) {
            for (int y = -64; y <= 128; y += 16) {
                for (int z = -4000; z <= 4000; z += 41) {
                    out.append(noise.getValue(x, y, z)).append('\n');
                }
            }
        }
        System.out.print(out);
    }
}
