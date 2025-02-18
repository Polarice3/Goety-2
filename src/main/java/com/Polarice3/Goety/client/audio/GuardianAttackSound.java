package com.Polarice3.Goety.client.audio;

import com.Polarice3.Goety.common.entities.ally.GuardianServant;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class GuardianAttackSound extends AbstractTickableSoundInstance {
    private static final float VOLUME_MIN = 0.0F;
    private static final float VOLUME_SCALE = 1.0F;
    private static final float PITCH_MIN = 0.7F;
    private static final float PITCH_SCALE = 0.5F;
    private final GuardianServant guardian;

    public GuardianAttackSound(GuardianServant p_119690_) {
        super(SoundEvents.GUARDIAN_ATTACK, SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.guardian = p_119690_;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.looping = true;
        this.delay = 0;
    }

    public boolean canPlaySound() {
        return !this.guardian.isSilent();
    }

    public void tick() {
        if (!this.guardian.isRemoved() && this.guardian.getTarget() == null) {
            this.x = (double)((float)this.guardian.getX());
            this.y = (double)((float)this.guardian.getY());
            this.z = (double)((float)this.guardian.getZ());
            float f = this.guardian.getAttackAnimationScale(0.0F);
            this.volume = 0.0F + 1.0F * f * f;
            this.pitch = 0.7F + 0.5F * f;
        } else {
            this.stop();
        }
    }
}
