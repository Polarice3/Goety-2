package com.Polarice3.Goety.common.entities.vehicle;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.equipment.HauntedBroomItem;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.CBroomCollisionPacket;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class HauntedBroom extends Entity implements OwnableEntity {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(HauntedBroom.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> OWNER_CLIENT_ID = SynchedEntityData.defineId(HauntedBroom.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(HauntedBroom.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> HURT_TIME = SynchedEntityData.defineId(HauntedBroom.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HURT_DIR = SynchedEntityData.defineId(HauntedBroom.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(HauntedBroom.class, EntityDataSerializers.FLOAT);
    private static final double BASE_SPEED_MULTIPLIER = 0.07D;
    private static final double MAX_ACCELERATION = 0.35D;
    private static final double ACCEL_FACTOR = MAX_ACCELERATION * 100.0D;
    private static final double SPEED_LIMIT = 0.9D;
    private static final double DRAG = 0.99D;
    private static final double GRAVITY = 0.03D;
    public Vec3 prevLoc = Vec3.ZERO;
    public double speedMultiplier = BASE_SPEED_MULTIPLIER;
    public float damageThreshold = 40.0F;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    public HauntedBroom(EntityType<? extends HauntedBroom> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    public HauntedBroom(ItemStack itemStack, Level level, double x, double y, double z) {
        this(ModEntityType.HAUNTED_BROOM.get(), level);
        this.setItem(itemStack.copy());
        this.setPos(x, y, z);
        this.prevLoc = new Vec3(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
        this.entityData.define(OWNER_CLIENT_ID, -1);
        this.entityData.define(ITEM, ItemStack.EMPTY);
        this.entityData.define(HURT_TIME, 0);
        this.entityData.define(HURT_DIR, 1);
        this.entityData.define(DAMAGE, 0.0F);
    }

    @Nullable
    public LivingEntity getOwner() {
        if (!this.level.isClientSide){
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        } else {
            int id = this.getOwnerClientId();
            return id <= -1 ? null : this.level.getEntity(this.getOwnerClientId()) instanceof LivingEntity living ? living : null;
        }
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.getOwnerId();
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public int getOwnerClientId(){
        return this.entityData.get(OWNER_CLIENT_ID);
    }

    public void setOwnerClientId(int id){
        this.entityData.set(OWNER_CLIENT_ID, id);
    }

    public void setOwner(LivingEntity livingEntity){
        if (livingEntity != null) {
            this.setOwnerId(livingEntity.getUUID());
            this.setOwnerClientId(livingEntity.getId());
        }
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.1D;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.level.isClientSide && !this.isRemoved()) {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.setDamage(this.getDamage() + amount * 10.0F);
            this.markHurt();
            this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
            boolean flag = source.getEntity() instanceof Player player && player.getAbilities().instabuild;
            if (flag || this.getDamage() > this.getDamageThreshold()) {
                this.playSound(ModSounds.BROOM_BREAK.get(), 0.8F, 1.0F);
                if (!flag && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.spawnBroomItem();
                }
                this.discard();
            } else {
                this.playSound(ModSounds.BROOM_SWING.get(), 0.8F, 1.0F);
            }
            return true;
        } else {
            return true;
        }
    }

    @Override
    public void push(Entity entity) {
        if (entity instanceof HauntedBroom) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(entity);
        }
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps, boolean flag) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYRot = yRot;
        this.lerpXRot = xRot;
        this.lerpSteps = 10;
    }

    @Override
    public Direction getMotionDirection() {
        return this.getDirection().getClockWise();
    }

    @Override
    protected void playStepSound(final BlockPos pos, final BlockState blockState) {
    }

    @Override
    protected @NotNull MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public void tick() {
        super.tick();
        this.tickLerp();

        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        if (this.getOwner() != null) {
            this.ownerCheck();
        }

        if (this.isVehicle() && this.getControllingPassenger() != null) {
            this.setYRot(this.getControllingPassenger().getYRot());
            if (!this.level.isClientSide) {
                if (this.getControllingPassenger() instanceof Player player) {
                    if (ItemConfig.HauntedBroomSoulDistance.get() > 0) {
                        if (this.prevLoc.distanceTo(this.position()) >= Mth.square(ItemConfig.HauntedBroomSoulDistance.get())) {
                            this.prevLoc = this.position();
                            if (SEHelper.getSoulsAmount(player, ItemConfig.HauntedBroomSouls.get())) {
                                SEHelper.decreaseSouls(player, ItemConfig.HauntedBroomSouls.get());
                            } else {
                                SEHelper.addCooldown(player, this.getItem().getItem(), MathHelper.minutesToTicks(1));
                                this.hurt(this.damageSources().dryOut(), 100.0F);
                            }
                        }
                    }
                }
            }
        }

        if (this.isControlledByLocalInstance()) {
            this.handleInputs();
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double stepX = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double stepY = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double stepZ = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            float stepYRot = (float) (this.getYRot() + Mth.wrapDegrees(this.lerpYRot - this.getYRot()) / this.lerpSteps);
            float stepXRot = this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps;

            this.setYRot(stepYRot);
            this.setXRot(stepXRot);
            --this.lerpSteps;
            this.setPos(stepX, stepY, stepZ);
            this.setRot(this.getYRot(), this.getXRot());
        }
    }

    private void handleInputs() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        double distance = this.getDeltaMovement().horizontalDistance();

        Vec3 vec3 = this.getDeltaMovement();
        double dX = vec3.x;
        double dY = vec3.y;
        double dZ = vec3.z;

        LivingEntity rider = this.getControllingPassenger();
        if (rider != null) {
            boolean goingDown = rider.hasEffect(GoetyEffects.PLUNGE.get()) || MobUtil.starAmuletActive(rider);
            float forward = rider.zza;
            if (forward > 0.0F) {
                double riderDirection = rider.getYRot() * Math.PI / 180.0D;
                double multiplier = this.speedMultiplier * (0.1D + this.getSpeedBoost());
                dX += -Math.sin(riderDirection) * multiplier;
                dZ +=  Math.cos(riderDirection) * multiplier;

                double riderView = -Math.sin(rider.getXRot() * Math.PI / 180.0D);
                if (riderView > -0.5D && riderView < 0.2D) {
                    riderView = 0.0D;
                } else if (riderView < 0.0D) {
                    riderView *= 0.5D;
                }
                if (!goingDown) {
                    dY = riderView * this.speedMultiplier * 2.0D;
                }
            }
            if (goingDown) {
                dY = -0.2D;
            }
        } else {
            if (!this.onGround()) {
                dY = -GRAVITY;
            }
        }

        double horizontal = Math.sqrt(dX * dX + dZ * dZ);
        double speedLimit = SPEED_LIMIT + (this.getSpeedBoost() * 3);
        if (horizontal > speedLimit) {
            double speed = speedLimit / horizontal;
            dX *= speed;
            dY *= speed;
            dZ *= speed;
            horizontal = speedLimit;
        }

        if (horizontal > distance && this.speedMultiplier < MAX_ACCELERATION) {
            this.speedMultiplier += (MAX_ACCELERATION - this.speedMultiplier) / ACCEL_FACTOR;
            if (this.speedMultiplier > MAX_ACCELERATION) {
                this.speedMultiplier = MAX_ACCELERATION;
            }
        } else {
            this.speedMultiplier -= (this.speedMultiplier - BASE_SPEED_MULTIPLIER) / ACCEL_FACTOR;
            if (this.speedMultiplier < BASE_SPEED_MULTIPLIER) {
                this.speedMultiplier = BASE_SPEED_MULTIPLIER;
            }
        }

        this.setDeltaMovement(dX, dY, dZ);
        double speedBeforeImpact = this.getDeltaMovement().horizontalDistance();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.checkCollisionDamage(speedBeforeImpact);
        this.setDeltaMovement(this.getDeltaMovement().multiply(DRAG, DRAG, DRAG));
    }

    public void applyCollisionDamage(double speedBeforeImpact) {
        if (!this.level.isClientSide) {
            double d11 = this.getDeltaMovement().horizontalDistance();
            double d7 = speedBeforeImpact - d11;
            float f1 = (float)(d7 * 10.0D - 3.0D);
            if (f1 > 0.0F) {
                DamageSource damageSource = this.damageSources().flyIntoWall();

                LivingEntity rider = this.getControllingPassenger();
                if (rider != null) {
                    rider.hurt(damageSource, f1);
                }
                float threshold = this.getDamageThreshold() / 10.0F;
                if (f1 > threshold) {
                    this.hurt(damageSource, f1);
                    if (rider instanceof Player player) {
                        player.getCooldowns().addCooldown(this.getItem().getItem(), 50);
                    }
                    if (this.isBurning()) {
                        float explodeSize = f1 / threshold;
                        ExplosionUtil.lootExplode(this.level, this, this.getX(), this.getY(), this.getZ(), explodeSize, true, Explosion.BlockInteraction.DESTROY, LootingExplosion.Mode.LOOT);
                    }
                }
            }
        }
    }

    private void checkCollisionDamage(double speedBeforeImpact) {
        if (this.horizontalCollision) {
            if (this.level.isClientSide) {
                ModNetwork.sendToServer(new CBroomCollisionPacket(this.getId(), speedBeforeImpact));
            } else {
                this.applyCollisionDamage(speedBeforeImpact);
            }
        }
    }

    @Override
    public void positionRider(Entity rider, MoveFunction p_19958_) {
        super.positionRider(rider, p_19958_);
        if (rider instanceof Player player) {
            player.setYBodyRot(player.getYHeadRot());
        }
    }

    public void ownerCheck(){
        if (!this.level.isClientSide) {
            if (this.getOwner() != null) {
                if (this.getOwner().tickCount < 20) {
                    Entity entity = this.level.getEntity(this.getOwnerClientId());
                    if (entity instanceof LivingEntity livingEntity) {
                        if (livingEntity != this.getOwner()) {
                            this.setOwnerClientId(this.getOwner().getId());
                        }
                    } else {
                        this.setOwnerClientId(this.getOwner().getId());
                    }
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        if (this.getOwnerId() != null) {
            compoundTag.putUUID("Owner", this.getOwnerId());
        }
        if (this.getOwnerClientId() > -1) {
            compoundTag.putInt("OwnerClient", this.getOwnerClientId());
        }
        compoundTag.putFloat("DamageThreshold", this.getDamageThreshold());
        compoundTag.put("Item", this.getItem().save(new CompoundTag()));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.contains("Owner")) {
            this.setOwnerId(compoundTag.getUUID("Owner"));
        }

        if (compoundTag.contains("OwnerClient")){
            this.setOwnerClientId(compoundTag.getInt("OwnerClient"));
        }

        if (compoundTag.contains("DamageThreshold")){
            this.setDamageThreshold(compoundTag.getFloat("DamageThreshold"));
        }
        CompoundTag itemTag = compoundTag.getCompound("Item");
        this.setItem(ItemStack.of(itemTag));
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        if (pPlayer.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else if (this.getOwner() == null || this.getOwner() == pPlayer) {
            if (!this.level.isClientSide) {
                if (ItemConfig.HauntedBroomSoulDistance.get() > 0 && !SEHelper.getSoulsAmount(pPlayer, ItemConfig.HauntedBroomSouls.get())) {
                    pPlayer.displayClientMessage(Component.translatable("info.goety.broom.noSouls.ride"), true);
                    pPlayer.level.playSound(pPlayer, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    return InteractionResult.PASS;
                }
                return pPlayer.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            } else {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void checkFallDamage(double p_19911_, boolean p_19912_, BlockState p_19913_, BlockPos p_19914_) {
        this.fallDistance = 0.0F;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity;
        }
        return null;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float) this.lerpYRot, (float) this.lerpXRot);
        }
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.HAUNTED_BROOM.get());
    }

    public void spawnBroomItem() {
        if (!this.getItem().isEmpty()) {
            if (this.getOwner() instanceof Player player) {
                ItemStack itemStack = this.getItem();
                HauntedBroomItem.setOwner(player, itemStack);
                if (!player.getInventory().add(itemStack)) {
                    player.drop(itemStack, false, true);
                } else {
                    if (!this.level.isClientSide){
                        ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ITEM_PICKUP, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F));
                    }
                }
            } else {
                this.spawnAtLocation(this.getItem());
            }
        } else {
            this.spawnAtLocation(new ItemStack(ModItems.HAUNTED_BROOM.get()));
        }
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float p_38312_) {
        this.entityData.set(DAMAGE, p_38312_);
    }

    public int getHurtTime() {
        return this.entityData.get(HURT_TIME);
    }

    public void setHurtTime(int p_38355_) {
        this.entityData.set(HURT_TIME, p_38355_);
    }

    public int getHurtDir() {
        return this.entityData.get(HURT_DIR);
    }

    public void setHurtDir(int p_38363_) {
        this.entityData.set(HURT_DIR, p_38363_);
    }

    public ItemStack getItem() {
        return this.entityData.get(ITEM);
    }

    public void setItem(ItemStack itemStack) {
        this.entityData.set(ITEM, itemStack);
    }

    public float getDamageThreshold() {
        return this.damageThreshold;
    }

    public void setDamageThreshold(float damageThreshold) {
        this.damageThreshold = damageThreshold;
    }

    public boolean isBurning() {
        return this.getItem().getEnchantmentLevel(ModEnchantments.BURNING.get()) > 0;
    }

    public double getSpeedBoost() {
        return this.getItem().getEnchantmentLevel(ModEnchantments.VELOCITY.get()) / 10.0F;
    }
}
