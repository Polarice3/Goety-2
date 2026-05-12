package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.google.common.collect.Lists;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EffectBlastTrap extends Entity implements ISpellEntity {
    private static final EntityDataAccessor<Boolean> IMMEDIATE = SynchedEntityData.defineId(EffectBlastTrap.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(EffectBlastTrap.class, EntityDataSerializers.INT);
    private Potion potion = Potions.EMPTY;
    private final List<MobEffectInstance> effects = Lists.newArrayList();
    private boolean fixedColor;
    public boolean playSound;
    public LivingEntity owner;
    private UUID ownerUniqueId;
    private float extraDamage;
    private float areaOfEffect = 0.0F;

    public EffectBlastTrap(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.noPhysics = true;
    }

    public EffectBlastTrap(Level worldIn, double x, double y, double z) {
        this(ModEntityType.EFFECT_BLAST_TRAP.get(), worldIn);
        this.setPos(x, y, z);
    }

    public EffectBlastTrap(Level worldIn, Vec3 vec3) {
        this(worldIn, vec3.x, vec3.y, vec3.z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(IMMEDIATE, false);
        this.getEntityData().define(DATA_COLOR, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.ownerUniqueId = compound.getUUID("Owner");
        }
        this.playSound = compound.getBoolean("PlaySound");
        this.extraDamage = compound.getFloat("ExtraDamage");
        this.areaOfEffect = compound.getFloat("AreaOfEffect");
        if (compound.contains("Color", 99)) {
            this.setFixedColor(compound.getInt("Color"));
        }

        if (compound.contains("Potion", 8)) {
            this.setPotion(PotionUtils.getPotion(compound));
        }

        if (compound.contains("Effects", 9)) {
            ListTag listtag = compound.getList("Effects", 10);
            this.effects.clear();

            for(int i = 0; i < listtag.size(); ++i) {
                MobEffectInstance mobeffectinstance = MobEffectInstance.load(listtag.getCompound(i));
                if (mobeffectinstance != null) {
                    this.addEffect(mobeffectinstance);
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.ownerUniqueId != null) {
            compound.putUUID("Owner", this.ownerUniqueId);
        }
        compound.putBoolean("PlaySound", this.playSound);
        compound.putFloat("ExtraDamage", this.extraDamage);
        compound.putFloat("AreaOfEffect", this.areaOfEffect);
        if (this.fixedColor) {
            compound.putInt("Color", this.getColor());
        }

        if (this.potion != Potions.EMPTY) {
            compound.putString("Potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
        }

        if (!this.effects.isEmpty()) {
            ListTag listtag = new ListTag();

            for(MobEffectInstance mobeffectinstance : this.effects) {
                listtag.add(mobeffectinstance.save(new CompoundTag()));
            }

            compound.put("Effects", listtag);
        }
    }

    public void setOwner(@Nullable LivingEntity ownerIn) {
        this.owner = ownerIn;
        this.ownerUniqueId = ownerIn == null ? null : ownerIn.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUniqueId != null && this.level instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level).getEntity(this.ownerUniqueId);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    public void setPotion(Potion p_19723_) {
        this.potion = p_19723_;
        if (!this.fixedColor) {
            this.updateColor();
        }

    }

    public Potion getPotion() {
        return this.potion;
    }

    private void updateColor() {
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.getEntityData().set(DATA_COLOR, 0);
        } else {
            this.getEntityData().set(DATA_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
        }

    }

    public void addEffect(MobEffectInstance p_19717_) {
        this.effects.add(p_19717_);
        if (!this.fixedColor) {
            this.updateColor();
        }

    }

    public int getColor() {
        return this.getEntityData().get(DATA_COLOR);
    }

    public void setFixedColor(int p_19715_) {
        this.fixedColor = true;
        this.getEntityData().set(DATA_COLOR, p_19715_);
    }

    public void setExtraDamage(float damage) {
        this.extraDamage = damage;
    }

    public float getExtraDamage() {
        return this.extraDamage;
    }

    public void setAreaOfEffect(float damage) {
        this.areaOfEffect = damage;
    }

    public float getAreaOfEffect() {
        return this.areaOfEffect;
    }

    public void setImmediate(boolean immediate){
        this.entityData.set(IMMEDIATE, immediate);
    }

    public boolean getImmediate(){
        return this.entityData.get(IMMEDIATE);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level instanceof ServerLevel serverLevel) {
            float area = this.getAreaOfEffect() / 2;
            float f = 1.5F + area;
            float f5 = (float) Math.PI * f * f;
            for (int k1 = 0; (float) k1 < f5; ++k1) {
                float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                float f8 = Mth.cos(f6) * f7;
                float f9 = Mth.sin(f6) * f7;
                double d5 = (float)(this.getColor() >> 16 & 255) / 255.0F;
                double d6 = (float)(this.getColor() >> 8 & 255) / 255.0F;
                double d7 = (float)(this.getColor() & 255) / 255.0F;
                serverLevel.sendParticles(ParticleTypes.ENTITY_EFFECT, this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 0, d5, d6, d7, 1.0F);
            }
            ColorUtil color = new ColorUtil(this.getColor());
            ServerParticleUtil.windParticle(serverLevel, color, (f - 1.0F) + serverLevel.random.nextFloat() * 0.5F, 0.0F, this.getId(), this.position());
            ServerParticleUtil.windParticle(serverLevel, color, f + serverLevel.random.nextFloat() * 0.5F, 0.0F, this.getId(), this.position());

            if (this.tickCount == 20 || this.getImmediate()){
                for (int j1 = 0; j1 < 16; ++j1) {
                    for (int k1 = 0; (float) k1 < f5; ++k1) {
                        float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                        float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                        float f8 = Mth.cos(f6) * f7;
                        float f9 = Mth.sin(f6) * f7;
                        double d5 = (float)(this.getColor() >> 16 & 255) / 255.0F;
                        double d6 = (float)(this.getColor() >> 8 & 255) / 255.0F;
                        double d7 = (float)(this.getColor() & 255) / 255.0F;
                        serverLevel.sendParticles(ParticleTypes.ENTITY_EFFECT, this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 0, d5, d6, d7, 1.0F);
                    }
                }
                List<Entity> targets = new ArrayList<>();
                float area0 = 1.0F + area;
                AABB aabb = this.getBoundingBox();
                AABB aabb1 = new AABB(aabb.minX - area0, aabb.minY - 1.0F, aabb.minZ - area0, aabb.maxX + area0, aabb.maxY + 1.0F, aabb.maxZ + area0);
                for (Entity entity : this.level.getEntitiesOfClass(Entity.class, aabb1)){
                    if (this.owner != null) {
                        if (entity != this.owner && !MobUtil.areAllies(entity, this.owner)) {
                            if (this.owner instanceof Mob mob && this.owner instanceof Enemy && entity instanceof Enemy) {
                                if (mob.getTarget() == entity) {
                                    targets.add(entity);
                                }
                            } else {
                                targets.add(entity);
                            }
                        }
                    } else {
                        targets.add(entity);
                    }
                }
                List<MobEffectInstance> list = Lists.newArrayList();
                list.addAll(this.potion.getEffects());
                list.addAll(this.effects);
                if (!targets.isEmpty()){
                    for (Entity entity : targets) {
                        if (entity instanceof LivingEntity livingEntity) {
                            MobUtil.push(livingEntity, 0.0D, 1.0D, 0.0D, 0.5D);
                            for(MobEffectInstance mobeffectinstance1 : list) {
                                if (mobeffectinstance1.getEffect().isInstantenous()) {
                                    mobeffectinstance1.getEffect().applyInstantenousEffect(this, this.getOwner(), livingEntity, mobeffectinstance1.getAmplifier(), 0.5D);
                                } else {
                                    livingEntity.addEffect(new MobEffectInstance(mobeffectinstance1), this);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.tickCount > 20 || (this.getImmediate() && this.tickCount > 5)){
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.25D, 0.0D));
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
        if (this.playSound) {
            if (this.tickCount == 20 || (this.getImmediate() && this.tickCount == 5)) {
                this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F);
            }
        }
        if (this.owner != null){
            if (this.owner.isDeadOrDying() || this.owner.isRemoved()){
                this.discard();
            }
        }
        if (this.tickCount % 30 == 0){
            this.discard();
        }
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
