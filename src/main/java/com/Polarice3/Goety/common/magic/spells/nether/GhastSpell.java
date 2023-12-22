package com.Polarice3.Goety.common.magic.spells.nether;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.AllyVex;
import com.Polarice3.Goety.common.entities.ally.MiniGhast;
import com.Polarice3.Goety.common.magic.SummonSpells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class GhastSpell extends SummonSpells {
    private final TargetingConditions ghastCountTargeting = TargetingConditions.DEFAULT.range(64.0D).ignoreInvisibilityTesting();

    public int defaultSoulCost() {
        return SpellConfig.GhastCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.GhastDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.GhastSummonDown.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.GhastCoolDown.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SUMMON.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    public boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving){
        return ghastLimit(entityLiving) < SpellConfig.GhastLimit.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    public void commonResult(ServerLevel worldIn, LivingEntity entityLiving){
        if (WandUtil.enchantedFocus(entityLiving)){
            enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            duration = WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving) + 1;
        }
        if (isShifting(entityLiving)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof MiniGhast) {
                    if (((MiniGhast) entity).getTrueOwner() == entityLiving) {
                        ((MiniGhast) entity).lifeSpanDamage();
                    }
                }
            }
            for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.POOF, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
            }
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.SUMMON_SPELL.get(), this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        this.commonResult(worldIn, entityLiving);
        if (!isShifting(entityLiving)) {
            int i = 3;
            for (int i1 = 0; i1 < i; ++i1) {
                BlockPos blockpos = entityLiving.blockPosition().offset(-2 + entityLiving.getRandom().nextInt(5), 1, -2 + entityLiving.getRandom().nextInt(5));
                MiniGhast miniGhast = new MiniGhast(ModEntityType.MINI_GHAST.get(), worldIn);
                miniGhast.setTrueOwner(entityLiving);
                miniGhast.moveTo(blockpos, 0.0F, 0.0F);
                miniGhast.finalizeSpawn(worldIn, entityLiving.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                miniGhast.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                if (enchantment > 0){
                    int boost = Mth.clamp(enchantment, 1, 10);
                    miniGhast.setFireBallDamage(miniGhast.getFireBallDamage() + boost);
                }
                this.SummonSap(entityLiving, miniGhast);
                this.setTarget(worldIn, entityLiving, miniGhast);
                worldIn.addFreshEntity(miniGhast);
                this.summonAdvancement(entityLiving, miniGhast);
            }
            this.SummonDown(entityLiving);
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.SUMMON_SPELL.get(), this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public int ghastLimit(LivingEntity entityLiving){
        return entityLiving.level.getNearbyEntities(AllyVex.class, this.ghastCountTargeting, entityLiving, entityLiving.getBoundingBox().inflate(64.0D)).size();
    }
}
