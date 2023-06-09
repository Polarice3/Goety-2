package com.Polarice3.Goety.client;

import com.Polarice3.Goety.client.events.BossBarEvent;
import com.Polarice3.Goety.client.particles.ShockwaveParticle;
import com.Polarice3.Goety.init.ModProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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

    @Override
    public void soulExplode(BlockPos blockPos, int radius) {
        spawnSoulExplosion(Minecraft.getInstance().level, blockPos, radius);
    }

    public void spawnSoulExplosion(Level level, BlockPos blockPos, int radius){
        if (!(level instanceof ClientLevel)){
            return;
        }
        Minecraft.getInstance().particleEngine.add(new ShockwaveParticle.Explosion((ClientLevel) level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0, 0, 0, radius, Minecraft.getInstance().particleEngine));
    }
}
