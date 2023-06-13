package com.veteam.voluminousenergy.compat.jei.category;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.ElectrolyzerRecipe;
import com.veteam.voluminousenergy.util.TextUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ElectrolyzingCategory implements IRecipeCategory<ElectrolyzerRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.ELECTROLYZING_UID, ElectrolyzerRecipe.class);

    public ElectrolyzingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 52, 5, 120, 78).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.ELECTROLYZER_BLOCK.get()));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI,199,0,23,17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);

    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.electrolyzing");
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
    public void draw(ElectrolyzerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,25, 30);
        emptyArrow.draw(matrixStack,25,30);
        slotDrawable.draw(matrixStack,5,20); // Input
        slotDrawable.draw(matrixStack,5,38); // Bucket
        slotDrawable.draw(matrixStack,49,2); // First Output
        slotDrawable.draw(matrixStack,49,20); // First RNG
        slotDrawable.draw(matrixStack,49,38); // Second RNG
        slotDrawable.draw(matrixStack,49,56); // Third RNG

        if (recipe.getRngItemSlot0() != null && recipe.getRngItemSlot0().getItem() != Items.AIR){
            int chance = (int)(recipe.getChance0()*100);
            TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty(chance + "%"),  74, 26, VEContainerScreen.GREY_TEXT_STYLE);
        }

        if (recipe.getRngItemSlot1() != null && recipe.getRngItemSlot1().getItem() != Items.AIR){
            int chance = (int)(recipe.getChance1()*100);
            TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty(chance + "%"),  74, 44, VEContainerScreen.GREY_TEXT_STYLE);

        }

        if (recipe.getRngItemSlot2() != null && recipe.getRngItemSlot2().getItem() != Items.AIR){
            int chance = (int)(recipe.getChance2()*100);
            TextUtil.renderUnshadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty(chance + "%"),  74, 62, VEContainerScreen.GREY_TEXT_STYLE);
        }

    }

    public void ingredientHandler(ElectrolyzerRecipe recipe,
                                  IIngredientAcceptor itemInputAcceptor,
                                  IIngredientAcceptor bucketInputAcceptor,
                                  IIngredientAcceptor primaryOutputAcceptor,
                                  IIngredientAcceptor rng0OutputAcceptor,
                                  IIngredientAcceptor rng1OutputAcceptor,
                                  IIngredientAcceptor rng2OutputAcceptor) {
        ArrayList<ItemStack> inputStacks = new ArrayList<>();
        for (ItemStack itemStack : recipe.ingredient.get().getItems()){
            itemStack.setCount(recipe.ingredientCount);
            inputStacks.add(itemStack);
        }

        itemInputAcceptor.addIngredients(VanillaTypes.ITEM_STACK, inputStacks);

        if (recipe.needsBuckets() > 0){
            ItemStack bucketStack = new ItemStack(Items.BUCKET, recipe.needsBuckets());
            bucketInputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, bucketStack);
        }

        // Output --> ItemStacks here are not guaranteed to have correct amount; must do so manually
        ItemStack primaryOutputStack = recipe.result.copy();
        primaryOutputStack.setCount(recipe.getOutputAmount());
        primaryOutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, primaryOutputStack);

        ItemStack rng0 = recipe.getRngItemSlot0().copy();
        rng0.setCount(recipe.getOutputRngAmount0());
        rng0OutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, rng0);

        ItemStack rng1 = recipe.getRngItemSlot1().copy();
        rng1.setCount(recipe.getOutputRngAmount1());
        rng1OutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, rng1);

        ItemStack rng2 = recipe.getRngItemSlot2().copy();
        rng2.setCount(recipe.getOutputRngAmount2());
        rng2OutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, rng2);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, ElectrolyzerRecipe recipe, IFocusGroup focusGroup) {
        // Inputs
        IRecipeSlotBuilder itemInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 6, 21);
        IRecipeSlotBuilder bucketInput = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 6, 39);

        // Output
        IRecipeSlotBuilder itemOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 50,3);
        IRecipeSlotBuilder rng0Output = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 50,21);
        IRecipeSlotBuilder rng1Output = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 50,39);
        IRecipeSlotBuilder rng2Output = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 50,57);

        itemInput.setSlotName(TextUtil.TRANSLATED_INPUT_SLOT.getString());
        bucketInput.setSlotName(TextUtil.TRANSLATED_BUCKET_SLOT.getString());
        itemOutput.setSlotName(TextUtil.TRANSLATED_OUTPUT_SLOT.getString());
        rng0Output.setSlotName(TextUtil.TRANSLATED_RNG_SLOT.getString());
        rng1Output.setSlotName(TextUtil.TRANSLATED_RNG_SLOT.getString());
        rng2Output.setSlotName(TextUtil.TRANSLATED_RNG_SLOT.getString());

        this.ingredientHandler(recipe, itemInput, bucketInput, itemOutput, rng0Output, rng1Output, rng2Output);
    }
}