package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.VEDataComponents;
import com.veteam.voluminousenergy.util.extensions.VEEnums;
import com.veteam.voluminousenergy.util.records.ChunkFluidData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class RFIDChip extends Item {

    public RFIDChip() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(16)
                .rarity(VEEnums.ELECTRONIC_RARITY.getValue())
        );
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @NotNull TooltipContext pContext, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> componentList, @NotNull TooltipFlag tooltipFlag) {


        ChunkFluidData chunkFluidData = itemStack.get(VEDataComponents.CHUNK_FLUID_DATA);

        if (chunkFluidData != null) {

            ChunkFluid fluid = new ChunkFluid(chunkFluidData);
            fluid.getFluids().forEach(f -> componentList.accept(TextUtil.fluidNameAndAmountWithUnitsAndColours(f)));

            componentList.accept(
                    TextUtil.translateString("text.voluminousenergy.chunk").copy()
                            .append(" X: " + chunkFluidData.x() + " | ")
                            .append(TextUtil.translateString("text.voluminousenergy.chunk").copy().append(" Z: " + chunkFluidData.z())));
        }
        super.appendHoverText(itemStack, pContext, tooltipDisplay, componentList, tooltipFlag);
    }

}
