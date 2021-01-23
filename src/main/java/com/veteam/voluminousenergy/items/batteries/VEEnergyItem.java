package com.veteam.voluminousenergy.items.batteries;

import com.veteam.voluminousenergy.tools.energy.VEEnergyItemStorage;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class VEEnergyItem extends Item {
    private final int maxEnergy;
    private final int maxReceive;
    private final int maxExtract;

    public VEEnergyItem(Properties properties, int maxEnergy, int maxTransfer) {
        this(properties, maxEnergy, maxTransfer, maxEnergy);
    }

    public VEEnergyItem(Properties properties, int maxEnergy, int maxReceive, int maxExtract){
        super(properties);
        this.maxEnergy = maxEnergy;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    public static float getChargeRatio(ItemStack stack){
        LazyOptional<IEnergyStorage> energy = stack.getCapability(CapabilityEnergy.ENERGY);
        if(energy.isPresent()){
            IEnergyStorage energyStorage = energy.orElseThrow(IllegalStateException::new);
            return (float) energyStorage.getEnergyStored()/ energyStorage.getMaxEnergyStored();
        }
        return 0;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack itemStack, @Nullable CompoundNBT nbt){
        return new ICapabilityProvider() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if(cap == CapabilityEnergy.ENERGY){
                    return LazyOptional.of(() -> new VEEnergyItemStorage(itemStack, maxEnergy, maxReceive, maxExtract)).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public void addInformation(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag){
        if(CapabilityEnergy.ENERGY == null) return; // sanity check
        itemStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e ->{
            ITextComponent textComponent = new StringTextComponent("FE: " + e.getEnergyStored() + "/" + e.getMaxEnergyStored());
            tooltip.add(textComponent);
        });
    }

    @Override
    public void fillItemGroup(ItemGroup itemGroup, NonNullList<ItemStack> itemStacks){ // Clone and make fully charged itemStack
        if (this.isInGroup(itemGroup)){
            itemStacks.add(new ItemStack(this));
            ItemStack chargedStack = new ItemStack(this);
            chargedStack.getOrCreateTag().putInt("energy",this.maxEnergy);
            itemStacks.add(chargedStack);
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack itemStack){return true;}

    @Override
    public double getDurabilityForDisplay(ItemStack itemStack){return 1-getChargeRatio(itemStack);}

    public int getMaxReceive() {return maxReceive;}
    public int getMaxExtract() {return maxExtract;}

}
