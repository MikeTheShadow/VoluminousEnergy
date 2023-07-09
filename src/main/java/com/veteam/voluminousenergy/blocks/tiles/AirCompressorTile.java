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

public class AirCompressorTile extends VEFluidTileEntity implements IVEPoweredTileEntity,IVECountable {

    public VESlotManager[] slotManagers = new VESlotManager[]{
            new VESlotManager(0,Direction.UP,true, SlotType.FLUID_HYBRID)
    };

    private final ItemStackHandler inventory = createHandler(2,this);

    private final RelationalTank airTank = new RelationalTank( new FluidTank(TANK_CAPACITY),0, TankType.OUTPUT,"air_tank:air_tank_properties");

    public AirCompressorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.AIR_COMPRESSOR_TILE.get(), pos, state, null);
        airTank.setValidFluids(Collections.singletonList(VEFluids.COMPRESSED_AIR_REG.get()));
    }

    @Override
    public void tick(){
        updateClients();

        ItemStack slotStack = inventory.getStackInSlot(0).copy();

        // Check item in the slot to see if it's a bucket. If it is--and there is fluid for it--fill it.
        if (slotStack.copy() != ItemStack.EMPTY) {
            if (slotStack.getItem() == Items.BUCKET && airTank.getTank().getFluidAmount() >= 1000 && slotStack.getCount() == 1) {
                ItemStack bucketStack = new ItemStack(airTank.getTank().getFluid().getRawFluid().getBucket(), 1);
                airTank.getTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                inventory.extractItem(0, 1, false);
                inventory.insertItem(0, bucketStack, false);
            }
        }

        if (airTank != null && (airTank.getTank().getFluidAmount() + 250) <= TANK_CAPACITY && counter == 0 && canConsumeEnergy()){
            // Check blocks around the Air Compressor to see if it's air
            int x = this.worldPosition.getX();
            int y = this.worldPosition.getY();
            int z = this.worldPosition.getZ();

            // Sanity check
            if (Blocks.AIR == level.getBlockState(new BlockPos(x,y,z)).getBlock()){
                addAirToTank();
            }

            // Check X offsets
            if (Blocks.AIR == level.getBlockState(new BlockPos(x+1,y,z)).getBlock()){
                //LOGGER.debug("HIT! x+1: " + (x+1) + " y: " + y + " z: " + z + " is AIR!");
                addAirToTank();
            }
            if (Blocks.AIR == level.getBlockState(new BlockPos(x-1,y,z)).getBlock()){
                //LOGGER.debug("HIT! x-1: " + (x-1) + " y: " + y + " z: " + z + " is AIR!");
                addAirToTank();
            }

            // Check Y offsets
            if (Blocks.AIR == level.getBlockState(new BlockPos(x,y+1,z)).getBlock()){
                //LOGGER.debug("HIT! x: " + x + " y+1: " + (y+1) + " z: " + z + " is AIR!");
                addAirToTank();
            }
            if (Blocks.AIR == level.getBlockState(new BlockPos(x,y-1,z)).getBlock()){
                //LOGGER.debug("HIT! x: " + x + " y-1: " + (y-1) + " z: " + z + " is AIR!");
                addAirToTank();
            }

            if (Blocks.AIR == level.getBlockState(new BlockPos(x,y,z+1)).getBlock()){
                //LOGGER.debug("HIT! x: " + x + " y: " + y + " z+1: " + (z+1) + " is AIR!");
                addAirToTank();
            }
            if (Blocks.AIR == level.getBlockState(new BlockPos(x,y,z-1)).getBlock()){
                //LOGGER.debug("HIT! x: " + x + " y: " + y + " z-1: " + (z-1) + " is AIR!");
                addAirToTank();
            }

        } else if (airTank.getTank() != null && (airTank.getTank().getFluidAmount() + 250) <= TANK_CAPACITY && canConsumeEnergy()){
            consumeEnergy();
            if(++sound_tick == 19) {
                sound_tick = 0;
                if (Config.PLAY_MACHINE_SOUNDS.get()) {
                    level.playSound(null, this.getBlockPos(), VESounds.AIR_COMPRESSOR, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        }

        if(counter == 0) counter = (byte)this.calculateCounter(20,this.inventory.getStackInSlot(this.getUpgradeSlotId()));
        else counter--;
    }

    public void addAirToTank() {
        if ((airTank.getTank().getFluidAmount() + 250) <= TANK_CAPACITY) {
            airTank.getTank().fill(new FluidStack(VEFluids.COMPRESSED_AIR_REG.get(), 250), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new AirCompressorContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public FluidStack getAirTankFluid(){
        return this.airTank.getTank().getFluid();
    }

    public int getTankCapacity(){
        return TANK_CAPACITY;
    }

    @Override
    public @Nonnull List<RelationalTank> getRelationalTanks() {
        return Collections.singletonList(airTank);
    }

    public RelationalTank getAirTank(){
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
