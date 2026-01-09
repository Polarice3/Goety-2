package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.*;
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
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        if (this.getOwner() instanceof AbstractIllagerServant
                && MobsConfig.IllagerServantGhostArrows.get()) {
            if (!MobUtil.canHitEntity(arrow, pEntity)) {
                callback.setReturnValue(false);
            }
        }
        if (this.getOwner() instanceof OwnableEntity ownable && this.getOwner() instanceof LivingEntity livingEntity) {
            if (livingEntity.getMobType() == MobType.UNDEAD) {
                if (ownable.getOwner() != null) {
                    if (CuriosFinder.hasUndeadCape(ownable.getOwner())) {
                        if (!MobUtil.canHitEntity(arrow, pEntity)) {
                            callback.setReturnValue(false);
                        }
                    }
                }
            }
        }
    }
}
