package com.Polarice3.Goety.common.magic.spells.wild;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.CryptSlimeServant;
import com.Polarice3.Goety.common.entities.ally.MagmaCubeServant;
import com.Polarice3.Goety.common.entities.ally.SlimeServant;
import com.Polarice3.Goety.common.entities.ally.TropicalSlimeServant;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SlimySpell extends SummonSpell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.SlimyCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.SlimyDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WILD_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.SlimyCoolDown.get();
    }

    @Override
    public int SummonDownDuration() {
        return SpellConfig.SlimySummonDown.get();
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
        return livingEntity -> livingEntity instanceof SlimeServant;
    }

    @Override
    public int summonLimit() {
        return SpellConfig.SlimyLimit.get();
    }

    public boolean specialStaffs(ItemStack stack){
        return typeStaff(stack, SpellType.NECROMANCY)
                || typeStaff(stack, SpellType.NETHER)
                || typeStaff(stack, SpellType.ABYSS);
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
                i = 2 + caster.level.random.nextInt(2);
            } else if (specialStaffs(staff)){
                i = 2;
            }
            for (int i1 = 0; i1 < i; ++i1) {
                SlimeServant slimeServant = new SlimeServant(ModEntityType.SLIME_SERVANT.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), slimeServant, worldIn);
                if (caster.isUnderWater()){
                    blockPos = BlockFinder.SummonWaterRadius(caster, worldIn);
                }
                if (specialStaffs(staff)) {
                    if (typeStaff(staff, SpellType.ABYSS)) {
                        slimeServant = new TropicalSlimeServant(ModEntityType.TROPICAL_SLIME_SERVANT.get(), worldIn);
                    } else if (typeStaff(staff, SpellType.NECROMANCY)) {
                        slimeServant = new CryptSlimeServant(ModEntityType.CRYPT_SLIME_SERVANT.get(), worldIn);
                    } else if (typeStaff(staff, SpellType.NETHER)) {
                        slimeServant = new MagmaCubeServant(ModEntityType.MAGMA_CUBE_SERVANT.get(), worldIn);
                    }
                } else {
                    if (worldIn.isWaterAt(blockPos)) {
                        slimeServant = new TropicalSlimeServant(ModEntityType.TROPICAL_SLIME_SERVANT.get(), worldIn);
                    } else if (worldIn.dimension() == Level.NETHER) {
                        slimeServant = new MagmaCubeServant(ModEntityType.MAGMA_CUBE_SERVANT.get(), worldIn);
                    } else if (BlockFinder.findStructure(worldIn, blockPos, ModTags.Structures.CRYPT)) {
                        slimeServant = new CryptSlimeServant(ModEntityType.CRYPT_SLIME_SERVANT.get(), worldIn);
                    }
                }
                slimeServant.setTrueOwner(caster);
                slimeServant.moveTo(blockPos, 0.0F, 0.0F);
                if (slimeServant.getType() != ModEntityType.TROPICAL_SLIME_SERVANT.get()){
                    MobUtil.moveDownToGround(slimeServant);
                }
                slimeServant.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                slimeServant.setPersistenceRequired();
                slimeServant.finalizeSpawn(worldIn, caster.level.getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED,null,null);
                slimeServant.setSize(2, true);
                this.buffSummon(caster, slimeServant, potency);
                this.SummonSap(caster, slimeServant);
                this.setTarget(caster, slimeServant);
                if (worldIn.addFreshEntity(slimeServant)) {
                    this.uponSummon(worldIn, caster, staff, slimeServant);
                }
                this.summonAdvancement(caster, slimeServant);
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
        if (summoned.getType() == ModEntityType.SLIME_SERVANT.get()) {
            colorUtil = new ColorUtil(0x403b14);
            colorFrom = 0x403b14;
            colorTo = 0x5b4e1d;
        } else if (summoned.getType() == ModEntityType.CRYPT_SLIME_SERVANT.get()) {
            colorUtil = new ColorUtil(0x8FE6DF);
            colorFrom = 0x17b0e0;
        } else if (summoned.getType() == ModEntityType.MAGMA_CUBE_SERVANT.get()) {
            colorUtil = new ColorUtil(0xffa300);
            colorFrom = 0xffa300;
            colorTo = 0xffff6e;
        }
        ServerParticleUtil.summonUndeadParticles(worldIn, summoned, colorUtil, colorFrom, colorTo);
    }
}
