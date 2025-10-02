package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.VoidFrameBlock;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class VoidFrameBlockEntity extends BlockEntity {
    public int coolTick;

    public VoidFrameBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.VOID_FRAME.get(), p_155229_, p_155230_);
    }

    public void tick() {
        if (this.level instanceof ServerLevel serverLevel) {
            if (!this.getBlockState().getValue(VoidFrameBlock.LOCKED)) {
                ++this.coolTick;
                if (this.coolTick < 20) {
                    for (Player player : serverLevel.getEntitiesOfClass(Player.class, new AABB(this.getBlockPos()).inflate(64.0F), EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
                        player.addEffect(new MobEffectInstance(GoetyEffects.IMPAIRED.get(), 5, 0, false, false));
                    }
                }
                if (this.coolTick >= MathHelper.minecraftDayToTicks(1)) {
                    this.coolTick = 0;
                    serverLevel.playSound(null, this.getBlockPos().above(), ModSounds.VOID_SPAWNER_CLOSE_SHUTTER.get(), SoundSource.BLOCKS, 1.0F, 0.8F);
                    serverLevel.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(VoidFrameBlock.LOCKED, true));
                }
            } else {
                Vec3 vec3 = this.getBlockPos().getCenter().offsetRandom(serverLevel.getRandom(), 1.0F);
                serverLevel.sendParticles(ParticleTypes.ENCHANT, vec3.x, vec3.y + 0.5F, vec3.z, 1, 0.0F, 0.0F, 0.0F, 0.0F);
            }
        }
    }

    public void setCoolTick(int coolTick) {
        this.coolTick = coolTick;
    }

    public void load(CompoundTag p_155113_) {
        super.load(p_155113_);
        this.coolTick = p_155113_.getInt("CoolTick");
    }

    protected void saveAdditional(CompoundTag p_187463_) {
        super.saveAdditional(p_187463_);
        p_187463_.putInt("CoolTick", this.coolTick);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            this.load(pkt.getTag());
        }
        super.onDataPacket(net, pkt);
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }
}
