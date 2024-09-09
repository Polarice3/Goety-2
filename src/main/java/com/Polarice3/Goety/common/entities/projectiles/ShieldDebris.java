package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShieldDebris extends ModFireball{
    public ShieldDebris(EntityType<? extends ModFireball> p_i50160_1_, Level p_i50160_2_) {
        super(p_i50160_1_, p_i50160_2_);
    }

    public ShieldDebris(Level pWorld, LivingEntity pEntity, double pAccelX, double pAccelY, double pAccelZ) {
        super(pWorld, pEntity, pAccelX, pAccelY, pAccelZ);
    }

    public ShieldDebris(Level pWorld, double pX, double pY, double pZ, double pAccelX, double pAccelY, double pAccelZ) {
        super(pWorld, pX, pY, pZ, pAccelX, pAccelY, pAccelZ);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityType.SHIELD_DEBRIS.get();
    }

    protected boolean shouldBurn() {
        return false;
    }

    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.NONE.get();
    }

    public void tick() {
        ProjectileUtil.rotateTowardsMovement(this, 1.0F);
        super.tick();
        Entity entity = this.getOwner();
        if (this.tickCount >= MathHelper.secondsToTicks(10)){
            this.discard();
        }
        if (this.level.isClientSide || (entity == null || !entity.isRemoved()) && this.level.hasChunkAt(this.blockPosition())) {
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() - vec3.x;
            double d1 = this.getY() - vec3.y;
            double d2 = this.getZ() - vec3.z;
            if (this.level.random.nextFloat() <= 0.05F){
                this.level.addParticle(ModParticleTypes.BIG_FIRE.get(), d0, d1 + 0.15D, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        this.playSound(ModSounds.SHIELD_DEBRIS_IMPACT.get());
        if (this.level instanceof ServerLevel serverLevel){
            ParticleOptions particleOptions = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.BLAZE_ROD));
            for(int i = 0; i < 10; ++i) {
                double d0 = serverLevel.random.nextGaussian() * 0.02D;
                double d1 = serverLevel.random.nextGaussian() * 0.02D;
                double d2 = serverLevel.random.nextGaussian() * 0.02D;
                double d3 = hitResult.getLocation().x + (2.0D * this.random.nextDouble() - 1.0D);
                double d4 = this.random.nextDouble();
                double d5 = hitResult.getLocation().z + (2.0D * this.random.nextDouble() - 1.0D);
                serverLevel.sendParticles(particleOptions, d3, d4, d5, 0, d0, d1, d2, 0.5F);
            }
        }
    }
}
