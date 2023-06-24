package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class BerserkFungus extends ThrowableFungus {

    public BerserkFungus(EntityType<? extends ThrowableFungus> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public BerserkFungus(double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(ModEntityType.BERSERK_FUNGUS.get(), p_37457_, p_37458_, p_37459_, p_37460_);
    }

    public BerserkFungus(LivingEntity p_37463_, Level p_37464_) {
        super(ModEntityType.BERSERK_FUNGUS.get(), p_37463_, p_37464_);
    }

    protected void onHit(HitResult p_37406_) {
        super.onHit(p_37406_);
        if (!this.level.isClientSide) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.BLAST_FUNGUS_EXPLODE.get(), SoundSource.BLOCKS, 1.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
            this.level.broadcastEntityEvent(this, (byte) 15);
            this.applySplash();
            this.discard();
        }
    }

    private void applySplash() {
        AABB aabb = this.getBoundingBox().inflate(1.5D, 1.5D, 1.5D);
        List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, aabb);
        if (!list.isEmpty()) {
            for(LivingEntity livingentity : list) {
                if (livingentity.isAffectedByPotions()) {
                    double d0 = this.distanceToSqr(livingentity);
                    if (d0 < 16.0D) {
                        int duration = MathHelper.secondsToTicks(10);
                        livingentity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration));
                        livingentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration));
                        livingentity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration));
                    }
                }
            }
        }

    }

    public void handleEntityEvent(byte p_34138_) {
        if (p_34138_ == 15) {
            float f = 1.0F;
            for (int k1 = 0; (float) k1 < 100; ++k1) {
                float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                float f8 = Mth.cos(f6) * f7;
                float f9 = Mth.sin(f6) * f7;
                this.level.addParticle(ModParticleTypes.CULT_SPELL.get(), this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, MathHelper.rgbToSpeed(162), MathHelper.rgbToSpeed(28), 0.0D);
            }
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.BLAST_FUNGUS_EXPLODE.get(), SoundSource.BLOCKS, 1.0F, (2.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
        } else {
            super.handleEntityEvent(p_34138_);
        }

    }
}
