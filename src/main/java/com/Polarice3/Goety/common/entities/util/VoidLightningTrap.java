package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.client.particles.AoEParticleOption;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.GatherTrailParticle;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.MagicFire;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningBoltPacket;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VoidLightningTrap extends MagicLightningTrap {

    public VoidLightningTrap(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public VoidLightningTrap(Level worldIn, double x, double y, double z) {
        this(ModEntityType.VOID_LIGHTNING_TRAP.get(), worldIn);
        this.setPos(x, y, z);
    }

    public void aoeIndicate() {
        float size = this.radius() * 2.0F;
        if (this.level instanceof ServerLevel serverLevel) {
            ColorUtil colorUtil = new ColorUtil(ChatFormatting.LIGHT_PURPLE);
            serverLevel.sendParticles(new AoEParticleOption(size, size / this.getDuration(), 0.0F, this.getDuration()), this.getX(), this.getY() + 0.1F, this.getZ(), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
        }
    }

    @Override
    public void hurtEffect(LivingEntity livingEntity) {
        livingEntity.addEffect(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get(), MathHelper.secondsToTicks(3), 1, false, true));
    }

    public void finalizeAttack(){
        if (this.level instanceof ServerLevel serverLevel) {
            ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_PURPLE);
            ColorUtil colorUtil2 = new ColorUtil(ChatFormatting.LIGHT_PURPLE);
            serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 3.0F, 1), this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(new VerticalCircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 3.0F, 1), this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            ModNetwork.sendToALL(new SLightningBoltPacket(new Vec3(this.getX(), this.getY() + 250, this.getZ()), this.position(), colorUtil2, 10));
            for (int i = 0; i < 8; ++i) {
                Vec3 vector3d1 = this.position().add((this.level.getRandom().nextFloat() - 0.5F) * 6.0D, 3.0D, (this.level.getRandom().nextFloat() - 0.5F) * 6.0D);
                serverLevel.sendParticles(new GatherTrailParticle.Option(colorUtil, vector3d1), this.getX(), this.getY(), this.getZ(), 0, 0.0F, 0.0F, 0.0F, 0.5F);
            }
            for (int i = 0; i < 16; ++i) {
                Vec3 vec3 = this.position().add(0.0D, 1.0D, 0.0D);
                int random1 = this.level.getRandom().nextIntBetweenInclusive(-4, 4);
                int random2 = this.level.getRandom().nextIntBetweenInclusive(-4, 4);
                Vec3 vec31 = vec3.add(this.level.getRandom().nextDouble() * random1, this.level.getRandom().nextDouble(), this.level.getRandom().nextDouble() * random2);
                ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, colorUtil2, 12));
            }
            if (this.level.getRandom().nextBoolean()) {
                if (this.getOwner() != null) {
                    MagicFire magicFire = new MagicFire(this.level, Vec3.atCenterOf(this.blockPosition()), this.getOwner());
                    if (this.level.noCollision(magicFire.getBoundingBox().deflate(0.25D)) && this.level.getEntityCollisions(magicFire, magicFire.getBoundingBox().deflate(0.25D)).isEmpty()) {
                        if (this.level.addFreshEntity(magicFire)) {
                            if (this.level.getRandom().nextFloat() <= 0.25F) {
                                for (Direction direction : Direction.values()) {
                                    if (direction.getAxis().isHorizontal()) {
                                        MagicFire magicFire1 = new MagicFire(this.level, Vec3.atCenterOf(magicFire.blockPosition().relative(direction)), this.getOwner());
                                        if (this.level.noCollision(magicFire1.getBoundingBox().deflate(0.25D)) && this.level.getEntityCollisions(magicFire1, magicFire1.getBoundingBox().deflate(0.25D)).isEmpty()) {
                                            this.level.addFreshEntity(magicFire1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.discard();
    }
}
