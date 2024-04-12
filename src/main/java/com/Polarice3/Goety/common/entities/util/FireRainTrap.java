package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.HellBolt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;

public class FireRainTrap extends AbstractTrap {
    private static final EntityDataAccessor<Boolean> HELLFIRE = SynchedEntityData.defineId(FireRainTrap.class, EntityDataSerializers.BOOLEAN);

    public FireRainTrap(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setParticle(ParticleTypes.ASH);
    }

    public FireRainTrap(Level worldIn, double x, double y, double z) {
        this(ModEntityType.FIRE_RAIN_TRAP.get(), worldIn);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HELLFIRE, false);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Hellfire", this.isHellfire());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Hellfire")){
            this.setHellfire(compound.getBoolean("Hellfire"));
        }
    }

    public boolean isHellfire(){
        return this.entityData.get(HELLFIRE);
    }

    public void setHellfire(boolean hellfire){
        this.entityData.set(HELLFIRE, hellfire);
    }

    public void tick() {
        super.tick();
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(this.getX(), this.getY(), this.getZ());

        while(blockpos$mutable.getY() < this.getY() + 32.0D && !this.level.getBlockState(blockpos$mutable).getMaterial().blocksMotion()) {
            blockpos$mutable.move(Direction.UP);
        }
        if (this.getOwner() != null) {
            AbstractHurtingProjectile fireballEntity;
            if (this.isHellfire()){
                fireballEntity = new HellBolt(this.getOwner(), 0, -900D, 0, this.level);
            } else {
                fireballEntity = new SmallFireball(this.level, this.getOwner(), 0, -900D, 0);
            }
            fireballEntity.setPos(this.getX() + this.random.nextInt(5), blockpos$mutable.getY(), this.getZ() + this.random.nextInt(5));
            if (fireballEntity instanceof HellBolt hellBolt){
                hellBolt.setRain(true);
            }
            this.level.addFreshEntity(fireballEntity);
        } else {
            this.discard();
        }
        if (this.tickCount >= this.getDuration()) {
            this.discard();
        }
    }

}
