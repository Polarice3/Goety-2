package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.common.blocks.VoidBarrelBlock;
import com.Polarice3.Goety.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

public class VoidBarrelBlockEntity extends BlockEntity {
    public int tick;

    public VoidBarrelBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.VOID_BARREL.get(), p_155229_, p_155230_);
    }

    public void tick() {
        if (this.level instanceof ServerLevel serverLevel) {
            if (this.getBlockState().getValue(VoidBarrelBlock.HALF) == DoubleBlockHalf.LOWER) {
                Vec3 vec3 = this.worldPosition.getCenter();
                if (this.getBlockState().getValue(VoidBarrelBlock.TRIGGERED) && !this.getBlockState().getValue(VoidBarrelBlock.LIT)) {
                    ++this.tick;
                    if (this.tick >= MathHelper.secondsToTicks(3)) {
                        ColorUtil colorUtil = new ColorUtil(ChatFormatting.LIGHT_PURPLE);
                        serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 3, 1), vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 5, 1), vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        serverLevel.sendParticles(new VerticalCircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 5, 1), vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        for (int i = 0; i < 4; ++i) {
                            serverLevel.sendParticles(ModParticleTypes.BIG_CULT_SPELL.get(), vec3.x + (serverLevel.getRandom().nextGaussian() / 16.0F), vec3.y + 1.0F + (serverLevel.getRandom().nextGaussian() / 16.0F), vec3.z + (serverLevel.getRandom().nextGaussian() / 16.0F), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
                        }
                        new SpellExplosion(serverLevel, null, ModDamageSource.getDamageSource(serverLevel, ModDamageSource.VOIDED), vec3.x, vec3.y, vec3.z, 6.0F, 5.0F) {
                            public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage) {
                                target.hurt(damageSource, actualDamage);
                                if (target instanceof LivingEntity) {
                                    Vec3 vec31 = new Vec3(x, y, z).scale(1.5D);
                                    MobUtil.push(target, vec31.x, vec31.y, vec31.z);
                                }
                            }
                        };
                        new SpellExplosion(serverLevel, null, serverLevel.damageSources().explosion(null), vec3.x, vec3.y, vec3.z, 5.0F, 4.0F);
                        //this.level.playSound(null, this.worldPosition, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 4.0F, 1.0F);
                        this.level.playSound(null, this.worldPosition, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (serverLevel.getRandom().nextFloat() - serverLevel.getRandom().nextFloat()) * 0.2F) * 0.7F);
                        this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(VoidBarrelBlock.LIT, true));
                    }
                } else if (this.getBlockState().getValue(VoidBarrelBlock.LIT)) {
                    if (serverLevel.getRandom().nextInt(10) == 0) {
                        ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_PURPLE);
                        serverLevel.sendParticles(ModParticleTypes.BIG_CULT_SPELL.get(), vec3.x, vec3.y + 1.0F, vec3.z, 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
                    }
                }
            }
        }
    }

    public void load(CompoundTag p_155113_) {
        super.load(p_155113_);
        this.tick = p_155113_.getInt("Tick");
    }

    protected void saveAdditional(CompoundTag p_187463_) {
        super.saveAdditional(p_187463_);
        p_187463_.putInt("Tick", this.tick);
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
