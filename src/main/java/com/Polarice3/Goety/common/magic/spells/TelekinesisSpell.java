package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

//Tweaked to be like @iron431's spell: https://github.com/iron431/irons-spells-n-spellbooks/blob/1.20.1/src/main/java/io/redspace/ironsspellbooks/spells/eldritch/TelekinesisSpell.java
public class TelekinesisSpell extends EverChargeSpell {
    public Entity victim = null;

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setPotency(1);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.TelekinesisCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.TelekinesisChargeUp.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster, SpellStat spellStat) {
        if (caster.hurtTime > 0){
            return false;
        }
        if (this.victim != null) {
            int range = spellStat.getRange();
            if (WandUtil.enchantedFocus(caster)){
                range += WandUtil.getRangeLevel(caster);
            }
            if (this.victim.distanceTo(caster) > range) {
                this.victim = null;
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void stopSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, ItemStack focus, int castTime, SpellStat spellStat) {
        this.victim = null;
        if (caster instanceof Player player){
            SEHelper.addCooldown(player, focus.getItem(), MathHelper.secondsToTicks(5));
            SEHelper.sendSEUpdatePacket(player);
        }
        super.stopSpell(worldIn, caster, staff, focus, castTime, spellStat);
    }

    @Override
    public void startSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        super.startSpell(worldIn, caster, staff, spellStat);
        this.victim = null;
        this.findVictim(worldIn, caster, spellStat);
    }

    public void findVictim(ServerLevel worldIn, LivingEntity caster, SpellStat spellStat) {
        double potency = spellStat.getPotency();
        int range = spellStat.getRange();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster) / 2.0D;
            range += WandUtil.getRangeLevel(caster);
        }
        Entity target = MobUtil.getSingleTarget(worldIn, caster, range, 3, EntitySelector.NO_CREATIVE_OR_SPECTATOR);
        if (caster instanceof Mob mob) {
            target = mob.getTarget();
        }
        if (target != null) {
            boolean flag = true;
            if ((caster.getBoundingBox().inflate(0.5D).getSize() * potency) >= target.getBoundingBox().getSize()) {
                if (this.victim instanceof LivingEntity livingTarget){
                    if (livingTarget.getMaxHealth() >= SpellConfig.TelekinesisMaxHealth.get()
                            || MobUtil.hasEntityTypesConfig(SpellConfig.TelekinesisBlackList.get(), livingTarget.getType())){
                        flag = false;
                    }
                }
            }
            if (flag) {
                this.victim = target;
            }
        }
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        double potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster) / 2.0D;
        }
        if (this.victim != null){
            try {
                Vec3 vec3 = this.victim.getDeltaMovement();
                Vec3 force = (caster.getLookAngle()
                        .normalize()
                        .scale(Math.max(caster.distanceTo(this.victim), 4))
                        .add(caster.position())
                        .subtract(this.victim.position()))
                        .scale(potency);
                if (force.y > 0) {
                    this.victim.resetFallDistance();
                }
                Vec3 newMotion = force.subtract(vec3).scale(0.25).add(vec3);
                Vec3 delta = newMotion.subtract(vec3);
                Vec3 clampedMotion = new Vec3(signedMin(delta.x, force.x * 4), signedMin(delta.y, force.y * 4), signedMin(delta.z, force.z * 4));
                MobUtil.push(this.victim, clampedMotion, 0.25D);
                Vec3 old = new Vec3(this.victim.xOld, this.victim.yOld, this.victim.zOld);
                Vec3 travel = this.victim.position().subtract(old);
                if (this.victim.horizontalCollision && this.victim instanceof LivingEntity livingTarget) {
                    double d11 = livingTarget.getDeltaMovement().horizontalDistance();
                    float f1 = (float) (d11 * 10.0D - 3.0D);
                    int airborne = (int) travel.horizontalDistanceSqr() / 2;
                    if (f1 >= 1.0F) {
                        if (livingTarget.hurt(livingTarget.damageSources().flyIntoWall(), 4.0F + (airborne + 1) * 0.5F)) {
                            livingTarget.playSound(livingTarget.getFallSounds().big(), 2.0F, 1.0F);
                        }
                    }
                }
                if (CuriosFinder.hasCurio(caster, ModItems.RING_OF_FORCE.get())){
                    this.victim.setAirSupply(Math.max(-20, this.victim.getAirSupply() - 10));
                    if (this.victim.getAirSupply() <= -20){
                        this.victim.setAirSupply(0);
                        this.victim.hurt(ModDamageSource.choke(caster, caster), 2.0F);
                    }
                }
                ServerParticleUtil.addParticlesAroundMiddleSelf(worldIn, ParticleTypes.PORTAL, this.victim);
            } catch (NullPointerException exception) {
                caster.stopUsingItem();
            }
        } else {
            this.findVictim(worldIn, caster, spellStat);
        }
    }

    public static double signedMin(double a, double b) {
        return (a < 0 ? -1 : 1) * Math.min(Math.abs(a), Math.abs(b));
    }
}
