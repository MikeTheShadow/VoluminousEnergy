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
    public void tick(VETileEntity tile) {

        if(lY <= tile.getLevel().dimensionType().minY()) return;

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
                FluidPumpData newData = new FluidPumpData(0,0,0, fluid);
                tile.setData(VEAttachments.FLUID_PUMP, newData);
                this.pumpingFluid = fluid;
            } else {
                FluidPumpData fluidPumpData = data.get();
                this.pumpingFluid = fluidPumpData.fluid();
                this.lX = 0;
                this.lY = 0;
                this.lZ = 0;
            }
        }
        if (pumpingFluid == null) {
            return;
        }

        if (!tile.canConsumeEnergy())
            return;
        VERelationalTank fluidTank = tile.getRelationalTank(0);

        if (fluidTank.getTank().getFluidAmount() + 1000 <= DEFAULT_TANK_CAPACITY) {
            tile.setLit(true);
            for (int i = 0; i < Config.PUMP_CHECK_CYCLES_PER_TICK.get(); i++) {
                if (fluidPumpMethod(tile)) {
                    tile.consumeEnergy();
                    break;
                }
            }
            tile.setChanged();

            int soundTick = tile.getData(VEAttachments.SOUND_TICK);
            if (++soundTick == 19) {
                soundTick = 0;
                tile.getLevel().playSound(null,
                        tile.getBlockPos(), VESounds.AIR_COMPRESSOR, SoundSource.BLOCKS, 1.0F, 1.0F);
                tile.setData(VEAttachments.SOUND_TICK, soundTick);
            }
        } else {
            tile.setLit(false);
        }
    }


    public boolean fluidPumpMethod(VETileEntity tile) {
        Level level = tile.getLevel();
        if (lX < 22) {
            lX++;
            BlockPos checkPos = tile.getBlockPos().offset(lX, lY, lZ);
            BlockState state = level.getBlockState(checkPos);
            FluidState fluidState = state.getFluidState();

            if (fluidState.isSource() && pumpingFluid.isSame(fluidState.getType())) {
                addFluidToTank(tile, pumpingFluid);
                return true;
            }

        } else if (lZ < 22) {
            lZ++;
            lX = -22;
            BlockPos checkPos = tile.getBlockPos().offset(lX, lY, lZ);
            if (pumpingFluid
                .isSame(level.getBlockState(checkPos).getFluidState().getType())) {
                addFluidToTank(tile, pumpingFluid);
                return true;
            }
        } else if (tile.getBlockPos().offset(0, lY, 0).getY() > level.dimensionType().minY()) {
            lY--;
            lX = -22;
            lZ = -22;
            BlockPos checkPos = tile.getBlockPos().offset(lX, lY, lZ);
            if (pumpingFluid
                .isSame(level.getBlockState(checkPos).getFluidState().getType())) {
                addFluidToTank(tile, pumpingFluid);
                return true;
            }
        }
        return false;
    }

    void addFluidToTank(VETileEntity tile, Fluid fluid) {
        tile.getLevel().setBlockAndUpdate(tile.getBlockPos().offset(lX, lY, lZ), Blocks.AIR.defaultBlockState());
        tile.getRelationalTank(0).getTank()
            .fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
        tile.setData(VEAttachments.FLUID_PUMP, new FluidPumpData(lX, lY, lZ, fluid));
        tile.setChanged();
        tile.markFluidInputDirty();
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new PumpTileProcessor();
    }
}
