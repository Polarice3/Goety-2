package com.Polarice3.Goety.common.magic.spells.necromancy;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.VanguardServant;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SummonSpells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
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

public class VanguardSpell extends SummonSpells {

    public int SoulCost() {
        return SpellConfig.VanguardCost.get();
    }

    public int CastDuration() {
        return SpellConfig.VanguardDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.VanguardSummonDown.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SUMMON.get();
    }

    @Override
    public int SpellCooldown() {
        return SpellConfig.VanguardCoolDown.get();
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

    public void commonResult(ServerLevel worldIn, LivingEntity entityLiving){
        if (WandUtil.enchantedFocus(entityLiving)){
            enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            duration = WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving) + 1;
        }
        if (isShifting(entityLiving)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof VanguardServant) {
                    if (((VanguardServant) entity).getTrueOwner() == entityLiving) {
                        entity.moveTo(entityLiving.position());
                    }
                }
            }
            for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.POOF, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
            }
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.VANGUARD_SUMMON.get(), this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        this.commonResult(worldIn, entityLiving);
        if (!isShifting(entityLiving)) {
            int i = 1;
            if (staff.is(ModItems.NAMELESS_STAFF.get())){
                i = 7;
            } else if (rightStaff(staff)){
                i = 2 + entityLiving.level.random.nextInt(6);
            }
            for (int i1 = 0; i1 < i; ++i1) {
                BlockPos blockPos = BlockFinder.SummonRadius(entityLiving, worldIn);
                VanguardServant summonedentity = new VanguardServant(ModEntityType.VANGUARD_SERVANT.get(), worldIn);
                summonedentity.setTrueOwner(entityLiving);
                summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                MobUtil.moveDownToGround(summonedentity);
                summonedentity.setPersistenceRequired();
                summonedentity.setUpgraded(this.NecroPower(entityLiving));
                summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                if (enchantment > 0){
                    int boost = Mth.clamp(enchantment - 1, 0, 10);
                    summonedentity.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), Integer.MAX_VALUE, boost, false, false));
                }
                summonedentity.finalizeSpawn(worldIn, entityLiving.level.getCurrentDifficultyAt(entityLiving.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                this.SummonSap(entityLiving, summonedentity);
                this.setTarget(worldIn, entityLiving, summonedentity);
                worldIn.addFreshEntity(summonedentity);
                this.summonAdvancement(entityLiving, summonedentity);
            }
            this.SummonDown(entityLiving);
            worldIn.playSound((Player) null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.VANGUARD_SUMMON.get(), this.getSoundSource(), 1.0F, 1.0F);
        }
    }
}
