package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.render.HauntedSkullTextures;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.BoneLord;
import com.Polarice3.Goety.common.entities.hostile.SkullLord;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class HauntedSkullProjectile extends ExplosiveProjectile{
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(HauntedSkullProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> Y_ROT_VISUAL = SynchedEntityData.defineId(HauntedSkullProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> X_ROT_VISUAL = SynchedEntityData.defineId(HauntedSkullProjectile.class, EntityDataSerializers.FLOAT);
    public boolean isPowered;
    private boolean exploding = false;

    public HauntedSkullProjectile(EntityType<? extends ExplosiveProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public HauntedSkullProjectile(double x, double y, double z, double xPower, double yPower, double zPower, Level level) {
        super(ModEntityType.HAUNTED_SKULL_SHOT.get(), x, y, z, xPower, yPower, zPower, level);
    }

    public HauntedSkullProjectile(LivingEntity shooter, double xPower, double yPower, double zPower, Level level) {
        super(ModEntityType.HAUNTED_SKULL_SHOT.get(), shooter, xPower, yPower, zPower, level);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 9);
        this.entityData.define(Y_ROT_VISUAL, 0.0F);
        this.entityData.define(X_ROT_VISUAL, 0.0F);
    }

    public void defaultExplosionAndDamage(){
        this.entityData.define(DATA_EXPLOSION, 1.0F);
        this.entityData.define(DATA_DAMAGE, SpellConfig.HauntedSkullDamage.get().floatValue() * WandUtil.damageMultiply());
    }

    public ResourceLocation getResourceLocation() {
        return HauntedSkullTextures.TEXTURES.getOrDefault(this.getAnimation(), HauntedSkullTextures.TEXTURES.get(0));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("VisualYRot", this.getYRotVisual());
        compound.putFloat("VisualXRot", this.getXRotVisual());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("VisualYRot")) {
            this.setYRotVisual(compound.getFloat("VisualYRot"));
            this.setXRotVisual(compound.getFloat("VisualXRot"));
            this.setYRot(compound.getFloat("VisualYRot"));
            this.setXRot(compound.getFloat("VisualXRot"));
        }
    }

    public float getYRotVisual() {
        return this.entityData.get(Y_ROT_VISUAL);
    }

    public void setYRotVisual(float yRot) {
        this.entityData.set(Y_ROT_VISUAL, yRot);
    }

    public float getXRotVisual() {
        return this.entityData.get(X_ROT_VISUAL);
    }

    public void setXRotVisual(float xRot) {
        this.entityData.set(X_ROT_VISUAL, xRot);
    }

    @Override
    public void setYRot(float p_146923_) {
        super.setYRot(p_146923_);
        this.setYRotVisual(p_146923_);
    }

    @Override
    public void setXRot(float p_146927_) {
        super.setXRot(p_146927_);
        this.setXRotVisual(p_146927_);
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            Vec3 vector3d = this.getDeltaMovement();
            double d0 = this.getX() + vector3d.x;
            double d1 = this.getY() + vector3d.y;
            double d2 = this.getZ() + vector3d.z;
            if (this.getAnimation() < 16){
                this.setAnimation(this.getAnimation() + 1);
            } else {
                this.setAnimation(9);
            }
            for(int j = 0; j < 2; ++j) {
                this.level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d0 + this.random.nextGaussian() * (double)0.3F, d1 + this.random.nextGaussian() * (double)0.3F, d2 + this.random.nextGaussian() * (double)0.3F, 0.0D, 0.0D, 0.0D);
            }
        } else {
            if (this.isUpgraded()){
                this.level.broadcastEntityEvent(this, (byte) 4);
            } else {
                this.level.broadcastEntityEvent(this, (byte) 5);
            }
        }
    }

    public int getAnimation() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setAnimation(int pType) {
        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public boolean isPowered() {
        return this.isPowered;
    }

    public void setDamage(double damage) {
        this.entityData.set(DATA_DAMAGE, (float) damage);
    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level.isClientSide) {
            Entity target = pResult.getEntity();
            Entity owner = this.getOwner();
            boolean flag;
            float enchantment = this.getExtraDamage();
            int flaming = this.getFiery();
            if (owner instanceof LivingEntity livingentity) {
                flag = target.hurt(this.damageSources().indirectMagic(this, livingentity), this.getDamage() + enchantment);
                if (livingentity instanceof SkullLord){
                    if (target instanceof BoneLord){
                        flag = false;
                    }
                }
                if (flag) {
                    if (target.isAlive()) {
                        this.doEnchantDamageEffects(livingentity, target);
                        if (flaming != 0) {
                            target.setSecondsOnFire(5 * flaming);
                        }
                    } else {
                        livingentity.heal(1.0F);
                    }
                }
            } else {
                target.hurt(this.damageSources().magic(), this.getDamage());
            }
        }
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        this.explode();
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (pEntity == this.getOwner()){
                return false;
            }
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity);
            } else {
                if (MobUtil.areAllies(this.getOwner(), pEntity)){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        if (this.isUpgraded()){
            if (pEntity instanceof AbstractHurtingProjectile){
                return false;
            }
        }
        return super.canHitEntity(pEntity);
    }

    public void explode(){
        if (this.exploding || this.isRemoved()) {
            return;
        }
        this.exploding = true;
        if (!this.level.isClientSide) {
            Entity owner = this.getOwner();
            boolean flaming = this.getFiery() > 0;
            Explosion.BlockInteraction explodeMode = Explosion.BlockInteraction.KEEP;
            boolean damaging;
            if (owner instanceof Player || (owner instanceof IOwned iOwned && iOwned.getMasterOwner() instanceof Player)) {
                damaging = SpellConfig.HauntedSkullGriefing.get();
            } else {
                damaging = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this);
            }
            if (!damaging) {
                flaming = false;
            }
            if (this.isDangerous()) {
                if (this.getOwner() instanceof Player) {
                    explodeMode = damaging ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP;
                } else {
                    explodeMode = damaging ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP;
                }
            }
            LootingExplosion.Mode lootMode = CuriosFinder.hasWanting(owner) ? LootingExplosion.Mode.LOOT : LootingExplosion.Mode.REGULAR;
            ExplosionUtil.lootExplode(this.level, this, this.getX(), this.getY(), this.getZ(), this.getExplosionPower(), flaming, explodeMode, lootMode);
            this.discard();
        }
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean ignoreExplosion(){
        return true;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.exploding || this.isRemoved()) {
            return false;
        } else if (this.isUpgraded()) {
            return false;
        } else {
            this.explode();
            this.markHurt();
            return !this.isInvulnerableTo(source);
        }
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.LARGE_SMOKE;
    }

    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public void handleEntityEvent(byte p_19882_) {
        if (p_19882_ == 4){
            this.isPowered = true;
        } else if (p_19882_ == 5) {
            this.isPowered = false;
        } else {
            super.handleEntityEvent(p_19882_);
        }
    }
}
