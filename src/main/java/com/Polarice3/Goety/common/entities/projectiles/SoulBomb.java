package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SSoulExplodePacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class SoulBomb extends ThrowableProjectile {

    public SoulBomb(EntityType<? extends ThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public SoulBomb(double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(ModEntityType.SOUL_BOMB.get(), p_37457_, p_37458_, p_37459_, p_37460_);
    }

    public SoulBomb(LivingEntity p_37463_, Level p_37464_) {
        super(ModEntityType.SOUL_BOMB.get(), p_37463_, p_37464_);
    }

    @Override
    protected void defineSynchedData() {
    }

    protected void onHit(HitResult p_37406_) {
        super.onHit(p_37406_);
        this.playSound(ModSounds.BOLT_IMPACT.get());
        if (!this.level.isClientSide) {
            float damage = 0;
            if (this.getOwner() instanceof Mob mob && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null){
                damage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
            }
            MobUtil.explosionDamage(this.level, this, this.damageSources().indirectMagic(this, this.getOwner()), this.getX(), this.getY(), this.getZ(), 2.0F, damage);
            ModNetwork.sendToALL(new SSoulExplodePacket(this.blockPosition(), 2));
            this.discard();
        }
    }
}
