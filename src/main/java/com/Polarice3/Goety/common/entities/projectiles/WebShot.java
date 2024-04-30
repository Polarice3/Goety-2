package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class WebShot extends ThrowableProjectile {

    public WebShot(EntityType<? extends ThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public WebShot(double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(ModEntityType.WEB_SHOT.get(), p_37457_, p_37458_, p_37459_, p_37460_);
    }

    public WebShot(LivingEntity p_37463_, Level p_37464_) {
        super(ModEntityType.WEB_SHOT.get(), p_37463_, p_37464_);
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        Vec3 vec3 = this.position();
        if (pResult instanceof EntityHitResult entityHitResult){
            vec3 = entityHitResult.getEntity().position();
        }
        SpiderWeb spiderWeb = new SpiderWeb(ModEntityType.SPIDER_WEB.get(), this.level);
        if (this.getOwner() instanceof LivingEntity livingEntity) {
            spiderWeb.setOwner(livingEntity);
        }
        spiderWeb.setLifeSpan(MathHelper.secondsToTicks(3));
        spiderWeb.setPos(vec3);
        this.level.addFreshEntity(spiderWeb);
        this.discard();
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (pEntity == this.getOwner()){
                return false;
            } else if (this.getOwner().isPassengerOfSameVehicle(pEntity)){
                return false;
            } else if (pEntity instanceof Projectile projectile && projectile.getOwner() == this.getOwner()){
                return false;
            } else if (this.getOwner() instanceof IOwned owned){
                if (pEntity instanceof IOwned owned1){
                    if (owned.getTrueOwner() == owned1.getTrueOwner()){
                        return false;
                    }
                } else if (owned.getTrueOwner() == pEntity){
                    return false;
                }
            }
        }
        return (!pEntity.isSpectator() && pEntity.isAlive() && pEntity.isPickable()) || this.getOwner() == null;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
