package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.FireTornado;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public class FireTornadoTrap extends AbstractTrap {

    public FireTornadoTrap(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setParticle(ParticleTypes.LARGE_SMOKE);
    }

    public FireTornadoTrap(Level worldIn, double x, double y, double z) {
        this(ModEntityType.FIRE_TORNADO_TRAP.get(), worldIn);
        this.setPos(x, y, z);
    }

    public void tick() {
        super.tick();
        if (this.tickCount >= this.getDuration()) {
            if (this.owner != null) {
                if (this.owner instanceof Mob owner) {
                    LivingEntity livingentity = owner.getTarget();
                    if (livingentity != null) {
                        double d1 = livingentity.getX() - this.getX();
                        double d2 = livingentity.getY(0.5D) - this.getY(0.5D);
                        double d3 = livingentity.getZ() - this.getZ();
                        FireTornado fireTornadoEntity = new FireTornado(this.level, this.owner, d1, d2, d3);
                        fireTornadoEntity.setOwnerId(this.owner.getUUID());
                        fireTornadoEntity.setTotalLife(1200);
                        fireTornadoEntity.setPos(this.getX(), this.getY(), this.getZ());
                        this.level.addFreshEntity(fireTornadoEntity);
                        this.discard();
                    } else {
                        this.discard();
                    }
                } else {
                    FireTornado fireTornadoEntity = new FireTornado(this.level, this.owner, 0, 0, 0);
                    fireTornadoEntity.setOwnerId(this.owner.getUUID());
                    fireTornadoEntity.setTotalLife(1200);
                    fireTornadoEntity.setPos(this.getX(), this.getY(), this.getZ());
                    this.level.addFreshEntity(fireTornadoEntity);
                    this.discard();
                }
            } else {
                FireTornado fireTornadoEntity = new FireTornado(ModEntityType.FIRE_TORNADO.get(), this.level);
                fireTornadoEntity.setTotalLife(1200);
                fireTornadoEntity.setPos(this.getX(), this.getY(), this.getZ());
                this.level.addFreshEntity(fireTornadoEntity);
                this.discard();
            }
        }
    }

}