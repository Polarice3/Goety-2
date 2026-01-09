package com.Polarice3.Goety.common.ritual.type;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.common.ritual.RitualTypes;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SabbathRitualType implements IRitualType {
    @Override
    public String getName() {
        return RitualTypes.SABBATH;
    }

    @Override
    public ItemStack getJeiIcon() {
        return new ItemStack(Items.CRYING_OBSIDIAN);
    }

    @Override
    public boolean getRequirement(RitualBlockEntity pTileEntity, Player pPlayer, BlockPos pPos, Level pLevel) {
        return RitualRequirements.getStructures(this.getName(), pPlayer, pPos, pLevel);
    }

    public void sendFinishRay(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                              Player castingPlayer, ItemStack activationItem) {
        if (world instanceof ServerLevel serverLevel) {
            ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_RED);
            Vec3 vec3 = darkAltarPos.getCenter();
            ServerParticleUtil.sendStretchedGodRay(serverLevel, vec3.x, vec3.y, vec3.z, colorUtil);
        }
    }
}
