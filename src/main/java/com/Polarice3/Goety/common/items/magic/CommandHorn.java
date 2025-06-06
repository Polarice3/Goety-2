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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class CommandHorn extends Item {
    public CommandHorn() {
        super(new Item.Properties().stacksTo(1));
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        if (level instanceof ServerLevel serverlevel) {
            ColorUtil colorUtil = new ColorUtil(ChatFormatting.AQUA);
            serverlevel.sendParticles(new ShockwaveParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 16.0F, 0, true), player.getX(), player.getY() + 0.25F, player.getZ(), 0, 0, 0, 0, 0);
            for (LivingEntity livingEntity : serverlevel.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(16.0F))){
                if (livingEntity instanceof IServant servant){
                    if (servant.getTrueOwner() == player){
                        if (!servant.isGuardingArea()
                                && !servant.isCommanded()
                                && servant.canUpdateMove()
                                && servant.canStay()
                                && servant.canWander()
                                && !SEHelper.getGroundedEntities(player).contains(livingEntity)
                                && !SEHelper.getGroundedEntityTypes(player).contains(livingEntity.getType())){
                            if (servant.isStaying() || servant.isWandering()){
                                servant.setFollowing();
                                serverlevel.sendParticles(ModParticleTypes.GO.get(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 0, 2.0D, 0, 1.0F);
                            } else {
                                servant.setBoundPos(null);
                                servant.setWandering(false);
                                servant.setStaying(true);
                                serverlevel.sendParticles(ModParticleTypes.STOP.get(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 0, 2.0D, 0, 1.0F);
                            }
                        }
                    }
                }
            }
            player.playSound(ModSounds.WIND_HORN.get(), 1.0F, 0.45F);
            ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(ModSounds.WIND_HORN.get(), 1.0F, 0.45F));
            level.gameEvent(player, GameEvent.INSTRUMENT_PLAY, player.position());
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
                    for (LivingEntity livingEntity : serverlevel.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(16.0F))){
                        if (livingEntity instanceof IServant servant){
                            if (servant.getTrueOwner() == player){
                                if (!servant.isGuardingArea()
                                        && !servant.isCommanded()
                                        && servant.canBeCommanded()
                                        && servant.canUpdateMove()
                                        && !SEHelper.getGroundedEntities(player).contains(livingEntity)
                                        && !SEHelper.getGroundedEntityTypes(player).contains(livingEntity.getType())){
                                    servant.setCommandPos(blockpos.above());
                                    serverlevel.sendParticles(ModParticleTypes.GO.get(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 0, 2.0D, 0, 1.0F);
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
}
