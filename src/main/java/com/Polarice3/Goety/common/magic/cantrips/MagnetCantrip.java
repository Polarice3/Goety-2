package com.Polarice3.Goety.common.magic.cantrips;

import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class MagnetCantrip {

    public void callItems(Player pPlayer) {
        if (pPlayer != null && !pPlayer.isSpectator()) {
            for (Entity entity : pPlayer.level.getEntitiesOfClass(Entity.class, pPlayer.getBoundingBox().inflate(16.0D))){
                if (entity instanceof ItemEntity || entity instanceof ExperienceOrb) {
                    Vec3 vector3d = new Vec3(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
                    Vec3 vector3d1 = new Vec3(entity.getX(), entity.getY(), entity.getZ());
                    Vec3 vector3d2 = vector3d.subtract(vector3d1);
                    if (vector3d2.lengthSqr() > 1) {
                        vector3d2.normalize();
                    }
                    double speed = 0.2D;
                    Vec3 motion = new Vec3(vector3d2.x * speed, vector3d2.y * speed, vector3d2.z * speed);
                    entity.setDeltaMovement(motion);
                    if (!pPlayer.level.isClientSide){
                        ServerParticleUtil.smokeParticles(ParticleTypes.WITCH, entity.getX(), entity.getY(), entity.getZ(), pPlayer.level);
                    }
                }
            }
        }
    }
}
