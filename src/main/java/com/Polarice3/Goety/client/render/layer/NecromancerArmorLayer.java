package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.client.render.AbstractNecromancerRenderer;
import com.Polarice3.Goety.client.render.model.NecromancerModel;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * Based of @AlexModGuy's Kangaroo Armor Layer: <a href="https://github.com/AlexModGuy/AlexsMobs/blob/1.19/src/main/java/com/github/alexthe666/alexsmobs/client/render/layer/LayerKangarooArmor.java">...</a>
 */
public class NecromancerArmorLayer extends RenderLayer<AbstractNecromancer, NecromancerModel<AbstractNecromancer>> {
    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
    private final HumanoidModel defaultBipedModel;
    private AbstractNecromancerRenderer renderer;

    public NecromancerArmorLayer(AbstractNecromancerRenderer render, EntityRendererProvider.Context context) {
        super(render);
        defaultBipedModel = new HumanoidModel(context.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR));
        this.renderer = render;
    }

    public static ResourceLocation getArmorResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @javax.annotation.Nullable String type) {
        ArmorItem item = (ArmorItem) stack.getItem();
        String texture = item.getMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (1), type == null ? "" : String.format("_%s", type));

        s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
        ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s1);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(s1);
            ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
        }

        return resourcelocation;
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, AbstractNecromancer roo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStackIn.pushPose();
        if(!roo.isBaby()) {
            matrixStackIn.pushPose();
            ItemStack itemstack = roo.getItemBySlot(EquipmentSlot.HEAD);
            if (itemstack.getItem() instanceof ArmorItem armoritem) {
                if (itemstack.canEquip(EquipmentSlot.HEAD, roo)) {
                    HumanoidModel<?> a = defaultBipedModel;
                    a = getArmorModelHook(roo, itemstack, EquipmentSlot.HEAD, a);
                    boolean notAVanillaModel = a != defaultBipedModel;
                    this.setModelSlotVisible(a, EquipmentSlot.HEAD);
                    translateToHead(matrixStackIn);
                    matrixStackIn.translate(0, 0.0F, 0.0F);
                    matrixStackIn.scale(1.0F, 1.0F, 1.0F);
                    boolean flag1 = itemstack.hasFoil();
                    if (armoritem instanceof net.minecraft.world.item.DyeableLeatherItem) { // Allow this for anything, not only cloth
                        int i = ((net.minecraft.world.item.DyeableLeatherItem) armoritem).getColor(itemstack);
                        float f = (float) (i >> 16 & 255) / 255.0F;
                        float f1 = (float) (i >> 8 & 255) / 255.0F;
                        float f2 = (float) (i & 255) / 255.0F;
                        renderHelmet(roo, matrixStackIn, bufferIn, packedLightIn, flag1, a, f, f1, f2, getArmorResource(roo, itemstack, EquipmentSlot.HEAD, null), notAVanillaModel);
                        renderHelmet(roo, matrixStackIn, bufferIn, packedLightIn, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack, EquipmentSlot.HEAD, "overlay"), notAVanillaModel);
                    } else {
                        renderHelmet(roo, matrixStackIn, bufferIn, packedLightIn, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack, EquipmentSlot.HEAD, null), notAVanillaModel);
                    }
                }
            } else {
                translateToHead(matrixStackIn);
                matrixStackIn.translate(0, -0.2, -0.1F);
                matrixStackIn.mulPose(new Quaternion(Vector3f.XP, 180, true));
                matrixStackIn.mulPose(new Quaternion(Vector3f.YP, 180, true));
                matrixStackIn.scale(1.0F, 1.0F, 1.0F);
                Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemTransforms.TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 0);
            }
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            ItemStack itemstack2 = roo.getItemBySlot(EquipmentSlot.CHEST);
            if (itemstack2.getItem() instanceof ArmorItem armoritem) {
                if (armoritem.getSlot() == EquipmentSlot.CHEST) {
                    HumanoidModel<?> a = defaultBipedModel;
                    a = getArmorModelHook(roo, itemstack2, EquipmentSlot.CHEST, a);
                    boolean notAVanillaModel = a != defaultBipedModel;
                    this.setModelSlotVisible(a, EquipmentSlot.CHEST);
                    translateToChest(matrixStackIn);
                    matrixStackIn.translate(0, 0.0F, 0F);
                    matrixStackIn.scale(1F, 1F, 1F);
                    boolean flag1 = itemstack2.hasFoil();
                    if (armoritem instanceof net.minecraft.world.item.DyeableLeatherItem) { // Allow this for anything, not only cloth
                        int i = ((net.minecraft.world.item.DyeableLeatherItem) armoritem).getColor(itemstack2);
                        float f = (float) (i >> 16 & 255) / 255.0F;
                        float f1 = (float) (i >> 8 & 255) / 255.0F;
                        float f2 = (float) (i & 255) / 255.0F;
                        renderChestplate(roo, matrixStackIn, bufferIn, packedLightIn, flag1, a, f, f1, f2, getArmorResource(roo, itemstack2, EquipmentSlot.CHEST, null), notAVanillaModel);
                        renderChestplate(roo, matrixStackIn, bufferIn, packedLightIn, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack2, EquipmentSlot.CHEST, "overlay"), notAVanillaModel);
                    } else {
                        renderChestplate(roo, matrixStackIn, bufferIn, packedLightIn, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack2, EquipmentSlot.CHEST, null), notAVanillaModel);
                    }

                }
            }
            matrixStackIn.popPose();
        }
        matrixStackIn.popPose();

    }

    private void translateToHead(PoseStack matrixStackIn) {
        translateToChest(matrixStackIn);
        /*this.renderer.getModel().head.translateAndRotate(matrixStackIn);*/
    }

    private void translateToChest(PoseStack matrixStackIn) {
        this.renderer.getModel().skeleton.translateAndRotate(matrixStackIn);
/*        this.renderer.getModel().root.translateAndRotate(matrixStackIn);
        this.renderer.getModel().body.translateAndRotate(matrixStackIn);*/
    }

    private void renderChestplate(AbstractNecromancer entity, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, boolean glintIn, HumanoidModel modelIn, float red, float green, float blue, ResourceLocation armorResource, boolean notAVanillaModel) {
        VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(armorResource), false, glintIn);
        renderer.getModel().copyPropertiesTo(modelIn);
        modelIn.body.xRot = renderer.getModel().body.xRot;
        modelIn.body.yRot = renderer.getModel().body.yRot;
        modelIn.body.zRot = renderer.getModel().body.zRot;
        modelIn.body.x = renderer.getModel().body.x;
        modelIn.body.y = renderer.getModel().body.y - 12.0F;
        modelIn.body.z = renderer.getModel().body.z;
        modelIn.rightArm.x = renderer.getModel().rightArm.x;
        modelIn.rightArm.y = renderer.getModel().rightArm.y - 12.0F;
        modelIn.rightArm.z = renderer.getModel().rightArm.z;
        modelIn.rightArm.xRot = renderer.getModel().rightArm.xRot;
        modelIn.rightArm.yRot = renderer.getModel().rightArm.yRot;
        modelIn.rightArm.zRot = renderer.getModel().rightArm.zRot;
        modelIn.leftArm.x = renderer.getModel().leftArm.x;
        modelIn.leftArm.y = renderer.getModel().leftArm.y - 12.0F;
        modelIn.leftArm.z = renderer.getModel().leftArm.z;
        modelIn.leftArm.xRot = renderer.getModel().leftArm.xRot;
        modelIn.leftArm.yRot = renderer.getModel().leftArm.yRot;
        modelIn.leftArm.zRot = renderer.getModel().leftArm.zRot;
        modelIn.body.visible = false;
        modelIn.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
        modelIn.body.visible = true;
        modelIn.rightArm.visible = false;
        modelIn.leftArm.visible = false;
        matrixStackIn.pushPose();
        matrixStackIn.scale(1.0F, 1.0F, 1.0F);
        modelIn.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
        matrixStackIn.popPose();
        modelIn.rightArm.visible = true;
        modelIn.leftArm.visible = true;
    }

    private void renderHelmet(AbstractNecromancer entity, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, boolean glintIn, HumanoidModel modelIn, float red, float green, float blue, ResourceLocation armorResource, boolean notAVanillaModel) {
        VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(armorResource), false, glintIn);
        renderer.getModel().copyPropertiesTo(modelIn);
        modelIn.head.xRot = renderer.getModel().head.xRot;
        modelIn.head.yRot = renderer.getModel().head.yRot;
        modelIn.head.zRot = renderer.getModel().head.zRot;
        modelIn.hat.xRot = 0F;
        modelIn.hat.yRot = 0F;
        modelIn.hat.zRot = 0F;
        modelIn.head.x = renderer.getModel().head.x;
        modelIn.head.y = renderer.getModel().head.y - 12.0F;
        modelIn.head.z = renderer.getModel().head.z;
        modelIn.hat.x = 0F;
        modelIn.hat.y = 0F;
        modelIn.hat.z = 0F;
        modelIn.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);

    }

    protected void setModelSlotVisible(HumanoidModel p_188359_1_, EquipmentSlot slotIn) {
        this.setModelVisible(p_188359_1_);
        switch (slotIn) {
            case HEAD:
                p_188359_1_.head.visible = true;
                p_188359_1_.hat.visible = true;
                break;
            case CHEST:
                p_188359_1_.body.visible = true;
                p_188359_1_.rightArm.visible = true;
                p_188359_1_.leftArm.visible = true;
                break;
            case LEGS:
                p_188359_1_.body.visible = true;
                p_188359_1_.rightLeg.visible = true;
                p_188359_1_.leftLeg.visible = true;
                break;
            case FEET:
                p_188359_1_.rightLeg.visible = true;
                p_188359_1_.leftLeg.visible = true;
        }
    }

    protected void setModelVisible(HumanoidModel model) {
        model.setAllVisible(false);
    }

    protected HumanoidModel<?> getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel model) {
        Model basicModel = net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
        return basicModel instanceof HumanoidModel ? (HumanoidModel<?>) basicModel : model;
    }

}
