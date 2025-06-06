package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class IllagerPutFoodChestGoal extends IllagerChestGoal {

    public IllagerPutFoodChestGoal(AbstractIllagerServant illager, int range) {
        super(illager, range);
        this.predicate = ItemStack::isEdible;
        this.chestPredicate = itemStack -> true;
    }

    @Override
    public boolean canUse() {
        if (this.illager.getChestPos() == null) {
            return false;
        }
        if (this.illager.getChestLevel() != this.illager.level.dimension()) {
            return false;
        }
        if (this.illager.getBoundPos() != null){
            if (this.illager.getChestPos() != null){
                if (!this.illager.isWithinGuard(this.illager.getChestPos())){
                    return false;
                }
            }
        }
        if (!this.illager.hasExcessFood()) {
            return false;
        }
        if (this.getChest(this.illager.level, this.illager.getChestPos()) == null) {
            return false;
        } else if (this.isFull(this.getItem(), this.illager.level, this.illager.getChestPos())) {
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
        Optional<ItemStack> optional = this.illager.itemsInInv(this.predicate).stream().findFirst();
        if (optional.isPresent()) {
            ItemStack itemStack = optional.get();
            int h = 0;
            if (itemStack.getCount() > itemStack.getMaxStackSize() / 2) {
                h = itemStack.getCount() / 2;
            }

            if (itemStack.getCount() > 24) {
                h = itemStack.getCount() - 24;
            }
            itemStack = itemStack.split(h);
            for (int i = 0; i < container.getContainerSize(); ++i) {
                ItemStack containerItem = container.getItem(i);
                if (!itemStack.isEmpty()) {
                    if (containerItem.isEmpty()) {
                        container.setItem(i, itemStack.copyAndClear());
                        container.setChanged();
                        return;
                    } else if (containerItem.getItem() == itemStack.getItem()) {
                        final int j = Math.min(container.getMaxStackSize(), containerItem.getMaxStackSize());
                        final int k = Math.min(itemStack.getCount(), j - containerItem.getCount());
                        if (k > 0) {
                            int l = 0;
                            while (l < k && containerItem.getCount() < containerItem.getMaxStackSize()) {
                                ++l;
                                containerItem.grow(1);
                                itemStack.shrink(1);
                            }

                            if (l >= k || containerItem.getCount() == containerItem.getMaxStackSize()) {
                                if (!itemStack.isEmpty()) {
                                    if (this.illager.getInventory().canAddItem(itemStack)) {
                                        this.illager.getInventory().addItem(itemStack);
                                    }
                                }
                            }
                            container.setChanged();
                            return;
                        }
                    }
                }
            }
        }
    }
}
