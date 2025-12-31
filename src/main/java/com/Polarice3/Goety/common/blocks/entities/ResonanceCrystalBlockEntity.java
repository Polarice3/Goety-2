package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.blocks.entities.IWindPowered;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ResonanceCrystalBlock;
import com.Polarice3.Goety.common.entities.ally.golem.SquallGolem;
import com.Polarice3.Goety.common.items.block.ResonanceBlockItem;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResonanceCrystalBlockEntity extends ModBlockEntity implements IWindPowered {
    public static String GOLEM_LIST = ResonanceBlockItem.GOLEM_LIST;
    public static String BLOCK_LIST = "BlockList";
    public List<BlockPos> blockPosList = new ArrayList<>();
    public List<SquallGolem> squallGolems = new ArrayList<>();
    public List<UUID> uuids = new ArrayList<>();
    public int active;
    private boolean isOn;
    public boolean showBlock;
    public long ticketTime = 0;

    public ResonanceCrystalBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.RESONANCE_CRYSTAL.get(), p_155229_, p_155230_);
    }

    public void tick(){
        if (this.level != null) {
            if (this.active > 0) {
                --this.active;
            }
            if (this.getBlockState().getValue(ResonanceCrystalBlock.POWERED)) {
                if (!this.isOn){
                    this.level.playSound(null, this.getBlockPos(), ModSounds.RESONANCE_CRYSTAL_ON.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    this.isOn = true;
                } else {
                    if (this.level.getGameTime() % MathHelper.secondsToTicks(6) == 0){
                        this.level.playSound(null, this.getBlockPos(), ModSounds.RESONANCE_CRYSTAL_LOOP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
                if (this.level instanceof ServerLevel world) {
                    ChunkPos chunkPos = this.level.getChunkAt(this.worldPosition).getPos();
                    if (--this.ticketTime <= 0L) {
                        world.getChunkSource().addRegionTicket(ModTicketTypes.BLOCK, chunkPos, 5, this.worldPosition);
                        this.ticketTime = ModTicketTypes.BLOCK.timeout() - 1L;
                    }
                    BlockPos blockPos = this.getBlockPos();
                    ServerParticleUtil.gatheringBlockParticles(ModParticleTypes.RESONANCE_GATHER.get(), blockPos, world);
                    ColorUtil color = new ColorUtil(0xffffff);
                    ServerParticleUtil.windParticle(world, color, 1.0F + world.random.nextFloat() * 0.5F, 0.0F, -1, Vec3.atBottomCenterOf(blockPos));

                    if (!this.uuids.isEmpty()) {
                        this.uuids.removeIf(uuid -> {
                            Entity entity = EntityFinder.getEntityByUuiD(uuid);
                            if (!(entity instanceof SquallGolem squallGolem)){
                                return true;
                            } else if (!squallGolem.isAlive()) {
                                this.squallGolems.remove(squallGolem);
                                return true;
                            }
                            return false;
                        });
                        for (UUID uuid : this.uuids) {
                            if (uuid != null) {
                                Entity entity = EntityFinder.getEntityByUuiD(uuid);
                                if (entity instanceof SquallGolem squallGolem) {
                                    if (!this.squallGolems.contains(squallGolem)) {
                                        this.squallGolems.add(squallGolem);
                                    }
                                }
                            }
                        }
                    }
                    this.activateGolems();
                }
                if (!this.getBlockPosList().isEmpty()){
                    for (BlockPos blockPos1 : this.getBlockPosList()){
                        BlockEntity blockEntity = this.level.getBlockEntity(blockPos1);
                        if (blockEntity instanceof ResonanceCrystalBlockEntity crystalBlock){
                            crystalBlock.activate(20);
                        }
                    }
                }
            } else {
                if (this.isOn){
                    this.level.playSound(null, this.getBlockPos(), ModSounds.RESONANCE_CRYSTAL_OFF.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    this.isOn = false;
                }
            }
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(ResonanceCrystalBlock.POWERED, this.active > 0), 3);
        }
    }

    public void activateGolems(){
        if (!this.squallGolems.isEmpty()){
            for (SquallGolem squallGolem : this.squallGolems){
                if (squallGolem != null) {
                    squallGolem.activate(20);
                }
            }
        }
    }

    public List<BlockPos> getBlockPosList() {
        return this.blockPosList;
    }

    public void addBlockPos(BlockPos blockPos){
        if (!this.blockPosList.contains(blockPos)) {
            this.blockPosList.add(blockPos);
        }
    }

    public void removeBlockPos(BlockPos blockPos){
        this.blockPosList.remove(blockPos);
    }

    public void clearBlocks(){
        this.blockPosList.clear();
    }

    public List<SquallGolem> getSquallGolems() {
        return this.squallGolems;
    }

    public void addSquallGolem(SquallGolem squallGolem){
        this.uuids.add(squallGolem.getUUID());
    }

    public List<UUID> getUuids(){
        return this.uuids;
    }

    @Override
    public int activeTicks() {
        return this.active;
    }

    @Override
    public void activate(int tick) {
        this.active = tick;
        this.setChanged();
    }

    public boolean isShowBlock(){
        return this.showBlock;
    }

    public void setShowBlock(boolean showBlock){
        this.showBlock = showBlock;
        this.setChanged();
    }

    @Override
    public void readNetwork(CompoundTag tag) {
        if (tag.contains("active")){
            this.active = tag.getInt("active");
        }
        if (tag.contains("showBlock")) {
            this.showBlock = tag.getBoolean("showBlock");
        }
        if (tag.contains(BLOCK_LIST)){
            ListTag list = tag.getList(BLOCK_LIST, 10);
            for(int i = 0; i < list.size(); ++i) {
                this.blockPosList.add(NbtUtils.readBlockPos(list.getCompound(i)));
            }
        }
        if (tag.contains(GOLEM_LIST)){
            ListTag list = tag.getList(GOLEM_LIST, 8);
            for(int i = 0; i < list.size(); ++i) {
                this.uuids.add(UUID.fromString(list.getString(i)));
            }
        }
        if (tag.contains("isOn")){
            this.isOn = tag.getBoolean("isOn");
        }
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag tag) {
        tag.putInt("active", this.active);
        List<String> list = new ArrayList<>();
        List<BlockPos> list2 = new ArrayList<>();
        if (tag.contains(GOLEM_LIST)) {
            for (int i = 0; i < tag.getList(GOLEM_LIST, 8).size(); ++i) {
                list.add(tag.getList(GOLEM_LIST, 8).getString(i));
            }
        }
        if (tag.contains(BLOCK_LIST)) {
            for (int i = 0; i < tag.getList(BLOCK_LIST, 10).size(); ++i) {
                list2.add(NbtUtils.readBlockPos(tag.getList(BLOCK_LIST, 10).getCompound(i)));
            }
        }
        if (!this.blockPosList.isEmpty()){
            for (BlockPos blockPos : this.blockPosList){
                if (!list2.contains(blockPos)){
                    ListTag nbttaglist = new ListTag();
                    if (tag.contains(BLOCK_LIST)) {
                        nbttaglist = tag.getList(BLOCK_LIST, 10);
                    }

                    nbttaglist.add(NbtUtils.writeBlockPos(blockPos));
                    tag.put(BLOCK_LIST, nbttaglist);
                }
            }
        }
        if (!this.uuids.isEmpty()) {
            for (UUID uuid : this.uuids) {
                if (!list.contains(uuid.toString())) {
                    ListTag nbttaglist = new ListTag();
                    if (tag.contains(GOLEM_LIST)) {
                        nbttaglist = tag.getList(GOLEM_LIST, 8);
                    }

                    nbttaglist.add(StringTag.valueOf(uuid.toString()));
                    tag.put(GOLEM_LIST, nbttaglist);
                }
            }
        }
        tag.putBoolean("showBlock", this.showBlock);
        tag.putBoolean("isOn", this.isOn);
        return tag;
    }
}
