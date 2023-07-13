package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FangSpell extends Spells {

    public int SoulCost() {
        return SpellConfig.FangCost.get();
    }

    public int CastDuration() {
        return SpellConfig.FangDuration.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.EVOKER_PREPARE_ATTACK;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ILL;
    }

    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving){
        Player playerEntity = (Player) entityLiving;
        int range = 16;
        double radius = 2.0D;
        if (WandUtil.enchantedFocus(playerEntity)){
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), playerEntity);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, playerEntity, range, radius);
        Vec3 vector3d = rayTraceResult.getLocation();
        double d0 = Math.min(vector3d.y, entityLiving.getY());
        double d1 = Math.max(vector3d.y, entityLiving.getY()) + 1.0D;
        float f = (float) Mth.atan2(vector3d.z - entityLiving.getZ(), vector3d.x - entityLiving.getX());
        if (rayTraceResult instanceof EntityHitResult){
            Entity target = ((EntityHitResult) rayTraceResult).getEntity();
            d0 = Math.min(target.getY(), entityLiving.getY());
            d1 = Math.max(target.getY(), entityLiving.getY()) + 1.0D;
            f = (float)Mth.atan2(target.getZ() - entityLiving.getZ(), target.getX() - entityLiving.getX());
        }
        if (!isShifting(entityLiving)) {
            for (int l = 0; l < range; ++l) {
                double d2 = 1.25D * (double) (l + 1);
                WandUtil.spawnFangs(entityLiving, entityLiving.getX() + (double) Mth.cos(f) * d2, entityLiving.getZ() + (double) Mth.sin(f) * d2, d0, d1, f, l);
            }
        } else {
            for(int i = 0; i < 5; ++i) {
                float f1 = f + (float)i * (float)Math.PI * 0.4F;
                WandUtil.spawnFangs(entityLiving,entityLiving.getX() + (double)Mth.cos(f1) * 1.5D, entityLiving.getZ() + (double)Mth.sin(f1) * 1.5D, d0, d1, f1, 0);
            }

            for(int k = 0; k < 8; ++k) {
                float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                WandUtil.spawnFangs(entityLiving,entityLiving.getX() + (double)Mth.cos(f2) * 2.5D, entityLiving.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1, f2, 3);
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, this.getSoundSource(), 1.0F, 1.0F);
    }

    public void StaffResult(ServerLevel worldIn, LivingEntity entityLiving){
        Player playerEntity = (Player) entityLiving;
        int range = 16;
        double radius = 2.0D;
        if (WandUtil.enchantedFocus(playerEntity)){
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), playerEntity);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, playerEntity, range, radius);
        Vec3 vector3d = rayTraceResult.getLocation();
        double d0 = Math.min(vector3d.y, entityLiving.getY());
        double d1 = Math.max(vector3d.y, entityLiving.getY()) + 1.0D;
        float f = (float) Mth.atan2(vector3d.z - entityLiving.getZ(), vector3d.x - entityLiving.getX());
        if (rayTraceResult instanceof EntityHitResult){
            Entity target = ((EntityHitResult) rayTraceResult).getEntity();
            d0 = Math.min(target.getY(), entityLiving.getY());
            d1 = Math.max(target.getY(), entityLiving.getY()) + 1.0D;
            f = (float)Mth.atan2(target.getZ() - entityLiving.getZ(), target.getX() - entityLiving.getX());
        }
        if (!isShifting(entityLiving)) {
            for(int l = 0; l < range; ++l) {
                double d2 = 1.25D * (double)(l + 1);
                float fleft = f + 0.4F;
                float fright = f - 0.4F;
                WandUtil.spawnFangs(entityLiving,entityLiving.getX() + (double)Mth.cos(f) * d2, entityLiving.getZ() + (double)Mth.sin(f) * d2, d0, d1, f, l);
                WandUtil.spawnFangs(entityLiving,entityLiving.getX() + (double)Mth.cos(fleft) * d2, entityLiving.getZ() + (double)Mth.sin(fleft) * d2, d0, d1, fleft, l);
                WandUtil.spawnFangs(entityLiving,entityLiving.getX() + (double)Mth.cos(fright) * d2, entityLiving.getZ() + (double)Mth.sin(fright) * d2, d0, d1, fright, l);
            }
        } else {
            for(int i = 0; i < 5; ++i) {
                float f1 = f + (float)i * (float)Math.PI * 0.4F;
                WandUtil.spawnFangs(entityLiving,entityLiving.getX() + (double)Mth.cos(f1) * 1.5D, entityLiving.getZ() + (double)Mth.sin(f1) * 1.5D, d0, d1, f1, 0);
            }

            for(int k = 0; k < 8; ++k) {
                float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                WandUtil.spawnFangs(entityLiving,entityLiving.getX() + (double)Mth.cos(f2) * 2.5D, entityLiving.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1, f2, 3);
            }

            for(int k = 0; k < 11; ++k) {
                float f2 = f + (float)k * (float)Math.PI * 4.0F / 16.0F + 2.5133462F;
                WandUtil.spawnFangs(entityLiving,entityLiving.getX() + (double)Mth.cos(f2) * 3.5D, entityLiving.getZ() + (double)Mth.sin(f2) * 3.5D, d0, d1, f2, 6);
            }

            for(int k = 0; k < 14; ++k) {
                float f2 = f + (float)k * (float)Math.PI * 8.0F / 32.0F + 5.0266924F;
                WandUtil.spawnFangs(entityLiving,entityLiving.getX() + (double)Mth.cos(f2) * 4.5D, entityLiving.getZ() + (double)Mth.sin(f2) * 4.5D, d0, d1, f2, 9);
            }
        }
        worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, this.getSoundSource(), 1.0F, 1.0F);
    }

}
