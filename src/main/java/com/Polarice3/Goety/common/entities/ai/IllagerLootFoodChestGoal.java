package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class IllagerLootFoodChestGoal extends IllagerChestGoal {

    public IllagerLootFoodChestGoal(AbstractIllagerServant illager, int range) {
        super(illager, range);
        this.chestPredicate = illager::validFood;
    }

    @Override
    public boolean canUse() {
        if (this.illager.getChestPos() == null) {
            return false;
        }
        if (this.illager.getBoundPos() != null){
            if (this.illager.getChestPos() != null){
                if (!this.illager.isWithinGuard(this.illager.getChestPos())){
                    return false;
                }
            }
        }
        if (this.illager.getChestLevel() != this.illager.level.dimension()) {
            return false;
        }
        if (!this.illager.wantsMoreFood()) {
            return false;
        }
        if (!this.isChestRaidable(this.illager.level, this.illager.getChestPos())){
            return false;
        }
        return super.canUse();
    }

    protected boolean findNearestBlock() {
        if (this.illager.getChestPos() != null) {
            this.blockPos = this.illager.getChestPos();
            if (this.blockPos != null){
                return this.illager.distanceToSqr(this.blockPos.getX() + 0.5F, this.blockPos.getY() + 0.5F, this.blockPos.getZ() + 0.5F) <= Mth.square(this.searchRange);
            }
        }
        return false;
    }

    @Override
    public void chestInteract(Container container) {
        for (ItemStack itemStack : this.getItems(container)) {
            if (this.illager.getInventory().canAddItem(itemStack) && this.illager.wantsMoreFood()){
                this.illager.getInventory().addItem(itemStack.split(12));
                container.setChanged();
            }
        }
    }
}
