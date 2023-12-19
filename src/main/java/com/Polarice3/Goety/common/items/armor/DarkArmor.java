package com.Polarice3.Goety.common.items.armor;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.DarkArmorModel;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class DarkArmor extends ArmorItem implements ISoulRepair {
    public DarkArmor(EquipmentSlot p_40387_) {
        super(ModArmorMaterials.DARK, p_40387_, ModItems.baseProperities());
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String layer) {
        if (slot == EquipmentSlot.LEGS) {
            return Goety.location("textures/models/armor/dark_armor_layer.png").toString();
        } else {
            return Goety.location("textures/models/armor/dark_armor.png").toString();
        }
    }

    public static int getSoulDiscount(){
        return 5;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
           public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
               EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
               ModelPart root = modelSet.bakeLayer(slot == EquipmentSlot.LEGS ? ModModelLayer.DARK_ARMOR_INNER : ModModelLayer.DARK_ARMOR_OUTER);
               DarkArmorModel model = new DarkArmorModel(root).animate(livingEntity);
               model.hat.visible = equipmentSlot == EquipmentSlot.HEAD;
               model.body.visible = equipmentSlot == EquipmentSlot.CHEST;
               model.rightArm.visible = equipmentSlot == EquipmentSlot.CHEST;
               model.leftArm.visible = equipmentSlot == EquipmentSlot.CHEST;
               model.bottom.visible = equipmentSlot == EquipmentSlot.LEGS;
               model.rightLeg.visible = equipmentSlot == EquipmentSlot.FEET;
               model.leftLeg.visible = equipmentSlot == EquipmentSlot.FEET;

               if (livingEntity instanceof AbstractClientPlayer player){
                   if (player.isCapeLoaded() && player.isModelPartShown(PlayerModelPart.CAPE) && player.getCloakTextureLocation() != null){
                       model.cape.visible = false;
                   }
               }

               model.young = original.young;
               model.crouching = original.crouching;
               model.riding = original.riding;
               model.rightArmPose = original.rightArmPose;
               model.leftArmPose = original.leftArmPose;

               return model;
           }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.literal(String.valueOf(getSoulDiscount())).append("% ").append(Component.translatable("info.goety.armor.discount")).withStyle(ChatFormatting.DARK_AQUA));
    }
}
