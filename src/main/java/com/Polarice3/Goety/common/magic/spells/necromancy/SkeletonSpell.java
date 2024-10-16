package com.Polarice3.Goety.common.magic.spells.necromancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.*;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraftforge.common.Tags;

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

    public void commonResult(ServerLevel worldIn, LivingEntity entityLiving){
        if (WandUtil.enchantedFocus(entityLiving)){
            enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            duration = WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving) + 1;
        }
        if (isShifting(entityLiving)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof AbstractSkeletonServant skeletonServant) {
                    if (skeletonServant.getTrueOwner() == entityLiving) {
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

    public boolean specialStaffs(ItemStack stack){
        return typeStaff(stack, SpellType.FROST)
                || typeStaff(stack, SpellType.WILD)
                || typeStaff(stack, SpellType.NETHER)
                || stack.is(ModItems.OMINOUS_STAFF.get());
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        this.commonResult(worldIn, entityLiving);
        int i = 1;
        if (staff.is(ModItems.NAMELESS_STAFF.get())){
            i = 7;
        } else if (rightStaff(staff)){
            i = 2 + entityLiving.level.random.nextInt(4);
        } else if (specialStaffs(staff)){
            i = 2;
        }
        if (!isShifting(entityLiving)) {
            for (int i1 = 0; i1 < i; ++i1) {
                AbstractSkeletonServant summonedentity = new SkeletonServant(ModEntityType.SKELETON_SERVANT.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(entityLiving.blockPosition(), summonedentity, worldIn);
                if (entityLiving.isUnderWater()){
                    blockPos = BlockFinder.SummonWaterRadius(entityLiving, worldIn);
                }
                if (specialStaffs(staff)) {
                    if (typeStaff(staff, SpellType.FROST)) {
                        summonedentity = new StrayServant(ModEntityType.STRAY_SERVANT.get(), worldIn);
                    } else if (typeStaff(staff, SpellType.WILD)) {
                        summonedentity = new MossySkeletonServant(ModEntityType.MOSSY_SKELETON_SERVANT.get(), worldIn);
                    } else if (typeStaff(staff, SpellType.NETHER)) {
                        summonedentity = new WitherSkeletonServant(ModEntityType.WITHER_SKELETON_SERVANT.get(), worldIn);
                    } else if (staff.is(ModItems.OMINOUS_STAFF.get())) {
                        summonedentity = new SkeletonPillagerServant(ModEntityType.SKELETON_PILLAGER_SERVANT.get(), worldIn);
                    }
                } else if (worldIn.dimension() == Level.NETHER
                        && (entityLiving instanceof Player player && SEHelper.hasResearch(player, ResearchList.BYGONE)    )
                        && BlockFinder.findStructure(worldIn, entityLiving, BuiltinStructures.FORTRESS)){
                    summonedentity = new WitherSkeletonServant(ModEntityType.WITHER_SKELETON_SERVANT.get(), worldIn);
                } else if (worldIn.getBiome(blockPos).is(Tags.Biomes.IS_COLD_OVERWORLD) && worldIn.canSeeSky(blockPos)){
                    summonedentity = new StrayServant(ModEntityType.STRAY_SERVANT.get(), worldIn);
                } else if (BlockFinder.findStructure(worldIn, entityLiving, BuiltinStructures.PILLAGER_OUTPOST)){
                    summonedentity = new SkeletonPillagerServant(ModEntityType.SKELETON_PILLAGER_SERVANT.get(), worldIn);
                } else if (worldIn.getBiome(blockPos).is(BiomeTags.IS_JUNGLE) && worldIn.random.nextBoolean()){
                    summonedentity = new MossySkeletonServant(ModEntityType.MOSSY_SKELETON_SERVANT.get(), worldIn);
                } else if (entityLiving.isUnderWater() && worldIn.isWaterAt(blockPos)){
                    summonedentity = new SunkenSkeletonServant(ModEntityType.SUNKEN_SKELETON_SERVANT.get(), worldIn);
                }
                summonedentity.setTrueOwner(entityLiving);
                summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                if (summonedentity.getType() != ModEntityType.SUNKEN_SKELETON_SERVANT.get()){
                    MobUtil.moveDownToGround(summonedentity);
                }
                summonedentity.setPersistenceRequired();
                summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                summonedentity.setArrowPower(enchantment);
                summonedentity.finalizeSpawn(worldIn, entityLiving.level.getCurrentDifficultyAt(entityLiving.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                this.SummonSap(entityLiving, summonedentity);
                this.setTarget(entityLiving, summonedentity);
                if (worldIn.addFreshEntity(summonedentity)) {
                    ColorUtil colorUtil = new ColorUtil(0x2ac9cf);
                    ServerParticleUtil.windShockwaveParticle(worldIn, colorUtil, 0.1F, 0.1F, 0.05F, -1, summonedentity.position());
                }
                this.summonAdvancement(entityLiving, summonedentity);
            }
            this.SummonDown(entityLiving);
            SoundUtil.playNecromancerSummon(entityLiving);
        }
    }
}
