package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class VESolarTile extends VETileEntity {
    int generation;
    int currentEnergy;

    public VESolarTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state,null);
    }

    @Override
    public void tick() {
        updateClients();
        if (this.level != null){
            if (level.dimensionType().hasSkyLight() && isClear()) {
                this.generation = this.getGeneration();
                generateEnergy(this.generation);
                setChanged();
            }
        }
        sendOutPower();
    }

    void generateEnergy(int fe){
        energy.addEnergy(fe);
    }

    public static int receiveEnergy(BlockEntity tileEntity, Direction from, int maxReceive){
        return tileEntity.getCapability(ForgeCapabilities.ENERGY, from).map(handler ->
                handler.receiveEnergy(maxReceive, false)).orElse(0);
    }

    void sendOutPower() {
            for (Direction dir : Direction.values()){
                BlockEntity tileEntity = level.getBlockEntity(getBlockPos().relative(dir));
                Direction opposite = dir.getOpposite();
                if(tileEntity != null){
                    // If less energy stored then max transfer send the all the energy stored rather than the max transfer amount
                    int smallest = Math.min(energy.getMaxTransfer(), energy.getEnergyStored());
                    int received = receiveEnergy(tileEntity, opposite, smallest);
                    energy.consumeEnergy(received);
                    if (energy.getEnergyStored() <=0){
                        break;
                    }
                }
            }
    }

    /**
    * Cosine curve based off the location of the Sun(? I think, at least it looks like that)
    * Noon is the Zenith, hence why we use a cosine curve, since cosine curves start at a max
    * amplitude, which of course is Noon/Zenith. We do manipulate the curve a bit to make it more "reasonable"
    */
    protected float solarIntensity(){
        if(level == null) return 0;
        float celestialAngle = this.level.getSunAngle(1.0f); // Zenith = 0rad

        if(celestialAngle > Math.PI) celestialAngle = (2 * ((float) Math.PI) - celestialAngle);

        float intensity = Mth.cos(0.2f + (celestialAngle / 1.2f));
        intensity = Mth.clamp(intensity, 0, 1);

        if(intensity > 0.1f) {
            intensity = intensity * 1.5f;
            if(intensity > 1f) intensity = 1f;
        }

        if(intensity > 0){
            if(this.level.isRaining()) return intensity * 0.6f;
            if(this.level.isThundering()) return intensity * 0.2f;
        }

        return intensity;
    }

    protected boolean isClear(){
        if (level == null) return false;
        return level.canSeeSky(new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY()+1, this.getBlockPos().getZ()));
    }

    @Override
    public void load(CompoundTag tag) {
        tag.putInt("generation_rate", this.generation);
        super.load(tag);
        this.currentEnergy = this.getEnergyStored();

    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        this.generation = tag.getInt("generation_rate");
        super.saveAdditional(tag);
    }

   public abstract int getGeneration();

    @Nullable
    @Override
    public ItemStackHandler getInventoryHandler() {
        return null;
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return new ArrayList<>();
    }
}
