package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.ally.illager.ILooter;
import com.Polarice3.Goety.common.entities.ally.illager.RaiderServant;
import net.minecraft.util.Mth;

public abstract class IllagerStoreChestGoal<T extends RaiderServant & ILooter> extends IllagerChestGoal<T>{
    public IllagerStoreChestGoal(T illager) {
        super(illager);
    }

    protected boolean findNearestBlock() {
        if (this.illager.getDumpChestPos() != null) {
            this.blockPos = this.illager.getDumpChestPos();
            if (this.blockPos != null){
                return this.illager.distanceToSqr(this.blockPos.getX() + 0.5F, this.blockPos.getY() + 0.5F, this.blockPos.getZ() + 0.5F) <= Mth.square(this.searchRange);
            }
        }
        return super.findNearestBlock();
    }

    @Override
    public boolean canUse() {
        if (this.illager.getDumpChestPos() == null) {
            return false;
        } else if (this.illager.getChestPos() == null) {
            return false;
        }
        if (this.illager.getDumpChestLevel() != this.illager.level.dimension()) {
            return false;
        } else if (this.illager.getChestLevel() != this.illager.level.dimension()) {
            return false;
        }
        if (this.illager.getBoundPos() != null){
            if (this.illager.getDumpChestPos() != null && !this.illager.isWithinGuard(this.illager.getDumpChestPos())){
                return false;
            } else if (this.illager.getChestPos() != null && !this.illager.isWithinGuard(this.illager.getChestPos())){
                return false;
            }
        }
        if (this.getChest(this.illager.level, this.illager.getDumpChestPos()) == null) {
            return false;
        } else if (this.isFull(this.getItem(), this.illager.level, this.illager.getDumpChestPos())) {
            return false;
        } else if (this.getChest(this.illager.level, this.illager.getChestPos()) == null) {
            return false;
        } else if (this.isFull(this.getItem(), this.illager.level, this.illager.getChestPos())) {
            return false;
        }
        return super.canUse();
    }
}
