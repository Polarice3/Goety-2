package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class FangSpell extends Spell {

    public int defaultSoulCost() {
        return SpellConfig.FangCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.FangDuration.get();
    }

    public SoundEvent CastingSound(LivingEntity caster, ItemStack wand) {
        if (this.rightStaff(wand)) {
            return ModSounds.MASTER_FANG_TELEGRAPH.get();
        }
        return SoundEvents.EVOKER_PREPARE_ATTACK;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.FangCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ILL;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.BURNING.get());
        list.add(ModEnchantments.ABSORB.get());
        if (SpellConfig.FangGainSouls.get() > 0){
            list.add(ModEnchantments.SOUL_EATER.get());
        }
        return list;
    }

    @Override
    public ColorUtil particleColors(LivingEntity caster) {
        return new ColorUtil(0.4F, 0.3F, 0.35F);
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        int range = spellStat.getRange();
        int potency = spellStat.getPotency();
        int burning = spellStat.getBurning();
        double radius = spellStat.getRadius();
        boolean absorb = false;
        int soulEater = 0;
        if (WandUtil.enchantedFocus(caster)){
            range += WandUtil.getRangeLevel(caster);
            potency += WandUtil.getPotencyLevel(caster);
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
            absorb = WandUtil.getLevels(ModEnchantments.ABSORB.get(), caster) > 0;
            soulEater += WandUtil.getLevels(ModEnchantments.SOUL_EATER.get(), caster);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, caster, range, radius);
        Vec3 vector3d = rayTraceResult.getLocation();
        double d0 = Math.min(vector3d.y, caster.getY());
        double d1 = Math.max(vector3d.y, caster.getY()) + 1.0D;
        float f = (float) Mth.atan2(vector3d.z - caster.getZ(), vector3d.x - caster.getX());
        LivingEntity target = this.getTarget(caster, range);
        if (target != null){
            d0 = Math.min(target.getY(), caster.getY());
            d1 = Math.max(target.getY(), caster.getY()) + 1.0D;
            f = (float) Mth.atan2(target.getZ() - caster.getZ(), target.getX() - caster.getX());
        }
        if (!isShifting(caster)) {
            if (this.rightStaff(staff)) {
                WandUtil.spawnFangLine(caster, target != null ? target.position() : vector3d, potency, burning, absorb, soulEater);
            } else {
                for(int l = 0; l < range; ++l) {
                    double d2 = 1.25D * (double)(l + 1);
                    WandUtil.spawnFangs(caster, caster.getX() + (double)Mth.cos(f) * d2, caster.getZ() + (double)Mth.sin(f) * d2, d0, d1, f, l, potency, burning, absorb, soulEater);
                }
            }
        } else {
            if (rightStaff(staff)) {
                if (target != null) {
                    WandUtil.spawnFangsAroundTarget(caster, target.position(), potency, burning, absorb, soulEater);
                } else {
                    WandUtil.spawnFangRound(caster, vector3d, potency, burning, absorb, soulEater);
                }
            } else {
                for(int i = 0; i < 5; ++i) {
                    float f1 = f + (float)i * (float)Math.PI * 0.4F;
                    WandUtil.spawnFangs(caster, caster.getX() + (double)Mth.cos(f1) * 1.5D, caster.getZ() + (double)Mth.sin(f1) * 1.5D, d0, d1, f1, 0, potency, burning, absorb, soulEater);
                }

                for(int k = 0; k < 8; ++k) {
                    float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                    WandUtil.spawnFangs(caster, caster.getX() + (double)Mth.cos(f2) * 2.5D, caster.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1, f2, 3, potency, burning, absorb, soulEater);
                }
            }
        }
        this.playSound(worldIn, caster, this.rightStaff(staff) ? ModSounds.MASTER_FANG_SUMMON.get() : SoundEvents.EVOKER_CAST_SPELL);
    }

}
