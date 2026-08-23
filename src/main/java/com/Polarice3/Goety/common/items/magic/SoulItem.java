package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.items.magic.ISoulContainer;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SoulItem extends Item implements ISoulContainer {

    public SoulItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        ISoulContainer.setSoulsAmount(pStack, 0);
        super.onCraftedBy(pStack, pLevel, pPlayer);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
        int soulCount = ISoulContainer.currentSouls(itemInHand);
        if (soulCount > 0) {
            if (SEHelper.getSoulsContainer(pPlayer)) {
                if (pLevel.isClientSide) {
                    pLevel.playSound(pPlayer, pPlayer.blockPosition(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 0.5F, 1.0F);
                    return InteractionResultHolder.consume(itemInHand);
                } else {
                    SEHelper.increaseSouls(pPlayer, soulCount);
                }
                return InteractionResultHolder.success(itemInHand);
            }
        }
        return InteractionResultHolder.pass(itemInHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.getTag() != null) {
            int soulCounts = stack.getTag().getInt(SOULS_AMOUNT);
            tooltip.add(Component.translatable("info.goety.item.souls", soulCounts));
        }
    }
}
