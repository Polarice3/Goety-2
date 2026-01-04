package com.Polarice3.Goety.common.ritual.type;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.common.ritual.RitualTypes;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class StormRitualType implements IRitualType {
    @Override
    public String getName() {
        return RitualTypes.STORM;
    }

    @Override
    public ItemStack getJeiIcon() {
        return new ItemStack(Items.LIGHTNING_ROD);
    }

    @Override
    public boolean getRequirement(RitualBlockEntity pTileEntity, Player pPlayer, BlockPos pPos, Level pLevel) {
        if (!pLevel.canSeeSky(pPos.above())) {
            if (pPlayer != null) {
                pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.exposed"), true);
            }
            return false;
        } else if (!pLevel.isThundering()) {
            if (pPlayer != null) {
                pPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.storm"), true);
            }
            return false;
        }
        return RitualRequirements.getStructures(this.getName(), pPlayer, pPos, pLevel) && RitualRequirements.skyRitual(pPlayer, pTileEntity, pLevel, pPos);
    }

    public void onPerformRitual(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                                 Player castingPlayer, ItemStack activationItem) {
        if (world instanceof ServerLevel serverLevel) {
            ColorUtil color = new ColorUtil(0xffffff);
            ServerParticleUtil.windParticle(serverLevel, color, 1.0F + world.random.nextFloat() * 0.5F, 0.0F, -1, Vec3.atBottomCenterOf(darkAltarPos));
            Vec3 vec3 = Vec3.atCenterOf(darkAltarPos);
            if (world.getRandom().nextInt(20) == 0) {
                Vec3 endVec = vec3.add(0.0D, 2.5D, 0.0D);
                ModNetwork.sendToALL(new SLightningPacket(vec3, endVec, new ColorUtil(ChatFormatting.AQUA), 2));
            }
        }
    }

    @Override
    public void onFinishRitual(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity, Player castingPlayer, ItemStack activationItem) {
        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, world);
        lightningBolt.setVisualOnly(true);
        lightningBolt.setPos(Vec3.atCenterOf(darkAltarPos));
        world.addFreshEntity(lightningBolt);
    }
}
