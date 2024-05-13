package com.Polarice3.Goety.common.magic.spells.necromancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.*;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinBruteServant;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
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

public class ZombieSpell extends SummonSpell {

    public int defaultSoulCost() {
        return SpellConfig.ZombieCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.ZombieDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.ZombieSummonDown.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SUMMON.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.ZombieCoolDown.get();
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
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving) {
        int count = 0;
        for (Entity entity : worldIn.getAllEntities()) {
            if (entity instanceof Summoned servant) {
                if (servant instanceof ZombieServant || servant instanceof ZPiglinServant) {
                    if (servant.getTrueOwner() == entityLiving && servant.isAlive()) {
                        ++count;
                    }
                }
            }
        }
        if (count >= SpellConfig.ZombieLimit.get()){
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
        }
        if (isShifting(entityLiving)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof ZombieServant) {
                    if (((ZombieServant) entity).getTrueOwner() == entityLiving) {
                        entity.moveTo(entityLiving.position());
                    }
                }
            }
            for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.POOF, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
            }
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.SUMMON_SPELL.get(), this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public boolean specialStaffs(ItemStack stack){
        return typeStaff(stack, SpellType.FROST)
                || typeStaff(stack, SpellType.WILD)
                || stack.is(ModItems.OMINOUS_STAFF.get());
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        this.commonResult(worldIn, entityLiving);
        if (!isShifting(entityLiving)) {
            int i = 1;
            if (staff.is(ModItems.NAMELESS_STAFF.get())){
                i = 7;
            } else if (rightStaff(staff)){
                i = 2 + entityLiving.level.random.nextInt(4);
            } else if (specialStaffs(staff)){
                i = 2;
            }
            for (int i1 = 0; i1 < i; ++i1) {
                Summoned summonedentity = new ZombieServant(ModEntityType.ZOMBIE_SERVANT.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(entityLiving, worldIn);
                if (entityLiving.isUnderWater()){
                    blockPos = BlockFinder.SummonWaterRadius(entityLiving, worldIn);
                }
                if (specialStaffs(staff)) {
                    if (staff.is(ModItems.FROST_STAFF.get())) {
                        summonedentity = new FrozenZombieServant(ModEntityType.FROZEN_ZOMBIE_SERVANT.get(), worldIn);
                    } else if (staff.is(ModItems.WILD_STAFF.get())) {
                        summonedentity = new JungleZombieServant(ModEntityType.JUNGLE_ZOMBIE_SERVANT.get(), worldIn);
                    } else if (staff.is(ModItems.OMINOUS_STAFF.get())) {
                        summonedentity = new ZombieVindicator(ModEntityType.ZOMBIE_VINDICATOR.get(), worldIn);
                    }
                } else if (entityLiving.isUnderWater() && worldIn.isWaterAt(blockPos)){
                    summonedentity = new DrownedServant(ModEntityType.DROWNED_SERVANT.get(), worldIn);
                } else if (worldIn.getBiome(blockPos).is(Tags.Biomes.IS_DESERT) && worldIn.canSeeSky(blockPos)){
                    summonedentity = new HuskServant(ModEntityType.HUSK_SERVANT.get(), worldIn);
                } else if (worldIn.dimension() == Level.NETHER){
                    Summoned summoned = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), worldIn);
                    if (worldIn.random.nextFloat() <= 0.25F && BlockFinder.findStructure(worldIn, entityLiving, BuiltinStructures.BASTION_REMNANT)){
                        summoned = new ZPiglinBruteServant(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), worldIn);
                    }
                    summonedentity = summoned;
                } else if (BlockFinder.findStructure(worldIn, blockPos, StructureTags.ON_WOODLAND_EXPLORER_MAPS)){
                    summonedentity = new ZombieVindicator(ModEntityType.ZOMBIE_VINDICATOR.get(), worldIn);
                } else if (worldIn.getBiome(blockPos).get().coldEnoughToSnow(blockPos)){
                    summonedentity = new FrozenZombieServant(ModEntityType.FROZEN_ZOMBIE_SERVANT.get(), worldIn);
                } else if (worldIn.getBiome(blockPos).is(BiomeTags.IS_JUNGLE) && worldIn.random.nextBoolean()){
                    summonedentity = new JungleZombieServant(ModEntityType.JUNGLE_ZOMBIE_SERVANT.get(), worldIn);
                }
                summonedentity.setTrueOwner(entityLiving);
                summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                if (summonedentity.getType() != ModEntityType.DROWNED_SERVANT.get()){
                    MobUtil.moveDownToGround(summonedentity);
                }
                summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                summonedentity.setPersistenceRequired();
                summonedentity.setUpgraded(this.NecroPower(entityLiving));
                summonedentity.finalizeSpawn(worldIn, entityLiving.level.getCurrentDifficultyAt(entityLiving.blockPosition()), MobSpawnType.MOB_SUMMONED,null,null);
                if (enchantment > 0){
                    int boost = Mth.clamp(enchantment - 1, 0, 10);
                    summonedentity.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), Integer.MAX_VALUE, boost, false, false));
                }
                this.SummonSap(entityLiving, summonedentity);
                this.setTarget(entityLiving, summonedentity);
                worldIn.addFreshEntity(summonedentity);
                this.summonAdvancement(entityLiving, entityLiving);
            }
            this.SummonDown(entityLiving);
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.SUMMON_SPELL.get(), this.getSoundSource(), 1.0F, 1.0F);
        }
    }
}
