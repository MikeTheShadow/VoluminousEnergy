package com.veteam.voluminousenergy.world.feature;

import com.mojang.datafixers.Dynamic;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.CrudeOil;
import com.veteam.voluminousenergy.fluids.Naphtha;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import static java.lang.StrictMath.abs;

public class GeyserFeature extends Feature<NoFeatureConfig> {
    public static GeyserFeature INSTANCE = new GeyserFeature(NoFeatureConfig::deserialize);

    private final BlockState crudeOil = CrudeOil.CRUDE_OIL.getDefaultState().getBlockState();
    public GeyserFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    public final ArrayList<Block> allowList = new ArrayList<>(Arrays.asList(Blocks.SNOW,Blocks.ICE,Blocks.PACKED_ICE,Blocks.SANDSTONE,Blocks.SAND,Blocks.RED_SAND,Blocks.GRAVEL,Blocks.WATER,Blocks.LAVA));

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

        // Setup Geyser top
        while(worldIn.isAirBlock(pos) && pos.getY() > 2) {
            pos = pos.down();
        }

        pos = pos.up(rand.nextInt(4));
        int height = 136; //91; // Height
        int thickness = height / 20; //height / 20; // Thickness

        if (thickness > 1 && rand.nextInt(60) == 0) {
            pos = pos.up(10 + rand.nextInt(30));
        }

        // Actual main Geyser Generation
        for(int k = 0; k < height; ++k) {
            float f = (1.0F - (float)k / (float)height) * (float)thickness;
            int l = MathHelper.ceil(f);

            for(int i1 = -l; i1 <= l; ++i1) {
                float f1 = (float)MathHelper.abs(i1) - 0.25F;

                for(int j1 = -l; j1 <= l; ++j1) {
                    float f2 = (float)MathHelper.abs(j1) - 0.25F;
                    if ((i1 == 0 && j1 == 0 || !(f1 * f1 + f2 * f2 > f * f)) && (i1 != -l && i1 != l && j1 != -l && j1 != l || !(rand.nextFloat() > 0.75F))) {
                        BlockState blockstate = worldIn.getBlockState(pos.add(i1, k, j1));
                        Block block = blockstate.getBlock();
                        if (blockstate.isAir(worldIn, pos.add(i1, k, j1)) || isDirt(block) || isStone(block) || allowList.contains(block.getBlock())) {
                            this.setBlockState(worldIn, pos.add(i1, k, j1), crudeOil);
                        }

                        if (k != 0 && l > 1) {
                            blockstate = worldIn.getBlockState(pos.add(i1, -k, j1));
                            block = blockstate.getBlock();
                            if (blockstate.isAir(worldIn, pos.add(i1, -k, j1)) || isDirt(block) || isStone(block) || allowList.contains(block.getBlock()) ) {
                                this.setBlockState(worldIn, pos.add(i1, -k, j1), crudeOil);
                            }
                        }
                    }
                }
            }
        }

        // Set up Geyser bottom generation
        while(worldIn.isAirBlock(pos) && pos.getY() > 2) {
            pos = pos.down();
        }

        pos = pos.up(rand.nextInt(4));
        int height1 = 40; // Height
        int thickness1 = 20; // Thickness

        // Actual Geyser Bottom Generation
        for(int y = pos.getY(); y > 0; --y) {
            float fy = (1.0F - (float)y / (float)height1) * (float)thickness1;
            int l = MathHelper.ceil(fy);

            for(int x = -l; x <= l; ++x) {
                float fx = (float)MathHelper.abs(x) - 0.25F;

                for(int z = -l; z <= l; ++z) {
                    float fz = (float)MathHelper.abs(z) - 0.25F;
                    if ((x == 0 && z == 0 || !(fx * fx + fz * fz > fy * fy)) && (x != -l && x != l && z != -l && z != l || !(rand.nextFloat() > 0.75F))) {
                        BlockState blockstate = worldIn.getBlockState(pos.add(x, y, z));
                        Block block = blockstate.getBlock();
                        if (blockstate.isAir(worldIn, pos.add(x, y, z)) || isDirt(block) || isStone(block) || allowList.contains(block.getBlock())) {
                            this.setBlockState(worldIn, pos.add(x, y, z), crudeOil);
                        }

                        if (y != 0 && l > 1) {
                            blockstate = worldIn.getBlockState(pos.add(x, -y, z));
                            block = blockstate.getBlock();
                            if (blockstate.isAir(worldIn, pos.add(x, -y, z)) || isDirt(block) || isStone(block) || allowList.contains(block.getBlock()) ) {
                                this.setBlockState(worldIn, pos.add(x, -y, z), crudeOil);
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

    void generateUnderground(IWorld worldIn, BlockPos pos, Random rand){

        int height = -50; // Height
        int thickness = -50; // Thickness

        // Underground Generation
        for(int k = 0; k > abs(height); --k) {
            float f = (1.0F - (float)k / (float)height) * (float)thickness;
            int l = MathHelper.ceil(f);

            for(int i1 = -l; i1 <= l; ++i1) {
                float f1 = (float)MathHelper.abs(i1) - 0.25F;

                for(int j1 = -l; j1 <= l; ++j1) {
                    float f2 = (float)MathHelper.abs(j1) - 0.25F;
                    if ((i1 == 0 && j1 == 0 || !(f1 * f1 + f2 * f2 > f * f)) && (i1 != -l && i1 != l && j1 != -l && j1 != l || !(rand.nextFloat() > 0.75F))) {
                        BlockState blockstate = worldIn.getBlockState(pos.add(i1, k, j1));
                        Block block = blockstate.getBlock();
                        if (blockstate.isAir(worldIn, pos.add(i1, k, j1)) || isDirt(block) || isStone(block) || allowList.contains(block.getBlock())) {
                            this.setBlockState(worldIn, pos.add(i1, k, j1), crudeOil);
                        }

                        if (k != 0 && l > 1) {
                            blockstate = worldIn.getBlockState(pos.add(i1, -k, j1));
                            block = blockstate.getBlock();
                            if (blockstate.isAir(worldIn, pos.add(i1, -k, j1)) || isDirt(block) || isStone(block) || allowList.contains(block.getBlock()) ) {
                                this.setBlockState(worldIn, pos.add(i1, -k, j1), crudeOil);
                            }
                        }
                    }
                }
            }
        }
    }

}
