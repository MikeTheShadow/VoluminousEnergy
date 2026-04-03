package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VEAttachments;
import com.veteam.voluminousenergy.util.VERelationalTank;
import com.veteam.voluminousenergy.util.records.CounterLength;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class AirCompressorProcessor implements AbstractRecipeProcessor {

    @Override
    public void tick(VETileEntity tile) {
        if (!tile.canConsumeEnergy()) {
            tile.setLit(false);
            return;
        }
        tile.setLit(true);

        int soundTick = tile.getData(VEAttachments.SOUND_TICK);

        CounterLength counterLength = tile.getData(VEAttachments.COUNTER_LENGTH);
        int counter = counterLength.counter();

        if (counter <= 0) {
            // Check blocks around the Air Compressor to see if it's air
            int x = tile.getBlockPos().getX();
            int y = tile.getBlockPos().getY();
            int z = tile.getBlockPos().getZ();

            Level level = tile.getLevel();

            int airMultiplier = 0;
            // Check X offsets
            if (Blocks.AIR == level.getBlockState(new BlockPos(x + 1, y, z)).getBlock())
                airMultiplier++;
            if (Blocks.AIR == level.getBlockState(new BlockPos(x - 1, y, z)).getBlock())
                airMultiplier++;
            // Check Y offsets
            if (Blocks.AIR == level.getBlockState(new BlockPos(x, y + 1, z)).getBlock())
                airMultiplier++;
            if (Blocks.AIR == level.getBlockState(new BlockPos(x, y - 1, z)).getBlock())
                airMultiplier++;
            if (Blocks.AIR == level.getBlockState(new BlockPos(x, y, z + 1)).getBlock())
                airMultiplier++;
            if (Blocks.AIR == level.getBlockState(new BlockPos(x, y, z - 1)).getBlock())
                airMultiplier++;
            if (addAirToTank(airMultiplier, tile.getRelationalTank(0), tile)) {
                tile.consumeEnergy();
                if (++soundTick == 19) {
                    soundTick = 0;
                    if (Config.PLAY_MACHINE_SOUNDS.get()) {
                        level.playSound(null, tile.getBlockPos(), VESounds.AIR_COMPRESSOR, SoundSource.BLOCKS, 1.0F,
                            1.0F);
                    }
                }
                tile.setData(VEAttachments.SOUND_TICK, soundTick);
                counter = BasicProcessor.updateCounter(20,tile);
                tile.setChanged();
            }
        } else {
            --counter;
        }
        tile.setData(VEAttachments.COUNTER_LENGTH, new CounterLength(--counter, counterLength.length()));
    }

    public boolean addAirToTank(int multiplier, VERelationalTank tank, VETileEntity tile) {

        int totalToAdd = 25 * multiplier;

        int amountLeft = tank.getTank().getCapacity() - tank.getTank().getFluidAmount();

        int amountToFill = Math.min(totalToAdd, amountLeft);
        if (amountToFill == 0) {
            tile.setLit(false);
            return false;
        }

        tank.getTank().fill(new FluidStack(VEFluids.COMPRESSED_AIR_REG.get(), amountToFill),
                IFluidHandler.FluidAction.EXECUTE);
        return true;
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new AirCompressorProcessor();
    }
}
