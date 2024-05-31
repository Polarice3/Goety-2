package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.DustCloudParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.MagmaCubeServant;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class MagmaBomb extends SpellThrowableProjectile {
    public float explosionPower = 3.0F;
    public int duration = 0;

    public MagmaBomb(EntityType<? extends SpellThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public MagmaBomb(double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(ModEntityType.MAGMA_BOMB.get(), p_37457_, p_37458_, p_37459_, p_37460_);
    }

    public MagmaBomb(LivingEntity pOwner, Level p_37464_) {
        super(ModEntityType.MAGMA_BOMB.get(), pOwner, p_37464_);
    }

    public void setExplosionPower(float pExplosionPower) {
        this.explosionPower = pExplosionPower;
    }

    public float getExplosionPower() {
        return this.explosionPower;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return this.duration;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("ExplosionPower", this.getExplosionPower());
        pCompound.putInt("Duration",this.getDuration());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ExplosionPower", 99)) {
            this.setExplosionPower(pCompound.getFloat("ExplosionPower"));
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
        this.level.addParticle(ParticleTypes.LARGE_SMOKE, d0 + level.random.nextDouble()/2, d1 + 0.5D, d2 + level.random.nextDouble()/2, 0.0D, 0.0D, 0.0D);
        this.level.addParticle(ModParticleTypes.BIG_FIRE.get(), d0 + level.random.nextDouble()/2, d1 + 0.5D, d2 + level.random.nextDouble()/2, 0.0D, 0.0D, 0.0D);
    }

    public void explode(HitResult pResult){
        if (!this.level.isClientSide) {
            Vec3 vec3 = Vec3.atCenterOf(this.blockPosition());
            if (pResult instanceof BlockHitResult blockHitResult){
                BlockPos blockpos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                if (BlockFinder.canBeReplaced(this.level, blockpos)) {
                    vec3 = Vec3.atCenterOf(blockpos);
                }
            } else if (pResult instanceof EntityHitResult entityHitResult){
                Entity entity1 = entityHitResult.getEntity();
                vec3 = Vec3.atCenterOf(entity1.blockPosition());
            }
            MobUtil.explosionDamage(this.level, this.getOwner() != null ? this.getOwner() : this, DamageSource.explosion(this.getOwner() != null ? this.getOwner() : null), vec3.x, vec3.y, vec3.z, this.explosionPower, 0);
            if (this.level instanceof ServerLevel serverLevel){
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), this);
                ColorUtil colorUtil = new ColorUtil(0xdd9c16);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, this.explosionPower + 1.0F, 1), vec3.x, BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_FIRE_GROUND.get(), vec3.x, this.getY() + 0.25D, vec3.z, 0, 0, 0, this.explosionPower);
                DustCloudParticleOption cloudParticleOptions = new DustCloudParticleOption(new Vector3f(Vec3.fromRGB24(0x7a6664)), 1.0F);
                DustCloudParticleOption cloudParticleOptions2 = new DustCloudParticleOption(new Vector3f(Vec3.fromRGB24(0xeca294)), 1.0F);
                for (int i = 0; i < 2; ++i) {
                    ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions, vec3.x, this.getY() + 0.25D, vec3.z, 0, 0.14D, 0, this.explosionPower / 2.0F);
                }
                ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions2, vec3.x, this.getY() + 0.25D, vec3.z, 0, 0.14D, 0, this.explosionPower / 2.0F);
            }
            this.playSound(SoundEvents.GENERIC_EXPLODE, 4.0F, 1.0F);
            for (int i1 = 0; i1 < 2; i1++) {
                MagmaCubeServant magmaCube = ModEntityType.MAGMA_CUBE_SERVANT.get().create(this.level);
                if (magmaCube != null) {
                    if (this.getOwner() != null) {
                        magmaCube.setTrueOwner(this.getOwner());
                    } else {
                        magmaCube.setHostile(true);
                    }
                    magmaCube.finalizeSpawn((ServerLevelAccessor) this.level, this.level.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    if (this.isStaff()){
                        magmaCube.setSize(4, true);
                    } else {
                        magmaCube.setSize(2, true);
                    }
                    magmaCube.moveTo(this.position());
                    magmaCube.setLimitedLife(MobUtil.getSummonLifespan(this.level) * (this.getDuration() + 1));
                    this.level.addFreshEntity(magmaCube);
                }
            }
            this.discard();
        }
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        this.explode(pResult);
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
