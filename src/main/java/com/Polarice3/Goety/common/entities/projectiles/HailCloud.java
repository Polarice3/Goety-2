package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

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
            this.setTarget(pTarget);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.isStaff()) {
                if (this.getTarget() == null) {
                    for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(16.0F))){
                        if (MobUtil.ownedPredicate(this).test(livingEntity)){
                            this.setTarget(livingEntity);
                        }
                    }
                }
                float speed = 0.175F;
                if (this.getTarget() != null && this.getTarget().isAlive()) {
                    this.setDeltaMovement(Vec3.ZERO);
                    double d0 = this.getTarget().getX() - this.getX();
                    double d1 = (this.getTarget().getY() + 4.0D) - this.getY();
                    double d2 = this.getTarget().getZ() - this.getZ();
                    double d = Math.sqrt((d0 * d0 + d2 * d2));
                    double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    if (d > 0.5) {
                        this.setDeltaMovement(this.getDeltaMovement().add(d0 / d3, d1 / d3, d2 / d3).scale(speed));
                    }
                }
                this.move(MoverType.SELF, this.getDeltaMovement());
            }
        }
    }

    public void hurtEntities(LivingEntity livingEntity){
        if (livingEntity != null) {
            float baseDamage = SpellConfig.HailDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
            baseDamage += this.getExtraDamage();
            if (livingEntity.hurt(ModDamageSource.frostBreath(this, this.getOwner()), baseDamage)) {
                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(5)));
            }
        }
    }
}
