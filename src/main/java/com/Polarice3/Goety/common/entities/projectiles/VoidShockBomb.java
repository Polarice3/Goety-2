package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SpellExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class VoidShockBomb extends SpellThrowableProjectile {
    public double prevDeltaMovementX, prevDeltaMovementY, prevDeltaMovementZ;
    public int growTick = 0;
    public float size = 1.0F;
    public float alpha = 1.0F;
    public float baseDamage = 6.0F;
    private Vec3[] trailPositions = new Vec3[64];
    private int trailPointer = -1;

    public VoidShockBomb(EntityType<? extends VoidShockBomb> type, Level world) {
        super(type, world);
    }

    public VoidShockBomb(LivingEntity thrower, Level world) {
        super(ModEntityType.VOID_SHOCK_BOMB.get(), thrower, world);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("GrowTicks")){
            this.growTick = pCompound.getInt("GrowTicks");
        }
        if (pCompound.contains("BaseDamage")){
            this.baseDamage = pCompound.getFloat("BaseDamage");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("GrowTicks", this.growTick);
        pCompound.putFloat("BaseDamage", this.baseDamage);
    }

    public void setBaseDamage(float baseDamage) {
        this.baseDamage = baseDamage;
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level.isClientSide) {
            if (this.growTick <= 0) {
                Entity entity = this.getOwner();
                Vec3 vec30 = Vec3.atCenterOf(this.blockPosition());
                if (entity instanceof LivingEntity livingEntity) {
                    if (result instanceof BlockHitResult blockHitResult){
                        BlockPos blockpos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                        if (BlockFinder.canBeReplaced(this.level, blockpos)) {
                            vec30 = Vec3.atCenterOf(blockpos);
                        }
                    } else if (result instanceof EntityHitResult entityHitResult){
                        Entity entity1 = entityHitResult.getEntity();
                        vec30 = Vec3.atCenterOf(entity1.blockPosition());
                    }
                    MagicFire magicFire = new MagicFire(this.level, vec30, livingEntity);
                    if (this.level.addFreshEntity(magicFire)) {
                        for (Direction direction : Direction.values()) {
                            if (direction.getAxis().isHorizontal()) {
                                MagicFire magicFire1 = new MagicFire(this.level, Vec3.atCenterOf(magicFire.blockPosition().relative(direction)), livingEntity);
                                this.level.addFreshEntity(magicFire1);
                            }
                        }
                    }
                }
                this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.5F, 0.75F);
                for (int i = 0; i < 8; ++i) {
                    VoidShock voidShock = new VoidShock(ModEntityType.VOID_SHOCK.get(), this.level);
                    voidShock.setPos(this.getX(), this.getY(), this.getZ());
                    Vec3 vec3 = this.position().add(this.level.getRandom().nextInt(-3, 3), this.level.getRandom().nextInt(3, 6), this.level.getRandom().nextInt(-3, 3));
                    voidShock.signalTo(vec3, MathHelper.secondsToTicks(2) + (i * 5));
                    voidShock.setBaseDamage(this.baseDamage);
                    voidShock.setExtraDamage(this.getExtraDamage());
                    if (this.getOwner() != null) {
                        voidShock.setOwner(this.getOwner());
                        if (this.getOwner() instanceof Mob mob) {
                            voidShock.setTarget(mob.getTarget());
                        }
                    }
                    this.level.addFreshEntity(voidShock);
                }
                this.level.broadcastEntityEvent(this, (byte) 6);
                this.growTick = 1;
                float damage = this.baseDamage + this.getExtraDamage();
                new SpellExplosion(this.level, this, this.damageSources().indirectMagic(this, this.getOwner()), this.getX(), this.getY(), this.getZ(), 2.0F, damage);
            }
        }

    }

    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity shooter = this.getOwner();
        Entity entity = result.getEntity();
        if (!this.level.isClientSide && !MobUtil.areAllies(shooter, entity)) {
            boolean flag;
            float damage = this.baseDamage + this.getExtraDamage();
            if (shooter instanceof LivingEntity livingentity) {
                flag = entity.hurt(this.damageSources().indirectMagic(this, livingentity), damage);
                if (flag) {
                    if (entity.isAlive()) {
                        this.doEnchantDamageEffects(livingentity, entity);
                    }
                }
            } else {
                entity.hurt(this.damageSources().magic(), damage);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.prevDeltaMovementX = this.getDeltaMovement().x;
        this.prevDeltaMovementY = this.getDeltaMovement().y;
        this.prevDeltaMovementZ = this.getDeltaMovement().z;

        this.setYRot(-((float) Mth.atan2(this.getDeltaMovement().x, this.getDeltaMovement().z)) * (180F / (float)Math.PI)) ;

        if (!this.level.isClientSide) {
            if (this.growTick >= 1){
                this.setDeltaMovement(Vec3.ZERO);
                ++this.growTick;
                this.level.broadcastEntityEvent(this, (byte) 8);
                if (this.growTick >= 5){
                    this.discard();
                }
            }
        }

        Vec3 trailAt = this.position().add(0, this.getBbHeight() / 2F, 0);
        if (this.trailPointer == -1) {
            Vec3 backAt = trailAt;
            for (int i = 0; i < this.trailPositions.length; i++) {
                this.trailPositions[i] = backAt;
            }
        }
        if (++this.trailPointer == this.trailPositions.length) {
            this.trailPointer = 0;
        }
        this.trailPositions[this.trailPointer] = trailAt;
    }

    public Vec3 getTrailPosition(int pointer, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        Vec3 d0 = this.trailPositions[j];
        Vec3 d1 = this.trailPositions[i].subtract(d0);
        return d0.add(d1.scale(partialTick));
    }

    public boolean hasTrail() {
        return trailPointer != -1;
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity);
            } else {
                if (MobUtil.areAllies(this.getOwner(), pEntity)){
                    return false;
                }
                if (this.getOwner() instanceof Enemy && pEntity instanceof Enemy){
                    return false;
                }
                if (pEntity instanceof Projectile projectile && projectile.getOwner() == this.getOwner()){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return super.canHitEntity(pEntity);
    }

    public void handleEntityEvent(byte id) {
        if (id == 6){
            this.growTick = 1;
        } else if (id == 8){
            this.size += 3.0F;
            this.alpha -= 0.2F;
        } else {
            super.handleEntityEvent(id);
        }
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    protected float getGravity() {
        return 0.025F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
