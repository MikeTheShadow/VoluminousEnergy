package com.veteam.voluminousenergy.compat.jei;

public class AqueoulizingCategory {/*implements IRecipeCategory<AqueoulizerRecipe> {
    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    private IDrawable emptyArrow;

    public AqueoulizingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(GUI, 68, 12, 90, 40).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEBlocks.AQUEOULIZER_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(GUI, 176, 0, 23, 17).build();
        emptyArrow = guiHelper.drawableBuilder(GUI,199,0,23,17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, true);
    }

    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.AQUEOULIZING_UID;
    }

    @Override
    public Class<? extends AqueoulizerRecipe> getRecipeClass() {
        return AqueoulizerRecipe.class;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.aqueoulizing");
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
    public void draw(AqueoulizerRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,48, 12);
        emptyArrow.draw(matrixStack,48,12);
        slotDrawable.draw(matrixStack,2,10);
        slotDrawable.draw(matrixStack,24,10);
        slotDrawable.draw(matrixStack,72,10);

        Minecraft.getInstance().font.draw(matrixStack,"mB:", 2, 32,0x606060);
        Minecraft.getInstance().font.draw(matrixStack,recipe.getInputAmount() + "", 24, 32,0x606060);
        Minecraft.getInstance().font.draw(matrixStack,recipe.getOutputAmount() + "", 72, 32,0x606060);
    }

    @Override
    public void setIngredients(AqueoulizerRecipe recipe, IIngredients ingredients) {

        // INPUT
        List<ItemStack> inputList = new ArrayList<>();
        for (ItemStack testStack : recipe.getIngredient().getItems()){
            testStack.setCount(64);
            inputList.add(testStack);
        }
        ingredients.setInputs(VanillaTypes.ITEM, inputList);

        ingredients.setInputs(VanillaTypes.FLUID, recipe.fluidInputList);

        // OUTPUT
        List<FluidStack> outputStacks = new ArrayList<>();
        outputStacks.add(recipe.getOutputFluid()); // Normal output
        ingredients.setOutputs(VanillaTypes.FLUID, outputStacks);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AqueoulizerRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

        itemStacks.init(0, false, 2, 10);
        fluidStacks.init(1, false, 25, 11);
        fluidStacks.init(2, false, 73,11);

        // Input

        // Should only be one ingredient...
        List<ItemStack> inputs = new ArrayList<>();
        Arrays.stream(recipe.getIngredient().getItems()).map(s -> {
            ItemStack stack = s.copy();
            stack.setCount(recipe.getIngredientCount());
            return stack;
        }).forEach(inputs::add);
        itemStacks.set(0, inputs);

        fluidStacks.set(1, recipe.fluidInputList);

        // Calculate output
        fluidStacks.set(2, recipe.getOutputFluid());
    }*/
}