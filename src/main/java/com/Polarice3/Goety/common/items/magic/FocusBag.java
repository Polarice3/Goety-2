package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.inventory.container.FocusBagContainer;
import com.Polarice3.Goety.common.items.capability.FocusBagItemCapability;
import com.Polarice3.Goety.common.items.handler.FocusBagItemHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FocusBag extends Item {
    public FocusBag(){
        super(new Properties()
                .tab(Goety.TAB)
                .rarity(Rarity.UNCOMMON)
                .setNoRepair()
                .stacksTo(1)
        );
    }

    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide) {
            SimpleMenuProvider provider = new SimpleMenuProvider(
                    (id, inventory, player) -> new FocusBagContainer(id, inventory, FocusBagItemHandler.get(itemstack), itemstack), getName(itemstack));
            NetworkHooks.openScreen((ServerPlayer) playerIn, provider, (buffer) -> {});
        }
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag result = new CompoundTag();
        CompoundTag tag = super.getShareTag(stack);
        CompoundTag cap = FocusBagItemHandler.get(stack).serializeNBT();
        if (tag != null) {
            result.put("tag", tag);
        }
        if (cap != null) {
            result.put("cap", cap);
        }
        return result;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        if (nbt != null) {
            if (nbt.contains("tag")) {
                stack.setTag(nbt.getCompound("tag"));
            }
            if (nbt.contains("cap")) {
                FocusBagItemHandler.get(stack).deserializeNBT(nbt.getCompound("cap"));
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
    }

    @Override
    @Nullable
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundTag nbt) {
        return new FocusBagItemCapability(stack);
    }
}
