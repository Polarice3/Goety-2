package com.Polarice3.Goety.common.commands;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.events.IllagerSpawner;
import com.Polarice3.Goety.common.research.Research;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.utils.ConstantPaths;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Collection;

public class GoetyCommand {
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed"));
    private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));
    private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID = new SimpleCommandExceptionType(Component.translatable("commands.goety.soul.set.points.invalid"));
    private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID2 = new SimpleCommandExceptionType(Component.translatable("commands.goety.illager.rest.set.points.invalid"));
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_RESEARCHES = (p_136344_, p_136345_) -> {
        Collection<Research> collection = ResearchList.getResearchIdList().values();
        return SharedSuggestionProvider.suggestResource(collection.stream().map(Research::getLocation), p_136345_);
    };

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher, CommandBuildContext p_250122_) {
        pDispatcher.register(Commands.literal("goety")
                .requires((p_198442_0_) -> {
                    return p_198442_0_.hasPermission(2);
                })
                .then(Commands.literal("soul")
                        .then(Commands.literal("add")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer()).executes((p_198445_0_) -> {
                            return addSoulEnergy(p_198445_0_.getSource(), EntityArgument.getPlayers(p_198445_0_, "targets"), IntegerArgumentType.getInteger(p_198445_0_, "amount"));
                        }))))
                        .then(Commands.literal("set")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes((p_198439_0_) -> {
                            return setSoulEnergy(p_198439_0_.getSource(), EntityArgument.getPlayers(p_198439_0_, "targets"), IntegerArgumentType.getInteger(p_198439_0_, "amount"));
                        })))))
                .then(Commands.literal("illager")
                        .then(Commands.literal("spawn").executes((p_198352_0_) -> {
                            return spawnIllagers(p_198352_0_.getSource(), p_198352_0_.getSource().getPlayerOrException());
                        })
                                .then(Commands.argument("targets", EntityArgument.player()).executes((p_198435_0_) -> {
                                    return spawnIllagers(p_198435_0_.getSource(), EntityArgument.getPlayer(p_198435_0_, "targets"));
                                })))
                        .then(Commands.literal("rest")
                                .then(Commands.literal("get").executes((p_198352_0_) -> {
                                            return getRestPeriod(p_198352_0_.getSource(), p_198352_0_.getSource().getPlayerOrException());
                                        })
                                        .then(Commands.argument("targets", EntityArgument.player()).executes((p_198435_0_) -> {
                                            return getRestPeriod(p_198435_0_.getSource(), EntityArgument.getPlayer(p_198435_0_, "targets"));
                                        })))
                                .then(Commands.literal("add")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.argument("ticks", IntegerArgumentType.integer()).executes((p_198445_0_) -> {
                                                    return addRestPeriod(p_198445_0_.getSource(), EntityArgument.getPlayers(p_198445_0_, "targets"), IntegerArgumentType.getInteger(p_198445_0_, "ticks"));
                                                }))))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.argument("ticks", IntegerArgumentType.integer(0)).executes((p_198439_0_) -> {
                                                    return setRestPeriod(p_198439_0_.getSource(), EntityArgument.getPlayers(p_198439_0_, "targets"), IntegerArgumentType.getInteger(p_198439_0_, "ticks"));
                                                }))))))
                .then(Commands.literal("map")
                        .then(Commands.literal("summon_noai")
                                .then(Commands.argument("entity", ResourceArgument.resource(p_250122_, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((p_198738_0_) -> {
                                    return spawnNoAIEntity(p_198738_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198738_0_, "entity"), p_198738_0_.getSource().getPosition(), new CompoundTag(), false, true);
                                })
                                        .then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_198735_0_) -> {
                                            return spawnNoAIEntity(p_198735_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198735_0_, "entity"), Vec3Argument.getVec3(p_198735_0_, "pos"), new CompoundTag(), false, true);
                                        })
                                                .then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((p_198739_0_) -> {
                                                    return spawnNoAIEntity(p_198739_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198739_0_, "entity"), Vec3Argument.getVec3(p_198739_0_, "pos"), CompoundTagArgument.getCompoundTag(p_198739_0_, "nbt"), false, false);
                                                })))))
                        .then(Commands.literal("summon_noai_gen")
                                .then(Commands.argument("entity", ResourceArgument.resource(p_250122_, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((p_198738_0_) -> {
                                            return spawnNoAIEntity(p_198738_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198738_0_, "entity"), p_198738_0_.getSource().getPosition(), new CompoundTag(), true, true);
                                        })
                                        .then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_198735_0_) -> {
                                                    return spawnNoAIEntity(p_198735_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198735_0_, "entity"), Vec3Argument.getVec3(p_198735_0_, "pos"), new CompoundTag(), true, true);
                                                })
                                                .then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((p_198739_0_) -> {
                                                    return spawnNoAIEntity(p_198739_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198739_0_, "entity"), Vec3Argument.getVec3(p_198739_0_, "pos"), CompoundTagArgument.getCompoundTag(p_198739_0_, "nbt"), true, false);
                                                })))))
                        .then(Commands.literal("summon_persist")
                                .then(Commands.argument("entity", ResourceArgument.resource(p_250122_, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((p_198738_0_) -> {
                                    return spawnPersistEntity(p_198738_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198738_0_, "entity"), p_198738_0_.getSource().getPosition(), new CompoundTag(), true);
                                })
                                        .then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_198735_0_) -> {
                                            return spawnPersistEntity(p_198735_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198735_0_, "entity"), Vec3Argument.getVec3(p_198735_0_, "pos"), new CompoundTag(), true);
                                        })
                                                .then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((p_198739_0_) -> {
                                                    return spawnPersistEntity(p_198739_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198739_0_, "entity"), Vec3Argument.getVec3(p_198739_0_, "pos"), CompoundTagArgument.getCompoundTag(p_198739_0_, "nbt"), false);
                                                })))))
                        .then(Commands.literal("summon_tamed")
                                .then(Commands.argument("entity", ResourceArgument.resource(p_250122_, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((p_198738_0_) -> {
                                            return spawnTamedEntity(p_198738_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198738_0_, "entity"), p_198738_0_.getSource().getPosition(), new CompoundTag(), true);
                                        })
                                        .then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_198735_0_) -> {
                                                    return spawnTamedEntity(p_198735_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198735_0_, "entity"), Vec3Argument.getVec3(p_198735_0_, "pos"), new CompoundTag(), true);
                                                })
                                                .then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((p_198739_0_) -> {
                                                    return spawnTamedEntity(p_198739_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198739_0_, "entity"), Vec3Argument.getVec3(p_198739_0_, "pos"), CompoundTagArgument.getCompoundTag(p_198739_0_, "nbt"), false);
                                                })))))
                        .then(Commands.literal("summon_hostile")
                                .then(Commands.argument("entity", ResourceArgument.resource(p_250122_, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((p_198738_0_) -> {
                                            return spawnHostileEntity(p_198738_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198738_0_, "entity"), p_198738_0_.getSource().getPosition(), new CompoundTag(), true);
                                        })
                                        .then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_198735_0_) -> {
                                                    return spawnHostileEntity(p_198735_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198735_0_, "entity"), Vec3Argument.getVec3(p_198735_0_, "pos"), new CompoundTag(), true);
                                                })
                                                .then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((p_198739_0_) -> {
                                                    return spawnHostileEntity(p_198739_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198739_0_, "entity"), Vec3Argument.getVec3(p_198739_0_, "pos"), CompoundTagArgument.getCompoundTag(p_198739_0_, "nbt"), false);
                                                }))))))
                .then(Commands.literal("research")
                        .then(Commands.literal("get").executes((p_198352_0_) -> {
                            return getResearches(p_198352_0_.getSource(), p_198352_0_.getSource().getPlayerOrException());
                        })
                                .then(Commands.argument("targets", EntityArgument.player()).executes((p_198435_0_) -> {
                            return getResearches(p_198435_0_.getSource(), EntityArgument.getPlayer(p_198435_0_, "targets"));
                        })))
                        .then(Commands.literal("add").executes((p_198352_0_) -> {
                                    return getResearches(p_198352_0_.getSource(), p_198352_0_.getSource().getPlayerOrException());
                                })
                                .then(Commands.argument("targets", EntityArgument.player()).then(Commands.literal("only").then(Commands.argument("research", ResourceLocationArgument.id()).suggests(SUGGEST_RESEARCHES).executes((p_136363_) -> {
                                    return addResearch(p_136363_.getSource(), EntityArgument.getPlayers(p_136363_, "targets"), ResourceLocationArgument.getId(p_136363_, "research"));
                                })))
                                        .then(Commands.literal("all").executes(context -> {
                                            return addAllResearch(context.getSource(), EntityArgument.getPlayers(context, "targets"));
                                        }))))
                        .then(Commands.literal("remove").executes((p_198352_0_) -> {
                                    return getResearches(p_198352_0_.getSource(), p_198352_0_.getSource().getPlayerOrException());
                                })
                                .then(Commands.argument("targets", EntityArgument.player()).then(Commands.literal("only").then(Commands.argument("research", ResourceLocationArgument.id()).suggests(SUGGEST_RESEARCHES).executes((p_136363_) -> {
                                    return removeResearch(p_136363_.getSource(), EntityArgument.getPlayers(p_136363_, "targets"), ResourceLocationArgument.getId(p_136363_, "research"));
                                })))
                                        .then(Commands.literal("all").executes(context -> {
                                            return removeAllResearch(context.getSource(), EntityArgument.getPlayers(context, "targets"));
                                        }))))));
    }

    private static int addSoulEnergy(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets, int pAmount) {
        for(ServerPlayer serverPlayer : pTargets) {
            if (SEHelper.getSoulsContainer(serverPlayer)) {
                SEHelper.increaseSouls(serverPlayer, pAmount);
            } else {
                pSource.sendFailure(Component.translatable("commands.goety.soul.failed", pAmount, pTargets.iterator().next().getDisplayName()));
            }
        }

        if (pTargets.size() == 1) {
            pSource.sendSuccess(() -> Component.translatable("commands.goety.soul.add"+ ".success.single", pAmount, pTargets.iterator().next().getDisplayName()), true);
        } else {
            pSource.sendSuccess(() -> Component.translatable("commands.goety.soul.add" + ".success.multiple", pAmount, pTargets.size()), true);
        }

        return pTargets.size();
    }

    private static int setSoulEnergy(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets, int pAmount) throws CommandSyntaxException {
        int i = 0;

        for(ServerPlayer serverPlayer : pTargets) {
            if (SEHelper.getSoulsContainer(serverPlayer)) {
                SEHelper.setSoulsAmount(serverPlayer, pAmount);
                ++i;
            } else {
                pSource.sendFailure(Component.translatable("commands.goety.soul.failed", pAmount, pTargets.iterator().next().getDisplayName()));
            }
        }

        if (i == 0) {
            throw ERROR_SET_POINTS_INVALID.create();
        } else {
            if (pTargets.size() == 1) {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.soul.set" + ".success.single", pAmount, pTargets.iterator().next().getDisplayName()), true);
            } else {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.soul.set" + ".success.multiple", pAmount, pTargets.size()), true);
            }

            return pTargets.size();
        }
    }

    private static int spawnIllagers(CommandSourceStack pSource, ServerPlayer pPlayer) {
        int i = SEHelper.getSoulAmountInt(pPlayer);
        if (i > MainConfig.IllagerAssaultSEThreshold.get()){
            pSource.sendSuccess(() -> Component.translatable("commands.goety.illager.spawn.success", pPlayer.getDisplayName()), false);
            IllagerSpawner illagerSpawner = new IllagerSpawner();
            illagerSpawner.forceSpawn(pPlayer.serverLevel(), pPlayer);
            return 1;
        } else {
            pSource.sendFailure(Component.translatable("commands.goety.illager.spawn.failure", pPlayer.getDisplayName()));
        }
        return i;
    }

    private static int getRestPeriod(CommandSourceStack pSource, ServerPlayer pPlayer){
        int i = SEHelper.getRestPeriod(pPlayer);
        pSource.sendSuccess(() -> Component.translatable("commands.goety.illager.rest.get.success", pPlayer.getDisplayName(), StringUtil.formatTickDuration(i)), false);
        return 1;
    }

    private static int addRestPeriod(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets, int tick) {
        for(ServerPlayer serverPlayer : pTargets) {
            SEHelper.increaseRestPeriod(serverPlayer, tick);
        }

        if (pTargets.size() == 1) {
            pSource.sendSuccess(() -> Component.translatable("commands.goety.illager.rest.add.success.single", tick, pTargets.iterator().next().getDisplayName()), true);
        } else {
            pSource.sendSuccess(() -> Component.translatable("commands.goety.illager.rest.add.success.multiple", tick, pTargets.size()), true);
        }

        return pTargets.size();
    }

    private static int setRestPeriod(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets, int tick) throws CommandSyntaxException{
        int i = 0;

        for(ServerPlayer serverPlayer : pTargets) {
            SEHelper.setRestPeriod(serverPlayer, tick);
            ++i;
        }

        if (i == 0) {
            throw ERROR_SET_POINTS_INVALID2.create();
        } else {
            if (pTargets.size() == 1) {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.illager.rest.set.success.single", tick, pTargets.iterator().next().getDisplayName()), true);
            } else {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.illager.rest.set.success.multiple", tick, pTargets.size()), true);
            }

            return pTargets.size();
        }
    }

    private static int spawnNoAIEntity(CommandSourceStack pSource, Holder.Reference<EntityType<?>> pType, Vec3 pPos, CompoundTag pNbt, boolean tagged, boolean pRandomizeProperties) throws CommandSyntaxException {
        BlockPos blockpos = BlockPos.containing(pPos);
        if (!Level.isInSpawnableBounds(blockpos)) {
            throw INVALID_POSITION.create();
        } else {
            CompoundTag compoundnbt = pNbt.copy();
            compoundnbt.putString("id", pType.key().location().toString());
            ServerLevel serverworld = pSource.getLevel();
            Entity entity = EntityType.loadEntityRecursive(compoundnbt, serverworld, (p_218914_1_) -> {
                p_218914_1_.moveTo(pPos.x, pPos.y, pPos.z, p_218914_1_.getYRot(), p_218914_1_.getXRot());
                return p_218914_1_;
            });
            if (entity == null) {
                throw ERROR_FAILED.create();
            } else {
                if (entity instanceof Mob mob){
                    mob.setNoAi(true);
                    mob.setPersistenceRequired();
                    if (tagged){
                        mob.addTag(ConstantPaths.giveAI());
                    }
                    if (pRandomizeProperties){
                        ForgeEventFactory.onFinalizeSpawn(mob, pSource.getLevel(), pSource.getLevel().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, null, null);
                    }
                }

                if (!serverworld.tryAddFreshEntityWithPassengers(entity)) {
                    throw ERROR_DUPLICATE_UUID.create();
                } else {
                    pSource.sendSuccess(() -> Component.translatable("commands.summon_noai.success", entity.getDisplayName()), true);
                    return 1;
                }
            }
        }
    }

    private static int spawnPersistEntity(CommandSourceStack pSource, Holder.Reference<EntityType<?>> pType, Vec3 pPos, CompoundTag pNbt, boolean pRandomizeProperties) throws CommandSyntaxException {
        BlockPos blockpos = BlockPos.containing(pPos);
        if (!Level.isInSpawnableBounds(blockpos)) {
            throw INVALID_POSITION.create();
        } else {
            CompoundTag compoundnbt = pNbt.copy();
            compoundnbt.putString("id", pType.key().location().toString());
            ServerLevel serverworld = pSource.getLevel();
            Entity entity = EntityType.loadEntityRecursive(compoundnbt, serverworld, (p_218914_1_) -> {
                p_218914_1_.moveTo(pPos.x, pPos.y, pPos.z, p_218914_1_.getYRot(), p_218914_1_.getXRot());
                return p_218914_1_;
            });
            if (entity == null) {
                throw ERROR_FAILED.create();
            } else {
                if (entity instanceof Mob mob){
                    mob.setPersistenceRequired();
                    if (pRandomizeProperties){
                        ForgeEventFactory.onFinalizeSpawn(mob, pSource.getLevel(), pSource.getLevel().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, null, null);
                    }
                }

                if (!serverworld.tryAddFreshEntityWithPassengers(entity)) {
                    throw ERROR_DUPLICATE_UUID.create();
                } else {
                    pSource.sendSuccess(() -> Component.translatable("commands.summon_persist.success", entity.getDisplayName()), true);
                    return 1;
                }
            }
        }
    }

    private static int spawnTamedEntity(CommandSourceStack pSource, Holder.Reference<EntityType<?>> pType, Vec3 pPos, CompoundTag pNbt, boolean pRandomizeProperties) throws CommandSyntaxException {
        BlockPos blockpos = BlockPos.containing(pPos);
        if (!Level.isInSpawnableBounds(blockpos)) {
            throw INVALID_POSITION.create();
        } else {
            CompoundTag compoundnbt = pNbt.copy();
            compoundnbt.putString("id", pType.key().location().toString());
            ServerLevel serverworld = pSource.getLevel();
            Entity entity = EntityType.loadEntityRecursive(compoundnbt, serverworld, (p_218914_1_) -> {
                p_218914_1_.moveTo(pPos.x, pPos.y, pPos.z, p_218914_1_.getYRot(), p_218914_1_.getXRot());
                return p_218914_1_;
            });
            if (entity == null) {
                throw ERROR_FAILED.create();
            } else {
                if (entity instanceof Mob mob){
                    MobUtil.summonTame(mob, pSource.getPlayerOrException());
                    if (pRandomizeProperties){
                        ForgeEventFactory.onFinalizeSpawn(mob, pSource.getLevel(), pSource.getLevel().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, null, null);
                    }
                }

                if (!serverworld.tryAddFreshEntityWithPassengers(entity)) {
                    throw ERROR_DUPLICATE_UUID.create();
                } else {
                    pSource.sendSuccess(() -> Component.translatable("commands.summon_tame.success", entity.getDisplayName()), true);
                    return 1;
                }
            }
        }
    }

    private static int spawnHostileEntity(CommandSourceStack pSource, Holder.Reference<EntityType<?>> pType, Vec3 pPos, CompoundTag pNbt, boolean pRandomizeProperties) throws CommandSyntaxException {
        BlockPos blockpos = BlockPos.containing(pPos);
        if (!Level.isInSpawnableBounds(blockpos)) {
            throw INVALID_POSITION.create();
        } else {
            CompoundTag compoundnbt = pNbt.copy();
            compoundnbt.putString("id", pType.key().location().toString());
            ServerLevel serverworld = pSource.getLevel();
            Entity entity = EntityType.loadEntityRecursive(compoundnbt, serverworld, (p_218914_1_) -> {
                p_218914_1_.moveTo(pPos.x, pPos.y, pPos.z, p_218914_1_.getYRot(), p_218914_1_.getXRot());
                return p_218914_1_;
            });
            if (entity == null) {
                throw ERROR_FAILED.create();
            } else {
                if (entity instanceof Mob mob){
                    mob.setPersistenceRequired();
                    if (pRandomizeProperties){
                        ForgeEventFactory.onFinalizeSpawn(mob, pSource.getLevel(), pSource.getLevel().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, null, null);
                    }
                    if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null){
                        if (mob instanceof Owned owned){
                            owned.setHostile(true);
                        } else {
                            mob.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(mob, Player.class, true));
                        }
                    }
                }

                if (!serverworld.tryAddFreshEntityWithPassengers(entity)) {
                    throw ERROR_DUPLICATE_UUID.create();
                } else {
                    pSource.sendSuccess(() -> Component.translatable("commands.summon_hostile.success", entity.getDisplayName()), true);
                    return 1;
                }
            }
        }
    }

    private static int getResearches(CommandSourceStack pSource, ServerPlayer pPlayer){
        if (SEHelper.getResearch(pPlayer).isEmpty()){
            pSource.sendFailure(Component.translatable("commands.goety.research.get.empty", pPlayer.getDisplayName()));
        } else {
            pSource.sendSuccess(() -> Component.translatable("commands.goety.research.get", pPlayer.getDisplayName()), true);
        }
        for (Research research : SEHelper.getResearch(pPlayer)){
            pSource.sendSuccess(() -> Component.literal(research.getId()), true);
        }
        return 1;
    }

    private static int addResearch(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets, ResourceLocation string){
        for(ServerPlayer serverPlayer : pTargets) {
            if (ResearchList.getResearch(string) != null){
                Research research = ResearchList.getResearch(string);
                if (SEHelper.hasResearch(serverPlayer, research)){
                    pSource.sendFailure(Component.translatable("commands.goety.research.add.failure", serverPlayer.getDisplayName()));
                } else {
                    SEHelper.addResearch(serverPlayer, research);
                    pSource.sendSuccess(() -> Component.translatable("commands.goety.research.add.success", serverPlayer.getDisplayName()), true);
                }
            }
        }

        return 1;
    }

    private static int removeResearch(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets, ResourceLocation string){
        for(ServerPlayer serverPlayer : pTargets) {
            if (ResearchList.getResearch(string) != null){
                Research research = ResearchList.getResearch(string);
                if (!SEHelper.hasResearch(serverPlayer, research)){
                    pSource.sendFailure(Component.translatable("commands.goety.research.remove.failure", serverPlayer.getDisplayName()));
                } else {
                    SEHelper.removeResearch(serverPlayer, research);
                    pSource.sendSuccess(() -> Component.translatable("commands.goety.research.remove.success", serverPlayer.getDisplayName()), true);
                }
            }
        }

        return 1;
    }

    private static int addAllResearch(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets){
        for(ServerPlayer serverPlayer : pTargets) {
            for (Research research : ResearchList.getResearchList().values()){
                if (!SEHelper.hasResearch(serverPlayer, research)){
                    SEHelper.addResearch(serverPlayer, research);
                }
            }
            pSource.sendSuccess(() -> Component.translatable("commands.goety.research.addAll", serverPlayer.getDisplayName()), true);
        }

        return 1;
    }

    private static int removeAllResearch(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets){
        for(ServerPlayer serverPlayer : pTargets) {
            for (Research research : ResearchList.getResearchList().values()){
                if (SEHelper.hasResearch(serverPlayer, research)){
                    SEHelper.removeResearch(serverPlayer, research);
                }
            }
            pSource.sendSuccess(() -> Component.translatable("commands.goety.research.removeAll", serverPlayer.getDisplayName()), true);
        }

        return 1;
    }
}
