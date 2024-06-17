package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayLoopSoundPacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.SpellExplosion;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VoidRift extends CastSpellTrap {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(VoidRift.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> ID_SIZE = SynchedEntityData.defineId(VoidRift.class, EntityDataSerializers.FLOAT);
    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, Goety.location("textures/entity/projectiles/void_rift/open_1.png"));
        map.put(1, Goety.location("textures/entity/projectiles/void_rift/open_2.png"));
        map.put(2, Goety.location("textures/entity/projectiles/void_rift/open_3.png"));
        map.put(3, Goety.location("textures/entity/projectiles/void_rift/rift_1.png"));
        map.put(4, Goety.location("textures/entity/projectiles/void_rift/rift_2.png"));
        map.put(5, Goety.location("textures/entity/projectiles/void_rift/rift_3.png"));
        map.put(6, Goety.location("textures/entity/projectiles/void_rift/rift_4.png"));
        map.put(7, Goety.location("textures/entity/projectiles/void_rift/rift_5.png"));
    });
    public int warmUp;
    public boolean playEvent;
    public boolean isClosing;

    public VoidRift(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.setNoGravity(false);
    }

    public VoidRift(Level worldIn, double x, double y, double z) {
        this(ModEntityType.VOID_RIFT.get(), worldIn);
        this.setPos(x, y, z);
        this.playSound(ModSounds.RUMBLE.get(), 2.0F, this.random.nextFloat() + 0.75F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 0);
        this.entityData.define(ID_SIZE, 0.0F);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33134_) {
        if (ID_SIZE.equals(p_33134_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_33134_);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Size")) {
            this.setSize(compound.getFloat("Size"));
        }
        if (compound.contains("TickTime")) {
            this.tickCount = compound.getInt("TickTime");
        }
        if (compound.contains("WarmUp")) {
            this.warmUp = compound.getInt("WarmUp");
        }
        if (compound.contains("PlayEvent")) {
            this.playEvent = compound.getBoolean("PlayEvent");
        }
        if (compound.contains("IsClosing")) {
            this.isClosing = compound.getBoolean("IsClosing");
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Size", this.getSize());
        compound.putInt("TickTime", this.tickCount);
        compound.putInt("WarmUp", this.warmUp);
        compound.putBoolean("PlayEvent", this.playEvent);
        compound.putBoolean("IsClosing", this.isClosing);
    }

    public EntityDimensions getDimensions(Pose p_33113_) {
        float i = this.getSize();
        EntityDimensions entitydimensions = super.getDimensions(p_33113_);
        return entitydimensions.scale(i);
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getAnimation(), TEXTURE_BY_TYPE.get(0));
    }

    public void setSize(float p_33109_) {
        this.entityData.set(ID_SIZE, Mth.clamp(p_33109_, 0, 64));
    }

    public float getSize() {
        return this.entityData.get(ID_SIZE);
    }

    public int getAnimation() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setAnimation(int pType) {
        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public void setWarmUp(int warmUp){
        this.warmUp = warmUp;
        this.setDuration(this.getDuration() + warmUp);
    }

    public void tick() {
        super.tick();
        if (!this.isActivated()){
            int count = SpellConfig.RuptureDuration.get() / 3;
            if (this.tickCount % count == 0) {
                if (this.level.isClientSide) {
                    if (this.getAnimation() < 2) {
                        this.setAnimation(this.getAnimation() + 1);
                    }
                }
                this.playSound(ModSounds.RUMBLE.get(), 2.0F, this.random.nextFloat() + 0.75F);
            }
        } else {
            if (this.level.isClientSide) {
                if (this.tickCount % 20 == 0) {
                    if (this.isClosing) {
                        if (this.getAnimation() > 0) {
                            this.setAnimation(this.getAnimation() - 1);
                        }
                    } else {
                        if (this.getAnimation() < 7) {
                            this.setAnimation(this.getAnimation() + 1);
                        } else {
                            this.setAnimation(3);
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide) {
            if (this.warmUp <= 0) {
                if (!this.isActivated()) {
                    this.setActivated(true);
                    this.level.broadcastEntityEvent(this, (byte) 100);
                }
                if (!this.playEvent) {
                    this.playSound(ModSounds.VOID_RIFT_OPEN.get(), 5.0F, 1.0F);
                    ModNetwork.sendToALL(new SPlayLoopSoundPacket(this, ModSounds.VOID_RIFT.get(), 5.0F));
                    this.playEvent = true;
                }
                if (this.tickCount % 200 == 0){
                    this.playSound(SoundEvents.PORTAL_AMBIENT, 5.0F, 0.75F);
                }
                List<Entity> targets = new ArrayList<>();
                float range = 16.0F * (this.getSize() + 1.0F);
                if (this.level instanceof ServerLevel serverWorld) {
                    ServerParticleUtil.gatheringParticles(ParticleTypes.PORTAL, this, serverWorld, (int)range);
                }
                for (Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(range))) {
                    if (!(entity instanceof Endermite)) {
                        if (this.getOwner() != null) {
                            if (entity != this.getOwner() && !MobUtil.areAllies(this.getOwner(), entity)) {
                                targets.add(entity);
                            }
                        } else {
                            targets.add(entity);
                        }
                    }
                }
                if (!targets.isEmpty()) {
                    for (Entity entity : targets) {
                        double distance = 1.0D - (entity.distanceTo(this) / range);
                        double scale = distance * 0.25D;
                        Vec3 vec3 = entity.position().subtract(this.position());
                        vec3 = vec3.normalize().scale(scale);
                        MobUtil.pull(entity, vec3.x, vec3.y, vec3.z);
                        if (entity.distanceTo(this) <= this.getBoundingBox().getSize()){
                            if (entity instanceof LivingEntity livingEntity) {
                                float damage = SpellConfig.RuptureDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
                                if (this.getOwner() != null) {
                                    if (this.getOwner() instanceof Mob mob && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                                        damage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2.0F;
                                    }
                                }
                                damage += this.getExtraDamage();
                                livingEntity.hurt(this.damageSources().indirectMagic(this, this.getOwner()), damage);
                            }
                        }
                    }
                }
            } else {
                if (this.level instanceof ServerLevel serverWorld) {
                    double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth();
                    double d2 = this.getY() + 0.5F;
                    double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth();
                    serverWorld.sendParticles(ParticleTypes.PORTAL, d1, d2, d3, 0, 0.0D, 0.0D, 0.0D, 0.5F);
                }
                if (this.getOwner() != null && this.getOwner().isDeadOrDying()) {
                    this.discard();
                }
                --this.warmUp;
            }
            if (this.tickCount >= this.getDuration()) {
                if (!this.isClosing){
                    this.isClosing = true;
                    this.level.broadcastEntityEvent(this, (byte) 101);
                }
                if (this.tickCount >= this.getDuration() + 20) {
                    if (this.level instanceof ServerLevel serverLevel) {
                        ColorUtil colorUtil = new ColorUtil(0x7317d2);
                        float range = 8.0F * (this.getSize() + 1.0F);
                        serverLevel.sendParticles(new ShockwaveParticleOption(0, colorUtil.red, colorUtil.green, colorUtil.blue, range, 0, true), this.getX(), this.getY() + 0.5D, this.getZ(), 0, 0, 0, 0, 0);
                        ServerParticleUtil.createParticleBall(ParticleTypes.DRAGON_BREATH, this.getX(), this.getY() + 0.5D, this.getZ(), serverLevel,  8 + (int) this.getSize());
                        this.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), 5.0F, 0.5F);
                        this.playSound(SoundEvents.GENERIC_EXPLODE, 5.0F, 0.5F);
                        new SpellExplosion(this.level, this.getOwner() != null ? this.getOwner() : this, this.damageSources().indirectMagic(this, this.getOwner()), this.blockPosition(), range / 4, 0.0F);
                    }
                    this.discard();
                }
            }
        }
    }

    @Override
    public void handleEntityEvent(byte p_19882_) {
        if (p_19882_ == 100){
            this.setActivated(true);
            this.setAnimation(3);
        } else if (p_19882_ == 101){
            this.isClosing = true;
            this.setAnimation(2);
        } else {
            super.handleEntityEvent(p_19882_);
        }
    }
}
