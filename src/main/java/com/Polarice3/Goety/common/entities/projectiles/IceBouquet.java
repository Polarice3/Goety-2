package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.render.IceBouquetTextures;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.IOwned;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class IceBouquet extends GroundProjectile {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(IceBouquet.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SOUL_EATING = SynchedEntityData.defineId(IceBouquet.class, EntityDataSerializers.BOOLEAN);

    public IceBouquet(EntityType<? extends Entity> p_i50170_1_, Level p_i50170_2_) {
        super(p_i50170_1_, p_i50170_2_);
        this.setYRot(0.0F);
        this.lifeTicks = 80;
    }

    public IceBouquet(Level world, double pPosX, double pPosY, double pPosZ, @Nullable LivingEntity owner) {
        this(ModEntityType.ICE_BOUQUET.get(), world);
        this.setOwner(owner);
        this.setPos(pPosX, pPosY, pPosZ);
    }

    public IceBouquet(Level world, BlockPos blockPos, @Nullable LivingEntity owner) {
        this(ModEntityType.ICE_BOUQUET.get(), world);
        this.setOwner(owner);
        this.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public IceBouquet(Level world, Vec3 vector3d, @Nullable LivingEntity owner) {
        this(ModEntityType.ICE_BOUQUET.get(), world);
        this.setOwner(owner);
        this.setPos(vector3d.x(), vector3d.y(), vector3d.z());
    }

    public ResourceLocation getResourceLocation() {
        return IceBouquetTextures.TEXTURES.getOrDefault(this.getAnimation(), IceBouquetTextures.TEXTURES.get(0));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 0);
        this.entityData.define(SOUL_EATING, false);
    }

    public int getAnimation() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setAnimation(int pType) {
        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public boolean isSoulEating(){
        return this.entityData.get(SOUL_EATING);
    }

    public void setSoulEating(boolean soulEating){
        this.entityData.set(SOUL_EATING, soulEating);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setAnimation(pCompound.getInt("Animation"));
        this.setSoulEating(pCompound.getBoolean("soulEating"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Animation", this.getAnimation());
        pCompound.putBoolean("soulEating", this.isSoulEating());
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.sentTrapEvent) {
                if (this.animationTicks > 9){
                    --this.animationTicks;
                }
                if (this.getAnimation() < 44){
                    this.setAnimation(this.getAnimation() + 1);
                } else {
                    this.setAnimation(13);
                }
                --this.lifeTicks;
                if (this.tickCount >= 10) {
                    for(int i = 0; i < 3; ++i) {
                        this.level.addParticle(ParticleTypes.SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
                    }
                    if (this.level.random.nextInt(24) == 0) {
                        this.level.playLocalSound((double)this.blockPosition().getX() + 0.5D, (double)this.blockPosition().getY() + 0.5D, (double)this.blockPosition().getZ() + 0.5D, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + this.level.random.nextFloat(), this.level.random.nextFloat() * 0.7F + 0.3F, false);
                    }
                }
            }
        } else {
            if (!this.isNoGravity()) {
                MobUtil.moveDownToGround(this);
            }
            if (!this.sentTrapEvent) {
                this.level.broadcastEntityEvent(this, (byte)4);
                this.sentTrapEvent = true;
            }

            if (!this.playSound) {
                this.level.broadcastEntityEvent(this, (byte) 5);
                this.playSound = true;
            }

            if (this.tickCount >= 12){
                for(LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
                    this.dealDamageTo(livingentity);
                }
            }

            if (this.tickCount > 0) {
                if (--this.lifeTicks < 0) {
                    this.discard();
                }
            }

            if (this.getOwner() != null){
                if (this.getOwner() instanceof Mob){
                    if (this.getOwner().hurtTime > 0 && this.tickCount < 10 && !this.getOwner().isDeadOrDying()){
                        this.lifeTicks = 14;
                    }
                }
            }

            if (this.isInLava() || this.isInWaterOrBubble()){
                this.level.broadcastEntityEvent(this, (byte)6);
                this.discard();
            }
        }

    }

    public void dealDamageTo(LivingEntity target) {
        LivingEntity owner = this.getOwner();
        float damage = 4.0F;
        if (this.tickCount >= 14){
            damage = 2.0F;
        }
        if (target.isAlive() && !target.isInvulnerable() && target != owner) {
            if (target.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)){
                return;
            }
            if (owner == null) {
                boolean flag = target.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES);
                if (flag){
                    damage *= 2.0F;
                }
                int i = target.getTicksFrozen();
                target.setTicksFrozen(Math.min(target.getTicksRequiredToFreeze(), i + 1));
                target.hurt(DamageSource.FREEZE, damage);
            } else {
                if (owner instanceof Mob mobOwner) {
                    if (mobOwner instanceof Enemy && target instanceof Enemy) {
                        if (mobOwner.getTarget() != target) {
                            return;
                        }
                    }
                    if (mobOwner instanceof IOwned owned){
                        if (owned.getTrueOwner() != null){
                            if (target.isAlliedTo(owned.getTrueOwner()) || owned.getTrueOwner().isAlliedTo(target) || target == owned.getTrueOwner()){
                                return;
                            }
                        }
                    }
                } else {
                    if (target.isAlliedTo(owner)){
                        return;
                    }
                    if (owner.isAlliedTo(target)) {
                        return;
                    }
                }
                if (owner instanceof Player player){
                    if (WandUtil.enchantedFocus(player)) {
                        damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
                    }
                } else {
                    if (owner.getAttribute(Attributes.ATTACK_DAMAGE) != null){
                        damage = (float) owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                        if (this.tickCount >= 14){
                            damage /= 2.0F;
                        }
                    }
                }
                if (target.hurt(ModDamageSource.iceBouquet(this, owner), damage)){
                    if (owner instanceof Player) {
                        if (this.isSoulEating()) {
                            SEHelper.increaseSouls((Player) owner, 1);
                        }
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte pId) {
        super.handleEntityEvent(pId);
        if (pId == 5) {
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.WRAITH_FIRE.get(), this.getSoundSource(), 1.0F, 1.0F, false);
            }
        }
        if (pId == 6){
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.FIRE_EXTINGUISH, this.getSoundSource(), 1.0F, 1.0F, false);
            }
        }

    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
