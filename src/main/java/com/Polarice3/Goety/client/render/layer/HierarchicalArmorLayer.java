package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.joml.Quaternionf;

import javax.annotation.Nullable;

/**
 * <a href="https://github.com/baguchan/BagusLib/blob/1.20/src/main/java/bagu_chan/bagus_lib/client/layer/CustomArmorLayer.java">...</a>
 * Thanks Alex and Bagu!!
 */
public class HierarchicalArmorLayer<T extends LivingEntity, M extends EntityModel<T> & HierarchicalArmor> extends RenderLayer<T, M> {
    private final HumanoidModel defaultBipedModel;
    private final HumanoidModel innerModel;
    private final RenderLayerParent<T, M> renderer;
    private final TextureAtlas armorTrimAtlas;
    private final EntityRendererProvider.Context context;

    public HierarchicalArmorLayer(RenderLayerParent<T, M> render, EntityRendererProvider.Context context) {
        this(render, context, ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR, ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR);
    }

    public HierarchicalArmorLayer(RenderLayerParent<T, M> render, EntityRendererProvider.Context context, ModelLayerLocation outerArmor, ModelLayerLocation innerArmor) {
        super(render);
        this.defaultBipedModel = new HumanoidModel(context.bakeLayer(outerArmor));
        this.innerModel = new HumanoidModel(context.bakeLayer(innerArmor));
        this.renderer = render;
        this.context = context;
        this.armorTrimAtlas = context.getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
    }

    public static ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        ArmorItem item = (ArmorItem) stack.getItem();
        String texture = item.getMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (usesInnerModel(slot) ? 2 : 1), type == null ? "" : String.format("_%s", type));

        s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
        return new ResourceLocation(s1);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        {
            matrixStackIn.pushPose();
            ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
            if (headItem.getItem() instanceof ArmorItem armoritem) {
                if (headItem.canEquip(EquipmentSlot.HEAD, entity)) {
                    HumanoidModel a = defaultBipedModel;
                    a = getArmorModelHook(entity, headItem, EquipmentSlot.HEAD, a);
                    boolean notAVanillaModel = a != defaultBipedModel;
                    this.setModelSlotVisible(a, EquipmentSlot.HEAD);
                    boolean flag1 = headItem.hasFoil();
                    int clampedLight = packedLightIn;
                    if (armoritem instanceof net.minecraft.world.item.DyeableLeatherItem) { // Allow this for anything, not only cloth
                        int i = ((net.minecraft.world.item.DyeableLeatherItem) armoritem).getColor(headItem);
                        float f = (float) (i >> 16 & 255) / 255.0F;
                        float f1 = (float) (i >> 8 & 255) / 255.0F;
                        float f2 = (float) (i & 255) / 255.0F;
                        renderHelmet(headItem, entity, matrixStackIn, bufferIn, clampedLight, flag1, a, f, f1, f2, getArmorResource(entity, headItem, EquipmentSlot.HEAD, null), notAVanillaModel);
                        renderHelmet(headItem, entity, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(entity, headItem, EquipmentSlot.HEAD, "overlay"), notAVanillaModel);
                    } else {
                        renderHelmet(headItem, entity, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(entity, headItem, EquipmentSlot.HEAD, null), notAVanillaModel);
                    }
                }
            } else {
                if (headItem.is(ItemTags.BANNERS)) {
                    this.renderer.getModel().headPartArmors().forEach(part -> {
                        this.renderer.getModel().translateToHead(part, matrixStackIn);
                        translateToHead(matrixStackIn);
                        this.context.getItemInHandRenderer().renderItem(entity, headItem, ItemDisplayContext.HEAD, false, matrixStackIn, bufferIn, packedLightIn);
                    });
                } else {
                    this.renderer.getModel().headPartArmors().forEach(part -> {
                        this.renderer.getModel().translateToHead(part, matrixStackIn);
                        matrixStackIn.mulPose((new Quaternionf()).rotateX((float) Math.PI));
                        matrixStackIn.mulPose((new Quaternionf()).rotateY((float) Math.PI));
                        matrixStackIn.scale(0.625F, 0.625F, 0.625F);
                        Minecraft.getInstance().getItemRenderer().renderStatic(headItem, ItemDisplayContext.HEAD, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, entity.level(), 0);
                    });
                }
            }
            matrixStackIn.popPose();
        }
        {
            matrixStackIn.pushPose();
            ItemStack chestItem = entity.getItemBySlot(EquipmentSlot.CHEST);
            if (chestItem.getItem() instanceof ArmorItem armoritem) {
                if (armoritem.getEquipmentSlot() == EquipmentSlot.CHEST) {
                    HumanoidModel a = defaultBipedModel;
                    a = getArmorModelHook(entity, chestItem, EquipmentSlot.CHEST, a);
                    boolean notAVanillaModel = a != defaultBipedModel;
                    this.setModelSlotVisible(a, EquipmentSlot.CHEST);

                    boolean flag1 = chestItem.hasFoil();
                    int clampedLight = packedLightIn;
                    if (armoritem instanceof net.minecraft.world.item.DyeableLeatherItem) { // Allow this for anything, not only cloth
                        int i = ((net.minecraft.world.item.DyeableLeatherItem) armoritem).getColor(chestItem);
                        float f = (float) (i >> 16 & 255) / 255.0F;
                        float f1 = (float) (i >> 8 & 255) / 255.0F;
                        float f2 = (float) (i & 255) / 255.0F;
                        renderChestplate(chestItem, entity, matrixStackIn, bufferIn, clampedLight, flag1, a, f, f1, f2, getArmorResource(entity, chestItem, EquipmentSlot.CHEST, null), notAVanillaModel);
                        renderChestplate(chestItem, entity, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(entity, chestItem, EquipmentSlot.CHEST, "overlay"), notAVanillaModel);
                    } else {
                        renderChestplate(chestItem, entity, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(entity, chestItem, EquipmentSlot.CHEST, null), notAVanillaModel);
                    }

                }
            }
            matrixStackIn.popPose();
        }
        {
            matrixStackIn.pushPose();
            ItemStack legItem = entity.getItemBySlot(EquipmentSlot.LEGS);
            if (legItem.getItem() instanceof ArmorItem armoritem) {
                if (armoritem.getEquipmentSlot() == EquipmentSlot.LEGS) {
                    HumanoidModel a = this.innerModel;
                    a = getArmorModelHook(entity, legItem, EquipmentSlot.LEGS, a);
                    boolean notAVanillaModel = a != defaultBipedModel;
                    this.setModelSlotVisible(a, EquipmentSlot.LEGS);

                    boolean flag1 = legItem.hasFoil();
                    int clampedLight = packedLightIn;
                    if (armoritem instanceof net.minecraft.world.item.DyeableLeatherItem) { // Allow this for anything, not only cloth
                        int i = ((net.minecraft.world.item.DyeableLeatherItem) armoritem).getColor(legItem);
                        float f = (float) (i >> 16 & 255) / 255.0F;
                        float f1 = (float) (i >> 8 & 255) / 255.0F;
                        float f2 = (float) (i & 255) / 255.0F;
                        renderLeg(legItem, entity, matrixStackIn, bufferIn, clampedLight, flag1, a, f, f1, f2, getArmorResource(entity, legItem, EquipmentSlot.LEGS, null), notAVanillaModel);
                        renderLeg(legItem, entity, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(entity, legItem, EquipmentSlot.LEGS, "overlay"), notAVanillaModel);
                    } else {
                        renderLeg(legItem, entity, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(entity, legItem, EquipmentSlot.LEGS, null), notAVanillaModel);
                    }

                }
            }
            matrixStackIn.popPose();
        }
        {
            matrixStackIn.pushPose();
            ItemStack feetItem = entity.getItemBySlot(EquipmentSlot.FEET);
            if (feetItem.getItem() instanceof ArmorItem armoritem) {
                if (armoritem.getEquipmentSlot() == EquipmentSlot.FEET) {
                    HumanoidModel a = defaultBipedModel;
                    a = getArmorModelHook(entity, feetItem, EquipmentSlot.FEET, a);
                    boolean notAVanillaModel = a != defaultBipedModel;
                    this.setModelSlotVisible(a, EquipmentSlot.FEET);

                    boolean flag1 = feetItem.hasFoil();
                    int clampedLight = packedLightIn;
                    if (armoritem instanceof net.minecraft.world.item.DyeableLeatherItem) { // Allow this for anything, not only cloth
                        int i = ((net.minecraft.world.item.DyeableLeatherItem) armoritem).getColor(feetItem);
                        float f = (float) (i >> 16 & 255) / 255.0F;
                        float f1 = (float) (i >> 8 & 255) / 255.0F;
                        float f2 = (float) (i & 255) / 255.0F;
                        renderBoot(feetItem, entity, matrixStackIn, bufferIn, clampedLight, flag1, a, f, f1, f2, getArmorResource(entity, feetItem, EquipmentSlot.FEET, null), notAVanillaModel);
                        renderBoot(feetItem, entity, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(entity, feetItem, EquipmentSlot.FEET, "overlay"), notAVanillaModel);
                    } else {
                        renderBoot(feetItem, entity, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(entity, feetItem, EquipmentSlot.FEET, null), notAVanillaModel);
                    }

                }
            }
            matrixStackIn.popPose();
        }

    }

    private static boolean usesInnerModel(EquipmentSlot p_117129_) {
        return p_117129_ == EquipmentSlot.LEGS;
    }

    private HumanoidModel getArmorModel(EquipmentSlot p_117079_) {
        return (usesInnerModel(p_117079_) ? this.innerModel : this.defaultBipedModel);
    }

    public static void translateToHead(PoseStack p_174484_) {
        float f = 0.625F;
        p_174484_.translate(0.0F, -0.25F, 0.0F);
        p_174484_.mulPose(Axis.YP.rotationDegrees(180.0F));
        p_174484_.scale(f, -f, -f);
    }

    private void renderTrim(ModelPart part, ArmorMaterial armorMaterial, PoseStack poseStack, MultiBufferSource pBuffer, int packedLight, ArmorTrim armorTrim, boolean glintIn, boolean innerModel, float red, float green, float blue) {
        TextureAtlasSprite textureatlassprite = this.armorTrimAtlas.getSprite(innerModel ? armorTrim.innerTexture(armorMaterial) : armorTrim.outerTexture(armorMaterial));
        VertexConsumer vertexconsumer = textureatlassprite.wrap(ItemRenderer.getFoilBufferDirect(pBuffer, Sheets.armorTrimsSheet(), false, glintIn));
        part.render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
    }

    private void renderTrim(ModelPart part, ItemStack item, LivingEntity entity, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, boolean glintIn, EquipmentSlot equipmentSlot) {
        if (item.getItem() instanceof ArmorItem armorItem) {
            ArmorTrim.getTrim(entity.level().registryAccess(), item).ifPresent((armorTrim) -> {
                this.renderTrim(part, armorItem.getMaterial(), matrixStackIn, bufferIn, packedLightIn, armorTrim, glintIn, usesInnerModel(equipmentSlot), 1.0F, 1.0F, 1.0F);
            });
        }
    }

    private void renderLeg(ItemStack legItem, LivingEntity entity, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, boolean glintIn, HumanoidModel modelIn, float red, float green, float blue, ResourceLocation armorResource, boolean notAVanillaModel) {
        VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(armorResource), false, glintIn);
        renderer.getModel().copyPropertiesTo(modelIn);
        modelIn.body.xRot = 0F;
        modelIn.body.yRot = 0;
        modelIn.body.zRot = 0;
        modelIn.body.x = 0;
        modelIn.body.y = 0F;
        modelIn.body.z = 0F;
        modelIn.rightLeg.x = 0F;
        modelIn.rightLeg.xRot = 0F;
        modelIn.rightLeg.yRot = 0F;
        modelIn.rightLeg.zRot = 0F;
        modelIn.leftLeg.x = 0F;
        modelIn.leftLeg.xRot = 0F;
        modelIn.leftLeg.yRot = 0F;
        modelIn.leftLeg.zRot = 0F;
        modelIn.leftLeg.y = 0F;
        modelIn.rightLeg.y = 0F;
        modelIn.leftLeg.z = 0F;
        modelIn.rightLeg.z = 0F;
        renderer.getModel().rightLegPartArmors().forEach(part -> {
            matrixStackIn.pushPose();
            renderer.getModel().translateToLeg(part, matrixStackIn);
            modelIn.rightLeg.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
            matrixStackIn.popPose();
        });
        renderer.getModel().leftLegPartArmors().forEach(part -> {
            matrixStackIn.pushPose();
            renderer.getModel().translateToLeg(part, matrixStackIn);
            modelIn.leftLeg.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
            matrixStackIn.popPose();
        });
        renderer.getModel().bodyPartArmors().forEach(part -> {
            matrixStackIn.pushPose();
            renderer.getModel().translateToChest(part, matrixStackIn);
            modelIn.body.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
            matrixStackIn.popPose();
        });
        if (MobsConfig.HierarchicalArmorTrim.get()) {
            ArmorTrim.getTrim(entity.level().registryAccess(), legItem).ifPresent(armorTrim -> {
                if (legItem.getItem() instanceof ArmorItem armorItem) {
                    TextureAtlasSprite sprite = this.armorTrimAtlas.getSprite(armorTrim.innerTexture(armorItem.getMaterial()));
                    renderer.getModel().rightLegPartArmors().forEach(part -> {
                        matrixStackIn.pushPose();
                        renderer.getModel().translateToLeg(part, matrixStackIn);
                        VertexConsumer consumer = sprite.wrap(ItemRenderer.getFoilBufferDirect(bufferIn, Sheets.armorTrimsSheet(), false, glintIn));
                        modelIn.rightLeg.render(matrixStackIn, consumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                        matrixStackIn.popPose();
                    });
                    renderer.getModel().leftLegPartArmors().forEach(part -> {
                        matrixStackIn.pushPose();
                        renderer.getModel().translateToLeg(part, matrixStackIn);
                        VertexConsumer consumer = sprite.wrap(ItemRenderer.getFoilBufferDirect(bufferIn, Sheets.armorTrimsSheet(), false, glintIn));
                        modelIn.leftLeg.render(matrixStackIn, consumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                        matrixStackIn.popPose();
                    });
                    renderer.getModel().bodyPartArmors().forEach(part -> {
                        matrixStackIn.pushPose();
                        renderer.getModel().translateToChest(part, matrixStackIn);
                        VertexConsumer consumer = sprite.wrap(ItemRenderer.getFoilBufferDirect(bufferIn, Sheets.armorTrimsSheet(), false, glintIn));
                        modelIn.body.render(matrixStackIn, consumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                        matrixStackIn.popPose();
                    });
                }
            });
        }
    }

    private void renderBoot(ItemStack feetItem, LivingEntity entity, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, boolean glintIn, HumanoidModel modelIn, float red, float green, float blue, ResourceLocation armorResource, boolean notAVanillaModel) {
        VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(armorResource), false, glintIn);
        renderer.getModel().copyPropertiesTo(modelIn);
        modelIn.rightLeg.x = 0F;
        modelIn.rightLeg.xRot = 0F;
        modelIn.rightLeg.yRot = 0F;
        modelIn.rightLeg.zRot = 0F;
        modelIn.leftLeg.x = 0F;
        modelIn.leftLeg.xRot = 0F;
        modelIn.leftLeg.yRot = 0F;
        modelIn.leftLeg.zRot = 0F;
        modelIn.leftLeg.y = 0F;
        modelIn.rightLeg.y = 0F;
        modelIn.leftLeg.z = 0F;
        modelIn.rightLeg.z = 0F;
        renderer.getModel().rightLegPartArmors().forEach(part -> {
            matrixStackIn.pushPose();
            renderer.getModel().translateToLeg(part, matrixStackIn);
            modelIn.rightLeg.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
            matrixStackIn.popPose();
        });
        renderer.getModel().leftLegPartArmors().forEach(part -> {
            matrixStackIn.pushPose();
            renderer.getModel().translateToLeg(part, matrixStackIn);
            modelIn.leftLeg.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
            matrixStackIn.popPose();
        });
        if (MobsConfig.HierarchicalArmorTrim.get()) {
            ArmorTrim.getTrim(entity.level().registryAccess(), feetItem).ifPresent(armorTrim -> {
                if (feetItem.getItem() instanceof ArmorItem armorItem) {
                    TextureAtlasSprite sprite = this.armorTrimAtlas.getSprite(armorTrim.outerTexture(armorItem.getMaterial()));
                    renderer.getModel().rightLegPartArmors().forEach(part -> {
                        matrixStackIn.pushPose();
                        renderer.getModel().translateToLeg(part, matrixStackIn);
                        VertexConsumer consumer = sprite.wrap(ItemRenderer.getFoilBufferDirect(bufferIn, Sheets.armorTrimsSheet(), false, glintIn));
                        modelIn.rightLeg.render(matrixStackIn, consumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                        matrixStackIn.popPose();
                    });
                    renderer.getModel().leftLegPartArmors().forEach(part -> {
                        matrixStackIn.pushPose();
                        renderer.getModel().translateToLeg(part, matrixStackIn);
                        VertexConsumer consumer = sprite.wrap(ItemRenderer.getFoilBufferDirect(bufferIn, Sheets.armorTrimsSheet(), false, glintIn));
                        modelIn.leftLeg.render(matrixStackIn, consumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                        matrixStackIn.popPose();
                    });
                }
            });
        }
    }


    private void renderChestplate(ItemStack chestItem, LivingEntity entity, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, boolean glintIn, HumanoidModel modelIn, float red, float green, float blue, ResourceLocation armorResource, boolean notAVanillaModel) {
        VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(armorResource), false, glintIn);
        renderer.getModel().copyPropertiesTo(modelIn);
        modelIn.body.xRot = 0F;
        modelIn.body.yRot = 0;
        modelIn.body.zRot = 0;
        modelIn.body.x = 0;
        modelIn.body.y = 0F;
        modelIn.body.z = 0F;
        modelIn.rightArm.x = 0F;
        modelIn.rightArm.y = 0F;
        modelIn.rightArm.z = 0F;
        modelIn.rightArm.xRot = 0F;
        modelIn.rightArm.yRot = 0F;
        modelIn.rightArm.zRot = 0F;
        modelIn.leftArm.x = 0F;
        modelIn.leftArm.y = 0F;
        modelIn.leftArm.z = 0F;
        modelIn.leftArm.xRot = 0F;
        modelIn.leftArm.yRot = 0F;
        modelIn.leftArm.zRot = 0F;
        modelIn.leftArm.y = 0F;
        modelIn.rightArm.y = 0F;
        modelIn.leftArm.z = 0F;
        modelIn.rightArm.z = 0F;
        renderer.getModel().rightHandArmors().forEach(part -> {
            matrixStackIn.pushPose();
            renderer.getModel().translateToArms(part, matrixStackIn);
            modelIn.rightArm.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
            matrixStackIn.popPose();
        });
        renderer.getModel().leftHandArmors().forEach(part -> {
            matrixStackIn.pushPose();
            renderer.getModel().translateToArms(part, matrixStackIn);
            modelIn.leftArm.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
            matrixStackIn.popPose();
        });
        renderer.getModel().bodyPartArmors().forEach(part -> {
            matrixStackIn.pushPose();
            renderer.getModel().translateToChest(part, matrixStackIn);
            modelIn.body.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
            matrixStackIn.popPose();
        });
        if (MobsConfig.HierarchicalArmorTrim.get()) {
            ArmorTrim.getTrim(entity.level().registryAccess(), chestItem).ifPresent(armorTrim -> {
                if (chestItem.getItem() instanceof ArmorItem armorItem) {
                    TextureAtlasSprite sprite = this.armorTrimAtlas.getSprite(armorTrim.outerTexture(armorItem.getMaterial()));
                    renderer.getModel().rightHandArmors().forEach(part -> {
                        matrixStackIn.pushPose();
                        renderer.getModel().translateToArms(part, matrixStackIn);
                        VertexConsumer consumer = sprite.wrap(ItemRenderer.getFoilBufferDirect(bufferIn, Sheets.armorTrimsSheet(), false, glintIn));
                        modelIn.rightArm.render(matrixStackIn, consumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                        matrixStackIn.popPose();
                    });
                    renderer.getModel().leftHandArmors().forEach(part -> {
                        matrixStackIn.pushPose();
                        renderer.getModel().translateToArms(part, matrixStackIn);
                        VertexConsumer consumer = sprite.wrap(ItemRenderer.getFoilBufferDirect(bufferIn, Sheets.armorTrimsSheet(), false, glintIn));
                        modelIn.leftArm.render(matrixStackIn, consumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                        matrixStackIn.popPose();
                    });
                    renderer.getModel().bodyPartArmors().forEach(part -> {
                        matrixStackIn.pushPose();
                        renderer.getModel().translateToChest(part, matrixStackIn);
                        VertexConsumer consumer = sprite.wrap(ItemRenderer.getFoilBufferDirect(bufferIn, Sheets.armorTrimsSheet(), false, glintIn));
                        modelIn.body.render(matrixStackIn, consumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                        matrixStackIn.popPose();
                    });
                }
            });
        }
    }

    private void renderHelmet(ItemStack headItem, LivingEntity entity, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, boolean glintIn, HumanoidModel modelIn, float red, float green, float blue, ResourceLocation armorResource, boolean notAVanillaModel) {
        VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(armorResource), false, glintIn);
        renderer.getModel().copyPropertiesTo(modelIn);
        modelIn.head.xRot = 0F;
        modelIn.head.yRot = 0F;
        modelIn.head.zRot = 0F;
        modelIn.hat.xRot = 0F;
        modelIn.hat.yRot = 0F;
        modelIn.hat.zRot = 0F;
        modelIn.head.x = 0F;
        modelIn.head.y = 0F;
        modelIn.head.z = 0F;
        modelIn.hat.x = 0F;
        modelIn.hat.y = 0F;
        modelIn.hat.z = 0F;
        renderer.getModel().headPartArmors().forEach(part -> {
            matrixStackIn.pushPose();
            renderer.getModel().translateToHead(part, matrixStackIn);
            modelIn.head.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
            matrixStackIn.popPose();
        });
        if (MobsConfig.HierarchicalArmorTrim.get()) {
            ArmorTrim.getTrim(entity.level().registryAccess(), headItem).ifPresent(armorTrim -> {
                if (headItem.getItem() instanceof ArmorItem armorItem) {
                    TextureAtlasSprite sprite = this.armorTrimAtlas.getSprite(armorTrim.outerTexture(armorItem.getMaterial()));
                    renderer.getModel().headPartArmors().forEach(part -> {
                        matrixStackIn.pushPose();
                        renderer.getModel().translateToHead(part, matrixStackIn);
                        VertexConsumer consumer = sprite.wrap(ItemRenderer.getFoilBufferDirect(bufferIn, Sheets.armorTrimsSheet(), false, glintIn));
                        modelIn.head.render(matrixStackIn, consumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                        matrixStackIn.popPose();
                    });
                }
            });
        }
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
