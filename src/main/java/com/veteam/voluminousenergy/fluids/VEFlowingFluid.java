package com.veteam.voluminousenergy.fluids;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.Map;

public class VEFlowingFluid extends ForgeFlowingFluid {
    private int flowWidth;
    private Properties fluidProperties;
    // DANGER
    public static final BooleanProperty FALLING = BlockStateProperties.FALLING;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_FLOWING;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>(200) {
            protected void rehash(int p_76102_) {
            }
        };
        object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
        return object2bytelinkedopenhashmap;
    });
    //

    protected VEFlowingFluid(Properties properties, int blocksToFlowOutWide) {
        super(properties);
        this.flowWidth = blocksToFlowOutWide;
    }

    // DANGEROUS CODE GOING HERE
    public void tick(Level level, BlockPos blockPos, FluidState fluidState) {
        if (!fluidState.isSource()) {
            FluidState fluidstate = this.getNewLiquid(level, blockPos, level.getBlockState(blockPos));
            int i = this.getSpreadDelay(level, blockPos, fluidState, fluidstate);
            if (fluidstate.isEmpty()) {
                fluidState = fluidstate;
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
            } else if (!fluidstate.equals(fluidState)) {
                fluidState = fluidstate;
                BlockState blockstate = fluidstate.createLegacyBlock();
                level.setBlock(blockPos, blockstate, 2);
                level.scheduleTick(blockPos, fluidstate.getType(), i);
                level.updateNeighborsAt(blockPos, blockstate.getBlock());
            }
        }

        this.spread(level, blockPos, fluidState);
    }

    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> stateDefinitionBuilder) {
        super.createFluidStateDefinition(stateDefinitionBuilder);
        stateDefinitionBuilder.add(LEVEL);
    }

    @Override
    public Vec3 getFlow(BlockGetter getter, BlockPos pos, FluidState fluidState) {
        double d0 = 0.0D;
        double d1 = 0.0D;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            blockpos$mutableblockpos.setWithOffset(pos, direction);
            FluidState fluidstate = getter.getFluidState(blockpos$mutableblockpos);
            if (this.affectsFlow(fluidstate)) {
                float f = fluidstate.getOwnHeight();
                float f1 = 0.0F;
                if (f == 0.0F) {
                    if (!getter.getBlockState(blockpos$mutableblockpos).getMaterial().blocksMotion()) {
                        BlockPos blockpos = blockpos$mutableblockpos.above(); // flipped
                        FluidState fluidstate1 = getter.getFluidState(blockpos);
                        if (this.affectsFlow(fluidstate1)) {
                            f = fluidstate1.getOwnHeight();
                            if (f > 0.0F) {
                                f1 = fluidState.getOwnHeight() - (f - 0.8888889F);
                            }
                        }
                    }
                } else if (f > 0.0F) {
                    f1 = fluidState.getOwnHeight() - f;
                }

                if (f1 != 0.0F) {
                    d0 += (float)direction.getStepX() * f1;
                    d1 += (float)direction.getStepZ() * f1;
                }
            }
        }

        Vec3 vec3 = new Vec3(d0, 0.0D, d1);
        if (fluidState.getValue(FALLING)) {
            for(Direction direction1 : Direction.Plane.HORIZONTAL) {
                blockpos$mutableblockpos.setWithOffset(pos, direction1);
                if (this.isSolidFace(getter, blockpos$mutableblockpos, direction1) || this.isSolidFace(getter, blockpos$mutableblockpos.above(), direction1)) {
                    vec3 = vec3.normalize().subtract(0.0D, -6.0D, 0.0D);
                    break;
                }
            }
        }

        return vec3.normalize();
    }

    private boolean affectsFlow(FluidState fluidState) {
        return fluidState.isEmpty() || fluidState.getType().isSame(this);
    }

    protected void spread(LevelAccessor levelAccessor, BlockPos blockPos, FluidState fluidState) {
        if (!fluidState.isEmpty()) {
            if (!(blockPos.getY() < 320)) return;

            BlockState blockstate = levelAccessor.getBlockState(blockPos);
            BlockPos blockpos = blockPos.above();
            BlockState blockstate1 = levelAccessor.getBlockState(blockpos);
            FluidState fluidstate = this.getNewLiquid(levelAccessor, blockpos.below(), blockstate1); // may Need to be reworked
            System.out.println("SPREAD CALLED");
            if (this.canSpreadTo(levelAccessor, blockPos, blockstate, Direction.UP, blockpos, blockstate1, levelAccessor.getFluidState(blockpos), fluidstate.getType())) {
                System.out.println("CAN SPREAD FIRST IF PASS");
                this.spreadTo(levelAccessor, blockpos, blockstate1, Direction.UP, fluidstate);
                if (this.sourceNeighborCount(levelAccessor, blockPos) >= 3 || this.isSource(fluidState)) {
                    System.out.println("CAN SPREAD: SPREAD TO SIDES");
                    this.spreadToSides(levelAccessor, blockPos, fluidState, blockstate);
                }
            } else if (fluidState.isSource() || !this.isWaterHole(levelAccessor, fluidstate.getType(), blockPos, blockstate, blockpos, blockstate1)) {
                System.out.println("SECOND IF PASS FOR SPREAD CHECK");
                this.spreadToSides(levelAccessor, blockPos, fluidState, blockstate);
            } else {
                System.out.println("TOTAL FAILURE FOR SPREAD CHECKS");
            }

        }
    }

    private void spreadToSides(LevelAccessor levelAccessor, BlockPos pos, FluidState fluidState, BlockState blockState) {
        int i = fluidState.getAmount() - this.getDropOff(levelAccessor);
        if (fluidState.getValue(FALLING)) {
            i = 7;
        }

        if (i > 0) {
            Map<Direction, FluidState> map = this.getSpread(levelAccessor, pos, blockState);

            for(Map.Entry<Direction, FluidState> entry : map.entrySet()) {
                Direction direction = entry.getKey();
                FluidState fluidstate = entry.getValue();
                BlockPos blockpos = pos.relative(direction);
                BlockState blockstate = levelAccessor.getBlockState(blockpos);
                if (this.canSpreadTo(levelAccessor, pos, blockState, direction, blockpos, blockstate, levelAccessor.getFluidState(blockpos), fluidstate.getType())) {
                    this.spreadTo(levelAccessor, blockpos, blockstate, direction, fluidstate);
                }
            }

        }
    }

    private int sourceNeighborCount(LevelReader levelReader, BlockPos pos) {
        int i = 0;

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.relative(direction);
            FluidState fluidstate = levelReader.getFluidState(blockpos);
            if (this.isSourceBlockOfThisType(fluidstate)) {
                ++i;
            }
        }

        return i;
    }

    private boolean isSourceBlockOfThisType(FluidState fluidState) {
        return fluidState.getType().isSame(this) && fluidState.isSource();
    }

    private boolean isWaterHole(BlockGetter getter, Fluid fluid, BlockPos pos0, BlockState blockState0, BlockPos pos1, BlockState blockState1) {
        if (!this.canPassThroughWall(Direction.UP, getter, pos0, blockState0, pos1, blockState1)) {
            return false;
        } else {
            return blockState1.getFluidState().getType().isSame(this) || this.canHoldFluid(getter, pos1, blockState1, fluid);
        }
    }

    private boolean canPassThroughWall(Direction p_76062_, BlockGetter p_76063_, BlockPos p_76064_, BlockState p_76065_, BlockPos p_76066_, BlockState p_76067_) {
        Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2bytelinkedopenhashmap;
        if (!p_76065_.getBlock().hasDynamicShape() && !p_76067_.getBlock().hasDynamicShape()) {
            object2bytelinkedopenhashmap = OCCLUSION_CACHE.get();
        } else {
            object2bytelinkedopenhashmap = null;
        }

        Block.BlockStatePairKey block$blockstatepairkey;
        if (object2bytelinkedopenhashmap != null) {
            block$blockstatepairkey = new Block.BlockStatePairKey(p_76065_, p_76067_, p_76062_);
            byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$blockstatepairkey);
            if (b0 != 127) {
                return b0 != 0;
            }
        } else {
            block$blockstatepairkey = null;
        }

        VoxelShape voxelshape1 = p_76065_.getCollisionShape(p_76063_, p_76064_);
        VoxelShape voxelshape = p_76067_.getCollisionShape(p_76063_, p_76066_);
        boolean flag = !Shapes.mergedFaceOccludes(voxelshape1, voxelshape, p_76062_);
        if (object2bytelinkedopenhashmap != null) {
            if (object2bytelinkedopenhashmap.size() == 200) {
                object2bytelinkedopenhashmap.removeLastByte();
            }

            object2bytelinkedopenhashmap.putAndMoveToFirst(block$blockstatepairkey, (byte)(flag ? 1 : 0));
        }

        return flag;
    }

    private boolean canHoldFluid(BlockGetter getter, BlockPos pos, BlockState blockState, Fluid fluid) {
        Block block = blockState.getBlock();
        if (block instanceof LiquidBlockContainer) {
            return ((LiquidBlockContainer)block).canPlaceLiquid(getter, pos, blockState, fluid);
        } else if (!(block instanceof DoorBlock) && !blockState.is(BlockTags.SIGNS) && !blockState.is(Blocks.LADDER) && !blockState.is(Blocks.SUGAR_CANE) && !blockState.is(Blocks.BUBBLE_COLUMN)) {
            Material material = blockState.getMaterial();
            if (material != Material.PORTAL && material != Material.STRUCTURAL_AIR && material != Material.WATER_PLANT && material != Material.REPLACEABLE_WATER_PLANT) {
                return !material.blocksMotion();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    @Override
    protected void spreadTo(LevelAccessor accessor, BlockPos pos, BlockState blockStateInQuestion, Direction p_76008_, FluidState fluidState) {
        if (blockStateInQuestion.getBlock() instanceof LiquidBlockContainer) {
            System.out.println("spreadTo: instanceof LiquidBlockContainer for pos: " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
            ((LiquidBlockContainer)blockStateInQuestion.getBlock()).placeLiquid(accessor, pos, blockStateInQuestion, fluidState);
        } else {
            System.out.println("spreadTo: else hit ");

            if (!blockStateInQuestion.isAir()) {
                this.beforeDestroyingBlock(accessor, pos, blockStateInQuestion);
                System.out.println("spreadTo: before destroying block ");

            }

            accessor.setBlock(pos, fluidState.createLegacyBlock(), 3);
        }

    }

    /*
    @Override
    protected FluidState getNewLiquid(LevelReader levelReader, BlockPos pos, BlockState blockState) {
        int i = 0;
        int j = 0;

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.relative(direction);
            BlockState blockstate = levelReader.getBlockState(blockpos);
            FluidState fluidstate = blockstate.getFluidState();
            if (fluidstate.getType().isSame(this) && this.canPassThroughWall(direction, levelReader, pos, blockState, blockpos, blockstate)) {
                if (fluidstate.isSource() && net.minecraftforge.event.ForgeEventFactory.canCreateFluidSource(levelReader, blockpos, blockstate, this.canConvertToSource())) {
                    ++j;
                }

                i = Math.max(i, fluidstate.getAmount());
            }
        }

        if (j >= 2) {
            BlockState blockstate1 = levelReader.getBlockState(pos.above());//flipped
            FluidState fluidstate1 = blockstate1.getFluidState();
            if (blockstate1.getMaterial().isSolid() || this.isSourceBlockOfThisType(fluidstate1)) {
                return this.getSource(false);
            }
        }

        BlockPos blockpos1 = pos.below();//flipped
        BlockState blockstate2 = levelReader.getBlockState(blockpos1);
        FluidState fluidstate2 = blockstate2.getFluidState();
        if (!fluidstate2.isEmpty() && fluidstate2.getType().isSame(this) && this.canPassThroughWall(Direction.DOWN, levelReader, pos, blockState, blockpos1, blockstate2)) { // Direction.UP ORIGNIAL
            return this.getFlowing(8, true);
        } else {
            int k = i - this.getDropOff(levelReader);
            return k <= 0 ? Fluids.EMPTY.defaultFluidState() : this.getFlowing(k, false);
        }
    }*/

    //

    @Override
    public boolean isSource(FluidState fluidState) {
        return false;
    }

    @Override
    public int getAmount(FluidState fluidState) {
        return this.flowWidth;
    }

    public static class Flowing extends VEFlowingFluid {

        protected Flowing(Properties properties, int blocksToFlowOutWide) {
            super(properties, blocksToFlowOutWide);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends VEFlowingFluid {
        private final int flowWidth;

        protected Source(Properties properties, int blocksToFlowOutWide) {
            super(properties, blocksToFlowOutWide);
            this.flowWidth = blocksToFlowOutWide;
        }

        @Override
        public int getAmount(FluidState state) {
            return this.flowWidth;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
