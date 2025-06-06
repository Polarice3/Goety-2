package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {
    public AbstractArrowMixin(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    @Inject(
            method = {"canHitEntity(Lnet/minecraft/world/entity/Entity;)Z"},
            at = @At(value = "HEAD"),
            cancellable = true
    )
    protected void canHitEntity(Entity pEntity, CallbackInfoReturnable<Boolean> callback) {
        if (this.getOwner() instanceof AbstractIllagerServant
                && MobsConfig.IllagerServantGhostArrows.get()) {
            if (pEntity == this.getOwner()){
                callback.setReturnValue(false);
            }
            if (!(this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity)){
                if (MobUtil.areAllies(this.getOwner(), pEntity)){
                    callback.setReturnValue(false);
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    callback.setReturnValue(!MobUtil.ownerStack(owned0, owned1));
                }
            }
        }
    }
}
