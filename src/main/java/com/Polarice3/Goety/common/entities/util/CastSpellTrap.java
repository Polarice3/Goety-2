package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.common.entities.projectiles.SpellEntity;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nullable;

public abstract class CastSpellTrap extends SpellEntity {
    private int duration = 600;
    private boolean activated = false;

    public CastSpellTrap(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.noPhysics = true;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Duration")) {
            this.duration = compound.getInt("Duration");
        }
        if (compound.contains("Activated")) {
            this.activated = compound.getBoolean("Activated");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Duration", this.duration);
        compound.putBoolean("Activated", this.activated);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int durationIn) {
        this.duration = durationIn;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public float radius(){
        return 3.0F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level instanceof ServerLevel serverWorld) {
            if (this.getParticle() != null) {
                ServerParticleUtil.circularParticles(serverWorld, this.getParticle(), this.getX(), this.getY(), this.getZ(), this.radius());
            }
        }
        if (this.getOwner() instanceof Player player){
            if (!MobUtil.isSpellCasting(player) && !this.isActivated()){
                this.discard();
            }
        }
    }

    @Nullable
    public ParticleOptions getParticle() {
        return null;
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }
}
