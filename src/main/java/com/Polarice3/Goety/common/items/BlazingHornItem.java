package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.WitherNecromancer;
import com.Polarice3.Goety.common.entities.util.SummonCircleBoss;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class BlazingHornItem extends Item {
    public BlazingHornItem() {
        super(new Properties().tab(Goety.TAB).stacksTo(1));
    }

    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        if (worldIn instanceof ServerLevel serverWorld){
            boolean flag = serverWorld.structureManager().getStructureWithPieceAt(entityLiving.blockPosition(), ModTags.Structures.WITHER_NECROMANCER_SPAWNS).isValid();
            if (flag){
                entityLiving.playSound(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(6), 16.0F, 1.0F);
                serverWorld.playSound(null, entityLiving.blockPosition(), SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(6), SoundSource.NEUTRAL, 16.0F, 1.0F);
                WitherNecromancer necromancer = ModEntityType.WITHER_NECROMANCER.get().create(worldIn);
                if (necromancer != null) {
                    BlockPos blockPos = BlockFinder.SummonFurtherRadius(entityLiving.blockPosition(), necromancer, worldIn);
                    necromancer.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    necromancer.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(entityLiving.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    SummonCircleBoss summonCircle = new SummonCircleBoss(worldIn, blockPos, necromancer);
                    serverWorld.addFreshEntity(summonCircle);
                    if (!(entityLiving instanceof Player && ((Player) entityLiving).isCreative())) {
                        stack.setCount(0);
                    }
                }
            } else {
                if (entityLiving instanceof Player player) {
                    player.displayClientMessage(Component.translatable("info.goety.items.blaze_horn.failure"), true);
                }
                entityLiving.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);
                serverWorld.playSound(null, entityLiving.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        }
        return stack;
    }

    public int getUseDuration(ItemStack stack) {
        return 25;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        playerIn.startUsingItem(handIn);
        return InteractionResultHolder.consume(itemstack);
    }

    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        if (!worldIn.isClientSide){
            ServerLevel serverWorld = (ServerLevel) worldIn;
            ServerParticleUtil.addParticlesAroundSelf(serverWorld, ParticleTypes.FLAME, livingEntityIn);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("info.goety.items.blaze_horn.desc").withStyle(ChatFormatting.DARK_PURPLE));
    }
}
