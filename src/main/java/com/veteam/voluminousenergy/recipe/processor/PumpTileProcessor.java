package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VEAttachments;
import com.veteam.voluminousenergy.util.VERelationalTank;
import com.veteam.voluminousenergy.util.records.FluidPumpData;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Optional;

import static com.veteam.voluminousenergy.blocks.tiles.VETileEntity.DEFAULT_TANK_CAPACITY;

public class PumpTileProcessor implements AbstractRecipeProcessor {

    int lX;
    int lY;
    int lZ;
    boolean init = false;
    Fluid pumpingFluid;

    @Override
    public void validateRecipe(VETileEntity tile) {
    }

    @Override
    public void processRecipe(VETileEntity tile) {

        if (!init) {
            init = true;

            Optional<FluidPumpData> data = tile.getExistingData(VEAttachments.FLUID_PUMP);

            if (data.isEmpty()) {
                BlockPos pos = tile.getBlockPos();
                BlockPos below = pos.below();
                FluidState state = tile.getLevel().getBlockState(below).getFluidState();
                Fluid fluid = state.getType();

                if (fluid == Fluids.EMPTY) {
                    return;
                }
                FluidPumpData newData = new FluidPumpData(below.getX(), below.getY(), below.getZ(), fluid);
                tile.setData(VEAttachments.FLUID_PUMP, newData);
                this.pumpingFluid = fluid;
                this.lX = below.getX();
                this.lY = below.getY();
                this.lZ = below.getZ();
            } else {
                FluidPumpData fluidPumpData = data.get();
                this.pumpingFluid = fluidPumpData.fluid();
                this.lX = fluidPumpData.x();
                this.lY = fluidPumpData.y();
                this.lZ = fluidPumpData.z();
            }
        }

        if (pumpingFluid == null) {
            return;
        }

        if (!tile.canConsumeEnergy()) return;

        VERelationalTank fluidTank = tile.getRelationalTank(0);

        if (fluidTank.getTank().getFluidAmount() + 1000 <= DEFAULT_TANK_CAPACITY) {
            for (int i = 0; i < 50; i++) {
                if (fluidPumpMethod(tile)) {
                    tile.markFluidInputDirty();
                    break;
                }
            }
            tile.setChanged();

            int soundTick = tile.getData(VEAttachments.SOUND_TICK);
            if (++soundTick == 19) {
                soundTick = 0;
                if (Config.PLAY_MACHINE_SOUNDS.get()) {
                    tile.getLevel().playSound(null,
                            tile.getBlockPos(), VESounds.AIR_COMPRESSOR, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                tile.setData(VEAttachments.SOUND_TICK, soundTick);
            }
        }
    }

    public boolean fluidPumpMethod(VETileEntity tile) {
        Level level = tile.getLevel();
        if (lX < 22) {
            lX++;
            BlockState state = level.getBlockState(tile.getBlockPos().offset(lX, lY, lZ));
            if (pumpingFluid.isSame(state.getFluidState().getType())) {
                addFluidToTank(tile, pumpingFluid);
                return true;
            }

        } else if (lZ < 22) {
            lZ++;
            lX = -22;
            if (pumpingFluid.isSame(level.getBlockState(tile.getBlockPos().offset(lX, lY, lZ)).getFluidState().getType())) {
                addFluidToTank(tile, pumpingFluid);
                return true;
            }
        } else if (tile.getBlockPos().offset(0, lY, 0).getY() > level.dimensionType().minY()) {
            lY--;
            lX = -22;
            lZ = -22;
            if (pumpingFluid.isSame(level.getBlockState(tile.getBlockPos().offset(lX, lY, lZ)).getFluidState().getType())) {
                addFluidToTank(tile, pumpingFluid);
                return true;
            }
        }
        return false;
    }


    void addFluidToTank(VETileEntity tile, Fluid fluid) {
        tile.getLevel().setBlockAndUpdate(tile.getBlockPos().offset(lX, lY, lZ), Blocks.AIR.defaultBlockState());
        tile.consumeEnergy();
        tile.getRelationalTank(0).getTank()
                .fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
        tile.setData(VEAttachments.FLUID_PUMP, new FluidPumpData(lX, lY, lZ, fluid));
        tile.setChanged();
    }
}
