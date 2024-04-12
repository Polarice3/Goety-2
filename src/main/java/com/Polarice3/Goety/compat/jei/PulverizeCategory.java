package com.Polarice3.Goety.compat.jei;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.crafting.PulverizeRecipe;
import com.Polarice3.Goety.common.items.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class PulverizeCategory implements IRecipeCategory<PulverizeRecipe> {
    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;
    private final Component localizedName;

    public PulverizeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.PULVERIZE_FOCUS.get()));
        background = guiHelper.drawableBuilder(new ResourceLocation(Goety.MOD_ID, "textures/gui/jei/jei_gui.png"), 0, 220, 82, 34)
                .addPadding(0, 0, 0, 0)
                .build();
        localizedName = Component.translatable("gui.jei.category.pulverize");
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
    public RecipeType<PulverizeRecipe> getRecipeType() {
        return JeiRecipeTypes.PULVERIZE;
    }

    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PulverizeRecipe recipe, IFocusGroup ingredients) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 9)
                .addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 9)
                .addItemStack(recipe.getResultItem().isEmpty()
                        ? recipe.getBlockResult().asItem().getDefaultInstance()
                        : recipe.getResultItem());
    }
}
