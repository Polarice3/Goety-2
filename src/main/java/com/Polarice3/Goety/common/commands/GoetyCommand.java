package com.Polarice3.Goety.common.commands;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.events.IllagerSpawner;
import com.Polarice3.Goety.utils.SEHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class GoetyCommand {
    private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID = new SimpleCommandExceptionType(Component.translatable("commands.goety.soul.set.points.invalid"));

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        pDispatcher.register(Commands.literal("goety").requires((p_198442_0_) -> {
            return p_198442_0_.hasPermission(2);
        }).then(Commands.literal("soul").then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("amount", IntegerArgumentType.integer()).executes((p_198445_0_) -> {
            return addSoulEnergy(p_198445_0_.getSource(), EntityArgument.getPlayers(p_198445_0_, "targets"), IntegerArgumentType.getInteger(p_198445_0_, "amount"));
        })))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes((p_198439_0_) -> {
            return setSoulEnergy(p_198439_0_.getSource(), EntityArgument.getPlayers(p_198439_0_, "targets"), IntegerArgumentType.getInteger(p_198439_0_, "amount"));
        }))))).then(Commands.literal("illager").then(Commands.literal("spawn").executes((p_198352_0_) -> {
            return spawnIllagers(p_198352_0_.getSource(), p_198352_0_.getSource().getPlayerOrException());
        }).then(Commands.argument("targets", EntityArgument.player()).executes((p_198435_0_) -> {
            return spawnIllagers(p_198435_0_.getSource(), EntityArgument.getPlayer(p_198435_0_, "targets"));
        })))));
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
            pSource.sendSuccess(Component.translatable("commands.goety.soul.add"+ ".success.single", pAmount, pTargets.iterator().next().getDisplayName()), true);
        } else {
            pSource.sendSuccess(Component.translatable("commands.goety.soul.add" + ".success.multiple", pAmount, pTargets.size()), true);
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
                pSource.sendSuccess(Component.translatable("commands.goety.soul.set" + ".success.single", pAmount, pTargets.iterator().next().getDisplayName()), true);
            } else {
                pSource.sendSuccess(Component.translatable("commands.goety.soul.set" + ".success.multiple", pAmount, pTargets.size()), true);
            }

            return pTargets.size();
        }
    }

    private static int spawnIllagers(CommandSourceStack pSource, ServerPlayer pPlayer) {
        int i = SEHelper.getSoulAmountInt(pPlayer);
        if (i > MainConfig.IllagerAssaultSEThreshold.get()){
            pSource.sendSuccess(Component.translatable("commands.goety.illager.spawn.success", pPlayer.getDisplayName()), false);
            IllagerSpawner illagerSpawner = new IllagerSpawner();
            illagerSpawner.forceSpawn(pPlayer.getLevel(), pPlayer);
            return 1;
        } else {
            pSource.sendFailure(Component.translatable("commands.goety.illager.spawn.failure", pPlayer.getDisplayName()));
        }
        return i;
    }
}
