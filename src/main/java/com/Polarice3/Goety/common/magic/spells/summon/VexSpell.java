package com.Polarice3.Goety.common.magic.spells.summon;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.AllyVex;
import com.Polarice3.Goety.common.magic.SummonSpells;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;

public class VexSpell extends SummonSpells {
    private final TargetingConditions vexCountTargeting = TargetingConditions.DEFAULT.range(16.0D).ignoreInvisibilityTesting();

    public int SoulCost() {
        return SpellConfig.VexCost.get();
    }

    public int CastDuration() {
        return SpellConfig.VexDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.VexCooldown.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.EVOKER_PREPARE_SUMMON;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ILL;
    }

    public void commonResult(ServerLevel worldIn, LivingEntity entityLiving){
        if (WandUtil.enchantedFocus(entityLiving)){
            enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            duration = WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving) + 1;
        }
        if (isShifting(entityLiving)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof AllyVex) {
                    if (((AllyVex) entity).getTrueOwner() == entityLiving) {
                        entity.kill();
                    }
                }
            }
            for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.POOF, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
            }
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving){
        this.commonResult(worldIn, entityLiving);
        if (!isShifting(entityLiving)) {
                for (int i1 = 0; i1 < 3; ++i1) {
                    BlockPos blockpos = entityLiving.blockPosition().offset(-2 + entityLiving.getRandom().nextInt(5), 1, -2 + entityLiving.getRandom().nextInt(5));
                    AllyVex vexentity = new AllyVex(ModEntityType.ALLY_VEX.get(), worldIn);
                    vexentity.setOwnerId(entityLiving.getUUID());
                    vexentity.moveTo(blockpos, 0.0F, 0.0F);
                    vexentity.finalizeSpawn(worldIn, entityLiving.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                    vexentity.setBoundOrigin(blockpos);
                    if (SpellConfig.WandVexLimit.get() > VexLimit(entityLiving)) {
                        vexentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                    } else {
                        vexentity.setLimitedLife(1);
                        vexentity.addEffect(new MobEffectInstance(MobEffects.WITHER, 800, 1));
                    }
                    if (enchantment > 0) {
                        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(vexentity.getMainHandItem());
                        map.putIfAbsent(Enchantments.SHARPNESS, enchantment);
                        EnchantmentHelper.setEnchantments(map, vexentity.getMainHandItem());
                        vexentity.setItemSlot(EquipmentSlot.MAINHAND, vexentity.getMainHandItem());
                    }
                    this.SummonSap(entityLiving, vexentity);
                    this.setTarget(worldIn, entityLiving, vexentity);
                    worldIn.addFreshEntity(vexentity);
                }
                worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, this.getSoundSource(), 1.0F, 1.0F);
                this.SummonDown(entityLiving);
            }
    }

    public void StaffResult(ServerLevel worldIn, LivingEntity entityLiving) {
        this.commonResult(worldIn, entityLiving);
        if (!isShifting(entityLiving)) {
                for (int i1 = 0; i1 < 3 + worldIn.random.nextInt(3); ++i1) {
                    BlockPos blockpos = entityLiving.blockPosition().offset(-2 + entityLiving.getRandom().nextInt(5), 1, -2 + entityLiving.getRandom().nextInt(5));
                    AllyVex vexentity = new AllyVex(ModEntityType.ALLY_VEX.get(), worldIn);
                    vexentity.setOwnerId(entityLiving.getUUID());
                    vexentity.moveTo(blockpos, 0.0F, 0.0F);
                    vexentity.finalizeSpawn(worldIn, entityLiving.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                    vexentity.setBoundOrigin(blockpos);
                    if (SpellConfig.WandVexLimit.get() > VexLimit(entityLiving)) {
                        vexentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                    } else {
                        vexentity.setLimitedLife(1);
                        vexentity.addEffect(new MobEffectInstance(MobEffects.WITHER, 800, 1));
                    }
                    if (enchantment > 0) {
                        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(vexentity.getMainHandItem());
                        map.putIfAbsent(Enchantments.SHARPNESS, enchantment);
                        EnchantmentHelper.setEnchantments(map, vexentity.getMainHandItem());
                        vexentity.setItemSlot(EquipmentSlot.MAINHAND, vexentity.getMainHandItem());
                    }
                    this.SummonSap(entityLiving, vexentity);
                    this.setTarget(worldIn, entityLiving, vexentity);
                    worldIn.addFreshEntity(vexentity);
                }
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, this.getSoundSource(), 1.0F, 1.0F);
            this.SummonDown(entityLiving);
        }
    }

    public int VexLimit(LivingEntity entityLiving){
        return entityLiving.level.getNearbyEntities(AllyVex.class, this.vexCountTargeting, entityLiving, entityLiving.getBoundingBox().inflate(16.0D)).size();
    }
}
