package com.Polarice3.Goety.common.network;

import com.Polarice3.Goety.Goety;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class ModServerBossInfo extends ServerBossEvent {
    private final Mob boss;
    private final Set<ServerPlayer> players = new HashSet<>();
    private UUID id;

    public ModServerBossInfo(UUID uuid, Mob boss, BossBarColor bossBarColor, boolean dark, boolean fog) {
        super(boss.getDisplayName(), bossBarColor, BossBarOverlay.PROGRESS);
        this.setVisible(true);
        this.setId(uuid);
        this.setDarkenScreen(dark);
        this.setCreateWorldFog(fog);
        this.boss = boss;
    }

    public void setId(UUID uuid){
        this.id = uuid;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    public void update() {
        this.setProgress(this.boss.getHealth() / this.boss.getMaxHealth());
        Iterator<ServerPlayer> it = this.players.iterator();

        while(it.hasNext()) {
            ServerPlayer player = it.next();
            if (this.boss.getSensing().hasLineOfSight(player)) {
                super.addPlayer(player);
                if (this.boss.level.isClientSide) {
                    Goety.PROXY.addBoss(this.boss);
                }

                it.remove();
            }
        }

    }

    public void addPlayer(ServerPlayer player) {
        if (this.boss.getSensing().hasLineOfSight(player)) {
            super.addPlayer(player);
            if (this.boss.level.isClientSide) {
                Goety.PROXY.addBoss(this.boss);
            }
        } else {
            this.players.add(player);
        }

    }

    public void removePlayer(ServerPlayer player) {
        super.removePlayer(player);
        this.players.remove(player);
        if (this.boss.level.isClientSide) {
            Goety.PROXY.removeBoss(this.boss);
        }

    }
}
