package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.capabilities.lichdom.ILichdom;
import com.Polarice3.Goety.common.capabilities.soulenergy.ISoulEnergy;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UndeathPotionItem extends Item {
    public UndeathPotionItem() {
        super(new Item.Properties()
                .tab(Goety.TAB)
                .rarity(Rarity.EPIC)
                .craftRemainder(Items.GLASS_BOTTLE)
                .setNoRepair()
                .stacksTo(1)
        );
    }

    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        super.finishUsingItem(pStack, pLevel, pEntityLiving);
        if (pEntityLiving instanceof Player player) {
            if (MainConfig.LichEnable.get()) {
                if (!pLevel.isClientSide) {
                    ILichdom lichdom = LichdomHelper.getCapability(player);
                    ISoulEnergy soulEnergy = SEHelper.getCapability(player);
                    boolean isLich = lichdom.getLichdom();
                    ServerLevel serverWorld = (ServerLevel) pLevel;
                    if (serverWorld.getMoonBrightness() > 0.9F) {
                        if (!isLich && soulEnergy.getSEActive() && soulEnergy.getArcaBlock() != null) {
                            if (pEntityLiving instanceof ServerPlayer serverPlayer) {
                                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, pStack);
                                serverPlayer.awardStat(Stats.ITEM_USED.get(this));
                            }
                            lichdom.setLichdom(true);
                            lichdom.setLichMode(true);
                            LichdomHelper.sendLichUpdatePacket(player);
                            player.displayClientMessage(Component.translatable("info.goety.lichdom.success"), true);
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 1));
                            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20, 1));
                            player.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.5F);
                            player.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 1.0F, 0.5F);
                            player.hurt(DamageSource.WITHER, 50.0F);
                        } else if (isLich) {
                            player.heal(20.0F);
                        } else {
                            player.displayClientMessage(Component.translatable("info.goety.lichdom.fail"), true);
                            player.hurt(DamageSource.MAGIC, 50.0F);
                        }
                    } else {
                        if (!isLich) {
                            player.displayClientMessage(Component.translatable("info.goety.lichdom.fail"), true);
                            player.hurt(DamageSource.MAGIC, 50.0F);
                        } else {
                            player.heal(20.0F);
                        }
                    }
                    if (!player.getAbilities().instabuild) {
                        pStack.shrink(1);
                    }
                }
            } else {
                player.displayClientMessage(Component.translatable("info.goety.lichdom.disable").withStyle(ChatFormatting.DARK_RED), false);
            }
        }
        return pStack.isEmpty() ? new ItemStack(Items.GLASS_BOTTLE) : pStack;
    }

    public int getUseDuration(ItemStack pStack) {
        return 40;
    }

    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        if (!pLevel.isClientSide){
            ServerLevel serverWorld = (ServerLevel) pLevel;
            for (int i = 0; i < pLevel.random.nextInt(35) + 10; ++i) {
                double d = pLevel.random.nextGaussian() * 0.2D;
                serverWorld.sendParticles(ParticleTypes.SMOKE, pPlayer.getX(), pPlayer.getEyeY(), pPlayer.getZ(), 0, d, d, d, 0.5F);
            }
        }
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
        if (!MainConfig.LichEnable.get()){
            p_41423_.add(Component.translatable("info.goety.lichdom.disable").withStyle(ChatFormatting.DARK_RED));
        }
    }
}
