package com.Polarice3.Goety.compat.jei;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.crafting.CauldronRecipe;
import com.Polarice3.Goety.common.crafting.CauldronSusStewRecipe;
import com.Polarice3.Goety.common.items.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.block.FlowerBlock;

public class ModCauldronCategory implements IRecipeCategory<CauldronRecipe> {
    private final IDrawable background;
    private final IDrawable cauldron;
    private final IDrawable square1;
    private final IDrawable square2;
    private final IDrawable arrow;
    private final IDrawable arrow2;
    private final Component localizedName;
    private final IDrawable icon;

    public ModCauldronCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(176, 80);
        this.localizedName = Component.translatable(Goety.MOD_ID + ".jei.cauldron");
        this.cauldron = guiHelper.createDrawable(
                new ResourceLocation(Goety.MOD_ID, "textures/gui/jei/cauldron.png"), 0, 0, 31, 31);
        this.square1 = guiHelper.createDrawable(
                new ResourceLocation(Goety.MOD_ID, "textures/gui/jei/squares.png"), 0, 0, 31, 31);
        this.square2 = guiHelper.createDrawable(
                new ResourceLocation(Goety.MOD_ID, "textures/gui/jei/squares.png"), 32, 0, 31, 31);
        this.arrow = guiHelper.createDrawable(
                new ResourceLocation(Goety.MOD_ID, "textures/gui/jei/arrow.png"), 0, 0, 64, 46);
        this.arrow2 = guiHelper.createDrawable(
                new ResourceLocation(Goety.MOD_ID, "textures/gui/jei/down_arrow.png"), 0, 0, 46, 64);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.NIGHTSHADE_BLOSSOM.get()));
    }

    @Override
    public RecipeType<CauldronRecipe> getRecipeType() {
        return JeiRecipeTypes.CAULDRON;
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
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, CauldronRecipe recipe, IFocusGroup ingredients) {

        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            recipeLayout.addSlot(RecipeIngredientRole.INPUT, i * 18 + 10, 6)
                    .addIngredients(recipe.getIngredients().get(i));
        }

        IRecipeSlotBuilder output = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 109, 35);
        if (recipe instanceof CauldronSusStewRecipe) {
            for (FlowerBlock flower : CauldronSusStewRecipe.getFlowers()) {
                ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);
                SuspiciousStewItem.saveMobEffect(stew, flower.getSuspiciousEffect(), flower.getEffectDuration());
                output.addItemStack(stew);
            }
        } else {
            output.addItemStack(recipe.getResultItem(null));
        }

        recipeLayout.addSlot(RecipeIngredientRole.CATALYST, 109 + 36, 35)
                        .addIngredients(recipe.getTakeWith());

        recipeLayout.addSlot(RecipeIngredientRole.CATALYST, 10, 35)
                .addItemStack(new ItemStack(ModItems.NIGHTSHADE_BLOSSOM.get()));
    }

    @Override
    public void draw(CauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX, double mouseY) {
        RenderSystem.enableBlend();
        this.cauldron.draw(stack, 40, 27);
        this.square1.draw(stack, 101 + 36, 27);
        this.square1.draw(stack, 2, 27);
        this.square2.draw(stack, 101, 27);
        this.arrow.draw(stack, 79, 35);
        this.arrow2.draw(stack, 48, 22);
        RenderSystem.disableBlend();
        this.drawStringCentered(stack, Minecraft.getInstance().font,
                I18n.get("jei.goety.single.soulcost", recipe.getSoulCost()), 46, 70);
    }

    protected void drawStringCentered(GuiGraphics matrixStack, Font fontRenderer, String text, int x, int y) {
        matrixStack.drawString(fontRenderer, text, (int) (x - fontRenderer.width(text) / 2.0f), y, 0, false);
    }
}