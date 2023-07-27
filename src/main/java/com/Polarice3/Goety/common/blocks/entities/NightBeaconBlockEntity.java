package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.client.particles.PortalShockwaveParticleOption;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;

public class NightBeaconBlockEntity extends BlockEntity {
    List<BeaconBeamSection> beamSections = Lists.newArrayList();
    private List<BeaconBeamSection> checkingBeamSections = Lists.newArrayList();
    private int lastCheckY;

    public NightBeaconBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.NIGHT_BEACON.get(), p_155229_, p_155230_);
    }

    public static void tick(Level p_155108_, BlockPos p_155109_, BlockState p_155110_, NightBeaconBlockEntity p_155111_) {
        int i = p_155109_.getX();
        int j = p_155109_.getY();
        int k = p_155109_.getZ();
        BlockPos blockpos;
        if (p_155111_.lastCheckY < j) {
            blockpos = p_155109_;
            p_155111_.checkingBeamSections = Lists.newArrayList();
            p_155111_.lastCheckY = p_155109_.getY() - 1;
        } else {
            blockpos = new BlockPos(i, p_155111_.lastCheckY + 1, k);
        }

        BeaconBeamSection beaconblockentity$beaconbeamsection = new BeaconBeamSection();
        int l = p_155108_.getHeight(Heightmap.Types.WORLD_SURFACE, i, k);

        for(int i1 = 0; i1 < 10 && blockpos.getY() <= l; ++i1) {
            BlockState blockstate = p_155108_.getBlockState(blockpos);
            p_155111_.checkingBeamSections.add(beaconblockentity$beaconbeamsection);
            if (blockstate.getLightBlock(p_155108_, blockpos) >= 15 && !blockstate.is(Blocks.BEDROCK)) {
                p_155111_.checkingBeamSections.clear();
                p_155111_.lastCheckY = l;
                break;
            }

            beaconblockentity$beaconbeamsection.increaseHeight();

            blockpos = blockpos.above();
            ++p_155111_.lastCheckY;
        }

        if (p_155108_.getGameTime() % 80L == 0L) {
            if (hasPortal(p_155108_, p_155109_) && !p_155111_.beamSections.isEmpty()) {
                playSound(p_155108_, p_155109_, SoundEvents.BEACON_AMBIENT);
            }
        }

        if (p_155108_.getServer() != null) {
            if (hasPortal(p_155108_, p_155109_) && !p_155111_.beamSections.isEmpty()) {
                for (ServerLevel serverLevel : p_155108_.getServer().getAllLevels()) {
                    serverLevel.setDayTime(18000);
                }
            }
        }

        if (p_155111_.lastCheckY >= l) {
            p_155111_.lastCheckY = p_155108_.getMinBuildHeight() - 1;
            boolean flag = hasPortal(p_155108_, p_155109_);
            p_155111_.beamSections = p_155111_.checkingBeamSections;
            if (!p_155108_.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) p_155108_;
                boolean flag1 = hasPortal(p_155108_, p_155109_);
                if (!flag && flag1) {
                    playSound(p_155108_, p_155109_, SoundEvents.BEACON_ACTIVATE);
                    serverLevel.sendParticles(new PortalShockwaveParticleOption(0), i, j, k, 0, 0, 0, 0, 0);
                } else if (flag && !flag1) {
                    playSound(p_155108_, p_155109_, SoundEvents.BEACON_DEACTIVATE);
                }
            }
        }

    }

    private static boolean hasPortal(Level level, BlockPos blockPos){
        return level.getBlockState(blockPos.below().below()).is(Blocks.NETHER_PORTAL) && MainConfig.EnableNightBeacon.get();
    }

    public void setRemoved() {
        if (this.level != null) {
            playSound(this.level, this.worldPosition, SoundEvents.BEACON_DEACTIVATE);
            if (MainConfig.EnableNightBeacon.get()) {
                if (this.level.getServer() != null) {
                    for (ServerLevel serverLevel : this.level.getServer().getAllLevels()) {
                        serverLevel.setDayTime(23000);
                    }
                }
            }
        }
        super.setRemoved();
    }

    public static void playSound(Level p_155104_, BlockPos p_155105_, SoundEvent p_155106_) {
        p_155104_.playSound((Player)null, p_155105_, p_155106_, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public List<BeaconBeamSection> getBeamSections() {
        return !hasPortal(this.level, this.worldPosition) ? ImmutableList.of() : this.beamSections;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public static class BeaconBeamSection {
        private int height;

        public BeaconBeamSection() {
            this.height = 1;
        }

        protected void increaseHeight() {
            ++this.height;
        }

        public int getHeight() {
            return this.height;
        }
    }
}
