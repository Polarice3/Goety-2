package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandHorn extends Item {
    public static String MODE = "Mode";
    public static String FOLLOW = "Follow";
    public static String WANDER = "Wander";
    public static String STAND_BY = "Stand_By";
    public static String GUARD = "Guard";
    public static String NONE = "None";
    public static float RANGE = 8.0F;

    public CommandHorn() {
        super(new Item.Properties().stacksTo(1));
    }

    public static void switchMode(ItemStack itemStack, Player player) {
        String string = "none";
        if (isNone(itemStack)) {
            setWander(itemStack);
            string = "wander";
        } else if (isWander(itemStack)) {
            setStandBy(itemStack);
            string = "stand";
        } else if (isStandBy(itemStack)) {
            setGuard(itemStack);
            string = "guard";
        } else if (isGuard(itemStack)) {
            setFollow(itemStack);
            string = "follow";
        } else {
            setNone(itemStack);
        }
        player.displayClientMessage(Component.translatable("info.goety.horn.set").append(" ").append(Component.translatable("info.goety.movement." + string)), true);
    }

    public static boolean isNone(ItemStack itemStack) {
        return getMode(itemStack).equals(NONE) || itemStack.getTag() == null;
    }

    public static boolean isWander(ItemStack itemStack) {
        return getMode(itemStack).equals(WANDER);
    }

    public static boolean isStandBy(ItemStack itemStack) {
        return getMode(itemStack).equals(STAND_BY);
    }

    public static boolean isGuard(ItemStack itemStack) {
        return getMode(itemStack).equals(GUARD);
    }

    public static boolean isFollow(ItemStack itemStack) {
        return getMode(itemStack).equals(FOLLOW);
    }

    public static String getMode(ItemStack itemStack) {
        if (itemStack.getTag() != null && itemStack.getTag().contains(MODE)) {
            return itemStack.getTag().getString(MODE);
        } else {
            return NONE;
        }
    }

    public static void setNone(ItemStack itemStack) {
        setMode(itemStack, NONE);
    }

    public static void setFollow(ItemStack itemStack) {
        setMode(itemStack, FOLLOW);
    }

    public static void setWander(ItemStack itemStack) {
        setMode(itemStack, WANDER);
    }

    public static void setStandBy(ItemStack itemStack) {
        setMode(itemStack, STAND_BY);
    }

    public static void setGuard(ItemStack itemStack) {
        setMode(itemStack, GUARD);
    }

    public static void setMode(ItemStack itemStack, String mode) {
        if (itemStack.getItem() instanceof CommandHorn) {
            CompoundTag tag = new CompoundTag();
            if (itemStack.getTag() != null) {
                tag = itemStack.getTag();
            }
            tag.putString(MODE, mode);
            itemStack.setTag(tag);
        }
    }

    public static List<LivingEntity> getEntities(Level level, Player player) {
        List<LivingEntity> list = new ArrayList<>();
        for (LivingEntity livingEntity : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(RANGE))) {
            if (livingEntity instanceof IServant servant) {
                if (servant.getTrueOwner() == player) {
                    list.add(livingEntity);
                }
            }
        }
        return list;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        if (player.isCrouching()) {
            switchMode(itemstack, player);
            if (level instanceof ServerLevel) {
                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
            } else {
                player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
            }
            level.gameEvent(player, GameEvent.INSTRUMENT_PLAY, player.position());
        } else {
            if (level instanceof ServerLevel serverlevel) {
                ColorUtil colorUtil = new ColorUtil(ChatFormatting.AQUA);
                serverlevel.sendParticles(new ShockwaveParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 16.0F, 0, true), player.getX(), player.getY() + 0.25F, player.getZ(), 0, 0, 0, 0, 0);
                for (LivingEntity livingEntity : serverlevel.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(RANGE))){
                    if (livingEntity instanceof IServant servant){
                        if (servant.getTrueOwner() == player){
                            if (!servant.isCommanded()
                                    && servant.canUpdateMove()
                                    && !SEHelper.getGroundedEntities(player).contains(livingEntity)
                                    && !SEHelper.getGroundedEntityTypes(player).contains(livingEntity.getType())) {
                                if (isNone(itemstack)) {
                                    if (!servant.isGuardingArea()
                                            && servant.canStay()
                                            && servant.canWander()) {
                                        if (servant.isStaying() || servant.isWandering()) {
                                            servant.setFollowing();
                                            serverlevel.sendParticles(ModParticleTypes.GO.get(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 0, 2.0D, 0, 1.0F);
                                        } else {
                                            servant.setBoundPos(null);
                                            servant.setWandering(false);
                                            servant.setStaying(true);
                                            serverlevel.sendParticles(ModParticleTypes.STOP.get(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 0, 2.0D, 0, 1.0F);
                                        }
                                    }
                                } else if (isFollow(itemstack)) {
                                    if (servant.canFollow()) {
                                        servant.setFollowing();
                                        serverlevel.sendParticles(ModParticleTypes.GO.get(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 0, 2.0D, 0, 1.0F);
                                    }
                                } else if (isWander(itemstack)) {
                                    if (servant.canWander()) {
                                        servant.setWandering();
                                        serverlevel.sendParticles(ModParticleTypes.GO.get(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 0, 2.0D, 0, 1.0F);
                                    }
                                } else if (isStandBy(itemstack)) {
                                    if (servant.canStay()) {
                                        servant.setStaying();
                                        serverlevel.sendParticles(ModParticleTypes.STOP.get(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 0, 2.0D, 0, 1.0F);
                                    }
                                } else if (isGuard(itemstack)) {
                                    if (servant.canGuardArea()) {
                                        servant.setGuarding();
                                        serverlevel.sendParticles(ModParticleTypes.STOP.get(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 0, 2.0D, 0, 1.0F);
                                    }
                                }
                            }
                        }
                    }
                }
                player.playSound(ModSounds.WIND_HORN.get(), 1.0F, 0.45F);
                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(ModSounds.WIND_HORN.get(), 1.0F, 0.45F));
                level.gameEvent(player, GameEvent.INSTRUMENT_PLAY, player.position());
            }
        }

        return InteractionResultHolder.consume(itemstack);
    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        ItemStack stack = pContext.getItemInHand();
        if (player != null) {
            if (stack.getItem() == this){
                if (level instanceof ServerLevel serverlevel) {
                    for (LivingEntity livingEntity : serverlevel.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(RANGE))){
                        if (livingEntity instanceof IServant servant){
                            if (servant.getTrueOwner() == player){
                                if (!servant.isCommanded()
                                        && servant.canUpdateMove()
                                        && !SEHelper.getGroundedEntities(player).contains(livingEntity)
                                        && !SEHelper.getGroundedEntityTypes(player).contains(livingEntity.getType())) {
                                    boolean flag = isGuard(stack) || !servant.isGuardingArea();
                                    if (flag && !servant.isCommanded() && servant.canBeCommanded()){
                                        servant.setCommandPos(blockpos.above());
                                        serverlevel.sendParticles(ModParticleTypes.GO.get(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 0, 2.0D, 0, 1.0F);
                                    }
                                }
                            }
                        }
                    }
                    player.playSound(ModSounds.WIND_HORN.get(), 1.0F, 0.45F);
                    player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                    ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(ModSounds.WIND_HORN.get(), 1.0F, 0.45F));
                    ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
                    level.gameEvent(player, GameEvent.INSTRUMENT_PLAY, player.position());
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String string = "none";
        if (isWander(stack)) {
            string = "wander";
        } else if (isStandBy(stack)) {
            string = "stand";
        } else if (isGuard(stack)) {
            string = "guard";
        } else if (isFollow(stack)) {
            string = "follow";
        }
        tooltip.add(Component.translatable("info.goety.horn.mode").withStyle(ChatFormatting.AQUA).append(" ").append(Component.translatable("info.goety.movement." + string)));
    }
}
