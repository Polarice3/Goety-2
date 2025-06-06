package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.illager.AllyIrk;
import com.Polarice3.Goety.common.entities.ally.illager.AllyVex;
import com.Polarice3.Goety.common.entities.neutral.Minion;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VexSpell extends SummonSpell {
    private final TargetingConditions vexCountTargeting = TargetingConditions.DEFAULT.range(16.0D).ignoreInvisibilityTesting();

    public int defaultSoulCost() {
        return SpellConfig.VexCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.VexDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.VexSummonDown.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.EVOKER_PREPARE_SUMMON;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.VexCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ILL;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public ColorUtil particleColors(LivingEntity caster) {
        return new ColorUtil(0.7F, 0.7F, 0.8F);
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster) {
        if (caster instanceof Mob){
            return SpellConfig.WandVexLimit.get() > this.VexLimit(caster);
        }
        return super.conditionsMet(worldIn, caster);
    }

    public void commonResult(ServerLevel worldIn, LivingEntity caster){
        if (isShifting(caster)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof Minion minion) {
                    if (minion instanceof AllyVex || minion instanceof AllyIrk) {
                        if (minion.getTrueOwner() == caster) {
                            if (minion instanceof AllyIrk){
                                caster.heal(2.0F);
                            }
                            entity.kill();
                        }
                    }
                }
            }
            for (int i = 0; i < caster.level.random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.POOF, caster.getX(), caster.getEyeY(), caster.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
            }
            this.playSound(worldIn, caster, SoundEvents.EVOKER_CAST_SPELL);
        }
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }
        if (!isShifting(caster)) {
            int i = 3;
            if (rightStaff(staff)){
                i = 3 + worldIn.random.nextInt(3);
            }
            for (int i1 = 0; i1 < i; ++i1) {
                BlockPos blockpos = caster.blockPosition().offset(-2 + caster.getRandom().nextInt(5), 1, -2 + caster.getRandom().nextInt(5));
                AllyVex vexentity = new AllyVex(ModEntityType.ALLY_VEX.get(), worldIn);
                vexentity.setTrueOwner(caster);
                vexentity.moveTo(blockpos, 0.0F, 0.0F);
                vexentity.finalizeSpawn(worldIn, caster.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                vexentity.setBoundOrigin(blockpos);
                int limit = rightStaff(staff) ? SpellConfig.StaffVexLimit.get() : SpellConfig.WandVexLimit.get();
                if (limit > VexLimit(caster)) {
                    vexentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                } else {
                    vexentity.setLimitedLife(1);
                    vexentity.addEffect(new MobEffectInstance(MobEffects.WITHER, 800, 1));
                }
                if (potency > 0) {
                    Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(vexentity.getMainHandItem());
                    map.putIfAbsent(Enchantments.SHARPNESS, potency);
                    EnchantmentHelper.setEnchantments(map, vexentity.getMainHandItem());
                    vexentity.setItemSlot(EquipmentSlot.MAINHAND, vexentity.getMainHandItem());
                }
                this.SummonSap(caster, vexentity);
                this.setTarget(caster, vexentity);
                worldIn.addFreshEntity(vexentity);
                this.summonAdvancement(caster, vexentity);
            }
            this.playSound(worldIn, caster, SoundEvents.EVOKER_CAST_SPELL);
            this.SummonDown(caster);
        }
    }

    public int VexLimit(LivingEntity entityLiving){
        return entityLiving.level.getNearbyEntities(AllyVex.class, this.vexCountTargeting, entityLiving, entityLiving.getBoundingBox().inflate(16.0D)).size();
    }
}
