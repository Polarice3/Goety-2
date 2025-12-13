package com.Polarice3.Goety.common.magic.spells.wind;

import com.Polarice3.Goety.api.blocks.entities.IWindPowered;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.WindBlowParticle;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.AbstractCyclone;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class WindBlastSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setPotency(1).setRange(8);
    }

    public int defaultSoulCost() {
        return SpellConfig.WindBlastCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.WindBlastDuration.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.WindBlastCoolDown.get();
    }

    public SpellType getSpellType(){
        return SpellType.WIND;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        Vec3 srcVec = new Vec3(caster.getX(), caster.getEyeY(), caster.getZ());
        Vec3 lookVec = caster.getViewVector(1.0F);
        double knock = spellStat.getPotency();
        int range = spellStat.getRange();
        if (rightStaff(staff)){
            range *= 2;
            knock *= 2;
        }
        if (WandUtil.enchantedFocus(caster)){
            knock += WandUtil.getPotencyLevel(caster) / 4.0D;
            range += WandUtil.getRangeLevel(caster);
        }
        double dist = 0.9;
        double px = caster.getX() + lookVec.x * dist;
        double py = caster.getEyeY() + lookVec.y * dist;
        double pz = caster.getZ() + lookVec.z * dist;
        for (int i = 0; i < 16; i++) {
            double pVelocity = 0.3F + ((double) range / 10);
            double velocity = pVelocity + worldIn.getRandom().nextDouble() * pVelocity;
            Vec3 vec3 = lookVec.multiply(velocity, velocity, velocity);
            Vec3 pos = new Vec3(px, py, pz);
            pos = pos.add(caster.getRandom().nextGaussian() / 2, caster.getRandom().nextGaussian() / 2, caster.getRandom().nextGaussian() / 2);
            int width = worldIn.getRandom().nextIntBetweenInclusive(1, 4);
            float height = worldIn.getRandom().nextFloat() * 0.5F;
            worldIn.sendParticles(new WindBlowParticle.Option(ColorUtil.WHITE, width, height), pos.x, pos.y, pos.z, 0, vec3.x, vec3.y, vec3.z, 1.0F);
        }
        for(int i = 1; i < range; ++i) {
            Vec3 vector3d2 = srcVec.add(lookVec.scale(i));
            if (typeStaff(staff, SpellType.FROST)){
                worldIn.sendParticles(ModParticleTypes.FROST.get(), vector3d2.x, vector3d2.y, vector3d2.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            if (typeStaff(staff, SpellType.WILD)){
                worldIn.sendParticles(ModParticleTypes.FLY.get(), vector3d2.x, vector3d2.y, vector3d2.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            if (typeStaff(staff, SpellType.NETHER)){
                worldIn.sendParticles(ParticleTypes.FLAME, vector3d2.x, vector3d2.y, vector3d2.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
        Vec3 rangeVec = new Vec3(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        BlockHitResult result = this.blockResult(worldIn, caster, range);
        if (result != null){
            BlockPos blockPos = result.getBlockPos();
            BlockEntity blockEntity = worldIn.getBlockEntity(blockPos);
            if (blockEntity instanceof IWindPowered windPowered){
                windPowered.activate(MathHelper.secondsToTicks(15));
            }
        }
        List<Entity> entities = caster.level.getEntities(caster, caster.getBoundingBox().inflate(1.0D).expandTowards(rangeVec));
        for (Entity entity : entities){
            if (caster.hasLineOfSight(entity)){
                if (!MobUtil.areAllies(entity, caster) && !entity.getType().is(ModTags.EntityTypes.UNBLOWABLE_ENTITIES)) {
                    MobUtil.knockBack(entity, caster, 2.0D * knock, 0.2D * knock, 2.0D * knock);
                    if (entity instanceof LivingEntity living) {
                        if (typeStaff(staff, SpellType.FROST)) {
                            living.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(5)));
                        }
                        if (typeStaff(staff, SpellType.WILD)) {
                            MobEffect mobEffect = MobEffects.POISON;
                            if (CuriosFinder.hasWildRobe(caster)) {
                                mobEffect = GoetyEffects.ACID_VENOM.get();
                            }
                            living.addEffect(new MobEffectInstance(mobEffect, MathHelper.secondsToTicks(5)));
                        }
                        if (typeStaff(staff, SpellType.STORM)) {
                            living.addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(), MathHelper.secondsToTicks(5)));
                        }
                        if (typeStaff(staff, SpellType.VOID)) {
                            living.addEffect(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get(), MathHelper.secondsToTicks(5)));
                        }
                        if (typeStaff(staff, SpellType.NETHER)) {
                            living.setSecondsOnFire(5);
                        }
                    }
                    if (entity instanceof AbstractCyclone cyclone && (cyclone.getTrueOwner() == null || !MobUtil.areAllies(caster, cyclone.getTrueOwner()))) {
                        cyclone.trueRemove();
                    }
                }
            }
        }
        this.playSound(worldIn, caster, ModSounds.WIND_BLAST.get(), 0.3F, 1.3F);
    }
}
