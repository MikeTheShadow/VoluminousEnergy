package com.veteam.voluminousenergy.blocks.blocks.machines;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntities;
import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class StirlingGeneratorBlock extends VEFaceableMachineBlock {
    public StirlingGeneratorBlock() {
        super(Properties.of().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentBlockId())
                .sound(SoundType.METAL)
                .strength(2.0f)
                .lightLevel(l -> l.getValue(LIT) ? 13 : 0)
                .requiresCorrectToolForDrops()
        );
        setRName("stirling_generator");
        VETagDataGenerator.setRequiresPickaxe(this);
        VETagDataGenerator.setRequiresStoneAndBlacklistLowerTiers(this);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) { // Replaces old createBlockEntity method
        return VETileEntities.STIRLING_GENERATOR_FACTORY.create(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTicker(level, blockEntityType, VEBlocks.STIRLING_GENERATOR.tile().get());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            double x = (double) pos.getX() + 0.5D;
            double y = pos.getY() + 0.33D; // Adjusted height for better visual
            double z = (double) pos.getZ() + 0.5D;

            if (random.nextDouble() < 0.1D) {
                level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            var sideDirections = List.of(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH);
            for (Direction direction : sideDirections) {
                Direction.Axis axis = direction.getAxis();
                double offset = random.nextDouble() * 0.6D - 0.3D;
                double dx = axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52D : offset;
                double dy = random.nextDouble() * 4.5D / 16.0D; // Slightly reduced vertical spread
                double dz = axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52D : offset;

                level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0.0D, 0.0D, 0.0D);
                level.addParticle(ParticleTypes.FLAME, x + dx, y + dy, z + dz, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
