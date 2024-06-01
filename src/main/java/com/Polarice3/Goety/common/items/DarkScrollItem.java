package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.List;

public class DarkScrollItem extends Item {
    public DarkScrollItem() {
        super(new Properties().stacksTo(1));
    }

    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        boolean flag = false;
        if (worldIn instanceof ServerLevel serverWorld){
            flag = serverWorld.structureManager().getStructureWithPieceAt(entityLiving.blockPosition(), ModTags.Structures.VIZIER_SPAWNS).isValid();
        }
        if (flag){
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1.0F, 1.0F);
            Vizier vizier = ModEntityType.VIZIER.get().create(worldIn);
            if (vizier != null) {
                vizier.setPos(entityLiving.getX(), entityLiving.getEyeY(), entityLiving.getZ());
                vizier.finalizeSpawn((ServerLevelAccessor) worldIn, worldIn.getCurrentDifficultyAt(entityLiving.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                vizier.makeInvulnerable();
                worldIn.addFreshEntity(vizier);
                if (!(entityLiving instanceof Player && ((Player) entityLiving).isCreative())) {
                    stack.setCount(0);
                }
            }
        } else {
            if (entityLiving instanceof Player player) {
                player.displayClientMessage(Component.translatable("info.goety.items.dark_scroll.failure"), true);
            }
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
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
            serverWorld.sendParticles(ParticleTypes.ANGRY_VILLAGER, livingEntityIn.getX(), livingEntityIn.getY(), livingEntityIn.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("info.goety.items.dark_scroll.desc").withStyle(ChatFormatting.DARK_PURPLE));
    }
}
