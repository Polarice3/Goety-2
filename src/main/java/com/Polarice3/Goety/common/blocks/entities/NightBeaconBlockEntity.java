package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.client.particles.PortalShockwaveParticleOption;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.MathHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class NightBeaconBlockEntity extends BlockEntity {
    List<BeaconBeamSection> beamSections = Lists.newArrayList();
    private List<BeaconBeamSection> checkingBeamSections = Lists.newArrayList();
    private boolean daylightTrue;
    private boolean isNight;
    private boolean hasPortal;
    private boolean isActive;
    private int lastCheckY;

    public NightBeaconBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.NIGHT_BEACON.get(), p_155229_, p_155230_);
    }

    public static void tick(Level p_155108_, BlockPos p_155109_, BlockState p_155110_, NightBeaconBlockEntity p_155111_) {
        if (p_155108_ instanceof ServerLevel world) {
            ChunkPos chunkPos = world.getChunkAt(p_155109_).getPos();
            if (!world.getForcedChunks().contains(chunkPos.toLong())) {
                world.setChunkForced(chunkPos.x, chunkPos.z, true);
                if (!world.isAreaLoaded(p_155109_, 2)) {
                    world.getChunkAt(p_155109_).setLoaded(true);
                }
            }
        }
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

        BeaconBeamSection beaconblockentity$beaconbeamsection = p_155111_.checkingBeamSections.isEmpty() ? null : p_155111_.checkingBeamSections.get(p_155111_.checkingBeamSections.size() - 1);
        int l = p_155108_.getHeight(Heightmap.Types.WORLD_SURFACE, i, k);

        for(int i1 = 0; i1 < 10 && blockpos.getY() <= l; ++i1) {
            BlockState blockstate = p_155108_.getBlockState(blockpos);
            float[] afloat = blockstate.getBeaconColorMultiplier(p_155108_, blockpos, p_155109_);
            if (afloat != null) {
                if (p_155111_.checkingBeamSections.size() <= 1) {
                    beaconblockentity$beaconbeamsection = new BeaconBeamSection();
                    p_155111_.checkingBeamSections.add(beaconblockentity$beaconbeamsection);
                } else if (beaconblockentity$beaconbeamsection != null) {
                    beaconblockentity$beaconbeamsection.increaseHeight();
                }
            } else {
                if (beaconblockentity$beaconbeamsection == null || blockstate.getLightBlock(p_155108_, blockpos) >= 15 && !blockstate.is(Blocks.BEDROCK)) {
                    p_155111_.checkingBeamSections.clear();
                    p_155111_.lastCheckY = l;
                    break;
                }

                beaconblockentity$beaconbeamsection.increaseHeight();
            }

            blockpos = blockpos.above();
            ++p_155111_.lastCheckY;
        }

        if (p_155108_.getGameTime() % 20L == 0L){
            if (!p_155111_.beamSections.isEmpty()) {
                p_155111_.hasPortal = hasPortal(p_155108_, p_155109_);
            }
        }

        if (p_155108_.getGameTime() % 80L == 0L) {
            if (p_155111_.isActive && !p_155111_.beamSections.isEmpty()) {
                playSound(p_155108_, p_155109_, SoundEvents.BEACON_AMBIENT);
            }
        }

        if (p_155108_.getServer() != null) {
            if (p_155111_.isActive && !p_155111_.beamSections.isEmpty()) {
                for (ServerLevel serverLevel : p_155108_.getServer().getAllLevels()) {
                    serverLevel.setDayTime(MathHelper.setDayNumberAndTime(40, 18000));
                    for (ServerPlayer player : serverLevel.players()) {
                        player.connection.send(new ClientboundSetTimePacket(serverLevel.getGameTime(), serverLevel.getDayTime(), serverLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
                    }
                }
            }
        }

        if (p_155111_.lastCheckY >= l) {
            p_155111_.lastCheckY = p_155108_.getMinBuildHeight() - 1;
            p_155111_.beamSections = p_155111_.checkingBeamSections;
        }

        if (!p_155108_.isClientSide && p_155108_ instanceof ServerLevel serverLevel) {
            if (!p_155111_.isActive && !p_155111_.getBeamSections().isEmpty()) {
                playSound(p_155108_, p_155109_, SoundEvents.BEACON_ACTIVATE);
                serverLevel.sendParticles(new PortalShockwaveParticleOption(0), i + 0.5F, j, k + 0.5F, 0, 0, 0, 0, 0);
                p_155111_.isActive = true;
            } else if (p_155111_.isActive && p_155111_.getBeamSections().isEmpty()){
                playSound(p_155108_, p_155109_, SoundEvents.BEACON_DEACTIVATE);
                p_155111_.isActive = false;
            }
        }
    }

    private static boolean hasPortal(Level level, BlockPos blockPos){
        return level.getBlockState(blockPos.below().below()).is(Blocks.NETHER_PORTAL) && MainConfig.EnableNightBeacon.get();
    }

    public void setDaylLight(boolean daylightTrue){
        this.daylightTrue = daylightTrue;
    }

    public void setRemoved() {
        if (this.level != null) {
            playSound(this.level, this.worldPosition, SoundEvents.BEACON_DEACTIVATE);
            if (MainConfig.EnableNightBeacon.get()) {
                if (this.level.getServer() != null && this.isActive && !this.beamSections.isEmpty()) {
                    for (ServerLevel serverLevel : this.level.getServer().getAllLevels()) {
                        serverLevel.setDayTime(MathHelper.setDayNumberAndTime(40, 23000));
                        for (ServerPlayer player : serverLevel.players()) {
                            player.connection.send(new ClientboundSetTimePacket(serverLevel.getGameTime(), serverLevel.getDayTime(), serverLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
                        }
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
        return !this.hasPortal ? ImmutableList.of() : this.beamSections;
    }

    public void load(CompoundTag p_155113_) {
        super.load(p_155113_);
        this.daylightTrue = p_155113_.getBoolean("daylightTrue");
        this.isNight = p_155113_.getBoolean("isNight");
        this.hasPortal = p_155113_.getBoolean("hasPortal");
        this.isActive = p_155113_.getBoolean("isActive");
    }

    protected void saveAdditional(CompoundTag p_187463_) {
        super.saveAdditional(p_187463_);
        p_187463_.putBoolean("daylightTrue", this.daylightTrue);
        p_187463_.putBoolean("isNight", this.isNight);
        p_187463_.putBoolean("hasPortal", this.hasPortal);
        p_187463_.putBoolean("isActive", this.isActive);
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

    @Override
    public AABB getRenderBoundingBox() {
        return BlockEntity.INFINITE_EXTENT_AABB;
    }

    public void setLevel(Level p_155091_) {
        super.setLevel(p_155091_);
        this.lastCheckY = p_155091_.getMinBuildHeight() - 1;
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
