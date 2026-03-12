package com.Polarice3.Goety.common.magic.spells.abyss;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class WaterJetSpell extends EverChargeSpell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(8);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.WaterJetCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.WaterJetChargeUp.get();
    }

    @Override
    public int shotsNumber() {
        return SpellConfig.WaterJetDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.WaterJetCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public SoundEvent loopSound(LivingEntity caster) {
        return ModSounds.WATER_JET.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster, SpellStat spellStat) {
        if (caster instanceof Mob mob){
            if (mob.getTarget() != null){
                int range = spellStat.getRange();
                if (WandUtil.enchantedFocus(caster)){
                    range += WandUtil.getRangeLevel(caster);
                }
                return mob.hasLineOfSight(mob.getTarget()) && mob.distanceTo(mob.getTarget()) <= range + 4.0D;
            }
        }
        return super.conditionsMet(worldIn, caster, spellStat);
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        float potency = spellStat.getPotency();
        int range = spellStat.getRange();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            range += WandUtil.getRangeLevel(caster);
        }
        float damage = SpellConfig.WaterJetDamage.get().floatValue() * WandUtil.damageMultiply();
        damage += potency;
        HitResult result = this.rayTrace(worldIn, caster, range, 3.0F);
        if (result.getType() != HitResult.Type.MISS) {
            double particleOffset = 0;
            if (result instanceof EntityHitResult entityHitResult) {
                Entity target = entityHitResult.getEntity();
                if (!MobUtil.areAllies(caster, target)) {
                    MiscCapHelper.setClientTarget(caster, target);
                    if (target.hurt(ModDamageSource.directDrench(caster), damage)) {
                        if (this.rightStaff(staff) && target instanceof LivingEntity livingEntity) {
                            livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), MathHelper.secondsToTicks(5)));
                        }
                    }
                }
                target.clearFire();
                particleOffset = target.getBbHeight() / 2;
            } else if (result instanceof BlockHitResult blockHitResult) {
                BlockPos blockPos = blockHitResult.getBlockPos();
                this.dowseFire(caster, worldIn, blockPos);
            }
            Vec3 vec3 = result.getLocation();
            worldIn.sendParticles(ModParticleTypes.WATER_TRAIL.get(), vec3.x, vec3.y + particleOffset, vec3.z, 1, 0, 0, 0, 0.15);
        }
        if (result.getType() == HitResult.Type.MISS || !(result instanceof EntityHitResult)) {
            MiscCapHelper.setClientTarget(caster, null);
        }
    }

    @Override
    public void stopSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, ItemStack focus, int castTime, SpellStat spellStat) {
        super.stopSpell(worldIn, caster, staff, focus, castTime, spellStat);
        MiscCapHelper.setClientTarget(caster, null);
    }

    private void dowseFire(LivingEntity caster, Level world, BlockPos blockPos) {
        BlockState blockstate = world.getBlockState(blockPos);
        if (blockstate.is(BlockTags.FIRE)) {
            world.levelEvent(null, 1009, blockPos, 0);
            world.removeBlock(blockPos, false);
        } else if (AbstractCandleBlock.isLit(blockstate)) {
            AbstractCandleBlock.extinguish(null, blockstate, world, blockPos);
        } else if (CampfireBlock.isLitCampfire(blockstate)) {
            world.levelEvent(null, 1009, blockPos, 0);
            CampfireBlock.dowse(caster, world, blockPos, blockstate);
            world.setBlockAndUpdate(blockPos, blockstate.setValue(CampfireBlock.LIT, Boolean.valueOf(false)));
        }

    }
}
