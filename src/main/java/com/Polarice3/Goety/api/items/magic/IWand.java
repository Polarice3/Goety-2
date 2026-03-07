package com.Polarice3.Goety.api.items.magic;

import com.Polarice3.Goety.api.magic.IBlockSpell;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.ITouchSpell;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.items.capability.SoulUsingItemCapability;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IWand extends IForgeItem {
    String SOULUSE = "Soul Use";
    String CASTTIME = "Cast Time";
    String SOULCOST = "Soul Cost";
    String DURATION = "Duration";
    String COOLDOWN = "Cooldown";
    String COOL = "Cool";
    String SECONDS = "Seconds";
    String SHOTS = "Shots";

    SpellType getSpellType();

    static ItemStack getFocus(ItemStack itemstack) {
        SoulUsingItemHandler handler = SoulUsingItemHandler.get(itemstack);
        return handler.getSlot();
    }

    static IFocus getMagicFocus(ItemStack itemStack){
        if (getFocus(itemStack) != null && !getFocus(itemStack).isEmpty() && getFocus(itemStack).getItem() instanceof IFocus magicFocus){
            return magicFocus;
        } else {
            return null;
        }
    }

    default ISpell getSpell(ItemStack stack){
        IFocus focus = getMagicFocus(stack);
        if (focus != null && focus.getSpell() != null){
            return focus.getSpell();
        } else {
            return null;
        }
    }

    default int SoulUse(LivingEntity entityLiving, ItemStack stack){
        return 0;
    }

    default boolean cannotCast(LivingEntity livingEntity, ItemStack stack){
        return this.cannotCast(livingEntity, stack, this.getSpell(stack));
    }

    default boolean cannotCast(LivingEntity livingEntity, ItemStack stack, ISpell spell){
        boolean flag = false;
        if (livingEntity.level instanceof ServerLevel serverLevel){
            if (spell != null){
                if (!spell.conditionsMet(serverLevel, livingEntity)){
                    flag = true;
                }
            }
        }
        return this.isOnCooldown(livingEntity, stack) || flag;
    }

    default boolean isOnCooldown(LivingEntity livingEntity, ItemStack stack){
        if (livingEntity instanceof Player player){
            if (IWand.getFocus(stack) != null){
                Item item = IWand.getFocus(stack).getItem();
                return SEHelper.getFocusCoolDown(player).isOnCooldown(item);
            }
        }
        return false;
    }

    @Deprecated()
    default boolean isNotInstant(ISpell spells){
        return this.isNotInstant(spells, null, ItemStack.EMPTY);
    }

    default boolean isNotInstant(ISpell spells, @Nullable LivingEntity caster, ItemStack staff){
        return spells != null && (caster != null ? spells.castDuration(caster, staff) > 0 : spells.defaultCastDuration() > 0);
    }

    default boolean notTouch(ISpell spells){
        return !(spells instanceof ITouchSpell) && !(spells instanceof IBlockSpell);
    }

    default void useParticles(Level worldIn, LivingEntity livingEntity, ItemStack stack, ISpell iSpell){
        if (iSpell != null){
            iSpell.useParticle(worldIn, livingEntity, stack);
        }
    }

    default float getWandVisualHeight(Level level, LivingEntity entity, ItemStack stack) {
        return 0.8F;
    }

    default int currentCastTime(LivingEntity livingEntity, ItemStack itemstack){
        if (livingEntity.isUsingItem() && livingEntity.getUseItem() == itemstack) {
            return livingEntity.getTicksUsingItem();
        } else {
            return 0;
        }
    }

    default int ShotsFired(ItemStack itemStack){
        return 0;
    }

    default void failParticles(Level worldIn, LivingEntity entityLiving){
        for (int i = 0; i < entityLiving.level.random.nextInt(35) + 10; ++i) {
            double d = worldIn.random.nextGaussian() * 0.2D;
            worldIn.addParticle(ParticleTypes.CLOUD, entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ(), d, d, d);
        }
    }

    /**
     * Found Creative Server Bug fix from @mraof's Minestuck Music Player Weapon code.
     */
    static IItemHandler getItemHandler(ItemStack itemStack) {
        return itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() ->
                new IllegalArgumentException("Expected an item handler for the Magic Focus item, but " + itemStack + " does not expose an item handler."));
    }

    default CompoundTag getShareTag(ItemStack stack) {
        IItemHandler iitemHandler = getItemHandler(stack);
        CompoundTag nbt = stack.getTag() != null ? stack.getTag() : new CompoundTag();
        if(iitemHandler instanceof ItemStackHandler itemHandler) {
            nbt.put("cap", itemHandler.serializeNBT());
        }
        return nbt;
    }

    default void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
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
    default ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundTag nbt) {
        return new SoulUsingItemCapability(stack);
    }

    @Override
    default boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.equals(newStack) && slotChanged;
    }
}
