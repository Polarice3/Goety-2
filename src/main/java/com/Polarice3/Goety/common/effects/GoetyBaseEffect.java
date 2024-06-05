package com.Polarice3.Goety.common.effects;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class GoetyBaseEffect extends MobEffect {
    public GoetyBaseEffect(MobEffectCategory p_19451_, int p_19452_) {
        super(p_19451_, p_19452_);
    }

    @SuppressWarnings("deprecation")
    public void applyEffectTick(LivingEntity livingEntity, int amplify) {
        Level world = livingEntity.level;
        if (this == GoetyEffects.NYCTOPHOBIA.get()){
            if (world.getLightLevelDependentMagicValue(livingEntity.blockPosition()) < 0.1 || livingEntity.hasEffect(MobEffects.DARKNESS)) {
                livingEntity.hurt(ModDamageSource.getDamageSource(livingEntity.level, ModDamageSource.PHOBIA), 1.0F);
            }
        }
        if (this == GoetyEffects.SUN_ALLERGY.get()){
            boolean burn = MobUtil.isInSunlight(livingEntity);

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
        if (this == GoetyEffects.PHOTOSYNTHESIS.get()){
            if (MobUtil.isInSunlight(livingEntity)) {
                if (livingEntity.getHealth() < livingEntity.getMaxHealth()) {
                    livingEntity.heal(1.0F);
                }
            }
        }
        if (this == GoetyEffects.TRIPPING.get()) {
            if (world.random.nextFloat() <= 0.25F + (amplify / 10.0F) && MobUtil.isMoving(livingEntity)) {
                MobUtil.push(livingEntity, world.random.nextDouble(), world.random.nextDouble() / 2.0D, world.random.nextDouble());
            }
        }
        if (this == GoetyEffects.ARROWMANTIC.get()){
            for (AbstractArrow abstractArrow : world.getEntitiesOfClass(AbstractArrow.class, livingEntity.getBoundingBox().inflate(2.0F + amplify))){
                if (!abstractArrow.onGround()){
                    double d0 = livingEntity.getX() - abstractArrow.getX();
                    double d1 = livingEntity.getY(0.3333333333333333D) - abstractArrow.getY();
                    double d2 = livingEntity.getZ() - abstractArrow.getZ();
                    double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
                    abstractArrow.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.0F + (amplify / 5.0F), 10);
                }
            }
        }
        if (this == GoetyEffects.FIERY_AURA.get()){
            if (world instanceof ServerLevel serverLevel) {
                float f = 2.0F + amplify;
                ServerParticleUtil.addAuraParticles(serverLevel, ParticleTypes.FLAME, livingEntity, f);
                for (LivingEntity living : world.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(f))) {
                    if (!living.isOnFire() && !living.fireImmune() && MobUtil.validEntity(living) && living != livingEntity) {
                        ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.FLAME, living);
                        ModNetwork.sendToALL(new SPlayWorldSoundPacket(livingEntity.blockPosition(), SoundEvents.FIRECHARGE_USE, 1.0F, 0.75F));
                        living.setSecondsOnFire(5 * (amplify + 1));
                    }
                }
            }
        }
        if (this == GoetyEffects.FROSTY_AURA.get()){
            if (world instanceof ServerLevel serverLevel) {
                float f = 2.0F + amplify;
                ServerParticleUtil.addAuraParticles(serverLevel, ParticleTypes.SNOWFLAKE, livingEntity, f);
                for (LivingEntity living : world.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(f))) {
                    if (!living.isFreezing() && living.canFreeze() && MobUtil.validEntity(living) && living != livingEntity) {
                        ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.SNOWFLAKE, living);
                        ModNetwork.sendToALL(new SPlayWorldSoundPacket(livingEntity.blockPosition(), SoundEvents.PLAYER_HURT_FREEZE, 1.0F, 0.75F));
                        living.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), 100, amplify));
                    }
                }
            }
        }
        if (this == GoetyEffects.FIRE_TRAIL.get()){
            BlockState blockState = world.getBlockState(livingEntity.blockPosition());
            if (MobUtil.isMoving(livingEntity)){
                if (blockState.canBeReplaced(new DirectionalPlaceContext(world, livingEntity.blockPosition(), Direction.DOWN, ItemStack.EMPTY, Direction.UP))
                        && world.getFluidState(livingEntity.blockPosition()).isEmpty()
                        && world.getBlockState(livingEntity.blockPosition().below()).isSolidRender(world, livingEntity.blockPosition().below())){
                    world.setBlockAndUpdate(livingEntity.blockPosition(), BaseFireBlock.getState(world, livingEntity.blockPosition()));
                }
            }
        }
        if (this == GoetyEffects.PLUNGE.get()){
            if (!livingEntity.hasEffect(MobEffects.LEVITATION)){
                if (MobUtil.validEntity(livingEntity)) {
                    if (livingEntity instanceof Player player) {
                        player.getAbilities().flying &= player.isCreative();
                    }
                    if (livingEntity.isInFluidType()) {
                        livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().subtract(0, 0.05D + (amplify / 100.0D), 0));
                    } else if (BlockFinder.distanceFromGround(livingEntity) > 4) {
                        livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().subtract(0, 0.2D + (amplify / 10.0D), 0));
                    }
                }
            }
        }
        if (this == GoetyEffects.STUNNED.get()){
            if (livingEntity.level instanceof ServerLevel serverLevel){
                if (livingEntity.tickCount % 15 == 0) {
                    serverLevel.sendParticles(ModParticleTypes.STUN.get(), livingEntity.getX(), livingEntity.getY() + livingEntity.getBbHeight() + 0.5F, livingEntity.getZ(), 1, 0, 0, 0, 0.5F);
                }
            }
        }
        if (this == GoetyEffects.WILD_RAGE.get()){
            if (!livingEntity.level.isClientSide){
                if (livingEntity instanceof Mob mob && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null && mob.getAttribute(Attributes.FOLLOW_RANGE) != null){
                    double follow = mob.getAttributeValue(Attributes.FOLLOW_RANGE);
                    LivingEntity target = world.getNearestEntity(world.getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(follow, 4.0D, follow), (p_148152_) -> {
                        return true;
                    }), TargetingConditions.forCombat(), mob, mob.getX(), mob.getEyeY(), mob.getZ());
                    if (target != null && mob != target && mob.getTarget() != target){
                        mob.setTarget(target);
                        mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, target.getUUID(), 600L);
                        mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, target, 600L);
                    }
                    if (mob.getTarget() != null && mob.getTarget().isRemoved()){
                        mob.setTarget(null);
                        mob.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
                        mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                    }
                }
            }
        }
    }

    public boolean isDurationEffectTick(int tick, int amplify) {
        if (this == GoetyEffects.NYCTOPHOBIA.get()) {
            int j = 40 >> amplify;
            if (j > 0) {
                return tick % j == 0;
            }
        }
        if (this == GoetyEffects.PHOTOSYNTHESIS.get()) {
            int j = 50 >> amplify;
            if (j > 0) {
                return tick % j == 0;
            }
        }
        if (this == GoetyEffects.TRIPPING.get()) {
            int j = 20 >> amplify;
            if (j > 0) {
                return tick % j == 0;
            }
        }
        return true;
    }
}
