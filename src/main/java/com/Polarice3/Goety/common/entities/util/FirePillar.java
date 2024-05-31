package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.WitherNecromancer;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FirePillar extends CastSpellTrap{
    public int warmUp;
    public boolean playEvent;

    public FirePillar(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public FirePillar(Level worldIn, double x, double y, double z) {
        this(ModEntityType.FIRE_PILLAR.get(), worldIn);
        this.setPos(x, y, z);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("WarmUp")) {
            this.warmUp = compound.getInt("WarmUp");
        }
        if (compound.contains("PlayEvent")) {
            this.playEvent = compound.getBoolean("PlayEvent");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("WarmUp", this.warmUp);
        compound.putBoolean("PlayEvent", this.playEvent);
    }

    @Override
    public float radius() {
        return 1.0F;
    }

    @Nullable
    @Override
    public ParticleOptions getParticle() {
        return ModParticleTypes.BURNING.get();
    }

    public void setWarmUp(int warmUp){
        this.warmUp = warmUp;
        this.setDuration(this.getDuration() + warmUp);
    }

    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.warmUp <= 0) {
                if (this.level instanceof ServerLevel serverWorld) {
                    serverWorld.sendParticles(ModParticleTypes.FIERY_PILLAR.get(), this.getX(), this.getY() + 0.5D, this.getZ(), 0, 0, 0.5D, 0, 1.0D);
                }
                this.setActivated(true);
                if (!this.playEvent) {
                    this.playSound(ModSounds.FIRE_BREATH_START.get(), 0.05F, 1.0F);
                    this.playEvent = true;
                }
                if (this.tickCount % 20 == 0) {
                    this.playSound(ModSounds.FIRE_TORNADO_AMBIENT.get(), 1.0F, 1.0F);
                }
                List<LivingEntity> targets = new ArrayList<>();
                for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0, 8, 0))) {
                    if (this.getOwner() != null) {
                        if (livingEntity != this.getOwner() && !MobUtil.areAllies(this.getOwner(), livingEntity)) {
                            targets.add(livingEntity);
                        }
                    } else {
                        targets.add(livingEntity);
                    }
                }
                if (!targets.isEmpty()) {
                    for (LivingEntity livingEntity : targets) {
                        int distance = Math.max((int) (livingEntity.getY() - this.getY()), 1);
                        if (BlockFinder.emptySpaceBetween(this.level, this.blockPosition().above(), Math.min(8, distance), true)) {
                            float damage = SpellConfig.FlameStrikeDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
                            if (this.getOwner() != null) {
                                if (this.getOwner() instanceof Mob mob && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                                    damage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2.0F;
                                }
                            }
                            damage += this.getExtraDamage();
                            DamageSource damageSource = ModDamageSource.fireBreath(this, this.getOwner());
                            if (this.getOwner() instanceof WitherNecromancer){
                                damageSource = ModDamageSource.hellfire(this, this.getOwner());
                            }
                            livingEntity.hurt(damageSource, damage);
                            livingEntity.setSecondsOnFire(5);
                        }
                    }
                }
            } else {
                if (!this.isNoGravity()) {
                    MobUtil.moveDownToGround(this);
                }
                if (this.level instanceof ServerLevel serverWorld) {
                    double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth();
                    double d2 = this.getY() + 0.5F;
                    double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth();
                    serverWorld.sendParticles(ModParticleTypes.BIG_FIRE_GROUND.get(), d1, d2, d3, 0, 0.0D, 0.0D, 0.0D, 0.5F);
                }
                if (this.getOwner() != null && this.getOwner().isDeadOrDying()) {
                    this.discard();
                }
                --this.warmUp;
            }
            if (this.tickCount >= this.getDuration()) {
                this.discard();
            }
        }
    }
}
