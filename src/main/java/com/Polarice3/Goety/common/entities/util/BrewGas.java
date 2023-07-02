package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.BrewUtils;
import com.Polarice3.Goety.utils.ConstantPaths;
import com.Polarice3.Goety.utils.MathHelper;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class BrewGas extends Entity {
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(BrewGas.class, EntityDataSerializers.INT);
    public List<MobEffectInstance> effects = Lists.newArrayList();
    public List<BrewEffectInstance> brewEffects = Lists.newArrayList();
    private boolean fixedColor;
    public int duration = 600;
    public int area = 10;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public BrewGas(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.noPhysics = true;
    }

    public BrewGas(Level p_19707_, double p_19708_, double p_19709_, double p_19710_) {
        this(ModEntityType.BREW_EFFECT_GAS.get(), p_19707_);
        this.setPos(p_19708_, p_19709_, p_19710_);
    }

    public void setGas(List<MobEffectInstance> mobEffectInstances, List<BrewEffectInstance> brewEffectInstances, int duration, int area, @Nullable LivingEntity owner){
        if (!mobEffectInstances.isEmpty()) {
            for (MobEffectInstance mobEffectInstance : mobEffectInstances) {
                this.addEffect(mobEffectInstance);
            }
        }
        if (!brewEffectInstances.isEmpty()) {
            for (BrewEffectInstance brewEffectInstance : brewEffectInstances) {
                this.addBrewEffect(brewEffectInstance);
            }
        }
        this.duration = duration;
        this.area = area;
        this.owner = owner;
        this.ownerUUID = owner != null ? owner.getUUID() : null;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_COLOR, 0xffffff);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {
        if (p_20052_.contains("Effects", 9)) {
            ListTag listtag = p_20052_.getList("Effects", 10);

            for(int i = 0; i < listtag.size(); ++i) {
                CompoundTag compoundtag = listtag.getCompound(i);
                MobEffectInstance instance = MobEffectInstance.load(compoundtag);
                if (instance != null) {
                    this.addEffect(instance);
                }
            }
        }
        if (p_20052_.contains("BrewEffects", 9)) {
            ListTag listtag = p_20052_.getList("BrewEffects", 10);

            for(int i = 0; i < listtag.size(); ++i) {
                CompoundTag compoundtag = listtag.getCompound(i);
                BrewEffectInstance instance = BrewEffectInstance.load(compoundtag);
                if (instance != null) {
                    this.addBrewEffect(instance);
                }
            }
        }
        if (p_20052_.hasUUID("Owner")) {
            this.ownerUUID = p_20052_.getUUID("Owner");
        }
        if (p_20052_.contains("Color", 99)) {
            this.setFixedColor(p_20052_.getInt("Color"));
        }
        if (p_20052_.contains("Duration")) {
            this.duration = p_20052_.getInt("Duration");
        }
        if (p_20052_.contains("Area")) {
            this.area = p_20052_.getInt("Area");
        }
        if (p_20052_.contains("Age")) {
            this.tickCount = p_20052_.getInt("Age");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {
        if (!this.effects.isEmpty()) {
            ListTag listtag = new ListTag();

            for(MobEffectInstance mobeffectinstance : this.effects) {
                listtag.add(mobeffectinstance.save(new CompoundTag()));
            }

            p_20139_.put("Effects", listtag);
        }

        if (!this.brewEffects.isEmpty()) {
            ListTag listtag = new ListTag();

            for(BrewEffectInstance brewEffectInstance : this.brewEffects) {
                listtag.add(brewEffectInstance.save(new CompoundTag()));
            }

            p_20139_.put("BrewEffects", listtag);
        }
        if (this.ownerUUID != null) {
            p_20139_.putUUID("Owner", this.ownerUUID);
        }
        if (this.fixedColor) {
            p_20139_.putInt("Color", this.getColor());
        }
        if (this.duration > 0) {
            p_20139_.putInt("Duration", this.duration);
        }
        p_20139_.putInt("Area", this.area);
        p_20139_.putInt("Age", this.tickCount);
    }

    public void setPos(double p_33449_, double p_33450_, double p_33451_) {
        super.setPos((double) Mth.floor(p_33449_) + 0.5D, (double)Mth.floor(p_33450_ + 0.5D), (double)Mth.floor(p_33451_) + 0.5D);
    }

    public boolean isInSolid() {
        if (!this.level.getBlockState(this.blockPosition()).isAir() && !BlockFinder.canBeReplaced(this.level, this.blockPosition())){
            return true;
        } else if (!this.level.getFluidState(this.blockPosition()).isEmpty() || this.level.getBlockState(this.blockPosition()).is(BlockTags.FIRE)){
            return true;
        }
        float f = this.getDimensions(Pose.STANDING).width * 0.8F;
        AABB aabb = AABB.ofSize(this.getEyePosition(), (double)f, 1.0E-6D, (double)f);
        return BlockPos.betweenClosedStream(aabb).anyMatch((p_201942_) -> {
            BlockState blockstate = this.level.getBlockState(p_201942_);
            return !blockstate.isAir() && blockstate.isSuffocating(this.level, p_201942_) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level, p_201942_).move((double)p_201942_.getX(), (double)p_201942_.getY(), (double)p_201942_.getZ()), Shapes.create(aabb), BooleanOp.AND);
        });
    }

    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.level instanceof ServerLevel serverLevel){
                serverLevel.sendParticles(ModParticleTypes.CULT_SPELL.get(), this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0, MathHelper.rgbParticle(this.getColor())[0], MathHelper.rgbParticle(this.getColor())[1], MathHelper.rgbParticle(this.getColor())[2], 0.5F);
            }
            if (this.isInSolid()){
                this.discard();
            }
            int i = this.duration - this.tickCount;
            if (this.area > 0) {
                if (this.random.nextInt(40) == 0 || this.tickCount == 5) {
                    boolean expanded = false;
                    for (Direction direction : Direction.values()) {
                        double probability = 0.4D;
                        if (direction.getAxis().isHorizontal()) {
                            probability = 0.8D;
                        }
                        if (this.level.random.nextDouble() < probability) {
                            BlockPos blockPos = this.blockPosition().relative(direction);
                            BlockState blockState = this.level.getBlockState(blockPos);
                            if (blockState.isAir() || blockState.canBeReplaced(Fluids.FLOWING_WATER)) {
                                if (this.level.getEntitiesOfClass(BrewGas.class, new AABB(blockPos)).isEmpty()) {
                                    BrewGas brewGas = new BrewGas(ModEntityType.BREW_EFFECT_GAS.get(), this.level);
                                    brewGas.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                    brewGas.setGas(this.effects, this.brewEffects, this.duration, this.area - 1, this.owner != null ? this.owner : null);
                                    if (this.level.addFreshEntity(brewGas)) {
                                        expanded = true;
                                    }
                                } else {
                                    expanded = true;
                                }
                            }
                        }
                    }
                    if (expanded) {
                        --this.area;
                    }
                }
            } else if (this.tickCount >= this.duration || this.level.random.nextInt(i) == 0) {
                this.discard();
            }
            if (!this.effects.isEmpty() || !this.brewEffects.isEmpty()) {
                List<LivingEntity> list1 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
                if (!list1.isEmpty()) {
                    for(LivingEntity livingentity : list1) {
                        if (livingentity.isAffectedByPotions() && !livingentity.getTags().contains(ConstantPaths.gassed())) {
                            for(MobEffectInstance mobEffectInstance : this.effects) {
                                if (mobEffectInstance.getEffect().isInstantenous()) {
                                    mobEffectInstance.getEffect().applyInstantenousEffect(this, this.getOwner(), livingentity, mobEffectInstance.getAmplifier(), 0.5D);
                                } else {
                                    livingentity.addEffect(new MobEffectInstance(mobEffectInstance), this);
                                }
                            }

                            for(BrewEffectInstance brewEffectInstance : this.brewEffects) {
                                if (brewEffectInstance.getEffect().canLinger()) {
                                    brewEffectInstance.getEffect().applyInstantenousEffect(this, this.getOwner(), livingentity, brewEffectInstance.getAmplifier(), 0.5D);
                                }
                            }
                            livingentity.addTag(ConstantPaths.gassed());
                        }
                    }
                }
            }
        }
    }

    private void updateColor() {
        this.getEntityData().set(DATA_COLOR, BrewUtils.getColor(effects, brewEffects));
    }

    public void addEffect(MobEffectInstance p_19717_) {
        this.effects.add(p_19717_);
        if (!this.fixedColor) {
            this.updateColor();
        }

    }

    public void addBrewEffect(BrewEffectInstance p_19717_) {
        this.brewEffects.add(p_19717_);
        if (!this.fixedColor) {
            this.updateColor();
        }
    }

    public int getColor() {
        return this.getEntityData().get(DATA_COLOR);
    }

    public void setFixedColor(int p_19715_) {
        this.fixedColor = true;
        this.getEntityData().set(DATA_COLOR, p_19715_);
    }

    public void setOwner(@Nullable LivingEntity p_19719_) {
        this.owner = p_19719_;
        this.ownerUUID = p_19719_ == null ? null : p_19719_.getUUID();
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

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
