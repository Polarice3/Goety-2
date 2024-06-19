package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class EeriePickaxeItem extends PickaxeItem {
    public EeriePickaxeItem() {
        super(ModTiers.SPECIAL, 1, -2.8F, (new Properties()).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof Player player) {
            if (isSelected){
                if (worldIn.random.nextFloat() <= 7.5E-4F){
                    int i = 17;
                    BlockPos blockpos = BlockPos.containing(player.getX() + (double)worldIn.random.nextInt(i) - (double)8, player.getEyeY() + (double)worldIn.random.nextInt(i) - (double)8, player.getZ() + (double)worldIn.random.nextInt(i) - (double)8);
                    double d0 = (double)blockpos.getX() + 0.5D;
                    double d1 = (double)blockpos.getY() + 0.5D;
                    double d2 = (double)blockpos.getZ() + 0.5D;
                    double d3 = d0 - player.getX();
                    double d4 = d1 - player.getEyeY();
                    double d5 = d2 - player.getZ();
                    double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                    double d7 = d6 + 2.0D;
                    BlockPos blockPos = BlockPos.containing(player.getX() + d3 / d6 * d7, player.getEyeY() + d4 / d6 * d7, player.getZ() + d5 / d6 * d7);
                    SoundEvent soundEvent = SoundEvents.AMBIENT_CAVE.get();
                    if (worldIn.random.nextFloat() <= 0.01F){
                        soundEvent = SoundEvents.GOAT_SCREAMING_AMBIENT;
                    } else if (worldIn.random.nextFloat() <= 0.05F){
                        soundEvent = SoundEvents.WARDEN_NEARBY_CLOSE;
                    } else if (worldIn.random.nextFloat() <= 0.15F){
                        soundEvent = SoundEvents.SCULK_SHRIEKER_SHRIEK;
                    } else if (worldIn.random.nextFloat() <= 0.25F){
                        soundEvent = SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.get();
                    }
                    worldIn.playSound(player, blockPos, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
                if (!MobUtil.isShifting(player)){
                    for (Entity entity : worldIn.getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(10.0F))){
                        if (entity instanceof ExperienceOrb experienceOrb){
                            experienceOrb.playerTouch(player);
                        }
                    }
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = MultimapBuilder.hashKeys().hashSetValues().build(super.getAttributeModifiers(slot, stack));
        map.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(UUID.fromString("36907ccf-728b-4ae4-abf1-eaf3283838b5"), "Tool Modifier", 3, AttributeModifier.Operation.ADDITION));
        return slot == EquipmentSlot.MAINHAND ? map : super.getAttributeModifiers(slot, stack);
    }
}
