package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class IllagerLootFoodChestGoal<T extends AbstractIllagerServant> extends IllagerChestGoal<T> {

    public IllagerLootFoodChestGoal(T illager) {
        super(illager);
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
