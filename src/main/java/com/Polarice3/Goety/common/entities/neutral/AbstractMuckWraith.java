package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.AcidPool;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class AbstractMuckWraith extends AbstractWraith {

    public AbstractMuckWraith(EntityType<? extends Summoned> p_i48553_1_, Level p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.TOWER_WRAITH_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.TOWER_WRAITH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.TOWER_WRAITH_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return ModSounds.TOWER_WRAITH_FLY.get();
    }

    @Override
    protected SoundEvent getTeleportInSound() {
        return ModSounds.TOWER_WRAITH_TELEPORT_IN.get();
    }

    protected SoundEvent getTeleportOutSound(){
        return ModSounds.TOWER_WRAITH_TELEPORT_OUT.get();
    }

    @Override
    protected SoundEvent getAttackSound() {
        return ModSounds.TOWER_WRAITH_ATTACK.get();
    }

    @Override
    public void firingParticles() {
        this.level.broadcastEntityEvent(this, (byte) 103);
    }

    public void playAttackSound(){
        if (!this.isSilent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), this.getAttackSound(), this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public void magicFire(LivingEntity livingEntity){
        AcidPool acidPool = new AcidPool(ModEntityType.ACID_POOL.get(), this.level);
        acidPool.setColor(0xec67eb);
        acidPool.setWarmupColor(0xfdd4fb);
        acidPool.setPos(livingEntity.position());
        acidPool.setRadius(2.0F);
        acidPool.setDamage((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
        acidPool.setWarmupDelayTicks(MathHelper.secondsToTicks(0.7F));
        acidPool.setDuration(MathHelper.secondsToTicks(1.8F));
        acidPool.setOwner(this);
        acidPool.setSoundEvent(ModSounds.TOWER_WRAITH_ACID_VOCAL.get().getLocation().toString());
        if (!livingEntity.isInWater()) {
            MobUtil.moveDownToGround(acidPool);
        }
        if (this.level.addFreshEntity(acidPool)){
            acidPool.playSound(ModSounds.TOWER_WRAITH_ACID.get(), 1.0F, this.getVoicePitch());
        }
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
            this.setIsFiring(true);
            this.acidAnimationState.start(this.tickCount);
        } else if (pId == 5) {
            this.setIsFiring(false);
            this.acidAnimationState.stop();
        } else if (pId == 103) {
            for(int j = 0; j < 4; ++j) {
                double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                double d2 = this.getY() + (this.random.nextDouble() + 0.5D);
                double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                ColorUtil colorUtil = new ColorUtil(0xec67eb);
                this.level.addParticle(ModParticleTypes.BIG_CULT_SPELL.get(), d1, d2, d3, colorUtil.red(), colorUtil.green(), colorUtil.blue());
            }
        } else {
            super.handleEntityEvent(pId);
        }
    }
}
