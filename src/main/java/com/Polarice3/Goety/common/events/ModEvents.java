package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.capabilities.lichdom.ILichdom;
import com.Polarice3.Goety.common.capabilities.lichdom.LichProvider;
import com.Polarice3.Goety.common.capabilities.soulenergy.ISoulEnergy;
import com.Polarice3.Goety.common.capabilities.soulenergy.SEProvider;
import com.Polarice3.Goety.common.effects.ModEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ai.WitchBarterGoal;
import com.Polarice3.Goety.common.entities.ally.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.HauntedSkull;
import com.Polarice3.Goety.common.entities.ally.ZombieServant;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.entities.neutral.AbstractWraith;
import com.Polarice3.Goety.common.entities.neutral.IOwned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.common.entities.projectiles.Fangs;
import com.Polarice3.Goety.common.entities.util.StormEntity;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.equipment.DarkScytheItem;
import com.Polarice3.Goety.common.items.equipment.DeathScytheItem;
import com.Polarice3.Goety.compat.patchouli.PatchouliLoaded;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player player = event.getEntity();
            Player original = event.getOriginal();

            original.reviveCaps();

            ILichdom capability2 = LichdomHelper.getCapability(original);

            player.getCapability(LichProvider.CAPABILITY)
                    .ifPresent(lichdom ->
                            lichdom.setLichdom(capability2.getLichdom()));

            ISoulEnergy capability3 = SEHelper.getCapability(original);

            player.getCapability(SEProvider.CAPABILITY)
                    .ifPresent(soulEnergy ->
                            soulEnergy.setSEActive(capability3.getSEActive()));
            player.getCapability(SEProvider.CAPABILITY)
                    .ifPresent(soulEnergy ->
                            soulEnergy.setSoulEnergy(capability3.getSoulEnergy()));
            player.getCapability(SEProvider.CAPABILITY)
                    .ifPresent(soulEnergy ->
                            soulEnergy.setArcaBlock(capability3.getArcaBlock()));
            player.getCapability(SEProvider.CAPABILITY)
                    .ifPresent(soulEnergy ->
                            soulEnergy.setArcaBlockDimension(capability3.getArcaBlockDimension()));
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        Level world = event.getLevel();
        if (entity instanceof LivingEntity && !world.isClientSide()) {
            if (entity instanceof Player player) {
                SEHelper.sendSEUpdatePacket(player);
                LichdomHelper.sendLichUpdatePacket(player);
            }
            if (entity instanceof Witch witch){
                witch.goalSelector.addGoal(1, new WitchBarterGoal(witch));
            }
        }
        if (entity instanceof StormEntity){
            if (!entity.level.isClientSide){
                ServerLevel serverWorld = (ServerLevel) entity.level;
                serverWorld.setWeatherParameters(0, 6000, true, true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerFirstEntersWorld(PlayerEvent.PlayerLoggedInEvent event){
        CompoundTag playerData = event.getEntity().getPersistentData();
        CompoundTag data;

        if (!playerData.contains(Player.PERSISTED_NBT_TAG)) {
            data = new CompoundTag();
        } else {
            data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
        }
        if (!event.getEntity().level.isClientSide) {
            if (MainConfig.StarterTotem.get()) {
                if (!data.getBoolean("goety:gotTotem")) {
                    event.getEntity().addItem(new ItemStack(ModItems.TOTEM_OF_SOULS.get()));
                    data.putBoolean("goety:gotTotem", true);
                    playerData.put(Player.PERSISTED_NBT_TAG, data);
                }
            }
            if (PatchouliLoaded.PATCHOULI.isLoaded()){
                if (MainConfig.StarterBook.get()){
                    if (!data.getBoolean("goety:starterBook")) {
                        ItemStack book = PatchouliAPI.get().getBookStack(Goety.location("black_book"));
                        event.getEntity().addItem(book);
                        data.putBoolean("goety:starterBook", true);
                        playerData.put(Player.PERSISTED_NBT_TAG, data);
                    }
                }
            }
        }

    }

    private static final Map<ServerLevel, EffectsEvents> EFFECTS_EVENT_MAP = new HashMap<>();

    @SubscribeEvent
    public static void worldLoad(LevelEvent.Load event) {
        if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverWorld) {
            EFFECTS_EVENT_MAP.put(serverWorld, new EffectsEvents());
        }
    }

    @SubscribeEvent
    public static void worldUnload(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverWorld) {
            EFFECTS_EVENT_MAP.remove(serverWorld);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.LevelTickEvent tick){
        if(!tick.level.isClientSide && tick.level instanceof ServerLevel serverWorld){
            EffectsEvents effectsEvent = EFFECTS_EVENT_MAP.get(serverWorld);
            if (effectsEvent != null){
                effectsEvent.tick(serverWorld);
            }
        }

    }

    @SubscribeEvent
    public static void PlayerTick(TickEvent.PlayerTickEvent event){
        Player player = event.player;
        Level world = player.level;
        int zombies = 0;
        int skeletons = 0;
        int wraith = 0;
        int skull = 0;
        for (Owned summonedEntity : world.getEntitiesOfClass(Owned.class, player.getBoundingBox().inflate(32.0D))){
            if (summonedEntity.getTrueOwner() == player && summonedEntity.isAlive()){
                if (summonedEntity instanceof ZombieServant || summonedEntity instanceof ZPiglinServant){
                    ++zombies;
                    if (SpellConfig.ZombieLimit.get() < zombies){
                        if (summonedEntity.tickCount % 20 == 0){
                            summonedEntity.hurt(DamageSource.STARVE, summonedEntity.getMaxHealth()/4);
                        }
                    }
                }
                if (summonedEntity instanceof AbstractSkeletonServant){
                    ++skeletons;
                    if (SpellConfig.SkeletonLimit.get() < skeletons){
                        if (summonedEntity.tickCount % 20 == 0){
                            summonedEntity.hurt(DamageSource.STARVE, summonedEntity.getMaxHealth()/4);
                        }
                    }
                }
                if (summonedEntity instanceof AbstractWraith){
                    ++wraith;
                    if (SpellConfig.WraithLimit.get() < wraith){
                        if (summonedEntity.tickCount % 20 == 0){
                            summonedEntity.hurt(DamageSource.STARVE, summonedEntity.getMaxHealth()/4);
                        }
                    }
                }
                if (summonedEntity instanceof HauntedSkull){
                    ++skull;
                    if (skull > 8){
                        if (summonedEntity.tickCount % 20 == 0){
                            summonedEntity.hurt(DamageSource.STARVE, summonedEntity.getMaxHealth()/2);
                        }
                    }
                }
            }
        }

        AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
        boolean scythe = player.getMainHandItem().getItem() instanceof DarkScytheItem;

        float increaseAttackSpeed0 = 0.25F;
        AttributeModifier attributemodifier0 = new AttributeModifier(UUID.fromString("0c091f42-8c6d-4fde-96e9-148115731cbf"), "Two Handed Scythe", increaseAttackSpeed0, AttributeModifier.Operation.MULTIPLY_TOTAL);
        boolean flag0 = scythe && player.getOffhandItem().isEmpty();
        if (attackSpeed != null){
            if (flag0){
                if (!attackSpeed.hasModifier(attributemodifier0)){
                    attackSpeed.addPermanentModifier(attributemodifier0);
                }
            } else {
                if (attackSpeed.hasModifier(attributemodifier0)){
                    attackSpeed.removeModifier(attributemodifier0);
                }
            }
        }

        float increaseAttackSpeed = 0.5F;
        AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString("d4818bbc-54ed-4ecf-95a3-a15fbf71b31d"), "Scythe Proficiency", increaseAttackSpeed, AttributeModifier.Operation.MULTIPLY_TOTAL);
        boolean flag = CuriosFinder.hasCurio(player, ModItems.GRAVE_GLOVE.get()) && scythe;
        if (attackSpeed != null){
            if (flag){
                if (!attackSpeed.hasModifier(attributemodifier)){
                    attackSpeed.addPermanentModifier(attributemodifier);
                }
            } else {
                if (attackSpeed.hasModifier(attributemodifier)){
                    attackSpeed.removeModifier(attributemodifier);
                }
            }
        }
    }

    @SubscribeEvent
    public static void EmptyClickEvents(PlayerInteractEvent.LeftClickEmpty event){
        DeathScytheItem.emptyClick(event.getItemStack());
    }

    @SubscribeEvent
    public static void PlayerAttackEvents(AttackEntityEvent event){
        DeathScytheItem.entityClick(event.getEntity(), event.getEntity().level);
    }

    @SubscribeEvent
    public static void LivingEffects(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null){
            if (livingEntity.hasEffect(ModEffects.BURN_HEX.get())){
                if (livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)){
                    livingEntity.removeEffectNoUpdate(MobEffects.FIRE_RESISTANCE);
                }
            }
            if (CuriosFinder.hasWitchSet(livingEntity)){
                if (livingEntity.getRandom().nextFloat() < 7.5E-4F){
                    for(int i = 0; i < livingEntity.getRandom().nextInt(35) + 10; ++i) {
                        livingEntity.level.addParticle(ParticleTypes.WITCH, livingEntity.getX() + livingEntity.getRandom().nextGaussian() * (double)0.13F, livingEntity.getBoundingBox().maxY + 0.5D + livingEntity.getRandom().nextGaussian() * (double)0.13F, livingEntity.getZ() + livingEntity.getRandom().nextGaussian() * (double)0.13F, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void TargetEvents(LivingChangeTargetEvent event){
        LivingEntity attacker = event.getEntity();
        LivingEntity target = event.getOriginalTarget();
        if (attacker instanceof Mob mobAttacker) {
            if (target != null) {
                if (target instanceof Player) {
                    if (mobAttacker.getLastHurtByMob() instanceof IOwned && !(mobAttacker instanceof Apostle)){
                        event.setNewTarget(mobAttacker.getLastHurtByMob());
                    }
                    if (mobAttacker instanceof Witch){
                        if (CuriosFinder.hasWitchSet(target)){
                            if (mobAttacker.getLastHurtByMob() != target){
                                event.setNewTarget(null);
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }
                    if (CuriosFinder.hasNecroSet(target)){
                        boolean undead = mobAttacker.getMobType() == MobType.UNDEAD && mobAttacker.getMaxHealth() < 50.0F && !(mobAttacker instanceof IOwned && !(mobAttacker instanceof Enemy));
                        if (undead){
                            if (mobAttacker.getLastHurtByMob() != target){
                                event.setNewTarget(null);
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }
                }
                if ((mobAttacker.getMobType() == MobType.UNDEAD && !(mobAttacker instanceof IOwned) && mobAttacker.getMaxHealth() < 100.0F) || mobAttacker instanceof Creeper){
                    if (target instanceof Apostle){
                        event.setNewTarget(null);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void VisibilityEvent(LivingEvent.LivingVisibilityEvent event){
        LivingEntity entity = event.getEntity();
        if (event.getLookingEntity() instanceof LivingEntity looker) {
            boolean undead = looker.getMobType() == MobType.UNDEAD && looker.getMaxHealth() < 50.0F && !(looker instanceof IOwned && !(looker instanceof Enemy));
            if (CuriosFinder.hasCurio(entity, ModItems.NECRO_CROWN.get())) {
                if (undead) {
                    event.modifyVisibility(0.5);
                }
            }
            if (CuriosFinder.hasCurio(entity, ModItems.NECRO_CAPE.get())) {
                if (undead) {
                    event.modifyVisibility(0.5);
                }
            }
            if (CuriosFinder.hasCurio(entity, ModItems.ILLUSION_ROBE.get())){
                if (looker.getArmorCoverPercentage() < 0.1F){
                    event.modifyVisibility(0.0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void InteractEntityEvent(PlayerInteractEvent.EntityInteractSpecific event){
        if (CuriosFinder.hasWitchSet(event.getEntity())){
            if (event.getItemStack().getItem() == Items.EMERALD){
                if (event.getTarget() instanceof Witch witch){
                    if (!witch.isAggressive() && witch.getMainHandItem().isEmpty()){
                        event.getEntity().getMainHandItem().shrink(1);
                        witch.playSound(SoundEvents.WITCH_CELEBRATE);
                        witch.setItemInHand(InteractionHand.MAIN_HAND, event.getItemStack());
                        WitchBarterGoal.setTrader(witch, event.getEntity());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void AttackEvent(LivingAttackEvent event){
        LivingEntity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        Entity direct = event.getSource().getDirectEntity();

        if (ModDamageSource.freezeAttacks(event.getSource())){
            if (victim.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)){
                event.setCanceled(true);
            }
        }

        if (SpellConfig.MinionsMasterImmune.get()){
            if (attacker instanceof IOwned){
                if (((IOwned) attacker).getTrueOwner() == victim){
                    event.setCanceled(true);
                }
            }
        }
        if (SpellConfig.OwnerAttackCancel.get()){
            if (attacker != null) {
                if (victim instanceof IOwned) {
                    if (((IOwned) victim).getTrueOwner() == attacker) {
                        event.setCanceled(true);
                    }
                }
            }
        }

        if (event.getSource() instanceof NoKnockBackDamageSource damageSource){
            if (damageSource.getOwner() != null) {
                if (damageSource.getOwner() instanceof LivingEntity) {
                    victim.setLastHurtByMob((LivingEntity) damageSource.getOwner());
                }
                if (damageSource.getOwner() instanceof Player) {
                    victim.setLastHurtByPlayer((Player) damageSource.getOwner());
                }
                if (damageSource.getOwner() instanceof ServerPlayer) {
                    CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer) damageSource.getOwner(), victim, event.getSource(), event.getAmount(), event.getAmount(), false);
                }
            }
        }

        if (direct instanceof AbstractArrow arrowEntity){
            if (arrowEntity.getTags().contains(ConstantPaths.rainArrow()) || arrowEntity.getOwner() instanceof Apostle){
                if (arrowEntity.getOwner() != null) {
                    if (victim instanceof Owned ownedEntity) {
                        if (ownedEntity.getTrueOwner() != null) {
                            if (ownedEntity.getTrueOwner() == arrowEntity.getOwner()) {
                                event.setCanceled(true);
                            }
                        }
                    }
                    if (victim == arrowEntity.getOwner()){
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void PlayerAttackEvent(AttackEntityEvent event){
        if (SpellConfig.OwnerAttackCancel.get()) {
            if (event.getTarget() instanceof IOwned){
                if (((IOwned) event.getTarget()).getTrueOwner() == event.getEntity()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void HurtEvent(LivingHurtEvent event){
        LivingEntity victim = event.getEntity();
        if (victim.level.getDifficulty() == Difficulty.HARD){
            if (victim.hasEffect(ModEffects.BURN_HEX.get())){
                MobEffectInstance effectInstance = victim.getEffect(ModEffects.BURN_HEX.get());
                int i = 2;
                if (effectInstance != null) {
                    i = effectInstance.getAmplifier() + 2;
                }
                if (event.getSource().isFire()){
                    event.setAmount(event.getAmount() * i);
                }
            }
        }
        if (CuriosFinder.hasCurio(victim, ModItems.WITCH_ROBE.get())){
            if (victim instanceof Player player){
                if (!LichdomHelper.isLich(player)){
                    if (event.getSource().isMagic()){
                        event.setAmount(event.getAmount() * 0.15F);
                    }
                }
            }
        }
        if (victim.hasEffect(ModEffects.SAPPED.get())){
            MobEffectInstance effectInstance = victim.getEffect(ModEffects.SAPPED.get());
            if (effectInstance != null) {
                int i = effectInstance.getAmplifier() / 10;
                float j = 1.2F + ((float) i * 2);
                event.setAmount(event.getAmount() * j);
            }
        }
        if (ModDamageSource.freezeAttacks(event.getSource())){
            int i = victim.getTicksFrozen();
            victim.setTicksFrozen(Math.min(victim.getTicksRequiredToFreeze(), i + 1));
            if (victim.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)){
                event.setAmount(event.getAmount() * 2);
            }
        }
        if (event.getSource().getDirectEntity() instanceof LivingEntity livingAttacker){
            if (ModDamageSource.physicalAttacks(event.getSource())) {
                if (livingAttacker.getMainHandItem().getItem() instanceof TieredItem weapon) {
                    if (weapon instanceof DarkScytheItem){
                        victim.playSound(ModSounds.SCYTHE_HIT_MEATY.get());
                    }
                    if (weapon instanceof DeathScytheItem) {
                        if (!victim.hasEffect(ModEffects.SAPPED.get())) {
                            victim.addEffect(new MobEffectInstance(ModEffects.SAPPED.get(), 20));
                            victim.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 1.0F);
                        } else {
                            if (victim.level.random.nextBoolean()) {
                                EffectsUtil.amplifyEffect(victim, ModEffects.SAPPED.get(), 20);
                                victim.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 1.0F);
                            } else {
                                EffectsUtil.resetDuration(victim, ModEffects.SAPPED.get(), 20);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void DamageEvent(LivingDamageEvent event){
        LivingEntity entity = event.getEntity();
        if (event.getSource().getEntity() instanceof IOwned summonedEntity){
            if (summonedEntity.getTrueOwner() != null){
                if (summonedEntity.getTrueOwner() == entity){
                    event.setCanceled(true);
                }
            }
        }

        if (event.getSource().getDirectEntity() instanceof Fangs fangEntity){
            if (fangEntity.getOwner() instanceof Player player) {
                if (fangEntity.isAbsorbing()) {
                    player.heal(event.getAmount());
                }
            }
        }
    }

    @SubscribeEvent
    public static void SpecialDeath(LivingDeathEvent event){
        LivingEntity killed = event.getEntity();
        Entity killer = event.getSource().getEntity();
        Level world = killed.getCommandSenderWorld();
        if (killer instanceof Player player){
            int looting = Mth.clamp(EnchantmentHelper.getMobLooting(player), 0, 3);
            if (world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)){
                Entity entity = event.getSource().getDirectEntity();
                if (entity instanceof Fangs){
                    if (CuriosFinder.findRing(player).getItem() == ModItems.RING_OF_WANT.get()) {
                        if (EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.WANTING.get(), CuriosFinder.findRing(player)) >= 3) {
                            if (world.random.nextFloat() <= 0.25F) {
                                if (killed.getType() == EntityType.SKELETON) {
                                    killed.spawnAtLocation(new ItemStack(Items.SKELETON_SKULL));
                                }
                                if (killed.getType() == EntityType.ZOMBIE) {
                                    killed.spawnAtLocation(new ItemStack(Items.ZOMBIE_HEAD));
                                }
                                if (killed.getType() == EntityType.CREEPER) {
                                    killed.spawnAtLocation(new ItemStack(Items.CREEPER_HEAD));
                                }
                                if (killed.getType() == EntityType.WITHER_SKELETON) {
                                    killed.spawnAtLocation(new ItemStack(Items.WITHER_SKELETON_SKULL));
                                }
                                if (MainConfig.TallSkullDrops.get()) {
                                    if (killed instanceof Villager || killed instanceof AbstractIllager) {
                                        killed.spawnAtLocation(new ItemStack(ModBlocks.TALL_SKULL_ITEM.get()));
                                    }
                                    if (killed instanceof Witch) {
                                        killed.spawnAtLocation(new ItemStack(ModBlocks.TALL_SKULL_ITEM.get()));
                                    }
                                }
                            }
                            if (killed instanceof Player player1) {
                                CompoundTag tag = new CompoundTag();
                                tag.putString("SkullOwner", player1.getDisplayName().getString());
                                ItemStack head = new ItemStack(Items.PLAYER_HEAD);
                                head.setTag(tag);
                                killed.spawnAtLocation(head);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void SpellLoot(LootingLevelEvent event){
        if (event.getDamageSource() != null) {
            if (event.getEntity() != null) {
                if (!event.getEntity().level.isClientSide) {
                    int looting = 0;
                    if (event.getDamageSource() instanceof NoKnockBackDamageSource damageSource){
                        if (damageSource.getOwner() != null){
                            if (damageSource.getOwner() instanceof Player player) {
                                if (EnchantmentHelper.getMobLooting(player) != 0){
                                    looting = EnchantmentHelper.getMobLooting(player);
                                } else if (CuriosFinder.findRing(player).getItem() == ModItems.RING_OF_WANT.get()) {
                                    if (CuriosFinder.findRing(player).isEnchanted()) {
                                        looting = CuriosFinder.findRing(player).getEnchantmentLevel(ModEnchantments.WANTING.get());
                                    }
                                }
                                event.setLootingLevel(event.getLootingLevel() + looting);
                            }
                            if (damageSource.getOwner() instanceof IOwned ownedEntity) {
                                if (ownedEntity.getTrueOwner() instanceof Player player) {
                                    if (CuriosFinder.findRing(player).getItem() == ModItems.RING_OF_WANT.get()) {
                                        if (CuriosFinder.findRing(player).isEnchanted()) {
                                            looting = CuriosFinder.findRing(player).getEnchantmentLevel(ModEnchantments.WANTING.get());
                                        }
                                    }
                                    event.setLootingLevel(event.getLootingLevel() + looting);
                                }
                            }
                        }
                    }
                    if (event.getDamageSource().getEntity() != null) {
                        if (event.getDamageSource().getEntity() instanceof Player player) {
                            Entity spell = event.getDamageSource().getDirectEntity();
                            if (CuriosFinder.findRing(player).getItem() == ModItems.RING_OF_WANT.get()) {
                                if (CuriosFinder.findRing(player).isEnchanted()) {
                                    looting = CuriosFinder.findRing(player).getEnchantmentLevel(ModEnchantments.WANTING.get());
                                }
                            }
                            if (looting > EnchantmentHelper.getMobLooting(player)) {
                                if (spell != null) {
                                    if (!(spell instanceof LivingEntity)) {
                                        event.setLootingLevel(event.getLootingLevel() + looting);
                                    }
                                }
/*                                if (ModDamageSource.breathAttacks(event.getDamageSource())) {
                                    event.setLootingLevel(event.getLootingLevel() + looting);
                                }*/
                            }
                        }
                        if (event.getDamageSource().getEntity() instanceof IOwned ownedEntity) {
                            if (ownedEntity instanceof LivingEntity) {
                                if (ownedEntity.getTrueOwner() instanceof Player player) {
                                    if (CuriosFinder.findRing(player).getItem() == ModItems.RING_OF_WANT.get()) {
                                        if (CuriosFinder.findRing(player).isEnchanted()) {
                                            looting = CuriosFinder.findRing(player).getEnchantmentLevel(ModEnchantments.WANTING.get());
                                        }
                                    }
                                    if (looting > EnchantmentHelper.getMobLooting((LivingEntity) ownedEntity)) {
                                        event.setLootingLevel(event.getLootingLevel() + looting);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void DropEvents(LivingDropsEvent event){
        if (event.getEntity() != null) {
            LivingEntity living = event.getEntity();
            if (living instanceof Ravager){
                event.getDrops().add(ItemHelper.itemEntityDrop(living, new ItemStack(ModItems.SAVAGE_TOOTH.get(), living.level.random.nextInt(2))));
            }
            if (living instanceof Witch){
                if (living.level.getServer() != null) {
                    LootTable loottable = living.level.getServer().getLootTables().get(ModLootTables.WITCH);
                    LootContext.Builder lootcontext$builder = MobUtil.createLootContext(event.getSource(), living);
                    LootContext ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
                    loottable.getRandomItems(ctx).forEach((loot) -> event.getDrops().add(ItemHelper.itemEntityDrop(living, loot)));
                }
            }
            if (MainConfig.TallSkullDrops.get()) {
                if (living instanceof AbstractVillager || living instanceof AbstractIllager || living instanceof Witch || living instanceof Cultist) {
                    if (living.level.getServer() != null) {
                        LootTable loottable = living.level.getServer().getLootTables().get(ModLootTables.TALL_SKULL);
                        LootContext.Builder lootcontext$builder = MobUtil.createLootContext(event.getSource(), living);
                        LootContext ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
                        loottable.getRandomItems(ctx).forEach((loot) -> event.getDrops().add(ItemHelper.itemEntityDrop(living, loot)));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void finishItemEvents(LivingEntityUseItemEvent.Finish event){
        if (event.getItem().getItem() == Items.MILK_BUCKET){
            if (event.getEntity().hasEffect(ModEffects.ILLAGUE.get())){
                int duration = Objects.requireNonNull(event.getEntity().getEffect(ModEffects.ILLAGUE.get())).getDuration();
                int amp = Objects.requireNonNull(event.getEntity().getEffect(ModEffects.ILLAGUE.get())).getAmplifier();
                if (duration > 0){
                    if (amp <= 0) {
                        EffectsUtil.halveDuration(event.getEntity(), ModEffects.ILLAGUE.get(), duration, false, false);
                    } else {
                        EffectsUtil.deamplifyEffect(event.getEntity(), ModEffects.ILLAGUE.get(), duration, false, false);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void SleepEvents(PlayerWakeUpEvent event){
        Player player = event.getEntity();
        if (player.isSleepingLongEnough()) {
            if (player.hasEffect(ModEffects.ILLAGUE.get())) {
                int duration = Objects.requireNonNull(player.getEffect(ModEffects.ILLAGUE.get())).getDuration();
                int amp = Objects.requireNonNull(player.getEffect(ModEffects.ILLAGUE.get())).getAmplifier();
                if (duration > 0){
                    if (amp <= 0) {
                        EffectsUtil.halveDuration(player, ModEffects.ILLAGUE.get(), duration, false, false);
                    } else {
                        EffectsUtil.deamplifyEffect(player, ModEffects.ILLAGUE.get(), duration, false, false);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void UseItemEvent(LivingEntityUseItemEvent.Finish event){
        if (CuriosFinder.hasCurio(event.getEntity(), ModItems.WITCH_HAT.get())){
            if (event.getEntity().level.random.nextFloat() <= 0.1F){
                if (event.getItem().getItem() instanceof PotionItem){
                    event.setResultStack(event.getItem());
                }
            }
        }
    }

    @SubscribeEvent
    public static void KnockBackEvents(LivingKnockBackEvent event){
        LivingEntity knocked = event.getEntity();
        DamageSource lastDamage = knocked.getLastDamageSource();
        if (lastDamage != null) {
            if (lastDamage instanceof NoKnockBackDamageSource){
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void ExplosionDetonateEvent(ExplosionEvent.Detonate event){
        if (event.getExplosion() != null) {
            event.getAffectedEntities().removeIf(entity -> (entity instanceof ItemEntity && ((ItemEntity) entity).getItem().getItem() == ModItems.UNHOLY_BLOOD.get()));
            if (event.getExplosion().getSourceMob() != null) {
                if (event.getExplosion().getSourceMob() instanceof Apostle) {
                    event.getAffectedEntities().removeIf(entity -> (entity instanceof Owned && ((Owned) entity).getTrueOwner() instanceof Apostle) || (entity == event.getExplosion().getSourceMob()));
                }
                if (event.getExplosion().getSourceMob() instanceof Owned sourceMob) {
                    if (sourceMob.getTrueOwner() instanceof Apostle) {
                        event.getAffectedEntities().removeIf(entity -> (entity instanceof Owned && ((Owned) entity).getTrueOwner() instanceof Apostle) || entity == sourceMob.getTrueOwner());
                    }
                    if (sourceMob instanceof HauntedSkull){
                        event.getAffectedEntities().removeIf(entity -> (entity instanceof Owned && ((Owned) entity).getTrueOwner() == sourceMob.getTrueOwner() || entity == sourceMob.getTrueOwner()));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void ProjectileImpactEvent(ProjectileImpactEvent event){
        if (event.getProjectile() instanceof AbstractArrow arrowEntity) {
            if (arrowEntity.getTags().contains(ConstantPaths.rainArrow())) {
                arrowEntity.discard();
            }
        }
    }

    @SubscribeEvent
    public static void PotionApplicationEvents(MobEffectEvent.Applicable event){
        if (event.getEffectInstance().getEffect() == MobEffects.FIRE_RESISTANCE){
            if (event.getEntity().hasEffect(ModEffects.BURN_HEX.get())){
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void PotionAddedEvents(MobEffectEvent.Added event){
        MobEffect effect = event.getEffectInstance().getEffect();
        if (effect == ModEffects.BURN_HEX.get()){
            if (event.getEntity().hasEffect(MobEffects.FIRE_RESISTANCE)){
                event.getEntity().removeEffect(MobEffects.FIRE_RESISTANCE);
            }
        }
    }
}
