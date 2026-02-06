package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.illager.raider.Prisoner;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class OminousShacklesItem extends Item {
    public OminousShacklesItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.getCommandSenderWorld();
        if (!level.isClientSide) {
            if (target instanceof AbstractVillager villager && !villager.isBaby()) {
                Prisoner prisoner = villager.convertTo(ModEntityType.PRISONER.get(), true);
                if (prisoner != null) {
                    if (villager instanceof Villager villager1) {
                        prisoner.setVillagerData(villager1.getVillagerData());
                        prisoner.setGossips(villager1.getGossips().store(NbtOps.INSTANCE));
                        MobUtil.releaseAllPois(villager1);
                    }
                    prisoner.setTradeOffers(villager.getOffers().createTag());
                    prisoner.setVillagerXp(villager.getVillagerXp());
                    prisoner.setIsTrader(villager instanceof WanderingTrader);
                    prisoner.setTrueOwner(player);
                    net.minecraftforge.event.ForgeEventFactory.onLivingConvert(villager, prisoner);
                    if (!prisoner.isSilent()) {
                        prisoner.playSound(SoundEvents.IRON_TRAPDOOR_CLOSE);
                    }
                    for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(16.0D))) {
                        if (mob instanceof Villager villager1) {
                            Brain<?> brain = villager1.getBrain();
                            Player player1 = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).orElse(null);
                            if (player1 != null && player1 == player) {
                                if (villager1.getPlayerReputation(player) > -200) {
                                    villager1.getGossips().add(player.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
                                }
                            }
                        }
                        if (mob.getType().is(ModTags.EntityTypes.VILLAGE_GUARDS)) {
                            if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player) && !MobUtil.areAllies(mob, player)) {
                                if (mob instanceof IronGolem ironGolem) {
                                    if (!ironGolem.isPlayerCreated()) {
                                        ironGolem.setTarget(player);
                                    }
                                } else {
                                    mob.setTarget(player);
                                }
                            }
                        }
                    }
                    prisoner.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ModItems.OMINOUS_SHACKLES.get()));
                    prisoner.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
                    stack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ChatFormatting main = ChatFormatting.DARK_PURPLE;

        if (stack.is(this)) {
            tooltip.add(Component.translatable("info.goety.ominous_shackles").withStyle(main));
        }
    }
}
