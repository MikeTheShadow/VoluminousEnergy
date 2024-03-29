package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.achievements.triggers.VECriteriaTriggers;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.blocks.machines.SolariumMachineCasingBlock;
import com.veteam.voluminousenergy.blocks.containers.DimensionalLaserContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.tools.RFIDChip;
import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.persistence.SingleChunkFluid;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import com.veteam.voluminousenergy.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DimensionalLaserTile extends VEMultiBlockTileEntity implements IVEPoweredTileEntity, IVECountable {

    public VESlotManager bucketTopSm = new VESlotManager(0, Direction.UP, true, SlotType.FLUID_INPUT, 1, 0);
    public VESlotManager bucketBottomSm = new VESlotManager(1, Direction.DOWN, true, SlotType.FLUID_OUTPUT);
    public VESlotManager RFIDsm = new VESlotManager(2, Direction.NORTH, true, SlotType.OUTPUT);

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(bucketTopSm);
        add(bucketBottomSm);
        add(RFIDsm);
    }};

    RelationalTank outputTank = new RelationalTank(new FluidTank(TANK_CAPACITY), 0, 0, TankType.OUTPUT, "outputTank:output_tank_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {{
        add(outputTank);
    }};

    //private boolean multiBlockComplete = false;

    private int tickTimer = 0;
    private byte multiblockTickChecker = 19;
    private boolean complete = false;
    private boolean firstStageComplete = false;
    private boolean initialized = true;
    private boolean soundPlayed = false;

    public DimensionalLaserTile(BlockPos pos, BlockState state) {
        super(VEBlocks.DIMENSIONAL_LASER_TILE.get(), pos, state, null);
        this.outputTank.setAllowAny(true);
        this.RFIDsm.addAllowedItem(VEItems.RFID_CHIP.get());
    }

    public ItemStackHandler inventory = createHandler(4);

    @Override
    public void tick() {
        updateClients();
        multiblockTickChecker++;
        if (multiblockTickChecker == 20) {
            multiblockTickChecker = 0;
            validity = isMultiBlockValid();
        }
        if (!validity) return;

        if (!complete) {
            setChanged();
            if (!firstStageComplete) {
                tickTimer();
                if (tickTimer >= 400) {
                    setFirstStageComplete();
                    resetTickTimer();
                }
            } else {
                tickTimer();
                if (tickTimer >= 600) complete = true;
                if (tickTimer % 12 == 0 && (new Random()).nextInt(2) == 1) {
                    BlockPos blockPos = level.getBlockRandomPos(this.getBlockPos().getX(), 0, this.getBlockPos().getZ(), 5);

                    blockPos = blockPos.atY(this.getBlockPos().getY());

                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
                    lightningBolt.setVisualOnly(true);
                    lightningBolt.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    level.addFreshEntity(lightningBolt);
                }
            }

            if (initialized && !soundPlayed) {
                level.playSound(null, this.getBlockPos(), VESounds.ENERGY_BEAM_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
                soundPlayed = true;
            }

            if (initialized && firstStageComplete) {
                initialized = false;
                level.playSound(null, this.getBlockPos(), VESounds.ENERGY_BEAM_FIRED, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return;
        }


        int x = this.getBlockPos().getX();
        int y = this.getBlockPos().getY();
        int z = this.getBlockPos().getZ();
        for (ServerPlayer serverplayer : level.getEntitiesOfClass(ServerPlayer.class, (new AABB(x, y, z, x, y - 4, z)).inflate(50.0D, 50.0D, 50.0D))) {
            VECriteriaTriggers.CONSTRUCT_DIMENSIONAL_LASER_TRIGGER.trigger(serverplayer, 3);
        }

        // Main tick code
        processFluidIO();

        ItemStack rfidStack = inventory.getStackInSlot(2);

        if (rfidStack.getItem() instanceof RFIDChip) {
            CompoundTag rfidTag = rfidStack.getOrCreateTag();
            Tag veX = rfidTag.get("ve_x");
            Tag veZ = rfidTag.get("ve_z");

            if (veX != null && veZ != null) {

                int veXi = Integer.valueOf(veX.toString());
                int veZi = Integer.valueOf(veZ.toString());

                ChunkPos chunkPos = new ChunkPos(veXi, veZi);
                BlockPos blockPos = chunkPos.getBlockAt(0, 64, 0);

                ChunkFluid fluidFromPos = WorldUtil.getFluidFromPosition(level, blockPos);

                SingleChunkFluid fluid = fluidFromPos.getFluids().get(0);

                if (super.canConsumeEnergy() && outputTank.getTank().getFluidAmount() < TANK_CAPACITY) {
                    if (counter == 1) {

                        if (outputTank.isFluidValid(fluid.getFluid())) {
                            int fillSize = Math.min(fluid.getAmount(), TANK_CAPACITY - outputTank.getTank().getFluidAmount());
                            fillSize = Math.min(fillSize, fluid.getAmount());
                            outputTank.getTank().fill(new FluidStack(fluid.getFluid(), fillSize), IFluidHandler.FluidAction.EXECUTE);
                        }
                        counter--;
                        consumeEnergy();
                        this.setChanged();
                    } else if (counter > 0) {
                        counter--;
                        consumeEnergy();
                    } else {
                        int counterTemp = this.calculateCounter(Config.DIMENSIONAL_LASER_PROCESS_TIME.get(), inventory.getStackInSlot(this.getUpgradeSlotId()).copy());
                        counter = counterTemp != 0 ? counterTemp : 1;
                        length = counter;
                    }
                }
            } else { // If no RFID chip, set counter to 0
                counter = 0;
            }
        } else {
            counter = 0;
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public int getTickTimer() {
        return tickTimer;
    }

    public void tickTimer() {
        if (this.complete) return;
        this.tickTimer += 1;
    }

    @Override
    public void load(CompoundTag tag) {
        firstStageComplete = tag.getBoolean("first_stage_complete");
        complete = tag.getBoolean("fully_built");
        tickTimer = tag.getInt("tick_timer");
        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        tag.putBoolean("first_stage_complete", firstStageComplete);
        tag.putBoolean("fully_built", complete);
        tag.putInt("tick_timer", tickTimer);
        super.saveAdditional(tag);
    }

    public boolean isComplete() {
        return complete;
    }

    @Nonnull
    @Override
    public List<RelationalTank> getRelationalTanks() {
        return this.fluidManagers;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory, @Nonnull Player player) {
        return new DimensionalLaserContainer(id, level, worldPosition, playerInventory, player);
    }

    @Nullable
    @Override
    public ItemStackHandler getInventoryHandler() {
        return this.inventory;
    }

    @Nonnull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return this.slotManagers;
    }

    public void setComplete() {
        this.complete = true;
    }

    public boolean isFirstStageComplete() {
        return firstStageComplete;
    }

    public void setFirstStageComplete() {
        this.firstStageComplete = true;
    }

    public void resetTickTimer() {
        this.tickTimer = 0;
    }

    @Override
    public int getMaxPower() {
        return Config.DIMENSIONAL_LASER_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.DIMENSIONAL_LASER_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.DIMENSIONAL_LASER_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 3;
    }

    @Override
    public boolean isMultiBlockValid() {

        // Tweak box based on direction -- This is the search range to ensure this is a valid multiblock before operation
        for (final BlockPos blockPos : BlockPos.betweenClosed(worldPosition.offset(-1, -3, -1), worldPosition.offset(1, -1, 1))) {
            final BlockState blockState = level.getBlockState(blockPos);

            if (blockState.getBlock() != getCasingBlock()) { // Fails MultiBlock condition
                return false;
            }
        }
        return true;
    }

    @Override
    public Block getCasingBlock() {
        return VEBlocks.SOLARIUM_MACHINE_CASING_BLOCK.get();
    }

    public boolean getMultiblockValidity() {
        return validity;
    }

}
