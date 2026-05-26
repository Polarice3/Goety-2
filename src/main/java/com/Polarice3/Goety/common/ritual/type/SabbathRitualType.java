package com.Polarice3.Goety.common.ritual.type;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.common.ritual.Ritual;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.common.ritual.RitualTypes;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockState;
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

    @Override
    public void onFinishRitual(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity, Player castingPlayer, ItemStack activationItem) {
        if (tileEntity.getCurrentRitualRecipe().getEntityToSummon() == ModEntityType.SUMMON_APOSTLE.get()){
            if (world instanceof ServerLevel serverLevel) {
                ModNetwork.sendToALL(new SPlayWorldSoundPacket(darkAltarPos, SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.get(), 1.0F, 1.0F));
                Warden.applyDarknessAround(serverLevel, Vec3.atCenterOf(darkAltarPos), (Entity)null, 32);
            }
            for (int i = -Ritual.RANGE; i <= Ritual.RANGE; ++i) {
                for (int j = -Ritual.RANGE; j <= Ritual.RANGE; ++j) {
                    for (int k = -Ritual.RANGE; k <= Ritual.RANGE; ++k) {
                        BlockPos blockpos1 = darkAltarPos.offset(i, j, k);
                        BlockState blockstate = world.getBlockState(blockpos1);
                        if (blockstate.getBlock() instanceof SoulFireBlock){
                            world.destroyBlock(blockpos1, false);
                            world.levelEvent((Player)null, 1009, blockpos1, 0);
                        }
                    }
                }
            }
        }
    }
}
