package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class Lavaball extends LargeFireball {
    private static final EntityDataAccessor<Boolean> DATA_UPGRADED = SynchedEntityData.defineId(Lavaball.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(Lavaball.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> DATA_EXPLOSION = SynchedEntityData.defineId(Lavaball.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(Lavaball.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData.defineId(Lavaball.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> DATA_FIERY = SynchedEntityData.defineId(Lavaball.class, EntityDataSerializers.INT);

    public Lavaball(EntityType<? extends Lavaball> p_i50163_1_, Level p_i50163_2_) {
        super(p_i50163_1_, p_i50163_2_);
    }

    public Lavaball(Level pWorld, double pX, double pY, double pZ, double pAccelX, double pAccelY, double pAccelZ) {
        this(ModEntityType.LAVABALL.get(), pWorld);
        this.moveTo(pX, pY, pZ, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        double d0 = Math.sqrt(pAccelX * pAccelX + pAccelY * pAccelY + pAccelZ * pAccelZ);
        if (d0 != 0.0D) {
            this.xPower = pAccelX / d0 * 0.1D;
            this.yPower = pAccelY / d0 * 0.1D;
            this.zPower = pAccelZ / d0 * 0.1D;
        }
    }

    public Lavaball(Level p_i1769_1_, LivingEntity p_i1769_2_, double p_i1769_3_, double p_i1769_5_, double p_i1769_7_) {
        super(p_i1769_1_, p_i1769_2_, p_i1769_3_, p_i1769_5_, p_i1769_7_, 0);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityType.LAVABALL.get();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_UPGRADED, false);
        this.entityData.define(DATA_DANGEROUS, true);
        this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
        this.entityData.define(DATA_FIERY, 0);
        this.entityData.define(DATA_EXPLOSION, 1.0F);
        this.entityData.define(DATA_DAMAGE, 6.0F);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("ModExplosionPower", this.getExplosionPower());
        pCompound.putFloat("Damage", this.getDamage());
        pCompound.putFloat("ExtraDamage", this.getExtraDamage());
        pCompound.putInt("Fiery", this.getFiery());
        pCompound.putBoolean("Dangerous", this.isDangerous());
        pCompound.putBoolean("Upgraded", this.isUpgraded());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ModExplosionPower", 99)) {
            this.setExplosionPower(pCompound.getFloat("ModExplosionPower"));
        }
        if (pCompound.contains("Damage", 99)) {
            this.setDamage(pCompound.getFloat("Damage"));
        }
        if (pCompound.contains("ExtraDamage", 99)) {
            this.setExtraDamage(pCompound.getFloat("ExtraDamage"));
        }
        if (pCompound.contains("Fiery")) {
            this.setFiery(pCompound.getInt("Fiery"));
        }
        if (pCompound.contains("Dangerous")) {
            this.setDangerous(pCompound.getBoolean("Dangerous"));
        }
        if (pCompound.contains("Upgraded")) {
            this.setUpgraded(pCompound.getBoolean("Upgraded"));
        }

    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    public void setDangerous(boolean pDangerous) {
        this.entityData.set(DATA_DANGEROUS, pDangerous);
    }

    public float getExplosionPower() {
        return this.entityData.get(DATA_EXPLOSION);
    }

    public void setExplosionPower(float pExplosionPower){
        this.entityData.set(DATA_EXPLOSION, pExplosionPower);
    }

    public float getDamage() {
        return this.entityData.get(DATA_DAMAGE);
    }

    public void setDamage(float pDamage) {
        this.entityData.set(DATA_DAMAGE, pDamage);
    }

    public float getExtraDamage() {
        return this.entityData.get(DATA_EXTRA_DAMAGE);
    }

    public void setExtraDamage(float extra) {
        this.entityData.set(DATA_EXTRA_DAMAGE, extra);
    }

    public int getFiery() {
        return this.entityData.get(DATA_FIERY);
    }

    public void setFiery(int fiery) {
        this.entityData.set(DATA_FIERY, fiery);
    }

    public boolean isUpgraded() {
        return this.entityData.get(DATA_UPGRADED);
    }

    public void setUpgraded(boolean upgraded) {
        this.entityData.set(DATA_UPGRADED, upgraded);
    }

    protected void onHit(HitResult pResult) {
        HitResult.Type hitresult$type = pResult.getType();
        if (hitresult$type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult)pResult);
            this.level.gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), GameEvent.Context.of(this, (BlockState)null));
        } else if (hitresult$type == HitResult.Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult)pResult;
            this.onHitBlock(blockhitresult);
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level.gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, this.level.getBlockState(blockpos)));
        }
        if (!this.level.isClientSide) {
            Entity owner = this.getOwner();
            boolean flag = this.isDangerous();
            if (owner instanceof Player || (owner instanceof IOwned owned && owned.getTrueOwner() instanceof Player)) {
                if (!SpellConfig.LavaballGriefing.get()) {
                    flag = false;
                }
            }
            LootingExplosion.Mode lootMode = CuriosFinder.hasWanting(owner) ? LootingExplosion.Mode.LOOT : LootingExplosion.Mode.REGULAR;
            ExplosionUtil.lootExplode(this.level, owner, this.getX(), this.getY(), this.getZ(), this.getExplosionPower(), flag, flag ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE, lootMode);
            this.discard();
        }

    }

    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level.isClientSide) {
            Entity entity = pResult.getEntity();
            Entity entity1 = this.getOwner();
            float damage = 6.0F;
            float enchantment = this.getExtraDamage();
            int flaming = this.getFiery();
            if (entity1 instanceof Player){
                damage = SpellConfig.LavaballDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
            } else if (entity1 instanceof LivingEntity) {
                damage = this.getDamage();
            }
            DamageSource damageSource = DamageSource.fireball(this, this.getOwner());
            if (this.getOwner() instanceof LivingEntity livingEntity){
                if (CuriosFinder.hasNetherRobe(livingEntity)){
                    damageSource = ModDamageSource.magicFireball(this, this.getOwner());
                }
                if (livingEntity instanceof OwnableEntity ownable && ownable.getOwner() instanceof LivingEntity livingEntity1){
                    if (CuriosFinder.hasNetherRobe(livingEntity1)){
                        damageSource = ModDamageSource.magicFireball(this, this.getOwner());
                    }
                }
            }
            entity.hurt(damageSource, damage + enchantment);

            if (flaming != 0){
                entity.setSecondsOnFire(5 * flaming);
            }
            if (entity1 instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity)entity1, entity);
            }
        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() instanceof IOwned owned){
            if (pEntity instanceof IOwned owned1){
                if (owned.getTrueOwner() == owned1.getTrueOwner()){
                    return false;
                }
            }
            if (owned.getTrueOwner() == pEntity){
                return false;
            }
        }
        if (MobUtil.areAllies(this.getOwner(), pEntity)){
            return false;
        }
        if (this.isUpgraded()){
            if (pEntity instanceof AbstractHurtingProjectile){
                return false;
            }
        }
        return super.canHitEntity(pEntity);
    }

    @Override
    public boolean ignoreExplosion() {
        return this.isUpgraded();
    }

    public boolean hurt(DamageSource p_36839_, float p_36840_) {
        if (this.isUpgraded()){
            return false;
        } else {
            return super.hurt(p_36839_, p_36840_);
        }
    }
}
