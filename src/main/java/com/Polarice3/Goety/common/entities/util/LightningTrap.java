package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.client.particles.AoEParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.projectiles.SpellLightningBolt;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LightningTrap extends AbstractTrap {

    public LightningTrap(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setParticle(ModParticleTypes.NONE.get());
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
        if (this.tickCount == 1) {
            if (this.level instanceof ServerLevel serverLevel) {
                ColorUtil colorUtil = ColorUtil.WHITE;
                serverLevel.sendParticles(new AoEParticleOption(3.0F, 3.0F / this.getDuration(), 0.0F, this.getDuration()), this.getX(), this.getY() + 0.1F, this.getZ(), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
            }
        }
        if (this.tickCount >= this.getDuration()) {
            SpellLightningBolt lightning = new SpellLightningBolt(ModEntityType.SPELL_LIGHTNING_BOLT.get(), level);
            lightning.setPos(this.getX(),this.getY(),this.getZ());
            lightning.setOwner(this.getOwner());
            if (this.getOwner() instanceof Apostle){
                lightning.setDamage(AttributesConfig.ApostleMagicDamage.get().floatValue());
            }
            level.addFreshEntity(lightning);
            this.discard();
        }
    }
}
