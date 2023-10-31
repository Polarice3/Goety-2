package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class ModDragonFireball extends AbstractHurtingProjectile {
    public ModDragonFireball(EntityType<? extends AbstractHurtingProjectile> p_i50171_1_, Level p_i50171_2_) {
        super(p_i50171_1_, p_i50171_2_);
    }

    public ModDragonFireball(Level pWorld, double pX, double pY, double pZ, double pAccelX, double pAccelY, double pAccelZ) {
        super(ModEntityType.MOD_DRAGON_FIREBALL.get(), pX, pY, pZ, pAccelX, pAccelY, pAccelZ, pWorld);
    }

    public ModDragonFireball(Level p_i46776_1_, LivingEntity p_i46776_2_, double p_i46776_3_, double p_i46776_5_, double p_i46776_7_) {
        super(ModEntityType.MOD_DRAGON_FIREBALL.get(), p_i46776_2_, p_i46776_3_, p_i46776_5_, p_i46776_7_, p_i46776_1_);
    }

    protected void onHit(HitResult p_36913_) {
        super.onHit(p_36913_);
        Entity entity = this.getOwner();
        float radius = 0;
        int duration = 1;
        if (p_36913_.getType() != HitResult.Type.ENTITY || !this.ownedBy(((EntityHitResult)p_36913_).getEntity())) {
            if (!this.level().isClientSide) {
                List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D));
                AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
                if (entity instanceof LivingEntity livingEntity) {
                    if (entity instanceof Player player){
                        if (WandUtil.enchantedFocus(player)){
                            radius = WandUtil.getLevels(ModEnchantments.RADIUS.get(), player);
                            duration = WandUtil.getLevels(ModEnchantments.DURATION.get(), player) + 1;
                        }
                    }
                    areaeffectcloud.setOwner(livingEntity);
                }

                areaeffectcloud.setParticle(ParticleTypes.DRAGON_BREATH);
                areaeffectcloud.setRadius(3.0F + radius);
                areaeffectcloud.setDuration(600 * duration);
                areaeffectcloud.setRadiusPerTick((7.0F - areaeffectcloud.getRadius()) / (float)areaeffectcloud.getDuration());
                areaeffectcloud.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
                if (!list.isEmpty()) {
                    for(LivingEntity livingentity : list) {
                        double d0 = this.distanceToSqr(livingentity);
                        if (d0 < 16.0D) {
                            areaeffectcloud.setPos(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                            break;
                        }
                    }
                }

                this.level().levelEvent(2006, this.blockPosition(), this.isSilent() ? -1 : 1);
                this.level().addFreshEntity(areaeffectcloud);
                this.discard();
            }

        }
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource p_36910_, float p_36911_) {
        return false;
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.DRAGON_BREATH;
    }

    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
