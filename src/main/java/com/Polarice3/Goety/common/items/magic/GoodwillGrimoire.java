package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class GoodwillGrimoire extends Item {
    public GoodwillGrimoire() {
        super(new Properties()
                .tab(Goety.TAB)
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!entity.level.isClientSide) {
            if (entity instanceof LivingEntity pTarget) {
                if (MobUtil.isShifting(player)) {
                    if (SEHelper.addAllyEntityType(player, pTarget.getType())) {
                        if (SEHelper.getGrudgeEntityTypes(player).contains(pTarget.getType())){
                            SEHelper.removeGrudgeEntityType(player, pTarget.getType());
                            player.displayClientMessage(Component.translatable("info.goety.grudge.removeType", I18n.get(pTarget.getType().getDescriptionId())), true);
                        }
                        pTarget.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 0.25F);
                        player.displayClientMessage(Component.translatable("info.goety.grimoire.addType", I18n.get(pTarget.getType().getDescriptionId())), true);
                    }
                } else {
                    if (SEHelper.addAllyEntity(player, pTarget)) {
                        if (SEHelper.getGrudgeEntities(player).contains(pTarget)){
                            SEHelper.removeGrudgeEntity(player, pTarget);
                            player.displayClientMessage(Component.translatable("info.goety.grudge.remove", I18n.get(pTarget.getType().getDescriptionId())), true);
                        }
                        pTarget.playSound(SoundEvents.PLAYER_LEVELUP);
                        player.displayClientMessage(Component.translatable("info.goety.grimoire.add", pTarget.getDisplayName()), true);
                    }
                }
            }
        }
        return true;
    }

    @Nonnull
    @Override
    public InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, LivingEntity target, InteractionHand hand){
        if (!player.level.isClientSide) {
            if (MobUtil.isShifting(player)) {
                if (SEHelper.removeAllyEntityType(player, target.getType())) {
                    target.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 0.25F);
                    player.displayClientMessage(Component.translatable("info.goety.grimoire.removeType", I18n.get(target.getType().getDescriptionId())), true);
                    return InteractionResult.SUCCESS;
                }
            } else {
                if (SEHelper.removeAllyEntity(player, target)) {
                    target.playSound(SoundEvents.FIRE_EXTINGUISH);
                    player.displayClientMessage(Component.translatable("info.goety.grimoire.remove", target.getDisplayName()), true);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (MobUtil.isShifting(player)) {
                if (SEHelper.getAllyEntityTypes(player).isEmpty()){
                    player.displayClientMessage(Component.translatable("info.goety.goodwill.emptyTypes"), false);
                } else {
                    player.displayClientMessage(Component.translatable("info.goety.goodwill.type"), false);
                    for (EntityType<?> entityType : SEHelper.getAllyEntityTypes(player)) {
                        player.displayClientMessage(Component.translatable(entityType.getDescriptionId()), false);
                    }
                }
            } else {
                if (SEHelper.getAllyEntities(player).isEmpty()){
                    player.displayClientMessage(Component.translatable("info.goety.goodwill.empty"), false);
                } else {
                    player.displayClientMessage(Component.translatable("info.goety.grimoire.goodwill"), false);
                    for (Entity entity : SEHelper.getAllyEntities(player)) {
                        player.displayClientMessage(entity.getDisplayName(), false);
                    }
                }
            }
        }
        return InteractionResultHolder.consume(itemstack);
    }
}
