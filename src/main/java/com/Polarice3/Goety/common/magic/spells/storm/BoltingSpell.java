package com.Polarice3.Goety.common.magic.spells.storm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.events.TimedEvents;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BoltingSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.BoltingCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.BoltingDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.ZAP.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.BoltingCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.STORM;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        double velocity = spellStat.getVelocity();
        if (WandUtil.enchantedFocus(caster)){
            potency = WandUtil.getPotencyLevel(caster);
            velocity = WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        }
        caster.hurtMarked = true;
        caster.setOnGround(false);
        Vec3 vector3d = caster.getLookAngle();
        double power = rightStaff(staff) ? 3.5D : 2.5D;
        double d0 = power + (velocity / 4.0D);
        vector3d = vector3d.multiply(d0, d0, d0);
        caster.setDeltaMovement(vector3d.x, vector3d.y, vector3d.z);
        caster.hasImpulse = true;
        caster.fallDistance = 0;
        TimedEvents.submitTask("goety:bolting", new BoltingDashTask(caster.getUUID(), worldIn, this.rightStaff(staff), potency));
        this.playSound(worldIn, caster, ModSounds.HEAVY_WOOSH.get(), 3.0F, 1.0F);
        this.playSound(worldIn, caster, ModSounds.REDSTONE_EXPLODE.get(), 3.0F, 1.0F);
    }

    public static class BoltingDashTask implements EventTask {
        public UUID owner;
        public ServerLevel level;
        public boolean staff;
        public float damage;
        public int ticks = 0;

        public BoltingDashTask(UUID owner, ServerLevel level, boolean staff, float damage) {
            this.owner = owner;
            this.level = level;
            this.staff = staff;
            this.damage = damage;
        }

        @Override
        public void startTask() {
            if (this.level.getEntity(this.owner) instanceof LivingEntity living) {
                MiscCapHelper.setCustomSpinTexture(living, ConstantPaths.boltingDash());
                living.setLivingEntityFlag(4, true);
                for (int i = 0; i < 4; ++i){
                    ServerParticleUtil.windParticle(this.level, new ColorUtil(ChatFormatting.YELLOW), 2.0F, (float) ((2.0D * Math.random() - 1.0D) * 0.5D), living.getId(), living.position());
                }
                ColorUtil colorUtil = new ColorUtil(ChatFormatting.YELLOW);
                this.level.sendParticles(ModParticleTypes.ELECTRIC_EXPLODE.get(), living.getX(), living.getY() + 0.5F, living.getZ(), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
            }
        }

        @Override
        public void tickTask() {
            ++this.ticks;
            if (this.level.getEntity(this.owner) instanceof LivingEntity living) {
                living.fallDistance = 0;
                living.invulnerableTime = 20;
                double radius = 1.0D;
                if (this.staff){
                    radius += 0.25D;
                }
                for(int i = 0; i < 2; ++i) {
                    double d0 = (2.0D * Math.random() - 1.0D) * 0.2D;
                    double d1 = (2.0D * Math.random() - 1.0D) * 0.2D;
                    double d2 = (2.0D * Math.random() - 1.0D) * 0.2D;
                    this.level.sendParticles(ModParticleTypes.BIG_ELECTRIC.get(), living.getRandomX(1.0D), living.getRandomY(), living.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                }
                ServerParticleUtil.addParticlesAroundMiddleSelf(this.level, ModParticleTypes.ELECTRIC.get(), living);

                List<Entity> list = this.level.getEntities(living, living.getBoundingBox().inflate(radius));
                if (!list.isEmpty()) {
                    for (Entity entity : list) {
                        if (entity instanceof LivingEntity target) {
                            if (!MobUtil.areAllies(living, entity) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) && entity.isAttackable()){
                                float baseDamage = SpellConfig.BoltingDamage.get().floatValue() * WandUtil.damageMultiply();
                                if (target.hurt(ModDamageSource.directShock(living), baseDamage + this.damage)){
                                    float chance = this.staff ? 0.25F : 0.05F;
                                    float chainDamage = this.damage / 2.0F;
                                    if (this.level.isThundering() && this.level.isRainingAt(target.blockPosition())){
                                        chance += 0.25F;
                                        chainDamage = this.damage;
                                    }
                                    if (this.level.random.nextFloat() <= chance){
                                        target.addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(), MathHelper.secondsToTicks(5)));
                                    }
                                    if (this.staff){
                                        WandUtil.chainLightning(target, living, 2.0D, chainDamage);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public boolean getAsBoolean() {
            boolean flag = false;
            if (this.level.getEntity(this.owner) instanceof LivingEntity living){
                if (++this.ticks >= 20 || living.isDeadOrDying()) {
                    flag = true;
                }

                if (living.horizontalCollision) {
                    flag = true;
                }

                if (flag){
                    living.setLivingEntityFlag(4, false);
                    MiscCapHelper.setCustomSpinTexture(living, null);
                }
            } else {
                flag = true;
            }
            return flag;
        }

        @Override
        public void endTask() {
            if (this.level.getEntity(this.owner) instanceof LivingEntity living) {
                living.setLivingEntityFlag(4, false);
                MiscCapHelper.setCustomSpinTexture(living, null);
            }
        }
    }
}
