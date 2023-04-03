package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class SoulBullet extends AbstractHurtingProjectile {
    public SoulBullet(EntityType<? extends SoulBullet> p_i50147_1_, Level p_i50147_2_) {
        super(p_i50147_1_, p_i50147_2_);
    }

    public SoulBullet(Level p_i1794_1_, LivingEntity p_i1794_2_, double p_i1794_3_, double p_i1794_5_, double p_i1794_7_) {
        super(ModEntityType.SOUL_BULLET.get(), p_i1794_2_, p_i1794_3_, p_i1794_5_, p_i1794_7_, p_i1794_1_);
    }

        public SoulBullet(Level p_i1795_1_, double p_i1795_2_, double p_i1795_4_, double p_i1795_6_, double p_i1795_8_, double p_i1795_10_, double p_i1795_12_) {
        super(ModEntityType.SOUL_BULLET.get(), p_i1795_2_, p_i1795_4_, p_i1795_6_, p_i1795_8_, p_i1795_10_, p_i1795_12_, p_i1795_1_);
    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        Entity entity = pResult.getEntity();
        Entity entity1 = this.getOwner();
        LivingEntity livingentity = entity1 instanceof LivingEntity ? (LivingEntity)entity1 : null;
        if (livingentity != null && livingentity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            boolean flag = entity.hurt(DamageSource.indirectMagic(this, livingentity).setProjectile(), (float) livingentity.getAttributeValue(Attributes.ATTACK_DAMAGE));
            if (flag) {
                this.doEnchantDamageEffects(livingentity, entity);
            }
        } else {
            entity.hurt(DamageSource.MAGIC.setProjectile(), 4.0F);
        }

    }

    protected void onHitBlock(BlockHitResult p_230299_1_) {
        super.onHitBlock(p_230299_1_);
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        this.discard();
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null && this.getOwner().isAlliedTo(pEntity)){
            return false;
        } else {
            return super.canHitEntity(pEntity);
        }
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    protected boolean shouldBurn() {
        return false;
    }

    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.BULLET_EFFECT.get();
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
