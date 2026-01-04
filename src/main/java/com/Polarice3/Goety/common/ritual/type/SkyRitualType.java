package com.Polarice3.Goety.common.ritual.type;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.common.ritual.RitualTypes;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SkyRitualType implements IRitualType {
    @Override
    public String getName() {
        return RitualTypes.SKY;
    }

    @Override
    public ItemStack getJeiIcon() {
        return new ItemStack(ModBlocks.MARBLE_BLOCK.get());
    }

    @Override
    public boolean getRequirement(RitualBlockEntity pTileEntity, Player pPlayer, BlockPos pPos, Level pLevel) {
        return RitualRequirements.skyRitual(pPlayer, pTileEntity, pLevel, pPos);
    }

    public void onPerformRitual(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                                Player castingPlayer, ItemStack activationItem) {
        if (world instanceof ServerLevel serverLevel) {
            ColorUtil color = new ColorUtil(0xffffff);
            ServerParticleUtil.windParticle(serverLevel, color, 1.0F + world.random.nextFloat() * 0.5F, 0.0F, -1, Vec3.atBottomCenterOf(darkAltarPos));
        }
    }
}
