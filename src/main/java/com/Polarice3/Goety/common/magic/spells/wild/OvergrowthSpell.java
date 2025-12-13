package com.Polarice3.Goety.common.magic.spells.wild;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OvergrowthSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.OvergrowthCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.OvergrowthDuration.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WILD_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.OvergrowthCoolDown.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    public Vec3 getHorizontalLeftLookAngle(LivingEntity livingEntity) {
        return MobUtil.calculateViewVector(0, livingEntity.getYRot() - 90);
    }

    public Vec3 getHorizontalRightLookAngle(LivingEntity livingEntity) {
        return MobUtil.calculateViewVector(0, livingEntity.getYRot() + 90);
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int range = spellStat.getRange();
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getRangeLevel(caster);
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, caster, range, 3);
        LivingEntity target = this.getTarget(caster, range);
        if (target != null){
            if (this.isShifting(caster)){
                int x = (int) (this.getHorizontalLeftLookAngle(caster).x * 2);
                int z = (int) (this.getHorizontalLeftLookAngle(caster).z * 2);
                int x1 = (int) (this.getHorizontalRightLookAngle(caster).x * 2);
                int z1 = (int) (this.getHorizontalRightLookAngle(caster).z * 2);
                BlockPos left = new BlockPos(caster.blockPosition().offset(x, 0, z));
                BlockPos right = new BlockPos(caster.blockPosition().offset(x1, 0, z1));
                EntityType<? extends AbstractMonolith> entityType = ModEntityType.POISON_QUILL_VINE.get();
                WandUtil.summonTurret(caster, BlockFinder.SummonPosition(caster, left), entityType, target, duration, potency);
                if (rightStaff(staff)) {
                    WandUtil.summonTurret(caster, BlockFinder.SummonPosition(caster, right), entityType, target, duration, potency);
                }
            } else {
                int random = worldIn.random.nextInt(5);
                Direction direction = Direction.fromYRot(target.getYHeadRot());
                EntityType<? extends AbstractMonolith> entityType = ModEntityType.QUICK_GROWING_VINE.get();
                if (random == 0) {
                    WandUtil.summonMinorSquareTrap(caster, target, entityType, direction, duration);
                } else if (random == 1) {
                    WandUtil.summonHallTrap(caster, target, entityType, duration);
                } else if (random == 2) {
                    WandUtil.summonCubeTrap(caster, target, entityType, duration);
                } else if (random == 3) {
                    WandUtil.summonCircleTrap(caster, target, entityType, direction, duration);
                } else {
                    WandUtil.summonSurroundTrap(caster, target, entityType, duration);
                }
            }
        } else if (rayTraceResult instanceof BlockHitResult){
            BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
            if (this.isShifting(caster)){
                EntityType<? extends AbstractMonolith> entityType = ModEntityType.POISON_QUILL_VINE.get();
                WandUtil.summonTurret(caster, blockPos, entityType, null, duration, potency);
            } else {
                EntityType<? extends AbstractMonolith> entityType = ModEntityType.QUICK_GROWING_VINE.get();
                WandUtil.summonCubeTrap(caster, blockPos, entityType, duration);
            }
        }
    }
}
