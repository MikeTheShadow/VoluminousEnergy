package com.veteam.voluminousenergy.blocks.blocks.machines.tanks;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.machines.VEFaceableMachineBlock;
import com.veteam.voluminousenergy.blocks.blocks.util.FaceableBlock;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.NumberUtil;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

public abstract class TankBlock extends VEFaceableMachineBlock {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###");

    public TankBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext tooltipContext, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, tooltipContext, tooltip, flag);

        FluidStack fluid = stack.get(VEDataComponents.FLUID_STACK_DATA);

        if(fluid == null) {
            return;
        }

        int tankCapacity = this.getTankCapacity() * 1000;

        if (Config.SHORTEN_ITEM_TOOLTIP_VALUES.get()) {
            tooltip.add(
                    TextUtil.translateString(fluid.getHoverName().getString()).copy()
                            .append(": ")
                            .append(NumberUtil.numberToString4Fluids(fluid.getAmount()))
                            .append(" / ")
                            .append(NumberUtil.numberToString4Fluids(tankCapacity)
                            ));
        } else {
            String amount = String.format("%s mB", DECIMAL_FORMAT.format(fluid.getAmount()));
            String capacity = String.format("%s mB", DECIMAL_FORMAT.format(tankCapacity));
            tooltip.add(TextUtil.translateString(fluid.getHoverName().getString()).copy().append(": " + amount + " / " + capacity));
        }
    }

    public abstract int getTankCapacity();
}
