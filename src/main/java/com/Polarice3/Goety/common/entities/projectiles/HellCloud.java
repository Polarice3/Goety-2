package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class HellCloud extends AbstractSpellCloud{
    public HellCloud(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.setRainParticle(ModParticleTypes.DROPLET.get());
    }

    public HellCloud(Level pLevel, LivingEntity pOwner, LivingEntity pTarget){
        this(ModEntityType.HELL_CLOUD.get(), pLevel);
        if (pOwner != null){
            this.setOwner(pOwner);
        }
        this.setActivateTime(100);
        if (pTarget != null){
            BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(pTarget.getX(), pTarget.getY(), pTarget.getZ());

            while(blockpos$mutable.getY() < pTarget.getY() + 4.0D && !this.level.getBlockState(blockpos$mutable).blocksMotion()) {
                blockpos$mutable.move(Direction.UP);
            }
            this.setPos(pTarget.getX(), blockpos$mutable.getY(), pTarget.getZ());
            this.setTarget(pTarget);
        }
        this.playSound(ModSounds.HEAVY_WOOSH.get(), 1.0F, 0.5F);
    }

    public int getColor(){
        return 0x1f0000;
    }

    public void hurtEntities(LivingEntity livingEntity){
        if (livingEntity != null) {
            float baseDamage = 1.0F;
            baseDamage += this.getExtraDamage();
            if (livingEntity.hurt(ModDamageSource.hellfire(this, this.getOwner()), baseDamage)) {
                if (this.getOwner() instanceof Apostle) {
                    livingEntity.addEffect(new MobEffectInstance(GoetyEffects.BURN_HEX.get(), 1200));
                }
            }
        }
    }

    public void rainParticles(ParticleOptions particleRain){
        if (this.level instanceof ServerLevel serverWorld){
            float f = getRadius();
            float f5 = (float) Math.PI * f * f;
            for (int k1 = 0; (float) k1 < f5; ++k1) {
                float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                float f8 = Mth.cos(f6) * f7;
                float f9 = Mth.sin(f6) * f7;
                serverWorld.sendParticles(particleRain, this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 0, 1.0F, 0.2857143F, 0.083333336F, 1.0F);
            }
        }
    }
}
