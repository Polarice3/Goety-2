package com.Polarice3.Goety.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.Team;

import java.util.ArrayList;
import java.util.Collection;

public class DummyTeam extends Team {
    @Override
    public String getName() {
        return "goety:dummy";
    }

    @Override
    public MutableComponent getFormattedName(Component p_83538_) {
        return Component.literal("goety:dummy");
    }

    @Override
    public boolean canSeeFriendlyInvisibles() {
        return false;
    }

    @Override
    public boolean isAllowFriendlyFire() {
        return true;
    }

    @Override
    public Visibility getNameTagVisibility() {
        return Visibility.ALWAYS;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.WHITE;
    }

    @Override
    public Collection<String> getPlayers() {
        return new ArrayList<>();
    }

    @Override
    public Visibility getDeathMessageVisibility() {
        return Visibility.NEVER;
    }

    @Override
    public CollisionRule getCollisionRule() {
        return Team.CollisionRule.ALWAYS;
    }
}
