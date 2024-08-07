package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.client.particles.SparkleParticleOption;
import com.Polarice3.Goety.common.blocks.HoleBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.magic.spells.void_spells.TunnelSpell;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class HoleBlockEntity extends SaveBlockEntity{
    public int life = 0;
    public int lifespan = 120;
    public int count = 0;
    public Direction direction = null;

    public HoleBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.HOLE.get(), p_155229_, p_155230_);
    }

    public void setStats(BlockState oldBlock, int initLife, int lifespan, int count, Direction direction){
        this.oldBlock = oldBlock;
        this.life = initLife;
        this.lifespan = lifespan;
        this.count = count;
        this.direction = direction;
        this.setChanged();
    }

    public void setStats(BlockState oldBlock, int lifespan, int count, Direction direction){
        this.setStats(oldBlock, 0, lifespan, count, direction);
    }

    public void tick() {
        if (this.level == null){
            return;
        }
        if (this.level.isClientSide){
            for (int a = 0; a < 2; ++a) {
                this.sparkle();
            }
        }
        if (!this.level.isClientSide) {
            if (this.life == 0 && this.count > 1 && this.direction != null) {
                switch (this.direction.getAxis()) {
                    case Y -> {
                        for (int i = 0; i < 9; ++i) {
                            if (i / 3 != 1 || i % 3 != 1) {
                                TunnelSpell.createHole(this.level, this.getBlockPos().offset(-1 + i / 3, 0, -1 + i % 3), null, 1, this.lifespan);
                            }
                        }
                    }
                    case Z -> {
                        for (int i = 0; i < 9; ++i) {
                            if (i / 3 != 1 || i % 3 != 1) {
                                TunnelSpell.createHole(this.level, this.getBlockPos().offset(-1 + i / 3, -1 + i % 3, 0), null, 1, this.lifespan);
                            }
                        }
                    }
                    case X -> {
                        for (int i = 0; i < 9; ++i) {
                            if (i / 3 != 1 || i % 3 != 1) {
                                TunnelSpell.createHole(this.level, this.getBlockPos().offset(0, -1 + i / 3, -1 + i % 3), null, 1, this.lifespan);
                            }
                        }
                    }
                }
                if (this.count > 2) {
                    if (!TunnelSpell.createHole(this.level, this.getBlockPos().relative(this.direction.getOpposite()), this.direction, this.count - 1, this.lifespan)) {
                        this.count = 0;
                    }
                }
            }
            ++this.life;
            if (this.life % 20 == 0) {
                this.setChanged();
            }
            if (this.life >= this.lifespan) {
                BlockState blockState = Blocks.AIR.defaultBlockState();
                if (this.oldBlock != null) {
                    blockState = this.oldBlock;
                }
                this.level.setBlock(this.getBlockPos(), blockState, 3);
            }
        }
    }

    public boolean shouldRenderFace(Direction direction) {
        if (this.level == null){
            return false;
        }
        BlockPos blockPos = this.getBlockPos().relative(direction);
        BlockState blockState = this.level.getBlockState(blockPos);
        return !blockState.isAir()
                && !(blockState.getBlock() instanceof HoleBlock)
                && blockState.isSolidRender(this.level, blockPos);
    }

    private void sparkle() {
        if (this.level == null){
            return;
        }
        for (Direction direction : Direction.values()) {
            BlockPos blockPos = this.getBlockPos().relative(direction);
            BlockState blockState = this.level.getBlockState(this.getBlockPos().relative(direction));
            if (blockState.getBlock() != ModBlocks.HOLE.get() && !blockState.isSolidRender(this.level, blockPos)) {
                for (Direction direction1 : Direction.values()) {
                    BlockPos blockPos1 = this.getBlockPos().relative(direction1);
                    if (direction.getAxis() != direction1.getAxis()
                            && (this.level.getBlockState(blockPos1).isSolidRender(this.level, blockPos1)
                            || this.level.getBlockState(blockPos.relative(direction1)).isSolidRender(this.level, blockPos.relative(direction1)))) {
                        float x = 0.5F * direction.getStepX();
                        float y = 0.5F * direction.getStepY();
                        float z = 0.5F * direction.getStepZ();
                        if (x == 0.0F) {
                            x = 0.5F * direction1.getStepX();
                        }
                        if (y == 0.0F) {
                            y = 0.5F * direction1.getStepY();
                        }
                        if (z == 0.0F) {
                            z = 0.5F * direction1.getStepZ();
                        }
                        if (x == 0.0F) {
                            x = this.level.random.nextFloat();
                        } else {
                            x += 0.5F;
                        }
                        if (y == 0.0F) {
                            y = this.level.random.nextFloat();
                        } else {
                            y += 0.5F;
                        }
                        if (z == 0.0F) {
                            z = this.level.random.nextFloat();
                        } else {
                            z += 0.5F;
                        }
                        if (this.level.random.nextInt(6) == 0){
                            float[] color = MathHelper.rgbFloat(0x355b9e);
                            this.level.addParticle(new SparkleParticleOption(0.6F + this.level.random.nextFloat() * 0.2F,
                                            color[0], color[1], color[2],
                                            6 + this.level.random.nextInt(4)),
                                    this.getBlockPos().getX() + x,
                                    this.getBlockPos().getY() + y,
                                    this.getBlockPos().getZ() + z, 0.0D, 0.0D, 0.0D);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void readNetwork(CompoundTag compoundTag) {
        super.readNetwork(compoundTag);
        if (compoundTag.contains("Life")) {
            this.life = compoundTag.getInt("Life");
        }
        if (compoundTag.contains("Lifespan")) {
            this.lifespan = compoundTag.getInt("Lifespan");
        }
        if (compoundTag.contains("Count")) {
            this.count = compoundTag.getInt("Count");
        }
        if (compoundTag.contains("Direction")){
            int ordinal = compoundTag.getInt("Direction");
            if (ordinal > 0 && ordinal < Direction.values().length + 1){
                this.direction = Direction.values()[compoundTag.getInt("Direction")];
            } else {
                this.direction = null;
            }
        }
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag compoundTag) {
        super.writeNetwork(compoundTag);
        compoundTag.putInt("Life", this.life);
        compoundTag.putInt("Lifespan", this.lifespan);
        compoundTag.putInt("Count", this.count);
        int ordinalInt = -1;
        if (this.direction != null){
            ordinalInt = this.direction.ordinal();
        }
        compoundTag.putInt("Direction", ordinalInt);
        return compoundTag;
    }
}
