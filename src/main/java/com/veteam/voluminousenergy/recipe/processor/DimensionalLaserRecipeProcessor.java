package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.achievements.triggers.VECriteriaTriggers;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.persistence.SingleChunkFluid;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VEAttachments;
import com.veteam.voluminousenergy.util.VEDataComponents;
import com.veteam.voluminousenergy.util.records.ChunkFluidData;
import com.veteam.voluminousenergy.util.records.CounterLength;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Random;
import java.util.function.Supplier;

import static com.veteam.voluminousenergy.blocks.tiles.VETileEntity.DEFAULT_TANK_CAPACITY;

public class DimensionalLaserRecipeProcessor implements AbstractRecipeProcessor {

    private Supplier<? extends Block> blockSupplier;
    public DimensionalLaserRecipeProcessor(Supplier<? extends Block> block) {
        this.blockSupplier = block;
    }

    @Override
    public void tick(VETileEntity tile) {
        if (!isMultiBlockValid(tile))
            return;

        int buildTick = tile.getData(VEAttachments.BUILD_TICK);
        if (buildTick != 1000) {
            tile.setChanged();
            if (buildTick == 999) {
                int x = tile.getBlockPos().getX();
                int y = tile.getBlockPos().getY();
                int z = tile.getBlockPos().getZ();
                for (ServerPlayer serverplayer : tile.getLevel().getEntitiesOfClass(ServerPlayer.class,
                    (new AABB(x, y, z, x, y - 4, z)).inflate(50.0D, 50.0D, 50.0D))) {
                    VECriteriaTriggers.CONSTRUCT_DIMENSIONAL_LASER_TRIGGER.get().trigger(serverplayer, 3);
                }
            }

            // Pylon audio
            switch (buildTick) {
                case 50,
                     150,
                     250,
                     350 ->
                    tile.getLevel().playSound(null, tile.getBlockPos(), SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS,
                        1.0F, 1.0F);
            }

            if (buildTick == 1) {
                tile.getLevel().playSound(null, tile.getBlockPos(), VESounds.ENERGY_BEAM_ACTIVATE, SoundSource.BLOCKS,
                    1.0F, 1.0F);
            }

            if (buildTick == 400) {
                tile.getLevel().playSound(null, tile.getBlockPos(), VESounds.ENERGY_BEAM_FIRED, SoundSource.BLOCKS,
                    1.0F, 1.0F);
            }

            if (buildTick >= 400) {
                if ((buildTick - 400) % 12 == 0 && (new Random()).nextInt(2) == 1) {
                    BlockPos blockPos = tile
                        .getLevel().getBlockRandomPos(
                            tile.getBlockPos().getX(), 0, tile.getBlockPos().getZ(), 5);

                    blockPos = blockPos.atY(tile.getBlockPos().getY());

                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, tile.getLevel());
                    lightningBolt.setVisualOnly(true);
                    lightningBolt.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    tile.getLevel().addFreshEntity(lightningBolt);
                }
            }

            // if (!tile.canConsumeEnergy()) {
            // buildTick = 0;
            // } else {
            // tile.consumeEnergy();
            // }
            tile.setChanged();
            tile.setData(VEAttachments.BUILD_TICK, buildTick + 1);
            return;
        }

        if (!tile.canConsumeEnergy())
            return;
        ItemStack stack = tile.getInventory().getStackInSlot(2);

        ChunkFluidData data = stack.get(VEDataComponents.CHUNK_FLUID_DATA);

        DimensionalLaserRecipeProcessor processor = (DimensionalLaserRecipeProcessor) tile.getRecipeProcessor();

        if (data == null) {
            BasicProcessor.updateCounter(Config.DIMENSIONAL_LASER_PROCESS_TIME.get(), tile);
            return;
        }

        ChunkFluid fluid = ChunkFluids.getInstance().getChunkFluid(new ChunkPos(data.x(), data.z()));

        if (fluid == null) {
            VoluminousEnergy.LOGGER.error("Unable to find chunk fluid for what appears to be a scanned chunk: "
                + data.x() + " | " + data.z());
            return;
        }

        // If we ever need to validate a selected fluid we do so here.
        SingleChunkFluid singleChunkFluid = fluid.getFluids().get(0);
        FluidStack currentFluid = tile.getFluidStackFromTank(0);
        int amount = Math.min(singleChunkFluid.getAmount(), DEFAULT_TANK_CAPACITY - currentFluid.getAmount());

        boolean canFill = tile.getRelationalTank(0)
            .testFillTank(new FluidStack(singleChunkFluid.getFluid(), amount)) > 0;
        if (!canFill)
            return;

        CounterLength counterLength = tile.getData(VEAttachments.COUNTER_LENGTH);

        int counter = counterLength.counter();

        if (counter == 1) {
            counter--;
            FluidStack fluidStack = new FluidStack(singleChunkFluid.getFluid(), amount);
            tile.getRelationalTank(0).fillTank(fluidStack);
            tile.consumeEnergy();
            tile.setChanged();
        } else if (counter > 0) {
            counter--;
            tile.consumeEnergy();
        } else {
            counter = BasicProcessor.updateCounter(Config.DIMENSIONAL_LASER_PROCESS_TIME.get(), tile);
        }
        tile.setData(VEAttachments.COUNTER_LENGTH, new CounterLength(counter, counterLength.length()));
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new DimensionalLaserRecipeProcessor(blockSupplier);
    }

    private int counter = 0;
    private boolean lastReading = false;

    public boolean isMultiBlockValid(VETileEntity tile) {
        if (counter != 0) {
            counter--;
            return lastReading;
        }
        counter = 20;
        lastReading = true;
        // Tweak box based on direction -- This is the search range to ensure this is a
        // valid multiblock before operation
        for (final BlockPos blockPos : BlockPos.betweenClosed(
            tile.getBlockPos().offset(0, -3, 0),
            tile.getBlockPos().offset(0, -1, 0))) {

            final BlockState blockState = tile.getLevel().getBlockState(blockPos);

            if (blockState.getBlock() != blockSupplier.get()) { // Fails MultiBlock condition
                lastReading = false;
            }
        }
        return lastReading;
    }
}
