package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class IllagerPutLootChestGoal<T extends AbstractIllagerServant> extends IllagerStoreChestGoal<T> {

    public IllagerPutLootChestGoal(T illager) {
        super(illager);
        this.predicate = illager::validLootToStore;
        this.chestPredicate = itemStack -> true;
    }

    @Override
    public void chestInteract(Container container) {
        Optional<ItemStack> optional = this.illager.itemsInInv(this.predicate).stream().findFirst();
        if (optional.isPresent()) {
            ItemStack itemStack = optional.get();
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
