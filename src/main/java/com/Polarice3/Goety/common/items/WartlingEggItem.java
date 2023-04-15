package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.Wartling;
import com.Polarice3.Goety.utils.ModMathHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Iterator;

public class WartlingEggItem extends Item {
    public WartlingEggItem() {
        super(new Properties().tab(Goety.TAB));
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel) {
            Wartling wartling = new Wartling(ModEntityType.WARTLING.get(), level);
            wartling.setTrueOwner(player);
            wartling.setLimitedLife(ModMathHelper.ticksToSeconds(9));
            wartling.moveTo(player.blockPosition(), player.getYRot(), player.getXRot());
            if (!player.getActiveEffects().isEmpty()) {
                Iterator<MobEffectInstance> mobEffectInstanceIterator = player.getActiveEffects().iterator();
                if (mobEffectInstanceIterator.hasNext()) {
                    MobEffectInstance effectInstance = mobEffectInstanceIterator.next();
                    if (effectInstance != null) {
                        if (wartling.getStoredEffect() == null) {
                            wartling.setStoredEffect(effectInstance);
                            player.removeEffect(effectInstance.getEffect());
                        }
                    }
                }
            }
            wartling.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(player.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            if (player.level.addFreshEntity(wartling)){
                player.hurt(DamageSource.GENERIC, 2.0F);
            }
        }
        if (!player.isSilent()) {
            level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.SCULK_BLOCK_SPREAD, player.getSoundSource(), 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
        }
        itemstack.shrink(1);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

}
