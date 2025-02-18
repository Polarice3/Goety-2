package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
        if (!this.level.isClientSide) {
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
