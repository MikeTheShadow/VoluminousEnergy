package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class AirCompressorProcessor implements AbstractRecipeProcessor {

    @Override
    public void processRecipe(VETileEntity tile) {
        if (!tile.canConsumeEnergy()) return;

        int soundTick = tile.getData("sound_tick");

        int counter = tile.getData("counter");

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
            if (addAirToTank(airMultiplier,tile.getTank(0))) {
                tile.consumeEnergy();
                if (++soundTick == 19) {
                    soundTick = 0;
                    if (Config.PLAY_MACHINE_SOUNDS.get()) {
                        level.playSound(null, tile.getBlockPos(), VESounds.AIR_COMPRESSOR, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
                tile.setData("sound_tick",soundTick);
                counter = (byte) tile.calculateCounter(20, tile.getInventory().getStackInSlot(tile.getEnergy().getUpgradeSlotId()));
                tile.setChanged();
            }
        } else {
            --counter;
        }
        tile.setData("counter",--counter);
    }

    // We don't need to validate the recipe because it doesn't have one.
    @Override
    public void validateRecipe(VETileEntity tile) {}

    public boolean addAirToTank(int multiplier, VERelationalTank tank) {

        int totalToAdd = 250 * multiplier;
        int amountToAdd = Math.min(totalToAdd, (tank.getTank().getFluidAmount() + totalToAdd));
        if (amountToAdd == 0) return false;
        tank.getTank().fill(new FluidStack(VEFluids.COMPRESSED_AIR_REG.get(), amountToAdd), IFluidHandler.FluidAction.EXECUTE);
        return true;
    }

}
