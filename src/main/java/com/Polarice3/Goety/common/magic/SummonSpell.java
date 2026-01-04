package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.magic.ISummonSpell;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public abstract class SummonSpell extends Spell implements ISummonSpell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setDuration(1).setBurning(0);
    }

    public abstract int SummonDownDuration();

    public boolean NecroPower(LivingEntity entityLiving){
        return CuriosFinder.hasUndeadCape(entityLiving);
    }

    public boolean FrostPower(LivingEntity entityLiving){
        return CuriosFinder.hasFrostRobes(entityLiving);
    }

    public boolean WildPower(LivingEntity entityLiving){
        return CuriosFinder.hasWildRobe(entityLiving);
    }

    public boolean NetherPower(LivingEntity entityLiving){
        return CuriosFinder.hasNetherRobe(entityLiving);
    }

    public int summonLimit(){
        return 64;
    }

    public Predicate<LivingEntity> summonPredicate(){
        return livingEntity -> livingEntity instanceof IOwned;
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster) {
        int count = 0;
        for (Entity entity : worldIn.getAllEntities()) {
            if (entity instanceof LivingEntity livingEntity && entity instanceof IOwned owned) {
                if (this.summonPredicate().test(livingEntity)) {
                    if (owned.getTrueOwner() == caster && livingEntity.isAlive()) {
                        ++count;
                    }
                }
            }
        }
        if (count >= this.summonLimit() && !this.isShifting(caster)){
            if (caster instanceof Player player) {
                player.displayClientMessage(Component.translatable("info.goety.summon.limit"), true);
            }
            return false;
        } else {
            return super.conditionsMet(worldIn, caster);
        }
    }

    @Override
    public void commonResult(ServerLevel worldIn, LivingEntity caster) {
        if (isShifting(caster)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof LivingEntity livingEntity && this.summonPredicate().test(livingEntity)) {
                    this.teleportServants(caster, entity);
                }
            }
            this.commonResultHit(worldIn, caster);
        }
    }

    public void commonResultHit(ServerLevel worldIn, LivingEntity caster){
        for (int i = 0; i < caster.level.random.nextInt(35) + 10; ++i) {
            worldIn.sendParticles(ParticleTypes.POOF, caster.getX(), caster.getEyeY(), caster.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
        }
        this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
    }

    public void summonAdvancement(LivingEntity summoner, LivingEntity summoned){
        if(summoner instanceof ServerPlayer serverPlayer){
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, summoned);
        }
    }

    public void buffSummon(LivingEntity caster, LivingEntity summoned, int potency){
        if (potency > 0 && !this.hasSummonDown(caster)){
            int boost = Mth.clamp(potency - 1, 0, 10);
            summoned.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), EffectsUtil.infiniteEffect(), boost, false, false));
        }
    }

    public void SummonDown(LivingEntity entityLiving){
        if (SpellConfig.SummonDown.get()){
            ISummonSpell.super.SummonDown(entityLiving);
        }
    }

    public void teleportServants(LivingEntity owner, Entity servant){
        if (servant instanceof IOwned servant1 && servant instanceof LivingEntity servant2) {
            if (servant1.getTrueOwner() == owner && !SEHelper.isGrounded(owner, servant2)) {
                servant.moveTo(owner.position());
            }
        }
    }

    public void summonParticles(ServerLevel worldIn, LivingEntity caster, ItemStack staff, LivingEntity summoned) {
        ColorUtil colorUtil = new ColorUtil(0x8FE6DF);
        int colorFrom = 0x17b0e0;
        int colorTo = 0xffffff;
        if (staff.is(ModItems.NAMELESS_STAFF.get())) {
            colorUtil = new ColorUtil(0xa7fc3e);
            colorFrom = 0xa7fc3e;
            colorTo = 0xcffc97;
        } else if (this.typeStaff(staff, SpellType.WILD)) {
            colorUtil = new ColorUtil(0x403b14);
            colorFrom = 0x403b14;
            colorTo = 0x5b4e1d;
        } else if (this.typeStaff(staff, SpellType.NETHER)) {
            colorUtil = new ColorUtil(0xffa300);
            colorFrom = 0xffa300;
            colorTo = 0xffff6e;
        }
        ServerParticleUtil.summonUndeadParticles(worldIn, summoned, colorUtil, colorFrom, colorTo);
    }
}
