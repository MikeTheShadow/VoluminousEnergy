package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERNGExperienceRecipe;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ExperienceHelper {

    public static void awardUsedRecipesAndPopExperience(ServerPlayer pPlayer, VETileEntity tile) {
        List<RecipeHolder<?>> list = getRecipesToAwardAndPopExperience(pPlayer.serverLevel(), pPlayer.position(), tile);
        pPlayer.awardRecipes(list);

        for (RecipeHolder<?> recipeholder : list) {
            if (recipeholder != null) {
                pPlayer.triggerRecipeCrafted(recipeholder, new ArrayList<>());
            }
        }

        tile.getRecipesUsed().clear();
        tile.setChanged();
    }

    public static List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel pLevel, Vec3 pPopVec, VETileEntity tile) {
        List<RecipeHolder<?>> list = new ArrayList<>();

        for (Object2IntMap.Entry<ResourceLocation> entry : tile.getRecipesUsed().object2IntEntrySet()) {
            pLevel.getRecipeManager().byKey(entry.getKey()).ifPresent(recipeHolder -> {
                list.add(recipeHolder);
                if (recipeHolder.value() instanceof VERNGExperienceRecipe experienceRecipe) {
                    float xp = (experienceRecipe.getMinExp() + experienceRecipe.getMaxExp()) / 2.0f;
                    createExperience(pLevel, pPopVec, entry.getIntValue(), xp);
                } else if (recipeHolder.value() instanceof AbstractCookingRecipe cookingRecipe) {
                    createExperience(pLevel, pPopVec, entry.getIntValue(), cookingRecipe.getExperience());
                }
            });
        }

        return list;
    }

    private static void createExperience(ServerLevel pLevel, Vec3 pPopVec, int pRecipeIndex, float pExperience) {
        int i = Mth.floor((float) pRecipeIndex * pExperience);
        float f = Mth.frac((float) pRecipeIndex * pExperience);
        if (f != 0.0F && Math.random() < (double) f) {
            i++;
        }

        if (i > 0) {
            ExperienceOrb.award(pLevel, pPopVec, i);
        }
    }
}
