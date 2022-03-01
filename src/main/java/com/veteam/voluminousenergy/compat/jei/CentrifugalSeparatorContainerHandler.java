package com.veteam.voluminousenergy.compat.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.veteam.voluminousenergy.blocks.screens.CentrifugalSeparatorScreen;

import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public class CentrifugalSeparatorContainerHandler implements IGuiContainerHandler<CentrifugalSeparatorScreen> {
    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(CentrifugalSeparatorScreen containerScreen, double guiMouseX, double guiMouseY) {
        List<IGuiClickableArea> areas = new ArrayList<>();
        areas.add(new IGuiClickableArea() {
            @Override
            public Rect2i getArea() {
                return new Rect2i(78, 32, 14, 23);
            }

            @Override
            public List<Component> getTooltipStrings() {
                List<Component> tooltips = new ArrayList<>();
                tooltips.add(VoluminousEnergyPlugin.SHOW_RECIPES);
                tooltips.addAll(containerScreen.getTooltips());
                return tooltips;
            }

            @Override
            public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
                recipesGui.showCategories(Lists.newArrayList(VoluminousEnergyPlugin.CENTRIFUGAL_SEPARATION_UID));
            }
        });

        return areas;
    }
}
