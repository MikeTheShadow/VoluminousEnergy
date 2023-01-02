package com.veteam.voluminousenergy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.compat.jei.VoluminousEnergyPlugin;
import com.veteam.voluminousenergy.recipe.SawmillingRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.TextUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class SawmillCategory implements IRecipeCategory<SawmillingRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(VoluminousEnergyPlugin.SAWMILL_UID, SawmillingRecipe.class);
    //protected static ArrayList<Pair<ItemStack, ItemStack>> logPlankPairList = new ArrayList<>();


    public SawmillCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 86, 40).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VEBlocks.SAWMILL_BLOCK));
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI,199,0,23,17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);
        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public @NotNull RecipeType getRecipeType(){
        return RECIPE_TYPE;
    }

    @Deprecated
    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.SAWMILL_UID;
    }

    @Deprecated
    @Override
    public Class<? extends SawmillingRecipe> getRecipeClass() {
        return SawmillingRecipe.class;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.sawmilling");
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
    public void draw(SawmillingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        slotDrawable.draw(matrixStack,2,10);
        slotDrawable.draw(matrixStack,48,1);
        slotDrawable.draw(matrixStack,48,19);
        slotDrawable.draw(matrixStack,66,9);
        arrow.draw(matrixStack,24, 11);
        emptyArrow.draw(matrixStack,24,11);
    }

    public void ingredientHandler(SawmillingRecipe recipe,
                                  IIngredientAcceptor inputItemAcceptor,
                                  IIngredientAcceptor primaryItemOutputAcceptor,
                                  IIngredientAcceptor secondaryItemOutputAcceptor,
                                  IIngredientAcceptor fluidOutputAcceptor) {
        if (recipe.isLogRecipe() && Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get()){

            AtomicReference<ArrayList<ItemStack>> atomicLogStacks = new AtomicReference<>(new ArrayList<>());
            AtomicReference<ArrayList<ItemStack>> atomicPlankStacks = new AtomicReference<>(new ArrayList<>());

            // Calculate Logs and Planks based on registry
            ForgeRegistries.ITEMS.getValues().parallelStream().forEach(registeredItem -> {
                if (registeredItem.getRegistryName().getPath().contains("log")){
                    atomicLogStacks.get().add(new ItemStack(registeredItem, Config.SAWMILL_LOG_CONSUMPTION_RATE.get()));
                } else if (registeredItem.getRegistryName().getPath().contains("plank")) {
                    atomicPlankStacks.get().add(new ItemStack(registeredItem, Config.SAWMILL_PRIMARY_OUTPUT_COUNT.get()));
                }
            });
            inputItemAcceptor.addIngredients(VanillaTypes.ITEM_STACK, atomicLogStacks.get());
            primaryItemOutputAcceptor.addIngredients(VanillaTypes.ITEM_STACK, atomicPlankStacks.get());


            // Secondary Output
            ResourceLocation secondOutputItemResourceLocation = new ResourceLocation(Config.SAWMILL_SECOND_OUTPUT_RESOURCE_LOCATION.get());
            Item secondOutput = ForgeRegistries.ITEMS.getValue(secondOutputItemResourceLocation);
            if (secondOutput != null){
                secondaryItemOutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(secondOutput, Config.SAWMILL_SECOND_OUTPUT_COUNT.get()));
            }

            // Fluid Output
            ResourceLocation fluidLocation = new ResourceLocation(Config.SAWMILL_FLUID_LOCATION.get());
            Fluid outputFluid = ForgeRegistries.FLUIDS.getValue(fluidLocation);
            if (outputFluid != null){
                fluidOutputAcceptor.addIngredient(ForgeTypes.FLUID_STACK, new FluidStack(outputFluid, Config.SAWMILL_FLUID_AMOUNT.get()));
            }
        } else if (!recipe.isLogRecipe()) {
            // Primary Input (Typically logs)
            ArrayList<ItemStack> inputStacks = new ArrayList<>();
            for (ItemStack itemStack : recipe.ingredient.get().getItems()){
                itemStack.setCount(recipe.ingredientCount);
                inputStacks.add(itemStack);
            }
            inputItemAcceptor.addIngredients(VanillaTypes.ITEM_STACK, inputStacks);

            // First Item Output (Typically Planks)
            ItemStack resultStack = recipe.result.copy();
            resultStack.setCount(recipe.getOutputAmount());
            primaryItemOutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, resultStack);

            // Second Item Output
            ItemStack secondOutputStack = recipe.secondResult.copy();
            secondOutputStack.setCount(recipe.getSecondAmount());
            secondaryItemOutputAcceptor.addIngredient(VanillaTypes.ITEM_STACK, secondOutputStack);

            // Fluid Output
            fluidOutputAcceptor.addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutputFluid().copy());
        }

    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, SawmillingRecipe recipe, IFocusGroup focusGroup) {
        IRecipeSlotBuilder inputItem = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 3, 11);

        IRecipeSlotBuilder primaryOutputItem = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 49,2);
        IRecipeSlotBuilder secondaryOutputItem = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 49, 20);

        IRecipeSlotBuilder fluidOutput = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 67, 10);

        this.ingredientHandler(recipe, inputItem, primaryOutputItem, secondaryOutputItem, fluidOutput);
    }

    /* Can't really control the pairing so this is useless
    public static void buildLogPlankPairListCache(){
        if (!logPlankPairList.isEmpty()) return;

        AtomicReference<ArrayList<Pair<ItemStack, ItemStack>>> cacheReference = new AtomicReference<>(logPlankPairList);
        AtomicReference<Integer> logAmount = new AtomicReference<>(Config.SAWMILL_LOG_CONSUMPTION_RATE.get());
        AtomicReference<Integer> plankAmount = new AtomicReference<>(Config.SAWMILL_PRIMARY_OUTPUT_COUNT.get());

        ForgeRegistries.ITEMS.getValues().parallelStream().forEach(registeredLogItem -> {

            AtomicReference<String> atomicLogNamespace = new AtomicReference<>(registeredLogItem.getRegistryName().getNamespace());
            AtomicReference<String> atomicLogName = new AtomicReference<>(registeredLogItem.getRegistryName().getPath());

            if (atomicLogName.get().contains("log") || atomicLogName.get().contains("wood")){
                AtomicReference<Item> atomicLogItem = new AtomicReference<>(registeredLogItem);

                ForgeRegistries.ITEMS.getValues().parallelStream().forEach(registeredPlankItem -> {
                    String plankNamespace = registeredPlankItem.getRegistryName().getNamespace();
                    String plankName = registeredPlankItem.getRegistryName().getPath();
                    if (plankName.contains("plank") && plankNamespace.equals(atomicLogNamespace.get())){
                        List<String> processingLogWood;
                        List<String> processingPlank;

                        processingLogWood = Arrays.asList(atomicLogName.get().split("_"));
                        processingPlank = Arrays.asList(plankName.split("_"));

                        processingLogWood.remove("log");
                        processingLogWood.remove("wood");
                        processingPlank.remove("plank");

                        boolean hit = false;
                        for (String s : processingLogWood){
                            if (processingPlank.contains(s)) {
                                hit = true;
                                break;
                            }
                        }
                        cacheReference.get().add(new Pair<ItemStack, ItemStack>(new ItemStack(atomicLogItem.get(), logAmount.get()), new ItemStack(registeredPlankItem, plankAmount.get())));
                    }
                });
            }
        });
    }*/

}
