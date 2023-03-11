package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.IceBouquetTrapBlock;
import com.Polarice3.Goety.common.entities.projectiles.IceBouquet;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class IceBouquetTrapBlockEntity extends OwnedBlockEntity {
    public int activated;
    public int ticks;
    public boolean firing;

    public IceBouquetTrapBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ICE_BOUQUET_TRAP.get(), blockPos, blockState);
    }

    public void fire(){
        if (!this.firing) {
            this.playSound(ModSounds.SUMMON_SPELL.get());
            this.ticks = 0;
            this.firing = true;
        }
    }

    public void tick() {
        if (!this.level.isClientSide) {
            if (this.firing) {
                ++this.ticks;
            }
            if (this.ticks == 1) {
                this.activated = 20;
                BlockPos blockPos = this.getBlockPos().above();
                IceBouquet ghostFire = new IceBouquet(this.level, blockPos.getX() + 0.5F, blockPos.getY(), blockPos.getZ() + 0.5F, this.getTrueOwner());
                ghostFire.setSoulEating(true);
                this.level.addFreshEntity(ghostFire);
            }
            if (this.ticks >= 70) {
                this.firing = false;
                this.ticks = 0;
            }
            if (this.activated != 0) {
                --this.activated;
                this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(IceBouquetTrapBlock.POWERED, true), 3);
            } else {
                this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(IceBouquetTrapBlock.POWERED, false), 3);
            }
        }
    }

    public void playSound(SoundEvent sound) {
        this.level.playSound(null, this.worldPosition, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void setRemoved() {
        super.setRemoved();
    }
}
