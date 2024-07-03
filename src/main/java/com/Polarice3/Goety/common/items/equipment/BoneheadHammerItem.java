package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.client.particles.TeleportInShockwaveParticleOption;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

public class BoneheadHammerItem extends HammerItem{

    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pAttacker, (p_220045_0_) ->
                p_220045_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        if (pAttacker instanceof Player player){
            float f2 = player.getAttackStrengthScale(0.5F);
            if (f2 > 0.9F){
                this.attackMobs(pTarget, player);
                player.level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), ModSounds.HAMMER_SWING.get(), player.getSoundSource(), 1.0F, 1.0F);
                player.level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), ModSounds.BONEHEAD_HAMMER_HIT.get(), player.getSoundSource(), 1.0F, 1.0F);
                player.level.playSound((Player) null, pTarget.getX(), pTarget.getY(), pTarget.getZ(), ModSounds.DIRT_DEBRIS.get(), player.getSoundSource(), 1.0F, 1.0F);
                if (player.level instanceof ServerLevel serverLevel){
                    BlockPos blockPos = new BlockPos(pTarget.getX(), pTarget.getY() - 1.0F, pTarget.getZ());
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
                    for (int i = 0; i < 8; ++i) {
                        ServerParticleUtil.circularParticles(serverLevel, option, pTarget.getX(), pTarget.getY() + 0.25D, pTarget.getZ(), 1.5F);
                    }
                }
            }
        }
        return true;
    }

    public void attackMobs(LivingEntity pTarget, Player pPlayer){
        float f = (float)pPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = EnchantmentHelper.getDamageBonus(pPlayer.getMainHandItem(), pTarget.getMobType());
        float f2 = pPlayer.getAttackStrengthScale(0.5F);
        f = f * (0.2F + f2 * f2 * 0.8F);
        f1 = f1 * f2;
        f = f + f1;

        if (f > 0.5F || f1 > 0.5F) {
            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(pPlayer) * f;
            int j = EnchantmentHelper.getFireAspect(pPlayer);
            double area = 1.0D;
            if (f2 > 0.9F) {
                area = 1.75D;
            }
            for (LivingEntity livingentity : pPlayer.level.getEntitiesOfClass(LivingEntity.class, pTarget.getBoundingBox().inflate(area, 0.25D, area))) {
                if (livingentity != pPlayer && livingentity != pTarget && !pPlayer.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand) livingentity).isMarker()) && pPlayer.distanceToSqr(livingentity) < 16.0D && livingentity != pPlayer.getVehicle()) {
                    livingentity.knockback(0.4F, (double) Mth.sin(pPlayer.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(pPlayer.getYRot() * ((float) Math.PI / 180F))));
                    if (livingentity.hurt(DamageSource.playerAttack(pPlayer), f3)) {
                        if (j > 0) {
                            livingentity.setSecondsOnFire(j * 4);
                        }
                        EnchantmentHelper.doPostHurtEffects(livingentity, pPlayer);
                        EnchantmentHelper.doPostDamageEffects(pPlayer, livingentity);
                    }
                }
            }
            for (LivingEntity livingentity : pPlayer.level.getEntitiesOfClass(LivingEntity.class, pTarget.getBoundingBox().inflate(area * 2.0D, 1.0D, area * 2.0D))) {
                if (livingentity != pPlayer && livingentity != pTarget && !pPlayer.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand) livingentity).isMarker()) && livingentity != pPlayer.getVehicle()) {
                    Vec3 vec3 = livingentity.position().subtract(pTarget.position());
                    vec3 = vec3.normalize();
                    livingentity.level.playSound((Player) null, livingentity.getX(), livingentity.getY(), livingentity.getZ(), ModSounds.GRAVITY.get(), pPlayer.getSoundSource(), 1.0F, 1.0F);
                    MobUtil.pull(livingentity, vec3.x, vec3.y, vec3.z);
                    if (livingentity.level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(new TeleportInShockwaveParticleOption(0), livingentity.getX(), livingentity.getY() + 0.25F, livingentity.getZ(), 0, 0, 0, 0, 0.5F);
                    }
                }
            }
        }

        pPlayer.level.playSound((Player) null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), ModSounds.BONEHEAD_HAMMER_IMPACT.get(), pPlayer.getSoundSource(), 1.0F, 1.0F);
    }
}
