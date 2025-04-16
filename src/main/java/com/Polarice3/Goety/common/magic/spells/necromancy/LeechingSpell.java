package com.Polarice3.Goety.common.magic.spells.necromancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.AbsorbTrailParticleOption;
import com.Polarice3.Goety.client.particles.GatherTrailParticle;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LeechingSpell extends EverChargeSpell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(8);
    }

    public int defaultSoulCost() {
        return SpellConfig.LeechingCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.LeechingChargeUp.get();
    }

    @Override
    public int shotsNumber() {
        return SpellConfig.LeechingDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.LeechingCoolDown.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        float potency = SpellConfig.LeechingDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        int range = spellStat.getRange();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster) / 2.0F;
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), caster);
        }
        potency += spellStat.getPotency();
        LivingEntity livingEntity = this.getTarget(caster, range);
        if (livingEntity != null){
            ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_RED);
            Vec3 vector3d = new Vec3(livingEntity.getRandomX(1.0F), livingEntity.getRandomY(), livingEntity.getRandomZ(1.0F));
            Vec3 vector3d1 = new Vec3(caster.getRandomX(1.0F), caster.getEyeY(), caster.getRandomZ(1.0F));
            worldIn.sendParticles(new GatherTrailParticle.Option(colorUtil, vector3d1), vector3d.x, vector3d.y, vector3d.z, 0, 0.0F, 0.0F, 0.0F, 0.5F);
            worldIn.sendParticles(new AbsorbTrailParticleOption(vector3d1, 11141120, 10), vector3d.x, vector3d.y, vector3d.z, 1, 0.0, 0.0, 0.0, 0.0);
            if (livingEntity.hurt(ModDamageSource.lifeLeech(caster, caster), potency)) {
                if (this.rightStaff(staff)){
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
                }
                this.playSound(worldIn, caster, ModSounds.SOUL_EAT.get());
            }
        }
    }
}
