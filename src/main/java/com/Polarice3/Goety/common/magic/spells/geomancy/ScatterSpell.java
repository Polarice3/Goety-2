package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.ScatterMine;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SoundUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ScatterSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setDuration(10);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.ScatterCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.ScatterDuration.get();
    }

    @Override
    public int castDuration(LivingEntity caster, ItemStack staff) {
        if (rightStaff(staff)) {
            return MathHelper.secondsToTicks(5.15F);
        }
        return super.castDuration(caster, staff);
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.ScatterCoolDown.get();
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public void useParticle(Level worldIn, LivingEntity caster, ItemStack stack) {
        if (rightStaff(stack)) {
            for (int i = 0; i < 5; ++i) {
                double d0 = worldIn.random.nextGaussian() * 0.02D;
                double d1 = worldIn.random.nextGaussian() * 0.02D;
                double d2 = worldIn.random.nextGaussian() * 0.02D;
                if (worldIn instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticleTypes.BIG_ELECTRIC.get(), caster.getRandomX(0.5D), caster.getEyeY() - serverLevel.random.nextInt(5), caster.getRandomZ(0.5D), 0, d0, d1, d2, 0.5F);
                }
            }
        }
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int castTime, SpellStat spellStat) {
        if (rightStaff(staff)) {
            int potency = spellStat.getPotency();
            double radius = spellStat.getRadius();
            int duration = spellStat.getDuration();
            if (WandUtil.enchantedFocus(caster)){
                potency += WandUtil.getPotencyLevel(caster);
                radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 2.0F;
                duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
            }
            int time = MathHelper.secondsToTicks(5.15F - 1) / 14;
            if (castTime % time == 0) {
                BlockPos blockPos = caster.blockPosition();
                blockPos = blockPos.offset(-8 + worldIn.random.nextInt(16), 0, -8 + worldIn.random.nextInt(16));
                BlockPos blockPos2 = caster.blockPosition().offset(-8 + worldIn.random.nextInt(16), 0, -8 + worldIn.random.nextInt(16));
                Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
                Vec3 vec32 = Vec3.atBottomCenterOf(blockPos2);
                ScatterMine scatterMine = new ScatterMine(worldIn, caster, vec3);
                scatterMine.setExtraDamage(potency);
                scatterMine.setExtraRadius((float) radius);
                scatterMine.lifeTicks = MathHelper.secondsToTicks(duration);
                if (!worldIn.getEntitiesOfClass(ScatterMine.class, new AABB(blockPos)).isEmpty()) {
                    scatterMine.setPos(vec32.x(), vec32.y(), vec32.z());
                }
                if (worldIn.addFreshEntity(scatterMine)) {
                    if (worldIn.random.nextBoolean()) {
                        SoundUtil.playRedstoneMineLoad(scatterMine);
                    }
                }
            }
        }
        super.useSpell(worldIn, caster, staff, castTime, spellStat);
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        if (rightStaff(staff)){
            return;
        }
        int potency = spellStat.getPotency();
        double radius = spellStat.getRadius();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 2.0F;
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }
        int amount = 3;
        for (int i = 0; i < amount; ++i) {
            BlockPos blockPos = caster.blockPosition();
            blockPos = blockPos.offset(-4 + worldIn.random.nextInt(8), 0, -4 + worldIn.random.nextInt(8));
            BlockPos blockPos2 = caster.blockPosition().offset(-4 + worldIn.random.nextInt(8), 0, -4 + worldIn.random.nextInt(8));
            Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
            Vec3 vec31 = Vec3.atBottomCenterOf(blockPos2);
            ScatterMine scatterMine = new ScatterMine(worldIn, caster, vec3);
            scatterMine.setIsSpell();
            scatterMine.setExtraDamage(potency);
            scatterMine.setExtraRadius((float) radius);
            scatterMine.lifeTicks = MathHelper.secondsToTicks(duration);
            if (!worldIn.getEntitiesOfClass(ScatterMine.class, new AABB(blockPos)).isEmpty()) {
                scatterMine.setPos(vec31.x(), vec31.y(), vec31.z());
            }
            if (worldIn.addFreshEntity(scatterMine)) {
                SoundUtil.playRedstoneMineLoad(scatterMine);
            }
        }
    }
}
