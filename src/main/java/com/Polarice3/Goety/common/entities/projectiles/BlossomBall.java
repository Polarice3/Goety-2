package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class BlossomBall extends SpellThrowableProjectile{
    public float extraDamage = 0.0F;
    public double radius = 3.0D;
    public int duration = 0;

    public BlossomBall(EntityType<? extends SpellThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public BlossomBall(LivingEntity pOwner, Level p_37464_) {
        super(ModEntityType.BLOSSOM_BALL.get(), pOwner, p_37464_);
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return this.duration;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("ExtraDamage", this.extraDamage);
        pCompound.putDouble("Radius",this.getRadius());
        pCompound.putInt("Duration",this.getDuration());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ExtraDamage")){
            this.extraDamage = pCompound.getInt("ExtraDamage");
        }
        if (pCompound.contains("Radius")){
            this.setRadius(pCompound.getDouble("Radius"));
        }
        if (pCompound.contains("Duration")){
            this.setDuration(pCompound.getInt("Duration"));
        }
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vector3d = this.getDeltaMovement();
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        this.level.addParticle(ParticleTypes.DRAGON_BREATH, d0 + level.random.nextDouble()/2, d1 + 0.5D, d2 + level.random.nextDouble()/2, 0.0D, 0.0D, 0.0D);
    }

    public void explode(HitResult pResult){
        if (!this.level.isClientSide) {
            this.playSound(ModSounds.QUICK_GROWING_VINE_BURROW.get(), 2.0F, 0.75F);
            if (this.getOwner() != null) {
                Vec3 vec3 = Vec3.atCenterOf(this.blockPosition());
                if (pResult instanceof BlockHitResult blockHitResult) {
                    BlockPos blockpos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                    if (BlockFinder.canBeReplaced(this.level, blockpos)) {
                        vec3 = Vec3.atCenterOf(blockpos);
                    }
                } else if (pResult instanceof EntityHitResult entityHitResult) {
                    Entity entity1 = entityHitResult.getEntity();
                    vec3 = Vec3.atCenterOf(entity1.blockPosition());
                }
                List<Vec3> circlePoints = BlockFinder.buildBlockCircle(this.getRadius());
                for (Vec3 point : circlePoints) {
                    Vec3 vec31 = vec3.add(point);
                    BlossomThorn blossomThorn = new BlossomThorn(this.level, vec31.x, vec31.y, vec31.z, 30, this.getOwner());
                    MobUtil.moveDownToGround(blossomThorn);
                    blossomThorn.setDuration(this.getDuration());
                    blossomThorn.setExtraDamage(this.extraDamage);
                    if (this.level instanceof ServerLevel serverLevel){
                        serverLevel.sendParticles(ModParticleTypes.BLOSSOM_THORN_INDICATOR.get(), blossomThorn.position().x, blossomThorn.position().y + 0.1F, blossomThorn.position().z, 1, 0.0F, 0.0F, 0.0F, 0.0F);
                    }
                    this.level.addFreshEntity(blossomThorn);
                }
            }
            this.discard();
        }
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        this.explode(pResult);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (pResult.getEntity() instanceof LivingEntity target) {
            if (this.getOwner() != null) {
                float baseDamage = SpellConfig.BlossomDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
                if (target.hurt(DamageSource.thorns(this.getOwner()), baseDamage)) {
                    MobEffect effect = MobEffects.POISON;
                    if (CuriosFinder.hasWildRobe(this.getOwner())) {
                        effect = GoetyEffects.ACID_VENOM.get();
                    }
                    target.addEffect(new MobEffectInstance(effect, 140 + MathHelper.secondsToTicks(this.duration)), this);
                }
            }
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
                if (MobUtil.areAllies(this.getOwner(), pEntity)){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return super.canHitEntity(pEntity);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
