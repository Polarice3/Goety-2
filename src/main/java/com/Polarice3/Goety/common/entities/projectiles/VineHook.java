package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.magic.spells.wild.GrappleSpell;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * Potato, Poisonous even. Based on Lashing Potato codes from the 24w14potato snapshot.
 */
public class VineHook extends Projectile {
    public static final EntityDataAccessor<Boolean> ATTACHED = SynchedEntityData.defineId(VineHook.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> LENGTH = SynchedEntityData.defineId(VineHook.class, EntityDataSerializers.FLOAT);
    public boolean staff = false;

    public VineHook(EntityType<? extends VineHook> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
    }

    public VineHook(Level level, Player player, float speed) {
        this(ModEntityType.VINE_HOOK.get(), level);
        this.setOwner(player);
        this.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
        this.setDeltaMovement(player.getViewVector(1.0F).scale(speed));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACHED, false);
        this.entityData.define(LENGTH, 0.0F);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        Player player = this.getPlayerOwner();
        if (!this.isNoGravity() && !this.isAttached()) {
            Vec3 vec31 = this.getDeltaMovement();
            this.setDeltaMovement(vec31.x, vec31.y - (double)this.getGravity(), vec31.z);
        }
        if (player != null && (this.level.isClientSide() || !this.shouldRetract(player))) {
            HitResult hitResult = ProjectileUtil.getHitResult(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onHit(hitResult);
            }

            this.setPos(hitResult.getLocation());
            this.checkInsideBlocks();
        } else {
            this.discard();
        }
    }

    protected float getGravity() {
        return 0.03F;
    }

    private boolean shouldRetract(Player player) {
        if (!player.isRemoved()
                && player.isAlive()
                && WandUtil.getSpell(player) instanceof GrappleSpell
                && this.distanceToSqr(player) <= Mth.square(64)) {
            return false;
        } else {
            this.discard();
            return true;
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return false;
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        this.setDeltaMovement(Vec3.ZERO);
        this.setAttached(true);
        Player player = this.getPlayerOwner();
        if (player != null) {
            double distance = player.getEyePosition().subtract(blockHitResult.getLocation()).length();
            this.setLength(Math.max((float)distance * 0.5F - 3.0F, 1.5F));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putBoolean("Attached", this.isAttached());
        compoundTag.putFloat("Length", this.getLength());
        compoundTag.putBoolean("Staff", this.isStaff());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        this.setAttached(compoundTag.getBoolean("Attached"));
        this.setLength(compoundTag.getFloat("Length"));
        if (compoundTag.contains("Staff")) {
            this.staff = compoundTag.getBoolean("Staff");
        }
    }

    public void setStaff(boolean staff){
        this.staff = staff;
    }

    public boolean isStaff(){
        return this.staff;
    }

    private void setAttached(boolean attached) {
        this.getEntityData().set(ATTACHED, attached);
    }

    private void setLength(float length) {
        this.getEntityData().set(LENGTH, length);
    }

    public boolean isAttached() {
        return this.getEntityData().get(ATTACHED);
    }

    public float getLength() {
        return this.getEntityData().get(LENGTH);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        this.updateOwnerInfo(null);
        super.remove(removalReason);
    }

    @Override
    public void onClientRemoval() {
        this.updateOwnerInfo(null);
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        super.setOwner(entity);
        this.updateOwnerInfo(this);
    }

    private void updateOwnerInfo(@Nullable VineHook vineHook) {
        Player player = this.getPlayerOwner();
        if (player != null) {
            SEHelper.setGrappling(player, vineHook);
        }
    }

    @Nullable
    public Player getPlayerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof Player ? (Player)entity : null;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? this.getId() : entity.getId());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket clientboundAddEntityPacket) {
        super.recreateFromPacket(clientboundAddEntityPacket);
        if (this.getPlayerOwner() == null) {
            this.kill();
        }
    }
}
