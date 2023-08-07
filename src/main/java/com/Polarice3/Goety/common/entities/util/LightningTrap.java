package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;

public class LightningTrap extends AbstractTrap {

    public LightningTrap(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setParticle(ParticleTypes.CLOUD);
    }

    public LightningTrap(Level worldIn, double x, double y, double z) {
        this(ModEntityType.LIGHTNING_TRAP.get(), worldIn);
        this.setPos(x, y, z);
    }

    @Override
    public float radius() {
        return 1.5F;
    }

    public void tick() {
        super.tick();
        if (this.tickCount >= this.getDuration()) {
            LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            lightning.setPos(this.getX(),this.getY(),this.getZ());
            if (this.getOwner() instanceof Apostle){
                lightning.setDamage(AttributesConfig.ApostleMagicDamage.get().floatValue());
            }
            level.addFreshEntity(lightning);
            this.discard();
        }
    }
}
