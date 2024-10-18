package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.Vec3Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkHooks;

/**
 * Based on @cerbon's Spikes codes: <a href="https://github.com/CERBON-MODS/Bosses-of-Mass-Destruction-FORGE/blob/1.20.1/src/main/java/com/cerbon/bosses_of_mass_destruction/entity/custom/void_blossom/Spikes.java">...</a>
 */
public class BlossomThorn extends GroundProjectile {
    public int duration = 0;
    public float extraDamage = 0.0F;
    public float height;
    public Vec3 randVec;

    public BlossomThorn(EntityType<? extends Entity> p_i50170_1_, Level p_i50170_2_) {
        super(p_i50170_1_, p_i50170_2_);
        this.height = 4.0F + ((this.random.nextFloat() - 0.5F) * 2 * 0.5F);
        this.randVec = Vec3Util.randVec(this.random);
        this.lifeTicks = 20;
    }

    public BlossomThorn(Level world, double pPosX, double pPosY, double pPosZ, int pWarmUp, LivingEntity owner) {
        this(ModEntityType.BLOSSOM_THORN.get(), world);
        this.warmupDelayTicks = pWarmUp;
        this.setOwner(owner);
        this.setPos(pPosX, pPosY, pPosZ);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ExtraDamage")){
            this.extraDamage = pCompound.getInt("ExtraDamage");
        }
        if (pCompound.contains("Duration")){
            this.duration = pCompound.getInt("Duration");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("ExtraDamage", this.extraDamage);
        pCompound.putInt("Duration", this.duration);
    }

    public void setExtraDamage(float extraDamage){
        this.extraDamage = extraDamage;
    }

    public float getExtraDamage() {
        return this.extraDamage;
    }

    public void setDuration(int duration){
        this.duration = duration;
    }

    public int getDuration(){
        return this.duration;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.sentTrapEvent) {
                --this.lifeTicks;
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (!this.playSound){
                this.level.broadcastEntityEvent(this, (byte)5);
                this.playSound = true;
            }

            if (!this.sentTrapEvent) {
                this.level.broadcastEntityEvent(this, (byte)4);
                this.sentTrapEvent = true;
                for(Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(0.0F, 4.0F, 0.0F))) {
                    LivingEntity livingEntity = null;
                    if (entity instanceof PartEntity<?> partEntity && partEntity.getParent() instanceof LivingEntity living){
                        livingEntity = living;
                    } else if (entity instanceof LivingEntity living){
                        livingEntity = living;
                    }
                    if (livingEntity != null) {
                        this.dealDamageTo(livingEntity);
                    }
                }
            }

            if (--this.lifeTicks < 0) {
                this.discard();
            }
        }

    }

    private void dealDamageTo(LivingEntity target) {
        LivingEntity livingentity = this.getOwner();
        float baseDamage = SpellConfig.BlossomDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        if (target.isAlive() && !target.isInvulnerable() && MobUtil.validEntity(target) && target != livingentity) {
            boolean flag;
            if (livingentity != null) {
                if (target.isAlliedTo(livingentity)) {
                    return;
                }
                if (livingentity.isAlliedTo(target)) {
                    return;
                }
                flag = target.hurt(DamageSource.thorns(livingentity), baseDamage);
            } else {
                flag = target.hurt(DamageSource.thorns(this), baseDamage);
            }
            if (flag){
                MobEffect effect = MobEffects.POISON;
                if (livingentity != null){
                    if (CuriosFinder.hasWildRobe(livingentity)){
                        effect = GoetyEffects.ACID_VENOM.get();
                    }
                }
                target.addEffect(new MobEffectInstance(effect, 140 + MathHelper.secondsToTicks(this.duration)), this);
            }
        }
    }

    public void handleEntityEvent(byte pId) {
        super.handleEntityEvent(pId);
        if (pId == 5) {
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.VINE_TRAP_BURST.get(), this.getSoundSource(), 1.0F, 0.75F, false);
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.QUICK_GROWING_VINE_BURST.get(), this.getSoundSource(), 1.0F, 0.75F, false);
            }
        }

    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
