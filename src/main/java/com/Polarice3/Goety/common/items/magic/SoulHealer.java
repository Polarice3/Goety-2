package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.curios.IActivatable;
import com.Polarice3.Goety.api.items.magic.IMobCharm;
import com.Polarice3.Goety.api.items.magic.ISoulContainer;
import com.Polarice3.Goety.api.items.magic.ISpellHolder;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.curios.SingleStackItem;
import com.Polarice3.Goety.common.magic.ModSpells;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModKeybindings;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

public class SoulHealer extends SingleStackItem implements ISoulContainer, IActivatable, IMobCharm, ISpellHolder {

    public SoulHealer() {
        super(new Properties().rarity(Rarity.UNCOMMON));
    }

    @Override
    public ISpell getSpell() {
        return ModSpells.SOUL_HEAL;
    }

    @Override
    public int getMaxSouls() {
        return this.getSpell().defaultSoulCost() * 6;
    }

    @Override
    public void charmTick(ItemStack itemStack) {
        IMobCharm.super.charmTick(itemStack);
        this.setTagTick(itemStack);
    }

    public int getSoulCost(LivingEntity livingEntity) {
        return WandUtil.getSoulUse(livingEntity, this.getDefaultInstance(), this.getSpell().soulCost(livingEntity, ItemStack.EMPTY));
    }

    @Override
    public void activate(Level level, Player player, ItemStack itemStack) {
        if (!SEHelper.isOnCooldown(player, itemStack)) {
            int soulCost = this.getSoulCost(player);
            if (SEHelper.getSoulsAmount(player, soulCost)) {
                if (level instanceof ServerLevel serverLevel) {
                    this.mobUse(serverLevel, player, itemStack);
                    if (!player.getAbilities().instabuild) {
                        SEHelper.decreaseSouls(player, soulCost);
                        SEHelper.sendSEUpdatePacket(player);
                    }
                }
            }
        }
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (!SEHelper.isOnCooldown(playerIn, itemstack)) {
            int soulCost = this.getSoulCost(playerIn);
            if (SEHelper.getSoulsAmount(playerIn, soulCost)) {
                if (worldIn instanceof ServerLevel serverLevel) {
                    this.mobUse(serverLevel, playerIn, itemstack);
                    if (!playerIn.getAbilities().instabuild) {
                        SEHelper.decreaseSouls(playerIn, soulCost);
                        SEHelper.sendSEUpdatePacket(playerIn);
                    }
                }
                return InteractionResultHolder.success(itemstack);
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean mobShouldUse(ServerLevel serverLevel, LivingEntity livingEntity, ItemStack itemStack) {
        return ISoulContainer.currentSouls(itemStack) >= this.getSoulCost(livingEntity)
                && livingEntity.getHealth() <= livingEntity.getMaxHealth() * 0.5F
                && IMobCharm.isNotOnCoolDown(itemStack);
    }

    public void mobUse(ServerLevel serverLevel, LivingEntity livingEntity, ItemStack itemStack) {
        SpellStat spellStat = this.getSpell().defaultStats();
        int potency = spellStat.getPotency();
        double radius = spellStat.getRadius();
        if (itemStack.isEnchanted()) {
            potency += WandUtil.getPotencyItemLevel(itemStack);
            radius += itemStack.getEnchantmentLevel(ModEnchantments.RADIUS.get());
        }
        spellStat = spellStat.setPotency(potency).setRadius(radius);
        this.getSpell().SpellResult(serverLevel, livingEntity, ItemStack.EMPTY, spellStat);
        if (livingEntity instanceof Player player) {
            SEHelper.addSpellCooldown(player, this.getSpell(), this.getSpell().spellCooldown(livingEntity));
        } else {
            int soulCost = this.getSoulCost(livingEntity);
            ISoulContainer.decreaseSouls(itemStack, soulCost);
            IMobCharm.setCoolDown(itemStack, this.getSpell().spellCooldown(livingEntity));
        }
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return this.getSpell().acceptedEnchantments().contains(enchantment);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("info.goety.soul_healer", ModKeybindings.keyBindings[14].getTranslatedKeyMessage().getString()).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("info.goety.soul_healer.give").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.empty());
        Player player = Goety.PROXY.getPlayer();
        if (player != null) {
            tooltip.add(Component.translatable("info.goety.wand.cost", this.getSoulCost(player)));
            tooltip.add(Component.translatable("info.goety.wand.coolDown", this.getSpell().spellCooldown(player) / 20.0F));
        }
    }
}
