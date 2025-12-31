package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.*;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningBoltPacket;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MagicLightningTrap extends AbstractTrap {
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(MagicLightningTrap.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(MagicLightningTrap.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(MagicLightningTrap.class, EntityDataSerializers.INT);

    public MagicLightningTrap(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setParticle(ModParticleTypes.NONE.get());
    }

    public MagicLightningTrap(Level worldIn, double x, double y, double z) {
        this(ModEntityType.MAGIC_LIGHTNING_TRAP.get(), worldIn);
        this.setPos(x, y, z);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_RADIUS, 1.5F);
        this.getEntityData().define(DATA_DAMAGE, SpellConfig.ThunderboltDamage.get().floatValue() * WandUtil.damageMultiply());
        this.getEntityData().define(DATA_COLOR, 0xb1ebdc);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Radius", this.radius());
        compound.putFloat("Damage", this.getDamage());
        compound.putInt("Color", this.getColor());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Radius")){
            this.setRadius(compound.getFloat("Radius"));
        }
        if (compound.contains("Damage")){
            this.setDamage(compound.getFloat("Damage"));
        }
        if (compound.contains("Color")){
            this.setColor(compound.getInt("Color"));
        }
    }

    @Override
    public float radius() {
        return this.entityData.get(DATA_RADIUS);
    }

    public void setRadius(float p_19713_) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Mth.clamp(p_19713_, 0.0F, 32.0F));
        }
    }

    public int getColor() {
        return this.getEntityData().get(DATA_COLOR);
    }

    public void setColor(int color) {
        this.getEntityData().set(DATA_COLOR, color);
    }

    public void setDamage(float damage) {
        this.getEntityData().set(DATA_DAMAGE, damage);
    }

    public float getDamage() {
        return this.getEntityData().get(DATA_DAMAGE);
    }

    public void tick() {
        super.tick();
        if (this.tickCount == 1) {
            this.aoeIndicate();
        }
        if (this.tickCount >= this.getDuration()) {
            this.damageEntities();
            this.finalizeAttack();
        }
    }

    public void aoeIndicate() {
        float size = this.radius() * 2.0F;
        if (this.level instanceof ServerLevel serverLevel) {
            ColorUtil colorUtil = new ColorUtil(ChatFormatting.WHITE);
            serverLevel.sendParticles(new AoEParticleOption(size, size / this.getDuration(), 0.0F, this.getDuration()), this.getX(), this.getY() + 0.1F, this.getZ(), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
        }
    }

    public void damageEntities() {
        List<Entity> list1 = this.level.getEntities(this, new AABB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D, this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D), this::canHitEntity);
        for(Entity entity : list1) {
            if (entity instanceof LivingEntity livingEntity) {
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                    boolean flag = false;
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
                        if (livingEntity.hurt(damageSource, this.getDamage())){
                            this.hurtEffect(livingEntity);
                        }
                    }
                }
            }
        }
        this.playSound(ModSounds.THUNDER_STRIKE_FAST.get());
    }

    public void hurtEffect(LivingEntity livingEntity) {
        float chance = 0.25F;
        if (this.level.isThundering() && this.level.isRainingAt(livingEntity.blockPosition())){
            chance += 0.25F;
        }
        if (this.level.getRandom().nextFloat() <= chance){
            livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(), MathHelper.secondsToTicks(5)));
        }
    }

    public void finalizeAttack(){
        if (this.level instanceof ServerLevel serverLevel) {
            ColorUtil colorUtil = new ColorUtil(this.getColor());
            serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 3.0F, 1), this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(new VerticalCircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 3.0F, 1), this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            ModNetwork.sendToALL(new SLightningBoltPacket(new Vec3(this.getX(), this.getY() + 250, this.getZ()), this.position(), colorUtil, 10));
            for (int i = 0; i < 8; ++i) {
                Vec3 vector3d1 = this.position().add((this.level.getRandom().nextFloat() - 0.5F) * 6.0D, 3.0D, (this.level.getRandom().nextFloat() - 0.5F) * 6.0D);
                serverLevel.sendParticles(new GatherTrailParticle.Option(colorUtil, vector3d1), this.getX(), this.getY(), this.getZ(), 0, 0.0F, 0.0F, 0.0F, 0.5F);
            }
            for (int i = 0; i < 16; ++i) {
                Vec3 vec3 = this.position().add(0.0D, 1.0D, 0.0D);
                int random1 = this.level.getRandom().nextIntBetweenInclusive(-4, 4);
                int random2 = this.level.getRandom().nextIntBetweenInclusive(-4, 4);
                Vec3 vec31 = vec3.add(this.level.getRandom().nextDouble() * random1, this.level.getRandom().nextDouble(), this.level.getRandom().nextDouble() * random2);
                ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, colorUtil, 12));
            }
        }
        this.discard();
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
