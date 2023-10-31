package com.Polarice3.Goety.init;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface ModProxy {
    Player getPlayer();
    void addBoss(Mob mob);
    void removeBoss(Mob mob);
    void soulExplode(BlockPos blockPos, int radius);
    void shock(Level level, Vec3 vectorStart, Vec3 vectorEnd, int lifespan);
}
