package com.Polarice3.Goety.common.commands;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.capabilities.soulenergy.FocusCooldown;
import com.Polarice3.Goety.common.events.IllagerSpawner;
import com.Polarice3.Goety.common.events.WightSpawner;
import com.Polarice3.Goety.common.research.Research;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GoetyCommand {
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed"));
    private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));
    private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID = new SimpleCommandExceptionType(Component.translatable("commands.goety.soul.set.points.invalid"));
    private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID2 = new SimpleCommandExceptionType(Component.translatable("commands.goety.illager.rest.set.points.invalid"));
    private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID3 = new SimpleCommandExceptionType(Component.translatable("commands.goety.brew.level.set.failure"));
    private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID4 = new SimpleCommandExceptionType(Component.translatable("commands.goety.brew.xp.set.failure"));
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_RESEARCHES = (p_136344_, p_136345_) -> {
        Collection<Research> collection = ResearchList.getResearchIdList().values();
        return SharedSuggestionProvider.suggestResource(collection.stream().map(Research::getLocation), p_136345_);
    };
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_GOODWILL_ENTITIES =
            (ctx, builder) -> suggestGrimoireEntities(ctx, builder, false);

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_GRUDGE_ENTITIES =
            (ctx, builder) -> suggestGrimoireEntities(ctx, builder, true);

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
                .then(Commands.literal("summons")
                        .then(Commands.literal("summon_noai")
                                .then(Commands.argument("entity", ResourceArgument.resource(p_250122_, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((p_198738_0_) -> {
                                    return spawnNoAIEntity(p_198738_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198738_0_, "entity"), p_198738_0_.getSource().getPosition(), new CompoundTag(), true);
                                })
                                        .then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_198735_0_) -> {
                                            return spawnNoAIEntity(p_198735_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198735_0_, "entity"), Vec3Argument.getVec3(p_198735_0_, "pos"), new CompoundTag(), true);
                                        })
                                                .then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((p_198739_0_) -> {
                                                    return spawnNoAIEntity(p_198739_0_.getSource(), ResourceArgument.getSummonableEntityType(p_198739_0_, "entity"), Vec3Argument.getVec3(p_198739_0_, "pos"), CompoundTagArgument.getCompoundTag(p_198739_0_, "nbt"), false);
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
                                        })))))
                .then(Commands.literal("brew")
                        .then(Commands.literal("level")
                                .then(Commands.literal("get").executes((p_198352_0_) -> {
                                            return getBrewLevel(p_198352_0_.getSource(), p_198352_0_.getSource().getPlayerOrException());
                                        })
                                        .then(Commands.argument("targets", EntityArgument.player()).executes((p_198435_0_) -> {
                                            return getBrewLevel(p_198435_0_.getSource(), EntityArgument.getPlayer(p_198435_0_, "targets"));
                                        })))
                                .then(Commands.literal("add")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.argument("amount", IntegerArgumentType.integer()).executes((p_198445_0_) -> {
                                                    return addBrewLevel(p_198445_0_.getSource(), EntityArgument.getPlayers(p_198445_0_, "targets"), IntegerArgumentType.getInteger(p_198445_0_, "amount"));
                                                }))))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes((p_198439_0_) -> {
                                                    return setBrewLevel(p_198439_0_.getSource(), EntityArgument.getPlayers(p_198439_0_, "targets"), IntegerArgumentType.getInteger(p_198439_0_, "amount"));
                                                })))))
                        .then(Commands.literal("xp")
                                .then(Commands.literal("get").executes((p_198352_0_) -> {
                                            return getBrewBottling(p_198352_0_.getSource(), p_198352_0_.getSource().getPlayerOrException());
                                        })
                                        .then(Commands.argument("targets", EntityArgument.player()).executes((p_198435_0_) -> {
                                            return getBrewBottling(p_198435_0_.getSource(), EntityArgument.getPlayer(p_198435_0_, "targets"));
                                        })))
                                .then(Commands.literal("add")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.argument("amount", IntegerArgumentType.integer()).executes((p_198445_0_) -> {
                                                    return addBrewBottling(p_198445_0_.getSource(), EntityArgument.getPlayers(p_198445_0_, "targets"), IntegerArgumentType.getInteger(p_198445_0_, "amount"));
                                                }))))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes((p_198439_0_) -> {
                                                    return setBrewBottling(p_198439_0_.getSource(), EntityArgument.getPlayers(p_198439_0_, "targets"), IntegerArgumentType.getInteger(p_198439_0_, "amount"));
                                                }))))))
                .then(Commands.literal("spell")
                        .then(Commands.literal("cooldown")
                                .then(Commands.literal("held").executes((p_198352_0_) -> {
                                    if (p_198352_0_.getSource().isPlayer()) {
                                        List<ServerPlayer> list = new ArrayList<>();
                                        list.add(p_198352_0_.getSource().getPlayerOrException());
                                        return resetFocusCooldown(p_198352_0_.getSource(), list);
                                    }
                                    return 0;
                                })
                                        .then(Commands.argument("targets", EntityArgument.players()).executes((p_198435_0_) -> {
                                            return resetFocusCooldown(p_198435_0_.getSource(), EntityArgument.getPlayers(p_198435_0_, "targets"));
                                        })))
                                .then(Commands.literal("all").executes((p_198352_0_) -> {
                                            if (p_198352_0_.getSource().isPlayer()) {
                                                List<ServerPlayer> list = new ArrayList<>();
                                                list.add(p_198352_0_.getSource().getPlayerOrException());
                                                return resetAllFocusCooldowns(p_198352_0_.getSource(), list);
                                            }
                                            return 0;
                                        })
                                        .then(Commands.argument("targets", EntityArgument.players()).executes((p_198435_0_) -> {
                                            return resetAllFocusCooldowns(p_198435_0_.getSource(), EntityArgument.getPlayers(p_198435_0_, "targets"));
                                        })))))
                .then(Commands.literal("misc")
                        .then(Commands.literal("wight").executes((p_198352_0_) -> {
                            return spawnWight(p_198352_0_.getSource(), p_198352_0_.getSource().getPlayerOrException());
                        })
                                .then(Commands.argument("targets", EntityArgument.player()).executes((p_198435_0_) -> {
                                    return spawnWight(p_198435_0_.getSource(), EntityArgument.getPlayer(p_198435_0_, "targets"));
                                })))
                        .then(Commands.literal("despawn").requires((p_137812_) -> {
                            return p_137812_.hasPermission(2);
                        }).executes((p_137817_) -> {
                            return despawn(p_137817_.getSource(), ImmutableList.of(p_137817_.getSource().getEntityOrException()));
                        }).then(Commands.argument("targets", EntityArgument.entities()).executes((p_137810_) -> {
                            return despawn(p_137810_.getSource(), EntityArgument.getEntities(p_137810_, "targets"));
                        })))
                        .then(Commands.literal("repair")
                                .then(Commands.literal("held").executes((p_198352_0_) -> {
                                            if (p_198352_0_.getSource().isPlayer()) {
                                                List<ServerPlayer> list = new ArrayList<>();
                                                list.add(p_198352_0_.getSource().getPlayerOrException());
                                                return repairItem(p_198352_0_.getSource(), list);
                                            }
                                            return 0;
                                        })
                                        .then(Commands.argument("targets", EntityArgument.players()).executes((p_198435_0_) -> {
                                            return repairItem(p_198435_0_.getSource(), EntityArgument.getPlayers(p_198435_0_, "targets"));
                                        })))
                                .then(Commands.literal("inventory").executes((p_198352_0_) -> {
                                            if (p_198352_0_.getSource().isPlayer()) {
                                                List<ServerPlayer> list = new ArrayList<>();
                                                list.add(p_198352_0_.getSource().getPlayerOrException());
                                                return repairAllItems(p_198352_0_.getSource(), list);
                                            }
                                            return 0;
                                        })
                                        .then(Commands.argument("targets", EntityArgument.players()).executes((p_198435_0_) -> {
                                            return repairAllItems(p_198435_0_.getSource(), EntityArgument.getPlayers(p_198435_0_, "targets"));
                                        }))))
                        .then(Commands.literal("heal").executes((p_198352_0_) -> {
                                    if (p_198352_0_.getSource().isPlayer()) {
                                        List<ServerPlayer> list = new ArrayList<>();
                                        list.add(p_198352_0_.getSource().getPlayerOrException());
                                        return heal(p_198352_0_.getSource(), list);
                                    }
                                    return 0;
                                })
                                .then(Commands.argument("targets", EntityArgument.players()).executes((p_198435_0_) -> {
                                    return heal(p_198435_0_.getSource(), EntityArgument.getPlayers(p_198435_0_, "targets"));
                                })))
                        .then(Commands.literal("set_damage")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer()).executes((p_198445_0_) -> {
                                            return damageItem(p_198445_0_.getSource(), EntityArgument.getPlayers(p_198445_0_, "targets"), IntegerArgumentType.getInteger(p_198445_0_, "amount"));
                                        }))))
                                .then(Commands.literal("grimoire")
                                        .then(Commands.literal("goodwill")
                                                .then(Commands.literal("add")
                                                        .requires((p_137812_) -> {
                                                            return p_137812_.hasPermission(2);
                                                        })
                                                        .then(Commands.argument("targets", EntityArgument.players())
                                                                .then(Commands.literal("entity")
                                                                        .then(Commands.argument("entity", EntityArgument.entities()).executes((ctx) ->
                                                                                grimoireAddEntity(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                                                                                        EntityArgument.getEntities(ctx, "entity"), false))))
                                                                .then(Commands.literal("entityType")
                                                                        .then(Commands.argument("entityType", ResourceArgument.resource(p_250122_, Registries.ENTITY_TYPE))
                                                                                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((ctx) ->
                                                                                        grimoireAddType(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                                                                                                ResourceArgument.getSummonableEntityType(ctx, "entityType"), false))))))
                                                .then(Commands.literal("remove")
                                                        .requires((p_137812_) -> {
                                                            return p_137812_.hasPermission(2);
                                                        })
                                                        .then(Commands.argument("targets", EntityArgument.players())
                                                                .then(Commands.literal("entity")
                                                                        .then(Commands.argument("entity", StringArgumentType.string())
                                                                                .suggests(SUGGEST_GOODWILL_ENTITIES).executes((ctx) ->
                                                                                        grimoireRemoveEntityByUUID(ctx.getSource(),
                                                                                                EntityArgument.getPlayers(ctx, "targets"),
                                                                                                StringArgumentType.getString(ctx, "entity"), false))))
                                                                .then(Commands.literal("entityType")
                                                                        .then(Commands.argument("entityType", ResourceArgument.resource(p_250122_, Registries.ENTITY_TYPE))
                                                                                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((ctx) ->
                                                                                        grimoireRemoveType(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                                                                                                ResourceArgument.getSummonableEntityType(ctx, "entityType"), false))))))
                                                .then(Commands.literal("query")
                                                        .then(Commands.argument("target", EntityArgument.player()).executes((ctx) ->
                                                                grimoireQuery(ctx.getSource(), EntityArgument.getPlayer(ctx, "target"), false)))))
                                        .then(Commands.literal("grudge")
                                                .then(Commands.literal("add")
                                                        .requires((p_137812_) -> {
                                                            return p_137812_.hasPermission(2);
                                                        })
                                                        .then(Commands.argument("targets", EntityArgument.players())
                                                                .then(Commands.literal("entity")
                                                                        .then(Commands.argument("entity", EntityArgument.entities()).executes((ctx) ->
                                                                                grimoireAddEntity(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                                                                                        EntityArgument.getEntities(ctx, "entity"), true))))
                                                                .then(Commands.literal("entityType")
                                                                        .then(Commands.argument("entityType", ResourceArgument.resource(p_250122_, Registries.ENTITY_TYPE))
                                                                                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((ctx) ->
                                                                                        grimoireAddType(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                                                                                                ResourceArgument.getSummonableEntityType(ctx, "entityType"), true))))))
                                                .then(Commands.literal("remove")
                                                        .requires((p_137812_) -> {
                                                            return p_137812_.hasPermission(2);
                                                        })
                                                        .then(Commands.argument("targets", EntityArgument.players())
                                                                .then(Commands.literal("entity")
                                                                        .then(Commands.argument("entity", StringArgumentType.string())
                                                                                .suggests(SUGGEST_GRUDGE_ENTITIES).executes((ctx) ->
                                                                                        grimoireRemoveEntityByUUID(ctx.getSource(),
                                                                                                EntityArgument.getPlayers(ctx, "targets"),
                                                                                                StringArgumentType.getString(ctx, "entity"), true))))
                                                                .then(Commands.literal("entityType")
                                                                        .then(Commands.argument("entityType", ResourceArgument.resource(p_250122_, Registries.ENTITY_TYPE))
                                                                                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((ctx) ->
                                                                                        grimoireRemoveType(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                                                                                                ResourceArgument.getSummonableEntityType(ctx, "entityType"), true))))))
                                                .then(Commands.literal("query")
                                                        .then(Commands.argument("target", EntityArgument.player()).executes((ctx) ->
                                                                grimoireQuery(ctx.getSource(), EntityArgument.getPlayer(ctx, "target"), true))))))
                        ));
    }

    private static int addSoulEnergy(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets, int pAmount) {
        for(ServerPlayer serverPlayer : pTargets) {
            if (SEHelper.getSoulsContainer(serverPlayer)) {
                SEHelper.increaseSouls(serverPlayer, pAmount);
            } else {
                pSource.sendFailure(Component.translatable("commands.goety.soul.failed", pTargets.iterator().next().getDisplayName()));
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
                pSource.sendFailure(Component.translatable("commands.goety.soul.failed", pTargets.iterator().next().getDisplayName()));
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
        if (i > MobsConfig.IllagerAssaultSEThreshold.get()){
            IllagerSpawner illagerSpawner = new IllagerSpawner();
            illagerSpawner.forceSpawn(pPlayer.serverLevel(), pPlayer, pSource);
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

    private static int spawnNoAIEntity(CommandSourceStack pSource, Holder.Reference<EntityType<?>> pType, Vec3 pPos, CompoundTag pNbt, boolean pRandomizeProperties) throws CommandSyntaxException {
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
                    if (mob instanceof IOwned owned){
                        owned.setHostile(true);
                    }
                    if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null){
                        if (!(mob instanceof IOwned)){
                            mob.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(mob, Player.class, true));
                        }
                    }
                    if (pRandomizeProperties){
                        ForgeEventFactory.onFinalizeSpawn(mob, pSource.getLevel(), pSource.getLevel().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, null, null);
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

    private static int getBrewLevel(CommandSourceStack pSource, ServerPlayer pPlayer){
        int i = SEHelper.getBottleLevel(pPlayer);
        pSource.sendSuccess(() -> Component.translatable("commands.goety.brew.level.get.success", pPlayer.getDisplayName(), i), false);
        return 1;
    }

    private static int addBrewLevel(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets, int level) {
        for(ServerPlayer serverPlayer : pTargets) {
            SEHelper.setBottleLevel(serverPlayer, SEHelper.getBottleLevel(serverPlayer) + level);
        }

        if (pTargets.size() == 1) {
            pSource.sendSuccess(() -> Component.translatable("commands.goety.brew.level.add.success.single", level, pTargets.iterator().next().getDisplayName()), true);
        } else {
            pSource.sendSuccess(() -> Component.translatable("commands.goety.brew.level.add.success.multiple", level, pTargets.size()), true);
        }

        return pTargets.size();
    }

    private static int setBrewLevel(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets, int level) throws CommandSyntaxException{
        int i = 0;

        for(ServerPlayer serverPlayer : pTargets) {
            SEHelper.setBottleLevel(serverPlayer, level);
            ++i;
        }

        if (i == 0) {
            throw ERROR_SET_POINTS_INVALID3.create();
        } else {
            if (pTargets.size() == 1) {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.brew.level.set.success.single", level, pTargets.iterator().next().getDisplayName()), true);
            } else {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.brew.level.set.success.multiple", level, pTargets.size()), true);
            }

            return pTargets.size();
        }
    }

    private static int getBrewBottling(CommandSourceStack pSource, ServerPlayer pPlayer){
        int i = SEHelper.getBottling(pPlayer);
        pSource.sendSuccess(() -> Component.translatable("commands.goety.brew.xp.get.success", pPlayer.getDisplayName(), i), false);
        return 1;
    }

    private static int addBrewBottling(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets, int level) {
        for(ServerPlayer serverPlayer : pTargets) {
            SEHelper.increaseBottling(serverPlayer, level);
        }

        if (pTargets.size() == 1) {
            pSource.sendSuccess(() -> Component.translatable("commands.goety.brew.xp.add.success.single", level, pTargets.iterator().next().getDisplayName()), true);
        } else {
            pSource.sendSuccess(() -> Component.translatable("commands.goety.brew.xp.add.success.multiple", level, pTargets.size()), true);
        }

        return pTargets.size();
    }

    private static int setBrewBottling(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets, int level) throws CommandSyntaxException{
        int i = 0;

        for(ServerPlayer serverPlayer : pTargets) {
            SEHelper.setBottling(serverPlayer, level);
            ++i;
        }

        if (i == 0) {
            throw ERROR_SET_POINTS_INVALID4.create();
        } else {
            if (pTargets.size() == 1) {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.brew.xp.set.success.single", level, pTargets.iterator().next().getDisplayName()), true);
            } else {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.brew.xp.set.success.multiple", level, pTargets.size()), true);
            }

            return pTargets.size();
        }
    }

    private static int resetFocusCooldown(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets) {
        int i = 0;
        for(ServerPlayer serverPlayer : pTargets) {
            if (!WandUtil.findFocus(serverPlayer).isEmpty()){
                ItemStack itemStack = WandUtil.findFocus(serverPlayer);
                Item item = itemStack.getItem();
                if (SEHelper.getCooldowns(serverPlayer).containsKey(item)){
                    SEHelper.getFocusCoolDown(serverPlayer).removeCooldown(serverPlayer, pSource.getLevel(), item);
                    ++i;
                }
                if (SEHelper.getSpecificCooldowns(serverPlayer).containsKey(FocusCooldown.keyOf(itemStack))) {
                    SEHelper.getFocusCoolDown(serverPlayer).removeSpecificCooldown(serverPlayer, pSource.getLevel(), itemStack);
                    ++i;
                }
            }
        }

        if (i == 0){
            pSource.sendFailure(Component.translatable("commands.goety.spell.cooldown.held.failure"));
        } else {
            if (pTargets.size() == 1) {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.spell.cooldown.held.success.single", WandUtil.findFocus(pTargets.iterator().next()).getDisplayName()), true);
            } else {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.spell.cooldown.held.success.multiple", pTargets.size()), true);
            }
        }

        return i;
    }

    private static int resetAllFocusCooldowns(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets) {
        int i = 0;
        for(ServerPlayer serverPlayer : pTargets) {
            for (Item item : SEHelper.getCooldowns(serverPlayer).keySet()){
                SEHelper.getFocusCoolDown(serverPlayer).removeCooldown(serverPlayer, pSource.getLevel(), item);
            }
            for (String string : SEHelper.getSpecificCooldowns(serverPlayer).keySet()){
                SEHelper.getFocusCoolDown(serverPlayer).removeSpecificCooldown(serverPlayer, pSource.getLevel(), string);
            }
        }

        if (pTargets.size() == 1) {
            pSource.sendSuccess(() -> Component.translatable("commands.goety.spell.cooldown.inventory.success.single", pTargets.iterator().next().getDisplayName()), true);
        } else {
            pSource.sendSuccess(() -> Component.translatable("commands.goety.spell.cooldown.inventory.success.multiple", pTargets.size()), true);
        }

        return i;
    }

    private static int spawnWight(CommandSourceStack pSource, ServerPlayer pPlayer) {
        WightSpawner spawner = new WightSpawner();
        spawner.forceSpawn(pPlayer.serverLevel(), pPlayer, pSource);
        return 1;
    }

    private static int repairItem(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets) {
        int i = 0;
        for(ServerPlayer serverPlayer : pTargets) {
            if (!serverPlayer.getMainHandItem().isEmpty()){
                if (serverPlayer.getMainHandItem().isDamaged()){
                    serverPlayer.getMainHandItem().setDamageValue(0);
                    ++i;
                }
            }
        }

        if (i == 0){
            pSource.sendFailure(Component.translatable("commands.goety.misc.repair.held.failure"));
        } else {
            if (pTargets.size() == 1) {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.misc.repair.held.success.single", pTargets.iterator().next().getDisplayName(), pTargets.iterator().next().getMainHandItem().getDisplayName()), true);
            } else {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.misc.repair.held.success.multiple", pTargets.size()), true);
            }
        }
        return i;
    }

    private static int repairAllItems(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets) {
        int i0 = 0;
        for(ServerPlayer serverPlayer : pTargets) {
            for (int i = 0; i < serverPlayer.getInventory().getContainerSize(); ++i){
                ItemStack itemStack = serverPlayer.getInventory().getItem(i);
                if (itemStack.isDamaged()){
                    itemStack.setDamageValue(0);
                    ++i0;
                }
            }
        }

        if (i0 == 0){
            pSource.sendFailure(Component.translatable("commands.goety.misc.repair.inventory.failure"));
        } else {
            if (pTargets.size() == 1) {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.misc.repair.inventory.success.single", pTargets.iterator().next().getDisplayName()), true);
            } else {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.misc.repair.inventory.success.multiple", pTargets.size()), true);
            }
        }
        return i0;
    }

    private static int damageItem(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets, int damage) {
        int i = 0;
        for(ServerPlayer serverPlayer : pTargets) {
            if (!serverPlayer.getMainHandItem().isEmpty()){
                ItemStack itemStack = serverPlayer.getMainHandItem();
                if (itemStack.isDamageableItem()){
                    if (damage > itemStack.getMaxDamage() - 1) {
                        damage = itemStack.getMaxDamage() - 1;
                    }
                    serverPlayer.getMainHandItem().setDamageValue(damage);
                    ++i;
                }
            }
        }

        if (i == 0){
            pSource.sendFailure(Component.translatable("commands.goety.misc.damage.held.failure"));
        } else {
            if (pTargets.size() == 1) {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.misc.damage.held.success.single", pTargets.iterator().next().getDisplayName(), pTargets.iterator().next().getMainHandItem().getDisplayName()), true);
            } else {
                pSource.sendSuccess(() -> Component.translatable("commands.goety.misc.damage.held.success.multiple", pTargets.size()), true);
            }
        }
        return i;
    }

    private static int heal(CommandSourceStack pSource, Collection<? extends ServerPlayer> pTargets) {
        for(ServerPlayer serverPlayer : pTargets) {
            serverPlayer.removeAllEffects();
            serverPlayer.heal(serverPlayer.getMaxHealth());
            serverPlayer.getFoodData().setFoodLevel(20);
            serverPlayer.getFoodData().setSaturation(20);
            serverPlayer.getFoodData().setExhaustion(0);
        }

        if (pTargets.size() == 1) {
            pSource.sendSuccess(() -> Component.translatable("commands.goety.misc.heal.success.single", pTargets.iterator().next().getDisplayName()), true);
        } else {
            pSource.sendSuccess(() -> Component.translatable("commands.goety.misc.heal.success.multiple", pTargets.size()), true);
        }
        return 1;
    }

    private static int despawn(CommandSourceStack sourceStack, Collection<? extends Entity> collection) {
        int i = 0;
        for (Entity entity : collection) {
            if (!(entity instanceof Player)) {
                entity.discard();
                ++i;
            }
        }

        if (collection.size() == 1) {
            if (collection.stream().anyMatch(entity -> entity instanceof Player)) {
                sourceStack.sendFailure(Component.translatable("commands.goety.misc.despawn.failure.player"));
            } else {
                sourceStack.sendSuccess(() -> {
                    return Component.translatable("commands.goety.misc.despawn.success.single", collection.iterator().next().getDisplayName());
                }, true);
            }
        } else {
            int finalI = i;
            sourceStack.sendSuccess(() -> {
                return Component.translatable("commands.goety.misc.despawn.success.multiple", finalI);
            }, true);
        }

        return collection.size();
    }

    private static int grimoireAddEntity(CommandSourceStack source, Collection<? extends ServerPlayer> players, Collection<? extends Entity> entities, boolean grudge) {
        int total = 0;
        for (ServerPlayer player : players) {
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity living && living != player) {
                    boolean changed = grudge
                            ? SEHelper.addGrudgeEntity(player, living)
                            : SEHelper.addAllyEntity(player, living);
                    if (changed) {
                        total++;
                    }
                }
            }
        }
        final int count = total;
        String list = grudge ? Component.translatable("commands.goety.misc.grimoire.grudge").getString() : Component.translatable("commands.goety.misc.grimoire.goodwill").getString();
        source.sendSuccess(() -> Component.translatable("commands.goety.misc.grimoire.success.add.entity", count, list), true);
        return total;
    }

    private static int grimoireAddType(CommandSourceStack source, Collection<? extends ServerPlayer> players, Holder.Reference<EntityType<?>> typeHolder, boolean grudge) {
        EntityType<?> type = typeHolder.value();
        int total = 0;
        for (ServerPlayer player : players) {
            boolean changed = grudge
                    ? SEHelper.addGrudgeEntityType(player, type)
                    : SEHelper.addAllyEntityType(player, type);
            if (changed) {
                total++;
            }
        }
        final int count = total;
        String list = grudge ? Component.translatable("commands.goety.misc.grimoire.grudge").getString() : Component.translatable("commands.goety.misc.grimoire.goodwill").getString();
        Component typeName = type.getDescription();
        source.sendSuccess(() -> Component.translatable("commands.goety.misc.grimoire.success.add.entityType", typeName.getString(), list, count), true);

        return total;
    }

    private static int grimoireRemoveType(CommandSourceStack source, Collection<? extends ServerPlayer> players, Holder.Reference<EntityType<?>> typeHolder, boolean grudge) {
        EntityType<?> type = typeHolder.value();
        int total = 0;
        for (ServerPlayer player : players) {
            boolean changed = grudge
                    ? SEHelper.removeGrudgeEntityType(player, type)
                    : SEHelper.removeAllyEntityType(player, type);
            if (changed) {
                total++;
            }
        }
        final int count = total;
        String list = grudge ? Component.translatable("commands.goety.misc.grimoire.grudge").getString() : Component.translatable("commands.goety.misc.grimoire.goodwill").getString();
        Component typeName = type.getDescription();
        source.sendSuccess(() -> Component.translatable("commands.goety.misc.grimoire.success.remove.entityType", typeName.getString(), list, count), true);
        return total;
    }

    private static int grimoireQuery(CommandSourceStack source, ServerPlayer player, boolean grudge) {
        List<LivingEntity> entities = grudge ? SEHelper.getGrudgeEntities(player) : SEHelper.getAllyEntities(player);
        List<EntityType<?>> types = grudge ? SEHelper.getGrudgeEntityTypes(player) : SEHelper.getAllyEntityTypes(player);
        String list = grudge ? Component.translatable("commands.goety.misc.grimoire.grudge").getString() : Component.translatable("commands.goety.misc.grimoire.goodwill").getString();

        source.sendSuccess(() -> Component.translatable("commands.goety.misc.grimoire.list", player.getName().getString(), list), false);

        if (entities.isEmpty() && types.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("commands.goety.misc.grimoire.list.empty"), false);
            return 0;
        }

        if (!entities.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("commands.goety.misc.grimoire.list.entities"), false);
            for (LivingEntity livingEntity : entities) {
                Component name = livingEntity.getDisplayName();
                source.sendSuccess(() -> Component.literal(" - ").append(name)
                        .append(" (" + livingEntity.getUUID() + ")"), false);
            }
        }
        if (!types.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("commands.goety.misc.grimoire.list.entityTypes"), false);
            for (EntityType<?> entityType : types) {
                Component name = entityType.getDescription();
                source.sendSuccess(() -> Component.literal(" - ").append(name)
                        .append(" (" + EntityType.getKey(entityType) + ")"), false);
            }
        }
        return entities.size() + types.size();
    }

    private static CompletableFuture<Suggestions> suggestGrimoireEntities(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder, boolean grudge) {
        try {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "targets");
            List<String> uuids = new ArrayList<>();
            for (ServerPlayer player : players) {
                List<LivingEntity> stored = grudge
                        ? SEHelper.getGrudgeEntities(player)
                        : SEHelper.getAllyEntities(player);
                for (LivingEntity livingEntity : stored) {
                    String id = livingEntity.getUUID().toString();
                    if (!uuids.contains(id)) {
                        uuids.add(id);
                    }
                }
            }
            return SharedSuggestionProvider.suggest(uuids, builder);
        } catch (Exception e) {
            return builder.buildFuture();
        }
    }

    private static int grimoireRemoveEntityByUUID(CommandSourceStack source, Collection<? extends ServerPlayer> players, String uuidString, boolean grudge) {
        UUID uuid;
        try {
            uuid = UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.literal("Invalid UUID: " + uuidString));
            return 0;
        }

        int total = 0;
        for (ServerPlayer player : players) {
            List<LivingEntity> stored = grudge
                    ? SEHelper.getGrudgeEntities(player)
                    : SEHelper.getAllyEntities(player);
            for (LivingEntity living : stored) {
                if (living.getUUID().equals(uuid)) {
                    boolean changed = grudge
                            ? SEHelper.removeGrudgeEntity(player, living)
                            : SEHelper.removeAllyEntity(player, living);
                    if (changed) total++;
                    break;
                }
            }
        }
        final int count = total;
        String list = grudge ? Component.translatable("commands.goety.misc.grimoire.grudge").getString() : Component.translatable("commands.goety.misc.grimoire.goodwill").getString();
        source.sendSuccess(() -> Component.translatable("commands.goety.misc.grimoire.success.remove.entity", count, list), true);
        return total;
    }
}
