package com.Polarice3.Goety.compat.jei;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
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
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WitchBrewCategory implements IRecipeCategory<WitchBrewJeiRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final String inputSlotName = "inputSlot";
    private final String catalystSlotName = "catalystSlot";
    private final String outputSlotName = "outputSlot";

    public WitchBrewCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(125, 60);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.BREWING_CAULDRON.get()));
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
    public RecipeType<WitchBrewJeiRecipe> getRecipeType() {
        return JeiRecipeTypes.BREWING;
    }

    @Override
    public Component getTitle() {
        return ModBlocks.BREWING_CAULDRON.get().getName();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WitchBrewJeiRecipe recipe, IFocusGroup focuses) {

        int y = 20;

        builder.addSlot(RecipeIngredientRole.INPUT, 1, y)
                .addItemStack(new ItemStack(Items.GLASS_BOTTLE))
                .setSlotName(inputSlotName);

        builder.addSlot(RecipeIngredientRole.CATALYST, 36, y)
                .addItemStack(new ItemStack(Items.NETHER_WART))
                .setSlotName(catalystSlotName);

        builder.addSlot(RecipeIngredientRole.CATALYST, 72, y)
                .addItemStack(recipe.getCatalyst())
                .setSlotName(catalystSlotName);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 108, y)
                .addItemStack(recipe.getOutput())
                .setSlotName(outputSlotName);
    }


    @Override
    public void draw(@NotNull WitchBrewJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        Optional<ItemStack> rightStack = recipeSlotsView.findSlotByName(catalystSlotName)
                .flatMap(IRecipeSlotView::getDisplayedItemStack);

        Optional<ItemStack> outputStack = recipeSlotsView.findSlotByName(outputSlotName)
                .flatMap(IRecipeSlotView::getDisplayedItemStack);

        if (rightStack.isEmpty() || outputStack.isEmpty()) {
            return;
        }

        if (recipe.getCapacity() != 0) {
            this.drawStringCentered(graphics, Minecraft.getInstance().font,
                    I18n.get("jei.goety.capacityUse", recipe.getCapacity()),
                    63, 0);
        }

        if (recipe.getSoulCost() != 0) {
            this.drawStringCentered(graphics, Minecraft.getInstance().font,
                    I18n.get("jei.goety.single.soulcost", recipe.getSoulCost()),
                    63, 9);
        }

        graphics.pose().pushPose();
        graphics.pose().translate((getWidth() / 1.5F) - 10 * 1.4F, (getHeight() / 2.0F) + 8, 0);
        graphics.pose().scale(1.4F, 1.4F, 1.4F);
        this.icon.draw(graphics);
        graphics.pose().popPose();

        graphics.pose().pushPose();
        graphics.pose().translate((getWidth() / 3.0F) - 6 * 1.4F, (getHeight() / 2.0F) + 8, 0);
        graphics.pose().scale(1.4F, 1.4F, 1.4F);
        this.icon.draw(graphics);
        graphics.pose().popPose();
    }

    protected void drawStringCentered(GuiGraphics matrixStack, Font fontRenderer, String text, int x, int y) {
        matrixStack.drawString(fontRenderer, text, (int) (x - fontRenderer.width(text) / 2.0f), y, 0, false);
    }
}
