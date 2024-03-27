package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.entities.projectiles.PoisonQuill;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PoisonAnemone extends PoisonQuillVine{
    public PoisonAnemone(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.POISON_ANEMONE_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource p_21239_) {
        return ModSounds.POISON_ANEMONE_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.POISON_ANEMONE_DEATH.get();
    }

    @Override
    protected SoundEvent getBurstSound() {
        return ModSounds.POISON_ANEMONE_BURST.get();
    }

    @Override
    protected SoundEvent getBurrowSound() {
        return ModSounds.POISON_ANEMONE_BURST.get();
    }

    protected SoundEvent getCloseSound(){
        return ModSounds.POISON_ANEMONE_CLOSE.get();
    }

    protected SoundEvent getOpenSound(){
        return ModSounds.POISON_ANEMONE_OPEN.get();
    }

    public void shootQuill(@NotNull LivingEntity target) {
        PoisonQuill quill = new PoisonQuill(this.level, this);
        quill.setAqua(true);
        Vec3 vector3d = this.getViewVector( 1.0F);
        quill.setPos(this.getX() + vector3d.x,
                this.getEyeY(),
                this.getZ() + vector3d.z);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - quill.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
        quill.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, 1.0F);
        if (this.level.addFreshEntity(quill)){
            this.playSound(ModSounds.POISON_ANEMONE_SHOOT.get());
        }
    }

    @Override
    public boolean isAquatic() {
        return true;
    }
}
