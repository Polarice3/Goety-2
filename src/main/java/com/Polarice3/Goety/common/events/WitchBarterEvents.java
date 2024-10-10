package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.hostile.cultists.Crone;
import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.entities.hostile.cultists.Maverick;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.WitchBarterHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WitchBarterEvents {

    @SubscribeEvent
    public static void LivingEffects(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null && livingEntity.isAlive()){
            if (livingEntity instanceof Raider raider) {
                if (raider instanceof Cultist || raider instanceof Witch) {
                    if (WitchBarterHelper.getTimer(raider) > 0) {
                        WitchBarterHelper.decreaseTimer(raider);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void InteractEntityEvent(PlayerInteractEvent.EntityInteractSpecific event){
        Player player = event.getEntity();
        if (!event.getLevel().isClientSide) {
            if (CuriosFinder.isWitchFriendly(player)) {
                if (event.getTarget() instanceof Raider witch) {
                    if (witch instanceof Witch || witch instanceof Cultist cultist && cultist.isBarterable()) {
                        if (!witch.isAggressive()) {
                            if (WitchBarterHelper.getTimer(witch) <= 0) {
                                if (event.getHand() == InteractionHand.MAIN_HAND) {
                                    boolean maverick = witch instanceof Maverick && witch.getOffhandItem().isEmpty();
                                    if ((witch.getMainHandItem().isEmpty() || maverick) && (event.getItemStack().is(ModTags.Items.WITCH_CURRENCY) || event.getItemStack().is(ModTags.Items.WITCH_BETTER_CURRENCY))) {
                                        event.setCanceled(true);
                                        event.setCancellationResult(InteractionResult.SUCCESS);
                                        if (witch instanceof Crone) {
                                            witch.playSound(ModSounds.CRONE_AMBIENT.get());
                                        } else {
                                            witch.playSound(witch.getCelebrateSound());
                                        }
                                        ItemStack itemstack1;
                                        if (player.isCreative()) {
                                            itemstack1 = event.getItemStack();
                                        } else {
                                            itemstack1 = event.getItemStack().split(1);
                                        }
                                        if (witch instanceof Maverick){
                                            witch.setItemSlot(EquipmentSlot.OFFHAND, itemstack1);
                                        } else {
                                            witch.setItemSlot(EquipmentSlot.MAINHAND, itemstack1);
                                        }
                                        WitchBarterHelper.setTrader(witch, player);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
