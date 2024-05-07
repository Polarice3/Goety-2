package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class ModFireball extends Fireball {
    public static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(ModFireball.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(ModFireball.class, EntityDataSerializers.FLOAT);

    public ModFireball(EntityType<? extends ModFireball> p_i50160_1_, Level p_i50160_2_) {
        super(p_i50160_1_, p_i50160_2_);
    }

    public ModFireball(Level p_i1771_1_, LivingEntity p_i1771_2_, double p_i1771_3_, double p_i1771_5_, double p_i1771_7_) {
        super(ModEntityType.MOD_FIREBALL.get(), p_i1771_2_, p_i1771_3_, p_i1771_5_, p_i1771_7_, p_i1771_1_);
    }

    public ModFireball(Level pWorld, double pX, double pY, double pZ, double pAccelX, double pAccelY, double pAccelZ) {
        super(ModEntityType.MOD_FIREBALL.get(), pX, pY, pZ, pAccelX, pAccelY, pAccelZ, pWorld);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DANGEROUS, true);
        this.entityData.define(DATA_DAMAGE, 5.0F);
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

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level.isClientSide) {
            Entity entity = pResult.getEntity();
            if (!entity.fireImmune()) {
                Entity entity1 = this.getOwner();
                float enchantment = 0;
                float damage = 5.0F;
                int flaming = 1;
                if (entity1 instanceof Player player){
                    if (WandUtil.enchantedFocus(player)){
                        enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
                        flaming += WandUtil.getLevels(ModEnchantments.BURNING.get(), player);
                    }
                    damage = SpellConfig.FireballDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
                } else if (entity1 instanceof LivingEntity) {
                    damage = this.getDamage();
                }
                int i = entity.getRemainingFireTicks() + (flaming - 1);
                entity.setSecondsOnFire(5 * flaming);
                boolean flag = entity.hurt(entity.damageSources().fireball(this, entity1), damage + enchantment);
                if (!flag) {
                    entity.setRemainingFireTicks(i);
                } else if (entity1 instanceof LivingEntity) {
                    this.doEnchantDamageEffects((LivingEntity)entity1, entity);
                }
            }

        }
    }

    protected void onHitBlock(BlockHitResult p_230299_1_) {
        super.onHitBlock(p_230299_1_);
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

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level.isClientSide) {
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

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Dangerous", this.isDangerous());
        pCompound.putFloat("Damage", this.getDamage());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Dangerous")) {
            this.setDangerous(pCompound.getBoolean("Dangerous"));
        }
        if (pCompound.contains("Damage")) {
            this.setDamage(pCompound.getFloat("Damage"));
        }
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
