package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.AbstractCyclone;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UpdraftBlast extends Entity {
    public LivingEntity owner;
    private UUID ownerUniqueId;
    private float damage = SpellConfig.UpdraftBlastDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
    private float areaOfEffect = 0.0F;

    public UpdraftBlast(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.noPhysics = true;
    }

    public UpdraftBlast(Level worldIn, double x, double y, double z) {
        this(ModEntityType.UPDRAFT_BLAST.get(), worldIn);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.ownerUniqueId = compound.getUUID("Owner");
        }
        this.damage = compound.getFloat("Damage");
        this.areaOfEffect = compound.getFloat("AreaOfEffect");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.ownerUniqueId != null) {
            compound.putUUID("Owner", this.ownerUniqueId);
        }
        compound.putFloat("Damage", this.damage);
        compound.putFloat("AreaOfEffect", this.areaOfEffect);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return this.damage;
    }

    public void setAreaOfEffect(float damage) {
        this.areaOfEffect = damage;
    }

    public float getAreaOfEffect() {
        return this.areaOfEffect;
    }

    public void setOwner(@Nullable LivingEntity ownerIn) {
        this.owner = ownerIn;
        this.ownerUniqueId = ownerIn == null ? null : ownerIn.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUniqueId != null && this.level instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level).getEntity(this.ownerUniqueId);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level instanceof ServerLevel serverLevel) {
            float area = this.getAreaOfEffect() / 2;
            float f = 1.5F + area;
            ColorUtil color = new ColorUtil(0xffffff);
            ServerParticleUtil.windParticle(serverLevel, color, (f - 1.0F) + serverLevel.random.nextFloat() * 0.5F, 0.0F, this.getId(), this.position());
            ServerParticleUtil.windParticle(serverLevel, color, f + serverLevel.random.nextFloat() * 0.5F, 0.0F, this.getId(), this.position());

            if (this.tickCount == 20){
                List<Entity> targets = new ArrayList<>();
                float area0 = 1.0F + area;
                AABB aabb = this.getBoundingBox();
                AABB aabb1 = new AABB(aabb.minX - area0, aabb.minY - 1.0F, aabb.minZ - area0, aabb.maxX + area0, aabb.maxY + 1.0F, aabb.maxZ + area0);
                for (Entity entity : this.level.getEntitiesOfClass(Entity.class, aabb1)){
                    if (this.owner != null) {
                        if (entity != this.owner && !MobUtil.areAllies(entity, this.owner)) {
                            targets.add(entity);
                        }
                    } else {
                        targets.add(entity);
                    }
                }
                if (!targets.isEmpty()){
                    for (Entity entity : targets) {
                        if (entity instanceof LivingEntity livingEntity) {
                            livingEntity.hurt(ModDamageSource.windBlast(this, this.owner), this.damage);
                            MobUtil.push(livingEntity, 0.0D, 1.0D, 0.0D);
                        } else if (entity instanceof AbstractCyclone cyclone){
                            cyclone.trueRemove();
                        }
                    }
                }
            }
        }
        if (this.tickCount == 1){
            this.playSound(ModSounds.UPDRAFT_BLAST.get(), 1.0F, 1.0F);
        }
        if (this.tickCount > 20){
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.25D, 0.0D));
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
        if (this.tickCount % 30 == 0){
            this.discard();
        }
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
