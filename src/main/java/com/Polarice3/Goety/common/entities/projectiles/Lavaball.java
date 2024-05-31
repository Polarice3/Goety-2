package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class Lavaball extends ExplosiveProjectile {

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
            boolean flag = this.isDangerous();
            if (owner instanceof Player || (owner instanceof IOwned owned && owned.getTrueOwner() instanceof Player)) {
                if (!SpellConfig.LavaballGriefing.get()) {
                    flag = false;
                }
            }
            LootingExplosion.Mode lootMode = CuriosFinder.hasWanting(owner) ? LootingExplosion.Mode.LOOT : LootingExplosion.Mode.REGULAR;
            ExplosionUtil.lootExplode(this.level, owner, this.getX(), this.getY(), this.getZ(), this.getExplosionPower(), flag, flag ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, lootMode);
            this.discard();
        }

    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
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
