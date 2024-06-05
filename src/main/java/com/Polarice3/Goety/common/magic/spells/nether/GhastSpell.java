package com.Polarice3.Goety.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.GhastServant;
import com.Polarice3.Goety.common.entities.ally.MiniGhast;
import com.Polarice3.Goety.common.entities.hostile.servants.Malghast;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GhastSpell extends SummonSpell {

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

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof Malghast;
    }

    @Override
    public int summonLimit() {
        return SpellConfig.GhastLimit.get();
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
                if (entity instanceof Malghast malghast) {
                    if (malghast.getTrueOwner() == entityLiving) {
                        malghast.lifeSpanDamage();
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
            if (rightStaff(staff)){
                i = 1 + worldIn.random.nextInt(1);
            }
            for (int i1 = 0; i1 < i; ++i1) {
                BlockPos blockpos = entityLiving.blockPosition().offset(-2 + entityLiving.getRandom().nextInt(5), 1, -2 + entityLiving.getRandom().nextInt(5));
                Malghast ghast = new MiniGhast(ModEntityType.MINI_GHAST.get(), worldIn);
                if (rightStaff(staff)){
                    ghast = new GhastServant(ModEntityType.GHAST_SERVANT.get(), worldIn);
                    blockpos = BlockFinder.SummonFlyingRadius(entityLiving.blockPosition().above(2), ghast, worldIn, 12);
                }
                ghast.setTrueOwner(entityLiving);
                ghast.moveTo(blockpos, entityLiving.getYRot(), 0.0F);
                ghast.finalizeSpawn(worldIn, entityLiving.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                ghast.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                this.SummonSap(entityLiving, ghast);
                int boost = Mth.clamp(enchantment, 0, 10);
                ghast.setFireBallDamage(boost - EffectsUtil.getAmplifierPlus(entityLiving, MobEffects.WEAKNESS));
                float extraBlast = Mth.clamp(enchantment, 0, SpellConfig.MaxRadiusLevel.get()) / 2.5F;
                ghast.setExplosionPower(ghast.getExplosionPower() + extraBlast);
                this.setTarget(entityLiving, ghast);
                worldIn.addFreshEntity(ghast);
                this.summonAdvancement(entityLiving, ghast);
            }
            this.SummonDown(entityLiving);
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.SUMMON_SPELL.get(), this.getSoundSource(), 1.0F, 1.0F);
        }
    }
}
