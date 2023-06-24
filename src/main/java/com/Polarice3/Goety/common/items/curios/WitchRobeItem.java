package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.inventory.ModSaveInventory;
import com.Polarice3.Goety.common.inventory.WitchRobeInventory;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class WitchRobeItem extends SingleStackItem implements ICurioItem {
    public static String INVENTORY = "WITCH_ROBE_BREW";
    public static String AUTOMATING = "AUTOMATING";

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (ModSaveInventory.getInstance() != null) {
            if (!stack.hasTag()) {
                stack.setTag(new CompoundTag());
                stack.getOrCreateTag().putInt(INVENTORY, ModSaveInventory.getInstance().addAndCreateWitchRobe());
            } else {
                if (!stack.getOrCreateTag().contains(INVENTORY)) {
                    stack.getOrCreateTag().putInt(INVENTORY, ModSaveInventory.getInstance().addAndCreateWitchRobe());
                }

                WitchRobeInventory inventory = ModSaveInventory.getInstance().getWitchRobeInventory((stack.getOrCreateTag().getInt(INVENTORY)), slotContext.entity());

                if (!slotContext.entity().level.isClientSide) {
                    if (CuriosFinder.hasCurio(slotContext.entity(), ModItems.WITCH_HAT.get())) {
                        inventory.setIncreaseSpeed(1);
                    } else {
                        inventory.setIncreaseSpeed(0);
                    }

/*                    if (isAutomating(stack)) {
                        if (slotContext.entity() instanceof Player player) {
                            if (ItemHelper.hasItem(player, Items.WATER_BUCKET)) {
                                if (ItemHelper.hasItem(player, Items.GLASS_BOTTLE)) {
                                    inventory.autoAddWaterBottles(ItemHelper.findItem(player, Items.GLASS_BOTTLE));
                                }
                                if (ItemHelper.hasItem(player, Items.POTION)){
                                    if (PotionUtils.getPotion(ItemHelper.findItem(player, Items.POTION)) == Potions.WATER){
                                        inventory.addBottles(ItemHelper.findItem(player, Items.POTION), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
                                    }
                                }
                            }
                            if (ItemHelper.hasItem(player, Items.BLAZE_POWDER)) {
                                if (inventory.needsFuel()) {
                                    inventory.addFuel(ItemHelper.findItem(player, Items.BLAZE_POWDER));
                                }
                            }
                            if (ItemHelper.hasItem(player, Items.NETHER_WART)){
                                if (inventory.isWaterOrEmpty()){
                                    inventory.addBottlesOrCatalyst(ItemHelper.findItem(player, Items.NETHER_WART));
                                }
                            }
                        }
                    }*/
                    if (!inventory.isEmpty() && inventory.isBrewable()) {
                        inventory.tick();
                    }
                }
            }
        }
    }

/*    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return super.isFoil(p_41453_) || isAutomating(p_41453_);
    }

    public static boolean isAutomating(ItemStack itemStack){
        return itemStack.getTag() != null && itemStack.getTag().getBoolean(AUTOMATING);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() || player.isCrouching()){
            if (itemstack.getItem() instanceof WitchRobeItem){
                if (itemstack.getTag() != null){
                    itemstack.getTag().putBoolean(AUTOMATING, !itemstack.getTag().getBoolean(AUTOMATING));
                    player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }*/
}
