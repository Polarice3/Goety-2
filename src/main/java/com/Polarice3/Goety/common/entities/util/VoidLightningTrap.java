package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.*;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.MagicFire;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningBoltPacket;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class VoidLightningTrap extends AbstractTrap {

    public VoidLightningTrap(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setParticle(ModParticleTypes.NONE.get());
    }

    public VoidLightningTrap(Level worldIn, double x, double y, double z) {
        this(ModEntityType.VOID_LIGHTNING_TRAP.get(), worldIn);
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
                ColorUtil colorUtil = new ColorUtil(ChatFormatting.LIGHT_PURPLE);
                serverLevel.sendParticles(new AoEParticleOption(3.0F, 3.0F / this.getDuration(), 0.0F, this.getDuration()), this.getX(), this.getY() + 0.1F, this.getZ(), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
            }
        }
        if (this.tickCount >= this.getDuration()) {
            List<Entity> list1 = this.level.getEntities(this, new AABB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D, this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D), this::canHitEntity);
            for(Entity entity : list1) {
                if (entity instanceof LivingEntity livingEntity) {
                    if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                        boolean flag = false;
                        float damage = 12.0F;
                        DamageSource damageSource = this.damageSources().lightningBolt();
                        if (this.getOwner() != null) {
                            if (!MobUtil.areAllies(this.getOwner(), livingEntity)) {
                                flag = true;
                                damageSource = ModDamageSource.lightning(this, this.getOwner());
                            }
                        } else {
                            flag = true;
                        }
                        if (flag) {
                            if (livingEntity.hurt(damageSource, damage)){
                                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get(), MathHelper.secondsToTicks(3), 1, false, true));
                            }
                        }
                    }
                }
            }
            this.playSound(ModSounds.THUNDER_STRIKE_FAST.get());
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
                    Vec3 vec3 = this.position();
                    Vec3 vec31 = vec3.add(this.level.getRandom().nextDouble(), 1.0D, this.level.getRandom().nextDouble());
                    ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, colorUtil2, 8));
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

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return pEntity.isAlive();
            } else {
                if (MobUtil.areAllies(this.getOwner(), pEntity)){
                    return false;
                }
                if (this.getOwner() instanceof Enemy && pEntity instanceof Enemy){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return pEntity.isAlive();
    }
}
