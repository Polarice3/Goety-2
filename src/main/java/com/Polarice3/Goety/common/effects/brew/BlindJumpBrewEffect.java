package com.Polarice3.Goety.common.effects.brew;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;

public class BlindJumpBrewEffect extends BrewEffect {
    public BlindJumpBrewEffect(int soulCost) {
        super("blind_jump", soulCost, MobEffectCategory.BENEFICIAL, 0xff0659);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public boolean canLinger() {
        return true;
    }

    @Override
    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, @Nullable Entity pIndirectSource, int pAmplifier){
        if (!pTarget.level.isClientSide) {
            for (int i = 0; i < 128; ++i) {
                int d = pAmplifier + 1;
                double d0 = pTarget.getX() + (pTarget.getRandom().nextDouble() - 0.5D) * (16.0D * d);
                double d1 = Mth.clamp(pTarget.getY() + (double)(pTarget.getRandom().nextInt((16 * d)) - (8.0D * d)), (double)pTarget.level.getMinBuildHeight(), (double)(pTarget.level.getMinBuildHeight() + ((ServerLevel)pTarget.level).getLogicalHeight() - 1));
                double d2 = pTarget.getZ() + (pTarget.getRandom().nextDouble() - 0.5D) * (16.0D * d);
                net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(pTarget, d0, d1, d2);
                if (event.isCanceled()) {
                    break;
                }
                if (pTarget.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
                    pTarget.level.gameEvent(GameEvent.TELEPORT, pTarget.position(), GameEvent.Context.of(pTarget));
                    if (!pTarget.isSilent()) {
                        pTarget.level.playSound((Player) null, pTarget.xo, pTarget.yo, pTarget.zo, SoundEvents.ENDERMAN_TELEPORT, pTarget.getSoundSource(), 1.0F, 1.0F);
                        pTarget.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                    }
                    break;
                }
            }
        }
    }
}
