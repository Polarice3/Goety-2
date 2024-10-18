package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.EnchanteableBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.ModChestBlock;
import com.Polarice3.Goety.common.capabilities.lichdom.ILichdom;
import com.Polarice3.Goety.common.capabilities.lichdom.LichProvider;
import com.Polarice3.Goety.common.capabilities.misc.IMisc;
import com.Polarice3.Goety.common.capabilities.misc.MiscProvider;
import com.Polarice3.Goety.common.capabilities.soulenergy.ISoulEnergy;
import com.Polarice3.Goety.common.capabilities.soulenergy.SEProvider;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.TargetHostileOwnedGoal;
import com.Polarice3.Goety.common.entities.ai.WitchBarterGoal;
import com.Polarice3.Goety.common.entities.ally.AllyIrk;
import com.Polarice3.Goety.common.entities.ally.ModRavager;
import com.Polarice3.Goety.common.entities.ally.Ravaged;
import com.Polarice3.Goety.common.entities.ally.golem.IceGolem;
import com.Polarice3.Goety.common.entities.ally.undead.GraveGolem;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.hostile.WitherNecromancer;
import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.entities.hostile.cultists.Heretic;
import com.Polarice3.Goety.common.entities.hostile.cultists.Maverick;
import com.Polarice3.Goety.common.entities.hostile.cultists.Warlock;
import com.Polarice3.Goety.common.entities.hostile.illagers.*;
import com.Polarice3.Goety.common.entities.hostile.servants.Damned;
import com.Polarice3.Goety.common.entities.hostile.servants.ObsidianMonolith;
import com.Polarice3.Goety.common.entities.neutral.BlazeServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.Fangs;
import com.Polarice3.Goety.common.entities.projectiles.ModDragonFireball;
import com.Polarice3.Goety.common.entities.util.DragonBreathCloud;
import com.Polarice3.Goety.common.entities.util.StormEntity;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.common.items.armor.ModArmorMaterials;
import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.Polarice3.Goety.common.items.curios.WarlockGarmentItem;
import com.Polarice3.Goety.common.items.curios.WitchHatItem;
import com.Polarice3.Goety.common.items.equipment.DarkScytheItem;
import com.Polarice3.Goety.common.items.equipment.DeathScytheItem;
import com.Polarice3.Goety.common.items.equipment.HammerItem;
import com.Polarice3.Goety.common.items.equipment.PhilosophersMaceItem;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.common.research.Research;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.common.world.structures.ModStructureTags;
import com.Polarice3.Goety.compat.iron.IronAttributes;
import com.Polarice3.Goety.compat.iron.IronLoaded;
import com.Polarice3.Goety.compat.patchouli.PatchouliLoaded;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.init.RaidAdditions;
import com.Polarice3.Goety.utils.*;
import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.ArrayUtils;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.*;

import static net.minecraftforge.event.entity.living.LivingChangeTargetEvent.LivingTargetType.MOB_TARGET;

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

        player.getCapability(LichProvider.CAPABILITY)
                .ifPresent(lichdom ->
                        lichdom.setLichMode(capability2.isLichMode()));

        player.getCapability(LichProvider.CAPABILITY)
                .ifPresent(lichdom ->
                        lichdom.setNightVision(capability2.nightVision()));

        ISoulEnergy capability3 = SEHelper.getCapability(original);

        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy ->
                        soulEnergy.setSEActive(capability3.getSEActive()));
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy ->
                        soulEnergy.setSoulEnergy(capability3.getSoulEnergy()));
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy ->
                        soulEnergy.setRecoil(capability3.getRecoil()));
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy ->
                        soulEnergy.setArcaBlock(capability3.getArcaBlock()));
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy ->
                        soulEnergy.setArcaBlockDimension(capability3.getArcaBlockDimension()));
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy ->
                        soulEnergy.setRestPeriod(capability3.getRestPeriod()));
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy -> {
                    for (Research research : capability3.getResearch()){
                        soulEnergy.addResearch(research);
                    }
                });
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy -> {
                    for (UUID uuid : capability3.grudgeList()){
                        soulEnergy.addGrudge(uuid);
                    }
                });
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy -> {
                    for (UUID uuid : capability3.allyList()){
                        soulEnergy.addAlly(uuid);
                    }
                });
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy -> {
                    for (EntityType<?> entityType : capability3.grudgeTypeList()){
                        soulEnergy.addGrudgeType(entityType);
                    }
                });
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy -> {
                    for (EntityType<?> entityType : capability3.allyTypeList()){
                        soulEnergy.addAllyType(entityType);
                    }
                });
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy ->
                        soulEnergy.setApostleWarned(capability3.apostleWarned()));
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy ->
                        soulEnergy.setCooldowns(capability3.cooldowns()));
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy ->
                        soulEnergy.setBottling(capability3.bottling()));;
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy ->
                        soulEnergy.setCameraUUID(null));
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy ->
                        soulEnergy.setMiningProgress(0));
        player.getCapability(SEProvider.CAPABILITY)
                .ifPresent(soulEnergy ->
                        soulEnergy.setMiningPos(null));

        IMisc capability4 = MiscCapHelper.getCapability(original);

        player.getCapability(MiscProvider.CAPABILITY)
                .ifPresent(misc ->
                        misc.setShields(capability4.shieldsLeft()));
        player.getCapability(MiscProvider.CAPABILITY)
                .ifPresent(misc ->
                        misc.setShieldTime(capability4.shieldTime()));
        player.getCapability(MiscProvider.CAPABILITY)
                .ifPresent(misc ->
                        misc.setShieldCool(capability4.shieldCool()));
        player.getCapability(MiscProvider.CAPABILITY)
                .ifPresent(misc ->
                        misc.setAmbientSoundTime(0));

    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        Level world = event.getLevel();
        if (entity instanceof LivingEntity livingEntity && !world.isClientSide()) {
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
            if (entity instanceof PathfinderMob creeper && creeper.getType().is(ModTags.EntityTypes.CREEPERS)){
                creeper.goalSelector.addGoal(3, new AvoidEntityGoal<>(creeper, Player.class, (target) -> target != null && CuriosFinder.hasCurio(target, ModItems.FELINE_AMULET.get()), 6.0F, 1.0D, 1.2D, EntitySelector.NO_SPECTATORS::test));
            }
        }
        if (MainConfig.BetterDragonFireball.get()) {
            if (entity instanceof DragonFireball original) {
                ModDragonFireball dragonFireball;
                if (original.getOwner() instanceof LivingEntity livingEntity) {
                    dragonFireball = new ModDragonFireball(entity.level, livingEntity, original.xPower, original.yPower, original.zPower);
                } else {
                    dragonFireball = new ModDragonFireball(ModEntityType.MOD_DRAGON_FIREBALL.get(), entity.level);
                }
                dragonFireball.moveTo(original.position());
                if (entity.level.addFreshEntity(dragonFireball)) {
                    original.discard();
                    event.setCanceled(true);
                }
            }
            if (entity instanceof AreaEffectCloud cloud){
                if (cloud.getOwner() instanceof EnderDragon){
                    DragonBreathCloud breathCloud = new DragonBreathCloud(entity.level, cloud.getX(), cloud.getY(), cloud.getZ());
                    breathCloud.setOwner(cloud.getOwner());
                    breathCloud.setRadius(cloud.getRadius());
                    breathCloud.setRadiusOnUse(cloud.getRadiusOnUse());
                    breathCloud.setRadiusPerTick(cloud.getRadiusPerTick());
                    breathCloud.setDuration(cloud.getDuration());
                    breathCloud.setDurationOnUse(cloud.getDurationOnUse());
                    breathCloud.setWaitTime(cloud.getWaitTime());
                    if (entity.level.addFreshEntity(breathCloud)) {
                        cloud.discard();
                        event.setCanceled(true);
                    }
                }
            }
        }
        if (entity instanceof StormEntity){
            if (!entity.level.isClientSide){
                ServerLevel serverWorld = (ServerLevel) entity.level;
                serverWorld.setWeatherParameters(0, 6000, true, true);
            }
        }
        if (entity instanceof Raider raider){
            if (world instanceof ServerLevel) {
                if (raider.hasActiveRaid()) {
                    Raid raid = raider.getCurrentRaid();
                    if (raid != null && raid.isActive() && !raid.isBetweenWaves() && !raid.isOver() && !raid.isStopped()) {
                        Player player = EntityFinder.getNearbyPlayer(world, raid.getCenter());
                        if (player != null) {
                            if (MobsConfig.IllagerRaid.get()) {
                                if (SEHelper.getSoulAmountInt(player) < (MobsConfig.IllagerAssaultSEThreshold.get() * 2)) {
                                    if (raider instanceof HuntingIllagerEntity){
                                        raid.removeFromRaid(raider, true);
                                        event.setCanceled(true);
                                    }
                                }
                            } else {
                                if (raider instanceof HuntingIllagerEntity){
                                    raid.removeFromRaid(raider, true);
                                    event.setCanceled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerEntersWorld(PlayerEvent.PlayerLoggedInEvent event){
        CompoundTag playerData = event.getEntity().getPersistentData();
        CompoundTag data;

        if (!playerData.contains(Player.PERSISTED_NBT_TAG)) {
            data = new CompoundTag();
        } else {
            data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
        }
        if (!event.getEntity().level.isClientSide) {
            if (data.getBoolean(ConstantPaths.readScroll())){
                SEHelper.addResearch(event.getEntity(), ResearchList.FORBIDDEN);
            }
            if (MainConfig.StarterTotem.get()) {
                if (!data.getBoolean("goety:gotTotem")) {
                    event.getEntity().addItem(new ItemStack(ModItems.TOTEM_OF_ROOTS.get()));
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
                if (MainConfig.StarterWitchBook.get()){
                    if (!data.getBoolean("goety:witchBook")) {
                        ItemStack book = PatchouliAPI.get().getBookStack(Goety.location("witches_brew"));
                        event.getEntity().addItem(book);
                        data.putBoolean("goety:witchBook", true);
                        playerData.put(Player.PERSISTED_NBT_TAG, data);
                    }
                }
            }
        }

    }

    private static final Map<ServerLevel, IllagerSpawner> ILLAGER_SPAWN_MAP = new HashMap<>();
    private static final Map<ServerLevel, WightSpawner> WIGHT_SPAWN_MAP = new HashMap<>();

    @SubscribeEvent
    public static void worldLoad(LevelEvent.Load event) {
        RaidAdditions.addRaiders();
        if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverWorld) {
            ILLAGER_SPAWN_MAP.put(serverWorld, new IllagerSpawner());
            WIGHT_SPAWN_MAP.put(serverWorld, new WightSpawner());
        }
    }

    @SubscribeEvent
    public static void worldUnload(LevelEvent.Unload event) {
        Raid.RaiderType[] members = Raid.RaiderType.values();
        for (Raid.RaiderType member : members) {
            if (RaidAdditions.NEW_RAID_MEMBERS.contains(member)) {
                ArrayUtils.remove(members, member.ordinal());
            }
        }
        if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverWorld) {
            ILLAGER_SPAWN_MAP.remove(serverWorld);
            WIGHT_SPAWN_MAP.remove(serverWorld);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.LevelTickEvent tick){
        if(!tick.level.isClientSide && tick.level instanceof ServerLevel serverWorld){
            IllagerSpawner illagerSpawner = ILLAGER_SPAWN_MAP.get(serverWorld);
            if (illagerSpawner != null){
                illagerSpawner.tick(serverWorld);
            }
            WightSpawner wightSpawner = WIGHT_SPAWN_MAP.get(serverWorld);
            if (wightSpawner != null){
                wightSpawner.tick(serverWorld);
            }
        }

    }

    @SubscribeEvent
    public static void CheckSpawnEvents(MobSpawnEvent.FinalizeSpawn event){
        if (event.getEntity() instanceof SpellcasterIllager || event.getEntity() instanceof Witch || event.getEntity() instanceof Cultist){
            if (event.getSpawnType() == MobSpawnType.STRUCTURE){
                event.getEntity().addTag(ConstantPaths.structureMob());
            }
        }
        if (event.getSpawnType() == MobSpawnType.STRUCTURE) {
            if (event.getEntity().getTags().contains(ConstantPaths.giveAI())) {
                if (event.getEntity().isNoAi()) {
                    event.getEntity().setNoAi(false);
                    event.getEntity().removeTag(ConstantPaths.giveAI());
                }
            }
        }
        if (event.getEntity() instanceof Cultist cultist){
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                if (serverLevel.getRaidAt(cultist.blockPosition()) != null) {
                    if (event.getSpawnType() == MobSpawnType.NATURAL || event.getSpawnType() == MobSpawnType.CHUNK_GENERATION) {
                        event.setSpawnCancelled(true);
                    }
                }
            }
        }
        Mob mob = event.getEntity();
        if (IronLoaded.IRON_SPELLBOOKS.isLoaded()) {
            if (!IronAttributes.resistances(mob).isEmpty()) {
                for (AttributeInstance attributeInstance : IronAttributes.resistances(mob)) {
                    if (attributeInstance != null){
                        if (mob instanceof Inquillager) {
                            attributeInstance.setBaseValue(1.75D);
                        } else {
                            if (attributeInstance.getAttribute() == IronAttributes.EVOCATION_MAGIC_RESIST) {
                                if (mob instanceof Envioker || mob instanceof Minister || mob instanceof Vizier) {
                                    attributeInstance.setBaseValue(1.25D);
                                }
                            }
                            if (attributeInstance.getAttribute() == IronAttributes.NATURE_MAGIC_RESIST) {
                                if (mob instanceof Conquillager) {
                                    attributeInstance.setBaseValue(1.25D);
                                }
                                if (mob instanceof Apostle) {
                                    attributeInstance.setBaseValue(1.5D);
                                }
                                if (mob.getMobType() == ModMobType.NATURAL){
                                    attributeInstance.setBaseValue(1.5D);
                                }
                            }
                            if (attributeInstance.getAttribute() == IronAttributes.HOLY_MAGIC_RESIST) {
                                if (mob instanceof Conquillager || mob instanceof Preacher || mob instanceof Minister || mob instanceof Vizier) {
                                    attributeInstance.setBaseValue(1.25D);
                                }
                                if (mob instanceof Apostle) {
                                    attributeInstance.setBaseValue(0.25D);
                                }
                            }
                            if (attributeInstance.getAttribute() == IronAttributes.ICE_MAGIC_RESIST) {
                                if (mob instanceof Cryologer) {
                                    attributeInstance.setBaseValue(1.5D);
                                }
                                if (mob instanceof IceGolem){
                                    attributeInstance.setBaseValue(2.0D);
                                }
                                if (mob instanceof BlazeServant){
                                    attributeInstance.setBaseValue(0.5D);
                                }
                            }
                            if (attributeInstance.getAttribute() == IronAttributes.LIGHTNING_MAGIC_RESIST) {
                                if (mob instanceof StormCaster) {
                                    attributeInstance.setBaseValue(1.5D);
                                }
                            }
                            if (attributeInstance.getAttribute() == IronAttributes.FIRE_MAGIC_RESIST) {
                                if (mob instanceof Apostle) {
                                    attributeInstance.setBaseValue(2.0D);
                                }
                                if (mob instanceof IceGolem){
                                    attributeInstance.setBaseValue(0.33D);
                                }
                            }
                            if (attributeInstance.getAttribute() == IronAttributes.BLOOD_MAGIC_RESIST) {
                                if (mob instanceof Apostle) {
                                    attributeInstance.setBaseValue(1.75D);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void PlayerTick(TickEvent.PlayerTickEvent event){
        Player player = event.player;
        Level world = player.level;
        if (world instanceof ServerLevel serverLevel){
            if (player.tickCount % 20 == 0) {
                if (player instanceof ServerPlayer serverPlayer){
                    if (serverPlayer.getServer() != null) {
                        Advancement advancement3 = serverPlayer.getServer().getAdvancements().getAdvancement(Goety.location("goety/read_warred_and_haunting_scroll"));
                        if (advancement3 != null) {
                            AdvancementProgress advancementProgress3 = serverPlayer.getAdvancements().getOrStartProgress(advancement3);
                            if (!advancementProgress3.isDone()){
                                Advancement advancement1 = serverPlayer.getServer().getAdvancements().getAdvancement(Goety.location("goety/read_warred_scroll"));
                                Advancement advancement2 = serverPlayer.getServer().getAdvancements().getAdvancement(Goety.location("goety/read_haunting_scroll"));
                                if (advancement1 != null && advancement2 != null) {
                                    AdvancementProgress advancementProgress1 = serverPlayer.getAdvancements().getOrStartProgress(advancement1);
                                    AdvancementProgress advancementProgress2 = serverPlayer.getAdvancements().getOrStartProgress(advancement2);
                                    if (advancementProgress1.isDone() && advancementProgress2.isDone()){
                                        for(String s : advancementProgress3.getRemainingCriteria()) {
                                            serverPlayer.getAdvancements().award(advancement3, s);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (MainConfig.ShriekObeliskRaid.get()) {
                Raid raid = serverLevel.getRaidAt(player.blockPosition());
                if (raid != null) {
                    int cost = MainConfig.ShriekObeliskCost.get() * raid.getBadOmenLevel();
                    if (BlockFinder.findIllagerWard(serverLevel, player, cost)) {
                        raid.stop();
                    }
                }
            }
        }
        if (event.phase == TickEvent.Phase.END) {
            if (ItemHelper.findHelmet(player, ModItems.DARK_HELMET.get())){
                if (ItemConfig.DarkHelmetDarkness.get()) {
                    if (player.getEffect(MobEffects.DARKNESS) != null) {
                        player.removeEffect(MobEffects.DARKNESS);
                    }
                }
                if (ItemConfig.DarkHelmetBlindness.get()) {
                    if (player.getEffect(MobEffects.BLINDNESS) != null) {
                        player.removeEffect(MobEffects.BLINDNESS);
                    }
                }
            }

            Inventory inventory = player.getInventory();

            List<NonNullList<ItemStack>> compartments = ImmutableList.of(inventory.items, inventory.armor, inventory.offhand);

            for (NonNullList<ItemStack> nonnulllist : compartments) {
                for (int i = 0; i < nonnulllist.size(); ++i) {
                    if (!nonnulllist.get(i).isEmpty()) {
                        ItemStack itemStack = nonnulllist.get(i);
                        if (itemStack.getItem() instanceof ISoulRepair soulRepair) {
                            soulRepair.repairTick(nonnulllist.get(i), player, inventory.selected == i);
                        } else if (itemStack.getItem() instanceof TieredItem item && item.getTier() == ModTiers.DARK){
                            ItemHelper.repairTick(itemStack, player, inventory.selected == i);
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
        boolean flag = CuriosFinder.hasCurio(player, ModItems.GRAVE_GLOVE.get()) && (scythe || player.getMainHandItem().is(ModTags.Items.GRAVE_GLOVE_BOOST));
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

        boolean hammer = player.getMainHandItem().getItem() instanceof HammerItem;

        float increaseAttackSpeed1 = 0.25F;
        AttributeModifier attributemodifier1 = new AttributeModifier(UUID.fromString("3f0d53a8-f075-4d27-a0b7-a4d923542d4f"), "Two Handed Hammer", increaseAttackSpeed1, AttributeModifier.Operation.MULTIPLY_TOTAL);
        boolean flag1 = hammer && player.getOffhandItem().isEmpty();
        if (attackSpeed != null){
            if (flag1){
                if (!attackSpeed.hasModifier(attributemodifier1)){
                    attackSpeed.addPermanentModifier(attributemodifier1);
                }
            } else {
                if (attackSpeed.hasModifier(attributemodifier1)){
                    attackSpeed.removeModifier(attributemodifier1);
                }
            }
        }

        float increaseAttackSpeed2 = 0.5F;
        AttributeModifier attributemodifier2 = new AttributeModifier(UUID.fromString("39c01496-8161-4fde-ac2c-0bea379ceb37"), "Hammer Proficiency", increaseAttackSpeed2, AttributeModifier.Operation.MULTIPLY_TOTAL);
        boolean flag2 = CuriosFinder.hasCurio(player, ModItems.THRASH_GLOVE.get()) && (hammer || player.getMainHandItem().is(ModTags.Items.THRASH_GLOVE_BOOST));
        if (attackSpeed != null){
            if (flag2){
                if (!attackSpeed.hasModifier(attributemodifier2)){
                    attackSpeed.addPermanentModifier(attributemodifier2);
                }
            } else {
                if (attackSpeed.hasModifier(attributemodifier2)){
                    attackSpeed.removeModifier(attributemodifier2);
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
        if (livingEntity != null && livingEntity.isAlive()){
            if (MiscCapHelper.getShields(livingEntity) > 0) {
                if (MiscCapHelper.getShieldTime(livingEntity) > 0) {
                    MiscCapHelper.decreaseShieldTime(livingEntity);
                } else {
                    MiscCapHelper.setShields(livingEntity, 0);
                    if (!livingEntity.level.isClientSide) {
                        if (livingEntity instanceof Player player) {
                            ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(ModSounds.WALL_DISAPPEAR.get(), 1.0F, 2.0F));
                        } else {
                            livingEntity.playSound(ModSounds.WALL_DISAPPEAR.get(), 1.0F, 2.0F);
                        }
                    }
                }
            } else {
                if (MiscCapHelper.getShieldTime(livingEntity) > 0) {
                    MiscCapHelper.setShieldTime(livingEntity, 0);
                }
            }
            if (MiscCapHelper.getShieldCool(livingEntity) > 0){
                MiscCapHelper.decreaseShieldCool(livingEntity);
            }
            if (CuriosFinder.hasWitchSet(livingEntity)){
                if (livingEntity.getRandom().nextFloat() < 7.5E-4F){
                    for(int i = 0; i < livingEntity.getRandom().nextInt(35) + 10; ++i) {
                        livingEntity.level.addParticle(ParticleTypes.WITCH, livingEntity.getX() + livingEntity.getRandom().nextGaussian() * (double)0.13F, livingEntity.getBoundingBox().maxY + 0.5D + livingEntity.getRandom().nextGaussian() * (double)0.13F, livingEntity.getZ() + livingEntity.getRandom().nextGaussian() * (double)0.13F, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
            if (livingEntity instanceof Mob mob){
                double followRange = 32.0D;
                if (mob.getAttribute(Attributes.FOLLOW_RANGE) != null){
                    followRange = mob.getAttributeValue(Attributes.FOLLOW_RANGE) * 2;
                }
                if (mob.getTarget() instanceof Apostle apostle){
                    if (apostle.obsidianInvul > 5){
                        for (ObsidianMonolith obsidianMonolith : mob.level.getEntitiesOfClass(ObsidianMonolith.class, mob.getBoundingBox().inflate(followRange, 8.0D, followRange))){
                            if (obsidianMonolith.getOwner() == apostle){
                                mob.setTarget(obsidianMonolith);
                            }
                        }
                    }
                }
                if (mob.getTarget() instanceof ObsidianMonolith monolith){
                    if (monolith.empowered > 5){
                        for (Heretic heretic : mob.level.getEntitiesOfClass(Heretic.class, mob.getBoundingBox().inflate(followRange, 8.0D, followRange))){
                            if (heretic.getMonolith() == monolith){
                                mob.setTarget(heretic);
                            }
                        }
                    }
                }
            }
            if (livingEntity instanceof Raider raider) {
                if (raider.getTarget() instanceof Player player) {
                    if (SEHelper.getSoulAmountInt(player) > MobsConfig.IllagerAssaultSEThreshold.get() * 2){
                        if (!raider.isAggressive()) {
                            raider.setAggressive(true);
                        }
                    }
                }
            }
            if (livingEntity instanceof AbstractHorse horse){
                if (horse.getOwnerUUID() != null){
                    LivingEntity living = EntityFinder.getLivingEntityByUuiD(horse.getOwnerUUID());
                    if (living instanceof Player owner) {
                        if (horse.getMobType() == MobType.UNDEAD) {
                            if (!horse.isOnFire() && !horse.isDeadOrDying()) {
                                if (MobsConfig.UndeadMinionHeal.get() && horse.getHealth() < horse.getMaxHealth()) {
                                    if (CuriosFinder.hasUndeadCape(owner)) {
                                        int SoulCost = MobsConfig.UndeadMinionHealCost.get();
                                        if (SEHelper.getSoulsAmount(owner, SoulCost)){
                                            if (horse.tickCount % MathHelper.secondsToTicks(MobsConfig.UndeadMinionHealTime.get()) == 0) {
                                                horse.heal(horse.getMaxHealth() * 0.025F);
                                                Vec3 vector3d = horse.getDeltaMovement();
                                                if (!horse.level.isClientSide){
                                                    ServerLevel serverWorld = (ServerLevel) horse.level;
                                                    SEHelper.decreaseSouls(owner, SoulCost);
                                                    serverWorld.sendParticles(ParticleTypes.SCULK_SOUL, horse.getRandomX(0.5D), horse.getRandomY(), horse.getRandomZ(0.5D), 0, vector3d.x * -0.2D, 0.1D, vector3d.z * -0.2D, 0.5F);
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
            if (livingEntity instanceof Villager villager){
                if (!villager.level.isClientSide) {
                    Brain<?> brain = villager.getBrain();
                    Optional<LivingEntity> avoidIllager = Optional.empty();
                    NearestVisibleLivingEntities nearestvisiblelivingentities = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
                    for (LivingEntity livingentity : nearestvisiblelivingentities.findAll((p_186157_) -> true)) {
                        if (livingentity instanceof HuntingIllagerEntity) {
                            avoidIllager = Optional.of(livingentity);
                        }
                    }
                    if (avoidIllager.isPresent()) {
                        brain.setMemory(MemoryModuleType.NEAREST_HOSTILE, avoidIllager);
                    }
                    Player player = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).orElse(null);
                    if (player != null) {
                        if (MobsConfig.VillagerHate.get()) {
                            if (CuriosFinder.hasCurio(player, item -> item.is(ModTags.Items.ROBES))) {
                                if (villager.getPlayerReputation(player) > -25 && villager.getPlayerReputation(player) < 25) {
                                    villager.getGossips().add(player.getUUID(), GossipType.MINOR_NEGATIVE, 25);
                                }
                            }
                        }
                        if (MobsConfig.VillagerHateRavager.get()) {
                            for (Owned owned : player.level.getEntitiesOfClass(Owned.class, player.getBoundingBox().inflate(16.0D))) {
                                if (owned instanceof Ravaged || owned instanceof ModRavager) {
                                    if (owned.getTrueOwner() == player || owned.getMasterOwner() == player) {
                                        if (villager.getPlayerReputation(player) > -200) {
                                            villager.getGossips().add(player.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (villager.level instanceof ServerLevel serverLevel) {
                        if (MobsConfig.VillagerConvertWarlock.get()) {
                            if (BlockFinder.getVerticalBlock(serverLevel, villager.blockPosition(), Blocks.CRYING_OBSIDIAN.defaultBlockState(), 16, true)) {
                                if (villager.getRandom().nextFloat() < 7.5E-4F && serverLevel.getDifficulty() != Difficulty.PEACEFUL) {
                                    if (ForgeEventFactory.canLivingConvert(villager, ModEntityType.WARLOCK.get(), (timer) -> {
                                    })) {
                                        serverLevel.explode(villager, villager.getX(), villager.getY(), villager.getZ(), 0.1F, Level.ExplosionInteraction.NONE);
                                        Warlock warlock = ModEntityType.WARLOCK.get().create(serverLevel);
                                        if (warlock != null) {
                                            warlock.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
                                            warlock.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(warlock.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null, (CompoundTag) null);
                                            warlock.setNoAi(villager.isNoAi());
                                            if (villager.hasCustomName()) {
                                                warlock.setCustomName(villager.getCustomName());
                                                warlock.setCustomNameVisible(villager.isCustomNameVisible());
                                            }

                                            warlock.setPersistenceRequired();
                                            ForgeEventFactory.onLivingConvert(villager, warlock);
                                            serverLevel.addFreshEntityWithPassengers(warlock);
                                            MobUtil.releaseAllPois(villager);
                                            villager.discard();
                                        }
                                    }
                                }
                            }
                        }
                        if (MobsConfig.VillagerConvertHeretic.get()) {
                            if (BlockFinder.findNetherPortal(serverLevel, villager.blockPosition(), 8).isPresent()){
                                if (villager.getRandom().nextFloat() < 7.5E-4F && serverLevel.getDifficulty() != Difficulty.PEACEFUL) {
                                    if (ForgeEventFactory.canLivingConvert(villager, ModEntityType.HERETIC.get(), (timer) -> {
                                    })) {
                                        serverLevel.explode(villager, villager.getX(), villager.getY(), villager.getZ(), 0.1F, Level.ExplosionInteraction.NONE);
                                        Heretic heretic = ModEntityType.HERETIC.get().create(serverLevel);
                                        if (heretic != null) {
                                            heretic.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
                                            heretic.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(heretic.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null, (CompoundTag) null);
                                            heretic.setNoAi(villager.isNoAi());
                                            if (villager.hasCustomName()) {
                                                heretic.setCustomName(villager.getCustomName());
                                                heretic.setCustomNameVisible(villager.isCustomNameVisible());
                                            }

                                            heretic.setPersistenceRequired();
                                            ForgeEventFactory.onLivingConvert(villager, heretic);
                                            serverLevel.addFreshEntityWithPassengers(heretic);
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
            AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString("17cb060f-0465-412e-abe7-a9c397b2e548"), "Increase Armor", 4.0D, AttributeModifier.Operation.ADDITION);
            AttributeInstance armor = livingEntity.getAttribute(Attributes.ARMOR);
            AttributeModifier attributemodifier1 = new AttributeModifier(UUID.fromString("c3c510ca-76eb-4eb5-9f69-6763b7e40be2"), "Increase Toughness", 4.0D, AttributeModifier.Operation.ADDITION);
            AttributeInstance toughness = livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (armor != null){
                if (ItemHelper.armorSet(livingEntity, ModArmorMaterials.CURSED_KNIGHT) || ItemHelper.armorSet(livingEntity, ModArmorMaterials.CURSED_PALADIN)){
                    if (!armor.hasModifier(attributemodifier)){
                        armor.addPermanentModifier(attributemodifier);
                    }
                } else {
                    if (armor.hasModifier(attributemodifier)){
                        armor.removeModifier(attributemodifier);
                    }
                }
            }
            if (toughness != null){
                if (ItemHelper.armorSet(livingEntity, ModArmorMaterials.CURSED_PALADIN)){
                    if (!toughness.hasModifier(attributemodifier1)){
                        toughness.addPermanentModifier(attributemodifier1);
                    }
                } else {
                    if (toughness.hasModifier(attributemodifier1)){
                        toughness.removeModifier(attributemodifier1);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBreakingBlock(BlockEvent.BreakEvent event){
        Player player = event.getPlayer();
        if (player.getMainHandItem().getItem() instanceof PhilosophersMaceItem){
            if (event.getState().getBlock().getDescriptionId().contains("nether_gold")){
                if (!player.level.isClientSide) {
                    Block.dropResources(Blocks.GOLD_ORE.defaultBlockState(), player.level, event.getPos(), null, player, player.getMainHandItem());
                    player.level.setBlockAndUpdate(event.getPos(), Blocks.AIR.defaultBlockState());
                    event.setCanceled(true);
                }
            }
        }
        if (player.getMainHandItem().getItem() instanceof DarkScytheItem){
            ItemStack scythe = player.getMainHandItem();
            if (event.getState().getBlock().getDescriptionId().contains("sculk") && event.getState().is(BlockTags.MINEABLE_WITH_HOE)){
                if (!player.level.isClientSide) {
                    ItemStack fakeItem = new ItemStack(Items.DIAMOND_HOE);
                    fakeItem.enchant(Enchantments.SILK_TOUCH, 1);
                    Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(scythe);
                    if (!map1.isEmpty()) {
                        for (Enchantment enchantment : EnchantmentHelper.getEnchantments(scythe).keySet()) {
                            if (enchantment != Enchantments.SILK_TOUCH){
                                fakeItem.enchant(enchantment, map1.get(enchantment));
                            }
                        }
                    }
                    if (event.getState().getBlock() instanceof EnchanteableBlock enchanteableBlock){
                        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
                        enchanteableBlock.playerDestroy(player.level, event.getPlayer(), event.getPos(), event.getState(), blockEntity, fakeItem);
                    } else {
                        Block.dropResources(event.getState(), player.level, event.getPos(), null, player, fakeItem);
                    }
                    player.level.setBlockAndUpdate(event.getPos(), Blocks.AIR.defaultBlockState());
                    ItemHelper.hurtAndBreak(player.getMainHandItem(), 1, player);
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
                    if (MobUtil.isWitchType(mobAttacker)) {
                        if (CuriosFinder.isWitchFriendly(target)) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }

                    if (CuriosFinder.hasUnholyRobe(target) || CuriosFinder.hasUnholyHat(target)) {
                        if (mobAttacker instanceof Ghast || mobAttacker instanceof Blaze || mobAttacker instanceof MagmaCube) {
                            if (event.getTargetType() == MOB_TARGET) {
                                event.setNewTarget(null);
                            } else {
                                event.setCanceled(true);
                            }
                        }
                    }

                    if (CuriosFinder.neutralFrostSet(target)) {
                        if (CuriosFinder.validFrostMob(mobAttacker)) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }

                    if (CuriosFinder.neutralWildSet(target)) {
                        if (CuriosFinder.validWildMob(mobAttacker)) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }

                    if (CuriosFinder.neutralNetherSet(target)) {
                        if (CuriosFinder.validNetherMob(mobAttacker)) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }

                    if (CuriosFinder.neutralNecroSet(target) || CuriosFinder.neutralNamelessSet(target)) {
                        boolean undead = CuriosFinder.validNecroUndead(mobAttacker);
                        if (target.level instanceof ServerLevel serverLevel) {
                            if (MobsConfig.HostileCryptUndead.get()) {
                                if (BlockFinder.findStructure(serverLevel, target.blockPosition(), ModStructureTags.NECRO_HOSTILE)
                                        && !CuriosFinder.neutralNamelessSet(target)) {
                                    undead = false;
                                }
                            }
                        }
                        if (undead || (CuriosFinder.neutralNamelessSet(target) && CuriosFinder.validNamelessUndead(mobAttacker))) {
                            if (mobAttacker.getLastHurtByMob() != target) {
                                if (event.getTargetType() == MOB_TARGET) {
                                    event.setNewTarget(null);
                                } else {
                                    event.setCanceled(true);
                                }
                            } else {
                                mobAttacker.setLastHurtByMob(target);
                            }
                        }
                    }
                }
                if ((mobAttacker.getMobType() == MobType.UNDEAD && !(mobAttacker instanceof IOwned) && mobAttacker.getMaxHealth() < 100.0F) || mobAttacker instanceof Creeper) {
                    if (event.getNewTarget() instanceof Apostle) {
                        event.setCanceled(true);
                    }
                }
                if (mobAttacker.getType().is(ModTags.EntityTypes.CREEPERS) && CuriosFinder.hasCurio(target, ModItems.FELINE_AMULET.get())){
                    if (event.getTargetType() == MOB_TARGET) {
                        event.setNewTarget(null);
                    } else {
                        event.setCanceled(true);
                    }
                }
                if (mobAttacker instanceof Phantom && CuriosFinder.hasCurio(target, ModItems.FELINE_AMULET.get())){
                    if (event.getTargetType() == MOB_TARGET) {
                        event.setNewTarget(null);
                    } else {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void VisibilityEvent(LivingEvent.LivingVisibilityEvent event){
        LivingEntity entity = event.getEntity();
        if (event.getLookingEntity() instanceof LivingEntity looker && entity instanceof Player) {
            boolean undead = CuriosFinder.validNecroUndead(looker);
            if (entity.level instanceof ServerLevel serverLevel){
                if (MobsConfig.HostileCryptUndead.get()) {
                    if (BlockFinder.findStructure(serverLevel, entity.blockPosition(), ModStructureTags.NECRO_HOSTILE)
                            && !(CuriosFinder.neutralNamelessCrown(entity) || CuriosFinder.neutralNamelessCape(entity))) {
                        undead = false;
                    }
                }
            }
            if (CuriosFinder.neutralNecroCrown(entity)) {
                if (undead) {
                    event.modifyVisibility(0.5);
                }
            } else if (CuriosFinder.neutralNamelessCrown(entity)) {
                if (CuriosFinder.validNamelessUndead(looker)) {
                    event.modifyVisibility(0.5);
                }
            }
            if (CuriosFinder.neutralNecroCape(entity)) {
                if (undead) {
                    event.modifyVisibility(0.5);
                }
            } else if (CuriosFinder.neutralNamelessCape(entity)) {
                if (CuriosFinder.validNamelessUndead(looker)) {
                    event.modifyVisibility(0.5);
                }
            }
            if (ItemConfig.FrostSetMobNeutral.get()) {
                if (CuriosFinder.validFrostMob(looker)) {
                    if (CuriosFinder.hasFrostCrown(entity)) {
                        event.modifyVisibility(0.5);
                    }
                    if (CuriosFinder.hasFrostRobes(looker)) {
                        event.modifyVisibility(0.5);
                    }
                }
            }
            if (ItemConfig.WildSetMobNeutral.get()) {
                if (CuriosFinder.validWildMob(looker)) {
                    if (CuriosFinder.hasWildCrown(entity)) {
                        event.modifyVisibility(0.5);
                    }
                    if (CuriosFinder.hasWildRobe(looker)) {
                        event.modifyVisibility(0.5);
                    }
                }
            }
            if (ItemConfig.NetherSetMobNeutral.get()) {
                if (CuriosFinder.validNetherMob(looker)) {
                    if (CuriosFinder.hasNetherCrown(entity)) {
                        event.modifyVisibility(0.5);
                    }
                    if (CuriosFinder.hasNetherRobe(looker)) {
                        event.modifyVisibility(0.5);
                    }
                }
            }
            if (CuriosFinder.hasIllusionRobe(entity)){
                if (entity.isInvisible()){
                    event.modifyVisibility(0.0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void AttackEvent(LivingAttackEvent event){
        LivingEntity victim = event.getEntity();
        Entity source = event.getSource().getEntity();
        Entity direct = event.getSource().getDirectEntity();
        if (!event.getEntity().level.isClientSide) {
            if (MiscCapHelper.getShields(victim) > 0 && !event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS)){
                if (MiscCapHelper.getShieldCool(victim) <= 0) {
                    MiscCapHelper.decreaseShields(victim);
                    MiscCapHelper.setShieldCool(victim, 10);
                    if (event.getSource().getEntity() instanceof LivingEntity livingEntity){
                        MobUtil.knockBack(livingEntity, victim, 1.0D, 0.2D, 1.0D);
                    }
                }
                event.setCanceled(true);
            }
            if (MainConfig.GoodwillNoDamage.get()) {
                Player player = null;
                if (source instanceof Player player1) {
                    player = player1;
                } else if (source instanceof OwnableEntity ownable && ownable.getOwner() instanceof Player player1) {
                    player = player1;
                }
                if (player != null) {
                    if (SEHelper.getAllyEntities(player).contains(victim) || SEHelper.getAllyEntityTypes(player).contains(victim.getType())) {
                        event.setCanceled(true);
                    }
                }
            }
            if (event.getSource().is(DamageTypeTags.IS_FIRE)){
                LivingEntity source1 = null;
                Entity direct1 = direct;
                if (source instanceof LivingEntity living1) {
                    source1 = living1;
                } else if (source instanceof OwnableEntity ownable && ownable.getOwner() != null) {
                    source1 = ownable.getOwner();
                }
                if (event.getSource() instanceof NoKnockBackDamageSource damageSource){
                    if (damageSource.getOwner() instanceof LivingEntity living1) {
                        source1 = living1;
                    } else if (damageSource.getOwner() instanceof OwnableEntity ownable && ownable.getOwner() != null) {
                        source1 = ownable.getOwner();
                    }
                    direct1 = damageSource.getDirectAttacker();
                }
                if (CuriosFinder.hasNetherRobe(source1)){
                    if (victim.isInvulnerableTo(event.getSource()) || victim.hasEffect(MobEffects.FIRE_RESISTANCE)){
                        DamageSource damageSource = ModDamageSource.magicFireBreath(direct1, source1);
                        if (CuriosFinder.hasUnholyRobe(source1)){
                            damageSource = ModDamageSource.hellfire(direct1, source1);
                        }
                        victim.hurt(damageSource, event.getAmount());
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

        if (source instanceof MagmaCube magmaCube){
            if (CuriosFinder.neutralNetherSet(victim)){
                if (magmaCube.getLastHurtByMob() != victim) {
                    event.setCanceled(true);
                }
            }
        }

        if (direct instanceof AbstractArrow arrowEntity){
            if (arrowEntity.getTags().contains(ConstantPaths.rainArrow()) || arrowEntity.getOwner() instanceof Apostle){
                if (arrowEntity.getOwner() != null) {
                    if (victim instanceof IOwned ownedEntity) {
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
            if (!(arrowEntity.getOwner() instanceof Apostle && victim.level.getDifficulty() == Difficulty.HARD)) {
                if (victim instanceof Player player) {
                    if (MobUtil.starAmuletActive(player)) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void HurtEvent(LivingHurtEvent event){
        LivingEntity victim = event.getEntity();
        if (CuriosFinder.hasCurio(victim, ModItems.GRAND_TURBAN.get())){
            if (victim instanceof Player player) {
                if (SEHelper.getSoulsAmount(player, ItemConfig.ItemsRepairAmount.get())) {
                    int irks = victim.level.getEntitiesOfClass(AllyIrk.class, victim.getBoundingBox().inflate(32)).size();
                    if ((victim.level.random.nextBoolean() || victim.getHealth() <= victim.getMaxHealth() / 2) && irks < 16) {
                        AllyIrk irk = new AllyIrk(ModEntityType.ALLY_IRK.get(), victim.level);
                        irk.setPos(victim.getX(), victim.getY(), victim.getZ());
                        irk.setLimitedLife(MobUtil.getSummonLifespan(victim.level));
                        irk.setTrueOwner(victim);
                        if (victim.level.addFreshEntity(irk)) {
                            SEHelper.decreaseSouls(player, ItemConfig.ItemsRepairAmount.get());
                        }
                    }
                }
            }
        }
        if (CuriosFinder.hasCurio(victim, ModItems.GRAND_ROBE.get())){
            if (MobUtil.isSpellCasting(victim)){
                event.setAmount(event.getAmount() / 2.0F);
            }
        }
        if (CuriosFinder.hasWitchRobe(victim)){
            if (victim instanceof Player player){
                if (!(LichdomHelper.isLich(player) && MainConfig.LichMagicResist.get())){
                    if (event.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO)){
                        float resistance = 1.0F - (ItemConfig.WitchRobeResistance.get() / 100.0F);
                        event.setAmount(event.getAmount() * resistance);
                    }
                }
            }
        }
        if (ModDamageSource.shockAttacks(event.getSource())){
            if (victim.level instanceof ServerLevel serverLevel){
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_ELECTRIC.get(), victim);
                ModNetwork.sendToALL(new SPlayWorldSoundPacket(victim.blockPosition(), ModSounds.ZAP.get(), 2.0F, 1.0F));
            }
        }
        if (ModDamageSource.isMagicFire(event.getSource())){
            float amount = event.getAmount();
            if (victim.fireImmune()) {
                amount /= 2.0F;
            }
            int k = EnchantmentHelper.getDamageProtection(victim.getArmorSlots(), victim.damageSources().inFire());
            if (k > 0) {
                amount = CombatRules.getDamageAfterMagicAbsorb(amount, (float) k);
            }
            event.setAmount(amount);
        }
        if (ModDamageSource.hellfireAttacks(event.getSource())){
            if (victim.level instanceof ServerLevel serverLevel){
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), victim);
                ModNetwork.sendToALL(new SPlayWorldSoundPacket(victim.blockPosition(), SoundEvents.PLAYER_HURT_ON_FIRE, 2.0F, 1.0F));
            }
            float amount = event.getAmount();
            if (MobsConfig.HellfireFireImmune.get()) {
                if (victim.fireImmune()) {
                    amount /= 2.0F;
                }
            }
            if (MobsConfig.HellfireFireProtection.get()) {
                int k = EnchantmentHelper.getDamageProtection(victim.getArmorSlots(), victim.damageSources().inFire());
                if (k > 0) {
                    amount = CombatRules.getDamageAfterMagicAbsorb(amount, (float) k);
                }
            }
            event.setAmount(amount);
        }
        if (victim instanceof BlazeServant){
            if (event.getSource().getDirectEntity() instanceof Snowball){
                if (event.getSource().is(DamageTypes.THROWN)) {
                    if (event.getAmount() <= 0.0F) {
                        event.setAmount(3.0F);
                    }
                }
            }
        }
        if (event.getAmount() > 0.0F) {
            if (event.getSource().getDirectEntity() instanceof LivingEntity livingAttacker) {
                if (ModDamageSource.physicalAttacks(event.getSource())) {
                    ItemHelper.setItemEffect(livingAttacker.getMainHandItem(), victim);
                    if (livingAttacker.getMainHandItem().getItem() instanceof TieredItem weapon) {
                        if (weapon instanceof DarkScytheItem) {
                            victim.playSound(ModSounds.SCYTHE_HIT_MEATY.get());
                        }
                        if (weapon instanceof DeathScytheItem) {
                            if (!victim.hasEffect(GoetyEffects.SAPPED.get())) {
                                victim.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), 100));
                                victim.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 1.0F);
                            } else {
                                if (victim.level.random.nextFloat() <= 0.2F) {
                                    EffectsUtil.amplifyEffect(victim, GoetyEffects.SAPPED.get(), 100);
                                    victim.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 1.0F);
                                } else {
                                    EffectsUtil.resetDuration(victim, GoetyEffects.SAPPED.get(), 100);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (victim instanceof Player player) {
            if (CuriosFinder.hasCurio(victim, ModItems.SPITEFUL_BELT.get())) {
                int a = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.THORNS, CuriosFinder.findCurio(victim, ModItems.SPITEFUL_BELT.get()));
                if (SEHelper.getSoulsAmount(player, ItemConfig.SpitefulBeltUseAmount.get() * (a + 1))) {
                    if (!event.getSource().is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS) && !event.getSource().is(DamageTypes.THORNS) && event.getSource().getEntity() instanceof LivingEntity livingentity && livingentity != victim) {
                        livingentity.hurt(livingentity.damageSources().thorns(victim), 2.0F + a);
                        SEHelper.decreaseSouls(player, ItemConfig.SpitefulBeltUseAmount.get() * (a + 1));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void DamageEvent(LivingDamageEvent event){
        LivingEntity target = event.getEntity();
        if (target instanceof Player player) {
            if (MobUtil.starAmuletActive(player)){
                if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow && !(arrow.getOwner() instanceof Apostle && target.level.getDifficulty() == Difficulty.HARD)){
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
        if (event.getAmount() > 0.0F){
            float damageAmount = event.getAmount();
            if (target.isInWaterOrRain()){
                if (ModDamageSource.shockAttacks(event.getSource())){
                    event.setAmount(damageAmount * 1.5F);
                }
            }
            if (ModDamageSource.freezeAttacks(event.getSource())){
                if (target.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)){
                    event.setAmount(damageAmount * 2.0F);
                } else if (target.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)){
                    event.setAmount(damageAmount * 0.5F);
                }
            }
            float totalReduce = 0;
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()){
                if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR){
                    ItemStack itemStack = target.getItemBySlot(equipmentSlot);
                    if (itemStack.getItem() instanceof ArmorItem armorItem){
                        if (armorItem.getMaterial() == ModArmorMaterials.BLACK_IRON
                                || armorItem.getMaterial() == ModArmorMaterials.DARK) {
                            float reducedDamage = getReducedDamage(event, armorItem);
                            if (reducedDamage > 0) {
                                ItemHelper.hurtAndBreak(itemStack, (int) Math.max(1, reducedDamage), target);
                            }
                            totalReduce += reducedDamage;
                        }
                    }
                }
            }
            if (totalReduce > 0) {
                damageAmount -= totalReduce;
                event.setAmount(damageAmount);
            }
        }
    }

    private static float getReducedDamage(LivingDamageEvent event, ArmorItem armorItem) {
        float reduction = 0;
        if (event.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            reduction = armorItem.getDefense() / 25.0F;
        } else if (event.getSource().is(DamageTypeTags.IS_FIRE) || event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
            reduction = armorItem.getDefense() / 10.0F;
        }
        return event.getAmount() * reduction;
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event){
        if ((event.getEntity().hasEffect(GoetyEffects.CURSED.get()) || ModDamageSource.hellfireAttacks(event.getEntity().getLastDamageSource())) && event.getAmount() > 0.0F){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void SpecialDeath(LivingDeathEvent event){
        LivingEntity killed = event.getEntity();
        Entity killer = event.getSource().getEntity();
        Level world = killed.getCommandSenderWorld();
        if (event.getSource() instanceof NoKnockBackDamageSource noKnockBackDamageSource){
            killer = noKnockBackDamageSource.getOwner();
        }
        if (killed instanceof PathfinderMob){
            if (killed.hasEffect(GoetyEffects.GOLD_TOUCHED.get())){
                if (world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    int amp = Objects.requireNonNull(killed.getEffect(GoetyEffects.GOLD_TOUCHED.get())).getAmplifier() + 1;
                    for (int i = 0; i < (killed.level.random.nextInt(3) + 1) * amp; ++i) {
                        killed.spawnAtLocation(new ItemStack(Items.GOLD_NUGGET));
                    }
                }
            }
        }
        if (world instanceof ServerLevel serverLevel) {
            if (killed instanceof Villager villager) {
                if (villager.hasEffect(GoetyEffects.ILLAGUE.get())) {
                    ZombieVillager zombievillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
                    if (zombievillager != null) {
                        zombievillager.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), (CompoundTag) null);
                        zombievillager.setVillagerData(villager.getVillagerData());
                        zombievillager.setGossips(villager.getGossips().store(NbtOps.INSTANCE));
                        zombievillager.setTradeOffers(villager.getOffers().createTag());
                        zombievillager.setVillagerXp(villager.getVillagerXp());
                        ForgeEventFactory.onLivingConvert(villager, zombievillager);
                        if (!zombievillager.isSilent()) {
                            serverLevel.levelEvent((Player) null, 1026, zombievillager.blockPosition(), 0);
                        }
                    }
                }
            }
            if (killed instanceof AbstractIllager illager){
                if (!illager.getType().getDescriptionId().contains("magispeller")
                        && !illager.getType().getDescriptionId().contains("faker")
                        && !illager.getType().getDescriptionId().contains("spiritcaller")) {
                    for (Apostle apostle : world.getEntitiesOfClass(Apostle.class, illager.getBoundingBox().inflate(32))) {
                        if (apostle.hasLineOfSight(illager)) {
                            Damned damned = new Damned(ModEntityType.DAMNED.get(), world);
                            damned.moveTo(illager.blockPosition().below(2), apostle.getYHeadRot(), apostle.getXRot());
                            damned.setTrueOwner(apostle);
                            damned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(illager.blockPosition().below()), MobSpawnType.MOB_SUMMONED, null, null);
                            if (illager.hasCustomName()){
                                damned.setCustomName(illager.getCustomName());
                            }
                            damned.setHuman(false);
                            if (apostle.getTarget() != null) {
                                damned.setTarget(apostle.getTarget());
                            }
                            damned.setLimitedLife(100);
                            ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), damned);
                            world.addFreshEntity(damned);
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
                        int enchantment = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.WANTING.get(), CuriosFinder.findRing(player));
                        if (enchantment >= 3) {
                            if (world.random.nextFloat() <= (enchantment / 9.0F)) {
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
                                if (MobsConfig.TallSkullDrops.get()) {
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
        if (killer instanceof WitherNecromancer necromancer){
            MobUtil.createWitherRose(killed, necromancer);
        }
/*        if (killer instanceof LivingEntity livingEntity){
            net.minecraft.network.chat.Component deathMessage = killed.getCombatTracker().getDeathMessage();
            livingEntity.sendSystemMessage(deathMessage);
        }*/
        if (!event.isCanceled()){
            MiscCapHelper.setFreezing(killed, 0);
            MiscCapHelper.setShields(killed, 0);
            MiscCapHelper.setShieldTime(killed, 0);
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
                                event.setLootingLevel(looting);
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
                                        event.setLootingLevel(looting);
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
                        LootTable loottable = living.level.getServer().getLootData().getLootTable(ModLootTables.PLAYER_WITCH);
                        LootParams.Builder lootcontext$builder = MobUtil.createLootContext(event.getSource(), living);
                        LootParams ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
                        loottable.getRandomItems(ctx).forEach((loot) -> event.getDrops().add(ItemHelper.itemEntityDrop(living, loot)));
                    }
                }
                if (!living.level.isClientSide) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        for (LivingEntity livingEntity : serverPlayer.level.getEntitiesOfClass(LivingEntity.class, serverPlayer.getBoundingBox().inflate(64.0D))) {
                            if (livingEntity instanceof GraveGolem graveGolem) {
                                if (graveGolem.getTrueOwner() == serverPlayer) {
                                    graveGolem.addDrops(event.getDrops());
                                    event.getDrops().clear();
                                    break;
                                }
                            }
                        }
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
            if (MobsConfig.TallSkullDrops.get()) {
                if (living.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    if (living instanceof AbstractVillager || living instanceof AbstractIllager || living instanceof Witch || living instanceof Cultist) {
                        if (living.level.getServer() != null) {
                            LootTable loottable = living.level.getServer().getLootData().getLootTable(ModLootTables.TALL_SKULL);
                            LootParams.Builder lootcontext$builder = MobUtil.createLootContext(event.getSource(), living);
                            LootParams lootparams = lootcontext$builder.create(LootContextParamSets.ENTITY);
                            loottable.getRandomItems(lootparams).forEach((loot) -> event.getDrops().add(ItemHelper.itemEntityDrop(living, loot)));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void usingItemEvents(LivingEntityUseItemEvent.Tick event){
        if (!event.getEntity().level.isClientSide) {
            if (event.getItem().getItem() instanceof IWand && CuriosFinder.hasCurio(event.getEntity(), ModItems.TARGETING_MONOCLE.get())) {
                Entity entity = MobUtil.getSingleTarget(event.getEntity().level, event.getEntity(), 16, 3);
                if (entity instanceof LivingEntity living && MobUtil.areNotFullAllies(entity, event.getEntity())) {
                    event.getEntity().lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(living.getX(), living.getEyeY(), living.getZ()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void UseItemEvent(LivingEntityUseItemEvent.Finish event){
        if (CuriosFinder.hasCurio(event.getEntity(), ModItems.CRONE_HAT.get())){
            if (event.getEntity().level.random.nextFloat() <= 0.25F){
                if (event.getItem().getItem() instanceof PotionItem){
                    event.setResultStack(event.getItem());
                }
            }
            if (event.getEntity().level.random.nextFloat() <= 0.1F){
                if (event.getItem().getItem() instanceof BrewItem){
                    event.setResultStack(event.getItem());
                }
            }
        } else if (CuriosFinder.hasCurio(event.getEntity(), itemStack -> itemStack.getItem() instanceof WitchHatItem)){
            if (event.getEntity().level.random.nextFloat() <= 0.1F){
                if (event.getItem().getItem() instanceof PotionItem){
                    event.setResultStack(event.getItem());
                }
            }
        }
        if (event.getEntity() instanceof Player player) {
            if (MainConfig.WandCoolItemUse.get()) {
                if (!(event.getItem().getItem() instanceof IWand)) {
                    Item main = event.getEntity().getMainHandItem().getItem();
                    Item off = event.getEntity().getOffhandItem().getItem();
                    if (main instanceof IWand) {
                        player.getCooldowns().addCooldown(main, 20);
                    } else if (off instanceof IWand) {
                        player.getCooldowns().addCooldown(off, 20);
                    }
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
    public static void addVillagerTrade(VillagerTradesEvent event){
        ModTradeUtil.addVillagerTrades(event, VillagerProfession.CARTOGRAPHER, 3, new ModTradeUtil.TreasureMapForEmeralds(14, ModStructureTags.CRYPT, "filled_map.goety.crypt", MapDecoration.Type.MANSION, 12, 10));
    }

    @SubscribeEvent
    public static void addWanderTrade(WandererTradesEvent event){
        List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
        List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();
        genericTrades.add(new ModTradeUtil.ItemsForEmeralds(ModItems.JADE.get(), 1, 64, 16));
        rareTrades.add(new ModTradeUtil.TreasureMapForEmeralds(8, ModStructureTags.WIND_SHRINE, "filled_map.goety.wind_shrine", MapDecoration.Type.TARGET_X, 12, 10));
        rareTrades.add(new ModTradeUtil.TreasureMapForEmeralds(8, ModStructureTags.BLIGHTED_SHACK, "filled_map.goety.blighted_shack", MapDecoration.Type.MANSION, 12, 10));
    }

    @SubscribeEvent
    public static void LightningStruckEvent(EntityStruckByLightningEvent event){
        Entity entity = event.getEntity();
        Level level = entity.level;
        if (level instanceof ServerLevel serverLevel) {
            if (entity instanceof WanderingTrader trader) {
                if (MobsConfig.TraderConvertMaverick.get()) {
                    if (serverLevel.getDifficulty() != Difficulty.PEACEFUL && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(trader, ModEntityType.MAVERICK.get(), (timer) -> {
                    })) {
                        Maverick maverick = ModEntityType.MAVERICK.get().create(serverLevel);
                        if (maverick != null) {
                            maverick.moveTo(trader.getX(), trader.getY(), trader.getZ(), trader.getYRot(), trader.getXRot());
                            maverick.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(maverick.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null, (CompoundTag) null);
                            maverick.setNoAi(trader.isNoAi());
                            if (trader.hasCustomName()) {
                                maverick.setCustomName(trader.getCustomName());
                                maverick.setCustomNameVisible(trader.isCustomNameVisible());
                            }

                            maverick.setPersistenceRequired();
                            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(trader, maverick);
                            serverLevel.addFreshEntityWithPassengers(maverick);
                            trader.discard();
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void ExplosionDetonateEvent(ExplosionEvent.Detonate event){
        if (event.getExplosion() != null) {
            event.getAffectedEntities().removeIf(entity -> (entity instanceof ItemEntity && ((ItemEntity) entity).getItem().getItem() == ModItems.UNHOLY_BLOOD.get()));
            event.getAffectedEntities().removeIf(entity -> (entity instanceof ItemEntity && ((ItemEntity) entity).getItem().getItem() == ModBlocks.NIGHT_BEACON_ITEM.get()));
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
    public static void FurnaceBurnItems(FurnaceFuelBurnTimeEvent event){
        if (!event.getItemStack().isEmpty()){
            ItemStack itemStack = event.getItemStack();
            if (itemStack.is(ModBlocks.HAUNTED_BOOKSHELF.get().asItem())
                    || itemStack.is(ModBlocks.ROTTEN_BOOKSHELF.get().asItem())
                    || itemStack.is(ModBlocks.WINDSWEPT_BOOKSHELF.get().asItem())
                    || (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ModChestBlock)
                    || itemStack.is(ModBlocks.COMPACTED_WINDSWEPT_PLANKS.get().asItem())
                    || itemStack.is(ModBlocks.THATCHED_WINDSWEPT_PLANKS.get().asItem())
                    || itemStack.is(ModBlocks.OVERGROWN_ROOTS.get().asItem())) {
                event.setBurnTime(300);
            }
            if (itemStack.is(ModBlocks.WITCH_POLE.get().asItem())){
                event.setBurnTime(200);
            }
        }
    }
}
