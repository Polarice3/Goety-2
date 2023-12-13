package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.client.render.HauntedSkullTextures;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.BoneLord;
import com.Polarice3.Goety.common.entities.hostile.SkullLord;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class HauntedSkullProjectile extends ExplosiveProjectile{
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(HauntedSkullProjectile.class, EntityDataSerializers.INT);
    public float explosionPower = 1.0F;
    private int burning = 0;
    public float damage = SpellConfig.HauntedSkullDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
    public boolean upgraded;
    public boolean isPowered;

    public HauntedSkullProjectile(EntityType<? extends ExplosiveProjectile> p_i50166_1_, Level p_i50166_2_) {
        super(p_i50166_1_, p_i50166_2_);
    }

    public HauntedSkullProjectile(double p_i50167_2_, double p_i50167_4_, double p_i50167_6_, double p_i50167_8_, double p_i50167_10_, double p_i50167_12_, Level p_i50167_14_) {
        super(ModEntityType.HAUNTED_SKULL_SHOT.get(), p_i50167_2_, p_i50167_4_, p_i50167_6_, p_i50167_8_, p_i50167_10_, p_i50167_12_, p_i50167_14_);
    }

    public HauntedSkullProjectile(LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_, Level p_i50168_9_) {
        super(ModEntityType.HAUNTED_SKULL_SHOT.get(), p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 9);
    }

    public ResourceLocation getResourceLocation() {
        return HauntedSkullTextures.TEXTURES.getOrDefault(this.getAnimation(), HauntedSkullTextures.TEXTURES.get(0));
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
            if (this.upgraded){
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

    public void setBurning(int burning) {
        this.burning = burning;
    }

    public void setDamage(float damage){
        this.damage = damage;
    }

    public void setUpgraded(boolean upgraded) {
        this.upgraded = upgraded;
    }

    public boolean isPowered() {
        return this.isPowered;
    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level.isClientSide) {
            Entity target = pResult.getEntity();
            Entity owner = this.getOwner();
            boolean flag;
            float enchantment = 0;
            int flaming = this.burning;
            if (owner instanceof LivingEntity livingentity) {
                if (livingentity instanceof Mob mob){
                    if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null){
                        this.damage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    }
                }
                flag = target.hurt(DamageSource.indirectMagic(this, livingentity), this.damage + enchantment);
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
                target.hurt(DamageSource.MAGIC, this.damage);
            }
        }
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level.isClientSide) {
            Entity owner = this.getOwner();
            float enchantment = 0;
            boolean flaming = false;
            boolean loot = false;
            if (owner instanceof Player player) {
                if (WandUtil.enchantedFocus(player)) {
                    enchantment = WandUtil.getLevels(ModEnchantments.RADIUS.get(), player) / 2.5F;
                    if (WandUtil.getLevels(ModEnchantments.BURNING.get(), player) > 0) {
                        flaming = true;
                    }
                }
                if (CuriosFinder.findRing(player).getItem() == ModItems.RING_OF_WANT.get()) {
                    if (CuriosFinder.findRing(player).isEnchanted()) {
                        float wanting = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.WANTING.get(), CuriosFinder.findRing(player));
                        if (wanting > 0) {
                            loot = true;
                        }
                    }
                }
            }
            Explosion.BlockInteraction explodeMode = Explosion.BlockInteraction.NONE;
            if (this.isDangerous()) {
                if (this.getOwner() instanceof Player) {
                    explodeMode = SpellConfig.HauntedSkullGriefing.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
                } else {
                    explodeMode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
                }
            }
            LootingExplosion.Mode lootMode = loot ? LootingExplosion.Mode.LOOT : LootingExplosion.Mode.REGULAR;
            ExplosionUtil.lootExplode(this.level, this, this.getX(), this.getY(), this.getZ(), this.explosionPower + enchantment, flaming, explodeMode, lootMode);
            this.discard();
        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (pEntity == this.getOwner()){
                return false;
            }
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity);
            } else {
                if(this.getOwner().isAlliedTo(pEntity) || pEntity.isAlliedTo(this.getOwner())){
                    return false;
                }
                if (pEntity instanceof Owned owned0 && this.getOwner() instanceof Owned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        if (this.upgraded){
            if (pEntity instanceof AbstractHurtingProjectile){
                return false;
            }
        }
        return super.canHitEntity(pEntity);
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
        return false;
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.LARGE_SMOKE;
    }

    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public void setExplosionPower(float pExplosionPower) {
        this.explosionPower = pExplosionPower;
    }

    @Override
    public float getExplosionPower() {
        return this.explosionPower;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Burning", this.burning);
        pCompound.putBoolean("Upgraded", this.upgraded);
        pCompound.putFloat("Damage", this.damage);
        pCompound.putFloat("ExplosionPower", this.explosionPower);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Burning")){
            this.setBurning(pCompound.getInt("Burning"));
        }
        if (pCompound.contains("Upgraded")){
            this.setUpgraded(pCompound.getBoolean("Upgraded"));
        }
        if (pCompound.contains("Damage")) {
            this.setDamage(pCompound.getFloat("Damage"));
        }
        if (pCompound.contains("ExplosionPower")) {
            this.setExplosionPower(pCompound.getFloat("ExplosionPower"));
        }
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

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
