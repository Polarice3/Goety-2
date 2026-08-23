package com.Polarice3.Goety.compat.jei;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.crafting.SoulAbsorberRecipes;
import com.Polarice3.Goety.common.items.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class SoulAbsorberCategory implements IRecipeCategory<SoulAbsorberRecipes> {
    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;
    private final Component localizedName;

    public SoulAbsorberCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.SOUL_ABSORBER.get()));
        background = guiHelper.drawableBuilder(Goety.location("textures/gui/jei/jei_gui.png"), 82, 220, 74, 34)
                .addPadding(0, 0, 20, 20)
                .build();
        localizedName = Component.translatable("gui.jei.category.soul_absorber");
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
    public RecipeType<SoulAbsorberRecipes> getRecipeType() {
        return JeiRecipeTypes.SOUL_ABSORBER;
    }

    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SoulAbsorberRecipes recipe, IFocusGroup ingredients) {
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 9)
                .addIngredients(recipe.getIngredients().get(0));

        ItemStack itemStack = new ItemStack(ModItems.SOUL_ENERGY.get());
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 77, 9).addItemStack(itemStack);
    }

    @Override
    public void draw(SoulAbsorberRecipes recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX, double mouseY) {
        int infoTextX = 85;
        int infotextY = 27;

        this.drawStringCentered(stack, Minecraft.getInstance().font,
                I18n.get("jei.goety.soulAmount", recipe.getSoulIncrease()),
                infoTextX, infotextY);
    }

    protected void drawStringCentered(GuiGraphics matrixStack, Font fontRenderer, String text, int x, int y) {
        matrixStack.drawString(fontRenderer, text, (int) (x - fontRenderer.width(text) / 2.0f), y, 0x01a7ac, false);
    }
}
