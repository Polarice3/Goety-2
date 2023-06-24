package com.Polarice3.Goety.common.items.brew;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.utils.BrewUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class BrewApple extends Item {
    public BrewApple() {
        super((new Item.Properties()).tab(Goety.TAB).food(Foods.APPLE));
    }

    public ItemStack finishUsingItem(ItemStack pStack, Level p_42985_, LivingEntity p_42986_) {
        Player player = p_42986_ instanceof Player ? (Player)p_42986_ : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, pStack);
        }

        if (!p_42985_.isClientSide) {
            for(MobEffectInstance mobeffectinstance : PotionUtils.getMobEffects(pStack)) {
                if (mobeffectinstance.getEffect().isInstantenous()) {
                    mobeffectinstance.getEffect().applyInstantenousEffect(player, player, p_42986_, mobeffectinstance.getAmplifier(), 1.0D);
                } else {
                    p_42986_.addEffect(new MobEffectInstance(mobeffectinstance));
                }
            }
            for (BrewEffectInstance brewEffectInstance : BrewUtils.getBrewEffects(pStack)){
                brewEffectInstance.getEffect().drinkBlockEffect(player, player, p_42986_, brewEffectInstance.getAmplifier(), BrewUtils.getAreaOfEffect(pStack));
            }
        }

        if (player != null) {
            if (!player.getAbilities().instabuild) {
                pStack.shrink(1);
            }
        }

        p_42986_.gameEvent(GameEvent.EAT);
        return pStack;
    }

    public int getUseDuration(ItemStack p_43001_) {
        return 32 - BrewUtils.getQuaff(p_43001_);
    }

    public UseAnim getUseAnimation(ItemStack p_42997_) {
        return UseAnim.EAT;
    }

    public InteractionResultHolder<ItemStack> use(Level p_42993_, Player p_42994_, InteractionHand p_42995_) {
        return ItemUtils.startUsingInstantly(p_42993_, p_42994_, p_42995_);
    }
}
