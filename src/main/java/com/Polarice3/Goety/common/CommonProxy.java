package com.Polarice3.Goety.common;

import com.Polarice3.Goety.init.ModProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class CommonProxy implements ModProxy {
    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public void addBossBar(UUID id, Mob mob) {
    }

    @Override
    public void removeBossBar(UUID id, Mob mob) {
    }

    @Override
    public void soulExplode(BlockPos blockPos, int radius) {
    }

    @Override
    public void shock(Vec3 vectorStart, Vec3 vectorEnd, int lifespan) {
    }

    @Override
    public void thunderBolt(Vec3 vectorStart, Vec3 vectorEnd, int lifespan) {
    }

    @Override
    public void lightningBolt(Vec3 vectorStart, Vec3 vectorEnd, int lifespan) {
    }

}
