package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.ReverseShockwaveParticleOption;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Doppelganger;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SSetPlayerOwnerPacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class IllusionSpell extends Spell {
    public int defaultSoulCost() {
        return SpellConfig.IllusionCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.IllusionDuration.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.IllusionCoolDown.get();
    }

    @Override
    public ColorUtil particleColors(LivingEntity entityLiving) {
        return new ColorUtil(0.3F, 0.3F, 0.8F);
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        for (Entity entity : worldIn.getAllEntities()) {
            if (entity instanceof Doppelganger doppelganger) {
                if (doppelganger.getTrueOwner() == entityLiving && doppelganger.tickCount > 10) {
                    doppelganger.die(DamageSource.STARVE);
                }
            }
        }
        int i0 = 4;
        if (staff.is(ModItems.NAMELESS_STAFF.get())){
            i0 = 8;
        }
        boolean undead = CuriosFinder.hasNamelessSet(entityLiving) && staff.is(ModItems.NAMELESS_STAFF.get());
        ParticleOptions particleOptions = ParticleTypes.POOF;
        if (undead){
            particleOptions = ModParticleTypes.LICH.get();
        }
        for (int i1 = 0; i1 < i0; ++i1) {
            Doppelganger summonedentity = new Doppelganger(ModEntityType.DOPPELGANGER.get(), worldIn);
            summonedentity.setTrueOwner(entityLiving);
            if (entityLiving instanceof Player player) {
                ModNetwork.sendTo(player, new SSetPlayerOwnerPacket(summonedentity));
            }
            Vec3 vec3 = Vec3.atCenterOf(BlockFinder.SummonRadius(entityLiving.blockPosition(), summonedentity, worldIn));
            summonedentity.setPos(vec3);
            summonedentity.setUndeadClone(undead);
            summonedentity.setLimitedLife(undead ? MathHelper.secondsToTicks(2.875F) : 1200);
            summonedentity.setPersistenceRequired();
            summonedentity.setUpgraded(CuriosFinder.hasIllusionRobe(entityLiving));
            summonedentity.finalizeSpawn(worldIn, entityLiving.level.getCurrentDifficultyAt(BlockFinder.SummonRadius(entityLiving.blockPosition(), summonedentity, worldIn)), MobSpawnType.MOB_SUMMONED, null, null);
            LivingEntity target = this.getTarget(entityLiving);
            if (target != null) {
                double d2 = target.getX() - summonedentity.getX();
                double d1 = target.getZ() - summonedentity.getZ();
                summonedentity.setYRot(-((float)Mth.atan2(d2, d1)) * (180F / (float)Math.PI));
                if (undead){
                    float f = (float) Mth.atan2(d1, d2);
                    float f2 = f + (float) i1 * (float) Math.PI * 0.25F + 4.0F;
                    vec3 = new Vec3(target.getX() + (double) Mth.cos(f2) * 4.0D, target.getY(), target.getZ() + (double) Mth.sin(f2) * 4.0D);
                    summonedentity.setPos(vec3);
                }
            }
            MobUtil.moveDownToGround(summonedentity);
            worldIn.addFreshEntity(summonedentity);
            for (int i = 0; i < entityLiving.level.random.nextInt(10) + 10; ++i) {
                ServerParticleUtil.smokeParticles(particleOptions, summonedentity.getX(), summonedentity.getY(), summonedentity.getZ(), worldIn);
            }
            if (undead){
                worldIn.sendParticles(new ReverseShockwaveParticleOption(new ColorUtil(0x36e416), 2.0F, 0.5F, 1), summonedentity.getX(), summonedentity.getY() + 0.5F, summonedentity.getZ(), 0, 0, 0, 0, 0.5F);
            }
        }
        if (CuriosFinder.hasIllusionRobe(entityLiving)){
            entityLiving.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1200));
            LivingEntity target = this.getTarget(entityLiving);
            if (target != null) {
                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400));
            }
        }
        SoundEvent soundEvent = SoundEvents.ILLUSIONER_MIRROR_MOVE;
        if (undead){
            soundEvent = ModSounds.LICH_TELEPORT_IN.get();
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), soundEvent, this.getSoundSource(), 1.0F, 1.0F);
        for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
            worldIn.sendParticles(particleOptions, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), 0, 0.0F, 0.0F, 0.0F, 0);
        }
    }

}
