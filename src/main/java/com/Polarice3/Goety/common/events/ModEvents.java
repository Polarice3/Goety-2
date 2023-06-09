package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.capabilities.lichdom.ILichdom;
import com.Polarice3.Goety.common.capabilities.lichdom.LichProvider;
import com.Polarice3.Goety.common.capabilities.soulenergy.ISoulEnergy;
import com.Polarice3.Goety.common.capabilities.soulenergy.SEProvider;
import com.Polarice3.Goety.common.effects.ModEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.TargetHostileOwnedGoal;
import com.Polarice3.Goety.common.entities.ai.WitchBarterGoal;
import com.Polarice3.Goety.common.entities.ally.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.HauntedSkull;
import com.Polarice3.Goety.common.entities.ally.ZombieServant;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.entities.hostile.cultists.Warlock;
import com.Polarice3.Goety.common.entities.hostile.illagers.Envioker;
import com.Polarice3.Goety.common.entities.hostile.illagers.HuntingIllagerEntity;
import com.Polarice3.Goety.common.entities.neutral.AbstractWraith;
import com.Polarice3.Goety.common.entities.neutral.IOwned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.common.entities.projectiles.Fangs;
import com.Polarice3.Goety.common.entities.projectiles.ThrowableFungus;
import com.Polarice3.Goety.common.entities.util.StormEntity;
import com.Polarice3.Goety.common.items.ISoulRepair;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.common.items.armor.ModArmorMaterials;
import com.Polarice3.Goety.common.items.curios.WarlockGarmentItem;
import com.Polarice3.Goety.common.items.equipment.DarkScytheItem;
import com.Polarice3.Goety.common.items.equipment.DeathScytheItem;
import com.Polarice3.Goety.common.items.equipment.PhilosophersMaceItem;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import com.Polarice3.Goety.compat.patchouli.PatchouliLoaded;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.*;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
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
            if (entity instanceof AbstractGolem golemEntity && !(entity instanceof Enemy)){
                golemEntity.targetSelector.addGoal(3, new TargetHostileOwnedGoal<>(golemEntity, Owned.class));
            }
        }
        if (entity instanceof StormEntity){
            if (!entity.level.isClientSide){
                ServerLevel serverWorld = (ServerLevel) entity.level;
                serverWorld.setWeatherParameters(0, 6000, true, true);
            }
        }
        if (entity instanceof Raider raider){
            if (world instanceof ServerLevel serverLevel) {
                if (raider.hasActiveRaid()) {
                    Raid raid = raider.getCurrentRaid();
                    if (raid != null && raid.isActive() && !raid.isBetweenWaves() && !raid.isOver() && !raid.isStopped()) {
                        int h = raid.getGroupsSpawned() + 1;
                        boolean flag1 = MobUtil.shouldSpawnBonusGroup(raid);
                        int warlockNum = raid.getGroupsSpawned() == 4 ? 1 : raid.getGroupsSpawned() == 5 ? 2 : raid.getGroupsSpawned() == 7 ? 1 : 0;
                        if (entity == raid.getLeader(raider.getWave())){
                            for (int i = 0; i < warlockNum + MobUtil.getPotentialBonusSpawns(Raid.RaiderType.WITCH, serverLevel.random, h, serverLevel.getCurrentDifficultyAt(raid.getCenter()), flag1); ++i){
                                Warlock warlock = new Warlock(ModEntityType.WARLOCK.get(), serverLevel);
                                warlock.setPos(entity.position());
                                warlock.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(raider.blockPosition()), MobSpawnType.EVENT, null, null);
                                if (serverLevel.random.nextFloat() <= 0.25F){
                                    AbstractHorse donkey = new Donkey(EntityType.DONKEY, serverLevel);
                                    if (serverLevel.random.nextFloat() <= 0.25F){
                                        donkey = new Mule(EntityType.MULE, serverLevel);
                                    }
                                    donkey.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(raider.blockPosition()), MobSpawnType.EVENT, null, null);
                                    donkey.setAge(0);
                                    donkey.setTamed(true);
                                    donkey.setOwnerUUID(warlock.getUUID());
                                    donkey.setPos(warlock.position());
                                    serverLevel.addFreshEntity(donkey);
                                    warlock.startRiding(donkey);
                                }
                                serverLevel.addFreshEntity(warlock);
                            }
                        }
                        Player player = EntityFinder.getNearbyPlayer(world, raid.getCenter());
                        if (player != null) {
                            if (MainConfig.IllagerRaid.get()) {
                                if (SEHelper.getSoulAmountInt(player) >= (MainConfig.IllagerAssaultSEThreshold.get() * 2)) {
                                    int badOmen = Mth.clamp(raid.getBadOmenLevel(), 0, 5) + 1;
                                    int pillager = world.random.nextInt((int) 12 / badOmen);
                                    if (SEHelper.getSoulAmountInt(player) >= (MainConfig.IllagerAssaultSEThreshold.get() * 4)){
                                        pillager = world.random.nextInt(3);
                                    }
                                    if (pillager == 0) {
                                        if (raider.getType() == EntityType.PILLAGER) {
                                            HuntingIllagerEntity illager = ModEntityType.CONQUILLAGER.get().create(world);
                                            if (illager != null) {
                                                if (world.random.nextInt(4) == 0) {
                                                    illager.setRider(true);
                                                }
                                                illager.moveTo(raider.getX(), raider.getY(), raider.getZ());
                                                illager.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(raider.blockPosition()), MobSpawnType.EVENT, null, null);
                                                serverLevel.addFreshEntity(illager);
                                            }
                                        }
                                        if (raider.getType() == EntityType.EVOKER) {
                                            Envioker illager = ModEntityType.ENVIOKER.get().create(world);
                                            if (illager != null) {
                                                illager.moveTo(raider.getX(), raider.getY(), raider.getZ());
                                                illager.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(raider.blockPosition()), MobSpawnType.EVENT, null, null);
                                                serverLevel.addFreshEntity(illager);
                                            }
                                        }
                                    }
                                    int vindicator = world.random.nextInt((int) 12 / badOmen);
                                    if (vindicator == 0) {
                                        if (raid.getGroupsSpawned() > 3 || SEHelper.getSoulAmountInt(player) >= (MainConfig.IllagerAssaultSEThreshold.get() * 4)) {
                                            if (raider.getType() == EntityType.VINDICATOR) {
                                                HuntingIllagerEntity illager = ModEntityType.INQUILLAGER.get().create(world);
                                                if (illager != null) {
                                                    illager.moveTo(raider.getX(), raider.getY(), raider.getZ());
                                                    if (world.random.nextInt(4) == 0) {
                                                        illager.setRider(true);
                                                    }
                                                    illager.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(raider.blockPosition()), MobSpawnType.EVENT, null, null);
                                                    serverLevel.addFreshEntity(illager);
                                                }
                                            }
                                        }
                                        if (raider.getType() == EntityType.RAVAGER) {
                                            Envioker envioker = ModEntityType.ENVIOKER.get().create(world);
                                            if (envioker != null) {
                                                if (world.random.nextInt(4) == 0) {
                                                    envioker.setRider(true);
                                                }
                                                envioker.moveTo(raider.getX(), raider.getY(), raider.getZ());
                                                envioker.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(raider.blockPosition()), MobSpawnType.EVENT, null, null);
                                                serverLevel.addFreshEntity(envioker);
                                            }
                                        }
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
    private static final Map<ServerLevel, IllagerSpawner> ILLAGER_SPAWN_MAP = new HashMap<>();

    @SubscribeEvent
    public static void worldLoad(LevelEvent.Load event) {
        if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverWorld) {
            EFFECTS_EVENT_MAP.put(serverWorld, new EffectsEvents());
            ILLAGER_SPAWN_MAP.put(serverWorld, new IllagerSpawner());
        }
    }

    @SubscribeEvent
    public static void worldUnload(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverWorld) {
            EFFECTS_EVENT_MAP.remove(serverWorld);
            ILLAGER_SPAWN_MAP.remove(serverWorld);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.LevelTickEvent tick){
        if(!tick.level.isClientSide && tick.level instanceof ServerLevel serverWorld){
            EffectsEvents effectsEvent = EFFECTS_EVENT_MAP.get(serverWorld);
            if (effectsEvent != null){
                effectsEvent.tick(serverWorld);
            }
            IllagerSpawner illagerSpawner = ILLAGER_SPAWN_MAP.get(serverWorld);
            if (illagerSpawner != null){
                illagerSpawner.tick(serverWorld);
            }
        }

    }

    @SubscribeEvent
    public static void SpecialSpawnEvents(LivingSpawnEvent.CheckSpawn event){
        if (event.getEntity() instanceof SpellcasterIllager || event.getEntity() instanceof Witch || event.getEntity() instanceof Cultist){
            if (event.getSpawnReason() == MobSpawnType.STRUCTURE){
                event.getEntity().addTag(ConstantPaths.structureMob());
            }
        }
        if (event.getEntity() instanceof Cultist cultist){
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                if (serverLevel.getRaidAt(cultist.blockPosition()) != null) {
                    if (event.getSpawnReason() == MobSpawnType.NATURAL || event.getSpawnReason() == MobSpawnType.CHUNK_GENERATION) {
                        event.setResult(Event.Result.DENY);
                    }
                }
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
        if (world instanceof ServerLevel serverLevel){
            for (Entity entity : serverLevel.getAllEntities()){
                if (entity instanceof Owned summonedEntity){
                    if (summonedEntity.getTrueOwner() == player && summonedEntity.isAlive()){
                        if (summonedEntity instanceof ZombieServant || summonedEntity instanceof ZPiglinServant){
                            ++zombies;
                            if (zombies > SpellConfig.ZombieLimit.get()){
                                if (summonedEntity.tickCount % 20 == 0){
                                    summonedEntity.hurt(DamageSource.STARVE, summonedEntity.getMaxHealth()/4);
                                }
                            }
                        }
                        if (summonedEntity instanceof AbstractSkeletonServant){
                            ++skeletons;
                            if (skeletons > SpellConfig.SkeletonLimit.get()){
                                if (summonedEntity.tickCount % 20 == 0){
                                    summonedEntity.hurt(DamageSource.STARVE, summonedEntity.getMaxHealth()/4);
                                }
                            }
                        }
                        if (summonedEntity instanceof AbstractWraith){
                            ++wraith;
                            if (wraith > SpellConfig.WraithLimit.get()){
                                if (summonedEntity.tickCount % 20 == 0){
                                    summonedEntity.hurt(DamageSource.STARVE, summonedEntity.getMaxHealth()/4);
                                }
                            }
                        }
                        if (summonedEntity instanceof HauntedSkull){
                            ++skull;
                            if (skull > SpellConfig.SkullLimit.get()){
                                if (summonedEntity.tickCount % 20 == 0){
                                    summonedEntity.hurt(DamageSource.STARVE, summonedEntity.getMaxHealth()/2);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (SEHelper.getSoulAmountInt(player) > MainConfig.IllagerAssaultSEThreshold.get() * 2){
            for (Raider pillagerEntity : player.level.getEntitiesOfClass(Raider.class, player.getBoundingBox().inflate(32))){
                if (pillagerEntity.getTarget() == player) {
                    if (!pillagerEntity.isAggressive()) {
                        pillagerEntity.setAggressive(true);
                    }
                }
            }
        }

        if (!ItemHelper.findArmor(player, ModItems.DARK_HELMET.get()).isEmpty()){
            if (player.getEffect(MobEffects.DARKNESS) != null){
                player.removeEffect(MobEffects.DARKNESS);
            }
            if (player.getEffect(MobEffects.BLINDNESS) != null){
                player.removeEffect(MobEffects.BLINDNESS);
            }
        }

        if (event.phase == TickEvent.Phase.END) {
            Inventory inventory = player.getInventory();

            List<NonNullList<ItemStack>> compartments = ImmutableList.of(inventory.items, inventory.armor, inventory.offhand);

            for (NonNullList<ItemStack> nonnulllist : compartments) {
                for (int i = 0; i < nonnulllist.size(); ++i) {
                    if (!nonnulllist.get(i).isEmpty()) {
                        ItemStack itemStack = nonnulllist.get(i);
                        if (itemStack.getItem() instanceof ISoulRepair soulRepair) {
                            soulRepair.repairTick(nonnulllist.get(i), player, inventory.selected == i);
                        }
                    }
                }
            }

            if (ItemHelper.armorSet(player, ModArmorMaterials.DARK)){
                if (player.getFoodData().needsFood()){
                    if (player.tickCount % 40 == 0){
                        player.heal(1.0F);
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
        if (CuriosFinder.hasCurio(player, ModItems.WIND_ROBE.get()) && !player.isSpectator()){
            if (SEHelper.getSoulsAmount(player, MainConfig.WindRobeSouls.get()) || player.isCreative()) {
                Vec3 vector3d = player.getDeltaMovement();
                if (player.hasEffect(MobEffects.SLOW_FALLING)){
                    player.removeEffect(MobEffects.SLOW_FALLING);
                }
                if (!player.isOnGround() && vector3d.y < 0.0D
                        && !player.isNoGravity()
                        && !player.getAbilities().flying
                        && !player.onClimbable()
                        && !player.isInFluidType()
                        && !player.isInWater()
                        && !player.isInLava()
                        && player.fallDistance >= 2.0F) {
                    if (player.tickCount % 20 == 0 && !player.isCreative() && player.fallDistance > 3.0F) {
                        SEHelper.decreaseSouls(player, MainConfig.WindRobeSouls.get());
                    }
                    float f = 1.0F;
                    float f5 = (float) Math.PI * f * f;
                    for (int k1 = 0; (float) k1 < f5; ++k1) {
                        float f6 = world.random.nextFloat() * ((float) Math.PI * 2F);
                        float f7 = Mth.sqrt(world.random.nextFloat()) * f;
                        float f8 = Mth.cos(f6) * f7;
                        float f9 = Mth.sin(f6) * f7;
                        world.addParticle(ParticleTypes.CLOUD, player.getX() + (double) f8, player.getY(), player.getZ() + (double) f9, 0, 0, 0);
                    }
                    if (!player.isCrouching() && !player.isShiftKeyDown()) {
                        player.setDeltaMovement(vector3d.multiply(1.0D, 0.875D, 1.0D));
                    } else {
                        player.setDeltaMovement(vector3d.multiply(1.0D, 0.99D, 1.0D));
                    }
                }
            }
        }
        if (MobUtil.starAmuletActive(player)){
            player.getAbilities().flying &= player.isCreative();
        }
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
            if (livingEntity.hasEffect(ModEffects.CLIMBING.get())){
                MobUtil.ClimbAnyWall(livingEntity);
                MobUtil.WebMovement(livingEntity);
            }
            if (CuriosFinder.hasWitchSet(livingEntity)){
                if (livingEntity.getRandom().nextFloat() < 7.5E-4F){
                    for(int i = 0; i < livingEntity.getRandom().nextInt(35) + 10; ++i) {
                        livingEntity.level.addParticle(ParticleTypes.WITCH, livingEntity.getX() + livingEntity.getRandom().nextGaussian() * (double)0.13F, livingEntity.getBoundingBox().maxY + 0.5D + livingEntity.getRandom().nextGaussian() * (double)0.13F, livingEntity.getZ() + livingEntity.getRandom().nextGaussian() * (double)0.13F, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
            if (CuriosFinder.hasCurio(livingEntity, ModItems.WARLOCK_ROBE.get())){
                if (livingEntity.getRandom().nextFloat() < 7.5E-4F){
                    for(int i = 0; i < livingEntity.getRandom().nextInt(35) + 10; ++i) {
                        livingEntity.level.addParticle(ModParticleTypes.WARLOCK.get(), livingEntity.getX() + livingEntity.getRandom().nextGaussian() * (double)0.13F, livingEntity.getBoundingBox().maxY + 0.5D + livingEntity.getRandom().nextGaussian() * (double)0.13F, livingEntity.getZ() + livingEntity.getRandom().nextGaussian() * (double)0.13F, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
            if (livingEntity instanceof Raider witch) {
                if (livingEntity instanceof Warlock || livingEntity instanceof Witch) {
                    if (WitchBarterHelper.getTimer(witch) > 0) {
                        WitchBarterHelper.decreaseTimer(witch);
                    }
                }
            }
            if (livingEntity instanceof Piglin){
                if (!livingEntity.level.isClientSide) {
                    Piglin piglinEntity = (Piglin) livingEntity;
                    Brain<?> brain = piglinEntity.getBrain();
                    Optional<LivingEntity> avoidUndead = Optional.empty();
                    NearestVisibleLivingEntities nearestvisiblelivingentities = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
                    for (LivingEntity livingentity : nearestvisiblelivingentities.findAll((p_186157_) -> {
                        return true;
                    })) {
                        if (livingentity instanceof ZPiglinServant) {
                            avoidUndead = Optional.of(livingentity);
                        }
                    }
                    if (avoidUndead.isPresent()) {
                        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, avoidUndead);
                    }
                }
            }
            if (MainConfig.VillagerConvertWarlock.get()) {
                if (livingEntity instanceof Villager villager) {
                    if (villager.level instanceof ServerLevel serverLevel) {
                        if (BlockFinder.getVerticalBlock(serverLevel, villager.blockPosition(), Blocks.CRYING_OBSIDIAN.defaultBlockState(), 16, true)) {
                            if (villager.getRandom().nextFloat() < 7.5E-4F && serverLevel.getDifficulty() != Difficulty.PEACEFUL) {
                                if (net.minecraftforge.event.ForgeEventFactory.canLivingConvert(villager, ModEntityType.WARLOCK.get(), (timer) -> {
                                })) {
                                    serverLevel.explode(villager, villager.getX(), villager.getY(), villager.getZ(), 0.1F, Explosion.BlockInteraction.NONE);
                                    Warlock witch = ModEntityType.WARLOCK.get().create(serverLevel);
                                    if (witch != null) {
                                        witch.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
                                        witch.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(witch.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null, (CompoundTag) null);
                                        witch.setNoAi(villager.isNoAi());
                                        if (villager.hasCustomName()) {
                                            witch.setCustomName(villager.getCustomName());
                                            witch.setCustomNameVisible(villager.isCustomNameVisible());
                                        }

                                        witch.setPersistenceRequired();
                                        net.minecraftforge.event.ForgeEventFactory.onLivingConvert(villager, witch);
                                        serverLevel.addFreshEntityWithPassengers(witch);
                                        MobUtil.releaseAllPois(villager);
                                        villager.discard();
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
    public static void onBreakingBlock(BlockEvent.BreakEvent event){
        Player player = event.getPlayer();
        if (player.getMainHandItem().getItem() instanceof PhilosophersMaceItem){
            if (event.getState() == Blocks.NETHER_GOLD_ORE.defaultBlockState()){
                if (!player.level.isClientSide) {
                    if (player.level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !player.level.restoringBlockSnapshots) {
                        ItemStack itemStack = new ItemStack(Items.RAW_GOLD);
                        double d0 = (double) (player.level.random.nextFloat() * 0.5F) + 0.25D;
                        double d1 = (double) (player.level.random.nextFloat() * 0.5F) + 0.25D;
                        double d2 = (double) (player.level.random.nextFloat() * 0.5F) + 0.25D;
                        ItemEntity itementity = new ItemEntity(player.level, (double) event.getPos().getX() + d0, (double) event.getPos().getY() + d1, (double) event.getPos().getZ() + d2, itemStack);
                        itementity.setDefaultPickUpDelay();
                        player.level.addFreshEntity(itementity);
                    }
                    player.level.setBlockAndUpdate(event.getPos(), Blocks.AIR.defaultBlockState());
                    event.setCanceled(true);
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
                    if (mobAttacker instanceof Witch || mobAttacker instanceof Warlock){
                        if (CuriosFinder.hasWitchSet(target) || CuriosFinder.hasCurio(target, ModItems.WARLOCK_ROBE.get())){
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
            if (CuriosFinder.hasUndeadCrown(entity)) {
                if (undead) {
                    event.modifyVisibility(0.5);
                }
            }
            if (CuriosFinder.hasUndeadCape(entity)) {
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
        if (!event.getLevel().isClientSide) {
            if (CuriosFinder.hasWitchSet(event.getEntity()) || CuriosFinder.hasCurio(event.getEntity(), ModItems.WARLOCK_ROBE.get())) {
                if (event.getTarget() instanceof Raider witch) {
                    if (event.getTarget() instanceof Witch || event.getTarget() instanceof Warlock) {
                        if (!witch.isAggressive()) {
                            if (WitchBarterHelper.getTimer(witch) <= 0) {
                                if (witch.getMainHandItem().isEmpty() && event.getItemStack().is(ModTags.Items.WITCH_CURRENCY)) {
                                    event.setCanceled(true);
                                    event.setCancellationResult(InteractionResult.SUCCESS);
                                    if (witch instanceof Witch) {
                                        witch.playSound(SoundEvents.WITCH_CELEBRATE);
                                    } else if (witch instanceof Warlock){
                                        witch.playSound(ModSounds.WARLOCK_CELEBRATE.get());
                                    }
                                    ItemStack itemstack1;
                                    if (event.getEntity().isCreative()){
                                        itemstack1 = event.getItemStack();
                                    } else {
                                        itemstack1 = event.getItemStack().split(1);
                                    }
                                    witch.setItemSlot(EquipmentSlot.MAINHAND, itemstack1);
                                    WitchBarterHelper.setTrader(witch, event.getEntity());
                                }
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
            if (victim instanceof Player player){
                if (MobUtil.starAmuletActive(player)){
                    event.setCanceled(true);
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
        if (CuriosFinder.hasCurio(victim, ModItems.WARLOCK_ROBE.get())){
            if (event.getSource().isExplosion()){
                event.setAmount(event.getAmount() * 0.15F);
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
                            victim.addEffect(new MobEffectInstance(ModEffects.SAPPED.get(), 60));
                            victim.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 1.0F);
                        } else {
                            if (victim.level.random.nextFloat() <= 0.1F) {
                                EffectsUtil.amplifyEffect(victim, ModEffects.SAPPED.get(), 60);
                                victim.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 1.0F);
                            } else {
                                EffectsUtil.resetDuration(victim, ModEffects.SAPPED.get(), 60);
                            }
                        }
                    }
                    if (weapon.getTier() == ModTiers.DARK){
                        victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void DamageEvent(LivingDamageEvent event){
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            if (MobUtil.starAmuletActive(player)){
                if (event.getSource().getDirectEntity() instanceof AbstractArrow){
                    event.setCanceled(true);
                }
            }
        }
        if (entity.hasEffect(ModEffects.SAPPED.get())){
            MobEffectInstance effectInstance = entity.getEffect(ModEffects.SAPPED.get());
            float original = event.getAmount();
            if (effectInstance != null) {
                int i = effectInstance.getAmplifier() + 1;
                original += event.getAmount() * 0.2F * i;
                event.setAmount(original);
            }
        }
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
    public static void onLivingFall(LivingFallEvent event){
        if (CuriosFinder.hasCurio(event.getEntity(), ModItems.WIND_ROBE.get())){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void SpecialDeath(LivingDeathEvent event){
        LivingEntity killed = event.getEntity();
        Entity killer = event.getSource().getEntity();
        Level world = killed.getCommandSenderWorld();
        if (killed instanceof PathfinderMob){
            if (killed.hasEffect(ModEffects.GOLD_TOUCHED.get())){
                if (world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    int amp = Objects.requireNonNull(killed.getEffect(ModEffects.GOLD_TOUCHED.get())).getAmplifier() + 1;
                    for (int i = 0; i < (killed.level.random.nextInt(3) + 1) * amp; ++i) {
                        killed.spawnAtLocation(new ItemStack(Items.GOLD_NUGGET));
                    }
                }
            }
        }
        if (world instanceof ServerLevel serverLevel) {
            if (killed instanceof Villager villager) {
                if (villager.hasEffect(ModEffects.ILLAGUE.get())) {
                    ZombieVillager zombievillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
                    if (zombievillager != null) {
                        zombievillager.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), (CompoundTag) null);
                        zombievillager.setVillagerData(villager.getVillagerData());
                        zombievillager.setGossips(villager.getGossips().store(NbtOps.INSTANCE).getValue());
                        zombievillager.setTradeOffers(villager.getOffers().createTag());
                        zombievillager.setVillagerXp(villager.getVillagerXp());
                        net.minecraftforge.event.ForgeEventFactory.onLivingConvert(villager, zombievillager);
                        if (!zombievillager.isSilent()) {
                            serverLevel.levelEvent((Player) null, 1026, zombievillager.blockPosition(), 0);
                        }
                    }
                }
            }
        }
        if (killer instanceof Player player){
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
                                    if (killed instanceof Witch || (killed instanceof Cultist && killed.getType() != ModEntityType.APOSTLE.get())) {
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
                if (killed.getType() == EntityType.SPIDER){
                    if (CuriosFinder.hasCurio(player, itemStack -> itemStack.getItem() instanceof WarlockGarmentItem)){
                        if (world.random.nextFloat() <= 0.075F){
                            for (int i = 0; i < (world.random.nextInt(2) + 1); ++i) {
                                killed.spawnAtLocation(new ItemStack(ModItems.SPIDER_EGG.get()));
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
            if (living instanceof Player player){
                if (CuriosFinder.hasWitchSet(player)){
                    if (living.level.getServer() != null) {
                        LootTable loottable = living.level.getServer().getLootTables().get(ModLootTables.PLAYER_WITCH);
                        LootContext.Builder lootcontext$builder = MobUtil.createLootContext(event.getSource(), living);
                        LootContext ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
                        loottable.getRandomItems(ctx).forEach((loot) -> event.getDrops().add(ItemHelper.itemEntityDrop(living, loot)));
                    }
                }
            }
            if (living instanceof SpellcasterIllager || living instanceof Witch || living instanceof Cultist) {
                if (living.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    if (living.getTags().contains(ConstantPaths.structureMob())) {
                        float chance = 0.025F;
                        chance += (float) event.getLootingLevel() / 100;
                        if (living.level.random.nextFloat() <= chance) {
                            event.getDrops().add(ItemHelper.itemEntityDrop(living, new ItemStack(ModItems.FORBIDDEN_FRAGMENT.get())));
                        }
                    }
                }
            }
            if (MainConfig.TallSkullDrops.get()) {
                if (living.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
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
    }

    @SubscribeEvent
    public static void usingItemEvents(LivingEntityUseItemEvent.Tick event){
        if (event.getItem().getItem() instanceof DarkWand && CuriosFinder.hasCurio(event.getEntity(), ModItems.TARGETING_MONOCLE.get())){
            if (MobUtil.getSingleTarget(event.getEntity().level, event.getEntity(), 16, 3) instanceof LivingEntity living){
                event.getEntity().lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(living.getX(), living.getEyeY(), living.getZ()));
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
    public static void addWanderTrade(WandererTradesEvent event){
        List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();
        rareTrades.add(new ModTradeUtil.ItemsForEmeralds(ModItems.JADE.get(), 1, 64, 16));
    }

    @SubscribeEvent
    public static void ExplosionDetonateEvent(ExplosionEvent.Detonate event){
        if (event.getExplosion() != null) {
            event.getAffectedEntities().removeIf(entity -> (entity instanceof ItemEntity && ((ItemEntity) entity).getItem().getItem() == ModItems.UNHOLY_BLOOD.get()));
            if (event.getExplosion().getSourceMob() != null) {
                if (event.getExplosion().getSourceMob() instanceof Apostle) {
                    event.getAffectedEntities().removeIf(entity -> (entity instanceof IOwned && ((IOwned) entity).getTrueOwner() instanceof Apostle) || (entity == event.getExplosion().getSourceMob()));
                }
                if (event.getExplosion().getSourceMob() instanceof IOwned sourceMob) {
                    if (sourceMob.getTrueOwner() instanceof Apostle) {
                        event.getAffectedEntities().removeIf(entity -> (entity instanceof IOwned && ((IOwned) entity).getTrueOwner() instanceof Apostle) || entity == sourceMob.getTrueOwner());
                    }
                    if (sourceMob instanceof HauntedSkull){
                        event.getAffectedEntities().removeIf(entity ->
                                (entity instanceof IOwned && ((IOwned) entity).getTrueOwner() == sourceMob.getTrueOwner()
                                        || entity instanceof OwnableEntity && ((OwnableEntity) entity).getOwner() == sourceMob.getTrueOwner()
                                        || entity == sourceMob.getTrueOwner()));
                    }
                }
                if (event.getExplosion().getExploder() instanceof ThrowableFungus fungus){
                    event.getAffectedEntities().removeIf(entity ->
                            (entity instanceof IOwned && ((IOwned) entity).getTrueOwner() == fungus.getOwner()
                                    || entity instanceof OwnableEntity && ((OwnableEntity) entity).getOwner() == fungus.getOwner()
                                    || entity instanceof AbstractHorse && fungus.getOwner() != null &&  ((AbstractHorse) entity).getOwnerUUID() == fungus.getOwner().getUUID()
                                    || entity == fungus.getOwner()
                                    || entity instanceof ThrowableFungus));
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
        if (event.getEffectInstance().getEffect() == MobEffects.BLINDNESS){
            if (CuriosFinder.hasIllusionRobe(event.getEntity())){
                event.setResult(Event.Result.DENY);
            }
            if (event.getEntity() instanceof Player player) {
                if (!ItemHelper.findArmor(player, ModItems.DARK_HELMET.get()).isEmpty()){
                    event.setResult(Event.Result.DENY);
                }
            }
        }
        if (event.getEffectInstance().getEffect() == MobEffects.DARKNESS){
            if (event.getEntity() instanceof Player player) {
                if (!ItemHelper.findArmor(player, ModItems.DARK_HELMET.get()).isEmpty()){
                    event.setResult(Event.Result.DENY);
                }
            }
        }
        if (event.getEffectInstance().getEffect() == MobEffects.SLOW_FALLING){
            if (CuriosFinder.hasCurio(event.getEntity(), ModItems.WIND_ROBE.get())){
                event.setResult(Event.Result.DENY);
            }
        }
        if (event.getEffectInstance().getEffect() == ModEffects.ILLAGUE.get()){
            if (event.getEntity().getType().is(EntityTypeTags.RAIDERS) || event.getEntity() instanceof PatrollingMonster){
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
