package com.veteam.voluminousenergy.items.tools.multitool;

import com.veteam.voluminousenergy.items.tools.multitool.bits.MultitoolBit;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.veteam.voluminousenergy.VoluminousEnergy.LOGGER;

public class CombustionMultitool extends Multitool {

    private LazyOptional<IFluidHandler> inputFluidHandler = LazyOptional.of(this::createToolFluidHandler);
    private RelationalTank tank = new RelationalTank(new FluidTank(4000),0,null,null, TankType.INPUT);
    private Level level;

    public CombustionMultitool(MultitoolBit bit, String registryName, Properties itemProperties) {
        super(bit, registryName, itemProperties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
        /*if(this.level == null && world != null){
            this.level = world; // WARN: This may be BAD trying to grab the world via a tooltip method
        }*/

        if(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY == null) return; // sanity check
        itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluid -> {
            FluidStack fluidStack = fluid.getFluidInTank(0).copy();
            tooltip.add(
                    new TranslatableComponent(fluidStack.getTranslationKey())
                            .append(", Amount: " // TODO: Not translation friendly
                                    + fluidStack.getAmount()
                                    + " mB / "
                                    + tank.getTank().getCapacity()
                                    + " mB"
                            )
            );
        });
    }

    @Override
    public boolean showDurabilityBar(ItemStack itemStack){
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack itemStack){
        AtomicInteger fluidInTank = new AtomicInteger(0);
        itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluid -> {
            FluidStack fluidStack = fluid.getFluidInTank(0).copy();
            fluidInTank.set(fluidStack.getAmount());
        });

        return 1-(fluidInTank.get() / (double)this.tank.getTank().getTankCapacity(0));
    }

    // This should initialize the FluidHandler and also allow one to get the fluidHandler from this item
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack itemStack, @Nullable CompoundTag nbt){
        return new ICapabilityProvider() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                return inputFluidHandler.cast();
            }
        };
    }

    // Fluid handler for the CombustionMultitool
    public IFluidHandler createToolFluidHandler(){
        return this.createFluidHandler(new CombustionGeneratorFuelRecipe(), this.tank);
    }

    public IFluidHandler createFluidHandler(VEFluidRecipe veRecipe, RelationalTank... relationalTanks) { // Adapted from VEFluidTileEntity

        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return relationalTanks.length;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {

                for(RelationalTank t : relationalTanks) {
                    if(t.getId() == tank) {
                        return t.getTank() == null ? FluidStack.EMPTY : t.getTank().getFluid();
                    }
                }
                LOGGER.debug("Invalid tankId in CombustionMultitool for getFluidInTank");
                return FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {

                for(RelationalTank t : relationalTanks) {
                    if(t.getId() == tank) {
                        return t.getTank() == null ? 0 : t.getTank().getCapacity();
                    }
                }
                LOGGER.debug("Invalid tankId in CombustionMultitool for getTankCapacity");
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if (level != null){
                    for (RelationalTank t : relationalTanks) {
                        if (t.getTankType() == TankType.INPUT) {
                            ItemStack bucketStack = new ItemStack(stack.getRawFluid().getBucket());
                            VEFluidRecipe recipe = level.getRecipeManager().getRecipeFor(veRecipe.getType(), new SimpleContainer(bucketStack), level).orElse(null);
                            return recipe != null && t.getTank() != null && t.getTank().isFluidValid(stack);
                        } else {
                            AtomicBoolean recipeHit = new AtomicBoolean(false);
                            veRecipe.getIngredientList().forEach(i -> {
                                VEFluidRecipe recipe = level.getRecipeManager().getRecipeFor(veRecipe.getType(), new SimpleContainer(new ItemStack(i)), level).orElse(null);
                                if (recipe != null && recipe.getFluids().get(t.getOutputID()).getFluid().isSame(stack.getFluid())) { // In theory should never be null
                                    recipeHit.set(true);
                                }
                            });
                            return recipeHit.get() && t.getTank() != null && t.getTank().isFluidValid(stack);
                        }
                    }
                    return false;
                }
                return true;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {

                for(RelationalTank t : relationalTanks) {
                    if(isFluidValid(t.getId(),resource) && t.getTank().isEmpty() || resource.isFluidEqual(t.getTank().getFluid())) {
                        return t.getTank().fill(resource, action);
                    }
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }

                for(RelationalTank t : relationalTanks) {
                    if(resource.isFluidEqual(t.getTank().getFluid())) {
                        return t.getTank().drain(resource,action);
                    }
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                for(RelationalTank t : relationalTanks) {
                    if(t.getTank().getFluidAmount() > 0) {
                        if (Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                            return t.getTank().drain(maxDrain, action);
                        } else if (t.getTankType() != TankType.INPUT) {
                            return t.getTank().drain(maxDrain, action);
                        }
                    }
                }
                return FluidStack.EMPTY;
            }
        };
    }
}
