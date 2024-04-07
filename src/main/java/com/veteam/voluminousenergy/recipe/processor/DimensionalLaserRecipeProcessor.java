package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.persistence.SingleChunkFluid;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.Random;

import static com.veteam.voluminousenergy.blocks.tiles.VETileEntity.DEFAULT_TANK_CAPACITY;

public class DimensionalLaserRecipeProcessor extends MultiBlockRecipeProcessor {


    public DimensionalLaserRecipeProcessor(RegistryObject<? extends Block> block) {
        super(block);
    }

    @Override
    public void processRecipe(VETileEntity tile) {
        if (!isMultiBlockValid(tile)) return;

        int buildTick = tile.getData("build_tick");
        if (buildTick != 1000) {

            if (buildTick == 1) {
                tile.getLevel().playSound(null, tile.getBlockPos(), VESounds.ENERGY_BEAM_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            if (buildTick == 400) {
                tile.getLevel().playSound(null, tile.getBlockPos(), VESounds.ENERGY_BEAM_FIRED, SoundSource.BLOCKS, 1.0F, 1.0F);
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
//            if (!tile.canConsumeEnergy()) {
//                buildTick = 0;
//            } else {
//                tile.consumeEnergy();
//            }
            tile.setData("build_tick", buildTick + 1);
            return;
        }
        if (!tile.canConsumeEnergy()) return;
        ItemStack stack = tile.getStackInSlot(2);
        if (stack.isEmpty()) {
            int counterTemp = tile.calculateCounter(Config.DIMENSIONAL_LASER_PROCESS_TIME.get(),
                    tile.getStackInSlot(tile.getEnergy().getUpgradeSlotId()).copy());
            int counter = counterTemp != 0 ? counterTemp : 1;
            tile.setData("length", counter);
            tile.setData("counter", counter);
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();

        int x = tag.getInt("ve_x");
        int z = tag.getInt("ve_z");

        ChunkFluid fluid = ChunkFluids.getInstance().getChunkFluid(new ChunkPos(x, z));

        if (fluid == null) {
            VoluminousEnergy.LOGGER.error("Unable to find chunk fluid for what appears to be a scanned chunk: " + x + " | " + z);
            return;
        }

        // If we ever need to validate a selected fluid we do so here.
        SingleChunkFluid singleChunkFluid = fluid.getFluids().get(0);
        FluidStack currentFluid = tile.getFluidStackFromTank(0);
        int amount = Math.min(singleChunkFluid.getAmount(), DEFAULT_TANK_CAPACITY - currentFluid.getAmount());

        boolean canFill = tile.getTank(0).testFillTank(new FluidStack(singleChunkFluid.getFluid(), amount)) > 0;
        if (!canFill) return;

        int counter = tile.getData("counter");

        if (counter == 1) {
            FluidStack fluidStack = new FluidStack(singleChunkFluid.getFluid(), amount);
            tile.getTank(0).fillTank(fluidStack);
            counter--;
            tile.consumeEnergy();
            tile.setChanged();
        } else if (counter > 0) {
            counter--;
            tile.consumeEnergy();
        } else {
            int counterTemp = tile.calculateCounter(Config.DIMENSIONAL_LASER_PROCESS_TIME.get(),
                    tile.getStackInSlot(tile.getEnergy().getUpgradeSlotId()).copy());
            counter = counterTemp != 0 ? counterTemp : 1;
            tile.setData("length", counter);
        }
        tile.setData("counter", counter);

    }

    private int counter = 0;
    private boolean lastReading = false;

    @Override
    public boolean isMultiBlockValid(VETileEntity tile) {
        if (counter != 0) {
            counter--;
            return lastReading;
        }
        counter = 20;
        lastReading = true;
        // Tweak box based on direction -- This is the search range to ensure this is a valid multiblock before operation
        for (final BlockPos blockPos : BlockPos.betweenClosed(
                tile.getBlockPos().offset(-1, -3, -1),
                tile.getBlockPos().offset(1, -1, 1))) {

            final BlockState blockState = tile.getLevel().getBlockState(blockPos);

            if (blockState.getBlock() != getBlock()) { // Fails MultiBlock condition
                lastReading = false;
            }
        }
        return lastReading;
    }

    @Override
    public void validateRecipe(VETileEntity tile) {
    }
}
