package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.DustCloudParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import com.google.common.collect.Maps;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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

import java.util.Map;

public class HellBolt extends WaterHurtingProjectile {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(HellBolt.class, EntityDataSerializers.INT);
    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, Goety.location("textures/entity/projectiles/hell_bolt/bolt_1.png"));
        map.put(1, Goety.location("textures/entity/projectiles/hell_bolt/bolt_2.png"));
        map.put(2, Goety.location("textures/entity/projectiles/hell_bolt/bolt_3.png"));
    });
    public static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(HellBolt.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> RAIN = SynchedEntityData.defineId(HellBolt.class, EntityDataSerializers.BOOLEAN);

    public HellBolt(EntityType<? extends HellBolt> p_i50160_1_, Level p_i50160_2_) {
        super(p_i50160_1_, p_i50160_2_);
    }

    public HellBolt(LivingEntity p_i1771_2_, double p_i1771_3_, double p_i1771_5_, double p_i1771_7_, Level p_i1771_1_) {
        super(ModEntityType.HELL_BOLT.get(), p_i1771_2_, p_i1771_3_, p_i1771_5_, p_i1771_7_, p_i1771_1_);
    }

    public HellBolt(double pX, double pY, double pZ, double pAccelX, double pAccelY, double pAccelZ, Level pWorld) {
        super(ModEntityType.HELL_BOLT.get(), pX, pY, pZ, pAccelX, pAccelY, pAccelZ, pWorld);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DAMAGE, 5.0F);
        this.entityData.define(DATA_TYPE_ID, 0);
        this.entityData.define(RAIN, false);
    }

    public void tick() {
        super.tick();
        if (this.getAnimation() < 2) {
            this.setAnimation(this.getAnimation() + 1);
        } else {
            this.setAnimation(0);
        }
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

    public float getDamage() {
        return this.entityData.get(DATA_DAMAGE);
    }

    public void setDamage(float pDamage) {
        this.entityData.set(DATA_DAMAGE, pDamage);
    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level.isClientSide) {
            Entity entity = pResult.getEntity();
            Entity entity1 = this.getOwner();
            float enchantment = 0;
            float damage = 5.0F;
            if (entity1 instanceof Player player){
                if (WandUtil.enchantedFocus(player)){
                    enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
                }
                damage = SpellConfig.FireballDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
            } else if (entity1 instanceof LivingEntity) {
                damage = this.getDamage();
            }
            entity.hurt(ModDamageSource.hellfire(this, entity1), damage + enchantment);
            if (entity1 instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity)entity1, entity);
            }
        }
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level.isClientSide) {
            if (!this.isRain()) {
                Entity entity = this.getOwner();
                Vec3 vec3 = Vec3.atCenterOf(this.blockPosition());
                if (entity instanceof LivingEntity livingEntity) {
                    if (pResult instanceof BlockHitResult blockHitResult) {
                        BlockPos blockpos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                        if (BlockFinder.canBeReplaced(this.level, blockpos)) {
                            Hellfire hellfire = new Hellfire(this.level, Vec3.atCenterOf(blockpos), livingEntity);
                            vec3 = Vec3.atCenterOf(blockpos);
                            this.level.addFreshEntity(hellfire);
                        }
                    } else if (pResult instanceof EntityHitResult entityHitResult) {
                        Entity entity1 = entityHitResult.getEntity();
                        Hellfire hellfire = new Hellfire(this.level, Vec3.atCenterOf(entity1.blockPosition()), livingEntity);
                        vec3 = Vec3.atCenterOf(entity1.blockPosition());
                        this.level.addFreshEntity(hellfire);
                    }
                }
                if (this.level instanceof ServerLevel serverLevel) {
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), this);
                    ColorUtil colorUtil = new ColorUtil(0xdd9c16);
                    serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 2, 1), vec3.x, BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    DustCloudParticleOption cloudParticleOptions = new DustCloudParticleOption(new Vector3f(Vec3.fromRGB24(0x7a6664)), 1.0F);
                    DustCloudParticleOption cloudParticleOptions2 = new DustCloudParticleOption(new Vector3f(Vec3.fromRGB24(0xeca294)), 1.0F);
                    for (int i = 0; i < 2; ++i) {
                        ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions, vec3.x, this.getY() + 0.25D, vec3.z, 0, 0.14D, 0, 1.0F);
                    }
                    ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions2, vec3.x, this.getY() + 0.25D, vec3.z, 0, 0.14D, 0, 1.0F);
                }
                this.playSound(ModSounds.HELL_BOLT_IMPACT.get(), 1.0F, 1.0F);
            }
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
                if(this.getOwner().isAlliedTo(pEntity) || pEntity.isAlliedTo(this.getOwner())){
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
        pCompound.putFloat("Damage", this.getDamage());
        pCompound.putBoolean("Rain", this.isRain());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setAnimation(pCompound.getInt("Animation"));
        if (pCompound.contains("Damage")) {
            this.setDamage(pCompound.getFloat("Damage"));
        }
        if (pCompound.contains("Rain")) {
            this.setRain(pCompound.getBoolean("Rain"));
        }
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getAnimation(), TEXTURE_BY_TYPE.get(0));
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

    public boolean isRain(){
        return this.entityData.get(RAIN);
    }

    public void setRain(boolean rain){
        this.entityData.set(RAIN, rain);
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
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
