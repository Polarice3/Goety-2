package com.Polarice3.Goety.common.magic.spells.necromancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.HauntedSkull;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class HauntedSkullSpell extends SummonSpell {
    public int burning = 0;
    public int radius = 0;

    public int defaultSoulCost() {
        return SpellConfig.HauntedSkullCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.HauntedSkullDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.HauntedSkullSummonDown.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SUMMON.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.HauntedSkullCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.BURNING.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving) {
        int count = 0;
        for (Entity entity : worldIn.getAllEntities()) {
            if (entity instanceof HauntedSkull servant) {
                if (servant.getTrueOwner() == entityLiving && servant.isAlive()) {
                    ++count;
                }
            }
        }
        if (count >= SpellConfig.SkullLimit.get()){
            if (entityLiving instanceof Player player) {
                player.displayClientMessage(Component.translatable("info.goety.summon.limit"), true);
            }
            return false;
        } else {
            return super.conditionsMet(worldIn, entityLiving);
        }
    }

    public void commonResult(ServerLevel worldIn, LivingEntity entityLiving){
        if (WandUtil.enchantedFocus(entityLiving)){
            enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            duration = WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving) + 1;
            burning = WandUtil.getLevels(ModEnchantments.BURNING.get(), entityLiving);
            radius = WandUtil.getLevels(ModEnchantments.RADIUS.get(), entityLiving);
        }
        if (isShifting(entityLiving)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof HauntedSkull) {
                    if (((HauntedSkull) entity).getTrueOwner() == entityLiving) {
                        entity.moveTo(entityLiving.position());
                    }
                }
            }
            for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.POOF, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
            }
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        this.commonResult(worldIn, entityLiving);
        if (!isShifting(entityLiving)) {
            int i = 1;
            if (rightStaff(staff)) {
                i = 3;
            }
            for (int i1 = 0; i1 < i; ++i1) {
                BlockPos blockpos = entityLiving.blockPosition().offset(-2 + entityLiving.getRandom().nextInt(5), 1, -2 + entityLiving.getRandom().nextInt(5));
                HauntedSkull summonedentity = new HauntedSkull(ModEntityType.HAUNTED_SKULL.get(), worldIn);
                summonedentity.setTrueOwner(entityLiving);
                summonedentity.moveTo(blockpos, 0.0F, 0.0F);
                summonedentity.finalizeSpawn(worldIn, entityLiving.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                summonedentity.setBoundOrigin(blockpos);
                summonedentity.setLimitedLife(MathHelper.minutesToTicks(1) * duration);
                if (enchantment > 0) {
                    int boost = Mth.clamp(enchantment - 1, 0, 10);
                    summonedentity.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), Integer.MAX_VALUE, boost));
                }
                if (radius > 0) {
                    summonedentity.setExplosionPower(1.0F + radius / 4.0F);
                }
                if (burning > 0) {
                    summonedentity.setBurning(burning);
                }
                this.setTarget(entityLiving, summonedentity);
                summonedentity.setUpgraded(this.NecroPower(entityLiving));
                worldIn.addFreshEntity(summonedentity);
                this.summonAdvancement(entityLiving, summonedentity);
            }
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, this.getSoundSource(), 1.0F, 1.0F);
        }
    }
}
