package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ModWitherSkull extends WitherSkull {
   private static final EntityDataAccessor<Boolean> DATA_UPGRADED = SynchedEntityData.defineId(ModWitherSkull.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<Float> DATA_EXPLOSION = SynchedEntityData.defineId(ModWitherSkull.class, EntityDataSerializers.FLOAT);
   public static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(ModWitherSkull.class, EntityDataSerializers.FLOAT);
   public static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData.defineId(ModWitherSkull.class, EntityDataSerializers.FLOAT);
   public static final EntityDataAccessor<Integer> DATA_FIERY = SynchedEntityData.defineId(ModWitherSkull.class, EntityDataSerializers.INT);

   public ModWitherSkull(EntityType<? extends WitherSkull> p_37598_, Level p_37599_) {
      super(p_37598_, p_37599_);
   }

   public ModWitherSkull(double p_i50167_2_, double p_i50167_4_, double p_i50167_6_, double p_i50167_8_, double p_i50167_10_, double p_i50167_12_, Level p_i50167_14_) {
      this(ModEntityType.MOD_WITHER_SKULL.get(), p_i50167_14_);
      this.moveTo(p_i50167_2_, p_i50167_4_, p_i50167_6_, this.getYRot(), this.getXRot());
      this.reapplyPosition();
      double d0 = Math.sqrt(p_i50167_8_ * p_i50167_8_ + p_i50167_10_ * p_i50167_10_ + p_i50167_12_ * p_i50167_12_);
      if (d0 != 0.0D) {
         this.xPower = p_i50167_8_ / d0 * 0.1D;
         this.yPower = p_i50167_10_ / d0 * 0.1D;
         this.zPower = p_i50167_12_ / d0 * 0.1D;
      }
   }

   @Override
   public EntityType<?> getType() {
      return ModEntityType.MOD_WITHER_SKULL.get();
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_UPGRADED, false);
      this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
      this.entityData.define(DATA_FIERY, 0);
      this.entityData.define(DATA_EXPLOSION, 1.0F);
      this.entityData.define(DATA_DAMAGE, 8.0F);
   }

   public float getExplosionPower() {
      return this.entityData.get(DATA_EXPLOSION);
   }

   public void setExplosionPower(float pExplosionPower){
      this.entityData.set(DATA_EXPLOSION, pExplosionPower);
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

   public boolean isUpgraded() {
      return this.entityData.get(DATA_UPGRADED);
   }

   public void setUpgraded(boolean upgraded) {
      this.entityData.set(DATA_UPGRADED, upgraded);
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putFloat("ExplosionPower", this.getExplosionPower());
      pCompound.putFloat("Damage", this.getDamage());
      pCompound.putFloat("ExtraDamage", this.getExtraDamage());
      pCompound.putInt("Fiery", this.getFiery());
      pCompound.putBoolean("Upgraded", this.isUpgraded());
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      if (pCompound.contains("ExplosionPower", 99)) {
         this.setExplosionPower(pCompound.getFloat("ExplosionPower"));
      }
      if (pCompound.contains("Damage", 99)) {
         this.setDamage(pCompound.getFloat("Damage"));
      }
      if (pCompound.contains("ExtraDamage", 99)) {
         this.setExtraDamage(pCompound.getFloat("ExtraDamage"));
      }
      if (pCompound.contains("Fiery")) {
         this.setFiery(pCompound.getInt("Fiery"));
      }
      if (pCompound.contains("Upgraded")) {
         this.setUpgraded(pCompound.getBoolean("Upgraded"));
      }
   }

   protected void onHitEntity(EntityHitResult p_37626_) {
      if (!this.level.isClientSide) {
         Entity entity = p_37626_.getEntity();
         Entity entity1 = this.getOwner();
         boolean flag;
         float enchantment = this.getExtraDamage();
         int duration = 1;
         float damage;
         if (entity1 instanceof LivingEntity livingentity) {
            if (entity1 instanceof Player){
               damage = SpellConfig.WitherSkullDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
            } else {
               damage = this.getDamage();
            }
            flag = entity.hurt(this.damageSources().witherSkull(this, livingentity), damage + enchantment);
            if (flag) {
               if (entity.isAlive()) {
                  this.doEnchantDamageEffects(livingentity, entity);
               } else {
                  livingentity.heal(5.0F);
               }
            }
         } else {
            flag = entity.hurt(this.damageSources().magic(), 5.0F);
         }

         if (flag && entity instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof Player player){
               if (WandUtil.enchantedFocus(player)){
                  duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), player);
               }
            }
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 200 * duration, 1), this.getEffectSource());
         }

      }
   }

   protected void onHit(HitResult pResult) {
      HitResult.Type hitresult$type = pResult.getType();
      if (hitresult$type == HitResult.Type.ENTITY) {
         this.onHitEntity((EntityHitResult)pResult);
         this.level().gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), GameEvent.Context.of(this, (BlockState)null));
      } else if (hitresult$type == HitResult.Type.BLOCK) {
         BlockHitResult blockhitresult = (BlockHitResult)pResult;
         this.onHitBlock(blockhitresult);
         BlockPos blockpos = blockhitresult.getBlockPos();
         this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, this.level().getBlockState(blockpos)));
      }
      if (!this.level.isClientSide) {
         Entity owner = this.getOwner();
         boolean flaming = this.getFiery() > 0;
         boolean loot = CuriosFinder.hasWanting(owner);
         Explosion.BlockInteraction explodeMode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP;
         if (this.getOwner() instanceof Player) {
            explodeMode = SpellConfig.WitherSkullGriefing.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP;
         }
         LootingExplosion.Mode lootMode = loot ? LootingExplosion.Mode.LOOT : LootingExplosion.Mode.REGULAR;
         ExplosionUtil.lootExplode(this.level, this, this.getX(), this.getY(), this.getZ(), this.getExplosionPower(), flaming, explodeMode, lootMode);
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
            if(MobUtil.areAllies(this.getOwner(), pEntity)){
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
