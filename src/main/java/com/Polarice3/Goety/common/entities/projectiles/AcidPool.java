package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.FoggyCloudParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.entities.util.AbstractTrap;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.google.common.collect.Lists;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;

public class AcidPool extends AbstractTrap {
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(AcidPool.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(AcidPool.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PERCENT_DAMAGE = SynchedEntityData.defineId(AcidPool.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(AcidPool.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_WARMUP_COLOR = SynchedEntityData.defineId(AcidPool.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_INV_TIME = SynchedEntityData.defineId(AcidPool.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_SOUND_EVENT = SynchedEntityData.defineId(AcidPool.class, EntityDataSerializers.STRING);
    private Potion potion = Potions.EMPTY;
    private final List<MobEffectInstance> effects = Lists.newArrayList();
    private boolean fixedColor;
    private boolean explodeEffect = true;
    private int warmupDelayTicks = 0;
    private int lifeTick;
    private boolean sentSpikeEvent;

    public AcidPool(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setParticle(ModParticleTypes.NONE.get());
        this.setDuration(50);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_COLOR, 0x44b529);
        this.getEntityData().define(DATA_WARMUP_COLOR, 0x44b529);
        this.getEntityData().define(DATA_INV_TIME, -1);
        this.getEntityData().define(DATA_RADIUS, 2.0F);
        this.getEntityData().define(DATA_DAMAGE, 2.0F);
        this.getEntityData().define(DATA_PERCENT_DAMAGE, 0.0F);
        this.getEntityData().define(DATA_SOUND_EVENT, "");
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_19729_) {
        if (DATA_RADIUS.equals(p_19729_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_19729_);
    }

    public EntityDimensions getDimensions(Pose p_19721_) {
        return EntityDimensions.scalable(this.radius() * 2.0F, 0.5F);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Active", this.sentSpikeEvent);
        compound.putBoolean("ExplodeEffect", this.explodeEffect);
        compound.putFloat("Radius", this.radius());
        compound.putFloat("Damage", this.getDamage());
        compound.putFloat("PercentDamage", this.getPercentDamage());
        compound.putInt("Color", this.getColor());
        compound.putInt("WarmupColor", this.getWarmupColor());
        compound.putInt("InvTime", this.getInvTime());
        compound.putInt("Warmup", this.getWarmupDelayTicks());
        compound.putInt("LifeTick", this.lifeTick);
        compound.putString("Sound", this.getSoundEvent());
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

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Active")){
            this.sentSpikeEvent = compound.getBoolean("Active");
        }
        if (compound.contains("ExplodeEffect")){
            this.explodeEffect = compound.getBoolean("ExplodeEffect");
        }
        if (compound.contains("Radius")){
            this.setRadius(compound.getFloat("Radius"));
        }
        if (compound.contains("Damage")){
            this.setDamage(compound.getFloat("Damage"));
        }
        if (compound.contains("PercentDamage")){
            this.setPercentDamage(compound.getFloat("PercentDamage"));
        }
        if (compound.contains("Color")){
            this.setUnfixedColor(compound.getInt("Color"));
        }
        if (compound.contains("WarmupColor")){
            this.setWarmupColor(compound.getInt("WarmupColor"));
        }
        if (compound.contains("InvTime")){
            this.setInvTime(compound.getInt("InvTime"));
        }
        if (compound.contains("Warmup")){
            this.setWarmupDelayTicks(compound.getInt("Warmup"));
        }
        if (compound.contains("LifeTick")){
            this.lifeTick = compound.getInt("LifeTick");
        }
        if (compound.contains("Sound")){
            this.setSoundEvent(compound.getString("Sound"));
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

    public void setRadius(float p_19713_) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Mth.clamp(p_19713_, 0.0F, 32.0F));
        }
    }

    public void setDamage(float damage) {
        this.getEntityData().set(DATA_DAMAGE, damage);
    }

    public float getDamage() {
        return this.getEntityData().get(DATA_DAMAGE);
    }

    public void setPercentDamage(float damage) {
        this.getEntityData().set(DATA_PERCENT_DAMAGE, damage);
    }

    public float getPercentDamage() {
        return this.getEntityData().get(DATA_PERCENT_DAMAGE);
    }

    private void updateColor() {
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.getEntityData().set(DATA_COLOR, 0);
        } else {
            this.getEntityData().set(DATA_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
        }

    }

    public void setPotion(Potion p_19723_) {
        this.potion = p_19723_;
        if (!this.fixedColor) {
            this.updateColor();
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

    public void setColor(int p_19715_) {
        this.fixedColor = true;
        this.getEntityData().set(DATA_COLOR, p_19715_);
    }

    public void setUnfixedColor(int p_19715_) {
        this.getEntityData().set(DATA_COLOR, p_19715_);
    }

    public List<MobEffectInstance> getEffects() {
        List<MobEffectInstance> list = new ArrayList<>();

        for (MobEffectInstance mobeffectinstance : this.potion.getEffects()) {
            list.add(new MobEffectInstance(mobeffectinstance.getEffect(), mobeffectinstance.mapDuration((p_267926_) -> p_267926_ / 4), mobeffectinstance.getAmplifier(), mobeffectinstance.isAmbient(), mobeffectinstance.isVisible()));
        }

        if (!this.effects.isEmpty()) {
            list.addAll(this.effects);
        }
        return list;
    }

    public int getWarmupColor() {
        return this.getEntityData().get(DATA_WARMUP_COLOR);
    }

    public void setWarmupColor(int p_19715_) {
        this.getEntityData().set(DATA_WARMUP_COLOR, p_19715_);
    }

    public int getInvTime() {
        return this.getEntityData().get(DATA_INV_TIME);
    }

    public void setInvTime(int p_19715_) {
        this.getEntityData().set(DATA_INV_TIME, p_19715_);
    }

    public int getWarmupDelayTicks() {
        return this.warmupDelayTicks;
    }

    public void setWarmupDelayTicks(int p_19715_) {
        this.warmupDelayTicks = p_19715_;
    }

    public String getSoundEvent() {
        return this.getEntityData().get(DATA_SOUND_EVENT);
    }

    public void setSoundEvent(String p_19715_) {
        this.getEntityData().set(DATA_SOUND_EVENT, p_19715_);
    }

    public void setSoundEvent(ResourceLocation location) {
        this.setSoundEvent(location.toString());
    }

    public void setSoundEvent(SoundEvent soundEvent) {
        this.setSoundEvent(soundEvent.getLocation());
    }

    public void setExplodeEffect(boolean explodeEffect) {
        this.explodeEffect = explodeEffect;
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    @Override
    public float radius() {
        return this.entityData.get(DATA_RADIUS);
    }

    public void tick() {
        super.tick();
        if (this.level instanceof ServerLevel serverLevel) {
            if (this.getWarmupDelayTicks() <= 0) {
                if (!this.sentSpikeEvent) {
                    this.sentSpikeEvent = true;
                    if (this.explodeEffect) {
                        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(new ResourceLocation(this.getSoundEvent()));
                        this.playSound(soundEvent, 1.0F, 1.0F);
                        ColorUtil colorUtil = new ColorUtil(this.getColor());
                        ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(), this.getX(), this.getY() + 1.0F, this.getZ(), colorUtil.red(), colorUtil.green(), colorUtil.blue(), this.radius());
                        ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.ROLLING_SPIRAL.get(), this.getX(), this.getY() + 1.0F, this.getZ(), colorUtil.red(), colorUtil.green(), colorUtil.blue(), this.radius());
                        serverLevel.sendParticles(new SphereExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), this.radius(), 1), this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), this.radius(), 1), this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    }
                }
            } else {
                --this.warmupDelayTicks;
                if (this.level.getRandom().nextBoolean()) {
                    ColorUtil colorUtil = new ColorUtil(this.getWarmupColor());
                    ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(), this.getX(), this.getY() - 0.5F, this.getZ(), colorUtil.red(), colorUtil.green(), colorUtil.blue(), this.radius() / 2.0F);
                }
            }

            if (this.sentSpikeEvent){
                ++this.lifeTick;
                if (this.getParticle() == null || this.getParticle() == ModParticleTypes.NONE.get()) {
                    if (this.tickCount % 5 == 0) {
                        serverLevel.sendParticles(new FoggyCloudParticleOption(new ColorUtil(this.getColor()), this.radius() / 2.0F, 1), this.getX(), this.getY() + 0.25D, this.getZ(), 1, 0, 0, 0, 0);
                    }
                }
                this.poolTick();
                if (this.lifeTick >= this.getDuration()) {
                    this.discard();
                }
            }
        }
    }

    public void poolTick(){
        List<LivingEntity> targets = new ArrayList<>();
        for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())){
            if (this.owner != null) {
                if (livingEntity != this.owner && !MobUtil.areAllies(this.owner, livingEntity)) {
                    targets.add(livingEntity);
                }
            } else {
                targets.add(livingEntity);
            }
        }
        if (!targets.isEmpty()){
            for (LivingEntity livingEntity : targets) {
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                    float damage = this.getDamage();
                    if (this.getPercentDamage() > 0.0F) {
                        damage = livingEntity.getMaxHealth() * this.getPercentDamage();
                    }
                    boolean flag;
                    if (this.owner != null) {
                        flag = livingEntity.hurt(ModDamageSource.acid(this, this.owner), damage);
                    } else {
                        flag = livingEntity.hurt(this.damageSources().magic(), damage);
                    }
                    if (flag) {
                        if (this.getInvTime() > -1) {
                            livingEntity.invulnerableTime = this.getInvTime();
                        }
                        if (!this.getEffects().isEmpty()) {
                            for (MobEffectInstance mobeffectinstance1 : this.getEffects()) {
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
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
