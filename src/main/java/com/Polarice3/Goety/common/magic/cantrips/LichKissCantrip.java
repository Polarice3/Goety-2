package com.Polarice3.Goety.common.magic.cantrips;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class LichKissCantrip {

    public void sendRay(Player pPlayer) {
        if (pPlayer != null && !pPlayer.isSpectator()) {
            Entity target = null;
            if (this.entityResult(pPlayer) != null) {
                target = this.entityResult(pPlayer).getEntity();
            }
            if (target instanceof LivingEntity livingEntity) {
                if (!livingEntity.isAlive()) {
                    return;
                } else {
                    this.drawParticleBeam(pPlayer, livingEntity);
                }
                if (livingEntity.hurt(ModDamageSource.soulLeech(pPlayer, pPlayer), 1.0F)) {
                    int souls = SEHelper.getSoulGiven(livingEntity) < 5 ? SEHelper.getSoulGiven(livingEntity) : (int)SEHelper.getSoulGiven(livingEntity)/5;
                    SEHelper.increaseSouls(pPlayer, souls);
                    pPlayer.playSound(ModSounds.SOUL_EAT.get(), 1.0F, 1.0F);
                    ModNetwork.sendTo(pPlayer, new SPlayPlayerSoundPacket(ModSounds.SOUL_EAT.get(), 1.0F, 1.0F));
                }
            }
        }
    }

    protected EntityHitResult entityResult(LivingEntity pSource){
        Vec3 srcVec = pSource.getEyePosition(1.0F);
        Vec3 lookVec = pSource.getViewVector(1.0F);
        Vec3 destVec = srcVec.add(lookVec.x * 16, lookVec.y * 16, lookVec.z * 16);
        AABB axisalignedbb = pSource.getBoundingBox().expandTowards(lookVec.scale(16)).inflate(1.0D);
        return ProjectileUtil.getEntityHitResult(pSource.level, pSource, srcVec, destVec, axisalignedbb, entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.isPickable() && pSource.hasLineOfSight(entity));
    }

    private void drawParticleBeam(LivingEntity pSource, LivingEntity pTarget) {
        double d0 = pTarget.getX() - pSource.getX();
        double d1 = (pTarget.getY() + (double) pTarget.getBbHeight() * 0.5F) - (pSource.getY() + (double) pSource.getBbHeight() * 0.5D);
        double d2 = pTarget.getZ() - pSource.getZ();
        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        d0 = d0 / d3;
        d1 = d1 / d3;
        d2 = d2 / d3;
        double d4 = pSource.level.random.nextDouble();
        if (!pSource.level.isClientSide) {
            ServerLevel serverWorld = (ServerLevel) pSource.level;
            while (d4 < d3) {
                d4 += 1.0D;
                serverWorld.sendParticles(ModParticleTypes.LEECH.get(), pSource.getX() + d0 * d4, pSource.getY() + d1 * d4 + (double) pSource.getEyeHeight() * 0.5D, pSource.getZ() + d2 * d4, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
