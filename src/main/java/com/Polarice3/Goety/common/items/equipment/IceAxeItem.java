package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

public class IceAxeItem extends DiggerItem {
    private static final double MAX_ICE_AXE_DISTANCE = Math.sqrt(ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE) - 1.0D;

    public IceAxeItem(Tier tier, Properties properties) {
        super(6.0F, -3.1F, tier, BlockTags.ICE, properties);
    }

    public IceAxeItem(Tier tier) {
        this(tier, new Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        Player player = context.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);
        if (player != null) {
            ItemStack itemStack = context.getItemInHand();
            if (direction.getAxis().isHorizontal() && (blockstate.isSolidRender(level, blockpos) || blockstate.is(BlockTags.ICE))) {
                ItemHelper.hurtAndBreak(itemStack, 1, player);
                double yDelta = 0.52D;
                player.swing(context.getHand());
                player.setDeltaMovement(player.getDeltaMovement().x(), yDelta, player.getDeltaMovement().z());
                player.resetFallDistance();
            } else if (direction == Direction.UP && (blockstate.isSolidRender(level, blockpos) || blockstate.is(BlockTags.ICE))) {
                player.startUsingItem(context.getHand());
            }
        }
        return super.useOn(context);
    }

    public UseAnim getUseAnimation(ItemStack p_273490_) {
        return UseAnim.BOW;
    }

    public int getUseDuration(ItemStack p_272765_) {
        return 72000;
    }

    public void onUseTick(Level p_273467_, LivingEntity p_273619_, ItemStack p_273316_, int p_273101_) {
        if (p_273101_ >= 0 && p_273619_ instanceof Player player) {
            HitResult hitresult = this.calculateHitResult(p_273619_);
            if (hitresult instanceof BlockHitResult blockhitresult) {
                if (hitresult.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockPos = blockhitresult.getBlockPos();
                    BlockState blockState = p_273467_.getBlockState(blockPos);
                    if (blockhitresult.getDirection() == Direction.UP && (blockState.isSolidRender(p_273467_, blockPos) || blockState.is(BlockTags.ICE))) {
                        player.addEffect(new MobEffectInstance(GoetyEffects.TANGLED.get(), 2, 0, false, false));
                    } else {
                        p_273619_.releaseUsingItem();
                    }
                } else {
                    p_273619_.releaseUsingItem();
                }
            } else {
                p_273619_.releaseUsingItem();
            }
        } else {
            p_273619_.releaseUsingItem();
        }
    }

    private HitResult calculateHitResult(LivingEntity p_281264_) {
        return ProjectileUtil.getHitResultOnViewVector(p_281264_, (p_281111_) -> {
            return !p_281111_.isSpectator() && p_281111_.isPickable();
        }, MAX_ICE_AXE_DISTANCE);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ChatFormatting main = ChatFormatting.DARK_PURPLE;
        ChatFormatting secondary = ChatFormatting.BLUE;
        tooltip.add(Component.translatable("info.goety.ice_axe").withStyle(main));
        tooltip.add(Component.translatable("info.goety.ice_axe.push").withStyle(secondary));
    }
}
