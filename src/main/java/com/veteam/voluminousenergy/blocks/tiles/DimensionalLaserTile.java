package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.client.renderers.VEBlockEntities;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DimensionalLaserTile extends VEFluidTileEntity {

    //private boolean multiBlockComplete = false;

    private int tickTimer = 0;
    private boolean complete = false;
    private boolean firstStageComplete = false;
    private boolean initialized = true;
    private boolean soundPlayed = false;

    public DimensionalLaserTile(BlockPos pos, BlockState state) {
        super(VEBlockEntities.DIMENSIONAL_LASER.get(), pos, state);
    }

    @Override
    public void tick() {
        updateClients();
//        //TODO when multiblock stuff is added rewrite how this is done
        if (!complete) {
            if (!firstStageComplete) {
                tickTimer();
                if (tickTimer >= 20 * 20) {
                    setFirstStageComplete();
                    resetTickTimer();
                }
            } else {
                tickTimer();
                if (tickTimer >= 20 * 30) complete = true;
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
//        TODO Create an achievement for this
//        int i = this.getBlockPos().getX();
//        int j = this.getBlockPos().getY();
//        int k = this.getBlockPos().getZ();
//
//        for (ServerPlayer serverplayer : level.getEntitiesOfClass(ServerPlayer.class, (new AABB(i, j, k, i, j - 4, k)).inflate(10.0D, 5.0D, 10.0D))) {
//            CriteriaTriggers.CONSTRUCT_BEACON.trigger(serverplayer, 3);
//        }
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
        VoluminousEnergy.LOGGER.info("Ticker load: " + tag.getInt("tick_timer") + " | " + tickTimer);
        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        tag.putBoolean("first_stage_complete", firstStageComplete);
        tag.putBoolean("fully_built", complete);
        tag.putInt("tick_timer", tickTimer);
        VoluminousEnergy.LOGGER.info("Ticker save: " + tag.getInt("tick_timer") + " | " + tickTimer);
        super.saveAdditional(tag);
    }

    public boolean isComplete() {
        return complete;
    }

    @Nonnull
    @Override
    public List<RelationalTank> getRelationalTanks() {
        return new ArrayList<>();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory, @Nonnull Player player) {
        return null;
    }

    @Nullable
    @Override
    public ItemStackHandler getInventoryHandler() {
        return null;
    }

    @Nonnull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return new ArrayList<>();
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
}
