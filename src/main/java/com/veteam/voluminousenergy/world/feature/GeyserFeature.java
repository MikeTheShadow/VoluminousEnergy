package com.veteam.voluminousenergy.world.feature;

import com.mojang.datafixers.Dynamic;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.CrudeOil;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;
import java.util.function.Function;

public class GeyserFeature extends Feature<NoFeatureConfig> {
    public static GeyserFeature INSTANCE = new GeyserFeature(NoFeatureConfig::deserialize);

    private final BlockState crudeOil = CrudeOil.CRUDE_OIL.getDefaultState().getBlockState();
    public GeyserFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        ///int base = worldIn.getChunk(pos).getTopFilledSegment();
        int base = 40;
        if (base - 25 > pos.getY()){
            return false;
        } else if (base < pos.getY()) {
            return false;
        }

        for(int i = -2; i <= 2; ++i) {
            for(int j = -2; j <= 2; ++j) {
                if (worldIn.isAirBlock(pos.add(i, -1, j)) && worldIn.isAirBlock(pos.add(i, -2, j))) {
                    return false;
                }
            }
        }

        int totalBuckets = 0;


        // Underground portion
        int baseWidth = 50; //these values are double what they are set as
        int baseLength = 50; //Both should be an odd number so that they can have a middle point
        int baseHeight = -11;

        int increase = baseHeight/2;
        for(int depth = baseHeight; depth <= 0; ++depth) { // depth, initial negative is lowest point, <= highest point

            for(int widthX = (-1 * baseWidth) - increase; widthX < baseWidth + increase; ++widthX) { // width on x

                for(int lengthZ = (-1 * baseLength) - increase; lengthZ < baseLength + increase; ++lengthZ) { // length on z

                    if (pos.getY() + depth >= 1 && pos.getY() + depth <= 250) {
                        worldIn.setBlockState(
                                pos.add(
                                widthX,
                                depth,
                                lengthZ),
                                this.crudeOil, 2
                        );
                        totalBuckets++;
                    }
                }
            }
            if(depth > baseHeight / 2)increase--;
            else increase ++;
        }

        // Main Geyser
        int additive = 0;
        for(int depth = 150; depth > -5; --depth) { // depth, initial negative is lowest point, <= highest point
            if(depth % 50 == 0) additive += 1;
            for(int widthX = -additive - 1; widthX < additive; ++widthX) { // width on x
                for(int lengthZ = -additive - 1; lengthZ < additive; ++lengthZ) { // length on z
                    if (pos.getY() + depth >= 1 && pos.getY() + depth <= 250){
                        worldIn.setBlockState(pos.add(widthX, depth, lengthZ), this.crudeOil, 2);
                        totalBuckets++;
                    }
                }
            }
        }
        //VoluminousEnergy.LOGGER.info("Total buckets: " + totalBuckets);


        //worldIn.setBlockState(pos, this.crudeOil, 2);

        /* Structure based on Desert well
        for(int l = -1; l <= 0; ++l) {
            for(int l1 = -2; l1 <= 2; ++l1) {
                for(int k = -2; k <= 2; ++k) {
                    worldIn.setBlockState(pos.add(l1, l, k), this.crudeOil, 2);
                }
            }
        }

        worldIn.setBlockState(pos, this.crudeOil, 2);

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            worldIn.setBlockState(pos.offset(direction), this.crudeOil, 2);
        }

        for(int i1 = -2; i1 <= 2; ++i1) {
            for(int i2 = -2; i2 <= 2; ++i2) {
                if (i1 == -2 || i1 == 2 || i2 == -2 || i2 == 2) {
                    worldIn.setBlockState(pos.add(i1, 1, i2), this.crudeOil, 2);
                }
            }
        }

        worldIn.setBlockState(pos.add(2, 1, 0), this.crudeOil, 2);
        worldIn.setBlockState(pos.add(-2, 1, 0), this.crudeOil, 2);
        worldIn.setBlockState(pos.add(0, 1, 2), this.crudeOil, 2);
        worldIn.setBlockState(pos.add(0, 1, -2), this.crudeOil, 2);

        for(int j1 = -1; j1 <= 1; ++j1) {
            for(int j2 = -1; j2 <= 1; ++j2) {
                if (j1 == 0 && j2 == 0) {
                    worldIn.setBlockState(pos.add(j1, 4, j2), this.crudeOil, 2);
                } else {
                    worldIn.setBlockState(pos.add(j1, 4, j2), this.crudeOil, 2);
                }
            }
        }

        for(int k1 = 1; k1 <= 3; ++k1) {
            worldIn.setBlockState(pos.add(-1, k1, -1), this.crudeOil, 2);
            worldIn.setBlockState(pos.add(-1, k1, 1), this.crudeOil, 2);
            worldIn.setBlockState(pos.add(1, k1, -1), this.crudeOil, 2);
            worldIn.setBlockState(pos.add(1, k1, 1), this.crudeOil, 2);
        }
        */
        return true;


    }
}
