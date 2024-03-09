package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

public class GhostArrow extends Arrow {
    public GhostArrow(EntityType<? extends Arrow> p_36858_, Level p_36859_) {
        super(p_36858_, p_36859_);
    }

    public GhostArrow(Level p_36861_, double p_36862_, double p_36863_, double p_36864_) {
        super(p_36861_, p_36862_, p_36863_, p_36864_);
    }

    public GhostArrow(Level p_36866_, LivingEntity p_36867_) {
        super(p_36866_, p_36867_);
    }

    @Override
    protected Component getTypeName() {
        return EntityType.ARROW.getDescription();
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityType.GHOST_ARROW.get();
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (pEntity == this.getOwner()){
                return false;
            }
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity);
            } else {
                if(this.getOwner().isAlliedTo(pEntity) || pEntity.isAlliedTo(this.getOwner())){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return super.canHitEntity(pEntity);
    }
}
