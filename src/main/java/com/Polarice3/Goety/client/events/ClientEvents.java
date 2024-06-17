package com.Polarice3.Goety.client.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.blocks.entities.IOwnedBlock;
import com.Polarice3.Goety.api.blocks.entities.ITrainingBlock;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.client.audio.BossLoopMusic;
import com.Polarice3.Goety.client.audio.ItemLoopSound;
import com.Polarice3.Goety.client.audio.LoopSound;
import com.Polarice3.Goety.client.audio.SummonNoveltySound;
import com.Polarice3.Goety.client.gui.screen.inventory.BrewRadialMenuScreen;
import com.Polarice3.Goety.client.gui.screen.inventory.FocusRadialMenuScreen;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.WearRenderer;
import com.Polarice3.Goety.client.render.model.LichModeModel;
import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.Leapleaf;
import com.Polarice3.Goety.common.entities.ally.golem.SquallGolem;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.hostile.servants.Inferno;
import com.Polarice3.Goety.common.entities.neutral.ApostleShade;
import com.Polarice3.Goety.common.entities.neutral.InsectSwarm;
import com.Polarice3.Goety.common.entities.projectiles.CorruptedBeam;
import com.Polarice3.Goety.common.entities.projectiles.IceStorm;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.common.items.curios.GloveItem;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.*;
import com.Polarice3.Goety.common.network.client.brew.CBrewBagKeyPacket;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModKeybindings;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
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
                }
                if (entity instanceof Vizier && !((Vizier) entity).isNoAi()) {
                    minecraft.getMusicManager().stopPlaying();
                    minecraft.gui.setNowPlaying(Component.translatable("item.goety.music_disc_vizier.desc"));
                }
            }
            if (entity instanceof CorruptedBeam){
                soundHandler.play(new LoopSound(ModSounds.CORRUPT_BEAM_LOOP.get(), entity));
                soundHandler.play(new LoopSound(ModSounds.CORRUPT_BEAM_SOUL.get(), entity));
            }
            if (entity instanceof ApostleShade){
                soundHandler.play(new LoopSound(ModSounds.APOSTLE_SHADE.get(), entity));
            }
            if (entity instanceof Inferno){
                soundHandler.play(new LoopSound(ModSounds.INFERNO_LOOP.get(), entity));
            }
            if (entity instanceof InsectSwarm){
                soundHandler.play(new LoopSound(ModSounds.INSECT_SWARM.get(), entity));
            }
            if (entity instanceof IceStorm){
                soundHandler.play(new LoopSound(ModSounds.ICE_STORM_LOOP.get(), entity));
            }
        }
    }

    /**
     * Ripped from @BobMowzie's codes:<a href="https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/client/ClientEventHandler.java#L211">...</a>
     */
    @SubscribeEvent
    public static void onSetupCamera(ViewportEvent.ComputeCameraAngles event) {
        Player player = Minecraft.getInstance().player;
        float delta = Minecraft.getInstance().getFrameTime();
        if (player != null) {
            float ticksExistedDelta = player.tickCount + delta;
            if (MainConfig.CameraShake.get() && !Minecraft.getInstance().isPaused()) {
                float shakeAmplitude = 0;
                for (CameraShake cameraShake : player.level.getEntitiesOfClass(CameraShake.class, player.getBoundingBox().inflate(20))) {
                    if (cameraShake.distanceTo(player) < cameraShake.getRadius()) {
                        shakeAmplitude += cameraShake.getShakeAmount(player, delta);
                    }
                }
                if (shakeAmplitude > 1.0F) {
                    shakeAmplitude = 1.0F;
                }
                event.setPitch((float) (event.getPitch() + shakeAmplitude * Math.cos(ticksExistedDelta * 3.0D + 2.0D) * 25.0D));
                event.setYaw((float) (event.getYaw() + shakeAmplitude * Math.cos(ticksExistedDelta * 5.0D + 1.0D) * 25.0D));
                event.setRoll((float) (event.getRoll() + shakeAmplitude * Math.cos(ticksExistedDelta * 4.0D) * 25.0D));
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
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if (SEHelper.hasCamera(event.player)){
            event.player.turn(0.0F, 0.0F);
            event.player.xxa = 0.0F;
            event.player.zza = 0.0F;
            event.player.setJumping(false);
        }
    }

    @SubscribeEvent
    public static void onInputInteract(InputEvent.InteractionKeyMappingTriggered event){
        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player != null){
            if (SEHelper.hasCamera(player)){
                if (event.isAttack() || event.isPickBlock() || event.isUseItem()){
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemUse(LivingEntityUseItemEvent.Start event){
        if (event.getEntity().level instanceof ClientLevel){
            Minecraft minecraft = Minecraft.getInstance();
            SoundManager soundHandler = minecraft.getSoundManager();
            if (WandUtil.getSpell(event.getEntity()) != null){
                ISpell spells = WandUtil.getSpell(event.getEntity());
                if (spells != null) {
                    if (spells.loopSound(event.getEntity()) != null) {
                        soundHandler.play(new ItemLoopSound(spells.loopSound(event.getEntity()), event.getEntity()));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event){
        Entity entity = event.getEntity();
        if (entity.level instanceof ClientLevel){
            Minecraft minecraft = Minecraft.getInstance();
            SoundManager soundHandler = minecraft.getSoundManager();
            if (entity instanceof SquallGolem squallGolem){
                if (squallGolem.noveltyTick == 1) {
                    soundHandler.play(new SummonNoveltySound(squallGolem, ModSounds.SQUALL_GOLEM_ALERT.get()));
                }
            }
            if (event.getEntity() instanceof Leapleaf leapleaf){
                if (leapleaf.noveltyTick == 1) {
                    soundHandler.play(new SummonNoveltySound(leapleaf, ModSounds.LEAPLEAF_ALERT.get()));
                }
            }
            if (MainConfig.BossMusic.get()) {
                if (entity instanceof Apostle && !((Apostle) entity).isNoAi()) {
                    playBossMusic(ModSounds.APOSTLE_THEME.get(), (Apostle) entity);
                }
                if (entity instanceof Vizier && !((Vizier) entity).isNoAi()) {
                    playBossMusic(ModSounds.VIZIER_THEME.get(), (Vizier) entity);
                }
            }
        }
    }

    public static BossLoopMusic BOSS_MUSIC;

    public static void playBossMusic(SoundEvent soundEvent, Mob mob){
        if (MainConfig.BossMusic.get()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (soundEvent != null && mob.isAlive()) {
                if (BOSS_MUSIC == null) {
                    BOSS_MUSIC = new BossLoopMusic(soundEvent, mob);
                }
            } else {
                BOSS_MUSIC = null;
            }
            if (BOSS_MUSIC != null && !minecraft.getSoundManager().isActive(BOSS_MUSIC)) {
                Minecraft.getInstance().getSoundManager().play(BOSS_MUSIC);
            }
        }
    }

    @SubscribeEvent
    public static void renderGlove(RenderArmEvent event){
        if (event.isCanceled() || !ItemConfig.FirstPersonGloves.get()){
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
        if (event.getPlayer().hasEffect(GoetyEffects.SHADOW_WALK.get())){
            if (event.getPlayer().getMainHandItem().isEmpty() && event.getArm() == event.getPlayer().getMainArm()){
                event.setCanceled(true);
            } else if (event.getPlayer().getOffhandItem().isEmpty() && event.getArm() != event.getPlayer().getMainArm()){
                event.setCanceled(true);
            }
        }
        if (SEHelper.hasCamera(event.getPlayer())){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void renderHand(RenderHandEvent event){
        final AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            if (SEHelper.hasCamera(player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
        final Player player = event.getEntity();
        if (player.hasEffect(GoetyEffects.SHADOW_WALK.get())){
            event.setCanceled(true);
        }
        if (player.isInvisible() && CuriosFinder.hasIllusionRobe(player)){
            event.setCanceled(true);
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
            if (minecraft.level != null) {
                if (hitResult instanceof BlockHitResult blockRayTraceResult) {
                    BlockEntity blockEntity = minecraft.level.getBlockEntity(blockRayTraceResult.getBlockPos());
                    int width = minecraft.getWindow().getGuiScaledWidth();
                    int height = minecraft.getWindow().getGuiScaledHeight();
                    if (blockEntity instanceof ArcaBlockEntity arcaTile) {
                        if ((player.isShiftKeyDown() || player.isCrouching())) {
                            if (arcaTile.getPlayer() == player && SEHelper.getSEActive(player)) {
                                event.getPoseStack().pushPose();
                                event.getPoseStack().translate((float) (width / 2), (float) (height - 68), 0.0F);
                                RenderSystem.enableBlend();
                                RenderSystem.defaultBlendFunc();
                                int SoulEnergy = SEHelper.getSESouls(player);
                                int SoulEnergyTotal = MainConfig.MaxArcaSouls.get();
                                String s = Component.translatable("tooltip.goety.blockSoul").getString() + SoulEnergy + "/" + SoulEnergyTotal;
                                int l = fontRenderer.width(s);
                                fontRenderer.drawShadow(event.getPoseStack(), s, (float) (-l / 2), -4.0F, 0xFFFFFF);
                                RenderSystem.disableBlend();
                                event.getPoseStack().popPose();
                            } else if (arcaTile.getPlayer() != null) {
                                event.getPoseStack().pushPose();
                                event.getPoseStack().translate((float)(width / 2), (float)(height - 68), 0.0F);
                                RenderSystem.enableBlend();
                                RenderSystem.defaultBlendFunc();
                                String s = Component.translatable("tooltip.goety.blockOwner").getString() + arcaTile.getPlayer().getDisplayName().getString();
                                int l = fontRenderer.width(s);
                                fontRenderer.drawShadow(event.getPoseStack(), s, (float)(-l / 2), -4.0F, 0xFFFFFF);
                                RenderSystem.disableBlend();
                                event.getPoseStack().popPose();
                            }
                        }
                    } else if (blockEntity instanceof IOwnedBlock ownedBlock && ownedBlock.getPlayer() != null && ownedBlock.screenView()){
                        if (blockEntity instanceof ITrainingBlock trainingBlock){
                            event.getPoseStack().pushPose();
                            event.getPoseStack().translate((float)(width / 2), (float)(height - 58), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            String s = Component.translatable("tooltip.goety.blockOwner").getString() + ownedBlock.getPlayer().getDisplayName().getString();
                            int l = fontRenderer.width(s);
                            fontRenderer.drawShadow(event.getPoseStack(), s, (float)(-l / 2), -4.0F, 0xFFFFFF);
                            RenderSystem.disableBlend();
                            event.getPoseStack().popPose();

                            if (trainingBlock.getPlayer() == player) {
                                if (trainingBlock.isSensorSensitive()) {
                                    event.getPoseStack().pushPose();
                                    event.getPoseStack().translate((float) (width / 2), (float) (height - 90), 0.0F);
                                    RenderSystem.enableBlend();
                                    RenderSystem.defaultBlendFunc();
                                    String s0 = Component.translatable("tooltip.goety.blockSense").getString();
                                    int l0 = fontRenderer.width(s0);
                                    fontRenderer.drawShadow(event.getPoseStack(), s0, (float) (-l0 / 2), -4, 0xFFFFFF);
                                    RenderSystem.disableBlend();
                                    event.getPoseStack().popPose();
                                }

                                event.getPoseStack().pushPose();
                                event.getPoseStack().translate((float) (width / 2), (float) (height - 68), 0.0F);
                                RenderSystem.enableBlend();
                                RenderSystem.defaultBlendFunc();
                                String s1 = Component.translatable("tooltip.goety.blockTrain").getString() + trainingBlock.amountTrainLeft() + "/" + trainingBlock.maxTrainAmount() + " " + trainingBlock.getTrainMob().getDescription().getString();
                                int l1 = fontRenderer.width(s1);
                                fontRenderer.drawShadow(event.getPoseStack(), s1, (float) (-l1 / 2), -4.0F, 0xFFFFFF);
                                RenderSystem.disableBlend();
                                event.getPoseStack().popPose();

                                event.getPoseStack().pushPose();
                                int train = 64;
                                train *= ((double) trainingBlock.getTrainingTime() / trainingBlock.getMaxTrainTime());
                                RenderSystem.setShaderTexture(0, new ResourceLocation(Goety.MOD_ID, "textures/gui/train_bar.png"));
                                GuiComponent.blit(event.getPoseStack(), ((width - 64) / 2), (height - 86), 0, 0, 64, 16, 64, 32);
                                RenderSystem.setShaderTexture(0, new ResourceLocation(Goety.MOD_ID, "textures/gui/train_bar.png"));
                                GuiComponent.blit(event.getPoseStack(), ((width - 64) / 2), (height - 86), 0, 16, train, 16, 64, 32);
                                event.getPoseStack().popPose();
                            }
                        } else if ((player.isShiftKeyDown() || player.isCrouching()) && ownedBlock.getPlayer() != null){
                            event.getPoseStack().pushPose();
                            event.getPoseStack().translate((float)(width / 2), (float)(height - 68), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            String s = Component.translatable("tooltip.goety.blockOwner").getString() + ownedBlock.getPlayer().getDisplayName().getString();
                            int l = fontRenderer.width(s);
                            fontRenderer.drawShadow(event.getPoseStack(), s, (float)(-l / 2), -4.0F, 0xFFFFFF);
                            RenderSystem.disableBlend();
                            event.getPoseStack().popPose();
                        }
                    } else if (blockEntity instanceof CursedCageBlockEntity cageBlockEntity){
                        if (player.isShiftKeyDown() || player.isCrouching() && !cageBlockEntity.getItem().isEmpty()){
                            event.getPoseStack().pushPose();
                            event.getPoseStack().translate((float)(width / 2), (float)(height - 68), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            String s = Component.translatable("tooltip.goety.blockSoul").getString() + cageBlockEntity.getSouls();
                            int l = fontRenderer.width(s);
                            fontRenderer.drawShadow(event.getPoseStack(), s, (float)(-l / 2), -4.0F, 0xFFFFFF);
                            RenderSystem.disableBlend();
                            event.getPoseStack().popPose();
                        }
                    } else if (blockEntity instanceof BrewCauldronBlockEntity cauldronBlock){
                        if (player.isShiftKeyDown() || player.isCrouching()){
                            event.getPoseStack().pushPose();
                            event.getPoseStack().translate((float)(width / 2), (float)(height - 60), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            String s1 = Component.translatable("tooltip.goety.brew.capacity").getString() + cauldronBlock.getCapacityUsed() + "/" + cauldronBlock.getCapacity();
                            int l2 = fontRenderer.width(s1);
                            fontRenderer.drawShadow(event.getPoseStack(), s1, (float)(-l2 / 2), -4.0F, 0xFFFFFF);
                            RenderSystem.disableBlend();
                            event.getPoseStack().popPose();
                            event.getPoseStack().pushPose();
                            event.getPoseStack().translate((float)(width / 2), (float)(height - 68), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            String s = Component.translatable("tooltip.goety.blockSoulCost").getString() + (cauldronBlock.getBrewCost() - cauldronBlock.soulTime);
                            int l = fontRenderer.width(s);
                            fontRenderer.drawShadow(event.getPoseStack(), s, (float)(-l / 2), -4.0F, 0xFFFFFF);
                            RenderSystem.disableBlend();
                            event.getPoseStack().popPose();
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

    public static boolean isKeyDown0(KeyMapping keybind) {
        if (keybind.isUnbound()) {
            return false;
        }

        return switch (keybind.getKey().getType()) {
                    case KEYSYM -> InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keybind.getKey().getValue());
                    case MOUSE -> GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), keybind.getKey().getValue()) == GLFW.GLFW_PRESS;
                    default -> false;
                };
    }

    public static boolean isKeyDown(KeyMapping keybind) {
        if (keybind.isUnbound()) {
            return false;
        }

        return isKeyDown0(keybind) && keybind.getKeyConflictContext().isActive() && keybind.getKeyModifier().isActive(keybind.getKeyConflictContext());
    }

    @SubscribeEvent
    public static void updateInputEvent(MovementInputUpdateEvent event) {
        if (MainConfig.WheelGuiMovement.get()) {
            if (Minecraft.getInstance().screen instanceof FocusRadialMenuScreen || Minecraft.getInstance().screen instanceof BrewRadialMenuScreen) {
                Options settings = Minecraft.getInstance().options;
                Input input = event.getInput();
                input.up = isKeyDown0(settings.keyUp);
                input.down = isKeyDown0(settings.keyDown);
                input.left = isKeyDown0(settings.keyLeft);
                input.right = isKeyDown0(settings.keyRight);

                input.forwardImpulse = input.up == input.down ? 0.0F : (input.up ? 1.0F : -1.0F);
                input.leftImpulse = input.left == input.right ? 0.0F : (input.left ? 1.0F : -1.0F);
                input.jumping = isKeyDown0(settings.keyJump);
                input.shiftKeyDown = isKeyDown0(settings.keyShift);
                if (Minecraft.getInstance().player.isMovingSlowly()) {
                    input.leftImpulse = (float) ((double) input.leftImpulse * 0.3D);
                    input.forwardImpulse = (float) ((double) input.forwardImpulse * 0.3D);
                }
            }
        }
        /*Player player = event.getEntity();
        if (player.isUsingItem() && !player.isPassenger()){
            ItemStack itemStack = player.getUseItem();
            if (itemStack.getItem() instanceof DarkWand){
                Input input = event.getInput();
                input.leftImpulse *= 5.0F;
                input.forwardImpulse *= 5.0F;
            }
        }*/
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
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CMagnetPacket());
        }
        if (ModKeybindings.keyBindings[6].isDown() && MINECRAFT.isWindowActive()){
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CSetLichNightVisionMode());
            if (MINECRAFT.player != null && MainConfig.LichNightVision.get()){
                if (LichdomHelper.isLich(MINECRAFT.player)){
                    MINECRAFT.player.playSound(SoundEvents.END_PORTAL_FRAME_FILL);
                }
            }
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
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CAutoRideablePacket());
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

    @SuppressWarnings("unchecked")
    public <T extends LivingEntity> void followBodyRotations(final T livingEntity, final HumanoidModel<T> model) {
        EntityRenderer<? super T> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(livingEntity);
        if (render instanceof LivingEntityRenderer) {
            LivingEntityRenderer<T, EntityModel<T>> livingRenderer = (LivingEntityRenderer<T, EntityModel<T>>) render;
            EntityModel<T> entityModel = livingRenderer.getModel();
            if (entityModel instanceof HumanoidModel<T> humanoidModel) {
                humanoidModel.copyPropertiesTo(model);
            }
        }
    }
}
