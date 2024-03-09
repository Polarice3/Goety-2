package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class HailCloud extends AbstractSpellCloud{
    public HailCloud(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.setRainParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SNOW_BLOCK.defaultBlockState()));
    }

    public HailCloud(Level pLevel, LivingEntity pOwner, LivingEntity pTarget){
        this(ModEntityType.HAIL_CLOUD.get(), pLevel);
        if (pOwner != null){
            this.setOwner(pOwner);
        }
        if (pTarget != null){
            BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(pTarget.getX(), pTarget.getY(), pTarget.getZ());

            while(blockpos$mutable.getY() < pTarget.getY() + 4.0D && !this.level.getBlockState(blockpos$mutable).blocksMotion()) {
                blockpos$mutable.move(Direction.UP);
            }
            this.setPos(pTarget.getX(), blockpos$mutable.getY(), pTarget.getZ());
        }
    }

    public void hurtEntities(LivingEntity livingEntity){
        if (livingEntity != null) {
            float baseDamage = SpellConfig.HailDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
            if (livingEntity.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)){
                baseDamage *= 2.0F;
            }
            baseDamage += this.extraDamage;
            if (livingEntity.hurt(ModDamageSource.frostBreath(this, this.getOwner()), baseDamage)) {
                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(5)));
            }
        }
    }
}
