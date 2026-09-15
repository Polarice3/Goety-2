package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.IShowOutlines;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

//Based on @Minecraft-LightLand's codes:https://github.com/Minecraft-LightLand/ModularGolems/blob/1.20/src/main/java/dev/xkmc/modulargolems/content/item/card/PathRecordCard.java
public class PatrolPlan extends Item implements IShowOutlines {
    private static final Codec<List<GlobalPos>> LIST_CODEC = GlobalPos.CODEC.listOf();
    private static final String PATROL_PATH = "PatrolPath";

    public PatrolPlan() {
        super(new Properties().rarity(Rarity.UNCOMMON));
    }

    public static List<GlobalPos> getList(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(PATROL_PATH, Tag.TAG_LIST)) {
            return new ArrayList<>();
        }
        return LIST_CODEC.parse(NbtOps.INSTANCE, tag.get(PATROL_PATH))
                .resultOrPartial(Goety.LOGGER::error)
                .<List<GlobalPos>>map(ArrayList::new)
                .orElseGet(ArrayList::new);
    }

    public static void setList(ItemStack stack, List<GlobalPos> list) {
        if (list.isEmpty()) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(PATROL_PATH);
            }
            return;
        }
        LIST_CODEC.encodeStart(NbtOps.INSTANCE, list)
                .resultOrPartial(Goety.LOGGER::error)
                .ifPresent(encoded -> stack.getOrCreateTag().put(PATROL_PATH, encoded));
    }

    public static void addPos(ItemStack stack, GlobalPos pos) {
        List<GlobalPos> list = getList(stack);
        list.add(pos);
        setList(stack, list);
    }

    public static boolean togglePos(ItemStack stack, GlobalPos pos) {
        List<GlobalPos> list = getList(stack);
        if (list.remove(pos)) {
            setList(stack, list);
            return false;
        }
        list.add(pos);
        setList(stack, list);
        return true;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() || player.isCrouching()){
            itemstack.setTag(null);
            player.displayClientMessage(Component.translatable("info.goety.patrol.clear"), true);
            if (!level.isClientSide) {
                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.BOOK_PAGE_TURN, 1.0F, 0.5F));
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        Level level = ctx.getLevel();
        if (!level.isClientSide()) {
            Player player = ctx.getPlayer();
            BlockPos blockPos = ctx.getClickedPos();
            BlockState blockState = level.getBlockState(blockPos);
            if (!blockState.getShape(level, blockPos).isEmpty()) {
                blockPos = blockPos.relative(ctx.getClickedFace());
            }
            List<GlobalPos> list = getList(stack);
            GlobalPos entry = GlobalPos.of(level.dimension(), blockPos);
            if (!list.isEmpty()) {
                GlobalPos last = list.get(list.size() - 1);
                if (!entry.dimension().equals(last.dimension())) {
                    if (player != null) {
                        player.displayClientMessage(Component.translatable("info.goety.patrol.dimension"), true);
                    }
                    return InteractionResult.FAIL;
                }
                if (!list.contains(entry) && entry.pos().distSqr(last.pos()) > 256) {
                    if (player != null) {
                        player.displayClientMessage(Component.translatable("info.goety.patrol.distance"), true);
                    }
                    return InteractionResult.FAIL;
                }
            }
            if (togglePos(stack, entry)) {
                if (player != null) {
                    player.displayClientMessage(Component.translatable("info.goety.patrol.add"), true);
                    if (!level.isClientSide) {
                        ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F));
                    }
                }
            } else {
                if (player != null) {
                    player.displayClientMessage(Component.translatable("info.goety.patrol.remove"), true);
                    if (!level.isClientSide) {
                        ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.BOOK_PAGE_TURN, 1.0F, 0.5F));
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.level.isClientSide) {
            if (target instanceof IServant servant) {
                if (servant.canPatrol()) {
                    if (servant.getTrueOwner() == player) {
                        if (player.isShiftKeyDown()) {
                            servant.clearPatrol();
                            player.displayClientMessage(Component.translatable("info.goety.patrol.clear", target.getDisplayName()), true);
                            return InteractionResult.SUCCESS;
                        }

                        List<GlobalPos> route = getList(stack);
                        if (route.isEmpty()) {
                            player.displayClientMessage(Component.translatable("info.goety.patrol.empty"), true);
                            return InteractionResult.FAIL;
                        }
                        if (!route.get(0).dimension().equals(target.level.dimension())) {
                            player.displayClientMessage(Component.translatable("info.goety.patrol.dimension"), true);
                            return InteractionResult.FAIL;
                        }

                        servant.setPatrolRoute(route);
                        servant.setPatrolIndex(0);
                        servant.setBoundPos(route.get(0).pos());
                        servant.setWandering(false);
                        servant.setStaying(false);
                        target.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 1.0F, 1.0F);
                        player.displayClientMessage(Component.translatable("info.goety.patrol.assign", target.getDisplayName()), true);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        list.add(Component.translatable("info.goety.patrol.number", getList(stack).size()).withStyle(ChatFormatting.BLUE));
        list.add(Component.translatable("info.goety.patrol").withStyle(ChatFormatting.DARK_PURPLE));
    }
}
