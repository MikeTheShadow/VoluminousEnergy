package com.veteam.voluminousenergy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitools;
import com.veteam.voluminousenergy.recipe.ToolingRecipe;
import com.veteam.voluminousenergy.util.TextUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class ToolingCategory implements IRecipeCategory<ToolingRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.TOOLING_UID, ToolingRecipe.class);

    public ToolingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        ResourceLocation ToolingGUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/tooling_station_gui.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 70, 50).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEMultitools.IRON_DRILL_MULTITOOL));
        arrow = guiHelper.drawableBuilder(ToolingGUI, 188, 0, 22, 47).build();
        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return RECIPE_TYPE;
    }

    @Deprecated
    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.TOOLING_UID;
    }

    @Deprecated
    @Override
    public Class<? extends ToolingRecipe> getRecipeClass() {
        return ToolingRecipe.class;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.tooling");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(ToolingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        slotDrawable.draw(matrixStack,2,14); // Completed Multitool
        slotDrawable.draw(matrixStack,48,1); // Bit
        slotDrawable.draw(matrixStack,48,29); // Base
        arrow.draw(matrixStack,24, 0);
    }

    public void ingredientHandler(ToolingRecipe recipe,
                                  IIngredientAcceptor completeMultitoolItemAcceptor,
                                  IIngredientAcceptor bitItemAcceptor,
                                  IIngredientAcceptor baseItemAcceptor) {

        // Bits
        bitItemAcceptor.addIngredients(VanillaTypes.ITEM_STACK, Arrays.asList(recipe.ingredient.get().getItems()));

        // Bases
        ArrayList<ItemStack> baseStacks = new ArrayList<>();
        for (Item base : recipe.getBases()){
            baseStacks.add(new ItemStack(base));
        }
        baseItemAcceptor.addIngredients(VanillaTypes.ITEM_STACK, baseStacks);

        // Completed Multitool
        completeMultitoolItemAcceptor.addIngredient(VanillaTypes.ITEM_STACK, recipe.result.copy());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, ToolingRecipe recipe, IFocusGroup focusGroup) {

        IRecipeSlotBuilder completeMultitoolItem = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 3, 15);
        IRecipeSlotBuilder bitItem = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 49, 2);
        IRecipeSlotBuilder baseItem = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 49, 30);

        completeMultitoolItem.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());
        bitItem.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());
        baseItem.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());

        this.ingredientHandler(recipe, completeMultitoolItem, bitItem, baseItem);
    }
}
