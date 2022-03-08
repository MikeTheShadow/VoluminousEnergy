package com.veteam.voluminousenergy.compat.jei.containerHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.veteam.voluminousenergy.blocks.screens.AqueoulizerScreen;

import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public class AqueoulizerContainerHandler implements IGuiContainerHandler<AqueoulizerScreen> {
    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(AqueoulizerScreen containerScreen, double guiMouseX, double guiMouseY) {
        List<IGuiClickableArea> areas = new ArrayList<>();
        areas.add(new IGuiClickableArea() {
            @Override
            public Rect2i getArea() {
                return containerScreen.getTooltipArea();
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
                recipesGui.showCategories(Lists.newArrayList(VoluminousEnergyPlugin.AQUEOULIZING_UID));
            }
        });

        return areas;
    }
}
