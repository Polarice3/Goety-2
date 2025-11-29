package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

//This doesn't use Raiding Chest so that other Illagers can take excess food
public class IllagerPutFoodChestGoal<T extends AbstractIllagerServant> extends IllagerChestGoal<T> {

    public IllagerPutFoodChestGoal(T illager) {
        super(illager);
        this.predicate = illager::canEat;
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
        if (!this.canStore()) {
            return false;
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

    public boolean canStore() {
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
            return h > 0;
        }
        return false;
    }

    @Override
    public void chestInteract(Container container) {
        Optional<ItemStack> optional = this.illager.itemsInInv(this.predicate).stream().findFirst();
        ItemStack itemStack0 = ItemStack.EMPTY;
        if (optional.isPresent()) {
            ItemStack itemStack = optional.get();
            int h = 0;
            if (itemStack.getCount() > itemStack.getMaxStackSize() / 2) {
                h = itemStack.getCount() / 2;
            }

            if (itemStack.getCount() > 24) {
                h = itemStack.getCount() - 24;
            }
            if (h > 0) {
                itemStack0 = itemStack.split(h);
            }
            if (!itemStack0.isEmpty()) {
                for (int i = 0; i < container.getContainerSize(); ++i) {
                    ItemStack containerItem = container.getItem(i);
                    if (containerItem.isEmpty()) {
                        container.setItem(i, itemStack0.copyAndClear());
                        container.setChanged();
                        return;
                    } else if (containerItem.getItem() == itemStack0.getItem()) {
                        final int j = Math.min(container.getMaxStackSize(), containerItem.getMaxStackSize());
                        final int k = Math.min(itemStack0.getCount(), j - containerItem.getCount());
                        if (k > 0) {
                            int l = 0;
                            while (l < k && containerItem.getCount() < containerItem.getMaxStackSize()) {
                                ++l;
                                containerItem.grow(1);
                                itemStack0.shrink(1);
                            }

                            if (l >= k || containerItem.getCount() == containerItem.getMaxStackSize()) {
                                if (!itemStack0.isEmpty()) {
                                    if (this.illager.getInventory().canAddItem(itemStack0)) {
                                        this.illager.getInventory().addItem(itemStack0);
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
