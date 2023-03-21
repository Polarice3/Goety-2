package com.Polarice3.Goety.init;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public interface ModProxy {
    Player getPlayer();
    void addBoss(Mob mob);
    void removeBoss(Mob mob);
}
