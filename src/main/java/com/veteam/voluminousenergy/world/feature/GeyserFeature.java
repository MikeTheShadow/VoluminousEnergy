package com.veteam.voluminousenergy.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.Arrays;

public class GeyserFeature extends Feature<GeyserFeature.Configuration> {

    public GeyserFeature(Codec<GeyserFeature.Configuration> geyserConfiguration) {
        super(geyserConfiguration);
    }

    public final ArrayList<Block> allowList = new ArrayList<>(Arrays.asList(
            Blocks.SNOW,Blocks.ICE,Blocks.PACKED_ICE,Blocks.SANDSTONE,Blocks.SAND,Blocks.RED_SAND,Blocks.GRAVEL,
            Blocks.WATER,Blocks.LAVA,Blocks.CLAY,Blocks.MOSS_BLOCK,Blocks.MOSS_CARPET,Blocks.FLOWERING_AZALEA,
            Blocks.AZALEA,Blocks.GRASS_BLOCK,Blocks.SHORT_GRASS,Blocks.TALL_GRASS,Blocks.BIG_DRIPLEAF,Blocks.BIG_DRIPLEAF_STEM,
            Blocks.SMALL_DRIPLEAF,Blocks.CAVE_VINES,Blocks.CAVE_VINES_PLANT,Blocks.SCULK,Blocks.SCULK_VEIN,
            Blocks.SCULK_CATALYST
    ));

    @Override
    public boolean place(FeaturePlaceContext<GeyserFeature.Configuration> context) {
        BlockPos pos = context.origin();
        WorldGenLevel worldIn = context.level();
        RandomSource rand = context.random();
        FluidState fluidState = context.config().fluidState;

        ///int base = worldIn.getChunk(pos).getTopFilledSegment();
        int base = 40;
        if (base - 25 > pos.getY()){
            return false;
        } else if (base < pos.getY()) {
            return false;
        }

        for(int i = -2; i <= 2; ++i) {
            for(int j = -2; j <= 2; ++j) {
                if (worldIn.isEmptyBlock(pos.offset(i, -1, j)) && worldIn.isEmptyBlock(pos.offset(i, -2, j))) {
                    return false;
                }
            }
        }

        // Setup Geyser top
        while(worldIn.isEmptyBlock(pos) && pos.getY() > 2) {
            pos = pos.below();
        }

        pos = pos.above(rand.nextInt(4));
        int height = 136; //91; // Height
        int thickness = height / 20; //height / 20; // Thickness

        if (thickness > 1 && rand.nextInt(60) == 0) {
            pos = pos.above(10 + rand.nextInt(30));
        }

        // Actual main Geyser Generation
        for(int k = 0; k < height; ++k) {
            float f = (1.0F - (float)k / (float)height) * (float)thickness;
            int l = Mth.ceil(f);

            for(int i1 = -l; i1 <= l; ++i1) {
                float f1 = (float)Mth.abs(i1) - 0.25F;

                for(int j1 = -l; j1 <= l; ++j1) {
                    float f2 = (float)Mth.abs(j1) - 0.25F;
                    if ((i1 == 0 && j1 == 0 || !(f1 * f1 + f2 * f2 > f * f)) && (i1 != -l && i1 != l && j1 != -l && j1 != l || !(rand.nextFloat() > 0.75F))) {
                        BlockState blockstate = worldIn.getBlockState(pos.offset(i1, k, j1));
                        Block block = blockstate.getBlock();
                        if (blockstate.isAir() || isDirt(blockstate) || isStone(blockstate) || allowList.contains(block)) {
                            this.setBlock(worldIn, pos.offset(i1, k, j1), fluidState.createLegacyBlock());
                            worldIn.scheduleTick(pos.offset(i1, k, j1), fluidState.getType(), 0);
                        }

                        if (k != 0 && l > 1) {
                            blockstate = worldIn.getBlockState(pos.offset(i1, -k, j1));
                            block = blockstate.getBlock();
                            if (blockstate.isAir() || isDirt(blockstate) || isStone(blockstate) || allowList.contains(block) ) {
                                this.setBlock(worldIn, pos.offset(i1, -k, j1), fluidState.createLegacyBlock());
                                worldIn.scheduleTick(pos.offset(i1, -k, j1), fluidState.getType(), 0);
                            }
                        }
                    }
                }
            }
        }

        // Set up Geyser bottom generation
        while(worldIn.isEmptyBlock(pos) && pos.getY() > 2) {
            pos = pos.below();
        }

        pos = pos.above(rand.nextInt(4));
        int height1 = 40; // Height
        int thickness1 = 20; // Thickness

        // Actual Geyser Bottom Generation
        for(int y = pos.getY(); y > 0; --y) {
            float fy = (1.0F - (float)y / (float)height1) * (float)thickness1;
            int l = Mth.ceil(fy);

            for(int x = -l; x <= l; ++x) {
                float fx = (float)Mth.abs(x) - 0.25F;

                for(int z = -l; z <= l; ++z) {
                    float fz = (float)Mth.abs(z) - 0.25F;
                    if ((x == 0 && z == 0 || !(fx * fx + fz * fz > fy * fy)) && (x != -l && x != l && z != -l && z != l || !(rand.nextFloat() > 0.75F))) {
                        BlockState blockstate = worldIn.getBlockState(pos.offset(x, y, z));
                        Block block = blockstate.getBlock();
                        if (blockstate.isAir() || isDirt(blockstate) || isStone(blockstate) || allowList.contains(block)) {
                            this.setBlock(worldIn, pos.offset(x, y, z), fluidState.createLegacyBlock());
                            worldIn.scheduleTick(pos.offset(x, y, z), fluidState.getType(), 0);
                        }

                        if (y != 0 && l > 1) {
                            blockstate = worldIn.getBlockState(pos.offset(x, -y, z));
                            block = blockstate.getBlock();
                            if (blockstate.isAir() || isDirt(blockstate) || isStone(blockstate) || allowList.contains(block) ) {
                                this.setBlock(worldIn, pos.offset(x, -y, z), fluidState.createLegacyBlock());
                                worldIn.scheduleTick(pos.offset(x, -y, z), fluidState.getType(), 0);
                            }
                        }
                    }
                }
            }
        }

        /* Main Geyser
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
        */

        //VoluminousEnergy.LOGGER.info("Total buckets: " + totalBuckets);
        return true;
    }

    // TODO: I'm pretty sure unused
//    void generateUnderground(LevelAccessor worldIn, BlockPos pos, Random rand){
//
//        int height = -50; // Height
//        int thickness = -50; // Thickness
//
//        // Underground Generation
//        for(int k = 0; k > abs(height); --k) {
//            float f = (1.0F - (float)k / (float)height) * (float)thickness;
//            int l = Mth.ceil(f);
//
//            for(int i1 = -l; i1 <= l; ++i1) {
//                float f1 = (float)Mth.abs(i1) - 0.25F;
//
//                for(int j1 = -l; j1 <= l; ++j1) {
//                    float f2 = (float)Mth.abs(j1) - 0.25F;
//                    if ((i1 == 0 && j1 == 0 || !(f1 * f1 + f2 * f2 > f * f)) && (i1 != -l && i1 != l && j1 != -l && j1 != l || !(rand.nextFloat() > 0.75F))) {
//                        BlockState blockstate = worldIn.getBlockState(pos.offset(i1, k, j1));
//                        Block block = blockstate.getBlock();
//                        if (blockstate.isAir() || isDirt(blockstate) || isStone(blockstate) || allowList.contains(block)) {
//                            this.setBlock(worldIn, pos.offset(i1, k, j1), crudeOil);
//                        }
//
//                        if (k != 0 && l > 1) {
//                            blockstate = worldIn.getBlockState(pos.offset(i1, -k, j1));
//                            block = blockstate.getBlock();
//                            if (blockstate.isAir() || isDirt(blockstate) || isStone(blockstate) || allowList.contains(block) ) {
//                                this.setBlock(worldIn, pos.offset(i1, -k, j1), crudeOil);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    public static record Configuration(FluidState fluidState) implements FeatureConfiguration {
        public static final Codec<GeyserFeature.Configuration> CODEC =
                FluidState.CODEC.fieldOf("fluidstate")
                        .xmap(GeyserFeature.Configuration::new, (fluidState) -> fluidState.fluidState).codec();
    }

}
