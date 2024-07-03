package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class ModFireball extends SmallFireball {
    public static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(ModFireball.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(ModFireball.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData.defineId(ModFireball.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> DATA_FIERY = SynchedEntityData.defineId(ModFireball.class, EntityDataSerializers.INT);

    public ModFireball(EntityType<? extends ModFireball> p_i50160_1_, Level p_i50160_2_) {
        super(p_i50160_1_, p_i50160_2_);
    }

    public ModFireball(Level p_i1771_1_, LivingEntity p_i1771_2_, double p_i1771_3_, double p_i1771_5_, double p_i1771_7_) {
        super(p_i1771_1_, p_i1771_2_, p_i1771_3_, p_i1771_5_, p_i1771_7_);
    }

    public ModFireball(Level pWorld, double pX, double pY, double pZ, double pAccelX, double pAccelY, double pAccelZ) {
        super(pWorld, pX, pY, pZ, pAccelX, pAccelY, pAccelZ);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityType.MOD_FIREBALL.get();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANGEROUS, true);
        this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
        this.entityData.define(DATA_FIERY, 0);
        this.entityData.define(DATA_DAMAGE, 5.0F);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("Damage", this.getDamage());
        pCompound.putFloat("ExtraDamage", this.getExtraDamage());
        pCompound.putInt("Fiery", this.getFiery());
        pCompound.putBoolean("Dangerous", this.isDangerous());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Damage", 99)) {
            this.setDamage(pCompound.getFloat("Damage"));
        }
        if (pCompound.contains("ExtraDamage", 99)) {
            this.setExtraDamage(pCompound.getFloat("ExtraDamage"));
        }
        if (pCompound.contains("Fiery")) {
            this.setFiery(pCompound.getInt("Fiery"));
        }
        if (pCompound.contains("Dangerous")) {
            this.setDangerous(pCompound.getBoolean("Dangerous"));
        }
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    public void setDangerous(boolean pDangerous) {
        this.entityData.set(DATA_DANGEROUS, pDangerous);
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

    public int getFiery() {
        return this.entityData.get(DATA_FIERY);
    }

    public void setFiery(int fiery) {
        this.entityData.set(DATA_FIERY, fiery);
    }

    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level.isClientSide) {
            Entity entity = pResult.getEntity();
            Entity entity1 = this.getOwner();
            float enchantment = this.getExtraDamage();
            float damage = 5.0F;
            int flaming = 1 + this.getFiery();
            if (entity1 instanceof Player){
                damage = SpellConfig.FireballDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
            } else if (entity1 instanceof LivingEntity) {
                damage = this.getDamage();
            }
            int i = entity.getRemainingFireTicks() + (flaming - 1);
            entity.setSecondsOnFire(5 * flaming);
            DamageSource damageSource = DamageSource.fireball(this, entity1);
            if (entity1 instanceof LivingEntity livingEntity){
                if (CuriosFinder.hasNetherRobe(livingEntity)){
                    damageSource = ModDamageSource.magicFireball(this, entity1);
                }
                if (livingEntity instanceof OwnableEntity ownable && ownable.getOwner() instanceof LivingEntity livingEntity1){
                    if (CuriosFinder.hasNetherRobe(livingEntity1)){
                        damageSource = ModDamageSource.magicFireball(this, entity1);
                    }
                }
            }
            boolean flag = entity.hurt(damageSource, damage + enchantment);
            if (!flag) {
                entity.setRemainingFireTicks(i);
            } else if (entity1 instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity)entity1, entity);
            }

        }
    }

    protected void onHitBlock(BlockHitResult p_230299_1_) {
        BlockState blockstate = this.level.getBlockState(p_230299_1_.getBlockPos());
        blockstate.onProjectileHit(this.level, blockstate, p_230299_1_, this);
        if (!this.level.isClientSide) {
            Entity entity = this.getOwner();
            if (this.isDangerous()) {
                boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.getOwner());
                if (entity instanceof Player || (entity instanceof IOwned iOwned && iOwned.getTrueOwner() instanceof Player)){
                    flag = SpellConfig.FireballGriefing.get();
                }
                if (flag) {
                    BlockPos blockpos = p_230299_1_.getBlockPos().relative(p_230299_1_.getDirection());
                    if (this.level.isEmptyBlock(blockpos)) {
                        this.level.setBlockAndUpdate(blockpos, BaseFireBlock.getState(this.level, blockpos));
                    }
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
}
