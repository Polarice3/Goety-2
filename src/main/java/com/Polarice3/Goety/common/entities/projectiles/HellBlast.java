package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.DustCloudParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.render.HellblastTextures;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

public class HellBlast extends WaterHurtingProjectile {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(HellBlast.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_FIERY = SynchedEntityData.defineId(HellBlast.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(HellBlast.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(HellBlast.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData.defineId(HellBlast.class, EntityDataSerializers.FLOAT);

    public HellBlast(EntityType<? extends HellBlast> p_i50160_1_, Level p_i50160_2_) {
        super(p_i50160_1_, p_i50160_2_);
    }

    public HellBlast(LivingEntity p_i1771_2_, double p_i1771_3_, double p_i1771_5_, double p_i1771_7_, Level p_i1771_1_) {
        super(ModEntityType.HELL_BLAST.get(), p_i1771_2_, p_i1771_3_, p_i1771_5_, p_i1771_7_, p_i1771_1_);
        this.rotateToMatchMovement();
    }

    public HellBlast(double pX, double pY, double pZ, double pAccelX, double pAccelY, double pAccelZ, Level pWorld) {
        super(ModEntityType.HELL_BLAST.get(), pX, pY, pZ, pAccelX, pAccelY, pAccelZ, pWorld);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DAMAGE, 6.0F);
        this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
        this.entityData.define(DATA_RADIUS, 1.5F);
        this.entityData.define(DATA_TYPE_ID, 0);
        this.entityData.define(DATA_FIERY, 0);
    }

    public void tick() {
        super.tick();
        if (this.getAnimation() < HellblastTextures.TEXTURES.size()){
            this.setAnimation(this.getAnimation() + 1);
        } else {
            this.setAnimation(0);
        }
        if (this.tickCount >= MathHelper.secondsToTicks(10)){
            this.discard();
        }
    }

    @Override
    public void trailParticle() {
        Entity entity = this.getOwner();
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

    public float getDamage() {
        return this.entityData.get(DATA_DAMAGE);
    }

    public void setDamage(float pDamage) {
        this.entityData.set(DATA_DAMAGE, pDamage);
    }

    public float getExtraDamage() {
        return this.entityData.get(DATA_EXTRA_DAMAGE);
    }

    public void setExtraDamage(float extra) {
        this.entityData.set(DATA_EXTRA_DAMAGE, extra);
    }

    public float getRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    public void setRadius(float pRadius) {
        this.entityData.set(DATA_RADIUS, pRadius);
    }

    public int getFiery() {
        return this.entityData.get(DATA_FIERY);
    }

    public void setFiery(int fiery) {
        this.entityData.set(DATA_FIERY, fiery);
    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level.isClientSide) {
            Entity entity = pResult.getEntity();
            Entity entity1 = this.getOwner();
            float damage = this.getDamage();
            float enchantment = this.getExtraDamage();
            int flaming = this.getFiery();
            if (entity1 instanceof Player){
                damage = SpellConfig.LavaballDamage.get().floatValue() * WandUtil.damageMultiply();
            }
            entity.hurt(ModDamageSource.hellfire(this, entity1), damage + enchantment);
            if (entity1 instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity)entity1, entity);
            }
            if (flaming != 0){
                entity.setSecondsOnFire(5 * flaming);
            }
        }
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level.isClientSide) {
            Entity entity = this.getOwner();
            Vec3 vec3 = Vec3.atCenterOf(this.blockPosition());
            if (entity instanceof LivingEntity livingEntity) {
                if (pResult instanceof BlockHitResult blockHitResult){
                    BlockPos blockpos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                    if (BlockFinder.canBeReplaced(this.level, blockpos)) {
                        vec3 = Vec3.atCenterOf(blockpos);
                    }
                } else if (pResult instanceof EntityHitResult entityHitResult){
                    Entity entity1 = entityHitResult.getEntity();
                    vec3 = Vec3.atCenterOf(entity1.blockPosition());
                }
                Hellfire hellfire = new Hellfire(this.level, vec3, livingEntity);
                if (this.level.addFreshEntity(hellfire)) {
                    for (Direction direction : Direction.values()) {
                        if (direction.getAxis().isHorizontal()) {
                            Hellfire hellfire1 = new Hellfire(this.level, Vec3.atCenterOf(hellfire.blockPosition().relative(direction)), livingEntity);
                            this.level.addFreshEntity(hellfire1);
                        }
                    }
                }
            }
            new SpellExplosion(this.level, this.getOwner() != null ? this.getOwner() : this, ModDamageSource.hellfire(this, this.getOwner()), vec3.x, vec3.y, vec3.z, this.getRadius(), 0){
                @Override
                public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage) {
                    super.explodeHurt(target, damageSource, x, y, z, seen, actualDamage);
                    if (damageSource.getDirectEntity() instanceof HellBlast hellBlast){
                        if (hellBlast.getFiery() > 0){
                            entity.setSecondsOnFire(5 * hellBlast.getFiery());
                        }
                    }
                }
            };
            if (this.level instanceof ServerLevel serverLevel){
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), this);
                ColorUtil colorUtil = new ColorUtil(0xdd9c16);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 4, 1), vec3.x, BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 5, 1), vec3.x, BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                DustCloudParticleOption cloudParticleOptions = new DustCloudParticleOption(new Vector3f(Vec3.fromRGB24(0x7a6664).toVector3f()), 1.0F);
                DustCloudParticleOption cloudParticleOptions2 = new DustCloudParticleOption(new Vector3f(Vec3.fromRGB24(0xeca294).toVector3f()), 1.0F);
                for (int i = 0; i < 2; ++i) {
                    ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions, vec3.x, this.getY() + 0.25D, vec3.z, 0, 0.14D, 0, this.getRadius() * 2);
                }
                ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions2, vec3.x, this.getY() + 0.25D, vec3.z, 0, 0.14D, 0, this.getRadius() * 2);
            }
            this.playSound(SoundEvents.GENERIC_EXPLODE, 4.0F, 1.0F);
            this.playSound(ModSounds.HELL_BLAST_IMPACT.get(), 4.0F, 1.0F);
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
    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.NONE.get();
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Animation", this.getAnimation());
        pCompound.putInt("Fiery", this.getFiery());
        pCompound.putFloat("Radius", this.getRadius());
        pCompound.putFloat("Damage", this.getDamage());
        pCompound.putFloat("ExtraDamage", this.getExtraDamage());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setAnimation(pCompound.getInt("Animation"));
        if (pCompound.contains("Fiery")) {
            this.setFiery(pCompound.getInt("Fiery"));
        }
        if (pCompound.contains("Radius")) {
            this.setRadius(pCompound.getFloat("Radius"));
        }
        if (pCompound.contains("Damage")) {
            this.setDamage(pCompound.getFloat("Damage"));
        }
        if (pCompound.contains("ExtraDamage", 99)) {
            this.setExtraDamage(pCompound.getFloat("ExtraDamage"));
        }
    }

    public ResourceLocation getResourceLocation() {
        return HellblastTextures.TEXTURES.getOrDefault(this.getAnimation(), HellblastTextures.TEXTURES.get(0));
    }

    public void rotateToMatchMovement() {
        this.updateRotation();
    }

    public int getAnimation() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setAnimation(int pType) {
        this.entityData.set(DATA_TYPE_ID, pType);
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

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
