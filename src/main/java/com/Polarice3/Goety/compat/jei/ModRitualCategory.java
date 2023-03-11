package com.Polarice3.Goety.compat.jei;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.items.ModItems;
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
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModRitualCategory implements IRecipeCategory<RitualRecipe> {
    private final IDrawable background;
    private final IDrawable arrow;
    private final Component localizedName;
    private final ItemStack darkAltar = new ItemStack(ModBlocks.DARK_ALTAR.get());
    private final ItemStack pedestals = new ItemStack(ModItems.PEDESTAL_DUMMY.get());
    private final int iconWidth = 16;
    private final int ritualCenterX;
    private final int ritualCenterY;
    private int recipeOutputOffsetX = 50;

    public ModRitualCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(168, 120);
        this.ritualCenterX = this.background.getWidth() / 2 - this.iconWidth / 2 - 30;
        this.ritualCenterY = this.background.getHeight() / 2 - this.iconWidth / 2 + 10;
        this.localizedName = Component.translatable(Goety.MOD_ID + ".jei.ritual");
        this.darkAltar.getOrCreateTag().putBoolean("RenderFull", true);
        this.pedestals.getOrCreateTag().putBoolean("RenderFull", true);
        this.arrow = guiHelper.createDrawable(
                new ResourceLocation(Goety.MOD_ID, "textures/gui/jei/arrow.png"), 0, 0, 64, 46);
    }

    @Override
    public RecipeType<RitualRecipe> getRecipeType() {
        return JeiRecipeTypes.RITUAL;
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
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, RitualRecipe recipe, IFocusGroup ingredients) {
        int index = 0;
        this.recipeOutputOffsetX = 75;

        recipeLayout.addSlot(RecipeIngredientRole.INPUT, this.ritualCenterX, this.ritualCenterY - 15)
                .addIngredients(recipe.getActivationItem());
        recipeLayout.addSlot(RecipeIngredientRole.CATALYST, this.ritualCenterX, this.ritualCenterY)
                .addItemStack(this.darkAltar);

        int sacrificialCircleRadius = 30;
        int pedestalsPaddingVertical = 20;
        int pedestalsPaddingHorizontal = 15;
        List<Vec3i> pedestalsPosition = Stream.of(
                new Vec3i(this.ritualCenterX, this.ritualCenterY - sacrificialCircleRadius, 0),
                new Vec3i(this.ritualCenterX + sacrificialCircleRadius, this.ritualCenterY, 0),
                new Vec3i(this.ritualCenterX, this.ritualCenterY + sacrificialCircleRadius, 0),
                new Vec3i(this.ritualCenterX - sacrificialCircleRadius, this.ritualCenterY, 0),

                new Vec3i(this.ritualCenterX + pedestalsPaddingHorizontal,
                        this.ritualCenterY - sacrificialCircleRadius,
                        0),
                new Vec3i(this.ritualCenterX + sacrificialCircleRadius,
                        this.ritualCenterY - pedestalsPaddingVertical, 0),
                new Vec3i(this.ritualCenterX - pedestalsPaddingHorizontal,
                        this.ritualCenterY + sacrificialCircleRadius,
                        0),
                new Vec3i(this.ritualCenterX - sacrificialCircleRadius,
                        this.ritualCenterY + pedestalsPaddingVertical, 0),

                new Vec3i(this.ritualCenterX - pedestalsPaddingHorizontal,
                        this.ritualCenterY - sacrificialCircleRadius,
                        0),
                new Vec3i(this.ritualCenterX + sacrificialCircleRadius,
                        this.ritualCenterY + pedestalsPaddingVertical, 0),
                new Vec3i(this.ritualCenterX + pedestalsPaddingHorizontal,
                        this.ritualCenterY + sacrificialCircleRadius,
                        0),
                new Vec3i(this.ritualCenterX - sacrificialCircleRadius,
                        this.ritualCenterY - pedestalsPaddingVertical, 0)
        ).collect(Collectors.toList());

        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            Vec3i pos = pedestalsPosition.get(i);

            recipeLayout.addSlot(RecipeIngredientRole.INPUT, pos.getX(), pos.getY() - 5)
                    .addIngredients(recipe.getIngredients().get(i));

            recipeLayout.addSlot(RecipeIngredientRole.RENDER_ONLY, pos.getX(), pos.getY())
                    .addItemStack(this.pedestals);

        }

        recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, this.ritualCenterX + this.recipeOutputOffsetX, this.ritualCenterY - 15)
                .addItemStack(recipe.getResultItem());

        recipeLayout.addSlot(RecipeIngredientRole.CATALYST, this.ritualCenterX + this.recipeOutputOffsetX, this.ritualCenterY)
                .addItemStack(this.darkAltar);

        if (recipe.getCraftType().contains("animation")){
            recipeLayout.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0)
                    .addItemStack(new ItemStack(Items.EGG));
        } else if (recipe.getCraftType().contains("necroturgy") || recipe.getCraftType().contains("lich")){
            recipeLayout.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0)
                    .addItemStack(new ItemStack(Items.ZOMBIE_HEAD));
        } else if (recipe.getCraftType().contains("forge")){
            recipeLayout.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0)
                    .addItemStack(new ItemStack(Items.ANVIL));
        } else if (recipe.getCraftType().contains("magic")){
            recipeLayout.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0)
                    .addItemStack(new ItemStack(Items.ENCHANTING_TABLE));
        } else if (recipe.getCraftType().contains("adept_nether")){
            recipeLayout.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0)
                    .addItemStack(new ItemStack(Items.NETHERRACK));
        } else if (recipe.getCraftType().contains("sabbath")){
            recipeLayout.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0)
                    .addItemStack(new ItemStack(Items.CRYING_OBSIDIAN));
        } else if (recipe.getCraftType().contains("air")){
            recipeLayout.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0)
                    .addItemStack(new ItemStack(Items.FEATHER));
        } else if (recipe.getCraftType().contains("storm")){
            recipeLayout.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0)
                    .addItemStack(new ItemStack(Items.LIGHTNING_ROD));
        } else {
            recipeLayout.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0)
                    .addItemStack(new ItemStack(Items.OBSIDIAN));
        }
    }

    @Override
    public void draw(RitualRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        RenderSystem.enableBlend();
        this.arrow.draw(stack, this.ritualCenterX + this.recipeOutputOffsetX - 20, this.ritualCenterY);
        RenderSystem.disableBlend();

        int infotextY = 0;
        if (recipe.requiresSacrifice()) {
            infotextY += 13;
            this.drawStringCentered(stack, Minecraft.getInstance().font,
                    I18n.get("jei.goety.sacrifice", I18n.get(recipe.getEntityToSacrificeDisplayName())), 84, infotextY);
        }

        if (recipe.getEntityToSummon() != null) {
            infotextY += 13;
            this.drawStringCentered(stack, Minecraft.getInstance().font,
                    I18n.get("jei.goety.summon", I18n.get(recipe.getEntityToSummon().getDescriptionId())),
                    84, infotextY);
        }

        if (recipe.getCraftType() != null) {
            this.drawStringCentered(stack, Minecraft.getInstance().font,
                    I18n.get("jei.goety.craftType." + I18n.get(recipe.getCraftType())),
                    84, 5);
        }

    }

    protected int getStringCenteredMaxX(Font fontRenderer, String text, int x, int y) {
        int width = fontRenderer.width(text);
        int actualX = (int) (x - width / 2.0f);
        return actualX + width;
    }

    protected void drawStringCentered(PoseStack matrixStack, Font fontRenderer, String text, int x, int y) {
        fontRenderer.draw(matrixStack, text, (x - fontRenderer.width(text) / 2.0f), y, 0);
    }

}
/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */