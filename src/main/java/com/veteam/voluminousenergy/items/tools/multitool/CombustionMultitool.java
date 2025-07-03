package com.veteam.voluminousenergy.items.tools.multitool;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.tools.multitool.bits.MultitoolBit;
import com.veteam.voluminousenergy.recipe.CombustionGeneratorRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.util.NumberUtil;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CombustionMultitool extends Multitool {

    public final int TANK_CAPACITY = VETileEntity.DEFAULT_TANK_CAPACITY;

    public CombustionMultitool(MultitoolBit bit, String registryName, Properties itemProperties) {
        super(bit, registryName, itemProperties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {

        IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        IFluidHandler fluidHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);

        if (energyStorage == null || fluidHandler == null) {
            return;
        }
        FluidStack fluidStack = fluidHandler.getFluidInTank(0).copy();
        tooltip.add(
                TextUtil.translateString(fluidStack.getHoverName().getString()).copy()
                        .append(": "
                                + NumberUtil.formatNumber(fluidStack.getAmount())
                                + " mB / "
                                + NumberUtil.formatNumber(this.TANK_CAPACITY)
                                + " mB"
                        )
        );
        tooltip.add(TextUtil.translateString("text.voluminousenergy.energy").copy()
                .append(": " + NumberUtil.formatNumber(energyStorage.getEnergyStored())));
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        IFluidHandler fluidHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
        return (int) Math.round(13 * (fluidHandler.getFluidInTank(0).getAmount() / (double) this.TANK_CAPACITY));
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        IFluidHandler fluidHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
        return Mth.hsvToRgb(fluidHandler.getFluidInTank(0).getAmount() / 3.0F, 1.0F, 1.0F);
    }

    /* THIS IS FOR DAMAGE
     * UNDER NO CIRCUMSTANCES SHOULD YOU USE THE TAG 'damage' AS IT APPEARS TO BE FILTERED OR SOMETHING
     * WE WILL USE 'energy' INSTEAD AS WE CAN MANIPULATE THIS VALUE WITHOUT OUR MANIPULATIONS BEING IGNORED.
     */

    @Override
    public void setDamage(ItemStack stack, int damage) {

        Integer damageComponent = stack.get(DataComponents.DAMAGE);
        if (damageComponent == null) return;

        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);

        if (energyStorage == null || fluidHandler == null) {
            return;
        }

        int usesLeftUntilRefuel = energyStorage.getEnergyStored();
        if (usesLeftUntilRefuel < 1) {
            int volumetricEnergy = 0;
            FluidStack itemFluid = fluidHandler.getFluidInTank(0).copy();
            if (!itemFluid.isEmpty() && isCombustibleFuel(itemFluid.getFluid())) {
                if (fluidHandler.getFluidInTank(0).getAmount() > 50) {
                    fluidHandler.drain(50, IFluidHandler.FluidAction.EXECUTE);
                    //volumetricEnergy.set(CombustionGeneratorFuelRecipe.rawFluidWithVolumetricEnergy.getOrDefault(fluid.getFluidInTank(0).getRawFluid(), 0)/50);
                    volumetricEnergy = getVolumetricEnergyFromFluid(fluidHandler.getFluidInTank(0).getFluid()) / 50;
                }
            }

            energyStorage.extractEnergy(energyStorage.getEnergyStored(),false);
            energyStorage.receiveEnergy(volumetricEnergy,false);
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Runnable onBroken) {
        Integer usesLeftUntilRefuel = stack.get(VEDataComponents.MECHANICAL_ENERGY);

        if (usesLeftUntilRefuel == null && stack.getCapability(Capabilities.FluidHandler.ITEM) != null) {
            AtomicInteger volumetricEnergy = new AtomicInteger();
            IFluidHandlerItem fluid = stack.getCapability(Capabilities.FluidHandler.ITEM);

            FluidStack itemFluid = fluid.getFluidInTank(0).copy();

            if (isCombustibleFuel(itemFluid.getFluid())) {
                if (fluid.getFluidInTank(0).getAmount() > 50) {
                    fluid.drain(50, IFluidHandler.FluidAction.EXECUTE);
                    volumetricEnergy.set(getVolumetricEnergyFromFluid(fluid.getFluidInTank(0).getFluid()) / 50);
                    stack.set(DataComponents.DAMAGE, volumetricEnergy.get());// does nothing
                }
            }
            return -(volumetricEnergy.get()) > 0 ? -(volumetricEnergy.get()) : -1;
        } else if (usesLeftUntilRefuel == null) {
            return 0; // Technically this should never occur
        }

        if (usesLeftUntilRefuel > 1) {
            stack.set(VEDataComponents.MECHANICAL_ENERGY, (usesLeftUntilRefuel - amount));
            return -1;
        } else if (usesLeftUntilRefuel <= 1) {
            AtomicInteger volumetricEnergy = new AtomicInteger(0);
            IFluidHandlerItem fluid = stack.getCapability(Capabilities.FluidHandler.ITEM);

            FluidStack itemFluid = fluid.getFluidInTank(0).copy();
            if (isCombustibleFuel(itemFluid.getFluid())) {
                if (fluid.getFluidInTank(0).getAmount() >= 50) {
                    fluid.drain(50, IFluidHandler.FluidAction.EXECUTE);
                    volumetricEnergy.set(getVolumetricEnergyFromFluid(fluid.getFluidInTank(0).getFluid()) / 50);
                }
            }

            stack.set(VEDataComponents.MECHANICAL_ENERGY, volumetricEnergy.get()); //  THIS RESETS THE ENERGY
            return -(volumetricEnergy.get()) > 0 ? -(volumetricEnergy.get()) : -1; // CANNOT 0 or + result will destroy item
        }

        return -1;
    }

    @Override
    public boolean isDamageable(ItemStack itemStack) {
        return true;
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) { // Doesn't seem to work, but should never fire with current design
        this.onDestroyed(itemEntity.getItem());
    }

    public void onDestroyed(ItemStack itemStack) { // Doesn't seem to work, but should never fire with current design
        var fluid = itemStack.getCapability(Capabilities.FluidHandler.ITEM);

        FluidStack itemFluid = fluid.getFluidInTank(0).copy();

        if (isCombustibleFuel(itemFluid.getFluid())) {
            if (fluid.getFluidInTank(0).getAmount() > 50) {
                fluid.drain(50, IFluidHandler.FluidAction.EXECUTE);
                int volumetricEnergy = getVolumetricEnergyFromFluid(fluid.getFluidInTank(0).getFluid());
                itemFluid.set(VEDataComponents.MECHANICAL_ENERGY, volumetricEnergy);
            }
        }
    }


    @Override
    public boolean isDamaged(ItemStack stack) {
        return stack.getDamageValue() > 0;
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack itemStack, BlockState blockStateToMine) {
        Integer mechanicalEnergy = itemStack.get(VEDataComponents.MECHANICAL_ENERGY);
        if (mechanicalEnergy != null) {
            if (mechanicalEnergy > 1) {
                return super.getDestroySpeed(itemStack, blockStateToMine);
            } else {
                AtomicBoolean notEmpty = new AtomicBoolean(false);
                var fluid = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
                notEmpty.set(!fluid.getFluidInTank(0).isEmpty());
                if (notEmpty.get()) {
                    return super.getDestroySpeed(itemStack, blockStateToMine);
                }
            }
        }
        return 0; // disables the tool
    }


    private static int getVolumetricEnergyFromFluid(Fluid fluid) {
        for (VERecipe recipe : VERecipe.getCachedRecipes(CombustionGeneratorRecipe.RECIPE_TYPE)) {
            if (recipe.getFluidIngredient(0).test(fluid)) return recipe.getFluidIngredientAmount(0);
        }
        return 0;
    }

    public static boolean isCombustibleFuel(Fluid fluid) {
        for (VERecipe recipe : VERecipe.getCachedRecipes(CombustionGeneratorRecipe.RECIPE_TYPE)) {
            if (recipe.getFluidIngredient(0).test(fluid)) return true;
        }
        return false;
    }
}
