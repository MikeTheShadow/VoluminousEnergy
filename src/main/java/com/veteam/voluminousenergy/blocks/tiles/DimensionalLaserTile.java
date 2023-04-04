package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.achievements.triggers.VECriteriaTriggers;
import com.veteam.voluminousenergy.blocks.containers.DimensionalLaserContainer;
import com.veteam.voluminousenergy.client.renderers.VEBlockEntities;
import com.veteam.voluminousenergy.items.tools.RFIDChip;
import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.persistence.SingleChunkFluid;
import com.veteam.voluminousenergy.sounds.VESounds;
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

public class DimensionalLaserTile extends VEFluidTileEntity implements IVEPoweredTileEntity,IVECountable {

    public VESlotManager bucketTopSm = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT,"input_0_sm");
    public VESlotManager bucketBottomSm = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT,"input_1_sm");
    public VESlotManager RFIDsm = new VESlotManager(2, Direction.NORTH, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT,"output_0_sm");

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(bucketTopSm);
        add(bucketBottomSm);
        add(RFIDsm);
    }};

    RelationalTank outputTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.OUTPUT,"outputTank:output_tank_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {{
        add(outputTank);
    }};

    //private boolean multiBlockComplete = false;

    private int tickTimer = 0;
    private boolean complete = false;
    private boolean firstStageComplete = false;
    private boolean initialized = true;
    private boolean soundPlayed = false;

    public DimensionalLaserTile(BlockPos pos, BlockState state) {
        super(VEBlockEntities.DIMENSIONAL_LASER.get(), pos, state);
        this.outputTank.setAllowAny(true);
    }

    public ItemStackHandler inventory = createHandler(4);

    @Override
    public void tick() {
        updateClients();
//        //TODO when multiblock stuff is added rewrite how this is done
        if (!complete) {
            setChanged();
            if (!firstStageComplete) {
                tickTimer();
                if (tickTimer >= 20 * 20) {
                    setFirstStageComplete();
                    resetTickTimer();
                }
            } else {
                tickTimer();
                if (tickTimer >= 20 * 30) complete = true;
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
        }

        int x = this.getBlockPos().getX();
        int y = this.getBlockPos().getY();
        int z = this.getBlockPos().getZ();
        if(this.complete) {

            for (ServerPlayer serverplayer : level.getEntitiesOfClass(ServerPlayer.class, (new AABB(x, y, z, x, y - 4, z)).inflate(50.0D, 50.0D, 50.0D))) {
                VECriteriaTriggers.CONSTRUCT_DIMENSIONAL_LASER_TRIGGER.trigger(serverplayer, 3);
            }

            // Main tick code

            // Tank setup
            ItemStack bucketTop = inventory.getStackInSlot(0);
            ItemStack bucketBottom = inventory.getStackInSlot(1);

            outputTank.setInput(bucketTop.copy());
            outputTank.setOutput(bucketBottom.copy());

            if(this.inputFluid(outputTank,0,1)) return;
            if(this.outputFluid(outputTank,0,1)) return;


            ItemStack rfidStack = inventory.getStackInSlot(2);

            if (rfidStack.getItem() instanceof RFIDChip) { // TODO: Better error/sanity checking
                CompoundTag rfidTag = rfidStack.getOrCreateTag();
                Tag veX = rfidTag.get("ve_x");
                Tag veZ = rfidTag.get("ve_z");

                if (veX != null && veZ != null) {

                    int veXi = Integer.valueOf(veX.toString());
                    int veZi = Integer.valueOf(veZ.toString());

                    ChunkPos chunkPos = new ChunkPos(veXi, veZi);
                    BlockPos blockPos = chunkPos.getBlockAt(0,64,0);

                    ChunkFluid fluidFromPos = WorldUtil.getFluidFromPosition(level, blockPos);

                    SingleChunkFluid fluid = fluidFromPos.getFluids().get(0);

                    if (super.canConsumeEnergy() && fluid.getAmount() > 0 && outputTank.getTank().getFluidAmount() < TANK_CAPACITY) {
                        if (counter == 1) {

                            if (outputTank.isFluidValid(fluid.getFluid())) {
                                int fillSize = Math.min(250, TANK_CAPACITY - outputTank.getTank().getFluidAmount()); // TODO: Config fluid drain amount (MAX 4,000 mB)
                                fillSize = Math.min(fillSize, fluid.getAmount());
                                outputTank.getTank().fill(new FluidStack(fluid.getFluid(), fillSize), IFluidHandler.FluidAction.EXECUTE);
                                fluid.setAmount(fluid.getAmount() - fillSize);
                                fluidFromPos.setFluidRemaining(fluid);
                            }

                            counter--;
                            consumeEnergy();
                            this.setChanged();
                        } else if (counter > 0){
                            counter--;
                            consumeEnergy();
                        } else {
                            counter = 800; // TODO: Config
                            length = counter;
                        }
                    } else { // Energy Check
                        decrementSuperCounterOnNoPower();
                    }
                } else { // If no RFID chip, set counter to 0
                    counter = 0;
                }
            } else {
                counter = 0;
            }
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
        return new DimensionalLaserContainer(id,level,worldPosition,playerInventory,player);
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

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    @Override
    public int getMaxPower() {
        return Integer.MAX_VALUE / 4; // TODO: CONFIG
    }

    @Override
    public int getPowerUsage() {
        return 1024; // TODO: CONFIG
    }

    @Override
    public int getTransferRate() {
        return Integer.MAX_VALUE / 16; // TODO: CONFIG
    }

    @Override
    public int getUpgradeSlotId() {
        return 3;
    }
}
