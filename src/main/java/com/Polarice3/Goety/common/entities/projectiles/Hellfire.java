package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.MagicSmokeParticle;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class Hellfire extends GroundProjectile {
    public static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData.defineId(Hellfire.class, EntityDataSerializers.FLOAT);
    public boolean isDying;

    public Hellfire(EntityType<? extends Entity> p_i50170_1_, Level p_i50170_2_) {
        super(p_i50170_1_, p_i50170_2_);
        this.setYRot(0.0F);
        this.lifeTicks = MathHelper.secondsToTicks(7);
    }

    public Hellfire(Level world, double pPosX, double pPosY, double pPosZ, @Nullable LivingEntity owner) {
        this(ModEntityType.HELLFIRE.get(), world);
        this.setOwner(owner);
        this.setPos(pPosX, pPosY, pPosZ);
    }

    public Hellfire(Level world, BlockPos blockPos, @Nullable LivingEntity owner) {
        this(ModEntityType.HELLFIRE.get(), world);
        this.setOwner(owner);
        this.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public Hellfire(Level world, Vec3 vector3d, @Nullable LivingEntity owner) {
        this(ModEntityType.HELLFIRE.get(), world);
        this.setOwner(owner);
        this.setPos(vector3d.x(), vector3d.y(), vector3d.z());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
    }

    public boolean isDying(){
        return this.isDying;
    }

    public void setDying(boolean dying){
        this.isDying = dying;
    }

    public float getExtraDamage() {
        return this.entityData.get(DATA_EXTRA_DAMAGE);
    }

    public void setExtraDamage(float pDamage) {
        this.entityData.set(DATA_EXTRA_DAMAGE, pDamage);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Dying")) {
            this.setDying(pCompound.getBoolean("Dying"));
        }
        if (pCompound.contains("ExtraDamage")) {
            this.setExtraDamage(pCompound.getFloat("ExtraDamage"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Dying", this.isDying());
        pCompound.putFloat("ExtraDamage", this.getExtraDamage());
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.lifeTicks <= 26) {
                --this.lifeTicks;
            }
            this.level.addParticle(new MagicSmokeParticle.Option(0xcf7a06, 0xb13f00, 10 + this.level.getRandom().nextInt(10), 0.2F), this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
            if (this.level.random.nextInt(24) == 0) {
                this.level.playLocalSound((double)this.blockPosition().getX() + 0.5D, (double)this.blockPosition().getY() + 0.5D, (double)this.blockPosition().getZ() + 0.5D, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + this.level.random.nextFloat(), this.level.random.nextFloat() * 0.7F + 0.3F, false);
            }
        } else {
            if (!this.isNoGravity()) {
                MobUtil.moveDownToGround(this);
            }

            if (this.lifeTicks > 13) {
                for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
                    this.dealDamageTo(livingentity);
                }
            }

            --this.lifeTicks;
            if (this.lifeTicks <= 26){
                this.level.broadcastEntityEvent(this, (byte)7);
            }
            if (this.lifeTicks < 0) {
                this.discard();
            }

            if (this.isInLava()){
                this.level.broadcastEntityEvent(this, (byte)6);
                this.discard();
            }
        }

    }

    public void dealDamageTo(LivingEntity target) {
        LivingEntity owner = this.getOwner();
        float damage = 2.0F;
        damage += this.getExtraDamage();
        if (target.isAlive() && !target.isInvulnerable()) {
            if (owner == null) {
                if (target.hurt(ModDamageSource.getDamageSource(this.level, ModDamageSource.HELLFIRE), damage) && !target.fireImmune()) {
                    target.invulnerableTime = 15;
                }
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
                if (MobUtil.areAllies(owner, target)){
                    return;
                }
                if (target.hurt(ModDamageSource.hellfire(this, this.getOwner()), damage) && !target.fireImmune()) {
                    target.invulnerableTime = 15;
                }
            }
        }
    }

    public float getAnimationProgress(float pPartialTicks) {
        if (this.lifeTicks <= 24) {
            int i = this.lifeTicks;
            return Math.max(1.0F - ((24.0F - i) / 24.0F), 0.0F);
        } else {
            return 1.0F;
        }
    }

    public void handleEntityEvent(byte pId) {
        super.handleEntityEvent(pId);
        if (pId == 6){
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.FIRE_EXTINGUISH, this.getSoundSource(), 1.0F, 1.0F, false);
            }
        }
        if (pId == 7){
            if (!this.isDying()) {
                this.lifeTicks = 26;
                this.setDying(true);
            }
        }

    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
