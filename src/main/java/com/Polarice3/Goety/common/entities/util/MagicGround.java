package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.PartEntity;

import java.util.ArrayList;
import java.util.List;

public class MagicGround extends AbstractTrap{
    public MagicGround(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setParticle(ParticleTypes.WITCH);
    }

    public MagicGround(Level worldIn, double x, double y, double z) {
        this(ModEntityType.MAGIC_GROUND.get(), worldIn);
        this.setPos(x, y, z);
    }

    @Override
    public float radius() {
        return 1.0F;
    }

    public void tick() {
        super.tick();
        if (this.level instanceof ServerLevel serverLevel) {
            float f5 = (float) Math.PI * this.radius() * this.radius();
            if (serverLevel.random.nextFloat() <= 0.05F) {
                for (int j1 = 0; j1 < serverLevel.random.nextInt(16) + 1; ++j1) {
                    for (int k1 = 0; (float) k1 < f5; ++k1) {
                        float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                        float f7 = Mth.sqrt(this.random.nextFloat()) * this.radius();
                        float f8 = Mth.cos(f6) * f7;
                        float f9 = Mth.sin(f6) * f7;
                        serverLevel.sendParticles(ParticleTypes.WITCH, this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 0, 0, 0.5D, 0, 0.5F);
                    }
                }
            }
        }
        List<LivingEntity> targets = new ArrayList<>();
        for (Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox())) {
            LivingEntity livingEntity = null;
            if (entity instanceof PartEntity<?> partEntity && partEntity.getParent() instanceof LivingEntity living){
                livingEntity = living;
            } else if (entity instanceof LivingEntity living){
                livingEntity = living;
            }
            if (livingEntity != null) {
                if (this.getOwner() != null) {
                    if (livingEntity != this.getOwner() && !MobUtil.areAllies(this.getOwner(), livingEntity)) {
                        targets.add(livingEntity);
                    }
                } else {
                    targets.add(livingEntity);
                }
            }
        }
        if (!targets.isEmpty()){
            for (LivingEntity livingEntity : targets) {
                livingEntity.hurt(DamageSource.MAGIC, 4.0F);
                if (this.owner != null){
                    float damage = 4.0F;
                    if (this.owner instanceof Mob mob && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null){
                        damage = (float) (mob.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2.0F);
                    }
                    livingEntity.hurt(DamageSource.indirectMagic(this, this.owner), damage);
                }
            }
        }
        if (this.tickCount >= this.getDuration()) {
            this.discard();
        }
    }
}
