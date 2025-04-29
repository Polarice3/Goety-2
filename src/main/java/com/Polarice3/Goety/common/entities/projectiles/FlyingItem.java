package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FlyingItem extends SpellEntity implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(FlyingItem.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE = SynchedEntityData.defineId(FlyingItem.class, EntityDataSerializers.PARTICLE);
    private int secondsCool;
    private int life;

    public FlyingItem(EntityType<? extends FlyingItem> p_36957_, Level p_36958_) {
        super(p_36957_, p_36958_);
    }

    public FlyingItem(EntityType<? extends FlyingItem> p_36957_, Level p_36960_, double p_36961_, double p_36962_, double p_36963_) {
        this(p_36957_, p_36960_);
        this.setPos(p_36961_, p_36962_, p_36963_);
    }

    @Override
    public Component getName() {
        if (this.getItem().isEmpty()){
            return super.getName();
        } else {
            return this.getItem().getHoverName();
        }
    }

    public void setItem(ItemStack p_36973_) {
        this.getEntityData().set(DATA_ITEM_STACK, p_36973_.copyWithCount(1));
    }

    private ItemStack getItemRaw() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(Items.ENDER_EYE) : itemstack;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
        this.getEntityData().define(DATA_PARTICLE, ParticleTypes.PORTAL);
    }

    public ParticleOptions getParticle() {
        return this.getEntityData().get(DATA_PARTICLE);
    }

    public void setParticle(ParticleOptions p_19725_) {
        this.getEntityData().set(DATA_PARTICLE, p_19725_);
    }

    public int getSecondsCool(){
        return this.secondsCool;
    }

    public void setSecondsCool(int cool){
        this.secondsCool = cool;
    }

    public boolean shouldRenderAtSqrDistance(double p_36966_) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 *= 64.0D;
        return p_36966_ < d0 * d0;
    }

    public void tick() {
        super.tick();
        this.noPhysics = true;
        if (this.getItem().isEmpty()){
            this.discard();
        }
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        double d3 = vec3.horizontalDistance();
        this.setXRot(lerpRotation(this.xRotO, (float)(Mth.atan2(vec3.y, d3) * (double)(180F / (float)Math.PI))));
        this.setYRot(lerpRotation(this.yRotO, (float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI))));
        if (this.getOwner() != null){
            double d4 = this.getOwner().getX() - this.getX();
            double d5 = this.getOwner().getZ() - this.getZ();
            this.setYRot(-((float) Mth.atan2(d4, d5)) * (180F / (float) Math.PI));
        }
        if (!this.level.isClientSide) {
            ++this.life;
            if (this.life < 80){
                this.setDeltaMovement(0.0D, 0.05D, 0.0D);
            } else if (this.life > 100) {
                if (this.getOwner() != null && this.getOwner().level.dimension() == this.level.dimension()) {
                    if (this.getOwner().distanceTo(this) >= 64){
                        this.teleportTowards(this.getOwner());
                    } else {
                        Vec3 vector3d = this.getOwner().getEyePosition();
                        Vec3 vector3d1 = this.position();
                        Vec3 vector3d2 = vector3d.subtract(vector3d1);
                        if (vector3d2.lengthSqr() > 1) {
                            vector3d2.normalize();
                        }
                        double speed = 0.1D;
                        Vec3 motion = new Vec3(vector3d2.x * speed, vector3d2.y * speed, vector3d2.z * speed);
                        this.setDeltaMovement(motion);
                        if (this.getOwner().getBoundingBox().inflate(1.0F).intersects(this.getBoundingBox())){
                            if (this.getOwner() instanceof Player player){
                                if (this.getSecondsCool() > 0){
                                    SEHelper.addCooldown(player, this.getItem().getItem(), MathHelper.secondsToTicks(this.getSecondsCool()));
                                }
                                if (!player.getInventory().add(this.getItem())) {
                                    player.drop(this.getItem(), false, true);
                                } else {
                                    if (!this.level.isClientSide){
                                        ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ITEM_PICKUP, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F));
                                    }
                                }
                            } else {
                                this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), this.getItem()));
                            }
                            this.discard();
                        }
                    }
                } else {
                    if (this.getOwner() instanceof Player player){
                        if (this.getSecondsCool() > 0){
                            SEHelper.addCooldown(player, this.getItem().getItem(), MathHelper.secondsToTicks(this.getSecondsCool()));
                        }
                        if (!player.getInventory().add(this.getItem())) {
                            player.drop(this.getItem(), false, true);
                        } else {
                            if (!this.level.isClientSide){
                                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ITEM_PICKUP, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F));
                            }
                        }
                    } else {
                        this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), this.getItem()));
                    }
                    this.discard();
                }
            }
        }

        if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
                this.level.addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25D, d1 - vec3.y * 0.25D, d2 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
            }
        } else {
            this.level.addParticle(this.getParticle(), d0 - vec3.x * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, d1 - vec3.y * 0.25D, d2 - vec3.z * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, vec3.x, vec3.y, vec3.z);
        }

        if (!this.level.isClientSide) {
            this.setPos(d0, d1, d2);
        } else {
            this.setPosRaw(d0, d1, d2);
        }

    }

    public void teleportTowards(Entity target) {
        Vec3 vec3 = new Vec3(this.getX() - target.getX(), this.getY(0.5D) - target.getEyeY(), this.getZ() - target.getZ());
        vec3 = vec3.normalize();
        double d0 = 16.0D;
        double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.x * 16.0D;
        double d2 = this.getY() + (double)(this.random.nextInt(16) - 8) - vec3.y * 16.0D;
        double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.z * 16.0D;
        this.teleportTo(d1, d2, d3);
    }

    private void drawParticleBeam(LivingEntity pSource) {
        double d0 = this.getX() - pSource.getX();
        double d1 = (this.getY() + (double) this.getBbHeight() * 0.5F) - (pSource.getY() + (double) pSource.getBbHeight() * 0.5D);
        double d2 = this.getZ() - pSource.getZ();
        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        d0 = d0 / d3;
        d1 = d1 / d3;
        d2 = d2 / d3;
        double d4 = pSource.level.random.nextDouble();
        if (pSource.level instanceof ServerLevel serverWorld) {
            while (d4 < d3) {
                d4 += 1.0D;
                serverWorld.sendParticles(ParticleTypes.ELECTRIC_SPARK, pSource.getX() + d0 * d4, pSource.getY() + d1 * d4 + (double) pSource.getEyeHeight() * 0.5D, pSource.getZ() + d2 * d4, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void lerpMotion(double p_36984_, double p_36985_, double p_36986_) {
        this.setDeltaMovement(p_36984_, p_36985_, p_36986_);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = Math.sqrt(p_36984_ * p_36984_ + p_36986_ * p_36986_);
            this.setYRot((float)(Mth.atan2(p_36984_, p_36986_) * (double)(180F / (float)Math.PI)));
            this.setXRot((float)(Mth.atan2(p_36985_, d0) * (double)(180F / (float)Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

    }

    public void addAdditionalSaveData(CompoundTag p_36975_) {
        super.addAdditionalSaveData(p_36975_);
        p_36975_.putString("Particle", this.getParticle().writeToString());
        ItemStack itemstack = this.getItemRaw();
        if (!itemstack.isEmpty()) {
            p_36975_.put("Item", itemstack.save(new CompoundTag()));
        }
        p_36975_.putInt("Life", this.life);
        p_36975_.putInt("Cool", this.secondsCool);
    }

    public void readAdditionalSaveData(CompoundTag p_36970_) {
        super.readAdditionalSaveData(p_36970_);
        if (p_36970_.contains("Particle", 8)) {
            try {
                this.setParticle(ParticleArgument.readParticle(new StringReader(p_36970_.getString("Particle")), BuiltInRegistries.PARTICLE_TYPE.asLookup()));
            } catch (CommandSyntaxException ignored) {
            }
        }
        ItemStack itemstack = ItemStack.of(p_36970_.getCompound("Item"));
        this.setItem(itemstack);
        if (p_36970_.contains("Life")){
            this.life = p_36970_.getInt("Life");
        }
        if (p_36970_.contains("Cool")){
            this.secondsCool = p_36970_.getInt("Cool");
        }
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public boolean isAttackable() {
        return false;
    }

    protected static float lerpRotation(float p_37274_, float p_37275_) {
        while(p_37275_ - p_37274_ < -180.0F) {
            p_37274_ -= 360.0F;
        }

        while(p_37275_ - p_37274_ >= 180.0F) {
            p_37274_ += 360.0F;
        }

        return Mth.lerp(0.2F, p_37274_, p_37275_);
    }
}
