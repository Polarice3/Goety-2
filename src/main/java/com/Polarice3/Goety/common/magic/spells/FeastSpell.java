package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.ChargingSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class FeastSpell extends ChargingSpell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRadius(16.0D);
    }

    public int defaultSoulCost() {
        return SpellConfig.FeastCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.FeastChargeUp.get();
    }

    @Override
    public int Cooldown() {
        return SpellConfig.FeastDuration.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.EVOKER_PREPARE_ATTACK;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ILL;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RADIUS.get());
        list.add(ModEnchantments.BURNING.get());
        list.add(ModEnchantments.ABSORB.get());
        return list;
    }

    public List<LivingEntity> getLivingEntities(ServerLevel worldIn, LivingEntity caster, SpellStat spellStat){
        int i = (int) caster.getX();
        int j = (int) caster.getY();
        int k = (int) caster.getZ();
        double radius = spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)){
            radius *= (WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 2.0D) + 1.0D;
        }
        return worldIn.getEntitiesOfClass(LivingEntity.class, (new AABB(i, j, k, i, j - 4, k)).inflate(radius));
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int castTime, SpellStat spellStat) {
        for (LivingEntity entity : this.getLivingEntities(worldIn, caster, spellStat)) {
            if (entity != caster && !MobUtil.areAllies(entity, caster)){
                BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getY() - 1.0F, entity.getZ());
                BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, worldIn.getBlockState(blockPos));
                for (int i = 0; i < 4; ++i) {
                    float radius = 1.0F;
                    if (this.rightStaff(staff)){
                        radius = 3.0F;
                    }
                    ServerParticleUtil.circularParticles(worldIn, option, entity.getX(), entity.getY() + 0.25D, entity.getZ(), radius);
                }
            }
        }
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        int potency = spellStat.getPotency();
        int burning = spellStat.getBurning();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
        }
        for (LivingEntity entity : this.getLivingEntities(worldIn, caster, spellStat)) {
            float f = (float) Mth.atan2(entity.getZ() - caster.getZ(), entity.getX() - caster.getX());
            if (entity != caster && !MobUtil.areAllies(entity, caster)){
                WandUtil.spawnFangs(caster, entity.getX(), entity.getZ(), entity.getY(), entity.getY() + 1.0D, f, 1, potency, burning);
                if (rightStaff(staff)) {
                    for (int i1 = 0; i1 < 5; ++i1) {
                        float f1 = f + (float) i1 * (float) Math.PI * 0.4F;
                        WandUtil.spawnFangs(caster, entity.getX() + (double) Mth.cos(f1) * 1.5D, entity.getZ() + (double) Mth.sin(f1) * 1.5D, entity.getY(), entity.getY() + 1.0D, f1, 0, potency, burning);
                    }
                }
            }
        }
        for(int i1 = 0; i1 < caster.level.random.nextInt(35) + 10; ++i1) {
            worldIn.sendParticles(ParticleTypes.POOF, caster.getX(), caster.getEyeY(), caster.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
        }
    }

}
