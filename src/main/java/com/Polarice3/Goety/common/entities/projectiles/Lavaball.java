package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class Lavaball extends ExplosiveProjectile {
    private static final EntityDataAccessor<Boolean> DATA_UPGRADED = SynchedEntityData.defineId(Lavaball.class, EntityDataSerializers.BOOLEAN);
    public float explosionPower = 1.0F;

    public Lavaball(EntityType<? extends Lavaball> p_i50163_1_, Level p_i50163_2_) {
        super(p_i50163_1_, p_i50163_2_);
    }

    public Lavaball(Level pWorld, double pX, double pY, double pZ, double pAccelX, double pAccelY, double pAccelZ) {
        super(ModEntityType.LAVABALL.get(), pX, pY, pZ, pAccelX, pAccelY, pAccelZ, pWorld);
    }

    public Lavaball(Level p_i1769_1_, LivingEntity p_i1769_2_, double p_i1769_3_, double p_i1769_5_, double p_i1769_7_) {
        super(ModEntityType.LAVABALL.get(), p_i1769_2_, p_i1769_3_, p_i1769_5_, p_i1769_7_, p_i1769_1_);
    }

    public boolean defaultDangerous(){
        return true;
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level.isClientSide) {
            Entity owner = this.getOwner();
            float enchantment = 0;
            boolean flag = this.isDangerous();
            if (owner instanceof Player player){
                if (WandUtil.enchantedFocus(player)){
                    enchantment = WandUtil.getLevels(ModEnchantments.RADIUS.get(), player)/2.5F;
                }
                if (!SpellConfig.LavaballGriefing.get()){
                    flag = false;
                }
            }
            this.level.explode(owner, this.getX(), this.getY(), this.getZ(), this.explosionPower + enchantment, flag, flag ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE);
            this.discard();
        }

    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level.isClientSide) {
            Entity entity = pResult.getEntity();
            Entity entity1 = this.getOwner();
            float damage = 6.0F;
            float enchantment = 0;
            int flaming = 0;
            if (entity1 instanceof Player player){
                if (WandUtil.enchantedFocus(player)){
                    enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
                    flaming = WandUtil.getLevels(ModEnchantments.BURNING.get(), player);
                }
                damage = SpellConfig.LavaballDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
            }
            entity.hurt(ModDamageSource.modFireball(this.getOwner(), this.level), damage + enchantment);

            if (flaming != 0){
                entity.setSecondsOnFire(5 * flaming);
            }
            if (entity1 instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity)entity1, entity);
            }
        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() instanceof Owned){
            Owned owner = (Owned) this.getOwner();
            if (pEntity instanceof Owned){
                Owned entity = (Owned) pEntity;
                if (owner.getTrueOwner() == entity.getTrueOwner()){
                    return false;
                }
            }
            if (owner.getTrueOwner() == pEntity){
                return false;
            }
        }
        if (this.isUpgraded()){
            if (pEntity instanceof AbstractHurtingProjectile){
                return false;
            }
        }
        return super.canHitEntity(pEntity);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_UPGRADED, false);
    }

    @Override
    public void setExplosionPower(float pExplosionPower) {
        this.explosionPower = pExplosionPower;
    }

    @Override
    public float getExplosionPower() {
        return this.explosionPower;
    }

    @Override
    public boolean ignoreExplosion() {
        return this.isUpgraded();
    }

    public boolean isUpgraded() {
        return this.entityData.get(DATA_UPGRADED);
    }

    public void setUpgraded(boolean pInvulnerable) {
        this.entityData.set(DATA_UPGRADED, pInvulnerable);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
