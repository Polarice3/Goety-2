package com.Polarice3.Goety.common.magic.spells.wild;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.*;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class HuntingSpell extends SummonSpell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.HuntingCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.HuntingDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WILD_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.HuntingCoolDown.get();
    }

    @Override
    public int SummonDownDuration() {
        return SpellConfig.HuntingSummonDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WILD;
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
        return livingEntity -> livingEntity instanceof BlackWolf || livingEntity instanceof SkeletonWolf || livingEntity instanceof TwilightGoat || livingEntity instanceof Snapper;
    }

    @Override
    public int summonLimit() {
        return SpellConfig.HuntingLimit.get();
    }

    @Override
    public void commonResult(ServerLevel worldIn, LivingEntity caster) {
        if (isShifting(caster)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof LivingEntity livingEntity && summonPredicate().test(livingEntity)) {
                    if (livingEntity instanceof Snapper){
                        if (caster.isUnderWater()){
                            this.teleportServants(caster, entity);
                        }
                    } else {
                        this.teleportServants(caster, entity);
                    }
                }
            }
            this.commonResultHit(worldIn, caster);
        }
    }

    public boolean specialStaffs(ItemStack stack){
        return typeStaff(stack, SpellType.NECROMANCY)
                || typeStaff(stack, SpellType.WIND)
                || typeStaff(stack, SpellType.STORM)
                || typeStaff(stack, SpellType.ABYSS)
                || typeStaff(stack, SpellType.FROST)
                || typeStaff(stack, SpellType.NETHER);
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }
        if (!isShifting(caster)) {
            int i = 1;
            if (rightStaff(staff)){
                i = 3;
            } else if (specialStaffs(staff)){
                i = 2;
            }
            for (int i1 = 0; i1 < i; ++i1) {
                Summoned summonedentity = new BlackWolf(ModEntityType.BLACK_WOLF.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, worldIn);
                if (caster.isUnderWater()){
                    blockPos = BlockFinder.SummonWaterRadius(caster, worldIn);
                }
                if (this.typeStaff(staff, SpellType.NECROMANCY)){
                    summonedentity = new SkeletonWolf(ModEntityType.SKELETON_WOLF.get(), worldIn);
                } else if ((worldIn.isThundering() && worldIn.canSeeSky(blockPos)) || this.typeStaff(staff, SpellType.STORM)) {
                    summonedentity = new Stormhound(ModEntityType.STORMHOUND.get(), worldIn);
                } else if (worldIn.getBiome(blockPos).is(Tags.Biomes.IS_MOUNTAIN) || this.typeStaff(staff, SpellType.WIND)){
                    summonedentity = new TwilightGoat(ModEntityType.TWILIGHT_GOAT.get(), worldIn);
                } else if (worldIn.isWaterAt(blockPos) || this.typeStaff(staff, SpellType.ABYSS)) {
                    summonedentity = new Snapper(ModEntityType.SNAPPER.get(), worldIn);
                } else if (worldIn.getBiome(blockPos).get().coldEnoughToSnow(blockPos) || this.typeStaff(staff, SpellType.FROST)) {
                    summonedentity = new WinterWolf(ModEntityType.WINTER_WOLF.get(), worldIn);
                } else if (worldIn.dimensionType().ultraWarm() || worldIn.getBiome(blockPos).is(BiomeTags.IS_NETHER) || this.typeStaff(staff, SpellType.NETHER)){
                    summonedentity = new Hellhound(ModEntityType.HELLHOUND.get(), worldIn);
                }
                summonedentity.setTrueOwner(caster);
                summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                if (summonedentity.getType() != ModEntityType.SNAPPER.get()){
                    MobUtil.moveDownToGround(summonedentity);
                }
                summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                summonedentity.setPersistenceRequired();
                summonedentity.finalizeSpawn(worldIn, caster.level.getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED,null,null);
                this.buffSummon(caster, summonedentity, potency);
                this.SummonSap(caster, summonedentity);
                this.setTarget(caster, summonedentity);
                if (worldIn.addFreshEntity(summonedentity)) {
                    this.uponSummon(worldIn, caster, staff, summonedentity);
                }
                this.summonAdvancement(caster, summonedentity);
            }
            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }

    @Override
    public void summonParticles(ServerLevel worldIn, LivingEntity caster, ItemStack staff, LivingEntity summoned) {
        ColorUtil colorUtil = ColorUtil.WHITE;
        int colorFrom = 0xffffff;
        int colorTo = 0xffffff;
        if (summoned.getType() == ModEntityType.BLACK_WOLF.get()) {
            colorUtil = new ColorUtil(0x0a080a);
            colorFrom = 0x0a080a;
            colorTo = 0x0a080a;
        } else if (summoned.getType() == ModEntityType.HELLHOUND.get()) {
            colorUtil = new ColorUtil(0xffa300);
            colorFrom = 0xffa300;
            colorTo = 0xffff6e;
        }
        ServerParticleUtil.summonUndeadParticles(worldIn, summoned, colorUtil, colorFrom, colorTo);
    }
}
