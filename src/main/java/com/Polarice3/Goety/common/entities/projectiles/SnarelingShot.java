package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SnarelingShot extends ThrowableProjectile {
    private final Vec3[] trailPositions = new Vec3[64];
    private int trailPointer = -1;

    public SnarelingShot(EntityType<? extends ThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public SnarelingShot(double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(ModEntityType.SNARELING_SHOT.get(), p_37457_, p_37458_, p_37459_, p_37460_);
    }

    public SnarelingShot(LivingEntity p_37463_, Level p_37464_) {
        super(ModEntityType.SNARELING_SHOT.get(), p_37463_, p_37464_);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        ColorUtil colorUtil = new ColorUtil(0xdef44a);
        for (int i = 0; i < 2; ++i) {
            this.level.addParticle(ModParticleTypes.GOO_STAIN.get(), d0 + (this.random.nextGaussian() / 2), d1 + 0.5D + (this.random.nextGaussian() / 2), d2 + (this.random.nextGaussian() / 2), colorUtil.red(), colorUtil.green(), colorUtil.blue());
        }
        Vec3 trailAt = this.position().add(0, this.getBbHeight() / 2F, 0);
        if (this.trailPointer == -1) {
            Arrays.fill(trailPositions, trailAt);
        }
        if (++this.trailPointer == this.trailPositions.length) {
            this.trailPointer = 0;
        }
        this.trailPositions[this.trailPointer] = trailAt;
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level.isClientSide) {
            Vec3 vec3 = this.position();
            if (pResult instanceof EntityHitResult entityHitResult){
                vec3 = entityHitResult.getEntity().position();
            }
            if (this.level instanceof ServerLevel serverLevel) {
                for (int i = 0; i <= 16; ++i) {
                    ColorUtil colorUtil = new ColorUtil(0xdef44a);
                    serverLevel.sendParticles(ModParticleTypes.GOO_STAIN.get(), this.getRandomX(0.5D), this.getY(), this.getRandomZ(0.5D), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
                }
            }
            SnarelingGoop goop = new SnarelingGoop(ModEntityType.SNARELING_GOOP.get(), this.level);
            if (this.getOwner() instanceof LivingEntity livingEntity) {
                goop.setOwner(livingEntity);
            }
            goop.setLifeSpan(MathHelper.secondsToTicks(3));
            goop.setPos(vec3);
            this.level.addFreshEntity(goop);
            this.discard();
        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity);
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
        return super.canHitEntity(pEntity);
    }

    /**
     * Ripped Trail effect from @AlexModGuy: <a href="https://github.com/AlexModGuy/AlexsCaves/blob/main/src/main/java/com/github/alexmodguy/alexscaves/server/entity/item/WaterBoltEntity.java">...</a>
     */
    public Vec3 getTrailPosition(int pointer, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        Vec3 d0 = this.trailPositions[j];
        Vec3 d1 = this.trailPositions[i].subtract(d0);
        return d0.add(d1.scale(partialTick));
    }

    public boolean hasTrail() {
        return this.trailPointer != -1;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void makeStuckInBlock(BlockState p_33796_, Vec3 p_33797_) {
        if (!p_33796_.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(p_33796_, p_33797_);
        }

    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
