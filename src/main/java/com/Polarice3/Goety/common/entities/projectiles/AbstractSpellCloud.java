package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class AbstractSpellCloud extends Entity {
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(AbstractSpellCloud.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE = SynchedEntityData.defineId(AbstractSpellCloud.class, EntityDataSerializers.PARTICLE);
    private LivingEntity owner;
    private UUID ownerUUID;
    public boolean activated;
    public int lifeSpan = 0;
    public float extraDamage = 0.0F;

    public AbstractSpellCloud(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public AbstractSpellCloud(EntityType<?> p_19870_, Level pLevel, LivingEntity pOwner, LivingEntity pTarget){
        this(p_19870_, pLevel);
        if (pOwner != null){
            this.setOwner(pOwner);
        }
        if (pTarget != null){
            BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(pTarget.getX(), pTarget.getY(), pTarget.getZ());

            while(blockpos$mutable.getY() < pTarget.getY() + 4.0D && !this.level.getBlockState(blockpos$mutable).blocksMotion()) {
                blockpos$mutable.move(Direction.UP);
            }
            this.setPos(pTarget.getX(), blockpos$mutable.getY(), pTarget.getZ());
        }
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_PARTICLE, ParticleTypes.RAIN);
        this.getEntityData().define(DATA_RADIUS, 2.0F);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_19729_) {
        if (DATA_RADIUS.equals(p_19729_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_19729_);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {
        if (p_20052_.contains("Particle", 8)) {
            try {
                this.setRainParticle(ParticleArgument.readParticle(new StringReader(p_20052_.getString("Particle")), BuiltInRegistries.PARTICLE_TYPE.asLookup()));
            } catch (CommandSyntaxException ignored) {
            }
        }
        if (p_20052_.contains("Activated")){
            this.activated = p_20052_.getBoolean("Activated");
        }
        if (p_20052_.contains("LifeSpan")) {
            this.lifeSpan = p_20052_.getInt("LifeSpan");
        }
        if (p_20052_.contains("ExtraDamage")){
            this.extraDamage = p_20052_.getFloat("ExtraDamage");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {
        p_20139_.putString("Particle", this.getRainParticle().writeToString());
        p_20139_.putBoolean("Activated", this.activated);
        p_20139_.putInt("LifeSpan", this.lifeSpan);
        p_20139_.putFloat("ExtraDamage", this.extraDamage);
    }

    public void setOwner(@Nullable LivingEntity p_190549_1_) {
        this.owner = p_190549_1_;
        this.ownerUUID = p_190549_1_ == null ? null : p_190549_1_.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    public void setLifeSpan(int lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public ParticleOptions getRainParticle() {
        return this.getEntityData().get(DATA_PARTICLE);
    }

    public void setRainParticle(ParticleOptions p_19725_) {
        this.getEntityData().set(DATA_PARTICLE, p_19725_);
    }

    public int getColor(){
        return 0xffffff;
    }

    public void setExtraDamage(float extraDamage){
        this.extraDamage = extraDamage;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide){
            this.cloudParticles(this.getColor());
            if (this.activated) {
                if (this.lifeSpan > 0) {
                    --this.lifeSpan;
                    this.rainParticles(this.getRainParticle());
                    AABB below = this.getBoundingBox().move(0, -16, 0).inflate(0, 16, 0);

                    for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, below)) {
                        boolean flag = false;
                        if (this.getOwner() != null) {
                            if (livingEntity != this.getOwner() && !livingEntity.isAlliedTo(this.getOwner()) && !this.getOwner().isAlliedTo(livingEntity)){
                                flag = true;
                            }
                        } else {
                            flag = true;
                        }
                        if (flag) {
                            int distance = (int) (this.getY() - livingEntity.getY());
                            if (BlockFinder.emptySpaceBetween(this.level, livingEntity.blockPosition(), distance, true)) {
                                this.hurtEntities(livingEntity);
                            }
                        }
                    }
                } else {
                    this.discard();
                }
            } else {
                if (this.tickCount % 20 == 0){
                    this.activated = true;
                }
            }
        }
    }

    public void setRadius(float radius){
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Mth.clamp(radius, 0.0F, 32.0F));
        }
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public void cloudParticles(int color){
        if (this.level instanceof ServerLevel serverWorld) {
            float f = getRadius();
            float f5 = (float) Math.PI * f * f;
            for (int k1 = 0; (float) k1 < f5; ++k1) {
                float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                float f8 = Mth.cos(f6) * f7;
                float f9 = Mth.sin(f6) * f7;
                if (color == 0xffffff){
                    serverWorld.sendParticles(ModParticleTypes.SPELL_CLOUD.get(), this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 0, 1.0F, 1.0F, 1.0F, 0.5F);
                } else {
                    serverWorld.sendParticles(ModParticleTypes.SPELL_CLOUD.get(), this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 0, MathHelper.rgbParticle(color)[0], MathHelper.rgbParticle(color)[1], MathHelper.rgbParticle(color)[2], 0.5F);
                }
            }
        }
    }

    public void rainParticles(ParticleOptions particleRain){
        if (this.level instanceof ServerLevel serverWorld){
            float f = getRadius();
            float f5 = (float) Math.PI * f * f;
            for (int k1 = 0; (float) k1 < f5; ++k1) {
                float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                float f8 = Mth.cos(f6) * f7;
                float f9 = Mth.sin(f6) * f7;
                serverWorld.sendParticles(particleRain, this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 1, 0, 0, 0, 0);
            }
        }
    }

    public void hurtEntities(LivingEntity livingEntity){
    }

    public EntityDimensions getDimensions(Pose p_19721_) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
