package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class PoisonQuill extends Arrow {
    private static final EntityDataAccessor<Boolean> AQUA = SynchedEntityData.defineId(PoisonQuill.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SPEAR = SynchedEntityData.defineId(PoisonQuill.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> PIERCE_LEVEL = SynchedEntityData.defineId(PoisonQuill.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData.defineId(PoisonQuill.class, EntityDataSerializers.FLOAT);
    public int duration;
    @Nullable
    private IntOpenHashSet piercingIgnoreEntityIds;
    @Nullable
    private List<Entity> piercedAndKilledEntities;

    public PoisonQuill(EntityType<? extends Arrow> p_36858_, Level p_36859_) {
        super(p_36858_, p_36859_);
    }

    public PoisonQuill(Level p_36861_, double p_36862_, double p_36863_, double p_36864_) {
        super(p_36861_, p_36862_, p_36863_, p_36864_);
    }

    public PoisonQuill(Level p_36866_, LivingEntity p_36867_) {
        super(p_36866_, p_36867_);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityType.POISON_QUILL.get();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AQUA, false);
        this.entityData.define(SPEAR, false);
        this.entityData.define(PIERCE_LEVEL, 0);
        this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
    }

    public void addAdditionalSaveData(CompoundTag p_36881_) {
        super.addAdditionalSaveData(p_36881_);
        p_36881_.putBoolean("Aqua", this.isAqua());
        p_36881_.putBoolean("Spear", this.isSpear());
        p_36881_.putInt("SpearLevel", this.getSpearLevel());
        p_36881_.putInt("Duration", this.getDuration());
        p_36881_.putFloat("ExtraDamage", this.getExtraDamage());
    }

    public void readAdditionalSaveData(CompoundTag p_36875_) {
        super.readAdditionalSaveData(p_36875_);
        if (p_36875_.contains("Aqua")){
            this.setAqua(p_36875_.getBoolean("Aqua"));
        }
        if (p_36875_.contains("Spear")){
            this.setSpear(p_36875_.getBoolean("Spear"));
        }
        if (p_36875_.contains("SpearLevel")){
            this.setSpearLevel(p_36875_.getInt("SpearLevel"));
        }
        if (p_36875_.contains("Duration")){
            this.setDuration(p_36875_.getInt("Duration"));
        }
        if (p_36875_.contains("ExtraDamage")) {
            this.setExtraDamage(p_36875_.getFloat("ExtraDamage"));
        }
    }

    public float getExtraDamage() {
        return this.entityData.get(DATA_EXTRA_DAMAGE);
    }

    public void setExtraDamage(float pDamage) {
        this.entityData.set(DATA_EXTRA_DAMAGE, pDamage);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return this.duration;
    }

    protected float getWaterInertia() {
        if (this.isAqua()) {
            return 0.99F;
        }
        return super.getWaterInertia();
    }

    public boolean isAqua(){
        return this.entityData.get(AQUA);
    }

    public void setAqua(boolean aqua){
        this.entityData.set(AQUA, aqua);
    }

    public boolean isSpear(){
        return this.entityData.get(SPEAR);
    }

    public void setSpear(boolean spear){
        this.entityData.set(SPEAR, spear);
    }

    public void setSpear(boolean spear, int pierce){
        this.setSpear(spear);
        this.setSpearLevel(pierce);
    }

    public int getSpearLevel() {
        if (this.isSpear()){
            return this.entityData.get(PIERCE_LEVEL);
        }
        return 0;
    }

    public void setSpearLevel(int level){
        this.entityData.set(PIERCE_LEVEL, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.inGround) {
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() + vec3.x;
            double d1 = this.getY() + vec3.y;
            double d2 = this.getZ() + vec3.z;
            ColorUtil colorUtil = new ColorUtil(0xefec8b);
            for (int i = 0; i < 4; ++i) {
                this.level.addParticle(ModParticleTypes.TRAIL.get(), d0 + (this.random.nextGaussian() / 2), d1 + 0.5D + (this.random.nextGaussian() / 2), d2 + (this.random.nextGaussian() / 2), colorUtil.red(), colorUtil.green(), colorUtil.blue());
            }
        } else {
            this.discard();
        }
    }

    private void resetPiercedEntities() {
        if (this.piercedAndKilledEntities != null) {
            this.piercedAndKilledEntities.clear();
        }

        if (this.piercingIgnoreEntityIds != null) {
            this.piercingIgnoreEntityIds.clear();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level.isClientSide) {
            Entity entity = pResult.getEntity();
            if (this.getSpearLevel() > 0) {
                if (this.piercingIgnoreEntityIds == null) {
                    this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
                }

                if (this.piercedAndKilledEntities == null) {
                    this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
                }

                if (this.piercingIgnoreEntityIds.size() >= this.getSpearLevel() + 1) {
                    this.discard();
                    return;
                }

                this.piercingIgnoreEntityIds.add(entity.getId());
            }
            Entity entity1 = this.getOwner();
            boolean flag;
            float damage = entity1 instanceof Mob mob ? (float) MobUtil.getAttributeValue(mob, Attributes.ATTACK_DAMAGE, 2.0D) : SpellConfig.PoisonDartDamage.get().floatValue();
            damage += this.getExtraDamage();
            if (entity1 instanceof LivingEntity livingentity) {
                if (!entity.isAlive() && this.piercedAndKilledEntities != null) {
                    this.piercedAndKilledEntities.add(livingentity);
                }
                flag = entity.hurt(DamageSource.arrow(this, entity1), damage);
                if (flag) {
                    if (entity.isAlive()) {
                        this.doEnchantDamageEffects(livingentity, entity);
                    }
                }
            } else {
                flag = entity.hurt(DamageSource.arrow(this, this), damage);
            }
            if (flag && entity.getType() != EntityType.ENDERMAN && entity instanceof LivingEntity livingEntity) {
                if (entity1 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingEntity, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, livingEntity);
                }

                this.doPostHurtEffects(livingEntity);

                MobEffect mobEffect = MobEffects.POISON;
                if ((entity1 instanceof IOwned owned && CuriosFinder.hasWildRobe(owned.getMasterOwner()))
                        || (entity1 instanceof LivingEntity livingEntity1 && CuriosFinder.hasWildRobe(livingEntity1))) {
                    mobEffect = GoetyEffects.ACID_VENOM.get();
                }
                livingEntity.addEffect(new MobEffectInstance(mobEffect, MathHelper.secondsToTicks(2 + this.getDuration())));

                this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                if (this.getSpearLevel() <= 0) {
                    this.discard();
                }
            }
        }
    }

    protected void onHitBlock(BlockHitResult p_36755_) {
        super.onHitBlock(p_36755_);
        this.resetPiercedEntities();
        if (!this.level.isClientSide) {
            this.discard();
        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        boolean flag = (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(pEntity.getId()));
        if (this.getOwner() != null){
            if (pEntity == this.getOwner()){
                return false;
            }
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity) && flag;
            } else {
                if(this.getOwner().isAlliedTo(pEntity) || pEntity.isAlliedTo(this.getOwner())){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1) && flag;
                }
            }
        }
        return super.canHitEntity(pEntity) && flag;
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        if (this.isAqua()){
            return ModSounds.POISON_QUILL_AQUA_IMPACT.get();
        }
        return ModSounds.POISON_QUILL_IMPACT.get();
    }
}
