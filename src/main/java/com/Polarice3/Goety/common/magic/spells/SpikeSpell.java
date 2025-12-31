package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
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

public class SpikeSpell extends Spell {

    public int defaultSoulCost() {
        return SpellConfig.SpikeCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.SpikeDuration.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.EVOKER_PREPARE_SUMMON;
    }

    @Override
    public float castingPitch() {
        return 0.5F;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.SpikeCoolDown.get();
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
        if (SpellConfig.SpikeGainSouls.get() > 0){
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
        double radius = spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)){
            range += WandUtil.getRangeLevel(caster);
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
            for(int l = 0; l < range; ++l) {
                double d2 = 1.25D * (double)(l + 1);
                WandUtil.spawnSpikes(caster, caster.getX() + (double)Mth.cos(f) * d2, caster.getZ() + (double)Mth.sin(f) * d2, d0, d1, f, l, spellStat);
                if (rightStaff(staff)) {
                    float fleft = f + 0.2F;
                    float fright = f - 0.2F;
                    WandUtil.spawnSpikes(caster, caster.getX() + (double) Mth.cos(fleft) * d2, caster.getZ() + (double) Mth.sin(fleft) * d2, d0, d1, fleft, l, spellStat);
                    WandUtil.spawnSpikes(caster, caster.getX() + (double) Mth.cos(fright) * d2, caster.getZ() + (double) Mth.sin(fright) * d2, d0, d1, fright, l, spellStat);
                }
            }
        } else {
            for(int i = 0; i < 5; ++i) {
                float f1 = f + (float)i * (float)Math.PI * 0.4F;
                WandUtil.spawnSpikes(caster, caster.getX() + (double)Mth.cos(f1) * 1.5D, caster.getZ() + (double)Mth.sin(f1) * 1.5D, d0, d1, f1, 0, spellStat);
            }

            for(int k = 0; k < 8; ++k) {
                float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                WandUtil.spawnSpikes(caster, caster.getX() + (double)Mth.cos(f2) * 2.5D, caster.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1, f2, 3, spellStat);
            }

            if (rightStaff(staff)) {
                for(int k = 0; k < 11; ++k) {
                    float f2 = f + (float)k * (float)Math.PI * 4.0F / 16.0F + 2.5133462F;
                    WandUtil.spawnSpikes(caster, caster.getX() + (double)Mth.cos(f2) * 3.5D, caster.getZ() + (double)Mth.sin(f2) * 3.5D, d0, d1, f2, 6, spellStat);
                }
            }
        }
        this.playSound(worldIn, caster, SoundEvents.EVOKER_CAST_SPELL, 1.0F, 0.5F);
    }

}
