package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import static com.veteam.voluminousenergy.blocks.tiles.VETileEntity.DEFAULT_TANK_CAPACITY;

public class PumpTileProcessor implements AbstractRecipeProcessor {

    int lX;
    int lY;
    int lZ;

    @Override
    public void validateRecipe(VETileEntity tile) {
    }

    @Override
    public void processRecipe(VETileEntity tile) {

        if (!tile.canConsumeEnergy()) return;

        lX = tile.getData("lx");
        lY = tile.getData("ly");
        lZ = tile.getData("lz");

        VERelationalTank fluidTank = tile.getRelationalTank(0);

        if (fluidTank.getTank().getFluidAmount() + 1000 <= DEFAULT_TANK_CAPACITY) {
            for (int i = 0; i < 50; i++) {
                if (fluidPumpMethod(tile)) {
                    tile.markFluidInputDirty();
                    break;
                }
            }
            tile.setChanged();

            int soundTick = tile.getData("sound_tick");
            if (++soundTick == 19) {
                soundTick = 0;
                if (Config.PLAY_MACHINE_SOUNDS.get()) {
                    tile.getLevel().playSound(null,
                            tile.getBlockPos(), VESounds.AIR_COMPRESSOR, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
            tile.setData("sound_tick", soundTick);
            tile.setData("lx", lX);
            tile.setData("ly", lY);
            tile.setData("lz", lZ);
            tile.setChanged();
        }
    }

    public boolean fluidPumpMethod(VETileEntity tile) {
        CompoundTag fluidTag = tile.getCompoundTag("selected_fluid");
        Fluid pumpingFluid = null;
        if (fluidTag.isEmpty()) {
            BlockState block = tile.getLevel().getBlockState(tile.getBlockPos().offset(0, -1, 0));
            if (!block.getFluidState().is(Fluids.EMPTY)) {
                pumpingFluid = block.getFluidState().getType();

                CompoundTag tag = new CompoundTag();
                new FluidStack(pumpingFluid, 1).writeToNBT(tag);
                tile.setCompoundTag("selected_fluid", tag);
                lX = -22;
                lY = -1;
                lZ = -22;
                tile.setData("lx", lX);
                tile.setData("ly", lY);
                tile.setData("lz", lZ);
            }
        } else {
            pumpingFluid = FluidStack.loadFluidStackFromNBT(fluidTag).getFluid();
        }
        if (pumpingFluid == null) {
            VoluminousEnergy.LOGGER.info("Pumping fluid is null!");
            return false;
        }

        Level level = tile.getLevel();
        if (lX < 22) {
            lX++;
            BlockState state = level.getBlockState(tile.getBlockPos().offset(lX, lY, lZ));
            if (pumpingFluid.isSame(state.getFluidState().getType())) {
                addFluidToTank(tile,pumpingFluid);
                return true;
            }

        } else if (lZ < 22) {
            lZ++;
            lX = -22;
            if (pumpingFluid.isSame(level.getBlockState(tile.getBlockPos().offset(lX, lY, lZ)).getFluidState().getType())) {
                addFluidToTank(tile,pumpingFluid);
                return true;
            }
        } else if (tile.getBlockPos().offset(0, lY, 0).getY() > level.dimensionType().minY()) {
            lY--;
            lX = -22;
            lZ = -22;
            if (pumpingFluid.isSame(level.getBlockState(tile.getBlockPos().offset(lX, lY, lZ)).getFluidState().getType())) {
                addFluidToTank(tile,pumpingFluid);
                return true;
            }
        }
        return false;
    }


    void addFluidToTank(VETileEntity tile,Fluid fluid) {
        tile.getLevel().setBlockAndUpdate(tile.getBlockPos().offset(lX, lY, lZ), Blocks.AIR.defaultBlockState());
        tile.consumeEnergy();
        tile.getRelationalTank(0).getTank()
                .fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
    }
}
