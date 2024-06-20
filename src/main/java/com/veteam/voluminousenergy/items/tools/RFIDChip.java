package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RFIDChip extends Item {

    public RFIDChip() {
        super(new Item.Properties()
                .stacksTo(16)
                .rarity(Rarity.create("ELECTRONIC", ResourceLocation.tryBuild(VoluminousEnergy.MODID,"ELECTRONIC"), ChatFormatting.GREEN))
        );
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @NotNull TooltipContext pContext, @NotNull List<Component> componentList, @NotNull TooltipFlag tooltipFlag) {


        VEDataComponents.ChunkFluidData chunkFluidData = itemStack.get(VEDataComponents.CHUNK_FLUID_DATA);

        if (chunkFluidData != null) {

            ChunkFluid fluid = new ChunkFluid(chunkFluidData);
            fluid.getFluids().forEach(f -> componentList.add(TextUtil.fluidNameAndAmountWithUnitsAndColours(f)));

            componentList.add(
                    TextUtil.translateString("text.voluminousenergy.chunk").copy()
                            .append(" X: " + chunkFluidData.x() + " | ")
                            .append(TextUtil.translateString("text.voluminousenergy.chunk").copy().append(" Z: " + chunkFluidData.z())));
        }
        super.appendHoverText(itemStack, pContext, componentList, tooltipFlag);
    }

}
