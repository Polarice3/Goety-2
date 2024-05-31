package com.Polarice3.Goety.init;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public interface ModProxy {
    Player getPlayer();
    void addBossBar(UUID id, Mob mob);
    void removeBossBar(UUID id, Mob mob);
    void soulExplode(BlockPos blockPos, int radius);
    void shock(Vec3 vectorStart, Vec3 vectorEnd, int lifespan);
    void thunderBolt(Vec3 vectorStart, Vec3 vectorEnd, int lifespan);
    void lightningBolt(Vec3 vectorStart, Vec3 vectorEnd, int lifespan);
}
