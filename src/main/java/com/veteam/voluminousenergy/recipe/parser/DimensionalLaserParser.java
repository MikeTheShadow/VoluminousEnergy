package com.veteam.voluminousenergy.recipe.parser;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.tools.RFIDChip;
import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.persistence.SingleChunkFluid;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.fluids.FluidStack;

import static com.veteam.voluminousenergy.blocks.tiles.VETileEntity.DEFAULT_TANK_CAPACITY;

public class DimensionalLaserParser extends RecipeParser {

    public DimensionalLaserParser(VERecipe recipe) {
        super(recipe);
    }

    @Override
    public boolean isPartialRecipe(VETileEntity tile) {
        return tile.getStackInSlot(0).getItem() instanceof RFIDChip;
    }

    @Override
    public boolean isCompleteRecipe(VETileEntity tile) {
        return tile.getStackInSlot(0).getItem() instanceof RFIDChip;
    }

    @Override
    public boolean canCompleteRecipe(VETileEntity tile) {

        ItemStack stack = tile.getStackInSlot(0);

        if (!(stack.getItem() instanceof RFIDChip)) return false;
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains("ve_x")) return false;

        int x = tag.getInt("ve_x");
        int z = tag.getInt("ve_z");

        ChunkFluid fluid = ChunkFluids.getInstance().getChunkFluid(new ChunkPos(x,z));

        if(fluid == null) {
            VoluminousEnergy.LOGGER.warn("Unable to find chunk fluid for what appears to be a scanned chunk: " + x + " | " + z);
            return false;
        }

        // If we ever need to validate a selected fluid we do so here.
        SingleChunkFluid singleChunkFluid = fluid.getFluids().get(0);
        FluidStack currentFluid = tile.getFluidStackFromTank(0);
        int amount = Math.min(singleChunkFluid.getAmount(),DEFAULT_TANK_CAPACITY - currentFluid.getAmount());

        return tile.getTank(0).testFillTank(new FluidStack(singleChunkFluid.getFluid(),amount)) > 0;
    }

    @Override
    public void completeRecipe(VETileEntity tile) {

        ItemStack stack = tile.getStackInSlot(0);
        CompoundTag tag = stack.getOrCreateTag();
        int x = tag.getInt("ve_x");
        int z = tag.getInt("ve_z");

        ChunkFluid fluid = ChunkFluids.getInstance().getChunkFluid(new ChunkPos(x,z));
        SingleChunkFluid singleChunkFluid = fluid.getFluids().get(0);
        FluidStack currentFluid = tile.getFluidStackFromTank(0);
        int insertAmount = Math.min(singleChunkFluid.getAmount(),DEFAULT_TANK_CAPACITY - currentFluid.getAmount());
        tile.getTank(0).fillTank(new FluidStack(singleChunkFluid.getFluid(),insertAmount));
    }
}
