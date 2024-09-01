package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkHooks;

public class Spike extends GroundProjectile {
    public int burning = 0;
    public int soulEater = 0;
    public float extraDamage = 0.0F;

    public Spike(EntityType<? extends Spike> p_i50170_1_, Level p_i50170_2_) {
        super(p_i50170_1_, p_i50170_2_);
        this.lifeTicks = 600;
    }

    public Spike(Level world, double pPosX, double pPosY, double pPosZ, float pYRot, int pWarmUp, LivingEntity owner) {
        this(ModEntityType.SPIKE.get(), world);
        this.warmupDelayTicks = pWarmUp;
        this.setOwner(owner);
        this.setYRot(pYRot * (180F / (float)Math.PI));
        this.setPos(pPosX, pPosY, pPosZ);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ExtraDamage")){
            this.extraDamage = pCompound.getInt("ExtraDamage");
        }
        if (pCompound.contains("Burning")){
            this.burning = pCompound.getInt("Burning");
        }
        if (pCompound.contains("SoulEater")){
            this.soulEater = pCompound.getInt("SoulEater");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("ExtraDamage", this.extraDamage);
        pCompound.putInt("Burning", this.burning);
        pCompound.putInt("SoulEater", this.soulEater);
    }

    public void setBurning(int burning){
        this.burning = burning;
    }

    public int getBurning() {
        return this.burning;
    }

    public void setExtraDamage(float extraDamage){
        this.extraDamage = extraDamage;
    }

    public float getExtraDamage() {
        return this.extraDamage;
    }

    public void setSoulEater(int soulEater){
        this.soulEater = soulEater;
    }

    public int getSoulEater(){
        return this.soulEater;
    }

    public void push(double p_70024_1_, double p_70024_3_, double p_70024_5_) {
    }

    public void move(MoverType p_213315_1_, Vec3 p_213315_2_) {
    }

    public boolean canCollideWith(Entity p_241849_1_) {
        return (p_241849_1_.canBeCollidedWith() || p_241849_1_.isPushable()) && !this.isPassengerOfSameVehicle(p_241849_1_);
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean isPushable() {
        return false;
    }

    public boolean isAttackable() {
        return false;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.sentTrapEvent) {
                --this.lifeTicks;
                if (this.animationTicks > 9){
                    --this.animationTicks;
                }
            }
        } else if (--this.warmupDelayTicks < 0) {
            for(Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().move(0.0F, 0.2F, 0.0F).inflate(0.1F, 0.0F, 0.1F))) {
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

            if (!this.playSound){
                this.level.broadcastEntityEvent(this, (byte)5);
                this.playSound = true;
            }

            if (!this.sentTrapEvent) {
                this.level.broadcastEntityEvent(this, (byte)4);
                this.sentTrapEvent = true;
            }

            if (--this.lifeTicks < 0) {
                this.discard();
            }
        }

    }

    private void dealDamageTo(LivingEntity target) {
        LivingEntity livingEntity = this.getOwner();
        if (target.isAlive() && !target.isInvulnerable() && target != livingEntity) {
            boolean flag;
            if (livingEntity == null) {
                flag = target.hurt(ModDamageSource.spike(this, this), SpellConfig.SpikeDamage.get().floatValue() + this.getExtraDamage());
            } else {
                if (MobUtil.areAllies(livingEntity, target)){
                    return;
                }
                flag = target.hurt(ModDamageSource.spike(this, livingEntity), SpellConfig.SpikeDamage.get().floatValue() + this.getExtraDamage());
            }
            if (flag){
                if (livingEntity instanceof Player player) {
                    int soulEater = Mth.clamp(this.getSoulEater(), 0, 10);
                    SEHelper.increaseSouls(player, SpellConfig.SpikeGainSouls.get() * soulEater);
                    if (this.getBurning() > 0){
                        target.setSecondsOnFire(5 * this.getBurning());
                    }
                }
            }
        }
    }

    public void handleEntityEvent(byte pId) {
        super.handleEntityEvent(pId);
        if (pId == 5) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.GRINDSTONE_USE, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
