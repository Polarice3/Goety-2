package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.Wartling;
import com.Polarice3.Goety.common.items.curios.WarlockGarmentItem;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WartlingEggItem extends Item {
    public WartlingEggItem() {
        super(new Properties());
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        warlockUse(level, player, itemstack);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    public static void warlockUse(Level level, Player player, ItemStack itemStack){
        if (level instanceof ServerLevel serverLevel) {
            Wartling wartling = new Wartling(ModEntityType.WARTLING.get(), level);
            wartling.setTrueOwner(player);
            wartling.setLimitedLife(MathHelper.secondsToTicks(9));
            wartling.moveTo(player.blockPosition(), player.getYRot(), player.getXRot());
            player.getActiveEffects().stream().filter(mobEffect -> mobEffect.getEffect().getCategory() == MobEffectCategory.HARMFUL && !mobEffect.getEffect().getCurativeItems().isEmpty()).findFirst().ifPresent(effect -> {
                wartling.setStoredEffect(effect);
                player.removeEffect(effect.getEffect());
            });
            wartling.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(player.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            if (!CuriosFinder.hasCurio(player, itemStack1 -> itemStack1.getItem() instanceof WarlockGarmentItem)) {
                if (player.level.addFreshEntity(wartling)) {
                    player.hurt(player.damageSources().generic(), 2.0F);
                }
            } else {
                player.level.addFreshEntity(wartling);
            }
        }
        if (!player.isSilent()) {
            level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.SCULK_BLOCK_SPREAD, player.getSoundSource(), 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
        }
        if (!player.getAbilities().instabuild) {
            if (CuriosFinder.hasWarlockRobe(player)) {
                if (level.random.nextFloat() > 0.1F){
                    itemStack.shrink(1);
                }
            } else {
                itemStack.shrink(1);
            }
        }
    }

}
