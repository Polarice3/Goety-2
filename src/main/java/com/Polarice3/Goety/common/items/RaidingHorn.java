package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class RaidingHorn extends Item {
    public RaidingHorn() {
        super(new Properties().stacksTo(1));
    }

    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        if (worldIn instanceof ServerLevel serverWorld){
            boolean flag = serverWorld.isVillage(entityLiving.blockPosition());
            entityLiving.playSound(SoundEvents.RAID_HORN.get(), 16.0F, 1.0F);
            serverWorld.playSound(null, entityLiving.blockPosition(), SoundEvents.RAID_HORN.get(), SoundSource.NEUTRAL, 16.0F, 1.0F);
            if (entityLiving instanceof Player player) {
                if (SEHelper.getAllyEntityTypes(player).contains(EntityType.VILLAGER)) {
                    flag = false;
                }
            }
            if (flag){
                int i = 0;
                for (RaiderServant servant : worldIn.getEntitiesOfClass(RaiderServant.class, entityLiving.getBoundingBox().inflate(16.0F))) {
                    if (servant.getTrueOwner() == entityLiving) {
                        ++i;
                        servant.setRaidPos(entityLiving.blockPosition());
                        servant.setRaidDim(serverWorld.dimension());
                        if (servant.isStaying()) {
                            servant.setStaying(false);
                        }
                        if (servant.isGuardingArea()) {
                            servant.setBoundPos(null);
                        }
                        if (servant.isLeader()) {
                            for (RaiderServant servant1 : servant.getNearbyCompanions()) {
                                servant1.setRaidPos(entityLiving.blockPosition());
                                servant1.setRaidDim(serverWorld.dimension());
                                if (servant1.getLeader() == null) {
                                    servant1.setLeader(servant);
                                    servant1.setFollowing();
                                }
                            }
                        }
                    }
                }
                if (i > 0) {
                    if (entityLiving instanceof Player player) {
                        SEHelper.addCooldown(player, this, 300);
                    }
                }
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ChatFormatting main = ChatFormatting.DARK_PURPLE;

        if (stack.is(this)) {
            tooltip.add(Component.translatable("info.goety.raiding_horn").withStyle(main));
        }
    }
}
