package com.Polarice3.Goety.compat.jei;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.crafting.BrazierRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ModBrazierCategory implements IRecipeCategory<BrazierRecipe> {
    private final IDrawable background;
    private final IDrawable flame;
    private final IDrawable circle;
    private final IDrawable arrow;
    private final IDrawable arrow2;
    private final Component localizedName;
    private final ItemStack brazier = new ItemStack(ModBlocks.NECRO_BRAZIER.get());

    public ModBrazierCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(134, 80);
        this.localizedName = Component.translatable(Goety.MOD_ID + ".jei.brazier");
        this.brazier.getOrCreateTag().putBoolean("RenderFull", true);
        this.flame = guiHelper.createDrawable(
                new ResourceLocation(Goety.MOD_ID, "textures/gui/jei/brazier.png"), 0, 0, 31, 31);
        this.circle = guiHelper.createDrawable(
                new ResourceLocation(Goety.MOD_ID, "textures/gui/jei/circle.png"), 0, 0, 31, 31);
        this.arrow = guiHelper.createDrawable(
                new ResourceLocation(Goety.MOD_ID, "textures/gui/jei/arrow.png"), 0, 0, 64, 46);
        this.arrow2 = guiHelper.createDrawable(
                new ResourceLocation(Goety.MOD_ID, "textures/gui/jei/down_arrow.png"), 0, 0, 46, 64);
    }

    @Override
    public RecipeType<BrazierRecipe> getRecipeType() {
        return JeiRecipeTypes.BRAZIER;
    }

    @Override
    public Component getTitle() {
        return this.localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, BrazierRecipe recipe, IFocusGroup ingredients) {

        recipeLayout.addSlot(RecipeIngredientRole.CATALYST, 48, 35)
                .addItemStack(this.brazier);

        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            recipeLayout.addSlot(RecipeIngredientRole.INPUT, i * 18 + 10, 6)
                    .addIngredients(recipe.getIngredients().get(i));
        }

        recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 99, 35)
                .addItemStack(recipe.getResultItem());
    }

    @Override
    public void draw(BrazierRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        RenderSystem.enableBlend();
        this.flame.draw(stack, 40, 20);
        this.circle.draw(stack, 91, 27);
        this.arrow.draw(stack, 79, 35);
        this.arrow2.draw(stack, 48, 22);
        RenderSystem.disableBlend();
        this.drawStringCentered(stack, Minecraft.getInstance().font,
                I18n.get("jei.goety.brazier.soulcost", recipe.getSoulCost()), 46, 70);
    }

    protected void drawStringCentered(PoseStack matrixStack, Font fontRenderer, String text, int x, int y) {
        fontRenderer.draw(matrixStack, text, (x - fontRenderer.width(text) / 2.0f), y, 0);
    }
}