package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
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

import javax.annotation.Nonnull;

public class GroundGrimoire extends Item {
    public GroundGrimoire() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!entity.level.isClientSide) {
            if (entity instanceof LivingEntity pTarget && pTarget instanceof IOwned owned && owned.getTrueOwner() == player) {
                if (MobUtil.isShifting(player)) {
                    if (SEHelper.addGroundedEntityType(player, pTarget.getType())) {
                        pTarget.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 0.25F);
                        player.displayClientMessage(Component.translatable("info.goety.grimoire.addType", pTarget.getType().getDescription()), true);
                    }
                } else {
                    if (SEHelper.addGroundedEntity(player, pTarget)) {
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
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand){
        if (!player.level.isClientSide) {
            if (MobUtil.isShifting(player)) {
                if (SEHelper.removeGroundedEntityType(player, target.getType())) {
                    target.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 0.25F);
                    player.displayClientMessage(Component.translatable("info.goety.grimoire.removeType", target.getType().getDescription()), true);
                    return InteractionResult.SUCCESS;
                }
            } else {
                if (SEHelper.removeGroundedEntity(player, target)) {
                    target.playSound(SoundEvents.PLAYER_LEVELUP);
                    player.displayClientMessage(Component.translatable("info.goety.grimoire.remove", target.getDisplayName()), true);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (MobUtil.isShifting(player)) {
                if (SEHelper.getGroundedEntityTypes(player).isEmpty()){
                    player.displayClientMessage(Component.translatable("info.goety.ground.emptyTypes"), false);
                } else {
                    player.displayClientMessage(Component.translatable("info.goety.ground.type"), false);
                }
                for (EntityType<?> entityType : SEHelper.getGroundedEntityTypes(player)) {
                    player.displayClientMessage(Component.translatable(entityType.getDescriptionId()), false);
                }
            } else {
                if (SEHelper.getGroundedEntities(player).isEmpty()){
                    player.displayClientMessage(Component.translatable("info.goety.ground.empty"), false);
                } else {
                    player.displayClientMessage(Component.translatable("info.goety.grimoire.ground"), false);
                }
                for (Entity entity : SEHelper.getGroundedEntities(player)) {
                    player.displayClientMessage(entity.getDisplayName(), false);
                }
            }
        }
        return InteractionResultHolder.consume(itemstack);
    }
}
