package com.Polarice3.Goety.common.commands;

import com.Polarice3.Goety.common.capabilities.lichdom.ILichdom;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;

import java.util.Collection;

public class LichCommand {
    private static final SimpleCommandExceptionType ERROR_ALREADY_LICH = new SimpleCommandExceptionType(Component.translatable("commands.lich.turnlich.failed"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_NOT_LICH = new SimpleCommandExceptionType(Component.translatable("commands.lich.delich.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        pDispatcher.register(Commands.literal("lichdom").requires((p_198442_0_) -> {
            return p_198442_0_.hasPermission(2);
        }).then(Commands.literal("grant").then(Commands.argument("targets", EntityArgument.players()).executes((p_198445_0_) -> {
            return grantLichdom(p_198445_0_.getSource(), EntityArgument.getPlayers(p_198445_0_, "targets"));
        }))).then(Commands.literal("revoke").then(Commands.argument("targets", EntityArgument.players()).executes((p_198445_0_) -> {
            return revokeLichdom(p_198445_0_.getSource(), EntityArgument.getPlayers(p_198445_0_, "targets"));
        }))).then(Commands.literal("query").then(Commands.argument("targets", EntityArgument.player()).executes((p_198435_0_) -> {
            return queryLich(p_198435_0_.getSource(), EntityArgument.getPlayer(p_198435_0_, "targets"));
        }))));
    }

    private static int grantLichdom(CommandSourceStack pSource, Collection<ServerPlayer> targets) throws CommandSyntaxException {
        for (ServerPlayer player : targets){
            ILichdom lichdom = LichdomHelper.getCapability(player);
            boolean isLich = lichdom.getLichdom();
            if (!isLich) {
                lichdom.setLichdom(true);
                LichdomHelper.sendLichUpdatePacket(player);
                pSource.sendSuccess(Component.translatable("commands.lich.turnlich.success", player.getDisplayName()), false);
            } else {
                throw ERROR_ALREADY_LICH.create();
            }
        }
        return 0;
    }

    private static int revokeLichdom(CommandSourceStack pSource, Collection<ServerPlayer> targets) throws CommandSyntaxException {
        for (ServerPlayer player : targets){
            ILichdom lichdom = LichdomHelper.getCapability(player);
            boolean isLich = lichdom.getLichdom();
            if (isLich) {
                lichdom.setLichdom(false);
                if (player.hasEffect(MobEffects.NIGHT_VISION)){
                    player.removeEffect(MobEffects.NIGHT_VISION);
                }
                LichdomHelper.sendLichUpdatePacket(player);
                pSource.sendSuccess(Component.translatable("commands.lich.delich.success", player.getDisplayName()), false);
            } else {
                throw ERROR_ALREADY_NOT_LICH.create();
            }
        }
        return 0;
    }

    private static int queryLich(CommandSourceStack pSource, ServerPlayer pPlayer) {
        ILichdom lichdom = LichdomHelper.getCapability(pPlayer);
        boolean isLich = lichdom.getLichdom();
        if (isLich){
            pSource.sendSuccess(Component.translatable("commands.lich.query.true", pPlayer.getDisplayName()), false);
        } else {
            pSource.sendSuccess(Component.translatable("commands.lich.query.false", pPlayer.getDisplayName()), false);
        }
        return 0;
    }
}
