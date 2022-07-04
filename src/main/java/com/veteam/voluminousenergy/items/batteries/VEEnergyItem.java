package com.veteam.voluminousenergy.items.batteries;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.tools.energy.VEEnergyItemStorage;
import com.veteam.voluminousenergy.util.NumberUtil;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class VEEnergyItem extends VEItem {
    private final int maxEnergy;
    private final int maxTransfer;

    @Deprecated
    public VEEnergyItem(Properties properties, int maxEnergy, int maxTransfer){
        super(properties);
        this.maxEnergy = maxEnergy;
        this.maxTransfer = maxTransfer;
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
    public ICapabilityProvider initCapabilities(ItemStack itemStack, @Nullable CompoundTag nbt){
        return new ICapabilityProvider() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if(cap == CapabilityEnergy.ENERGY){
                    return LazyOptional.of(() -> new VEEnergyItemStorage(itemStack, maxEnergy, maxTransfer)).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
        if(CapabilityEnergy.ENERGY == null) return; // sanity check
        itemStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e ->{
//            Component textComponent = new TextComponent("FE: " + e.getEnergyStored() + "/" + e.getMaxEnergyStored());
            Component textComponent = TextUtil.translateString("text.voluminousenergy.energy").copy().append(": " + NumberUtil.numberToString4FE(e.getEnergyStored()) + " / " + NumberUtil.numberToString4FE(e.getMaxEnergyStored()));
            tooltip.add(textComponent);
        });
    }

    @Override
    public void fillItemCategory(CreativeModeTab itemGroup, NonNullList<ItemStack> itemStacks){ // Clone and make fully charged itemStack
        if (this.allowedIn(itemGroup)){
            itemStacks.add(new ItemStack(this));
            ItemStack chargedStack = new ItemStack(this);
            chargedStack.getOrCreateTag().putInt("energy",this.maxEnergy);
            itemStacks.add(chargedStack);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack){return true;}

    @Override
    public int getBarWidth(ItemStack itemStack){
        //return Math.round(getChargeRatio(itemStack));
        return Math.round(13 * getChargeRatio(itemStack));
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        float f = getChargeRatio(itemStack);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    public int getMaxTransfer() {return maxTransfer;}

}
