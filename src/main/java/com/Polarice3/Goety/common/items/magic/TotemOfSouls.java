package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.ItemConfig;
import com.Polarice3.Goety.api.items.magic.ITotem;
import com.Polarice3.Goety.common.blocks.CursedCageBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Learned how to make Totem of Souls gain Soul Energy from codes by @Ipsis
 */
public class TotemOfSouls extends Item implements ITotem {

    public TotemOfSouls() {
        super(new Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    public int getMaxSouls(){
        return MAX_SOULS;
    }

    public ItemStack getEmptyTotem(){
        ItemStack emptySouls = new ItemStack(this);
        ITotem.setSoulsamount(emptySouls, 0);
        ITotem.setMaxSoulAmount(emptySouls, this.getMaxSouls());
        return emptySouls;
    }

    public ItemStack getFilledTotem(){
        ItemStack maxSouls = new ItemStack(this);
        ITotem.setSoulsamount(maxSouls, this.getMaxSouls());
        ITotem.setMaxSoulAmount(maxSouls, this.getMaxSouls());
        return maxSouls;
    }

    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float) (1.0F - amountColor(stack))/2.0F);
        return Mth.hsvToRgb(f, 1.0F, 1.0F);
    }

    public double amountColor(ItemStack stack){
        if (stack.getTag() != null) {
            int Soulcount = stack.getTag().getInt(SOULS_AMOUNT);
            int MaxSouls = stack.getTag().getInt(MAX_SOUL_AMOUNT);
            return 1.0D - (Soulcount / (double) MaxSouls);
        } else {
            return 1.0D;
        }
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        ITotem.setSoulsamount(pStack, 0);
        ITotem.setMaxSoulAmount(pStack, this.getMaxSouls());
        super.onCraftedBy(pStack, pLevel, pPlayer);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack container = itemStack.copy();
        if (container.getTag() != null) {
            if (container.getTag().getInt(SOULS_AMOUNT) > ItemConfig.CraftingSouls.get()) {
                ITotem.decreaseSouls(container, ItemConfig.CraftingSouls.get());
                return container;
            } else {
                return new ItemStack(ModItems.SPENT_TOTEM.get());
            }
        } else {
            return new ItemStack(ModItems.SPENT_TOTEM.get());
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        this.setTagTick(stack);
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public static boolean isActivated(ItemStack itemStack){
        return itemStack.getTag() != null;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getTag() != null;
    }

    @Override
    public int getBarWidth(ItemStack stack){
        if (stack.getTag() != null) {
            int Soulcount = stack.getTag().getInt(SOULS_AMOUNT);
            int MaxSouls = stack.getTag().getInt(MAX_SOUL_AMOUNT);
            return Math.round((Soulcount * 13.0F / MaxSouls));
        } else {
            return 0;
        }
    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level world = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.is(ModBlocks.CURSED_CAGE_BLOCK.get()) && !blockstate.getValue(CursedCageBlock.POWERED)) {
            ItemStack itemstack = pContext.getItemInHand();
            if (!world.isClientSide) {
                ((CursedCageBlock) ModBlocks.CURSED_CAGE_BLOCK.get()).setItem(world, blockpos, blockstate, itemstack);
                world.levelEvent(null, 1010, blockpos, Item.getId(this));
                itemstack.shrink(1);
            }

            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.getTag() != null) {
            int Soulcounts = stack.getTag().getInt(SOULS_AMOUNT);
            int MaxSouls = stack.getTag().getInt(MAX_SOUL_AMOUNT);
            tooltip.add(Component.translatable("info.goety.totem_of_souls.souls", Soulcounts, MaxSouls));
        }
    }

}
