package com.Polarice3.Goety.client;

import com.Polarice3.Goety.client.events.BossBarEvent;
import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.client.particles.LightningEffect;
import com.Polarice3.Goety.client.particles.LightningParticleOptions;
import com.Polarice3.Goety.client.particles.ShockwaveParticle;
import com.Polarice3.Goety.init.ModProxy;
import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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

    public void shock(Vec3 vectorStart, Vec3 vectorEnd, int lifespan) {
        shock(Minecraft.getInstance().level, vectorStart, vectorEnd, lifespan);
    }

    public void shock(Level level, Vec3 vectorStart, Vec3 vectorEnd, int lifespan){
        if (!(level instanceof ClientLevel)){
            return;
        }
        LightningEffect.INSTANCE.add(level, new LightningParticleOptions(vectorStart, vectorEnd, lifespan).size(0.04F), ClientEvents.PARTIAL_TICK);
    }

    public void thunderBolt(Vec3 vectorStart, Vec3 vectorEnd, int lifespan) {
        thunderBolt(Minecraft.getInstance().level, vectorStart, vectorEnd, lifespan);
    }

    public void thunderBolt(Level level, Vec3 vectorStart, Vec3 vectorEnd, int lifespan){
        if (!(level instanceof ClientLevel)){
            return;
        }
        LightningEffect.INSTANCE.add(level, new LightningParticleOptions(LightningParticleOptions.BoltRenderInfo.thunderBolt(new ColorUtil(177, 235, 220, 1.0F)), vectorStart, vectorEnd, lifespan).size(0.25F), ClientEvents.PARTIAL_TICK);
    }

    public void lightningBolt(Vec3 vectorStart, Vec3 vectorEnd, int lifespan) {
        lightningBolt(Minecraft.getInstance().level, vectorStart, vectorEnd, lifespan);
    }

    public void lightningBolt(Level level, Vec3 vectorStart, Vec3 vectorEnd, int lifespan){
        if (!(level instanceof ClientLevel)){
            return;
        }
        LightningEffect.INSTANCE.add(level, new LightningParticleOptions(LightningParticleOptions.BoltRenderInfo.thunderBolt(new ColorUtil(100, 100, 220, 1.0F)).noise(1.0F, 0.001F), vectorStart, vectorEnd, lifespan).size(0.5F), ClientEvents.PARTIAL_TICK);
    }

    public void spawnSoulExplosion(Level level, BlockPos blockPos, int radius){
        if (!(level instanceof ClientLevel)){
            return;
        }
        Minecraft.getInstance().particleEngine.add(new ShockwaveParticle.Explosion((ClientLevel) level, blockPos.getX() + 0.5F, blockPos.getY(), blockPos.getZ() + 0.5F, 0, 0, 0, radius, Minecraft.getInstance().particleEngine));
    }
}
