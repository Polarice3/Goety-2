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
import net.minecraft.client.gui.GuiGraphics;
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
        this.background = guiHelper.createBlankDrawable(125, 38);
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

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .addItemStack(new ItemStack(Items.GLASS_BOTTLE))
                .setSlotName(inputSlotName);

        builder.addSlot(RecipeIngredientRole.CATALYST, 36, 1)
                .addItemStack(new ItemStack(Items.NETHER_WART))
                .setSlotName(catalystSlotName);

        builder.addSlot(RecipeIngredientRole.CATALYST, 72, 1)
                .addItemStack(recipe.getCatalyst())
                .setSlotName(catalystSlotName);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 108, 1)
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

        graphics.pose().pushPose();
        graphics.pose().translate((getWidth() / 1.5F) - 10 * 1.4F, (getHeight() / 2.0F) - 2, 0);
        graphics.pose().scale(1.4F, 1.4F, 1.4F);
        this.icon.draw(graphics);
        graphics.pose().popPose();

        graphics.pose().pushPose();
        graphics.pose().translate((getWidth() / 3.0F) - 6 * 1.4F, (getHeight() / 2.0F) - 2, 0);
        graphics.pose().scale(1.4F, 1.4F, 1.4F);
        this.icon.draw(graphics);
        graphics.pose().popPose();
    }
}
