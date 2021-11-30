package com.veteam.voluminousenergy.compat.jei;

public class CrushingCategory {/*implements IRecipeCategory<CrusherRecipe> {

    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;

    public CrushingCategory(IGuiHelper guiHelper){
        // 68, 12 | 40, 65 -> 10 px added for chance
        //ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/jei/jei.png");
        background = guiHelper.drawableBuilder(CrusherScreen.getGUI(), 68, 12, 40, 70).build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(VEBlocks.CRUSHER_BLOCK));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(CrusherScreen.getGUI(), 176, 0, 17, 24).buildAnimated(200, IDrawableAnimated.StartDirection.TOP, false);
    }

    @Override
    public ResourceLocation getUid(){
        return VoluminousEnergyPlugin.CRUSHING_UID;
    }

    @Override
    public Class<? extends CrusherRecipe> getRecipeClass() {
        return CrusherRecipe.class;
    }

    @Override
    public Component getTitle() {
        return TextUtil.translateString("jei.voluminousenergy.crushing");
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
    public void draw(CrusherRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack,10, 19);


        if (recipe.getRngItem() != null && recipe.getRngItem().getItem() != Items.AIR){ // Check RNG if it's not air
            int chance = (int)(recipe.getChance()*100);
            int xPos = 20;
            if (chance < 100 && chance >= 10){
                xPos += 3;
            } else if (chance < 10){
                xPos += 5;
            }
            Minecraft.getInstance().font.draw(matrixStack,chance + "%",xPos,65,0x606060);
        }

    }

    @Override
    public void setIngredients(CrusherRecipe recipe, IIngredients ingredients) {
        // STACK needs to be 64 for recipes that require more than 1 of the input item
        // This for loop ensures that every input can be right clicked, maybe it can just fetch the current ingredient
        // to save CPU cycles... but this works.
        ArrayList<ItemStack> inputStacks = new ArrayList<>();
        for (ItemStack tempStack : recipe.getIngredient().getItems()){
            tempStack.setCount(64);
            inputStacks.add(tempStack.copy());
        }
        ingredients.setInputs(VanillaTypes.ITEM, inputStacks);

        // OUTPUT
        List<ItemStack> outputStacks = new ArrayList<>();
        outputStacks.add(recipe.getResultItem()); // Normal output

        if (recipe.getRngItem() != null && recipe.getRngItem().getItem() != Items.AIR){ // Check RNG if it's not air
            outputStacks.add(recipe.getRngItem());
        }

        ingredients.setOutputs(VanillaTypes.ITEM, outputStacks);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CrusherRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, false, 11, 0);
        itemStacks.init(1, false, 2, 45);

        // Should only be one ingredient...
        List<ItemStack> inputs = new ArrayList<>();
        Arrays.stream(recipe.getIngredient().getItems()).map(s -> {
            ItemStack stack = s.copy();
            stack.setCount(recipe.getIngredientCount());
            return stack;
        }).forEach(inputs::add);
        itemStacks.set(0, inputs);

        // Calculate output
        ItemStack tempStack = recipe.getResultItem(); // Get Item since amount will be wrong
        Item outputItem = tempStack.getItem();
        ItemStack jeiStack = new ItemStack(outputItem, recipe.getOutputAmount()); // Create new stack for JEI with correct amount
        itemStacks.set(1, jeiStack);

        // Calculate RNG stack, only if RNG stack exists
        if (recipe.getRngItem() != null && recipe.getRngItem().getItem() != Items.AIR){ // Don't create the slot if the slot will be empty!
            itemStacks.init(2, true, 20, 45);
            tempStack = recipe.getRngItem();
            Item rngItem = tempStack.getItem();
            ItemStack rngStack = new ItemStack(rngItem, recipe.getOutputRngAmount());
            itemStacks.set(2, rngStack);
        }
    }

*/}