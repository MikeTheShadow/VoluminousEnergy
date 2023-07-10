package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.AirCompressorContainer;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class AirCompressorTile extends VEFluidTileEntity implements IVEPoweredTileEntity, IVECountable {

    public VESlotManager[] slotManagers = new VESlotManager[]{
            new VESlotManager(0,Direction.UP,true, SlotType.FLUID_HYBRID),
            new VESlotManager(1, Direction.DOWN, true, SlotType.FLUID_OUTPUT)
    };

    private final ItemStackHandler inventory = createHandler(3, this);

    private final RelationalTank airTank = new RelationalTank(new FluidTank(TANK_CAPACITY), 0, TankType.OUTPUT, "air_tank:air_tank_properties");

    public AirCompressorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.AIR_COMPRESSOR_TILE.get(), pos, state, null);
        airTank.setValidFluids(Collections.singletonList(VEFluids.COMPRESSED_AIR_REG.get()));
    }

    @Override
    public void tick() {
        updateClients();

        if (!canConsumeEnergy()) return;
        if (airTank.getTank().getFluidAmount() == TANK_CAPACITY && counter == 0) {
            // Check blocks around the Air Compressor to see if it's air
            int x = this.worldPosition.getX();
            int y = this.worldPosition.getY();
            int z = this.worldPosition.getZ();

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
            if (addAirToTank(airMultiplier)) {
                consumeEnergy();
                if (++sound_tick == 19) {
                    sound_tick = 0;
                    if (Config.PLAY_MACHINE_SOUNDS.get()) {
                        level.playSound(null, this.getBlockPos(), VESounds.AIR_COMPRESSOR, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
                counter = (byte) this.calculateCounter(20, this.inventory.getStackInSlot(this.getUpgradeSlotId()));
            }
        } else {
            counter--;
        }
    }

    public boolean addAirToTank(int multiplier) {

        int totalToAdd = 250 * multiplier;
        int amountToAdd = Math.min(totalToAdd, (airTank.getTank().getFluidAmount() + totalToAdd));
        if (amountToAdd == 0) return false;
        airTank.getTank().fill(new FluidStack(VEFluids.COMPRESSED_AIR_REG.get(), amountToAdd), IFluidHandler.FluidAction.EXECUTE);
        return true;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new AirCompressorContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public FluidStack getAirTankFluid() {
        return this.airTank.getTank().getFluid();
    }

    @Override
    public @Nonnull List<RelationalTank> getRelationalTanks() {
        return Collections.singletonList(airTank);
    }

    public RelationalTank getAirTank() {
        return this.airTank;
    }

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return this.inventory;
    }

    @Override
    public @Nonnull List<VESlotManager> getSlotManagers() {
        return List.of(slotManagers);
    }

    @Override
    public int getMaxPower() {
        return Config.AIR_COMPRESSOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.AIR_COMPRESSOR_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.AIR_COMPRESSOR_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 1;
    }
}
