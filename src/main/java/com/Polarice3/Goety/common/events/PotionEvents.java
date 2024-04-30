package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.spells.void_spells.EndWalkSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayEntitySoundPacket;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PotionEvents {

    @SubscribeEvent
    public static void LivingEffects(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null){
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
                        int h = Objects.requireNonNull(livingEntity.getEffect(GoetyEffects.FREEZING.get())).getAmplifier() + 1;
                        MiscCapHelper.setFreezing(livingEntity, h);
                        if (livingEntity.level instanceof ServerLevel serverLevel){
                            if (serverLevel.random.nextFloat() <= 0.25F) {
                                for (int h1 = 0; h1 < h; ++h1) {
                                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.SNOWFLAKE, livingEntity);
                                }
                            }
                        }
                        int i = livingEntity.getTicksFrozen();
                        int j = h * 4;
                        livingEntity.setTicksFrozen(Math.min(livingEntity.getTicksRequiredToFreeze() + 5, i + j));
                    }
                }
            } else {
                if (!livingEntity.level.isClientSide){
                    if (MiscCapHelper.isFreezing(livingEntity)){
                        MiscCapHelper.setFreezing(livingEntity, 0);
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
    public static void DamageEvents(LivingDamageEvent event){
        LivingEntity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();

        if (attacker instanceof LivingEntity living) {
            if (living.hasEffect(GoetyEffects.SHADOW_WALK.get())) {
                float multiply = 1.25F + (EffectsUtil.getAmplifier(living, GoetyEffects.SHADOW_WALK.get()) / 4.0F);
                event.setAmount(event.getAmount() * multiply);
                living.removeEffect(GoetyEffects.SHADOW_WALK.get());
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
            if (SEHelper.hasEndWalk(player)){
                SEHelper.removeEndWalk(player);
            }
        }
        if (event.getSource().getEntity() instanceof Player player){
            if (player.hasEffect(GoetyEffects.CORPSE_EATER.get())){
                MobEffectInstance mobEffectInstance = player.getEffect(GoetyEffects.CORPSE_EATER.get());
                if (mobEffectInstance != null) {
                    int amp = mobEffectInstance.getAmplifier() + 1;
                    int amount = Mth.floor(event.getEntity().getMaxHealth() / 5);
                    amount = (player.level.random.nextInt(amount + 1) + 2) * amp;
                    if (amount > 0) {
                        player.getFoodData().eat(amount, 0.1F);
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
            if (event.getEntity().hasEffect(GoetyEffects.SHADOW_WALK.get())){
                event.modifyVisibility(0.0D);
            }
        }
    }

    @SubscribeEvent
    public static void changeTarget(LivingChangeTargetEvent event){
        LivingEntity target = event.getOriginalTarget();
        if (target != null) {
            if (target.hasEffect(GoetyEffects.SHADOW_WALK.get())) {
                event.setNewTarget(null);
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
        if (event.getItem().getItem() == Items.APPLE){
            for(MobEffectInstance mobeffectinstance : PotionUtils.getMobEffects(event.getItem())) {
                if (mobeffectinstance.getEffect().isInstantenous()) {
                    mobeffectinstance.getEffect().applyInstantenousEffect(event.getEntity(), event.getEntity(), event.getEntity(), mobeffectinstance.getAmplifier(), 1.0D);
                } else {
                    event.getEntity().addEffect(new MobEffectInstance(mobeffectinstance));
                }
            }
            for (BrewEffectInstance brewEffectInstance : BrewUtils.getBrewEffects(event.getItem())){
                brewEffectInstance.getEffect().drinkBlockEffect(event.getEntity(), event.getEntity(), event.getEntity(), brewEffectInstance.getAmplifier(), BrewUtils.getAreaOfEffect(event.getItem()));
            }
        }
        if (!(event.getItem().getItem() instanceof IWand)) {
            if (event.getEntity().hasEffect(GoetyEffects.SHADOW_WALK.get())) {
                event.getEntity().removeEffect(GoetyEffects.SHADOW_WALK.get());
            }
        }
    }

    @SubscribeEvent
    public static void onCastSpell(CastMagicEvent event){
        if (!(event.getSpell() instanceof EndWalkSpell)){
            if (event.getEntity().hasEffect(GoetyEffects.SHADOW_WALK.get())) {
                event.getEntity().removeEffect(GoetyEffects.SHADOW_WALK.get());
            }
        }
    }

    @SubscribeEvent
    public static void ProjectileAddEvents(EntityJoinLevelEvent event){
        if (!event.getLevel().isClientSide){
            if (event.getEntity() instanceof Projectile projectile){
                if (projectile.getOwner() instanceof LivingEntity livingEntity){
                    if (livingEntity.hasEffect(GoetyEffects.SHADOW_WALK.get())){
                        livingEntity.removeEffect(GoetyEffects.SHADOW_WALK.get());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void PlayerInteractItemEvents(PlayerInteractEvent.RightClickItem event){
    }

    @SubscribeEvent
    public static void PlayerInteractEntityEvents(PlayerInteractEvent.EntityInteract event){
        Player player = event.getEntity();
        if (player.hasEffect(GoetyEffects.SHADOW_WALK.get())){
            if (SEHelper.hasEndWalk(player)) {
                if (event.getTarget() instanceof Merchant merchant) {
                    merchant.setTradingPlayer(null);
                }
            }
            player.removeEffect(GoetyEffects.SHADOW_WALK.get());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void PlayerInteractBlockEvents(PlayerInteractEvent.RightClickBlock event){
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockHitResult blockHitResult = event.getHitVec();
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);
        if (player.hasEffect(GoetyEffects.SHADOW_WALK.get())){
            if (blockState.use(level, player, player.getUsedItemHand(), blockHitResult).consumesAction()){
                player.removeEffect(GoetyEffects.SHADOW_WALK.get());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void BreakingBlockEvents(BlockEvent.BreakEvent event){
        Player player = event.getPlayer();
        if (!event.getState().isAir()) {
            if (player.hasEffect(GoetyEffects.SHADOW_WALK.get())) {
                player.removeEffect(GoetyEffects.SHADOW_WALK.get());
            }
        }
    }

    @SubscribeEvent
    public static void PlacingBlockEvents(BlockEvent.EntityPlaceEvent event){
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living) {
            if (!event.getState().isAir()) {
                if (living.hasEffect(GoetyEffects.SHADOW_WALK.get())) {
                    living.removeEffect(GoetyEffects.SHADOW_WALK.get());
                }
            }
        }
    }

    @SubscribeEvent
    public static void DimensionChangeEvents(EntityTravelToDimensionEvent event){
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living){
            if (living.hasEffect(GoetyEffects.SHADOW_WALK.get())) {
                if (living instanceof Player player){
                    if (SEHelper.hasEndWalk(player)){
                        SEHelper.removeEndWalk(player);
                    }
                }
                living.removeEffect(GoetyEffects.SHADOW_WALK.get());
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
            if (CuriosFinder.hasWindyRobes(event.getEntity())){
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
        if (event.getEffectInstance().getEffect() == GoetyEffects.DOOM.get()){
            if (!event.getEntity().canChangeDimensions() || event.getEntity().getType().is(Tags.EntityTypes.BOSSES)){
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
        if (effected != null) {
            if (event.getEffect() != null) {
                if (effected.hasEffect(GoetyEffects.SAVE_EFFECTS.get())) {
                    event.setCanceled(event.getEffect() != GoetyEffects.SAVE_EFFECTS.get()
                            && (effected instanceof Player player
                            && SEHelper.hasEndWalk(player)
                            && event.getEffect() != GoetyEffects.SHADOW_WALK.get()));
                }
                if (event.getEffect() != null) {
                    if (event.getEffect() == GoetyEffects.SHADOW_WALK.get()) {
                        if (effected instanceof Player player) {
                            teleportShadowWalk(player);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void PotionExpiredEvents(MobEffectEvent.Expired event){
        LivingEntity effected = event.getEntity();
        MobEffectInstance mobEffectInstance = event.getEffectInstance();
        if (mobEffectInstance != null) {
            if (mobEffectInstance.getEffect() == GoetyEffects.SHADOW_WALK.get()) {
                if (effected instanceof Player player) {
                    teleportShadowWalk(player);
                }
            }

            if (mobEffectInstance.getEffect() == GoetyEffects.DOOM.get()){
                int a = mobEffectInstance.getAmplifier() + 1;
                float doom = 0.05F * a;
                if (effected.getHealth() <= effected.getMaxHealth() * doom){
                    effected.hurt(ModDamageSource.DOOM, effected.getMaxHealth() * 20);
                }
            }
        }
    }

    public static void teleportShadowWalk(Player player){
        BlockPos blockPos = SEHelper.getEndWalkPos(player);
        if (blockPos != null) {
            if (SEHelper.getEndWalkDimension(player) != null
                    && player.level.dimension() == SEHelper.getEndWalkDimension(player)) {
                player.closeContainer();
                player.teleportTo(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D);
                if (!player.level.isClientSide) {
                    player.level.broadcastEntityEvent(player, (byte) 46);
                    ModNetwork.sendToALL(new SPlayWorldSoundPacket(player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                    ModNetwork.sendToALL(new SPlayEntitySoundPacket(player.getUUID(), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                }
                Vec3 vec3 = player.position();
                player.level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(player));
            }
            SEHelper.removeEndWalk(player);
        }
    }
}
