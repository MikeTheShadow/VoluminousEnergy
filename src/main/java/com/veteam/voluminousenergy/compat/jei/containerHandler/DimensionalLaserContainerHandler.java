package com.veteam.voluminousenergy.compat.jei.containerHandler;

import com.google.common.collect.Lists;
import com.veteam.voluminousenergy.blocks.screens.DimensionalLaserScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.compat.jei.category.DimensionalLasingCategory;
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

public class DimensionalLaserContainerHandler implements IGuiContainerHandler<DimensionalLaserScreen> {
    @Override
    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull DimensionalLaserScreen containerScreen, double guiMouseX, double guiMouseY) {
        List<IGuiClickableArea> areas = new ArrayList<>();
        areas.add(new IGuiClickableArea() {
            @Override
            public Rect2i getArea() {
                return containerScreen.getTooltipArea();
            }

            @Override
            public void getTooltip(@NotNull ITooltipBuilder tooltip) {
                tooltip.add(VoluminousEnergyPlugin.SHOW_RECIPES);
                tooltip.addAll(containerScreen.getTooltips());
            }

            @Override
            public void onClick(@NotNull IFocusFactory focusFactory, @NotNull IRecipesGui recipesGui) {
                recipesGui.showTypes(Lists.newArrayList(DimensionalLasingCategory.RECIPE_TYPE));
            }
        });

        return areas;
    }
}