package com.Polarice3.Goety.client;

import com.Polarice3.Goety.client.events.BossBarEvent;
import com.Polarice3.Goety.init.ModProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class ClientProxy implements ModProxy {
    @Override
    public Player getPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public void addBoss(Mob mob) {
        BossBarEvent.addBoss(mob);
    }

    @Override
    public void removeBoss(Mob mob) {
        BossBarEvent.removeBoss(mob);
    }
}
