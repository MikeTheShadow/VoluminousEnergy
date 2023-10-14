package com.veteam.voluminousenergy.items.tools.multitool;

import com.veteam.voluminousenergy.blocks.tiles.VEFluidTileEntity;
import com.veteam.voluminousenergy.items.tools.multitool.bits.MultitoolBit;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.util.NumberUtil;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class CombustionMultitool extends Multitool {

    public final int TANK_CAPACITY = VEFluidTileEntity.TANK_CAPACITY;

    public CombustionMultitool(MultitoolBit bit, String registryName, Properties itemProperties) {
        super(bit, registryName, itemProperties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
        if(ForgeCapabilities.FLUID_HANDLER_ITEM == null) return; // sanity check
        itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluid -> {
            FluidStack fluidStack = fluid.getFluidInTank(0).copy();
            tooltip.add(
                    TextUtil.translateString(fluidStack.getTranslationKey()).copy()
                            .append(": "
                                    + NumberUtil.formatNumber(fluidStack.getAmount())
                                    + " mB / "
                                    + NumberUtil.formatNumber(this.TANK_CAPACITY)
                                    + " mB"
                            )
            );
            if (itemStack.getTag() != null){
                tooltip.add(TextUtil.translateString("text.voluminousenergy.energy").copy()
                        .append(": " + NumberUtil.formatNumber(itemStack.getTag().getInt("energy")))
                );
            }
        });
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack){
        return true;
    }

    @Override
    public int getBarWidth(ItemStack itemStack){
        AtomicInteger fluidInTank = new AtomicInteger(0);
        itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluid -> {
            FluidStack fluidStack = fluid.getFluidInTank(0).copy();
            fluidInTank.set(fluidStack.getAmount());
        });

        return (int)Math.round(13 * (fluidInTank.get() / (double)this.TANK_CAPACITY));
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        AtomicReference<Float> ratio = new AtomicReference<>(0F);
        itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluid -> {
            ratio.set(fluid.getFluidInTank(0).getAmount() / (float)this.TANK_CAPACITY);
        });
        return Mth.hsvToRgb(ratio.get() / 3.0F, 1.0F, 1.0F);
    }

    // This should initialize the FluidHandler and also allow one to get the fluidHandler from this item
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack itemStack, @Nullable CompoundTag nbt){
        return new FluidHandlerItemStack(itemStack, this.TANK_CAPACITY);
    }

    /* THIS IS FOR DAMAGE
     * UNDER NO CIRCUMSTANCES SHOULD YOU USE THE TAG 'damage' AS IT APPEARS TO BE FILTERED OR SOMETHING
     * WE WILL USE 'energy' INSTEAD AS WE CAN MANIPULATE THIS VALUE WITHOUT OUR MANIPULATIONS BEING IGNORED.
     */

    @Override
    public void setDamage(ItemStack stack, int damage){ // I don't think this fires
        CompoundTag tag = stack.getTag();

        if (tag == null) return;
        int usesLeftUntilRefuel = tag.getInt("energy");
        if (usesLeftUntilRefuel < 1){
            AtomicInteger volumetricEnergy = new AtomicInteger(0);
            stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluid -> {
                FluidStack itemFluid = fluid.getFluidInTank(0).copy();
                if (!itemFluid.isEmpty() && isCombustibleFuel(itemFluid.getRawFluid())){
                    if (fluid.getFluidInTank(0).getAmount() > 50){
                        fluid.drain(50, IFluidHandler.FluidAction.EXECUTE);
                        //volumetricEnergy.set(CombustionGeneratorFuelRecipe.rawFluidWithVolumetricEnergy.getOrDefault(fluid.getFluidInTank(0).getRawFluid(), 0)/50);
                        volumetricEnergy.set(getVolumetricEnergyFromFluid(fluid.getFluidInTank(0).getRawFluid())/50);
                    }
                }
            });
            stack.getOrCreateTag().putInt("energy",volumetricEnergy.get()); // Resets the energy tag
        }
    }

    @Override
    public  <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        CompoundTag tag = stack.getTag();

        if (tag == null && stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()){
            AtomicInteger volumetricEnergy = new AtomicInteger();
            stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluid -> {
                FluidStack itemFluid = fluid.getFluidInTank(0).copy();

                if (isCombustibleFuel(itemFluid.getRawFluid())){
                    if (fluid.getFluidInTank(0).getAmount() > 50){
                        fluid.drain(50, IFluidHandler.FluidAction.EXECUTE);
                        volumetricEnergy.set(getVolumetricEnergyFromFluid(fluid.getFluidInTank(0).getRawFluid())/50);
                        stack.getOrCreateTag().putInt("damage", volumetricEnergy.get()); // does nothing
                    }
                }

            });
            return -(volumetricEnergy.get()) > 0 ? -(volumetricEnergy.get()) : -1;
        } else if (tag == null){
            return 0; // Technically this should never occur
        }

        int usesLeftUntilRefuel = tag.getInt("energy");
        if (usesLeftUntilRefuel > 1){
            stack.getTag().putInt("energy", (usesLeftUntilRefuel-amount));
            return -1;
        } else if (usesLeftUntilRefuel <= 1){
            AtomicInteger volumetricEnergy = new AtomicInteger(0);
            stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluid -> {
                FluidStack itemFluid = fluid.getFluidInTank(0).copy();
                if (isCombustibleFuel(itemFluid.getRawFluid())){
                    if (fluid.getFluidInTank(0).getAmount() >= 50){
                        fluid.drain(50, IFluidHandler.FluidAction.EXECUTE);
                        volumetricEnergy.set(getVolumetricEnergyFromFluid(fluid.getFluidInTank(0).getRawFluid())/50);
                    }
                }

            });
            stack.getOrCreateTag().putInt("energy",volumetricEnergy.get()); //  THIS RESETS THE ENERGY
            return -(volumetricEnergy.get()) > 0 ? -(volumetricEnergy.get()) : -1; // CANNOT 0 or + result will destroy item
        }

        return -1;
    }

    @Override
    public boolean isDamageable(ItemStack itemStack){
        return true;
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity){ // Doesn't seem to work, but should never fire with current design
        this.onDestroyed(itemEntity.getItem());
    }

    public void onDestroyed(ItemStack itemStack){ // Doesn't seem to work, but should never fire with current design
        itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluid -> {
            FluidStack itemFluid = fluid.getFluidInTank(0).copy();

            if (isCombustibleFuel(itemFluid.getRawFluid())){
                if (fluid.getFluidInTank(0).getAmount() > 50){
                    fluid.drain(50, IFluidHandler.FluidAction.EXECUTE);
                    int volumetricEnergy = getVolumetricEnergyFromFluid(fluid.getFluidInTank(0).getRawFluid());
                    itemFluid.getOrCreateTag().putInt("energy", volumetricEnergy);
                }
            }
        });
    }


    public boolean isDamaged(ItemStack stack) {
        return stack.getDamageValue() > 0;
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockStateToMine) {
        CompoundTag tag = itemStack.getTag();
        if (tag != null){
            if (tag.getInt("energy") > 1){
                return super.getDestroySpeed(itemStack, blockStateToMine);
            } else {
                AtomicBoolean notEmpty = new AtomicBoolean(false);
                itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluid -> notEmpty.set(!fluid.getFluidInTank(0).isEmpty()));
                if (notEmpty.get()){
                    return super.getDestroySpeed(itemStack, blockStateToMine);
                }
            }
        }
        return 0; // disables the tool
    }


    private static int getVolumetricEnergyFromFluid(Fluid fluid) {
        for(VERecipe r : VERecipe.getCachedRecipes(CombustionGeneratorFuelRecipe.RECIPE_TYPE)) {
            VEFluidRecipe recipe = (VEFluidRecipe) r;
            if(recipe.getFluidIngredient(0).test(fluid)) return recipe.getFluidIngredientAmount(0);
        }
        return 0;
    }

    public static boolean isCombustibleFuel(Fluid fluid) {
        for(VERecipe r : VERecipe.getCachedRecipes(CombustionGeneratorFuelRecipe.RECIPE_TYPE)) {
            VEFluidRecipe recipe = (VEFluidRecipe) r;
            if(recipe.getFluidIngredient(0).test(fluid)) return true;
        }
        return false;
    }
}
