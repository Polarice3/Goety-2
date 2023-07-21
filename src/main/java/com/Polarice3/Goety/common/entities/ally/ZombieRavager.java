package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.level.Level;

public class ZombieRavager extends ModRavager {
    public ZombieRavager(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 75.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public double regularSpeed() {
        return 0.23D;
    }

    @Override
    public double aggressiveSpeed() {
        return 0.28D;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected boolean isSunSensitive() {
        return this.getArmor().isEmpty();
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.ZOMBIE_RAVAGER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33359_) {
        return ModSounds.ZOMBIE_RAVAGER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.ZOMBIE_RAVAGER_DEATH.get();
    }

    protected SoundEvent getStepSound(){
        if (this.hasSaddle()) {
            return ModSounds.ZOMBIE_RAVAGER_STEP.get();
        } else {
            return SoundEvents.POLAR_BEAR_STEP;
        }
    }

    protected SoundEvent getAttackSound(){
        return ModSounds.ZOMBIE_RAVAGER_BITE.get();
    }

    protected SoundEvent getStunnedSound(){
        return ModSounds.ZOMBIE_RAVAGER_STUN.get();
    }

    protected SoundEvent getRoarSound(){
        return ModSounds.ZOMBIE_RAVAGER_ROAR.get();
    }

    public void convertNewEquipment(Entity entity){
        if (!this.level.isClientSide) {
            if (entity instanceof Ravager) {
                this.equipSaddle(false);
                this.setHealth(((Ravager) entity).getHealth());
                this.updateArmor();
            } else if (entity instanceof ModRavager modRavager) {
                if (modRavager.hasSaddle()) {
                    this.equipSaddle(false);
                }
                this.setHealth(modRavager.getHealth());
                this.updateArmor();
            }
        }
    }
}
