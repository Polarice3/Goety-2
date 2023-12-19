package com.Polarice3.Goety.client.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.api.magic.IEverChargeSpell;
import com.Polarice3.Goety.client.audio.BossLoopMusic;
import com.Polarice3.Goety.client.audio.ItemLoopSound;
import com.Polarice3.Goety.client.audio.LoopSound;
import com.Polarice3.Goety.client.gui.screen.inventory.BrewRadialMenuScreen;
import com.Polarice3.Goety.client.gui.screen.inventory.FocusRadialMenuScreen;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.WearRenderer;
import com.Polarice3.Goety.client.render.model.LichModeModel;
import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.OwnedBlockEntity;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.projectiles.CorruptedBeam;
import com.Polarice3.Goety.common.items.curios.GloveItem;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.*;
import com.Polarice3.Goety.common.network.client.brew.CBrewBagKeyPacket;
import com.Polarice3.Goety.init.ModKeybindings;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.mixin.PlayerRendererAccessor;
import com.Polarice3.Goety.utils.*;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (event.getLevel() instanceof ClientLevel){
            Minecraft minecraft = Minecraft.getInstance();
            SoundManager soundHandler = minecraft.getSoundManager();
            if (MainConfig.BossMusic.get()) {
                if (entity instanceof Apostle && !((Apostle) entity).isNoAi()) {
                    minecraft.getMusicManager().stopPlaying();
                    minecraft.gui.setNowPlaying(Component.translatable("item.goety.music_disc_apostle.desc"));
                    soundHandler.play(new BossLoopMusic(ModSounds.APOSTLE_THEME.get(), (Apostle) entity));
                }
                if (entity instanceof Vizier && !((Vizier) entity).isNoAi()) {
                    minecraft.getMusicManager().stopPlaying();
                    minecraft.gui.setNowPlaying(Component.translatable("item.goety.music_disc_vizier.desc"));
                    soundHandler.play(new BossLoopMusic(ModSounds.VIZIER_THEME.get(), (Vizier) entity));
                }
            }
            if (entity instanceof CorruptedBeam){
                soundHandler.play(new LoopSound(ModSounds.CORRUPT_BEAM_LOOP.get(), entity));
                soundHandler.play(new LoopSound(ModSounds.CORRUPT_BEAM_SOUL.get(), entity));
            }
        }
    }

    public static float PARTIAL_TICK = 0;

    @SubscribeEvent
    public static void clientTick(TickEvent.RenderTickEvent event){
        if (event.phase == TickEvent.Phase.START){
            PARTIAL_TICK = event.renderTickTime;
        }
    }

    @SubscribeEvent
    public static void onItemUse(LivingEntityUseItemEvent.Start event){
        if (event.getEntity().level instanceof ClientLevel){
            Minecraft minecraft = Minecraft.getInstance();
            SoundManager soundHandler = minecraft.getSoundManager();
            if (WandUtil.getSpell(event.getEntity()) instanceof IEverChargeSpell spells){
                if (spells.loopSound() != null) {
                    soundHandler.play(new ItemLoopSound(spells.loopSound(), event.getEntity()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void renderGlove(RenderArmEvent event){
        if (event.isCanceled() || !MainConfig.FirstPersonGloves.get()){
            return;
        }

        if (CuriosFinder.hasCurio(event.getPlayer(), itemStack -> itemStack.getItem() instanceof GloveItem)){
            ItemStack itemStack = CuriosFinder.findCurio(event.getPlayer(), itemStack1 -> itemStack1.getItem() instanceof GloveItem);
            WearRenderer renderer = WearRenderer.getRenderer(itemStack);
            if (renderer != null) {
                renderer.renderFirstPersonArm(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getPlayer(), event.getArm(), itemStack.hasFoil());
            }
        }
    }

    @SubscribeEvent
    public static void renderArm(RenderArmEvent event){
        final AbstractClientPlayer player = event.getPlayer();
        if (!player.isSpectator() && LichdomHelper.isInLichMode(player)){
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            final int i = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(false));
            final ResourceLocation texture = Goety.location("textures/entity/lich.png");
            LichModeModel<?> lichModeModel = new LichModeModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayer.LICH));
            if (event.getArm() == HumanoidArm.RIGHT) {
                lichModeModel.rightArm.render(poseStack, event.getMultiBufferSource().getBuffer(RenderType.entityTranslucent(texture)), event.getPackedLight(), i);
                event.setCanceled(true);
            } else if (event.getArm() == HumanoidArm.LEFT) {
                lichModeModel.leftArm.render(poseStack, event.getMultiBufferSource().getBuffer(RenderType.entityTranslucent(texture)), event.getPackedLight(), i);
                event.setCanceled(true);
            }
            poseStack.popPose();
        }
    }

    @SubscribeEvent
    public static void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
        final Player player = event.getEntity();
        final PlayerRenderer playerRenderer = event.getRenderer();
        PlayerModel<?> playerModel = playerRenderer.getModel();
        if (LichdomHelper.isInLichMode(player)){
            playerModel.head.visible = false;
            playerModel.hat.visible = false;
            playerModel.body.visible = false;
            playerModel.jacket.visible = false;
            playerModel.leftArm.visible = false;
            playerModel.leftSleeve.visible = false;
            playerModel.rightArm.visible = false;
            playerModel.rightSleeve.visible = false;
            playerModel.leftLeg.visible = false;
            playerModel.leftPants.visible = false;
            playerModel.rightLeg.visible = false;
            playerModel.rightPants.visible = false;
        }
    }

    @SubscribeEvent
    public static void onPlayerRenderPost(RenderPlayerEvent.Post event) {
        final Player player = event.getEntity();
        final PlayerRenderer playerRenderer = event.getRenderer();
        PlayerModel<?> playerModel = playerRenderer.getModel();
        if (LichdomHelper.isInLichMode(player)){
            final ResourceLocation texture = Goety.location("textures/entity/lich.png");
            final PoseStack poseStack = event.getPoseStack();
            final int packedLight = event.getPackedLight();
            LichModeModel<?> lichModeModel = new LichModeModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayer.LICH));

            final int i = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(false));

            poseStack.pushPose();

            if (player instanceof AbstractClientPlayer clientPlayer) {
                setupRotation(poseStack, clientPlayer, playerRenderer, event.getPartialTick());
            }

            poseStack.scale(0.9375F, 0.9375F, 0.9375F);
            poseStack.translate(0.0F, (1.0F / 16.0F) * 24.0F, 0.0F);
            poseStack.scale(1.0F, -1.0F, -1.0F);

            lichModeModel.head.loadPose(playerModel.head.storePose());
            lichModeModel.body.loadPose(playerModel.body.storePose());
            lichModeModel.leftArm.loadPose(playerModel.leftArm.storePose());
            lichModeModel.rightArm.loadPose(playerModel.rightArm.storePose());
            lichModeModel.leftLeg.loadPose(playerModel.leftLeg.storePose());
            lichModeModel.rightLeg.loadPose(playerModel.rightLeg.storePose());

            poseStack.mulPose(Axis.YN.rotationDegrees(180F));

            for (ModelPart modelPart : lichModeModel.modifiedBodyParts()){
                modelPart.render(poseStack, event.getMultiBufferSource().getBuffer(RenderType.entityTranslucent(texture)), packedLight, i);
            }

            poseStack.popPose();
        }
    }

    @SubscribeEvent
    public static void renderLichHUD(final RenderGuiOverlayEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        final Player player = minecraft.player;

        if (LichdomHelper.isLich(player)){
            if (event.getOverlay().id() == VanillaGuiOverlay.FOOD_LEVEL.id()){
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void renderArcaAmount(final RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        final Player player = minecraft.player;

        if (player != null) {
            HitResult hitResult = minecraft.hitResult;
            Font fontRenderer = minecraft.font;
            PoseStack poseStack = event.getGuiGraphics().pose();
            if (minecraft.level != null) {
                if (hitResult instanceof BlockHitResult blockRayTraceResult) {
                    BlockEntity blockEntity = minecraft.level.getBlockEntity(blockRayTraceResult.getBlockPos());
                    if (blockEntity instanceof ArcaBlockEntity arcaTile) {
                        if ((player.isShiftKeyDown() || player.isCrouching())) {
                            if (arcaTile.getPlayer() == player && SEHelper.getSEActive(player)) {
                                poseStack.pushPose();
                                poseStack.translate((float) (minecraft.getWindow().getGuiScaledWidth() / 2), (float) (minecraft.getWindow().getGuiScaledHeight() - 68), 0.0F);
                                RenderSystem.enableBlend();
                                RenderSystem.defaultBlendFunc();
                                int SoulEnergy = SEHelper.getSESouls(player);
                                int SoulEnergyTotal = MainConfig.MaxArcaSouls.get();
                                String s = "Soul Energy: " + SoulEnergy + "/" + "" + SoulEnergyTotal;
                                int l = fontRenderer.width(s);
                                event.getGuiGraphics().drawString(fontRenderer, s, (-l / 2), -4, 0xFFFFFF);
                                RenderSystem.disableBlend();
                                poseStack.popPose();
                            } else if (arcaTile.getPlayer() != null) {
                                poseStack.pushPose();
                                poseStack.translate((float)(minecraft.getWindow().getGuiScaledWidth() / 2), (float)(minecraft.getWindow().getGuiScaledHeight() - 68), 0.0F);
                                RenderSystem.enableBlend();
                                RenderSystem.defaultBlendFunc();
                                String s = "Owner:" + arcaTile.getPlayer().getDisplayName().getString();
                                int l = fontRenderer.width(s);
                                event.getGuiGraphics().drawString(fontRenderer, s, (-l / 2), -4, 0xFFFFFF);
                                RenderSystem.disableBlend();
                                poseStack.popPose();
                            }
                        }
                    } else if (blockEntity instanceof OwnedBlockEntity ownedBlock && ownedBlock.screenView()){
                        if ((player.isShiftKeyDown() || player.isCrouching()) && ownedBlock.getPlayer() != null){
                            poseStack.pushPose();
                            poseStack.translate((float)(minecraft.getWindow().getGuiScaledWidth() / 2), (float)(minecraft.getWindow().getGuiScaledHeight() - 68), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            String s = "Owner: " + ownedBlock.getPlayer().getDisplayName().getString();
                            int l = fontRenderer.width(s);
                            event.getGuiGraphics().drawString(fontRenderer, s, (-l / 2), -4, 0xFFFFFF);
                            RenderSystem.disableBlend();
                            poseStack.popPose();
                        }
                    } else if (blockEntity instanceof CursedCageBlockEntity cageBlockEntity){
                        if (player.isShiftKeyDown() || player.isCrouching() && !cageBlockEntity.getItem().isEmpty()){
                            poseStack.pushPose();
                            poseStack.translate((float)(minecraft.getWindow().getGuiScaledWidth() / 2), (float)(minecraft.getWindow().getGuiScaledHeight() - 68), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            String s = "Soul Energy: " + cageBlockEntity.getSouls();
                            int l = fontRenderer.width(s);
                            event.getGuiGraphics().drawString(fontRenderer, s, (-l / 2), -4, 0xFFFFFF);
                            RenderSystem.disableBlend();
                            poseStack.popPose();
                        }
                    } else if (blockEntity instanceof BrewCauldronBlockEntity cauldronBlock){
                        if (player.isShiftKeyDown() || player.isCrouching()){
                            poseStack.pushPose();
                            poseStack.translate((float)(minecraft.getWindow().getGuiScaledWidth() / 2), (float)(minecraft.getWindow().getGuiScaledHeight() - 60), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            String s1 = "Capacity: " + cauldronBlock.getCapacityUsed() + "/" + cauldronBlock.getCapacity();
                            int l2 = fontRenderer.width(s1);
                            event.getGuiGraphics().drawString(fontRenderer, s1, (-l2 / 2), -4, 0xFFFFFF);
                            RenderSystem.disableBlend();
                            poseStack.popPose();
                            poseStack.pushPose();
                            poseStack.translate((float)(minecraft.getWindow().getGuiScaledWidth() / 2), (float)(minecraft.getWindow().getGuiScaledHeight() - 68), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            String s = "Soul Cost: " + (cauldronBlock.getBrewCost() - cauldronBlock.soulTime);
                            int l = fontRenderer.width(s);
                            event.getGuiGraphics().drawString(fontRenderer, s, (-l / 2), -4, 0xFFFFFF);
                            RenderSystem.disableBlend();
                            poseStack.popPose();
                        }
                    }
                }
            }
        }
    }

    /**
     * From here, code is modified and based of @gigaherz ClientEvents codes: <a href="https://github.com/gigaherz/ToolBelt/blob/master/src/main/java/dev/gigaherz/toolbelt/client/ClientEvents.java">...</a>
     * */
    public static void wipeOpen()
    {
        while (ModKeybindings.wandCircle().consumeClick()) {
        }
        while (ModKeybindings.brewCircle().consumeClick()) {
        }
    }

    private static boolean toolMenuKeyWasDown = false;

    @SubscribeEvent
    public static void handleKeys(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.screen == null) {
            boolean toolMenuKeyIsDown = ModKeybindings.wandCircle().isDown() || ModKeybindings.brewCircle().isDown();
            boolean wandCircle = ModKeybindings.wandCircle().isDown();
            boolean brewCircle = ModKeybindings.brewCircle().isDown();
            if (toolMenuKeyIsDown && !toolMenuKeyWasDown) {
                if (wandCircle) {
                    while (ModKeybindings.wandCircle().consumeClick()) {
                        if (minecraft.screen == null && minecraft.player != null) {
                            ItemStack inHand = WandUtil.findWand(minecraft.player);
                            if (!inHand.isEmpty() && ((TotemFinder.canOpenWandCircle(minecraft.player)))) {
                                minecraft.setScreen(new FocusRadialMenuScreen());
                            }
                        }
                    }
                } else if (brewCircle){
                    while (ModKeybindings.brewCircle().consumeClick()) {
                        if (minecraft.screen == null && minecraft.player != null) {
                            if (CuriosFinder.hasBrewInBag(minecraft.player)) {
                                minecraft.setScreen(new BrewRadialMenuScreen());
                            }
                        }
                    }
                }
            }
            toolMenuKeyWasDown = toolMenuKeyIsDown;
        } else {
            toolMenuKeyWasDown = true;
        }
    }

    public static boolean isKeyDown(KeyMapping keybind) {
        if (keybind.isUnbound()) {
            return false;
        }

        boolean isDown =
                switch (keybind.getKey().getType()) {
                    case KEYSYM -> InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keybind.getKey().getValue());
                    case MOUSE -> GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), keybind.getKey().getValue()) == GLFW.GLFW_PRESS;
                    default -> false;
                };
        return isDown && keybind.getKeyConflictContext().isActive() && keybind.getKeyModifier().isActive(keybind.getKeyConflictContext());
    }
    /**
     * To Here
     * */

    @SubscribeEvent
    public static void KeyInputs(InputEvent.Key event) {
        Minecraft MINECRAFT = Minecraft.getInstance();

        if (ModKeybindings.keyBindings[0].isDown() && MINECRAFT.isWindowActive()){
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CWandKeyPacket());
        }
        if (ModKeybindings.keyBindings[2].isDown() && MINECRAFT.isWindowActive()){
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CBagKeyPacket());
        }
        if (ModKeybindings.keyBindings[3].isDown() && MINECRAFT.isWindowActive()){
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CWitchRobePacket());
        }
        if (ModKeybindings.keyBindings[4].isDown() && MINECRAFT.isWindowActive()){
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CStopAttackPacket());
        }
        if (ModKeybindings.keyBindings[5].isDown() && MINECRAFT.isWindowActive()){
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CLichKissPacket());
        }
        if (ModKeybindings.keyBindings[6].isDown() && MINECRAFT.isWindowActive()){
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CMagnetPacket());
        }
        if (ModKeybindings.keyBindings[7].isDown() && MINECRAFT.isWindowActive()){
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CExtractPotionKeyPacket());
        }
        if (ModKeybindings.keyBindings[8].isDown() && MINECRAFT.isWindowActive()){
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CBrewBagKeyPacket());
        }
        if (ModKeybindings.keyBindings[10].isDown() && MINECRAFT.isWindowActive()){
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CRavagerRoarPacket());
        }
        if (ModKeybindings.keyBindings[11].isDown() && MINECRAFT.isWindowActive()){
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CRavagerAutoPacket());
        }
        if (ModKeybindings.keyBindings[12].isDown() && MINECRAFT.isWindowActive()){
            if (MINECRAFT.player != null){
                if (LichdomHelper.isLich(MINECRAFT.player)){
                    LichdomHelper.setLichMode(MINECRAFT.player, !LichdomHelper.isInLichMode(MINECRAFT.player));
                    if (!LichdomHelper.isInLichMode(MINECRAFT.player)) {
                        MINECRAFT.player.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED);
                    } else {
                        if (MINECRAFT.level != null) {
                            for (int i = 0; i < 5; ++i) {
                                double d0 = MINECRAFT.level.random.nextGaussian() * 0.02D;
                                double d1 = MINECRAFT.level.random.nextGaussian() * 0.02D;
                                double d2 = MINECRAFT.level.random.nextGaussian() * 0.02D;
                                MINECRAFT.level.addParticle(ParticleTypes.SCULK_SOUL, MINECRAFT.player.getRandomX(1.0D), MINECRAFT.player.getRandomY() + 1.0D, MINECRAFT.player.getRandomZ(1.0D), d0, d1, d2);
                            }
                        }
                        MINECRAFT.player.playSound(ModSounds.SOUL_EXPLODE.get(), 1.0F, 0.75F);
                    }
                    ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CSetLichMode());
                }
            }
        }
    }

    /**
     * Based on @Mrbysco's codes:<a href="https://github.com/Mrbysco/Limbs/blob/1.20/src/main/java/com/mrbysco/limbs/client/ClientHandler.java">...</a>
     */
    protected static void setupRotation(PoseStack poseStack, AbstractClientPlayer player, PlayerRenderer playerRenderer, float partialTicks) {
        boolean shouldSit = player.isPassenger() && (player.getVehicle() != null && player.getVehicle().shouldRiderSit());
        float f = Mth.rotLerp(partialTicks, player.yBodyRotO, player.yBodyRot);
        float f1 = Mth.rotLerp(partialTicks, player.yHeadRotO, player.yHeadRot);
        if (shouldSit && player.getVehicle() instanceof LivingEntity livingentity) {
            f = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            float f2 = f1 - f;
            float f3 = Mth.wrapDegrees(f2);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
                f += f3 * 0.2F;
            }
        }


        float f7 = player.tickCount + partialTicks;
        ((PlayerRendererAccessor) playerRenderer).limbs_setupRotations(player, poseStack, f7, f, partialTicks);
    }
}
