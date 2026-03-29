package com.veteam.voluminousenergy.compat.jei.containerHandler;

import com.google.common.collect.Lists;
import com.veteam.voluminousenergy.blocks.screens.ElectricFurnaceScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ElectricFurnaceContainerHandler implements IGuiContainerHandler<ElectricFurnaceScreen> {
    @Override
    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull ElectricFurnaceScreen containerScreen, double guiMouseX, double guiMouseY) {
        List<IGuiClickableArea> areas = new ArrayList<>();
        areas.add(new IGuiClickableArea() {
            @Override
            public @NotNull Rect2i getArea() {
                return containerScreen.getTooltipArea();
            }

            @Override
            public void getTooltip(@NotNull ITooltipBuilder tooltip) {
                tooltip.add(VoluminousEnergyPlugin.SHOW_RECIPES);
                tooltip.addAll(containerScreen.getTooltips());
            }

            @Override
            public void onClick(@NotNull IFocusFactory focusFactory, @NotNull IRecipesGui recipesGui) {
                recipesGui.showTypes(Lists.newArrayList(RecipeTypes.SMELTING, RecipeTypes.BLASTING));
            }
        });

        return areas;
    }
}
