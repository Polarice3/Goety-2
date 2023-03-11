package com.Polarice3.Goety.common;

import com.Polarice3.Goety.init.ModProxy;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class CommonProxy implements ModProxy {
    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public void addBoss(Mob mob) {
    }

    @Override
    public void removeBoss(Mob mob) {
    }

}
