package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.ally.illager.ILooter;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
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
        if (this.hasDumpChest() || this.hasRegularChest()) {
            return super.canUse();
        }
        return false;
    }

    public boolean hasDumpChest() {
        if (this.illager.getDumpChestPos() != null) {
            if (this.illager.getDumpChestLevel() == this.illager.level.dimension()) {
                boolean flag = true;
                if (this.illager.getBoundPos() != null){
                    flag = this.illager.isWithinGuard(this.illager.getDumpChestPos());
                }
                if (flag) {
                    return this.getChest(this.illager.level, this.illager.getDumpChestPos()) != null && !this.isFull(this.getItem(), this.illager.level, this.illager.getDumpChestPos());
                }
            }
        }
        return false;
    }

    public boolean hasRegularChest() {
        if (this.illager.getChestPos() != null) {
            if (this.illager.getChestLevel() == this.illager.level.dimension()) {
                boolean flag = true;
                if (this.illager.getBoundPos() != null){
                    flag = this.illager.isWithinGuard(this.illager.getChestPos());
                }
                if (flag) {
                    return this.getChest(this.illager.level, this.illager.getChestPos()) != null && !this.isFull(this.getItem(), this.illager.level, this.illager.getChestPos());
                }
            }
        }
        return false;
    }
}
