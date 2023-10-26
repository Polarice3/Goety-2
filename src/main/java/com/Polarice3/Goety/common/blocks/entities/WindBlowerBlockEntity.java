package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.WindBlowerBlock;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Fan Mechanic based of @vadis365 code: <a href="https://github.com/vadis365/Mob-Grinding-Utils/blob/MC1.19/MobGrindingUtils/MobGrindingUtils/src/main/java/mob_grinding_utils/tile/TileEntityFan.java">...</a>
 */
public class WindBlowerBlockEntity extends BlockEntity {

    public WindBlowerBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.WIND_BLOWER.get(), p_155229_, p_155230_);
    }

    public void tick() {
        if (this.level != null) {
            if (this.level.getBlockState(this.getBlockPos()).getBlock() instanceof WindBlowerBlock){
                Direction facing = this.level.getBlockState(this.getBlockPos()).getValue(WindBlowerBlock.FACING);
                double power = this.level.getBlockState(this.getBlockPos()).getValue(WindBlowerBlock.POWER);
                boolean active = power > 0;
                if (this.level.getBlockEntity(this.worldPosition) instanceof WindBlowerBlockEntity fan) {
                    if (this.level.getGameTime() % 2 == 0 && this.level.getBlockState(this.worldPosition).getBlock() instanceof WindBlowerBlock){
                        if (active) {
                            fan.blowEntities();
                        }
                    }
                    if (!this.level.isClientSide) {
                        this.level.sendBlockUpdated(this.getBlockPos(), this.level.getBlockState(this.getBlockPos()), this.level.getBlockState(this.getBlockPos()), 8);
                    }
                    //Particle codes based of Create mod's Air Flow Particle: https://github.com/Creators-of-Create/Create/blob/mc1.18/dev/src/main/java/com/simibubi/create/content/kinetics/fan/AirFlowParticle.java
                    if (this.level.isClientSide){
                        if (active && this.level.random.nextFloat() <= 0.25F) {
                            Vec3 pos = getCenterOf(this.getBlockPos()).add(Vec3.atLowerCornerOf(facing.getNormal()).scale(0.5D));
                            Vec3 direction = Vec3.atLowerCornerOf(facing.getNormal());
                            Vec3 motion = direction.scale(1 / 8.0F);
                            double distance = new Vec3(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ()).subtract(getCenterOf(this.getBlockPos())).multiply(direction).length() - 0.5F;
                            motion = motion.scale(power - (distance - 1.0F)).scale(0.5F);
                            this.level.addParticle(ModParticleTypes.FAN_CLOUD.get(), pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
                        }
                    }
                }
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(WindBlowerBlock.POWERED, active), 3);
            }
        }
    }

    public static Vec3 getCenterOf(Vec3i pos) {
        if (pos.equals(Vec3i.ZERO)) {
            return new Vec3(0.5D, 0.5D, 0.5D);
        }
        return Vec3.atLowerCornerOf(pos).add(0.5F, 0.5F, 0.5F);
    }

    public AABB getAABB() {
        if (this.level != null) {
            BlockState state = this.level.getBlockState(getBlockPos());
            if (!(state.getBlock() instanceof WindBlowerBlock)) {
                return new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            Direction facing = state.getValue(WindBlowerBlock.FACING);

            int distance;
            for (distance = 1; distance < state.getValue(WindBlowerBlock.POWER); distance++) {
                BlockState state2 = this.level.getBlockState(getBlockPos().relative(facing, distance));
                if (state2.isSolid() || state2.liquid()) {
                    break;
                }
            }
            BlockPos blockPos2 = this.getBlockPos().relative(facing, distance);
            switch (facing) {
                case UP, EAST, SOUTH -> blockPos2 = blockPos2.offset(1, 1, 1);
                case DOWN -> blockPos2 = blockPos2.offset(1, 0, 1);
                case WEST -> blockPos2 = blockPos2.offset(0, 1, 1);
                case NORTH -> blockPos2 = blockPos2.offset(1, 1, 0);
            }
            return new AABB(this.getBlockPos(), blockPos2);
        } else {
            return new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    protected void blowEntities() {
        if (this.level != null) {
            BlockState state = this.level.getBlockState(getBlockPos());
            if (!(state.getBlock() instanceof WindBlowerBlock)) {
                return;
            }
            Direction facing = state.getValue(WindBlowerBlock.FACING);
            List<Entity> list = this.level.getEntitiesOfClass(Entity.class, getAABB(), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
            for (Entity entity : list) {
                if (entity != null) {
                    if (facing.getAxis().isHorizontal()) {
                        double strength = entity.isShiftKeyDown() ? 0.005D : 0.25D;
                        MobUtil.push(entity, Mth.sin(facing.getOpposite().toYRot() * (float) Math.PI / 180.0F) * strength, 0.0D, -Mth.cos(facing.getOpposite().toYRot() * (float) Math.PI / 180.0F) * strength);
                    } else if (facing == Direction.UP) {
                        Vec3 vec3d = entity.getDeltaMovement();
                        entity.setDeltaMovement(vec3d.x, 0.125F, vec3d.z);
                        MobUtil.push(entity, 0D, 0.25D, 0D);
                        entity.fallDistance = 0;
                    } else {
                        MobUtil.push(entity, 0D, -0.2D, 0D);
                    }
                }
            }
        }
    }
}
