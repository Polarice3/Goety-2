package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
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
            AttributeInstance armor = livingEntity.getAttribute(Attributes.ARMOR);
            AttributeModifier soulArmorBuff = new AttributeModifier(UUID.fromString("3e4b414b-466c-4b90-8a92-a878e2542bb8"), "Increase Armor", 2.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
            if (armor != null){
                if (livingEntity.hasEffect(GoetyEffects.SOUL_ARMOR.get())){
                    if (ItemHelper.noArmor(livingEntity)){
                        if (!armor.hasModifier(soulArmorBuff)){
                            armor.addPermanentModifier(soulArmorBuff);
                        }
                    } else {
                        if (armor.hasModifier(soulArmorBuff)){
                            armor.removeModifier(soulArmorBuff);
                        }
                    }
                } else {
                    if (armor.hasModifier(soulArmorBuff)){
                        armor.removeModifier(soulArmorBuff);
                    }
                }
            }
            if (livingEntity.getTags().contains(ConstantPaths.gassed())){
                if (livingEntity.tickCount % 20 == 0){
                    livingEntity.getTags().remove(ConstantPaths.gassed());
                }
            }
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
            if (livingEntity instanceof Bee bee){
                if (!bee.level.isClientSide) {
                    if (bee.getTags().contains(ConstantPaths.conjuredBee())){
                        if ((bee.getTarget() == null && bee.getPersistentAngerTarget() == null) || bee.hasStung()){
                            if (bee.tickCount % MathHelper.secondsToTicks(10) == 0){
                                bee.spawnAnim();
                                bee.discard();
                            }
                        }
                    }
                }
            }
            if (livingEntity instanceof Bat bat){
                if (!bat.level.isClientSide){
                    if (bat.getTags().contains(ConstantPaths.conjuredBat())){
                        if (bat.tickCount % MathHelper.secondsToTicks(20) == 0){
                            bat.spawnAnim();
                            bat.discard();
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
                        if (j > 0) {
                            if (livingEntity.tickCount % j == 0) {
                                livingEntity.hurt(ModDamageSource.PHOBIA, 1.0F);
                            }
                        }
                    }
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.SUN_ALLERGY.get())){
                boolean burn = false;
                if (!world.isClientSide && world.isDay()) {
                    float f = livingEntity.getLightLevelDependentMagicValue();
                    if (MobUtil.isInSunlight(livingEntity) && world.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
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
            if (livingEntity.hasEffect(GoetyEffects.PHOTOSYNTHESIS.get())){
                if (!world.isClientSide && world.isDay()) {
                    MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.PHOTOSYNTHESIS.get());
                    if (mobEffectInstance != null) {
                        int a = mobEffectInstance.getAmplifier();
                        int j = 50 >> a;
                        if (j > 0){
                            float f = livingEntity.getLightLevelDependentMagicValue();
                            if (MobUtil.isInSunlight(livingEntity) && world.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                                if (livingEntity.getHealth() < livingEntity.getMaxHealth()) {
                                    livingEntity.heal(1.0F);
                                }
                            }
                        }
                    }
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.TRIPPING.get())){
                MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.TRIPPING.get());
                if (mobEffectInstance != null){
                    int a = mobEffectInstance.getAmplifier();
                    int j = 20 >> a;
                    if (j > 0) {
                        if (livingEntity.tickCount % j == 0 && world.random.nextFloat() <= 0.25F + (a / 10.0F) && MobUtil.isMoving(livingEntity)) {
                            MobUtil.push(livingEntity, world.random.nextDouble(), world.random.nextDouble() / 2.0D, world.random.nextDouble());
                        }
                    }
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.ARROWMANTIC.get())){
                MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.ARROWMANTIC.get());
                if (mobEffectInstance != null){
                    int a = mobEffectInstance.getAmplifier();
                    for (AbstractArrow abstractArrow : world.getEntitiesOfClass(AbstractArrow.class, livingEntity.getBoundingBox().inflate(2.0F + a))){
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
            if (livingEntity.hasEffect(GoetyEffects.FIERY_AURA.get())){
                if (world instanceof ServerLevel serverLevel) {
                    MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.FIERY_AURA.get());
                    if (mobEffectInstance != null){
                        int a = mobEffectInstance.getAmplifier();
                        float f = 2.0F + a;
                        ServerParticleUtil.addAuraParticles(serverLevel, ParticleTypes.FLAME, livingEntity, f);
                        for (LivingEntity living : world.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(f))) {
                            if (!living.isOnFire() && !living.fireImmune() && MobUtil.validEntity(living) && living != livingEntity) {
                                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.FLAME, living);
                                ModNetwork.sendToALL(new SPlayWorldSoundPacket(livingEntity.blockPosition(), SoundEvents.FIRECHARGE_USE, 1.0F, 0.75F));
                                living.setSecondsOnFire(5 * (a + 1));
                            }
                        }
                    }
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.FROSTY_AURA.get())){
                if (world instanceof ServerLevel serverLevel) {
                    MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.FROSTY_AURA.get());
                    if (mobEffectInstance != null){
                        int a = mobEffectInstance.getAmplifier();
                        float f = 2.0F + a;
                        ServerParticleUtil.addAuraParticles(serverLevel, ParticleTypes.SNOWFLAKE, livingEntity, f);
                        for (LivingEntity living : world.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(f))) {
                            if (!living.isFreezing() && living.canFreeze() && MobUtil.validEntity(living) && living != livingEntity) {
                                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.SNOWFLAKE, living);
                                ModNetwork.sendToALL(new SPlayWorldSoundPacket(livingEntity.blockPosition(), SoundEvents.PLAYER_HURT_FREEZE, 1.0F, 0.75F));
                                living.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), 100, a));
                            }
                        }
                    }
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.FIRE_TRAIL.get())){
                MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.FIRE_TRAIL.get());
                if (mobEffectInstance != null){
                    BlockState blockState = world.getBlockState(livingEntity.blockPosition());
                    if (MobUtil.isMoving(livingEntity)){
                        if (blockState.canBeReplaced(new DirectionalPlaceContext(world, livingEntity.blockPosition(), Direction.DOWN, ItemStack.EMPTY, Direction.UP))
                                && world.getFluidState(livingEntity.blockPosition()).isEmpty()
                                && world.getBlockState(livingEntity.blockPosition().below()).isSolidRender(world, livingEntity.blockPosition().below())){
                            world.setBlockAndUpdate(livingEntity.blockPosition(), BaseFireBlock.getState(world, livingEntity.blockPosition()));
                        }
                    }
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.PLUNGE.get())){
                MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.PLUNGE.get());
                if (mobEffectInstance != null && !livingEntity.hasEffect(MobEffects.LEVITATION)){
                    if (MobUtil.validEntity(livingEntity)) {
                        if (livingEntity instanceof Player player) {
                            player.getAbilities().flying &= player.isCreative();
                        }
                        if (livingEntity.isInFluidType()) {
                            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().subtract(0, 0.05D + (mobEffectInstance.getAmplifier() / 100.0D), 0));
                        } else if (BlockFinder.distanceFromGround(livingEntity) > 4) {
                            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().subtract(0, 0.2D + (mobEffectInstance.getAmplifier() / 10.0D), 0));
                        }
                    }
                }
            }
            if (livingEntity instanceof Player && livingEntity.hasEffect(GoetyEffects.SENSE_LOSS.get())){
                MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.SENSE_LOSS.get());
                if (mobEffectInstance != null) {
                    if (livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, mobEffectInstance.getDuration(), mobEffectInstance.getAmplifier(), mobEffectInstance.isAmbient(), mobEffectInstance.isVisible()))){
                        livingEntity.removeEffect(GoetyEffects.SENSE_LOSS.get());
                    }
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.EXPLOSIVE.get())) {
                MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.EXPLOSIVE.get());
                if (mobEffectInstance != null){
                    int a = mobEffectInstance.getAmplifier() + 1;
                    if (MobUtil.isPushed(livingEntity)) {
                        if (livingEntity.getRandom().nextInt(100 - (a * 10)) == 0) {
                            if (!livingEntity.level.isClientSide) {
                                livingEntity.level.explode(livingEntity, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 3.0F + (a / 2.0F), Explosion.BlockInteraction.DESTROY);
                                livingEntity.removeEffect(GoetyEffects.EXPLOSIVE.get());
                            }
                        }
                    }
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.SNOW_SKIN.get())){
                MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.SNOW_SKIN.get());
                if(mobEffectInstance != null){
                    if (!livingEntity.level.isClientSide){
                        int i = Mth.floor(livingEntity.getX());
                        int j = Mth.floor(livingEntity.getY());
                        int k = Mth.floor(livingEntity.getZ());
                        BlockPos blockpos = new BlockPos(i, j, k);
                        Biome biome = livingEntity.level.getBiome(blockpos).value();
                        if (biome.shouldSnowGolemBurn(blockpos)) {
                            livingEntity.hurt(DamageSource.ON_FIRE, 1.0F);
                        }

                        livingEntity.setIsInPowderSnow(false);
                        livingEntity.setTicksFrozen(0);

                        BlockState blockstate = Blocks.SNOW.defaultBlockState();

                        for(int l = 0; l < 4; ++l) {
                            i = Mth.floor(livingEntity.getX() + (double)((float)(l % 2 * 2 - 1) * 0.25F));
                            j = Mth.floor(livingEntity.getY());
                            k = Mth.floor(livingEntity.getZ() + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
                            BlockPos blockpos1 = new BlockPos(i, j, k);
                            if (livingEntity.level.isEmptyBlock(blockpos1) && blockstate.canSurvive(livingEntity.level, blockpos1)) {
                                livingEntity.level.setBlockAndUpdate(blockpos1, blockstate);
                                livingEntity.level.gameEvent(GameEvent.BLOCK_PLACE, blockpos1, GameEvent.Context.of(livingEntity, blockstate));
                            }
                        }
                    }
                }
            }
            if (livingEntity.hasEffect(GoetyEffects.WILD_RAGE.get())){
                MobEffectInstance mobEffectInstance = livingEntity.getEffect(GoetyEffects.WILD_RAGE.get());
                if(mobEffectInstance != null){
                    if (!livingEntity.level.isClientSide){
                        if (livingEntity instanceof Mob mob && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null && mob.getAttribute(Attributes.FOLLOW_RANGE) != null){
                            double follow = mob.getAttributeValue(Attributes.FOLLOW_RANGE);
                            LivingEntity target = world.getNearestEntity(world.getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(follow, 4.0D, follow), (p_148152_) -> {
                                return true;
                            }), TargetingConditions.forCombat(), mob, mob.getX(), mob.getEyeY(), mob.getZ());
                            if (target != null && mob != target && mob.getTarget() != target){
                                mob.setTarget(target);
                            }
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
                if (living.hasEffect(GoetyEffects.VENOMOUS_HANDS.get())) {
                    MobEffectInstance mobEffectInstance = living.getEffect(GoetyEffects.VENOMOUS_HANDS.get());
                    if (mobEffectInstance != null) {
                        int a = mobEffectInstance.getAmplifier();
                        victim.addEffect(new MobEffectInstance(MobEffects.POISON, 200, a), living);
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
                    if (!living.level.isClientSide){
                        ModNetwork.sendToALL(new SPlayWorldSoundPacket(living.blockPosition(), SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F));
                        MobUtil.knockBack(living, victim, 1.0D + (a / 2.0D), 0.4D + (a * 0.2D), 1.0D + (a / 2.0D));
                    }
                }
            }
        }
        if (victim.hasEffect(GoetyEffects.SOUL_ARMOR.get())){
            MobEffectInstance mobEffectInstance = victim.getEffect(GoetyEffects.SOUL_ARMOR.get());
            if (mobEffectInstance != null){
                if (mobEffectInstance.getDuration() > MathHelper.secondsToTicks(event.getAmount())){
                    EffectsUtil.decreaseDuration(victim, GoetyEffects.SOUL_ARMOR.get(), MathHelper.secondsToTicks(event.getAmount()), mobEffectInstance.isAmbient(), mobEffectInstance.isVisible());
                }
                if (victim instanceof Player player){
                    SEHelper.decreaseSouls(player, (int) event.getAmount());
                }
            }
        }
        if (victim.hasEffect(GoetyEffects.EXPLOSIVE.get())) {
            MobEffectInstance mobEffectInstance = victim.getEffect(GoetyEffects.EXPLOSIVE.get());
            if (mobEffectInstance != null){
                int a = mobEffectInstance.getAmplifier() + 1;
                if (victim.getRandom().nextInt(5 - a) == 0) {
                    if (!victim.level.isClientSide) {
                        victim.level.explode(victim, victim.getX(), victim.getY(), victim.getZ(), 3.0F + (a / 2.0F), Explosion.BlockInteraction.DESTROY);
                        victim.removeEffect(GoetyEffects.EXPLOSIVE.get());
                    }
                }
            }
        }
        if (victim.hasEffect(GoetyEffects.FLAMMABLE.get())){
            MobEffectInstance mobEffectInstance = victim.getEffect(GoetyEffects.FLAMMABLE.get());
            if (mobEffectInstance != null){
                int a = mobEffectInstance.getAmplifier() + 2;
                if (event.getSource().isFire()) {
                    event.setAmount(event.getAmount() * a);
                }
            }
        }
    }

    @SubscribeEvent
    public static void ExperienceEvents(LivingExperienceDropEvent event){
        Player player = event.getAttackingPlayer();
        LivingEntity living = event.getEntity();
        if (player != null && living != null){
            if (player.hasEffect(GoetyEffects.INSIGHT.get())){
                MobEffectInstance mobEffectInstance = player.getEffect(GoetyEffects.INSIGHT.get());
                if (mobEffectInstance != null){
                    int a = mobEffectInstance.getAmplifier() + 2;
                    event.setDroppedExperience(event.getOriginalExperience() * a);
                }
            }
        }
    }

    @SubscribeEvent
    public static void DeathEvents(LivingDeathEvent event){
        if (event.getEntity() instanceof Player player){
            if (player.hasEffect(GoetyEffects.SAVE_EFFECTS.get())){
                if (!player.getActiveEffects().isEmpty()){
                    List<MobEffectInstance> instanceList = new ArrayList<>(player.getActiveEffects());
                    if (!instanceList.isEmpty()){
                        ListTag listtag = new ListTag();
                        CompoundTag playerData = event.getEntity().getPersistentData();
                        CompoundTag data;

                        if (!playerData.contains(Player.PERSISTED_NBT_TAG)) {
                            data = new CompoundTag();
                        } else {
                            data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
                        }
                        for(MobEffectInstance mobeffectinstance : instanceList) {
                            listtag.add(mobeffectinstance.save(new CompoundTag()));
                        }
                        data.put(ConstantPaths.keepEffects(), listtag);
                        playerData.put(Player.PERSISTED_NBT_TAG, data);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void RespawnEvents(PlayerEvent.PlayerRespawnEvent event){
        CompoundTag playerData = event.getEntity().getPersistentData();
        if (playerData.contains(Player.PERSISTED_NBT_TAG)) {
            CompoundTag data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
            if (data.contains(ConstantPaths.keepEffects(), 9)) {
                ListTag listtag = data.getList(ConstantPaths.keepEffects(), 10);

                for (int i = 0; i < listtag.size(); ++i) {
                    MobEffectInstance mobeffectinstance = MobEffectInstance.load(listtag.getCompound(i));
                    if (mobeffectinstance != null
                            && !event.getEntity().hasEffect(mobeffectinstance.getEffect())
                            && mobeffectinstance.getEffect() != GoetyEffects.SAVE_EFFECTS.get()) {
                        event.getEntity().addEffect(mobeffectinstance);
                    }
                }

                data.remove(ConstantPaths.keepEffects());
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
    public static void EffectVisibilityEvents(LivingEvent.LivingVisibilityEvent event){
        if (event.getLookingEntity() instanceof LivingEntity living){
            if (living.hasEffect(GoetyEffects.SENSE_LOSS.get())){
                MobEffectInstance mobEffectInstance = living.getEffect(GoetyEffects.SENSE_LOSS.get());
                if (mobEffectInstance != null){
                    int a = mobEffectInstance.getAmplifier();
                    event.modifyVisibility(0.5D - (a / 10.0D));
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
            if (event.getEntity().hasEffect(GoetyEffects.SOUL_ARMOR.get())){
                event.getEntity().removeEffect(GoetyEffects.SOUL_ARMOR.get());
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
        if (event.getEffectInstance().getEffect() == GoetyEffects.FREEZING.get()){
            if (event.getEntity().hasEffect(GoetyEffects.SNOW_SKIN.get()) || event.getEntity().getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)){
                event.setResult(Event.Result.DENY);
            }
        }
        if (event.getEffectInstance().getEffect() == GoetyEffects.BUSTED.get()){
            if (event.getEntity().getAttribute(Attributes.ARMOR) == null || event.getEntity().getAttributeValue(Attributes.ARMOR) <= 0.0D){
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
        if (effect == GoetyEffects.SNOW_SKIN.get()){
            if (effected.hasEffect(GoetyEffects.FREEZING.get())){
                effected.removeEffect(GoetyEffects.FREEZING.get());
            }
        }
        if (effect == GoetyEffects.SENSE_LOSS.get()){
            if (effected instanceof Mob mob){
                mob.setTarget(null);
            }
        }
    }

    @SubscribeEvent
    public static void PotionRemoveEvents(MobEffectEvent.Remove event){
        LivingEntity effected = event.getEntity();
        if (effected.hasEffect(GoetyEffects.SAVE_EFFECTS.get())){
            event.setCanceled(event.getEffect() != GoetyEffects.SAVE_EFFECTS.get());
        }
    }
}
