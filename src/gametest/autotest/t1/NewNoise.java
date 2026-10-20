import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Samples the committed petroleum reservoir boundary noise file on the 26.3 vanilla jar, at the same
 * points as OldNoise, for autotest T1.1.
 */
public class NewNoise {
    public static void main(String[] args) throws Exception {
        PositionalRandomFactory factory = new XoroshiroRandomSource(Long.parseLong(args[0])).forkPositional();
        NormalNoise parameters = NormalNoise.DIRECT_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(Path.of(args[1])))).getOrThrow();
        Noise noise = parameters.create(factory.fromHashOf("voluminousenergy:petroleum_reservoir_boundary"));
        StringBuilder out = new StringBuilder();
        for (int x = -4000; x <= 4000; x += 37) {
            for (int y = -64; y <= 128; y += 16) {
                for (int z = -4000; z <= 4000; z += 41) {
                    out.append(noise.get(x, y, z)).append('\n');
                }
            }
        }
        System.out.print(out);
    }
}
