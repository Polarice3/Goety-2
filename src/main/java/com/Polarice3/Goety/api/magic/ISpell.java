package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import java.util.List;

public interface ISpell {
    int SoulCost();

    int CastDuration();

    SoundEvent CastingSound();

    int SpellCooldown();

    void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff);

    SpellType getSpellType();

    boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving);

    List<Enchantment> acceptedEnchantments();

    default ColorUtil particleColors(LivingEntity entityLiving){
        return new ColorUtil(0.2F, 0.2F, 0.2F);
    }

    default HitResult rayTrace(Level worldIn, LivingEntity livingEntity, int range, double radius) {
        if (this.entityResult(worldIn, livingEntity, range, radius) == null){
            return this.blockResult(worldIn, livingEntity, range);
        } else {
            return this.entityResult(worldIn, livingEntity, range, radius);
        }
    }

    default BlockHitResult blockResult(Level worldIn, LivingEntity livingEntity, double range) {
        float f = livingEntity.getXRot();
        float f1 = livingEntity.getYRot();
        Vec3 vector3d = livingEntity.getEyePosition(1.0F);
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vector3d1 = vector3d.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
        return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, livingEntity));
    }

    default EntityHitResult entityResult(Level worldIn, LivingEntity livingEntity, int range, double radius){
        Vec3 srcVec = livingEntity.getEyePosition(1.0F);
        Vec3 lookVec = livingEntity.getViewVector(1.0F);
        Vec3 destVec = srcVec.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        AABB axisalignedbb = livingEntity.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(radius, radius, radius);
        return ProjectileUtil.getEntityHitResult(worldIn, livingEntity, srcVec, destVec, axisalignedbb, entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.isPickable());
    }
}
