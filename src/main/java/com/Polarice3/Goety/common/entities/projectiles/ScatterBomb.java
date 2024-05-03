package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.SpellExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ScatterBomb extends Projectile {

    public ScatterBomb(EntityType<? extends Projectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public ScatterBomb(double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        this(ModEntityType.SCATTER_BOMB.get(), p_37460_);
        this.setPos(p_37457_, p_37458_, p_37459_);
    }

    public ScatterBomb(LivingEntity p_37463_, Level p_37464_) {
        this(p_37463_.getX(), p_37463_.getEyeY() - (double)0.1F, p_37463_.getZ(), p_37464_);
        this.setOwner(p_37463_);
    }

    public boolean shouldRenderAtSqrDistance(double p_37470_) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 *= 64.0D;
        return p_37470_ < d0 * d0;
    }

    @Override
    protected void defineSynchedData() {
    }

    public void tick() {
        super.tick();
        HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        boolean flag = false;
        if (hitresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult)hitresult).getBlockPos();
            BlockState blockstate = this.level.getBlockState(blockpos);
            if (blockstate.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal(blockpos);
                flag = true;
            } else if (blockstate.is(Blocks.END_GATEWAY)) {
                BlockEntity blockentity = this.level.getBlockEntity(blockpos);
                if (blockentity instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
                    TheEndGatewayBlockEntity.teleportEntity(this.level, blockpos, blockstate, this, (TheEndGatewayBlockEntity)blockentity);
                }

                flag = true;
            }
        }

        if (hitresult.getType() != HitResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }

        this.checkInsideBlocks();
        Vec3 vec3 = this.getDeltaMovement();
        double d2 = this.getX() + vec3.x;
        double d0 = this.getY() + vec3.y;
        double d1 = this.getZ() + vec3.z;
        this.updateRotation();

        this.setDeltaMovement(vec3.scale(0.95F));
        if (!this.isNoGravity()) {
            Vec3 vec31 = this.getDeltaMovement();
            this.setDeltaMovement(vec31.x, vec31.y - 0.03D, vec31.z);
        }

        this.setPos(d2, d0, d1);
    }

    protected void onHit(HitResult p_37406_) {
        super.onHit(p_37406_);
        if (!this.level.isClientSide) {
            this.playSound(SoundEvents.GENERIC_EXPLODE, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F);
            float damage = 12.5F;
            if (this.getOwner() instanceof Mob mob && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null){
                damage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2.0F;
            }
            new SpellExplosion(this.level, this, DamageSource.explosion(this.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null), this.getX(), this.getY(), this.getZ(), 4.5F, damage){
                @Override
                public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage) {
                    super.explodeHurt(target, damageSource, x, y, z, seen, actualDamage);
                    target.invulnerableTime = 15;
                }
            };
            if (this.level instanceof ServerLevel serverLevel) {
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), this);
                ServerParticleUtil.addAuraParticles(serverLevel, ModParticleTypes.BIG_FIRE_GROUND.get(), this, 2.0F);
                ColorUtil colorUtil = new ColorUtil(0xdd9c16);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 4.5F, 1), this.getX(), BlockFinder.moveDownToGround(this), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            this.discard();
        }
    }
}
