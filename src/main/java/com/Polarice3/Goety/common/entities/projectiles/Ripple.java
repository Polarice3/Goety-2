package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.ColoredSpellSquareParticleOption;
import com.Polarice3.Goety.client.render.RippleRenderer;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;

public class Ripple extends SpellEntity {
    private static final EntityDataAccessor<CompoundTag> DATA_CONFIG = SynchedEntityData.defineId(Ripple.class, EntityDataSerializers.COMPOUND_TAG);
    private RippleStyle rippleStyle = RippleStyle.LIGHTNING;
    private RippleDamage rippleDamage = RippleDamage.NORMAL;
    private float minRadius = 0.0F;
    private float maxRadius = 10.0F;
    private float speed = 2.5F;
    private float margin = 0.1F;
    private float angle = 360.0F;
    private float rotationOffset = 0.0F;
    private boolean reversed = false;
    private float maxHeightDiff = 1.0F;
    private boolean jumpable = true;
    private float jumpClearance = 0.4F;
    private int startDelay = 0;
    private int rippleCount = 1;
    private int delayBetween = 0;
    private boolean loop = false;
    private int lifespan = -1;
    private int color = 0xFFFFFF;
    private float particleDensity = 1.0F;
    private long spawnTime = Long.MIN_VALUE;
    private float damage = 2.0F;
    private float percentDamage = 0.0F;
    private boolean friendlyFire = false;
    private boolean affectOnce = true;
    private int timeBetweenHits = 20; 
    private int fireSeconds = 0;
    private int invTime = -1;
    private String soundEvent = "";
    private final List<MobEffectInstance> effects = new ArrayList<>();
    private final Map<Integer, Set<UUID>> waveHits = new HashMap<>();
    private final Map<UUID, Integer> lastHitAge = new HashMap<>();
    private boolean hadOwner = false;

    public Ripple(EntityType<? extends Ripple> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public Ripple(Level level, double x, double y, double z) {
        this(ModEntityType.RIPPLE.get(), level);
        this.setPos(x, y, z);
    }

    public Ripple(Level level, @Nullable LivingEntity owner, double x, double y, double z, Preset preset) {
        this(level, x, y, z);
        if (owner != null) {
            this.setOwner(owner);
            this.setYRot(owner.getYRot());
        }
        this.applyPreset(preset);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CONFIG, new CompoundTag());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (DATA_CONFIG.equals(accessor) && this.level.isClientSide) {
            this.readConfig(this.entityData.get(DATA_CONFIG));
        }
    }

    public int getWaveDuration() {
        float perTick = Math.max(this.speed, 0.001F) / 20.0F;
        return Math.max(1, Mth.ceil(Math.abs(this.maxRadius - this.minRadius) / perTick));
    }

    public int getWaveInterval() {
        if (this.delayBetween > 0) {
            return this.delayBetween;
        }
        return this.loop ? this.getWaveDuration() : 0;
    }

    public int getWaveCount() {
        if (this.loop) {
            return Integer.MAX_VALUE;
        }
        return this.getWaveInterval() <= 0 ? 1 : Math.max(1, this.rippleCount);
    }

    public int getWaveStart(int wave) {
        return this.startDelay + wave * this.getWaveInterval();
    }

    public float getWaveProgress(int wave, float age) {
        float t = age - this.getWaveStart(wave);
        int duration = this.getWaveDuration();
        if (t < 0 || t > duration) {
            return -1.0F;
        }
        return t / duration;
    }

    public float getWaveRadius(float progress) {
        progress = Mth.clamp(progress, 0.0F, 1.0F);
        return this.reversed ? Mth.lerp(progress, this.maxRadius, this.minRadius) : Mth.lerp(progress, this.minRadius, this.maxRadius);
    }

    public int[] getActiveWaveRange(float age) {
        int interval = this.getWaveInterval();
        int count = this.getWaveCount();
        if (interval <= 0) {
            return new int[]{0, 0};
        }
        int last = Mth.floor((age - this.startDelay) / interval);
        int first = Mth.floor((age - this.startDelay - this.getWaveDuration()) / interval);
        return new int[]{Math.max(0, first), Math.min(count - 1, last)};
    }

    public float getAge(float partialTicks) {
        if (this.level.isClientSide && this.spawnTime != Long.MIN_VALUE) {
            return (float) (this.level.getGameTime() - this.spawnTime) + partialTicks;
        }
        return this.tickCount + partialTicks;
    }

    public float getFacingYaw() {
        return this.getYRot() + this.rotationOffset;
    }

    public float getRadius() {
        float age = this.getAge(0);
        int[] range = this.getActiveWaveRange(age);
        float r = 0.0F;
        for (int w = range[0]; w <= range[1]; ++w) {
            float p = this.getWaveProgress(w, age);
            if (p >= 0) {
                r = Math.max(r, this.getWaveRadius(p));
            }
        }
        return r;
    }

    public Ripple setStyle(RippleStyle rippleStyle) {
        this.rippleStyle = rippleStyle;
        this.syncConfig();
        return this;
    }

    public Ripple setDamageType(RippleDamage rippleDamage) {
        this.rippleDamage = rippleDamage;
        this.syncConfig();
        return this;
    }

    public Ripple setStartRadius(float radius) {
        this.minRadius = Math.max(0, radius);
        this.syncConfig();
        return this;
    }

    public Ripple setEndRadius(float radius) {
        this.maxRadius = Math.max(0, radius);
        this.syncConfig();
        return this;
    }

    public Ripple setSpeed(float blocksPerSecond) {
        this.speed = blocksPerSecond;
        this.syncConfig();
        return this;
    }

    public Ripple setMargin(float margin) {
        this.margin = margin;
        this.syncConfig();
        return this;
    }

    public Ripple setAngle(float degrees) {
        this.angle = Mth.clamp(degrees, 1.0F, 360.0F);
        this.syncConfig();
        return this;
    }

    public Ripple setRotationOffset(float degrees) {
        this.rotationOffset = degrees;
        this.syncConfig();
        return this;
    }

    public Ripple setReversed(boolean reversed) {
        this.reversed = reversed;
        this.syncConfig();
        return this;
    }

    public Ripple setMaxHeightDiff(float blocks) {
        this.maxHeightDiff = blocks;
        this.syncConfig();
        return this;
    }

    public Ripple setJumpable(boolean jumpable) {
        this.jumpable = jumpable;
        this.syncConfig();
        return this;
    }

    public Ripple setJumpClearance(float blocks) {
        this.jumpClearance = blocks;
        this.syncConfig();
        return this;
    }

    public Ripple setStartDelay(int ticks) {
        this.startDelay = Math.max(0, ticks);
        this.syncConfig();
        return this;
    }

    public Ripple setRippleCount(int count) {
        this.rippleCount = Math.max(1, count);
        this.syncConfig();
        return this;
    }

    public Ripple setDelayBetween(int ticks) {
        this.delayBetween = Math.max(0, ticks);
        this.syncConfig();
        return this;
    }

    public Ripple setLoop(boolean loop) {
        this.loop = loop;
        this.syncConfig();
        return this;
    }

    public Ripple setLifespan(int ticks) {
        this.lifespan = ticks;
        this.syncConfig();
        return this;
    }

    public Ripple setParticleDensity(float density) {
        this.particleDensity = Math.max(0, density);
        this.syncConfig();
        return this;
    }

    public void setColor(int rgb) {
        this.color = rgb;
        this.syncConfig();
    }

    public Ripple setDamage(float damage) {
        this.damage = damage;
        return this;
    }

    public Ripple setPercentDamage(float fraction) {
        this.percentDamage = fraction;
        return this;
    }

    public Ripple setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
        return this;
    }

    public Ripple setAffectOnce(boolean once) {
        this.affectOnce = once;
        return this;
    }

    public Ripple setTimeBetweenHits(int ticks) {
        this.timeBetweenHits = ticks;
        return this;
    }

    public Ripple setFireSeconds(int seconds) {
        this.fireSeconds = seconds;
        return this;
    }

    public void setInvTime(int ticks) {
        this.invTime = ticks;
    }

    public void setSoundEvent(String id) {
        this.soundEvent = id == null ? "" : id;
    }

    public Ripple addEffect(MobEffectInstance effect) {
        this.effects.add(effect);
        return this;
    }

    public Ripple setRadius(float radius) {
        return this.setStartRadius(radius);
    }

    public RippleStyle getStyle() {
        return this.rippleStyle;
    }

    public RippleDamage getDamageType() {
        return this.rippleDamage;
    }

    public float getStartRadius() {
        return this.minRadius;
    }

    public float getEndRadius() {
        return this.maxRadius;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getAngle() {
        return this.angle;
    }

    public float getParticleDensity() {
        return this.particleDensity;
    }

    public int getColor() {
        return this.color;
    }

    public float getDamage() {
        return this.damage;
    }

    public float getPercentDamage() {
        return this.percentDamage;
    }

    public int getInvTime() {
        return this.invTime;
    }

    public String getSoundEvent() {
        return this.soundEvent;
    }

    public List<MobEffectInstance> getEffects() {
        return this.effects;
    }
    
    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(0, 0, 0);

        if (this.level.isClientSide) {
            RippleRenderer.clientTick(this);
            return;
        }

        if (this.spawnTime == Long.MIN_VALUE || this.level.getGameTime() - this.spawnTime != this.tickCount) {
            this.spawnTime = this.level.getGameTime() - this.tickCount;
            this.syncConfig();
        }

        int age = this.tickCount;
        LivingEntity owner = this.getOwner();
        if (owner != null) {
            this.hadOwner = true;
        }

        if (this.loop && this.hadOwner && (owner == null || !owner.isAlive())) {
            int interval = Math.max(1, this.getWaveInterval());
            int started = Math.max(0, Mth.floor((float) (age - this.startDelay) / interval) + 1);
            this.loop = false;
            this.rippleCount = Math.max(1, started);
            this.delayBetween = interval;
            this.syncConfig();
        }

        int[] range = this.getActiveWaveRange(age);
        for (int wave = range[0]; wave <= range[1]; ++wave) {
            int start = this.getWaveStart(wave);
            if (age < start) {
                continue;
            }
            if (age == start) {
                this.onWaveStart(wave);
            }
            float p = this.getWaveProgress(wave, age);
            if (p < 0) {
                this.waveHits.remove(wave);
                continue;
            }
            float pPrev = Math.max(0.0F, this.getWaveProgress(wave, Math.max(start, age - 1)));
            this.hurtEntities(wave, this.getWaveRadius(pPrev), this.getWaveRadius(p), owner);
        }
        this.waveHits.keySet().removeIf(w -> w < range[0]);

        boolean finished = !this.loop && age > this.getWaveStart(this.getWaveCount() - 1) + this.getWaveDuration() + 1;
        if (finished || (this.lifespan > 0 && age >= this.lifespan)) {
            this.discard();
        }
    }

    protected void onWaveStart(int wave) {
        if (!this.soundEvent.isEmpty()) {
            ResourceLocation id = ResourceLocation.tryParse(this.soundEvent);
            SoundEvent sound = id != null ? ForgeRegistries.SOUND_EVENTS.getValue(id) : null;
            if (sound != null) {
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.HOSTILE, 1.0F, 1.0F);
            }
        }
    }

    protected void hurtEntities(int wave, float rPrev, float rNow, @Nullable LivingEntity owner) {
        float lo = Math.min(rPrev, rNow) - this.margin;
        float hi = Math.max(rPrev, rNow) + this.margin;
        double cx = this.getX(), cy = this.getY(), cz = this.getZ();
        AABB search = new AABB(cx - hi - 2, cy - this.maxHeightDiff - 2, cz - hi - 2, cx + hi + 2, cy + this.maxHeightDiff + 2, cz + hi + 2);

        for (LivingEntity target : this.level.getEntitiesOfClass(LivingEntity.class, search)) {
            if (!this.canHit(target, owner)) {
                continue;
            }
            if (!this.ringTouches(target.getBoundingBox(), lo, hi, target)) {
                continue;
            }
            UUID id = target.getUUID();
            if (this.affectOnce) {
                Set<UUID> hit = this.waveHits.computeIfAbsent(wave, w -> new HashSet<>());
                if (hit.contains(id)) {
                    continue;
                }
                hit.add(id);
            } else {
                Integer last = this.lastHitAge.get(id);
                if (last != null && this.tickCount - last < this.timeBetweenHits) {
                    continue;
                }
                this.lastHitAge.put(id, this.tickCount);
            }
            this.applyHit(target, owner);
        }
    }

    protected boolean canHit(LivingEntity target, @Nullable LivingEntity owner) {
        if (!target.isAlive() || target.isSpectator() || !target.isAttackable() || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            return false;
        }
        if (owner != null) {
            if (target == owner) {
                return false;
            }
            return this.friendlyFire || !MobUtil.areAllies(owner, target);
        }
        return true;
    }

    protected boolean ringTouches(AABB box, float lo, float hi, Entity target) {
        double cy = this.getY();
        if (box.maxY < cy - this.maxHeightDiff || box.minY > cy + this.maxHeightDiff) {
            return false;
        }
        if (this.jumpable && !target.onGround() && box.minY > cy + this.jumpClearance) {
            return false;
        }

        double cx = this.getX(), cz = this.getZ();
        double nx = Mth.clamp(cx, box.minX, box.maxX) - cx;
        double nz = Mth.clamp(cz, box.minZ, box.maxZ) - cz;
        double near = Math.sqrt(nx * nx + nz * nz);
        double fx = Math.max(Math.abs(box.minX - cx), Math.abs(box.maxX - cx));
        double fz = Math.max(Math.abs(box.minZ - cz), Math.abs(box.maxZ - cz));
        double far = Math.sqrt(fx * fx + fz * fz);
        if (near > hi || far < lo) {
            return false;
        }

        if (this.angle < 360.0F) {
            double dx = (box.minX + box.maxX) * 0.5 - cx;
            double dz = (box.minZ + box.maxZ) * 0.5 - cz;
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > 1.0E-4) {
                float yawTo = (float) (Mth.atan2(-dx, dz) * Mth.RAD_TO_DEG);
                float halfWidth = (float) ((box.maxX - box.minX) * 0.5);
                float slack = (float) (Math.atan2(halfWidth, dist) * Mth.RAD_TO_DEG);
                return Math.abs(Mth.wrapDegrees(yawTo - this.getFacingYaw())) <= this.angle * 0.5F + slack;
            }
        }
        return true;
    }

    protected DamageSource getRippleDamageSource(@Nullable LivingEntity owner) {
        return owner != null ? this.rippleDamage.getWithOwner(this, owner) : this.rippleDamage.getWithoutOwner(this.level);
    }

    protected void applyHit(LivingEntity target, @Nullable LivingEntity owner) {
        float amount = this.getDamage() + (this.getPercentDamage() * target.getMaxHealth());
        if (target.hurt(this.getRippleDamageSource(owner), amount)) {
            for (MobEffectInstance effect : this.effects) {
                target.addEffect(new MobEffectInstance(effect), owner != null ? owner : this);
            }
            if (this.fireSeconds > 0) {
                target.setSecondsOnFire(this.fireSeconds);
            }
            if (this.invTime >= 0) {
                target.invulnerableTime = this.invTime;
            }
            this.onRippleHit(target, owner);
        }
    }

    protected void onRippleHit(LivingEntity target, @Nullable LivingEntity owner) {
    }
    
    private void syncConfig() {
        if (!this.level.isClientSide) {
            this.entityData.set(DATA_CONFIG, this.writeConfig(new CompoundTag()));
        }
    }

    private CompoundTag writeConfig(CompoundTag tag) {
        tag.putString("Style", this.rippleStyle.name());
        tag.putFloat("MinRadius", this.minRadius);
        tag.putFloat("MaxRadius", this.maxRadius);
        tag.putFloat("Speed", this.speed);
        tag.putFloat("Margin", this.margin);
        tag.putFloat("Angle", this.angle);
        tag.putFloat("RotationOffset", this.rotationOffset);
        tag.putBoolean("Reversed", this.reversed);
        tag.putFloat("MaxHeightDiff", this.maxHeightDiff);
        tag.putBoolean("Jumpable", this.jumpable);
        tag.putFloat("JumpClearance", this.jumpClearance);
        tag.putInt("StartDelay", this.startDelay);
        tag.putInt("RippleCount", this.rippleCount);
        tag.putInt("DelayBetween", this.delayBetween);
        tag.putBoolean("Loop", this.loop);
        tag.putInt("Lifespan", this.lifespan);
        tag.putInt("Color", this.color);
        tag.putFloat("ParticleDensity", this.particleDensity);
        tag.putLong("SpawnTime", this.spawnTime);
        return tag;
    }

    private void readConfig(CompoundTag tag) {
        if (tag.isEmpty()) {
            return;
        }
        try {
            this.rippleStyle = RippleStyle.valueOf(tag.getString("Style"));
        } catch (IllegalArgumentException ignored) {
            this.rippleStyle = RippleStyle.NONE;
        }
        this.minRadius = tag.getFloat("MinRadius");
        this.maxRadius = tag.getFloat("MaxRadius");
        this.speed = tag.getFloat("Speed");
        this.margin = tag.getFloat("Margin");
        this.angle = tag.getFloat("Angle");
        this.rotationOffset = tag.getFloat("RotationOffset");
        this.reversed = tag.getBoolean("Reversed");
        this.maxHeightDiff = tag.getFloat("MaxHeightDiff");
        this.jumpable = tag.getBoolean("Jumpable");
        this.jumpClearance = tag.getFloat("JumpClearance");
        this.startDelay = tag.getInt("StartDelay");
        this.rippleCount = tag.getInt("RippleCount");
        this.delayBetween = tag.getInt("DelayBetween");
        this.loop = tag.getBoolean("Loop");
        this.lifespan = tag.getInt("Lifespan");
        this.color = tag.getInt("Color");
        this.particleDensity = tag.getFloat("ParticleDensity");
        this.spawnTime = tag.getLong("SpawnTime");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("RippleConfig", this.writeConfig(new CompoundTag()));
        compound.putInt("Age", this.tickCount);
        compound.putFloat("Damage", this.damage);
        compound.putFloat("PercentDamage", this.percentDamage);
        compound.putBoolean("FriendlyFire", this.friendlyFire);
        compound.putBoolean("AffectOnce", this.affectOnce);
        compound.putInt("TimeBetweenHits", this.timeBetweenHits);
        compound.putInt("FireSeconds", this.fireSeconds);
        compound.putInt("InvTime", this.invTime);
        compound.putString("SoundEvent", this.soundEvent);
        compound.putBoolean("HadOwner", this.hadOwner);
        ListTag list = new ListTag();
        for (MobEffectInstance effect : this.effects) {
            list.add(effect.save(new CompoundTag()));
        }
        compound.put("Effects", list);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("RippleConfig")) {
            this.readConfig(compound.getCompound("RippleConfig"));
        }
        this.tickCount = compound.getInt("Age");
        this.spawnTime = Long.MIN_VALUE;
        this.damage = compound.getFloat("Damage");
        this.percentDamage = compound.getFloat("PercentDamage");
        this.friendlyFire = compound.getBoolean("FriendlyFire");
        this.affectOnce = compound.getBoolean("AffectOnce");
        this.timeBetweenHits = compound.getInt("TimeBetweenHits");
        this.fireSeconds = compound.getInt("FireSeconds");
        this.invTime = compound.getInt("InvTime");
        this.soundEvent = compound.getString("SoundEvent");
        this.hadOwner = compound.getBoolean("HadOwner");
        this.effects.clear();
        ListTag list = compound.getList("Effects", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); ++i) {
            MobEffectInstance effect = MobEffectInstance.load(list.getCompound(i));
            if (effect != null) {
                this.effects.add(effect);
            }
        }
        this.syncConfig();
    }
    
    @Override
    public AABB getBoundingBoxForCulling() {
        double r = Math.max(this.minRadius, this.maxRadius) + 2.0;
        return new AABB(this.getX() - r, this.getY() - 1.0, this.getZ() - r, this.getX() + r, this.getY() + 2.0, this.getZ() + r);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double r = Math.max(this.minRadius, this.maxRadius) + 64.0;
        return distance < r * r;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public enum Preset {
        SCULK(RippleStyle.SCULK, 0.0F, 10.0F, 2.5F, 360.0F, true, 1.0F, 0, (15.0F / 100.0F) * 20.0F, RippleDamage.SOUL),
        HOPPER(RippleStyle.SCULK, 0.0F, 5.01F, 5.0F, 360.0F, true, 1.0F, 0, (10.0F / 100.0F) * 20.0F, RippleDamage.SOUL),
        LIGHTNING(RippleStyle.LIGHTNING, 0.0F, 14.0F, 4.0F, 360.0F, true, 1.0F, 0, (15.0F / 100.0F) * 20.0F, RippleDamage.LIGHTNING),
        REDSTONE(RippleStyle.REDSTONE, 0.0F, 10.0F, 4.0F, 360.0F, true, 1.0F, 4, (15.0F / 100.0F) * 20.0F, RippleDamage.FIRE),
        SOUL_STOMP(RippleStyle.SOUL, 0.0F, 8.0F, 8.0F, 90.0F, false, 2.5F, 0, (5.0F / 100.0F) * 20.0F, RippleDamage.SOUL),
        SOUL_RIPPLE(RippleStyle.SOUL, 0.0F, 8.0F, 4.0F, 90.0F, true, 1.0F, 0, (5.0F / 100.0F) * 20.0F, RippleDamage.SOUL),
        NECRO_RIPPLE(RippleStyle.NECRO, 0.0F, 8.0F, 4.0F, 90.0F, true, 1.0F, 0, (5.0F / 100.0F) * 20.0F, RippleDamage.SOUL);

        public final RippleStyle rippleStyle;
        public final float minRadius, maxRadius, speed, angle, maxHeightDiff, damage;
        public final boolean jumpable;
        public final int fireSeconds;
        public final RippleDamage rippleDamage;

        Preset(RippleStyle rippleStyle, float minRadius, float maxRadius, float speed, float angle, boolean jumpable, float maxHeightDiff, int fireSeconds, float damage, RippleDamage rippleDamage) {
            this.rippleStyle = rippleStyle;
            this.minRadius = minRadius;
            this.maxRadius = maxRadius;
            this.speed = speed;
            this.angle = angle;
            this.jumpable = jumpable;
            this.maxHeightDiff = maxHeightDiff;
            this.fireSeconds = fireSeconds;
            this.damage = damage;
            this.rippleDamage = rippleDamage;
        }
    }

    public Ripple applyPreset(Preset preset) {
        this.rippleStyle = preset.rippleStyle;
        this.minRadius = preset.minRadius;
        this.maxRadius = preset.maxRadius;
        this.speed = preset.speed;
        this.angle = preset.angle;
        this.jumpable = preset.jumpable;
        this.maxHeightDiff = preset.maxHeightDiff;
        this.fireSeconds = preset.fireSeconds;
        this.margin = 0.1F;
        this.affectOnce = true;
        this.friendlyFire = false;
        this.damage = preset.damage;
        this.rippleDamage = preset.rippleDamage;
        this.syncConfig();
        return this;
    }


    public enum Facing {
        CAMERA,
        FLAT,
        WALL
    }

    public enum Fade {
        NONE, 
        LATE_HALF, 
        LAST_FIFTH, 
        LAST_TENTH;

        public float alpha(float p) {
            return switch (this) {
                case NONE -> 1.0F;
                case LATE_HALF -> p < 0.5F ? 1.0F : 0.5F + 0.5F * Mth.cos((float) Math.PI * (p - 0.5F) / 0.5F);
                case LAST_FIFTH -> p < 0.8F ? 1.0F : 0.5F + 0.5F * Mth.cos((float) Math.PI * (p - 0.8F) / 0.2F);
                case LAST_TENTH -> p < 0.9F ? 1.0F : 1.0F - (p - 0.9F) / 0.1F;
            };
        }
    }

    public enum Ramp {
        CONSTANT, 
        EASE_OUT, 
        RAMP_UP;

        public float apply(float p) {
            return switch (this) {
                case CONSTANT -> 1.0F;
                case EASE_OUT -> 1.0F - (1.0F - p) * (1.0F - p) * (1.0F - p);
                case RAMP_UP -> p < 0.9F ? p / 0.9F : (1.0F - p) / 0.1F;
            };
        }
    }

    public record Band(Facing facing, boolean additive, float yOffset, float width, float widthJitter, float jag,
                       int inner, int center, int outer, boolean electric) {
        public Band(Facing facing, boolean additive, float yOffset, float width, float widthJitter, float jag,
                    int inner, int center, int outer) {
            this(facing, additive, yOffset, width, widthJitter, jag, inner, center, outer, false);
        }

        public static Band electric(Facing facing, float yOffset, float width, float widthJitter, int argb) {
            return new Band(facing, true, yOffset, width, widthJitter, 0.0F, argb, argb, argb, true);
        }
    }

    @FunctionalInterface
    public interface ParticleFactory {
        ParticleOptions create(float r, float g, float b, float size, int life);

        ParticleFactory PIXEL = (r, g, b, size, life) -> new ColoredSpellSquareParticleOption(size, r, g, b, life);
        ParticleFactory DUST = (r, g, b, size, life) -> new DustParticleOptions(new Vector3f(r, g, b), Mth.clamp(size, 0.01F, 4.0F));
    }

    public record Emitter(ParticleFactory factory, float perTick, Ramp ramp, int colorA, int colorB,
                          float sizeMin, float sizeMax, int lifeMin, int lifeMax, float tube, float yOffset,
                          float outwardMin, float outwardMax, float upMin, float upMax) {
    }

    public enum RippleStyle {
        NONE(1, Fade.NONE, new Band[0], new Emitter[0]),
        LIGHTNING(2, Fade.LATE_HALF, new Band[]{
                new Band(Facing.CAMERA, true, 0.2F, 0.6F, 1.5F, 0.0F, 0x00FF9000, 0x50FFB020, 0x00FF9000),
                Band.electric(Facing.CAMERA, 0.2F, 0.3F, 3.0F, 0xFFFFE040)   // 0.3 x (1..4), HDR yellow
        }, new Emitter[0]),
        REDSTONE(2, Fade.LAST_TENTH, new Band[]{
                new Band(Facing.CAMERA, false, 0.2F, 0.75F, 0.25F, 0.0F, 0x00200000, 0xB0400000, 0x00200000),
                new Band(Facing.CAMERA, true, 0.2F, 0.9F, 0.25F, 0.0F, 0x00FF2000, 0x40FF3010, 0x00FF2000),
                Band.electric(Facing.CAMERA, 0.2F, 0.6F, 0.25F, 0xFFFF4C1A)  // 0.3 x (2..2.5), (2.5, .25-.5, 0-.25)
        }, new Emitter[]{
                new Emitter(ParticleFactory.PIXEL, 15.0F, Ramp.EASE_OUT, 0xFF1A00, 0xFF4020,
                        0.06F, 0.12F, 8, 16, 0.25F, 0.2F, 0.08F, 0.15F, -0.01F, 0.03F),
                new Emitter(ParticleFactory.DUST, 2.0F, Ramp.CONSTANT, 0xB01010, 0xC04040,
                        2.5F, 4.0F, 4, 8, 0.25F, 0.3F, 0.0F, 0.02F, 0.0F, 0.02F)
        }),
        SCULK(2, Fade.LAST_FIFTH, new Band[]{
                new Band(Facing.FLAT, true, 0.05F, 0.65F, 0.25F, 0.0F, 0x00008070, 0xB000D8D8, 0xFF60FFFF),
                new Band(Facing.FLAT, true, 0.06F, 0.12F, 0.0F, 0.0F, 0x0040FFFF, 0xFFC0FFFF, 0x0040FFFF)
        }, new Emitter[]{
                new Emitter(ParticleFactory.PIXEL, 5.0F, Ramp.CONSTANT, 0x30E0FF, 0x80FFFF,
                        0.04F, 0.07F, 2, 10, 0.05F, 0.1F, 0.0F, 0.0F, 0.02F, 0.03F),
                new Emitter(ParticleFactory.PIXEL, 20.0F, Ramp.RAMP_UP, 0x001010, 0x002A2A,
                        0.07F, 0.13F, 2, 6, 0.05F, 0.1F, 0.0F, 0.0F, 0.02F, 0.03F),
                new Emitter(ParticleFactory.PIXEL, 25.0F, Ramp.RAMP_UP, 0x000808, 0x001818,
                        0.05F, 0.09F, 2, 3, 0.05F, 0.1F, 0.0F, 0.0F, 0.75F, 1.0F)
        }),
        SOUL(2, Fade.LAST_FIFTH, new Band[]{
                new Band(Facing.FLAT, true, 0.05F, 0.7F, 0.2F, 0.0F, 0x00205080, 0xA040B0FF, 0xFFA0F0FF),
                new Band(Facing.CAMERA, true, 0.2F, 0.12F, 0.3F, 0.0F, 0x0060D0FF, 0xC0C0F8FF, 0x0060D0FF)
        }, new Emitter[]{
                new Emitter(ParticleFactory.PIXEL, 8.0F, Ramp.CONSTANT, 0x40C0FF, 0xA0F0FF,
                        0.04F, 0.08F, 4, 12, 0.1F, 0.15F, 0.0F, 0.02F, 0.02F, 0.05F)
        }),
        NECRO(2, Fade.LAST_FIFTH, new Band[]{
                new Band(Facing.FLAT, true, 0.05F, 0.7F, 0.2F, 0.0F, 0x001B3600, 0xA036DC0B, 0xFF9DF431),
                new Band(Facing.CAMERA, true, 0.2F, 0.12F, 0.3F, 0.0F, 0x0036DC0B, 0xC0C3FF80, 0x0036DC0B)
        }, new Emitter[]{
                new Emitter(ParticleFactory.PIXEL, 8.0F, Ramp.CONSTANT, 0x36dc0b, 0x9df431,
                        0.04F, 0.08F, 4, 12, 0.1F, 0.15F, 0.0F, 0.02F, 0.02F, 0.05F)
        }),
        CUSTOM(2, Fade.LAST_FIFTH, new Band[0], new Emitter[0]) {
            @Override
            public Band[] bands(Ripple ripple) {
                int c = ripple.getColor() & 0xFFFFFF;
                return new Band[]{
                        new Band(Facing.CAMERA, true, 0.2F, 0.7F, 1.0F, 0.0F, c, 0x50000000 | c, c),
                        Band.electric(Facing.CAMERA, 0.2F, 0.35F, 1.5F, 0xFF000000 | lighten(c, 0.35F))
                };
            }

            @Override
            public Emitter[] emitters(Ripple ripple) {
                int c = ripple.getColor() & 0xFFFFFF;
                return new Emitter[]{
                        new Emitter(ParticleFactory.PIXEL, 6.0F, Ramp.CONSTANT, c, lighten(c, 0.5F),
                                0.05F, 0.09F, 4, 10, 0.1F, 0.2F, 0.0F, 0.04F, 0.0F, 0.03F)
                };
            }
        };
        public final int flickerTicks;
        public final Fade fade;
        private final Band[] bands;
        private final Emitter[] emitters;

        RippleStyle(int flickerTicks, Fade fade, Band[] bands, Emitter[] emitters) {
            this.flickerTicks = flickerTicks;
            this.fade = fade;
            this.bands = bands;
            this.emitters = emitters;
        }

        public Band[] bands(Ripple ripple) {
            return this.bands;
        }

        public Emitter[] emitters(Ripple ripple) {
            return this.emitters;
        }

        private static int lighten(int rgb, float t) {
            int r = (rgb >> 16) & 255, g = (rgb >> 8) & 255, b = rgb & 255;
            r = (int) Mth.lerp(t, r, 255);
            g = (int) Mth.lerp(t, g, 255);
            b = (int) Mth.lerp(t, b, 255);
            return (r << 16) | (g << 8) | b;
        }
    }

    public enum RippleDamage implements IExtensibleEnum {
        NORMAL(DamageTypes.INDIRECT_MAGIC, DamageTypes.MAGIC),
        SOUL(ModDamageSource.INDIRECT_SOUL, ModDamageSource.SOUL),
        FIRE(ModDamageSource.FIRE_BREATH, DamageTypes.IN_FIRE),
        LIGHTNING(ModDamageSource.INDIRECT_SHOCK, ModDamageSource.SHOCK);
        public final ResourceKey<DamageType> withOwner;
        public final ResourceKey<DamageType> withoutOwner;

        RippleDamage(ResourceKey<DamageType> withOwner, ResourceKey<DamageType> withoutOwner) {
            this.withOwner = withOwner;
            this.withoutOwner = withoutOwner;
        }

        public static RippleDamage create(String name, ResourceKey<DamageType> withOwner, ResourceKey<DamageType> withoutOwner){
            throw new IllegalStateException("Enum not extended");
        }

        public DamageSource getWithOwner(Entity source, LivingEntity indirectSource) {
            return new DamageSource(source.level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(this.withOwner), source, indirectSource);
        }

        public DamageSource getWithoutOwner(Level level) {
            return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(this.withoutOwner));
        }
    }
}
