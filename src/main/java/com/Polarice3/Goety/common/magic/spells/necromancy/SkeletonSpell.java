package com.Polarice3.Goety.common.magic.spells.necromancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.*;
import com.Polarice3.Goety.common.entities.neutral.DrownedNecromancer;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SoundUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SkeletonSpell extends SummonSpell {

    public int defaultSoulCost() {
        return SpellConfig.SkeletonCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.SkeletonDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.SkeletonSummonDown.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SUMMON.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.SkeletonCoolDown.get();
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
        return list;
    }

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof AbstractSkeletonServant;
    }

    @Override
    public int summonLimit() {
        return SpellConfig.SkeletonLimit.get();
    }

    public boolean specialStaffs(ItemStack stack){
        return typeStaff(stack, SpellType.FROST)
                || typeStaff(stack, SpellType.STORM)
                || typeStaff(stack, SpellType.WILD)
                || typeStaff(stack, SpellType.NETHER)
                || typeStaff(stack, SpellType.ABYSS)
                || stack.is(ModItems.OMINOUS_STAFF.get());
    }

    @Override
    public void commonResult(ServerLevel worldIn, LivingEntity caster) {
        if (isShifting(caster)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof LivingEntity livingEntity && livingEntity instanceof AbstractSkeletonServant && !(livingEntity instanceof DrownedNecromancer)) {
                    this.teleportServants(caster, entity);
                }
            }
            this.commonResultHit(worldIn, caster);
        }
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }
        int i = 1;
        if (staff.is(ModItems.NAMELESS_STAFF.get())){
            i = 7;
        } else if (rightStaff(staff)){
            i = 2 + caster.level.random.nextInt(4);
        } else if (specialStaffs(staff)){
            i = 2;
        }
        if (!isShifting(caster)) {
            for (int i1 = 0; i1 < i; ++i1) {
                EntityType<?> entityType;
                AbstractSkeletonServant summonedentity = new SkeletonServant(ModEntityType.SKELETON_SERVANT.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, worldIn);
                if (caster.isUnderWater()){
                    blockPos = BlockFinder.SummonWaterRadius(caster, worldIn);
                }
                if (specialStaffs(staff)) {
                    if (typeStaff(staff, SpellType.FROST)) {
                        summonedentity = new StrayServant(ModEntityType.STRAY_SERVANT.get(), worldIn);
                    } else if (typeStaff(staff, SpellType.STORM)) {
                        summonedentity = new RattledServant(ModEntityType.RATTLED_SERVANT.get(), worldIn);
                    } else if (typeStaff(staff, SpellType.WILD)) {
                        summonedentity = new MossySkeletonServant(ModEntityType.MOSSY_SKELETON_SERVANT.get(), worldIn);
                    } else if (typeStaff(staff, SpellType.NETHER)) {
                        summonedentity = new WitherSkeletonServant(ModEntityType.WITHER_SKELETON_SERVANT.get(), worldIn);
                    } else if (typeStaff(staff, SpellType.ABYSS)) {
                        summonedentity = new SunkenSkeletonServant(ModEntityType.SUNKEN_SKELETON_SERVANT.get(), worldIn);
                    } else if (staff.is(ModItems.OMINOUS_STAFF.get())) {
                        summonedentity = new SkeletonPillagerServant(ModEntityType.SKELETON_PILLAGER_SERVANT.get(), worldIn);
                    }
                } else {
                    Player player = null;
                    if (caster instanceof Player player1){
                        player = player1;
                    }
                    entityType = summonedentity.getVariant(player, worldIn, blockPos);
                    if (entityType != null){
                        Entity entity = entityType.create(worldIn);
                        if (entity instanceof AbstractSkeletonServant summoned){
                            summonedentity = summoned;
                        }
                    }
                }
                summonedentity.setTrueOwner(caster);
                summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                if (summonedentity.getType() != ModEntityType.SUNKEN_SKELETON_SERVANT.get()){
                    MobUtil.moveDownToGround(summonedentity);
                }
                summonedentity.setPersistenceRequired();
                summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                summonedentity.setArrowPower(potency);
                summonedentity.finalizeSpawn(worldIn, caster.level.getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                this.buffSummon(caster, summonedentity, potency);
                this.SummonSap(caster, summonedentity);
                this.setTarget(caster, summonedentity);
                if (worldIn.addFreshEntity(summonedentity)) {
                    this.uponSummon(worldIn, caster, staff, summonedentity);
                }
                this.summonAdvancement(caster, summonedentity);
            }
            this.SummonDown(caster);
            SoundUtil.playNecromancerSummon(caster);
        }
    }
}
