package com.veteam.voluminousenergy.util.recipe;


/**
 * This is for recipes that produce experience. An example of this is the vanilla
 * furnace or the CrusherRecipe
 * {@link com.veteam.voluminousenergy.recipe.CrusherRecipe}
 */
public interface IExperienceRecipe {

    int getMinExperience();
    int getMaxExperience();

    void setBoth(int min,int max);

}
