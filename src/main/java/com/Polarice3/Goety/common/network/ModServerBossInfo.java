package com.Polarice3.Goety.common.network;

import com.Polarice3.Goety.common.network.server.SUpdateBossBar;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ModServerBossInfo extends ServerBossEvent {
    private final Mob boss;
    private final Set<ServerPlayer> players = new HashSet<>();

    public ModServerBossInfo(Mob boss, BossBarColor bossBarColor, boolean dark, boolean fog) {
        super(boss.getDisplayName(), bossBarColor, BossBarOverlay.PROGRESS);
        this.setVisible(true);
        this.setDarkenScreen(dark);
        this.setCreateWorldFog(fog);
        this.boss = boss;
    }

    public void update() {
        this.setProgress(this.boss.getHealth() / this.boss.getMaxHealth());
        Iterator<ServerPlayer> it = this.players.iterator();

        while(it.hasNext()) {
            ServerPlayer player = it.next();
            if (this.boss.getSensing().hasLineOfSight(player)) {
                super.addPlayer(player);
                it.remove();
            }
        }

    }

    public void addPlayer(ServerPlayer player) {
        ModNetwork.sendToClient(player, new SUpdateBossBar(this.getId(), this.boss, false));
        if (this.boss.getSensing().hasLineOfSight(player)) {
            super.addPlayer(player);
        } else {
            this.players.add(player);
        }

    }

    public void removePlayer(ServerPlayer player) {
        super.removePlayer(player);
        this.players.remove(player);
        ModNetwork.sendToClient(player, new SUpdateBossBar(this.getId(), this.boss, true));
    }
}
