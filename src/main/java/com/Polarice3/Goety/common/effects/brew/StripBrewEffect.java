package com.Polarice3.Goety.common.effects.brew;

import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class StripBrewEffect extends BrewEffect {
    public StripBrewEffect(int soulCost, int capacityExtra) {
        super("strip", soulCost, capacityExtra, MobEffectCategory.HARMFUL, 0x515151);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public boolean canLinger() {
        return true;
    }

    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, @Nullable Entity pIndirectSource, int pAmplifier){
        for (int i = 0; i < pAmplifier + 1; ++i){
            EquipmentSlot equipmentSlot = EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, pTarget.getRandom().nextInt(4));
            ItemStack armor = pTarget.getItemBySlot(equipmentSlot);
            if (!armor.isEmpty()){
                ItemEntity itemEntity = new ItemEntity(pTarget.level,
                        pTarget.getX() + pTarget.getRandom().nextDouble(),
                        pTarget.getY() + pTarget.getRandom().nextDouble(),
                        pTarget.getZ() + pTarget.getRandom().nextDouble(), armor);
                itemEntity.setPickUpDelay(80);
                if (pTarget.level.addFreshEntity(itemEntity)){
                    pTarget.setItemSlot(equipmentSlot, ItemStack.EMPTY);
                    if (!pTarget.level.isClientSide) {
                        ModNetwork.sendToALL(new SPlayWorldSoundPacket(pTarget.blockPosition(), SoundEvents.ARMOR_EQUIP_GENERIC, 10.0F, 1.5F));
                    }
                    pTarget.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 10.0F, 1.5F);
                }
            }
        }
    }
}
