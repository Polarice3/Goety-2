package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class RainArrow extends Arrow {
    public RainArrow(EntityType<? extends Arrow> p_36858_, Level p_36859_) {
        super(p_36858_, p_36859_);
    }

    public RainArrow(Level p_36861_, double p_36862_, double p_36863_, double p_36864_) {
        super(p_36861_, p_36862_, p_36863_, p_36864_);
    }

    public RainArrow(Level p_36866_, LivingEntity p_36867_) {
        super(p_36866_, p_36867_);
    }

    @Override
    protected Component getTypeName() {
        return EntityType.ARROW.getDescription();
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityType.RAIN_ARROW.get();
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    public void tick() {
        super.tick();
        if (this.inGround){
            if (this.tickCount % 5 == 0){
                this.discard();
            }
        }
    }

    protected void onHitBlock(BlockHitResult p_36755_) {
        super.onHitBlock(p_36755_);
        if (!this.level.isClientSide) {
            this.discard();
        }
    }

    @Override
    public void remove(RemovalReason p_146834_) {
        if (p_146834_ == RemovalReason.DISCARDED){
            if (this.level instanceof ServerLevel serverLevel){
                ColorUtil colorUtil = new ColorUtil(0x16feff);
                serverLevel.sendParticles(ModParticleTypes.CULT_SPELL.get(), this.getX(), this.getY(), this.getZ(), 0, colorUtil.red, colorUtil.green, colorUtil.blue, 1.0F);
            }
        }
        super.remove(p_146834_);
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (pEntity == this.getOwner()){
                return false;
            }
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity);
            } else {
                if (MobUtil.areAllies(this.getOwner(), pEntity)){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1) && super.canHitEntity(pEntity);
                }
            }
        }
        return super.canHitEntity(pEntity);
    }
}
