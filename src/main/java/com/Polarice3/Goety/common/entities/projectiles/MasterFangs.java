package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class MasterFangs extends Fangs {
    private boolean sentSnapEvent;
    public AnimationState attackAnimationState = new AnimationState();

    public MasterFangs(EntityType<? extends MasterFangs> p_36923_, Level p_36924_) {
        super(p_36923_, p_36924_);
    }

    public MasterFangs(Level world, double pPosX, double pPosY, double pPosZ, float pYRot, int pWarmUp, LivingEntity owner) {
        this(ModEntityType.MASTER_FANG.get(), world);
        this.warmupDelayTicks = pWarmUp;
        this.setOwner(owner);
        this.setYRot(pYRot * (180F / (float)Math.PI));
        this.setPos(pPosX, pPosY, pPosZ);
    }

    public MasterFangs(Level world, double pPosX, double pPosY, double pPosZ, float pYRot, int pWarmUp, int damage, int burning, int soulEater, LivingEntity owner) {
        this(ModEntityType.MASTER_FANG.get(), world);
        this.warmupDelayTicks = pWarmUp;
        this.setOwner(owner);
        this.setYRot(pYRot * (180F / (float)Math.PI));
        this.damage = damage;
        this.burning = burning;
        this.soulEater = soulEater;
        this.setTotemSpawned(true);
        this.setPos(pPosX, pPosY, pPosZ);
    }

    public boolean isSentSpikeEvent() {
        return this.sentSpikeEvent;
    }

    @Override
    public EntityDimensions getDimensions(Pose p_19975_) {
        if (!this.sentSpikeEvent) {
            return super.getDimensions(p_19975_).scale(0.0F);
        }
        return super.getDimensions(p_19975_);
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isSentSpikeEvent();
    }

    @Override
    public void fangTick() {
        if (!this.level.isClientSide) {
            --this.warmupDelayTicks;
            if (this.warmupDelayTicks < 0) {
                if (!this.sentSpikeEvent) {
                    this.level.broadcastEntityEvent(this, (byte)4);
                    this.sentSpikeEvent = true;
                    this.refreshDimensions();
                }

                if (this.warmupDelayTicks <= -19) {
                    if (!this.sentSnapEvent) {
                        this.level.broadcastEntityEvent(this, (byte) 5);
                        this.sentSnapEvent = true;
                    }
                }

                if (this.warmupDelayTicks == -21) {
                    AABB aabb = new AABB(this.blockPosition()).inflate(1.4F);
                    for(LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, aabb)) {
                        this.dealDamageTo(livingentity);
                    }
                }

                if (this.warmupDelayTicks < -33) {
                    this.discard();
                }
            }
        }
    }

    public void dealDamageTo(LivingEntity target) {
        LivingEntity owner = this.getOwner();
        float baseDamage = SpellConfig.FangDamage.get().floatValue() * WandUtil.damageMultiply();
        baseDamage += this.damage;
        boolean hurt;
        if (target.isAlive() && !target.isInvulnerable()) {
            if (owner == null) {
                hurt = target.hurt(this.damageSources().magic(), baseDamage);
            } else {
                if (target == owner){
                    return;
                }
                if (owner instanceof Mob mobOwner) {
                    if (mobOwner instanceof Enemy && target instanceof Enemy) {
                        if (mobOwner.getTarget() != target) {
                            return;
                        }
                    }
                    if (mobOwner instanceof IOwned owned){
                        if (owned.getTrueOwner() != null){
                            if (MobUtil.areAllies(owned.getTrueOwner(), target)){
                                return;
                            }
                        }
                    }
                }
                if (MobUtil.areAllies(target, owner)){
                    return;
                }
                if (owner instanceof Player player){
                    hurt = target.hurt(this.damageSources().indirectMagic(this, owner), baseDamage);
                    if (hurt){
                        if (SEHelper.getSoulGiven(target) > 0) {
                            int soulEater = Mth.clamp(this.getSoulEater(), 0, 10);
                            SEHelper.increaseSouls(player, SpellConfig.FangGainSouls.get() * soulEater);
                        }
                        if (this.burning > 0){
                            target.setSecondsOnFire(5 * this.burning);
                        }
                    }
                } else {
                    hurt = target.hurt(this.damageSources().indirectMagic(this, owner), baseDamage);
                }
            }
            if (hurt) {
                target.invulnerableTime = 15;
                target.knockback(1.0D, (this.getX() - target.getX()) * MathHelper.modelDegrees(45), (this.getZ() - target.getZ()) * MathHelper.modelDegrees(45));
            }
        }
    }

    @Override
    public void toClientHandle(byte event) {
        if (event == 4) {
            this.sentSpikeEvent = true;
            this.attackAnimationState.start(this.tickCount);
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.MASTER_FANG_SPAWN.get(), this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
            }
        }
        if (event == 5) {
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.MASTER_FANG_SNAP.get(), this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
            }
        }
    }
}
