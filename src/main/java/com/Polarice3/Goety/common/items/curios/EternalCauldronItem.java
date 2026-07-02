package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.api.items.curios.IActivatable;
import com.Polarice3.Goety.client.inventory.container.EternalCauldronContainer;
import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.effects.brew.BrewEffects;
import com.Polarice3.Goety.common.items.capability.EternalCauldronItemCapability;
import com.Polarice3.Goety.common.items.handler.EternalCauldronItemHandler;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BrewUtils;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EternalCauldronItem extends SingleStackItem implements IActivatable {

    @Override
    public void activate(Level level, Player player, ItemStack itemStack) {
        if (itemStack.is(this)) {
            if (!level.isClientSide) {
                if (player.isShiftKeyDown()) {
                    SimpleMenuProvider provider = new SimpleMenuProvider(
                            (id, inventory, playerIn) -> new EternalCauldronContainer(id, inventory, EternalCauldronItemHandler.get(itemStack), itemStack), getName(itemStack));
                    NetworkHooks.openScreen((ServerPlayer) player, provider, (buffer) -> {});
                } else {
                    if (!getBottle(itemStack).isEmpty() && !SEHelper.isOnCooldown(player, itemStack)) {
                        ItemStack bottle = getBottle(itemStack);
                        int duration = MathHelper.secondsToTicks(45);
                        int add = 1;
                        for(MobEffectInstance mobeffectinstance : PotionUtils.getMobEffects(bottle)) {
                            BrewEffect brewEffect = BrewEffects.INSTANCE.getBrewEffect(mobeffectinstance.getDescriptionId());
                            int amp = 1;
                            amp += mobeffectinstance.getAmplifier();
                            if (brewEffect != null) {
                                add += (brewEffect.getCapacityExtra() * amp);
                            } else if (amp > 1) {
                                add += amp - 1;
                            }
                            if (mobeffectinstance.getEffect().isInstantenous()) {
                                mobeffectinstance.getEffect().applyInstantenousEffect(player, player, player, mobeffectinstance.getAmplifier(), 1.0D);
                            } else {
                                player.addEffect(new MobEffectInstance(mobeffectinstance));
                            }
                        }
                        for (BrewEffectInstance brewEffectInstance : BrewUtils.getBrewEffects(bottle)){
                            brewEffectInstance.getEffect().drinkBlockEffect(player, player, player, brewEffectInstance.getAmplifier(), BrewUtils.getAreaOfEffect(bottle));
                        }
                        ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(ModSounds.POTION_DRINK.get(), 1.0F, 1.0F));
                        SEHelper.addCooldown(player, this, duration * add);
                    }
                }
            }
        }
    }

    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide) {
            SimpleMenuProvider provider = new SimpleMenuProvider(
                    (id, inventory, player) -> new EternalCauldronContainer(id, inventory, EternalCauldronItemHandler.get(itemstack), itemstack), getName(itemstack));
            NetworkHooks.openScreen((ServerPlayer) playerIn, provider, (buffer) -> {});
        }
        return InteractionResultHolder.success(itemstack);
    }

    public static ItemStack getBottle(ItemStack itemstack) {
        EternalCauldronItemHandler handler = EternalCauldronItemHandler.get(itemstack);
        return handler.getSlot();
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    public static IItemHandler getItemHandler(ItemStack itemStack) {
        return itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() ->
                new IllegalArgumentException("Expected an item handler for the Potion/Brew item, but " + itemStack + " does not expose an item handler."));
    }

    public CompoundTag getShareTag(ItemStack stack) {
        IItemHandler iitemHandler = getItemHandler(stack);
        CompoundTag nbt = stack.getTag() != null ? stack.getTag() : new CompoundTag();
        if(iitemHandler instanceof ItemStackHandler itemHandler) {
            nbt.put("cap", itemHandler.serializeNBT());
        }
        return nbt;
    }

    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        if(nbt == null) {
            stack.setTag(null);
        } else {
            IItemHandler iitemHandler = getItemHandler(stack);
            if(iitemHandler instanceof ItemStackHandler itemHandler)
                itemHandler.deserializeNBT(nbt.getCompound("cap"));
            stack.setTag(nbt);
        }
    }

    @Override
    @Nullable
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundTag nbt) {
        return new EternalCauldronItemCapability(stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.equals(newStack) && slotChanged;
    }
}
