package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.util.UpdraftBlast;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class UpdraftSpell extends Spells {

    public int SoulCost() {
        return SpellConfig.UpdraftCost.get();
    }

    public int CastDuration() {
        return SpellConfig.UpdraftDuration.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    public void commonResult(ServerLevel worldIn, LivingEntity entityLiving, int range){
        double radius = 2.0D;
        float damage = SpellConfig.UpdraftBlastDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        if (WandUtil.enchantedFocus(entityLiving)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), entityLiving);
            damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, entityLiving, range, radius);
        if (rayTraceResult instanceof EntityHitResult){
            Entity target = ((EntityHitResult) rayTraceResult).getEntity();
            if (target instanceof LivingEntity) {
                UpdraftBlast updraftBlast = new UpdraftBlast(ModEntityType.UPDRAFT_BLAST.get(), worldIn);
                updraftBlast.setDamage(damage);
                updraftBlast.setAreaOfEffect((float) radius);
                updraftBlast.setPos(target.position());
                worldIn.addFreshEntity(updraftBlast);
            }
        } else if (rayTraceResult instanceof BlockHitResult){
            BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
            UpdraftBlast updraftBlast = new UpdraftBlast(ModEntityType.UPDRAFT_BLAST.get(), worldIn);
            updraftBlast.setDamage(damage);
            updraftBlast.setAreaOfEffect((float) radius);
            updraftBlast.setPos(blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ());
            worldIn.addFreshEntity(updraftBlast);
        }
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        commonResult(worldIn, entityLiving, rightStaff(staff) ? 32 : 16);
    }
}
