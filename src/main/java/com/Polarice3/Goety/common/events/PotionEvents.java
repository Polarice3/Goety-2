package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PotionEvents {
    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void LivingEffects(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null){
            Level world = livingEntity.level;
            if (livingEntity.hasEffect(GoetyEffects.BURN_HEX.get())){
                if (livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)){
                    livingEntity.removeEffectNoUpdate(MobEffects.FIRE_RESISTANCE);
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.CLIMBING.get())){
                MobUtil.ClimbAnyWall(livingEntity);
                MobUtil.WebMovement(livingEntity);
            }
            if (livingEntity instanceof Creeper creeper){
                if (!creeper.level.isClientSide) {
                    if (creeper.getTarget() != null) {
                        if (creeper.getTarget().hasEffect(GoetyEffects.PRESSURE.get())) {
                            if (creeper.getSwellDir() >= 1 && creeper.tickCount % 30 == 0) {
                                MobUtil.explodeCreeper(creeper);
                            }
                        }
                    }
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.FREEZING.get())){
                if (!livingEntity.level.isClientSide){
                    livingEntity.setIsInPowderSnow(true);
                    if (livingEntity.canFreeze()) {
                        int i = livingEntity.getTicksFrozen();
                        int j = Objects.requireNonNull(livingEntity.getEffect(GoetyEffects.FREEZING.get())).getAmplifier() + 3;
                        livingEntity.setTicksFrozen(Math.min(livingEntity.getTicksRequiredToFreeze() + 5, i + j));
                    }
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.NYCTOPHOBIA.get())){
                MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.NYCTOPHOBIA.get());
                if (mobEffectInstance != null){
                    int a = mobEffectInstance.getAmplifier();
                    int j = 40 >> a;
                    if (livingEntity.level.getLightLevelDependentMagicValue(livingEntity.blockPosition()) < 0.1 || livingEntity.hasEffect(MobEffects.DARKNESS)) {
                        if (livingEntity.tickCount % j == 0) {
                            livingEntity.hurt(ModDamageSource.PHOBIA, 1.0F);
                        }
                    }
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.SUN_ALLERGY.get())){
                boolean burn = false;
                if (!world.isClientSide && world.isDay()) {
                    float f = livingEntity.getLightLevelDependentMagicValue();
                    BlockPos blockpos = livingEntity.getVehicle() instanceof Boat ? (new BlockPos(livingEntity.getX(), (double) Math.round(livingEntity.getY()), livingEntity.getZ())).above() : new BlockPos(livingEntity.getX(), (double) Math.round(livingEntity.getY()), livingEntity.getZ());
                    if (f > 0.5F && world.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && world.canSeeSky(blockpos)) {
                        burn = true;
                    }
                }

                if (burn){
                    ItemStack helmet = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
                    if (!helmet.isEmpty()) {
                        if (MobUtil.validEntity(livingEntity)) {
                            if (helmet.isDamageableItem()) {
                                helmet.setDamageValue(helmet.getDamageValue() + world.random.nextInt(2));
                                if (helmet.getDamageValue() >= helmet.getMaxDamage()) {
                                    livingEntity.broadcastBreakEvent(EquipmentSlot.HEAD);
                                    livingEntity.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                                }
                            }
                        }
                        burn = false;
                    }
                    if (burn){
                        livingEntity.setSecondsOnFire(8);
                    }
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.TRIPPING.get())){
                MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.TRIPPING.get());
                if (mobEffectInstance != null){
                    int a = mobEffectInstance.getAmplifier();
                    int j = 20 >> a;
                    if (livingEntity.tickCount % j == 0 && world.random.nextFloat() <= 0.25F + (a / 10.0F) && livingEntity.isOnGround() && livingEntity.getDeltaMovement().horizontalDistanceSqr() > (double)2.5000003E-7F) {
                        MobUtil.push(livingEntity, world.random.nextDouble(), world.random.nextDouble() / 2.0D, world.random.nextDouble());
                    }
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.ARROWMANTIC.get())){
                MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.ARROWMANTIC.get());
                if (mobEffectInstance != null){
                    int a = mobEffectInstance.getAmplifier();
                    for (AbstractArrow abstractArrow : livingEntity.level.getEntitiesOfClass(AbstractArrow.class, livingEntity.getBoundingBox().inflate(2.0F + a))){
                        if (!abstractArrow.isOnGround()){
                            double d0 = livingEntity.getX() - abstractArrow.getX();
                            double d1 = livingEntity.getY(0.3333333333333333D) - abstractArrow.getY();
                            double d2 = livingEntity.getZ() - abstractArrow.getZ();
                            double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
                            abstractArrow.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.0F + (a / 5.0F), 10);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void AttackEvent(LivingAttackEvent event){
        LivingEntity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();

        if (attacker instanceof LivingEntity living){
            if (ModDamageSource.physicalAttacks(event.getSource())) {
                if (living.hasEffect(GoetyEffects.FLAME_HANDS.get())) {
                    MobEffectInstance mobEffectInstance = living.getEffect(GoetyEffects.FLAME_HANDS.get());
                    if (mobEffectInstance != null) {
                        int a = mobEffectInstance.getAmplifier() + 1;
                        victim.setSecondsOnFire(a * 4);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void HurtEvent(LivingHurtEvent event){
        LivingEntity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();

        if (attacker instanceof LivingEntity living) {
            if (victim.hasEffect(GoetyEffects.REPULSIVE.get())) {
                MobEffectInstance mobEffectInstance = victim.getEffect(GoetyEffects.REPULSIVE.get());
                if (mobEffectInstance != null){
                    int a = mobEffectInstance.getAmplifier();
                    living.playSound(SoundEvents.IRON_GOLEM_ATTACK);
                    living.knockback(1.0D + (a * 0.75D), 0.5D + (a * 0.2D), 1.0D + (a * 0.75D));
                }
            }
            if (victim.hasEffect(GoetyEffects.EXPLOSIVE.get())) {
                MobEffectInstance mobEffectInstance = victim.getEffect(GoetyEffects.EXPLOSIVE.get());
                if (mobEffectInstance != null){
                    int a = mobEffectInstance.getAmplifier() + 1;
                    if (victim.getRandom().nextInt(5 - a) == 0){
                        if (!victim.level.isClientSide) {
                            victim.level.explode(victim, victim.getX(), victim.getY(), victim.getZ(), 1.5F * (a / 2.0F), Explosion.BlockInteraction.DESTROY);
                            victim.removeEffect(GoetyEffects.EXPLOSIVE.get());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void ChargeEffect(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null){
            AttributeInstance speed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
            AttributeInstance attack = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);

            AttributeModifier addSpeed = new AttributeModifier(UUID.fromString("d4818bbc-54ed-4ecf-95a3-a15fbf71b31d"), "Charged Speed I", 0.1, AttributeModifier.Operation.MULTIPLY_TOTAL);
            AttributeModifier addAttack = new AttributeModifier(UUID.fromString("4bf0a8e3-a8f8-4bf6-95d2-f0ddbadd793e"), "Charged Attack I", 0.1, AttributeModifier.Operation.MULTIPLY_TOTAL);

            AttributeModifier addMoreSpeed = new AttributeModifier(UUID.fromString("e8ea9f21-c671-4a61-a297-db8fa50f3d13"), "Charged Speed II", 0.25, AttributeModifier.Operation.MULTIPLY_TOTAL);
            AttributeModifier reduceAttack = new AttributeModifier(UUID.fromString("a55e53d6-dd6a-41e8-8c1f-8f548887ed30"), "Charged Attack II", -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL);

            MobEffectInstance chargeInstance = livingEntity.getEffect(GoetyEffects.CHARGED.get());
            boolean notNull = chargeInstance != null;
            boolean flag = notNull && chargeInstance.getAmplifier() < 1;
            boolean flag2 = notNull && chargeInstance.getAmplifier() >= 1;
            if (attack != null && speed != null) {
                if (notNull) {
                    if (flag) {
                        if (speed.hasModifier(addMoreSpeed)){
                            speed.removeModifier(addMoreSpeed);
                        }
                        if (attack.hasModifier(reduceAttack)){
                            attack.removeModifier(reduceAttack);
                        }
                        if (!speed.hasModifier(addSpeed)) {
                            speed.addPermanentModifier(addSpeed);
                        }
                        if (!attack.hasModifier(addAttack)) {
                            attack.addPermanentModifier(addAttack);
                        }
                    } else if (flag2) {
                        if (speed.hasModifier(addSpeed)){
                            speed.removeModifier(addSpeed);
                        }
                        if (attack.hasModifier(addAttack)){
                            attack.removeModifier(addAttack);
                        }
                        if (!speed.hasModifier(addMoreSpeed)) {
                            speed.addPermanentModifier(addMoreSpeed);
                        }
                        if (!attack.hasModifier(reduceAttack)) {
                            attack.addPermanentModifier(reduceAttack);
                        }
                    }
                } else {
                    if (speed.hasModifier(addSpeed)){
                        speed.removeModifier(addSpeed);
                    }
                    if (attack.hasModifier(addAttack)){
                        attack.removeModifier(addAttack);
                    }
                    if (speed.hasModifier(addMoreSpeed)) {
                        speed.removeModifier(addMoreSpeed);
                    }
                    if (attack.hasModifier(reduceAttack)) {
                        attack.removeModifier(reduceAttack);
                    }
                }
            }
            if (notNull){
                if (chargeInstance.getAmplifier() >= 2 && livingEntity.hurtTime > 0){
                    livingEntity.removeEffect(chargeInstance.getEffect());
                } else {
                    if (livingEntity.tickCount % 20 == 0){
                        if (livingEntity.level instanceof ServerLevel serverLevel){
                            ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.ELECTRIC.get(), livingEntity);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void enderTeleport(EntityTeleportEvent event){
        if (!(event instanceof EntityTeleportEvent.TeleportCommand) && !(event instanceof EntityTeleportEvent.SpreadPlayersCommand)) {
            if (event.getEntity() instanceof LivingEntity living) {
                if (living.hasEffect(GoetyEffects.ENDER_GROUND.get())) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void finishItemEvents(LivingEntityUseItemEvent.Finish event){
        if (event.getItem().getItem() == Items.MILK_BUCKET){
            if (event.getEntity().hasEffect(GoetyEffects.ILLAGUE.get())){
                int duration = Objects.requireNonNull(event.getEntity().getEffect(GoetyEffects.ILLAGUE.get())).getDuration();
                int amp = Objects.requireNonNull(event.getEntity().getEffect(GoetyEffects.ILLAGUE.get())).getAmplifier();
                if (duration > 0){
                    if (amp <= 0) {
                        EffectsUtil.halveDuration(event.getEntity(), GoetyEffects.ILLAGUE.get(), duration, false, false);
                    } else {
                        EffectsUtil.deamplifyEffect(event.getEntity(), GoetyEffects.ILLAGUE.get(), duration, false, false);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void SleepEvents(PlayerWakeUpEvent event){
        Player player = event.getEntity();
        if (player.isSleepingLongEnough()) {
            if (player.hasEffect(GoetyEffects.ILLAGUE.get())) {
                int duration = Objects.requireNonNull(player.getEffect(GoetyEffects.ILLAGUE.get())).getDuration();
                int amp = Objects.requireNonNull(player.getEffect(GoetyEffects.ILLAGUE.get())).getAmplifier();
                if (duration > 0){
                    if (amp <= 0) {
                        EffectsUtil.halveDuration(player, GoetyEffects.ILLAGUE.get(), duration, false, false);
                    } else {
                        EffectsUtil.deamplifyEffect(player, GoetyEffects.ILLAGUE.get(), duration, false, false);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void PotionApplicationEvents(MobEffectEvent.Applicable event){
        if (event.getEffectInstance().getEffect() == MobEffects.FIRE_RESISTANCE){
            if (event.getEntity().hasEffect(GoetyEffects.BURN_HEX.get())){
                event.setResult(Event.Result.DENY);
            }
        }
        if (event.getEffectInstance().getEffect() == MobEffects.BLINDNESS){
            if (CuriosFinder.hasIllusionRobe(event.getEntity())){
                event.setResult(Event.Result.DENY);
            }
            if (event.getEntity() instanceof Player player) {
                if (ItemHelper.findHelmet(player, ModItems.DARK_HELMET.get())){
                    event.setResult(Event.Result.DENY);
                }
            }
        }
        if (event.getEffectInstance().getEffect() == MobEffects.DARKNESS){
            if (event.getEntity() instanceof Player player) {
                if (ItemHelper.findHelmet(player, ModItems.DARK_HELMET.get())){
                    event.setResult(Event.Result.DENY);
                }
            }
        }
        if (event.getEffectInstance().getEffect() == MobEffects.SLOW_FALLING){
            if (CuriosFinder.hasCurio(event.getEntity(), ModItems.WIND_ROBE.get())){
                event.setResult(Event.Result.DENY);
            }
        }
        if (event.getEffectInstance().getEffect() == GoetyEffects.ILLAGUE.get()){
            if (event.getEntity().getType().is(EntityTypeTags.RAIDERS) || event.getEntity() instanceof PatrollingMonster){
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void PotionAddedEvents(MobEffectEvent.Added event){
        LivingEntity effected = event.getEntity();
        MobEffectInstance instance = event.getEffectInstance();
        MobEffect effect = instance.getEffect();
        if (effect == GoetyEffects.BURN_HEX.get()){
            if (effected.hasEffect(MobEffects.FIRE_RESISTANCE)){
                effected.removeEffect(MobEffects.FIRE_RESISTANCE);
            }
        }
    }
}
