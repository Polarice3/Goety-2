package com.Polarice3.Goety.init;

import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public interface ModProxy {
    @Nullable
    Player getPlayer();
    @Nullable
    Level getLevel();
    void addBossBar(UUID id, Mob mob);
    void removeBossBar(UUID id, Mob mob);
    void soulExplode(BlockPos blockPos, int radius);

    default void shock(Vec3 vectorStart, Vec3 vectorEnd, ColorUtil colorUtil, int lifespan){
    }

    default void thunderBolt(Vec3 vectorStart, Vec3 vectorEnd, ColorUtil colorUtil, int lifespan){
    }

    default void lightningBolt(Vec3 vectorStart, Vec3 vectorEnd, ColorUtil colorUtil, int lifespan){
    }
}
