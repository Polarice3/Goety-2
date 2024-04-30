package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class ModWitherSkull extends ExplosiveProjectile {
   public float explosionPower = 1.0F;
   public float damage = SpellConfig.WitherSkullDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();

   public ModWitherSkull(EntityType<? extends ExplosiveProjectile> p_37598_, Level p_37599_) {
      super(p_37598_, p_37599_);
   }

   public ModWitherSkull(double p_i50167_2_, double p_i50167_4_, double p_i50167_6_, double p_i50167_8_, double p_i50167_10_, double p_i50167_12_, Level p_i50167_14_) {
      super(ModEntityType.MOD_WITHER_SKULL.get(), p_i50167_2_, p_i50167_4_, p_i50167_6_, p_i50167_8_, p_i50167_10_, p_i50167_12_, p_i50167_14_);
   }

   public ModWitherSkull(LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_, Level p_i50168_9_) {
      super(ModEntityType.MOD_WITHER_SKULL.get(), p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
   }

   protected float getInertia() {
      return this.isDangerous() ? 0.73F : super.getInertia();
   }

   public boolean isOnFire() {
      return false;
   }

   public float getBlockExplosionResistance(Explosion p_37619_, BlockGetter p_37620_, BlockPos p_37621_, BlockState p_37622_, FluidState p_37623_, float p_37624_) {
      return this.isDangerous() && p_37622_.canEntityDestroy(p_37620_, p_37621_, new WitherSkull(EntityType.WITHER_SKULL, this.level)) ? Math.min(0.8F, p_37624_) : p_37624_;
   }

   protected void onHitEntity(EntityHitResult p_37626_) {
      super.onHitEntity(p_37626_);
      if (!this.level.isClientSide) {
         Entity entity = p_37626_.getEntity();
         Entity entity1 = this.getOwner();
         boolean flag;
         float enchantment = 0;
         int duration = 1;
         if (entity1 instanceof LivingEntity livingentity) {
            if (livingentity instanceof Player player){
               if (WandUtil.enchantedFocus(player)){
                  enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
               }
            }
            flag = entity.hurt(ModDamageSource.modWitherSkull(this, livingentity), this.damage + enchantment);
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
      super.onHit(pResult);
      if (!this.level.isClientSide) {
         Entity owner = this.getOwner();
         float enchantment = 0;
         boolean flaming = false;
         boolean loot = false;
         if (owner instanceof Player player) {
            if (WandUtil.enchantedFocus(player)) {
               enchantment = WandUtil.getLevels(ModEnchantments.RADIUS.get(), player) / 2.5F;
               if (WandUtil.getLevels(ModEnchantments.BURNING.get(), player) > 0) {
                  flaming = true;
               }
            }
            if (CuriosFinder.findRing(player).getItem() == ModItems.RING_OF_WANT.get()) {
               if (CuriosFinder.findRing(player).isEnchanted()) {
                  float wanting = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.WANTING.get(), CuriosFinder.findRing(player));
                  if (wanting > 0) {
                     loot = true;
                  }
               }
            }
         }
         Explosion.BlockInteraction explodeMode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP;
         if (this.getOwner() instanceof Player) {
            explodeMode = SpellConfig.WitherSkullGriefing.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP;
         }
         LootingExplosion.Mode lootMode = loot ? LootingExplosion.Mode.LOOT : LootingExplosion.Mode.REGULAR;
         ExplosionUtil.lootExplode(this.level, this, this.getX(), this.getY(), this.getZ(), this.explosionPower + enchantment, flaming, explodeMode, lootMode);
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

   public boolean isPickable() {
      return false;
   }

   public boolean hurt(DamageSource p_37616_, float p_37617_) {
      return false;
   }

   protected boolean shouldBurn() {
      return false;
   }

   @Override
   public void setExplosionPower(float pExplosionPower) {
      this.explosionPower = pExplosionPower;
   }

   @Override
   public float getExplosionPower() {
      return this.explosionPower;
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putFloat("ExplosionPower", this.explosionPower);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      if (pCompound.contains("ExplosionPower")) {
         this.setExplosionPower(pCompound.getFloat("ExplosionPower"));
      }
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
