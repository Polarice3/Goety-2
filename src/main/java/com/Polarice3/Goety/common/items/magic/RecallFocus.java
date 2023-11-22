package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ArcaBlock;
import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import com.Polarice3.Goety.common.events.ArcaTeleporter;
import com.Polarice3.Goety.common.magic.spells.utility.RecallSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class RecallFocus extends MagicFocus{
    public static final String TAG_POS = "RecallPos";
    public static final String TAG_DIMENSION = "RecallDimension";

    public RecallFocus() {
        super(new RecallSpell());
    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level world = pContext.getLevel();
        ItemStack stack = pContext.getItemInHand();
        Player player = pContext.getPlayer();
        if (player != null) {
            if (!stack.isEmpty() && !hasRecall(stack)) {
                CompoundTag compoundTag = stack.getOrCreateTag();
                if (stack.getItem() instanceof RecallFocus) {
                    BlockPos blockpos = pContext.getClickedPos();
                    BlockEntity tileEntity = world.getBlockEntity(blockpos);
                    if (tileEntity instanceof ArcaBlockEntity arcaTile) {
                        if (pContext.getPlayer() == arcaTile.getPlayer() && arcaTile.getLevel() != null) {
                            this.addRecallTags(arcaTile.getLevel().dimension(), arcaTile.getBlockPos(), compoundTag);
                            stack.setTag(compoundTag);
                            player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                            return InteractionResult.sidedSuccess(world.isClientSide);
                        }
                    }
                    BlockState blockstate = world.getBlockState(blockpos);
                    if (blockstate.is(ModTags.Blocks.RECALL_BLOCKS)) {
                        this.addRecallTags(world.dimension(), blockpos, compoundTag);
                        stack.setTag(compoundTag);
                        player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                        return InteractionResult.sidedSuccess(world.isClientSide);
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() || player.isCrouching()){
            if (itemstack.getItem() instanceof RecallFocus){
                if (hasRecall(itemstack) && itemstack.getTag() != null){
                    itemstack.getTag().remove(TAG_DIMENSION);
                    itemstack.getTag().remove(TAG_POS);
                }
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }

    public static boolean recall(ServerPlayer player, ItemStack stack){
        if (hasRecall(stack) && stack.getTag() != null) {
            if (getDimension(stack.getTag()).isPresent() && getRecallBlockPos(stack.getTag()) != null) {
                BlockPos blockPos = getRecallBlockPos(stack.getTag());
                if (blockPos != null) {
                    if (getDimension(stack.getTag()).get() == player.level.dimension()) {
                        Optional<Vec3> optional = RespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, player.level, blockPos);
                        if (optional.isPresent()) {
                            player.teleportTo(optional.get().x, optional.get().y, optional.get().z);
                            ModNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), new SPlayWorldSoundPacket(new BlockPos(player.xo, player.yo, player.zo), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                            ModNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), new SPlayWorldSoundPacket(new BlockPos(optional.get()), SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F));
                            return true;
                        }
                    } else {
                        if (player.getServer() != null) {
                            ServerLevel serverWorld = player.getServer().getLevel(getDimension(stack.getTag()).get());
                            if (serverWorld != null) {
                                Optional<Vec3> optional = RespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, serverWorld, blockPos);
                                if (optional.isPresent()) {
                                    player.changeDimension(serverWorld, new ArcaTeleporter(optional.get()));
                                    player.teleportTo(optional.get().x, optional.get().y, optional.get().z);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasRecall(ItemStack p_40737_) {
        CompoundTag compoundtag = p_40737_.getTag();
        return compoundtag != null && (compoundtag.contains(TAG_DIMENSION) || compoundtag.contains(TAG_POS));
    }

    public static BlockPos getRecallBlockPos(CompoundTag compoundTag){
        boolean flag = compoundTag.contains(TAG_POS);
        boolean flag1 = compoundTag.contains(TAG_DIMENSION);
        if (flag && flag1) {
            Optional<ResourceKey<Level>> optional = getDimension(compoundTag);
            if (optional.isPresent()) {
                return NbtUtils.readBlockPos(compoundTag.getCompound(TAG_POS));
            }
        }
        return null;
    }

    public static Optional<ResourceKey<Level>> getDimension(CompoundTag p_40728_) {
        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, p_40728_.get(TAG_DIMENSION)).result();
    }

    public void addRecallTags(ResourceKey<Level> p_40733_, BlockPos p_40734_, CompoundTag p_40735_) {
        p_40735_.put(TAG_POS, NbtUtils.writeBlockPos(p_40734_));
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, p_40733_).resultOrPartial(Goety.LOGGER::error).ifPresent((p_40731_) -> {
            p_40735_.put(TAG_DIMENSION, p_40731_);
        });
    }

    public static boolean isValid(ServerLevel serverLevel, ItemStack stack){
        if (hasRecall(stack) && stack.getTag() != null) {
            if (getDimension(stack.getTag()).isPresent()) {
                ServerLevel serverLevel1 = serverLevel.getServer().getLevel(getDimension(stack.getTag()).get());
                if (serverLevel1 != null) {
                    if (getRecallBlockPos(stack.getTag()) != null) {
                        BlockPos blockPos = getRecallBlockPos(stack.getTag());
                        if (blockPos != null) {
                            BlockState blockState = serverLevel1.getBlockState(blockPos);
                            Block block = blockState.getBlock();
                            return block instanceof ArcaBlock || blockState.is(ModTags.Blocks.RECALL_BLOCKS);
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        addRecallText(stack, tooltip);
    }

    public static void addRecallText(ItemStack stack, List<Component> tooltip){
        if (stack.getTag() != null) {
            if (!hasRecall(stack)) {
                tooltip.add(Component.translatable("info.goety.focus.noPos"));
            } else {
                BlockPos blockPos = getRecallBlockPos(stack.getTag());
                if (blockPos != null && getDimension(stack.getTag()).isPresent()) {
                    tooltip.add(Component.translatable("info.goety.focus.Pos").append(" ").append(Component.translatable("info.goety.focus.PosNum", blockPos.getX(), blockPos.getY(), blockPos.getZ())));
                    tooltip.add(Component.translatable("info.goety.focus.PosDim", getDimension(stack.getTag()).get().location().toString()));
                }
            }
        }
    }

}
