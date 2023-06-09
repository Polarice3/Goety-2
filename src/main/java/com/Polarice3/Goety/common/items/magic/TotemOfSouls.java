package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.blocks.CursedCageBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.TotemFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Learned how to make Totem of Souls gain Soul Energy from codes by @Ipsis
 */
public class TotemOfSouls extends Item {
    public static final String SOULS_AMOUNT = "Souls";
    public static final int MAX_SOULS = MainConfig.MaxSouls.get();

    public TotemOfSouls() {
        super(new Properties().tab(Goety.TAB).stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public void fillItemCategory(CreativeModeTab pGroup, NonNullList<ItemStack> pItems) {
        if (this.allowedIn(pGroup)){
            ItemStack emptySouls = new ItemStack(this);
            setSoulsamount(emptySouls, 0);
            pItems.add(emptySouls);

            ItemStack maxSouls = new ItemStack(this);
            setSoulsamount(maxSouls, MAX_SOULS);
            pItems.add(maxSouls);
        }
    }

    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float) (1.0F - amountColor(stack))/2.0F);
        return Mth.hsvToRgb(f, 1.0F, 1.0F);
    }

    public double amountColor(ItemStack stack){
        if (stack.getTag() != null) {
            int Soulcount = stack.getTag().getInt(SOULS_AMOUNT);
            return 1.0D - (Soulcount / (double) MAX_SOULS);
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
        setSoulsamount(pStack, 0);
        super.onCraftedBy(pStack, pLevel, pPlayer);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack container = itemStack.copy();
        if (container.getTag() != null) {
            if (container.getTag().getInt(SOULS_AMOUNT) > MainConfig.CraftingSouls.get()) {
                TotemOfSouls.decreaseSouls(container, MainConfig.CraftingSouls.get());
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
        if (stack.getTag() == null){
            CompoundTag compound = stack.getOrCreateTag();
            compound.putInt(SOULS_AMOUNT, 0);
        }
        if (stack.getTag().getInt(SOULS_AMOUNT) > MAX_SOULS){
            stack.getTag().putInt(SOULS_AMOUNT, MAX_SOULS);
        }
        if (stack.getTag().getInt(SOULS_AMOUNT) < 0){
            stack.getTag().putInt(SOULS_AMOUNT, 0);
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public static boolean isActivated(ItemStack itemStack){
        return itemStack.getTag() != null;
    }

    private static boolean isFull(ItemStack itemStack) {
        assert itemStack.getTag() != null;
        int Soulcount = itemStack.getTag().getInt(SOULS_AMOUNT);
        return Soulcount == MAX_SOULS;
    }

    private static boolean isEmpty(ItemStack itemStack) {
        assert itemStack.getTag() != null;
        int Soulcount = itemStack.getTag().getInt(SOULS_AMOUNT);
        return Soulcount == 0;
    }

    public static boolean UndyingEffect(Player player){
        ItemStack itemStack = TotemFinder.FindTotem(player);
        if (!itemStack.isEmpty()) {
            if (itemStack.getTag() != null) {
                if (MainConfig.TotemUndying.get()) {
                    return itemStack.getTag().getInt(SOULS_AMOUNT) == MAX_SOULS;
                }
            }
        }
        return false;
    }

    public static int currentSouls(ItemStack itemStack){
        if (itemStack.getTag() != null){
            return itemStack.getTag().getInt(SOULS_AMOUNT);
        } else {
            return 0;
        }
    }

    public static void setSoulsamount(ItemStack itemStack, int souls){
        if (itemStack.getItem() != ModItems.TOTEM_OF_SOULS.get()) {
            return;
        }
        itemStack.getOrCreateTag().putInt(SOULS_AMOUNT, souls);
    }

    public static void increaseSouls(ItemStack itemStack, int souls) {
        if (itemStack.getItem() != ModItems.TOTEM_OF_SOULS.get()) {
            return;
        }
        assert itemStack.getTag() != null;
        int Soulcount = itemStack.getTag().getInt(SOULS_AMOUNT);
        if (!isFull(itemStack)) {
            Soulcount += souls;
            itemStack.getOrCreateTag().putInt(SOULS_AMOUNT, Soulcount);
        }
    }

    public static void decreaseSouls(ItemStack itemStack, int souls) {
        if (itemStack.getItem() != ModItems.TOTEM_OF_SOULS.get()) {
            return;
        }
        assert itemStack.getTag() != null;
        int Soulcount = itemStack.getTag().getInt(SOULS_AMOUNT);
        if (!isEmpty(itemStack)) {
            Soulcount -= souls;
            itemStack.getOrCreateTag().putInt(SOULS_AMOUNT, Soulcount);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getTag() != null;
    }

    @Override
    public int getBarWidth(ItemStack stack){
        if (stack.getTag() != null) {
            int Soulcount = stack.getTag().getInt(SOULS_AMOUNT);
            return Math.round((Soulcount * 13.0F / MAX_SOULS));
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
            tooltip.add(Component.translatable("info.goety.totem_of_souls.souls", Soulcounts, MAX_SOULS));
        }
    }

}
