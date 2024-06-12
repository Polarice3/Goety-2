package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HookBellBlockEntity extends BlockEntity {
    private long lastRingTimestamp;
    public int ticks;
    public boolean shaking;
    public Direction clickDirection;
    private List<LivingEntity> nearbyEntities;
    private boolean resonating;
    private int resonationTicks;
    private static final double minRange = 16.0D;
    private static final double maxRange = 128.0D;

    public HookBellBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.HOOK_BELL.get(), p_155229_, p_155230_);
    }

    public boolean triggerEvent(int p_58837_, int p_58838_) {
        if (p_58837_ == 1) {
            this.updateEntities();
            this.resonationTicks = 0;
            this.clickDirection = Direction.from3DDataValue(p_58838_);
            this.ticks = 0;
            this.shaking = true;
            return true;
        } else {
            return super.triggerEvent(p_58837_, p_58838_);
        }
    }

    private static void tick(Level p_155181_, BlockPos p_155182_, BlockState p_155183_, HookBellBlockEntity p_155184_) {
        if (p_155184_.shaking) {
            ++p_155184_.ticks;
            if (p_155181_ instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 2; ++i) {
                    serverLevel.sendParticles(ParticleTypes.PORTAL, p_155184_.getBlockPos().getX() + 0.5D, p_155184_.getBlockPos().getY() + 0.5D, p_155184_.getBlockPos().getZ() + 0.5D, 0, (p_155181_.random.nextDouble() - 0.5D) * 2.0D, -p_155181_.random.nextDouble(), (p_155181_.random.nextDouble() - 0.5D) * 2.0D, 0.5F);
                }
            }
        }

        if (p_155184_.ticks >= 50) {
            p_155184_.shaking = false;
            p_155184_.ticks = 0;
        }

        if (p_155184_.ticks >= 5 && p_155184_.resonationTicks == 0 && areRaidersNearby(p_155182_, p_155184_.nearbyEntities)) {
            p_155184_.resonating = true;
            p_155181_.playSound((Player)null, p_155182_, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 1.0F, 0.5F);
        }

        if (p_155184_.resonating) {
            if (p_155184_.resonationTicks < 40) {
                ++p_155184_.resonationTicks;
            } else {
                teleportRaiders(p_155181_, p_155182_, p_155184_.nearbyEntities);
                p_155184_.resonating = false;
            }
        }

    }

    public static void clientTick(Level p_155176_, BlockPos p_155177_, BlockState p_155178_, HookBellBlockEntity p_155179_) {
        tick(p_155176_, p_155177_, p_155178_, p_155179_);
    }

    public static void serverTick(Level p_155203_, BlockPos p_155204_, BlockState p_155205_, HookBellBlockEntity p_155206_) {
        tick(p_155203_, p_155204_, p_155205_, p_155206_);
    }

    public void onHit(Direction p_58835_) {
        BlockPos blockpos = this.getBlockPos();
        this.clickDirection = p_58835_;
        if (this.shaking) {
            this.ticks = 0;
        } else {
            this.shaking = true;
        }

        if (this.level != null) {
            this.level.blockEvent(blockpos, this.getBlockState().getBlock(), 1, p_58835_.get3DDataValue());
        }
    }

    private void updateEntities() {
        if (this.level != null) {
            BlockPos blockpos = this.getBlockPos();
            if (this.level.getGameTime() > this.lastRingTimestamp + 60L || this.nearbyEntities == null) {
                this.lastRingTimestamp = this.level.getGameTime();
                AABB aabb = (new AABB(blockpos)).inflate(maxRange);
                this.nearbyEntities = this.level.getEntitiesOfClass(LivingEntity.class, aabb);
            }

            if (!this.level.isClientSide) {
                for (LivingEntity livingentity : this.nearbyEntities) {
                    if (livingentity.isAlive() && !livingentity.isRemoved() && blockpos.closerToCenterThan(livingentity.position(), maxRange)) {
                        livingentity.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, this.level.getGameTime());
                    }
                }
            }
        }

    }

    private static boolean areRaidersNearby(BlockPos p_155200_, List<LivingEntity> p_155201_) {
        for(LivingEntity livingentity : p_155201_) {
            if (isRaiderWithinRange(p_155200_, livingentity)) {
                return true;
            }
        }

        return false;
    }

    private static boolean areRaidersClose(BlockPos origin, LivingEntity living){
        return origin.closerToCenterThan(living.position(), maxRange) && !origin.closerToCenterThan(living.position(), minRange);
    }

    private static void teleportRaiders(Level p_155187_, BlockPos p_155188_, List<LivingEntity> p_155189_) {
        if (p_155187_.getBlockEntity(p_155188_) instanceof HookBellBlockEntity callerBellBlock) {
            p_155189_.stream().filter((p_155219_) -> {
                return isRaiderWithinRange(p_155188_, p_155219_) || isRaiderVehicle(p_155188_, p_155219_);
            }).forEach((entity) -> teleport(entity, callerBellBlock));
        }
    }

    private static boolean isRaiderVehicle(BlockPos p_155197_, LivingEntity p_155198_){
        return p_155198_.isAlive()
                && !p_155198_.isRemoved()
                && p_155198_.getFirstPassenger() instanceof LivingEntity rider
                && isRaiderWithinRange(p_155197_, rider);
    }

    private static boolean isRaiderWithinRange(BlockPos p_155197_, LivingEntity p_155198_) {
        return p_155198_.isAlive() && !p_155198_.isRemoved() && areRaidersClose(p_155197_, p_155198_) && p_155198_.getType().is(EntityTypeTags.RAIDERS) && !MobUtil.hasEntityTypesConfig(MainConfig.HookBellBlackList.get(), p_155198_.getType());
    }

    private static void teleport(LivingEntity p_58841_, HookBellBlockEntity blockEntity) {
        for (int i = 0; i < 128; ++i) {
            BlockPos blockPos = blockEntity.worldPosition;
            double d3 = blockPos.getX() + (p_58841_.getRandom().nextDouble() - 0.5D) * 8;
            double d4 = blockPos.getY() + (double) (p_58841_.getRandom().nextInt(16) - 8);
            double d5 = blockPos.getZ() + (p_58841_.getRandom().nextDouble() - 0.5D) * 8;
            if (p_58841_.randomTeleport(d3, d4, d5, true)) {
                p_58841_.level.gameEvent(GameEvent.TELEPORT, p_58841_.position(), GameEvent.Context.of(p_58841_));
                p_58841_.level.playSound((Player) null, p_58841_.xo, p_58841_.yo, p_58841_.zo, SoundEvents.ENDERMAN_TELEPORT, p_58841_.getSoundSource(), 1.0F, 1.0F);
                p_58841_.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                break;
            }
        }
    }
}
