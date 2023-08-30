package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class DeathArrow extends Arrow {

    public DeathArrow(EntityType<? extends Arrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
    }

    public DeathArrow(Level p_36866_, LivingEntity p_36867_) {
        super(p_36866_, p_36867_);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityType.DEATH_ARROW.get();
    }

    protected void doPostHurtEffects(LivingEntity p_36873_) {
        super.doPostHurtEffects(p_36873_);

        if (p_36873_ instanceof Apostle && p_36873_.level.dimension() == Level.NETHER) {
            float voidDamage = p_36873_.getMaxHealth() * 0.05F;

            if (p_36873_.getHealth() > voidDamage + 1.0F) {
                p_36873_.heal(-voidDamage);
            }
        }
    }

    @Override
    protected void onHit(HitResult p_37260_) {
        super.onHit(p_37260_);
        if (!this.level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level;
            if (!this.inGround) {
                for (int p = 0; p < 32; ++p) {
                    double d0 = (double) this.getX() + this.level.random.nextDouble();
                    double d1 = (double) this.getY() + this.level.random.nextDouble();
                    double d2 = (double) this.getZ() + this.level.random.nextDouble();
                    serverLevel.sendParticles(ModParticleTypes.TOTEM_EFFECT.get(), d0, d1, d2, 0, 0.45, 0.45, 0.45, 0.5F);
                }
            }
        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null && (this.getOwner().isAlliedTo(pEntity) || pEntity.isAlliedTo(this.getOwner()))){
            return false;
        } else {
            return super.canHitEntity(pEntity);
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
