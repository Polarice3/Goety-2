package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.MagicSmokeParticle;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class VoidSlash extends SlashProjectile{
    public int voidLevel = 2;
    private ItemStack weapon = new ItemStack(ModItems.BLADE_OF_ENDER.get());

    public VoidSlash(EntityType<? extends SlashProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public VoidSlash(Level levelIn, LivingEntity shooter) {
        super(ModEntityType.VOID_SLASH.get(), levelIn, shooter);
    }

    public VoidSlash(ItemStack itemStack, Level world, LivingEntity shooter) {
        this(world, shooter);
        this.weapon = itemStack;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("VoidLevel")) {
            this.setVoidLevel(pCompound.getInt("VoidLevel"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("VoidLevel", this.getVoidLevel());
    }

    public void setVoidLevel(int voidLevel) {
        this.voidLevel = voidLevel;
    }

    public int getVoidLevel() {
        return this.voidLevel;
    }

    public void spawnParticles() {
        if (this.level instanceof ServerLevel serverLevel) {
            float width = (float) this.getBoundingBox().getXsize();
            float step = 0.25F;
            float radians = Mth.DEG_TO_RAD * getYRot();
            float speed = 0.1F;
            for (int i = 0; i < width / step; i++) {
                double x = getX();
                double y = getY();
                double z = getZ();
                double offset = step * (i - width / step / 2);
                double rotX = offset * Math.cos(radians);
                double rotZ = -offset * Math.sin(radians);

                double dx = Math.random() * speed * 2.0D - speed;
                double dy = Math.random() * speed * 2.0D - speed;
                double dz = Math.random() * speed * 2.0D - speed;

                serverLevel.sendParticles(new MagicSmokeParticle.Option(16733695, 11141290, 10, 0.35F), x + rotX + dx, y + dy, z + rotZ + dz, 0, dx, dy, dz, 1.0F);
            }
        }
    }

    public void damageEntity(Entity entity) {
        DamageSource damageSource = entity.damageSources().magic();
        float f = this.getDamage();
        if (entity instanceof LivingEntity livingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.weapon, livingEntity.getMobType());
        }
        if (this.getOwner() instanceof Player player) {
            damageSource = entity.damageSources().playerAttack(player);
        } else if (this.getOwner() instanceof LivingEntity livingEntity) {
            damageSource = entity.damageSources().mobAttack(livingEntity);
        }
        if (entity.hurt(damageSource, f)) {
            if (entity instanceof LivingEntity livingEntity && !livingEntity.hasEffect(GoetyEffects.VOID_TOUCHED.get())) {
                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get(), MathHelper.secondsToTicks(3), this.getVoidLevel() - 1, false, true));
            }
        }
    }
}
